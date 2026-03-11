# Load Balancing — Complete Deep Dive

> Distribute requests efficiently across servers using strategies like Round Robin, Least Connections, and Consistent Hashing.
> These approaches keep performance smooth, even under heavy load.

---

## Table of Contents

1. [What Is Load Balancing?](#1-what-is-load-balancing)
2. [Why Load Balancing Matters](#2-why-load-balancing-matters)
3. [Types of Load Balancers](#3-types-of-load-balancers)
4. [Load Balancing Algorithms — Deep Dive](#4-load-balancing-algorithms--deep-dive)
5. [Consistent Hashing — The Game Changer](#5-consistent-hashing--the-game-changer)
6. [Health Checks and Failover](#6-health-checks-and-failover)
7. [Load Balancing at Every Layer](#7-load-balancing-at-every-layer)
8. [Real-World Architectures](#8-real-world-architectures)
9. [Load Balancer Products Comparison](#9-load-balancer-products-comparison)
10. [System Design Interview — Load Balancing Questions](#10-system-design-interview--load-balancing-questions)
11. [Quick Reference — Cheat Sheet](#11-quick-reference--cheat-sheet)

---

## 1. What Is Load Balancing?

A load balancer sits **between clients and servers**, distributing incoming requests across multiple backend servers to ensure no single server is overwhelmed.

```
                        WITHOUT Load Balancer
                        ─────────────────────
  User A ─────┐
  User B ─────┼──────→  [ Server 1 ]    ← ALL traffic hits one server
  User C ─────┘                          ← Server overloaded → crash
  User D ─────→  ❌ timeout


                        WITH Load Balancer
                        ──────────────────
  User A ─────┐                    ┌──→ [ Server 1 ]  ← 25% traffic
  User B ─────┤                    │
  User C ─────┼──→ [Load Balancer]─┼──→ [ Server 2 ]  ← 25% traffic
  User D ─────┤                    │
  User E ─────┤                    ├──→ [ Server 3 ]  ← 25% traffic
  User F ─────┘                    │
                                   └──→ [ Server 4 ]  ← 25% traffic
```

> **Analogy**: A load balancer is like a **restaurant host** who seats guests at different tables to ensure no single waiter is overwhelmed, while empty tables don't go to waste.

---

## 2. Why Load Balancing Matters

| Problem Without LB | How LB Solves It |
|---------------------|-----------------|
| Single point of failure | Routes to healthy servers if one goes down |
| Uneven load distribution | Spreads requests based on algorithm |
| Wasted server capacity | Utilizes all available servers |
| Poor user experience (slow/timeout) | Keeps response times low |
| Can't scale horizontally | Seamlessly adds/removes servers |
| No zero-downtime deployments | Drain connections during rolling updates |

### The Numbers That Matter

```
┌─────────────────────────────────────────────────────────────┐
│  Amazon: Every 100ms of latency costs 1% in sales           │
│  Google: A 500ms delay drops traffic by 20%                  │
│  Netflix: Serves 250M+ users using load balancers at every   │
│           layer across 1000+ microservices                   │
│  Cloudflare: Handles 57M+ HTTP requests/second globally      │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Types of Load Balancers

### By OSI Layer

```
┌────────────────────────────────────────────────────────────────┐
│                      OSI MODEL                                  │
│                                                                │
│  Layer 7 (Application)     HTTP, HTTPS, WebSocket              │
│  ─────────────────────     ─────────────────────               │
│  • Inspects URL path, headers, cookies, body                   │
│  • Content-based routing (/api → service A, /static → CDN)    │
│  • SSL termination, compression, caching                       │
│  • Can modify requests/responses                               │
│  • Slower but smarter                                          │
│                                                                │
│  Layer 4 (Transport)       TCP, UDP                            │
│  ───────────────────       ───────                             │
│  • Forwards based on IP + port only                            │
│  • Cannot inspect content (encrypted or not)                   │
│  • Very fast — operates at kernel/NIC level                    │
│  • Used for databases, game servers, raw TCP                   │
│                                                                │
│  Layer 3 (Network)         IP                                  │
│  ────────────────          ──                                  │
│  • DNS-based or BGP Anycast routing                            │
│  • Routes to nearest data center                               │
│  • Used by CDNs and global services                            │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

### Layer 4 vs Layer 7 Comparison

| Feature | Layer 4 (Transport) | Layer 7 (Application) |
|---------|--------------------|-----------------------|
| **Speed** | Very fast (kernel-level) | Slower (user-space parsing) |
| **Visibility** | IP + Port only | Full HTTP: URL, headers, cookies, body |
| **Routing logic** | Simple (IP hash, round robin) | Content-based (path, header, cookie) |
| **SSL termination** | No (pass-through) | Yes (decrypt, inspect, re-encrypt) |
| **Use case** | DB connections, gaming, TCP services | Web apps, APIs, microservices |
| **Connection handling** | Forwards raw TCP/UDP | Terminates and creates new connection |
| **Example** | AWS NLB, HAProxy (TCP mode) | AWS ALB, Nginx, Envoy |

### Real-World Example: E-Commerce Architecture

```
                            Layer 7 LB
User Request ──→ [  ALB  ] ─┬──→ /api/*        → API Service (3 instances)
                             ├──→ /images/*     → CDN / Image Service
                             ├──→ /checkout/*   → Payment Service (2 instances)
                             └──→ /ws/*         → WebSocket Service (sticky)

                            Layer 4 LB
API Service ──→ [  NLB  ] ─┬──→ PostgreSQL Primary
                            └──→ PostgreSQL Read Replica
```

### By Deployment Model

| Model | Description | Example |
|-------|-------------|---------|
| **Hardware LB** | Physical appliance in data center | F5 BIG-IP, Citrix ADC |
| **Software LB** | Runs on commodity servers | Nginx, HAProxy, Envoy |
| **Cloud LB** | Managed service by cloud provider | AWS ALB/NLB, GCP Load Balancer, Azure LB |
| **DNS LB** | Returns different IPs for same domain | Route 53, Cloudflare DNS |
| **Client-side LB** | Client chooses backend server | gRPC, Netflix Ribbon (deprecated → Spring Cloud LB) |
| **Service Mesh LB** | Sidecar proxy handles routing | Istio (Envoy), Linkerd |

---

## 4. Load Balancing Algorithms — Deep Dive

### 4.1 Round Robin

The simplest algorithm: distribute requests sequentially, one after another.

```
Request 1 ──→ Server A
Request 2 ──→ Server B
Request 3 ──→ Server C
Request 4 ──→ Server A    ← wraps around
Request 5 ──→ Server B
Request 6 ──→ Server C
...
```

**How it works:**

```
servers = [A, B, C]
counter = 0

function getServer():
    server = servers[counter % servers.length]
    counter++
    return server
```

| Pros | Cons |
|------|------|
| Dead simple to implement | Ignores server capacity differences |
| Zero overhead | Ignores current server load |
| Predictable distribution | Long-running requests cause imbalance |
| No state required | All servers assumed equal |

**When to use**: Stateless services with identical servers (e.g., static content servers, API gateways where all instances are the same size).

**Real-World**: Nginx default upstream balancing, DNS round-robin for simple multi-server setups.

### 4.2 Weighted Round Robin

Assigns weights to servers based on capacity, sending more requests to more powerful servers.

```
Server A (weight 5) ──→ Gets 5 out of 8 requests
Server B (weight 2) ──→ Gets 2 out of 8 requests
Server C (weight 1) ──→ Gets 1 out of 8 requests

Sequence: A, A, A, B, A, A, B, C, A, A, A, B, ...
```

**Real-World — Gradual Deployment (Canary)**:

```
Production (weight 95) ──→ 95% of traffic → v2.3.0 (stable)
Canary     (weight  5) ──→  5% of traffic → v2.4.0 (new release)

If canary has no errors after 30 min:
  Canary   (weight 50) ──→ 50%
  Finally  (weight 100) ──→ full rollout
```

### 4.3 Least Connections

Routes each new request to the server with the **fewest active connections**.

```
Server A: 12 active connections
Server B:  3 active connections  ◄── next request goes here
Server C:  8 active connections

After routing:
Server A: 12 connections
Server B:  4 connections
Server C:  8 connections
```

**Why it's better than Round Robin:**

```
Scenario: Server A is processing a large file upload (30 seconds)

Round Robin:
  Request 1 → A (upload, 30s)
  Request 2 → B (fast, 50ms)
  Request 3 → C (fast, 50ms)
  Request 4 → A (queued behind upload!)  ← User waits!

Least Connections:
  Request 1 → A (upload, 30s, connections: 1)
  Request 2 → B (fast, connections: 0→1→0)
  Request 3 → C (fast, connections: 0→1→0)
  Request 4 → B (connections: 0, not A!)  ← Smart!
```

| Pros | Cons |
|------|------|
| Adapts to variable request durations | Requires tracking active connections |
| Handles slow requests well | New server gets all traffic initially (thundering herd) |
| Good for WebSocket/long-polling | Slight overhead per request |

**When to use**: Mixed workloads — some requests are fast (API calls), some are slow (file uploads, long-running queries, WebSocket connections).

**Real-World**: 
- **AWS ALB** uses least outstanding requests (similar concept)
- **HAProxy** supports `leastconn` balancing
- **Database connection pools** use this to balance query load

### 4.4 Weighted Least Connections

Combines server capacity weights with connection awareness.

```
Server A (weight 10, 50 connections) → ratio: 50/10 = 5.0
Server B (weight  5, 20 connections) → ratio: 20/5  = 4.0  ◄── lowest ratio wins
Server C (weight  3, 15 connections) → ratio: 15/3  = 5.0
```

### 4.5 IP Hash

Hashes the client's IP address to deterministically route to the same server.

```
hash("203.0.113.1") % 3 = 0 → Server A    (always goes to A)
hash("198.51.100.7") % 3 = 1 → Server B   (always goes to B)
hash("192.0.2.42")   % 3 = 2 → Server C   (always goes to C)
```

| Pros | Cons |
|------|------|
| Session persistence without cookies | Uneven distribution if IPs cluster |
| Simple and stateless | Adding/removing servers breaks all mappings |
| Good for caching (same user → same cache) | Corporate NAT makes many users share one IP |

**When to use**: When you need basic session affinity without cookie-based sticky sessions (e.g., gaming servers, stateful TCP connections).

### 4.6 Least Response Time

Routes to the server with the **fastest recent response times**.

```
Server A: avg response = 45ms   ◄── next request goes here
Server B: avg response = 120ms
Server C: avg response = 78ms
```

**Real-World**: Nginx Plus uses this with `least_time` directive. Ideal when backend servers have varying processing capabilities or when some are geographically closer.

### 4.7 Random

Simply picks a random server. Surprisingly effective at large scale.

```
Request → random(0, server_count) → Server
```

At **large scale** (millions of requests), random distribution approaches uniform distribution (law of large numbers). Used by **Google Maglev** as a fallback and by some service mesh implementations.

### 4.8 Resource-Based (Adaptive)

Queries real-time server metrics (CPU, memory, disk I/O) and routes to the healthiest server.

```
┌──────────────────────────────────────────┐
│  Server A: CPU 85%, MEM 72%   → Score: 3 │
│  Server B: CPU 20%, MEM 45%   → Score: 9 │  ◄── best
│  Server C: CPU 60%, MEM 90%   → Score: 4 │
└──────────────────────────────────────────┘
```

**When to use**: Heterogeneous environments where servers have different hardware, or workloads cause uneven resource usage.

### Algorithm Comparison Summary

| Algorithm | Awareness | Session Sticky | Complexity | Best For |
|-----------|-----------|----------------|------------|----------|
| **Round Robin** | None | No | O(1) | Identical stateless servers |
| **Weighted Round Robin** | Capacity | No | O(1) | Mixed server sizes, canary deploys |
| **Least Connections** | Load | No | O(n) or O(log n) | Variable request durations |
| **IP Hash** | Client identity | Yes (by IP) | O(1) | Caching, basic session persistence |
| **Least Response Time** | Performance | No | O(n) | Latency-sensitive workloads |
| **Consistent Hashing** | Data distribution | Yes (by key) | O(log n) | Caches, sharded databases |
| **Random** | None | No | O(1) | Large-scale, simple systems |
| **Resource-Based** | Server health | No | O(n) | Heterogeneous environments |

---

## 5. Consistent Hashing — The Game Changer

### The Problem with Simple Hashing

```
Normal hash: server = hash(key) % N

With 3 servers:
  hash("user:123") % 3 = 1 → Server B
  hash("user:456") % 3 = 0 → Server A
  hash("user:789") % 3 = 2 → Server C

Now ADD a 4th server (N=3 → N=4):
  hash("user:123") % 4 = 3 → Server D    ← MOVED! (was B)
  hash("user:456") % 4 = 0 → Server A    ← OK
  hash("user:789") % 4 = 1 → Server B    ← MOVED! (was C)

Result: ~75% of keys remapped! → Cache miss storm → Database crushed
```

### How Consistent Hashing Works

**Step 1: Arrange servers on a ring (0 to 2³²)**

```
                    0 / 2³²
                     ╱╲
                   ╱    ╲
                 ╱        ╲
        Server A ●          ● Server B
               ╱              ╲
             ╱                  ╲
           ╱                      ╲
          ●                        ●
     Server D                  Server C
           ╲                      ╱
             ╲                  ╱
               ╲              ╱
                 ╲          ╱
                   ╲      ╱
                    2¹⁶ (midpoint)
```

**Step 2: Hash keys and walk clockwise to find the server**

```
hash("user:123") = position on ring → walk clockwise → first server = owner

     0
     │
     ● Server A (pos 1000)
     │
     │    ← hash("user:123") = 1500 → walks to Server B
     │
     ● Server B (pos 2000)
     │
     │    ← hash("user:456") = 2800 → walks to Server C
     │
     ● Server C (pos 3000)
     │
     │    ← hash("user:789") = 3500 → walks to Server D
     │
     ● Server D (pos 4000)
```

**Step 3: Adding a server — only 1 segment remaps!**

```
Add Server E at position 2500:

Before: hash("user:456") = 2800 → Server C
After:  hash("user:456") = 2800 → Server E  (2500 < 2800 < 3000)

Only keys between Server B (2000) and Server E (2500) move.
= ~1/N of keys  (not 75%!)
```

### Virtual Nodes (Vnodes) — Solving Uneven Distribution

With few servers, the ring can be **unbalanced** (one server gets 60% of keys). Virtual nodes fix this:

```
Physical servers: A, B, C

Virtual nodes (6 per server):
  A₁, A₂, A₃, A₄, A₅, A₆  → all route to Server A
  B₁, B₂, B₃, B₄, B₅, B₆  → all route to Server B
  C₁, C₂, C₃, C₄, C₅, C₆  → all route to Server C

Ring with 18 virtual nodes:

     B₃ ● ─── A₁ ● ─── C₂ ●
    ╱                          ╲
  C₅ ●                      B₁ ●
    │                          │
  A₄ ●                      A₃ ●
    │                          │
  B₆ ●                      C₁ ●
    ╲                          ╱
     A₂ ● ─── C₄ ● ─── B₅ ●

Result: Each server gets ~33% of keys (much more even!)
```

### Real-World Consistent Hashing

| System | How It Uses Consistent Hashing |
|--------|-------------------------------|
| **Amazon DynamoDB** | Partition data across storage nodes; adding a node only moves 1/N of data |
| **Apache Cassandra** | Token ring partitioning with virtual nodes (default 256 vnodes per node) |
| **Memcached (by clients)** | Client libraries use consistent hashing to route cache keys |
| **Discord** | Distributes chat servers and voice channels across backend nodes |
| **Akamai CDN** | Maps content URLs to edge servers globally |
| **Redis Cluster** | Hash slots (0-16383) distributed across nodes — a form of consistent hashing |

### Consistent Hashing — Discord Example

```
Discord: 200M+ monthly users, 19M+ active servers (guilds)

Problem: Route user messages to the right backend server for their guild.

                    ┌──────────────────────────────┐
                    │     CONSISTENT HASH RING      │
                    │                                │
  Guild "Gaming"    │    hash("Gaming")  → Node 5   │
  Guild "Study"     │    hash("Study")   → Node 12  │
  Guild "Music"     │    hash("Music")   → Node 3   │
                    │                                │
                    │  Add Node 15:                  │
                    │    Only guilds between Node 12 │
                    │    and Node 15 move.           │
                    │    Other guilds: no change!    │
                    └──────────────────────────────┘

Benefits:
  • Adding a server: only ~1/N guilds need to migrate
  • Removing a crashed server: only that server's guilds re-route
  • Predictable, minimal disruption during scaling
```

---

## 6. Health Checks and Failover

A load balancer is only useful if it knows **which servers are healthy**.

### Types of Health Checks

```
┌─────────────────────────────────────────────────────────┐
│                    HEALTH CHECKS                        │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  1. PASSIVE (observe traffic)                           │
│     • Count 5xx errors in last 30 seconds               │
│     • If error rate > 50% → mark unhealthy              │
│     • No extra traffic generated                        │
│                                                         │
│  2. ACTIVE (dedicated probes)                           │
│     • Send HTTP GET /health every 10 seconds            │
│     • Expect 200 OK within 3 seconds                    │
│     • If 3 consecutive failures → mark unhealthy        │
│                                                         │
│  3. DEEP (application-level)                            │
│     • Check database connectivity                       │
│     • Verify Redis/cache availability                   │
│     • Validate disk space > threshold                   │
│     • Return JSON: {"status":"healthy","db":"ok",...}    │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### Failover in Action

```
Timeline:
  t=0s   All healthy:  A✅  B✅  C✅
           LB routing:  A(33%)  B(33%)  C(33%)

  t=10s  Health check:  A✅  B✅  C✅    (all OK)

  t=20s  Server B crashes! 💥
         Health check:  A✅  B❌  C✅

  t=20s  LB detects failure (active health check fails)
         LB routing:    A(50%)  C(50%)
         In-flight requests to B: retried on A or C

  t=60s  Server B recovers, passes 3 health checks
         LB routing:    A(33%)  B(33%)  C(33%)    (back to normal)
```

### Connection Draining (Graceful Shutdown)

```
Deploying new version of Service B:

  1. LB stops sending NEW requests to B-old
  2. B-old finishes processing in-flight requests (drain: 30s timeout)
  3. B-old shuts down
  4. B-new starts, passes health checks
  5. LB starts routing to B-new

         ┌─────────────────────────────────────────┐
  Time:  │ Draining  │  Shutdown  │  Starting  │ ✅ │
         │ (finish   │            │  (health   │    │
         │  existing)│            │   checks)  │    │
         └─────────────────────────────────────────┘
  Traffic: ████░░░░░░│░░░░░░░░░░░│░░░░████████│████│
```

---

## 7. Load Balancing at Every Layer

In a real production system, load balancing happens at **multiple layers**:

```
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│  USER                                                            │
│    │                                                             │
│    ▼                                                             │
│  ┌──────────────────────────┐                                    │
│  │  LAYER 1: DNS            │  Route 53 / Cloudflare             │
│  │  Geo-based routing       │  Return nearest DC's IP            │
│  │  Weighted round robin    │                                    │
│  └────────────┬─────────────┘                                    │
│               │                                                  │
│    ▼                                                             │
│  ┌──────────────────────────┐                                    │
│  │  LAYER 2: Edge / CDN     │  CloudFront / Akamai               │
│  │  Static content cached   │  Serve from edge (< 50ms)          │
│  │  DDoS protection         │                                    │
│  └────────────┬─────────────┘                                    │
│               │ (dynamic requests only)                          │
│    ▼                                                             │
│  ┌──────────────────────────┐                                    │
│  │  LAYER 3: L7 Load Balancer│  ALB / Nginx / Envoy              │
│  │  Path-based routing      │  /api → API, /web → Web            │
│  │  SSL termination         │  Rate limiting, auth               │
│  └────────────┬─────────────┘                                    │
│               │                                                  │
│    ▼                                                             │
│  ┌──────────────────────────┐                                    │
│  │  LAYER 4: Service Mesh    │  Istio / Envoy sidecar             │
│  │  Per-service LB          │  Circuit breaking, retry            │
│  │  mTLS between services   │  Canary traffic splitting           │
│  └────────────┬─────────────┘                                    │
│               │                                                  │
│    ▼                                                             │
│  ┌──────────────────────────┐                                    │
│  │  LAYER 5: Database LB     │  PgBouncer / ProxySQL / NLB       │
│  │  Read/Write splitting    │  Writes → Primary                  │
│  │  Connection pooling      │  Reads → Replicas (round robin)    │
│  └──────────────────────────┘                                    │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 8. Real-World Architectures

### Netflix — Multi-Layer Load Balancing

```
250M+ subscribers, 190 countries, 1000+ microservices

┌──────────────────────────────────────────────────────────┐
│                                                          │
│  DNS: AWS Route 53                                       │
│  ├── Latency-based routing (nearest region)              │
│  └── Health-checked failover                             │
│                                                          │
│  Edge: Open Connect CDN                                  │
│  ├── 17,000+ servers in 6,000+ ISP locations             │
│  ├── Serve video from edge (> 95% of traffic)            │
│  └── Only metadata/API calls reach origin                │
│                                                          │
│  API Gateway: Zuul (custom L7 LB)                        │
│  ├── Request routing by path/header                      │
│  ├── Canary deployments (1% → 5% → 50% → 100%)          │
│  ├── Rate limiting per user/device                       │
│  └── Circuit breaking (Hystrix)                          │
│                                                          │
│  Internal: Eureka + Ribbon (client-side LB)              │
│  ├── Service discovery: microservices register with      │
│  │   Eureka; clients query for available instances       │
│  ├── Client-side LB: Ribbon in each service picks        │
│  │   backend using weighted round robin                  │
│  └── Zone-aware: prefer same-AZ instances (lower latency)│
│                                                          │
│  Database: EVCache (Memcached) + Cassandra               │
│  ├── EVCache: consistent hashing across cache nodes      │
│  └── Cassandra: token ring (consistent hashing)          │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Uber — Geographically Aware Load Balancing

```
Problem: Match riders with drivers in < 100ms worldwide

┌──────────────────────────────────────────────────────────┐
│                                                          │
│  DNS: Route to nearest region                            │
│  ├── US users → us-east / us-west                        │
│  ├── India users → ap-south                              │
│  └── EU users → eu-west                                  │
│                                                          │
│  Edge LB: Custom L7 + L4 load balancer                   │
│  ├── Least-connections across API gateway instances       │
│  └── TLS termination at edge                             │
│                                                          │
│  Ring Pop (Consistent Hashing):                          │
│  ├── hash(city_id) → determines which server handles     │
│  │   ride matching for that city                         │
│  ├── Adding servers: only one city's data migrates       │
│  └── Geospatial index kept in-memory per city-server     │
│                                                          │
│  Database: Schemaless (MySQL sharded by city)             │
│  ├── Consistent hashing to route to correct shard        │
│  └── Each shard has 3 replicas (primary + 2 secondaries) │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Shopify — Handling Black Friday / Cyber Monday

```
Peak: 80K+ requests/sec during BFCM sales

┌──────────────────────────────────────────────────────────┐
│                                                          │
│  DNS: Cloudflare (DDoS protection + geo routing)         │
│                                                          │
│  Edge: Nginx (L7 LB)                                     │
│  ├── Path routing: /admin → admin service                │
│  ├── Rate limiting: per-shop throttling                  │
│  └── Pod-level LB: weighted round robin to Kubernetes    │
│                                                          │
│  Internal: Kubernetes Service LB                          │
│  ├── kube-proxy iptables rules (round robin)             │
│  ├── Auto-scaling: 0 → 1000 pods in minutes              │
│  └── Graceful drain during deploys                       │
│                                                          │
│  Database: Vitess (MySQL sharding layer)                  │
│  ├── Consistent hashing by shop_id                       │
│  ├── Read replicas with least-connections routing         │
│  └── Connection pooling via VTGate                       │
│                                                          │
│  Strategy for BFCM:                                       │
│  ├── Pre-scale 2x capacity 24h before                    │
│  ├── Queue checkout requests (throttle surges)           │
│  └── Serve cached product pages (CDN)                    │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## 9. Load Balancer Products Comparison

| Product | Type | Layer | Algorithm Support | Best For |
|---------|------|-------|-------------------|----------|
| **Nginx** | Software | L4/L7 | Round Robin, Least Conn, IP Hash, Least Time | Web apps, API proxies |
| **HAProxy** | Software | L4/L7 | Round Robin, Least Conn, Source IP, URI hash | High-performance TCP/HTTP |
| **Envoy** | Software | L4/L7 | Round Robin, Least Request, Ring Hash, Maglev | Service mesh, microservices |
| **Traefik** | Software | L7 | Round Robin, Weighted RR | Kubernetes, Docker |
| **AWS ALB** | Cloud | L7 | Round Robin, Least Outstanding | AWS web apps |
| **AWS NLB** | Cloud | L4 | Flow hash (5-tuple) | AWS TCP/UDP, low latency |
| **GCP LB** | Cloud | L4/L7 | Round Robin, Least Request | GCP global anycast |
| **Azure LB** | Cloud | L4 | 5-tuple hash | Azure VMs |
| **Cloudflare LB** | Cloud | L7 | Geo, Random, Least Conn, IP Hash | Global, multi-cloud |
| **F5 BIG-IP** | Hardware | L4/L7 | 10+ algorithms | Enterprise on-prem |

### Nginx Configuration Example

```nginx
# Round Robin (default)
upstream backend {
    server 10.0.0.1:8080;
    server 10.0.0.2:8080;
    server 10.0.0.3:8080;
}

# Weighted Round Robin
upstream backend_weighted {
    server 10.0.0.1:8080 weight=5;    # gets 5x traffic
    server 10.0.0.2:8080 weight=3;    # gets 3x traffic
    server 10.0.0.3:8080 weight=1;    # gets 1x traffic
}

# Least Connections
upstream backend_least {
    least_conn;
    server 10.0.0.1:8080;
    server 10.0.0.2:8080;
    server 10.0.0.3:8080;
}

# IP Hash (session persistence)
upstream backend_sticky {
    ip_hash;
    server 10.0.0.1:8080;
    server 10.0.0.2:8080;
    server 10.0.0.3:8080;
}

server {
    listen 80;

    location /api/ {
        proxy_pass http://backend_least;     # least conn for APIs
    }

    location /static/ {
        proxy_pass http://backend;           # round robin for static
    }

    location /ws/ {
        proxy_pass http://backend_sticky;    # sticky for WebSockets
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

---

## 10. System Design Interview — Load Balancing Questions

### Common Questions and Answers

**Q: "Where would you put a load balancer in this system?"**
> Between every layer: DNS → Edge/CDN → API gateway → microservices → database. Each layer may need a different algorithm — L7 with path routing for APIs, L4 for database connections, consistent hashing for caches.

**Q: "How do you handle session persistence?"**
> Three approaches: (1) Sticky sessions via cookie/IP hash — simple but limits scalability. (2) Externalized session store (Redis) — best for horizontal scaling; any server can handle any request. (3) JWT tokens — stateless; session data is in the token itself.

**Q: "What happens when a server goes down?"**
> The LB detects failure via health checks (active: periodic HTTP probes, passive: tracking 5xx errors). It removes the unhealthy server from the pool, redistributes traffic to remaining servers, and re-adds the server once it passes consecutive health checks.

**Q: "How would you handle a 10x traffic spike?"**
> (1) Auto-scaling: add more instances behind the LB. (2) CDN: serve cached static content from edge. (3) Rate limiting at the LB layer. (4) Queue surge traffic (accept-and-process-later). (5) Graceful degradation: serve simplified responses under extreme load.

**Q: "Why use consistent hashing instead of simple modular hashing?"**
> When servers are added or removed, modular hashing (`key % N`) remaps ~(N-1)/N keys (almost all!). Consistent hashing only remaps ~1/N keys — critical for caches where remapping means cache misses that could overwhelm the database.

**Q: "How do you achieve zero-downtime deployments?"**
> (1) Blue-green: two identical environments; LB switches traffic from blue to green. (2) Rolling update: update one server at a time; LB drains connections before each update. (3) Canary: route 1% traffic to new version, monitor errors, gradually increase.

---

## 11. Quick Reference — Cheat Sheet

### Algorithm Decision Tree

```
Do your servers have different capacities?
├── Yes → Weighted Round Robin or Weighted Least Connections
└── No
    ├── Are request durations highly variable?
    │   ├── Yes → Least Connections
    │   └── No → Round Robin
    ├── Do you need session persistence?
    │   ├── Yes → IP Hash or Cookie-based sticky sessions
    │   └── No → (continue below)
    ├── Are you distributing cache keys or data?
    │   ├── Yes → Consistent Hashing
    │   └── No → Round Robin or Least Connections
    └── Is latency the top priority?
        ├── Yes → Least Response Time
        └── No → Round Robin
```

### One-Line Summaries

| Algorithm | One Liner |
|-----------|-----------|
| **Round Robin** | Take turns, one after another |
| **Weighted Round Robin** | Take turns, but some get more turns |
| **Least Connections** | Send to whoever is least busy right now |
| **IP Hash** | Same client always goes to the same server |
| **Consistent Hashing** | Hash ring so adding/removing servers moves minimal data |
| **Least Response Time** | Send to whoever has been responding fastest |
| **Random** | Just pick one — works well at scale |
| **Resource-Based** | Ask servers how they're feeling, pick the healthiest |

### Key Numbers

```
┌──────────────────────────────────────────────────────────┐
│  Nginx:          Can handle 10,000+ connections/worker    │
│  HAProxy:        Can handle 1M+ concurrent connections    │
│  AWS ALB:        Auto-scales to millions of requests/sec  │
│  AWS NLB:        Handles millions of requests/sec at L4   │
│  Envoy:          Sub-millisecond routing overhead          │
│  DNS TTL:        Typically 60-300 seconds                  │
│  Health check:   Every 5-30 seconds is standard            │
│  Connection drain: 30-120 seconds typical                  │
└──────────────────────────────────────────────────────────┘
```

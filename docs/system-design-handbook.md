# System Design Interview Handbook

> System Design isn't just for senior engineers anymore. It's a core interview and on-the-job skill.
> You don't need to memorize buzzwords — you need clear fundamentals and practical thinking.

**Reference:** [Ritik Jain — System Design Interview Handbook](https://www.linkedin.com/posts/rjritikjain_system-design-interview-handbook-activity-7438048715480850432-fVse)

---

## Table of Contents

1. [How Load Balancers Distribute Traffic](#1-how-load-balancers-distribute-traffic-in-real-systems)
2. [Latency vs Throughput](#2-latency-vs-throughput--and-when-each-one-matters)
3. [CAP Theorem — Real-World Trade-offs](#3-cap-theorem-and-real-world-trade-offs)
4. [Architecture Patterns](#4-architecture-patterns-monoliths-microservices-event-driven)
5. [Designing for Scalability, Reliability & Availability](#5-designing-for-scalability-reliability--availability-from-day-one)
6. [How to Think Through a System Design Interview](#6-how-to-think-through-a-system-design-interview)
7. [15 System Design Concepts Cheat Sheet](#7-15-system-design-concepts-cheat-sheet)
8. [Design Questions Mapped to Concepts](#8-common-design-questions-mapped-to-concepts)

---

## 1. How Load Balancers Distribute Traffic in Real Systems

### What Is a Load Balancer?

A load balancer sits between clients and servers, routing each request to a healthy backend so no single server is overwhelmed.

```
═══════════════════════════════════════════════════════════════════
  WITHOUT Load Balancer             WITH Load Balancer
═══════════════════════════════════════════════════════════════════

  Client 1 ──┐                      Client 1 ──┐
  Client 2 ──┼──► Single Server     Client 2 ──┼──► ┌────────────┐
  Client 3 ──┘    (overwhelmed!)    Client 3 ──┘    │    Load    │
                                                    │  Balancer  │
                  CPU: 100% ❌                      └──┬───┬───┬─┘
                  RAM: 95%  ❌                         │   │   │
                  Users: 503 errors                    ▼   ▼   ▼
                                                    ┌──┐ ┌──┐ ┌──┐
                                                    │S1│ │S2│ │S3│
                                                    │30%│ │35%│ │28%│ ← balanced
                                                    └──┘ └──┘ └──┘
```

### Layer 4 vs Layer 7 Load Balancing

```
┌──────────────────────────────────────────────────────────────────┐
│                     OSI Model Layers                              │
│                                                                    │
│  Layer 7 (Application):  HTTP, HTTPS, WebSocket                   │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │ L7 Load Balancer                                            │  │
│  │ • Can read HTTP headers, cookies, URL path                  │  │
│  │ • Route /api/* to API servers, /static/* to CDN             │  │
│  │ • A/B testing: route 10% traffic to new version             │  │
│  │ • SSL termination (decrypt HTTPS here)                      │  │
│  │ • Example: AWS ALB, Nginx, HAProxy, Envoy                  │  │
│  └─────────────────────────────────────────────────────────────┘  │
│                                                                    │
│  Layer 4 (Transport):  TCP, UDP                                   │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │ L4 Load Balancer                                            │  │
│  │ • Routes by IP + port only (cannot read HTTP content)       │  │
│  │ • Faster (no need to parse HTTP headers)                    │  │
│  │ • Good for: databases, gRPC, gaming, video streaming       │  │
│  │ • Example: AWS NLB, F5, LVS                                │  │
│  └─────────────────────────────────────────────────────────────┘  │
│                                                                    │
│  ┌───────────────────┬───────────────────┐                        │
│  │     Layer 4       │     Layer 7       │                        │
│  ├───────────────────┼───────────────────┤                        │
│  │ IP + Port routing │ Content routing   │                        │
│  │ Faster, simpler   │ Smarter, flexible │                        │
│  │ Can't inspect     │ SSL termination   │                        │
│  │ payload           │ Path-based routing│                        │
│  │ ~1M conn/sec      │ ~100K req/sec     │                        │
│  │ NLB, F5           │ ALB, Nginx, Envoy │                        │
│  └───────────────────┴───────────────────┘                        │
└──────────────────────────────────────────────────────────────────┘
```

### Load Balancing Algorithms Compared

```
═══════════════════════════════════════════════════════════════════
  ROUND ROBIN — Simplest, rotate through servers
═══════════════════════════════════════════════════════════════════

  Request 1 ──► Server A
  Request 2 ──► Server B
  Request 3 ──► Server C
  Request 4 ──► Server A   ← cycle repeats
  Request 5 ──► Server B
  Request 6 ──► Server C

  Pros: Dead simple, no state needed
  Cons: Ignores server health/load; uneven if servers differ in capacity

═══════════════════════════════════════════════════════════════════
  WEIGHTED ROUND ROBIN — Bigger servers get more traffic
═══════════════════════════════════════════════════════════════════

  Server A (weight=5): 50% of requests
  Server B (weight=3): 30% of requests
  Server C (weight=2): 20% of requests

  Sequence: A, A, A, B, B, A, C, A, B, C, ...

  Use when: servers have different CPU/memory capacity

═══════════════════════════════════════════════════════════════════
  LEAST CONNECTIONS — Send to the least busy server
═══════════════════════════════════════════════════════════════════

  Server A: 23 active connections
  Server B: 12 active connections   ← next request goes here
  Server C: 45 active connections

  Pros: Adapts to actual load in real time
  Cons: Requires tracking connections (stateful LB)
  Use when: requests have variable processing time (long API calls)

═══════════════════════════════════════════════════════════════════
  IP HASH — Same client always hits same server
═══════════════════════════════════════════════════════════════════

  hash(client_ip) % num_servers = server_index

  Client 1.2.3.4  → hash → 7 % 3 = 1 → Server B (always!)
  Client 5.6.7.8  → hash → 2 % 3 = 2 → Server C (always!)

  Pros: Session affinity without cookies
  Cons: Adding/removing a server rehashes ALL clients

═══════════════════════════════════════════════════════════════════
  CONSISTENT HASHING — Minimal disruption when servers change
═══════════════════════════════════════════════════════════════════

  Imagine a circular ring (0 to 2^32):

            0
           ╱ ╲
         ╱     ╲
    S3 ●         ● S1        S1 handles keys from S3 to S1
       │         │            (clockwise)
       │         │
    S2 ●─────────●            When S4 is added, only keys
           ↑                  between S3 and S4 move.
          S4 added here       Other servers unaffected! ✅

  With virtual nodes (each server has 100-200 positions on ring):
    → Even more balanced distribution
    → When a server dies, its load spreads evenly across all others

  Used by: DynamoDB, Cassandra, Memcached, CDNs
```

### Real-World: How Netflix Routes 250M+ Users

```
┌───────────────────────────────────────────────────────────────┐
│              NETFLIX TRAFFIC ROUTING (simplified)              │
│                                                                │
│  User in São Paulo opens Netflix app                          │
│       │                                                        │
│       ▼                                                        │
│  ┌──────────────┐   DNS returns nearest                       │
│  │  AWS Route53 │   edge location based                       │
│  │  (DNS)       │   on GeoDNS + latency                       │
│  └──────┬───────┘                                              │
│         │                                                      │
│         ▼                                                      │
│  ┌──────────────┐   Static content (images,                   │
│  │  Open Connect│   video) served from ISP-                   │
│  │  CDN (OCA)   │   embedded Netflix boxes                    │
│  │  São Paulo   │   Latency: < 5ms                            │
│  └──────────────┘                                              │
│                                                                │
│  For API calls (browse, search, recommendations):              │
│         │                                                      │
│         ▼                                                      │
│  ┌──────────────┐   Layer 7 LB                                │
│  │  Zuul/Spring │   ├── Rate limiting                         │
│  │  Cloud       │   ├── Authentication                        │
│  │  Gateway     │   ├── A/B routing (canary)                  │
│  │              │   └── Route to correct microservice          │
│  └──────┬───────┘                                              │
│         │                                                      │
│    ┌────┼────┬────┬────┐                                      │
│    ▼    ▼    ▼    ▼    ▼                                      │
│  ┌────┐┌────┐┌────┐┌────┐┌────┐                              │
│  │Auth││Srch││Rec ││Play││Bill│  1000+ microservices          │
│  └────┘└────┘└────┘└────┘└────┘  each auto-scaled             │
│                                                                │
│  Key patterns:                                                │
│  • GeoDNS for global routing                                  │
│  • CDN caches at ISP level (70% of traffic never leaves ISP) │
│  • L7 gateway for smart routing and resilience                │
│  • Each microservice behind its own internal LB               │
└───────────────────────────────────────────────────────────────┘
```

### Health Checks — Detecting Dead Servers

```
Load Balancer pings each server periodically:

  LB ──── GET /health ────► Server A → 200 OK ✅ (keep in pool)
  LB ──── GET /health ────► Server B → 200 OK ✅ (keep in pool)
  LB ──── GET /health ────► Server C → timeout  ❌ (remove!)

  ┌─────────────────────────────────────────────────┐
  │ Health Check Configuration                       │
  │                                                   │
  │ Interval:        10 seconds                       │
  │ Timeout:         5 seconds                        │
  │ Unhealthy after: 3 consecutive failures           │
  │ Healthy after:   2 consecutive successes          │
  │ Endpoint:        GET /health                      │
  │                                                   │
  │ Smart health check (deep):                       │
  │ {                                                 │
  │   "status": "UP",                                │
  │   "db": "connected",                             │
  │   "redis": "connected",                          │
  │   "disk_free": "45GB",                           │
  │   "cpu_load": "35%"                              │
  │ }                                                 │
  └─────────────────────────────────────────────────┘
```

---

## 2. Latency vs Throughput — and When Each One Matters

### The Core Difference

```
═══════════════════════════════════════════════════════════════════
  LATENCY vs THROUGHPUT — Coffee Shop Analogy
═══════════════════════════════════════════════════════════════════

  LATENCY = Time to make ONE coffee
  ┌─────────────────────────────────────────────┐
  │  Order → Grind → Brew → Pour → Serve        │
  │  0s      10s     30s    5s     2s = 47s     │
  │                                              │
  │  Customer waits 47 seconds for one coffee.  │
  └─────────────────────────────────────────────┘

  THROUGHPUT = Coffees made PER HOUR
  ┌─────────────────────────────────────────────┐
  │  1 barista:   60 coffees/hour               │
  │  3 baristas: 180 coffees/hour               │
  │  3 baristas + pre-ground: 300 coffees/hour  │
  │                                              │
  │  More baristas = more throughput             │
  │  Pre-grinding = reduces latency too!         │
  └─────────────────────────────────────────────┘

  KEY INSIGHT: You can increase throughput (more baristas)
  WITHOUT reducing latency (each coffee still takes 47s).

  But reducing latency (faster machine) often increases
  throughput too (each barista makes more per hour).
```

### Latency Numbers Every Engineer Should Know

```
┌─────────────────────────────────────────────────────────────────┐
│                 LATENCY REFERENCE CHART                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  L1 cache reference ................... 0.5   ns    ▎            │
│  L2 cache reference ................... 7     ns    ▎            │
│  Main memory reference ................ 100   ns    █            │
│                                                                  │
│  ──── 1 μs ────────────────────────────────────────────────────  │
│                                                                  │
│  SSD random read ...................... 150   μs    ████         │
│  Round trip same datacenter ........... 500   μs    ██████████   │
│                                                                  │
│  ──── 1 ms ────────────────────────────────────────────────────  │
│                                                                  │
│  SSD sequential read (1MB) ............ 1     ms    █            │
│  HDD sequential read (1MB) ............ 20    ms    ████████     │
│  Cross-continent round trip ........... 150   ms    ██████████   │
│                                                                  │
│  ──── TAKEAWAYS ───────────────────────────────────────────────  │
│                                                                  │
│  Memory is ~100x faster than SSD                                │
│  SSD is ~20x faster than spinning disk                          │
│  Same-DC round trip: 0.5ms (fast!)                              │
│  Cross-continent: 150ms (speed of light limit)                  │
│  → This is why CDNs and geo-replication exist                   │
└─────────────────────────────────────────────────────────────────┘
```

### Measuring Latency — Why Percentiles Matter

```
Distribution of 10,000 request response times:

  Count
  ▲
  │
  │  █
  │  ██
  │  ████
  │  ██████
  │  █████████
  │  ████████████
  │  ██████████████████
  │  ████████████████████████████████████████████           ▪ ▪
  └────────────────────────────────────────────────────────────► ms
     20    50   100   200   500  1000  2000  5000  8000

  P50 (median):  50ms   ← Half of users see ≤ 50ms
  P95:           200ms  ← 95% of users see ≤ 200ms
  P99:           2000ms ← 1% of users wait > 2 seconds!
  Average:       120ms  ← MISLEADING! Hides the tail.

  At 10M requests/day:
    P99 = 100,000 requests taking > 2 seconds EVERY DAY!
    These users are your most active (more requests → more chances to hit P99)
```

### When Latency Matters vs When Throughput Matters

```
┌─────────────────────┬──────────────────────┬──────────────────────┐
│ System              │ Optimize For         │ Why                  │
├─────────────────────┼──────────────────────┼──────────────────────┤
│ Google Search       │ LATENCY (< 200ms)    │ Users abandon after  │
│                     │                      │ 400ms                │
├─────────────────────┼──────────────────────┼──────────────────────┤
│ Trading Platform    │ LATENCY (< 1ms)      │ Microseconds = $$    │
│                     │                      │ in HFT               │
├─────────────────────┼──────────────────────┼──────────────────────┤
│ Video Streaming     │ THROUGHPUT (Gbps)     │ Sustained bandwidth  │
│ (Netflix)           │                      │ for 4K streams       │
├─────────────────────┼──────────────────────┼──────────────────────┤
│ Batch ETL Pipeline  │ THROUGHPUT (rows/sec) │ Process 1B rows     │
│                     │                      │ overnight            │
├─────────────────────┼──────────────────────┼──────────────────────┤
│ Chat App            │ LATENCY (< 100ms)    │ Real-time feeling    │
│ (WhatsApp)          │                      │ for users            │
├─────────────────────┼──────────────────────┼──────────────────────┤
│ Payment Gateway     │ BOTH                 │ Fast response +      │
│ (Visa)              │                      │ 65K TPS peak         │
├─────────────────────┼──────────────────────┼──────────────────────┤
│ Log Aggregation     │ THROUGHPUT           │ Ingest millions of   │
│ (ELK/Splunk)        │                      │ events/sec           │
├─────────────────────┼──────────────────────┼──────────────────────┤
│ E-commerce Cart     │ LATENCY              │ Slow cart = abandoned│
│                     │                      │ purchases            │
└─────────────────────┴──────────────────────┴──────────────────────┘

Amazon found: every 100ms of added latency = 1% drop in sales.
Google found: 500ms slower = 20% drop in search traffic.
```

### Optimization Techniques

```
┌──────────────────────┬───────────────────────┬───────────────────┐
│ Technique            │ Latency Improvement   │ Throughput Impact  │
├──────────────────────┼───────────────────────┼───────────────────┤
│ Caching (Redis)      │ 10-100x faster        │ +++ (fewer DB hits)│
│ CDN                  │ -50 to -200ms         │ +++ (offload origin│
│ Connection Pooling   │ -5 to -20ms/req       │ ++ (reuse conns)  │
│ Async Processing     │ Respond immediately   │ +++ (decouple)    │
│ Compression (gzip)   │ -10 to -50ms          │ + (less bandwidth)│
│ HTTP/2 Multiplexing  │ -10 to -100ms         │ ++ (parallel)     │
│ Read Replicas        │ -2 to -10ms           │ +++ (scale reads) │
│ Denormalization      │ -5 to -20ms (no JOIN) │ + (more storage)  │
│ Batching             │ + (batch delay)       │ +++ (10-100x)     │
│ Horizontal Scaling   │ same per request      │ +++ (linear)      │
└──────────────────────┴───────────────────────┴───────────────────┘
```

---

## 3. CAP Theorem and Real-World Trade-offs

### Not Textbook — Real Decisions

The CAP theorem says a distributed system can guarantee at most **two of three**: Consistency, Availability, Partition Tolerance.

```
                    ┌─────────────────┐
                    │  CONSISTENCY    │
                    │  Every read     │
                    │  gets the latest│
                    │  write          │
                    └────────┬────────┘
                            ╱ ╲
                           ╱   ╲
                  CP      ╱     ╲      CA
               (banking) ╱       ╲  (single DB,
                        ╱ PICK 2  ╲  no partition)
                       ╱           ╲
                      ╱             ╲
         ┌───────────╱───┐     ┌─────╲──────────┐
         │ AVAILABILITY  │     │  PARTITION      │
         │ Every request │     │  TOLERANCE      │
         │ gets a        │     │  System works   │
         │ response      │     │  despite network│
         │               │     │  failures       │
         └───────────────┘     └────────────────┘

  In reality: network partitions WILL happen in distributed systems.
  P is mandatory → you're choosing between C and A:

  ┌─────────────┬─────────────────────────┬──────────────────────┐
  │ Choice      │ During Partition         │ Real Systems         │
  ├─────────────┼─────────────────────────┼──────────────────────┤
  │ CP          │ Return errors rather    │ ZooKeeper, MongoDB,  │
  │ Consistency │ than stale data          │ Spanner, etcd,       │
  │ + Partition │ "Better no answer than  │ HBase                │
  │             │ a wrong answer"          │                      │
  ├─────────────┼─────────────────────────┼──────────────────────┤
  │ AP          │ Return potentially      │ Cassandra, DynamoDB, │
  │ Availability│ stale data              │ CouchDB, Riak,       │
  │ + Partition │ "Better a stale answer  │ Couchbase             │
  │             │ than no answer"          │                      │
  └─────────────┴─────────────────────────┴──────────────────────┘
```

### When to Choose CP vs AP — Decision Framework

```
═══════════════════════════════════════════════════════════════════
  "Should I choose CP or AP?"
═══════════════════════════════════════════════════════════════════

  Ask yourself:
  ┌────────────────────────────────────────────────────┐
  │ What's WORSE for your users?                       │
  │                                                     │
  │   A) Seeing stale/wrong data     → Choose CP       │
  │   B) Seeing an error page        → Choose AP       │
  └────────────────────────────────────────────────────┘

  Examples:

  BANK ACCOUNT BALANCE:
    Wrong balance: user withdraws money they don't have → loss
    Error message: user waits, tries again later → mild annoyance
    → CP ✅ (correctness > uptime)

  SOCIAL MEDIA LIKE COUNT:
    Stale count (999 instead of 1001): nobody cares
    Error page: "Instagram is down" → millions of complaints
    → AP ✅ (uptime > precision)

  SHOPPING CART:
    Stale cart: item shows up again after deletion → minor annoyance
    Error page: user can't shop during Black Friday → lost revenue
    → AP ✅ (Amazon chose this for DynamoDB)

  MEDICAL RECORDS:
    Wrong dosage: patient harmed
    System down: doctor calls pharmacy manually
    → CP ✅ (correctness is life-critical)

  CONFIG/SERVICE DISCOVERY:
    Wrong config: service connects to wrong database → outage
    Config unavailable: service uses cached config → brief delay
    → CP ✅ (etcd, ZooKeeper chose this)
```

### Real-World CAP Example — E-Commerce Inventory

```
Scenario: 1 iPhone left in stock. Two users try to buy simultaneously.

═══════════════════════════════════════════════════════════════════
  CP APPROACH (Strong Consistency)
═══════════════════════════════════════════════════════════════════

  User A (New York)              User B (London)
       │                              │
       ▼                              ▼
  ┌──────────┐                   ┌──────────┐
  │ NY Node  │──── lock row ────►│ LDN Node │
  │          │   (sync write)    │          │
  │ stock: 1 │                   │ stock: 1 │
  │ → buy    │                   │ → wait.. │
  │ stock: 0 │                   │ → buy    │
  │ SUCCESS ✅│                   │ DENIED ❌ │
  └──────────┘                   └──────────┘
  
  Both nodes agree: stock = 0. No overselling ✅
  But: User B experienced 150ms+ delay (cross-Atlantic lock)
  If network partition: User B gets an ERROR (can't confirm stock)

═══════════════════════════════════════════════════════════════════
  AP APPROACH (Eventual Consistency)
═══════════════════════════════════════════════════════════════════

  User A (New York)              User B (London)
       │                              │
       ▼                              ▼
  ┌──────────┐                   ┌──────────┐
  │ NY Node  │  (async sync)     │ LDN Node │
  │ stock: 1 │                   │ stock: 1 │
  │ → buy    │                   │ → buy    │
  │ stock: 0 │                   │ stock: 0 │
  │ SUCCESS ✅│                   │ SUCCESS ✅│
  └──────────┘                   └──────────┘
  
  BOTH succeed! But only 1 iPhone exists → OVERSOLD ❌
  Must reconcile: send apology to one user, offer discount

  → For inventory with limited stock: CP is better
  → For "add to cart" (reservation, not purchase): AP is fine
```

### Consistency Spectrum

```
STRONG ◄────────────────────────────────────────────────► WEAK

┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│Linearizability│ │ Sequential   │ │   Causal     │ │  Eventual    │
│               │ │ Consistency  │ │ Consistency  │ │ Consistency  │
│               │ │              │ │              │ │              │
│ Real-time     │ │ Ops in some  │ │ Cause-effect │ │ All replicas │
│ order         │ │ total order  │ │ relationships│ │ converge     │
│               │ │              │ │ preserved    │ │ eventually   │
│               │ │              │ │              │ │              │
│ Google        │ │ ZooKeeper    │ │ MongoDB      │ │ DynamoDB     │
│ Spanner       │ │              │ │ (sessions)   │ │ Cassandra    │
│               │ │              │ │              │ │              │
│ Highest       │ │              │ │              │ │ Lowest       │
│ latency       │ │              │ │              │ │ latency      │
└──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
```

---

## 4. Architecture Patterns: Monoliths, Microservices, Event-Driven

### The Three Major Architecture Styles

```
═══════════════════════════════════════════════════════════════════
  1. MONOLITH — Everything in one deployable unit
═══════════════════════════════════════════════════════════════════

  ┌────────────────────────────────────────────────┐
  │               MONOLITH APPLICATION              │
  │                                                  │
  │  ┌──────────┐ ┌──────────┐ ┌──────────┐        │
  │  │   Auth   │ │  Orders  │ │ Payments │        │
  │  │  Module  │ │  Module  │ │  Module  │        │
  │  └────┬─────┘ └────┬─────┘ └────┬─────┘        │
  │       │            │            │               │
  │       └────────────┼────────────┘               │
  │                    │                             │
  │              ┌─────▼─────┐                      │
  │              │ Shared DB │                      │
  │              └───────────┘                      │
  │                                                  │
  │  Single WAR/JAR, single deployment pipeline     │
  │  All modules share the same process + memory    │
  └────────────────────────────────────────────────┘

  ✅ Pros:                         ❌ Cons:
  • Simple to develop initially    • Hard to scale individual parts
  • Easy to test end-to-end        • One bug can bring down everything
  • No network calls between       • Deployment = all or nothing
    modules (in-process calls)     • Tech stack locked (all Java/Python)
  • Simple deployment (one unit)   • Team coupling (merge conflicts)

═══════════════════════════════════════════════════════════════════
  2. MICROSERVICES — Each service is independent
═══════════════════════════════════════════════════════════════════

  ┌──────────┐    ┌──────────┐    ┌──────────┐
  │   Auth   │    │  Orders  │    │ Payments │
  │ Service  │    │ Service  │    │ Service  │
  │ (Java)   │    │ (Go)     │    │ (Node)   │
  │          │    │          │    │          │
  │ Own DB   │    │ Own DB   │    │ Own DB   │
  │ ┌──────┐ │    │ ┌──────┐ │    │ ┌──────┐ │
  │ │Postgres│ │    │ │ Mongo│ │    │ │Redis │ │
  │ └──────┘ │    │ └──────┘ │    │ └──────┘ │
  └────┬─────┘    └────┬─────┘    └────┬─────┘
       │               │               │
       └───────────────┼───────────────┘
                       │
                ┌──────▼──────┐
                │ API Gateway │
                └─────────────┘

  Communication: REST/gRPC (sync) or Kafka/RabbitMQ (async)

  ✅ Pros:                         ❌ Cons:
  • Scale services independently   • Distributed system complexity
  • Team autonomy (own deploys)    • Network latency between services
  • Polyglot (different langs/DBs) • Distributed transactions (hard!)
  • Fault isolation                • Operational overhead (K8s, etc.)
  • Smaller, focused codebases    • Data consistency challenges

═══════════════════════════════════════════════════════════════════
  3. EVENT-DRIVEN — Services communicate via events
═══════════════════════════════════════════════════════════════════

  ┌──────────┐  publish   ┌─────────────┐  consume  ┌──────────┐
  │  Order   │──────────►│  Event Bus  │──────────►│ Payment  │
  │ Service  │ "OrderPlaced" │ (Kafka /   │          │ Service  │
  └──────────┘           │  EventBridge)│          └──────────┘
                         │              │
                         │              │──────────►┌──────────┐
                         │              │          │Inventory │
                         │              │          │ Service  │
                         │              │          └──────────┘
                         │              │
                         │              │──────────►┌──────────┐
                         └──────────────┘          │ Notif.   │
                                                   │ Service  │
                                                   └──────────┘

  Order Service doesn't know or care who consumes the event.
  Adding a new consumer = zero changes to Order Service.

  ✅ Pros:                         ❌ Cons:
  • Loose coupling                 • Eventual consistency (not instant)
  • Easy to add new consumers      • Debugging: hard to trace event flow
  • Natural audit trail (event log)• Event schema evolution
  • Async = better throughput      • Message ordering challenges
  • Temporal decoupling            • Duplicate event handling (idempotency)
```

### Monolith → Microservices Migration Path

```
═══════════════════════════════════════════════════════════════════
  THE STRANGLER FIG PATTERN — Gradual Migration
═══════════════════════════════════════════════════════════════════

Phase 1: MONOLITH (where you start)
  ┌────────────────────────────────────┐
  │  [Auth] [Orders] [Pay] [Notif]    │ ← everything in one app
  │                ┌────┐              │
  │                │ DB │              │
  │                └────┘              │
  └────────────────────────────────────┘

Phase 2: EXTRACT first service (highest value / most independent)
  ┌───────────────────────────────┐    ┌──────────┐
  │  [Auth] [Orders] [Pay]       │    │  Notif.  │ ← extracted
  │              ┌────┐           │    │ Service  │
  │              │ DB │           │    │  ┌────┐  │
  │              └────┘           │    │  │ DB │  │
  └───────────────────────────────┘    │  └────┘  │
           │                           └──────────┘
           └──── API calls / events ────►

Phase 3: CONTINUE extracting
  ┌──────────────────────┐  ┌──────────┐  ┌──────────┐
  │  [Auth] [Orders]     │  │ Payment  │  │  Notif.  │
  │        ┌────┐        │  │ Service  │  │ Service  │
  │        │ DB │        │  │  ┌────┐  │  │  ┌────┐  │
  │        └────┘        │  │  │ DB │  │  │  │ DB │  │
  └──────────────────────┘  │  └────┘  │  │  └────┘  │
                            └──────────┘  └──────────┘

Phase 4: MONOLITH RETIRED
  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
  │   Auth   │  │  Orders  │  │ Payment  │  │  Notif.  │
  │ Service  │  │ Service  │  │ Service  │  │ Service  │
  └──────────┘  └──────────┘  └──────────┘  └──────────┘

  Timeline: months to years (not a big-bang rewrite!)
```

### Which Architecture Should You Choose?

```
┌──────────────────┬──────────────┬──────────────┬──────────────┐
│ Factor           │ Monolith     │ Microservices│ Event-Driven │
├──────────────────┼──────────────┼──────────────┼──────────────┤
│ Team size        │ < 10 devs    │ 10-100+ devs │ 10-100+ devs │
│ Complexity       │ Low-Medium   │ High         │ High         │
│ Deployment speed │ Minutes      │ Independent  │ Independent  │
│ Scalability      │ Vertical     │ Horizontal   │ Horizontal   │
│ Data consistency │ ACID (easy)  │ Eventual     │ Eventual     │
│ Latency          │ Lowest       │ Higher (net) │ Higher (async│
│ Fault isolation  │ None         │ Per-service  │ Per-service  │
│ Best for         │ Startups,    │ Large teams, │ Workflows,   │
│                  │ MVPs,        │ independent  │ IoT, real-   │
│                  │ simple apps  │ teams        │ time, CQRS   │
├──────────────────┼──────────────┼──────────────┼──────────────┤
│ Examples         │ Basecamp,    │ Netflix,     │ Uber,        │
│                  │ StackOverflow│ Amazon,      │ LinkedIn,    │
│                  │ (mostly)     │ Uber         │ EventBridge  │
└──────────────────┴──────────────┴──────────────┴──────────────┘

  RULE OF THUMB:
  ┌────────────────────────────────────────────────────────┐
  │ Start with a monolith. Extract microservices when:     │
  │                                                         │
  │ 1. Team grows beyond 2 pizza teams (> 8 devs)         │
  │ 2. Parts of the system need independent scaling        │
  │ 3. Deployment of one module breaks another             │
  │ 4. Different parts need different tech stacks          │
  │ 5. Release cycle is slowing down (merge conflicts)     │
  │                                                         │
  │ "If you can't build a well-structured monolith,        │
  │  what makes you think microservices is the answer?"    │
  │                              — Simon Brown              │
  └────────────────────────────────────────────────────────┘
```

### Event-Driven Patterns in Detail

```
═══════════════════════════════════════════════════════════════════
  PATTERN 1: EVENT NOTIFICATION (fire and forget)
═══════════════════════════════════════════════════════════════════

  Order Service ──"OrderPlaced"──► Email Service sends confirmation
                                  Inventory Service reduces stock
                                  Analytics Service logs event

  Publisher doesn't wait for response. Doesn't know who listens.

═══════════════════════════════════════════════════════════════════
  PATTERN 2: EVENT-CARRIED STATE TRANSFER
═══════════════════════════════════════════════════════════════════

  Customer Service publishes:
  {
    "type": "CustomerUpdated",
    "data": { "id": 123, "name": "Alice", "email": "a@b.com" }
  }

  Order Service stores a LOCAL COPY of customer data.
  → No need to call Customer Service for every order!
  → Reduces coupling and latency

═══════════════════════════════════════════════════════════════════
  PATTERN 3: EVENT SOURCING
═══════════════════════════════════════════════════════════════════

  Instead of storing CURRENT STATE, store ALL EVENTS:

  Traditional:  accounts table → { id: 1, balance: 900 }

  Event Sourced:
    Event 1: AccountCreated { id: 1 }
    Event 2: MoneyDeposited { amount: 1000 }
    Event 3: MoneyWithdrawn { amount: 100 }
    → Current balance = replay events = 0 + 1000 - 100 = 900

  Used by: Banking systems, Git (commit log), Blockchain

═══════════════════════════════════════════════════════════════════
  PATTERN 4: CQRS (Command Query Responsibility Segregation)
═══════════════════════════════════════════════════════════════════

  WRITES (Commands)              READS (Queries)
  ┌──────────────┐               ┌──────────────┐
  │ Command API  │               │  Query API   │
  │ (create,     │               │ (search,     │
  │  update,     │               │  list,       │
  │  delete)     │               │  report)     │
  └──────┬───────┘               └──────┬───────┘
         │                              │
         ▼                              ▼
  ┌──────────────┐  events  ┌──────────────┐
  │  Write DB    │────────►│  Read DB     │
  │ (normalized) │         │ (denormalized)│
  │ PostgreSQL   │         │ Elasticsearch │
  │              │         │ or Redis      │
  └──────────────┘         └──────────────┘

  Write DB optimized for consistency (ACID, normalized).
  Read DB optimized for queries (denormalized, indexed, cached).
  Sync via events (eventual consistency).
```

---

## 5. Designing for Scalability, Reliability & Availability from Day One

### The Three Pillars — Interconnected

```
              ┌──────────────────┐
              │   SCALABILITY    │
              │  "Can it grow?"  │
              └────────┬─────────┘
                       │
          ┌────────────┼────────────┐
          │            │            │
          ▼            │            ▼
  ┌──────────────┐     │     ┌──────────────┐
  │ AVAILABILITY │     │     │ RELIABILITY  │
  │ "Is it up?"  │     │     │"Does it work │
  │              │◄────┘     │  correctly?" │
  └──────────────┘           └──────────────┘

  SCALABILITY fuels AVAILABILITY:
    More nodes = more redundancy = higher uptime

  SCALABILITY enables RELIABILITY:
    More replicas = more copies of data = harder to lose

  But TENSION exists:
    More nodes = more things that can fail
    Strong consistency = higher latency = scalability bottleneck
```

### Scalability Checklist — Design from Day One

```
┌────────────────────────────────────────────────────────────┐
│           SCALABILITY CHECKLIST                             │
│                                                             │
│ STATELESS SERVICES ✅                                      │
│  ├── No server-side sessions (use JWT or Redis sessions)    │
│  ├── Any request can go to any server                       │
│  └── Enables horizontal auto-scaling                        │
│                                                             │
│ DATABASE STRATEGY ✅                                        │
│  ├── Read replicas for read-heavy workloads                 │
│  ├── Connection pooling (HikariCP: max 20-50 per pod)      │
│  ├── Caching hot data in Redis (reduce DB load 80%)        │
│  └── Sharding plan if table exceeds 100M+ rows             │
│                                                             │
│ ASYNC EVERYWHERE ✅                                         │
│  ├── Message queues for non-critical operations             │
│  │   (email, notifications, analytics)                      │
│  ├── Return 202 Accepted for long-running operations        │
│  └── Webhooks or polling for results                        │
│                                                             │
│ CACHING LAYERS ✅                                           │
│  ├── L1: Application-level (in-memory, Caffeine)            │
│  ├── L2: Distributed (Redis/Memcached)                      │
│  ├── L3: CDN (CloudFront/Cloudflare for static assets)     │
│  └── Cache invalidation strategy defined                    │
│                                                             │
│ API DESIGN ✅                                               │
│  ├── Pagination for list endpoints (cursor-based preferred)│
│  ├── Rate limiting per user/API key                         │
│  ├── Idempotency keys for write operations                  │
│  └── Versioned APIs (/api/v1/, /api/v2/)                   │
└────────────────────────────────────────────────────────────┘
```

### Availability Checklist — Design from Day One

```
┌────────────────────────────────────────────────────────────┐
│           AVAILABILITY CHECKLIST                            │
│                                                             │
│ REDUNDANCY ✅                                               │
│  ├── Minimum 3 replicas for every stateful service         │
│  ├── Multi-AZ deployment (survive AZ failure)              │
│  ├── Multi-region for global services (survive region)      │
│  └── No single points of failure (SPOF)                     │
│                                                             │
│ HEALTH CHECKS ✅                                            │
│  ├── Liveness probe: "is the process alive?"               │
│  ├── Readiness probe: "can it serve traffic?"              │
│  └── Deep health: DB connection, cache, downstream services│
│                                                             │
│ GRACEFUL DEGRADATION ✅                                     │
│  ├── Circuit breakers on all external calls (Resilience4j) │
│  ├── Fallback responses (cached data, default values)      │
│  ├── Feature flags to disable non-critical features        │
│  └── Bulkhead: isolate thread pools per dependency          │
│                                                             │
│ DEPLOYMENT SAFETY ✅                                        │
│  ├── Blue-green or canary deployments (no big-bang)        │
│  ├── Automated rollback on error rate spike                │
│  ├── Database migrations backward compatible               │
│  └── Feature flags for gradual rollout                      │
│                                                             │
│ MONITORING ✅                                               │
│  ├── P99 latency alerts (not just averages)                │
│  ├── Error rate > 0.1% → page on-call                      │
│  ├── Distributed tracing (Jaeger/X-Ray)                    │
│  └── Runbooks for common failure scenarios                  │
└────────────────────────────────────────────────────────────┘
```

### Reliability Checklist — Design from Day One

```
┌────────────────────────────────────────────────────────────┐
│           RELIABILITY CHECKLIST                             │
│                                                             │
│ DATA INTEGRITY ✅                                           │
│  ├── ACID transactions for financial/critical operations   │
│  ├── Idempotent APIs (safe to retry without side effects)  │
│  ├── Checksums for data at rest and in transit             │
│  └── Soft deletes (mark as deleted, don't actually delete) │
│                                                             │
│ FAILURE HANDLING ✅                                         │
│  ├── Retry with exponential backoff + jitter               │
│  ├── Dead-letter queues for failed messages                │
│  ├── Compensating transactions (saga pattern)              │
│  └── Timeout on EVERY external call (no infinite waits)    │
│                                                             │
│ TESTING ✅                                                  │
│  ├── Chaos engineering (kill random pods in staging)        │
│  ├── Load testing before every major release               │
│  ├── Integration tests with real dependencies              │
│  └── Disaster recovery drills (quarterly)                  │
│                                                             │
│ BACKUPS ✅                                                  │
│  ├── Automated daily backups with point-in-time recovery   │
│  ├── Cross-region backup replication                        │
│  ├── Test backup restoration monthly                        │
│  └── RTO/RPO defined and tested                             │
│       (Recovery Time Objective / Recovery Point Objective) │
└────────────────────────────────────────────────────────────┘
```

### Real-World Architecture: Designing a Ride-Sharing App

```
═══════════════════════════════════════════════════════════════════
  SYSTEM DESIGN: UBER-LIKE RIDE SHARING
═══════════════════════════════════════════════════════════════════

                    ┌─────────────────┐
  Riders ──────────►│   API Gateway   │◄────────── Drivers
  (mobile app)      │   (rate limit,  │            (mobile app)
                    │    auth, route)  │
                    └────────┬────────┘
                             │
          ┌──────────────────┼──────────────────┐
          ▼                  ▼                  ▼
  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
  │    Rider     │  │   Matching   │  │   Driver     │
  │   Service    │  │   Service    │  │   Service    │
  │              │  │              │  │              │
  │ • Profile    │  │ • Geospatial │  │ • Location   │
  │ • History    │  │   index      │  │   updates    │
  │ • Payment   │  │ • ETA calc   │  │ • Status     │
  │   methods    │  │ • Pricing    │  │ • Earnings   │
  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘
         │                 │                  │
         ▼                 ▼                  ▼
  ┌──────────┐    ┌────────────────┐  ┌──────────┐
  │PostgreSQL│    │ Redis (driver  │  │ Cassandra│
  │(user data│    │  locations +   │  │ (ride    │
  │  ACID)   │    │  geospatial)   │  │  history)│
  └──────────┘    └────────────────┘  └──────────┘

  Event Bus (Kafka):
  ┌──────────────────────────────────────────────────────┐
  │ Topics: ride.requested, ride.matched, ride.started,  │
  │         ride.completed, payment.processed             │
  │                                                       │
  │ Consumers: Notification, Analytics, Billing, Fraud   │
  └──────────────────────────────────────────────────────┘

  SCALABILITY:
  • Matching Service: scale horizontally by geo-region
  • Driver locations: Redis with geo-indexes (O(log N) search)
  • Ride history: Cassandra (write-heavy, horizontally scalable)

  AVAILABILITY:
  • Multi-AZ in each region
  • Circuit breakers between every service
  • Fallback: if Matching fails, use cached driver list

  RELIABILITY:
  • Payment via saga: reserve → charge → confirm
  • Idempotent ride requests (no duplicate rides)
  • Event sourcing for ride state (complete audit trail)
```

---

## 6. How to Think Through a System Design Interview

### The Framework (15-20 Minutes)

```
┌─────────────────────────────────────────────────────────────┐
│  MINUTE 0-3: CLARIFY REQUIREMENTS                           │
│                                                               │
│  Functional:                                                 │
│  • "What are the core features?"                             │
│  • "Who are the users?"                                      │
│  • "What's the read/write ratio?"                            │
│                                                               │
│  Non-functional:                                             │
│  • "How many users/requests per second?"                     │
│  • "What's acceptable latency?"                              │
│  • "Can we lose data? (durability)"                          │
│  • "What's the availability target? (99.9%? 99.99%?)"       │
│  • "Is eventual consistency acceptable?"                     │
└─────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────┐
│  MINUTE 3-5: BACK-OF-ENVELOPE ESTIMATION                    │
│                                                               │
│  • DAU (Daily Active Users)                                  │
│  • QPS (Queries Per Second) = DAU × avg_queries / 86400     │
│  • Storage = users × data_per_user × retention_period        │
│  • Bandwidth = QPS × avg_response_size                       │
│                                                               │
│  Example (URL shortener):                                    │
│  • 100M URLs created/day = ~1200 writes/sec                 │
│  • Read:write = 100:1 → 120K reads/sec                      │
│  • Storage: 100M × 500 bytes × 365 days × 5 years = ~91 TB │
└─────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────┐
│  MINUTE 5-10: HIGH-LEVEL DESIGN (draw the diagram)          │
│                                                               │
│  Client → CDN → LB → API Servers → Cache → Database         │
│                                                               │
│  Identify: which components? how do they communicate?        │
│  At this point: boxes and arrows, not implementation details │
└─────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────┐
│  MINUTE 10-18: DEEP DIVE (most important part!)             │
│                                                               │
│  Pick 2-3 components and go deep:                            │
│  • Database schema + indexing strategy                       │
│  • API design (endpoints, request/response format)           │
│  • Caching strategy (what to cache, TTL, invalidation)       │
│  • Scaling bottlenecks and how to address them               │
│  • Data partitioning / sharding strategy                     │
│  • Consistency model (CP or AP?)                             │
└─────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────┐
│  MINUTE 18-20: WRAP UP                                       │
│                                                               │
│  • Summarize trade-offs you made                             │
│  • Mention what you'd improve with more time                 │
│  • Discuss monitoring and alerting                           │
│  • Identify bottlenecks and scaling limits                   │
└─────────────────────────────────────────────────────────────┘
```

### How to Communicate Design Decisions

```
BAD: "I'll use Redis for caching."

GOOD: "The read-to-write ratio is 100:1 and we need sub-50ms
       response times. I'll add Redis as a cache layer with a
       60-second TTL and cache-aside pattern. This reduces DB
       load by ~80% for repeated reads. The trade-off is slightly
       stale data for up to 60 seconds, which is acceptable for
       a social media feed but wouldn't work for a bank balance."

STRUCTURE:
  1. WHAT: "I'll use [technology/pattern]"
  2. WHY: "Because [specific requirement]"
  3. HOW: "With [configuration/strategy]"
  4. TRADE-OFF: "The downside is [limitation], which is
                 acceptable because [reasoning]"
```

---

## 7. 15 System Design Concepts Cheat Sheet

```
┌────┬──────────────────────┬──────────────────────────────────────┐
│  # │ Concept              │ One-Liner                            │
├────┼──────────────────────┼──────────────────────────────────────┤
│  1 │ Load Balancing       │ Distribute traffic across servers    │
│  2 │ Caching              │ Store hot data in memory (Redis)     │
│  3 │ CDN                  │ Serve static assets from edge nodes  │
│  4 │ Database Replication │ Multiple copies for reads + failover │
│  5 │ Database Sharding    │ Split data across multiple DBs       │
│  6 │ Message Queues       │ Decouple producers and consumers     │
│  7 │ API Gateway          │ Single entry point for all clients   │
│  8 │ Rate Limiting        │ Protect services from overload       │
│  9 │ Circuit Breaker      │ Stop cascading failures              │
│ 10 │ Consistent Hashing   │ Distribute keys with minimal rehash │
│ 11 │ Heartbeat / Health   │ Detect and remove dead servers       │
│ 12 │ Idempotency          │ Safe retries without side effects    │
│ 13 │ CAP Theorem          │ Pick 2: Consistency, Availability, P │
│ 14 │ CQRS                 │ Separate read and write models       │
│ 15 │ Event Sourcing       │ Store events, not current state      │
└────┴──────────────────────┴──────────────────────────────────────┘
```

### Concept Relationship Map

```
                        ┌──────────────┐
                        │   Clients    │
                        └──────┬───────┘
                               │
                        ┌──────▼───────┐
                        │     CDN      │ ← static assets
                        └──────┬───────┘
                               │
                        ┌──────▼───────┐
                        │  API Gateway │ ← auth, rate limit, routing
                        └──────┬───────┘
                               │
                        ┌──────▼───────┐
                        │Load Balancer │ ← distribute traffic
                        └──┬───┬───┬───┘
                           │   │   │
                    ┌──────┘   │   └──────┐
                    ▼          ▼          ▼
              ┌─────────┐┌─────────┐┌─────────┐
              │Service A││Service B││Service C│ ← microservices
              └────┬────┘└────┬────┘└────┬────┘
                   │          │          │
              ┌────▼────┐    │     ┌────▼────┐
              │  Cache  │    │     │  Queue  │ ← async processing
              │ (Redis) │    │     │ (Kafka) │
              └────┬────┘    │     └────┬────┘
                   │         │          │
              ┌────▼─────────▼──────────▼────┐
              │        Database Layer          │
              │  ┌────────┐   ┌────────────┐  │
              │  │Primary │   │  Replicas   │  │ ← replication
              │  │(writes)│   │  (reads)    │  │
              │  └────────┘   └────────────┘  │
              │                                │
              │  Sharded across multiple nodes │ ← sharding
              └────────────────────────────────┘
```

---

## 8. Common Design Questions Mapped to Concepts

```
┌───────────────────────┬──────────────────────────────────────────┐
│ Design Question       │ Key Concepts to Discuss                  │
├───────────────────────┼──────────────────────────────────────────┤
│ URL Shortener         │ Hashing, Base62, Read replicas, Cache,   │
│ (TinyURL)             │ 301 vs 302 redirect, Analytics           │
├───────────────────────┼──────────────────────────────────────────┤
│ Chat System           │ WebSockets, Message queues, Fan-out,     │
│ (WhatsApp)            │ Presence, E2E encryption, Offline msgs   │
├───────────────────────┼──────────────────────────────────────────┤
│ Social Media Feed     │ Fan-out (on-write vs on-read), Timeline  │
│ (Twitter/Instagram)   │ cache, Celebrity problem, Ranking        │
├───────────────────────┼──────────────────────────────────────────┤
│ Video Streaming       │ CDN, Transcoding, Adaptive bitrate,     │
│ (YouTube/Netflix)     │ Chunked upload, Recommendations          │
├───────────────────────┼──────────────────────────────────────────┤
│ E-Commerce            │ Inventory (CP), Cart (AP), Payment saga, │
│ (Amazon)              │ Search (Elasticsearch), Recommendations  │
├───────────────────────┼──────────────────────────────────────────┤
│ Search Engine         │ Inverted index, Crawling, Ranking,       │
│ (Google)              │ Sharding, Fan-out queries, Caching       │
├───────────────────────┼──────────────────────────────────────────┤
│ Ride Sharing          │ Geospatial index, Real-time matching,    │
│ (Uber/Lyft)           │ ETA, Dynamic pricing, Event-driven       │
├───────────────────────┼──────────────────────────────────────────┤
│ File Storage          │ Chunking, Deduplication, Replication,    │
│ (Dropbox/S3)          │ Metadata DB, CDC, Sync conflicts         │
├───────────────────────┼──────────────────────────────────────────┤
│ Notification System   │ Priority queues, Rate limiting, Multiple │
│                       │ channels (push/email/SMS), Templates     │
├───────────────────────┼──────────────────────────────────────────┤
│ Rate Limiter          │ Token bucket, Sliding window, Redis,     │
│                       │ Distributed rate limiting, 429 responses │
├───────────────────────┼──────────────────────────────────────────┤
│ Payment System        │ Idempotency, Saga, Double-entry ledger,  │
│ (Stripe)              │ Reconciliation, PCI compliance           │
├───────────────────────┼──────────────────────────────────────────┤
│ Metrics/Monitoring    │ Time-series DB, Aggregation, Alerting,   │
│ (Datadog)             │ Sampling, Retention tiers                │
└───────────────────────┴──────────────────────────────────────────┘
```

### The 5 Questions That Solve 80% of System Design Interviews

```
┌─────────────────────────────────────────────────────────────┐
│                                                               │
│  1. "What's the read/write ratio?"                           │
│     → Determines caching strategy, DB choice, CQRS need     │
│                                                               │
│  2. "What happens during a network partition?" (CAP)         │
│     → CP for money/config, AP for social/content             │
│                                                               │
│  3. "What's the single biggest bottleneck?"                  │
│     → Usually the database; add cache, replicas, sharding    │
│                                                               │
│  4. "What if this component fails?"                          │
│     → Redundancy, circuit breakers, graceful degradation     │
│                                                               │
│  5. "How does this scale to 100x current load?"             │
│     → Horizontal scaling, partitioning, async processing     │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## Further Reading

| Topic | Document |
|-------|----------|
| [System Design Fundamentals](system-design-fundamentals.md) | Deep dive into Scalability, Availability, Reliability, Latency, Throughput, CAP |
| [Load Balancing](load-balancing.md) | Round Robin, Least Connections, Consistent Hashing, Health Checks |
| [Databases](databases.md) | SQL vs NoSQL, Replication, Sharding, Indexing |
| [Caching Strategies](caching-strategies.md) | Write-Through, Write-Back, LRU, LFU, Eviction Policies |
| [Rate Limiting](rate-limiting.md) | Token Bucket, Sliding Window, Distributed Rate Limiting |
| [Apache Kafka](kafka.md) | Topics, Partitions, Consumer Groups, Exactly-Once |
| [Senior Java Interview](senior-java-interview.md) | 20 Production-Grade Questions with Diagrams |

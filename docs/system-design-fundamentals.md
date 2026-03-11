# Fundamentals of System Design

> Scalability, availability, and reliability form the backbone of every large-scale system.
> Understanding latency, throughput, and the CAP theorem helps you make the right trade-offs between performance and consistency.

---

## Table of Contents

1. [Why Fundamentals Matter](#1-why-fundamentals-matter)
2. [Scalability](#2-scalability)
3. [Availability](#3-availability)
4. [Reliability](#4-reliability)
5. [Latency](#5-latency)
6. [Throughput](#6-throughput)
7. [CAP Theorem](#7-cap-theorem)
8. [Scalability vs Availability vs Reliability — How They Connect](#8-scalability-vs-availability-vs-reliability--how-they-connect)
9. [Trade-offs in Practice — Real-World Architecture Decisions](#9-trade-offs-in-practice--real-world-architecture-decisions)
10. [System Design Interview — Key Questions and How to Apply These Concepts](#10-system-design-interview--key-questions-and-how-to-apply-these-concepts)
11. [Quick Reference — Cheat Sheet](#11-quick-reference--cheat-sheet)

---

## 1. Why Fundamentals Matter

Every system design interview — and every real production system — boils down to six core concepts:

```
┌──────────────────────────────────────────────────────────────┐
│                   SYSTEM DESIGN FUNDAMENTALS                 │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│   │ Scalability  │  │ Availability │  │ Reliability  │      │
│   │   Can it     │  │   Is it      │  │  Does it     │      │
│   │   grow?      │  │   up?        │  │  work right? │      │
│   └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│          │                 │                 │               │
│   ┌──────┴───────┐  ┌──────┴───────┐  ┌──────┴───────┐      │
│   │   Latency    │  │  Throughput  │  │ CAP Theorem  │      │
│   │   How fast?  │  │  How much?   │  │ Pick two     │      │
│   └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

A recruiter might ask: *"Design a URL shortener"* or *"Design Instagram."* But really, they are testing whether you understand **how to balance** these six forces under constraints.

---

## 2. Scalability

### What Is It?

Scalability is the ability of a system to handle **increasing load** — more users, more data, more requests — without degrading performance.

> **Analogy**: A restaurant that seats 20 customers is fine for a neighborhood. When it goes viral on social media, can it serve 2,000? That's the scalability question.

### Two Types of Scaling

```
                    SCALING
                   ┌───┴───┐
           ┌───────┤       ├───────┐
           ▼                       ▼
   VERTICAL (Scale UP)     HORIZONTAL (Scale OUT)
   ┌─────────────────┐    ┌─────────────────┐
   │ Bigger machine  │    │ More machines   │
   │ More CPU / RAM  │    │ Behind a load   │
   │ More disk       │    │ balancer        │
   │                 │    │                 │
   │ Single server   │    │ Cluster of      │
   │ ┌─────────────┐ │    │ servers         │
   │ │ 128 GB RAM  │ │    │ ┌──┐ ┌──┐ ┌──┐ │
   │ │ 64 cores    │ │    │ │S1│ │S2│ │S3│ │
   │ │ 10 TB SSD   │ │    │ └──┘ └──┘ └──┘ │
   │ └─────────────┘ │    │                 │
   └─────────────────┘    └─────────────────┘
```

| Aspect | Vertical Scaling (Scale Up) | Horizontal Scaling (Scale Out) |
|--------|---------------------------|-------------------------------|
| **How** | Upgrade to a bigger machine | Add more machines |
| **Cost** | Expensive (high-end hardware) | Cheaper (commodity hardware) |
| **Limit** | Physical ceiling (you can't add infinite RAM) | Virtually unlimited |
| **Complexity** | Simple (one machine) | Complex (distributed systems) |
| **Downtime** | Often needed to upgrade | Zero downtime (add nodes live) |
| **Fault tolerance** | Single point of failure | Built-in redundancy |

### Real-World Examples

#### Netflix — Horizontal Scaling

Netflix serves **250M+ subscribers** across **190 countries**. A single server can never handle that.

```
User (Brazil) ──→ CDN Edge (São Paulo) ──→ Origin (AWS us-east-1)
User (Japan)  ──→ CDN Edge (Tokyo)     ──→ Origin (AWS ap-northeast-1)
User (UK)     ──→ CDN Edge (London)    ──→ Origin (AWS eu-west-1)
```

- **Content delivery**: Thousands of CDN edge servers worldwide
- **Microservices**: 1,000+ microservices, each horizontally scalable independently
- **Data tier**: Cassandra (scales horizontally by adding nodes)

#### Instagram — Database Sharding (Horizontal)

Instagram stores **2 billion+ photos**. A single PostgreSQL server cannot hold them all.

```
User ID 1-100M     ──→ Shard 1  (DB Server A)
User ID 100M-200M  ──→ Shard 2  (DB Server B)
User ID 200M-300M  ──→ Shard 3  (DB Server C)
         ...
User ID 1.9B-2B    ──→ Shard 20 (DB Server T)
```

Each shard holds a slice of the data. When more users join, add more shards.

#### Slack — Vertical → Horizontal Migration

Slack initially ran on a single MySQL server (vertical). As they grew to millions of users, they:
1. Moved to **sharded MySQL** (one database per workspace)
2. Added **read replicas** for heavy read workloads
3. Introduced **Vitess** for MySQL horizontal scaling

### Scalability Patterns

| Pattern | How It Helps | Example |
|---------|-------------|---------|
| **Load Balancing** | Distribute requests across servers | AWS ALB, Nginx, HAProxy |
| **Database Sharding** | Split data across multiple DB instances | Instagram (user-ID based shards) |
| **Read Replicas** | Offload reads to copies of the primary DB | Amazon RDS read replicas |
| **Caching** | Reduce DB load by storing hot data in memory | Redis, Memcached |
| **CDN** | Serve static assets from edge locations | CloudFront, Akamai |
| **Message Queues** | Decouple producers from consumers | Kafka, RabbitMQ, SQS |
| **Microservices** | Scale individual services independently | Netflix, Uber |
| **Auto-scaling** | Add/remove servers based on demand | AWS Auto Scaling Groups |

### Key Metrics to Monitor

```
Scalability Health Dashboard:
┌──────────────────────────────────────────┐
│  Request Rate:     12,500 req/sec   ✅    │
│  CPU Utilization:  68%              ✅    │
│  Memory Usage:     72%              ⚠️    │
│  DB Connections:   450/500          ⚠️    │
│  Response Time:    45ms (p99)       ✅    │
│  Error Rate:       0.02%            ✅    │
│  Queue Depth:      1,200            ⚠️    │
└──────────────────────────────────────────┘
```

---

## 3. Availability

### What Is It?

Availability measures the **percentage of time** a system is operational and accessible. It's usually expressed in "nines."

> **Analogy**: A hospital emergency room should be available 24/7/365. If it closes for maintenance every Monday, that's low availability. Your e-commerce site during Black Friday must be the same.

### The Nines of Availability

| Availability | Downtime/Year | Downtime/Month | Downtime/Week | Real-World Target |
|-------------|---------------|----------------|---------------|-------------------|
| **99%** (two nines) | 3.65 days | 7.3 hours | 1.68 hours | Internal tools |
| **99.9%** (three nines) | 8.77 hours | 43.8 minutes | 10.1 minutes | Business apps |
| **99.95%** | 4.38 hours | 21.9 minutes | 5 minutes | E-commerce |
| **99.99%** (four nines) | 52.6 minutes | 4.38 minutes | 1.01 minutes | Financial systems |
| **99.999%** (five nines) | 5.26 minutes | 26.3 seconds | 6 seconds | Telecom, healthcare |

### How Availability Is Calculated

```
                    Uptime
Availability = ─────────────────────
               Uptime + Downtime


Example: System was up 8,750 hours out of 8,760 hours in a year
         = 8750 / 8760
         = 99.886% ≈ 99.9%
```

### Serial vs Parallel Availability

When components are connected in **series** (both must work):

```
[Service A: 99.9%] ──→ [Service B: 99.9%]

Total = 99.9% × 99.9% = 99.8%    ← LOWER than either!
```

When components are connected in **parallel** (either can serve):

```
┌─ [Service A: 99.9%] ─┐
│                       │ ──→ Output
└─ [Service B: 99.9%] ─┘

Total = 1 - (0.001 × 0.001) = 1 - 0.000001 = 99.9999%   ← MUCH HIGHER!
```

**Key insight**: Adding **redundancy (parallel)** dramatically increases availability. Adding **dependencies (serial)** decreases it.

### Real-World Examples

#### AWS S3 — 99.999999999% Durability, 99.99% Availability

```
Your File Upload
      │
      ▼
┌──────────────────────┐
│     S3 API Gateway   │    ← Receives upload
└──────────┬───────────┘
           │
    ┌──────┴──────┐
    ▼             ▼
┌────────┐   ┌────────┐
│ AZ-1   │   │ AZ-2   │   ← Replicated across Availability Zones
│ Copy 1 │   │ Copy 2 │
└────────┘   └────────┘
                  │
                  ▼
             ┌────────┐
             │ AZ-3   │   ← Third copy in another AZ
             │ Copy 3 │
             └────────┘
```

- Data is replicated to **at least 3 Availability Zones**
- Each AZ is a physically separate data center
- If one AZ burns down, data is still safe in 2 others

#### Google Search — Multi-Layer Redundancy

```
User Query
    │
    ▼
┌─────────────┐
│  DNS (BGP   │    ← Routes to nearest data center
│  Anycast)   │       Multiple DNS providers for redundancy
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Load        │    ← Google Front End (GFE)
│ Balancer    │       Distributes across thousands of servers
└──────┬──────┘
       │
  ┌────┴────┐
  ▼         ▼
┌────┐   ┌────┐
│ DC1│   │ DC2│    ← Multiple data centers per region
└────┘   └────┘       Automatic failover
```

Google achieves **~99.999%** availability for Search by:
- Multiple data centers per region
- Automatic failover in < 1 second
- Canary deployments (roll out to 1% of servers first)
- Graceful degradation (show cached results if backend slow)

#### GitHub — The 2018 Outage (What Happens When Availability Fails)

In October 2018, GitHub experienced a **24-hour degraded service** because:
1. A 43-second network partition between US East data centers
2. MySQL primary became inconsistent with replicas
3. They had to **reprocess 5 hours of database events** to restore consistency

Lesson: Even with redundancy, **split-brain scenarios** can cause extended outages.

### Availability Patterns

| Pattern | How It Works | Example |
|---------|-------------|---------|
| **Active-Active** | Multiple instances serve traffic simultaneously | Google Search (multiple DCs) |
| **Active-Passive** | Standby takes over when primary fails | Traditional DB failover |
| **Load Balancing** | Distribute across healthy instances | AWS ALB health checks |
| **Health Checks** | Continuously monitor instance health | Kubernetes liveness probes |
| **Circuit Breaker** | Stop calling failing services | Netflix Hystrix |
| **Graceful Degradation** | Serve reduced functionality during outages | Netflix shows cached content |
| **Multi-Region** | Deploy across geographic regions | AWS multi-region with Route 53 |
| **Blue-Green Deployment** | Two identical environments; switch traffic | Zero-downtime deploys |

---

## 4. Reliability

### What Is It?

Reliability is the probability that a system **performs its intended function correctly** over a given period, even when things go wrong (hardware failures, software bugs, human errors).

> **Analogy**: Your car starting every morning is reliability. A car that starts but sometimes turns left when you steer right is *available* but not *reliable*.

### Availability vs Reliability

| Aspect | Availability | Reliability |
|--------|-------------|-------------|
| **Question** | Is the system up? | Is it working correctly? |
| **Metric** | % uptime | Mean Time Between Failures (MTBF) |
| **Focus** | Reachability | Correctness |
| **Example** | Server responds to requests | Server returns correct data |

A system can be **available but not reliable**:
- A database that is online (available) but returns stale data (unreliable)
- An API that responds with 200 OK but gives wrong results

A system can be **reliable but not available**:
- A perfectly working server that goes down for 2 hours for maintenance

### Key Reliability Metrics

```
MTBF (Mean Time Between Failures)
   = Total Operating Time / Number of Failures
   Example: Server ran for 8,760 hours with 2 failures
            MTBF = 8,760 / 2 = 4,380 hours

MTTR (Mean Time To Recovery)
   = Total Downtime / Number of Failures
   Example: 2 failures, total downtime = 4 hours
            MTTR = 4 / 2 = 2 hours

Availability = MTBF / (MTBF + MTTR)
             = 4,380 / (4,380 + 2)
             = 99.95%
```

### Types of Failures

```
┌──────────────────────────────────────────────────┐
│                FAILURE TAXONOMY                   │
├──────────────────────────────────────────────────┤
│                                                  │
│  1. HARDWARE FAILURES                            │
│     ├── Disk failure (RAID, replication)          │
│     ├── Server crash (redundant nodes)            │
│     ├── Network partition (multi-path)            │
│     └── Power outage (UPS, generators)            │
│                                                  │
│  2. SOFTWARE FAILURES                            │
│     ├── Memory leaks (monitoring + restart)       │
│     ├── Deadlocks (timeouts, circuit breakers)    │
│     ├── Cascading failures (bulkheads)            │
│     └── Bug in deployment (canary releases)       │
│                                                  │
│  3. HUMAN ERRORS (largest source: ~70%)          │
│     ├── Misconfiguration (IaC, peer review)       │
│     ├── Wrong deployment (blue-green, rollback)   │
│     ├── Accidental deletion (backups, soft delete) │
│     └── Security breach (least privilege, audit)  │
│                                                  │
└──────────────────────────────────────────────────┘
```

### Real-World Examples

#### Amazon DynamoDB — Reliability Through Replication

```
Write Request
     │
     ▼
┌──────────┐
│  Router  │
└────┬─────┘
     │
     ├───────────────────┐───────────────────┐
     ▼                   ▼                   ▼
┌─────────┐        ┌─────────┐        ┌─────────┐
│ Node A  │        │ Node B  │        │ Node C  │
│ (Leader)│──sync──│(Follower)│──sync──│(Follower)│
│         │        │         │        │         │
│ AZ-1    │        │ AZ-2    │        │ AZ-3    │
└─────────┘        └─────────┘        └─────────┘

Write is acknowledged after 2 of 3 nodes confirm (quorum).
If Node A fails, Node B or C is promoted to leader.
```

- **Quorum writes**: Data is confirmed after majority of replicas acknowledge
- **Automatic leader election**: If leader fails, a follower takes over in seconds
- **Result**: 99.999% availability with strong reliability guarantees

#### Chaos Engineering — Netflix's Chaos Monkey

Netflix **intentionally kills production servers** to ensure reliability:

```
┌─────────────────────────────────────────┐
│         NETFLIX SIMIAN ARMY             │
├─────────────────────────────────────────┤
│                                         │
│  Chaos Monkey    → Kills random servers │
│  Latency Monkey  → Adds artificial lag  │
│  Chaos Gorilla   → Kills entire AZ      │
│  Chaos Kong      → Kills entire region  │
│                                         │
│  Philosophy: "If you want to find       │
│  weaknesses, break things in            │
│  production on a Tuesday morning,       │
│  not at 3 AM on Saturday."              │
│                                         │
└─────────────────────────────────────────┘
```

#### Banking Systems — ACID for Reliability

When you transfer $500 from Account A to Account B:

```
BEGIN TRANSACTION
  1. Debit  Account A: $1000 → $500    ← Atomicity: both happen
  2. Credit Account B: $200  → $700    ← or neither happens
COMMIT

If step 2 fails → ROLLBACK step 1
Money is never lost or created (Consistency)
```

### Reliability Patterns

| Pattern | Purpose | Example |
|---------|---------|---------|
| **Replication** | Survive node failures | DynamoDB 3-way replication |
| **Checksums** | Detect data corruption | S3 MD5 verification |
| **Idempotency** | Safe retries | Stripe payment API (idempotency keys) |
| **Transactions (ACID)** | Data correctness | Banking transfers |
| **Chaos Engineering** | Find weaknesses proactively | Netflix Chaos Monkey |
| **Backups + Point-in-Time Recovery** | Recover from data loss | AWS RDS automated backups |
| **Immutable Infrastructure** | Prevent configuration drift | Docker images, Terraform |
| **Saga Pattern** | Distributed transactions with compensation | Uber ride booking across services |

---

## 5. Latency

### What Is It?

Latency is the **time it takes** for a single request to travel from client to server and back. It's the delay the user *feels*.

> **Analogy**: Ordering coffee. Latency is the time from when you order to when the barista hands you the cup. It's not about how many coffees the shop makes per hour — that's throughput.

### Latency Numbers Every Engineer Should Know

```
┌─────────────────────────────────────────────────────────────────┐
│          LATENCY NUMBERS (approximate, 2024)                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  L1 cache reference .................. 0.5   ns                 │
│  Branch mispredict ................... 5     ns                 │
│  L2 cache reference .................. 7     ns                 │
│  Mutex lock/unlock ................... 25    ns                 │
│  Main memory reference ............... 100   ns                 │
│                                                                 │
│  ──── 1 μs (1,000 ns) ────────────────────────────────────────  │
│                                                                 │
│  Compress 1KB with Snappy ............ 3     μs                 │
│  Send 1KB over 1 Gbps network ........ 10    μs                 │
│  Read 4KB randomly from SSD .......... 150   μs                 │
│  Read 1 MB sequentially from memory .. 250   μs                 │
│  Round trip within same datacenter ... 500   μs                 │
│                                                                 │
│  ──── 1 ms (1,000 μs) ────────────────────────────────────────  │
│                                                                 │
│  Read 1 MB sequentially from SSD ..... 1     ms                 │
│  Read 1 MB sequentially from disk .... 20    ms                 │
│  Send packet CA → Netherlands → CA ... 150   ms                 │
│                                                                 │
│  ──── 1 s (1,000 ms) ─────────────────────────────────────────  │
│                                                                 │
│  KEY TAKEAWAYS:                                                 │
│  • Memory is 100x faster than SSD                               │
│  • SSD is 20x faster than spinning disk                         │
│  • Network round trip within DC: 0.5ms                          │
│  • Cross-continent round trip: 150ms                            │
│  • Reads are faster than writes                                 │
│  • Sequential access is faster than random access               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Measuring Latency — Percentiles

**Average** latency is misleading. Use **percentiles**:

```
Example: 100 requests with response times (ms):
  Most: 20-50ms
  Some: 100-200ms
  Tail: 1 request at 5,000ms

Average = 85ms    ← Looks fine!
p50     = 40ms    ← 50% of users experience ≤ 40ms
p95     = 200ms   ← 95% of users experience ≤ 200ms
p99     = 1,200ms ← 1% of users wait > 1 second!
p99.9   = 5,000ms ← That one terrible request

                    Users affected
          ┌────────────────────────┐
  p50     │████████████████████████│  50%
  p95     │████████████████████    │  5%
  p99     │████                   │  1%
  p99.9   │█                      │  0.1%
          └────────────────────────┘
```

**Why p99 matters**: If you have 100M users, p99 = 1M users having a bad experience.

Amazon found that **every 100ms of latency cost them 1% in sales**.

### Real-World Examples

#### Google — < 200ms Search Results

```
User types "best coffee shop"
     │
     ▼  (~0ms)
┌──────────────┐
│  DNS Lookup  │ ← Pre-resolved via Chrome DNS prefetch
└──────┬───────┘
       │  (~10ms)
       ▼
┌──────────────┐
│  TLS Handshake│ ← Session resumption (0-RTT with TLS 1.3)
└──────┬───────┘
       │  (~20ms)
       ▼
┌──────────────────────────┐
│  Google Web Server (GWS) │ ← Nearest data center via Anycast
└──────┬───────────────────┘
       │
  ┌────┴────────────────┐
  │  Fan-out to 1000+   │ ← Parallel search across index shards
  │  index servers      │    Each shard: ~5-10ms
  └────┬────────────────┘
       │  (~50ms)
       ▼
┌──────────────┐
│  Merge +     │ ← Combine, rank, personalize
│  Rank        │
└──────┬───────┘
       │  (~20ms)
       ▼
┌──────────────┐
│  Render HTML │ ← Send response
└──────────────┘

Total: ~200ms end-to-end for billions of queries/day
```

**How they achieve low latency:**
- Serve from the **nearest data center** (BGP Anycast)
- **Fan-out** queries to thousands of servers in parallel
- **Tail-at-scale**: Send redundant requests to mitigate slow servers
- **Caching**: Common queries served from in-memory cache

#### Uber — Real-Time Ride Matching (< 100ms)

```
Rider Request                    Driver Locations
     │                                │
     ▼                                ▼
┌──────────┐                   ┌──────────────┐
│ API      │                   │ Location     │
│ Gateway  │                   │ Service      │
└────┬─────┘                   │ (in-memory   │
     │                         │  geospatial  │
     ▼                         │  index)      │
┌──────────────┐               └──────┬───────┘
│ Matching     │◄──── query ──────────┘
│ Service      │
│ • Find nearby│   ← Geospatial query: ~5ms
│ • ETA calc   │   ← Routing engine: ~20ms
│ • Pricing    │   ← Dynamic pricing: ~10ms
│ • Assign     │   ← Assignment: ~5ms
└──────────────┘
     │
     ▼
Total: ~40-80ms to match a rider with a driver
```

### Latency Optimization Techniques

| Technique | Latency Saved | Example |
|-----------|--------------|---------|
| **Caching** | 10-100x | Redis: 0.5ms vs DB: 5-50ms |
| **CDN** | 50-200ms | Static assets served from edge |
| **Connection pooling** | 5-20ms per request | Pre-established DB connections |
| **Async processing** | Return immediately | Queue the work, respond with 202 |
| **Data locality** | 50-150ms | Geo-replicated databases |
| **Compression** | 10-50ms | gzip/brotli for HTTP responses |
| **Protocol optimization** | 10-100ms | HTTP/2 multiplexing, QUIC |
| **Read replicas** | 2-10ms | Read from nearest replica |
| **Denormalization** | 5-20ms | Avoid JOINs by pre-computing |
| **Tail-at-scale** | p99 reduction | Hedged/tied requests (Google) |

---

## 6. Throughput

### What Is It?

Throughput is the **amount of work** a system can handle in a given time period — requests per second (RPS), transactions per second (TPS), or bytes per second.

> **Analogy**: Latency is how fast one car drives through a tunnel. Throughput is how many cars pass through the tunnel per hour. A wider tunnel (more lanes) increases throughput even if each car's speed stays the same.

### Latency vs Throughput

```
       Latency                    Throughput
    ┌───────────┐              ┌───────────────┐
    │           │              │               │
    │  Single   │              │  Total work   │
    │  request  │              │  per unit     │
    │  time     │              │  of time      │
    │           │              │               │
    │  "How     │              │  "How much    │
    │   fast?"  │              │   can it      │
    │           │              │   handle?"    │
    └───────────┘              └───────────────┘

 Can be LOW latency, LOW throughput:
   → A Lamborghini on an empty single-lane road

 Can be HIGH latency, HIGH throughput:
   → A cargo ship: slow but carries 20,000 containers

 Goal: LOW latency AND HIGH throughput
   → A 10-lane highway with 200 km/h speed limit
```

### Throughput Numbers in Practice

| System | Throughput | Scale |
|--------|-----------|-------|
| Single PostgreSQL | 10,000-50,000 TPS | Small-medium apps |
| Single Redis | 100,000+ ops/sec | Caching layer |
| Single Kafka broker | 100,000-200,000 msgs/sec | Event streaming |
| Google Search | 100,000+ queries/sec | Global search |
| Visa payment network | 65,000 TPS (peak) | Global payments |
| Twitter firehose | 500M+ tweets/day (~6,000/sec) | Social media |
| WhatsApp | 100B+ messages/day | Messaging |

### Real-World Examples

#### Visa — 65,000 TPS at Peak

```
Black Friday Traffic:
  Normal:  2,000 TPS ──────────┐
  Peak:   65,000 TPS ──────────┤ 32.5x spike!
                               │
  ┌────────────────────────────┘
  │
  │  How Visa handles it:
  │
  │  1. VisaNet: Private fiber-optic network
  │     (not public internet)
  │
  │  2. Active-Active data centers
  │     East US + West US (simultaneous)
  │
  │  3. In-memory processing
  │     Authorization decision in < 1 second
  │
  │  4. Connection pooling
  │     Pre-established links to 15,000+ banks
  │
  │  5. Capacity planning
  │     Provision for 4x projected peak
```

#### Kafka at LinkedIn — 7 Trillion Messages/Day

```
LinkedIn Kafka Deployment:
  ┌────────────────────────────────────────┐
  │  Clusters:        100+                 │
  │  Brokers:         4,000+               │
  │  Topics:          100,000+             │
  │  Messages/day:    7,000,000,000,000    │
  │  Peak:            13 million msgs/sec  │
  │                                        │
  │  Use cases:                            │
  │  • Activity tracking (page views)      │
  │  • Metrics aggregation                 │
  │  • Log aggregation                     │
  │  • News feed generation                │
  │  • Notifications                       │
  └────────────────────────────────────────┘
```

### Improving Throughput

| Strategy | How It Works | Throughput Gain |
|----------|-------------|----------------|
| **Horizontal scaling** | Add more servers | Linear (N servers ≈ N× throughput) |
| **Batching** | Group multiple operations | 10-100× (Kafka batch produces) |
| **Async processing** | Queue work, respond immediately | Decouples request from processing |
| **Connection pooling** | Reuse DB/HTTP connections | 2-5× |
| **Compression** | Send less data over the wire | 2-10× for text-heavy payloads |
| **Partitioning** | Parallel processing across partitions | Kafka: more partitions = more consumers |
| **Caching** | Avoid repeated computation | 10-100× for cache hits |
| **Read replicas** | Distribute read load | N replicas ≈ N× read throughput |

---

## 7. CAP Theorem

### What Is It?

The **CAP theorem** (Brewer's theorem, 2000) states that a distributed data store can only guarantee **two out of three** properties simultaneously:

```
                 ┌───────────────┐
                 │  Consistency  │
                 │  (C)          │
                 └───────┬───────┘
                        ╱ ╲
                       ╱   ╲
           ┌──────────╱─────╲──────────┐
           │    CP   ╱       ╲   CA    │
           │        ╱   YOU   ╲        │
           │       ╱   CAN'T   ╲       │
           │      ╱   HAVE ALL  ╲      │
           │     ╱     THREE     ╲     │
           │    ╱                 ╲    │
           └───╱───────────────────╲───┘
              ╱         AP          ╲
   ┌─────────╱───┐            ┌─────╲────────┐
   │ Availability │            │  Partition   │
   │ (A)          │            │  Tolerance   │
   │              │            │  (P)         │
   └──────────────┘            └──────────────┘
```

### The Three Properties Explained

| Property | Meaning | Real-World Analogy |
|----------|---------|-------------------|
| **Consistency (C)** | Every read receives the **most recent write** or an error. All nodes see the same data at the same time. | Everyone looking at the same bank account balance sees the same number |
| **Availability (A)** | Every request receives a **non-error response**, without guaranteeing the data is the most recent. | The ATM always gives you *some* balance — even if slightly outdated |
| **Partition Tolerance (P)** | The system continues to operate despite **network partitions** (messages lost or delayed between nodes). | The system works even when the network cable between data centers is cut |

### Why You Must Choose P

In any real distributed system, **network partitions will happen** (cables get cut, switches fail, packets get lost). So `P` is not optional — you're really choosing between:

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│   Network partition happens. You have TWO choices:          │
│                                                             │
│   ┌─────────────────────────────┐                           │
│   │ CHOICE 1: CP                │                           │
│   │ (Consistency + Partition)    │                           │
│   │                             │                           │
│   │ Refuse to serve requests    │                           │
│   │ until partition heals to    │                           │
│   │ guarantee consistent data.  │                           │
│   │                             │                           │
│   │ User sees: ERROR / TIMEOUT  │                           │
│   └─────────────────────────────┘                           │
│                                                             │
│   ┌─────────────────────────────┐                           │
│   │ CHOICE 2: AP                │                           │
│   │ (Availability + Partition)   │                           │
│   │                             │                           │
│   │ Keep serving requests with  │                           │
│   │ potentially stale data.     │                           │
│   │ Reconcile when partition    │                           │
│   │ heals.                      │                           │
│   │                             │                           │
│   │ User sees: STALE DATA       │                           │
│   └─────────────────────────────┘                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Real-World CAP Decisions

| System | CAP Choice | Why |
|--------|-----------|-----|
| **Google Spanner** | CP | Financial data must be consistent. Uses TrueTime (atomic clocks + GPS) to achieve global consistency |
| **Amazon DynamoDB** | AP (default) | Shopping cart must always be available. Eventual consistency is acceptable — you might see an item you removed briefly |
| **Apache ZooKeeper** | CP | Configuration coordination must be consistent. Rather go offline than serve wrong config |
| **Apache Cassandra** | AP (tunable) | Social media feeds: better to show a slightly old feed than show nothing |
| **MongoDB** | CP (default) | Document store with strong consistency; unavailable during leader election |
| **CockroachDB** | CP | SQL database designed for global consistency (similar to Spanner) |
| **Couchbase** | AP | High availability for real-time applications |
| **etcd** | CP | Kubernetes config store must be consistent (uses Raft consensus) |

### Consistency Models Deep Dive

CAP's "Consistency" is binary (strong or not), but in practice there's a **spectrum**:

```
STRONG ◄──────────────────────────────────────────► WEAK

┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐
│ Lineariz-  │  │ Sequential │  │ Causal     │  │ Eventual   │
│ ability    │  │ Consistency│  │ Consistency│  │ Consistency│
│            │  │            │  │            │  │            │
│ Real-time  │  │ Operations │  │ Causally   │  │ Eventually │
│ order      │  │ appear in  │  │ related    │  │ all nodes  │
│ preserved  │  │ some total │  │ ops are    │  │ converge   │
│            │  │ order      │  │ ordered    │  │            │
│ Example:   │  │            │  │            │  │ Example:   │
│ Spanner    │  │ Example:   │  │ Example:   │  │ DynamoDB   │
│            │  │ ZooKeeper  │  │ MongoDB    │  │ (default)  │
│ Latency:   │  │            │  │ causal     │  │            │
│ HIGH       │  │ Latency:   │  │            │  │ Latency:   │
│            │  │ MEDIUM     │  │ Latency:   │  │ LOW        │
│            │  │            │  │ LOW-MED    │  │            │
└────────────┘  └────────────┘  └────────────┘  └────────────┘
```

### CAP Theorem Example — Social Media "Like" Counter

```
Scenario: User in New York "likes" a post. User in London views the post.

── CP Approach (Strong Consistency) ──────────────────────

NY User clicks "Like"
    │
    ▼
┌─────────┐    sync write    ┌─────────┐
│ NY Node │ ──────────────── │ LDN Node│    (150ms cross-Atlantic)
│ Likes:  │                  │ Likes:  │
│ 1001    │                  │ 1001    │
└─────────┘                  └─────────┘
                             London user sees: 1001 ✅

But if network between NY and London is down:
→ NY Node refuses writes (503 error) until connection restored
→ OR London Node returns error
→ User experience: "Service Unavailable" ❌


── AP Approach (Eventual Consistency) ────────────────────

NY User clicks "Like"
    │
    ▼
┌─────────┐                  ┌─────────┐
│ NY Node │    async sync     │ LDN Node│
│ Likes:  │ ···(eventual)··· │ Likes:  │
│ 1001    │                  │ 1000    │  ← Stale for a few seconds
└─────────┘                  └─────────┘
                             London user sees: 1000 (briefly)
                             Then: 1001 ✅ (after sync)

If network is down:
→ Both nodes keep serving
→ NY: 1001, London: 1000
→ When network heals: reconcile (last-write-wins or CRDT merge)
→ User experience: Always works, briefly inconsistent ⚠️
```

**Facebook/Instagram chose AP** for likes and comments because showing a like count that's 1 off for 2 seconds is better than showing an error page.

**Banking chose CP** because showing $100 when the balance is really $0 would be catastrophic.

### PACELC — The Extended CAP

The **PACELC theorem** extends CAP: even when there's **no partition** (normal operation), you still have to choose between **latency** and **consistency**:

```
┌───────────────────────────────────────────────────────┐
│                     PACELC                             │
│                                                       │
│  IF there is a Partition (P):                         │
│     Choose between Availability (A) and               │
│     Consistency (C)                                   │
│                                                       │
│  ELSE (normal operation, E):                          │
│     Choose between Latency (L) and                    │
│     Consistency (C)                                   │
│                                                       │
│  ┌──────────┬──────────┬──────────┐                   │
│  │ System   │ P: A/C   │ E: L/C   │                   │
│  ├──────────┼──────────┼──────────┤                   │
│  │ DynamoDB │  PA      │  EL      │  Fast + Available │
│  │ Cassandra│  PA      │  EL      │  Fast + Available │
│  │ Spanner  │  PC      │  EC      │  Consistent always│
│  │ MongoDB  │  PC      │  EC      │  Consistent always│
│  │ Cosmos DB│  PA      │  EL/EC   │  Tunable!         │
│  └──────────┴──────────┴──────────┘                   │
│                                                       │
└───────────────────────────────────────────────────────┘
```

---

## 8. Scalability vs Availability vs Reliability — How They Connect

These three are related but independent. You can have any combination:

```
┌────────────┬────────────┬────────────┬──────────────────────────┐
│ Scalable?  │ Available? │ Reliable?  │ Example                  │
├────────────┼────────────┼────────────┼──────────────────────────┤
│     ✅     │     ✅     │     ✅     │ AWS S3, Google Search    │
│     ✅     │     ✅     │     ❌     │ System returns wrong data│
│     ✅     │     ❌     │     ✅     │ CP system during partitn │
│     ❌     │     ✅     │     ✅     │ Single server (works but │
│            │            │            │ can't handle growth)     │
│     ❌     │     ❌     │     ❌     │ Weekend hackathon project│
└────────────┴────────────┴────────────┴──────────────────────────┘
```

### The Interplay

```
        Scalability ────────── fuels ───────── Availability
            │                                      │
            │   Adding more nodes increases        │
            │   both capacity AND redundancy        │
            │                                      │
            └──── enables ────── Reliability ──────┘
                                     │
                    More nodes = more copies of data
                    More nodes = more failure tolerance
```

But there's tension too:
- **More nodes** (scalability) = **more things that can fail** (reliability challenge)
- **Strong consistency** (reliability) = **higher latency** (scalability bottleneck)
- **High availability** = may serve **stale data** (reliability trade-off)

---

## 9. Trade-offs in Practice — Real-World Architecture Decisions

### Case Study 1: Designing a Payment System (Stripe)

```
Priority: Reliability > Consistency > Availability > Latency > Throughput

Decision:
┌──────────────────────────────────────────────────┐
│  • CP system (never lose or duplicate a payment) │
│  • Idempotency keys (safe retries)               │
│  • Synchronous replication (data durability)     │
│  • Circuit breakers (prevent cascading failures) │
│  • Accept higher latency (200-500ms per call)    │
│  • Trade throughput for correctness              │
└──────────────────────────────────────────────────┘
```

### Case Study 2: Designing a Social Media Feed (Twitter/X)

```
Priority: Availability > Throughput > Latency > Scalability > Consistency

Decision:
┌──────────────────────────────────────────────────┐
│  • AP system (show feed even if slightly stale)  │
│  • Fanout-on-write for celebrities               │
│  • Timeline cache in Redis                       │
│  • Eventual consistency (like counts may lag)     │
│  • CDN for media                                 │
│  • Accept brief inconsistency for speed          │
└──────────────────────────────────────────────────┘
```

### Case Study 3: Designing a Search Engine (Google)

```
Priority: Latency > Availability > Throughput > Scalability > Consistency

Decision:
┌──────────────────────────────────────────────────┐
│  • Index is eventually consistent (web changes)  │
│  • Massive fan-out (1000s of shards in parallel) │
│  • Hedged requests (send to 2 servers, use first)│
│  • Multi-datacenter with anycast routing         │
│  • Graceful degradation (fewer results if slow)  │
│  • Target: < 200ms end-to-end                    │
└──────────────────────────────────────────────────┘
```

### Case Study 4: Designing a Chat Application (WhatsApp)

```
Priority: Reliability > Availability > Latency > Scalability > Consistency

Decision:
┌──────────────────────────────────────────────────┐
│  • Messages must never be lost (reliability)     │
│  • End-to-end encryption (security)              │
│  • Erlang/BEAM VM (2M connections per server)    │
│  • Eventual consistency for "last seen" / status │
│  • CP for message delivery (guaranteed once)     │
│  • Queue undelivered messages (offline users)    │
└──────────────────────────────────────────────────┘
```

---

## 10. System Design Interview — Key Questions and How to Apply These Concepts

### Framework: Apply Fundamentals to Any Design Question

```
Step 1: CLARIFY REQUIREMENTS
   └─→ What is the read/write ratio?
   └─→ How many users? (scalability needs)
   └─→ What's the acceptable downtime? (availability SLA)
   └─→ Can we lose data? (reliability requirements)
   └─→ How fast must it respond? (latency target)
   └─→ What's the peak load? (throughput requirements)

Step 2: IDENTIFY TRADE-OFFS
   └─→ Is this CP or AP? (CAP decision)
   └─→ Strong or eventual consistency?
   └─→ Optimize for reads or writes?
   └─→ Is it OK to show stale data?

Step 3: CHOOSE PATTERNS
   └─→ Load balancing (scalability)
   └─→ Replication (availability + reliability)
   └─→ Caching (latency + throughput)
   └─→ Sharding (scalability)
   └─→ Message queues (throughput + decoupling)
   └─→ CDN (latency for static content)
```

### Common Interview Questions Mapped to Fundamentals

| Question | Key Fundamental | Why |
|----------|----------------|-----|
| "Design a URL shortener" | Throughput + Scalability | Millions of reads/sec, billions of entries |
| "Design a chat system" | Reliability + Latency | Messages must not be lost, delivered fast |
| "Design a payment system" | Reliability + Consistency | Money must never be lost or duplicated |
| "Design a news feed" | Availability + Throughput | Always show something, handle celebrity fan-out |
| "Design a search engine" | Latency + Scalability | Sub-200ms for billions of web pages |
| "Design a rate limiter" | Throughput + Availability | Must work even under DDoS |
| "Design a notification system" | Reliability + Scalability | Deliver to billions, never miss one |
| "Design a file storage (S3)" | Reliability + Availability | 11 nines durability, 4 nines availability |

---

## 11. Quick Reference — Cheat Sheet

### Concept Summary

| Concept | Definition | Key Metric | Goal |
|---------|-----------|------------|------|
| **Scalability** | Handle growing load | Max RPS/TPS at acceptable latency | Scale horizontally |
| **Availability** | System is accessible | % uptime (nines) | 99.99%+ for critical systems |
| **Reliability** | System works correctly | MTBF, MTTR | Survive any single failure |
| **Latency** | Time for one request | p50, p95, p99 (ms) | < 100ms for user-facing |
| **Throughput** | Work per time unit | RPS, TPS, MB/s | Match or exceed demand |
| **CAP Theorem** | Pick 2 of 3 (C, A, P) | Consistency model | CP for money, AP for social |

### Decision Matrix

```
┌────────────────────────┬──────┬──────┬──────┬──────┬──────┬──────┐
│ Use Case               │ Scal │ Avail│ Reli │ Lat  │ Thru │ CAP  │
├────────────────────────┼──────┼──────┼──────┼──────┼──────┼──────┤
│ Banking / Payments     │  ★★  │  ★★  │ ★★★★ │  ★★  │  ★★  │  CP  │
│ Social Media Feed      │ ★★★★ │ ★★★★ │  ★★  │ ★★★  │ ★★★★ │  AP  │
│ Search Engine          │ ★★★★ │ ★★★  │  ★★  │ ★★★★ │ ★★★★ │  AP  │
│ Chat / Messaging       │ ★★★  │ ★★★  │ ★★★★ │ ★★★★ │ ★★★  │  CP  │
│ E-commerce Catalog     │ ★★★  │ ★★★★ │ ★★★  │ ★★★  │ ★★★  │  AP  │
│ Video Streaming        │ ★★★★ │ ★★★★ │  ★★  │ ★★★  │ ★★★★ │  AP  │
│ IoT Sensor Data        │ ★★★★ │ ★★★  │ ★★★  │  ★★  │ ★★★★ │  AP  │
│ Config / Coordination  │  ★★  │  ★★  │ ★★★★ │  ★★  │  ★   │  CP  │
└────────────────────────┴──────┴──────┴──────┴──────┴──────┴──────┘
  ★ = Low priority   ★★★★ = Highest priority
```

### The Golden Rules

1. **Start simple, scale when needed.** Premature optimization is the root of all evil.
2. **Horizontal scaling > Vertical scaling** for production systems.
3. **Network partitions WILL happen.** Design for them (choose CP or AP).
4. **Measure p99, not averages.** Averages hide tail latency problems.
5. **Caching is the #1 latency reducer.** But cache invalidation is hard.
6. **Redundancy is the #1 availability booster.** Run at least 3 replicas.
7. **Idempotency is the #1 reliability pattern.** Safe retries prevent data corruption.
8. **Every system design is a trade-off.** There is no perfect architecture — only the right one for your constraints.

---

### Interview Quick Answers

**Q: "How would you make this system more scalable?"**
> Add load balancers, shard the database, introduce caching (Redis), use a CDN for static assets, and adopt microservices so each service scales independently.

**Q: "How would you ensure high availability?"**
> Deploy across multiple availability zones with active-active configuration, implement health checks and automatic failover, use circuit breakers to isolate failures, and design for graceful degradation.

**Q: "How would you make this reliable?"**
> Use synchronous replication for critical data, implement idempotent APIs for safe retries, add checksums for data integrity, run chaos engineering tests, and maintain automated backups with point-in-time recovery.

**Q: "How would you reduce latency?"**
> Add a caching layer (Redis/Memcached), use a CDN, denormalize hot read paths, implement connection pooling, compress responses, and consider edge computing for latency-sensitive operations.

**Q: "CP or AP for this system?"**
> If we're handling money, medical records, or configuration data → **CP** (consistency is critical; better to show an error than wrong data). If we're building social feeds, product catalogs, or analytics → **AP** (availability matters more; brief staleness is acceptable).

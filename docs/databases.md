# Databases — Complete Deep Dive

> **SQL**: Structured, relational, and ACID-compliant — great for strong consistency.
> **NoSQL**: Flexible, schema-less, and built for horizontal scalability.
> Scaling strategies include vertical scaling, replication, and sharding to handle growing data demands.

---

## Table of Contents

1. [SQL vs NoSQL — The Fundamental Divide](#1-sql-vs-nosql--the-fundamental-divide)
2. [SQL Databases — Deep Dive](#2-sql-databases--deep-dive)
3. [NoSQL Databases — Deep Dive](#3-nosql-databases--deep-dive)
4. [ACID vs BASE](#4-acid-vs-base)
5. [Database Scaling Strategies](#5-database-scaling-strategies)
6. [Replication — Deep Dive](#6-replication--deep-dive)
7. [Sharding — Deep Dive](#7-sharding--deep-dive)
8. [Indexing — The Performance Multiplier](#8-indexing--the-performance-multiplier)
9. [Real-World Database Architectures](#9-real-world-database-architectures)
10. [Database Selection Guide](#10-database-selection-guide)
11. [System Design Interview — Database Questions](#11-system-design-interview--database-questions)
12. [Quick Reference — Cheat Sheet](#12-quick-reference--cheat-sheet)

---

## 1. SQL vs NoSQL — The Fundamental Divide

```
┌────────────────────────────────────────────────────────────────────┐
│                                                                    │
│   SQL (Relational)                NoSQL (Non-Relational)           │
│   ────────────────                ──────────────────────           │
│                                                                    │
│   ┌──────────────────┐           ┌──────────────────┐             │
│   │ Structured tables│           │ Flexible docs,   │             │
│   │ with rows & cols │           │ key-value, graphs│             │
│   │                  │           │                  │             │
│   │  Users           │           │  {                │             │
│   │  ┌────┬────┬───┐ │           │    "id": 1,      │             │
│   │  │ id │name│age│ │           │    "name": "Jo", │             │
│   │  ├────┼────┼───┤ │           │    "orders": [   │             │
│   │  │ 1  │ Jo │ 30│ │           │      {...},      │             │
│   │  │ 2  │ Al │ 25│ │           │      {...}       │             │
│   │  └────┴────┴───┘ │           │    ]             │             │
│   │                  │           │  }               │             │
│   └──────────────────┘           └──────────────────┘             │
│                                                                    │
│   Schema: RIGID                  Schema: FLEXIBLE                  │
│   Joins:  NATIVE                 Joins:  EXPENSIVE / NONE         │
│   Scale:  VERTICAL (primary)    Scale:  HORIZONTAL (native)       │
│   ACID:   YES                    ACID:   Usually BASE              │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

### Side-by-Side Comparison

| Aspect | SQL | NoSQL |
|--------|-----|-------|
| **Data model** | Tables with fixed schema (rows, columns) | Documents, key-value, wide-column, graph |
| **Schema** | Rigid, predefined, ALTER TABLE to change | Flexible, schema-on-read, evolve freely |
| **Relationships** | JOINs across tables | Embedded/nested data or application-level joins |
| **Query language** | SQL (standardized) | Database-specific (MongoDB Query, CQL, etc.) |
| **Transactions** | Multi-table ACID transactions | Usually single-document; some support multi-doc |
| **Scaling** | Primarily vertical; sharding is complex | Built for horizontal scaling (add nodes) |
| **Consistency** | Strong consistency (default) | Eventual consistency (tunable) |
| **Best for** | Complex queries, relationships, transactions | High volume, flexible schema, horizontal scale |

---

## 2. SQL Databases — Deep Dive

### What Makes SQL "Relational"

```
Tables are related through FOREIGN KEYS:

  Users                    Orders                   Products
  ┌────┬───────┐          ┌─────┬────────┬──────┐   ┌────┬────────┬───────┐
  │ id │ name  │          │ id  │user_id │prod_id│   │ id │ name   │ price │
  ├────┼───────┤          ├─────┼────────┼──────┤   ├────┼────────┼───────┤
  │ 1  │ Alice │◄────FK───│ 101 │  1     │  10  │──►│ 10 │ Laptop │ 999   │
  │ 2  │ Bob   │◄────FK───│ 102 │  2     │  20  │──►│ 20 │ Phone  │ 699   │
  │ 3  │ Carol │          │ 103 │  1     │  20  │──►│ 20 │ Phone  │ 699   │
  └────┴───────┘          └─────┴────────┴──────┘   └────┴────────┴───────┘

  Query: "What did Alice buy?"
  SELECT u.name, p.name, p.price
  FROM Users u
  JOIN Orders o ON u.id = o.user_id
  JOIN Products p ON o.prod_id = p.id
  WHERE u.name = 'Alice';

  Result:
  ┌───────┬────────┬───────┐
  │ Alice │ Laptop │ 999   │
  │ Alice │ Phone  │ 699   │
  └───────┴────────┴───────┘
```

### Normalization — Eliminating Redundancy

```
UNNORMALIZED (bad):
┌────┬───────┬──────────┬────────┬───────┐
│ id │ name  │ order_id │ product│ price │
├────┼───────┼──────────┼────────┼───────┤
│ 1  │ Alice │ 101      │ Laptop │ 999   │   ← "Alice" stored twice
│ 1  │ Alice │ 103      │ Phone  │ 699   │   ← Update anomaly: change name in one row?
└────┴───────┴──────────┴────────┴───────┘

NORMALIZED (3NF):
  Users table    →   name stored ONCE
  Orders table   →   references user_id
  Products table →   product info stored ONCE

Benefits: No update anomalies, less storage, data integrity
Cost: JOINs required for complex queries
```

### Popular SQL Databases

| Database | Best For | Used By | Key Feature |
|----------|---------|---------|-------------|
| **PostgreSQL** | General-purpose, extensible | Instagram, Uber, Stripe | JSONB support, extensions, full-text search |
| **MySQL** | Web applications | Facebook, Twitter, Shopify | InnoDB engine, Vitess for sharding |
| **SQL Server** | Enterprise Windows stack | Stack Overflow, Bing | Tight .NET integration |
| **Oracle** | Large enterprise | Banks, airlines, governments | RAC clustering, partitioning |
| **CockroachDB** | Global distributed SQL | DoorDash, Bose | Spanner-like, survives region failures |
| **Google Spanner** | Global consistency | Google Ads, Google Play | TrueTime (atomic clocks + GPS) |
| **Amazon Aurora** | Cloud-native MySQL/PG | Airbnb, Samsung | 5x MySQL throughput, auto-scaling storage |

### Real-World: Instagram on PostgreSQL

```
Instagram: 2B+ monthly users, 100M+ photos uploaded daily

Database Architecture:
┌──────────────────────────────────────────────────┐
│                                                  │
│  Users table:     Sharded by user_id             │
│  Photos table:    Sharded by user_id             │
│  Likes table:     Sharded by photo_id            │
│  Comments table:  Sharded by photo_id            │
│  Followers table: Sharded by user_id             │
│                                                  │
│  Sharding strategy:                              │
│  ├── user_id % N = shard number                  │
│  ├── Each shard: PostgreSQL primary + 2 replicas │
│  ├── Django ORM + custom sharding middleware      │
│  └── PgBouncer for connection pooling             │
│                                                  │
│  Scale:                                          │
│  ├── Thousands of PostgreSQL shards              │
│  ├── Read replicas for heavy read paths          │
│  └── Redis/Memcached cache layer in front         │
│                                                  │
└──────────────────────────────────────────────────┘
```

---

## 3. NoSQL Databases — Deep Dive

### Four Types of NoSQL

```
┌────────────────────────────────────────────────────────────────────┐
│                       NoSQL CATEGORIES                              │
├───────────────────┬───────────────────┬──────────────┬─────────────┤
│   KEY-VALUE       │   DOCUMENT        │  WIDE-COLUMN │   GRAPH     │
│                   │                   │              │             │
│  key → value      │  key → JSON doc   │  row → cols  │  nodes +    │
│                   │                   │  (families)  │  edges      │
│  ┌───┬──────┐     │  {                │              │             │
│  │"u1"│{...} │     │    "_id": "u1",  │  Row  CF1 CF2│   (A)──→(B) │
│  │"u2"│{...} │     │    "name": "Jo", │  ─── ─── ───│     │       │
│  │"u3"│{...} │     │    "orders": []  │  r1  a b c d│   (C)──→(D) │
│  └───┴──────┘     │  }               │  r2  e f g h│             │
│                   │                   │              │             │
│  Redis            │  MongoDB          │  Cassandra   │  Neo4j      │
│  DynamoDB         │  Couchbase        │  HBase       │  Amazon     │
│  Memcached        │  Firestore        │  ScyllaDB    │  Neptune    │
│                   │                   │  BigTable     │             │
├───────────────────┼───────────────────┼──────────────┼─────────────┤
│ Caching, sessions │ Catalogs, CMS,    │ Time series, │ Social nets,│
│ leaderboards,     │ user profiles,    │ IoT, logging,│ fraud detect│
│ rate limiting     │ real-time apps    │ analytics    │ knowledge   │
│                   │                   │              │ graphs      │
└───────────────────┴───────────────────┴──────────────┴─────────────┘
```

### 3.1 Key-Value Stores

**The simplest NoSQL model**: every piece of data is stored as a key-value pair.

```
SET user:1001 '{"name":"Alice","age":30}'     → O(1)
GET user:1001                                  → O(1)
DEL user:1001                                  → O(1)
```

**Redis — Real-World Use Cases:**

```
┌──────────────────────────────────────────────────┐
│                  REDIS USE CASES                  │
├──────────────────────────────────────────────────┤
│                                                  │
│  1. SESSION STORE (Twitter)                      │
│     SET session:abc123 '{"user_id":1001}'        │
│     EXPIRE session:abc123 3600                   │
│                                                  │
│  2. CACHING (Instagram)                          │
│     SET post:5001:likes 42857                    │
│     INCR post:5001:likes  → 42858               │
│                                                  │
│  3. RATE LIMITING (Stripe)                       │
│     INCR rate:api_key:minute                     │
│     EXPIRE rate:api_key:minute 60                │
│                                                  │
│  4. LEADERBOARD (Gaming)                         │
│     ZADD leaderboard 9500 "player:42"            │
│     ZREVRANGE leaderboard 0 9  → top 10         │
│                                                  │
│  5. PUB/SUB (Chat)                               │
│     PUBLISH chat:room1 "Hello everyone!"         │
│     SUBSCRIBE chat:room1                         │
│                                                  │
│  6. DISTRIBUTED LOCK (Microservices)              │
│     SET lock:order:123 "worker-1" NX EX 30       │
│                                                  │
└──────────────────────────────────────────────────┘
```

**DynamoDB — Real-World (Amazon):**

```
Amazon Shopping Cart:
  Table: CartItems
  Partition Key: user_id
  Sort Key: product_id

  ┌──────────┬────────────┬──────┬──────────┐
  │ user_id  │ product_id │ qty  │ added_at │
  ├──────────┼────────────┼──────┼──────────┤
  │ user_001 │ PROD_A     │ 2    │ 2024-... │
  │ user_001 │ PROD_B     │ 1    │ 2024-... │
  │ user_002 │ PROD_C     │ 3    │ 2024-... │
  └──────────┴────────────┴──────┴──────────┘

  Why DynamoDB?
  • AP system: cart always available (even if slightly stale)
  • Single-digit millisecond reads/writes at any scale
  • Auto-scales from 0 to millions of TPS
  • On-demand pricing: pay per request during low traffic
```

### 3.2 Document Stores

Store data as **JSON-like documents** — flexible schema, nested structures.

```
MongoDB document:
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),
  "username": "alice",
  "email": "alice@example.com",
  "profile": {
    "bio": "Software engineer",
    "avatar": "https://..."
  },
  "orders": [
    {
      "order_id": "ORD-001",
      "total": 149.99,
      "items": [
        {"product": "Keyboard", "qty": 1, "price": 79.99},
        {"product": "Mouse", "qty": 1, "price": 49.99}
      ]
    }
  ],
  "tags": ["premium", "developer"]
}

vs. SQL equivalent: 4 tables (Users, Profiles, Orders, OrderItems) + 3 JOINs
```

**MongoDB — Real-World (Uber):**

```
Uber Trip Document:
{
  "_id": "trip_abc123",
  "rider_id": "user_001",
  "driver_id": "driver_042",
  "status": "completed",
  "pickup": {
    "lat": 37.7749, "lng": -122.4194,
    "address": "123 Market St, SF",
    "time": "2024-01-15T08:30:00Z"
  },
  "dropoff": {
    "lat": 37.7849, "lng": -122.4094,
    "address": "456 Mission St, SF",
    "time": "2024-01-15T08:45:00Z"
  },
  "fare": {
    "base": 2.50, "distance": 5.20, "time": 3.10,
    "surge": 1.5, "total": 16.20
  },
  "route": [ [37.7749,-122.4194], ..., [37.7849,-122.4094] ]
}

Why MongoDB for trips?
• Schema varies: UberX vs UberPool vs UberEats have different fields
• Embedded route data avoids JOINs (one read fetches everything)
• Horizontal sharding by city/region
• Time-series queries on completed trips for analytics
```

### 3.3 Wide-Column Stores

Optimized for **massive write volumes** and **time-series / event data**.

```
Cassandra data model:

  Row Key (Partition Key): sensor_id
  Clustering Columns: timestamp (sorted)
  Column Families: temperature, humidity, pressure

  ┌───────────────┬────────────────────┬──────┬──────┬──────┐
  │ sensor_id     │ timestamp          │ temp │ humid│ press│
  ├───────────────┼────────────────────┼──────┼──────┼──────┤
  │ sensor_001    │ 2024-01-15T08:00   │ 22.5 │ 45.2 │ 1013 │
  │ sensor_001    │ 2024-01-15T08:01   │ 22.6 │ 45.1 │ 1013 │
  │ sensor_001    │ 2024-01-15T08:02   │ 22.4 │ 45.3 │ 1012 │
  ├───────────────┼────────────────────┼──────┼──────┼──────┤
  │ sensor_002    │ 2024-01-15T08:00   │ 19.1 │ 62.0 │ 1015 │
  │ sensor_002    │ 2024-01-15T08:01   │ 19.2 │ 61.8 │ 1015 │
  └───────────────┴────────────────────┴──────┴──────┴──────┘

  Query: "Get all readings for sensor_001 in last hour"
  → FAST: data is co-located and sorted by time
```

**Cassandra — Real-World (Netflix):**

```
Netflix: 250M+ subscribers, stores viewing history, recommendations

┌──────────────────────────────────────────────────────────┐
│  Viewing History Table:                                  │
│    Partition Key: user_id                                │
│    Clustering Key: viewed_at DESC                        │
│                                                          │
│  Why Cassandra?                                          │
│  ├── Write-heavy: millions of "user watched X" events   │
│  ├── Always available (AP): show history even if stale   │
│  ├── Multi-region replication (US, EU, APAC)             │
│  ├── Linear scalability: add nodes = more capacity       │
│  └── Time-series friendly: sorted by timestamp           │
│                                                          │
│  Scale: 10,000+ Cassandra nodes across regions           │
│  Throughput: Millions of reads/writes per second          │
└──────────────────────────────────────────────────────────┘
```

### 3.4 Graph Databases

**Optimized for relationships** — nodes (entities) connected by edges (relationships).

```
Social Network Graph:

  (Alice)──FRIENDS──(Bob)──FRIENDS──(Carol)
     │                │                │
  FOLLOWS          FOLLOWS          FOLLOWS
     │                │                │
     ▼                ▼                ▼
  (TechBlog)      (NewsPage)      (TechBlog)

  Query: "Find friends-of-friends who follow TechBlog"

  Cypher (Neo4j):
  MATCH (alice:User {name: "Alice"})-[:FRIENDS]->(friend)-[:FRIENDS]->(fof)
  WHERE (fof)-[:FOLLOWS]->(:Page {name: "TechBlog"})
  RETURN fof.name

  Result: Carol

  SQL equivalent: Multiple self-joins on a friendship table
  → O(n³) or worse vs graph traversal O(V+E)
```

**Neo4j — Real-World (Fraud Detection at PayPal):**

```
┌──────────────────────────────────────────────────────────┐
│  PayPal: Detect fraud rings in real-time                  │
│                                                          │
│  Graph: Users → Transactions → Merchants → Devices       │
│                                                          │
│  Fraud pattern: "Money circling"                         │
│  (A)──$100──→(B)──$95──→(C)──$90──→(A)                   │
│                                                          │
│  Graph query finds cycles in < 100ms                     │
│  SQL with JOINs: would take minutes on large datasets    │
│                                                          │
│  Also used by:                                           │
│  ├── LinkedIn: "People you may know" (2-hop traversal)   │
│  ├── Google: Knowledge Graph (entity relationships)       │
│  ├── Airbnb: Trust & Safety (detect fake accounts)        │
│  └── eBay: Product recommendations via purchase graphs    │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## 4. ACID vs BASE

### ACID (SQL Default)

```
ACID — Banking Transfer Example: Send $500 from Alice to Bob

┌───────────────────────────────────────────────────────┐
│                                                       │
│  A — Atomicity (All or Nothing)                       │
│  ─────────────────────────────                        │
│  BEGIN TRANSACTION                                    │
│    UPDATE accounts SET balance = balance - 500        │
│      WHERE user = 'Alice';                            │
│    UPDATE accounts SET balance = balance + 500        │
│      WHERE user = 'Bob';                              │
│  COMMIT;                                              │
│                                                       │
│  If step 2 fails → ROLLBACK step 1                   │
│  Money is never lost or created                       │
│                                                       │
│  C — Consistency (Rules Always Hold)                  │
│  ──────────────────────────────────                   │
│  CHECK (balance >= 0) — can't go negative            │
│  FOREIGN KEY constraints enforced                     │
│  If Alice has $400, transfer of $500 is REJECTED     │
│                                                       │
│  I — Isolation (Concurrent Txns Don't Interfere)     │
│  ───────────────────────────────────────────────      │
│  Two transfers happening simultaneously:              │
│    T1: Alice → Bob ($500)                             │
│    T2: Alice → Carol ($300)                           │
│  Each sees a consistent snapshot, no phantom reads    │
│                                                       │
│  D — Durability (Committed = Permanent)              │
│  ──────────────────────────────────────               │
│  Once COMMIT returns, data survives power failure     │
│  Written to disk (WAL) + replicated                   │
│                                                       │
└───────────────────────────────────────────────────────┘
```

### BASE (NoSQL Default)

```
BASE — Shopping Cart Example: Add item to cart

┌───────────────────────────────────────────────────────┐
│                                                       │
│  BA — Basically Available                             │
│  ────────────────────────                             │
│  The cart is ALWAYS accessible, even during            │
│  network partitions or node failures.                 │
│  Showing a slightly stale cart is OK.                 │
│                                                       │
│  S — Soft State                                       │
│  ──────────────                                       │
│  Cart contents may differ between replicas             │
│  temporarily. Node A has 3 items, Node B has 2.       │
│  This is acceptable — it will converge.               │
│                                                       │
│  E — Eventually Consistent                            │
│  ────────────────────────                             │
│  Given enough time (usually milliseconds), all         │
│  replicas will converge to the same state.            │
│  User might briefly see an old cart, then it updates. │
│                                                       │
│  Trade-off:                                           │
│  ACID: "I'd rather show an error than wrong data"     │
│  BASE: "I'd rather show possibly-stale data than      │
│         an error"                                     │
│                                                       │
└───────────────────────────────────────────────────────┘
```

### When to Use ACID vs BASE

| Scenario | Choose | Why |
|----------|--------|-----|
| Banking / Payments | ACID | Money must never be lost or duplicated |
| E-commerce checkout | ACID | Order + payment + inventory must be atomic |
| User registration | ACID | Username uniqueness constraint |
| Social media likes | BASE | A like count off by 1 for 2 seconds is fine |
| Product catalog | BASE | Slightly stale product info is acceptable |
| Analytics / logging | BASE | Eventual accuracy is sufficient |
| Chat messages | ACID (per message) | Messages must not be lost |
| Shopping cart | BASE | Always available, reconcile later |

---

## 5. Database Scaling Strategies

```
┌────────────────────────────────────────────────────────────────┐
│                  DATABASE SCALING SPECTRUM                       │
│                                                                │
│  Simple ◄─────────────────────────────────────────► Complex    │
│                                                                │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│  │ Vertical │  │Connection│  │  Read    │  │ Sharding │      │
│  │ Scaling  │  │ Pooling  │  │ Replicas │  │          │      │
│  │          │  │          │  │          │  │          │      │
│  │ Bigger   │  │ Reuse    │  │ Separate │  │ Split    │      │
│  │ machine  │  │ conns    │  │ R from W │  │ data     │      │
│  │          │  │          │  │          │  │ across   │      │
│  │ $$$      │  │ $        │  │ $$       │  │ nodes    │      │
│  │          │  │          │  │          │  │ $$$      │      │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘      │
│                                                                │
│  Handles:       Handles:      Handles:      Handles:           │
│  10K → 50K     50K → 200K    200K → 1M     1M → ∞            │
│  QPS            QPS           read QPS      QPS                │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

### Vertical Scaling (Scale Up)

```
Before:  4 CPU, 16 GB RAM, 500 GB SSD   →   10,000 QPS
After:   64 CPU, 512 GB RAM, 4 TB NVMe  →   50,000 QPS

Pros: No code changes, no distributed complexity
Cons: Expensive hardware, physical limit, single point of failure
Limit: Largest AWS RDS instance: db.r6g.16xlarge (64 vCPU, 512 GB)
```

### Connection Pooling

```
WITHOUT pooling:
  Request 1 → Open conn → Query → Close conn   (5ms overhead per request)
  Request 2 → Open conn → Query → Close conn
  Request 3 → Open conn → Query → Close conn
  ...
  Problem: 10,000 requests = 10,000 connections = DB overwhelmed

WITH pooling (PgBouncer / HikariCP):
  Pool: [conn1, conn2, ..., conn20]   (20 reusable connections)

  Request 1 → Borrow conn1 → Query → Return conn1
  Request 2 → Borrow conn2 → Query → Return conn2
  Request 3 → Borrow conn1 → Query → Return conn1   (reused!)
  ...
  10,000 requests served by 20 connections!

  Real numbers:
  ├── PostgreSQL max_connections default: 100
  ├── PgBouncer can handle 10,000+ clients with 100 DB connections
  └── HikariCP (Java): recommended pool size = CPU cores × 2
```

---

## 6. Replication — Deep Dive

### What Is Replication?

Replication copies data from one database server (primary/leader) to one or more servers (replicas/followers) for **availability, fault tolerance, and read scalability**.

### Replication Topologies

```
1. SINGLE-LEADER (most common)
   ──────────────────────────

   Writes ──→ [Primary] ──sync/async──→ [Replica 1] ──→ Reads
                  │                      [Replica 2] ──→ Reads
                  │                      [Replica 3] ──→ Reads
                  └──→ Writes only here

   Used by: PostgreSQL, MySQL, SQL Server, MongoDB


2. MULTI-LEADER (cross-region)
   ───────────────────────────

   US Region          EU Region          Asia Region
   [Primary] ◄──sync──► [Primary] ◄──sync──► [Primary]
       │                    │                    │
   [Replica]            [Replica]            [Replica]

   Writes accepted at ANY primary (conflict resolution needed!)
   Used by: CockroachDB, Google Spanner, MySQL Group Replication


3. LEADERLESS (peer-to-peer)
   ─────────────────────────

   [Node A]  ◄──────►  [Node B]  ◄──────►  [Node C]
       ▲                    ▲                    ▲
       └────────────────────┴────────────────────┘

   Any node accepts reads AND writes
   Quorum: Write to W nodes, Read from R nodes, W+R > N
   Used by: Cassandra, DynamoDB, Riak
```

### Synchronous vs Asynchronous Replication

```
SYNCHRONOUS:
  Client → Primary → Wait for Replica ACK → Respond to Client

  Timeline:
  Client  ──write──→ Primary ──replicate──→ Replica
                                              │
                                          ack ─┘
                         ◄── ack ───────────┘
  Client  ◄── OK ─────┘

  Pros: Zero data loss (RPO = 0)
  Cons: Higher latency, unavailable if replica down
  Used by: Financial systems, Spanner

ASYNCHRONOUS:
  Client → Primary → Respond immediately → Replicate in background

  Timeline:
  Client  ──write──→ Primary
  Client  ◄── OK ─────┘
                       └── replicate ──→ Replica (later, ~100ms)

  Pros: Low latency, primary unaffected by replica issues
  Cons: Data loss possible if primary crashes before replication
  Used by: Most web applications (MySQL, PostgreSQL default)

SEMI-SYNCHRONOUS:
  Wait for at least ONE replica to ACK; others replicate async
  Balance between safety and performance
  Used by: MySQL semi-sync, AWS RDS Multi-AZ
```

### Replication Lag — The Real-World Challenge

```
Scenario: User updates profile picture

  t=0ms   User uploads new avatar → Primary writes it
  t=0ms   Primary responds: "Upload successful!"
  t=0ms   Page redirects to profile page
  t=0ms   Profile page reads from REPLICA → still has OLD avatar!
  t=100ms Replica receives update → new avatar appears

  User sees: "I uploaded my photo but it's still the old one!"

Solutions:
  1. Read-your-own-writes: route user's reads to primary for 5 seconds
  2. Sticky sessions: same user always reads from same replica
  3. Causal consistency: track version, ensure replica has >= version
  4. Synchronous replication for critical reads (slower but consistent)
```

### Real-World: GitHub — Single-Leader with Read Replicas

```
┌──────────────────────────────────────────────────────────┐
│  GitHub Database Architecture (simplified):               │
│                                                          │
│  Primary MySQL (writes):                                 │
│  ├── All git push, PR creation, issue updates            │
│  ├── Single point for write consistency                  │
│  └── ProxySQL routes writes here                         │
│                                                          │
│  Read Replicas (3-5 per region):                         │
│  ├── git clone, PR views, code browsing                  │
│  ├── Async replication (< 1 second lag typically)        │
│  └── ProxySQL load-balances reads across replicas        │
│                                                          │
│  Failover:                                               │
│  ├── Orchestrator monitors primary health                │
│  ├── If primary fails → promote replica in ~30 seconds   │
│  └── October 2018 outage: 24h degraded due to split-brain│
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## 7. Sharding — Deep Dive

### What Is Sharding?

Sharding (horizontal partitioning) splits data across multiple database instances, each holding a **subset** of the total data.

```
BEFORE sharding:
  One giant database: 10 TB, 50,000 QPS → overwhelmed

AFTER sharding (4 shards):
  Shard 1: 2.5 TB, 12,500 QPS  (users A-F)
  Shard 2: 2.5 TB, 12,500 QPS  (users G-M)
  Shard 3: 2.5 TB, 12,500 QPS  (users N-S)
  Shard 4: 2.5 TB, 12,500 QPS  (users T-Z)

  Each shard: Primary + 2 Read Replicas
```

### Sharding Strategies

```
1. RANGE-BASED SHARDING
   ─────────────────────

   Shard by: user_id ranges
   Shard 1: user_id 1 - 1,000,000
   Shard 2: user_id 1,000,001 - 2,000,000
   Shard 3: user_id 2,000,001 - 3,000,000

   ✅ Simple, range queries efficient
   ❌ Hot spots: new users all go to last shard
   ❌ Uneven distribution over time


2. HASH-BASED SHARDING
   ────────────────────

   Shard = hash(user_id) % num_shards

   hash(user_001) % 4 = 2 → Shard 2
   hash(user_002) % 4 = 0 → Shard 0
   hash(user_003) % 4 = 3 → Shard 3

   ✅ Even distribution
   ❌ Range queries require scatter-gather to ALL shards
   ❌ Resharding (changing num_shards) remaps most keys


3. DIRECTORY-BASED SHARDING
   ─────────────────────────

   Lookup table: "Which shard has this user?"

   ┌──────────┬────────┐
   │ user_id  │ shard  │
   ├──────────┼────────┤
   │ user_001 │ shard2 │
   │ user_002 │ shard1 │
   │ user_003 │ shard3 │
   └──────────┴────────┘

   ✅ Flexible — can move individual users between shards
   ✅ No resharding problem
   ❌ Lookup service is a bottleneck / single point of failure
   ❌ Extra hop for every query


4. GEO-BASED SHARDING
   ────────────────────

   Shard by geographic region:
   Shard US: all US users → us-east-1
   Shard EU: all EU users → eu-west-1
   Shard APAC: all APAC users → ap-southeast-1

   ✅ Low latency (data close to users)
   ✅ Data sovereignty compliance (GDPR)
   ❌ Cross-region queries are slow
   ❌ Uneven shard sizes (US might be 5x larger)
```

### The Hard Problems with Sharding

```
┌────────────────────────────────────────────────────────────────┐
│                SHARDING CHALLENGES                              │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  1. CROSS-SHARD JOINS                                          │
│     "Get all orders for users in shard 1 with products         │
│      in shard 3" → requires scatter-gather across shards       │
│     Solution: Denormalize or use application-level joins       │
│                                                                │
│  2. CROSS-SHARD TRANSACTIONS                                   │
│     Transfer money from user on Shard 1 to user on Shard 3    │
│     Solution: Two-phase commit (2PC) or Saga pattern           │
│                                                                │
│  3. RESHARDING (adding/removing shards)                        │
│     hash(key) % 4 ≠ hash(key) % 5 → data migration needed    │
│     Solution: Consistent hashing, or double-write migration    │
│                                                                │
│  4. HOT SPOTS                                                  │
│     Celebrity user on Shard 2 → Shard 2 gets 100x traffic     │
│     Solution: Further split hot shard, or cache hot data       │
│                                                                │
│  5. GLOBAL UNIQUE IDs                                          │
│     Auto-increment doesn't work across shards (duplicates!)    │
│     Solution: Snowflake IDs, UUIDs, or centralized ID service  │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

### Real-World: Shopify — Sharding MySQL with Vitess

```
┌──────────────────────────────────────────────────────────┐
│  Shopify: Millions of stores, billions of products        │
│                                                          │
│  Sharding strategy: SHARD PER SHOP (pod-based)           │
│                                                          │
│  Shop "Nike"     → Pod 1 (MySQL shard group)             │
│  Shop "Adidas"   → Pod 2 (MySQL shard group)             │
│  Shop "SmallBiz" → Pod 3 (MySQL shard group, shared)     │
│                                                          │
│  Each pod:                                               │
│  ├── MySQL Primary + 2 Read Replicas                     │
│  ├── VTGate (Vitess query router)                        │
│  └── Connection pooling via Vitess VTTablet              │
│                                                          │
│  Benefits:                                               │
│  ├── One shop's Black Friday doesn't affect others       │
│  ├── Can migrate shops between pods live                 │
│  ├── Independent scaling per pod                         │
│  └── Blast radius limited to one pod                     │
│                                                          │
│  Scale: 100+ pods, thousands of MySQL instances           │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Real-World: Discord — Message Sharding with Cassandra → ScyllaDB

```
┌──────────────────────────────────────────────────────────┐
│  Discord: Trillions of messages, 200M+ users              │
│                                                          │
│  Original: Cassandra sharded by (channel_id, bucket)     │
│                                                          │
│  Problem:                                                │
│  ├── Hot partitions: popular channels (1M+ members)      │
│  ├── Cassandra GC pauses → latency spikes               │
│  └── Compaction storms during peak hours                 │
│                                                          │
│  Migration to ScyllaDB (C++ rewrite of Cassandra):       │
│  ├── Same data model, same sharding strategy             │
│  ├── No GC pauses (C++ vs Java)                          │
│  ├── p99 latency: 5ms → 1ms                             │
│  └── Nodes needed: reduced by 10x                        │
│                                                          │
│  Shard key design:                                       │
│  ├── Partition: (channel_id, message_bucket)              │
│  ├── Bucket: 10-day time window                          │
│  ├── Old buckets → cold storage (S3)                     │
│  └── Hot channels get more replicas                      │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## 8. Indexing — The Performance Multiplier

### Without Index vs With Index

```
WITHOUT INDEX:
  SELECT * FROM users WHERE email = 'alice@example.com';

  Database scans ALL 10 million rows: O(n) = ~5 seconds
  ┌────────────────────────────────────────┐
  │ scan → scan → scan → ... → FOUND!     │  10M rows scanned
  └────────────────────────────────────────┘

WITH INDEX on email:
  Same query: O(log n) = ~2 milliseconds
  ┌──────────────────┐
  │ B-Tree lookup:   │
  │   root → a*      │
  │         → ali*   │
  │         → alice@ │  ← FOUND in 3 hops!
  └──────────────────┘

  Speedup: 2,500x faster
```

### Types of Indexes

```
┌────────────────────────────────────────────────────────────────┐
│                      INDEX TYPES                                │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  B-TREE (default for most databases)                           │
│  ├── Balanced tree, O(log n) for point + range queries         │
│  ├── Supports: =, <, >, BETWEEN, LIKE 'abc%'                 │
│  └── Used by: PostgreSQL, MySQL, SQL Server                    │
│                                                                │
│  HASH INDEX                                                    │
│  ├── O(1) for exact match only                                 │
│  ├── Does NOT support range queries                            │
│  └── Used by: Redis, DynamoDB, PostgreSQL (manual)             │
│                                                                │
│  LSM TREE (Log-Structured Merge Tree)                          │
│  ├── Write-optimized: writes go to in-memory buffer first      │
│  ├── Background compaction merges levels                       │
│  └── Used by: Cassandra, RocksDB, LevelDB, HBase              │
│                                                                │
│  COMPOSITE INDEX (multi-column)                                │
│  ├── INDEX(country, city, zipcode)                             │
│  ├── Satisfies: WHERE country=X AND city=Y                    │
│  ├── Left-prefix rule: can use (country) or (country, city)   │
│  └── Cannot use: WHERE city=Y (skips first column)            │
│                                                                │
│  COVERING INDEX                                                │
│  ├── Index contains ALL columns needed by the query            │
│  ├── No need to read the actual table row (index-only scan)    │
│  └── Example: INDEX(user_id, email) for SELECT email FROM ...  │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

### Indexing Trade-offs

```
                  Reads                    Writes
              ┌───────────┐            ┌───────────┐
  No Index:   │  SLOW ❌   │            │  FAST ✅   │
              │  Full scan │            │  Just write│
              └───────────┘            └───────────┘

  With Index: │  FAST ✅   │            │  SLOWER ⚠️ │
              │  B-tree    │            │  Update    │
              │  lookup    │            │  index too │
              └───────────┘            └───────────┘

Rule of thumb:
  • Index columns used in WHERE, JOIN, ORDER BY
  • Don't over-index: each index costs write performance + storage
  • 5-10 indexes per table is typical
  • Monitor slow query logs to find missing indexes
```

---

## 9. Real-World Database Architectures

### Twitter / X — Hybrid SQL + NoSQL

```
┌──────────────────────────────────────────────────────────┐
│                                                          │
│  User Data:     MySQL (sharded by user_id)               │
│  ├── Profiles, settings, auth                            │
│  └── ACID transactions for account operations            │
│                                                          │
│  Tweets:        MySQL → Manhattan (custom KV store)      │
│  ├── Originally MySQL, hit scaling limits                │
│  └── Migrated to Manhattan for horizontal scaling        │
│                                                          │
│  Timeline:      Redis (sorted sets)                      │
│  ├── Each user's home timeline cached in Redis           │
│  ├── Fan-out-on-write for users with < 5K followers      │
│  └── Fan-out-on-read for celebrities (> 5K followers)    │
│                                                          │
│  Social Graph:  FlockDB (custom graph store)             │
│  ├── Who follows whom                                    │
│  └── Optimized for high-fanout graph traversals          │
│                                                          │
│  Search:        Earlybird (custom Lucene-based)          │
│  ├── Real-time tweet indexing                            │
│  └── Inverted index for full-text search                 │
│                                                          │
│  Analytics:     BigQuery + Hadoop + Druid                │
│  └── Batch and real-time analytics on tweet data         │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Airbnb — PostgreSQL at Scale

```
┌──────────────────────────────────────────────────────────┐
│                                                          │
│  Primary Database: PostgreSQL (sharded)                   │
│  ├── Listings, reservations, reviews, payments           │
│  ├── Sharded by listing_id and user_id                   │
│  └── Strong consistency for bookings (double-booking!)   │
│                                                          │
│  Search: Elasticsearch                                    │
│  ├── Full-text search on listings                        │
│  ├── Geo-spatial queries (listings near coordinates)     │
│  └── Faceted search (filters: price, amenities, etc.)   │
│                                                          │
│  Cache: Redis + Memcached                                │
│  ├── Listing details (99% reads)                         │
│  ├── Session storage                                     │
│  └── Rate limiting                                       │
│                                                          │
│  Analytics: Apache Hive + Spark on S3                    │
│  ├── Data lake for historical analytics                  │
│  └── ML models for pricing, fraud detection              │
│                                                          │
│  Key Design Decision:                                     │
│  ├── Booking = ACID transaction (SQL)                    │
│  ├── Search = Eventually consistent (NoSQL)              │
│  └── Analytics = Batch processing (Data Lake)            │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## 10. Database Selection Guide

### Decision Framework

```
What's your primary access pattern?

├── Complex queries with JOINs across entities?
│   └── SQL (PostgreSQL, MySQL)
│
├── Simple key-value lookups at massive scale?
│   └── Key-Value (Redis, DynamoDB)
│
├── Flexible schema that changes frequently?
│   └── Document (MongoDB, Firestore)
│
├── High write volume (logs, events, time-series)?
│   └── Wide-Column (Cassandra, ScyllaDB)
│
├── Complex relationship traversals?
│   └── Graph (Neo4j, Neptune)
│
├── Full-text search?
│   └── Search Engine (Elasticsearch, OpenSearch)
│
└── Need global consistency with SQL?
    └── NewSQL (CockroachDB, Spanner, Aurora)
```

### Database Selection Matrix

```
┌────────────────────────┬──────┬──────┬──────┬──────┬──────┬──────┐
│ Use Case               │ PG   │ MySQL│ Mongo│ Redis│ Cass │ Neo4j│
├────────────────────────┼──────┼──────┼──────┼──────┼──────┼──────┤
│ E-commerce catalog     │ ★★★★ │ ★★★★ │ ★★★★ │  ★★  │  ★★  │  ★   │
│ Banking / Payments     │ ★★★★ │ ★★★  │  ★   │  ★   │  ★   │  ★   │
│ Social media feed      │ ★★★  │ ★★★  │ ★★★★ │ ★★★★ │ ★★★  │  ★★  │
│ Chat / Messaging       │  ★★  │  ★★  │ ★★★  │ ★★★★ │ ★★★★ │  ★   │
│ IoT / Sensor data      │  ★★  │  ★★  │  ★★  │  ★★  │ ★★★★ │  ★   │
│ Session storage        │  ★★  │  ★★  │  ★★  │ ★★★★ │  ★★  │  ★   │
│ Fraud detection        │  ★★  │  ★★  │  ★★  │  ★★  │  ★   │ ★★★★ │
│ Recommendation engine  │  ★★  │  ★★  │ ★★★  │  ★★  │  ★★  │ ★★★★ │
│ Config management      │ ★★★  │ ★★★  │ ★★★  │ ★★★★ │  ★   │  ★   │
│ Analytics (real-time)  │ ★★★  │  ★★  │  ★★  │ ★★★  │ ★★★★ │  ★   │
└────────────────────────┴──────┴──────┴──────┴──────┴──────┴──────┘
  ★ = Poor fit   ★★★★ = Excellent fit
```

---

## 11. System Design Interview — Database Questions

### Common Questions and Answers

**Q: "SQL or NoSQL for this system?"**
> Ask: (1) Do you need complex JOINs? → SQL. (2) Is the schema well-defined and stable? → SQL. (3) Need to scale writes horizontally? → NoSQL. (4) Data is naturally nested/document-shaped? → Document DB. (5) Need sub-millisecond latency? → Key-value store. Most real systems use BOTH — SQL for transactional data, NoSQL for caching/sessions/analytics.

**Q: "How would you handle a table with 1 billion rows?"**
> (1) Add proper indexes on query columns. (2) Partition the table (range or hash). (3) Archive old data to cold storage. (4) If read-heavy, add read replicas. (5) If still not enough, shard across multiple database instances. (6) Cache hot data in Redis/Memcached.

**Q: "How do you handle cross-shard queries?"**
> (1) Design shard keys to minimize cross-shard queries (co-locate related data). (2) For unavoidable cross-shard reads, use scatter-gather pattern (query all shards, merge results). (3) For cross-shard writes, use Saga pattern (compensating transactions) instead of 2PC. (4) Denormalize heavily-read data to avoid cross-shard JOINs.

**Q: "How do you handle replication lag?"**
> (1) For the writing user, read from primary for a few seconds after writes (read-your-own-writes). (2) Use causal consistency tokens so replicas serve reads only after they've caught up to a required version. (3) For critical reads (e.g., balance check), always read from primary. (4) Monitor replication lag and alert if it exceeds thresholds.

**Q: "When would you choose eventual consistency over strong consistency?"**
> Strong: Financial transactions, inventory counts, user registration (uniqueness). Eventual: Social media likes/views (brief staleness is OK), product catalog (price updates can lag by seconds), analytics counters (exact count not critical in real-time), DNS propagation (inherently eventual).

---

## 12. Quick Reference — Cheat Sheet

### Concept Summary

| Concept | Definition | Key Decision |
|---------|-----------|--------------|
| **SQL** | Structured, relational, ACID | Use when data has clear relationships and you need strong consistency |
| **NoSQL** | Flexible, distributed, BASE | Use when you need horizontal scale, flexible schema, or specific data models |
| **Replication** | Copy data to multiple servers | Choose sync (safety) vs async (speed) based on data criticality |
| **Sharding** | Split data across servers | Choose shard key carefully — it determines query efficiency and data distribution |
| **Indexing** | Speed up reads at cost of writes | Index columns in WHERE/JOIN/ORDER BY; don't over-index |
| **Connection Pooling** | Reuse DB connections | Always use one; pool size ≈ 2 × CPU cores |

### The Golden Rules

1. **Start with PostgreSQL.** It handles more use cases than you think.
2. **Add a cache (Redis) before scaling the database.** 80% of reads hit the same 20% of data.
3. **Read replicas before sharding.** Most apps are 90% reads — replicas handle that cheaply.
4. **Shard only when you must.** Sharding adds enormous complexity — exhaust vertical scaling and replicas first.
5. **Choose the shard key wisely.** A bad shard key creates hot spots and makes cross-shard queries expensive.
6. **Use the right database for the job.** No single database is best at everything — polyglot persistence is the norm.
7. **Index, don't query-optimize.** A missing index is almost always the cause of slow queries.
8. **Plan for failure.** Every database will go down — design for automatic failover, backups, and point-in-time recovery.

### Interview One-Liners

| Question | Quick Answer |
|----------|-------------|
| SQL vs NoSQL? | SQL for relationships + transactions; NoSQL for scale + flexibility |
| ACID vs BASE? | ACID: never wrong data; BASE: never down |
| When to shard? | When vertical scaling + read replicas aren't enough |
| Replication lag? | Read-your-own-writes from primary; eventual for others |
| Hot spot? | Shard key distributes unevenly; fix with better key or sub-sharding |
| Cross-shard JOIN? | Denormalize, or scatter-gather, or co-locate related data |
| Connection pooling? | Always. PgBouncer / HikariCP. Pool size ≈ 2 × CPU cores |
| When to cache? | Read-heavy data that doesn't change every request |

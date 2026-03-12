# Caching Strategies — Complete Deep Dive

> Improve response times and reduce database load with write-through and write-back caching,
> and eviction policies like LRU, LFU, and FIFO to manage cache efficiently.

---

## Table of Contents

1. [What Is Caching and Why It Matters](#1-what-is-caching-and-why-it-matters)
2. [Top 5 Caching Strategies You Should Know](#top-5-caching-strategies-you-should-know)
3. [Cache Topologies — Where to Cache](#2-cache-topologies--where-to-cache)
4. [Read Strategies — Cache-Aside, Read-Through](#3-read-strategies--cache-aside-read-through)
5. [Write Strategies — Write-Through, Write-Back, Write-Around](#4-write-strategies--write-through-write-back-write-around)
6. [Eviction Policies — LRU, LFU, FIFO, and More](#5-eviction-policies--lru-lfu-fifo-and-more)
7. [Cache Invalidation — The Hardest Problem](#6-cache-invalidation--the-hardest-problem)
8. [Distributed Caching — Scaling Beyond One Node](#7-distributed-caching--scaling-beyond-one-node)
9. [Cache Stampede, Thundering Herd, and Other Pitfalls](#8-cache-stampede-thundering-herd-and-other-pitfalls)
10. [Real-World Caching Architectures](#9-real-world-caching-architectures)
11. [Caching Products Comparison](#10-caching-products-comparison)
12. [System Design Interview — Caching Questions](#11-system-design-interview--caching-questions)
13. [Quick Reference — Cheat Sheet](#12-quick-reference--cheat-sheet)

---

## 1. What Is Caching and Why It Matters

A cache is a **high-speed data storage layer** that stores a subset of data — typically the most frequently accessed or recently used — so future requests can be served faster than fetching from the primary data source.

```
                WITHOUT Cache
                ──────────────
  User ──→ App Server ──→ Database
                            │
                        50-200ms per query
                        1,000 QPS max


                WITH Cache
                ──────────
  User ──→ App Server ──→ Cache (Redis)  ──→ Database
                            │                   │
                         0.5-5ms              50-200ms
                       (cache HIT)          (cache MISS only)
                       50,000 QPS             rarely hit

  Result: 100x lower latency, 50x higher throughput
```

> **Analogy**: A cache is like keeping your most-used books on your desk instead of walking to the library every time. The desk is small (limited capacity), so you need a strategy for which books to keep (eviction policy) and when to update them (write strategy).

### The Numbers That Prove Caching Works

```
┌──────────────────────────────────────────────────────────────┐
│                   LATENCY COMPARISON                          │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  L1 CPU Cache ........................ 0.5   ns              │
│  L2 CPU Cache ........................ 7     ns              │
│  Main Memory (RAM) .................. 100   ns              │
│  Redis (in-memory, same DC) ......... 500   μs  (0.5ms)    │
│  Memcached (in-memory, same DC) ..... 500   μs  (0.5ms)    │
│  SSD Random Read ..................... 150   μs              │
│  PostgreSQL Query (indexed) ......... 5-50  ms              │
│  PostgreSQL Query (complex JOIN) .... 50-500 ms              │
│  MongoDB Query ...................... 5-50   ms              │
│  Cross-region network round trip .... 100-200 ms             │
│                                                              │
│  Cache vs Database: 100x - 1000x faster                     │
│                                                              │
│  Real Impact:                                                │
│  ├── Facebook: Memcached serves 75%+ of reads               │
│  ├── Twitter: Redis timeline cache reduces DB load by 99%   │
│  ├── Amazon: Every 100ms latency = 1% revenue loss          │
│  └── Netflix: EVCache handles 30M+ requests/second          │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### Cache Hit Ratio — The Key Metric

```
                   Cache Hits
Hit Ratio = ─────────────────────────
            Cache Hits + Cache Misses


Example:
  Total requests: 100,000
  Cache hits: 95,000
  Cache misses: 5,000

  Hit Ratio = 95,000 / 100,000 = 95%

  ┌─────────────────────────────────────────────────┐
  │  Hit Ratio    │  Impact                          │
  ├───────────────┼──────────────────────────────────┤
  │  < 50%        │  Cache is barely helping          │
  │  50% - 80%    │  Decent; review access patterns   │
  │  80% - 95%    │  Good for most applications       │
  │  95% - 99%    │  Excellent; typical for mature    │
  │               │  production systems               │
  │  > 99%        │  Outstanding; most reads from     │
  │               │  cache (Facebook, Netflix)         │
  └───────────────┴──────────────────────────────────┘

  Impact of hit ratio on DB load (100K requests):
    80% hit ratio → 20,000 DB queries
    95% hit ratio → 5,000 DB queries       (4x fewer)
    99% hit ratio → 1,000 DB queries       (20x fewer)
```

---

## Top 5 Caching Strategies You Should Know

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                     TOP 5 CACHING STRATEGIES                                  │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  1. READ THROUGH                                                             │
│  ───────────────                                                             │
│  The application checks the cache first. On a cache miss, the cache          │
│  ITSELF fetches data from the DB, stores it, and returns it to the app.      │
│                                                                              │
│     App ──→ Cache ──HIT?──→ Return data                                      │
│                │                                                             │
│              MISS ──→ Cache queries DB ──→ Store + Return                     │
│                                                                              │
│  Best for: Read-heavy apps like CDNs and social feeds.                       │
│  Real-world: AWS DAX, Hibernate L2 Cache, NCache                             │
│                                                                              │
│  ─────────────────────────────────────────────────────────────────────────    │
│                                                                              │
│  2. WRITE THROUGH                                                            │
│  ────────────────                                                            │
│  Every write updates both the cache AND the database at the same time.       │
│  Ensures the cache always stays fresh and consistent with the DB.            │
│                                                                              │
│     App ──write──→ Cache ──write──→ Database                                 │
│                      │                  │                                    │
│                      ←── ACK ──────── ACK                                    │
│                                                                              │
│  Best for: Systems needing strong consistency (e.g., finance apps,           │
│            inventory counts, configuration systems).                         │
│  Real-world: Amazon DAX (DynamoDB Accelerator)                               │
│                                                                              │
│  ─────────────────────────────────────────────────────────────────────────    │
│                                                                              │
│  3. CACHE ASIDE (Lazy Loading)                                               │
│  ─────────────────────────────                                               │
│  The app looks in the cache first. On a miss, it fetches from the DB         │
│  and EXPLICITLY updates the cache. The most common caching pattern.          │
│                                                                              │
│     App ──→ Cache ──HIT?──→ Return data                                      │
│                │                                                             │
│              MISS ──→ App queries DB ──→ App writes to Cache ──→ Return      │
│                                                                              │
│  Best for: Read-heavy workloads where slight data staleness is okay.         │
│  Real-world: Facebook, Instagram, Stripe, GitHub                             │
│                                                                              │
│  ─────────────────────────────────────────────────────────────────────────    │
│                                                                              │
│  4. WRITE AROUND                                                             │
│  ──────────────                                                              │
│  Writes go straight to the DB, skipping the cache entirely.                  │
│  Cache gets updated only on a subsequent read (via cache-aside).             │
│                                                                              │
│     App ──write──→ Database (cache NOT updated)                              │
│     ...later...                                                              │
│     App ──read──→ Cache MISS ──→ DB ──→ populate cache                       │
│                                                                              │
│  Best for: Write-heavy systems with rare immediate reads                     │
│            (e.g., logging, analytics, audit trails).                          │
│                                                                              │
│  ─────────────────────────────────────────────────────────────────────────    │
│                                                                              │
│  5. WRITE BACK (Write-Behind)                                                │
│  ────────────────────────────                                                │
│  Writes go to the cache FIRST, and are asynchronously persisted to the       │
│  DB later. Minimizes write latency at the cost of durability risk.           │
│                                                                              │
│     App ──write──→ Cache ──→ ACK (immediate!)                                │
│                      │                                                       │
│                      └── async (later) ──→ Database                          │
│                                                                              │
│  Best for: High-performance, write-heavy systems                             │
│            (counters, analytics, gaming leaderboards, IoT data).              │
│  Real-world: Facebook like counts, gaming leaderboards                       │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Strategy Selection Quick Guide

```
┌──────────────────┬──────────────────┬───────────────────────────────────────┐
│     Strategy     │   Consistency    │   Best Use Case                       │
├──────────────────┼──────────────────┼───────────────────────────────────────┤
│ Read-Through     │ Cache-managed    │ CDNs, social feeds, read-heavy apps   │
│ Write-Through    │ Strong           │ Finance, inventory, config systems    │
│ Cache-Aside      │ Eventual (brief  │ General web apps, APIs, most CRUD     │
│                  │  staleness OK)   │                                       │
│ Write-Around     │ Eventual         │ Logging, analytics, audit trails      │
│ Write-Back       │ Eventual (risk   │ Counters, leaderboards, IoT, high-   │
│                  │  of data loss)   │  throughput writes                    │
└──────────────────┴──────────────────┴───────────────────────────────────────┘

Decision Flowchart:

  Is the workload read-heavy or write-heavy?
  │
  ├── READ-HEAVY
  │   ├── Does the app need full cache control?
  │   │   ├── YES → Cache-Aside (most common, app manages cache)
  │   │   └── NO  → Read-Through (cache auto-loads, cleaner app code)
  │   │
  │   └── Must reads ALWAYS return fresh data after writes?
  │       └── YES → Pair with Write-Through
  │
  └── WRITE-HEAVY
      ├── Is the written data read back immediately?
      │   ├── YES → Write-Through (consistent, but slower writes)
      │   └── NO  → Write-Around (avoids polluting cache with unread data)
      │
      └── Is write speed more important than data durability?
          ├── YES → Write-Back (fastest writes, async DB flush)
          └── NO  → Write-Through or Write-Around
```

### Combined Strategies in Production

Most real-world systems **mix multiple strategies** for different data types:

```
┌──────────────────────────────────────────────────────────────────┐
│  Example: E-Commerce Platform                                     │
│                                                                  │
│  User profile:      Write-Through + Cache-Aside                  │
│  ├── User sees changes immediately (write-through)               │
│  └── Other users get cached profile (cache-aside reads)          │
│                                                                  │
│  Product catalog:   Read-Through                                 │
│  ├── High read volume, catalog changes rarely                    │
│  └── Cache auto-loads products transparently                     │
│                                                                  │
│  View/click counts: Write-Back                                   │
│  ├── Millions of events per second                               │
│  └── Batch-flush to DB every few seconds                         │
│                                                                  │
│  Order audit logs:  Write-Around                                 │
│  ├── Written once, rarely re-read                                │
│  └── No need to cache write-heavy audit data                     │
│                                                                  │
│  Session data:      Cache-Aside + TTL                            │
│  ├── Load session on first request, cache with expiry            │
│  └── TTL = session timeout (30 min)                              │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 2. Cache Topologies — Where to Cache

```
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│  CLIENT LAYER                                                    │
│  ┌─────────────────────────────────────┐                         │
│  │  Browser Cache (HTTP Cache-Control) │  Static assets,         │
│  │  Service Worker Cache               │  API responses          │
│  │  Local Storage / IndexedDB          │  Offline data           │
│  └─────────────────────────────────────┘                         │
│                                                                  │
│  CDN LAYER                                                       │
│  ┌─────────────────────────────────────┐                         │
│  │  CloudFront / Akamai / Cloudflare   │  Images, CSS, JS,       │
│  │  Edge locations worldwide           │  API responses (TTL)    │
│  └─────────────────────────────────────┘                         │
│                                                                  │
│  API GATEWAY / REVERSE PROXY                                     │
│  ┌─────────────────────────────────────┐                         │
│  │  Nginx proxy_cache / Varnish        │  Full page caching,     │
│  │  API response caching               │  rate limit state       │
│  └─────────────────────────────────────┘                         │
│                                                                  │
│  APPLICATION LAYER                                               │
│  ┌─────────────────────────────────────┐                         │
│  │  In-process (Caffeine, Guava)       │  Hot objects, config    │
│  │  Local heap cache per instance      │  Zero network overhead  │
│  └─────────────────────────────────────┘                         │
│                                                                  │
│  DISTRIBUTED CACHE LAYER                                         │
│  ┌─────────────────────────────────────┐                         │
│  │  Redis / Memcached / EVCache        │  Shared across all      │
│  │  Centralized, consistent            │  app instances          │
│  └─────────────────────────────────────┘                         │
│                                                                  │
│  DATABASE LAYER                                                  │
│  ┌─────────────────────────────────────┐                         │
│  │  Query Cache (MySQL) / Buffer Pool  │  Automatic,             │
│  │  Materialized Views (PostgreSQL)    │  DB-managed             │
│  └─────────────────────────────────────┘                         │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### In-Process vs Distributed Cache

| Aspect | In-Process (Local) | Distributed (Remote) |
|--------|-------------------|---------------------|
| **Latency** | Nanoseconds (heap access) | ~0.5-2ms (network round trip) |
| **Consistency** | Per-instance only; other instances don't see updates | Shared across all instances |
| **Capacity** | Limited by JVM heap (~1-4 GB practical) | Limited by cluster size (TBs possible) |
| **Failure** | Lost on process restart | Survives app restarts (persisted in Redis) |
| **Use case** | Config, hot constants, lookup tables | User sessions, API responses, DB query results |
| **Example** | Java: Caffeine, Guava | Redis, Memcached |

**Best practice**: Use **both** — L1 in-process cache for ultra-hot data, L2 distributed cache for shared data.

```
Request ──→ L1 (Caffeine, in-process)
              │
         HIT? ──→ Return (nanoseconds)
              │
         MISS ──→ L2 (Redis, distributed)
                    │
               HIT? ──→ Populate L1, Return (1ms)
                    │
               MISS ──→ Database
                          │
                     Populate L2 + L1, Return (50ms)
```

---

## 3. Read Strategies — Cache-Aside, Read-Through

### 3.1 Cache-Aside (Lazy Loading)

The **most common** caching pattern. The application manages the cache directly.

```
READ flow:

  App ──→ Check Cache ──→ HIT? ──→ Return cached data
                │
            MISS ──→ Query Database
                       │
                  ←── Return data
                       │
                  Store in Cache (with TTL)
                       │
                  ←── Return to user


WRITE flow:

  App ──→ Write to Database
          │
          └──→ Invalidate/Delete cache key
               (next read will re-populate)
```

```
Pseudocode:

function getUser(userId):
    // 1. Try cache first
    user = cache.get("user:" + userId)
    if user != null:
        return user          // CACHE HIT

    // 2. Cache miss → query database
    user = database.query("SELECT * FROM users WHERE id = ?", userId)

    // 3. Populate cache with TTL
    cache.set("user:" + userId, user, TTL=300)  // 5 minutes

    return user
```

| Pros | Cons |
|------|------|
| Simple to implement | Cache miss penalty (cold start) |
| Only caches data that's actually requested | Potential stale data between DB write and cache invalidation |
| Application has full control | Requires cache management logic in application |
| Resilient to cache failure (falls back to DB) | "Cache stampede" on cold cache or key expiry |

**Used by**: Most web applications. This is the default pattern at Facebook, Instagram, Stripe, and GitHub.

### 3.2 Read-Through

The **cache itself** is responsible for loading data from the database on a miss. The application only talks to the cache.

```
READ flow:

  App ──→ Cache.get("user:123")
            │
       HIT? ──→ Return cached data
            │
       MISS ──→ Cache ITSELF queries Database
                  │
             ←── Cache stores result
                  │
             ←── Return to App


  The application doesn't know about the database for reads.
  The cache provider handles DB loading via a "cache loader" callback.
```

```
// Java with Caffeine + Read-Through:

LoadingCache<String, User> cache = Caffeine.newBuilder()
    .maximumSize(10_000)
    .expireAfterWrite(5, TimeUnit.MINUTES)
    .build(key -> database.findUserById(key));   // cache loader

User user = cache.get("user:123");  // automatic DB load on miss
```

| Pros | Cons |
|------|------|
| Cleaner application code (no cache miss logic) | Cache must understand DB schema |
| Consistent caching behavior | Less flexible — tied to specific data source |
| Reduces duplicate cache-miss handling | Cold start still incurs latency |

**Used by**: Hibernate L2 cache, AWS DAX (DynamoDB Accelerator), NCache.

---

## 4. Write Strategies — Write-Through, Write-Back, Write-Around

### 4.1 Write-Through

**Every write goes to both the cache AND the database simultaneously.** The write is only confirmed after both succeed.

```
WRITE flow:

  App ──write──→ Cache ──write──→ Database
                   │                 │
                   ←── ACK ──────── ←── ACK
                   │
              ←── Respond to App (both written)

Timeline:
  t=0ms   App sends write
  t=1ms   Cache updated
  t=50ms  Database updated
  t=50ms  App receives confirmation
```

```
Real-World Example — E-Commerce Product Price Update:

  updatePrice(productId, newPrice):
      // Write to both simultaneously
      cache.set("product:" + productId, {price: newPrice})
      database.update("UPDATE products SET price = ? WHERE id = ?",
                       newPrice, productId)
      // Both must succeed before confirming

  Benefit: Any subsequent read gets the latest price immediately
  Risk: If DB write fails, cache has wrong data (need rollback)
```

| Pros | Cons |
|------|------|
| Cache always consistent with DB | Higher write latency (write to 2 places) |
| No stale data served | Every write hits the DB (no write reduction) |
| Simple to reason about | Cache may store data that's never read (wasteful) |

**When to use**: Systems where **read consistency is critical** — financial dashboards, inventory counts, configuration systems.

**Real-World**: Amazon DynamoDB Accelerator (DAX) uses write-through. When you write to DAX, it writes to both DAX cache and DynamoDB table.

```
┌──────────────────────────────────────────────────────────┐
│  AWS DAX (DynamoDB Accelerator) — Write-Through          │
│                                                          │
│  App ──→ DAX ──write-through──→ DynamoDB                 │
│           │                                              │
│           └── read (cache HIT: ~600μs)                   │
│                                                          │
│  • Writes go to DAX AND DynamoDB                         │
│  • Reads served from DAX if cached (~600μs vs ~5ms)      │
│  • Consistent: cache always matches DB                   │
│  • 10x read performance improvement                      │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### 4.2 Write-Back (Write-Behind)

**Writes go to the cache immediately, and the cache asynchronously flushes to the database later.** The app gets instant write confirmation.

```
WRITE flow:

  App ──write──→ Cache ──→ ACK (immediate!)
                   │
                   └── async (later) ──→ Database
                       │
                       Batch writes every 100ms or 100 items

Timeline:
  t=0ms   App sends write
  t=1ms   Cache updated, App gets ACK        ← FAST!
  ...
  t=100ms Cache batch-flushes to Database    ← async
```

```
Real-World Example — Social Media Like Counter:

  likePost(postId):
      cache.increment("post:" + postId + ":likes")
      // Returns immediately! User sees updated count.
      // Background job flushes to DB every 5 seconds.

  Without write-back:
      500 likes/second on viral post → 500 DB writes/sec
      DB overwhelmed during viral moments

  With write-back:
      500 likes/second → 500 cache increments (instant)
      Batch flush: 1 DB write every 5 seconds with total count
      DB load: 0.2 writes/sec instead of 500!
```

| Pros | Cons |
|------|------|
| Extremely fast writes (cache-speed) | **Data loss risk** if cache crashes before flush |
| Reduces DB write load dramatically | Complex: need reliable async flush mechanism |
| Natural write batching / coalescing | Temporary inconsistency between cache and DB |
| Absorbs write spikes | Harder to implement correctly |

**When to use**: Write-heavy workloads where **speed matters more than durability** — analytics counters, like/view counts, gaming leaderboards, IoT sensor data aggregation.

**Real-World**: 

```
┌──────────────────────────────────────────────────────────┐
│  Facebook — Write-Behind for Like Counts                  │
│                                                          │
│  User clicks "Like" on a post                            │
│  ├── 1. Increment in Memcached/TAO cache (instant)       │
│  ├── 2. User sees updated count immediately              │
│  ├── 3. Background worker writes to MySQL every few sec  │
│  └── 4. If cache crashes: some likes lost (acceptable)   │
│                                                          │
│  Why: 100B+ interactions/day; DB can't handle each one   │
│                                                          │
│  Gaming — Write-Behind for Leaderboards                  │
│                                                          │
│  Player scores update → Redis ZADD (instant)             │
│  Background job → flush to PostgreSQL every 30 seconds   │
│  If Redis crashes: last 30 seconds of scores lost        │
│  (re-fetch from game servers; acceptable trade-off)      │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### 4.3 Write-Around

**Writes go directly to the database, bypassing the cache entirely.** The cache is only populated on reads (via cache-aside or read-through).

```
WRITE flow:

  App ──write──→ Database (directly)
                   │
                Cache is NOT updated
                   │
                Next read → cache MISS → load from DB → populate cache

Timeline:
  t=0ms   App writes to DB
  t=50ms  DB confirms write
  ...
  t=5000ms  Someone reads this data → cache MISS
  t=5050ms  Cache populated from DB
```

| Pros | Cons |
|------|------|
| Cache not polluted with rarely-read writes | Read-after-write gets stale data from cache |
| Simple — no cache write logic needed | Initial read after write is slow (cache miss) |
| Good when written data is rarely re-read | Requires cache-aside on the read path |

**When to use**: Write-heavy systems where most written data is **rarely read** — log ingestion, audit trails, batch data processing.

### Strategy Comparison

```
┌──────────────────────────────────────────────────────────────────────┐
│                    WRITE STRATEGY COMPARISON                          │
├──────────────┬──────────────┬──────────────┬────────────────────────┤
│              │ Write-Through│ Write-Back   │ Write-Around           │
├──────────────┼──────────────┼──────────────┼────────────────────────┤
│ Write speed  │ Slow (both)  │ Fast (cache) │ Medium (DB only)       │
│ Read after   │ Always fresh │ Always fresh │ May be stale           │
│  write       │              │  (in cache)  │  (cache not updated)   │
│ Data safety  │ Safe         │ Risk of loss │ Safe                   │
│ DB load      │ Every write  │ Reduced      │ Every write            │
│ Cache usage  │ May cache    │ Efficient    │ Only caches            │
│              │ unread data  │              │  read data             │
│ Complexity   │ Low          │ High         │ Low                    │
│ Best for     │ Financial,   │ Counters,    │ Logs, audit,           │
│              │ inventory    │ analytics    │  write-heavy rarely-read│
└──────────────┴──────────────┴──────────────┴────────────────────────┘
```

### Combined Strategies — Real-World

Most production systems **combine strategies**:

```
┌──────────────────────────────────────────────────────────┐
│  Instagram — Mixed Write Strategies                       │
│                                                          │
│  User profile update:                                    │
│  └── Write-Through (cache + DB)                          │
│      Profile must be consistent; user sees change now    │
│                                                          │
│  Like count:                                             │
│  └── Write-Back (cache → batch flush to DB)              │
│      Millions of likes/sec; brief inconsistency is OK    │
│                                                          │
│  Photo upload metadata:                                  │
│  └── Write-Around (DB only)                              │
│      Written once, read later when someone views profile │
│                                                          │
│  Feed generation:                                        │
│  └── Cache-Aside / Read-Through                          │
│      Pre-computed feed cached in Redis                   │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## 5. Eviction Policies — LRU, LFU, FIFO, and More

When the cache is **full** and a new item needs to be stored, the cache must **evict** (remove) an existing item. The eviction policy determines **which item to remove**.

### 5.1 LRU — Least Recently Used

Evict the item that was **accessed least recently**. The idea: if you haven't used it lately, you probably won't use it soon.

```
Cache capacity: 3

Operations:
  GET A  → Cache: [A]          (A is most recent)
  GET B  → Cache: [B, A]       (B is most recent)
  GET C  → Cache: [C, B, A]    (full!)
  GET D  → Cache: [D, C, B]    (A evicted — least recently used)
  GET B  → Cache: [B, D, C]    (B moves to front — just used)
  GET E  → Cache: [E, B, D]    (C evicted — least recently used)

         Most Recent                    Least Recent
            ◄───────────────────────────────►
         ┌─────┬─────┬─────┐
         │  E  │  B  │  D  │  ← C was here, evicted for E
         └─────┴─────┴─────┘
```

**Implementation**: Doubly-linked list + HashMap for O(1) get/put.

```
┌──────────────────────────────────────────────────────────┐
│  LRU Cache Implementation                                │
│                                                          │
│  HashMap: key → Node (O(1) lookup)                       │
│  Doubly Linked List: maintains access order               │
│                                                          │
│  GET(key):                                               │
│    1. Look up in HashMap → O(1)                          │
│    2. Move node to HEAD of list → O(1)                   │
│    3. Return value                                       │
│                                                          │
│  PUT(key, value):                                        │
│    1. If exists: update + move to HEAD → O(1)            │
│    2. If new + cache full: remove TAIL (LRU) → O(1)     │
│    3. Insert at HEAD → O(1)                              │
│    4. Add to HashMap → O(1)                              │
│                                                          │
│  HEAD ←→ [D] ←→ [B] ←→ [A] ←→ TAIL                     │
│  Most                            Least                   │
│  Recent                          Recent                  │
│                                  (evict this)            │
│                                                          │
│  Java: LinkedHashMap(capacity, 0.75f, true)              │
│        with removeEldestEntry() override                 │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

| Pros | Cons |
|------|------|
| Simple, O(1) operations | Doesn't consider frequency — a one-time scan evicts popular items |
| Good for temporal locality | Cache pollution from sequential scans |
| Well-understood, widely used | No distinction between "used once" and "used 1000 times" |

**When to use**: General-purpose caching where recent access is a good predictor of future access — web sessions, API responses, database query results.

**Used by**: Redis (default), Memcached, Linux page cache, CPU caches, all major browsers.

### 5.2 LFU — Least Frequently Used

Evict the item that was **accessed the fewest times**. The idea: items that are rarely accessed are less valuable.

```
Cache capacity: 3

Operations:
  GET A  → A(freq:1)   Cache: [A:1]
  GET A  → A(freq:2)   Cache: [A:2]
  GET B  → B(freq:1)   Cache: [A:2, B:1]
  GET C  → C(freq:1)   Cache: [A:2, B:1, C:1]    (full!)
  GET D  → D(freq:1)   Cache: [A:2, D:1, C:1]    (B evicted — freq 1, oldest)
  GET A  → A(freq:3)   Cache: [A:3, D:1, C:1]
  GET E  → E(freq:1)   Cache: [A:3, E:1, D:1]    (C evicted — freq 1, oldest)

  A stays because it's accessed most frequently!
  One-time accesses get evicted first.
```

**Implementation**: HashMap + frequency buckets (each bucket is a linked list of items with that frequency).

```
┌──────────────────────────────────────────────────────────┐
│  LFU Cache — Frequency Buckets                           │
│                                                          │
│  Freq 1: [D] → [E]        (evict from here first)       │
│  Freq 2: (empty)                                         │
│  Freq 3: [A]               (most frequently used)        │
│                                                          │
│  min_freq pointer → 1      (quickest eviction target)    │
│                                                          │
│  GET(key):                                               │
│    1. Look up in HashMap → O(1)                          │
│    2. Remove from freq bucket F                          │
│    3. Add to freq bucket F+1                             │
│    4. If bucket F was min and is now empty, min_freq++   │
│                                                          │
│  PUT(key, value) when full:                              │
│    1. Evict last item in min_freq bucket → O(1)          │
│    2. Insert new item in freq=1 bucket                   │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

| Pros | Cons |
|------|------|
| Keeps truly popular items | More complex (O(1) but tricky implementation) |
| Resists cache pollution from scans | Cold-start problem: new items always have freq=1 |
| Better than LRU for stable popularity | Old popular items may never get evicted (cache ossification) |
| | Frequency counts can become stale |

**When to use**: Workloads with **stable popularity patterns** — CDN caching (popular videos), product catalog (bestsellers), DNS caching.

**Used by**: Redis (supports `allkeys-lfu` policy), HTTP/2 server push prioritization, some CDNs.

### 5.3 FIFO — First In, First Out

Evict the **oldest item** (the one that was cached first), regardless of how recently or frequently it was accessed.

```
Cache capacity: 3

Operations:
  PUT A  → Cache: [A]           (A entered first)
  PUT B  → Cache: [A, B]
  PUT C  → Cache: [A, B, C]    (full!)
  PUT D  → Cache: [B, C, D]    (A evicted — first in)
  GET B  → Cache: [B, C, D]    (B is NOT moved — FIFO ignores access)
  PUT E  → Cache: [C, D, E]    (B evicted — it was oldest remaining)

  ┌──────────────────────────────────┐
  │  FRONT (oldest)      BACK (newest) │
  │  ┌───┐  ┌───┐  ┌───┐             │
  │  │ C │──│ D │──│ E │             │
  │  └───┘  └───┘  └───┘             │
  │  evict              insert        │
  │  here               here          │
  └──────────────────────────────────┘
```

| Pros | Cons |
|------|------|
| Simplest to implement (just a queue) | Ignores access patterns entirely |
| O(1) operations, minimal overhead | Popular items evicted if they were cached early |
| Predictable, deterministic behavior | Worst hit ratio of the three main policies |

**When to use**: When all items have **equal value** and there's no temporal or frequency pattern — message buffers, print queues, simple task queues.

### 5.4 Other Eviction Policies

```
┌────────────────────────────────────────────────────────────────────┐
│                    OTHER EVICTION POLICIES                          │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  TTL (Time To Live)                                                │
│  ─────────────────                                                 │
│  Each item has an expiration timestamp.                            │
│  Evict when TTL expires, regardless of access pattern.             │
│  Used: Redis EXPIRE, HTTP Cache-Control max-age                    │
│  Example: cache.set("session:abc", data, TTL=3600)  // 1 hour     │
│                                                                    │
│  LRU-K (LRU with K references)                                    │
│  ──────────────────────────────                                    │
│  Track the Kth-to-last access time instead of just the last.       │
│  More resistant to one-time scans than plain LRU.                  │
│  Used: Database buffer pools (K=2 is common)                       │
│                                                                    │
│  ARC (Adaptive Replacement Cache)                                  │
│  ────────────────────────────────                                  │
│  Automatically balances between LRU and LFU behavior.              │
│  Maintains two LRU lists: one for "recent" and one for "frequent". │
│  Self-tuning based on workload.                                    │
│  Used: IBM DB2, ZFS filesystem, PostgreSQL                         │
│                                                                    │
│  Random Eviction                                                   │
│  ────────────────                                                  │
│  Pick a random item to evict. Surprisingly good at scale.          │
│  O(1), no bookkeeping overhead.                                    │
│  Used: Redis allkeys-random policy                                 │
│                                                                    │
│  W-TinyLFU (Window Tiny LFU)                                      │
│  ────────────────────────────                                      │
│  Combines LRU admission window + LFU main cache.                   │
│  Uses Count-Min Sketch for frequency estimation (low memory).      │
│  State-of-the-art eviction policy.                                 │
│  Used: Caffeine (Java), Ristretto (Go)                             │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

### Eviction Policy Comparison

| Policy | Considers Recency | Considers Frequency | Complexity | Hit Ratio | Best For |
|--------|:-:|:-:|:-:|:-:|---------|
| **FIFO** | No | No | O(1) | Low | Simple buffers |
| **LRU** | Yes | No | O(1) | Good | General purpose |
| **LFU** | No | Yes | O(1) | Better* | Stable popularity |
| **LRU-K** | Yes (Kth) | Indirectly | O(log n) | Better | DB buffer pools |
| **ARC** | Yes | Yes | O(1) | Very Good | Self-tuning systems |
| **W-TinyLFU** | Yes | Yes | O(1) | Best | High-performance caches |
| **Random** | No | No | O(1) | Decent | Low-overhead, large scale |
| **TTL** | No (time-based) | No | O(1) | N/A | Time-sensitive data |

\* LFU can be worse than LRU if popularity shifts frequently.

### Real-World — Which Companies Use What

```
┌──────────────────────────────────────────────────────────┐
│  Redis:     LRU (default), LFU, Random, TTL              │
│  Memcached: LRU (only option)                             │
│  Caffeine:  W-TinyLFU (best-in-class Java cache)         │
│  Guava:     LRU + size/time eviction                     │
│  CDNs:      LFU variants (Akamai, Cloudflare)            │
│  CPU L1/L2: LRU approximation (pseudo-LRU)               │
│  Linux VM:  Clock (LRU approximation) / ARC              │
│  PostgreSQL: Clock-sweep (ARC variant for buffer pool)    │
│  MySQL:     Midpoint LRU (InnoDB buffer pool)             │
│  Browser:   LRU + TTL (HTTP cache)                        │
└──────────────────────────────────────────────────────────┘
```

---

## 6. Cache Invalidation — The Hardest Problem

> *"There are only two hard things in Computer Science: cache invalidation and naming things."*
> — Phil Karlton

### Invalidation Strategies

```
┌────────────────────────────────────────────────────────────────┐
│                  CACHE INVALIDATION                              │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  1. TTL-BASED (Time To Live)                                   │
│     ─────────────────────────                                  │
│     cache.set("user:123", data, TTL=300)  // expires in 5 min │
│                                                                │
│     Pro: Simple, automatic cleanup                             │
│     Con: Stale for up to TTL duration after DB change          │
│     Use: When brief staleness is acceptable                    │
│                                                                │
│  2. EVENT-BASED (Invalidate on Write)                          │
│     ──────────────────────────────────                         │
│     updateUser(123, newData):                                  │
│       database.update(...)                                     │
│       cache.delete("user:123")    // invalidate!               │
│                                                                │
│     Pro: Immediate consistency                                 │
│     Con: Cache and DB update must be coordinated               │
│     Use: When freshness is critical                            │
│                                                                │
│  3. PUBLISH-SUBSCRIBE                                          │
│     ─────────────────                                          │
│     Database change → Publish event → All cache nodes evict    │
│                                                                │
│     Pro: Works across distributed cache nodes                  │
│     Con: Added infrastructure (Kafka, Redis Pub/Sub)           │
│     Use: Microservices with shared data                        │
│                                                                │
│  4. VERSION-BASED                                              │
│     ──────────────                                             │
│     Cache key: "user:123:v5"                                   │
│     On update: increment version → "user:123:v6"              │
│     Old key expires via TTL; new key has fresh data            │
│                                                                │
│     Pro: No explicit invalidation needed                       │
│     Con: Extra storage for versioned keys                      │
│     Use: CDN cache busting (CSS/JS files)                      │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

### The Double-Delete Problem

```
Scenario: Invalidate-on-write with replication lag

Thread A (write):          Thread B (read):
  t=0ms  Update DB
  t=1ms  Delete cache
                            t=2ms  Cache MISS
                            t=3ms  Read from DB REPLICA
                                   (stale! replication lag)
                            t=4ms  Store stale data in cache!

  Result: Cache now has STALE data permanently until TTL!

Solution — Delayed Double-Delete:
  t=0ms  Delete cache
  t=1ms  Update DB
  ...
  t=500ms Delete cache AGAIN    ← catches any stale re-population
  
  Also set a short TTL (30-60s) as a safety net.
```

---

## 7. Distributed Caching — Scaling Beyond One Node

### How to Distribute Data Across Cache Nodes

```
                    CONSISTENT HASHING FOR CACHE

  Cache Nodes: A, B, C on a hash ring

       0
       │
       ● Node A (pos 1000)
       │
       │    ← hash("user:42") = 1500 → stored on Node B
       │
       ● Node B (pos 2000)
       │
       │    ← hash("product:99") = 2800 → stored on Node C
       │
       ● Node C (pos 3000)
       │

  Adding Node D at pos 2500:
    Only keys between B(2000) and D(2500) move to D
    Other keys: NO change!

  Removing Node B (crash):
    Only keys from B move to next node (C)
    Nodes A and C: unaffected
```

### Redis Cluster Architecture

```
┌──────────────────────────────────────────────────────────┐
│  Redis Cluster — Hash Slots                               │
│                                                          │
│  16,384 hash slots distributed across master nodes:      │
│                                                          │
│  Master A: slots 0 - 5460      (Replica A')              │
│  Master B: slots 5461 - 10922  (Replica B')              │
│  Master C: slots 10923 - 16383 (Replica C')              │
│                                                          │
│  key → CRC16(key) % 16384 = slot → master node          │
│                                                          │
│  "user:42" → CRC16("user:42") % 16384 = 7291            │
│           → slot 7291 → Master B                         │
│                                                          │
│  Adding Master D:                                        │
│    Migrate ~4096 slots from A, B, C to D                 │
│    Live migration — no downtime                          │
│                                                          │
│  If Master B fails:                                      │
│    Replica B' promoted to master                         │
│    Automatic failover in ~15 seconds                     │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## 8. Cache Stampede, Thundering Herd, and Other Pitfalls

### Cache Stampede (Thundering Herd)

```
Problem: Popular cache key expires → thousands of concurrent requests
         ALL hit the database simultaneously

  t=0s    cache.get("top_products") → HIT (data from cache)
  ...
  t=300s  TTL expires! cache.get("top_products") → MISS
  
  Thread 1 → Cache MISS → Query DB    ╲
  Thread 2 → Cache MISS → Query DB     ╲
  Thread 3 → Cache MISS → Query DB      ╲  DATABASE
  ...                                    ╱  OVERWHELMED!
  Thread 500 → Cache MISS → Query DB   ╱
  Thread 501 → Cache MISS → Query DB  ╱

  500 identical queries hit the DB at once → DB crashes → cascading failure
```

**Solutions:**

```
1. LOCK / SINGLE-FLIGHT (best)
   ─────────────────────────────
   First thread to miss acquires a lock.
   Other threads WAIT for that thread to populate the cache.

   Thread 1 → MISS → acquire lock → query DB → populate cache → release lock
   Thread 2 → MISS → wait for lock... → cache HIT from Thread 1's result
   Thread 3 → MISS → wait for lock... → cache HIT from Thread 1's result

   Result: Only 1 DB query instead of 500!


2. EARLY EXPIRATION (stale-while-revalidate)
   ──────────────────────────────────────────
   TTL = 300s, but start refreshing at 270s (90%).
   A background thread refreshes while stale data is still served.

   t=270s  Background: refresh cache from DB
   t=275s  Cache updated with fresh data
   t=300s  TTL would have expired, but cache was already refreshed!

   Result: Cache NEVER empty → no stampede


3. JITTER (randomized TTL)
   ─────────────────────────
   Instead of TTL=300 for all keys:
   TTL = 300 + random(-30, +30)   → each key expires at a different time

   Result: Keys don't expire simultaneously → spreads DB load
```

### Cache Penetration

```
Problem: Requests for data that DOESN'T EXIST in DB
         Every request = cache MISS + useless DB query

  GET user:99999999  → Cache MISS → DB query → null → no cache
  GET user:99999999  → Cache MISS → DB query → null → no cache  (again!)
  ...attackers send millions of non-existent keys → DB crushed

Solutions:
  1. Cache NULL results: cache.set("user:99999999", NULL, TTL=60)
  2. Bloom Filter: check if key MIGHT exist before querying DB
     └── Bloom filter says "definitely not" → skip DB entirely
  3. Input validation: reject obviously invalid keys at API layer
```

### Cache Avalanche

```
Problem: Large portion of cache expires at the same time
         OR entire cache node crashes → massive DB spike

  t=0      Cache loaded with 100K keys, all TTL=3600s
  t=3600s  100K keys expire simultaneously → 100K DB queries!

Solutions:
  1. Jittered TTL: TTL = 3600 + random(-600, +600)
  2. Warm standby cache: failover to backup cache on crash
  3. Rate limit DB queries: circuit breaker on DB access
  4. Multi-layer caching: L1 (in-process) survives L2 (Redis) crash
```

---

## 9. Real-World Caching Architectures

### Facebook / Meta — The Largest Memcached Deployment

```
┌──────────────────────────────────────────────────────────┐
│  Facebook: Serves 2B+ daily users                         │
│                                                          │
│  Cache Layer: Memcached (thousands of servers)            │
│  ├── 75%+ of all reads served from cache                 │
│  ├── TAO: Social graph cache (custom, on top of MySQL)   │
│  ├── Billions of cache operations per second              │
│  └── Custom mcrouter for routing + replication            │
│                                                          │
│  Architecture:                                           │
│  ┌─────────────────────────────────────────────┐         │
│  │  Region: US-East                             │         │
│  │  ┌─────┐  ┌─────┐  ┌─────┐                 │         │
│  │  │ Web │  │ Web │  │ Web │                 │         │
│  │  └──┬──┘  └──┬──┘  └──┬──┘                 │         │
│  │     │        │        │                     │         │
│  │     └────────┼────────┘                     │         │
│  │              ▼                              │         │
│  │  ┌──────────────────┐   mcrouter            │         │
│  │  │  Memcached Pool  │   (routes & batches)  │         │
│  │  │  500+ servers    │                       │         │
│  │  └────────┬─────────┘                       │         │
│  │           │                                 │         │
│  │  ┌────────▼─────────┐                       │         │
│  │  │  MySQL Primary   │                       │         │
│  │  │  + Read Replicas │                       │         │
│  │  └──────────────────┘                       │         │
│  └─────────────────────────────────────────────┘         │
│                                                          │
│  Write Strategy: Write-Around + Invalidation              │
│  ├── Write to MySQL primary                              │
│  ├── MySQL replication event → invalidate Memcached key  │
│  └── Next read → cache miss → load from MySQL            │
│                                                          │
│  Eviction: LRU with slab allocator                        │
│  TTL: Varies by data type (30s for feeds, 24h for profile)│
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Netflix — EVCache (Memcached-Based)

```
┌──────────────────────────────────────────────────────────┐
│  Netflix: 250M+ subscribers                               │
│                                                          │
│  EVCache: Custom caching layer (on top of Memcached)     │
│  ├── 30 million requests/second                          │
│  ├── Sub-millisecond p99 latency                         │
│  ├── Deployed across 3 AWS regions                       │
│  └── 80%+ cache hit ratio for personalization            │
│                                                          │
│  Use Cases:                                              │
│  ├── User session data                                   │
│  ├── Personalized recommendations (pre-computed)          │
│  ├── A/B test configurations                             │
│  ├── Subscriber entitlement data                         │
│  └── Video metadata (titles, thumbnails, ratings)        │
│                                                          │
│  Architecture:                                           │
│  ├── Zone-aware: replicate across AZs for HA             │
│  ├── Write to ALL zones (write-through)                  │
│  ├── Read from LOCAL zone only (low latency)             │
│  ├── Auto-scaling based on memory utilization            │
│  └── Warm-up: pre-populate cache on deploy               │
│                                                          │
│  Eviction: LRU + TTL (different per data type)            │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Twitter / X — Redis for Timelines

```
┌──────────────────────────────────────────────────────────┐
│  Twitter: 500M+ tweets/day                                │
│                                                          │
│  Timeline Cache: Redis (sorted sets)                     │
│                                                          │
│  Fan-out-on-write (for regular users):                   │
│  ├── User A tweets → push tweet ID to ALL followers'     │
│  │   timeline caches (Redis ZADD)                        │
│  ├── 1,000 followers = 1,000 Redis writes                │
│  └── Each follower's timeline is pre-built in Redis      │
│                                                          │
│  Fan-out-on-read (for celebrities):                      │
│  ├── Elon Musk (150M+ followers) can't fan-out on write │
│  ├── Celebrity tweets fetched at read time               │
│  └── Merged with pre-built timeline from Redis           │
│                                                          │
│  Timeline data structure (per user):                     │
│  ├── Redis Sorted Set: ZADD timeline:user42 <score> <tweetId>
│  ├── Score = tweet timestamp (for ordering)              │
│  ├── Keep only last 800 tweets per user (ZREMRANGEBYRANK)│
│  └── Total: ~300M timeline caches in Redis               │
│                                                          │
│  Write Strategy: Write-Behind (batch push to followers)   │
│  Eviction: TTL (inactive users' timelines expire)        │
│  + Size limit (800 tweets max per user)                   │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## 10. Caching Products Comparison

| Product | Type | Eviction | Persistence | Cluster | Best For |
|---------|------|----------|-------------|---------|----------|
| **Redis** | In-memory KV + data structures | LRU, LFU, Random, TTL | RDB + AOF | Redis Cluster (hash slots) | Sessions, leaderboards, queues, pub/sub |
| **Memcached** | In-memory KV | LRU only | None | Client-side sharding | Simple caching, large values |
| **Caffeine** | In-process (Java) | W-TinyLFU | None (in-process) | N/A (per-JVM) | L1 cache, hot data |
| **Hazelcast** | In-memory data grid | LRU, LFU | Disk persistence | Built-in clustering | Distributed computing + caching |
| **Apache Ignite** | In-memory computing | LRU, LFU, FIFO | Disk + SQL | Built-in clustering | SQL cache, compute grid |
| **AWS ElastiCache** | Managed Redis/Memcached | Same as Redis/Memcached | Redis: yes | Multi-AZ | AWS deployments |
| **AWS DAX** | DynamoDB cache | Write-through LRU | N/A (DynamoDB backed) | Multi-AZ | DynamoDB read acceleration |
| **Varnish** | HTTP reverse proxy cache | LRU | Disk | N/A | Full-page caching |
| **CDN** (CloudFront) | Edge cache | LRU + TTL | N/A | Global edge network | Static assets, API responses |

### Redis vs Memcached — When to Choose Which

```
┌──────────────────┬────────────────────┬────────────────────┐
│                  │ Redis              │ Memcached           │
├──────────────────┼────────────────────┼────────────────────┤
│ Data structures  │ Strings, Lists,    │ Strings only        │
│                  │ Sets, Sorted Sets, │                     │
│                  │ Hashes, Streams    │                     │
│ Eviction         │ LRU, LFU, Random   │ LRU only            │
│ Persistence      │ Yes (RDB + AOF)    │ No                  │
│ Clustering       │ Redis Cluster      │ Client-side only    │
│ Pub/Sub          │ Yes                │ No                  │
│ Transactions     │ MULTI/EXEC         │ CAS only            │
│ Lua scripting    │ Yes                │ No                  │
│ Multi-threading  │ Single-threaded*   │ Multi-threaded      │
│ Memory overhead  │ Higher (per-key)   │ Lower (slab alloc)  │
│ Max value size   │ 512 MB             │ 1 MB default        │
├──────────────────┼────────────────────┼────────────────────┤
│ Choose when:     │ Need data structs, │ Simple KV cache,    │
│                  │ persistence, pub/  │ large values,       │
│                  │ sub, flexibility   │ simplicity, multi-  │
│                  │                    │ threaded perf       │
└──────────────────┴────────────────────┴────────────────────┘
  * Redis 6+ uses I/O threads for network, but command execution is single-threaded
```

---

## 11. System Design Interview — Caching Questions

### Common Questions and Answers

**Q: "Where would you add caching in this system?"**
> At multiple layers: (1) Browser/CDN for static assets (images, CSS, JS). (2) API gateway for full response caching with short TTL. (3) Application layer (Caffeine/Guava) for config and hot lookup tables. (4) Distributed cache (Redis) for DB query results, user sessions, computed data. (5) Database layer (materialized views, query cache). Start with the layer that gives the biggest latency reduction for the most common access pattern.

**Q: "Write-through or write-back for this system?"**
> Write-through when consistency is critical (inventory, balances, user profiles) — every write updates both cache and DB. Write-back when write volume is extreme and brief inconsistency is acceptable (like counts, view counters, analytics events) — cache absorbs writes and batch-flushes to DB. Most systems use both: write-through for critical data, write-back for counters.

**Q: "LRU or LFU for this cache?"**
> LRU for general-purpose caching where recent access predicts future access (web sessions, API responses, most CRUD apps). LFU when popularity is stable and you want to protect frequently accessed items from being evicted by one-time scans (CDN, product catalog, DNS). If unsure, start with LRU — it works well for 90% of cases. Redis supports both, so you can switch without code changes.

**Q: "How do you prevent stale data in the cache?"**
> Three complementary strategies: (1) Short TTL (30-300 seconds) as a safety net — eventually all data refreshes. (2) Event-driven invalidation — when data changes, delete or update the cache key immediately. (3) Version-based keys for CDN/static assets. For critical data, also use read-your-own-writes: after a user writes, route their reads to the primary DB for a few seconds.

**Q: "How do you handle a cache failure?"**
> (1) Graceful fallback to database (cache-aside pattern does this naturally). (2) Circuit breaker: if cache is down, bypass it rather than timeout on every request. (3) Multi-AZ replication (Redis Sentinel or Cluster) for automatic failover. (4) L1 in-process cache as backup — survives Redis outages. (5) Rate-limit DB access to prevent stampede when cache recovers.

**Q: "How do you handle cache stampede on a hot key?"**
> (1) Distributed lock (Redis SETNX): only one thread refreshes, others wait. (2) Early refresh: start refreshing at 80% of TTL so cache never fully expires. (3) Stale-while-revalidate: serve stale data while refreshing in background. (4) Request coalescing: group identical pending requests into one DB query.

---

## 12. Quick Reference — Cheat Sheet

### Strategy Decision Matrix

```
What kind of data are you caching?

├── Read-heavy, rarely written?
│   └── Cache-Aside + LRU + TTL (simplest, most common)
│
├── Written frequently, must be immediately readable?
│   └── Write-Through + LRU
│
├── Extreme write volume (counters, analytics)?
│   └── Write-Back + batch flush
│
├── Written once, rarely read?
│   └── Write-Around (don't cache on write)
│
├── Stable popularity (bestsellers, top content)?
│   └── LFU eviction
│
└── Unknown or mixed access patterns?
    └── Start with Cache-Aside + LRU + TTL=300s
        Monitor hit ratio and adjust
```

### One-Line Summaries — Top 5 Caching Strategies

| # | Strategy | One Liner | Best For |
|---|---------|-----------|----------|
| 1 | **Read-Through** | Cache auto-loads from DB on miss; app only talks to cache | CDNs, social feeds, read-heavy apps |
| 2 | **Write-Through** | Write to cache AND DB synchronously; always consistent | Finance apps, inventory, strong consistency |
| 3 | **Cache-Aside** | App checks cache first; on miss, loads from DB and fills cache | General web apps, slight staleness OK |
| 4 | **Write-Around** | Write to DB only; cache populated on next read | Logging, analytics, write-heavy rarely-read |
| 5 | **Write-Back** | Write to cache only; async batch flush to DB; fast but risky | Counters, leaderboards, IoT, high-throughput |

### One-Line Summaries — Other Concepts

| Concept | One Liner |
|---------|-----------|
| **LRU** | Evict whatever was used least recently |
| **LFU** | Evict whatever was used least frequently |
| **FIFO** | Evict whatever was added first |
| **TTL** | Evict after a fixed time, regardless of access |
| **Cache Stampede** | Hot key expires → hundreds of DB queries; solve with locks |
| **Cache Penetration** | Non-existent keys → constant DB misses; solve with Bloom filter |
| **Cache Avalanche** | Mass expiry or cache crash; solve with jittered TTL + fallback |

### Key Numbers

```
┌──────────────────────────────────────────────────────────┐
│  Redis latency:         0.5-1ms (same DC)                │
│  Memcached latency:     0.5-1ms (same DC)                │
│  Caffeine latency:      ~50ns (in-process, JVM heap)     │
│  PostgreSQL query:      5-50ms (indexed)                 │
│  Target hit ratio:      > 95% for production systems     │
│  Redis max memory:      Plan for 70-80% utilization      │
│  Redis Cluster slots:   16,384 hash slots                │
│  Typical TTL range:     30 seconds to 24 hours           │
│  Cache warm-up:         Pre-populate on deploy            │
│  Facebook Memcached:    75%+ reads from cache             │
│  Netflix EVCache:       30M+ requests/second              │
│  Twitter timelines:     300M+ Redis sorted sets           │
└──────────────────────────────────────────────────────────┘
```

### The Golden Rules

1. **Cache the reads, not the writes.** Most apps are 90%+ reads — cache those.
2. **Start with Cache-Aside + LRU + TTL.** It's the right answer 90% of the time.
3. **Short TTL is your safety net.** Even with invalidation, TTL catches bugs.
4. **Monitor hit ratio religiously.** Below 80% means your cache isn't helping much.
5. **Never cache without a TTL.** Stale data without expiration is a ticking bomb.
6. **Warm the cache on deploy.** Cold cache after deployment = latency spike.
7. **Plan for cache failure.** The app must work (slower) without the cache.
8. **One cache key, one owner.** Don't let multiple services write the same key.

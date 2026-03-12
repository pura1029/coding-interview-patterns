# Caching Strategies

## What is it?
Caching stores frequently accessed data in a fast-access layer to reduce latency and backend load. Choosing the right **eviction policy** and **write strategy** is critical for performance, consistency, and scalability.

## Top 5 Caching Strategies You Should Know

### 1. Read-Through
The application checks the cache first. On a cache miss, the **cache itself** fetches data from the DB, stores it, and returns it to the application. The app never talks to the DB directly for reads.

**Best for**: Read-heavy apps like CDNs and social feeds.

### 2. Write-Through
Every write updates both the cache and DB **at the same time**. Ensures cache always stays fresh and consistent with the database. Write is only confirmed after both succeed.

**Best for**: Systems needing strong consistency (e.g., finance apps, inventory counts).

### 3. Cache-Aside (Lazy Loading)
The app looks in the cache first. On a miss, it fetches from the DB and **explicitly** updates the cache. The most common and simplest caching pattern — the application controls the cache.

**Best for**: Read-heavy workloads where slight data staleness is okay.

### 4. Write-Around
Writes go straight to the DB, **skipping the cache**. Cache gets updated only on a subsequent read. Prevents cache pollution from data that is written but rarely read back.

**Best for**: Write-heavy systems with rare immediate reads (e.g., logging, analytics, audit trails).

### 5. Write-Back (Write-Behind)
Writes go to the cache first, and are **asynchronously persisted** to the DB later. Minimizes write latency but introduces risk of data loss if the cache crashes before flushing.

**Best for**: High-performance, write-heavy systems (counters, leaderboards, IoT sensor data).

## Strategy Comparison

| Strategy | Consistency | Write Speed | Read Speed | Data Safety | Best Use Case |
|----------|:-----------:|:-----------:|:----------:|:-----------:|--------------|
| **Read-Through** | Cache-managed | N/A | Fast (cache hit) | Safe | CDNs, social feeds |
| **Write-Through** | Strong | Slow (both) | Fast (always fresh) | Safe | Finance, inventory |
| **Cache-Aside** | Eventual | N/A | Fast (after warmup) | Safe | General web apps |
| **Write-Around** | Eventual | Medium (DB) | Slow (first read) | Safe | Logging, analytics |
| **Write-Back** | Eventual | Fastest (cache only) | Fast | Risk of loss | Counters, IoT |

## Additional Strategies

| Strategy | How it Works | Trade-off |
|----------|-------------|-----------|
| **Refresh-Ahead** | Proactively refresh entries before expiry | Low latency, wasted refreshes if data not needed |

### Eviction Policies
| Policy | Evicts | Best For |
|--------|--------|----------|
| **FIFO** | Oldest inserted entry | Simple workloads with uniform access |
| **LRU** | Least recently accessed | General-purpose, temporal locality |
| **LFU** | Least frequently accessed | Skewed popularity distributions |
| **LRU-K** | Entry with oldest K-th access | Scan-resistant (database buffer pools) |
| **Random** | Random entry | Low overhead, surprisingly effective |
| **TTL** | Entries past time-to-live | Data with known freshness windows |

## When to Use
- Database query results (reduce DB round-trips)
- Session stores and authentication tokens
- API response caching (HTTP caching, CDN)
- DNS resolution caching
- CPU cache simulation and page replacement
- Rate limiting and token buckets
- Distributed systems (consistent hashing, partitioned caches)

## Complexity

| Operation | LRU (LinkedHashMap) | LFU (O(1)) | Bloom Filter |
|-----------|-------------------|-------------|--------------|
| Get | O(1) | O(1) | O(k) |
| Put | O(1) | O(1) | O(k) |
| Eviction | O(1) | O(1) | N/A |
| Space | O(n) | O(n) | O(m) bits |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Simple HashMap Cache | Easy | Basic key-value store with HashMap |
| 2 | FIFO Cache | Easy | Evict oldest inserted entry (insertion order) |
| 3 | LRU Cache (LinkedHashMap) | Easy | Access-order LinkedHashMap with removeEldestEntry |
| 4 | Random Eviction Cache | Easy | Evict a random key when full |
| 5 | Write-Through Cache | Easy | Synchronous write to both cache and database |
| 6 | Cache-Aside (Lazy Loading) | Easy | Load from DB on cache miss, app manages cache |
| 7 | TTL Cache (Time-To-Live) | Easy | Auto-expire entries after a duration |
| 8 | Cache Hit/Miss Counter | Easy | Instrumented cache tracking hit rate |
| 9 | Memoization (Function Cache) | Easy | Cache function results by input |
| 10 | Fibonacci with Memoization | Easy | Classic DP memoization pattern |
| 11 | LFU Cache | Medium | Track access frequency, evict lowest freq |
| 12 | Write-Back Cache (Write-Behind) | Medium | Deferred DB writes with dirty tracking |
| 13 | Write-Around Cache | Medium | Write to DB only, cache populated on read |
| 14 | Read-Through Cache | Medium | Cache auto-loads from backend on miss |
| 15 | Two-Level Cache (L1 + L2) | Medium | Small fast L1 backed by larger L2 |
| 16 | LRU Cache (Manual Doubly-Linked List) | Medium | Hand-rolled DLL + HashMap for O(1) ops |
| 17 | Bloom Filter | Medium | Probabilistic membership test (no false negatives) |
| 18 | Refresh-Ahead Cache | Medium | Proactively refresh data nearing expiry |
| 19 | Consistent Hashing | Medium | Distribute keys across cache nodes with virtual nodes |
| 20 | Bounded Buffer Queue | Medium | Fixed-capacity producer-consumer buffer |
| 21 | LFU Cache (O(1) All Operations) | Hard | Optimal LFU with freq buckets and LinkedHashSet |
| 22 | LRU-K Cache | Hard | Evict by K-th most recent access timestamp |
| 23 | Thread-Safe LRU Cache | Hard | Concurrent access with Collections.synchronizedMap |
| 24 | TTL + LRU Combined Cache | Hard | Expire by time AND evict by recency |
| 25 | CDN Simulator (Geo-Based Caching) | Hard | Edge caches per region with origin fallback |
| 26 | Database Query Cache with Invalidation | Hard | Cache SQL results, invalidate by table mutation |
| 27 | Token Bucket Rate Limiter | Hard | Rate limiting with refillable token bucket |
| 28 | Cuckoo Hashing | Hard | O(1) worst-case lookup with two hash functions |
| 29 | Distributed Cache Partitioning | Hard | Shard data across partitions by key hash |
| 30 | Auto-Warming Cache | Hard | Pre-populate cache with frequently accessed keys |

## Key Insight
> There is no single "best" caching strategy. The right choice depends on your **read/write ratio**, **consistency requirements**, and **data access patterns**. LRU is the default choice for most workloads; LFU excels with skewed popularity; write-back gives lowest latency but risks data loss; write-through guarantees consistency at the cost of write speed.

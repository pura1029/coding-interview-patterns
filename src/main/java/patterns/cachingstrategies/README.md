# Caching Strategies

## What is it?

Caching stores frequently accessed data in a fast-access layer to reduce latency and backend load. Choosing the right **eviction policy** and **write strategy** is critical for performance, consistency, and scalability.

---

## Top 5 Caching Strategies You Should Know

### 1. Read-Through

The application checks the cache first. On a cache miss, the **cache itself** fetches data from the DB, stores it, and returns it. The app never talks to the DB directly for reads — the cache acts as the single data source.

```
             Read Request
                 │
                 ▼
           ┌───────────┐
           │   Cache    │──── HIT ────▶ Return data
           └─────┬─────┘
                 │ MISS
                 ▼
           ┌───────────┐
           │ Cache loads│    (cache fetches from DB,
           │  from DB   │     stores result, returns)
           └─────┬─────┘
                 ▼
           ┌───────────┐
           │  Database  │
           └───────────┘
```

**How it works:**
1. App calls `cache.get(key)`
2. Cache checks internally — if HIT, return value
3. If MISS, the cache itself calls the backing data source (DB, API, file)
4. Cache stores the fetched value, then returns it to the caller
5. Subsequent reads for the same key are served from cache

**Pros:**
- Application code is clean — no manual cache population logic
- Cache is always populated transparently on first access
- Easy to add caching to existing systems (wrap the data source)

**Cons:**
- First read is always slow (cache miss + DB fetch + cache store)
- If the DB schema changes, the cache loader must be updated
- Cache must be configured with a data source / loader function

**When data goes stale:** Cache doesn't know when the DB is updated by another service. Use TTL or invalidation to bound staleness.

**Real-world:** Amazon DynamoDB DAX (transparently proxies DynamoDB reads), Hibernate L2 cache, Spring `@Cacheable` with CacheLoader, Guava `LoadingCache`.

---

### 2. Write-Through

Every write updates **both** the cache **and** the DB **synchronously**. The write is only confirmed after both succeed. The cache never contains data that isn't in the DB.

```
             Write Request
                 │
                 ▼
           ┌───────────┐
           │   Cache    │◄───── write ─────┐
           │ (update)   │                  │
           └─────┬─────┘                  │
                 │                         │
                 ▼ (synchronous)           │
           ┌───────────┐                  │
           │  Database  │◄── write ────── App
           │ (update)   │
           └───────────┘
           Both succeed → ACK to caller
```

**How it works:**
1. App calls `cache.put(key, value)`
2. Cache writes value to its local store
3. Cache **immediately** writes value to the database
4. Only when **both** writes succeed does the operation return success
5. Reads always find fresh data in the cache

**Pros:**
- **Strong consistency** — cache and DB are always in sync
- Reads are always fast (cache is always warm with latest writes)
- No risk of data loss from cache failures (DB always has the data)

**Cons:**
- **Higher write latency** — every write does two I/O operations (cache + DB)
- Write throughput is bottlenecked by the slower of the two stores (usually DB)
- If DB write fails, must roll back cache write (transaction complexity)

**Failure scenario:** If the DB write fails after the cache write succeeds, the cache has stale data. Solutions: wrap both in a transaction, or write DB first then cache.

**Real-world:** Amazon DynamoDB DAX (write-through mode), CPU L1/L2 caches (small caches use write-through to L3), financial trading systems, inventory management (Shopify), configuration stores.

---

### 3. Cache-Aside (Lazy Loading)

The **most common** caching pattern. The application is responsible for all cache interactions — it checks the cache, fetches from DB on miss, and explicitly puts the result into the cache. The cache has no knowledge of the database.

```
             Read Request
                 │
                 ▼
           ┌───────────┐
      ┌─── │   Cache    │──── HIT ────▶ Return data
      │    └───────────┘
      │ MISS
      ▼
 ┌───────────┐     put(key, value)     ┌───────────┐
 │  Database  │ ─────────────────────▶ │   Cache    │
 │  (fetch)   │                        │  (update)  │
 └───────────┘                         └───────────┘
      │
      ▼
  Return data to caller
```

**How it works:**
1. App calls `cache.get(key)` — if HIT, return value
2. If MISS, app fetches from DB directly: `db.query(key)`
3. App explicitly stores in cache: `cache.put(key, value)`
4. App returns value to caller
5. Writes go **directly** to the DB — the cache is not updated on writes (unless app explicitly does so)

**Pros:**
- **Simplest** to implement and reason about
- Cache only contains data that is actually requested (no wasted space)
- Cache failure is graceful — app falls back to DB
- Works with any cache system (Redis, Memcached, local HashMap)

**Cons:**
- **Cache miss penalty** — first request is always slow (3 steps: check cache, fetch DB, store cache)
- **Stale data** — if DB is updated, the cache still holds old value until TTL expires or manual invalidation
- Application code is more complex (must manage cache reads and writes)
- **Cache stampede** risk — many concurrent cache misses for same key all hit DB simultaneously

**Mitigation for stale data:**
- Set a TTL on cache entries
- On DB write, also update or invalidate the cache entry
- Use pub/sub to notify caches of DB changes

**Real-world:** This is what ~80% of production systems use. Facebook's Memcached layer, Twitter's cache tier, Netflix Zuul, any Redis-based caching in web apps, Spring `@Cacheable` (default mode).

---

### 4. Write-Around

Writes go **directly** to the database, **completely bypassing** the cache. The cache is only populated when data is read (via Cache-Aside or Read-Through). This prevents the cache from being filled with data that is written but never (or rarely) read back.

```
             Write Request                Read Request
                 │                             │
                 ▼                             ▼
           ┌───────────┐                ┌───────────┐
           │  Database  │◄── write      │   Cache    │── HIT ──▶ Return
           │  (direct)  │               └─────┬─────┘
           └───────────┘                      │ MISS
                                              ▼
                                        ┌───────────┐
                                        │  Database  │── fetch ──▶ Cache + Return
                                        └───────────┘
         Cache is NOT updated            Cache populated on read
         on write                        only
```

**How it works:**
1. Write: App writes directly to DB — cache is untouched (or entry is invalidated)
2. Read: App checks cache first; on miss, fetches from DB and populates cache (Cache-Aside)
3. The cache only ever contains data that has been *read* at least once after being written

**Pros:**
- **No cache pollution** — write-heavy data that is rarely read doesn't waste cache space
- DB always has the authoritative latest data
- Great for append-heavy workloads (logs, events, analytics)

**Cons:**
- **Read-after-write is slow** — immediately reading data you just wrote always misses the cache
- Not suitable when reads frequently follow writes (e.g., user profile update then display)
- Higher read latency for recently written data

**When to use vs. when NOT to:**
- USE when write:read ratio is high (10:1 or more) — logging, audit trails, event sourcing
- AVOID when users expect to see their own writes immediately (social media posts, order status)

**Real-world:** Analytics pipelines (write events to DB, only cache aggregated reports), audit logging systems, IoT sensor data ingestion, write-heavy time-series databases.

---

### 5. Write-Back (Write-Behind)

Writes go to the **cache first** and are acknowledged immediately. The cache **asynchronously flushes** dirty entries to the database in the background, either periodically or when certain thresholds are reached.

```
             Write Request
                 │
                 ▼
           ┌───────────┐
           │   Cache    │◄── write (instant ACK)
           │ (mark dirty│
           └─────┬─────┘
                 │ (async, batched, later)
                 ▼
           ┌───────────┐
           │  Database  │◄── background flush
           └───────────┘
```

**How it works:**
1. App calls `cache.put(key, value)` — cache stores value and marks it **dirty**
2. Cache immediately returns success to the caller (ultra-low latency)
3. A background thread/timer periodically scans dirty entries
4. Dirty entries are flushed to the database in batches
5. After successful DB write, the dirty flag is cleared

**Pros:**
- **Fastest write latency** — only a local cache write, no DB round-trip
- **Write coalescing** — multiple rapid writes to the same key result in only one DB write (the latest value)
- **Batch writes** — background flush can batch many writes into one DB transaction
- Reduces DB write load significantly under heavy write traffic

**Cons:**
- **Risk of data loss** — if the cache crashes before flushing, dirty entries are lost
- **Eventual consistency** — reads from the DB (not cache) may see stale data
- Complex failure handling — what if the background flush fails?
- Harder to debug — writes appear successful but may fail later

**Failure mitigation:**
- Write-ahead log (WAL) for dirty entries before acknowledging
- Replicated cache (Redis Cluster, Hazelcast) to survive single-node failure
- Periodic checkpointing with acknowledgment

**Real-world:** CPU L1/L2 caches (write-back to main memory), Linux page cache (`dirty_writeback_centisecs`), database write-ahead logs, Redis with AOF persistence, Elasticsearch refresh interval, game leaderboard counters, IoT sensor aggregation.

---

## Strategy Comparison


| Strategy          | Consistency   | Write Speed          | Read Speed          | Data Safety  | Best Use Case         |
| ----------------- | ------------- | -------------------- | ------------------- | ------------ | --------------------- |
| **Read-Through**  | Cache-managed | N/A                  | Fast (cache hit)    | Safe         | CDNs, social feeds    |
| **Write-Through** | Strong        | Slow (both)          | Fast (always fresh) | Safe         | Finance, inventory    |
| **Cache-Aside**   | Eventual      | N/A                  | Fast (after warmup) | Safe         | General web apps      |
| **Write-Around**  | Eventual      | Medium (DB)          | Slow (first read)   | Safe         | Logging, analytics    |
| **Write-Back**    | Eventual      | Fastest (cache only) | Fast                | Risk of loss | Counters, IoT, games  |


### How to Choose a Caching Strategy

```
Does the app read the same data much more often than it writes?
├── YES → Read-Through or Cache-Aside
│         ├── Want simple app code? → Read-Through
│         └── Want full control?    → Cache-Aside (most common)
└── NO (write-heavy) → Continue...

Must reads immediately see the latest writes?
├── YES → Write-Through (strong consistency, slower writes)
└── NO → Continue...

Is write latency critical (sub-millisecond)?
├── YES → Write-Back (fastest writes, risk of data loss)
└── NO → Continue...

Are writes rarely read back (logs, events, analytics)?
├── YES → Write-Around (prevents cache pollution)
└── NO → Cache-Aside + TTL (general-purpose default)
```

### Combined Strategies in Production

Most production systems combine two or more strategies:

| Combination                       | How                                                          | Example                                   |
| --------------------------------- | ------------------------------------------------------------ | ----------------------------------------- |
| **Cache-Aside + TTL**             | Cache-Aside for reads, TTL for freshness                     | 80% of web app caching (Redis + TTL)      |
| **Read-Through + Write-Through**  | Cache handles both reads and writes transparently            | DynamoDB DAX, Hibernate L2 cache          |
| **Cache-Aside + Write-Around**    | Reads populate cache; writes go to DB only                   | Analytics dashboards                      |
| **Write-Back + Read-Through**     | Writes buffered in cache; reads auto-load from DB            | Gaming leaderboards, IoT platforms        |
| **Cache-Aside + Write-Through**   | Reads from cache; writes update cache + DB synchronously     | E-commerce product catalog                |

---

## Advanced Caching Concepts

### Cache Invalidation

*"There are only two hard things in Computer Science: cache invalidation and naming things."* — Phil Karlton

| Approach                 | How                                                         | Trade-off                                  |
| ------------------------ | ----------------------------------------------------------- | ------------------------------------------ |
| **TTL-based**            | Entries expire after a fixed time                           | Simple but data can be stale until expiry   |
| **Event-based**          | DB change triggers cache delete/update (CDC, pub/sub)       | Fresh but requires infrastructure           |
| **Version-based**        | Each entry has a version; stale versions are rejected       | Precise but adds complexity per entry       |
| **Tag-based**            | Entries tagged by category; invalidate all entries by tag   | Bulk invalidation; good for related data    |
| **Write-invalidate**     | On write, delete cache entry (next read repopulates)        | Simple; slight delay on next read           |
| **Write-update**         | On write, update cache entry directly                       | Faster reads; risk of race conditions       |

### Cache Stampede (Thundering Herd)

When a popular cache entry expires, **hundreds of concurrent requests** all miss the cache simultaneously and flood the database.

```
TTL expires on popular key "user:123"
     │
     ├── Request 1 → MISS → query DB
     ├── Request 2 → MISS → query DB     ← All hit DB at once!
     ├── Request 3 → MISS → query DB
     ├── ...
     └── Request N → MISS → query DB
```

**Solutions:**

| Solution                 | How                                                          |
| ------------------------ | ------------------------------------------------------------ |
| **Locking / Mutex**      | First thread locks, fetches from DB, populates cache. Others wait. |
| **Probabilistic refresh**| Recompute *before* TTL expires with probability that increases as expiry approaches |
| **Stale-while-revalidate** | Return stale data immediately; refresh in background       |
| **Request coalescing**   | Deduplicate concurrent cache misses for the same key         |

### Cache Penetration

Requests for keys that **don't exist** in the DB bypass the cache every time (always miss) and repeatedly hit the database.

```
Request for key "nonexistent:999"
     │
     ├── Cache → MISS
     ├── Database → NOT FOUND
     ├── Cache is NOT populated (nothing to cache)
     └── Next request → same cycle repeats
```

**Solutions:**

| Solution                 | How                                                          |
| ------------------------ | ------------------------------------------------------------ |
| **Cache null values**    | Store `key → null` with a short TTL (prevents repeated DB hits) |
| **Bloom filter**         | Check if key *might* exist before querying DB. If not in filter, skip DB. |
| **Input validation**     | Reject obviously invalid keys at the API layer               |

### Cache Avalanche

A **mass expiration** event where many cache entries expire at the same time, causing a sudden flood of DB queries.

```
1000 keys all set with TTL=60s at the same time
     │
     At t=60s → ALL expire simultaneously
     └── 1000 concurrent DB queries → DB overload
```

**Solutions:**

| Solution                 | How                                                          |
| ------------------------ | ------------------------------------------------------------ |
| **Jittered TTL**         | Add random offset to TTL: `TTL = base + random(0, spread)` — staggers expirations |
| **Multi-level cache**    | L1 (local) + L2 (distributed) — L1 absorbs burst even if L2 expires |
| **Circuit breaker**      | If DB error rate spikes, serve stale cache and back off      |
| **Pre-warming**          | Proactively refresh popular keys before they expire          |

---

## Additional Strategies


| Strategy            | How it Works                                          | Trade-off                                        |
| ------------------- | ----------------------------------------------------- | ------------------------------------------------ |
| **Refresh-Ahead**   | Proactively refresh entries before expiry              | Low latency, wasted refreshes if data not needed |
| **Two-Level Cache** | L1 (local/fast) + L2 (distributed/large)              | Fast reads, complexity of consistency across levels |
| **Sidecar Cache**   | Cache runs as a sidecar process alongside the app     | Language-agnostic, network hop to sidecar        |
| **Near Cache**      | Local in-process cache backed by distributed cache     | Ultra-fast reads, staleness between nodes        |
| **Auto-Warming**    | Pre-populate cache on startup from DB or access logs   | No cold-start penalty, may cache unused data     |


### Eviction Policies — In Depth

When a cache is full and a new entry needs to be stored, the cache must decide **which existing entry to remove**. This decision is the **eviction policy**. Picking the right policy directly impacts your cache hit rate, and even a few percentage points of improvement can translate into massive performance gains at scale.

---

#### 1. FIFO (First-In, First-Out)

**How it works:** Evicts the entry that was *inserted first*, regardless of how often or recently it was accessed. Think of it as a queue — the oldest entry in the cache is always removed first.

**Data Structure:** `LinkedHashMap` (insertion order) or `Queue` + `HashMap`

```
Cache [A, B, C] (capacity 3)
Insert D → evict A (oldest inserted)
Cache [B, C, D]
```

**Pros:**
- Simplest to implement (just a queue)
- O(1) insertion and eviction
- No bookkeeping overhead per access

**Cons:**
- Ignores access patterns entirely — a heavily used entry gets evicted if it was inserted early
- Poor hit rate for workloads with temporal locality

**Real-world:** Operating system page replacement (the "second chance" algorithm is a FIFO variant), simple message queues, streaming data windows.

---

#### 2. LRU (Least Recently Used)

**How it works:** Evicts the entry that has not been accessed (read or written) for the longest time. Every access moves the entry to the "most recently used" end. The idea: if you haven't touched it recently, you probably won't need it soon.

**Data Structure:** `LinkedHashMap(accessOrder=true)` or `HashMap` + `Doubly-Linked List`

```
Cache [A, B, C] (capacity 3)
Access B → B moves to end: [A, C, B]
Insert D → evict A (least recently used)
Cache [C, B, D]
```

**Pros:**
- Great general-purpose policy — works well for most workloads
- Exploits temporal locality (recently accessed data is likely needed again)
- O(1) all operations with HashMap + DLL

**Cons:**
- A one-time scan of many items pollutes the cache (e.g., reading a large report once pushes out hot data)
- Doesn't consider frequency — an item accessed once recently beats one accessed 1000 times slightly earlier

**Real-world:** Redis default eviction, Memcached, CPU caches (approximated), browser caches, Android `LruCache`, LinkedIn's Couchbase layer.

---

#### 3. LFU (Least Frequently Used)

**How it works:** Evicts the entry with the fewest total accesses. Tracks a counter per entry; every get/put increments it. When eviction is needed, the entry with the lowest count is removed. On ties, the oldest among the least frequent is evicted.

**Data Structure:** `HashMap<Key, Value+Freq>` + `HashMap<Freq, LinkedHashSet<Key>>` + `minFreq` tracker

```
Cache: A(freq=5), B(freq=2), C(freq=8)
Insert D → evict B (lowest frequency = 2)
Cache: A(freq=5), C(freq=8), D(freq=1)
```

**Pros:**
- Excellent for skewed popularity — keeps truly popular items in cache
- Resistant to scan pollution (a one-time scan of items only gives them freq=1)
- O(1) get/put/evict with the optimal bucket-based implementation

**Cons:**
- "Frequency inertia" — previously hot items that are no longer relevant stay cached because of high historical counts
- Cold start problem — new entries start with freq=1 and may be evicted before they prove useful
- More complex to implement correctly than LRU

**Real-world:** CDN edge caches (popular content stays cached), database buffer pools where certain queries dominate, in-memory caches for recommendation engines.

---

#### 4. LRU-K (Least Recently Used — K-th Access)

**How it works:** Instead of evicting by the most recent access (LRU-1), it tracks the **K-th most recent access time** for each entry and evicts the entry whose K-th access is the oldest. Typically K=2 is used. An entry that has been accessed fewer than K times is always evicted first.

**Data Structure:** `HashMap<Key, List<Timestamp>>` (keeps last K access times per key)

```
K=2
Cache: A(accesses: [t5, t10]), B(accesses: [t3, t8]), C(accesses: [t1])
Insert D → evict C first (fewer than K accesses)
If all have K accesses → evict B (oldest 2nd access: t3)
```

**Pros:**
- Scan-resistant: a one-time access doesn't give an entry the same priority as a repeatedly accessed one
- Better than LRU for database buffer pools with mixed query patterns
- Academically proven to be closer to optimal than LRU for many workloads

**Cons:**
- Higher memory overhead (stores K timestamps per entry)
- More complex eviction logic
- K must be tuned per workload

**Real-world:** PostgreSQL buffer manager (uses a clock-sweep variant), Oracle database buffer cache, disk page replacement systems.

---

#### 5. Random Eviction

**How it works:** When eviction is needed, pick a random entry and remove it. No access tracking, no ordering, no bookkeeping.

**Data Structure:** `HashMap` + `ArrayList` of keys (for O(1) random selection)

```
Cache [A, B, C, D, E] (capacity 5)
Insert F → pick random (e.g., C) → evict C
Cache [A, B, D, E, F]
```

**Pros:**
- Zero bookkeeping overhead — no counters, no linked lists, no timestamps
- O(1) eviction with constant factor smaller than LRU/LFU
- Surprisingly competitive with LRU for many workloads (~90% of LRU's hit rate with much less overhead)
- No worst-case degenerate behavior (unlike LRU with scans)

**Cons:**
- Can evict hot items — purely probabilistic
- No intelligence about access patterns
- Hit rate slightly lower than LRU/LFU on average

**Real-world:** ARM processor TLBs, some Redis configurations (`volatile-random`, `allkeys-random`), research systems where simplicity matters more than optimal hit rate.

---

#### 6. TTL (Time-To-Live)

**How it works:** Each entry has an expiration timestamp. When accessed after expiry, the entry is treated as a miss and removed. Optionally, a background thread scans and proactively removes expired entries.

**Data Structure:** `HashMap<Key, Value>` + `HashMap<Key, ExpirationTime>` (or a single map with timestamped values)

```
Cache: A(expires=t+60s), B(expires=t+30s), C(expires=t+120s)
At t+35s → B expired, removed on next access or background sweep
```

**Pros:**
- Guarantees data freshness — stale data automatically disappears
- Essential for security tokens, sessions, DNS records
- Can combine with any other eviction policy (TTL + LRU is very common)

**Cons:**
- Not a capacity-based eviction — doesn't help when cache is full with non-expired entries
- Choosing the right TTL is hard: too short = low hit rate, too long = stale data
- Background cleanup can introduce latency spikes

**Real-world:** Redis `EXPIRE` command, DNS TTL records, HTTP `Cache-Control: max-age`, JWT token caches, CDN edge caches, session stores.

---

#### 7. TLRU (Time-aware Least Recently Used)

**How it works:** Combines LRU with TTL. Each entry has both an access order position and an expiration time. Eviction prefers expired entries first; among non-expired, evicts the least recently used. This prevents stale entries from occupying cache space even if they were recently accessed.

**Real-world:** Web proxy caches, API gateway caches where both freshness and recency matter.

---

#### 8. ARC (Adaptive Replacement Cache)

**How it works:** Maintains two LRU lists — one for entries accessed once (recency), one for entries accessed multiple times (frequency). Dynamically adjusts the size split between the two lists based on the workload, adapting to changing access patterns automatically.

**Real-world:** IBM DB2 buffer pool, ZFS file system (Solaris/FreeBSD), some enterprise storage systems.

---

### Eviction Policy Comparison

| Policy     | Evicts                         | Hit Rate     | Overhead     | Scan-Resistant | Best For                                 |
| ---------- | ------------------------------ | ------------ | ------------ | -------------- | ---------------------------------------- |
| **FIFO**   | Oldest inserted entry          | Low-Medium   | Very Low     | No             | Simple workloads, uniform access         |
| **LRU**    | Least recently accessed        | High         | Low (O(1))   | No             | General-purpose, temporal locality       |
| **LFU**    | Least frequently accessed      | Very High    | Medium       | Yes            | Skewed popularity distributions          |
| **LRU-K**  | Entry with oldest K-th access  | Very High    | Medium-High  | Yes            | Database buffer pools, mixed workloads   |
| **Random** | Random entry                   | Medium       | Negligible   | Partially      | Low overhead, hardware caches            |
| **TTL**    | Entries past time-to-live      | N/A          | Low          | N/A            | Data with known freshness windows        |
| **TLRU**   | Expired first, then LRU       | High         | Low          | No             | API caches, web proxies                  |
| **ARC**    | Adaptive (recency vs freq)     | Very High    | Medium       | Yes            | Self-tuning workloads, databases         |

### How to Choose an Eviction Policy

```
Is data freshness critical?
├── YES → Use TTL (combine with LRU or LFU for capacity limits)
└── NO → Continue...

Is access pattern skewed (some items much more popular)?
├── YES → Use LFU or ARC
└── NO → Continue...

Is the workload scan-heavy (large sequential reads)?
├── YES → Use LRU-K (K=2) or ARC (resists scan pollution)
└── NO → Continue...

Is implementation simplicity the priority?
├── YES → Use LRU (best balance of simplicity and performance)
└── NO → Continue...

Is overhead a critical constraint (embedded/hardware)?
├── YES → Use Random (near-zero overhead)
└── NO → Use LRU as default
```

### Common Combinations in Production

| Combination       | How                                            | Used By                        |
| ----------------- | ---------------------------------------------- | ------------------------------ |
| **TTL + LRU**     | Expire stale, evict least recent among valid   | Redis, Memcached, Caffeine     |
| **TTL + LFU**     | Expire stale, evict least popular among valid  | CDN edge caches                |
| **LRU + LFU (ARC)** | Self-tuning balance between recency and frequency | ZFS, IBM DB2              |
| **FIFO + TTL**    | Expire by age, simple queue for capacity       | Log caches, message queues     |
| **LRU + Bloom**   | Bloom filter pre-screens; LRU caches positives | Database query caches          |


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
| --------- | ------------------- | ---------- | ------------ |
| Get       | O(1)                | O(1)       | O(k)         |
| Put       | O(1)                | O(1)       | O(k)         |
| Eviction  | O(1)                | O(1)       | N/A          |
| Space     | O(n)                | O(n)       | O(m) bits    |


## Examples (30)


| #   | Problem                                | Difficulty | Key Idea                                              |
| --- | -------------------------------------- | ---------- | ----------------------------------------------------- |
| 1   | Simple HashMap Cache                   | Easy       | Basic key-value store with HashMap                    |
| 2   | FIFO Cache                             | Easy       | Evict oldest inserted entry (insertion order)         |
| 3   | LRU Cache (LinkedHashMap)              | Easy       | Access-order LinkedHashMap with removeEldestEntry     |
| 4   | Random Eviction Cache                  | Easy       | Evict a random key when full                          |
| 5   | Write-Through Cache                    | Easy       | Synchronous write to both cache and database          |
| 6   | Cache-Aside (Lazy Loading)             | Easy       | Load from DB on cache miss, app manages cache         |
| 7   | TTL Cache (Time-To-Live)               | Easy       | Auto-expire entries after a duration                  |
| 8   | Cache Hit/Miss Counter                 | Easy       | Instrumented cache tracking hit rate                  |
| 9   | Memoization (Function Cache)           | Easy       | Cache function results by input                       |
| 10  | Fibonacci with Memoization             | Easy       | Classic DP memoization pattern                        |
| 11  | LFU Cache                              | Medium     | Track access frequency, evict lowest freq             |
| 12  | Write-Back Cache (Write-Behind)        | Medium     | Deferred DB writes with dirty tracking                |
| 13  | Write-Around Cache                     | Medium     | Write to DB only, cache populated on read             |
| 14  | Read-Through Cache                     | Medium     | Cache auto-loads from backend on miss                 |
| 15  | Two-Level Cache (L1 + L2)              | Medium     | Small fast L1 backed by larger L2                     |
| 16  | LRU Cache (Manual Doubly-Linked List)  | Medium     | Hand-rolled DLL + HashMap for O(1) ops                |
| 17  | Bloom Filter                           | Medium     | Probabilistic membership test (no false negatives)    |
| 18  | Refresh-Ahead Cache                    | Medium     | Proactively refresh data nearing expiry               |
| 19  | Consistent Hashing                     | Medium     | Distribute keys across cache nodes with virtual nodes |
| 20  | Bounded Buffer Queue                   | Medium     | Fixed-capacity producer-consumer buffer               |
| 21  | LFU Cache (O(1) All Operations)        | Hard       | Optimal LFU with freq buckets and LinkedHashSet       |
| 22  | LRU-K Cache                            | Hard       | Evict by K-th most recent access timestamp            |
| 23  | Thread-Safe LRU Cache                  | Hard       | Concurrent access with Collections.synchronizedMap    |
| 24  | TTL + LRU Combined Cache               | Hard       | Expire by time AND evict by recency                   |
| 25  | CDN Simulator (Geo-Based Caching)      | Hard       | Edge caches per region with origin fallback           |
| 26  | Database Query Cache with Invalidation | Hard       | Cache SQL results, invalidate by table mutation       |
| 27  | Token Bucket Rate Limiter              | Hard       | Rate limiting with refillable token bucket            |
| 28  | Cuckoo Hashing                         | Hard       | O(1) worst-case lookup with two hash functions        |
| 29  | Distributed Cache Partitioning         | Hard       | Shard data across partitions by key hash              |
| 30  | Auto-Warming Cache                     | Hard       | Pre-populate cache with frequently accessed keys      |


## Key Insight

> There is no single "best" caching strategy. The right choice depends on your **read/write ratio**, **consistency requirements**, and **data access patterns**. LRU is the default choice for most workloads; LFU excels with skewed popularity; write-back gives lowest latency but risks data loss; write-through guarantees consistency at the cost of write speed.


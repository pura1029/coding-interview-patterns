# Senior Java Interview — 20 Real Production Questions

> Based on production-grade scenarios that separate senior engineers from mid-level developers.
> These are NOT LeetCode — they test real-world systems thinking.

**Topics:** Multithreading, Kafka, Microservices, AWS/Azure, System Design, Design Patterns, Caching, Transactions, NoSQL/SQL, Deployments

---

## Scoring Guide

| Score | Level |
|-------|-------|
| 18-20 | Staff Engineer material |
| 13-17 | Senior Ready |
| 6-12 | Mid-Level, keep grinding |
| 0-5 | Bookmark and revisit in 3 months |

---

## Q1: ThreadPoolExecutor with LinkedBlockingQueue — Tasks Rejected

**Category:** Multithreading | **Difficulty:** Hard

### Scenario

Your service has a `ThreadPoolExecutor` with `corePoolSize=10`, `maxPoolSize=50`, and a `LinkedBlockingQueue`. Under peak load, you notice tasks are being rejected. What's wrong and how do you fix it?

### Root Cause

`LinkedBlockingQueue` is **unbounded by default** (`Integer.MAX_VALUE` capacity). The `ThreadPoolExecutor` behavior is:

```
1. If threads < corePoolSize → create new thread
2. If threads >= corePoolSize → put task in queue
3. If queue is FULL → create thread up to maxPoolSize
4. If threads >= maxPoolSize AND queue is full → REJECT

With LinkedBlockingQueue (unbounded):
  Step 2 NEVER fills → Step 3 NEVER triggers
  maxPoolSize=50 is USELESS — only 10 threads ever run!

  ┌────────────────────────────────────────────────────┐
  │ Core Threads (10)                                   │
  │ [T1][T2][T3][T4][T5][T6][T7][T8][T9][T10]         │
  │                                                     │
  │ Queue (LinkedBlockingQueue — UNBOUNDED)              │
  │ [task][task][task][task]...[task] ← millions of     │
  │ tasks queue up, threads never scale beyond 10!      │
  │                                                     │
  │ Max Threads (50) ← NEVER reached!                  │
  └────────────────────────────────────────────────────┘
```

The rejection happens because the queue eventually **runs out of memory** (OOM), not because `maxPoolSize` was hit.

### Solutions

```
FIX 1: Use ArrayBlockingQueue with bounded capacity

  new ThreadPoolExecutor(
      10,                          // corePoolSize
      50,                          // maxPoolSize (NOW actually used!)
      60, TimeUnit.SECONDS,        // keepAliveTime
      new ArrayBlockingQueue<>(100) // bounded queue — fills → triggers scaling
  );

  Flow: 10 core threads busy → queue fills to 100 → creates up to 50 threads

FIX 2: Use SynchronousQueue (zero capacity)

  new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
      new SynchronousQueue<>()     // immediately hand off to new thread
  );

  Flow: if no free thread, immediately create new one (up to 50)

FIX 3: Use CallerRunsPolicy as rejection handler

  new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
      new ArrayBlockingQueue<>(100),
      new ThreadPoolExecutor.CallerRunsPolicy()  // caller thread runs task
  );

  Instead of rejecting → calling thread runs the task itself (backpressure)
```

### ThreadPoolExecutor Decision Flowchart

```
New task submitted
      │
      ▼
┌─────────────────────────┐
│ Active threads <         │──── YES ──► Create new CORE thread, run task
│ corePoolSize?            │
└─────────┬───────────────┘
          │ NO
          ▼
┌─────────────────────────┐
│ Queue has space?         │──── YES ──► Put task in queue, wait for free thread
│                          │
└─────────┬───────────────┘
          │ NO (queue full)
          ▼
┌─────────────────────────┐
│ Active threads <         │──── YES ──► Create new NON-CORE thread, run task
│ maxPoolSize?             │
└─────────┬───────────────┘
          │ NO (max threads reached)
          ▼
┌─────────────────────────┐
│ RejectedExecutionHandler │
│ ├── AbortPolicy (default)│──► throws RejectedExecutionException
│ ├── CallerRunsPolicy     │──► caller thread executes the task
│ ├── DiscardPolicy        │──► silently drops the task
│ └── DiscardOldestPolicy  │──► drops oldest queued task
└─────────────────────────┘
```

### Queue Type Comparison

```
┌──────────────────────┬──────────────────────┬───────────────────────┐
│  LinkedBlockingQueue  │  ArrayBlockingQueue   │  SynchronousQueue     │
│  (unbounded)          │  (bounded)            │  (zero capacity)      │
├──────────────────────┼──────────────────────┼───────────────────────┤
│ maxPoolSize IGNORED  │ maxPoolSize WORKS ✅  │ maxPoolSize WORKS ✅  │
│ Tasks queue forever  │ Tasks queue up to N   │ NO queuing at all     │
│ OOM risk ❌          │ Controlled memory ✅  │ Immediate handoff     │
│ 10 threads always    │ Scales 10 → 50       │ Scales 10 → 50       │
│                      │                      │ Rejects if all busy   │
├──────────────────────┼──────────────────────┼───────────────────────┤
│ Use: never in prod   │ Use: most APIs ✅     │ Use: fire-and-forget  │
└──────────────────────┴──────────────────────┴───────────────────────┘
```

### Production Best Practice

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    corePoolSize,
    maxPoolSize,
    60, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(queueCapacity),
    new ThreadPoolExecutor.CallerRunsPolicy()  // backpressure
);
executor.allowCoreThreadTimeOut(true);  // reclaim idle core threads
```

### Monitoring Metrics (Must-Have)

```
┌──────────────────────────────────────────────────┐
│ Thread Pool Dashboard                             │
│                                                    │
│ Active Threads:    ██████████████░░░░░  28/50      │
│ Queue Size:        ████████░░░░░░░░░░  42/100     │
│ Completed Tasks:   1,234,567                       │
│ Rejected Tasks:    0 ✅ (alert if > 0)             │
│ Pool Size:         28 (core=10, max=50)            │
│                                                    │
│ JMX beans to expose:                               │
│ ├── getActiveCount()                               │
│ ├── getQueue().size()                              │
│ ├── getCompletedTaskCount()                        │
│ └── custom RejectionHandler counter                │
└──────────────────────────────────────────────────┘
```

---

## Q2: Distributed Deadlock — Two Services Sharing a Database

**Category:** Microservices | **Difficulty:** Hard

### Scenario

Two microservices share a database. Service A holds a row lock while calling Service B via REST, which also tries to acquire the same lock. Production is frozen. How do you diagnose and architect your way out?

### Root Cause — Distributed Deadlock

```
Service A                    Service B
  │                             │
  ├── BEGIN TX                  │
  ├── LOCK row R1 ✅            │
  ├── REST call to B ──────────►│
  │   (waiting for response)    ├── BEGIN TX
  │                             ├── LOCK row R1 ❌ (blocked!)
  │◄─────── (waiting) ──────── │   (A holds lock, B waits)
  │                             │
  └── DEADLOCK! Both waiting forever
```

### Diagnosis Steps

1. **DB-level:** `SELECT * FROM pg_locks` — find blocked/blocking queries
2. **Application-level:** Thread dumps on both services — find threads blocked on HTTP response + DB lock
3. **Tracing:** Distributed trace (Jaeger/Zipkin) shows circular dependency

### Solutions

```
SOLUTION 1: Database per Service (Best — eliminates the root cause)

  BEFORE (shared DB = deadlock risk):
  ┌───────────┐    ┌───────────┐
  │ Service A │──┐ │ Service B │──┐
  └───────────┘  │ └───────────┘  │
                 └───────┬────────┘
                         ▼
                  ┌────────────┐
                  │ Shared DB  │ ← contention, deadlocks
                  └────────────┘

  AFTER (separate DBs = no shared locks):
  ┌───────────┐         ┌───────────┐
  │ Service A │         │ Service B │
  └─────┬─────┘         └─────┬─────┘
        ▼                     ▼
  ┌──────────┐         ┌──────────┐
  │   DB_A   │         │   DB_B   │  ← zero contention
  └──────────┘         └──────────┘

  Coordination via events (Kafka/RabbitMQ), not shared locks.

SOLUTION 2: Saga Pattern (event-driven coordination)

  ┌──────────┐   event    ┌──────────┐   event    ┌──────────┐
  │Service A │──────────►│ Message  │──────────►│Service B │
  │          │           │  Queue   │           │          │
  │ 1.Update │           │(Kafka/   │           │ 1.Consume│
  │   own DB │           │ SQS)     │           │ 2.Update │
  │ 2.Publish│           │          │           │   own DB │
  │   event  │           │          │           │ 3.Publish│
  └──────────┘           └──────────┘           │   result │
                                                └──────────┘
  Each service commits its own transaction independently.
  No lock held during cross-service communication.
  If B fails → compensating transaction reverses A.

SOLUTION 3: Async Messaging (decouple entirely)
  Service A ──(message queue)──► Service B
  A commits its transaction and publishes a message.
  B processes independently in its own transaction.
  No synchronous call within a lock = no deadlock.

SOLUTION 4: Short-Term Fix — Lock Ordering + Timeouts
  Always acquire locks in a consistent order (e.g., by row ID).
  Add timeouts: SELECT ... FOR UPDATE NOWAIT (or with timeout).
  Detect: SET lock_timeout = '5s';
```

### Anti-Pattern Detection Checklist

```
❌ Service holds DB lock WHILE making REST/gRPC call
❌ Two services write to the SAME table
❌ Transaction spans multiple service boundaries
❌ Synchronous calls between services that share state
❌ No timeout on SELECT ... FOR UPDATE

✅ Each service owns its data exclusively
✅ Cross-service communication via async events
✅ Transactions scoped to a single service
✅ Lock timeouts configured (lock_timeout, statement_timeout)
✅ Distributed tracing enabled (Jaeger/Zipkin)
```

---

## Q3: Kafka Consumer Lag — 10 Million Messages

**Category:** Kafka | **Difficulty:** Hard

### Scenario

A Kafka consumer group is lagging by 10 million messages. Increasing consumer instances doesn't help. Partition count equals consumer count. What are all the possible root causes and remediation steps?

### Diagnosis

```
If partition count = consumer count, adding MORE consumers is useless:

  Topic: 6 partitions
  Consumer Group: 6 consumers (one per partition)
  Consumer #7 → sits IDLE (no partition to consume)

  ┌──────────┐
  │ Partition │ → Consumer 1
  │ Partition │ → Consumer 2
  │ Partition │ → Consumer 3     6 consumers = max parallelism
  │ Partition │ → Consumer 4     7th consumer has nothing to do
  │ Partition │ → Consumer 5
  │ Partition │ → Consumer 6
  └──────────┘
```

### Root Causes and Fixes

| Root Cause | Diagnosis | Fix |
|------------|-----------|-----|
| **Partition count ceiling** | Consumer count ≥ partition count | Increase partitions (careful: rebalances keys) |
| **Slow processing per message** | Consumer processing time > 100ms | Optimize processing, batch DB writes, async I/O |
| **GC pauses** | Long GC pauses → consumer kicked from group | Tune GC (-XX:MaxGCPauseMillis), increase `session.timeout.ms` |
| **Rebalance storms** | Frequent rebalancing causes downtime | Use static group membership (`group.instance.id`), increase `session.timeout.ms` |
| **Offset commit blocking** | Synchronous commits after each message | Use async commits with periodic sync commit |
| **Downstream bottleneck** | DB/API calls in consumer are slow | Decouple: consume → write to local queue → batch process |
| **Deserialization cost** | Large messages (Avro/Protobuf) expensive to deserialize | Use schema caching, optimize message size |
| **Max.poll.records too low** | Fetching 1 message at a time | Increase `max.poll.records` to 500-1000 |

### Architecture Fix — Decouple Consumption from Processing

```
BEFORE (slow — 500 msg/sec):

  ┌─────────┐   poll   ┌──────────┐   1 row   ┌──────┐
  │  Kafka  │────────►│ Consumer │──────────►│  DB  │
  │ Broker  │         │          │  per msg  │      │
  └─────────┘         │ process  │ (2ms each)│      │
                      │ commit   │           │      │
                      └──────────┘           └──────┘
  Bottleneck: DB round-trip per message = 2ms × 500 = 1 second

AFTER (fast — 10,000+ msg/sec):

  ┌─────────┐  poll    ┌──────────┐  buffer   ┌────────────┐
  │  Kafka  │────────►│ Consumer │────────►│ In-Memory  │
  │ Broker  │ 500 msgs│          │         │   Buffer   │
  └─────────┘         └──────────┘         └──────┬─────┘
                                                   │ flush every
                                                   │ 100ms or 500 rows
                                                   ▼
                                           ┌────────────┐
                                           │  Batch DB  │
                                           │  INSERT    │ 500 rows = 5ms
                                           └────────────┘
                                                   │
                                                   ▼
                                           commit offset
```

### Kafka Consumer Tuning Cheatsheet

```
┌──────────────────────────┬───────────────┬─────────────────────────┐
│ Property                 │ Default       │ Recommended             │
├──────────────────────────┼───────────────┼─────────────────────────┤
│ max.poll.records         │ 500           │ 500-1000                │
│ max.poll.interval.ms     │ 300000 (5min) │ match processing time   │
│ session.timeout.ms       │ 45000         │ 30000-60000             │
│ heartbeat.interval.ms    │ 3000          │ session.timeout / 3     │
│ fetch.min.bytes          │ 1             │ 1024-65536              │
│ fetch.max.wait.ms        │ 500           │ 100-500                 │
│ enable.auto.commit       │ true          │ false (manual commit)   │
│ group.instance.id        │ null          │ set for static members  │
│ auto.offset.reset        │ latest        │ earliest (for replay)   │
└──────────────────────────┴───────────────┴─────────────────────────┘
```

---

## Q4: Singleton Cache in Kubernetes — Cache Inconsistency

**Category:** Design Patterns | **Difficulty:** Hard

### Scenario

Your monolith uses the Singleton pattern for a cache manager. After moving to Kubernetes with 20 pods, cache inconsistency causes data corruption. How do you refactor without downtime?

### Root Cause

```
Monolith (1 JVM):              Kubernetes (20 pods):

  ┌────────────────┐           ┌──────┐ ┌──────┐ ┌──────┐
  │  CacheManager  │           │Cache1│ │Cache2│ │Cache3│ ... x20
  │  (Singleton)   │           └──────┘ └──────┘ └──────┘
  │  {A: 100}      │           {A:100}   {A:200}  {A:100}
  └────────────────┘           
  ONE truth                    20 DIFFERENT truths! ❌

  Pod 1 updates A=200. Pod 2 still has A=100.
  User hits Pod 2 → sees stale data → makes wrong decision.
```

### Solutions

```
SOLUTION 1: Centralized Cache (Redis / Memcached)

  ┌──────┐  ┌──────┐  ┌──────┐
  │ Pod1 │  │ Pod2 │  │ Pod3 │
  └──┬───┘  └──┬───┘  └──┬───┘
     └──────────┼──────────┘
                ▼
         ┌───────────┐
         │   Redis    │   ← single source of truth
         │  {A: 200}  │
         └───────────┘

  All pods read/write from Redis. Consistent.

SOLUTION 2: Distributed Cache with Event-Driven Invalidation

  Pod1 updates A → publishes "INVALIDATE:A" to Kafka/Redis Pub-Sub
  Pod2 receives event → evicts A from local cache
  Pod3 receives event → evicts A from local cache
  Next read → fetches fresh value from DB and caches locally

SOLUTION 3: Two-Level Cache (L1 local + L2 Redis)

  Read: check L1 (local, fast) → miss → check L2 (Redis) → miss → DB
  Write: update DB → update L2 → publish invalidation to all L1s

  Benefits: L1 gives < 1ms reads, L2 ensures consistency
```

### Migration Strategy (Zero Downtime)

```
Phase 1 ──► Phase 2 ──► Phase 3 ──► Phase 4 ──► Phase 5

Phase 1: DEPLOY REDIS (no code changes)
┌──────┐       ┌───────────┐
│ Pods │──────►│ Local     │   Redis deployed but NOT used yet.
│      │       │ Singleton │   Validate Redis connectivity.
└──────┘       └───────────┘   ┌─────────┐
                               │  Redis  │ (warming up)
                               └─────────┘

Phase 2: DUAL WRITE (write to both, read from local)
┌──────┐──write──►┌───────────┐
│ Pods │          │ Local     │   Reads still from local.
│      │──write──►│           │   Writes go to BOTH.
└──────┘          └───────────┘   Verify data parity.
     │               ┌─────────┐
     └──write──────►│  Redis  │
                    └─────────┘

Phase 3: SWITCH READS (read from Redis, fallback local)
┌──────┐──read───►┌─────────┐
│ Pods │          │  Redis  │   Primary reads from Redis.
│      │          └─────────┘   Local cache as fallback.
└──────┘──fallback►┌───────────┐
                  │ Local     │
                  └───────────┘

Phase 4: REMOVE LOCAL SINGLETON
┌──────┐──────────►┌─────────┐
│ Pods │           │  Redis  │   All reads AND writes.
│      │           └─────────┘   Singleton code deleted.
└──────┘

Phase 5: PRODUCTION STABLE ✅
  All 20 pods → single Redis cluster → consistent data
```

### Real-World Example: E-Commerce Product Catalog

```
BEFORE (Singleton in monolith):
  User A → Pod 3 → local cache → price = $99 (old)
  User B → Pod 7 → local cache → price = $79 (new, after sale started)
  Same product, DIFFERENT prices shown to users! ❌

AFTER (Redis):
  Admin updates price → Redis key "product:123:price" = $79
  User A → Pod 3  → Redis → $79 ✅
  User B → Pod 7  → Redis → $79 ✅
  User C → Pod 12 → Redis → $79 ✅  Consistent everywhere.

  With L1 (local) + L2 (Redis):
  Admin updates price → Redis updated → Pub/Sub "INVALIDATE product:123"
  All pods receive → clear local L1 → next read fetches from Redis → L1 cached
  Result: consistent data with sub-millisecond reads after first fetch.
```

---

## Q5: API Gateway — Malicious Client Rotating IPs

**Category:** API Gateway | **Difficulty:** Hard

### Scenario

Your API Gateway is rate-limiting correctly per user, but a single malicious client bypasses it by rotating IPs. The backend is getting hammered. What layered defenses do you implement?

### The Problem

```
Normal rate limiting (per IP):
  IP 1.1.1.1 → 100 req/min ✅ → BLOCKED after limit

Attacker rotating IPs:
  IP 1.1.1.1 → 50 requests
  IP 2.2.2.2 → 50 requests    Each IP under limit,
  IP 3.3.3.3 → 50 requests    but total = 500 req/min!
  ...10 IPs  → 500 req/min ❌
```

### Layered Defense Architecture

```
Layer 1: WAF / CDN Edge (Cloudflare, AWS WAF)
  ├── Bot detection (CAPTCHA challenge for suspicious patterns)
  ├── Device fingerprinting (browser, TLS fingerprint, headers)
  ├── Geographic anomaly detection
  └── Request signature analysis (identical patterns across IPs)

Layer 2: API Gateway
  ├── Rate limit per API key / JWT token (not just IP)
  ├── Token bucket per authenticated user
  ├── Behavioral analysis: request patterns, timing, endpoints
  └── Adaptive rate limiting: lower limits for suspicious behavior

Layer 3: Application
  ├── Circuit breaker: protect downstream services
  ├── Request validation: reject malformed/suspicious payloads
  ├── Account-level throttling: regardless of source IP
  └── Anomaly detection: ML-based traffic pattern analysis

Layer 4: Database
  ├── Connection pool limits
  ├── Query timeouts
  └── Read replicas for heavy reads
```

### Specific Techniques

| Technique | How It Works |
|-----------|-------------|
| **Device Fingerprinting** | Track browser/OS/TLS version — same fingerprint across IPs = same client |
| **JWT-based Rate Limiting** | Rate limit per authenticated token, not IP — can't bypass by changing IP |
| **Behavioral Analysis** | Same request pattern (timing, endpoints, order) across IPs → flag |
| **CAPTCHA Challenge** | After suspicious activity threshold, require CAPTCHA proof |
| **Penalty Box** | Escalating bans: 1 min → 5 min → 1 hour → permanent |

### Defense-in-Depth Visualization

```
Internet Traffic
      │
      ▼
┌─────────────────────────────────────────────────┐
│ LAYER 1: CDN / WAF (Cloudflare, AWS Shield)     │
│                                                   │
│  ┌───────────┐  ┌───────────┐  ┌──────────────┐ │
│  │ Bot       │  │ TLS       │  │ Geo-fence    │ │
│  │ Detection │  │ Fingerpr. │  │ Anomaly      │ │
│  │ (JS chal.)│  │ ja3 hash  │  │ Check        │ │
│  └─────┬─────┘  └─────┬─────┘  └──────┬───────┘ │
│        └───────────────┼───────────────┘         │
│                        ▼                          │
│  Block 90% of automated attacks here              │
└────────────────────────┬────────────────────────┘
                         │ legitimate + sophisticated attacks
                         ▼
┌─────────────────────────────────────────────────┐
│ LAYER 2: API Gateway                             │
│                                                   │
│  ┌───────────┐  ┌───────────┐  ┌──────────────┐ │
│  │ JWT/API   │  │ Token     │  │ Adaptive     │ │
│  │ Key Rate  │  │ Bucket    │  │ Rate Limit   │ │
│  │ Limit     │  │ per User  │  │ (behavior)   │ │
│  └─────┬─────┘  └─────┬─────┘  └──────┬───────┘ │
│        └───────────────┼───────────────┘         │
│                        ▼                          │
│  Block accounts exceeding limits                  │
└────────────────────────┬────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────┐
│ LAYER 3: Application                             │
│                                                   │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐ │
│  │ Circuit    │  │ Account    │  │ ML Anomaly │ │
│  │ Breaker    │  │ Throttle   │  │ Detection  │ │
│  └────────────┘  └────────────┘  └────────────┘ │
└────────────────────────┬────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────┐
│ LAYER 4: Database / Downstream Protection        │
│  Connection pool limits, query timeouts,         │
│  read replicas for heavy reads                   │
└─────────────────────────────────────────────────┘
```

### Real-World Example: GitHub API Rate Limiting

```
GitHub uses multi-dimensional rate limiting:

┌──────────────────────────────────────────────────────────────┐
│ Dimension        │ Limit                │ Response Header    │
├──────────────────┼──────────────────────┼────────────────────┤
│ Authenticated    │ 5,000 req/hour       │ X-RateLimit-Limit  │
│ Unauthenticated  │ 60 req/hour per IP   │ X-RateLimit-Remain │
│ GraphQL          │ 5,000 points/hour    │ X-RateLimit-Reset  │
│ Search API       │ 30 req/min           │ Retry-After        │
│ Code Scanning    │ 1,000 req/hour       │                    │
└──────────────────┴──────────────────────┴────────────────────┘

When limit exceeded:
  HTTP 403 Forbidden
  {
    "message": "API rate limit exceeded for user.",
    "documentation_url": "https://docs.github.com/..."
  }

  Retry-After: 3600  (seconds until reset)

Key: Rate limit is per AUTHENTICATED USER (token),
     not per IP — IP rotation doesn't help!
```

---

## Q6: Spring @Transactional Self-Invocation — Silent Rollback Bug

**Category:** Transactions | **Difficulty:** Hard

### Scenario

A `@Transactional` method in Spring calls another `@Transactional` method in the same class. The inner transaction rollback is silently ignored. Explain why and describe 3 solutions.

### Root Cause — AOP Proxy Bypass

```
Spring uses a PROXY to intercept @Transactional calls:

External call (via proxy):
  Controller ──► [Proxy] ──► Service.methodA()
                  ↑ Proxy intercepts: BEGIN TX, handle rollback

Self-invocation (bypasses proxy):
  Service.methodA() ──► this.methodB()    ← direct call!
                        ↑ No proxy! @Transactional is IGNORED!

  @Service
  public class OrderService {
      @Transactional
      public void processOrder() {
          // ... do stuff ...
          this.updateInventory();  // ← BYPASSES PROXY!
      }

      @Transactional(propagation = REQUIRES_NEW)
      public void updateInventory() {
          // This @Transactional annotation has NO EFFECT
          // because it's called via "this" not the proxy
      }
  }
```

### Three Solutions

```
SOLUTION 1: Self-Injection

  @Service
  public class OrderService {
      @Autowired
      private OrderService self;  // inject proxy of itself

      @Transactional
      public void processOrder() {
          self.updateInventory();  // calls through proxy ✅
      }

      @Transactional(propagation = REQUIRES_NEW)
      public void updateInventory() { ... }
  }

SOLUTION 2: Extract to Separate Service

  @Service
  public class OrderService {
      @Autowired
      private InventoryService inventoryService;

      @Transactional
      public void processOrder() {
          inventoryService.updateInventory();  // different bean → proxy works ✅
      }
  }

  @Service
  public class InventoryService {
      @Transactional(propagation = REQUIRES_NEW)
      public void updateInventory() { ... }
  }

SOLUTION 3: AspectJ Weaving (Compile-Time or Load-Time)

  @EnableTransactionManagement(mode = AdviceMode.ASPECTJ)

  AspectJ weaves the transaction logic INTO the class bytecode.
  No proxy needed → self-invocation works ✅
  
  Trade-off: more complex build setup (AspectJ compiler)
```

### Proxy vs Direct Call Visualization

```
═══════════════════════════════════════════════════════════════
  HOW SPRING AOP PROXY WORKS
═══════════════════════════════════════════════════════════════

EXTERNAL CALL (through proxy — @Transactional WORKS ✅):

  ┌────────────┐       ┌────────────────────────────────────┐
  │ Controller │──────►│ Spring Proxy (CGLIB / JDK Dynamic) │
  │            │       │                                    │
  │ orderSvc.  │       │  1. Intercept method call          │
  │ process()  │       │  2. BEGIN TRANSACTION               │
  │            │       │  3. Invoke real method ──────────┐ │
  └────────────┘       │  4. If success → COMMIT          │ │
                       │  5. If exception → ROLLBACK      │ │
                       │                                  ▼ │
                       │  ┌──────────────────────────────┐ │
                       │  │     Real OrderService        │ │
                       │  │     processOrder()            │ │
                       │  └──────────────────────────────┘ │
                       └────────────────────────────────────┘

SELF-INVOCATION (bypasses proxy — @Transactional IGNORED ❌):

  ┌────────────────────────────────────┐
  │ Spring Proxy                       │
  │                                    │
  │  processOrder() called via proxy ✅│
  │      │                             │
  │      ▼                             │
  │  ┌──────────────────────────────┐ │
  │  │     Real OrderService        │ │
  │  │                               │ │
  │  │  processOrder() {             │ │
  │  │    this.updateInventory(); ◄──┼─┼── "this" = real object,
  │  │  }                           │ │    NOT the proxy!
  │  │                               │ │    No interception.
  │  │  @Transactional(REQUIRES_NEW) │ │    @Transactional
  │  │  updateInventory() { ... }    │ │    has NO effect!
  │  └──────────────────────────────┘ │
  └────────────────────────────────────┘
```

### Solution Comparison Matrix

```
┌─────────────────────┬──────────────┬──────────┬───────────────┐
│ Solution            │ Complexity   │ Safety   │ Performance   │
├─────────────────────┼──────────────┼──────────┼───────────────┤
│ Self-Injection      │ ★☆☆ Low     │ ★★☆     │ ★★★ Best     │
│ (@Autowired self)   │ Quick fix    │ Circular │ Same as proxy │
│                     │              │ dep risk │               │
├─────────────────────┼──────────────┼──────────┼───────────────┤
│ Extract to Service  │ ★★☆ Medium  │ ★★★     │ ★★★ Best     │
│ (separate bean)     │ Refactoring  │ Clean    │ Standard      │
│                     │ needed       │ design   │ proxy call    │
├─────────────────────┼──────────────┼──────────┼───────────────┤
│ AspectJ Weaving     │ ★★★ High   │ ★★★     │ ★★☆          │
│ (compile-time)      │ Build setup  │ Works    │ Compile-time  │
│                     │ AspectJ      │ always   │ overhead      │
├─────────────────────┼──────────────┼──────────┼───────────────┤
│ TransactionTemplate │ ★★☆ Medium  │ ★★★     │ ★★★ Best     │
│ (programmatic TX)   │ Manual TX    │ Explicit │ No proxy      │
│                     │ management   │ control  │ overhead      │
└─────────────────────┴──────────────┴──────────┴───────────────┘

 Recommended: Extract to separate service (Solution 2)
               Clean, follows SRP, no circular dependencies.
```

### Other Spring AOP Proxy Pitfalls

```
Same proxy bypass issue affects:
  ├── @Cacheable — self-invocation skips cache lookup
  ├── @Async      — self-invocation runs synchronously
  ├── @Retryable  — self-invocation skips retry logic
  └── @Secured    — self-invocation skips security checks

All annotation-driven AOP features require calls to go
THROUGH THE PROXY to be intercepted.
```

---

## Q7: Blue-Green Deployment — DB Schema Migration Errors

**Category:** Deployment | **Difficulty:** Hard

### Scenario

During a blue-green deployment, 5% of users see 500 errors post-cutover. Rollback also causes errors. DB schema migration ran before switching traffic. What went wrong?

### Root Cause

```
BEFORE deployment:
  Blue (v1) ──► DB (schema v1)     ← all users here

Migration step:
  Run DB migration → schema v2 (destructive: dropped column, renamed table)

AFTER cutover:
  Green (v2) ──► DB (schema v2)    ← new users here (works ✅)
  Blue (v1) ──► DB (schema v2)     ← straggler requests FAIL ❌

ROLLBACK attempt:
  Blue (v1) ──► DB (schema v2)     ← v1 code can't read v2 schema ❌
  
  The DESTRUCTIVE migration made rollback impossible!
```

### Solution — Expand/Contract Pattern

```
Phase 1: EXPAND (backward compatible)
  ┌────────────────────────────────────────────────┐
  │ Add new column (keep old column)                │
  │ ALTER TABLE orders ADD COLUMN status_v2 VARCHAR;│
  │ Both v1 and v2 code work with the schema       │
  └────────────────────────────────────────────────┘

Phase 2: MIGRATE DATA
  ┌────────────────────────────────────────────────┐
  │ Backfill new column from old column             │
  │ UPDATE orders SET status_v2 = old_status;       │
  │ Dual-write: new code writes to BOTH columns    │
  └────────────────────────────────────────────────┘

Phase 3: SWITCH (cutover)
  ┌────────────────────────────────────────────────┐
  │ New code reads from new column                  │
  │ Old code still works (old column still exists)  │
  │ SAFE to rollback at this point ✅               │
  └────────────────────────────────────────────────┘

Phase 4: CONTRACT (cleanup — days/weeks later)
  ┌────────────────────────────────────────────────┐
  │ Drop old column (once confident rollback not    │
  │ needed). ALTER TABLE orders DROP COLUMN old;    │
  └────────────────────────────────────────────────┘
```

### Expand/Contract Timeline Visualization

```
═════════════════════════════════════════════════════════════════════
  WEEK 1          WEEK 2          WEEK 3          WEEK 4+
═════════════════════════════════════════════════════════════════════

DB Schema:
  v1 (old col)    v1+v2 (both)    v1+v2 (both)    v2 only
                  ←─ EXPAND ─►    ←─ MIGRATE ─►   ←─ CONTRACT

Code:
  Blue (v1)       Blue (v1) ✅    Green (v2) ✅   Green (v2) ✅
                  Green (v2) ✅   Blue (v1) ✅     Blue retired

Traffic:
  100% Blue       Blue → Green    100% Green      100% Green
                  (gradual)       (rollback safe)

Rollback safe?
  ✅ Yes          ✅ Yes          ✅ Yes           ❌ No (old col gone)
═════════════════════════════════════════════════════════════════════
```

### Real-World Example: Renaming a Column

```
Goal: Rename column "status" → "order_status"

❌ WRONG (destructive — breaks during rollback):
  ALTER TABLE orders RENAME COLUMN status TO order_status;
  → Blue (v1) code: SELECT status FROM orders → ERROR! Column gone!

✅ RIGHT (expand/contract):
  Week 1 — EXPAND:
    ALTER TABLE orders ADD COLUMN order_status VARCHAR;
    ┌──────────────────────────────────────┐
    │ orders table                          │
    │ id │ status │ order_status │ amount   │
    │  1 │ PAID   │ NULL         │ 99.00    │  ← old col has data
    └──────────────────────────────────────┘

  Week 2 — MIGRATE DATA + DUAL WRITE:
    UPDATE orders SET order_status = status WHERE order_status IS NULL;
    New code writes to BOTH columns.
    ┌──────────────────────────────────────┐
    │ orders table                          │
    │ id │ status │ order_status │ amount   │
    │  1 │ PAID   │ PAID         │ 99.00    │  ← both have data
    └──────────────────────────────────────┘

  Week 3 — SWITCH:
    New code reads from order_status.
    Old code still works (reads from status).
    ROLLBACK IS SAFE at this point.

  Week 4+ — CONTRACT:
    ALTER TABLE orders DROP COLUMN status;
    Only after confirming no rollback needed.
```

### Rules for Safe Schema Migrations

| Do | Don't |
|----|-------|
| Add nullable columns | Drop columns during deployment |
| Add new tables | Rename columns/tables |
| Add indexes concurrently | Add NOT NULL without default |
| Dual-write during transition | Run destructive DDL before cutover |
| Use online DDL tools (pt-osc, gh-ost) | ALTER large tables without tooling |
| Test migration on staging with prod data | Apply migrations during peak traffic |

---

## Q8: Distributed Rate Limiter — 500M Users, 50 Data Centers

**Category:** System Design | **Difficulty:** Expert

### Scenario

Design a distributed rate limiter for 500M users, 1M RPS, that works across 50 geographically distributed data centers with <5ms latency budget for the rate check itself.

### Architecture

```
                    ┌─────────────────────┐
                    │  Global Coordinator  │
                    │  (async sync every   │
                    │   5-10 seconds)      │
                    └────────┬────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        ▼                    ▼                    ▼
  ┌──────────┐        ┌──────────┐        ┌──────────┐
  │ DC: US   │        │ DC: EU   │        │ DC: Asia │
  │          │        │          │        │          │
  │ Local    │        │ Local    │        │ Local    │
  │ Redis    │        │ Redis    │        │ Redis    │
  │ Cluster  │        │ Cluster  │        │ Cluster  │
  └──────────┘        └──────────┘        └──────────┘

  Each data center has LOCAL Redis for <1ms rate checks.
  Global sync happens ASYNCHRONOUSLY (eventual consistency).
```

### Design Decisions

```
1. ALGORITHM: Token Bucket per user
   - Allows bursts, smooth sustained rate
   - State: {tokens: int, lastRefill: timestamp}
   - Each check: refill tokens, decrement, allow/deny → O(1)

2. LOCAL-FIRST with GLOBAL SYNC
   - Rate check: LOCAL Redis only (< 1ms latency)
   - Background: sync counts to global coordinator every 5-10s
   - Trade-off: user can get ~2x limit across DCs in worst case
     (acceptable for 99.9% of use cases)

3. CONSISTENCY vs LATENCY
   - STRICT consistency: every check goes to central store
     → 50-200ms latency ❌ (violates <5ms requirement)
   - EVENTUAL consistency: local check + async sync
     → <1ms latency ✅ (slight over-allowance acceptable)

4. MEMORY: 500M users × 16 bytes = ~8 GB per DC
   - Fits in a single Redis node
   - Shard by user_id hash for horizontal scaling

5. FAILOVER: if local Redis fails
   - Fall back to in-memory rate limiting (per pod)
   - Slightly less accurate but service stays up
```

### Token Bucket Visualization

```
Token Bucket for user "alice" (limit: 10 tokens, refill: 1 token/sec)

Time 0s: bucket full                    Time 3s: 3 requests consumed
┌─────────────────┐                     ┌─────────────────┐
│ ● ● ● ● ● ● ●  │ 10/10 tokens       │ ● ● ● ● ● ● ●  │ 7/10 tokens
│ ● ● ●           │                     │                  │
└─────────────────┘                     └─────────────────┘

Time 10s: burst of 8 requests           Time 11s: 1 token refilled
┌─────────────────┐                     ┌─────────────────┐
│ ●               │ 2/10 tokens         │ ● ●             │ 3/10 tokens
│                  │ (7+3 refill=10,    │                  │ (2 + 1 refill)
│                  │  then -8 = 2)      │                  │
└─────────────────┘                     └─────────────────┘

Time 11s: request arrives               Time 11s: request arrives
   tokens=3 → ALLOW ✅                     tokens=0 → DENY 429 ❌
   tokens=2 (decremented)                  "Rate limit exceeded"
```

### Cross-DC Sync Strategy

```
═════════════════════════════════════════════════════════════════════
  LOCAL-FIRST RATE LIMITING WITH GLOBAL SYNC
═════════════════════════════════════════════════════════════════════

User "alice" — Global limit: 100 req/min

Allocated per DC (proportional to traffic):
  US-East: 40 req/min    EU-West: 35 req/min    Asia: 25 req/min

Scenario: Alice sends 45 requests from US-East

  US-East Local Redis:
    alice: 45/40 → DENY (over local allocation) ❌
    
    But globally, alice has only used 45/100 — room for 55 more!

  Solution: "Borrowing" from global pool
    US-East asks Global: "Alice needs 10 more tokens"
    Global: "OK, reallocating: US=50, EU=25, Asia=25"
    US-East: alice: 45/50 → ALLOW ✅

  ┌──────────────────────────────────────────────────────────────┐
  │ Global Coordinator (every 5-10 seconds)                      │
  │                                                               │
  │ User    │ Global Limit │ US-East │ EU-West │ Asia            │
  │ alice   │ 100/min      │ 50      │ 25      │ 25              │
  │ bob     │ 200/min      │ 80      │ 60      │ 60              │
  │ carol   │ 50/min       │ 20      │ 15      │ 15              │
  └──────────────────────────────────────────────────────────────┘

  Trade-off: Between sync intervals, user can exceed limit by
  ~2x in worst case (requests across all DCs simultaneously).
  For 99.9% of users, this is acceptable.
═════════════════════════════════════════════════════════════════════
```

### Token Bucket Implementation (Pseudocode)

```
function checkRateLimit(userId, limit, windowSec):
    key = "rate:" + userId
    now = currentTimeSeconds()

    // Atomic Redis operation (Lua script for thread safety)
    tokens = redis.get(key + ":tokens") ?? limit
    lastRefill = redis.get(key + ":time") ?? now

    elapsed = now - lastRefill
    refill = elapsed * (limit / windowSec)
    tokens = min(limit, tokens + refill)

    if tokens >= 1:
        redis.set(key + ":tokens", tokens - 1)
        redis.set(key + ":time", now)
        redis.expire(key, windowSec * 2)
        return ALLOW
    else:
        return DENY (429 + Retry-After header)
```

### Algorithm Comparison for Rate Limiting at Scale

```
┌─────────────────┬──────────────┬──────────────┬────────────────┐
│ Algorithm       │ Burst        │ Memory       │ Best For       │
│                 │ Handling     │              │                │
├─────────────────┼──────────────┼──────────────┼────────────────┤
│ Token Bucket    │ ✅ Allows    │ O(1) per user│ API rate limits│
│                 │ controlled   │ 16 bytes     │ general purpose│
│                 │ bursts       │              │                │
├─────────────────┼──────────────┼──────────────┼────────────────┤
│ Sliding Window  │ ✅ Smooth    │ O(N) per user│ Financial APIs │
│ Log             │ no bursts    │ stores all   │ strict limits  │
│                 │              │ timestamps   │                │
├─────────────────┼──────────────┼──────────────┼────────────────┤
│ Fixed Window    │ ❌ 2x burst  │ O(1) per user│ Simple metrics │
│ Counter         │ at boundary  │ 8 bytes      │ analytics      │
├─────────────────┼──────────────┼──────────────┼────────────────┤
│ Leaky Bucket    │ ❌ No bursts │ O(1) per user│ Network QoS    │
│                 │ constant rate│ 16 bytes     │ steady streams │
└─────────────────┴──────────────┴──────────────┴────────────────┘
```

---

## Q9: PostgreSQL 50ms → 45s — No Code Changes

**Category:** SQL/NoSQL | **Difficulty:** Hard

### Scenario

A PostgreSQL query that ran in 50ms now takes 45 seconds. No code changes deployed. Table grew from 1M to 80M rows. `EXPLAIN ANALYZE` shows `Seq Scan`. Walk through your full diagnosis and fix.

### Diagnosis Workflow

```
Step 1: Run EXPLAIN ANALYZE
  Before: Index Scan using idx_orders_status on orders (cost=0.42..8.44 rows=5)
  After:  Seq Scan on orders (cost=0.00..1823456.00 rows=80000000)
          Filter: (status = 'PENDING')

  The planner CHOSE Seq Scan over Index Scan. Why?

Step 2: Check table statistics
  SELECT n_live_tup, n_dead_tup, last_autovacuum, last_autoanalyze
  FROM pg_stat_user_tables WHERE relname = 'orders';

  If last_autoanalyze is old → statistics are STALE.
  Planner thinks table has 1M rows but it actually has 80M.
  With stale stats, planner estimates wrong row counts → bad plan.

Step 3: Check for bloat
  Dead rows from updates/deletes accumulate.
  Table physically 50GB but only 8GB of live data → index becomes inefficient.
  Planner may prefer Seq Scan on a bloated table.

Step 4: Check index health
  SELECT pg_relation_size('idx_orders_status');
  Index may be bloated too. After many updates, B-tree pages split
  and never merge back.
```

### Fixes

| Fix | Command | When |
|-----|---------|------|
| **Update statistics** | `ANALYZE orders;` | Stale stats (most common) |
| **Force vacuum** | `VACUUM ANALYZE orders;` | Dead rows, bloated table |
| **Rebuild index** | `REINDEX CONCURRENTLY INDEX idx_orders_status;` | Bloated index |
| **Table partitioning** | Partition by date/status | Table > 50M rows |
| **Partial index** | `CREATE INDEX ON orders(id) WHERE status='PENDING';` | Only small subset needed |
| **Connection pool** | Check PgBouncer pool exhaustion | All connections waiting |

### PostgreSQL Query Planner Decision Tree

```
Query: SELECT * FROM orders WHERE status = 'PENDING'

Planner estimates:
  ├── Table rows: 80,000,000 (from pg_class.reltuples)
  ├── Rows matching status='PENDING': ???
  │
  ▼
┌─────────────────────────────────────────────┐
│ Check pg_stats for 'status' column          │
│                                              │
│ If stats are FRESH:                          │
│   n_distinct = 5 (NEW, PENDING, SHIPPED...)  │
│   most_common_vals = {SHIPPED, DELIVERED...} │
│   most_common_freqs = {0.4, 0.35, ...}      │
│   PENDING freq = 0.02 → 1.6M rows           │
│   1.6M / 80M = 2% → INDEX SCAN ✅ (< 5%)   │
│                                              │
│ If stats are STALE (showing 1M total rows):  │
│   Planner thinks PENDING = 200K rows         │
│   Selectivity seems high → SEQ SCAN ❌       │
│   Actually 80M rows → disastrous!            │
└─────────────────────────────────────────────┘

Rule of thumb:
  Index Scan if selectivity < ~5-10% of table
  Seq Scan if selectivity > ~10-20% of table
  Planner chooses based on ESTIMATED selectivity
  Stale stats → wrong estimate → wrong plan!
```

### Index Types and When to Use Them

```
┌────────────────┬──────────────────────┬───────────────────────────┐
│ Index Type     │ Best For             │ Example                   │
├────────────────┼──────────────────────┼───────────────────────────┤
│ B-Tree         │ Equality, range,     │ CREATE INDEX ON orders    │
│ (default)      │ ORDER BY, BETWEEN    │   (created_at);           │
├────────────────┼──────────────────────┼───────────────────────────┤
│ Hash           │ Equality only        │ CREATE INDEX ON users     │
│                │ (faster than B-tree) │   USING hash (email);     │
├────────────────┼──────────────────────┼───────────────────────────┤
│ GIN            │ Full-text, JSONB,    │ CREATE INDEX ON products  │
│                │ array contains       │   USING gin (tags);       │
├────────────────┼──────────────────────┼───────────────────────────┤
│ GiST           │ Geometric, ranges,   │ CREATE INDEX ON events    │
│                │ nearest-neighbor     │   USING gist (location);  │
├────────────────┼──────────────────────┼───────────────────────────┤
│ BRIN           │ Naturally ordered    │ CREATE INDEX ON logs      │
│                │ large tables (dates) │   USING brin (timestamp); │
├────────────────┼──────────────────────┼───────────────────────────┤
│ Partial        │ Subset of rows       │ CREATE INDEX ON orders(id)│
│                │ (save space!)        │   WHERE status='PENDING'; │
├────────────────┼──────────────────────┼───────────────────────────┤
│ Covering       │ Index-only scans     │ CREATE INDEX ON orders    │
│ (INCLUDE)      │ (avoid table lookup) │   (status) INCLUDE (amt); │
└────────────────┴──────────────────────┴───────────────────────────┘
```

### Prevention

```
-- Tune autovacuum for large tables
ALTER TABLE orders SET (
    autovacuum_vacuum_threshold = 5000,
    autovacuum_analyze_threshold = 5000,
    autovacuum_vacuum_scale_factor = 0.01,
    autovacuum_analyze_scale_factor = 0.01
);

-- Add monitoring alert
-- Alert if pg_stat_user_tables.n_dead_tup > 10% of n_live_tup
```

### EXPLAIN ANALYZE Reading Guide

```
EXPLAIN ANALYZE SELECT * FROM orders WHERE status = 'PENDING';

                                    QUERY PLAN
  ──────────────────────────────────────────────────────────────────
  Seq Scan on orders  (cost=0.00..1823456.00 rows=80000000 width=64)
                       ▲              ▲          ▲           ▲
                       │              │          │           │
                  startup cost    total cost  estimated   avg row
                  (first row)     (all rows)  row count   width

    Filter: (status = 'PENDING'::text)
    Rows Removed by Filter: 78400000     ← scanned ALL 80M, kept 1.6M!
    Planning Time: 0.123 ms
    Execution Time: 45321.456 ms         ← 45 seconds!

  Compare with index scan after ANALYZE:
  Index Scan using idx_status on orders  (cost=0.57..8.59 rows=1600000)
    Index Cond: (status = 'PENDING'::text)
    Planning Time: 0.234 ms
    Execution Time: 48.123 ms            ← 48 milliseconds!
```

---

## Q10: Redis Failover — Cache Stampede

**Category:** Caching | **Difficulty:** Hard

### Scenario

After a Redis cluster failover, your application suffers a cache stampede that brings the database down. How does this happen, and what patterns prevent it?

### How Cache Stampede Happens

```
Normal operation:
  Request → Redis HIT → return cached data (fast)

After Redis failover (cache is empty):
  Request 1 → Redis MISS → query DB
  Request 2 → Redis MISS → query DB
  Request 3 → Redis MISS → query DB     ALL hit DB simultaneously!
  ...
  Request 10,000 → Redis MISS → query DB
  
  DB: 10,000 concurrent queries → overwhelmed → DOWN

  ┌──────────┐         ┌───────┐         ┌────┐
  │ 10K reqs │ ──ALL──►│ Redis │──MISS──►│ DB │ ← CRUSHED
  │          │         │(empty)│         │    │
  └──────────┘         └───────┘         └────┘
```

### Prevention Patterns

```
PATTERN 1: Mutex Lock (Singleflight)
  First request: acquire lock → query DB → populate cache → release lock
  Other requests: wait for lock → get cached value
  
  Only ONE request hits DB for each cache key.

PATTERN 2: Probabilistic Early Expiration
  TTL = 3600s (1 hour)
  At 3500s (before expiry), a random request refreshes the cache.
  
  probability = (now - fetchTime) / (ttl × beta)
  if random() < probability → refresh cache in background
  
  Cache is refreshed BEFORE expiry → no stampede.

PATTERN 3: Jitter in TTLs
  Instead of all keys expiring at exactly the same time:
    TTL = baseTTL + random(0, jitterRange)
  
  Keys expire at DIFFERENT times → load spreads out.

PATTERN 4: Stale-While-Revalidate
  Serve STALE cached data while refreshing in background.
  User gets fast (slightly stale) response.
  Cache is updated asynchronously.
  
  Response headers: Cache-Control: max-age=3600, stale-while-revalidate=600

PATTERN 5: Circuit Breaker on DB
  If DB error rate > threshold → return cached fallback / degraded response
  Don't let stampede cascade into total failure.
```

### Cache Stampede Timeline

```
TIME    CACHE              REQUESTS                DATABASE
═══════════════════════════════════════════════════════════════
T=0     ┌────────────┐     ────►  HIT              [idle]
        │  key: data │     ────►  HIT
        │  TTL: 60s  │     ────►  HIT
        └────────────┘

T=59    ┌────────────┐     ────►  HIT              [idle]
        │  key: data │     ────►  HIT
        │  TTL: 1s   │
        └────────────┘

T=60    ┌────────────┐     ────►  MISS ──────────► [query 1]
        │  (expired)  │     ────►  MISS ──────────► [query 2]
        │             │     ────►  MISS ──────────► [query 3]
        └────────────┘     ────►  MISS ──────────► [query 4]
                           ────►  MISS ──────────► [query 5]
                                                   [query 6]
                                                   [query 7]
                                                   [query ...]
                                                   [OVERLOAD!]
                                                   [DB DOWN ❌]

WITH MUTEX LOCK (Pattern 1):
T=60    ┌────────────┐     ─R1─►  MISS → acquire   [query 1 only]
        │  (expired)  │     ─R2─►  MISS → wait...   [R1 rebuilds]
        │             │     ─R3─►  MISS → wait...
        └────────────┘     ─R4─►  MISS → wait...

T=60.1  ┌────────────┐     ─R1─►  populate cache ✅
        │  key: data │     ─R2─►  HIT ✅ (lock released)
        │  TTL: 60s  │     ─R3─►  HIT ✅
        └────────────┘     ─R4─►  HIT ✅           [1 query total]

WITH PROBABILISTIC EARLY EXPIRY (Pattern 2):
T=55    ┌────────────┐     ─R1─►  HIT, TTL remaining = 5s
        │  key: data │              probability = 5/(60*0.5) = 16.7%
        │  TTL: 5s   │              random(0,1) = 0.12 < 0.167
        └────────────┘              → REFRESH in background!

T=56    ┌────────────┐     ─R2─►  HIT (still serving old data)
        │  key: NEW  │     Background refresh completes
        │  TTL: 60s  │     Cache refreshed BEFORE expiry!
        └────────────┘     No stampede! ✅
```

### Real-World Example: Product Detail Page

```
Amazon product page "iPhone 15" — 50K views/second

Without stampede protection:
  Cache key "product:iphone15" expires
  → 50,000 concurrent DB queries for same product
  → DB connection pool exhausted (max 200)
  → DB CPU 100% → cascading failure
  → ALL product pages fail, not just iPhone

With mutex + stale-while-revalidate:
  Cache key "product:iphone15" approaching expiry
  → 1 request refreshes cache in background
  → 49,999 requests get slightly stale data (< 100ms old)
  → DB handles 1 query, not 50,000
  → System stays healthy ✅
```

---

## Q11: CompletableFuture — Stale Data from Thread Pool

**Category:** Multithreading | **Difficulty:** Expert

### Scenario

A Java service processes financial transactions using `CompletableFuture` chains. Intermittently, completed futures return stale data. No exceptions are thrown. How do you track down this visibility bug?

### Root Causes

```
CAUSE 1: ThreadLocal Pollution
  Thread pools REUSE threads. ThreadLocal values from previous tasks
  leak into subsequent tasks.

  Thread-1: Task A sets ThreadLocal("user=Alice") → completes
  Thread-1: Task B runs → ThreadLocal still has "user=Alice" ← STALE!

  Fix: Always clear ThreadLocal in finally block, or use
       InheritableThreadLocal carefully.

CAUSE 2: Java Memory Model — Missing happens-before

  Thread 1: writes sharedData = 100   (in CPU cache, not flushed to main memory)
  Thread 2: reads sharedData = 0      ← sees OLD value!

  Without volatile or synchronization, there's no happens-before
  guarantee. Writes may not be visible across threads.

  Fix: Use volatile, AtomicReference, or synchronized blocks.

CAUSE 3: Shared Mutable State in CompletableFuture Chain

  List<Result> results = new ArrayList<>();  // NOT thread-safe!
  
  CompletableFuture.allOf(
      CompletableFuture.supplyAsync(() -> { results.add(fetch1()); }),
      CompletableFuture.supplyAsync(() -> { results.add(fetch2()); })
  ).join();
  // results may have 0, 1, or 2 items — race condition!

  Fix: Use ConcurrentLinkedQueue or collect results from
       individual futures after join.
```

### Java Memory Model — Happens-Before Visualization

```
═══════════════════════════════════════════════════════════════
  JAVA MEMORY MODEL — WHY VISIBILITY MATTERS
═══════════════════════════════════════════════════════════════

  Thread 1 (CPU Core 1)              Thread 2 (CPU Core 2)
  ┌──────────────────────┐           ┌──────────────────────┐
  │ CPU Cache (L1/L2)    │           │ CPU Cache (L1/L2)    │
  │                       │           │                       │
  │ sharedData = 100     │           │ sharedData = 0 ❌    │
  │ (written here,       │           │ (stale! never        │
  │  not yet flushed)    │           │  sees the write)     │
  └──────────┬───────────┘           └──────────┬───────────┘
             │                                   │
             ▼                                   ▼
  ┌───────────────────────────────────────────────────────────┐
  │                    Main Memory (RAM)                       │
  │                                                            │
  │  sharedData = 0   ← Thread 1's write NOT flushed yet     │
  │                                                            │
  │  Without volatile/synchronized, the JIT compiler can:     │
  │  1. Reorder writes (store buffer optimization)            │
  │  2. Keep values in CPU registers (never write to RAM)     │
  │  3. Cache values locally (never read from RAM again)      │
  └───────────────────────────────────────────────────────────┘

  FIX: volatile keyword
  ┌──────────────────────┐           ┌──────────────────────┐
  │ Thread 1             │           │ Thread 2             │
  │ volatile sharedData  │           │ volatile sharedData  │
  │   = 100              │           │   reads from RAM     │
  │   ↓ FLUSH to RAM     │           │   = 100 ✅           │
  └──────────────────────┘           └──────────────────────┘
```

### ThreadLocal Pollution in Thread Pools

```
═══════════════════════════════════════════════════════════════
  THREADLOCAL POLLUTION — HOW STALE DATA LEAKS
═══════════════════════════════════════════════════════════════

Thread Pool (reuses threads):

Round 1: Thread-1 runs Task A
  ┌────────────────────────────────────┐
  │ Task A: process financial TX       │
  │   ThreadLocal.set("user=Alice")    │
  │   ThreadLocal.set("txId=TX-001")   │
  │   ... process ...                  │
  │   return result                    │
  │   ← FORGOT to clear ThreadLocal!  │
  └────────────────────────────────────┘

Round 2: Thread-1 runs Task B (REUSED thread)
  ┌────────────────────────────────────┐
  │ Task B: process financial TX       │
  │   user = ThreadLocal.get()         │
  │        → "Alice" ← WRONG! ❌      │
  │   txId = ThreadLocal.get()         │
  │        → "TX-001" ← STALE! ❌     │
  │                                     │
  │   Charges Alice instead of Bob!    │
  │   Logs show TX-001 instead of      │
  │   TX-002 — audit trail corrupted!  │
  └────────────────────────────────────┘

FIX:
  ┌────────────────────────────────────┐
  │ try {                              │
  │   ThreadLocal.set("user=Bob");     │
  │   ThreadLocal.set("txId=TX-002");  │
  │   ... process ...                  │
  │ } finally {                        │
  │   ThreadLocal.remove(); ← ALWAYS! │
  │ }                                  │
  └────────────────────────────────────┘
```

### Diagnosis Steps

```
1. Enable NMT: -XX:NativeMemoryTracking=detail
2. Thread dumps: look for ThreadLocal references
3. Add logging: log thread ID + ThreadLocal state at start/end of each task
4. Static analysis: find shared mutable state accessed without synchronization
5. JFR (Java Flight Recorder): capture memory access patterns
```

### CompletableFuture Safe Patterns

```
═══════════════════════════════════════════════════════════════
  WRONG vs RIGHT — Collecting Results from Async Tasks
═══════════════════════════════════════════════════════════════

❌ WRONG — shared mutable state (race condition):
  List<Result> results = new ArrayList<>();

  CompletableFuture.allOf(
      supplyAsync(() -> { results.add(fetch1()); return null; }),
      supplyAsync(() -> { results.add(fetch2()); return null; }),
      supplyAsync(() -> { results.add(fetch3()); return null; })
  ).join();

  results.size() → could be 0, 1, 2, or 3! Unpredictable.
  ArrayList is NOT thread-safe.

✅ RIGHT — collect from individual futures:
  CompletableFuture<Result> f1 = supplyAsync(() -> fetch1());
  CompletableFuture<Result> f2 = supplyAsync(() -> fetch2());
  CompletableFuture<Result> f3 = supplyAsync(() -> fetch3());

  CompletableFuture.allOf(f1, f2, f3).join();

  List<Result> results = List.of(f1.get(), f2.get(), f3.get());
  results.size() → always 3. Deterministic. ✅

✅ ALSO RIGHT — thread-safe collection:
  ConcurrentLinkedQueue<Result> results = new ConcurrentLinkedQueue<>();

  CompletableFuture.allOf(
      supplyAsync(() -> { results.add(fetch1()); return null; }),
      supplyAsync(() -> { results.add(fetch2()); return null; })
  ).join();

  results.size() → always correct. ConcurrentLinkedQueue is thread-safe.
```

---

## Q12: Kafka At-Least-Once — Duplicate Charges

**Category:** Kafka | **Difficulty:** Hard

### Scenario

Your Kafka-based order processing system guarantees at-least-once delivery. Finance reports duplicate charges appearing 3 weeks after launch. Describe your end-to-end idempotency design.

### Why Duplicates Happen

```
At-least-once delivery:
  1. Consumer processes message → charges customer ✅
  2. Consumer tries to commit offset → network error ❌
  3. Consumer restarts → re-reads same message
  4. Consumer processes again → charges customer AGAIN ❌❌

  Timeline:
  [charge $100] → [commit fails] → [restart] → [charge $100 again]
```

### End-to-End Idempotency Design

```
LAYER 1: Idempotent Kafka Producer
  props.put("enable.idempotence", "true");
  props.put("acks", "all");
  
  Kafka assigns sequence numbers to each message.
  Broker deduplicates: same sequence = skip.
  Prevents producer-side duplicates (retries).

LAYER 2: Consumer Deduplication Table
  
  ┌──────────────────────────────────────────────┐
  │ processed_events table                        │
  │ ┌─────────────┬─────────────┬───────────────┐│
  │ │ event_id    │ processed_at│ result        ││
  │ │ (unique key)│             │               ││
  │ ├─────────────┼─────────────┼───────────────┤│
  │ │ order-12345 │ 2024-03-15  │ CHARGED       ││
  │ └─────────────┴─────────────┴───────────────┘│
  └──────────────────────────────────────────────┘

  Consumer logic:
    1. Read message with event_id
    2. Check: SELECT FROM processed_events WHERE event_id = ?
    3. If exists → SKIP (already processed)
    4. If not → process + INSERT into processed_events (in same TX)
    5. Commit Kafka offset

LAYER 3: Outbox Pattern (for producing events)

  Instead of:
    1. Update DB
    2. Send Kafka message  ← if this fails, DB and Kafka are inconsistent

  Do:
    1. Update DB + write to outbox table (same transaction)
    2. Background poller reads outbox → sends to Kafka
    3. Mark outbox entry as sent

  DB transaction ensures atomicity.
  Poller retries ensure delivery.
  Consumer dedup prevents double-processing.

LAYER 4: Exactly-Once Semantics (Kafka Streams)
  For Kafka-to-Kafka pipelines:
    processing.guarantee=exactly_once_v2
  
  Kafka manages offsets and output atomically.
```

### Outbox Pattern Architecture

```
═══════════════════════════════════════════════════════════════
  OUTBOX PATTERN — Reliable Event Publishing
═══════════════════════════════════════════════════════════════

❌ DUAL-WRITE PROBLEM (unsafe):

  ┌──────────────┐    ┌──────┐    ┌─────────┐
  │ Order Service │──1►│  DB  │    │  Kafka  │
  │               │    └──────┘    └─────────┘
  │ 1. Update DB  │         ✅         
  │ 2. Send Kafka │──2►──────────►     ❌ (what if this fails?)
  └──────────────┘
  
  If step 2 fails: DB has order, Kafka never gets the event.
  Downstream services never know about the order!

✅ OUTBOX PATTERN (safe):

  ┌──────────────┐   SAME TRANSACTION   ┌──────────────────┐
  │ Order Service │──────────────────────►│   Database       │
  │               │                      │                  │
  │ 1. Update    │                      │ orders table:    │
  │    orders    │                      │ {id:1, amt:100}  │
  │ 2. Insert   │                      │                  │
  │    outbox   │                      │ outbox table:    │
  └──────────────┘                      │ {id:1, event:    │
                                        │  "OrderCreated", │
                                        │  sent: false}    │
                                        └────────┬─────────┘
                                                 │
                          ┌──────────────────────┘
                          │ CDC (Debezium) or Poller
                          │ reads unsent outbox rows
                          ▼
                   ┌─────────────┐
                   │   Kafka     │  event published
                   │ "OrderCreated"│  reliably ✅
                   └─────────────┘
                          │
                          ▼
                   Mark outbox row sent=true

  Atomicity: Both writes in SAME DB transaction.
  Reliability: Poller/CDC retries until event is published.
  Idempotency: Consumer dedup table handles reprocessing.
```

### Idempotency Decision Flowchart

```
Message arrives at consumer
        │
        ▼
┌───────────────────────┐
│ Extract idempotency   │
│ key (event_id or      │
│ composite key)        │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────┐      ┌─────────────────┐
│ EXISTS in             │─YES─►│ Already processed│
│ processed_events?     │      │ → SKIP           │
└───────────┬───────────┘      │ → Commit offset  │
            │ NO               └─────────────────┘
            ▼
┌───────────────────────┐
│ BEGIN TRANSACTION     │
│                       │
│ 1. Process business   │
│    logic (charge card)│
│ 2. INSERT into        │
│    processed_events   │
│    (event_id, result) │
│                       │
│ COMMIT TRANSACTION    │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────┐
│ Commit Kafka offset   │
│ (async or sync)       │
└───────────────────────┘

Key: business logic + dedup insert in SAME DB transaction.
     If consumer crashes after processing but before offset
     commit → message redelivered → dedup table catches it.
```

---

## Q13: ECS OOMKilled — Off-Heap Memory

**Category:** AWS/Cloud | **Difficulty:** Expert

### Scenario

Your ECS service memory gradually increases until OOMKilled every 6-8 hours despite a Java heap limit of 512MB. The JVM heap shows healthy. What is consuming memory outside the heap?

### JVM Memory Layout

```
┌─────────────────────────────────────────────┐
│              ECS Container (1 GB limit)       │
│                                               │
│  ┌───────────────────────────┐               │
│  │   Java Heap (512 MB)      │  ← -Xmx512m  │
│  │   (GC manages this)       │               │
│  │   Heap usage: healthy ✅   │               │
│  └───────────────────────────┘               │
│                                               │
│  ┌───────────────────────────┐               │
│  │   OFF-HEAP (grows!)       │               │
│  │   ├── Metaspace: ~100MB   │  ← class data │
│  │   ├── Thread stacks: ~200MB│ ← 200 threads × 1MB │
│  │   ├── Direct ByteBuffers  │  ← NIO, Netty │
│  │   ├── JNI / Native libs   │               │
│  │   ├── Code cache: ~50MB   │  ← JIT compiled│
│  │   └── GC overhead: ~50MB  │               │
│  └───────────────────────────┘               │
│                                               │
│  Total: 512 + 100 + 200 + ... = > 1 GB ❌    │
│  Container OOMKilled!                         │
└─────────────────────────────────────────────┘
```

### Diagnosis Steps

```
1. Enable Native Memory Tracking:
   -XX:NativeMemoryTracking=detail

2. Check NMT report:
   jcmd <pid> VM.native_memory detail

   Output:
   Total: reserved=1500MB, committed=1100MB
   - Java Heap: 512MB
   - Thread: 200MB (200 threads × 1MB default stack)
   - Metaspace: 120MB
   - Direct ByteBuffer: 150MB  ← LEAK!
   - Code Cache: 50MB
   - GC: 40MB
   - Internal: 30MB

3. Common culprits:
   ├── Netty off-heap buffers (not released)
   ├── Direct ByteBuffers (NIO file operations)
   ├── Too many threads (each thread = 1MB stack)
   ├── Metaspace leak (dynamic class loading / proxies)
   └── JNI native memory allocations
```

### Fixes

| Culprit | Fix |
|---------|-----|
| **Thread stacks** | Reduce `-Xss512k` (from default 1MB), use virtual threads (Java 21) |
| **Metaspace** | Set `-XX:MaxMetaspaceSize=128m`, investigate class loading |
| **Direct ByteBuffers** | Set `-XX:MaxDirectMemorySize=256m`, ensure buffers are released |
| **Netty** | Configure `io.netty.maxDirectMemory`, enable leak detection |
| **Container sizing** | Set container memory = heap + off-heap + 20% buffer |

### JVM Memory Layout — Full Breakdown

```
═══════════════════════════════════════════════════════════════
  COMPLETE JVM MEMORY MAP (what's INSIDE the container)
═══════════════════════════════════════════════════════════════

  ┌─────────────────────────────────────────────────────────┐
  │                ECS Container: 1 GB limit                 │
  │                                                          │
  │  ┌─────────────────────────────────────────────────┐    │
  │  │              JAVA HEAP (-Xmx512m)                │    │
  │  │                                                   │    │
  │  │  ┌──────────┐ ┌──────────┐ ┌──────────────────┐ │    │
  │  │  │  Young   │ │  Young   │ │                    │ │    │
  │  │  │  Eden    │ │ Survivor │ │    Old (Tenured)   │ │    │
  │  │  │  ~170MB  │ │  ~10MB   │ │    ~332MB          │ │    │
  │  │  └──────────┘ └──────────┘ └──────────────────┘ │    │
  │  │              GC Managed ✅                        │    │
  │  └─────────────────────────────────────────────────┘    │
  │                                                          │
  │  ┌─────────────────────────────────────────────────┐    │
  │  │              OFF-HEAP (NOT GC managed!)           │    │
  │  │                                                   │    │
  │  │  ┌──────────────┐  Metaspace: class metadata     │    │
  │  │  │  Metaspace   │  default: unlimited!           │    │
  │  │  │  ~120MB      │  fix: -XX:MaxMetaspaceSize     │    │
  │  │  └──────────────┘                                │    │
  │  │                                                   │    │
  │  │  ┌──────────────┐  Thread stacks: 1 per thread   │    │
  │  │  │  Threads     │  default: 1MB each (-Xss)      │    │
  │  │  │  200 × 1MB   │  200 threads = 200MB!          │    │
  │  │  │  = 200MB     │  fix: -Xss512k, virtual threads│    │
  │  │  └──────────────┘                                │    │
  │  │                                                   │    │
  │  │  ┌──────────────┐  Direct ByteBuffers (NIO)      │    │
  │  │  │  Direct      │  Netty, gRPC, file I/O         │    │
  │  │  │  Memory      │  fix: -XX:MaxDirectMemorySize  │    │
  │  │  │  ~150MB      │  ensure release() is called    │    │
  │  │  └──────────────┘                                │    │
  │  │                                                   │    │
  │  │  ┌──────────────┐  JIT compiled code              │    │
  │  │  │  Code Cache  │  grows with app complexity      │    │
  │  │  │  ~50MB       │  -XX:ReservedCodeCacheSize     │    │
  │  │  └──────────────┘                                │    │
  │  │                                                   │    │
  │  │  ┌──────────────┐  GC data structures             │    │
  │  │  │  GC Overhead │  ~5-10% of heap                 │    │
  │  │  │  ~40MB       │  varies by GC algorithm         │    │
  │  │  └──────────────┘                                │    │
  │  │                                                   │    │
  │  │  Total off-heap: ~560MB                          │    │
  │  └─────────────────────────────────────────────────┘    │
  │                                                          │
  │  TOTAL: 512MB (heap) + 560MB (off-heap) = 1,072MB       │
  │  CONTAINER LIMIT: 1,024 MB (1 GB)                        │
  │  RESULT: OOMKilled! ❌ (48MB over limit)                 │
  └─────────────────────────────────────────────────────────┘
```

### NMT (Native Memory Tracking) Command Reference

```
Enable NMT:
  -XX:NativeMemoryTracking=detail

View current allocation:
  jcmd <pid> VM.native_memory summary

Compare against baseline (detect leaks):
  jcmd <pid> VM.native_memory baseline     ← take baseline
  ... wait hours ...
  jcmd <pid> VM.native_memory detail.diff  ← compare

Sample output:
  ┌──────────────────────────────────────────────────────────┐
  │ Native Memory Tracking:                                   │
  │                                                            │
  │ Total: reserved=1500MB, committed=1100MB                   │
  │                                                            │
  │ -        Java Heap (reserved=512MB, committed=512MB)       │
  │ -            Thread (reserved=210MB, committed=210MB)      │
  │                       (thread #208)                        │
  │ -         Metaspace (reserved=140MB, committed=120MB)      │
  │ -           Direct (reserved=180MB, committed=150MB) ← !! │
  │ -       Code Cache (reserved=64MB, committed=48MB)         │
  │ -               GC (reserved=42MB, committed=40MB)         │
  │ -         Internal (reserved=32MB, committed=30MB)         │
  │                                                            │
  │ Diff vs baseline (+200ms elapsed):                        │
  │ -           Direct: +50MB  ← LEAK! Growing steadily       │
  └──────────────────────────────────────────────────────────┘
```

### Rule of Thumb for Container Sizing

```
Container memory = Xmx + MaxMetaspaceSize + (threads × Xss)
                   + MaxDirectMemorySize + CodeCache + GC + buffer

Example:
  512MB heap
  + 128MB metaspace
  + 200 threads × 0.5MB = 100MB
  + 256MB direct memory
  + 64MB code cache
  + 50MB GC overhead
  + 128MB safety buffer
  ─────────────────────
  = 1,238MB → set container to 1,536MB (1.5 GB)

JVM flags:
  -Xmx512m -Xms512m
  -XX:MaxMetaspaceSize=128m
  -Xss512k
  -XX:MaxDirectMemorySize=256m
  -XX:ReservedCodeCacheSize=64m
  -XX:NativeMemoryTracking=summary
  -XX:+UseContainerSupport          ← auto-detect container limits
  -XX:MaxRAMPercentage=75.0         ← alternative: % of container
```

---

## Q14: REST API Conflict — PUT vs PATCH

**Category:** API Design | **Difficulty:** Medium

### Scenario

Two teams expose conflicting REST APIs — Team A uses PUT for partial updates, Team B uses PATCH. Now a shared API Gateway must route both, and consumers are confused. How do you resolve this at scale?

### The Problem

```
Team A:  PUT /orders/123  { "status": "shipped" }
         → Replaces ENTIRE resource (but they only send partial data)
         → Missing fields get set to null! ❌

Team B:  PATCH /orders/123  { "status": "shipped" }
         → Updates only specified fields ✅

Consumer confusion:
  "Do I use PUT or PATCH? What happens to fields I don't send?"
```

### Solution: API Standards Governance

```
1. DEFINE ORGANIZATION-WIDE API STANDARDS:
   ├── PUT = full replacement (client sends ALL fields)
   ├── PATCH = partial update (client sends ONLY changed fields)
   ├── POST = create new resource
   ├── DELETE = remove resource
   └── Document in OpenAPI spec (Swagger)

2. API VERSIONING:
   ├── v1: existing behavior (don't break consumers)
   ├── v2: follows new standards
   └── Deprecation timeline: v1 → v2 over 6 months

3. GATEWAY-LEVEL ENFORCEMENT:
   ├── Validate against OpenAPI schema
   ├── Reject non-compliant requests with helpful error
   └── Provide migration guide for consumers

4. LINTING IN CI/CD:
   ├── Spectral / OpenAPI linter in pipeline
   ├── Block PRs that violate API standards
   └── Auto-generate client SDKs from spec
```

### HTTP Methods — Semantics Quick Reference

```
═══════════════════════════════════════════════════════════════
  REST API METHOD SEMANTICS (RFC 7231 + RFC 5789)
═══════════════════════════════════════════════════════════════

  ┌──────────┬──────────────────┬──────────┬──────────┬──────────┐
  │ Method   │ Purpose          │ Idempot. │ Safe     │ Body     │
  ├──────────┼──────────────────┼──────────┼──────────┼──────────┤
  │ GET      │ Read resource    │ ✅ Yes   │ ✅ Yes  │ No       │
  │ POST     │ Create resource  │ ❌ No    │ ❌ No   │ Yes      │
  │ PUT      │ FULL replace     │ ✅ Yes   │ ❌ No   │ Full obj │
  │ PATCH    │ Partial update   │ ❌ No*   │ ❌ No   │ Partial  │
  │ DELETE   │ Remove resource  │ ✅ Yes   │ ❌ No   │ Optional │
  │ HEAD     │ GET without body │ ✅ Yes   │ ✅ Yes  │ No       │
  │ OPTIONS  │ Supported methods│ ✅ Yes   │ ✅ Yes  │ No       │
  └──────────┴──────────────────┴──────────┴──────────┴──────────┘
  * PATCH can be made idempotent with JSON Merge Patch

PUT vs PATCH — What Happens to Missing Fields:

  Current resource:
  { "id": 123, "status": "new", "amount": 99, "note": "rush" }

  PUT /orders/123  { "status": "shipped" }
  Result: { "id": 123, "status": "shipped", "amount": null, "note": null }
  ↑ ALL fields replaced! Missing = null ❌

  PATCH /orders/123  { "status": "shipped" }
  Result: { "id": 123, "status": "shipped", "amount": 99, "note": "rush" }
  ↑ Only status changed. Other fields untouched ✅
```

### API Versioning Strategies

```
┌──────────────────┬────────────────────────┬─────────────────────────┐
│ Strategy         │ Example                │ Trade-off               │
├──────────────────┼────────────────────────┼─────────────────────────┤
│ URL Path         │ /api/v1/orders         │ Simplest, most explicit │
│ (recommended)    │ /api/v2/orders         │ Easy to route in gateway│
├──────────────────┼────────────────────────┼─────────────────────────┤
│ Header           │ Api-Version: 2         │ Clean URL, harder to    │
│                  │ X-API-Version: 2       │ test in browser         │
├──────────────────┼────────────────────────┼─────────────────────────┤
│ Query Param      │ /api/orders?v=2        │ Easy to switch, can be  │
│                  │                        │ cached incorrectly      │
├──────────────────┼────────────────────────┼─────────────────────────┤
│ Content Negotiation│ Accept:              │ REST purist approach    │
│ (GitHub style)   │  application/vnd.     │ Most complex to         │
│                  │  myapi.v2+json        │ implement               │
└──────────────────┴────────────────────────┴─────────────────────────┘
```

---

## Q15: P99 Latency Spike — No Errors, No CPU Alerts

**Category:** Log/Monitoring | **Difficulty:** Hard

### Scenario

Production is degrading. CloudWatch shows no CPU/memory alerts. P99 latency jumped from 120ms to 8 seconds. No errors in logs. How do you diagnose with only observability tooling?

### Diagnosis Playbook

```
Step 1: DISTRIBUTED TRACES (Jaeger/X-Ray)
  Look at slow traces → which SERVICE in the chain is slow?
  
  Request: [Gateway 2ms] → [Auth 5ms] → [OrderService 7800ms!] → [DB 15ms]
                                          ↑ BOTTLENECK

Step 2: THREAD DUMPS on OrderService
  jstack <pid> 3 times, 10 seconds apart.
  
  If many threads BLOCKED on:
  ├── "waiting for monitor entry" → lock contention
  ├── "waiting on condition" (connection pool) → pool exhausted
  ├── "in Object.wait()" → deadlock or slow downstream
  └── "RUNNABLE" (GC) → check GC logs

Step 3: GC LOGS
  -Xlog:gc*:file=gc.log:time,level,tags
  
  If Full GC taking 5+ seconds → heap too small or memory leak
  G1 mixed collections too frequent → tune region size

Step 4: CONNECTION POOL METRICS
  HikariCP: pending thread count, active connections, idle connections
  
  If active = max AND pending > 0:
    All connections in use → new requests WAIT → latency spike
    Root cause: slow queries holding connections too long

Step 5: DEPENDENCY HEALTH
  ├── Redis latency (SLOWLOG)
  ├── Database slow query log
  ├── External API response times
  └── DNS resolution time (often overlooked)

Step 6: RECENT CHANGES
  ├── New deployment? (even config changes)
  ├── Certificate rotation? (TLS handshake issues)
  ├── DNS TTL expired? (new IP resolution)
  └── Dependency updated their infra?
```

### Diagnosis Decision Tree

```
P99 latency spike, no errors, no CPU/memory alerts
                    │
                    ▼
         ┌──────────────────┐
         │ Check distributed│
         │ traces (Jaeger)  │
         └────────┬─────────┘
                  │
        ┌─────────┴──────────┐
        │                    │
    All services          One service slow
    equally slow          (e.g., OrderService)
        │                    │
        ▼                    ▼
  ┌──────────────┐    ┌──────────────┐
  │ Network issue│    │ Thread dump  │
  │ DNS/TLS/CDN  │    │ jstack ×3    │
  └──────────────┘    └──────┬───────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
        Many threads    Many threads    Many threads
        BLOCKED on      in RUNNABLE     waiting for
        monitor entry   (GC related)    connection
              │              │              │
              ▼              ▼              ▼
        Lock contention  GC pauses     Pool exhaustion
        ├── Find lock    ├── Check     ├── HikariCP
        │   holder       │   gc.log    │   metrics
        ├── Reduce sync  ├── Tune heap ├── Slow queries
        └── Use          └── Switch GC └── Add timeout
            concurrent       (G1/ZGC)      (30s max)
            collections
```

### P50 vs P95 vs P99 — Why P99 Matters

```
Response time distribution for 10,000 requests:

  Count
  ▲
  │  ████
  │  █████
  │  ██████
  │  ████████
  │  ██████████
  │  █████████████
  │  ████████████████
  │  ██████████████████████
  │  ██████████████████████████████████
  └──────────────────────────────────────────────► Time (ms)
     50    100   150   200   500  1000  5000  8000
     │           │                 │           │
     P50=100ms   P95=200ms        P99=1000ms  P99.9=8000ms

  P50 (median): half of users see < 100ms ← looks healthy!
  P95: 95% of users < 200ms ← still ok
  P99: 1% of users see > 1000ms ← 100 users per 10K!
  P99.9: 0.1% see > 8000ms ← timeouts, lost sales

  At 1M req/day: P99 = 10,000 slow requests PER DAY!
  These 10K users = your most active/valuable customers
  (more requests → higher chance of hitting P99)
```

### Most Common Hidden Causes

| Cause | Symptom | Fix |
|-------|---------|-----|
| Connection pool exhaustion | Threads waiting for connections | Increase pool, add timeouts |
| GC pauses | Periodic latency spikes | Tune GC, increase heap |
| DNS resolution | First request after TTL slow | Cache DNS, lower TTL |
| TLS certificate issue | Handshake timeout | Check cert chain, OCSP stapling |
| Noisy neighbor (cloud) | Random latency spikes | Request dedicated hosts |

### Observability Stack Recommendation

```
┌─────────────────────────────────────────────────────────────┐
│                   Observability Pyramid                       │
│                                                               │
│                      ┌──────┐                                │
│                      │ TRACES│  Distributed tracing           │
│                      │Jaeger │  (where is the bottleneck?)    │
│                      │X-Ray  │                                │
│                    ┌─┴──────┴──┐                              │
│                    │  METRICS   │ Time-series data             │
│                    │ Prometheus │ (what's the trend?)          │
│                    │ CloudWatch │                              │
│                  ┌─┴────────────┴──┐                          │
│                  │     LOGS         │ Event records            │
│                  │ ELK / CloudWatch │ (what happened exactly?) │
│                  │ Splunk           │                          │
│                  └──────────────────┘                          │
│                                                               │
│ Key metrics to alert on:                                     │
│  ├── P99 latency per endpoint (> 2× baseline)                │
│  ├── Error rate (> 0.1%)                                     │
│  ├── Thread pool utilization (> 80%)                         │
│  ├── Connection pool utilization (> 80%)                     │
│  ├── GC pause time (> 200ms)                                 │
│  └── Heap utilization (> 85%)                                │
└─────────────────────────────────────────────────────────────┘
```

---

## Q16: Latency Cascade — Service A → B → C → D

**Category:** Microservices | **Difficulty:** Hard

### Scenario

Service A calls B calls C calls D. Service D has a 5-second timeout. Under load, this creates a latency cascade. A 200ms slowdown in D causes A to timeout. Redesign the resilience architecture.

### The Cascade Problem

```
Normal:  A(50ms) → B(50ms) → C(50ms) → D(50ms) = 200ms total ✅

D slows down:
  A(50ms) → B(50ms) → C(50ms) → D(5000ms timeout)
  
  A's total: 50 + 50 + 50 + 5000 = 5150ms → A times out at 3000ms ❌

  While waiting, A holds:
  ├── HTTP connection
  ├── Thread from thread pool
  ├── DB connection (if in transaction)
  └── Memory for request/response

  200 concurrent requests × 5s wait = thread pool exhausted
  → ALL requests to A fail (even unrelated ones!)
```

### Resilience Architecture

```
PATTERN 1: TIMEOUT PER HOP (with deadline propagation)
  A sets deadline: "respond by T+3000ms"
  A→B: timeout = 2500ms
  B→C: timeout = 2000ms (deadline minus elapsed)
  C→D: timeout = 1500ms
  
  Each hop gets progressively less time.
  If D is slow → C times out quickly → B responds quickly → A responds.

PATTERN 2: CIRCUIT BREAKER (Resilience4j)
  Each service wraps downstream calls in a circuit breaker.
  
  CLOSED: requests flow normally
  If D fails 50% in 10s → OPEN: immediately return fallback
  After 30s → HALF_OPEN: try one request
  If succeeds → CLOSED. If fails → OPEN again.

  A → B → C → [CIRCUIT BREAKER] → D
                    ↓ (if open)
               Return cached/fallback response

PATTERN 3: BULKHEAD (Isolate Thread Pools)
  Don't let D's slowness exhaust ALL of C's threads.
  
  C's thread pools:
  ├── Pool for D calls: 10 threads (if exhausted, fail fast)
  ├── Pool for E calls: 10 threads (unaffected by D!)
  └── Pool for local ops: 20 threads

PATTERN 4: ASYNC MESSAGING (Decouple)
  Instead of synchronous chain A→B→C→D:
  A → publish event → return 202 Accepted
  B consumes event → processes → publishes result
  C consumes event → processes → publishes result
  D consumes event → processes → publishes result
  
  No cascading timeouts. Each service processes independently.
```

### Circuit Breaker State Machine

```
═══════════════════════════════════════════════════════════════
  CIRCUIT BREAKER — THREE STATES (Resilience4j)
═══════════════════════════════════════════════════════════════

                  success rate > threshold
         ┌──────────────────────────────────────┐
         │                                      │
         ▼                                      │
  ┌──────────────┐                       ┌──────────────┐
  │              │  failure rate >        │              │
  │    CLOSED    │  threshold (50%)       │  HALF_OPEN   │
  │   (normal)   │──────────────────────►│   (testing)   │
  │              │                       │              │
  │  Requests    │                       │ Allow 1 test │
  │  flow through│   ┌──────────────┐    │ request      │
  │              │   │              │    │              │
  └──────────────┘   │    OPEN      │    └──────┬───────┘
                     │  (rejecting)  │           │
                     │              │◄──────────┘
                     │  Return      │  test request fails
                     │  fallback    │
                     │  immediately │
                     │              │
                     │  Wait 30s    │────► try HALF_OPEN
                     └──────────────┘

  Timeline example:
  ──────────────────────────────────────────────────────────
  T=0    CLOSED: requests succeed                    ✅✅✅
  T=10   Service D slows down                        ✅❌❌
  T=15   Failure rate > 50% → OPEN                   ❌❌❌
  T=15   All requests get fallback immediately       ⚡⚡⚡
  T=45   30s elapsed → HALF_OPEN                     try one
  T=46   Test request succeeds → CLOSED              ✅✅✅
  ──────────────────────────────────────────────────────────
```

### Bulkhead Pattern — Thread Pool Isolation

```
═══════════════════════════════════════════════════════════════
  WITHOUT BULKHEAD — One bad dependency kills everything
═══════════════════════════════════════════════════════════════

  Service C — Shared Thread Pool (20 threads)
  ┌──────────────────────────────────────────────────────┐
  │ Thread Pool: 20 threads                               │
  │                                                        │
  │ D calls:  [T1 waiting][T2 waiting]...[T15 waiting]    │ ← D is slow
  │ E calls:  [T16][T17][T18]                             │ ← E is fine
  │ Local:    [T19][T20]                                  │ ← starved!
  │                                                        │
  │ D's slowness consumed 15/20 threads!                  │
  │ E and local operations starved for threads ❌         │
  └──────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════
  WITH BULKHEAD — Failures are isolated
═══════════════════════════════════════════════════════════════

  Service C — Separate Thread Pools (bulkheads)
  ┌──────────────────────────────────────────────────────┐
  │ Pool for D:  [T1 wait][T2 wait]...[T8 wait]          │ ← D is slow
  │              8/8 exhausted → FAST FAIL for D calls    │
  │                                                        │
  │ Pool for E:  [T1][T2]...[T8]                          │ ← unaffected!
  │              Working normally ✅                       │
  │                                                        │
  │ Pool local:  [T1][T2]...[T10]                         │ ← unaffected!
  │              Working normally ✅                       │
  │                                                        │
  │ D's failure contained to 8 threads only.              │
  │ Rest of the system healthy ✅                         │
  └──────────────────────────────────────────────────────┘
```

### Deadline Propagation — Real-World Example

```
API Gateway SLA: respond within 3000ms

  ┌──────────┐  timeout=2800ms  ┌──────────┐
  │ Gateway  │─────────────────►│Service A │
  │ SLA:3000 │                  │took: 50ms│
  └──────────┘                  └────┬─────┘
                                     │ timeout=2750ms (2800-50)
                                     ▼
                                ┌──────────┐
                                │Service B │
                                │took: 80ms│
                                └────┬─────┘
                                     │ timeout=2670ms (2750-80)
                                     ▼
                                ┌──────────┐
                                │Service C │
                                │took: 70ms│
                                └────┬─────┘
                                     │ timeout=2600ms (2670-70)
                                     ▼
                                ┌──────────┐
                                │Service D │
                                │5000ms!!! │──► TIMEOUT at 2600ms
                                └──────────┘    Fail fast, don't
                                                wait full 5000ms

  Total response: 50 + 80 + 70 + 2600 = 2800ms < 3000ms SLA ✅
  Without deadline propagation: 50 + 80 + 70 + 5000 = 5200ms ❌
```

---

## Q17: DynamoDB Scan Throttling Transactional Workloads

**Category:** NoSQL | **Difficulty:** Hard

### Scenario

A DynamoDB table scan used for reporting is throttling your transactional workloads even though they use different partitions. Explain the root cause and redesign.

### Root Cause

```
DynamoDB capacity:

TABLE-level capacity: 1000 RCU total
Partitions: P1(250), P2(250), P3(250), P4(250)

Transactional workloads: targeted reads/writes to specific partitions ✅
Reporting SCAN: reads ALL partitions sequentially ❌

SCAN consumes RCU from ALL partitions:
  P1: txn(100) + scan(200) = 300 > 250 → THROTTLED! ❌
  P2: txn(50) + scan(200) = 250 → at limit
  P3: txn(150) + scan(200) = 350 > 250 → THROTTLED! ❌

Even though txn and scan use "different partitions",
SCAN reads FROM EVERY partition, consuming shared capacity.
```

### Solutions

```
SOLUTION 1: DynamoDB Streams + Athena
  ├── Enable DynamoDB Streams (CDC)
  ├── Stream to S3 via Kinesis Data Firehose
  ├── Query with Athena (serverless SQL on S3)
  └── Zero impact on transactional table ✅

SOLUTION 2: Global Secondary Index (GSI) for Reporting
  ├── Create GSI with reporting-friendly key design
  ├── GSI has its own provisioned capacity (separate from base table)
  ├── Scan GSI instead of base table
  └── Base table capacity unaffected ✅

SOLUTION 3: Export to S3 (for large reports)
  ├── Use DynamoDB Export to S3 (native feature)
  ├── Zero RCU consumed (reads from backups)
  ├── Query with Athena or Spark
  └── Best for batch reporting ✅
```

### DynamoDB Capacity Model Explained

```
═══════════════════════════════════════════════════════════════
  HOW DYNAMODB PARTITIONS AND CAPACITY INTERACT
═══════════════════════════════════════════════════════════════

Table: 1000 RCU (Read Capacity Units), 4 partitions

  ┌──────────────────────────────────────────────────────┐
  │ DynamoDB Table: "orders"                              │
  │                                                        │
  │ Partition Key Hash → determines partition assignment   │
  │                                                        │
  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ │
  │  │ Part. 1  │ │ Part. 2  │ │ Part. 3  │ │ Part. 4  │ │
  │  │ 250 RCU  │ │ 250 RCU  │ │ 250 RCU  │ │ 250 RCU  │ │
  │  │          │ │          │ │          │ │          │ │
  │  │ users    │ │ users    │ │ users    │ │ users    │ │
  │  │ A-G      │ │ H-M      │ │ N-S      │ │ T-Z      │ │
  │  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ │
  │       │            │            │            │        │
  │  Txn: 100     Txn: 50     Txn: 150    Txn: 80       │
  │  Scan: 200    Scan: 200   Scan: 200   Scan: 200     │
  │  ─────────    ─────────   ─────────   ─────────     │
  │  Total: 300   Total: 250  Total: 350  Total: 280    │
  │  ❌ Over!     At limit     ❌ Over!    ❌ Over!      │
  └──────────────────────────────────────────────────────┘

  SCAN reads from EVERY partition (full table scan).
  Even though transactions target specific partitions,
  SCAN consumes capacity across ALL partitions.

  FIX: Route reporting to a SEPARATE data store.
```

### CQRS for DynamoDB — Transactional + Reporting

```
┌──────────────────────────────────────────────────────────────┐
│                WRITE PATH (transactional)                     │
│                                                               │
│  App ──────► DynamoDB (orders table)                          │
│              │  RCU: 1000 (100% for transactions)            │
│              │  No scans, no reporting queries ✅              │
│              │                                                │
│              ├── DynamoDB Streams (CDC) ──────────────┐       │
│              │                                        │       │
└──────────────│────────────────────────────────────────│───────┘
               │                                        │
               ▼                                        ▼
┌──────────────────────┐    ┌─────────────────────────────────┐
│ READ PATH (reporting)│    │ ANALYTICS PATH (batch)           │
│                      │    │                                   │
│ Lambda function      │    │ Kinesis Data Firehose             │
│ consumes stream      │    │ → S3 (Parquet format)             │
│ → writes to          │    │ → Athena (SQL queries)            │
│   PostgreSQL/Redshift│    │ → QuickSight (dashboards)        │
│                      │    │                                   │
│ Report queries run   │    │ Zero RCU consumed ✅              │
│ against replica DB   │    │ Handles petabyte-scale            │
│ Zero DynamoDB load ✅│    │                                   │
└──────────────────────┘    └─────────────────────────────────┘
```

---

## Q18: Azure Service Bus — Slow Consumer, Growing DLQ

**Category:** Azure/Cloud | **Difficulty:** Hard

### Scenario

Your Azure Service Bus consumer is processing messages 10x slower than the producer sends them. Dead-letter queue is growing. Max delivery count is being hit. Message lock expiry keeps renewing.

### Root Cause Analysis

```
Message lifecycle in Service Bus:
  1. Message received → LOCKED (default 30s lock)
  2. Consumer processes → COMPLETE (acknowledges)
  3. If processing takes > 30s → lock expires → message redelivered
  4. After MaxDeliveryCount (default 10) → DEAD-LETTERED

Problem:
  Processing time: 45s per message
  Lock duration: 30s
  
  Message locked → processing at 30s mark → lock expires
  → message redelivered to ANOTHER consumer → processed AGAIN
  → both consumers complete → duplicate processing + original gets
  delivery count incremented → eventually dead-lettered
```

### Fixes

| Fix | How |
|-----|-----|
| **Increase lock duration** | Set `LockDuration` to 5 minutes (max: 5 min) |
| **Renew locks in background** | Use `RenewMessageLockAsync` periodically during processing |
| **Reduce processing time** | Optimize consumer logic, batch operations |
| **Increase consumer instances** | Scale consumers horizontally (up to session/partition count) |
| **Prefetch** | Set `PrefetchCount` to batch messages, reduce round-trips |
| **Poison message handling** | Detect repeated failures, move to custom DLQ early |

### Azure Service Bus Message Lifecycle

```
═══════════════════════════════════════════════════════════════
  MESSAGE LIFECYCLE — FROM PRODUCER TO DLQ
═══════════════════════════════════════════════════════════════

  Producer sends message
        │
        ▼
  ┌─────────────────┐
  │   Service Bus   │
  │   Queue/Topic   │
  │                  │
  │  Message state:  │
  │  ACTIVE          │
  └────────┬────────┘
           │ Consumer receives (PeekLock mode)
           ▼
  ┌─────────────────┐
  │  LOCKED          │ Lock duration: 30s (default)
  │  (invisible to   │
  │   others)        │
  └────────┬────────┘
           │
     ┌─────┼──────────────────────────────┐
     │     │                              │
     ▼     ▼                              ▼
  Complete   Abandon                  Lock Expires
  (success)  (retry)                  (auto-abandon)
     │        │                           │
     ▼        ▼                           ▼
  Message   Delivery count++           Delivery count++
  REMOVED   Back to ACTIVE             Back to ACTIVE
  ✅        (retry later)              (redelivered)
              │                           │
              └────────────┬──────────────┘
                           │
                           ▼
              ┌─────────────────────┐
              │ delivery count >    │──YES──► Dead-Letter Queue
              │ MaxDeliveryCount?   │         (poison message)
              │ (default: 10)       │
              └────────┬────────────┘
                       │ NO
                       ▼
                  Retry again...

PROBLEM: If processing takes 45s but lock is 30s:
  T=0:   Consumer receives msg, starts processing
  T=30:  Lock expires! Message goes back to queue
  T=31:  ANOTHER consumer receives same message
  T=45:  Original consumer finishes, tries to Complete
          → ERROR: "MessageLockLost" ❌
          Second consumer also processing → DUPLICATE!
```

### Consumer Scaling Strategy

```
┌────────────────────────────────────────────────────────────┐
│ Scaling Strategy: Match consumers to processing capacity   │
│                                                             │
│ Scenario: 1000 msg/sec incoming, 200ms processing each     │
│                                                             │
│ Single consumer:                                            │
│   1 msg / 200ms = 5 msg/sec                                │
│   Lag: 1000 - 5 = 995 msg/sec growing! ❌                  │
│                                                             │
│ With prefetch (batch 50):                                  │
│   50 msgs / (200ms × 50) = 5 msg/sec (no help, still slow)│
│   But reduced round-trip overhead ✅                       │
│                                                             │
│ With 200 consumers:                                        │
│   200 × 5 = 1000 msg/sec ← matches incoming rate ✅       │
│                                                             │
│ With 20 consumers + batch processing (50 at a time):       │
│   20 × 50 msgs / batch_200ms = 5000 msg/sec ← surplus ✅  │
│                                                             │
│ Best: Combine scaling + batching + optimized processing    │
└────────────────────────────────────────────────────────────┘
```

---

## Q19: Event Sourcing — Banking Ledger at 50K TPS

**Category:** System Design | **Difficulty:** Expert

### Scenario

Design an event sourcing system for a banking ledger that must handle 50K TPS, support point-in-time replay for audits going back 10 years, and remain ACID-compliant for balance queries.

### Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    COMMAND SIDE (Write)                   │
│                                                          │
│  Command → Validate → Append Event → Publish             │
│                         │                                 │
│                    ┌────▼─────┐                           │
│                    │  Event   │ append-only log            │
│                    │  Store   │ (PostgreSQL / EventStoreDB)│
│                    │          │ 50K TPS with partitioning  │
│                    └────┬─────┘                           │
│                         │                                 │
└─────────────────────────│───────────────────────────────┘
                          │ events published
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    QUERY SIDE (Read — CQRS)               │
│                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ Balance View │  │ Statement    │  │ Audit View   │   │
│  │ (Redis)      │  │ View (SQL)   │  │ (S3 + Athena)│   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
│                                                          │
│  Projections consume events and build read models.       │
│  Each view is optimized for its query pattern.           │
└─────────────────────────────────────────────────────────┘
```

### Key Design Decisions

```
1. EVENT STORE: Append-only, immutable log
   ├── Each event: {accountId, type, amount, timestamp, version}
   ├── Partition by accountId (each account = ordered stream)
   ├── ACID within partition (PostgreSQL with SERIALIZABLE)
   └── No updates, no deletes → perfect audit trail

2. SNAPSHOTTING: Don't replay 10 years of events
   ├── Snapshot every 1000 events per account
   ├── Balance query: load latest snapshot + replay events since
   ├── Snapshot: {accountId, balance, version, timestamp}
   └── Reduces replay from millions to hundreds of events

3. STORAGE TIERS (10-year retention):
   ├── HOT (0-30 days): PostgreSQL (fast queries)
   ├── WARM (30 days - 1 year): S3 Standard (cheaper)
   ├── COLD (1-10 years): S3 Glacier (archival)
   └── Total: ~5 TB/year at 50K TPS

4. ACID BALANCE QUERIES:
   ├── Command side: optimistic locking with event version
   ├── Read side: projections update balance in Redis (fast reads)
   ├── Strong consistency: read from event store + compute
   ├── Eventual consistency: read from projection (< 100ms lag)
   └── Financial queries use strong consistency path
```

### Event Sourcing vs Traditional CRUD

```
═══════════════════════════════════════════════════════════════
  CRUD (Traditional)          EVENT SOURCING (Banking Ledger)
═══════════════════════════════════════════════════════════════

  UPDATE account                INSERT INTO events
  SET balance = 900             (AccountDebited, $100)
  WHERE id = 'alice'

  ┌─────────────────┐          ┌─────────────────────────────┐
  │ accounts table   │          │ events table (append-only)   │
  │                  │          │                               │
  │ id    │ balance  │          │ id │ account │ type    │ amt  │
  │ alice │ 900      │          │ 1  │ alice   │ Created │ 0   │
  │                  │          │ 2  │ alice   │ Deposit │ 1000│
  │ Previous state?  │          │ 3  │ alice   │ Debit   │ -100│
  │ LOST! ❌         │          │                               │
  └─────────────────┘          │ Balance = SUM(amt)            │
                               │          = 0+1000-100 = 900   │
                               │                               │
                               │ Full history preserved ✅     │
                               │ Replay to any point in time ✅│
                               │ Perfect audit trail ✅        │
                               └─────────────────────────────┘
```

### Snapshotting — Performance Optimization

```
Without snapshots (replay 10 years of events):

  Event 1 ─► Event 2 ─► ... ─► Event 5,000,000 ─► Current Balance
  └──────── replay ALL events to compute balance ────────────┘
  Time: minutes to compute balance! ❌

With snapshots (every 1000 events):

  Event 1-1000 ─► [Snapshot: balance=$50,000, version=1000]
  Event 1001-2000 ─► [Snapshot: balance=$48,200, version=2000]
  ...
  Event 4999001-5000000 ─► [Snapshot: balance=$125,000, version=5M]

  To get current balance:
    1. Load latest snapshot (version=5M, balance=$125,000)
    2. Replay only events AFTER version 5M
    3. If 23 new events since snapshot → replay 23, not 5M!
    Time: milliseconds ✅
```

### Storage Tiering for 10-Year Retention

```
┌──────────────────────────────────────────────────────────────┐
│                   DATA TEMPERATURE TIERS                      │
│                                                               │
│  HOT (0-30 days) ─── PostgreSQL (SSD) ─── ~$0.10/GB/month   │
│  ├── Most recent transactions                                │
│  ├── Low latency queries (< 10ms)                            │
│  ├── ~500 GB (50K TPS × 30 days × avg 300 bytes)             │
│  └── Replicated for HA                                       │
│                                                               │
│  ▼ Background migration (monthly)                            │
│                                                               │
│  WARM (30 days - 1 year) ── S3 Standard ── ~$0.023/GB/month │
│  ├── Recent history, occasional audits                       │
│  ├── Query via Athena (seconds)                              │
│  ├── Parquet format (columnar, compressed)                   │
│  └── ~5 TB                                                   │
│                                                               │
│  ▼ Lifecycle policy (yearly)                                 │
│                                                               │
│  COLD (1-10 years) ── S3 Glacier ── ~$0.004/GB/month        │
│  ├── Regulatory compliance, rare audits                      │
│  ├── Retrieval: minutes to hours                             │
│  ├── ~40 TB total (10 years)                                 │
│  └── Cost: ~$160/month for 10 years of data!                │
│                                                               │
│  Monthly storage costs (estimated):                          │
│  ┌───────────┬───────────┬─────────────────────┐             │
│  │ Tier      │ Size      │ Monthly Cost         │             │
│  ├───────────┼───────────┼─────────────────────┤             │
│  │ Hot       │ 500 GB    │ $50                  │             │
│  │ Warm      │ 5 TB      │ $115                 │             │
│  │ Cold      │ 40 TB     │ $160                 │             │
│  │ TOTAL     │ 45.5 TB   │ ~$325/month          │             │
│  └───────────┴───────────┴─────────────────────┘             │
└──────────────────────────────────────────────────────────────┘
```

---

## Q20: Saga Compensation Failure During Network Partition

**Category:** Transactions | **Difficulty:** Expert

### Scenario

In a microservices saga pattern, Step 3 of a 7-step distributed transaction succeeds but the compensating transaction for Step 2 fails during rollback due to a network partition. How do you achieve eventual consistency?

### The Problem

```
Saga: Book Flight → Book Hotel → Charge Card → Send Confirmation
                                    ↑ FAILS!

Compensation (rollback):
  Step 3 compensation: refund card → SUCCESS ✅
  Step 2 compensation: cancel hotel → NETWORK PARTITION ❌
  Step 1 compensation: cancel flight → can't proceed until Step 2 done

  Hotel is still booked, but payment was refunded.
  INCONSISTENT STATE!
```

### Solution: Resilient Saga Design

```
1. SAGA LOG (persistent state machine):
   ┌────────────────────────────────────────────┐
   │ saga_log table                              │
   │ saga_id │ step │ status      │ retry_count │
   │ S001    │ 1    │ COMPLETED   │ 0           │
   │ S001    │ 2    │ COMPLETED   │ 0           │
   │ S001    │ 3    │ FAILED      │ 0           │
   │ S001    │ 3    │ COMPENSATED │ 1           │
   │ S001    │ 2    │ COMPENSATING│ 3 ← retrying│
   │ S001    │ 1    │ PENDING_COMP│ 0           │
   └────────────────────────────────────────────┘

2. RETRY WITH EXPONENTIAL BACKOFF:
   Compensation failure → retry after 1s, 2s, 4s, 8s...
   Each compensation MUST be idempotent (safe to retry).

3. DEAD-LETTER for failed compensations:
   After max retries → move to DLQ
   Alert operations team → human-in-the-loop resolution

4. IDEMPOTENT COMPENSATIONS:
   cancelHotel(bookingId):
     if booking already cancelled → return SUCCESS (idempotent)
     if booking exists → cancel it → return SUCCESS
     if booking not found → return SUCCESS (never existed or already cancelled)

5. TIMEOUT + RESERVATION PATTERN:
   Instead of immediate booking:
     Step 2: RESERVE hotel (auto-expires in 15 min)
     Step 3: CONFIRM hotel (only after payment succeeds)
   
   If saga fails → reservation expires automatically
   No compensation needed! ✅
```

### Orchestration vs Choreography — Two Saga Styles

```
═══════════════════════════════════════════════════════════════
  ORCHESTRATION (Central Coordinator)
═══════════════════════════════════════════════════════════════

             ┌────────────────────────────────────┐
             │          Saga Orchestrator          │
             │                                    │
             │  saga_log persists every step      │
             │                                    │
             │  1. BOOK FLIGHT ─────────────────► Flight Service
             │  2. BOOK HOTEL  ─────────────────► Hotel Service
             │  3. CHARGE CARD ─────────────────► Payment Service
             │         │                          │
             │         ▼ FAILED                   │
             │  4. COMPENSATE 2 ────────────────► Hotel Service
             │     ├── retry 1: NETWORK ERROR     │
             │     ├── retry 2: NETWORK ERROR     │
             │     └── retry 3: SUCCESS ✅        │
             │  5. COMPENSATE 1 ────────────────► Flight Service
             │         SUCCESS ✅                 │
             │                                    │
             │  Status: COMPENSATED              │
             └────────────────────────────────────┘

  Pros: Easy to understand, centralized logic, easy to debug
  Cons: Single point of failure, tight coupling

═══════════════════════════════════════════════════════════════
  CHOREOGRAPHY (Event-Driven, No Central Coordinator)
═══════════════════════════════════════════════════════════════

  Flight Service                  Hotel Service
  ┌──────────────┐                ┌──────────────┐
  │ Book flight  │──"FlightBooked"│ Book hotel   │
  │              │───────────────►│              │
  └──────────────┘                └──────┬───────┘
                                         │ "HotelBooked"
                                         ▼
                                  Payment Service
                                  ┌──────────────┐
                                  │ Charge card  │
                                  │   FAILED! ❌  │
                                  └──────┬───────┘
                                         │ "PaymentFailed"
                              ┌──────────┴──────────┐
                              ▼                     ▼
                       Hotel Service          Flight Service
                       ┌──────────────┐       ┌──────────────┐
                       │ Cancel hotel │       │ Cancel flight│
                       │ (compensate) │       │ (compensate) │
                       └──────────────┘       └──────────────┘

  Pros: Loosely coupled, no single point of failure
  Cons: Harder to debug, distributed logic, harder to monitor
```

### Architecture for Saga Orchestration

```
┌────────────────────────────────────────────────┐
│              Saga Orchestrator                   │
│                                                  │
│  Execute steps in order:                         │
│  ├── Step 1: Book Flight ✅                      │
│  ├── Step 2: Book Hotel  ✅                      │
│  ├── Step 3: Charge Card ❌ (failed)             │
│  │                                               │
│  Compensate in reverse:                          │
│  ├── Comp 3: (nothing to compensate)             │
│  ├── Comp 2: Cancel Hotel                        │
│  │   ├── Attempt 1: NETWORK ERROR                │
│  │   ├── Attempt 2: NETWORK ERROR                │
│  │   ├── Attempt 3: SUCCESS ✅                   │
│  ├── Comp 1: Cancel Flight ✅                    │
│  │                                               │
│  Saga Status: COMPENSATED ✅                     │
│                                                  │
│  Persisted to saga_log at each step.             │
│  On restart: reads log → resumes from last state.│
└────────────────────────────────────────────────┘
```

### Reservation Pattern — Self-Healing Sagas

```
═══════════════════════════════════════════════════════════════
  RESERVATION PATTERN — No Compensation Needed!
═══════════════════════════════════════════════════════════════

Traditional approach (needs compensation):
  Step 1: BOOK flight (immediate, permanent)
  Step 2: BOOK hotel (immediate, permanent)
  Step 3: Charge card → FAILS
  Step 4: COMPENSATE hotel → what if this fails too? 😰
  Step 5: COMPENSATE flight → what if this fails too? 😰

Reservation approach (self-healing):
  Step 1: RESERVE flight (auto-expires in 15 minutes)
           ┌──────────────────────────────────┐
           │ Reservation: FLT-123             │
           │ Status: RESERVED (not confirmed) │
           │ Expires: T+15min                 │
           │ Auto-cancels if not confirmed    │
           └──────────────────────────────────┘

  Step 2: RESERVE hotel (auto-expires in 15 minutes)
           ┌──────────────────────────────────┐
           │ Reservation: HTL-456             │
           │ Status: RESERVED (not confirmed) │
           │ Expires: T+15min                 │
           └──────────────────────────────────┘

  Step 3: Charge card
           ├── SUCCESS → CONFIRM flight + CONFIRM hotel ✅
           │
           └── FAILURE → DO NOTHING!
               Reservations auto-expire in 15 minutes.
               No compensation needed! ✅
               No network partition risk for compensation!

  Real-world: Hotel reservation sites hold rooms for 15 min.
              Airlines hold seats during booking flow.
              Payment gateways use auth + capture (2-phase).
```

---

---

## Interview Preparation Strategy

```
═══════════════════════════════════════════════════════════════
  HOW TO USE THESE 20 QUESTIONS
═══════════════════════════════════════════════════════════════

  Phase 1: UNDERSTAND (Day 1-3)
    Read each question. Understand the SCENARIO.
    Draw the architecture diagrams yourself.
    Can you explain the ROOT CAUSE to a colleague?

  Phase 2: INTERNALIZE (Day 4-7)
    For each question, practice explaining:
    1. The root cause (WHY it happens)
    2. The diagnosis steps (HOW to find it)
    3. Multiple solutions (TRADE-OFFS between them)
    4. Production best practices (REAL-WORLD experience)

  Phase 3: CROSS-REFERENCE (Day 8-10)
    These questions overlap! Identify connections:
    ├── Q1 + Q13: Both about JVM resource management
    ├── Q2 + Q20: Both about distributed transactions
    ├── Q3 + Q12: Both about Kafka reliability
    ├── Q5 + Q8: Both about rate limiting
    ├── Q10 + Q4: Both about caching architecture
    ├── Q15 + Q16: Both about latency diagnosis
    └── Q7 + Q19: Both about data evolution

  Phase 4: SIMULATE (Day 11-14)
    Practice with a timer (5 min per question).
    Whiteboard the architecture diagrams.
    Explain trade-offs out loud.
```

---

## Quick Reference — All 20 Questions

| # | Topic | Category | Key Concept |
|---|-------|----------|-------------|
| 1 | ThreadPoolExecutor + LinkedBlockingQueue | Multithreading | Unbounded queue prevents thread scaling |
| 2 | Distributed deadlock across services | Microservices | DB-per-service, saga, async messaging |
| 3 | Kafka consumer lag 10M messages | Kafka | Partition ceiling, batch processing, rebalance storms |
| 4 | Singleton cache in Kubernetes | Design Patterns | Centralized cache (Redis), event-driven invalidation |
| 5 | Rate limiting bypass via IP rotation | API Gateway | Device fingerprinting, JWT-based limits, WAF |
| 6 | @Transactional self-invocation | Transactions | AOP proxy bypass, self-injection, AspectJ |
| 7 | Blue-green deployment DB migration | Deployment | Expand/contract pattern, backward-compatible DDL |
| 8 | Distributed rate limiter at scale | System Design | Local+global rate limiting, token bucket, eventual consistency |
| 9 | PostgreSQL 50ms → 45s query | SQL/NoSQL | Stale statistics, autovacuum, index bloat |
| 10 | Redis failover cache stampede | Caching | Mutex lock, probabilistic early expiry, jitter |
| 11 | CompletableFuture stale data | Multithreading | ThreadLocal pollution, JMM visibility, volatile |
| 12 | Kafka duplicate charges | Kafka | Idempotent producer, dedup table, outbox pattern |
| 13 | ECS OOMKilled despite heap limits | AWS/Cloud | Off-heap: Metaspace, threads, DirectByteBuffer, NMT |
| 14 | PUT vs PATCH API conflict | API Design | API standards governance, OpenAPI, versioning |
| 15 | P99 latency spike, no errors | Monitoring | Traces, thread dumps, GC logs, connection pool |
| 16 | Latency cascade A→B→C→D | Microservices | Circuit breaker, bulkhead, deadline propagation |
| 17 | DynamoDB scan throttling | NoSQL | Streams + Athena, GSI isolation, export to S3 |
| 18 | Azure Service Bus slow consumer | Azure/Cloud | Lock renewal, poison messages, prefetch |
| 19 | Event sourcing banking ledger | System Design | CQRS, snapshotting, hot/warm/cold storage |
| 20 | Saga compensation failure | Transactions | Idempotent compensations, saga log, reservation pattern |

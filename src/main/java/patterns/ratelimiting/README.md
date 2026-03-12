# Rate Limiting

## What is it?

Rate limiting controls the **number of requests** a client can make to a service within a given time period. It acts as a gatekeeper: *"You can make X requests per Y seconds. After that, wait."*

```
Without Rate Limiting:                With Rate Limiting:

Client ─────────────────► Service     Client ──► [Rate Limiter] ──► Service
Client ─────────────────► Service              ├── 200 OK  ✅
Client ─────────────────► Service              ├── 200 OK  ✅
Client ─────────────────► Service              ├── 200 OK  ✅
Client ─────────────────► (CRASH!)             └── 429 Too Many Requests ❌
```

> **Real-world analogy:** A highway toll booth. Cars arrive at random speeds, but the booth processes N cars per minute. If too many arrive at once, they queue up or are turned away.

### Why Rate Limiting Matters

| Problem                    | Without Rate Limiting              | With Rate Limiting                    |
| -------------------------- | ---------------------------------- | ------------------------------------- |
| **DDoS Attack**            | 1M req/sec → service dies          | Attacker limited → service survives   |
| **Noisy Neighbor**         | One heavy user starves everyone    | Each user gets a fair share           |
| **Cost Control**           | Runaway script → $50K cloud bill   | Budget protected, alerts fire early   |
| **Cascading Failure**      | One slow service takes down all    | Backpressure prevents domino collapse |
| **API Abuse**              | Scrapers consume all resources     | Blocked after hitting limit           |

---

## Core Algorithms — In Depth

### 1. Fixed Window Counter

Divide time into fixed windows (e.g., each minute). Count requests per window. Reset at boundary.

```
Window:    |---- 12:00:00 - 12:00:59 ----|---- 12:01:00 - 12:01:59 ----|
Limit:     5 requests per window

Requests:  ✅ ✅ ✅ ✅ ✅ ❌ ❌           ✅ ✅ (counter reset!)
Count:      1  2  3  4  5  X  X            1  2

THE BOUNDARY PROBLEM:
           |-------- Window 1 --------|----- Window 2 ---------|
                              ✅✅✅✅✅  ✅✅✅✅✅
                              ^  12:00:55    ^  12:01:05

  10 requests in 10 seconds! (5 at end of W1 + 5 at start of W2)
  The limit is 5/minute, but a 10-second burst got 2x the limit through.
```

**Implementation:**
```
counter = 0, windowStart = now

function tryAcquire():
    if (now - windowStart >= windowMs):
        windowStart = now
        counter = 0
    if (counter < limit):
        counter++
        return ALLOW
    return REJECT
```

**Real-world:** Login attempt limiting (5 attempts/minute per IP). Simple, but use sliding window for security-critical endpoints to avoid the boundary exploit.

---

### 2. Token Bucket (Most Popular)

Imagine a bucket holding tokens. Tokens refill at a constant rate. Each request costs 1 token. Empty bucket = rejected. Bucket has a max capacity (burst limit).

```
Bucket capacity: 10 tokens    Refill rate: 2 tokens/sec

Time 0s:  [🪙🪙🪙🪙🪙🪙🪙🪙🪙🪙]  = 10 tokens (full)

Burst of 8 requests:
Time 1s:  [🪙🪙                    ]  = 2 tokens left
          + 2 refilled
Time 2s:  [🪙🪙🪙🪙                ]  = 4 tokens (2 left + 2 refilled)
Time 3s:  No requests. [🪙🪙🪙🪙🪙🪙]  = 6 tokens (4 + 2)

KEY INSIGHT:
  Sustained rate = refill rate (2/sec = 120/min)
  Burst capacity = bucket size (can handle 10 requests at once)
  The bucket smooths traffic while allowing short bursts
```

**Implementation:**
```
function tryAcquire():
    elapsed = now - lastRefillTime
    tokens = min(maxTokens, tokens + elapsed * refillRate)
    lastRefillTime = now
    if (tokens >= 1):
        tokens -= 1
        return ALLOW
    return REJECT
```

**Real-world:** Used by AWS API Gateway, Stripe, GitHub, Shopify, Google Cloud. The default choice for most APIs because it handles bursts gracefully.

```
AWS API Gateway:
  Default: 10,000 req/sec (sustained) + 5,000 burst

  Flash sale at 12:00:00:
    15,000 req/sec → 5,000 burst tokens consumed + 10,000 sustained → ALL served ✅
    12:00:01 → burst depleted → only 10,000 served, 5,000 get 429 ❌
    12:00:10 → burst recovered → back to normal ✅
```

---

### 3. Leaky Bucket

Requests enter a bucket. The bucket "leaks" (processes) at a **constant rate**. If the bucket is full, new requests are dropped. Output is always smooth regardless of input.

```
Input (bursty):     ████  ██  ████████    ██
                    ↓↓↓↓  ↓↓  ↓↓↓↓↓↓↓↓    ↓↓
                  ┌──────────────────────────┐
                  │    Leaky Bucket          │  ← overflow = dropped
                  │    capacity = 5          │
                  └──────────┬───────────────┘
                             │ (leak rate = 1 req/sec)
                             ▼
Output (smooth):   █  █  █  █  █  █  █  █  █

KEY DIFFERENCE vs Token Bucket:
  Token Bucket:  allows bursts up to bucket capacity
  Leaky Bucket:  output is ALWAYS constant (strict smoothing)
```

**Real-world:** ISP bandwidth shaping. Your 100 Mbps plan uses a leaky bucket to ensure steady output even when you try to download at 300 Mbps.

---

### 4. Sliding Window Log

Store the **timestamp of every request**. On each new request, remove timestamps outside the window, then count what remains. No boundary problem.

```
Window size: 60 seconds    Limit: 5 requests

Timeline:
  12:00:10 → add ts. Log: [10]                    count=1 ✅
  12:00:20 → add ts. Log: [10, 20]                count=2 ✅
  12:00:30 → add ts. Log: [10, 20, 30]            count=3 ✅
  12:00:45 → add ts. Log: [10, 20, 30, 45]        count=4 ✅
  12:00:55 → add ts. Log: [10, 20, 30, 45, 55]    count=5 ✅
  12:01:05 → remove 10 (outside window)
             Log: [20, 30, 45, 55]                 count=4 ✅ (add 65)

  No boundary problem! The window always looks back exactly 60 seconds.
```

**Trade-off:** Precise, but stores one timestamp per request. At 10,000 req/sec, that's 600K timestamps per user per minute.

**Real-world:** Twitter API uses sliding windows so a developer who sends 300 requests at 12:14:59 can't send 300 more at 12:15:01.

---

### 5. Sliding Window Counter

Hybrid of fixed window + sliding window log. Uses counts from current and previous window with a **weighted overlap**.

```
Previous window count: 8       Current window count: 3
Window size: 60 seconds
Current time: 12:01:45 (45 seconds into current window)

Overlap of previous window: (60 - 45) / 60 = 25%
Estimated count = previous × overlap + current = 8 × 0.25 + 3 = 5

Limit: 6 → 5 < 6 → ALLOW ✅

Memory: just 2 integers per client (no timestamps stored).
Accuracy: ~98% compared to true sliding window.
```

**Real-world:** Cloudflare uses this to protect 30M+ websites. At 50M+ req/sec globally:
- Sliding log: 50M × 100KB = 5 TB of memory per minute
- Sliding counter: 50M × 16B = 800 MB ✅

---

### Algorithm Comparison

```
┌─────────────────┬────────────┬──────────┬──────────┬──────────────┐
│                 │ Burst      │ Accuracy │ Memory   │ Best For     │
│                 │ Friendly?  │          │          │              │
├─────────────────┼────────────┼──────────┼──────────┼──────────────┤
│ Fixed Window    │ ❌ (edge)  │ Low      │ O(1)     │ Simple APIs  │
│ Sliding Log     │ ✅         │ Perfect  │ O(n)     │ Low-traffic  │
│ Sliding Counter │ ✅         │ ~98%     │ O(1)     │ High-traffic │
│ Token Bucket    │ ✅ (best)  │ Good     │ O(1)     │ Most APIs    │
│ Leaky Bucket    │ ❌         │ Good     │ O(1)     │ Traffic shape│
└─────────────────┴────────────┴──────────┴──────────┴──────────────┘
```

### How to Choose an Algorithm

```
Need constant output rate?    → Leaky Bucket
Need burst tolerance?         → Token Bucket ← DEFAULT CHOICE
Need 100% accuracy?           → Sliding Window Log
Need minimal memory?          → Fixed Window or Sliding Counter
Building an API gateway?      → Token Bucket + Sliding Counter (combo)
```

---

## Types of Rate Limiting

### By Scope

| Type             | Description                               | Example                            | Real-World                          |
| ---------------- | ----------------------------------------- | ---------------------------------- | ----------------------------------- |
| **Per-User**     | Each user has their own rate limit        | 100 req/min per API key            | Stripe: 100 req/sec per API key     |
| **Per-IP**       | Limit by client IP address                | 50 req/sec per IP                  | Cloudflare: DDoS protection         |
| **Per-Endpoint** | Different limits for different API routes | /search: 10/min, /write: 100/min  | OpenAI: GPT-4 500/min, GPT-3.5 3500/min |
| **Global**       | Single limit for the entire service       | 10,000 req/sec total               | AWS API Gateway default             |
| **Tiered**       | Different limits by subscription tier     | Free: 5/min, Premium: 100/min     | Twilio: 1 SMS/sec (free) vs 400/sec (enterprise) |
| **Geo-Based**    | Regional limits based on geography        | US: 100/min, EU: 50/min           | Uber: per-city token buckets        |


### By Resource

| Type              | Description                                  | Example                              | Real-World                            |
| ----------------- | -------------------------------------------- | ------------------------------------ | ------------------------------------- |
| **Request Count** | Limit number of requests                     | 100 req/minute                       | GitHub: 5,000 req/hour                |
| **Bandwidth**     | Limit data transferred                       | 10 MB/minute                         | CDN: bytes per second per client      |
| **Cost-Based**    | Assign costs to operations; limit total cost | Read=1pt, Write=3pt, limit=100pt/min | Google Cloud: API quota units         |
| **Concurrency**   | Limit simultaneous in-flight requests        | Max 10 concurrent connections        | AWS Lambda: 1,000 concurrent/account  |

---

## Real-World Rate Limiting — Case Studies

### GitHub API

```
GitHub's rate limits:
  Unauthenticated:  60 requests per hour
  Authenticated:    5,000 requests per hour
  GitHub Actions:   1,000 requests per hour

Response headers on every call:
  X-RateLimit-Limit: 5000           ← your max
  X-RateLimit-Remaining: 4987       ← how many left
  X-RateLimit-Reset: 1710523200     ← Unix timestamp when window resets

When exceeded:
  HTTP 403 Forbidden
  { "message": "API rate limit exceeded for user" }
```

### Stripe API

```
Stripe rate limits:
  Live mode:  100 requests/sec per API key
  Test mode:  25 requests/sec per API key
  Algorithm:  Token Bucket

  Merchant A: 50 req/sec  → fine ✅
  Merchant B: 150 req/sec → rate limited after 100 ❌
  Merchant C: 10 req/sec  → fine ✅

  Merchant B's limit doesn't affect A or C.

  Response when limited:
    HTTP 429 Too Many Requests
    { "error": { "type": "rate_limit_error" } }
    Retry-After: 1
```

### Banking — Fraud Detection

```
Banks use rate limiting for real-time fraud detection:

  Rule 1: Max 3 ATM withdrawals per hour per card
  Rule 2: Max $2,000 total ATM per day
  Rule 3: Max 10 POS transactions per hour
  Rule 4: No transactions in 2+ countries within 1 hour

  Fraud scenario:
    12:00 → ATM London, $500       ← Rule 1: 1/3 ✅
    12:05 → ATM London, $500       ← Rule 1: 2/3 ✅
    12:10 → POS Paris, €200        ← Rule 4: London + Paris in 10 min! ❌

  BLOCKED. Card frozen. SMS sent to cardholder.
  Algorithm: Sliding window log per card per rule (no boundary exploit)
```

### Netflix — Microservice Protection

```
Netflix has 1,000+ microservices. Without rate limiting, one slow
service could cascade into a global outage.

┌─────────┐     ┌──────────────────┐     ┌──────────────────┐
│ Mobile  │────►│  Zuul Gateway    │────►│ Recommendation   │
│ App     │     │ Rate: 2000/sec   │     │ Service          │
│         │     │ per device type  │     │ (sometimes slow) │
└─────────┘     └──────────────────┘     └──────────────────┘
                         │
                         │ If latency > 500ms:
                         ▼
                ┌──────────────────┐
                │ Circuit Breaker  │
                │ CLOSED → OPEN    │  "Stop calling slow service"
                │ After 30s:      │  "Return cached recommendations"
                │ HALF_OPEN       │  "Try 1 test request"
                │ OK? → CLOSED    │  "Resume normal traffic"
                └──────────────────┘
```

---

## Where to Place Rate Limiting — Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                   CLIENT                                       │
│  (browser, mobile app, API client)                             │
└───────────────────────┬────────────────────────────────────────┘
                        │
                        ▼
┌──────────────────────────────────────────────────────┐
│ Layer 1: CDN / Edge (Cloudflare, AWS CloudFront)     │  ← DDoS protection
│   - IP-based rate limiting                           │     10M+ req/sec
│   - Geographic blocking                              │
│   - Bot detection (CAPTCHA challenge)                │
└───────────────────────┬──────────────────────────────┘
                        │
                        ▼
┌──────────────────────────────────────────────────────┐
│ Layer 2: API Gateway (Kong, AWS API GW, Nginx)       │  ← Per-user /
│   - Token bucket per API key                         │     per-API-key
│   - Per-endpoint limits                              │     limits
│   - Response: 429 + Retry-After header               │
└───────────────────────┬──────────────────────────────┘
                        │
                        ▼
┌──────────────────────────────────────────────────────┐
│ Layer 3: Application / Service                       │  ← Business-specific
│   - Tiered limits (free vs premium)                  │     limits
│   - Cost-based limits (heavy queries)                │
│   - Circuit breaker for downstream calls             │
│   - Concurrency limits for DB connections            │
└───────────────────────┬──────────────────────────────┘
                        │
                        ▼
┌──────────────────────────────────────────────────────┐
│ Layer 4: Database / Backend                          │  ← Self-protection
│   - Connection pool limits                           │
│   - Query timeout limits                             │
│   - Read replica routing for heavy reads             │
└──────────────────────────────────────────────────────┘

Most production systems use ALL 4 layers.
Each layer catches different types of abuse.
```

---

## Distributed Rate Limiting

When your service runs on **multiple servers**, each server needs to share state.

### The Problem

```
Single server:                       Multiple servers:

┌──────────┐                         ┌──────────┐
│ Server   │                         │ Server 1 │ counter = 3
│ counter=5│                         └──────────┘
│ limit=5  │                         ┌──────────┐
│ → REJECT │                         │ Server 2 │ counter = 3
└──────────┘                         └──────────┘

User sent 5 → correctly blocked.    User sent 6 (3 to each server)
                                     Each thinks count=3 < limit=5
                                     → ALL allowed! Limit violated!
```

### Solution: Centralized Counter (Redis)

```
┌──────────┐      ┌──────────┐      ┌──────────┐
│ Server 1 │──┐   │ Server 2 │──┐   │ Server 3 │──┐
└──────────┘  │   └──────────┘  │   └──────────┘  │
              └─────────┬───────┘───────┘          │
                        ▼                           │
                 ┌──────────────┐                   │
                 │    Redis     │◄──────────────────┘
                 │              │
                 │ INCR key     │  ← atomic increment
                 │ EXPIRE key   │  ← auto-cleanup
                 │ count: 5     │
                 └──────────────┘

Pros: Simple, consistent, battle-tested
Cons: Redis becomes single point of failure, network latency per request
```

---

## Advanced Patterns — In Depth

### Circuit Breaker

Prevents repeated calls to a failing service. Three states:

```
         success
    ┌───────────────┐
    │               │
    ▼               │
┌────────┐    ┌─────────┐    ┌───────────┐
│ CLOSED │───►│  OPEN   │───►│ HALF_OPEN │
│(normal)│    │(blocked)│    │(test 1req)│
└────────┘    └─────────┘    └───────────┘
     ▲         failure          │    │
     │         threshold        │    │
     │         reached          │    │
     │                    success   failure
     └──────────────────────┘    └──► OPEN

CLOSED:    All requests pass through. Count failures.
           If failure rate > threshold (e.g., 50% in 10s) → OPEN

OPEN:      All requests immediately rejected (429 or cached response).
           After timeout (e.g., 30s) → HALF_OPEN

HALF_OPEN: Allow ONE test request through.
           If succeeds → CLOSED (resume normal).
           If fails → OPEN (wait again).
```

**Real-world:** Netflix Hystrix. When the recommendation service slows down, the circuit breaker opens and returns cached recommendations instead of waiting and cascading the failure.

---

### Exponential Backoff with Jitter

When you receive `429 Too Many Requests`, don't retry immediately.

```
BAD:  Retry instantly → server stays overloaded → more 429s → infinite loop

GOOD: Exponential backoff with jitter

  Attempt 1: wait 1s  + random(0-500ms) = ~1.3s
  Attempt 2: wait 2s  + random(0-500ms) = ~2.2s
  Attempt 3: wait 4s  + random(0-500ms) = ~4.4s
  Attempt 4: wait 8s  + random(0-500ms) = ~8.1s
  Attempt 5: wait 16s + random(0-500ms) = ~16.3s (cap)

  Formula: delay = min(cap, base × 2^attempt) + random(0, jitter)

Why jitter?
  Without jitter: 1,000 clients all retry at exactly 2s, 4s, 8s
    → "thundering herd" — server gets slammed at each retry interval

  With jitter: 1,000 clients retry at random times within each window
    → load spreads evenly
```

**Real-world:** AWS SDKs have this built-in. Every AWS service client retries with exponential backoff + full jitter by default.

---

### Adaptive / Dynamic Rate Limiting

Instead of fixed limits, adjust based on **server health**.

```
                  ┌───────────────────────────────────┐
                  │        Adaptive Rate Limiter        │
                  │                                     │
                  │  Monitor:                           │
                  │  ├── Error rate (5xx responses)     │
                  │  ├── Latency (p99)                  │
                  │  ├── CPU utilization                │
                  │  └── Queue depth                    │
                  │                                     │
                  │  Response:                          │
                  │  ├── All healthy → limit = 1000/sec │
                  │  ├── Error > 5% → limit = 500/sec  │
                  │  ├── Error > 20% → limit = 100/sec │
                  │  └── Error > 50% → limit = 10/sec  │
                  └───────────────────────────────────┘

Normal:   [████████████████████] 1000 req/sec allowed
Stressed: [████████████        ] 500 req/sec allowed
Critical: [████                ] 100 req/sec allowed
```

**Real-world:** Uber adjusts rate limits per geo-zone based on driver supply. During high demand (New Year's Eve), limits tighten and surge pricing kicks in.

---

### Penalty Box

Repeat violators get **escalating bans** — progressively longer lockouts.

```
Violation 1: Warning (allow, log)
Violation 2: Block for 1 minute
Violation 3: Block for 5 minutes
Violation 4: Block for 1 hour
Violation 5: Block for 24 hours
Violation 6+: Permanent ban (manual review)

┌─────────────────────────────────────────────┐
│ User "scraper-bot-42":                       │
│   Violations: 4                              │
│   Current penalty: BLOCKED until 13:45:00    │
│   Next violation penalty: 24-hour ban        │
└─────────────────────────────────────────────┘
```

**Real-world:** Login brute-force protection. After 5 failed login attempts, the account is locked for progressively longer periods.

---

## HTTP Response for Rate Limiting

```
Allowed request:
  HTTP/1.1 200 OK
  RateLimit-Limit: 100              ← max requests in window
  RateLimit-Remaining: 73           ← requests left
  RateLimit-Reset: 1710523260       ← Unix timestamp when window resets

Rate limited request:
  HTTP/1.1 429 Too Many Requests
  Retry-After: 30                   ← wait 30 seconds
  RateLimit-Limit: 100
  RateLimit-Remaining: 0
  RateLimit-Reset: 1710523260

  {
    "error": {
      "code": "RATE_LIMIT_EXCEEDED",
      "message": "You have exceeded 100 requests per minute.",
      "retry_after_seconds": 30
    }
  }
```

---

## Common Mistakes and How to Avoid Them

| Mistake | Problem | Fix |
|---------|---------|-----|
| Rate limit only at API gateway | Internal service-to-service calls bypass it | Rate limit at every layer |
| Retry immediately on 429 | Hammers the server in a tight loop | Use exponential backoff + jitter |
| Fixed window for login limits | Boundary exploit allows 2x burst | Use sliding window for security endpoints |
| Not rate limiting internal services | Deployment bug causes infinite retry loop | Rate limit ALL callers, internal and external |
| Same limit for all endpoints | Expensive queries starve cheap ones | Per-endpoint or cost-based limits |

---

## Rate Limits of Popular APIs

| Service      | Endpoint                | Limit                  | Algorithm        |
| ------------ | ----------------------- | ---------------------- | ---------------- |
| **GitHub**   | REST API (auth)         | 5,000/hour             | Fixed window     |
| **Twitter**  | Tweet lookup            | 300/15 min             | Sliding window   |
| **Stripe**   | API calls               | 100/sec (live)         | Token bucket     |
| **OpenAI**   | GPT-4                   | 500 req/min + 40K tok  | Token bucket     |
| **AWS**      | API Gateway             | 10,000/sec + 5K burst  | Token bucket     |
| **Shopify**  | REST Admin              | 40/sec (bucket of 80)  | Leaky bucket     |
| **Slack**    | Web API                 | 1/sec per method       | Token bucket     |
| **Discord**  | Bot API                 | 50/sec global          | Token bucket     |
| **Cloudflare** | API                   | 1,200/5 min            | Fixed window     |

---

## When to Use

- **API gateways** — protect backend services from abuse
- **Login endpoints** — prevent brute-force attacks (sliding window, no boundary exploit)
- **Expensive operations** — limit costly DB queries or computations (cost-based)
- **Third-party API calls** — stay within provider rate limits (client-side token bucket)
- **Microservices** — prevent cascading failures (circuit breaker + rate limit)
- **WebSocket connections** — limit message frequency (per-connection token bucket)
- **File uploads** — control bandwidth usage (bandwidth limiter)
- **Payment processing** — fraud detection velocity checks (sliding window log)

## Complexity

| Algorithm              | Time per Request | Space           | Burst Friendly | Accuracy |
| ---------------------- | ---------------- | --------------- | -------------- | -------- |
| Fixed Window           | O(1)             | O(1) per client | No (boundary)  | Low      |
| Sliding Window Log     | O(1) amortized   | O(n) per client | Yes            | Perfect  |
| Sliding Window Counter | O(1)             | O(1) per client | Yes            | ~98%     |
| Token Bucket           | O(1)             | O(1) per client | Yes (best)     | Good     |
| Leaky Bucket           | O(1)             | O(1) per client | No (strict)    | Good     |

## Examples (30)

| #   | Problem                               | Difficulty | Key Idea                                              |
| --- | ------------------------------------- | ---------- | ----------------------------------------------------- |
| 1   | Fixed Window Counter                  | Easy       | Count per time window, reset at boundary              |
| 2   | Token Bucket                          | Easy       | Refill tokens at constant rate, consume on request    |
| 3   | Leaky Bucket                          | Easy       | Fixed output rate, drop overflow                      |
| 4   | Simple Counter                        | Easy       | Basic request counter with manual reset               |
| 5   | Per-User Rate Limiter                 | Easy       | Separate fixed-window counter per user ID             |
| 6   | Request Throttler (Min Interval)      | Easy       | Enforce minimum time gap between requests             |
| 7   | Rate Limiter with Retry-After         | Easy       | Return wait time in ms when rate limited              |
| 8   | Concurrent Request Limiter            | Easy       | Semaphore-style: limit in-flight requests             |
| 9   | Bandwidth Rate Limiter                | Easy       | Limit bytes transferred per window                    |
| 10  | IP-Based Rate Limiter                 | Easy       | Per-IP fixed-window counters                          |
| 11  | Sliding Window Log                    | Medium     | Store timestamps, count within sliding window         |
| 12  | Sliding Window Counter                | Medium     | Weighted blend of current + previous window           |
| 13  | Token Bucket with Burst Control       | Medium     | Separate burst capacity from sustained rate           |
| 14  | Per-Endpoint Rate Limiter             | Medium     | Different limits for different API routes             |
| 15  | Tiered Rate Limiter                   | Medium     | Rate limits by subscription tier (Free/Basic/Premium) |
| 16  | Leaky Bucket Queue                    | Medium     | Queue-based: enqueue requests, process at fixed rate  |
| 17  | Quota Manager                         | Medium     | Daily + hourly quota tracking with auto-reset         |
| 18  | Exponential Backoff Calculator        | Medium     | Compute retry delay with jitter                       |
| 19  | Rate Limiter Middleware               | Medium     | HTTP middleware returning 200/429 status codes        |
| 20  | Global + Per-User Composite           | Medium     | Two-layer: global token bucket AND per-user bucket    |
| 21  | Adaptive Rate Limiter                 | Hard       | Dynamically adjust limits based on error rate         |
| 22  | Distributed Rate Limiter (Redis-Like) | Hard       | Shared counter store across service instances         |
| 23  | Sliding Window with Sub-Buckets       | Hard       | Fine-grained buckets for higher accuracy              |
| 24  | Priority Rate Limiter                 | Hard       | Reserved capacity per priority level                  |
| 25  | Circuit Breaker with Rate Limiting    | Hard       | CLOSED → OPEN → HALF_OPEN state machine               |
| 26  | Token Bucket with Priority Queuing    | Hard       | High-priority requests get tokens first               |
| 27  | Geo-Based Rate Limiter                | Hard       | Per-region, per-user rate limits                      |
| 28  | Cost-Based Rate Limiter               | Hard       | Operations have different costs (read=1, write=3)     |
| 29  | Penalty Box Rate Limiter              | Hard       | Repeat violators get escalating bans                  |
| 30  | Multi-Algorithm Rate Limiter          | Hard       | Combine burst + sustained + concurrency limits        |

## Key Insight

> There is no single "best" rate limiting algorithm. **Token Bucket** is the most popular for APIs (used by AWS, Stripe, GitHub) because it handles bursts gracefully. **Fixed Window** is simplest but has boundary spikes. **Sliding Window** variants provide accuracy. In production, combine multiple strategies: token bucket for burst control, sliding window for sustained rate, and concurrency limits for resource protection.


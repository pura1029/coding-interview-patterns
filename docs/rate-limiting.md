# Rate Limiting — Deep Concepts with Real-Time Examples

A comprehensive guide to rate limiting — from fundamentals to production architectures, with real-world examples from GitHub, Stripe, AWS, Cloudflare, Twitter/X, and more.

---

## 1. What is Rate Limiting?

Rate limiting is a technique to **control the rate of requests** a client can make to a service within a given time period. It acts as a gatekeeper that says: "You can make X requests per Y seconds. After that, wait."

**Think of it like a highway toll booth.** Cars arrive at random speeds, but the toll booth can only process N cars per minute. If too many arrive, they queue up or are turned away.

```
Without Rate Limiting:                  With Rate Limiting:

Client ──────────────────► Service      Client ──► [Rate Limiter] ──► Service
Client ──────────────────► Service               ├── 200 OK
Client ──────────────────► Service               ├── 200 OK
Client ──────────────────► Service               ├── 200 OK
Client ──────────────────► (CRASH!)              └── 429 Too Many Requests
```

### Why Rate Limiting Matters

| Reason | Without Rate Limiting | With Rate Limiting |
|--------|----------------------|-------------------|
| **DDoS Protection** | Attacker sends 1M req/sec → service dies | Attacker limited to 100 req/sec → service survives |
| **Fair Usage** | One heavy user starves everyone else | Each user gets equal share |
| **Cost Control** | Runaway script racks up $50K cloud bill overnight | Budget protected, alerts fire early |
| **Stability** | Cascading failures across microservices | Backpressure prevents domino collapse |
| **Compliance** | Violate third-party API limits → get banned | Stay within limits → maintain access |

### Real-Time Example: GitHub API

```
GitHub's API rate limits:

  Unauthenticated:  60 requests per hour
  Authenticated:    5,000 requests per hour
  GitHub Actions:   1,000 requests per hour

Response headers on every call:
  X-RateLimit-Limit: 5000           ← your max
  X-RateLimit-Remaining: 4987       ← how many left
  X-RateLimit-Reset: 1710523200     ← Unix timestamp when window resets

When exceeded:
  HTTP 403 Forbidden
  {
    "message": "API rate limit exceeded for user",
    "documentation_url": "https://docs.github.com/rest/rate-limit"
  }

Real scenario:
  A CI/CD pipeline fetches repo status every 10 seconds.
  60 sec/min × 60 min = 3,600 checks/hour ← fits in 5,000 limit ✅
  
  But if 3 pipelines do the same: 3 × 3,600 = 10,800 ← RATE LIMITED ❌
  Fix: Use conditional requests (If-None-Match) → GitHub doesn't count 304s
```

---

## 2. The 5 Core Algorithms — Explained with Visual Examples

### Algorithm 1: Fixed Window Counter

**Concept:** Divide time into fixed windows (e.g., each minute). Count requests per window. Reset at the boundary.

```
Window:    |---- 12:00:00 - 12:00:59 ----|---- 12:01:00 - 12:01:59 ----|
Limit:     5 requests per window

Requests:  ✅ ✅ ✅ ✅ ✅ ❌ ❌         ✅ ✅ (counter reset!)
Count:      1  2  3  4  5  X  X          1  2

THE BOUNDARY PROBLEM:
           |-------- Window 1 --------|----- Window 2 ---------|
                              ✅✅✅✅✅  ✅✅✅✅✅
                              ^  12:00:55    ^  12:01:05
                              
  10 requests in 10 seconds! (5 at end of W1 + 5 at start of W2)
  The limit is 5/minute, but a 10-second burst got through 2x the limit.
```

**Real-Time Example: Login Attempt Limiting**
```
An e-commerce site limits login attempts to 5 per minute per IP.

Attacker at 192.168.1.100:
  12:00:55 → attempt 1 ✅
  12:00:56 → attempt 2 ✅
  12:00:57 → attempt 3 ✅
  12:00:58 → attempt 4 ✅
  12:00:59 → attempt 5 ✅
  ──── window resets ────
  12:01:00 → attempt 6 ✅  ← allowed! New window.
  12:01:01 → attempt 7 ✅
  
  Result: 7 attempts in 6 seconds — bypasses the intent of "5/minute"
  
  For login limiting, use Sliding Window Log instead.
```

**Implementation (Redis):**
```
INCR   rate:user:42:1710523200    → returns current count
EXPIRE rate:user:42:1710523200 60 → auto-cleanup after 60 seconds

If count > limit → reject (429)
```

---

### Algorithm 2: Sliding Window Log

**Concept:** Store the timestamp of every request. On each new request, remove timestamps outside the window, then count what remains.

```
Window size: 60 seconds    Limit: 5 requests

Timeline:
  12:00:10 → add ts. Log: [10]                    count=1 ✅
  12:00:20 → add ts. Log: [10, 20]                count=2 ✅
  12:00:30 → add ts. Log: [10, 20, 30]            count=3 ✅
  12:00:45 → add ts. Log: [10, 20, 30, 45]        count=4 ✅
  12:00:55 → add ts. Log: [10, 20, 30, 45, 55]    count=5 ✅
  12:01:05 → remove 10 (outside window). Log: [20, 30, 45, 55]  count=4 ✅ (add 65)
  
  No boundary problem! The window always looks back exactly 60 seconds.
```

**Real-Time Example: Twitter/X API**
```
Twitter API v2 rate limits:
  - Tweet lookup:    300 requests / 15-minute window
  - User lookup:     300 requests / 15-minute window  
  - Post a tweet:    200 requests / 15-minute window

They use a sliding window so that a developer who sends
300 requests at 12:14:59 can't send 300 more at 12:15:01.

The window always looks back exactly 15 minutes from NOW:
  At 12:20:00, the window is 12:05:00 → 12:20:00
  At 12:20:30, the window is 12:05:30 → 12:20:30
```

**Trade-off:** Precise, but stores one timestamp per request. At 10,000 req/sec per user, that's 600K timestamps per user per minute in memory.

**Implementation (Redis Sorted Set):**
```
ZREMRANGEBYSCORE rate:user:42 0 <now - windowMs>   → evict old
ZADD rate:user:42 <now> <requestId>                → add new
ZCARD rate:user:42                                  → count
EXPIRE rate:user:42 <windowMs>                      → auto-cleanup

If count > limit → reject (429)
```

---

### Algorithm 3: Sliding Window Counter

**Concept:** Hybrid of fixed window and sliding window log. Uses counts from the current and previous window with a weighted overlap. Low memory, good accuracy.

```
Previous window count: 8       Current window count: 3
Window size: 60 seconds
Current time: 12:01:45 (45 seconds into current window)

Overlap of previous window: (60 - 45) / 60 = 25%
Estimated count = previous × overlap + current = 8 × 0.25 + 3 = 5

Limit: 6 → 5 < 6 → ALLOW ✅

This is approximate but very close to the true sliding window count.
Memory: just 2 integers per client (no timestamps stored).
```

**Real-Time Example: Cloudflare's Rate Limiting**
```
Cloudflare protects 30+ million websites.
They can't store a timestamp per request — that would be petabytes.

Instead, they use sliding window counters:
  - Each edge server keeps prev_count and curr_count per rule per client
  - Memory per client per rule: ~16 bytes (vs ~100KB for sliding log)
  
  At their scale (50+ million requests/sec):
    Sliding log:     50M × 100KB = 5 TB of memory per minute 😱
    Sliding counter: 50M × 16B   = 800 MB of memory           ✅
```

---

### Algorithm 4: Token Bucket

**Concept:** Imagine a bucket that holds tokens. Tokens are added at a constant rate (e.g., 10/sec). Each request costs 1 token. If the bucket is empty, the request is rejected. The bucket has a maximum capacity (burst limit).

```
Bucket capacity: 10 tokens    Refill rate: 2 tokens/sec

Time 0s:  [🪙🪙🪙🪙🪙🪙🪙🪙🪙🪙]  = 10 tokens (full)

Burst of 8 requests:
Time 1s:  [🪙🪙          ]  = 2 tokens left (8 consumed)
          + 2 refilled
Time 2s:  [🪙🪙🪙🪙      ]  = 4 tokens (2 left + 2 refilled)

Time 3s:  No requests. [🪙🪙🪙🪙🪙🪙]  = 6 tokens (4 + 2)

After recovery, another burst of 6 is possible!

KEY INSIGHT:
  - Sustained rate = refill rate (2/sec = 120/min)
  - Burst capacity = bucket size (can handle 10 requests at once)
  - The bucket smooths traffic while allowing short bursts
```

**Real-Time Example: AWS API Gateway**
```
AWS API Gateway uses Token Bucket:

  Default: 10,000 requests/sec (sustained) + 5,000 request burst

  Scenario: An e-commerce site during flash sale
  
  Normal traffic: 2,000 req/sec → well within limit ✅
  
  Flash sale starts (12:00:00):
    12:00:00 → 15,000 req/sec → 5,000 burst tokens consumed, 
                                 10,000 sustained → ALL served ✅
    12:00:01 → 15,000 req/sec → burst depleted, only 10,000 sustained
                                 → 5,000 get 429 ❌
    12:00:02 → 12,000 req/sec → some burst tokens recovered
                                 → most served ✅
    12:00:10 → 8,000 req/sec → burst fully recovered → all served ✅

  The token bucket absorbed the initial spike without rejecting anyone!
  A fixed window would have rejected all 15,000 at the boundary.
```

**Why Token Bucket is the Most Popular:**
```
Used by: AWS, Stripe, Shopify, GitHub, Google Cloud, Heroku

Advantages over others:
  1. Allows bursts (unlike Leaky Bucket)
  2. No boundary problem (unlike Fixed Window)
  3. O(1) time and space (unlike Sliding Log)
  4. Smooth sustained rate between bursts
  5. Easy to implement (just track tokens + last_refill_time)
```

**Implementation (Pseudocode):**
```
function tryAcquire():
    now = currentTime()
    elapsed = now - lastRefillTime
    tokens = min(maxTokens, tokens + elapsed * refillRate)
    lastRefillTime = now
    
    if tokens >= 1:
        tokens -= 1
        return ALLOW
    else:
        return REJECT (429)
```

---

### Algorithm 5: Leaky Bucket

**Concept:** Requests enter a bucket. The bucket "leaks" (processes) at a constant rate. If the bucket is full, new requests are dropped. Output rate is always constant regardless of input.

```
Input (bursty):     ████  ██  ████████    ██
                    ↓↓↓↓  ↓↓  ↓↓↓↓↓↓↓↓    ↓↓
                  ┌──────────────────────────┐
                  │    Leaky Bucket          │  ← overflow = dropped
                  │    capacity = 5          │
                  │    🪣🪣🪣🪣🪣           │
                  └──────────┬───────────────┘
                             │ (leak rate = 1 req/sec)
                             ▼
Output (smooth):   █  █  █  █  █  █  █  █  █  █

KEY DIFFERENCE vs Token Bucket:
  Token Bucket:  allows bursts up to bucket capacity
  Leaky Bucket:  output is ALWAYS constant (strict smoothing)
```

**Real-Time Example: Network Traffic Shaping**
```
ISP bandwidth shaping:

  Your plan: 100 Mbps download
  
  Without leaky bucket:
    User downloads 4K video + game update + backup = 300 Mbps spike
    → Network congestion for other users
  
  With leaky bucket:
    Input: 300 Mbps → Bucket fills up
    Output: 100 Mbps (constant)
    Excess: queued (or dropped if bucket full)
    
    Result: steady 100 Mbps, no congestion spikes
    
  Real ISPs use variants called "token bucket filter" (tbf) in Linux:
    tc qdisc add dev eth0 root tbf rate 100mbit burst 32kbit latency 400ms
```

---

### Algorithm Comparison — When to Use What

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

Decision tree:
  Need constant output rate? → Leaky Bucket
  Need burst tolerance?      → Token Bucket ← DEFAULT CHOICE
  Need 100% accuracy?        → Sliding Window Log
  Need minimal memory?       → Fixed Window or Sliding Counter
  Building an API gateway?   → Token Bucket + Sliding Counter (combo)
```

---

## 3. Types of Rate Limiting — Deep Dive with Real Examples

### Type 1: Per-User / Per-API-Key

```
Each authenticated user gets their own bucket.

Real-Time Example: Stripe API
  
  Stripe rate limits:
    Live mode:  100 requests/sec per API key
    Test mode:  25 requests/sec per API key

  Scenario: E-commerce SaaS with 1,000 merchants, each with their own Stripe key
    Merchant A: 50 req/sec  → fine ✅
    Merchant B: 150 req/sec → rate limited after 100 ❌
    Merchant C: 10 req/sec  → fine ✅
    
  Merchant B's limit doesn't affect A or C.
  
  Response when limited:
    HTTP 429 Too Many Requests
    {
      "error": {
        "type": "rate_limit_error",
        "message": "Too many requests. Please retry after 1 second."
      }
    }
    Retry-After: 1
```

### Type 2: Per-IP

```
Rate limit by source IP address (no authentication needed).

Real-Time Example: Login Brute-Force Protection

  Rule: Max 10 login attempts per IP per 15 minutes
  
  Attacker at IP 203.0.113.42:
    Attempt  1-10: ✅ (trying different passwords)
    Attempt 11:    ❌ 429 "Too many login attempts. Try again in 14 minutes."
    
  Legitimate user at IP 198.51.100.1:
    Attempt 1: ✅ (forgot password, wrong attempt)
    Attempt 2: ✅ (correct password → logged in)
    
  The attacker's limit doesn't affect the legitimate user.

  Caveat: NAT / shared IPs
    Office with 500 employees behind 1 IP:
    → 10 attempts shared across all 500 people
    → Solution: combine IP + account-level limiting
```

### Type 3: Per-Endpoint

```
Different endpoints have different limits based on cost/sensitivity.

Real-Time Example: OpenAI API

  Endpoint                    Limit
  ─────────────────────────── ────────────────────
  /v1/chat/completions (GPT-4)  500 req/min, 40K tokens/min
  /v1/chat/completions (GPT-3.5) 3,500 req/min, 90K tokens/min
  /v1/embeddings               3,000 req/min
  /v1/images/generations        50 images/min
  /v1/audio/transcriptions      50 req/min

  Why different limits?
    - GPT-4 is expensive (more GPU time) → lower limit
    - Embeddings are cheap → higher limit
    - Image generation is GPU-heavy → very low limit
    
  Real scenario:
    A chatbot app sends 100 GPT-4 requests/min AND 2,000 embedding requests/min
    → GPT-4:     100 < 500   → fine ✅
    → Embeddings: 2,000 < 3,000 → fine ✅
    → Each endpoint tracked independently
```

### Type 4: Tiered / Subscription-Based

```
Different limits for different pricing tiers.

Real-Time Example: Twilio (SMS API)

  Tier        Price     SMS/sec    Concurrent Calls
  ─────────── ──────── ────────── ──────────────────
  Free Trial  $0       1/sec      1
  Pay-as-go   $0.0079  10/sec     Unlimited
  Enterprise  Custom   400/sec    Unlimited

  Real scenario: Marketing platform sends Black Friday SMS blast
  
    Free tier: 1 SMS/sec × 3600 sec/hr = 3,600 SMS/hour
    → 10,000 customers? Takes 2.8 hours to send all!
    
    Enterprise: 400 SMS/sec × 25 sec = 10,000 SMS
    → All customers notified in 25 seconds!
    
  Rate limit drives pricing: "Want to send faster? Upgrade."
```

### Type 5: Cost-Based / Weighted

```
Not all requests are equal. A search query costs less than a write.

Real-Time Example: Google Cloud API Quotas

  Operation             Cost (units)
  ───────────────────── ────────────
  Read a document       1
  Write a document      5
  Delete a document     2
  Complex query         10
  Batch write (500 ops) 250
  
  Daily quota: 50,000 units
  
  Scenario: Data migration tool
    Read  1,000 docs: 1,000 × 1  = 1,000 units
    Write 1,000 docs: 1,000 × 5  = 5,000 units
    Total used:                     6,000 units
    Remaining:                      44,000 units
    
  This prevents a single expensive batch job from consuming all quota
  that lighter-weight reads depend on.
```

### Type 6: Concurrency-Based

```
Limits simultaneous in-flight requests, not requests per time window.

Real-Time Example: Database Connection Pooling

  Database max connections: 100
  Service instances: 10
  → Each instance gets a concurrency limit of 10 connections
  
  Instance A:
    Request 1 → acquires connection (in-flight: 1/10) ✅
    ...
    Request 10 → acquires connection (in-flight: 10/10) ✅
    Request 11 → WAIT (all connections busy) ⏳
    Request 1 completes → connection returned (in-flight: 9/10)
    Request 11 → now proceeds ✅
    
  Unlike rate limiting, concurrency limiting doesn't care about TIME.
  It cares about how many are running RIGHT NOW.
  
  Real use: AWS Lambda (1,000 concurrent executions per account)
            Salesforce Bulk API (10 concurrent batch jobs)
```

---

## 4. Where to Place Rate Limiting — Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                   CLIENT                                       │
│  (browser, mobile app, API client)                             │
└───────────────────────┬────────────────────────────────────────┘
                        │
                        ▼
┌──────────────────────────────────────────────────────┐
│ Layer 1: CDN / Edge (Cloudflare, AWS CloudFront)     │  ← DDoS protection
│   - IP-based rate limiting                           │     10M+ req/sec capacity
│   - Geographic blocking                              │     
│   - Bot detection (CAPTCHA challenge)                │
└───────────────────────┬──────────────────────────────┘
                        │
                        ▼
┌──────────────────────────────────────────────────────┐
│ Layer 2: API Gateway (Kong, AWS API Gateway, Nginx)  │  ← Per-user / per-API-key
│   - Token bucket per API key                         │     limits
│   - Per-endpoint limits                              │
│   - Authentication + rate check                      │
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

Real-world architecture: Most production systems use ALL 4 layers.
Each layer catches different types of abuse.
```

### Real-Time Example: Multi-Layer Rate Limiting at Shopify

```
Shopify processes $444 billion in commerce annually.
Every storefront API call passes through multiple layers:

Layer 1 (Edge): Cloudflare
  → Blocks known bad IPs and DDoS
  → 10,000 req/sec per IP before challenge
  
Layer 2 (API Gateway): Custom Lua/OpenResty
  → Token bucket per API key: 40 requests/sec
  → Response: "Retry-After: 2.0"
  → Cost-based: GraphQL query complexity scoring
     Simple query:  cost = 1
     Nested query:  cost = 50
     Budget: 1,000 cost units per second
  
Layer 3 (Application): Ruby on Rails
  → Per-store limits (prevent one store from affecting others)
  → Background job queue limits
  → Webhook delivery rate limits (max 19/sec per app)
  
Layer 4 (Database): MySQL + Redis
  → Connection pool: 100 connections per shard
  → Read replicas for heavy reports
  → Query kill after 30 seconds
```

---

## 5. Distributed Rate Limiting

When your service runs on multiple servers, rate limiting gets harder. Each server needs to share state.

### The Problem

```
Single server:                       Multiple servers:

┌──────────┐                         ┌──────────┐
│ Server   │                         │ Server 1 │ counter = 3
│ counter=5│                         └──────────┘
│ limit=5  │                         ┌──────────┐
│ → REJECT │                         │ Server 2 │ counter = 3
└──────────┘                         └──────────┘

User sent 5 requests → correctly blocked.     User sent 6 requests (3 to each server)
                                               Each server thinks count=3 < limit=5
                                               → ALL allowed! Limit violated!
```

### Solution 1: Centralized Counter (Redis)

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

Redis Lua script (atomic — no race conditions):
  local count = redis.call("INCR", KEYS[1])
  if count == 1 then
      redis.call("EXPIRE", KEYS[1], ARGV[1])
  end
  return count

Pros: Simple, consistent, battle-tested
Cons: Redis becomes single point of failure, network latency per request
```

### Real-Time Example: Stripe's Distributed Rate Limiter

```
Stripe processes billions of API requests across multiple data centers.

Architecture:
  ┌──────────────────────────────────────────────────┐
  │ Data Center: US-East                              │
  │                                                   │
  │  API Server 1 ─┐                                 │
  │  API Server 2 ─┤──► Redis Cluster (3 masters)    │
  │  API Server 3 ─┤    │ rate:sk_live_xxx = 87/100  │
  │  API Server N ─┘    │ TTL: 1 second              │
  │                     └─────────────────────────────│
  └──────────────────────────────────────────────────┘

  For each API request:
    1. Extract API key from Authorization header
    2. Redis: INCR rate:{api_key}:{window}
    3. If count > limit → 429 with Retry-After header
    4. If count ≤ limit → forward to application
    
  Latency added: ~0.5ms (Redis roundtrip)
  At 100K API req/sec: Redis handles ~100K INCR/sec easily
```

### Solution 2: Local + Sync (Approximate)

```
Each server keeps a local counter, syncs periodically:

┌──────────┐      ┌──────────┐      ┌──────────┐
│ Server 1 │      │ Server 2 │      │ Server 3 │
│ local: 3 │      │ local: 2 │      │ local: 4 │
│ limit: 5 │      │ limit: 5 │      │ limit: 5 │
└─────┬────┘      └─────┬────┘      └─────┬────┘
      │                 │                  │
      └────── sync every 1 sec ────────────┘
              "I counted 3" + "I counted 2" + "I counted 4" = 9
              Global estimate: 9 > limit of 15 (5 × 3 servers)

Pros: No per-request Redis call, very fast
Cons: Approximate (can temporarily exceed limit by sync interval)

Used by: High-throughput systems where exact accuracy isn't critical
  (e.g., video streaming bandwidth management)
```

### Solution 3: Token Bucket with Redis (Sliding)

```
Redis Lua script for token bucket:

  local key = KEYS[1]
  local maxTokens = tonumber(ARGV[1])
  local refillRate = tonumber(ARGV[2])
  local now = tonumber(ARGV[3])
  
  local data = redis.call("HMGET", key, "tokens", "lastRefill")
  local tokens = tonumber(data[1]) or maxTokens
  local lastRefill = tonumber(data[2]) or now
  
  -- Refill tokens
  local elapsed = now - lastRefill
  tokens = math.min(maxTokens, tokens + elapsed * refillRate)
  
  -- Try to consume
  if tokens >= 1 then
      tokens = tokens - 1
      redis.call("HMSET", key, "tokens", tokens, "lastRefill", now)
      redis.call("EXPIRE", key, 60)
      return 1  -- ALLOWED
  else
      redis.call("HMSET", key, "tokens", tokens, "lastRefill", now)
      redis.call("EXPIRE", key, 60)
      return 0  -- REJECTED
  end
```

---

## 6. Rate Limiting in Practice — Real-World Case Studies

### Case Study 1: Cloudflare — DDoS Mitigation

```
Cloudflare handles 55+ million HTTP requests per second globally.

Challenge: Distinguish between legitimate traffic spike (viral blog post)
           and malicious DDoS attack (bot flooding).

Their multi-layer approach:
  
  Layer 1: IP Reputation (always-on)
    Known bad IPs from threat intelligence → block immediately
    Tor exit nodes → CAPTCHA challenge
  
  Layer 2: Rate Rules (configurable per website)
    Rule: "If IP makes > 100 requests to /api/* in 10 seconds"
    Action: Block for 1 hour
    Algorithm: Sliding window counter (memory efficient)
    
  Layer 3: Bot Score (ML-based)
    Analyze: TLS fingerprint, HTTP headers, mouse movements, timing
    Score: 1 (definitely bot) to 99 (definitely human)
    Rule: "If bot_score < 30 AND rate > 50/min → CAPTCHA"
    
  Layer 4: Adaptive during attack
    Normal:  Allow 1,000 req/sec per IP
    Attack:  Dynamically lower to 10 req/sec per IP
    Auto-detect: 10x traffic spike in 30 seconds triggers adaptive mode
    
  Real attack example (2023):
    Attack: 201 million requests per second (largest ever HTTP DDoS)
    Duration: 3 minutes
    Result: Zero downtime for the customer
    How: Rate limiting kicked in within 3 seconds of attack detection
```

### Case Study 2: Netflix — Microservice Protection

```
Netflix has 1,000+ microservices. Without rate limiting, one slow service
could cascade into a global outage.

Architecture: Zuul API Gateway + Hystrix Circuit Breaker

┌─────────┐     ┌──────────────────┐     ┌──────────────────┐
│ Mobile  │     │  Zuul Gateway    │     │ Recommendation   │
│ App     │────►│                  │────►│ Service          │
│         │     │ Rate: 2000/sec   │     │ (sometimes slow) │
│         │     │ per device type  │     │                  │
└─────────┘     └──────────────────┘     └──────────────────┘
                         │
                         │ If Recommendation Service latency > 500ms:
                         │
                         ▼
                ┌──────────────────┐
                │ Circuit Breaker  │
                │                  │
                │ CLOSED → OPEN    │  "Stop calling the slow service"
                │                  │  "Return cached recommendations"
                │ After 30s:      │  "Try HALF_OPEN (1 test request)"
                │ HALF_OPEN       │  "If OK → CLOSED, if fail → OPEN"
                └──────────────────┘

Real incident (2015): 
  A database migration slowed the "User Preferences" service.
  Without rate limiting: All 1,000 services would retry → total outage
  With rate limiting: Circuit breaker opened after 10 failures
  → Returned cached data for 2 minutes while DB recovered
  → Users saw slightly stale recommendations but Netflix stayed up
```

### Case Study 3: Slack — WebSocket Message Rate Limiting

```
Slack limits message sending in channels to prevent spam.

Limits:
  - 1 message per second per channel per user
  - 20 API calls per minute per workspace token
  - Max message size: 40,000 characters

Real scenario: CI/CD bot flooding a channel
  Build system sends a message for every test result:
    13:00:00 → "Test 1 passed ✅"        ← sent
    13:00:00 → "Test 2 passed ✅"        ← RATE LIMITED
    13:00:00 → "Test 3 passed ✅"        ← RATE LIMITED
    13:00:01 → "Test 2 passed ✅"        ← sent (next second)
    
  Response: HTTP 429
  {
    "ok": false,
    "error": "rate_limited",
    "retry_after": 1
  }
  
  Fix: Batch test results into a single message every 5 seconds
    "Tests 1-50: 48 passed ✅, 2 failed ❌"
```

### Case Study 4: Uber — Surge Pricing Rate Control

```
During high-demand periods, Uber needs to control the rate of ride requests
to match available driver supply.

┌──────────────────────────────────────────────────────┐
│ City: San Francisco, New Year's Eve 11:55 PM          │
│                                                       │
│ Demand:  5,000 ride requests/minute                   │
│ Supply:  500 available drivers                        │
│ Ratio:   10:1 (extreme demand)                        │
│                                                       │
│ Rate limiting response:                               │
│   1. Surge multiplier: 3.5x                          │
│   2. Accept only 1,500 requests/minute (top 30%)      │
│   3. Remaining requests: show "No cars available"     │
│   4. Or: queue with estimated wait time               │
│                                                       │
│ Algorithm: Token bucket per geo-zone                  │
│   Zone "SFO-Downtown":                                │
│     bucket_capacity = 200 requests                    │
│     refill_rate = 50 requests/sec (matches driver      │
│                    availability in that zone)          │
│                                                       │
│ As drivers complete rides → tokens refill automatically│
│ As demand drops → surge price decreases               │
└──────────────────────────────────────────────────────┘
```

### Case Study 5: Banking — Transaction Velocity Checks

```
Banks use rate limiting to detect fraud in real-time.

Rules (layered):
  ┌────────────────────────────────────────────────────┐
  │ Rule 1: Max 3 ATM withdrawals per hour per card    │
  │ Rule 2: Max $2,000 total ATM per day               │
  │ Rule 3: Max 10 POS transactions per hour           │
  │ Rule 4: Max 2 international transactions per day   │
  │ Rule 5: No transactions in 2+ countries within 1hr │
  └────────────────────────────────────────────────────┘

Real fraud scenario:
  12:00 → ATM London, $500          ← Rule 1: 1/3 ✅
  12:05 → ATM London, $500          ← Rule 1: 2/3 ✅
  12:10 → POS Paris, €200           ← Rule 5: London + Paris in 10 min! ❌
  
  BLOCKED. Card frozen. SMS sent to cardholder:
  "Did you attempt a purchase in Paris at 12:10? Reply YES or NO."
  
  Algorithm: Sliding window log per card per rule
  Why not fixed window: Can't have the boundary problem with fraud!
  Storage: Redis sorted sets per card ID
```

---

## 7. Client-Side Rate Limiting — Being a Good Citizen

### Exponential Backoff with Jitter

```
When you receive 429 Too Many Requests, don't retry immediately!

BAD:  Retry instantly → server stays overloaded → more 429s → infinite loop

GOOD: Exponential backoff with jitter

  Attempt 1: wait 1s + random(0-500ms)   = ~1.3s
  Attempt 2: wait 2s + random(0-500ms)   = ~2.2s
  Attempt 3: wait 4s + random(0-500ms)   = ~4.4s
  Attempt 4: wait 8s + random(0-500ms)   = ~8.1s
  Attempt 5: wait 16s + random(0-500ms)  = ~16.3s (max cap)
  
  Formula: delay = min(cap, base × 2^attempt) + random(0, jitter)

Why jitter?
  Without jitter: 1,000 clients all retry at exactly 2s, 4s, 8s
    → "thundering herd" — server gets slammed at each retry interval
    
  With jitter: 1,000 clients retry at random times within each window
    → load spreads evenly
```

### Real-Time Example: AWS SDK Retry Behavior

```
AWS SDKs have built-in exponential backoff:

  // Java AWS SDK
  ClientConfiguration config = new ClientConfiguration()
      .withMaxErrorRetry(3)                // max 3 retries
      .withRetryPolicy(PredefinedRetryPolicies.DEFAULT);
  
  Retry timeline for a throttled DynamoDB call:
    Attempt 1: 400ms → 429 (ProvisionedThroughputExceededException)
    Wait: 50ms + jitter
    Attempt 2: 450ms → 429
    Wait: 100ms + jitter
    Attempt 3: 600ms → 200 OK ✅
    
  Total time: ~1.1 seconds (vs instant failure without retry)
```

### Rate Limiting Response Headers (Standard)

```
HTTP/1.1 200 OK (or 429 Too Many Requests)

Standard headers:
  RateLimit-Limit:     100          ← max requests in window
  RateLimit-Remaining: 73           ← requests remaining
  RateLimit-Reset:     1710523260   ← Unix timestamp when window resets

Draft standard headers (IETF):
  RateLimit-Policy: 100;w=60        ← 100 per 60-second window

Retry-specific:
  Retry-After: 30                   ← wait 30 seconds before retrying
  Retry-After: Wed, 13 Mar 2026 12:00:00 GMT  ← or absolute time

Real response from GitHub:
  HTTP/1.1 429 Too Many Requests
  X-RateLimit-Limit: 5000
  X-RateLimit-Remaining: 0
  X-RateLimit-Reset: 1710523200
  Retry-After: 3487
```

---

## 8. Rate Limiting Patterns for System Design Interviews

### Pattern 1: API Gateway Rate Limiter

```
Interview question: "Design a rate limiter for an API that handles
10,000 requests/sec across 100 servers."

Answer:

  ┌─────────┐     ┌──────────────────────┐     ┌──────────────┐
  │ Client  │────►│ Load Balancer        │────►│ API Server   │
  └─────────┘     │ (round-robin)        │     │ (1 of 100)   │
                  └──────────────────────┘     └──────┬───────┘
                                                      │
                                               ┌──────▼───────┐
                                               │ Redis Cluster│
                                               │              │
                                               │ Token bucket │
                                               │ per API key  │
                                               └──────────────┘

  Components:
    1. Rules database: stores limits per tier/endpoint
    2. Redis: stores current token counts (shared across servers)
    3. Rate limiter middleware: checks Redis before forwarding request
    
  Data model in Redis:
    Key:   rate:{api_key}:{minute_window}
    Value: { tokens: 87, last_refill: 1710523200 }
    TTL:   120 seconds (2× window for safety)
    
  Throughput: Redis handles 100K+ ops/sec on single node
  Latency:   0.1-0.5ms per rate check
  Failover:  If Redis is down → allow all (fail-open) or block all (fail-closed)
```

### Pattern 2: Distributed Rate Limiter at Scale

```
Interview question: "How would you rate limit across multiple data centers?"

  Data Center A (US-East)          Data Center B (EU-West)
  ┌───────────────────┐            ┌───────────────────┐
  │ API Servers       │            │ API Servers       │
  │      │            │            │      │            │
  │ ┌────▼─────────┐  │            │ ┌────▼─────────┐  │
  │ │ Local Redis  │  │            │ │ Local Redis  │  │
  │ │ count: 45    │  │──── sync ──│ │ count: 30    │  │
  │ └──────────────┘  │   1s/cycle │ └──────────────┘  │
  └───────────────────┘            └───────────────────┘
  
  Global limit: 100 req/sec
  Each DC gets proportional allocation: A=60, B=40 (based on traffic)
  
  Sync strategies:
    a) Periodic sync (every 1s): simple, slightly inaccurate
    b) Gossip protocol: each DC broadcasts its count
    c) Central coordinator: accurate but adds latency
    
  Usually: local rate limit (fast) + periodic global reconciliation
```

### Pattern 3: Rate Limiting with Graceful Degradation

```
Interview question: "What if your rate limiter (Redis) goes down?"

  Strategy 1: Fail-Open (allow all)
    Risk: No rate limiting during Redis outage
    Mitigation: Local in-memory fallback with conservative limits
    Use when: User experience matters more (e-commerce checkout)
    
  Strategy 2: Fail-Closed (block all)
    Risk: Complete service outage during Redis outage
    Use when: Security matters more (authentication endpoints)
    
  Strategy 3: Graceful degradation (recommended)
    ┌───────────────────────────────────────────────┐
    │ 1. Try Redis (primary)                        │
    │    └── Success → use result                   │
    │    └── Fail (timeout/error) →                 │
    │                                               │
    │ 2. Fall back to local token bucket (in-memory)│
    │    - Each server allows limit/N requests       │
    │    - N = number of servers                    │
    │    - Less accurate but functional             │
    │                                               │
    │ 3. Alert ops team about Redis issue           │
    └───────────────────────────────────────────────┘
```

---

## 9. Common Mistakes and How to Avoid Them

### Mistake 1: Rate Limiting Only at One Layer

```
❌ Only rate limit at the API gateway

Problem: Internal service-to-service calls bypass the gateway
  
  Client → API Gateway (rate limited) → Service A → Service B
                                                    ↑
  Internal cron job (no gateway) ──────────────────┘ (no rate limit!)
  
✅ Rate limit at every layer:
  - Gateway: per-user/per-IP
  - Service: per-caller service identity
  - Database: connection pool limits
```

### Mistake 2: Not Handling 429 on the Client Side

```
❌ Retry immediately on 429
  while (response.status == 429) {
      response = sendRequest();  // hammering the server!
  }

✅ Respect Retry-After header with backoff
  if (response.status == 429) {
      int wait = response.header("Retry-After");
      Thread.sleep(wait * 1000 + randomJitter());
      response = sendRequest();
  }
```

### Mistake 3: Using Fixed Window for Security-Critical Limits

```
❌ Fixed window for login attempts
  Window 1: [....XXXXX] Window 2: [XXXXX....]
  → 10 attempts in 2 seconds (5 at end of W1 + 5 at start of W2)

✅ Sliding window log for login attempts
  Always looks back exactly N minutes → no boundary exploit
```

### Mistake 4: Not Rate Limiting Internal Services

```
❌ Trusting internal services
  "It's internal, it won't abuse our API"
  → Deployment bug causes infinite retry loop → cascading failure

✅ Rate limit ALL callers, internal and external
  Service A → Service B (rate limited at 1000/sec)
  → Prevents runaway loops from taking down the whole system
```

### Mistake 5: Not Monitoring Rate Limiting Effectiveness

```
Key metrics to track:
  ┌────────────────────────────────────────────────┐
  │ Metric                  │ Alert Threshold      │
  ├─────────────────────────┼──────────────────────┤
  │ 429 responses / second  │ > 1% of total traffic│
  │ Rate limited unique IPs │ > 100 in 5 minutes   │
  │ Redis latency (p99)     │ > 5ms                │
  │ Rate limiter errors     │ > 0.1% of checks     │
  │ Top rate-limited users  │ Same user > 1000 429s│
  └─────────────────────────┴──────────────────────┘

  Dashboard should show:
    - Which users/IPs are being rate limited (abuse detection)
    - Whether limits are too strict (legitimate users hitting 429)
    - Rate limiter health (Redis connectivity, latency)
```

---

## 10. Rate Limiting HTTP Response Codes

```
┌──────┬───────────────────────────────────────────────────────┐
│ Code │ Meaning                                               │
├──────┼───────────────────────────────────────────────────────┤
│ 200  │ Request allowed (include remaining quota in headers)  │
│ 429  │ Too Many Requests (standard rate limit response)      │
│ 403  │ Forbidden (used by some APIs instead of 429)          │
│ 503  │ Service Unavailable (server overload, not per-user)   │
│ 202  │ Accepted (request queued due to rate limit, not dropped)│
└──────┴───────────────────────────────────────────────────────┘

Best practice: Always return 429 with:
  - Retry-After header
  - RateLimit-Remaining header
  - Clear error message with documentation link
  
Example response:
  HTTP/1.1 429 Too Many Requests
  Content-Type: application/json
  Retry-After: 30
  RateLimit-Limit: 100
  RateLimit-Remaining: 0
  RateLimit-Reset: 1710523260
  
  {
    "error": {
      "code": "RATE_LIMIT_EXCEEDED",
      "message": "You have exceeded 100 requests per minute. Please retry after 30 seconds.",
      "retry_after_seconds": 30,
      "docs": "https://docs.example.com/rate-limits"
    }
  }
```

---

## Quick Reference — Rate Limits of Popular APIs

| Service | Endpoint | Limit | Algorithm |
|---------|----------|-------|-----------|
| **GitHub** | REST API (authenticated) | 5,000/hour | Fixed window |
| **GitHub** | Search API | 30/min | Fixed window |
| **Twitter/X** | Tweet lookup | 300/15 min | Sliding window |
| **Twitter/X** | Post a tweet | 200/15 min | Sliding window |
| **Stripe** | API calls | 100/sec (live) | Token bucket |
| **OpenAI** | GPT-4 | 500 req/min + 40K tokens/min | Token bucket |
| **AWS API GW** | Default | 10,000/sec + 5,000 burst | Token bucket |
| **Google Maps** | Geocoding | 50/sec | Fixed window |
| **Shopify** | REST Admin | 40/sec (bucket of 80) | Leaky bucket |
| **Slack** | Web API | 1/sec per method | Token bucket |
| **Discord** | Bot API | 50/sec global | Token bucket |
| **Twilio** | SMS | 1-400/sec (tier) | Token bucket |
| **Cloudflare** | API | 1,200/5 min | Fixed window |
| **Reddit** | OAuth API | 60/min | Fixed window |

---

## Interview Questions — Rate Limiting

### Conceptual

| Question | Answer |
|----------|--------|
| What is rate limiting? | Controlling the number of requests a client can make within a time window to protect services |
| Name 5 rate limiting algorithms | Fixed window, sliding window log, sliding window counter, token bucket, leaky bucket |
| Token Bucket vs Leaky Bucket? | Token bucket allows bursts up to capacity; leaky bucket enforces strict constant output rate |
| Fixed Window boundary problem? | Requests can spike at 2x the limit by sending at the end of one window and start of the next |
| Where should rate limiting be placed? | Multiple layers: CDN/edge → API gateway → application → database |

### Design

| Question | Answer |
|----------|--------|
| How to rate limit across multiple servers? | Centralized counter in Redis (INCR + EXPIRE), or local counters with periodic sync |
| What if Redis goes down? | Fail-open (allow all), fail-closed (block all), or graceful degradation (local fallback) |
| How to handle different tiers? | Store rate limit config per tier in DB; look up user's tier on each request |
| How to rate limit WebSocket messages? | Token bucket per connection, check before broadcasting each message |
| How to rate limit by cost? | Assign cost to each operation type; token bucket where tokens = cost units instead of request count |

### Scenario-Based

| Scenario | Solution |
|----------|----------|
| "API getting DDoS'd" | IP-based rate limiting at CDN edge + CAPTCHA challenge for suspicious scores |
| "Heavy user starving others" | Per-user token bucket + global rate limit (composite limiter) |
| "Login brute force" | Sliding window log (no boundary exploit) + account lockout after N failures + CAPTCHA |
| "Microservice cascade failure" | Circuit breaker + per-caller rate limit + bulkhead (concurrency limit per dependency) |
| "Black Friday traffic spike" | Auto-scaling + adaptive rate limiting that loosens during expected events |
| "Third-party API limit" | Client-side token bucket matching their limit + exponential backoff + request queue |

---

## Resources

- [Stripe: Scaling API Rate Limiting](https://stripe.com/blog/rate-limiters)
- [Cloudflare: How Rate Limiting Works](https://blog.cloudflare.com/counting-things-a-lot-of-different-things/)
- [Google Cloud: API Rate Limiting Strategies](https://cloud.google.com/architecture/rate-limiting-strategies-techniques)
- [System Design: Rate Limiter (Alex Xu)](https://bytebytego.com/courses/system-design-interview/design-a-rate-limiter)
- [IETF Draft: RateLimit Header Fields](https://datatracker.ietf.org/doc/draft-ietf-httpapi-ratelimit-headers/)
- [Token Bucket Algorithm (Wikipedia)](https://en.wikipedia.org/wiki/Token_bucket)
- [Nginx Rate Limiting](https://www.nginx.com/blog/rate-limiting-nginx/)

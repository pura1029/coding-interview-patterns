# Rate Limiting

## What is it?
Rate limiting controls the number of requests a client can make to a service within a given time period. It protects services from abuse, ensures fair resource distribution, prevents cascading failures, and controls costs.

## Core Algorithms

| Algorithm | How It Works | Pros | Cons |
|-----------|-------------|------|------|
| **Fixed Window Counter** | Count requests per fixed time window (e.g., per second) | Simple, low memory | Boundary burst problem (2x spike at window edges) |
| **Sliding Window Log** | Store timestamp of every request; count within sliding window | Precise, no boundary issue | High memory for many requests |
| **Sliding Window Counter** | Weighted average of current + previous window counts | Good accuracy, low memory | Approximate (but close enough) |
| **Token Bucket** | Tokens refill at constant rate; each request costs one token | Allows controlled bursts | Slightly complex to tune |
| **Leaky Bucket** | Requests enter bucket; leak out at fixed rate | Smooth constant output | No burst tolerance |

## Types of Rate Limiting

### By Scope
| Type | Description | Example |
|------|-------------|---------|
| **Per-User** | Each user has their own rate limit | 100 req/min per API key |
| **Per-IP** | Limit by client IP address | 50 req/sec per IP |
| **Per-Endpoint** | Different limits for different API routes | /search: 10/min, /read: 100/min |
| **Global** | Single limit for the entire service | 10,000 req/sec total |
| **Tiered** | Different limits by subscription tier | Free: 5/min, Premium: 100/min |
| **Geo-Based** | Regional limits based on geography | US: 100/min, EU: 50/min |

### By Resource
| Type | Description | Example |
|------|-------------|---------|
| **Request Count** | Limit number of requests | 100 req/minute |
| **Bandwidth** | Limit data transferred | 10 MB/minute |
| **Cost-Based** | Assign costs to operations; limit total cost | Read=1pt, Write=3pt, limit=100pt/min |
| **Concurrency** | Limit simultaneous in-flight requests | Max 10 concurrent connections |

### Advanced Patterns
| Pattern | Description |
|---------|-------------|
| **Adaptive/Dynamic** | Adjust limits based on server health/latency |
| **Circuit Breaker** | CLOSED → OPEN → HALF_OPEN based on failure rate |
| **Retry-After** | Tell clients exactly how long to wait |
| **Exponential Backoff** | Clients increase retry delay exponentially |
| **Penalty Box** | Repeat offenders get progressively longer bans |
| **Priority Queuing** | High-priority requests served before low-priority |
| **Distributed** | Shared counter (e.g., Redis) across service instances |
| **Multi-Algorithm** | Combine burst (token bucket) + sustained (sliding window) + concurrency |

## When to Use
- **API gateways** — protect backend services from abuse
- **Login endpoints** — prevent brute-force attacks
- **Expensive operations** — limit costly DB queries or computations
- **Third-party API calls** — stay within provider rate limits
- **Microservices** — prevent cascading failures
- **WebSocket connections** — limit message frequency
- **File uploads** — control bandwidth usage

## Complexity

| Algorithm | Time per Request | Space |
|-----------|-----------------|-------|
| Fixed Window | O(1) | O(1) per client |
| Sliding Window Log | O(1) amortized | O(n) per client |
| Sliding Window Counter | O(1) | O(1) per client |
| Token Bucket | O(1) | O(1) per client |
| Leaky Bucket | O(1) | O(1) per client |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Fixed Window Counter | Easy | Count per time window, reset at boundary |
| 2 | Token Bucket | Easy | Refill tokens at constant rate, consume on request |
| 3 | Leaky Bucket | Easy | Fixed output rate, drop overflow |
| 4 | Simple Counter | Easy | Basic request counter with manual reset |
| 5 | Per-User Rate Limiter | Easy | Separate fixed-window counter per user ID |
| 6 | Request Throttler (Min Interval) | Easy | Enforce minimum time gap between requests |
| 7 | Rate Limiter with Retry-After | Easy | Return wait time in ms when rate limited |
| 8 | Concurrent Request Limiter | Easy | Semaphore-style: limit in-flight requests |
| 9 | Bandwidth Rate Limiter | Easy | Limit bytes transferred per window |
| 10 | IP-Based Rate Limiter | Easy | Per-IP fixed-window counters |
| 11 | Sliding Window Log | Medium | Store timestamps, count within sliding window |
| 12 | Sliding Window Counter | Medium | Weighted blend of current + previous window |
| 13 | Token Bucket with Burst Control | Medium | Separate burst capacity from sustained rate |
| 14 | Per-Endpoint Rate Limiter | Medium | Different limits for different API routes |
| 15 | Tiered Rate Limiter | Medium | Rate limits by subscription tier (Free/Basic/Premium) |
| 16 | Leaky Bucket Queue | Medium | Queue-based: enqueue requests, process at fixed rate |
| 17 | Quota Manager | Medium | Daily + hourly quota tracking with auto-reset |
| 18 | Exponential Backoff Calculator | Medium | Compute retry delay with jitter |
| 19 | Rate Limiter Middleware | Medium | HTTP middleware returning 200/429 status codes |
| 20 | Global + Per-User Composite | Medium | Two-layer: global token bucket AND per-user bucket |
| 21 | Adaptive Rate Limiter | Hard | Dynamically adjust limits based on error rate |
| 22 | Distributed Rate Limiter (Redis-Like) | Hard | Shared counter store across service instances |
| 23 | Sliding Window with Sub-Buckets | Hard | Fine-grained buckets for higher accuracy |
| 24 | Priority Rate Limiter | Hard | Reserved capacity per priority level |
| 25 | Circuit Breaker with Rate Limiting | Hard | CLOSED → OPEN → HALF_OPEN state machine |
| 26 | Token Bucket with Priority Queuing | Hard | High-priority requests get tokens first |
| 27 | Geo-Based Rate Limiter | Hard | Per-region, per-user rate limits |
| 28 | Cost-Based Rate Limiter | Hard | Operations have different costs (read=1, write=3) |
| 29 | Penalty Box Rate Limiter | Hard | Repeat violators get escalating bans |
| 30 | Multi-Algorithm Rate Limiter | Hard | Combine burst + sustained + concurrency limits |

## Key Insight
> There is no single "best" rate limiting algorithm. **Token Bucket** is the most popular for APIs (used by AWS, Stripe, GitHub) because it handles bursts gracefully. **Fixed Window** is simplest but has boundary spikes. **Sliding Window** variants provide accuracy. In production, combine multiple strategies: token bucket for burst control, sliding window for sustained rate, and concurrency limits for resource protection.

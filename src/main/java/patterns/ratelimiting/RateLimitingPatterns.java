package patterns.ratelimiting;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * RATE LIMITING — 30 Essential Examples
 *
 * Rate limiting controls the number of requests a client can make within a time
 * window. It protects services from abuse, ensures fair resource distribution,
 * and prevents cascading failures.
 *
 * Core Algorithms:
 *   - Token Bucket: smooth, bursty-friendly
 *   - Leaky Bucket: strict constant output rate
 *   - Fixed Window Counter: simple, boundary-spike prone
 *   - Sliding Window Log: precise, memory-heavy
 *   - Sliding Window Counter: hybrid balance
 *
 * Advanced:
 *   - Adaptive / dynamic rate limiting
 *   - Distributed rate limiting (Redis-like simulation)
 *   - Per-user / per-IP / per-endpoint / tiered limiting
 *   - Retry-After / backoff / circuit breaker integration
 *
 * 10 Easy | 10 Medium | 10 Hard
 */
public class RateLimitingPatterns {

    // ======================= EASY 1: Fixed Window Counter =======================
    /**
     * Fixed Window Counter
     *
     * <p><b>Approach:</b> Counts requests per fixed time window (e. g. , per second). Simple but allows burst at window edges.
     */
    static class FixedWindowCounter {
        private final int maxRequests;
        private final long windowMs;
        private long windowStart;
        private int count;

        FixedWindowCounter(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
            this.windowStart = System.currentTimeMillis();
        }

        public synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            if (now - windowStart >= windowMs) {
                windowStart = now;
                count = 0;
            }
            if (count < maxRequests) { count++; return true; }
            return false;
        }

        public int remaining() { return Math.max(0, maxRequests - count); }
    }

    // ======================= EASY 2: Token Bucket =======================
    /**
     * Token Bucket
     *
     * <p><b>Approach:</b> Tokens refill at a constant rate. Each request consumes one token. Allows bursts up to bucket capacity.
     */
    static class TokenBucket {
        private final int maxTokens;
        private final double refillRate; // tokens per second
        private double tokens;
        private long lastRefillNanos;

        TokenBucket(int maxTokens, double tokensPerSecond) {
            this.maxTokens = maxTokens;
            this.refillRate = tokensPerSecond;
            this.tokens = maxTokens;
            this.lastRefillNanos = System.nanoTime();
        }

        public synchronized boolean tryAcquire() {
            refill();
            if (tokens >= 1.0) { tokens -= 1.0; return true; }
            return false;
        }

        public synchronized boolean tryAcquire(int n) {
            refill();
            if (tokens >= n) { tokens -= n; return true; }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            double elapsed = (now - lastRefillNanos) / 1_000_000_000.0;
            tokens = Math.min(maxTokens, tokens + elapsed * refillRate);
            lastRefillNanos = now;
        }

        public double currentTokens() { refill(); return tokens; }
    }

    // ======================= EASY 3: Leaky Bucket =======================
    /**
     * Leaky Bucket
     *
     * <p><b>Approach:</b> Requests enter a bucket and leak out at a fixed rate. Excess requests are dropped. Ensures constant output rate.
     */
    static class LeakyBucket {
        private final int capacity;
        private final double leakRatePerSec;
        private double water;
        private long lastLeakNanos;

        LeakyBucket(int capacity, double leakRatePerSec) {
            this.capacity = capacity;
            this.leakRatePerSec = leakRatePerSec;
            this.lastLeakNanos = System.nanoTime();
        }

        public synchronized boolean tryAcquire() {
            leak();
            if (water < capacity) { water += 1; return true; }
            return false;
        }

        private void leak() {
            long now = System.nanoTime();
            double elapsed = (now - lastLeakNanos) / 1_000_000_000.0;
            water = Math.max(0, water - elapsed * leakRatePerSec);
            lastLeakNanos = now;
        }
    }

    // ======================= EASY 4: Simple Counter Rate Limiter =======================
    /**
     * Simple Counter Rate Limiter
     *
     * <p><b>Approach:</b> The simplest form: count requests and reject after N. Reset manually.
     */
    static class SimpleCounter {
        private final int limit;
        private int count;

        SimpleCounter(int limit) { this.limit = limit; }
        public boolean tryAcquire() { if (count < limit) { count++; return true; } return false; }
        public void reset() { count = 0; }
        public int getCount() { return count; }
    }

    // ======================= EASY 5: Per-User Rate Limiter =======================
    /**
     * Per-User Rate Limiter
     *
     * <p><b>Approach:</b> Maintains a separate fixed-window counter per user ID
     */
    static class PerUserRateLimiter {
        private final int maxRequests;
        private final long windowMs;
        private final Map<String, long[]> users = new ConcurrentHashMap<>(); // [windowStart, count]

        PerUserRateLimiter(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
        }

        public boolean tryAcquire(String userId) {
            long now = System.currentTimeMillis();
            long[] state = users.computeIfAbsent(userId, k -> new long[]{now, 0});
            synchronized (state) {
                if (now - state[0] >= windowMs) { state[0] = now; state[1] = 0; }
                if (state[1] < maxRequests) { state[1]++; return true; }
                return false;
            }
        }
    }

    // ======================= EASY 6: Request Throttler (Min Interval) =======================
    /**
     * Request Throttler (Min Interval)
     *
     * <p><b>Approach:</b> Enforces a minimum time interval between consecutive requests
     */
    static class Throttler {
        private final long minIntervalMs;
        private long lastRequestTime;

        Throttler(long minIntervalMs) { this.minIntervalMs = minIntervalMs; }

        public synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            if (now - lastRequestTime >= minIntervalMs) { lastRequestTime = now; return true; }
            return false;
        }

        public long waitTimeMs() {
            long elapsed = System.currentTimeMillis() - lastRequestTime;
            return Math.max(0, minIntervalMs - elapsed);
        }
    }

    // ======================= EASY 7: Rate Limiter with Retry-After =======================
    /**
     * Rate Limiter with Retry-After
     *
     * <p><b>Approach:</b> Returns how many milliseconds the client should wait before retrying
     */
    static class RetryAfterLimiter {
        private final int maxRequests;
        private final long windowMs;
        private long windowStart;
        private int count;

        RetryAfterLimiter(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
            this.windowStart = System.currentTimeMillis();
        }

        public synchronized long tryAcquire() {
            long now = System.currentTimeMillis();
            if (now - windowStart >= windowMs) { windowStart = now; count = 0; }
            if (count < maxRequests) { count++; return 0; }
            return windowMs - (now - windowStart); // Retry-After in ms
        }
    }

    // ======================= EASY 8: Concurrent Request Limiter (Semaphore) =======================
    /**
     * Concurrent Request Limiter (Semaphore)
     *
     * <p><b>Approach:</b> Limits the number of concurrent (in-flight) requests, not requests per time window
     */
    static class ConcurrencyLimiter {
        private final int maxConcurrent;
        private final AtomicInteger inFlight = new AtomicInteger(0);

        ConcurrencyLimiter(int maxConcurrent) { this.maxConcurrent = maxConcurrent; }
        public boolean acquire() { return inFlight.getAndIncrement() < maxConcurrent || release(); }
        public boolean release() { inFlight.decrementAndGet(); return false; }
        public int currentLoad() { return inFlight.get(); }
    }

    // ======================= EASY 9: Bandwidth Rate Limiter =======================
    /**
     * Bandwidth Rate Limiter
     *
     * <p><b>Approach:</b> Limits total bytes transferred per window instead of request count
     */
    static class BandwidthLimiter {
        private final long maxBytesPerWindow;
        private final long windowMs;
        private long windowStart;
        private long bytesUsed;

        BandwidthLimiter(long maxBytesPerWindow, long windowMs) {
            this.maxBytesPerWindow = maxBytesPerWindow;
            this.windowMs = windowMs;
            this.windowStart = System.currentTimeMillis();
        }

        public synchronized boolean tryConsume(long bytes) {
            long now = System.currentTimeMillis();
            if (now - windowStart >= windowMs) { windowStart = now; bytesUsed = 0; }
            if (bytesUsed + bytes <= maxBytesPerWindow) { bytesUsed += bytes; return true; }
            return false;
        }

        public long remainingBytes() { return Math.max(0, maxBytesPerWindow - bytesUsed); }
    }

    // ======================= EASY 10: IP-Based Rate Limiter =======================
    /**
     * IP-Based Rate Limiter
     *
     * <p><b>Approach:</b> Rate limits by IP address using a map of fixed-window counters
     */
    static class IPRateLimiter {
        private final int maxRequests;
        private final long windowMs;
        private final Map<String, long[]> ipMap = new ConcurrentHashMap<>();

        IPRateLimiter(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
        }

        public boolean tryAcquire(String ip) {
            long now = System.currentTimeMillis();
            long[] state = ipMap.computeIfAbsent(ip, k -> new long[]{now, 0});
            synchronized (state) {
                if (now - state[0] >= windowMs) { state[0] = now; state[1] = 0; }
                if (state[1] < maxRequests) { state[1]++; return true; }
                return false;
            }
        }

        public void cleanup() {
            long now = System.currentTimeMillis();
            ipMap.entrySet().removeIf(e -> now - e.getValue()[0] > windowMs * 2);
        }
    }

    // ======================= MEDIUM 1: Sliding Window Log =======================
    /**
     * Sliding Window Log
     *
     * <p><b>Approach:</b> Stores timestamps of each request. Precise but memory-intensive for high traffic.
     */
    static class SlidingWindowLog {
        private final int maxRequests;
        private final long windowMs;
        private final Deque<Long> timestamps = new ArrayDeque<>();

        SlidingWindowLog(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
        }

        public synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowMs) timestamps.pollFirst();
            if (timestamps.size() < maxRequests) { timestamps.addLast(now); return true; }
            return false;
        }

        public int currentCount() { return timestamps.size(); }
    }

    // ======================= MEDIUM 2: Sliding Window Counter =======================
    /**
     * Sliding Window Counter
     *
     * <p><b>Approach:</b> Hybrid: uses current + previous window counts with weighted overlap. More accurate than fixed window.
     */
    static class SlidingWindowCounter {
        private final int maxRequests;
        private final long windowMs;
        private long currentWindowStart;
        private int currentCount, previousCount;

        SlidingWindowCounter(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
            this.currentWindowStart = System.currentTimeMillis();
        }

        public synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            advanceWindow(now);
            double overlap = 1.0 - ((double)(now - currentWindowStart) / windowMs);
            double estimatedCount = previousCount * overlap + currentCount;
            if (estimatedCount < maxRequests) { currentCount++; return true; }
            return false;
        }

        private void advanceWindow(long now) {
            long elapsed = now - currentWindowStart;
            if (elapsed >= windowMs * 2) { currentWindowStart = now; currentCount = 0; previousCount = 0; }
            else if (elapsed >= windowMs) { previousCount = currentCount; currentCount = 0; currentWindowStart += windowMs; }
        }
    }

    // ======================= MEDIUM 3: Token Bucket with Burst Control =======================
    /**
     * Token Bucket with Burst Control
     *
     * <p><b>Approach:</b> Token bucket that distinguishes between sustained rate and burst allowance
     */
    static class BurstTokenBucket {
        private final int burstCapacity;
        private final double sustainedRate;
        private double tokens;
        private long lastNanos;

        BurstTokenBucket(int burstCapacity, double sustainedRatePerSec) {
            this.burstCapacity = burstCapacity;
            this.sustainedRate = sustainedRatePerSec;
            this.tokens = burstCapacity;
            this.lastNanos = System.nanoTime();
        }

        public synchronized boolean tryAcquire(int cost) {
            long now = System.nanoTime();
            double elapsed = (now - lastNanos) / 1e9;
            tokens = Math.min(burstCapacity, tokens + elapsed * sustainedRate);
            lastNanos = now;
            if (tokens >= cost) { tokens -= cost; return true; }
            return false;
        }

        public double availableTokens() { return tokens; }
    }

    // ======================= MEDIUM 4: Per-Endpoint Rate Limiter =======================
    /**
     * Per-Endpoint Rate Limiter
     *
     * <p><b>Approach:</b> Different rate limits for different API endpoints
     */
    static class EndpointRateLimiter {
        private final Map<String, int[]> config = new HashMap<>();  // endpoint → [maxReq, windowMs]
        private final Map<String, long[]> state = new ConcurrentHashMap<>(); // endpoint → [windowStart, count]

        public void configure(String endpoint, int maxRequests, long windowMs) {
            config.put(endpoint, new int[]{maxRequests, (int) windowMs});
        }

        public boolean tryAcquire(String endpoint) {
            int[] cfg = config.get(endpoint);
            if (cfg == null) return true;
            long now = System.currentTimeMillis();
            long[] st = state.computeIfAbsent(endpoint, k -> new long[]{now, 0});
            synchronized (st) {
                if (now - st[0] >= cfg[1]) { st[0] = now; st[1] = 0; }
                if (st[1] < cfg[0]) { st[1]++; return true; }
                return false;
            }
        }
    }

    // ======================= MEDIUM 5: Tiered Rate Limiter =======================
    /**
     * Tiered Rate Limiter
     *
     * <p><b>Approach:</b> Different rate limits based on user tier (free, basic, premium)
     */
    static class TieredRateLimiter {
        enum Tier { FREE(5), BASIC(20), PREMIUM(100);
            final int ratePerMinute; Tier(int r) { ratePerMinute = r; }
        }
        private final Map<String, Tier> userTiers = new HashMap<>();
        private final Map<String, long[]> state = new ConcurrentHashMap<>();

        public void setTier(String userId, Tier tier) { userTiers.put(userId, tier); }

        public boolean tryAcquire(String userId) {
            Tier tier = userTiers.getOrDefault(userId, Tier.FREE);
            long now = System.currentTimeMillis();
            long[] st = state.computeIfAbsent(userId, k -> new long[]{now, 0});
            synchronized (st) {
                if (now - st[0] >= 60_000) { st[0] = now; st[1] = 0; }
                if (st[1] < tier.ratePerMinute) { st[1]++; return true; }
                return false;
            }
        }
    }

    // ======================= MEDIUM 6: Leaky Bucket Queue =======================
    /**
     * Leaky Bucket Queue
     *
     * <p><b>Approach:</b> Queue-based leaky bucket: requests are queued and processed at a fixed rate
     */
    static class LeakyBucketQueue {
        private final int capacity;
        private final Queue<String> queue = new LinkedList<>();

        LeakyBucketQueue(int capacity) { this.capacity = capacity; }

        public synchronized boolean enqueue(String request) {
            if (queue.size() >= capacity) return false;
            queue.add(request);
            return true;
        }

        public synchronized String process() { return queue.poll(); }
        public int pending() { return queue.size(); }
        public boolean isFull() { return queue.size() >= capacity; }
    }

    // ======================= MEDIUM 7: Quota Manager =======================
    /**
     * Quota Manager
     *
     * <p><b>Approach:</b> Daily / hourly quota system. Tracks usage against allocated quota.
     */
    static class QuotaManager {
        private final Map<String, long[]> quotas = new HashMap<>();   // user → [dailyLimit, hourlyLimit]
        private final Map<String, long[]> usage = new HashMap<>();    // user → [dailyUsed, hourlyUsed]
        private final Map<String, long[]> resetTimes = new HashMap<>(); // user → [dayReset, hourReset]

        public void setQuota(String user, long daily, long hourly) {
            quotas.put(user, new long[]{daily, hourly});
            usage.put(user, new long[]{0, 0});
            long now = System.currentTimeMillis();
            resetTimes.put(user, new long[]{now + 86_400_000, now + 3_600_000});
        }

        public String tryConsume(String user) {
            long[] q = quotas.get(user);
            long[] u = usage.get(user);
            long[] r = resetTimes.get(user);
            if (q == null) return "NO_QUOTA";
            long now = System.currentTimeMillis();
            if (now >= r[0]) { u[0] = 0; r[0] = now + 86_400_000; }
            if (now >= r[1]) { u[1] = 0; r[1] = now + 3_600_000; }
            if (u[0] >= q[0]) return "DAILY_EXCEEDED";
            if (u[1] >= q[1]) return "HOURLY_EXCEEDED";
            u[0]++; u[1]++;
            return "OK";
        }
    }

    // ======================= MEDIUM 8: Exponential Backoff Calculator =======================
    /**
     * Exponential Backoff Calculator
     *
     * <p><b>Approach:</b> Computes retry delay with exponential backoff and optional jitter
     */
    static class ExponentialBackoff {
        private final long baseMs;
        private final long maxMs;
        private final boolean jitter;
        private final Random rand = new Random(42);
        private int attempt;

        ExponentialBackoff(long baseMs, long maxMs, boolean jitter) {
            this.baseMs = baseMs; this.maxMs = maxMs; this.jitter = jitter;
        }

        public long nextDelayMs() {
            long delay = Math.min(maxMs, baseMs * (1L << attempt));
            if (jitter) delay = (long) (rand.nextDouble() * delay);
            attempt++;
            return delay;
        }

        public void reset() { attempt = 0; }
        public int getAttempt() { return attempt; }
    }

    // ======================= MEDIUM 9: Rate Limiter Middleware Simulation =======================
    /**
     * Rate Limiter Middleware Simulation
     *
     * <p><b>Approach:</b> Simulates an HTTP middleware that checks rate limits and returns appropriate status codes
     */
    static class RateLimiterMiddleware {
        private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
        private final int maxTokens;
        private final double refillRate;

        RateLimiterMiddleware(int maxTokens, double refillRate) {
            this.maxTokens = maxTokens; this.refillRate = refillRate;
        }

        public String handleRequest(String clientId, String path) {
            TokenBucket bucket = buckets.computeIfAbsent(clientId, k -> new TokenBucket(maxTokens, refillRate));
            if (bucket.tryAcquire()) {
                return "200 OK [" + path + "] remaining≈" + String.format("%.0f", bucket.currentTokens());
            }
            return "429 Too Many Requests [Retry-After: 1s]";
        }
    }

    // ======================= MEDIUM 10: Global + Per-User Composite Limiter =======================
    /**
     * Global + Per-User Composite Limiter
     *
     * <p><b>Approach:</b> Two layers: a global rate limit for the whole service AND per-user limits. Both must pass.
     */
    static class CompositeRateLimiter {
        private final TokenBucket globalBucket;
        private final Map<String, TokenBucket> userBuckets = new ConcurrentHashMap<>();
        private final int userMax;
        private final double userRate;

        CompositeRateLimiter(int globalMax, double globalRate, int userMax, double userRate) {
            this.globalBucket = new TokenBucket(globalMax, globalRate);
            this.userMax = userMax; this.userRate = userRate;
        }

        public String tryAcquire(String userId) {
            TokenBucket userBucket = userBuckets.computeIfAbsent(userId, k -> new TokenBucket(userMax, userRate));
            if (!globalBucket.tryAcquire()) return "GLOBAL_LIMIT";
            if (!userBucket.tryAcquire()) return "USER_LIMIT";
            return "OK";
        }
    }

    // ======================= HARD 1: Adaptive Rate Limiter =======================
    /**
     * Adaptive Rate Limiter
     *
     * <p><b>Approach:</b> Dynamically adjusts the rate limit based on server response times / error rates
     */
    static class AdaptiveRateLimiter {
        private int currentLimit;
        private final int minLimit, maxLimit;
        private final double increaseRatio, decreaseRatio;
        private int successCount, errorCount;

        AdaptiveRateLimiter(int initial, int min, int max, double increase, double decrease) {
            this.currentLimit = initial; this.minLimit = min; this.maxLimit = max;
            this.increaseRatio = increase; this.decreaseRatio = decrease;
        }

        public synchronized void recordSuccess() {
            successCount++;
            if (successCount % 10 == 0) adjustUp();
        }

        public synchronized void recordError() {
            errorCount++;
            adjustDown();
        }

        private void adjustUp() { currentLimit = Math.min(maxLimit, (int)(currentLimit * increaseRatio)); }
        private void adjustDown() { currentLimit = Math.max(minLimit, (int)(currentLimit * decreaseRatio)); }
        public int getCurrentLimit() { return currentLimit; }
        public String stats() { return "limit=" + currentLimit + " ok=" + successCount + " err=" + errorCount; }
    }

    // ======================= HARD 2: Distributed Rate Limiter (Redis-Like) =======================
    /**
     * Distributed Rate Limiter (Redis-Like)
     *
     * <p><b>Approach:</b> Simulates distributed rate limiting using a shared counter store (like Redis INCR + EXPIRE)
     */
    static class DistributedRateLimiter {
        private final Map<String, long[]> store = new ConcurrentHashMap<>(); // key → [count, expireAt]
        private final int maxRequests;
        private final long windowMs;

        DistributedRateLimiter(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests; this.windowMs = windowMs;
        }

        public boolean tryAcquire(String key) {
            long now = System.currentTimeMillis();
            long[] state = store.compute(key, (k, v) -> {
                if (v == null || now >= v[1]) return new long[]{1, now + windowMs};
                v[0]++;
                return v;
            });
            return state[0] <= maxRequests;
        }

        public long getCount(String key) {
            long[] s = store.get(key);
            return s == null ? 0 : s[0];
        }
    }

    // ======================= HARD 3: Sliding Window with Sub-Buckets =======================
    /**
     * Sliding Window with Sub-Buckets
     *
     * <p><b>Approach:</b> Divides the window into N sub-buckets for finer granularity. More accurate than simple fixed window.
     */
    static class SlidingWindowBuckets {
        private final int maxRequests;
        private final long windowMs;
        private final int numBuckets;
        private final int[] buckets;
        private long bucketDuration;
        private long currentBucketStart;
        private int headIndex;

        SlidingWindowBuckets(int maxRequests, long windowMs, int numBuckets) {
            this.maxRequests = maxRequests; this.windowMs = windowMs; this.numBuckets = numBuckets;
            this.buckets = new int[numBuckets];
            this.bucketDuration = windowMs / numBuckets;
            this.currentBucketStart = System.currentTimeMillis();
        }

        public synchronized boolean tryAcquire() {
            advanceBuckets();
            int total = 0;
            for (int b : buckets) total += b;
            if (total < maxRequests) { buckets[headIndex]++; return true; }
            return false;
        }

        private void advanceBuckets() {
            long now = System.currentTimeMillis();
            long elapsed = now - currentBucketStart;
            int bucketsToAdvance = (int)(elapsed / bucketDuration);
            if (bucketsToAdvance > 0) {
                for (int i = 0; i < Math.min(bucketsToAdvance, numBuckets); i++) {
                    headIndex = (headIndex + 1) % numBuckets;
                    buckets[headIndex] = 0;
                }
                currentBucketStart += bucketsToAdvance * bucketDuration;
            }
        }
    }

    // ======================= HARD 4: Priority Rate Limiter =======================
    /**
     * Priority Rate Limiter
     *
     * <p><b>Approach:</b> Requests have priorities. High-priority requests are served first; low-priority dropped under load.
     */
    static class PriorityRateLimiter {
        enum Priority { HIGH, MEDIUM, LOW }
        private final int totalCapacity;
        private final Map<Priority, Double> reservedFraction;
        private final Map<Priority, Integer> counts = new EnumMap<>(Priority.class);
        private long windowStart;
        private final long windowMs;

        PriorityRateLimiter(int totalCapacity, long windowMs) {
            this.totalCapacity = totalCapacity; this.windowMs = windowMs;
            this.windowStart = System.currentTimeMillis();
            reservedFraction = new EnumMap<>(Priority.class);
            reservedFraction.put(Priority.HIGH, 0.5);
            reservedFraction.put(Priority.MEDIUM, 0.3);
            reservedFraction.put(Priority.LOW, 0.2);
            for (Priority p : Priority.values()) counts.put(p, 0);
        }

        public synchronized boolean tryAcquire(Priority priority) {
            long now = System.currentTimeMillis();
            if (now - windowStart >= windowMs) { windowStart = now; for (Priority p : Priority.values()) counts.put(p, 0); }
            int totalUsed = counts.values().stream().mapToInt(i -> i).sum();
            int reserved = (int)(totalCapacity * reservedFraction.get(priority));
            if (counts.get(priority) < reserved || totalUsed < totalCapacity) {
                counts.merge(priority, 1, Integer::sum);
                return true;
            }
            return false;
        }
    }

    // ======================= HARD 5: Circuit Breaker with Rate Limiting =======================
    /**
     * Circuit Breaker with Rate Limiting
     *
     * <p><b>Approach:</b> Combines rate limiting with circuit breaker pattern: CLOSED → OPEN → HALF_OPEN
     */
    static class CircuitBreakerLimiter {
        enum State { CLOSED, OPEN, HALF_OPEN }
        private State state = State.CLOSED;
        private final int failureThreshold;
        private final long openDurationMs;
        private final int halfOpenMaxRequests;
        private int failureCount, halfOpenRequests;
        private long openedAt;

        CircuitBreakerLimiter(int failureThreshold, long openDurationMs, int halfOpenMaxRequests) {
            this.failureThreshold = failureThreshold;
            this.openDurationMs = openDurationMs;
            this.halfOpenMaxRequests = halfOpenMaxRequests;
        }

        public synchronized boolean tryAcquire() {
            switch (state) {
                case OPEN:
                    if (System.currentTimeMillis() - openedAt >= openDurationMs) {
                        state = State.HALF_OPEN; halfOpenRequests = 0;
                    } else return false;
                case HALF_OPEN:
                    if (halfOpenRequests >= halfOpenMaxRequests) return false;
                    halfOpenRequests++;
                    return true;
                case CLOSED: default: return true;
            }
        }

        public synchronized void recordSuccess() { if (state == State.HALF_OPEN) { state = State.CLOSED; failureCount = 0; } }
        public synchronized void recordFailure() {
            failureCount++;
            if (failureCount >= failureThreshold) { state = State.OPEN; openedAt = System.currentTimeMillis(); }
        }
        public State getState() { return state; }
    }

    // ======================= HARD 6: Token Bucket with Priority Queuing =======================
    /**
     * Token Bucket with Priority Queuing
     *
     * <p><b>Approach:</b> Token bucket where high-priority requests get tokens before low-priority ones
     */
    static class PriorityTokenBucket {
        private final int maxTokens;
        private final double refillRate;
        private double tokens;
        private long lastNanos;
        private final Queue<String> highQueue = new LinkedList<>();
        private final Queue<String> lowQueue = new LinkedList<>();

        PriorityTokenBucket(int maxTokens, double refillRate) {
            this.maxTokens = maxTokens; this.refillRate = refillRate;
            this.tokens = maxTokens; this.lastNanos = System.nanoTime();
        }

        public synchronized void submit(String requestId, boolean highPriority) {
            if (highPriority) highQueue.add(requestId); else lowQueue.add(requestId);
        }

        public synchronized List<String> processAvailable() {
            refill();
            List<String> processed = new ArrayList<>();
            while (tokens >= 1 && !highQueue.isEmpty()) { processed.add(highQueue.poll() + "(H)"); tokens--; }
            while (tokens >= 1 && !lowQueue.isEmpty()) { processed.add(lowQueue.poll() + "(L)"); tokens--; }
            return processed;
        }

        private void refill() {
            long now = System.nanoTime();
            tokens = Math.min(maxTokens, tokens + (now - lastNanos) / 1e9 * refillRate);
            lastNanos = now;
        }
    }

    // ======================= HARD 7: Geo-Based Rate Limiter =======================
    /**
     * Geo-Based Rate Limiter
     *
     * <p><b>Approach:</b> Different rate limits for different geographic regions
     */
    static class GeoRateLimiter {
        private final Map<String, int[]> regionLimits = new HashMap<>(); // region → [maxReq, windowMs]
        private final Map<String, Map<String, long[]>> regionUserState = new HashMap<>();

        public void configureRegion(String region, int maxReq, long windowMs) {
            regionLimits.put(region, new int[]{maxReq, (int) windowMs});
            regionUserState.put(region, new ConcurrentHashMap<>());
        }

        public boolean tryAcquire(String region, String userId) {
            int[] limits = regionLimits.get(region);
            if (limits == null) return true;
            Map<String, long[]> userMap = regionUserState.get(region);
            long now = System.currentTimeMillis();
            long[] st = userMap.computeIfAbsent(userId, k -> new long[]{now, 0});
            synchronized (st) {
                if (now - st[0] >= limits[1]) { st[0] = now; st[1] = 0; }
                if (st[1] < limits[0]) { st[1]++; return true; }
                return false;
            }
        }
    }

    // ======================= HARD 8: Cost-Based Rate Limiter =======================
    /**
     * Cost-Based Rate Limiter
     *
     * <p><b>Approach:</b> Each API operation has a cost. Limits are in cost units per window, not request count.
     */
    static class CostBasedRateLimiter {
        private final Map<String, Integer> operationCosts = new HashMap<>();
        private final int maxCostPerWindow;
        private final long windowMs;
        private final Map<String, long[]> userState = new ConcurrentHashMap<>(); // [windowStart, costUsed]

        CostBasedRateLimiter(int maxCostPerWindow, long windowMs) {
            this.maxCostPerWindow = maxCostPerWindow; this.windowMs = windowMs;
        }

        public void setOperationCost(String operation, int cost) { operationCosts.put(operation, cost); }

        public boolean tryAcquire(String userId, String operation) {
            int cost = operationCosts.getOrDefault(operation, 1);
            long now = System.currentTimeMillis();
            long[] st = userState.computeIfAbsent(userId, k -> new long[]{now, 0});
            synchronized (st) {
                if (now - st[0] >= windowMs) { st[0] = now; st[1] = 0; }
                if (st[1] + cost <= maxCostPerWindow) { st[1] += cost; return true; }
                return false;
            }
        }
    }

    // ======================= HARD 9: Rate Limiter with Penalty Box =======================
    /**
     * Rate Limiter with Penalty Box
     *
     * <p><b>Approach:</b> Clients that exceed limits get put in a penalty box with increasingly longer bans
     */
    static class PenaltyBoxLimiter {
        private final int maxRequests;
        private final long windowMs;
        private final Map<String, long[]> state = new ConcurrentHashMap<>();  // [windowStart, count]
        private final Map<String, long[]> penalties = new ConcurrentHashMap<>(); // [penaltyUntil, strikes]

        PenaltyBoxLimiter(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests; this.windowMs = windowMs;
        }

        public String tryAcquire(String clientId) {
            long now = System.currentTimeMillis();
            long[] pen = penalties.get(clientId);
            if (pen != null && now < pen[0]) return "PENALTY (banned until +" + (pen[0] - now) + "ms)";

            long[] st = state.computeIfAbsent(clientId, k -> new long[]{now, 0});
            synchronized (st) {
                if (now - st[0] >= windowMs) { st[0] = now; st[1] = 0; }
                if (st[1] < maxRequests) { st[1]++; return "OK"; }
                long[] p = penalties.computeIfAbsent(clientId, k -> new long[]{0, 0});
                p[1]++;
                p[0] = now + windowMs * p[1]; // penalty grows with each strike
                return "RATE_LIMITED → penalty #" + (int) p[1];
            }
        }
    }

    // ======================= HARD 10: Multi-Algorithm Rate Limiter =======================
    /**
     * Multi-Algorithm Rate Limiter
     *
     * <p><b>Approach:</b> Combines multiple algorithms: token bucket for bursts, sliding window for sustained, concurrency limit
     */
    static class MultiAlgorithmLimiter {
        private final TokenBucket burstBucket;
        private final SlidingWindowLog sustainedWindow;
        private final AtomicInteger concurrent = new AtomicInteger(0);
        private final int maxConcurrent;

        MultiAlgorithmLimiter(int burstMax, double burstRefill, int sustainedMax, long sustainedWindowMs, int maxConcurrent) {
            this.burstBucket = new TokenBucket(burstMax, burstRefill);
            this.sustainedWindow = new SlidingWindowLog(sustainedMax, sustainedWindowMs);
            this.maxConcurrent = maxConcurrent;
        }

        public String tryAcquire() {
            if (concurrent.get() >= maxConcurrent) return "CONCURRENCY_LIMIT";
            if (!burstBucket.tryAcquire()) return "BURST_LIMIT";
            if (!sustainedWindow.tryAcquire()) return "SUSTAINED_LIMIT";
            concurrent.incrementAndGet();
            return "OK";
        }

        public void release() { concurrent.decrementAndGet(); }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) throws Exception {
        System.out.println("=== RATE LIMITING (30 Examples) ===\n");

        System.out.println("--- EASY ---");
        // 1. Fixed Window
        FixedWindowCounter fw = new FixedWindowCounter(3, 1000);
        System.out.println("1. Fixed Window: " + fw.tryAcquire() + " " + fw.tryAcquire() + " " + fw.tryAcquire() + " " + fw.tryAcquire() + " remaining=" + fw.remaining());

        // 2. Token Bucket
        TokenBucket tb = new TokenBucket(5, 10);
        StringBuilder tbRes = new StringBuilder();
        for (int i = 0; i < 7; i++) tbRes.append(tb.tryAcquire() ? "OK " : "NO ");
        System.out.println("2. Token Bucket: " + tbRes.toString().trim());

        // 3. Leaky Bucket
        LeakyBucket lb = new LeakyBucket(3, 1);
        System.out.println("3. Leaky Bucket: " + lb.tryAcquire() + " " + lb.tryAcquire() + " " + lb.tryAcquire() + " " + lb.tryAcquire());

        // 4. Simple Counter
        SimpleCounter sc = new SimpleCounter(3);
        System.out.println("4. Simple Counter: " + sc.tryAcquire() + " " + sc.tryAcquire() + " " + sc.tryAcquire() + " " + sc.tryAcquire());

        // 5. Per-User
        PerUserRateLimiter purl = new PerUserRateLimiter(2, 1000);
        System.out.println("5. Per-User: alice=" + purl.tryAcquire("alice") + " " + purl.tryAcquire("alice") + " " + purl.tryAcquire("alice") + " bob=" + purl.tryAcquire("bob"));

        // 6. Throttler
        Throttler thr = new Throttler(100);
        System.out.println("6. Throttler: " + thr.tryAcquire() + " " + thr.tryAcquire() + " wait=" + thr.waitTimeMs() + "ms");

        // 7. Retry-After
        RetryAfterLimiter ral = new RetryAfterLimiter(2, 1000);
        System.out.println("7. Retry-After: " + ral.tryAcquire() + "ms " + ral.tryAcquire() + "ms " + ral.tryAcquire() + "ms(wait)");

        // 8. Concurrency Limiter
        ConcurrencyLimiter cl = new ConcurrencyLimiter(2);
        System.out.println("8. Concurrency: " + cl.acquire() + " " + cl.acquire() + " " + cl.acquire() + " (after release: " + (cl.release() || true) + ")");

        // 9. Bandwidth
        BandwidthLimiter bwl = new BandwidthLimiter(1024, 1000);
        System.out.println("9. Bandwidth: 500B=" + bwl.tryConsume(500) + " 500B=" + bwl.tryConsume(500) + " 100B=" + bwl.tryConsume(100) + " remaining=" + bwl.remainingBytes());

        // 10. IP-Based
        IPRateLimiter iprl = new IPRateLimiter(2, 1000);
        System.out.println("10. IP-Based: " + iprl.tryAcquire("10.0.0.1") + " " + iprl.tryAcquire("10.0.0.1") + " " + iprl.tryAcquire("10.0.0.1") + " other_ip=" + iprl.tryAcquire("10.0.0.2"));

        System.out.println("\n--- MEDIUM ---");
        // 11. Sliding Window Log
        SlidingWindowLog swl = new SlidingWindowLog(3, 1000);
        System.out.println("11. Sliding Log: " + swl.tryAcquire() + " " + swl.tryAcquire() + " " + swl.tryAcquire() + " " + swl.tryAcquire());

        // 12. Sliding Window Counter
        SlidingWindowCounter swc = new SlidingWindowCounter(3, 1000);
        System.out.println("12. Sliding Counter: " + swc.tryAcquire() + " " + swc.tryAcquire() + " " + swc.tryAcquire() + " " + swc.tryAcquire());

        // 13. Burst Token Bucket
        BurstTokenBucket btb = new BurstTokenBucket(10, 2.0);
        System.out.println("13. Burst Bucket: single=" + btb.tryAcquire(1) + " bulk5=" + btb.tryAcquire(5) + " remaining≈" + String.format("%.0f", btb.availableTokens()));

        // 14. Per-Endpoint
        EndpointRateLimiter erl = new EndpointRateLimiter();
        erl.configure("/api/search", 2, 1000);
        erl.configure("/api/login", 5, 1000);
        System.out.println("14. Endpoint: search=" + erl.tryAcquire("/api/search") + " " + erl.tryAcquire("/api/search") + " " + erl.tryAcquire("/api/search") + " login=" + erl.tryAcquire("/api/login"));

        // 15. Tiered
        TieredRateLimiter trl = new TieredRateLimiter();
        trl.setTier("free_user", TieredRateLimiter.Tier.FREE);
        trl.setTier("premium_user", TieredRateLimiter.Tier.PREMIUM);
        int freeOk = 0; for (int i = 0; i < 10; i++) if (trl.tryAcquire("free_user")) freeOk++;
        System.out.println("15. Tiered: free(10 attempts)=" + freeOk + "/5 premium=" + trl.tryAcquire("premium_user"));

        // 16. Leaky Bucket Queue
        LeakyBucketQueue lbq = new LeakyBucketQueue(3);
        lbq.enqueue("req1"); lbq.enqueue("req2"); lbq.enqueue("req3");
        System.out.println("16. Leaky Queue: full=" + lbq.isFull() + " overflow=" + lbq.enqueue("req4") + " process=" + lbq.process());

        // 17. Quota Manager
        QuotaManager qm = new QuotaManager();
        qm.setQuota("alice", 100, 10);
        StringBuilder qmRes = new StringBuilder();
        for (int i = 0; i < 12; i++) qmRes.append(qm.tryConsume("alice")).append(" ");
        System.out.println("17. Quota: " + qmRes.toString().trim());

        // 18. Exponential Backoff
        ExponentialBackoff eb = new ExponentialBackoff(100, 10000, true);
        System.out.println("18. Backoff: " + eb.nextDelayMs() + "ms " + eb.nextDelayMs() + "ms " + eb.nextDelayMs() + "ms " + eb.nextDelayMs() + "ms");

        // 19. Middleware
        RateLimiterMiddleware mw = new RateLimiterMiddleware(3, 10);
        System.out.println("19. Middleware: " + mw.handleRequest("client1", "/api/data"));
        System.out.println("    " + mw.handleRequest("client1", "/api/data"));
        System.out.println("    " + mw.handleRequest("client1", "/api/data"));
        System.out.println("    " + mw.handleRequest("client1", "/api/data"));

        // 20. Composite
        CompositeRateLimiter comp = new CompositeRateLimiter(100, 50, 3, 10);
        System.out.println("20. Composite: " + comp.tryAcquire("u1") + " " + comp.tryAcquire("u1") + " " + comp.tryAcquire("u1") + " " + comp.tryAcquire("u1"));

        System.out.println("\n--- HARD ---");
        // 21. Adaptive
        AdaptiveRateLimiter arl = new AdaptiveRateLimiter(50, 10, 200, 1.2, 0.5);
        for (int i = 0; i < 20; i++) arl.recordSuccess();
        arl.recordError();
        System.out.println("21. Adaptive: " + arl.stats());

        // 22. Distributed
        DistributedRateLimiter drl = new DistributedRateLimiter(3, 1000);
        System.out.println("22. Distributed: " + drl.tryAcquire("api:user1") + " " + drl.tryAcquire("api:user1") + " " + drl.tryAcquire("api:user1") + " " + drl.tryAcquire("api:user1") + " count=" + drl.getCount("api:user1"));

        // 23. Sliding Buckets
        SlidingWindowBuckets swb = new SlidingWindowBuckets(5, 1000, 10);
        StringBuilder swbRes = new StringBuilder();
        for (int i = 0; i < 7; i++) swbRes.append(swb.tryAcquire() ? "OK " : "NO ");
        System.out.println("23. Sliding Buckets: " + swbRes.toString().trim());

        // 24. Priority
        PriorityRateLimiter prl = new PriorityRateLimiter(10, 1000);
        System.out.println("24. Priority: HIGH=" + prl.tryAcquire(PriorityRateLimiter.Priority.HIGH)
                + " MED=" + prl.tryAcquire(PriorityRateLimiter.Priority.MEDIUM)
                + " LOW=" + prl.tryAcquire(PriorityRateLimiter.Priority.LOW));

        // 25. Circuit Breaker
        CircuitBreakerLimiter cb = new CircuitBreakerLimiter(3, 1000, 2);
        System.out.println("25. Circuit: state=" + cb.getState() + " allow=" + cb.tryAcquire());
        cb.recordFailure(); cb.recordFailure(); cb.recordFailure();
        System.out.println("    After 3 fails: state=" + cb.getState() + " allow=" + cb.tryAcquire());

        // 26. Priority Token Bucket
        PriorityTokenBucket ptb = new PriorityTokenBucket(3, 10);
        ptb.submit("r1", false); ptb.submit("r2", true); ptb.submit("r3", false); ptb.submit("r4", true);
        System.out.println("26. Priority Bucket: processed=" + ptb.processAvailable());

        // 27. Geo-Based
        GeoRateLimiter geo = new GeoRateLimiter();
        geo.configureRegion("US", 5, 1000); geo.configureRegion("EU", 3, 1000);
        System.out.println("27. Geo: US=" + geo.tryAcquire("US", "u1") + " EU=" + geo.tryAcquire("EU", "u1"));

        // 28. Cost-Based
        CostBasedRateLimiter cbl = new CostBasedRateLimiter(10, 1000);
        cbl.setOperationCost("read", 1); cbl.setOperationCost("write", 3); cbl.setOperationCost("delete", 5);
        System.out.println("28. Cost-Based: read=" + cbl.tryAcquire("u1", "read") + " write=" + cbl.tryAcquire("u1", "write") + " delete=" + cbl.tryAcquire("u1", "delete") + " delete2=" + cbl.tryAcquire("u1", "delete"));

        // 29. Penalty Box
        PenaltyBoxLimiter pbl = new PenaltyBoxLimiter(2, 1000);
        System.out.println("29. Penalty: " + pbl.tryAcquire("bad_client") + " " + pbl.tryAcquire("bad_client") + " " + pbl.tryAcquire("bad_client"));

        // 30. Multi-Algorithm
        MultiAlgorithmLimiter mal = new MultiAlgorithmLimiter(5, 10, 10, 1000, 3);
        System.out.println("30. Multi-Algo: " + mal.tryAcquire() + " " + mal.tryAcquire() + " " + mal.tryAcquire() + " " + mal.tryAcquire());
    }
}

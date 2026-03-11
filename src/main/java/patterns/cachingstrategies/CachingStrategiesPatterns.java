package patterns.cachingstrategies;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * CACHING STRATEGIES — 30 Essential Examples
 *
 * Caching improves response times and reduces backend load by storing frequently
 * accessed data closer to the consumer. Key concepts include:
 *   - Eviction policies: LRU, LFU, FIFO, LRU-K, Random, TTL-based
 *   - Write strategies: Write-through, Write-back (write-behind), Write-around
 *   - Read strategies: Cache-aside (lazy loading), Read-through
 *   - Advanced: Multi-level cache, Bloom filter, Consistent hashing, CDN simulation
 *
 * 10 Easy | 10 Medium | 10 Hard
 */
public class CachingStrategiesPatterns {

    // ======================= EASY 1: Simple HashMap Cache =======================
    /** Basic key-value store backed by HashMap. O(1) average get/put, no eviction. */
    static class SimpleCache<K, V> {
        private final Map<K, V> store = new HashMap<>();
        public void put(K key, V value) { store.put(key, value); }
        public V get(K key) { return store.get(key); }
        public boolean contains(K key) { return store.containsKey(key); }
        public int size() { return store.size(); }
        public void remove(K key) { store.remove(key); }
    }

    // ======================= EASY 2: FIFO Cache (First In, First Out) =======================
    /** Evicts oldest inserted entry using LinkedHashMap insertion order. O(1) get/put. */
    static class FIFOCache<K, V> {
        private final int capacity;
        private final Map<K, V> map = new LinkedHashMap<>();
        FIFOCache(int capacity) { this.capacity = capacity; }
        public void put(K key, V value) {
            if (!map.containsKey(key) && map.size() >= capacity) {
                K oldest = map.keySet().iterator().next();
                map.remove(oldest);
            }
            map.put(key, value);
        }
        public V get(K key) { return map.get(key); }
        public int size() { return map.size(); }
        @Override public String toString() { return map.toString(); }
    }

    // ======================= EASY 3: LRU Cache (Least Recently Used) =======================
    /** Access-order LinkedHashMap with removeEldestEntry for automatic LRU eviction. O(1) get/put. */
    static class LRUCache<K, V> {
        private final int capacity;
        private final LinkedHashMap<K, V> map;
        LRUCache(int capacity) {
            this.capacity = capacity;
            this.map = new LinkedHashMap<>(16, 0.75f, true) {
                @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                    return size() > capacity;
                }
            };
        }
        public V get(K key) { return map.getOrDefault(key, null); }
        public void put(K key, V value) { map.put(key, value); }
        @Override public String toString() { return map.toString(); }
    }

    // ======================= EASY 4: Cache with Max Size (Random Eviction) =======================
    /** Evicts a random key when at capacity using ArrayList index swap. O(1) average get/put. */
    static class RandomEvictionCache<K, V> {
        private final int capacity;
        private final Map<K, V> map = new HashMap<>();
        private final List<K> keys = new ArrayList<>();
        private final Random rand = new Random(42);
        RandomEvictionCache(int capacity) { this.capacity = capacity; }
        public void put(K key, V value) {
            if (!map.containsKey(key) && map.size() >= capacity) {
                int idx = rand.nextInt(keys.size());
                K evict = keys.get(idx);
                keys.set(idx, keys.get(keys.size() - 1));
                keys.remove(keys.size() - 1);
                map.remove(evict);
            }
            if (!map.containsKey(key)) keys.add(key);
            map.put(key, value);
        }
        public V get(K key) { return map.get(key); }
        @Override public String toString() { return map.toString(); }
    }

    // ======================= EASY 5: Write-Through Cache =======================
    /** Writes synchronously to both cache and database; strong consistency, higher write latency. */
    static class WriteThroughCache {
        private final Map<String, String> cache = new HashMap<>();
        private final Map<String, String> database = new HashMap<>();
        public void put(String key, String value) {
            cache.put(key, value);
            database.put(key, value);  // synchronous write to DB
        }
        public String get(String key) {
            if (cache.containsKey(key)) return cache.get(key);
            String val = database.get(key);
            if (val != null) cache.put(key, val);
            return val;
        }
        public String getFromDB(String key) { return database.get(key); }
        public boolean inCache(String key) { return cache.containsKey(key); }
    }

    // ======================= EASY 6: Cache-Aside (Lazy Loading) =======================
    /** App checks cache first; on miss, loads from DB and populates cache. Simple but may serve stale data. */
    static class CacheAside {
        private final Map<String, String> cache = new HashMap<>();
        private final Map<String, String> database;
        CacheAside(Map<String, String> database) { this.database = database; }
        public String get(String key) {
            if (cache.containsKey(key)) return "[cache hit] " + cache.get(key);
            String val = database.get(key);
            if (val != null) cache.put(key, val);
            return val != null ? "[cache miss → loaded] " + val : null;
        }
        public void invalidate(String key) { cache.remove(key); }
    }

    // ======================= EASY 7: TTL Cache (Time-To-Live) =======================
    /** Entries auto-expire after a configurable duration. O(1) get/put with lazy expiration check. */
    static class TTLCache<K, V> {
        private final long ttlMs;
        private final Map<K, long[]> timestamps = new HashMap<>();
        private final Map<K, V> store = new HashMap<>();
        TTLCache(long ttlMs) { this.ttlMs = ttlMs; }
        public void put(K key, V value) { store.put(key, value); timestamps.put(key, new long[]{System.currentTimeMillis()}); }
        public V get(K key) {
            if (!store.containsKey(key)) return null;
            if (System.currentTimeMillis() - timestamps.get(key)[0] > ttlMs) { store.remove(key); timestamps.remove(key); return null; }
            return store.get(key);
        }
        public int size() { return store.size(); }
    }

    // ======================= EASY 8: Cache Hit/Miss Counter =======================
    /** Wraps a cache with hit/miss counters to track and report hit rate. O(1) get/put. */
    static class InstrumentedCache<K, V> {
        private final Map<K, V> store = new HashMap<>();
        private int hits, misses;
        public void put(K key, V value) { store.put(key, value); }
        public V get(K key) {
            if (store.containsKey(key)) { hits++; return store.get(key); }
            misses++; return null;
        }
        public double hitRate() { int total = hits + misses; return total == 0 ? 0 : (double) hits / total; }
        public String stats() { return "hits=" + hits + " misses=" + misses + " rate=" + String.format("%.1f%%", hitRate() * 100); }
    }

    // ======================= EASY 9: Memoization (Function Cache) =======================
    /** Caches function results by input using computeIfAbsent. O(1) lookup after first computation. */
    static class Memoizer<K, V> {
        private final Map<K, V> cache = new HashMap<>();
        private final Function<K, V> function;
        Memoizer(Function<K, V> function) { this.function = function; }
        public V compute(K key) { return cache.computeIfAbsent(key, function); }
        public int cacheSize() { return cache.size(); }
    }

    // ======================= EASY 10: Fibonacci with Memoization =======================
    /** Classic top-down DP: store computed fib values in HashMap to avoid recomputation. O(n) time, O(n) space. */
    public static long fibMemo(int n) {
        return fibHelper(n, new HashMap<>());
    }
    private static long fibHelper(int n, Map<Integer, Long> memo) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) return memo.get(n);
        long val = fibHelper(n - 1, memo) + fibHelper(n - 2, memo);
        memo.put(n, val);
        return val;
    }

    // ======================= MEDIUM 1: LFU Cache (Least Frequently Used) =======================
    /** Tracks access frequency per key; evicts least frequently used (LRU tiebreaker via LinkedHashSet). O(1) ops. */
    static class LFUCache {
        private final int capacity;
        private int minFreq;
        private final Map<Integer, int[]> keyToValFreq = new HashMap<>();
        private final Map<Integer, LinkedHashSet<Integer>> freqToKeys = new HashMap<>();
        LFUCache(int capacity) { this.capacity = capacity; }
        public int get(int key) {
            if (!keyToValFreq.containsKey(key)) return -1;
            touch(key);
            return keyToValFreq.get(key)[0];
        }
        public void put(int key, int value) {
            if (capacity <= 0) return;
            if (keyToValFreq.containsKey(key)) { keyToValFreq.get(key)[0] = value; touch(key); return; }
            if (keyToValFreq.size() >= capacity) evict();
            keyToValFreq.put(key, new int[]{value, 1});
            freqToKeys.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
            minFreq = 1;
        }
        private void touch(int key) {
            int[] vf = keyToValFreq.get(key);
            int freq = vf[1];
            freqToKeys.get(freq).remove(key);
            if (freqToKeys.get(freq).isEmpty()) { freqToKeys.remove(freq); if (minFreq == freq) minFreq++; }
            vf[1]++;
            freqToKeys.computeIfAbsent(vf[1], k -> new LinkedHashSet<>()).add(key);
        }
        private void evict() {
            LinkedHashSet<Integer> keys = freqToKeys.get(minFreq);
            int evictKey = keys.iterator().next();
            keys.remove(evictKey);
            if (keys.isEmpty()) freqToKeys.remove(minFreq);
            keyToValFreq.remove(evictKey);
        }
    }

    // ======================= MEDIUM 2: Write-Back Cache (Write-Behind) =======================
    /** Writes only to cache; tracks dirty keys and flushes to DB on demand. Low write latency, risk of data loss. */
    static class WriteBackCache {
        private final Map<String, String> cache = new HashMap<>();
        private final Map<String, String> database = new HashMap<>();
        private final Set<String> dirtyKeys = new HashSet<>();
        public void put(String key, String value) {
            cache.put(key, value);
            dirtyKeys.add(key);  // mark dirty, NOT written to DB immediately
        }
        public String get(String key) {
            if (cache.containsKey(key)) return cache.get(key);
            return database.get(key);
        }
        public void flush() {
            for (String key : dirtyKeys) database.put(key, cache.get(key));
            int flushed = dirtyKeys.size();
            dirtyKeys.clear();
            System.out.println("  Flushed " + flushed + " dirty entries to database");
        }
        public int dirtyCount() { return dirtyKeys.size(); }
        public String getFromDB(String key) { return database.get(key); }
    }

    // ======================= MEDIUM 3: Write-Around Cache =======================
    /** Writes directly to DB, invalidates cache; cache populated only on read miss. Avoids cache pollution. */
    static class WriteAroundCache {
        private final Map<String, String> cache = new HashMap<>();
        private final Map<String, String> database = new HashMap<>();
        public void put(String key, String value) {
            database.put(key, value);  // write directly to DB, skip cache
            cache.remove(key);         // invalidate stale cache entry
        }
        public String get(String key) {
            if (cache.containsKey(key)) return "[hit] " + cache.get(key);
            String val = database.get(key);
            if (val != null) cache.put(key, val);
            return val != null ? "[miss → loaded] " + val : null;
        }
    }

    // ======================= MEDIUM 4: Read-Through Cache =======================
    /** Cache auto-loads from backend via loader function on miss; transparent to caller. O(1) cached get. */
    static class ReadThroughCache<K, V> {
        private final int capacity;
        private final Map<K, V> cache;
        private final Function<K, V> loader;
        ReadThroughCache(int capacity, Function<K, V> loader) {
            this.capacity = capacity;
            this.loader = loader;
            this.cache = new LinkedHashMap<>(16, 0.75f, true) {
                @Override protected boolean removeEldestEntry(Map.Entry<K, V> e) { return size() > capacity; }
            };
        }
        public V get(K key) {
            if (cache.containsKey(key)) return cache.get(key);
            V val = loader.apply(key);
            if (val != null) cache.put(key, val);
            return val;
        }
        public int cacheSize() { return cache.size(); }
    }

    // ======================= MEDIUM 5: Two-Level Cache (L1 + L2) =======================
    /** Small fast L1 backed by larger L2; L1 miss promotes from L2. Mimics CPU cache hierarchy. */
    static class TwoLevelCache<K, V> {
        private final LRUCache<K, V> l1;
        private final LRUCache<K, V> l2;
        TwoLevelCache(int l1Cap, int l2Cap) { l1 = new LRUCache<>(l1Cap); l2 = new LRUCache<>(l2Cap); }
        public void put(K key, V value) { l1.put(key, value); l2.put(key, value); }
        public V get(K key) {
            V val = l1.get(key);
            if (val != null) return val;
            val = l2.get(key);
            if (val != null) l1.put(key, val);
            return val;
        }
    }

    // ======================= MEDIUM 6: LRU Cache with Manual Doubly-Linked List =======================
    /** Hand-rolled doubly-linked list + HashMap for O(1) get/put with explicit node management. */
    static class LRUCacheManual {
        private static class Node {
            int key, val; Node prev, next;
            Node(int k, int v) { key = k; val = v; }
        }
        private final int capacity;
        private final Map<Integer, Node> map = new HashMap<>();
        private final Node head = new Node(0, 0), tail = new Node(0, 0);
        LRUCacheManual(int capacity) { this.capacity = capacity; head.next = tail; tail.prev = head; }
        public int get(int key) {
            if (!map.containsKey(key)) return -1;
            Node node = map.get(key);
            remove(node); addToHead(node);
            return node.val;
        }
        public void put(int key, int value) {
            if (map.containsKey(key)) { Node n = map.get(key); n.val = value; remove(n); addToHead(n); return; }
            if (map.size() >= capacity) { Node lru = tail.prev; remove(lru); map.remove(lru.key); }
            Node n = new Node(key, value); addToHead(n); map.put(key, n);
        }
        private void remove(Node n) { n.prev.next = n.next; n.next.prev = n.prev; }
        private void addToHead(Node n) { n.next = head.next; n.prev = head; head.next.prev = n; head.next = n; }
        @Override public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (Node n = head.next; n != tail; n = n.next) { if (n != head.next) sb.append(", "); sb.append(n.key).append("=").append(n.val); }
            return sb.append("]").toString();
        }
    }

    // ======================= MEDIUM 7: Bloom Filter (Probabilistic Cache Check) =======================
    /** Probabilistic set membership: multiple hash functions into bit array. No false negatives. O(k) per op. */
    static class BloomFilter {
        private final boolean[] bits;
        private final int numHashes;
        BloomFilter(int size, int numHashes) { bits = new boolean[size]; this.numHashes = numHashes; }
        public void add(String item) { for (int i = 0; i < numHashes; i++) bits[hash(item, i)] = true; }
        public boolean mightContain(String item) { for (int i = 0; i < numHashes; i++) if (!bits[hash(item, i)]) return false; return true; }
        private int hash(String item, int seed) { return Math.abs((item.hashCode() * (seed + 1) * 31 + seed * 17)) % bits.length; }
    }

    // ======================= MEDIUM 8: Cache with Refresh-Ahead =======================
    /** Proactively refreshes entries nearing expiry via loader function. Reduces read-miss latency spikes. */
    static class RefreshAheadCache<K, V> {
        private final Map<K, V> cache = new HashMap<>();
        private final Map<K, Long> accessTime = new HashMap<>();
        private final long refreshThresholdMs;
        private final Function<K, V> loader;
        RefreshAheadCache(long refreshThresholdMs, Function<K, V> loader) {
            this.refreshThresholdMs = refreshThresholdMs; this.loader = loader;
        }
        public V get(K key) {
            if (cache.containsKey(key)) {
                long age = System.currentTimeMillis() - accessTime.getOrDefault(key, 0L);
                if (age > refreshThresholdMs) {
                    V fresh = loader.apply(key);
                    cache.put(key, fresh);
                    accessTime.put(key, System.currentTimeMillis());
                    return fresh;
                }
                return cache.get(key);
            }
            V val = loader.apply(key);
            cache.put(key, val);
            accessTime.put(key, System.currentTimeMillis());
            return val;
        }
    }

    // ======================= MEDIUM 9: Consistent Hashing (Cache Distribution) =======================
    /** Distributes keys across cache nodes using virtual nodes on a hash ring. Minimizes redistribution on scale. */
    static class ConsistentHashRing {
        private final TreeMap<Integer, String> ring = new TreeMap<>();
        private final int virtualNodes;
        ConsistentHashRing(int virtualNodes) { this.virtualNodes = virtualNodes; }
        public void addNode(String server) {
            for (int i = 0; i < virtualNodes; i++) ring.put(hash(server + "#" + i), server);
        }
        public void removeNode(String server) {
            for (int i = 0; i < virtualNodes; i++) ring.remove(hash(server + "#" + i));
        }
        public String getNode(String key) {
            if (ring.isEmpty()) return null;
            Integer k = ring.ceilingKey(hash(key));
            return ring.get(k != null ? k : ring.firstKey());
        }
        private int hash(String key) { return Math.abs(key.hashCode()); }
    }

    // ======================= MEDIUM 10: Bounded Buffer Queue (Producer-Consumer) =======================
    /** Fixed-capacity synchronized queue for producer-consumer pattern. Thread-safe offer/poll. */
    static class BoundedBuffer<T> {
        private final Queue<T> queue = new LinkedList<>();
        private final int capacity;
        BoundedBuffer(int capacity) { this.capacity = capacity; }
        public synchronized boolean offer(T item) {
            if (queue.size() >= capacity) return false;
            queue.add(item);
            return true;
        }
        public synchronized T poll() { return queue.poll(); }
        public synchronized int size() { return queue.size(); }
        public synchronized boolean isFull() { return queue.size() >= capacity; }
    }

    // ======================= HARD 1: LFU Cache (O(1) All Operations) =======================
    /** Optimal LFU with frequency buckets (LinkedHashSet) and minFreq tracking. O(1) get/put/evict. */
    static class LFUCacheOptimal {
        private final int capacity;
        private int minFreq;
        private final Map<Integer, int[]> cache = new HashMap<>();       // key → [val, freq]
        private final Map<Integer, LinkedHashSet<Integer>> freqMap = new HashMap<>();
        LFUCacheOptimal(int capacity) { this.capacity = capacity; }
        public int get(int key) {
            if (!cache.containsKey(key)) return -1;
            incrementFreq(key);
            return cache.get(key)[0];
        }
        public void put(int key, int value) {
            if (capacity <= 0) return;
            if (cache.containsKey(key)) { cache.get(key)[0] = value; incrementFreq(key); return; }
            if (cache.size() >= capacity) {
                LinkedHashSet<Integer> minSet = freqMap.get(minFreq);
                int evict = minSet.iterator().next();
                minSet.remove(evict); if (minSet.isEmpty()) freqMap.remove(minFreq);
                cache.remove(evict);
            }
            cache.put(key, new int[]{value, 1});
            freqMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
            minFreq = 1;
        }
        private void incrementFreq(int key) {
            int[] vf = cache.get(key);
            int oldFreq = vf[1];
            LinkedHashSet<Integer> oldSet = freqMap.get(oldFreq);
            oldSet.remove(key);
            if (oldSet.isEmpty()) { freqMap.remove(oldFreq); if (minFreq == oldFreq) minFreq++; }
            vf[1]++;
            freqMap.computeIfAbsent(vf[1], k -> new LinkedHashSet<>()).add(key);
        }
    }

    // ======================= HARD 2: LRU-K Cache (Evict by K-th Access) =======================
    /** Evicts entry with oldest K-th most recent access; scan-resistant. O(n) eviction, O(1) get/put. */
    static class LRUKCache {
        private final int capacity, k;
        private final Map<Integer, Integer> cache = new HashMap<>();
        private final Map<Integer, LinkedList<Long>> history = new HashMap<>();
        private long timestamp = 0;
        LRUKCache(int capacity, int k) { this.capacity = capacity; this.k = k; }
        public int get(int key) {
            if (!cache.containsKey(key)) return -1;
            recordAccess(key);
            return cache.get(key);
        }
        public void put(int key, int value) {
            if (cache.containsKey(key)) { cache.put(key, value); recordAccess(key); return; }
            if (cache.size() >= capacity) evict();
            cache.put(key, value);
            recordAccess(key);
        }
        private void recordAccess(int key) {
            history.computeIfAbsent(key, x -> new LinkedList<>()).addLast(++timestamp);
            if (history.get(key).size() > k) history.get(key).removeFirst();
        }
        private void evict() {
            int victim = -1; long oldestKth = Long.MAX_VALUE;
            for (int key : cache.keySet()) {
                LinkedList<Long> h = history.get(key);
                long kthAccess = h.size() < k ? Long.MIN_VALUE : h.getFirst();
                if (kthAccess < oldestKth || (kthAccess == oldestKth && victim == -1)) { oldestKth = kthAccess; victim = key; }
            }
            cache.remove(victim); history.remove(victim);
        }
    }

    // ======================= HARD 3: Thread-Safe LRU Cache =======================
    /** Collections.synchronizedMap wrapping access-order LinkedHashMap for thread-safe LRU. O(1) ops with locking. */
    static class ConcurrentLRUCache<K, V> {
        private final int capacity;
        private final Map<K, V> map;
        ConcurrentLRUCache(int capacity) {
            this.capacity = capacity;
            this.map = Collections.synchronizedMap(new LinkedHashMap<K, V>(16, 0.75f, true) {
                @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) { return size() > capacity; }
            });
        }
        public V get(K key) { return map.get(key); }
        public void put(K key, V value) { map.put(key, value); }
        public int size() { return map.size(); }
    }

    // ======================= HARD 4: Cache with TTL + LRU Eviction =======================
    /** Combines time-based expiration with LRU eviction when capacity exceeded. Lazy + eager expiration. */
    static class TTLLRUCache {
        private final int capacity;
        private final long ttlMs;
        private final LinkedHashMap<Integer, long[]> map; // key → [value, insertTime]
        TTLLRUCache(int capacity, long ttlMs) {
            this.capacity = capacity; this.ttlMs = ttlMs;
            map = new LinkedHashMap<>(16, 0.75f, true);
        }
        public int get(int key) {
            if (!map.containsKey(key)) return -1;
            long[] entry = map.get(key);
            if (System.currentTimeMillis() - entry[1] > ttlMs) { map.remove(key); return -1; }
            return (int) entry[0];
        }
        public void put(int key, int value) {
            evictExpired();
            if (!map.containsKey(key) && map.size() >= capacity) {
                int oldest = map.keySet().iterator().next();
                map.remove(oldest);
            }
            map.put(key, new long[]{value, System.currentTimeMillis()});
        }
        private void evictExpired() {
            long now = System.currentTimeMillis();
            map.entrySet().removeIf(e -> now - e.getValue()[1] > ttlMs);
        }
    }

    // ======================= HARD 5: CDN Simulator (Geo-Based Caching) =======================
    /** Per-region edge caches with origin fallback; simulates CDN cache hierarchy and invalidation. */
    static class CDNSimulator {
        private final Map<String, LRUCache<String, String>> edgeCaches = new HashMap<>();
        private final Map<String, String> origin = new HashMap<>();
        CDNSimulator(int edgeCacheSize, String... regions) {
            for (String r : regions) edgeCaches.put(r, new LRUCache<>(edgeCacheSize));
        }
        public void publishToOrigin(String key, String value) { origin.put(key, value); }
        public String fetch(String region, String key) {
            LRUCache<String, String> edge = edgeCaches.get(region);
            if (edge == null) return null;
            String val = edge.get(key);
            if (val != null) return "[edge-" + region + " hit] " + val;
            val = origin.get(key);
            if (val != null) edge.put(key, val);
            return val != null ? "[origin → cached at " + region + "] " + val : null;
        }
        public void invalidateAll(String key) { for (LRUCache<String, String> e : edgeCaches.values()) e.map.remove(key); }
    }

    // ======================= HARD 6: Database Query Cache with Invalidation =======================
    /** Caches SQL query results; invalidates all queries touching a mutated table. O(1) lookup, O(q) invalidation. */
    static class QueryCache {
        private final Map<String, String> cache = new LinkedHashMap<>();
        private final Map<String, Set<String>> tableToQueries = new HashMap<>();
        private final int maxEntries;
        QueryCache(int maxEntries) { this.maxEntries = maxEntries; }
        public void cacheResult(String query, String result, String... tables) {
            if (cache.size() >= maxEntries) { String old = cache.keySet().iterator().next(); removeQuery(old); }
            cache.put(query, result);
            for (String t : tables) tableToQueries.computeIfAbsent(t, k -> new HashSet<>()).add(query);
        }
        public String getResult(String query) { return cache.get(query); }
        public int invalidateByTable(String table) {
            Set<String> queries = tableToQueries.remove(table);
            if (queries == null) return 0;
            for (String q : queries) cache.remove(q);
            return queries.size();
        }
        private void removeQuery(String query) {
            cache.remove(query);
            for (Set<String> qs : tableToQueries.values()) qs.remove(query);
        }
    }

    // ======================= HARD 7: Sliding Window Rate Limiter (Token Bucket) =======================
    /** Token bucket: tokens refill over time at a fixed rate; request consumes one token. Thread-safe. */
    static class TokenBucketRateLimiter {
        private final int maxTokens;
        private final double refillRate;
        private double tokens;
        private long lastRefillTime;
        TokenBucketRateLimiter(int maxTokens, double tokensPerSecond) {
            this.maxTokens = maxTokens; this.refillRate = tokensPerSecond;
            this.tokens = maxTokens; this.lastRefillTime = System.nanoTime();
        }
        public synchronized boolean tryAcquire() {
            refill();
            if (tokens >= 1) { tokens--; return true; }
            return false;
        }
        private void refill() {
            long now = System.nanoTime();
            double elapsed = (now - lastRefillTime) / 1_000_000_000.0;
            tokens = Math.min(maxTokens, tokens + elapsed * refillRate);
            lastRefillTime = now;
        }
    }

    // ======================= HARD 8: Cuckoo Hashing (Fast Cache Lookup) =======================
    /** Two hash tables with displacement on collision; O(1) worst-case lookup, amortized O(1) insert. */
    static class CuckooHash {
        private final int capacity;
        private final int[] table1, table2;
        private static final int EMPTY = Integer.MIN_VALUE;
        private static final int MAX_KICKS = 500;
        CuckooHash(int capacity) {
            this.capacity = capacity;
            table1 = new int[capacity]; table2 = new int[capacity];
            Arrays.fill(table1, EMPTY); Arrays.fill(table2, EMPTY);
        }
        public boolean insert(int key) {
            if (lookup(key)) return true;
            int cur = key;
            for (int i = 0; i < MAX_KICKS; i++) {
                int pos1 = h1(cur);
                if (table1[pos1] == EMPTY) { table1[pos1] = cur; return true; }
                int tmp = table1[pos1]; table1[pos1] = cur; cur = tmp;
                int pos2 = h2(cur);
                if (table2[pos2] == EMPTY) { table2[pos2] = cur; return true; }
                tmp = table2[pos2]; table2[pos2] = cur; cur = tmp;
            }
            return false; // needs rehash
        }
        public boolean lookup(int key) { return table1[h1(key)] == key || table2[h2(key)] == key; }
        private int h1(int key) { return Math.abs(key * 2654435761L > 0 ? (int)((key * 2654435761L) % capacity) : (int)((-key * 2654435761L) % capacity)); }
        private int h2(int key) { return Math.abs((key * 40503) % capacity); }
    }

    // ======================= HARD 9: Distributed Cache Partitioning =======================
    /** Shards data across N partitions by key hash; each partition is an independent HashMap. */
    static class PartitionedCache {
        private final int numPartitions;
        private final List<Map<String, String>> partitions;
        PartitionedCache(int numPartitions) {
            this.numPartitions = numPartitions;
            partitions = new ArrayList<>();
            for (int i = 0; i < numPartitions; i++) partitions.add(new HashMap<>());
        }
        public void put(String key, String value) { getPartition(key).put(key, value); }
        public String get(String key) { return getPartition(key).get(key); }
        public int getPartitionIndex(String key) { return Math.abs(key.hashCode()) % numPartitions; }
        private Map<String, String> getPartition(String key) { return partitions.get(getPartitionIndex(key)); }
        public String stats() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numPartitions; i++) sb.append("P").append(i).append("=").append(partitions.get(i).size()).append(" ");
            return sb.toString().trim();
        }
    }

    // ======================= HARD 10: Auto-Warming Cache =======================
    /** Pre-populates cache with frequently accessed keys (above threshold) using access frequency tracking. */
    static class AutoWarmingCache<K, V> {
        private final Map<K, V> cache;
        private final Map<K, Integer> frequency = new HashMap<>();
        private final Function<K, V> loader;
        private final int capacity, warmThreshold;
        AutoWarmingCache(int capacity, int warmThreshold, Function<K, V> loader) {
            this.capacity = capacity; this.warmThreshold = warmThreshold; this.loader = loader;
            this.cache = new LinkedHashMap<>(16, 0.75f, true) {
                @Override protected boolean removeEldestEntry(Map.Entry<K, V> e) { return size() > capacity; }
            };
        }
        public V get(K key) {
            frequency.merge(key, 1, Integer::sum);
            V val = cache.get(key);
            if (val != null) return val;
            val = loader.apply(key);
            if (val != null) cache.put(key, val);
            return val;
        }
        public void warmUp() {
            frequency.entrySet().stream()
                .filter(e -> e.getValue() >= warmThreshold)
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(capacity)
                .forEach(e -> { V val = loader.apply(e.getKey()); if (val != null) cache.put(e.getKey(), val); });
        }
        public int cacheSize() { return cache.size(); }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== CACHING STRATEGIES (30 Examples) ===\n");

        System.out.println("--- EASY ---");
        // 1. Simple Cache
        SimpleCache<String, Integer> sc = new SimpleCache<>(); sc.put("a", 1); sc.put("b", 2);
        System.out.println("1. Simple Cache: get(a)=" + sc.get("a") + " size=" + sc.size());

        // 2. FIFO Cache
        FIFOCache<String, Integer> fifo = new FIFOCache<>(3);
        fifo.put("a", 1); fifo.put("b", 2); fifo.put("c", 3); fifo.put("d", 4);
        System.out.println("2. FIFO Cache: " + fifo + " (a evicted)");

        // 3. LRU Cache
        LRUCache<Integer, String> lru = new LRUCache<>(3);
        lru.put(1, "one"); lru.put(2, "two"); lru.put(3, "three"); lru.get(1); lru.put(4, "four");
        System.out.println("3. LRU Cache: " + lru + " (2 evicted, 1 was accessed)");

        // 4. Random Eviction
        RandomEvictionCache<String, Integer> rec = new RandomEvictionCache<>(3);
        rec.put("a", 1); rec.put("b", 2); rec.put("c", 3); rec.put("d", 4);
        System.out.println("4. Random Eviction: " + rec);

        // 5. Write-Through
        WriteThroughCache wtc = new WriteThroughCache(); wtc.put("user:1", "Alice");
        System.out.println("5. Write-Through: cache=" + wtc.inCache("user:1") + " db=" + wtc.getFromDB("user:1"));

        // 6. Cache-Aside
        Map<String, String> db = new HashMap<>(); db.put("k1", "v1"); db.put("k2", "v2");
        CacheAside ca = new CacheAside(db);
        System.out.println("6. Cache-Aside: " + ca.get("k1") + " → " + ca.get("k1"));

        // 7. TTL Cache
        TTLCache<String, String> ttl = new TTLCache<>(50);
        ttl.put("temp", "data");
        System.out.println("7. TTL Cache: " + ttl.get("temp") + " (alive before expiry)");

        // 8. Instrumented Cache
        InstrumentedCache<String, Integer> ic = new InstrumentedCache<>(); ic.put("x", 10);
        ic.get("x"); ic.get("x"); ic.get("y");
        System.out.println("8. Hit/Miss: " + ic.stats());

        // 9. Memoizer
        Memoizer<Integer, Integer> memo = new Memoizer<>(x -> x * x);
        System.out.println("9. Memoizer: square(5)=" + memo.compute(5) + " cache=" + memo.cacheSize());

        // 10. Fibonacci Memo
        System.out.println("10. Fib Memo: fib(40)=" + fibMemo(40));

        System.out.println("\n--- MEDIUM ---");
        // 11. LFU Cache
        LFUCache lfu = new LFUCache(3);
        lfu.put(1, 10); lfu.put(2, 20); lfu.put(3, 30); lfu.get(1); lfu.get(1); lfu.get(2); lfu.put(4, 40);
        System.out.println("11. LFU Cache: get(3)=" + lfu.get(3) + " (evicted, least freq)");

        // 12. Write-Back
        WriteBackCache wb = new WriteBackCache(); wb.put("x", "100"); wb.put("y", "200");
        System.out.println("12. Write-Back: dirty=" + wb.dirtyCount() + " db(x)=" + wb.getFromDB("x"));
        wb.flush();
        System.out.println("    After flush: db(x)=" + wb.getFromDB("x"));

        // 13. Write-Around
        WriteAroundCache wa = new WriteAroundCache(); wa.put("k", "val");
        System.out.println("13. Write-Around: " + wa.get("k") + " → " + wa.get("k"));

        // 14. Read-Through
        Map<String, String> dataSource = new HashMap<>(); dataSource.put("id:1", "Alice"); dataSource.put("id:2", "Bob");
        ReadThroughCache<String, String> rtc = new ReadThroughCache<>(10, dataSource::get);
        System.out.println("14. Read-Through: " + rtc.get("id:1") + " cached=" + rtc.cacheSize());

        // 15. Two-Level Cache
        TwoLevelCache<String, String> twoLevel = new TwoLevelCache<>(2, 5);
        twoLevel.put("a", "1"); twoLevel.put("b", "2"); twoLevel.put("c", "3");
        System.out.println("15. Two-Level: L1 get(a)=" + twoLevel.get("a") + " (promoted from L2 if evicted)");

        // 16. LRU Manual DLL
        LRUCacheManual lruManual = new LRUCacheManual(3);
        lruManual.put(1, 10); lruManual.put(2, 20); lruManual.put(3, 30); lruManual.get(1); lruManual.put(4, 40);
        System.out.println("16. LRU Manual: " + lruManual);

        // 17. Bloom Filter
        BloomFilter bf = new BloomFilter(1000, 3);
        bf.add("hello"); bf.add("world");
        System.out.println("17. Bloom Filter: hello=" + bf.mightContain("hello") + " xyz=" + bf.mightContain("xyz"));

        // 18. Refresh-Ahead
        RefreshAheadCache<String, String> rac = new RefreshAheadCache<>(5000, k -> "fresh-" + k);
        System.out.println("18. Refresh-Ahead: " + rac.get("key1"));

        // 19. Consistent Hashing
        ConsistentHashRing ring = new ConsistentHashRing(100);
        ring.addNode("server-A"); ring.addNode("server-B"); ring.addNode("server-C");
        System.out.println("19. Consistent Hash: user:1→" + ring.getNode("user:1") + " user:2→" + ring.getNode("user:2"));

        // 20. Bounded Buffer
        BoundedBuffer<String> buf = new BoundedBuffer<>(3);
        buf.offer("task1"); buf.offer("task2"); buf.offer("task3");
        System.out.println("20. Bounded Buffer: full=" + buf.isFull() + " poll=" + buf.poll() + " size=" + buf.size());

        System.out.println("\n--- HARD ---");
        // 21. LFU Optimal
        LFUCacheOptimal lfuOpt = new LFUCacheOptimal(2);
        lfuOpt.put(1, 1); lfuOpt.put(2, 2); lfuOpt.get(1); lfuOpt.put(3, 3);
        System.out.println("21. LFU Optimal: get(2)=" + lfuOpt.get(2) + " get(3)=" + lfuOpt.get(3));

        // 22. LRU-K
        LRUKCache lruk = new LRUKCache(3, 2);
        lruk.put(1, 10); lruk.put(2, 20); lruk.put(3, 30);
        lruk.get(1); lruk.get(1); lruk.get(2); lruk.get(2); lruk.put(4, 40);
        System.out.println("22. LRU-K: get(3)=" + lruk.get(3) + " (evicted, fewest K accesses)");

        // 23. Thread-Safe LRU
        ConcurrentLRUCache<String, String> clru = new ConcurrentLRUCache<>(3);
        clru.put("a", "1"); clru.put("b", "2"); clru.put("c", "3"); clru.put("d", "4");
        System.out.println("23. Concurrent LRU: size=" + clru.size());

        // 24. TTL + LRU
        TTLLRUCache ttlLru = new TTLLRUCache(3, 60000);
        ttlLru.put(1, 10); ttlLru.put(2, 20); ttlLru.put(3, 30);
        System.out.println("24. TTL+LRU: get(1)=" + ttlLru.get(1));

        // 25. CDN Simulator
        CDNSimulator cdn = new CDNSimulator(5, "US", "EU", "ASIA");
        cdn.publishToOrigin("page.html", "<html>Hello</html>");
        System.out.println("25. CDN: " + cdn.fetch("US", "page.html"));
        System.out.println("    CDN: " + cdn.fetch("US", "page.html"));

        // 26. Query Cache
        QueryCache qc = new QueryCache(100);
        qc.cacheResult("SELECT * FROM users", "[{id:1}]", "users");
        System.out.println("26. Query Cache: " + qc.getResult("SELECT * FROM users"));
        System.out.println("    Invalidated: " + qc.invalidateByTable("users") + " queries");

        // 27. Rate Limiter
        TokenBucketRateLimiter rl = new TokenBucketRateLimiter(5, 2);
        StringBuilder rlResult = new StringBuilder();
        for (int i = 0; i < 7; i++) rlResult.append(rl.tryAcquire() ? "OK " : "DENIED ");
        System.out.println("27. Rate Limiter: " + rlResult.toString().trim());

        // 28. Cuckoo Hash
        CuckooHash ck = new CuckooHash(101);
        ck.insert(42); ck.insert(99); ck.insert(7);
        System.out.println("28. Cuckoo Hash: 42=" + ck.lookup(42) + " 13=" + ck.lookup(13));

        // 29. Partitioned Cache
        PartitionedCache pc = new PartitionedCache(4);
        for (int i = 0; i < 20; i++) pc.put("key" + i, "val" + i);
        System.out.println("29. Partitioned: " + pc.stats());

        // 30. Auto-Warming
        Map<String, String> warmDb = new HashMap<>();
        for (int i = 0; i < 10; i++) warmDb.put("k" + i, "v" + i);
        AutoWarmingCache<String, String> awc = new AutoWarmingCache<>(5, 2, warmDb::get);
        for (int i = 0; i < 3; i++) { awc.get("k0"); awc.get("k1"); }
        awc.warmUp();
        System.out.println("30. Auto-Warming: cacheSize=" + awc.cacheSize());
    }
}

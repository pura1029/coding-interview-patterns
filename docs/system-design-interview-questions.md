# 16 System Design Interview Questions

> Detailed answers with architecture diagrams, multiple approaches with pros/cons, and Java LLD code for the most frequently asked system design interview questions.

**Reference:** Ashish Pratap Singh — *System Design Interview Handbook* (AlgoMaster.io)

---

## Quick Reference

| # | System | Key Challenges | Core Components |
|---|--------|---------------|-----------------|
| 1 | URL Shortener (TinyURL) | Hashing, read-heavy, analytics | Base62, Cache, DB |
| 2 | Chat App (WhatsApp) | Real-time, presence, E2E encryption | WebSocket, Queue, Cassandra |
| 3 | Social Media (Instagram) | News feed, fan-out, media storage | CDN, Fan-out, Timeline Cache |
| 4 | Video Streaming (YouTube) | Transcoding, adaptive bitrate, CDN | Chunked upload, HLS, CDN |
| 5 | E-Commerce (Amazon) | Inventory, cart, payment, search | Saga, Elasticsearch, Redis |
| 6 | Ride-Sharing (Uber) | Geospatial, real-time matching, ETA | Geohash, WebSocket, Redis |
| 7 | File Storage (Google Drive) | Sync, chunking, conflict resolution | CDC, Chunking, Metadata DB |
| 8 | Web Crawler | Politeness, dedup, prioritization | URL Frontier, Bloom Filter |
| 9 | Notification System | Multi-channel, priority, reliability | Priority Queue, Templates |
| 10 | Logging & Monitoring | High throughput, search, alerting | Kafka, Elasticsearch, Grafana |
| 11 | Train Ticketing (IRCTC) | Tatkal surge, seat allocation, waitlist | Queue, Redis Lock, Kafka |
| 12 | Bus Ticketing (RedBus) | Dynamic inventory, multi-operator, seat map | Aggregator, Optimistic Lock |
| 13 | Movie Ticketing (BookMyShow) | Seat locking, concurrent booking, surge | Redis TTL Lock, Event-Driven |
| 14 | Hotel Booking (MakeMyTrip) | Availability calendar, overbooking, pricing | Calendar Index, Rate Engine |
| 15 | Rate Limiting System | Distributed counters, algorithm choice, fairness | Token Bucket, Redis, Lua |
| 16 | Distributed Job Scheduler | Retries, failure detection, cancellation, scaling | Kafka, Docker, Redis, Cassandra |

---

## Q1: Design a URL Shortener (TinyURL)

### Requirements

```
Functional:
  • Generate unique short URL for a long URL
  • Redirect short URL → original URL
  • Custom short URLs (optional)
  • Link expiration
  • Analytics (click count, referrer)

Non-Functional:
  • 99.9% availability
  • Low latency (< 50ms redirect)
  • Scale: 100M URLs/day = ~1200 writes/sec, 120K reads/sec (100:1)
  • Durability: URLs work for years
```

### Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                    URL SHORTENER ARCHITECTURE                   │
│                                                                  │
│  User: "Shorten https://very-long-url.com/path?query=value"   │
│         │                                                       │
│         ▼                                                       │
│  ┌──────────────┐                                               │
│  │  API Gateway  │ ← rate limiting, auth                       │
│  └──────┬───────┘                                               │
│         │                                                       │
│         ├── POST /api/shorten ──────────────────────┐           │
│         │                                           ▼           │
│         │                                    ┌──────────────┐   │
│         │                                    │ URL Service  │   │
│         │                                    │              │   │
│         │                                    │ 1. Generate  │   │
│         │                                    │    short ID  │   │
│         │                                    │ 2. Store     │   │
│         │                                    │    mapping   │   │
│         │                                    └──────┬───────┘   │
│         │                                           │           │
│         │                              ┌────────────┼────────┐  │
│         │                              ▼            ▼        │  │
│         │                        ┌──────────┐ ┌──────────┐   │  │
│         │                        │  Cache   │ │ Database │   │  │
│         │                        │ (Redis)  │ │(Postgres/│   │  │
│         │                        │          │ │ DynamoDB)│   │
│         │                        └──────────┘ └──────────┘   │  │
│         │                                                    │  │
│         ├── GET /abc123 (redirect) ────────────────┐         │  │
│         │                                          ▼         │  │
│         │                                    ┌──────────┐    │  │
│         │                                    │  Cache   │    │  │
│         │                                    │  HIT? ──►│ return│
│         │                                    │  MISS?──►│ DB   │
│         │                                    └──────────┘    │  │
│         │                                          │         │  │
│         ▼                                          ▼         │  │
│  HTTP 301/302 Redirect ─────────────► original URL           │  │
│                                                              │  │
│  Analytics (async):                                          │  │
│  Kafka ──► Analytics Service ──► ClickHouse (time-series)    │  │
└──────────────────────────────────────────────────────────────┘
```

### Short URL Generation

```
APPROACH 1: Base62 Encoding (most common)

  Counter/Snowflake ID → Base62 encode → short string

  Characters: [a-z, A-Z, 0-9] = 62 characters
  6 chars: 62^6 = 56.8 billion unique URLs
  7 chars: 62^7 = 3.5 trillion unique URLs

  Example:
    ID = 123456789
    Base62 = 8M0kX  (5 chars)
    Short URL = https://tiny.url/8M0kX

APPROACH 2: MD5/SHA256 Hash + Truncate

  hash(long_url) → take first 7 chars → check collision
  If collision → rehash with salt

APPROACH 3: Pre-generated Key Service

  ┌────────────────────────────────────────┐
  │ Key Generation Service (KGS)           │
  │                                         │
  │ Pre-generates millions of unique keys  │
  │ Stores in DB: keys_available table     │
  │ On request: move key to keys_used      │
  │                                         │
  │ ✅ No collision possible               │
  │ ✅ Very fast (just lookup)             │
  │ ❌ Single point of failure (mitigate   │
  │    with replicas)                       │
  └────────────────────────────────────────┘

301 vs 302 REDIRECT:
  301 (Permanent): Browser caches, fewer server hits, less analytics
  302 (Temporary): Every request hits server, better for analytics ✅
```

### Approach Comparison

```
┌─────────────────────┬──────────────┬──────────────┬─────────────────┐
│                     │ Base62       │ MD5 Hash     │ Pre-generated   │
│                     │ Encoding     │ + Truncate   │ Key Service     │
├─────────────────────┼──────────────┼──────────────┼─────────────────┤
│ Collision           │ None         │ Possible     │ None            │
│ Predictability      │ Sequential   │ Random       │ Random          │
│ Speed               │ O(1) encode  │ O(1) hash    │ O(1) lookup     │
│ Same URL = same ID? │ No           │ Yes (dedup!) │ No              │
│ Distributed safe    │ Need counter │ Stateless ✅ │ Need KGS cluster│
│                     │ coordination │              │                 │
│ Complexity          │ Low          │ Medium       │ Medium          │
├─────────────────────┼──────────────┼──────────────┼─────────────────┤
│ ✅ Pros             │ Simple, fast │ Built-in     │ Zero collision  │
│                     │ no collision │ dedup for    │ Very fast       │
│                     │              │ same URL     │ reads           │
├─────────────────────┼──────────────┼──────────────┼─────────────────┤
│ ❌ Cons             │ Counter is   │ Collision    │ KGS is SPOF     │
│                     │ single point │ handling     │ Must pre-gen    │
│                     │ (use Snowflk)│ adds latency │ enough keys     │
├─────────────────────┼──────────────┼──────────────┼─────────────────┤
│ Best for            │ Most apps ✅ │ Dedup needed │ High-throughput  │
└─────────────────────┴──────────────┴──────────────┴─────────────────┘
```

### Database Schema

```
┌──────────────────────────────────────────┐
│ urls table                                │
│                                            │
│ short_code  VARCHAR(7) PRIMARY KEY        │
│ long_url    TEXT NOT NULL                  │
│ user_id     BIGINT                        │
│ created_at  TIMESTAMP                     │
│ expires_at  TIMESTAMP (nullable)          │
│ click_count BIGINT DEFAULT 0              │
│                                            │
│ INDEX on long_url (for dedup checking)    │
│ INDEX on expires_at (for cleanup job)     │
└──────────────────────────────────────────┘
```

### API Design

```
POST /api/v1/shorten
  Request:  { "long_url": "https://...", "custom_alias": "mylink", "ttl_days": 30 }
  Response: { "short_url": "https://tiny.url/abc123", "expires_at": "..." }
  Status:   201 Created

GET /{short_code}
  Response: 302 Redirect → Location: https://original-long-url.com
  Status:   302 Found (or 301 Moved Permanently)

GET /api/v1/stats/{short_code}
  Response: { "clicks": 12345, "created_at": "...", "referrers": {...} }
  Status:   200 OK

DELETE /api/v1/urls/{short_code}
  Status:   204 No Content
```

### Low-Level Design — Java Code

```java
public class UrlShortenerService {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int SHORT_URL_LENGTH = 7;

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> cache;
    private final SnowflakeIdGenerator idGenerator;

    // Approach 1: Base62 encoding with Snowflake ID
    public String shortenUrl(String longUrl, Long userId, Integer ttlDays) {
        // Check if URL already shortened (optional dedup)
        String existing = urlRepository.findByLongUrl(longUrl);
        if (existing != null) return existing;

        long uniqueId = idGenerator.nextId();
        String shortCode = encodeBase62(uniqueId);

        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode(shortCode);
        mapping.setLongUrl(longUrl);
        mapping.setUserId(userId);
        mapping.setCreatedAt(Instant.now());
        if (ttlDays != null) {
            mapping.setExpiresAt(Instant.now().plus(ttlDays, ChronoUnit.DAYS));
        }
        urlRepository.save(mapping);
        cache.opsForValue().set("url:" + shortCode, longUrl, Duration.ofDays(30));
        return shortCode;
    }

    // Approach 2: MD5 hash with collision handling
    public String shortenUrlWithHash(String longUrl) {
        String hash = DigestUtils.md5Hex(longUrl);
        String shortCode = hash.substring(0, SHORT_URL_LENGTH);

        int attempt = 0;
        while (urlRepository.existsByShortCode(shortCode)) {
            shortCode = DigestUtils.md5Hex(longUrl + attempt).substring(0, SHORT_URL_LENGTH);
            attempt++;
        }
        // ... save mapping
        return shortCode;
    }

    public String redirect(String shortCode) {
        // Check cache first (< 1ms)
        String cached = cache.opsForValue().get("url:" + shortCode);
        if (cached != null) {
            asyncAnalytics(shortCode); // non-blocking click tracking
            return cached;
        }

        // Cache miss → query DB
        UrlMapping mapping = urlRepository.findByShortCode(shortCode);
        if (mapping == null) throw new NotFoundException("URL not found");
        if (mapping.isExpired()) throw new GoneException("URL expired");

        cache.opsForValue().set("url:" + shortCode, mapping.getLongUrl());
        asyncAnalytics(shortCode);
        return mapping.getLongUrl();
    }

    private String encodeBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(BASE62.charAt((int) (num % 62)));
            num /= 62;
        }
        while (sb.length() < SHORT_URL_LENGTH) sb.append('0');
        return sb.reverse().toString();
    }

    @Async
    private void asyncAnalytics(String shortCode) {
        kafkaTemplate.send("url-clicks", shortCode,
            new ClickEvent(shortCode, Instant.now(), getClientIp(), getUserAgent()));
    }
}
```

### Capacity Estimation

```
Assumptions:
  100M new URLs/day = ~1,200 writes/sec
  Read:Write = 100:1 → 120,000 reads/sec

Storage (5 years):
  100M/day × 365 × 5 = 182.5 billion URLs
  Each record: ~500 bytes (URL + metadata)
  Total: 182.5B × 500B = ~91 TB

Cache (80% hit rate on hot URLs):
  Top 20% of URLs = 20% of daily URLs cached
  100M × 0.2 × 500B = ~10 GB Redis (fits in memory easily)

Bandwidth:
  Reads: 120K/sec × 500B = ~60 MB/sec
  Writes: 1.2K/sec × 500B = ~0.6 MB/sec
```

---

## Q2: Design a Chat Application (WhatsApp)

### Requirements

```
Functional:
  • One-on-one and group messaging
  • Online/offline status (presence)
  • Message status: sent ✓, delivered ✓✓, read (blue ✓✓)
  • Multimedia (images, videos, documents)
  • Push notifications

Non-Functional:
  • Real-time delivery (< 100ms for online users)
  • Millions of concurrent users
  • High availability (CP for message delivery)
  • Durability (messages never lost)
```

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                   CHAT APPLICATION ARCHITECTURE                   │
│                                                                    │
│  Alice (sender)                              Bob (receiver)       │
│  ┌──────────┐                               ┌──────────┐         │
│  │ Mobile   │                               │ Mobile   │         │
│  │ App      │                               │ App      │         │
│  └────┬─────┘                               └────┬─────┘         │
│       │ WebSocket                                │ WebSocket      │
│       ▼                                          ▼                │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │                    WebSocket Gateway                      │    │
│  │        (maintains persistent connections)                │    │
│  │                                                           │    │
│  │  Connection Registry (Redis):                            │    │
│  │  { "alice": gateway-3, "bob": gateway-7 }                │    │
│  └──────────┬────────────────────────────┬──────────────────┘    │
│             │                            │                        │
│             ▼                            ▼                        │
│  ┌──────────────────┐         ┌──────────────────┐              │
│  │  Chat Service    │         │ Presence Service  │              │
│  │                  │         │                   │              │
│  │ • Route message  │         │ • Track online/   │              │
│  │ • Store in DB    │         │   offline status  │              │
│  │ • Fan-out for    │         │ • Heartbeat every │              │
│  │   group chats    │         │   30 seconds      │              │
│  │ • Delivery ack   │         │ • Pub/Sub updates │              │
│  └────────┬─────────┘         └──────────────────┘              │
│           │                                                      │
│      ┌────┴──────────────────────────┐                          │
│      ▼                               ▼                          │
│  ┌──────────────┐           ┌──────────────────┐                │
│  │  Cassandra   │           │  Message Queue   │                │
│  │  (messages)  │           │  (Kafka)         │                │
│  │              │           │                  │                │
│  │ Partition by │           │ If receiver      │                │
│  │ chat_id      │           │ offline → queue  │                │
│  │ Sort by      │           │ for later        │                │
│  │ timestamp    │           │ delivery         │                │
│  └──────────────┘           └──────────────────┘                │
│                                      │                          │
│                                      ▼                          │
│                             ┌──────────────────┐                │
│                             │ Push Notification│                │
│                             │ Service (APNs/   │                │
│                             │ FCM)             │                │
│                             └──────────────────┘                │
│                                                                  │
│  Media Flow:                                                    │
│  Upload image ──► Object Storage (S3) ──► CDN ──► receiver     │
│  Store only URL in message, not the file itself                 │
└──────────────────────────────────────────────────────────────────┘
```

### Message Flow

```
Alice sends "Hello" to Bob:

  1. Alice ──WebSocket──► Gateway-3 ──► Chat Service
  2. Chat Service:
     ├── Store message in Cassandra (status: SENT ✓)
     ├── Lookup Bob's gateway: Redis → "gateway-7"
     │
     ├── Bob ONLINE:
     │   └── Route to Gateway-7 ──WebSocket──► Bob's device
     │       Bob's device ACK → status: DELIVERED ✓✓
     │       Bob opens chat → status: READ (blue ✓✓)
     │
     └── Bob OFFLINE:
         ├── Queue message in Kafka
         ├── Send push notification (APNs/FCM)
         └── When Bob comes online → deliver queued messages

GROUP CHAT FAN-OUT:
  Alice sends to Group (100 members):
  ┌──────────────────────────────────────────────────────┐
  │ Chat Service:                                         │
  │  1. Store message once (group_id, message_id)        │
  │  2. Lookup all group members                          │
  │  3. For each member:                                  │
  │     ├── Online → route via WebSocket gateway          │
  │     └── Offline → queue + push notification           │
  │                                                       │
  │ Small groups (< 500): fan-out on write               │
  │ Large groups (> 500): fan-out on read (lazy)         │
  └──────────────────────────────────────────────────────┘
```

### Approach Comparison — Real-Time Communication

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ WebSocket        │ Long Polling     │ Server-Sent      │
│                     │                  │ (HTTP)           │ Events (SSE)     │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Connection          │ Full-duplex,     │ Client repeatedly│ Server → client  │
│                     │ persistent       │ polls server     │ only (one-way)   │
│ Latency             │ < 50ms ✅        │ 100-500ms        │ < 100ms          │
│ Scalability         │ Needs connection │ Stateless,       │ Moderate         │
│                     │ management       │ easier to scale  │                  │
│ Battery (mobile)    │ Moderate         │ High drain ❌    │ Low              │
│ Bidirectional       │ Yes ✅           │ Simulated        │ No ❌            │
│ Firewall friendly   │ May be blocked   │ Yes ✅           │ Yes ✅           │
│ Protocol overhead   │ Low (frames)     │ High (headers)   │ Low              │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ True real-time   │ Simple, works    │ Simple, auto-    │
│                     │ Low overhead     │ everywhere       │ reconnect        │
│                     │ Bi-directional   │ No infra changes │ HTTP/2 friendly  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Complex infra    │ Wastes bandwidth │ Server→client    │
│                     │ Sticky sessions  │ Not real-time    │ only             │
│                     │ Connection limits│ Server load      │ No binary data   │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Chat ✅          │ Fallback/legacy  │ Notifications,   │
│                     │ Gaming           │ systems          │ live feeds       │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Approach Comparison — Message Storage

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Cassandra        │ MongoDB          │ PostgreSQL +     │
│                     │                  │                  │ Partitioning     │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Write throughput    │ Very high ✅     │ High             │ Moderate         │
│ Read pattern        │ Range queries by │ Flexible queries │ Complex joins OK │
│                     │ partition key    │                  │                  │
│ Availability        │ AP (tunable) ✅  │ AP (replica sets)│ CP (strong)      │
│ Schema flexibility  │ Rigid partitions │ Flexible ✅      │ Strict schema    │
│ Ordering guarantee  │ Within partition │ Within shard     │ Global ✅        │
│ Scaling             │ Linear scale-out │ Sharding         │ Vertical + part. │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Write-optimized  │ Flexible schema  │ ACID, strong     │
│                     │ Linear scaling   │ Rich queries     │ consistency      │
│                     │ No single master │ Aggregation      │ Mature tooling   │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Limited queries  │ Memory hungry    │ Hard to scale    │
│                     │ Eventual consist.│ Write contention │ horizontally     │
│                     │ Compaction cost  │ at scale         │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ WhatsApp/Discord │ Slack (flexible  │ Small-scale chat │
│                     │ at scale ✅      │ threads)         │ with consistency  │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class Message {
    private String messageId;
    private String chatId;
    private String senderId;
    private String content;
    private MessageType type; // TEXT, IMAGE, VIDEO, DOCUMENT
    private MessageStatus status; // SENT, DELIVERED, READ
    private Instant timestamp;
}

public enum MessageStatus { SENT, DELIVERED, READ }
public enum MessageType { TEXT, IMAGE, VIDEO, DOCUMENT }

public class Chat {
    private String chatId;
    private ChatType chatType; // ONE_ON_ONE, GROUP
    private List<String> participants;
    private Instant createdAt;
}

// --- WebSocket Handler ---
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentMap<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    private final MessageService messageService;
    private final PresenceService presenceService;
    private final KafkaTemplate<String, MessageEvent> kafkaTemplate;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = extractUserId(session);
        activeSessions.put(userId, session);
        presenceService.setOnline(userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        String userId = extractUserId(session);
        MessageRequest request = objectMapper.readValue(textMessage.getPayload(), MessageRequest.class);

        Message message = messageService.createMessage(
            request.getChatId(), userId, request.getContent(), request.getType());

        deliverMessage(message);
    }

    private void deliverMessage(Message message) {
        Chat chat = chatService.getChat(message.getChatId());

        for (String participantId : chat.getParticipants()) {
            if (participantId.equals(message.getSenderId())) continue;

            WebSocketSession recipientSession = activeSessions.get(participantId);
            if (recipientSession != null && recipientSession.isOpen()) {
                recipientSession.sendMessage(new TextMessage(toJson(message)));
                messageService.updateStatus(message.getMessageId(), MessageStatus.DELIVERED);
            } else {
                // Offline → queue for later delivery + push notification
                kafkaTemplate.send("offline-messages", participantId, new MessageEvent(message));
                pushNotificationService.send(participantId, message.getContent());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = extractUserId(session);
        activeSessions.remove(userId);
        presenceService.setOffline(userId);
    }
}

// --- Message Service ---
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RedisTemplate<String, Object> redis;

    public Message createMessage(String chatId, String senderId, String content, MessageType type) {
        Message msg = new Message();
        msg.setMessageId(UUID.randomUUID().toString());
        msg.setChatId(chatId);
        msg.setSenderId(senderId);
        msg.setContent(content);
        msg.setType(type);
        msg.setStatus(MessageStatus.SENT);
        msg.setTimestamp(Instant.now());

        messageRepository.save(msg); // Cassandra: partition by chatId, cluster by timestamp
        cacheRecentMessage(chatId, msg);
        return msg;
    }

    public List<Message> getChatHistory(String chatId, Instant before, int limit) {
        // Try cache for recent messages
        List<Message> cached = getCachedMessages(chatId);
        if (!cached.isEmpty() && cached.get(0).getTimestamp().isBefore(before)) {
            return cached.stream().filter(m -> m.getTimestamp().isBefore(before))
                .limit(limit).collect(Collectors.toList());
        }
        return messageRepository.findByChatIdBefore(chatId, before, limit);
    }

    public void updateStatus(String messageId, MessageStatus status) {
        messageRepository.updateStatus(messageId, status);
    }

    private void cacheRecentMessage(String chatId, Message msg) {
        String key = "chat:recent:" + chatId;
        redis.opsForList().leftPush(key, msg);
        redis.opsForList().trim(key, 0, 99); // keep last 100 messages in cache
        redis.expire(key, Duration.ofHours(24));
    }
}

// --- Presence Service ---
@Service
public class PresenceService {

    private final RedisTemplate<String, String> redis;
    private static final Duration HEARTBEAT_TTL = Duration.ofSeconds(60);

    public void setOnline(String userId) {
        redis.opsForValue().set("presence:" + userId, "online", HEARTBEAT_TTL);
        publishPresenceChange(userId, true);
    }

    public void heartbeat(String userId) {
        redis.expire("presence:" + userId, HEARTBEAT_TTL);
    }

    public void setOffline(String userId) {
        redis.delete("presence:" + userId);
        publishPresenceChange(userId, false);
    }

    public boolean isOnline(String userId) {
        return redis.hasKey("presence:" + userId);
    }

    public Map<String, Boolean> getBulkPresence(List<String> userIds) {
        List<String> keys = userIds.stream().map(id -> "presence:" + id).collect(Collectors.toList());
        List<String> values = redis.opsForValue().multiGet(keys);
        Map<String, Boolean> result = new HashMap<>();
        for (int i = 0; i < userIds.size(); i++) {
            result.put(userIds.get(i), values.get(i) != null);
        }
        return result;
    }

    private void publishPresenceChange(String userId, boolean online) {
        redis.convertAndSend("presence-updates",
            new PresenceEvent(userId, online, Instant.now()));
    }
}
```

### Database Schema

```sql
-- Cassandra: messages table (partitioned by chat_id for fast range queries)
CREATE TABLE messages (
    chat_id       UUID,
    message_id    TIMEUUID,          -- time-ordered unique ID
    sender_id     UUID,
    content       TEXT,
    message_type  TEXT,              -- 'TEXT', 'IMAGE', 'VIDEO', 'DOCUMENT'
    media_url     TEXT,              -- S3 URL for media (null for text)
    status        TEXT,              -- 'SENT', 'DELIVERED', 'READ'
    created_at    TIMESTAMP,
    PRIMARY KEY (chat_id, message_id)
) WITH CLUSTERING ORDER BY (message_id DESC);
-- Query: "Get last 50 messages in chat X" → single partition scan

-- PostgreSQL: chats table
CREATE TABLE chats (
    chat_id       UUID PRIMARY KEY,
    chat_type     VARCHAR(20) NOT NULL,  -- 'ONE_ON_ONE', 'GROUP'
    group_name    VARCHAR(255),
    group_icon_url TEXT,
    created_by    UUID REFERENCES users(user_id),
    created_at    TIMESTAMP DEFAULT NOW()
);

-- PostgreSQL: chat participants (who is in which chat)
CREATE TABLE chat_participants (
    chat_id       UUID REFERENCES chats(chat_id),
    user_id       UUID REFERENCES users(user_id),
    role          VARCHAR(20) DEFAULT 'MEMBER', -- 'ADMIN', 'MEMBER'
    joined_at     TIMESTAMP DEFAULT NOW(),
    muted_until   TIMESTAMP,
    PRIMARY KEY (chat_id, user_id)
);
CREATE INDEX idx_participant_user ON chat_participants(user_id);
-- Query: "Get all chats for user X" → index scan on user_id

-- PostgreSQL: users table
CREATE TABLE users (
    user_id       UUID PRIMARY KEY,
    phone_number  VARCHAR(20) UNIQUE NOT NULL,
    display_name  VARCHAR(100),
    avatar_url    TEXT,
    status_text   VARCHAR(255),
    last_seen_at  TIMESTAMP,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- Redis: presence tracking
-- Key: "presence:{user_id}" → "online" (TTL: 60s, refreshed by heartbeat)
-- Key: "conn:{user_id}" → "gateway-7" (which WebSocket gateway server)
```

### Capacity Estimation

```
Assumptions:
  500M daily active users
  Each user sends 40 messages/day → 20B messages/day
  Average message size: 100 bytes

QPS:
  Writes: 20B / 86400 ≈ 230K messages/sec
  Reads: ~5x writes (chat history, search) ≈ 1.15M reads/sec

Storage (5 years):
  20B/day × 365 × 5 × 100B = ~3.65 PB (text only)
  With media: 10-50x → 36-180 PB
  
WebSocket connections:
  Peak concurrent: ~50M connections
  Each connection: ~10KB memory
  Total: ~500 GB RAM across gateway fleet

Media:
  10% messages have media, avg 200KB
  20B × 0.1 × 200KB/day = ~400 TB/day media uploads
```

---

## Q3: Design a Social Media Platform (Instagram)

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                    INSTAGRAM ARCHITECTURE                         │
│                                                                    │
│  ┌──────────┐        ┌──────────────┐                            │
│  │ Mobile / │───────►│ API Gateway  │                            │
│  │ Web App  │        │ (rate limit, │                            │
│  └──────────┘        │  auth, route)│                            │
│                      └──────┬───────┘                            │
│                             │                                    │
│     ┌───────────────────────┼───────────────────────┐            │
│     ▼                       ▼                       ▼            │
│ ┌──────────┐         ┌──────────┐            ┌──────────┐       │
│ │ Post     │         │ Feed     │            │ User     │       │
│ │ Service  │         │ Service  │            │ Service  │       │
│ │          │         │          │            │          │       │
│ │ • Upload │         │ • Generate│           │ • Profile│       │
│ │ • Like   │         │   timeline│           │ • Follow │       │
│ │ • Comment│         │ • Ranking │           │ • Search │       │
│ └────┬─────┘         └────┬─────┘            └────┬─────┘       │
│      │                    │                       │              │
│      ▼                    ▼                       ▼              │
│ ┌──────────┐       ┌──────────────┐        ┌──────────┐         │
│ │ Object   │       │ Timeline     │        │PostgreSQL│         │
│ │ Storage  │       │ Cache (Redis)│        │ (users,  │         │
│ │ (S3)     │       │              │        │ follows) │         │
│ │          │       │ Pre-computed │        └──────────┘         │
│ │ images,  │       │ feed per user│                             │
│ │ videos   │       └──────────────┘                             │
│ └────┬─────┘                                                    │
│      │                                                          │
│      ▼                                                          │
│ ┌──────────┐                                                    │
│ │   CDN    │  ← serve images/videos from edge                  │
│ │CloudFront│                                                    │
│ └──────────┘                                                    │
└──────────────────────────────────────────────────────────────────┘
```

### News Feed Generation — Fan-out Strategies

```
═══════════════════════════════════════════════════════════════
  THE CELEBRITY PROBLEM: Fan-out on Write vs Read
═══════════════════════════════════════════════════════════════

FAN-OUT ON WRITE (push model):
  When Alice posts, push to ALL followers' timelines immediately.

  Alice posts photo
       │
       ▼
  Feed Service:
    FOR each follower of Alice (10,000 followers):
      INSERT into follower_timeline_cache
    
  ✅ Fast reads (timeline is pre-computed)
  ❌ Slow writes for celebrities (Beyoncé: 300M followers!)
  ❌ Wasted work if followers never check their feed

FAN-OUT ON READ (pull model):
  When Bob opens app, pull posts from all people he follows.

  Bob opens Instagram
       │
       ▼
  Feed Service:
    SELECT posts FROM users WHERE user_id IN (Bob's following)
    ORDER BY timestamp DESC
    LIMIT 50

  ✅ No wasted write work
  ❌ Slow reads (must query many users in real-time)

HYBRID (Instagram's approach):
  ┌────────────────────────────────────────────────────────┐
  │ Regular users (< 10K followers): Fan-out on WRITE     │
  │   → Push to followers' caches immediately             │
  │                                                        │
  │ Celebrities (> 10K followers): Fan-out on READ        │
  │   → When user opens feed, pull celebrity posts         │
  │   → Merge with pre-computed cache                      │
  │                                                        │
  │ Feed = pre-computed_timeline + fresh_celebrity_posts   │
  │         (from cache)              (fetched on read)    │
  └────────────────────────────────────────────────────────┘
```

### Approach Comparison — News Feed Generation

```
┌─────────────────────┬─────────────────┬─────────────────┬──────────────────┐
│                     │ Fan-out on      │ Fan-out on      │ Hybrid           │
│                     │ WRITE (push)    │ READ (pull)     │ (Instagram) ✅   │
├─────────────────────┼─────────────────┼─────────────────┼──────────────────┤
│ When work happens   │ On post create  │ On feed request │ Both             │
│ Read latency        │ O(1) ✅         │ O(N following)  │ O(1) + merge     │
│ Write latency       │ O(N followers)  │ O(1) ✅         │ Depends on user  │
│ Celebrity problem   │ ❌ 100M fan-out │ ✅ Handled      │ ✅ Handled       │
│ Resource waste      │ High (inactive  │ Low             │ Low              │
│                     │ users get feed) │                 │                  │
│ Consistency         │ Eventual        │ Real-time ✅    │ Near real-time   │
│ Infrastructure      │ Heavy caching   │ Heavy compute   │ Balanced         │
├─────────────────────┼─────────────────┼─────────────────┼──────────────────┤
│ ✅ Pros             │ Fast reads      │ No wasted work  │ Best of both     │
│                     │ Pre-computed    │ Always fresh    │ Efficient for    │
│                     │ Simple queries  │ Low write cost  │ all user sizes   │
├─────────────────────┼─────────────────┼─────────────────┼──────────────────┤
│ ❌ Cons             │ Celebrity posts │ Slow reads      │ Complex logic    │
│                     │ take forever    │ High read-time  │ Threshold tuning │
│                     │ Wasted for      │ compute         │ needed           │
│                     │ inactive users  │                 │                  │
├─────────────────────┼─────────────────┼─────────────────┼──────────────────┤
│ Used by             │ Twitter (old)   │ Twitter (early) │ Instagram ✅     │
│                     │ Facebook        │                 │ Twitter (now)    │
└─────────────────────┴─────────────────┴─────────────────┴──────────────────┘
```

### Approach Comparison — Media Storage

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ S3 + CloudFront  │ GCS + Cloud CDN  │ Self-hosted      │
│                     │ (AWS)            │ (GCP)            │ (MinIO + Nginx)  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Durability          │ 11 nines ✅      │ 11 nines ✅      │ Depends on setup │
│ Global edge caching │ 400+ PoPs ✅     │ 100+ PoPs        │ Manual ❌        │
│ Cost at scale       │ $$$ at PB scale  │ $$               │ $ (HW cost)      │
│ Operational burden  │ Low (managed)    │ Low (managed)    │ High ❌          │
│ Vendor lock-in      │ AWS-specific     │ GCP-specific     │ None ✅          │
│ Image processing    │ Lambda@Edge      │ Cloud Functions  │ Custom pipeline  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Most startups ✅ │ GCP-native apps  │ On-prem/privacy  │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class Post {
    private String postId;
    private String userId;
    private String caption;
    private List<String> mediaUrls;
    private PostType type; // IMAGE, VIDEO, STORY, REEL
    private Instant createdAt;
    private int likeCount;
    private int commentCount;
}

public class FeedItem {
    private String postId;
    private String authorId;
    private double score; // ranking score for feed ordering
    private Instant publishedAt;
}

// --- Feed Service with Hybrid Fan-out ---
@Service
public class FeedService {

    private static final int CELEBRITY_THRESHOLD = 10_000;
    private static final int FEED_PAGE_SIZE = 20;

    private final RedisTemplate<String, FeedItem> feedCache;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final KafkaTemplate<String, PostEvent> kafkaTemplate;
    private final FeedRankingService rankingService;

    public void onNewPost(Post post) {
        String authorId = post.getUserId();
        long followerCount = followRepository.countFollowers(authorId);

        if (followerCount < CELEBRITY_THRESHOLD) {
            // Small creator → fan-out on write (push to followers' caches)
            kafkaTemplate.send("fan-out-write", authorId, new PostEvent(post));
        }
        // Celebrity posts are pulled at read time — no fan-out
    }

    @KafkaListener(topics = "fan-out-write")
    public void fanOutOnWrite(PostEvent event) {
        List<String> followers = followRepository.getFollowers(event.getAuthorId());

        for (String followerId : followers) {
            FeedItem item = new FeedItem(event.getPostId(), event.getAuthorId(),
                0.0, event.getCreatedAt());
            feedCache.opsForZSet().add("feed:" + followerId,
                item, event.getCreatedAt().toEpochMilli());

            // Trim cache to keep last 1000 items
            feedCache.opsForZSet().removeRange("feed:" + followerId, 0, -1001);
        }
    }

    public List<Post> getFeed(String userId, int page) {
        long start = (long) page * FEED_PAGE_SIZE;
        long end = start + FEED_PAGE_SIZE - 1;

        // Step 1: Get pre-computed feed items (from fan-out on write)
        Set<FeedItem> cachedFeed = feedCache.opsForZSet()
            .reverseRange("feed:" + userId, start, end);

        // Step 2: Merge celebrity posts (fan-out on read)
        List<String> followedCelebrities = followRepository.getFollowing(userId).stream()
            .filter(uid -> followRepository.countFollowers(uid) >= CELEBRITY_THRESHOLD)
            .collect(Collectors.toList());

        List<Post> celebrityPosts = postRepository.findRecentByUsers(followedCelebrities,
            Instant.now().minus(Duration.ofDays(2)));

        // Step 3: Merge and rank
        List<Post> allPosts = new ArrayList<>();
        allPosts.addAll(toPostList(cachedFeed));
        allPosts.addAll(celebrityPosts);

        return rankingService.rank(userId, allPosts).subList(0,
            Math.min(FEED_PAGE_SIZE, allPosts.size()));
    }
}

// --- Post Service ---
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MediaUploadService mediaUploadService;
    private final KafkaTemplate<String, PostEvent> kafkaTemplate;

    @Transactional
    public Post createPost(String userId, String caption, List<MultipartFile> mediaFiles) {
        // Upload media to S3 via pre-signed URLs
        List<String> mediaUrls = mediaFiles.stream()
            .map(file -> mediaUploadService.upload(file, userId))
            .collect(Collectors.toList());

        Post post = new Post();
        post.setPostId(UUID.randomUUID().toString());
        post.setUserId(userId);
        post.setCaption(caption);
        post.setMediaUrls(mediaUrls);
        post.setCreatedAt(Instant.now());

        postRepository.save(post);
        kafkaTemplate.send("new-posts", post.getUserId(), new PostEvent(post));
        return post;
    }

    @Transactional
    public void likePost(String userId, String postId) {
        if (likeRepository.exists(userId, postId)) {
            throw new DuplicateException("Already liked");
        }
        likeRepository.save(new Like(userId, postId, Instant.now()));
        postRepository.incrementLikeCount(postId); // atomic increment
    }
}

// --- Feed Ranking Service ---
@Service
public class FeedRankingService {

    public List<Post> rank(String userId, List<Post> posts) {
        UserInterests interests = userInterestRepository.findByUserId(userId);

        return posts.stream()
            .map(post -> {
                double score = calculateScore(post, interests);
                post.setRankScore(score);
                return post;
            })
            .sorted(Comparator.comparingDouble(Post::getRankScore).reversed())
            .collect(Collectors.toList());
    }

    private double calculateScore(Post post, UserInterests interests) {
        double recency = recencyScore(post.getCreatedAt());        // 0.0 - 1.0
        double engagement = engagementScore(post);                  // likes, comments
        double affinity = affinityScore(post.getUserId(), interests); // user-author relationship
        double contentType = contentTypeBoost(post.getType());      // Reels get boost

        // Weighted combination
        return 0.3 * recency + 0.3 * engagement + 0.25 * affinity + 0.15 * contentType;
    }
}
```

### Database Schema

```sql
-- PostgreSQL: users table
CREATE TABLE users (
    user_id       UUID PRIMARY KEY,
    username      VARCHAR(30) UNIQUE NOT NULL,
    email         VARCHAR(255) UNIQUE NOT NULL,
    display_name  VARCHAR(100),
    bio           VARCHAR(500),
    avatar_url    TEXT,
    is_verified   BOOLEAN DEFAULT FALSE,
    is_private    BOOLEAN DEFAULT FALSE,
    follower_count  BIGINT DEFAULT 0,
    following_count BIGINT DEFAULT 0,
    post_count    INT DEFAULT 0,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- PostgreSQL: follows (social graph)
CREATE TABLE follows (
    follower_id   UUID REFERENCES users(user_id),
    following_id  UUID REFERENCES users(user_id),
    created_at    TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (follower_id, following_id)
);
CREATE INDEX idx_follows_following ON follows(following_id);
-- Query: "Who does user X follow?" → scan on follower_id
-- Query: "Who follows user X?" → index on following_id

-- PostgreSQL: posts table
CREATE TABLE posts (
    post_id       UUID PRIMARY KEY,
    user_id       UUID REFERENCES users(user_id),
    caption       TEXT,
    post_type     VARCHAR(20) NOT NULL,  -- 'IMAGE', 'VIDEO', 'STORY', 'REEL'
    location      VARCHAR(255),
    like_count    BIGINT DEFAULT 0,
    comment_count BIGINT DEFAULT 0,
    created_at    TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_posts_user_time ON posts(user_id, created_at DESC);

-- PostgreSQL: post media (multiple images per post — carousel)
CREATE TABLE post_media (
    media_id      UUID PRIMARY KEY,
    post_id       UUID REFERENCES posts(post_id),
    media_url     TEXT NOT NULL,         -- S3/CDN URL
    media_type    VARCHAR(10),           -- 'IMAGE', 'VIDEO'
    display_order INT NOT NULL,
    width         INT,
    height        INT
);
CREATE INDEX idx_media_post ON post_media(post_id);

-- PostgreSQL: likes table
CREATE TABLE likes (
    user_id       UUID REFERENCES users(user_id),
    post_id       UUID REFERENCES posts(post_id),
    created_at    TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (user_id, post_id)       -- prevents duplicate likes
);
CREATE INDEX idx_likes_post ON likes(post_id);

-- PostgreSQL: comments table
CREATE TABLE comments (
    comment_id    UUID PRIMARY KEY,
    post_id       UUID REFERENCES posts(post_id),
    user_id       UUID REFERENCES users(user_id),
    parent_id     UUID REFERENCES comments(comment_id),  -- for nested replies
    content       VARCHAR(2200),
    like_count    INT DEFAULT 0,
    created_at    TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_comments_post ON comments(post_id, created_at DESC);

-- Redis: pre-computed timeline cache
-- Key: "feed:{user_id}" → Sorted Set (score = timestamp, member = post_id)
-- Key: "story:{user_id}" → List of active story post_ids (TTL: 24 hours)
```

### Capacity Estimation

```
Assumptions:
  500M DAU, each user views feed 5x/day, scrolls 20 posts each time
  50M posts/day (10% users post)

Feed reads: 500M × 5 × 20 = 50B post reads/day ≈ 580K reads/sec
Post writes: 50M/day ≈ 580 writes/sec

Storage:
  Post metadata: 50M/day × 1KB = 50GB/day → ~91 TB over 5 years
  Media: 50M posts × avg 2MB = 100TB/day → ~180 PB over 5 years

Cache:
  Timeline cache per user: ~20KB (last 500 post IDs)
  500M users × 20KB = 10 TB Redis cluster
```

---

## Q4: Design a Video Streaming Service (YouTube)

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                    VIDEO STREAMING ARCHITECTURE                   │
│                                                                    │
│  UPLOAD FLOW:                                                     │
│  ┌──────┐  chunked   ┌──────────┐  original  ┌──────────────┐   │
│  │Client│──upload───►│ Upload   │──────────►│ Object Store │   │
│  │      │            │ Service  │           │ (S3/GCS)     │   │
│  └──────┘            └────┬─────┘           └──────────────┘   │
│                           │ event                               │
│                           ▼                                     │
│                    ┌──────────────┐                              │
│                    │  Transcoding │  ← most compute-intensive   │
│                    │  Pipeline    │                              │
│                    │              │                              │
│                    │ Input: 4K raw video                        │
│                    │ Output:                                    │
│                    │  ├── 2160p (4K)  → 15 Mbps                │
│                    │  ├── 1080p (HD)  → 5 Mbps                 │
│                    │  ├── 720p       → 2.5 Mbps                │
│                    │  ├── 480p       → 1 Mbps                  │
│                    │  └── 360p       → 0.5 Mbps                │
│                    │                                            │
│                    │ Format: HLS (.m3u8 + .ts segments)         │
│                    │ Each resolution split into 2-10s segments │
│                    └──────┬───────┘                              │
│                           │ transcoded segments                 │
│                           ▼                                     │
│                    ┌──────────────┐                              │
│                    │  CDN (edge)  │                              │
│                    │              │                              │
│                    │ Cached close │                              │
│                    │ to viewers   │                              │
│                    └──────────────┘                              │
│                                                                  │
│  STREAMING FLOW (Adaptive Bitrate):                             │
│  ┌──────┐  GET .m3u8   ┌─────┐  stream segments  ┌──────┐     │
│  │Viewer│─────────────►│ CDN │◄───────────────────│Origin│     │
│  │      │◄─────────────│     │                    │      │     │
│  │      │  .ts chunks  │     │                    │      │     │
│  └──────┘              └─────┘                    └──────┘     │
│                                                                  │
│  Client detects bandwidth:                                      │
│    Fast WiFi → request 1080p segments                           │
│    Slow 3G  → switch to 360p segments                           │
│    Bandwidth improves → switch back to 720p                     │
│  Seamless quality adjustment! ✅                                │
└──────────────────────────────────────────────────────────────────┘
```

### Video Processing Pipeline

```
Raw Upload (4K, 10GB)
       │
       ▼
┌──────────────────┐
│ 1. VALIDATE      │ Check format, size, content policy
└────────┬─────────┘
         ▼
┌──────────────────┐
│ 2. CHUNK         │ Split into 10-second segments
└────────┬─────────┘
         ▼
┌──────────────────┐
│ 3. TRANSCODE     │ Parallel: each segment → 5 resolutions
│ (FFmpeg workers) │ Distributed across 100s of workers
│                  │ Time: 1 hour video ≈ 5-15 min processing
└────────┬─────────┘
         ▼
┌──────────────────┐
│ 4. GENERATE      │ Thumbnails, preview sprites, subtitles
│    METADATA      │ Content fingerprint (copyright check)
└────────┬─────────┘
         ▼
┌──────────────────┐
│ 5. PUBLISH       │ Upload segments to CDN
│                  │ Update DB: video status = READY
│                  │ Notify uploader
└──────────────────┘
```

### Approach Comparison — Video Transcoding

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ On-demand        │ Pre-transcode    │ Hybrid           │
│                     │ Transcoding      │ All Resolutions  │ (YouTube) ✅     │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Upload latency      │ Low (store raw)  │ High (transcode  │ Medium           │
│                     │                  │ before publish)  │                  │
│ First view delay    │ High (transcode  │ None ✅          │ Low (quick       │
│                     │ on first request)│                  │ format first)    │
│ Storage cost        │ Low (only what's │ High (all combos)│ Medium           │
│                     │ requested)       │                  │                  │
│ Compute cost        │ Unpredictable    │ Front-loaded ✅  │ Balanced         │
│ Rare resolutions    │ Saved on demand  │ Wasted compute   │ On-demand        │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Low storage cost │ Instant playback │ Best user        │
│                     │ Simple pipeline  │ Predictable cost │ experience       │
│                     │                  │ No cold start    │ Cost efficient   │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Slow first view  │ Huge storage     │ Complex logic    │
│                     │ Burst compute    │ Wasted work for  │ Priority mgmt    │
│                     │ requirements     │ unpopular videos │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Niche platforms  │ Premium (Netflix)│ YouTube ✅       │
│                     │ UGC with low     │ Known catalog    │ Large UGC        │
│                     │ view count       │                  │ platforms        │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Approach Comparison — Content Delivery

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ CDN (Pull)       │ CDN (Push)       │ P2P + CDN        │
│                     │                  │                  │ (BitTorrent-like)│
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ How it works        │ CDN fetches from │ Origin pushes to │ Viewers share    │
│                     │ origin on miss   │ CDN proactively  │ chunks with      │
│                     │                  │                  │ each other       │
│ Popular content     │ Fast after first │ Pre-warmed ✅    │ Very efficient ✅│
│ Long-tail content   │ Cold start delay │ Wastes edge      │ Few peers ❌     │
│                     │                  │ storage ❌       │                  │
│ Cost                │ Pay per transfer │ Higher storage   │ Lowest CDN cost  │
│ Control             │ Cache headers    │ Full control ✅  │ Complex ❌       │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Simple, works    │ Zero cold starts │ Saves bandwidth  │
│                     │ for most cases   │ for viral content│ at massive scale │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ First-viewer     │ High storage     │ Privacy concerns │
│                     │ latency          │ cost at edge     │ Complex protocol │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Used by             │ YouTube ✅       │ Netflix (push    │ Twitch (WebRTC)  │
│                     │ Most platforms   │ to edge)         │ Peer5            │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class Video {
    private String videoId;
    private String uploaderId;
    private String title;
    private String description;
    private VideoStatus status; // UPLOADING, PROCESSING, READY, FAILED
    private long durationMs;
    private Map<Resolution, String> streamUrls; // resolution → HLS manifest URL
    private String thumbnailUrl;
    private Instant uploadedAt;
    private long viewCount;
}

public enum Resolution { R360P, R480P, R720P, R1080P, R1440P, R2160P }
public enum VideoStatus { UPLOADING, PROCESSING, READY, FAILED }

public class VideoChunk {
    private String chunkId;
    private String videoId;
    private int sequenceNumber;
    private long sizeBytes;
    private String storageKey; // S3 object key
}

// --- Upload Service (chunked upload) ---
@Service
public class VideoUploadService {

    private final S3Client s3Client;
    private final VideoRepository videoRepository;
    private final KafkaTemplate<String, TranscodeRequest> kafkaTemplate;
    private static final long CHUNK_SIZE = 5 * 1024 * 1024; // 5MB chunks

    public UploadInitResponse initUpload(String userId, VideoMetadata metadata) {
        String videoId = UUID.randomUUID().toString();
        String uploadKey = "raw/" + videoId + "/" + metadata.getFileName();

        CreateMultipartUploadResponse s3Response = s3Client.createMultipartUpload(
            CreateMultipartUploadRequest.builder()
                .bucket("video-uploads")
                .key(uploadKey)
                .build());

        Video video = new Video();
        video.setVideoId(videoId);
        video.setUploaderId(userId);
        video.setTitle(metadata.getTitle());
        video.setStatus(VideoStatus.UPLOADING);
        video.setUploadedAt(Instant.now());
        videoRepository.save(video);

        return new UploadInitResponse(videoId, s3Response.uploadId(),
            generatePresignedUrls(uploadKey, metadata.getFileSize()));
    }

    public void completeUpload(String videoId, String uploadId, List<CompletedPart> parts) {
        s3Client.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
            .bucket("video-uploads").key("raw/" + videoId + "/*")
            .uploadId(uploadId).multipartUpload(CompletedMultipartUpload.builder()
                .parts(parts).build())
            .build());

        videoRepository.updateStatus(videoId, VideoStatus.PROCESSING);

        kafkaTemplate.send("transcode-requests", videoId,
            new TranscodeRequest(videoId, "raw/" + videoId,
                List.of(Resolution.R360P, Resolution.R720P, Resolution.R1080P)));
    }

    private List<PresignedUrl> generatePresignedUrls(String key, long fileSize) {
        int totalChunks = (int) Math.ceil((double) fileSize / CHUNK_SIZE);
        List<PresignedUrl> urls = new ArrayList<>();
        for (int i = 1; i <= totalChunks; i++) {
            urls.add(generatePresignedUrl(key, i));
        }
        return urls;
    }
}

// --- Transcoding Service ---
@Service
public class TranscodingService {

    private final VideoRepository videoRepository;
    private final S3Client s3Client;

    @KafkaListener(topics = "transcode-requests")
    public void transcode(TranscodeRequest request) {
        try {
            String rawVideoPath = downloadFromS3(request.getStorageKey());
            Map<Resolution, String> outputUrls = new HashMap<>();

            for (Resolution resolution : request.getTargetResolutions()) {
                TranscodeResult result = transcodeToResolution(rawVideoPath, resolution);

                // Create HLS segments (2-10 second .ts files + .m3u8 manifest)
                List<HlsSegment> segments = createHlsSegments(result.getOutputPath());
                String manifestUrl = uploadSegmentsToS3(request.getVideoId(),
                    resolution, segments);
                outputUrls.put(resolution, manifestUrl);
            }

            String thumbnailUrl = generateThumbnail(rawVideoPath);
            long durationMs = getVideoDuration(rawVideoPath);

            videoRepository.updateVideoReady(request.getVideoId(),
                outputUrls, thumbnailUrl, durationMs, VideoStatus.READY);

        } catch (Exception e) {
            videoRepository.updateStatus(request.getVideoId(), VideoStatus.FAILED);
            alertService.notify("Transcode failed: " + request.getVideoId(), e);
        }
    }

    private TranscodeResult transcodeToResolution(String inputPath, Resolution resolution) {
        // Uses FFmpeg under the hood
        // ffmpeg -i input.mp4 -vf scale=-2:720 -c:v h264 -preset fast output_720p.mp4
        return ffmpegRunner.transcode(inputPath, resolution.getHeight(),
            resolution.getBitrate());
    }

    private List<HlsSegment> createHlsSegments(String videoPath) {
        // Split into 6-second segments for adaptive bitrate streaming
        // ffmpeg -i input.mp4 -hls_time 6 -hls_playlist_type vod output.m3u8
        return ffmpegRunner.segment(videoPath, 6);
    }
}

// --- Streaming Service (Adaptive Bitrate) ---
@RestController
@RequestMapping("/api/v1/stream")
public class StreamingController {

    private final VideoRepository videoRepository;
    private final ViewCountService viewCountService;

    @GetMapping("/{videoId}/manifest")
    public ResponseEntity<String> getManifest(@PathVariable String videoId) {
        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new NotFoundException("Video not found"));

        // Return master HLS manifest with all available resolutions
        String masterManifest = buildMasterManifest(video.getStreamUrls());
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
            .body(masterManifest);
    }

    @GetMapping("/{videoId}/view")
    public void recordView(@PathVariable String videoId, @RequestParam long watchTimeMs) {
        if (watchTimeMs >= 30_000) { // count as view only if watched > 30 seconds
            viewCountService.incrementView(videoId);
        }
    }

    private String buildMasterManifest(Map<Resolution, String> urls) {
        StringBuilder sb = new StringBuilder("#EXTM3U\n");
        for (Map.Entry<Resolution, String> entry : urls.entrySet()) {
            Resolution res = entry.getKey();
            sb.append("#EXT-X-STREAM-INF:BANDWIDTH=").append(res.getBitrate())
              .append(",RESOLUTION=").append(res.getWidth()).append("x").append(res.getHeight())
              .append("\n").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
```

### Database Schema

```sql
-- PostgreSQL: videos table
CREATE TABLE videos (
    video_id      UUID PRIMARY KEY,
    uploader_id   UUID REFERENCES users(user_id),
    title         VARCHAR(500) NOT NULL,
    description   TEXT,
    status        VARCHAR(20) DEFAULT 'UPLOADING',  -- UPLOADING, PROCESSING, READY, FAILED
    duration_ms   BIGINT,
    thumbnail_url TEXT,
    original_key  TEXT,                -- S3 key for raw upload
    view_count    BIGINT DEFAULT 0,
    like_count    BIGINT DEFAULT 0,
    dislike_count BIGINT DEFAULT 0,
    comment_count BIGINT DEFAULT 0,
    is_public     BOOLEAN DEFAULT TRUE,
    language      VARCHAR(10),
    uploaded_at   TIMESTAMP DEFAULT NOW(),
    published_at  TIMESTAMP
);
CREATE INDEX idx_videos_uploader ON videos(uploader_id, uploaded_at DESC);
CREATE INDEX idx_videos_published ON videos(published_at DESC) WHERE status = 'READY';

-- PostgreSQL: video_streams (one per resolution per video)
CREATE TABLE video_streams (
    stream_id     UUID PRIMARY KEY,
    video_id      UUID REFERENCES videos(video_id),
    resolution    VARCHAR(10) NOT NULL,   -- '360p', '720p', '1080p', '2160p'
    bitrate_kbps  INT,
    manifest_url  TEXT NOT NULL,          -- HLS .m3u8 URL on CDN
    codec         VARCHAR(20) DEFAULT 'h264',
    created_at    TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_streams_video ON video_streams(video_id);

-- PostgreSQL: channels (content creators)
CREATE TABLE channels (
    channel_id    UUID PRIMARY KEY,
    user_id       UUID UNIQUE REFERENCES users(user_id),
    channel_name  VARCHAR(100) NOT NULL,
    description   TEXT,
    banner_url    TEXT,
    subscriber_count BIGINT DEFAULT 0,
    total_views   BIGINT DEFAULT 0,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- PostgreSQL: subscriptions
CREATE TABLE subscriptions (
    subscriber_id UUID REFERENCES users(user_id),
    channel_id    UUID REFERENCES channels(channel_id),
    notification  BOOLEAN DEFAULT TRUE,  -- bell icon on/off
    subscribed_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (subscriber_id, channel_id)
);
CREATE INDEX idx_sub_channel ON subscriptions(channel_id);

-- PostgreSQL: comments
CREATE TABLE video_comments (
    comment_id    UUID PRIMARY KEY,
    video_id      UUID REFERENCES videos(video_id),
    user_id       UUID REFERENCES users(user_id),
    parent_id     UUID REFERENCES video_comments(comment_id),
    content       VARCHAR(10000),
    like_count    INT DEFAULT 0,
    created_at    TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_vcomments_video ON video_comments(video_id, created_at DESC);

-- Redis: view count buffer (batch flush to DB every minute)
-- Key: "views:{video_id}" → INCR per view → flush to DB periodically
-- Key: "trending:daily" → Sorted Set (score = view_count, member = video_id)
```

### Capacity Estimation

```
Assumptions:
  1B DAU, 500M videos total, 500K new uploads/day
  Average video: 5 min, 200MB raw

Upload storage:
  500K/day × 200MB = 100 TB/day raw uploads
  Transcoded (5 resolutions): ~500 TB/day
  5-year total: ~900 PB

Streaming bandwidth:
  1B users × avg 30 min/day = 30B minutes/day
  Average bitrate: 3 Mbps → 30B × 30 × 60 × 3Mbps = ~10 Exabytes/day
  CDN absorbs 95%+ → origin serves ~500 TB/day

View count QPS:
  1B users × 5 videos/day = 5B views/day ≈ 58K writes/sec
  Batch to Redis, flush to DB every minute
```

---

## Q5: Design an E-Commerce Platform (Amazon)

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                  E-COMMERCE PLATFORM ARCHITECTURE                 │
│                                                                    │
│  ┌──────────┐       ┌──────────────┐                             │
│  │ Web/     │──────►│ API Gateway  │                             │
│  │ Mobile   │       │ + CDN (static│                             │
│  └──────────┘       │   assets)    │                             │
│                     └──────┬───────┘                             │
│                            │                                     │
│  ┌─────────────────────────┼─────────────────────────────┐      │
│  │                         │                             │      │
│  ▼                         ▼                             ▼      │
│ ┌──────────┐        ┌──────────┐                 ┌──────────┐  │
│ │ Product  │        │  Order   │                 │  User    │  │
│ │ Catalog  │        │ Service  │                 │ Service  │  │
│ │ Service  │        │          │                 │          │  │
│ │          │        │ • Cart   │                 │ • Auth   │  │
│ │ • Search │        │ • Checkout│                │ • Profile│  │
│ │ • Browse │        │ • Payment │                │ • Address│  │
│ │ • Filter │        │ • Track   │                └────┬─────┘  │
│ └────┬─────┘        └────┬─────┘                     │        │
│      │                   │                            │        │
│      ▼                   ▼                            ▼        │
│ ┌──────────┐      ┌──────────────┐            ┌──────────┐    │
│ │Elastic-  │      │ PostgreSQL   │            │PostgreSQL│    │
│ │search    │      │ (orders,     │            │ (users)  │    │
│ │(product  │      │  payments)   │            └──────────┘    │
│ │ search)  │      └──────────────┘                            │
│ └──────────┘              │                                   │
│                           │ events                            │
│                           ▼                                   │
│                    ┌──────────────┐                            │
│                    │    Kafka     │                            │
│                    │              │                            │
│                    │ Order events │───►  Inventory Service     │
│                    │              │───►  Notification Service  │
│                    │              │───►  Analytics Service     │
│                    │              │───►  Recommendation Engine │
│                    └──────────────┘                            │
│                                                                │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ INVENTORY — the hardest part:                            │  │
│  │                                                           │  │
│  │ Problem: Last iPhone in stock, 2 users click "Buy Now"  │  │
│  │                                                           │  │
│  │ Solution: Optimistic Locking + Reservation               │  │
│  │   1. User clicks "Add to Cart" → RESERVE item (15 min)  │  │
│  │   2. If no payment in 15 min → release reservation      │  │
│  │   3. On payment → CONFIRM reservation                    │  │
│  │   4. UPDATE inventory SET qty = qty - 1                  │  │
│  │      WHERE product_id = X AND qty > 0  (atomic!)        │  │
│  └──────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
```

### Payment Flow (Saga Pattern)

```
Order checkout → multi-step distributed transaction:

  ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
  │1. Reserve│────►│2. Charge │────►│3. Confirm│────►│4. Ship   │
  │ Inventory│     │ Payment  │     │  Order   │     │ Order    │
  └──────────┘     └──────────┘     └──────────┘     └──────────┘

  If Step 2 (payment) fails:
    Compensate Step 1: Release inventory reservation

  If Step 3 fails:
    Compensate Step 2: Refund payment
    Compensate Step 1: Release inventory

  Each step is idempotent. Saga log tracks progress.
```

### Approach Comparison — Distributed Transaction

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Saga (Choreog.)  │ Saga (Orchestr.) │ Two-Phase Commit │
│                     │ Event-driven     │ Central coord.   │ (2PC)            │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Coordination        │ Decentralized    │ Central          │ Central          │
│                     │ (events)         │ (orchestrator)   │ (coordinator)    │
│ Coupling            │ Loose ✅         │ Moderate         │ Tight ❌         │
│ Consistency         │ Eventual         │ Eventual         │ Strong ✅        │
│ Rollback            │ Compensating     │ Compensating     │ Abort (native)   │
│ Complexity          │ Event spaghetti  │ Manageable ✅    │ Low but fragile  │
│ Performance         │ Non-blocking ✅  │ Non-blocking     │ Blocking ❌      │
│ Failure handling    │ Hard to debug    │ Centralized ✅   │ Coordinator SPOF │
│ Availability        │ High ✅          │ High             │ Low (locks held) │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ No SPOF, loose   │ Clear flow,      │ Strong           │
│                     │ coupling, easy   │ easy to debug,   │ consistency,     │
│                     │ to add services  │ central logging  │ simple mental    │
│                     │                  │                  │ model            │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Hard to trace    │ Orchestrator is  │ Blocks resources │
│                     │ flow, cyclic     │ potential        │ Low availability │
│                     │ dependencies     │ bottleneck       │ Doesn't scale    │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Simple 2-3 step  │ Complex checkout │ Monolith or      │
│                     │ workflows        │ flows ✅         │ single-DB apps   │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Approach Comparison — Product Search

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Elasticsearch    │ PostgreSQL FTS   │ Solr             │
│                     │                  │ (tsvector)       │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Full-text search    │ Excellent ✅     │ Good             │ Excellent ✅     │
│ Faceted search      │ Native ✅        │ Manual ❌        │ Native ✅        │
│ Relevance tuning    │ Flexible ✅      │ Basic            │ Flexible         │
│ Real-time indexing  │ Near real-time   │ Immediate ✅     │ Near real-time   │
│ Scaling             │ Horizontal ✅    │ Vertical         │ Horizontal       │
│ Autocomplete        │ Built-in ✅      │ Like/trigram     │ Built-in         │
│ Operational cost    │ High (cluster)   │ Low (same DB) ✅ │ High (ZooKeeper) │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Amazon-scale ✅  │ Small catalogs   │ Enterprise search│
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class Product {
    private String productId;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private String categoryId;
    private List<String> imageUrls;
    private double rating;
    private int reviewCount;
    private ProductStatus status;
}

public class Order {
    private String orderId;
    private String userId;
    private List<OrderItem> items;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String shippingAddressId;
    private PaymentInfo paymentInfo;
    private Instant createdAt;
}

public class OrderItem {
    private String productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}

public enum OrderStatus {
    CREATED, INVENTORY_RESERVED, PAYMENT_CHARGED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}

// --- Shopping Cart Service ---
@Service
public class CartService {

    private final RedisTemplate<String, CartItem> redisTemplate;
    private final ProductService productService;
    private static final Duration CART_TTL = Duration.ofDays(30);

    public Cart getCart(String userId) {
        String key = "cart:" + userId;
        Map<Object, Object> items = redisTemplate.opsForHash().entries(key);

        List<CartItem> cartItems = items.values().stream()
            .map(item -> (CartItem) item)
            .collect(Collectors.toList());

        BigDecimal total = cartItems.stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Cart(userId, cartItems, total);
    }

    public void addItem(String userId, String productId, int quantity) {
        Product product = productService.getProduct(productId);
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Only " + product.getStockQuantity() + " left");
        }

        String key = "cart:" + userId;
        CartItem existing = (CartItem) redisTemplate.opsForHash().get(key, productId);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            redisTemplate.opsForHash().put(key, productId, existing);
        } else {
            CartItem item = new CartItem(productId, product.getName(),
                quantity, product.getPrice());
            redisTemplate.opsForHash().put(key, productId, item);
        }
        redisTemplate.expire(key, CART_TTL);
    }

    public void removeItem(String userId, String productId) {
        redisTemplate.opsForHash().delete("cart:" + userId, productId);
    }
}

// --- Order Service with Saga Orchestration ---
@Service
public class OrderSagaOrchestrator {

    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final CartService cartService;

    @Transactional
    public Order checkout(String userId, String addressId, PaymentMethod paymentMethod) {
        Cart cart = cartService.getCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }

        Order order = createOrder(userId, cart, addressId);
        String sagaId = UUID.randomUUID().toString();

        try {
            // Step 1: Reserve Inventory
            order.setStatus(OrderStatus.CREATED);
            orderRepository.save(order);

            List<InventoryReservation> reservations =
                inventoryService.reserveAll(order.getItems(), sagaId);
            order.setStatus(OrderStatus.INVENTORY_RESERVED);
            orderRepository.save(order);

            // Step 2: Charge Payment
            PaymentResult paymentResult = paymentService.charge(
                userId, order.getTotalAmount(), paymentMethod, order.getOrderId());
            order.setStatus(OrderStatus.PAYMENT_CHARGED);
            order.setPaymentInfo(paymentResult.toPaymentInfo());
            orderRepository.save(order);

            // Step 3: Confirm Order
            inventoryService.confirmReservations(reservations);
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);

            cartService.clearCart(userId);
            notificationService.sendOrderConfirmation(userId, order);

            return order;

        } catch (InsufficientStockException e) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            throw e;

        } catch (PaymentDeclinedException e) {
            // Compensate: release inventory
            inventoryService.releaseReservation(sagaId);
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            throw e;

        } catch (Exception e) {
            // Compensate all steps
            compensateAll(order, sagaId);
            throw new OrderFailedException("Checkout failed", e);
        }
    }

    private void compensateAll(Order order, String sagaId) {
        if (order.getStatus().ordinal() >= OrderStatus.PAYMENT_CHARGED.ordinal()) {
            paymentService.refund(order.getPaymentInfo().getTransactionId());
        }
        if (order.getStatus().ordinal() >= OrderStatus.INVENTORY_RESERVED.ordinal()) {
            inventoryService.releaseReservation(sagaId);
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}

// --- Inventory Service (Optimistic Locking) ---
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryReservation reserve(String productId, int quantity, String sagaId) {
        // Optimistic locking: WHERE qty >= requested AND version = expected
        int updated = inventoryRepository.reserveStock(productId, quantity);
        if (updated == 0) {
            throw new InsufficientStockException("Product " + productId + " out of stock");
        }

        InventoryReservation reservation = new InventoryReservation();
        reservation.setSagaId(sagaId);
        reservation.setProductId(productId);
        reservation.setQuantity(quantity);
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setExpiresAt(Instant.now().plus(Duration.ofMinutes(15)));
        return inventoryRepository.saveReservation(reservation);
    }

    // Scheduler releases expired reservations every minute
    @Scheduled(fixedRate = 60_000)
    public void releaseExpiredReservations() {
        List<InventoryReservation> expired =
            inventoryRepository.findExpiredReservations(Instant.now());

        for (InventoryReservation res : expired) {
            inventoryRepository.restoreStock(res.getProductId(), res.getQuantity());
            res.setStatus(ReservationStatus.RELEASED);
            inventoryRepository.saveReservation(res);
        }
    }
}

// --- Product Search Service ---
@Service
public class ProductSearchService {

    private final ElasticsearchClient esClient;

    public SearchResult search(String query, Map<String, List<String>> filters,
                               String sortBy, int page, int size) {

        BoolQuery.Builder boolQuery = new BoolQuery.Builder()
            .must(m -> m.multiMatch(mm -> mm
                .query(query)
                .fields("name^3", "description", "category") // name has 3x weight
                .fuzziness("AUTO") // typo tolerance
            ));

        // Apply facet filters (category, price range, rating)
        if (filters.containsKey("category")) {
            boolQuery.filter(f -> f.terms(t -> t
                .field("categoryId")
                .terms(tv -> tv.value(toFieldValues(filters.get("category"))))));
        }
        if (filters.containsKey("minPrice")) {
            boolQuery.filter(f -> f.range(r -> r
                .field("price").gte(JsonData.of(filters.get("minPrice").get(0)))));
        }

        SearchResponse<Product> response = esClient.search(s -> s
            .index("products")
            .query(q -> q.bool(boolQuery.build()))
            .from(page * size).size(size)
            .sort(buildSort(sortBy))
            .aggregations("categories", a -> a.terms(t -> t.field("categoryId")))
            .aggregations("price_ranges", a -> a.range(r -> r.field("price")
                .ranges(rr -> rr.to("25"), rr -> rr.from("25").to("100"),
                        rr -> rr.from("100")))),
            Product.class);

        return new SearchResult(response.hits().hits(), response.aggregations(),
            response.hits().total().value());
    }
}
```

### Database Schema

```sql
-- PostgreSQL: products table
CREATE TABLE products (
    product_id    UUID PRIMARY KEY,
    name          VARCHAR(500) NOT NULL,
    description   TEXT,
    price         DECIMAL(12,2) NOT NULL,
    category_id   UUID REFERENCES categories(category_id),
    brand         VARCHAR(100),
    seller_id     UUID REFERENCES sellers(seller_id),
    stock_qty     INT NOT NULL DEFAULT 0,
    rating        DECIMAL(2,1) DEFAULT 0.0,
    review_count  INT DEFAULT 0,
    status        VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, INACTIVE, OUT_OF_STOCK
    weight_grams  INT,
    created_at    TIMESTAMP DEFAULT NOW(),
    updated_at    TIMESTAMP DEFAULT NOW(),
    version       INT DEFAULT 1                   -- optimistic locking
);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_seller ON products(seller_id);

-- PostgreSQL: orders table
CREATE TABLE orders (
    order_id      UUID PRIMARY KEY,
    user_id       UUID REFERENCES users(user_id),
    status        VARCHAR(30) NOT NULL,  -- CREATED, RESERVED, PAID, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    total_amount  DECIMAL(12,2) NOT NULL,
    tax_amount    DECIMAL(10,2),
    shipping_fee  DECIMAL(10,2),
    address_id    UUID REFERENCES addresses(address_id),
    payment_id    UUID,
    saga_id       UUID,                  -- for distributed transaction tracking
    created_at    TIMESTAMP DEFAULT NOW(),
    updated_at    TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_orders_user ON orders(user_id, created_at DESC);
CREATE INDEX idx_orders_status ON orders(status) WHERE status NOT IN ('DELIVERED', 'CANCELLED');

-- PostgreSQL: order items (line items)
CREATE TABLE order_items (
    item_id       UUID PRIMARY KEY,
    order_id      UUID REFERENCES orders(order_id),
    product_id    UUID REFERENCES products(product_id),
    product_name  VARCHAR(500),         -- denormalized snapshot at order time
    unit_price    DECIMAL(12,2),        -- price at time of purchase
    quantity      INT NOT NULL,
    subtotal      DECIMAL(12,2) NOT NULL
);
CREATE INDEX idx_items_order ON order_items(order_id);

-- PostgreSQL: inventory reservations
CREATE TABLE inventory_reservations (
    reservation_id UUID PRIMARY KEY,
    saga_id       UUID NOT NULL,
    product_id    UUID REFERENCES products(product_id),
    quantity      INT NOT NULL,
    status        VARCHAR(20) NOT NULL,  -- RESERVED, CONFIRMED, RELEASED
    expires_at    TIMESTAMP NOT NULL,
    created_at    TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_reserv_saga ON inventory_reservations(saga_id);
CREATE INDEX idx_reserv_expires ON inventory_reservations(expires_at)
    WHERE status = 'RESERVED';

-- PostgreSQL: payments table
CREATE TABLE payments (
    payment_id    UUID PRIMARY KEY,
    order_id      UUID REFERENCES orders(order_id),
    user_id       UUID REFERENCES users(user_id),
    amount        DECIMAL(12,2) NOT NULL,
    currency      VARCHAR(3) DEFAULT 'USD',
    method        VARCHAR(20),           -- CREDIT_CARD, DEBIT_CARD, UPI, WALLET
    status        VARCHAR(20),           -- PENDING, CHARGED, REFUNDED, FAILED
    transaction_id VARCHAR(100),         -- payment gateway reference
    idempotency_key VARCHAR(100) UNIQUE, -- prevent duplicate charges
    created_at    TIMESTAMP DEFAULT NOW()
);

-- Redis: shopping cart
-- Key: "cart:{user_id}" → Hash { product_id: { qty, price, name } }
-- TTL: 30 days
```

### Capacity Estimation

```
Assumptions:
  300M DAU, 5M orders/day, 100M product catalog

Order writes: 5M/day ≈ 58 orders/sec (peak: 500/sec on sale days)
Product reads: 300M users × 20 page views = 6B/day ≈ 70K reads/sec
Search QPS: 300M × 5 searches = 1.5B/day ≈ 17K search/sec

Storage:
  Products: 100M × 5KB = 500 GB
  Orders (5 years): 5M/day × 365 × 5 × 2KB = ~18 TB
  Product images: 100M × 5 images × 500KB = ~250 TB

Cart (Redis):
  50M active carts × 2KB = 100 GB Redis cluster
```

---

## Q6: Design a Ride-Sharing Service (Uber)

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                   RIDE-SHARING ARCHITECTURE                       │
│                                                                    │
│  Rider App                                Driver App              │
│  ┌──────────┐                            ┌──────────┐            │
│  │ Request  │                            │ Location │            │
│  │ ride     │                            │ updates  │            │
│  └────┬─────┘                            │ every 4s │            │
│       │                                  └────┬─────┘            │
│       ▼                                       ▼                  │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │                    API Gateway                            │    │
│  └──────────┬──────────────────────────────┬────────────────┘    │
│             │                              │                      │
│             ▼                              ▼                      │
│  ┌──────────────────┐           ┌──────────────────┐             │
│  │  Ride Matching   │           │ Location Service  │             │
│  │  Service         │           │                   │             │
│  │                  │◄──query──│ • Stores driver   │             │
│  │ • Find nearest   │          │   positions in    │             │
│  │   available      │          │   geospatial      │             │
│  │   drivers        │          │   index (Redis    │             │
│  │ • Calculate ETA  │          │   + Geohash)      │             │
│  │ • Match rider    │          │ • Updates every   │             │
│  │   to driver      │          │   4 seconds       │             │
│  └────────┬─────────┘          └──────────────────┘             │
│           │                                                      │
│           ▼                                                      │
│  ┌──────────────────┐                                            │
│  │  Pricing Service │  ← dynamic/surge pricing                  │
│  │                  │                                            │
│  │ Base fare + per-mile + per-minute + surge multiplier         │
│  │                  │                                            │
│  │ Surge: demand / supply ratio per geohash zone               │
│  │  > 2.0 → 1.5x price                                        │
│  │  > 3.0 → 2.0x price                                        │
│  └──────────────────┘                                            │
│                                                                  │
│  GEOSPATIAL INDEX (finding nearby drivers):                     │
│  ┌──────────────────────────────────────────────┐               │
│  │ Geohash divides Earth into grid cells:       │               │
│  │                                               │               │
│  │  ┌────┬────┬────┐    Each cell has a code:   │               │
│  │  │ 9q │ 9r │ 9x │    "9q8yyk" = San Fran    │               │
│  │  ├────┼────┼────┤                             │               │
│  │  │ 9p │ 9q │ 9w │    Query: "Give me all    │               │
│  │  ├────┼────┼────┤     drivers in 9q8yy*"     │               │
│  │  │ 9n │ 9p │ 9t │    → O(1) lookup in Redis │               │
│  │  └────┴────┴────┘                             │               │
│  │                                               │               │
│  │  Redis: GEOADD drivers lng lat driver_id     │               │
│  │  Query: GEOSEARCH drivers lng lat BYRADIUS 5 km│              │
│  └──────────────────────────────────────────────┘               │
└──────────────────────────────────────────────────────────────────┘
```

### Approach Comparison — Geospatial Indexing

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Geohash + Redis  │ QuadTree         │ H3 (Uber's       │
│                     │                  │ (in-memory)      │ Hexagonal Grid)  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Lookup speed        │ O(1) per cell ✅ │ O(log N)         │ O(1) per cell ✅ │
│ Proximity search    │ Check adjacent   │ Natural range    │ Ring of adjacent │
│                     │ cells (max 9)    │ search ✅        │ hexagons (6) ✅  │
│ Update frequency    │ High (Redis)     │ Rebuild needed   │ High (index)     │
│ Edge distortion     │ Rectangular, has │ Adaptive depth ✅│ Equal-area ✅    │
│                     │ edge artifacts   │                  │ No distortion    │
│ Memory              │ Redis cluster    │ In-process RAM   │ Index + DB       │
│ Implementation      │ Simple ✅        │ Complex tree ops │ Uber's library   │
│ Dynamic resolution  │ Prefix-based     │ Natural ✅       │ Multiple levels  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Simple, fast     │ Adaptive density │ Equal-area cells │
│                     │ Redis native     │ No edge issues   │ Uniform neighbor │
│                     │ Distributed      │ Efficient for    │ handling, used   │
│                     │                  │ dense areas      │ by Uber itself   │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Rectangular grid │ Hard to          │ Library           │
│                     │ Edge-case issues │ distribute       │ dependency        │
│                     │ across cells     │ across nodes     │ Less documented  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Most apps ✅     │ Single-server    │ Uber-scale ✅    │
│                     │ Quick prototype  │ gaming, mapping  │ Precision pricing│
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Approach Comparison — Ride Matching

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Nearest Driver   │ Batch Matching   │ ML-Based         │
│                     │ (greedy)         │ (optimal)        │ Matching         │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ How it works        │ Find closest     │ Collect requests │ Predict accept   │
│                     │ available driver │ for N seconds,   │ probability,     │
│                     │ and assign       │ solve assignment │ driver preference │
│ Latency             │ Instant ✅       │ N-second delay   │ ~1 second        │
│ Match quality       │ Local optimum    │ Global optimum ✅│ Highest ✅       │
│ Driver utilization  │ Uneven           │ Balanced ✅      │ Balanced ✅      │
│ Complexity          │ Simple ✅        │ Hungarian algo   │ Feature eng.     │
│ Fairness            │ Closest gets all │ Fair distribution│ Tunable          │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Fast, simple     │ Optimal matches  │ Personalized     │
│                     │ Good for low     │ Higher accept    │ Higher accept    │
│                     │ demand areas     │ rates, fairness  │ rate, ETA-aware  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Suboptimal       │ Added latency    │ Complex model    │
│                     │ globally, unfair │ Compute-heavy    │ Training data    │
│                     │ to distant       │                  │ needed           │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Used by             │ Early Uber       │ Lyft, DiDi       │ Uber (current) ✅│
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class Ride {
    private String rideId;
    private String riderId;
    private String driverId;
    private Location pickupLocation;
    private Location dropoffLocation;
    private RideStatus status;
    private BigDecimal estimatedFare;
    private BigDecimal actualFare;
    private double surgeMultiplier;
    private Instant requestedAt;
    private Instant startedAt;
    private Instant completedAt;
}

public class Location {
    private double latitude;
    private double longitude;
}

public enum RideStatus {
    REQUESTED, DRIVER_ASSIGNED, DRIVER_ARRIVING, IN_PROGRESS, COMPLETED, CANCELLED
}

public class Driver {
    private String driverId;
    private String name;
    private DriverStatus status; // AVAILABLE, ON_TRIP, OFFLINE
    private Location currentLocation;
    private double rating;
    private VehicleInfo vehicle;
}

// --- Location Tracking Service ---
@Service
public class LocationService {

    private final RedisTemplate<String, String> redis;
    private static final String DRIVER_GEO_KEY = "drivers:locations";

    public void updateDriverLocation(String driverId, double lat, double lng) {
        // Store in Redis sorted set with geospatial index
        redis.opsForGeo().add(DRIVER_GEO_KEY,
            new Point(lng, lat), driverId);

        // Also publish for real-time tracking by riders
        redis.convertAndSend("driver-location:" + driverId,
            new LocationUpdate(driverId, lat, lng, Instant.now()));
    }

    public List<DriverDistance> findNearbyDrivers(double lat, double lng,
                                                   double radiusKm, int limit) {
        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
            redis.opsForGeo().radius(DRIVER_GEO_KEY,
                new Circle(new Point(lng, lat), new Distance(radiusKm, Metrics.KILOMETERS)),
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                    .includeDistance()
                    .includeCoordinates()
                    .sortAscending()
                    .limit(limit));

        return results.getContent().stream()
            .map(result -> new DriverDistance(
                result.getContent().getName(),
                result.getDistance().getValue(),
                result.getContent().getPoint()))
            .collect(Collectors.toList());
    }

    public double calculateETA(Location from, Location to) {
        double distanceKm = haversineDistance(from, to);
        double averageSpeedKmph = 30.0; // city average
        double etaMinutes = (distanceKm / averageSpeedKmph) * 60;
        double trafficMultiplier = getTrafficMultiplier(from); // 1.0 - 2.5
        return etaMinutes * trafficMultiplier;
    }

    private double haversineDistance(Location l1, Location l2) {
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(l2.getLatitude() - l1.getLatitude());
        double dLng = Math.toRadians(l2.getLongitude() - l1.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(l1.getLatitude())) *
            Math.cos(Math.toRadians(l2.getLatitude())) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}

// --- Ride Matching Service ---
@Service
public class RideMatchingService {

    private final LocationService locationService;
    private final DriverRepository driverRepository;
    private final PricingService pricingService;
    private final NotificationService notificationService;

    private static final double SEARCH_RADIUS_KM = 5.0;
    private static final int MAX_CANDIDATES = 10;
    private static final Duration DRIVER_RESPONSE_TIMEOUT = Duration.ofSeconds(15);

    public Ride requestRide(String riderId, Location pickup, Location dropoff) {
        // Step 1: Estimate fare
        double distanceKm = locationService.calculateETA(pickup, dropoff);
        double surgeMultiplier = pricingService.getSurgeMultiplier(pickup);
        BigDecimal estimatedFare = pricingService.calculateFare(
            pickup, dropoff, surgeMultiplier);

        // Step 2: Create ride request
        Ride ride = new Ride();
        ride.setRideId(UUID.randomUUID().toString());
        ride.setRiderId(riderId);
        ride.setPickupLocation(pickup);
        ride.setDropoffLocation(dropoff);
        ride.setEstimatedFare(estimatedFare);
        ride.setSurgeMultiplier(surgeMultiplier);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestedAt(Instant.now());
        rideRepository.save(ride);

        // Step 3: Find and match driver
        matchDriver(ride);
        return ride;
    }

    private void matchDriver(Ride ride) {
        List<DriverDistance> nearby = locationService.findNearbyDrivers(
            ride.getPickupLocation().getLatitude(),
            ride.getPickupLocation().getLongitude(),
            SEARCH_RADIUS_KM, MAX_CANDIDATES);

        // Filter to available drivers only
        List<DriverDistance> available = nearby.stream()
            .filter(dd -> {
                Driver driver = driverRepository.findById(dd.getDriverId());
                return driver.getStatus() == DriverStatus.AVAILABLE;
            })
            .sorted(Comparator.comparingDouble(DriverDistance::getDistanceKm))
            .collect(Collectors.toList());

        if (available.isEmpty()) {
            ride.setStatus(RideStatus.CANCELLED);
            notificationService.notifyRider(ride.getRiderId(), "No drivers available");
            return;
        }

        // Send request to closest driver first, with timeout cascade
        for (DriverDistance candidate : available) {
            boolean accepted = notificationService.requestDriverAcceptance(
                candidate.getDriverId(), ride, DRIVER_RESPONSE_TIMEOUT);

            if (accepted) {
                ride.setDriverId(candidate.getDriverId());
                ride.setStatus(RideStatus.DRIVER_ASSIGNED);
                rideRepository.save(ride);

                driverRepository.updateStatus(candidate.getDriverId(), DriverStatus.ON_TRIP);
                notificationService.notifyRider(ride.getRiderId(),
                    "Driver " + candidate.getDriverId() + " is on the way!");
                return;
            }
        }

        ride.setStatus(RideStatus.CANCELLED);
        notificationService.notifyRider(ride.getRiderId(), "No driver accepted");
    }
}

// --- Dynamic Pricing (Surge) Service ---
@Service
public class PricingService {

    private final RedisTemplate<String, Object> redis;

    private static final BigDecimal BASE_FARE = new BigDecimal("2.50");
    private static final BigDecimal PER_KM = new BigDecimal("1.50");
    private static final BigDecimal PER_MINUTE = new BigDecimal("0.25");

    public BigDecimal calculateFare(Location pickup, Location dropoff,
                                     double surgeMultiplier) {
        double distanceKm = calculateDistance(pickup, dropoff);
        double etaMinutes = estimateTripDuration(distanceKm);

        BigDecimal fare = BASE_FARE
            .add(PER_KM.multiply(BigDecimal.valueOf(distanceKm)))
            .add(PER_MINUTE.multiply(BigDecimal.valueOf(etaMinutes)))
            .multiply(BigDecimal.valueOf(surgeMultiplier));

        return fare.setScale(2, RoundingMode.HALF_UP);
    }

    public double getSurgeMultiplier(Location location) {
        String geohash = encodeGeohash(location.getLatitude(), location.getLongitude(), 5);

        long demand = getRecentRideRequests(geohash);
        long supply = getAvailableDrivers(geohash);

        if (supply == 0) return 3.0; // max surge
        double ratio = (double) demand / supply;

        if (ratio > 3.0) return 2.5;
        if (ratio > 2.0) return 2.0;
        if (ratio > 1.5) return 1.5;
        if (ratio > 1.0) return 1.25;
        return 1.0; // no surge
    }
}
```

### Database Schema

```sql
-- PostgreSQL: riders table
CREATE TABLE riders (
    rider_id      UUID PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(255) UNIQUE,
    phone         VARCHAR(20) UNIQUE NOT NULL,
    rating        DECIMAL(2,1) DEFAULT 5.0,
    payment_method_id UUID,
    home_location GEOGRAPHY(POINT, 4326),
    work_location GEOGRAPHY(POINT, 4326),
    created_at    TIMESTAMP DEFAULT NOW()
);

-- PostgreSQL: drivers table
CREATE TABLE drivers (
    driver_id     UUID PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    phone         VARCHAR(20) UNIQUE NOT NULL,
    license_no    VARCHAR(50) UNIQUE NOT NULL,
    vehicle_type  VARCHAR(20) NOT NULL,    -- ECONOMY, PREMIUM, XL, MOTO
    vehicle_plate VARCHAR(20) NOT NULL,
    vehicle_model VARCHAR(50),
    rating        DECIMAL(2,1) DEFAULT 5.0,
    status        VARCHAR(20) DEFAULT 'OFFLINE',  -- ONLINE, BUSY, OFFLINE
    city          VARCHAR(50),
    created_at    TIMESTAMP DEFAULT NOW()
);

-- PostgreSQL: rides table
CREATE TABLE rides (
    ride_id       UUID PRIMARY KEY,
    rider_id      UUID REFERENCES riders(rider_id),
    driver_id     UUID REFERENCES drivers(driver_id),
    status        VARCHAR(30) NOT NULL,    -- REQUESTED, MATCHED, DRIVER_EN_ROUTE, 
                                           -- IN_PROGRESS, COMPLETED, CANCELLED
    pickup_lat    DOUBLE PRECISION NOT NULL,
    pickup_lng    DOUBLE PRECISION NOT NULL,
    pickup_address TEXT,
    dropoff_lat   DOUBLE PRECISION NOT NULL,
    dropoff_lng   DOUBLE PRECISION NOT NULL,
    dropoff_address TEXT,
    estimated_fare DECIMAL(10,2),
    actual_fare   DECIMAL(10,2),
    surge_multiplier DECIMAL(3,2) DEFAULT 1.0,
    distance_km   DECIMAL(8,2),
    duration_min  DECIMAL(8,2),
    requested_at  TIMESTAMP DEFAULT NOW(),
    matched_at    TIMESTAMP,
    started_at    TIMESTAMP,
    completed_at  TIMESTAMP
);
CREATE INDEX idx_rides_rider ON rides(rider_id, requested_at DESC);
CREATE INDEX idx_rides_driver ON rides(driver_id, requested_at DESC);
CREATE INDEX idx_rides_status ON rides(status) WHERE status NOT IN ('COMPLETED', 'CANCELLED');

-- PostgreSQL: payments
CREATE TABLE ride_payments (
    payment_id    UUID PRIMARY KEY,
    ride_id       UUID UNIQUE REFERENCES rides(ride_id),
    rider_id      UUID REFERENCES riders(rider_id),
    amount        DECIMAL(10,2) NOT NULL,
    currency      VARCHAR(3) DEFAULT 'USD',
    status        VARCHAR(20),            -- PENDING, CHARGED, REFUNDED
    method        VARCHAR(20),
    idempotency_key VARCHAR(100) UNIQUE,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- Redis: real-time driver locations (updated every 4 seconds)
-- GeoSet: "drivers:online:{city}" → GEOADD with (lng, lat, driver_id)
-- Query: GEORADIUS "drivers:online:bangalore" lng lat 5 km COUNT 20 ASC

-- Redis: surge pricing per geohash zone
-- Key: "surge:{geohash_6}" → multiplier (e.g., 1.8) TTL 5 min
```

### Capacity Estimation

```
Assumptions:
  20M rides/day, 5M active drivers
  Driver location update every 4 seconds

Location updates:
  5M drivers × 1 update/4s = 1.25M writes/sec to Redis
  Each update: ~100 bytes → 125 MB/sec ingestion

Ride matching:
  20M/day ≈ 230 rides/sec
  Each match: geospatial query + 3-5 driver notifications

Storage:
  Rides (5 years): 20M/day × 365 × 5 × 1KB = ~36 TB
  Driver locations (keep 24h): 5M × 21,600 updates × 100B = ~10 TB/day

Redis cluster for geo:
  5M active driver locations × ~200 bytes = ~1 GB (fits in single node)
  But distributed for throughput → 5-10 node cluster
```

---

## Q7: Design a File Storage Service (Google Drive)

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                  FILE STORAGE ARCHITECTURE                        │
│                                                                    │
│  ┌──────────┐                                                    │
│  │ Desktop  │  ← sync client watches local file changes         │
│  │ Client   │                                                    │
│  └────┬─────┘                                                    │
│       │                                                          │
│       ▼                                                          │
│  ┌──────────────────┐                                            │
│  │  Sync Service    │                                            │
│  │                  │                                            │
│  │ • Detect local   │                                            │
│  │   file changes   │                                            │
│  │ • Compute diff   │                                            │
│  │ • Upload only    │                                            │
│  │   changed chunks │                                            │
│  └────────┬─────────┘                                            │
│           │                                                      │
│     ┌─────┴───────────────────────────────────┐                  │
│     ▼                                         ▼                  │
│  ┌──────────────┐                    ┌──────────────────┐        │
│  │ Metadata     │                    │  Block Storage   │        │
│  │ Service      │                    │  (S3/GCS)        │        │
│  │              │                    │                   │        │
│  │ • File tree  │                    │ Files split into │        │
│  │ • Versions   │                    │ 4MB chunks:      │        │
│  │ • Permissions│                    │                   │        │
│  │ • Share links│                    │ report.pdf(20MB): │        │
│  │              │                    │ ├── chunk_1 (4MB) │        │
│  │ PostgreSQL   │                    │ ├── chunk_2 (4MB) │        │
│  │              │                    │ ├── chunk_3 (4MB) │        │
│  └──────────────┘                    │ ├── chunk_4 (4MB) │        │
│                                      │ └── chunk_5 (4MB) │        │
│  ┌──────────────────┐                └──────────────────┘        │
│  │ Notification     │                                            │
│  │ Service (Kafka)  │                                            │
│  │                  │                                            │
│  │ File changed →   │                                            │
│  │ notify all       │                                            │
│  │ synced devices   │                                            │
│  └──────────────────┘                                            │
│                                                                    │
│  CHUNKING — WHY?                                                 │
│  ┌────────────────────────────────────────────────────────┐      │
│  │ User edits 1 page of a 20MB PDF.                       │      │
│  │                                                         │      │
│  │ Without chunking: re-upload 20MB ❌                    │      │
│  │ With chunking: re-upload only changed 4MB chunk ✅     │      │
│  │                                                         │      │
│  │ Deduplication: if chunk hash matches existing chunk,   │      │
│  │ don't store again (save 50%+ storage across users)     │      │
│  └────────────────────────────────────────────────────────┘      │
└──────────────────────────────────────────────────────────────────┘
```

### Approach Comparison — File Sync Strategy

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Full File Sync   │ Delta/Diff Sync  │ Chunk-based      │
│                     │                  │ (rsync-like)     │ Sync (Dropbox) ✅│
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ How it works        │ Upload entire    │ Compute binary   │ Split file into  │
│                     │ file on change   │ diff, send delta │ chunks, upload   │
│                     │                  │                  │ only changed ones│
│ Bandwidth           │ Very high ❌     │ Minimal ✅       │ Low ✅           │
│ Client CPU          │ Low              │ High (diff calc) │ Moderate         │
│ Deduplication       │ None             │ Per-file         │ Cross-user ✅    │
│ Resume on failure   │ Restart ❌       │ Restart ❌       │ Resume from last │
│                     │                  │                  │ chunk ✅         │
│ Implementation      │ Simple ✅        │ Complex (binary  │ Moderate         │
│                     │                  │ diff algorithms) │                  │
│ Server storage      │ Full copies      │ Full + deltas    │ Deduplicated     │
│                     │                  │                  │ chunks ✅        │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Simple, no bugs  │ Minimal network  │ Best overall:    │
│                     │ Easy to reason   │ Fast for small   │ dedup, resume,   │
│                     │ about            │ edits            │ bandwidth-       │
│                     │                  │                  │ efficient        │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Wastes bandwidth │ Complex diff     │ Chunk boundary   │
│                     │ Slow for large   │ algorithm        │ computation,     │
│                     │ files            │ Memory-intensive │ metadata overhead│
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Used by             │ Simple backup    │ rsync, Git       │ Dropbox, Google  │
│                     │ tools            │ (packfiles)      │ Drive ✅         │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Approach Comparison — Conflict Resolution

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Last Write Wins  │ Operational      │ Manual Merge     │
│                     │ (LWW)            │ Transform (OT)   │ (user decides)   │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Data loss risk      │ High ❌          │ None ✅          │ None ✅          │
│ Real-time collab    │ No               │ Yes ✅           │ No               │
│ Complexity          │ Simple ✅        │ Very complex ❌  │ Moderate         │
│ User experience     │ Surprising       │ Seamless ✅      │ Disruptive       │
│ Implementation      │ Timestamps only  │ Transform engine │ Conflict UI      │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Simple, fast     │ Real-time collab │ No data loss     │
│                     │ No conflicts     │ No data loss     │ User in control  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Loses edits      │ Extremely complex│ Disrupts flow    │
│                     │ silently         │ to implement     │ User must decide │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Used by             │ S3 (default)     │ Google Docs ✅   │ Dropbox, Git     │
│                     │ Cassandra        │ Figma            │ Google Drive ✅  │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class FileMetadata {
    private String fileId;
    private String fileName;
    private String parentFolderId;
    private String ownerId;
    private long sizeBytes;
    private String contentHash; // SHA-256 of entire file
    private int version;
    private FileStatus status; // ACTIVE, DELETED, TRASHED
    private List<ChunkMetadata> chunks;
    private Instant createdAt;
    private Instant modifiedAt;
}

public class ChunkMetadata {
    private String chunkHash; // SHA-256 of chunk content
    private int sequenceNumber;
    private long sizeBytes;
    private String storageKey; // S3 object key
}

public class FileVersion {
    private String fileId;
    private int versionNumber;
    private String changeDescription;
    private String modifiedBy;
    private List<ChunkMetadata> chunks;
    private Instant timestamp;
}

// --- Chunking and Sync Service ---
@Service
public class FileSyncService {

    private static final int CHUNK_SIZE = 4 * 1024 * 1024; // 4MB
    private final S3Client s3Client;
    private final FileMetadataRepository metadataRepository;
    private final ChunkRepository chunkRepository;
    private final KafkaTemplate<String, SyncEvent> kafkaTemplate;

    public FileUploadResult uploadFile(String userId, String fileName,
                                        String parentFolderId, InputStream fileStream)
                                        throws IOException {

        String fileId = UUID.randomUUID().toString();
        List<ChunkMetadata> chunks = new ArrayList<>();
        MessageDigest fileDigest = MessageDigest.getInstance("SHA-256");
        long totalSize = 0;
        int seq = 0;

        byte[] buffer = new byte[CHUNK_SIZE];
        int bytesRead;

        while ((bytesRead = fileStream.read(buffer)) > 0) {
            byte[] chunkData = (bytesRead < CHUNK_SIZE)
                ? Arrays.copyOf(buffer, bytesRead) : buffer;

            fileDigest.update(chunkData, 0, bytesRead);

            String chunkHash = sha256Hex(chunkData);

            // Deduplication: skip upload if chunk already exists
            if (!chunkRepository.existsByHash(chunkHash)) {
                String storageKey = "chunks/" + chunkHash;
                s3Client.putObject(PutObjectRequest.builder()
                    .bucket("file-storage").key(storageKey).build(),
                    RequestBody.fromBytes(chunkData));
                chunkRepository.save(new ChunkRecord(chunkHash, storageKey, bytesRead));
            }

            chunks.add(new ChunkMetadata(chunkHash, seq++, bytesRead, "chunks/" + chunkHash));
            totalSize += bytesRead;
        }

        FileMetadata metadata = new FileMetadata();
        metadata.setFileId(fileId);
        metadata.setFileName(fileName);
        metadata.setParentFolderId(parentFolderId);
        metadata.setOwnerId(userId);
        metadata.setSizeBytes(totalSize);
        metadata.setContentHash(Hex.encodeHexString(fileDigest.digest()));
        metadata.setVersion(1);
        metadata.setChunks(chunks);
        metadata.setCreatedAt(Instant.now());
        metadata.setModifiedAt(Instant.now());
        metadataRepository.save(metadata);

        // Notify other devices about the new file
        kafkaTemplate.send("file-sync-events", userId,
            new SyncEvent(SyncEventType.FILE_CREATED, fileId, userId));

        return new FileUploadResult(fileId, metadata.getContentHash(), chunks.size());
    }

    public FileUpdateResult updateFile(String fileId, String userId, InputStream newContent)
                                        throws IOException {

        FileMetadata existing = metadataRepository.findById(fileId)
            .orElseThrow(() -> new NotFoundException("File not found"));

        Map<Integer, ChunkMetadata> existingChunks = existing.getChunks().stream()
            .collect(Collectors.toMap(ChunkMetadata::getSequenceNumber, c -> c));

        List<ChunkMetadata> newChunks = new ArrayList<>();
        int seq = 0;
        int chunksReused = 0;
        int chunksUploaded = 0;

        byte[] buffer = new byte[CHUNK_SIZE];
        int bytesRead;

        while ((bytesRead = newContent.read(buffer)) > 0) {
            byte[] chunkData = (bytesRead < CHUNK_SIZE)
                ? Arrays.copyOf(buffer, bytesRead) : buffer;
            String chunkHash = sha256Hex(chunkData);

            ChunkMetadata oldChunk = existingChunks.get(seq);
            if (oldChunk != null && oldChunk.getChunkHash().equals(chunkHash)) {
                newChunks.add(oldChunk); // unchanged chunk — skip upload
                chunksReused++;
            } else {
                if (!chunkRepository.existsByHash(chunkHash)) {
                    s3Client.putObject(PutObjectRequest.builder()
                        .bucket("file-storage").key("chunks/" + chunkHash).build(),
                        RequestBody.fromBytes(chunkData));
                }
                newChunks.add(new ChunkMetadata(chunkHash, seq, bytesRead,
                    "chunks/" + chunkHash));
                chunksUploaded++;
            }
            seq++;
        }

        // Save version history
        FileVersion version = new FileVersion();
        version.setFileId(fileId);
        version.setVersionNumber(existing.getVersion() + 1);
        version.setModifiedBy(userId);
        version.setChunks(newChunks);
        version.setTimestamp(Instant.now());
        versionRepository.save(version);

        existing.setChunks(newChunks);
        existing.setVersion(existing.getVersion() + 1);
        existing.setModifiedAt(Instant.now());
        metadataRepository.save(existing);

        kafkaTemplate.send("file-sync-events", existing.getOwnerId(),
            new SyncEvent(SyncEventType.FILE_UPDATED, fileId, userId));

        return new FileUpdateResult(fileId, existing.getVersion(),
            chunksUploaded, chunksReused);
    }
}

// --- Sharing and Permissions Service ---
@Service
public class SharingService {

    private final ShareRepository shareRepository;
    private final FileMetadataRepository metadataRepository;

    public ShareLink createShareLink(String fileId, String ownerId,
                                      Permission permission, Instant expiresAt) {
        FileMetadata file = metadataRepository.findById(fileId)
            .orElseThrow(() -> new NotFoundException("File not found"));

        if (!file.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Only owner can share");
        }

        ShareLink link = new ShareLink();
        link.setLinkId(generateSecureToken());
        link.setFileId(fileId);
        link.setPermission(permission); // VIEW, EDIT, COMMENT
        link.setCreatedBy(ownerId);
        link.setExpiresAt(expiresAt);
        shareRepository.save(link);
        return link;
    }

    public void shareWithUser(String fileId, String ownerId,
                               String targetUserId, Permission permission) {
        FilePermission perm = new FilePermission();
        perm.setFileId(fileId);
        perm.setUserId(targetUserId);
        perm.setPermission(permission);
        perm.setGrantedBy(ownerId);
        perm.setGrantedAt(Instant.now());
        shareRepository.savePermission(perm);

        notificationService.notify(targetUserId,
            ownerId + " shared '" + getFileName(fileId) + "' with you");
    }

    public boolean hasPermission(String userId, String fileId, Permission required) {
        FileMetadata file = metadataRepository.findById(fileId).orElse(null);
        if (file == null) return false;
        if (file.getOwnerId().equals(userId)) return true;

        FilePermission perm = shareRepository.findPermission(userId, fileId);
        if (perm == null) return false;
        return perm.getPermission().includes(required);
    }
}
```

### Database Schema

```sql
-- PostgreSQL: users table
CREATE TABLE drive_users (
    user_id       UUID PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    display_name  VARCHAR(100),
    storage_used  BIGINT DEFAULT 0,       -- bytes used
    storage_limit BIGINT DEFAULT 15737418240,  -- 15 GB free tier
    created_at    TIMESTAMP DEFAULT NOW()
);

-- PostgreSQL: files and folders (unified file_entries table)
CREATE TABLE file_entries (
    file_id       UUID PRIMARY KEY,
    owner_id      UUID REFERENCES drive_users(user_id),
    parent_id     UUID REFERENCES file_entries(file_id),  -- folder hierarchy
    name          VARCHAR(255) NOT NULL,
    is_folder     BOOLEAN DEFAULT FALSE,
    mime_type     VARCHAR(100),
    size_bytes    BIGINT DEFAULT 0,
    s3_key        TEXT,                    -- S3 object key for file content
    checksum      VARCHAR(64),            -- SHA-256 for dedup & integrity
    is_trashed    BOOLEAN DEFAULT FALSE,
    trashed_at    TIMESTAMP,
    created_at    TIMESTAMP DEFAULT NOW(),
    updated_at    TIMESTAMP DEFAULT NOW(),
    UNIQUE(parent_id, name, owner_id)     -- no duplicate names in same folder
);
CREATE INDEX idx_files_owner ON file_entries(owner_id);
CREATE INDEX idx_files_parent ON file_entries(parent_id) WHERE NOT is_trashed;
CREATE INDEX idx_files_trashed ON file_entries(owner_id, trashed_at) WHERE is_trashed;

-- PostgreSQL: file versions (version history)
CREATE TABLE file_versions (
    version_id    UUID PRIMARY KEY,
    file_id       UUID REFERENCES file_entries(file_id),
    version_num   INT NOT NULL,
    s3_key        TEXT NOT NULL,
    size_bytes    BIGINT NOT NULL,
    checksum      VARCHAR(64),
    is_current    BOOLEAN DEFAULT TRUE,
    edited_by     UUID REFERENCES drive_users(user_id),
    created_at    TIMESTAMP DEFAULT NOW(),
    UNIQUE(file_id, version_num)
);
CREATE INDEX idx_versions_file ON file_versions(file_id, version_num DESC);

-- PostgreSQL: file chunks (for large file resumable upload & delta sync)
CREATE TABLE file_chunks (
    chunk_id      UUID PRIMARY KEY,
    version_id    UUID REFERENCES file_versions(version_id),
    chunk_index   INT NOT NULL,
    size_bytes    INT NOT NULL,
    checksum      VARCHAR(64) NOT NULL,   -- for delta sync: only upload changed chunks
    s3_key        TEXT NOT NULL,
    UNIQUE(version_id, chunk_index)
);

-- PostgreSQL: sharing and permissions
CREATE TABLE file_shares (
    share_id      UUID PRIMARY KEY,
    file_id       UUID REFERENCES file_entries(file_id),
    shared_with   UUID REFERENCES drive_users(user_id),
    permission    VARCHAR(10) NOT NULL,    -- 'VIEWER', 'COMMENTER', 'EDITOR'
    shared_by     UUID REFERENCES drive_users(user_id),
    share_link    VARCHAR(100) UNIQUE,    -- nullable, for public link sharing
    link_access   VARCHAR(20),            -- 'ANYONE_VIEW', 'ANYONE_EDIT'
    expires_at    TIMESTAMP,
    created_at    TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_shares_file ON file_shares(file_id);
CREATE INDEX idx_shares_user ON file_shares(shared_with);
```

### Capacity Estimation

```
Assumptions:
  1B registered users, 100M DAU
  Each user: avg 5GB stored, 10 file ops/day

Storage:
  1B users × 5GB = 5 Exabytes total
  With deduplication (50% savings): ~2.5 EB
  Daily uploads: 100M × 2 uploads × 10MB avg = 2 PB/day

Operations:
  File metadata reads: 100M × 10 = 1B/day ≈ 12K QPS
  Chunk uploads: 100M × 2 × 5 chunks = 1B chunks/day ≈ 12K writes/sec
  Sync notifications: 100M × 10 = 1B events/day

Metadata DB:
  1B users × avg 200 files × 1KB metadata = 200 TB
  Version history adds 5x → ~1 PB
```

---

## Q8: Design a Web Crawler

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                    WEB CRAWLER ARCHITECTURE                       │
│                                                                    │
│  ┌──────────────────┐                                            │
│  │  Seed URLs       │  ← starting points (news sites, etc.)     │
│  │  • cnn.com       │                                            │
│  │  • bbc.co.uk     │                                            │
│  └────────┬─────────┘                                            │
│           │                                                      │
│           ▼                                                      │
│  ┌──────────────────┐                                            │
│  │  URL Frontier    │  ← priority queue of URLs to crawl        │
│  │  (Priority Queue)│                                            │
│  │                  │  Priorities:                               │
│  │  ├── P0: News    │  • PageRank score                         │
│  │  ├── P1: Popular │  • Freshness (when last crawled)          │
│  │  └── P2: Other   │  • Domain importance                     │
│  └────────┬─────────┘                                            │
│           │ dequeue URL                                          │
│           ▼                                                      │
│  ┌──────────────────┐     ┌──────────────────┐                  │
│  │  URL Seen?       │────►│  Bloom Filter    │                  │
│  │  (dedup check)   │     │  (billions URLs, │                  │
│  │                  │     │   low memory)    │                  │
│  │  Seen → skip     │     │                  │                  │
│  │  New → proceed   │     │  False positive  │                  │
│  └────────┬─────────┘     │  rate: < 1%     │                  │
│           │               └──────────────────┘                  │
│           ▼                                                      │
│  ┌──────────────────┐                                            │
│  │  Fetcher         │  ← HTTP client (polite!)                  │
│  │  (distributed)   │                                            │
│  │                  │  POLITENESS RULES:                        │
│  │  • Respect       │  ├── Check robots.txt first               │
│  │    robots.txt    │  ├── 1 request per domain per second      │
│  │  • Rate limit    │  ├── Identify as crawler (User-Agent)     │
│  │    per domain    │  └── Respect Crawl-Delay header           │
│  │  • Timeout: 30s  │                                            │
│  └────────┬─────────┘                                            │
│           │ HTML content                                         │
│           ▼                                                      │
│  ┌──────────────────┐     ┌──────────────────┐                  │
│  │  Parser          │────►│  Content Seen?   │                  │
│  │                  │     │  (SimHash dedup)  │                  │
│  │  • Extract text  │     │                  │                  │
│  │  • Extract links │     │  Duplicate page  │                  │
│  │  • Extract meta  │     │  → skip storing  │                  │
│  └────────┬─────────┘     └──────────────────┘                  │
│           │                                                      │
│     ┌─────┴──────────┐                                          │
│     ▼                ▼                                          │
│  ┌──────────┐   ┌──────────────┐                                │
│  │ Store    │   │ New URLs ──► │                                │
│  │ Content  │   │ URL Frontier │  ← cycle continues             │
│  │ (S3/HDFS)│   │ (enqueue)    │                                │
│  └──────────┘   └──────────────┘                                │
└──────────────────────────────────────────────────────────────────┘
```

### Approach Comparison — URL Deduplication

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Bloom Filter     │ HashSet (in-     │ Database         │
│                     │                  │ memory)          │ (Redis / DB)     │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Memory usage        │ ~1.2 GB for      │ ~80 GB for 1B    │ Disk-based ✅    │
│                     │ 1B URLs ✅       │ URLs ❌          │ (unlimited)      │
│ False positives     │ < 1% (tunable)   │ None ✅          │ None ✅          │
│ False negatives     │ None ✅          │ None ✅          │ None ✅          │
│ Lookup speed        │ O(k) ✅          │ O(1) ✅          │ O(1) + network   │
│ Distributed         │ Partitionable    │ Hard ❌          │ Native ✅        │
│ Deletions           │ Not supported ❌ │ Supported ✅     │ Supported ✅     │
│ Scale               │ Billions ✅      │ Millions         │ Billions ✅      │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Memory-efficient │ Zero false       │ Persistent       │
│                     │ Very fast        │ positives        │ Survives restart │
│                     │ Scale to 10B+    │ Simple API       │ Exact dedup      │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ False positives  │ Memory-bound     │ Network latency  │
│                     │ No deletion      │ Can't distribute │ Write-heavy load │
│                     │ (use Cuckoo)     │ easily           │ on DB            │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Large crawlers ✅│ Small crawlers   │ Medium crawlers  │
│                     │ Google-scale     │ Single machine   │ Need persistence │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Approach Comparison — Crawl Strategy

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ BFS (Breadth-    │ DFS (Depth-      │ Priority-Based   │
│                     │ First)           │ First)           │ (Mercator) ✅    │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Exploration pattern │ Level by level   │ Follow links     │ Highest value    │
│                     │ (broad)          │ deeply           │ first            │
│ Domain coverage     │ Wide ✅          │ Deep in 1 site   │ Most important ✅│
│ Freshness           │ Uniform          │ Stale for uncrawl│ Fresher for      │
│                     │                  │ domains          │ important pages  │
│ Memory              │ High (queue) ❌  │ Low (stack)      │ Priority queue   │
│ Politeness          │ Natural delays   │ Hammers 1 site ❌│ Per-domain rate  │
│                     │                  │                  │ limiting ✅      │
│ Link-trap safety    │ Less susceptible │ Can get stuck ❌ │ Depth limits ✅  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Good for site-   │ Low memory       │ Crawl valuable   │
│                     │ maps, wide       │ finds deep pages │ pages first      │
│                     │ coverage         │                  │ Configurable     │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ High memory      │ Can get trapped  │ Complex scoring  │
│                     │ Slow to find     │ Unfair to other  │ function needed  │
│                     │ deep pages       │ domains          │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Sitemaps, small  │ Specific site    │ Google, Bing ✅  │
│                     │ targeted crawls  │ archival         │ Production       │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class CrawlTask {
    private String url;
    private int priority;
    private int depth;
    private String parentUrl;
    private Instant scheduledAt;
    private String domain;
}

public class CrawlResult {
    private String url;
    private int httpStatus;
    private String contentHash; // for dedup
    private String content;
    private List<String> extractedUrls;
    private Map<String, String> metadata; // title, description, etc.
    private Instant crawledAt;
    private long responseTimeMs;
}

// --- URL Frontier (Priority Queue) ---
@Service
public class UrlFrontier {

    private final Map<String, PriorityBlockingQueue<CrawlTask>> domainQueues =
        new ConcurrentHashMap<>();
    private final BloomFilter<String> bloomFilter;
    private final RedisTemplate<String, Long> politenessTracker;

    private static final Duration POLITENESS_DELAY = Duration.ofSeconds(1);

    public UrlFrontier() {
        this.bloomFilter = BloomFilter.create(
            Funnels.stringFunnel(StandardCharsets.UTF_8),
            1_000_000_000L,  // expected insertions: 1 billion
            0.01             // false positive probability: 1%
        );
    }

    public void addUrl(String url, int priority, int depth, String parentUrl) {
        String normalizedUrl = normalizeUrl(url);

        if (bloomFilter.mightContain(normalizedUrl)) {
            return; // already seen (with < 1% false positive rate)
        }

        bloomFilter.put(normalizedUrl);
        String domain = extractDomain(normalizedUrl);

        CrawlTask task = new CrawlTask();
        task.setUrl(normalizedUrl);
        task.setPriority(priority);
        task.setDepth(depth);
        task.setParentUrl(parentUrl);
        task.setDomain(domain);
        task.setScheduledAt(Instant.now());

        domainQueues.computeIfAbsent(domain,
            k -> new PriorityBlockingQueue<>(1000,
                Comparator.comparingInt(CrawlTask::getPriority)))
            .offer(task);
    }

    public CrawlTask getNextTask() {
        for (Map.Entry<String, PriorityBlockingQueue<CrawlTask>> entry :
                domainQueues.entrySet()) {

            String domain = entry.getKey();
            if (!isPolitenessDelayMet(domain)) continue;

            CrawlTask task = entry.getValue().poll();
            if (task != null) {
                politenessTracker.opsForValue().set("crawl:last:" + domain,
                    Instant.now().toEpochMilli(), POLITENESS_DELAY);
                return task;
            }
        }
        return null;
    }

    private boolean isPolitenessDelayMet(String domain) {
        Long lastCrawl = politenessTracker.opsForValue().get("crawl:last:" + domain);
        if (lastCrawl == null) return true;
        return Instant.now().toEpochMilli() - lastCrawl >= POLITENESS_DELAY.toMillis();
    }

    private String normalizeUrl(String url) {
        // Remove fragment, trailing slash, sort query params, lowercase domain
        try {
            URI uri = new URI(url);
            String normalized = uri.getScheme() + "://" + uri.getHost().toLowerCase();
            if (uri.getPath() != null) {
                normalized += uri.getPath().replaceAll("/+$", "");
            }
            if (uri.getQuery() != null) {
                String sortedQuery = Arrays.stream(uri.getQuery().split("&"))
                    .sorted().collect(Collectors.joining("&"));
                normalized += "?" + sortedQuery;
            }
            return normalized;
        } catch (URISyntaxException e) {
            return url;
        }
    }
}

// --- Crawler Worker ---
@Service
public class CrawlerWorker {

    private final UrlFrontier frontier;
    private final RobotsChecker robotsChecker;
    private final ContentDeduplicator deduplicator;
    private final HttpClient httpClient;
    private final ContentStore contentStore;

    private static final int MAX_DEPTH = 15;
    private static final Duration FETCH_TIMEOUT = Duration.ofSeconds(30);

    @Scheduled(fixedRate = 100) // poll every 100ms
    public void crawl() {
        CrawlTask task = frontier.getNextTask();
        if (task == null) return;

        try {
            if (task.getDepth() > MAX_DEPTH) return;
            if (!robotsChecker.isAllowed(task.getUrl())) return;

            HttpResponse<String> response = httpClient.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(task.getUrl()))
                    .header("User-Agent", "MyCrawler/1.0 (+https://mycrawler.com/bot)")
                    .timeout(FETCH_TIMEOUT)
                    .GET().build(),
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return;

            String content = response.body();
            String contentHash = sha256Hex(content);

            // Content deduplication via SimHash
            if (deduplicator.isDuplicate(contentHash)) return;
            deduplicator.recordContent(contentHash);

            CrawlResult result = parseContent(task.getUrl(), content, contentHash);
            contentStore.store(result);

            // Extract links and add to frontier
            for (String link : result.getExtractedUrls()) {
                String absoluteUrl = resolveUrl(task.getUrl(), link);
                int priority = calculatePriority(absoluteUrl, task.getDepth() + 1);
                frontier.addUrl(absoluteUrl, priority, task.getDepth() + 1, task.getUrl());
            }

        } catch (Exception e) {
            // Log failure, retry if transient
            if (isTransientError(e) && task.getRetryCount() < 3) {
                task.incrementRetry();
                frontier.addUrl(task.getUrl(), task.getPriority() + 1,
                    task.getDepth(), task.getParentUrl());
            }
        }
    }

    private CrawlResult parseContent(String url, String html, String contentHash) {
        Document doc = Jsoup.parse(html, url);

        CrawlResult result = new CrawlResult();
        result.setUrl(url);
        result.setContentHash(contentHash);
        result.setContent(doc.body().text());
        result.setCrawledAt(Instant.now());

        Map<String, String> metadata = new HashMap<>();
        metadata.put("title", doc.title());
        metadata.put("description", doc.select("meta[name=description]").attr("content"));
        result.setMetadata(metadata);

        List<String> urls = doc.select("a[href]").stream()
            .map(a -> a.absUrl("href"))
            .filter(href -> !href.isEmpty() && href.startsWith("http"))
            .distinct()
            .collect(Collectors.toList());
        result.setExtractedUrls(urls);
        return result;
    }

    private int calculatePriority(String url, int depth) {
        int priority = depth; // deeper pages get lower priority
        if (url.contains("/news/") || url.contains("/article/")) priority -= 3; // boost news
        if (url.contains("/login") || url.contains("/signup")) priority += 5; // deprioritize
        return priority;
    }
}

// --- Robots.txt Checker ---
@Service
public class RobotsChecker {

    private final LoadingCache<String, RobotsTxt> robotsCache = CacheBuilder.newBuilder()
        .maximumSize(100_000)
        .expireAfterWrite(Duration.ofHours(24))
        .build(new CacheLoader<>() {
            @Override
            public RobotsTxt load(String domain) {
                return fetchAndParseRobotsTxt(domain);
            }
        });

    public boolean isAllowed(String url) {
        String domain = extractDomain(url);
        RobotsTxt robots = robotsCache.getUnchecked(domain);
        return robots.isAllowed("/MyCrawler", extractPath(url));
    }
}
```

### Database Schema

```sql
-- PostgreSQL: URL frontier (URLs to crawl)
CREATE TABLE url_frontier (
    url_hash      CHAR(64) PRIMARY KEY,   -- SHA-256 of normalized URL
    url           TEXT NOT NULL,
    domain        VARCHAR(255) NOT NULL,
    priority      INT DEFAULT 5,          -- 1 = highest, 10 = lowest
    depth         INT DEFAULT 0,          -- hops from seed URL
    status        VARCHAR(20) DEFAULT 'PENDING',  -- PENDING, IN_PROGRESS, DONE, FAILED
    retry_count   INT DEFAULT 0,
    last_crawled  TIMESTAMP,
    next_crawl_at TIMESTAMP,              -- for recrawl scheduling
    discovered_at TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_frontier_domain ON url_frontier(domain, next_crawl_at);
CREATE INDEX idx_frontier_status ON url_frontier(status, priority, next_crawl_at);

-- PostgreSQL: crawled pages (metadata)
CREATE TABLE crawled_pages (
    page_id       UUID PRIMARY KEY,
    url_hash      CHAR(64) REFERENCES url_frontier(url_hash),
    url           TEXT NOT NULL,
    domain        VARCHAR(255) NOT NULL,
    content_hash  CHAR(64),               -- SHA-256 of page content (dedup detection)
    http_status   INT,
    content_type  VARCHAR(100),
    title         TEXT,
    content_length INT,
    s3_key        TEXT,                    -- S3 key for raw HTML storage
    crawled_at    TIMESTAMP DEFAULT NOW(),
    response_time_ms INT
);
CREATE INDEX idx_crawled_url ON crawled_pages(url_hash);
CREATE INDEX idx_crawled_content ON crawled_pages(content_hash);

-- PostgreSQL: outgoing links (graph edges)
CREATE TABLE page_links (
    source_url_hash  CHAR(64),
    target_url_hash  CHAR(64),
    anchor_text      VARCHAR(500),
    discovered_at    TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (source_url_hash, target_url_hash)
);
CREATE INDEX idx_links_target ON page_links(target_url_hash);

-- PostgreSQL: domain metadata (rate limiting per domain)
CREATE TABLE domain_metadata (
    domain        VARCHAR(255) PRIMARY KEY,
    robots_txt    TEXT,                    -- cached robots.txt content
    robots_fetched_at TIMESTAMP,
    crawl_delay_ms INT DEFAULT 1000,      -- politeness delay
    last_crawled  TIMESTAMP,
    page_count    INT DEFAULT 0,
    avg_response_ms INT
);

-- Redis: URL dedup bloom filter / seen set
-- Key: "seen_urls" → Bloom filter or Set of url_hashes
-- Key: "domain_rate:{domain}" → rate limiter timestamp
```

### Capacity Estimation

```
Assumptions:
  Crawl 1B pages/day
  Average page size: 200KB (after compression)

Fetch rate:
  1B / 86,400s ≈ 11,500 pages/sec across crawler fleet
  With politeness (1 req/domain/sec): need many domains in parallel

Storage:
  1B × 200KB = 200 TB/day raw content
  With compression (5x): ~40 TB/day
  30-day retention: ~1.2 PB active storage

Bloom Filter:
  10B total URLs seen over time
  At 1% FPR: ~11.5 GB memory (very efficient)
  At 0.1% FPR: ~17.2 GB memory

Network:
  11,500 pages/sec × 200KB = ~2.3 GB/sec outbound
  Need: ~50 machines with 10Gbps network each
```

---

## Q9: Design a Notification System

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                  NOTIFICATION SYSTEM ARCHITECTURE                 │
│                                                                    │
│  Event Sources:                                                   │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │ Order    │ │ Payment  │ │ Marketing│ │ Scheduler│           │
│  │ Service  │ │ Service  │ │ Service  │ │ (cron)   │           │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘           │
│       └─────────────┼───────────┘             │                 │
│                     │                         │                 │
│                     ▼                         │                 │
│              ┌──────────────┐                 │                 │
│              │    Kafka     │◄────────────────┘                 │
│              │ (event bus)  │                                   │
│              └──────┬───────┘                                   │
│                     │                                           │
│                     ▼                                           │
│              ┌──────────────────┐                               │
│              │ Notification     │                               │
│              │ Service          │                               │
│              │                  │                               │
│              │ • Template       │  "Hi {name}, your order      │
│              │   rendering      │   #{order_id} shipped!"      │
│              │ • User prefs     │                               │
│              │   (opt-out check)│                               │
│              │ • Deduplication  │                               │
│              │ • Rate limiting  │  Max 5 notifs/hour/user      │
│              │ • Priority queue │                               │
│              └──┬────┬────┬────┘                               │
│                 │    │    │                                     │
│        ┌────────┘    │    └────────┐                            │
│        ▼             ▼             ▼                            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                     │
│  │  Email   │  │   Push   │  │   SMS    │                     │
│  │ Provider │  │ Provider │  │ Provider │                     │
│  │ (SES /   │  │ (APNs / │  │ (Twilio) │                     │
│  │ SendGrid)│  │  FCM)    │  │          │                     │
│  └──────────┘  └──────────┘  └──────────┘                     │
│                                                                │
│  PRIORITY HANDLING:                                             │
│  ┌──────────────────────────────────────────────────┐          │
│  │ P0 (Critical): OTP, security alerts → immediate │          │
│  │ P1 (High):     Order updates → within 30 sec    │          │
│  │ P2 (Medium):   Social activity → within 5 min   │          │
│  │ P3 (Low):      Promotions → batched hourly      │          │
│  └──────────────────────────────────────────────────┘          │
│                                                                │
│  RETRY:                                                        │
│  Failed delivery → retry with exponential backoff              │
│  After 3 retries → Dead-letter queue → alert ops team          │
└──────────────────────────────────────────────────────────────────┘
```

### Approach Comparison — Notification Delivery

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Direct Delivery  │ Queue-Based      │ Event-Driven     │
│                     │ (sync)           │ (per channel)    │ (Kafka) ✅       │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Latency             │ Low for P0 ✅    │ Moderate         │ Moderate         │
│ Reliability         │ Lost if provider │ Retry from       │ Replay from      │
│                     │ fails ❌         │ queue ✅         │ offset ✅        │
│ Throughput          │ Limited by       │ High (parallel   │ Very high ✅     │
│                     │ provider rate    │ consumers) ✅    │                  │
│ Ordering            │ Immediate        │ FIFO per queue   │ Per partition ✅ │
│ Complexity          │ Simple ✅        │ Moderate         │ Higher           │
│ Back-pressure       │ None (drops) ❌  │ Queue buffers ✅ │ Consumer lag     │
│                     │                  │                  │ tracking ✅      │
│ Multi-channel       │ Sequential       │ Parallel per     │ Topic per        │
│                     │                  │ channel ✅       │ channel ✅       │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Lowest latency   │ Good isolation   │ Best durability  │
│                     │ Simple to debug  │ Easy retry       │ Replay capability│
│                     │                  │ Channel-specific │ High throughput  │
│                     │                  │ scaling          │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ No retry         │ More infra       │ Kafka cluster    │
│                     │ Provider outage  │ Queue monitoring │ mgmt overhead    │
│                     │ = lost notifs    │ needed           │ Higher latency   │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Low-volume,      │ Medium-scale     │ Large-scale ✅   │
│                     │ non-critical     │ multi-channel    │ Critical notifs  │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Approach Comparison — Template Engine

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ String Format    │ Template Engine   │ Dynamic Template │
│                     │ (hardcoded)      │ (Mustache/FTL)   │ Service ✅       │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Flexibility         │ None (code chg)  │ File-based ✅    │ DB-stored ✅     │
│ i18n support        │ Manual ❌        │ Bundle files     │ Per-locale in DB │
│ A/B testing         │ Not possible ❌  │ Separate files   │ Native ✅        │
│ Non-engineer edits  │ No ❌            │ Template files   │ CMS UI ✅        │
│ Performance         │ Fastest ✅       │ Compiled cache   │ Cached ✅        │
│ Deployment          │ Needs release    │ Hot-reload       │ Instant ✅       │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Prototype only   │ Small teams      │ Enterprise ✅    │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class Notification {
    private String notificationId;
    private String userId;
    private NotificationType type; // ORDER_UPDATE, OTP, MARKETING, SOCIAL
    private NotificationChannel channel; // EMAIL, PUSH, SMS, IN_APP
    private Priority priority; // P0_CRITICAL, P1_HIGH, P2_MEDIUM, P3_LOW
    private String templateId;
    private Map<String, String> templateParams;
    private NotificationStatus status; // PENDING, SENT, DELIVERED, FAILED
    private Instant createdAt;
    private Instant sentAt;
    private int retryCount;
}

public enum Priority { P0_CRITICAL, P1_HIGH, P2_MEDIUM, P3_LOW }
public enum NotificationChannel { EMAIL, PUSH, SMS, IN_APP }

// --- Notification Service ---
@Service
public class NotificationService {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final UserPreferenceService preferenceService;
    private final TemplateService templateService;
    private final DeduplicationService deduplicationService;
    private final RateLimiter rateLimiter;
    private final NotificationRepository notificationRepository;

    public void send(NotificationRequest request) {
        // Step 1: Check user preferences (opt-out)
        UserPreference prefs = preferenceService.getPreferences(request.getUserId());
        if (!prefs.isChannelEnabled(request.getChannel())) {
            return; // user opted out
        }
        if (prefs.isQuietHours() && request.getPriority() != Priority.P0_CRITICAL) {
            scheduleForLater(request, prefs.getQuietHoursEnd());
            return;
        }

        // Step 2: Deduplication (prevent duplicate notifications)
        String dedupKey = request.getUserId() + ":" + request.getType() + ":"
            + request.getTemplateParams().hashCode();
        if (deduplicationService.isDuplicate(dedupKey, Duration.ofMinutes(15))) {
            return; // already sent this notification recently
        }

        // Step 3: Rate limiting (max 5 notifs/hour/user for non-critical)
        if (request.getPriority() != Priority.P0_CRITICAL) {
            if (!rateLimiter.tryAcquire("notif:" + request.getUserId(), 5, Duration.ofHours(1))) {
                if (request.getPriority() == Priority.P3_LOW) {
                    batchForLater(request); // batch low-priority
                    return;
                }
                // P1/P2 still go through but logged
            }
        }

        // Step 4: Render template
        String renderedContent = templateService.render(
            request.getTemplateId(), request.getTemplateParams(), prefs.getLocale());

        // Step 5: Create notification record
        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setChannel(request.getChannel());
        notification.setPriority(request.getPriority());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreatedAt(Instant.now());
        notificationRepository.save(notification);

        // Step 6: Route to priority topic in Kafka
        String topic = "notifications-" + request.getPriority().name().toLowerCase();
        kafkaTemplate.send(topic, request.getUserId(),
            new NotificationEvent(notification.getNotificationId(),
                request.getChannel(), renderedContent, request.getUserId()));
    }
}

// --- Channel-Specific Senders ---
@Service
public class EmailSender implements NotificationSender {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "notifications-p0_critical",
                   groupId = "email-sender",
                   containerFactory = "highPriorityFactory")
    @KafkaListener(topics = {"notifications-p1_high", "notifications-p2_medium"},
                   groupId = "email-sender")
    public void processEmailNotification(NotificationEvent event) {
        if (event.getChannel() != NotificationChannel.EMAIL) return;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(getUserEmail(event.getUserId()));
            helper.setSubject(event.getSubject());
            helper.setText(event.getContent(), true); // HTML content

            mailSender.send(message);
            notificationRepository.updateStatus(event.getNotificationId(),
                NotificationStatus.SENT, Instant.now());

        } catch (MailException e) {
            handleFailure(event, e);
        }
    }

    private void handleFailure(NotificationEvent event, Exception e) {
        int retryCount = notificationRepository.incrementRetry(event.getNotificationId());

        if (retryCount < 3) {
            // Exponential backoff: 1s, 4s, 16s
            long delayMs = (long) Math.pow(4, retryCount) * 1000;
            scheduledExecutor.schedule(
                () -> kafkaTemplate.send("notifications-retry", event),
                delayMs, TimeUnit.MILLISECONDS);
        } else {
            notificationRepository.updateStatus(event.getNotificationId(),
                NotificationStatus.FAILED, Instant.now());
            // Send to dead-letter queue for manual investigation
            kafkaTemplate.send("notifications-dlq", event);
            alertService.alert("Notification delivery failed after 3 retries: "
                + event.getNotificationId());
        }
    }
}

@Service
public class PushNotificationSender implements NotificationSender {

    private final FirebaseMessaging firebaseMessaging;

    @KafkaListener(topics = "notifications-p0_critical", groupId = "push-sender")
    public void processPushNotification(NotificationEvent event) {
        if (event.getChannel() != NotificationChannel.PUSH) return;

        List<String> deviceTokens = getDeviceTokens(event.getUserId());

        for (String token : deviceTokens) {
            com.google.firebase.messaging.Message fcmMessage =
                com.google.firebase.messaging.Message.builder()
                    .setToken(token)
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(event.getSubject())
                        .setBody(event.getContent())
                        .build())
                    .putData("notificationId", event.getNotificationId())
                    .putData("deepLink", event.getDeepLink())
                    .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setTtl(3600 * 1000L) // 1 hour TTL
                        .build())
                    .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                            .setSound("default")
                            .setBadge(getUnreadCount(event.getUserId()))
                            .build())
                        .build())
                    .build();

            try {
                firebaseMessaging.send(fcmMessage);
            } catch (FirebaseMessagingException e) {
                if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    removeInvalidToken(event.getUserId(), token);
                }
            }
        }
    }
}

// --- Template Service ---
@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final LoadingCache<String, CompiledTemplate> templateCache =
        CacheBuilder.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .build(new CacheLoader<>() {
                @Override
                public CompiledTemplate load(String key) {
                    return compileTemplate(key);
                }
            });

    public String render(String templateId, Map<String, String> params, Locale locale) {
        String cacheKey = templateId + ":" + locale.getLanguage();
        CompiledTemplate template = templateCache.getUnchecked(cacheKey);

        String rendered = template.getBody();
        for (Map.Entry<String, String> param : params.entrySet()) {
            rendered = rendered.replace("{{" + param.getKey() + "}}", param.getValue());
        }
        return rendered;
    }
}
```

### Database Schema

```sql
-- PostgreSQL: notification_templates
CREATE TABLE notification_templates (
    template_id   UUID PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    channel       VARCHAR(20) NOT NULL,   -- PUSH, EMAIL, SMS, IN_APP
    subject       VARCHAR(500),           -- for email
    body          TEXT NOT NULL,           -- with {{placeholder}} syntax
    locale        VARCHAR(10) DEFAULT 'en',
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT NOW(),
    updated_at    TIMESTAMP DEFAULT NOW(),
    UNIQUE(name, channel, locale)
);

-- PostgreSQL: user notification preferences
CREATE TABLE notification_preferences (
    user_id       UUID REFERENCES users(user_id),
    channel       VARCHAR(20) NOT NULL,
    category      VARCHAR(50) NOT NULL,   -- 'MARKETING', 'TRANSACTIONAL', 'SOCIAL'
    enabled       BOOLEAN DEFAULT TRUE,
    quiet_start   TIME,                   -- e.g., 22:00 (do not disturb)
    quiet_end     TIME,                   -- e.g., 07:00
    PRIMARY KEY (user_id, channel, category)
);

-- PostgreSQL: device tokens (for push notifications)
CREATE TABLE device_tokens (
    token_id      UUID PRIMARY KEY,
    user_id       UUID REFERENCES users(user_id),
    device_token  VARCHAR(500) NOT NULL,
    platform      VARCHAR(10) NOT NULL,   -- IOS, ANDROID, WEB
    app_version   VARCHAR(20),
    is_active     BOOLEAN DEFAULT TRUE,
    registered_at TIMESTAMP DEFAULT NOW(),
    last_used_at  TIMESTAMP
);
CREATE INDEX idx_device_user ON device_tokens(user_id) WHERE is_active;

-- Cassandra: notification log (high-volume writes, time-range queries)
CREATE TABLE notification_log (
    user_id       UUID,
    notification_id TIMEUUID,
    channel       TEXT,                   -- PUSH, EMAIL, SMS, IN_APP
    template_id   UUID,
    title         TEXT,
    body          TEXT,
    status        TEXT,                   -- QUEUED, SENT, DELIVERED, FAILED, CLICKED
    error_msg     TEXT,
    sent_at       TIMESTAMP,
    delivered_at  TIMESTAMP,
    read_at       TIMESTAMP,
    PRIMARY KEY (user_id, notification_id)
) WITH CLUSTERING ORDER BY (notification_id DESC);
-- Query: "Get last 50 notifications for user X" → single partition scan

-- Redis: dedup & rate limiting
-- Key: "notif_dedup:{user_id}:{template_id}:{hash}" → 1 (TTL: 1 hour)
-- Key: "notif_rate:{user_id}:{channel}" → counter (TTL: 1 hour)
```

### Capacity Estimation

```
Assumptions:
  500M users, 10 notifications/user/day avg
  Channels: 40% push, 30% email, 20% in-app, 10% SMS

Volume:
  5B notifications/day ≈ 58K/sec average
  Peak (Black Friday, flash sales): 10x → 580K/sec

Per channel:
  Push: 2B/day → FCM/APNs rate limits apply
  Email: 1.5B/day → SES handles ~100K/sec
  SMS:  500M/day → Twilio at ~10K/sec/account
  In-App: 1B/day → stored in DB, read on app open

Storage:
  Notification records: 5B/day × 500B = 2.5 TB/day
  Templates: ~10MB (cached in memory)
  User preferences: 500M × 200B = 100 GB

Kafka:
  5B messages/day × 1KB avg = 5 TB/day throughput
  Retention: 3 days → 15 TB Kafka storage
```

---

## Q10: Design a Logging & Monitoring System

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│              LOGGING & MONITORING ARCHITECTURE                    │
│                                                                    │
│  Log Sources:                                                     │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐                  │
│  │App 1 │ │App 2 │ │App N │ │DB    │ │Infra │                  │
│  │logs  │ │logs  │ │logs  │ │logs  │ │logs  │                  │
│  └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘                  │
│     └────────┼────────┼────────┼────────┘                       │
│              │        │        │                                 │
│              ▼        ▼        ▼                                 │
│       ┌──────────────────────────────┐                          │
│       │  Log Collectors / Agents     │                          │
│       │  (Fluentd / Filebeat /       │                          │
│       │   Vector / OpenTelemetry)    │                          │
│       │                              │                          │
│       │  • Tail log files            │                          │
│       │  • Parse & structure         │                          │
│       │  • Add metadata (host, svc)  │                          │
│       │  • Buffer & batch            │                          │
│       └──────────────┬───────────────┘                          │
│                      │                                          │
│                      ▼                                          │
│       ┌──────────────────────────────┐                          │
│       │  Message Queue (Kafka)       │  ← handles burst         │
│       │                              │    ingestion             │
│       │  Topics:                     │                          │
│       │  ├── logs.app               │                          │
│       │  ├── logs.infra             │                          │
│       │  └── metrics.system         │                          │
│       └──────────┬───────────────────┘                          │
│                  │                                              │
│         ┌────────┼────────────┐                                 │
│         ▼                     ▼                                 │
│  ┌──────────────────┐  ┌──────────────────┐                    │
│  │ Elasticsearch    │  │ Time-Series DB   │                    │
│  │ (log search)     │  │ (Prometheus /    │                    │
│  │                  │  │  InfluxDB)       │                    │
│  │ Full-text search │  │                  │                    │
│  │ on log messages  │  │ Metrics:         │                    │
│  │                  │  │ • CPU, memory    │                    │
│  │ Retention:       │  │ • Request rate   │                    │
│  │ Hot: 7 days     │  │ • Error rate     │                    │
│  │ Warm: 30 days   │  │ • P99 latency    │                    │
│  │ Cold: S3 (years)│  │                  │                    │
│  └────────┬─────────┘  └────────┬─────────┘                    │
│           │                     │                               │
│           ▼                     ▼                               │
│  ┌──────────────────────────────────────────┐                  │
│  │           VISUALIZATION LAYER             │                  │
│  │                                            │                  │
│  │  ┌─────────────┐    ┌─────────────────┐   │                  │
│  │  │  Kibana     │    │    Grafana      │   │                  │
│  │  │  (log       │    │   (metrics      │   │                  │
│  │  │   search &  │    │    dashboards)  │   │                  │
│  │  │   explore)  │    │                 │   │                  │
│  │  └─────────────┘    └─────────────────┘   │                  │
│  └──────────────────────────────────────────┘                  │
│                      │                                          │
│                      ▼                                          │
│  ┌──────────────────────────────────────────┐                  │
│  │           ALERTING ENGINE                 │                  │
│  │                                            │                  │
│  │  Rules:                                   │                  │
│  │  ├── error_rate > 1%  → PagerDuty (P0)   │                  │
│  │  ├── p99 > 2s         → Slack (P1)       │                  │
│  │  ├── disk > 80%       → Email (P2)       │                  │
│  │  └── cert expiry < 7d → Ticket (P3)      │                  │
│  │                                            │                  │
│  │  Alert fatigue prevention:                │                  │
│  │  • Dedup: same alert once per 15 min      │                  │
│  │  • Grouping: related alerts combined      │                  │
│  │  • Escalation: P0 → on-call → manager     │                  │
│  └──────────────────────────────────────────┘                  │
│                                                                  │
│  DATA LIFECYCLE:                                                │
│  ┌──────────────────────────────────────────────────────┐      │
│  │ Hot  (0-7 days):   Elasticsearch SSD  → fast search │      │
│  │ Warm (7-30 days):  Elasticsearch HDD  → cheaper     │      │
│  │ Cold (30d-1yr):    S3 Standard        → archival    │      │
│  │ Frozen (1yr+):     S3 Glacier         → compliance  │      │
│  │                                                       │      │
│  │ Cost: ~$0.10/GB/mo (hot) → $0.004/GB/mo (glacier)  │      │
│  └──────────────────────────────────────────────────────┘      │
└──────────────────────────────────────────────────────────────────┘
```

### Approach Comparison — Log Collection

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ File-based Agent │ Sidecar (K8s)    │ Direct SDK       │
│                     │ (Filebeat/Vector)│ (Fluentd DaemonS)│ (OpenTelemetry)  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Deployment model    │ Agent per host   │ Pod sidecar or   │ In-process        │
│                     │                  │ DaemonSet        │ library           │
│ App code changes    │ None ✅ (tail    │ None ✅ (stdout) │ Instrumentation  │
│                     │ log files)       │                  │ required ❌      │
│ Structured logging  │ Parse at agent   │ Parse at sidecar │ Native ✅        │
│ Resource overhead   │ Low per host     │ Per-pod overhead │ Shared with app  │
│ Reliability         │ Disk buffer ✅   │ Buffer in sidecar│ App-level buffer │
│ Language support    │ Any (file-based) │ Any (stdout) ✅  │ Java, Python,    │
│                     │                  │                  │ Go, .NET, etc.   │
│ Correlation (traces)│ Manual           │ Manual           │ Built-in ✅      │
│ Metrics + traces    │ Separate setup   │ Separate setup   │ Unified ✅       │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ No app changes   │ K8s native       │ Richest context  │
│                     │ Works everywhere │ Auto-discovery   │ Traces + metrics │
│                     │ Battle-tested    │ Central config   │ + logs unified   │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ File rotation    │ Resource per pod │ Code changes     │
│                     │ issues           │ Complex configs  │ SDK dependency   │
│                     │ Parse complexity │                  │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Legacy/VM-based  │ Kubernetes ✅    │ New services     │
│                     │ systems          │ environments     │ (greenfield) ✅  │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Approach Comparison — Log Storage & Search

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Elasticsearch    │ Loki             │ ClickHouse       │
│                     │ (ELK Stack)      │ (Grafana)        │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Index strategy      │ Full-text index  │ Labels only      │ Columnar +       │
│                     │ (inverted index) │ (no full-text)   │ sparse index     │
│ Query language      │ Lucene / KQL     │ LogQL            │ SQL ✅           │
│ Storage cost        │ High (indexes)❌ │ Low (compressed  │ Low (columnar    │
│                     │                  │ chunks only) ✅  │ compression) ✅  │
│ Full-text search    │ Excellent ✅     │ Grep-like only   │ Good             │
│ Scale               │ Horizontal       │ Horizontal ✅    │ Horizontal ✅    │
│ Aggregations        │ Powerful ✅      │ Limited          │ Excellent ✅     │
│ Operational cost    │ High (JVM heap,  │ Low (Prom-like)  │ Moderate         │
│                     │ cluster mgmt) ❌ │ ✅               │                  │
│ Ecosystem           │ Kibana, APM,     │ Grafana ✅       │ Grafana, custom  │
│                     │ Beats ✅         │                  │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Best full-text   │ 10x cheaper      │ Best for         │
│                     │ Mature ecosystem │ Simple operations│ analytics/SQL    │
│                     │ Rich queries     │ Grafana native   │ Fast aggregation │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Expensive at     │ No full-text     │ Less mature for  │
│                     │ scale            │ search           │ log use case     │
│                     │ Complex tuning   │ Limited queries  │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Enterprise ✅    │ Cost-conscious ✅│ Analytics-heavy  │
│                     │ Rich search      │ Kubernetes       │ Large-scale      │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Approach Comparison — Alerting Strategy

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Threshold-based  │ Anomaly Detection│ Composite/Multi- │
│                     │ (static rules)   │ (ML-based)       │ signal ✅        │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Setup effort        │ Low ✅           │ High (training)  │ Medium           │
│ False positives     │ High (seasonal   │ Lower ✅         │ Lowest ✅        │
│                     │ patterns ignored)│                  │ (correlated)     │
│ Alert fatigue       │ High ❌          │ Moderate         │ Low ✅           │
│ Unknown unknowns    │ Not detected ❌  │ Detected ✅      │ Partially        │
│ Explainability      │ Clear ✅         │ Black box ❌     │ Moderate         │
│ Maintenance         │ Manual threshold │ Model retraining │ Rule + ML combo  │
│                     │ updates          │                  │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Simple, clear    │ Adapts to        │ Reduces noise    │
│                     │ Predictable      │ patterns         │ Correlates       │
│                     │ Easy to debug    │ Catches novel    │ signals for      │
│                     │                  │ issues           │ root cause       │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Doesn't adapt    │ Training data    │ More complex     │
│                     │ Many false alarms│ Cold start issue │ setup            │
│                     │                  │ Hard to explain  │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Known metrics    │ Dynamic workloads│ Production SRE ✅│
│                     │ (CPU > 90%)      │ (traffic varies) │ Mature teams     │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class LogEntry {
    private String logId;
    private String serviceName;
    private String hostname;
    private LogLevel level; // TRACE, DEBUG, INFO, WARN, ERROR, FATAL
    private String message;
    private String traceId;
    private String spanId;
    private Map<String, String> labels;
    private String stackTrace;
    private Instant timestamp;
}

public class MetricPoint {
    private String metricName;
    private Map<String, String> labels;
    private double value;
    private MetricType type; // COUNTER, GAUGE, HISTOGRAM, SUMMARY
    private Instant timestamp;
}

public class AlertRule {
    private String ruleId;
    private String name;
    private String expression; // e.g., "error_rate > 0.01"
    private Duration evaluationWindow;
    private Duration cooldownPeriod;
    private AlertSeverity severity;
    private List<String> notifyChannels; // pagerduty, slack, email
}

public enum AlertSeverity { P0_PAGE, P1_URGENT, P2_WARNING, P3_INFO }

// --- Log Ingestion Service ---
@Service
public class LogIngestionService {

    private final KafkaTemplate<String, LogEntry> kafkaTemplate;
    private final MeterRegistry meterRegistry;
    private final List<LogFilter> filters;

    public void ingest(List<LogEntry> batch) {
        Counter ingestedCounter = meterRegistry.counter("logs.ingested");
        Counter droppedCounter = meterRegistry.counter("logs.dropped");

        for (LogEntry entry : batch) {
            // Enrich with standard fields
            enrichLogEntry(entry);

            // Apply filters (drop debug in production, PII scrubbing)
            if (shouldDrop(entry)) {
                droppedCounter.increment();
                continue;
            }

            // Scrub sensitive data (credit card numbers, SSNs, etc.)
            scrubPII(entry);

            // Route to appropriate Kafka topic based on service
            String topic = "logs." + entry.getServiceName();
            kafkaTemplate.send(topic, entry.getTraceId(), entry);
            ingestedCounter.increment();
        }
    }

    private void enrichLogEntry(LogEntry entry) {
        if (entry.getLogId() == null) {
            entry.setLogId(UUID.randomUUID().toString());
        }
        if (entry.getTimestamp() == null) {
            entry.setTimestamp(Instant.now());
        }
        // Add environment labels
        entry.getLabels().putIfAbsent("env", System.getenv("ENV"));
        entry.getLabels().putIfAbsent("region", System.getenv("AWS_REGION"));
    }

    private void scrubPII(LogEntry entry) {
        String msg = entry.getMessage();
        msg = msg.replaceAll("\\b\\d{4}[- ]?\\d{4}[- ]?\\d{4}[- ]?\\d{4}\\b", "****-****-****-****");
        msg = msg.replaceAll("\\b\\d{3}-\\d{2}-\\d{4}\\b", "***-**-****");
        msg = msg.replaceAll("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", "***@***.***");
        entry.setMessage(msg);
    }
}

// --- Log Indexing Service (Elasticsearch Consumer) ---
@Service
public class LogIndexingService {

    private final ElasticsearchClient esClient;

    @KafkaListener(topicPattern = "logs\\..*", groupId = "log-indexer")
    public void indexLogs(List<LogEntry> batch) {
        // Time-based index: logs-2026-03-11
        String indexName = "logs-" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        BulkRequest.Builder bulkRequest = new BulkRequest.Builder();
        for (LogEntry entry : batch) {
            bulkRequest.operations(op -> op.index(idx -> idx
                .index(indexName)
                .id(entry.getLogId())
                .document(entry)));
        }

        BulkResponse response = esClient.bulk(bulkRequest.build());

        if (response.errors()) {
            for (BulkResponseItem item : response.items()) {
                if (item.error() != null) {
                    log.error("Failed to index log {}: {}", item.id(), item.error().reason());
                }
            }
        }
    }
}

// --- Metrics Collection Service ---
@Service
public class MetricsCollectionService {

    private final MeterRegistry meterRegistry;
    private final TimeSeriesDB timeSeriesDB; // Prometheus or InfluxDB

    public void recordMetric(MetricPoint point) {
        switch (point.getType()) {
            case COUNTER:
                Counter counter = meterRegistry.counter(point.getMetricName(),
                    tagsFromLabels(point.getLabels()));
                counter.increment(point.getValue());
                break;

            case GAUGE:
                AtomicDouble gaugeValue = new AtomicDouble(point.getValue());
                meterRegistry.gauge(point.getMetricName(),
                    Tags.of(tagsFromLabels(point.getLabels())),
                    gaugeValue);
                break;

            case HISTOGRAM:
                DistributionSummary summary = meterRegistry.summary(
                    point.getMetricName(), tagsFromLabels(point.getLabels()));
                summary.record(point.getValue());
                break;
        }

        timeSeriesDB.write(point);
    }

    // Pre-configured application metrics
    public void recordRequestLatency(String service, String endpoint,
                                      int statusCode, long durationMs) {
        Timer.builder("http.request.duration")
            .tag("service", service)
            .tag("endpoint", endpoint)
            .tag("status", String.valueOf(statusCode))
            .tag("status_class", statusCode / 100 + "xx")
            .publishPercentiles(0.5, 0.9, 0.95, 0.99)
            .register(meterRegistry)
            .record(Duration.ofMillis(durationMs));
    }

    public void recordErrorRate(String service, String errorType) {
        meterRegistry.counter("error.count",
            "service", service, "type", errorType)
            .increment();
    }
}

// --- Alert Engine ---
@Service
public class AlertEngine {

    private final AlertRuleRepository ruleRepository;
    private final MetricsQueryService metricsQueryService;
    private final NotificationService notificationService;
    private final AlertStateStore alertStateStore; // Redis
    private final AlertHistoryRepository alertHistory;

    @Scheduled(fixedRate = 15_000) // evaluate every 15 seconds
    public void evaluateAlertRules() {
        List<AlertRule> rules = ruleRepository.findAllActive();

        for (AlertRule rule : rules) {
            try {
                double currentValue = metricsQueryService.evaluate(
                    rule.getExpression(), rule.getEvaluationWindow());

                boolean firing = metricsQueryService.isThresholdBreached(
                    rule.getExpression(), currentValue);

                AlertState previousState = alertStateStore.getState(rule.getRuleId());

                if (firing && previousState != AlertState.FIRING) {
                    // New alert — fire notification
                    fireAlert(rule, currentValue);
                    alertStateStore.setState(rule.getRuleId(), AlertState.FIRING);

                } else if (!firing && previousState == AlertState.FIRING) {
                    // Alert resolved
                    resolveAlert(rule);
                    alertStateStore.setState(rule.getRuleId(), AlertState.RESOLVED);
                }

            } catch (Exception e) {
                log.error("Failed to evaluate rule {}: {}", rule.getRuleId(), e.getMessage());
            }
        }
    }

    private void fireAlert(AlertRule rule, double currentValue) {
        // Check cooldown (don't fire same alert within cooldown period)
        Instant lastFired = alertStateStore.getLastFiredAt(rule.getRuleId());
        if (lastFired != null &&
            lastFired.plus(rule.getCooldownPeriod()).isAfter(Instant.now())) {
            return; // still in cooldown
        }

        Alert alert = new Alert();
        alert.setAlertId(UUID.randomUUID().toString());
        alert.setRuleId(rule.getRuleId());
        alert.setRuleName(rule.getName());
        alert.setSeverity(rule.getSeverity());
        alert.setCurrentValue(currentValue);
        alert.setFiredAt(Instant.now());
        alert.setStatus(AlertStatus.FIRING);
        alertHistory.save(alert);

        // Route to notification channels based on severity
        for (String channel : rule.getNotifyChannels()) {
            notificationService.sendAlert(channel, alert);
        }

        alertStateStore.setLastFiredAt(rule.getRuleId(), Instant.now());
    }

    private void resolveAlert(AlertRule rule) {
        alertHistory.resolveLatest(rule.getRuleId(), Instant.now());

        for (String channel : rule.getNotifyChannels()) {
            notificationService.sendResolution(channel, rule.getName());
        }
    }
}

// --- Log Search API ---
@RestController
@RequestMapping("/api/v1/logs")
public class LogSearchController {

    private final ElasticsearchClient esClient;

    @GetMapping("/search")
    public SearchResult searchLogs(@RequestParam String query,
                                    @RequestParam(defaultValue = "1h") String timeRange,
                                    @RequestParam(required = false) String service,
                                    @RequestParam(required = false) String level,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "100") int size) {

        BoolQuery.Builder boolQuery = new BoolQuery.Builder()
            .must(m -> m.queryString(qs -> qs.query(query)));

        // Time range filter
        Instant from = parseTimeRange(timeRange);
        boolQuery.filter(f -> f.range(r -> r
            .field("timestamp").gte(JsonData.of(from.toString()))));

        if (service != null) {
            boolQuery.filter(f -> f.term(t -> t.field("serviceName").value(service)));
        }
        if (level != null) {
            boolQuery.filter(f -> f.term(t -> t.field("level").value(level)));
        }

        SearchResponse<LogEntry> response = esClient.search(s -> s
            .index("logs-*")
            .query(q -> q.bool(boolQuery.build()))
            .sort(sort -> sort.field(f -> f.field("timestamp").order(SortOrder.Desc)))
            .from(page * size).size(size)
            .highlight(h -> h.fields("message", hf -> hf.preTags("<mark>").postTags("</mark>"))),
            LogEntry.class);

        return new SearchResult(response.hits().hits(), response.hits().total().value());
    }

    @GetMapping("/tail")
    public SseEmitter tailLogs(@RequestParam String service,
                                @RequestParam(defaultValue = "INFO") String minLevel) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        logTailService.subscribe(service, minLevel, emitter);
        return emitter;
    }
}

// --- Index Lifecycle Management ---
@Service
public class IndexLifecycleManager {

    private final ElasticsearchClient esClient;
    private final S3Client s3Client;

    @Scheduled(cron = "0 0 2 * * *") // daily at 2 AM
    public void manageLifecycle() {
        LocalDate today = LocalDate.now();

        // Hot → Warm: older than 7 days
        LocalDate warmThreshold = today.minusDays(7);
        moveToWarm("logs-" + warmThreshold.format(DateTimeFormatter.ISO_LOCAL_DATE));

        // Warm → Cold (S3): older than 30 days
        LocalDate coldThreshold = today.minusDays(30);
        archiveToS3("logs-" + coldThreshold.format(DateTimeFormatter.ISO_LOCAL_DATE));

        // Delete: older than 365 days (after S3 backup confirmed)
        LocalDate deleteThreshold = today.minusDays(365);
        deleteIndex("logs-" + deleteThreshold.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    private void moveToWarm(String indexName) {
        // Move to warm nodes (HDD, less replicas)
        esClient.indices().putSettings(s -> s
            .index(indexName)
            .settings(is -> is
                .routing(r -> r.allocation(a -> a.include(i -> i.tierPreference("data_warm"))))
                .numberOfReplicas("1")));
    }

    private void archiveToS3(String indexName) {
        // Snapshot to S3 for long-term storage
        esClient.snapshot().create(c -> c
            .repository("s3-archive")
            .snapshot(indexName)
            .indices(indexName));

        // After snapshot confirmed, delete from Elasticsearch
        esClient.indices().delete(d -> d.index(indexName));
    }
}
```

### Database Schema

```sql
-- Elasticsearch: log index mapping (daily rollover)
-- Index: logs-YYYY-MM-DD (ILM managed: hot 7d → warm 30d → cold 90d → delete)
PUT logs-template
{
  "mappings": {
    "properties": {
      "timestamp":     { "type": "date" },
      "level":         { "type": "keyword" },              -- INFO, WARN, ERROR, DEBUG
      "service":       { "type": "keyword" },              -- e.g., "payment-service"
      "instance_id":   { "type": "keyword" },              -- container/pod ID
      "trace_id":      { "type": "keyword" },              -- distributed tracing
      "span_id":       { "type": "keyword" },
      "message":       { "type": "text", "analyzer": "standard" },
      "logger":        { "type": "keyword" },              -- e.g., "com.app.PaymentController"
      "thread":        { "type": "keyword" },
      "environment":   { "type": "keyword" },              -- prod, staging, dev
      "region":        { "type": "keyword" },
      "exception":     { "type": "text" },
      "stack_trace":   { "type": "text" },
      "metadata":      { "type": "object", "enabled": true } -- custom key-value pairs
    }
  }
}

-- PostgreSQL: alert rules (configured by platform users)
CREATE TABLE alert_rules (
    rule_id       UUID PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    description   TEXT,
    query         TEXT NOT NULL,           -- Elasticsearch query DSL or Lucene query
    condition     VARCHAR(50) NOT NULL,    -- 'count > 100', 'avg(response_time) > 5000'
    window_min    INT NOT NULL DEFAULT 5,  -- evaluation window in minutes
    severity      VARCHAR(20) NOT NULL,    -- CRITICAL, WARNING, INFO
    service_filter VARCHAR(100),           -- optional: only for specific service
    is_active     BOOLEAN DEFAULT TRUE,
    notify_channel VARCHAR(20),            -- SLACK, PAGERDUTY, EMAIL, WEBHOOK
    notify_target TEXT,                    -- channel URL or email
    created_by    UUID,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- PostgreSQL: alert history (past fired alerts)
CREATE TABLE alert_history (
    alert_id      UUID PRIMARY KEY,
    rule_id       UUID REFERENCES alert_rules(rule_id),
    fired_at      TIMESTAMP NOT NULL,
    resolved_at   TIMESTAMP,
    severity      VARCHAR(20),
    match_count   INT,
    sample_log    TEXT,                    -- sample matching log entry
    acknowledged_by UUID,
    acknowledged_at TIMESTAMP
);
CREATE INDEX idx_alerts_rule ON alert_history(rule_id, fired_at DESC);

-- Kafka: topics
-- "logs-raw" → ingestion topic (partitioned by service name)
-- "logs-enriched" → after parsing/enrichment (partitioned by service name)
-- "alerts" → triggered alert events

-- S3: long-term archive
-- Bucket: "logs-archive/{service}/{year}/{month}/{day}/logs-{hour}.parquet"
```

### Capacity Estimation

```
Assumptions:
  1000 microservices, each producing 1000 logs/sec avg
  Average log size: 500 bytes (structured JSON)

Ingestion rate:
  1000 × 1000 = 1M logs/sec
  1M × 500B = 500 MB/sec = 43 TB/day

Kafka:
  43 TB/day × 3 replicas × 3 days retention = ~387 TB Kafka
  50+ partitions per topic for throughput

Elasticsearch:
  Hot tier (7 days): 43 TB/day × 7 × 1.3 (index overhead) = ~391 TB SSD
  Warm tier (30 days): 43 TB × 23 × 0.8 (compression) = ~794 TB HDD
  S3 (1 year): 43 TB × 335 × 0.3 (compression) = ~4.3 PB

Metrics (Prometheus):
  100K unique time series × 8 bytes × every 15s scrape
  = ~53 MB/sec = 4.5 TB/day
  90-day retention: ~400 TB

Query performance:
  Log search: < 2s for 7-day queries, < 10s for 30-day
  Metrics: < 500ms for dashboard queries (pre-aggregated)
  Real-time tail: < 3s end-to-end latency
```

---

## Q11: Design a Train Ticketing System (IRCTC)

### Requirements

```
Functional:
  • Search trains by source, destination, date
  • View seat availability across classes (1A, 2A, 3A, SL, General)
  • Book tickets (confirmed, RAC, waitlist)
  • Tatkal booking (high-speed window with surge)
  • PNR status check
  • Cancellation and refund
  • Waitlist auto-promotion (when someone cancels)

Non-Functional:
  • Handle 25,000+ bookings/min during Tatkal (10 AM spike)
  • 99.9% availability during booking hours
  • Strong consistency (no double-booking same seat)
  • Scale: 2500+ trains/day, 25M passengers/day
```

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                 IRCTC TRAIN TICKETING ARCHITECTURE                │
│                                                                    │
│  ┌──────────┐       ┌──────────────┐                              │
│  │ Web/     │──────►│ API Gateway  │                              │
│  │ Mobile   │       │ + CDN        │                              │
│  └──────────┘       │ • Rate limit │                              │
│                     │ • Tatkal queue│                              │
│                     └──────┬───────┘                              │
│                            │                                      │
│   ┌────────────────────────┼────────────────────────┐             │
│   │                        │                        │             │
│   ▼                        ▼                        ▼             │
│ ┌──────────┐        ┌──────────────┐         ┌──────────┐        │
│ │ Search   │        │  Booking     │         │ User     │        │
│ │ Service  │        │  Service     │         │ Service  │        │
│ │          │        │              │         │          │        │
│ │ • Trains │        │ • Reserve    │         │ • Auth   │        │
│ │ • Avail. │        │ • Confirm    │         │ • Profile│        │
│ │ • Fares  │        │ • Waitlist   │         │ • History│        │
│ └────┬─────┘        └────┬─────────┘         └────┬─────┘        │
│      │                   │                        │              │
│      ▼                   ▼                        ▼              │
│ ┌──────────┐      ┌──────────────┐         ┌──────────┐         │
│ │ Redis    │      │ PostgreSQL   │         │PostgreSQL│         │
│ │ (avail.  │      │ (bookings,   │         │ (users)  │         │
│ │  cache)  │      │  PNR, wait)  │         └──────────┘         │
│ └──────────┘      └──────┬───────┘                              │
│                          │ events                               │
│                          ▼                                      │
│                   ┌──────────────┐                               │
│                   │    Kafka     │                               │
│                   │              │                               │
│                   │ booking.     │───► Payment Service           │
│                   │ confirmed    │───► Notification (SMS/email)  │
│                   │ waitlist.    │───► Waitlist Promotion Engine │
│                   │ promoted     │───► Analytics                 │
│                   └──────────────┘                               │
│                                                                  │
│  TATKAL FLOW (10:00 AM surge):                                  │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ 1. Virtual Queue: users get a token + position       │       │
│  │    "You are #4521 in queue, estimated wait: 3 min"   │       │
│  │ 2. Token-based admission: 500 users/sec let through  │       │
│  │ 3. Reserved seat held for 10 minutes (Redis TTL)     │       │
│  │ 4. Payment must complete within 10 min or released   │       │
│  │ 5. Dynamic pricing: Tatkal charges = base + premium  │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  WAITLIST FLOW:                                                 │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ Train: 12345 | Class: 3A | Date: 2026-03-20         │       │
│  │ Confirmed: 72/72 (FULL)                               │       │
│  │ RAC: 8/8 (FULL)                                       │       │
│  │ Waitlist: WL1, WL2, WL3 ... WL45                     │       │
│  │                                                        │       │
│  │ When someone cancels:                                  │       │
│  │   RAC-1 → promoted to Confirmed (gets seat number)   │       │
│  │   WL-1  → promoted to RAC                            │       │
│  │   WL-2  → becomes WL-1 (everyone shifts up)          │       │
│  │   Kafka event → SMS: "Your ticket WL-1 is now RAC!" │       │
│  └──────────────────────────────────────────────────────┘       │
└──────────────────────────────────────────────────────────────────┘
```

### Approach Comparison — Handling Tatkal Surge

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Virtual Queue    │ Lottery System   │ First-Come-      │
│                     │ (IRCTC current)  │ (draw-based)     │ First-Served     │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ User experience     │ Wait in queue    │ Submit, wait for │ Fastest finger   │
│                     │ with position    │ result           │ wins             │
│ Server load at 10AM │ Controlled ✅    │ Spread over time │ 25K+ req/sec ❌  │
│ Fairness            │ FIFO order ✅    │ Random (fair?) ✅│ Network speed    │
│                     │                  │                  │ advantage ❌     │
│ Scalability         │ Queue scales     │ Batch processing │ Must handle      │
│                     │ horizontally     │ ✅               │ spike            │
│ Perception          │ "Waiting" feels  │ "Luck" → user    │ "Why can't I     │
│                     │ fair             │ frustration      │ get in?" ❌      │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Prevents server  │ Eliminates surge │ Simple, no queue │
│                     │ crash, fair FIFO │ No server spike  │ infrastructure   │
│                     │ Transparent      │ Everyone equal   │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Queue infra      │ Longer wait for  │ Crashes at spike │
│                     │ complexity       │ result           │ Unfair to slow   │
│                     │                  │ User distrust    │ connections      │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ IRCTC Tatkal ✅  │ Concert/event    │ Low-traffic      │
│                     │ High-demand      │ tickets          │ booking systems  │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Approach Comparison — Seat Allocation

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Pessimistic Lock │ Optimistic Lock  │ Redis Distributed│
│                     │ (SELECT FOR UPD) │ (version check)  │ Lock (Redlock)   │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Concurrency         │ Low (blocks) ❌  │ High (retry on   │ High ✅          │
│                     │                  │ conflict) ✅     │                  │
│ Consistency         │ Strong ✅        │ Strong ✅        │ Eventually       │
│ Throughput          │ Low (lock waits) │ Moderate         │ High ✅          │
│ Deadlock risk       │ Yes ❌           │ No ✅            │ No ✅            │
│ Implementation      │ Simple (SQL)     │ Version column   │ Redis + TTL      │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Low concurrency  │ Moderate traffic │ High-surge       │
│                     │ banking          │ booking systems  │ Tatkal ✅        │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class Train {
    private String trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private List<TrainStop> stops;
    private Map<SeatClass, Integer> totalSeats; // 3A → 72, SL → 300
    private List<DayOfWeek> runningDays;
}

public class Booking {
    private String pnr;
    private String trainNumber;
    private LocalDate journeyDate;
    private String source;
    private String destination;
    private SeatClass seatClass;
    private List<Passenger> passengers;
    private BookingStatus status; // CONFIRMED, RAC, WAITLISTED, CANCELLED
    private int waitlistNumber; // 0 if confirmed
    private String seatNumber; // null if waitlisted
    private BigDecimal fare;
    private Instant bookedAt;
}

public enum SeatClass { FIRST_AC, SECOND_AC, THIRD_AC, SLEEPER, GENERAL }
public enum BookingStatus { CONFIRMED, RAC, WAITLISTED, CANCELLED }

// --- Booking Service ---
@Service
public class TrainBookingService {

    private final SeatAvailabilityService availabilityService;
    private final BookingRepository bookingRepository;
    private final PaymentService paymentService;
    private final RedisTemplate<String, String> redis;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;
    private final WaitlistService waitlistService;

    private static final Duration SEAT_HOLD_TTL = Duration.ofMinutes(10);

    @Transactional
    public BookingResult bookTicket(BookingRequest request) {
        String lockKey = buildLockKey(request.getTrainNumber(),
            request.getJourneyDate(), request.getSeatClass());

        // Step 1: Acquire distributed lock for this train+date+class combo
        String lockValue = UUID.randomUUID().toString();
        boolean locked = redis.opsForValue()
            .setIfAbsent(lockKey, lockValue, Duration.ofSeconds(30));

        if (!locked) {
            throw new ConcurrentBookingException("Please try again in a moment");
        }

        try {
            SeatAvailability availability = availabilityService.getAvailability(
                request.getTrainNumber(), request.getJourneyDate(),
                request.getSeatClass());

            String pnr = generatePNR();
            int passengerCount = request.getPassengers().size();

            if (availability.getConfirmedAvailable() >= passengerCount) {
                return bookConfirmed(pnr, request, availability);
            } else if (availability.getRacAvailable() >= passengerCount) {
                return bookRAC(pnr, request, availability);
            } else if (availability.getWaitlistPosition() + passengerCount <= 200) {
                return bookWaitlist(pnr, request, availability);
            } else {
                throw new NoAvailabilityException("No seats available, waitlist full");
            }

        } finally {
            // Release lock only if we still own it
            String currentValue = redis.opsForValue().get(lockKey);
            if (lockValue.equals(currentValue)) {
                redis.delete(lockKey);
            }
        }
    }

    private BookingResult bookConfirmed(String pnr, BookingRequest request,
                                         SeatAvailability availability) {
        List<String> seatNumbers = availability.allocateSeats(
            request.getPassengers().size());

        Booking booking = createBooking(pnr, request, BookingStatus.CONFIRMED);
        booking.setSeatNumber(String.join(",", seatNumbers));
        bookingRepository.save(booking);

        availabilityService.decrementAvailability(request.getTrainNumber(),
            request.getJourneyDate(), request.getSeatClass(),
            request.getPassengers().size());

        // Hold seat for 10 min — must pay within this window
        redis.opsForValue().set("hold:" + pnr, "PENDING", SEAT_HOLD_TTL);

        kafkaTemplate.send("booking-events", pnr,
            new BookingEvent("CONFIRMED", pnr, request.getTrainNumber()));

        return new BookingResult(pnr, BookingStatus.CONFIRMED,
            seatNumbers, calculateFare(request));
    }

    private BookingResult bookWaitlist(String pnr, BookingRequest request,
                                        SeatAvailability availability) {
        int wlNumber = availability.getNextWaitlistNumber();

        Booking booking = createBooking(pnr, request, BookingStatus.WAITLISTED);
        booking.setWaitlistNumber(wlNumber);
        bookingRepository.save(booking);

        waitlistService.addToWaitlist(request.getTrainNumber(),
            request.getJourneyDate(), request.getSeatClass(), pnr, wlNumber);

        kafkaTemplate.send("booking-events", pnr,
            new BookingEvent("WAITLISTED", pnr, "WL" + wlNumber));

        return new BookingResult(pnr, BookingStatus.WAITLISTED,
            List.of("WL" + wlNumber), calculateFare(request));
    }

    @Transactional
    public void cancelBooking(String pnr) {
        Booking booking = bookingRepository.findByPnr(pnr)
            .orElseThrow(() -> new NotFoundException("PNR not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Release seat and trigger waitlist promotion
        availabilityService.incrementAvailability(booking.getTrainNumber(),
            booking.getJourneyDate(), booking.getSeatClass(),
            booking.getPassengers().size());

        kafkaTemplate.send("cancellation-events", pnr,
            new CancellationEvent(pnr, booking.getTrainNumber(),
                booking.getJourneyDate(), booking.getSeatClass()));

        paymentService.initiateRefund(pnr, calculateRefund(booking));
    }

    private String generatePNR() {
        return String.format("%010d", ThreadLocalRandom.current().nextLong(1_000_000_000L));
    }
}

// --- Waitlist Promotion Service ---
@Service
public class WaitlistService {

    private final BookingRepository bookingRepository;
    private final SeatAvailabilityService availabilityService;
    private final NotificationService notificationService;

    @KafkaListener(topics = "cancellation-events")
    public void onCancellation(CancellationEvent event) {
        List<Booking> waitlisted = bookingRepository.findWaitlistedByTrainAndDate(
            event.getTrainNumber(), event.getJourneyDate(), event.getSeatClass());

        if (waitlisted.isEmpty()) return;

        // Promote WL-1 → RAC or Confirmed
        Booking toPromote = waitlisted.get(0);
        SeatAvailability avail = availabilityService.getAvailability(
            event.getTrainNumber(), event.getJourneyDate(), event.getSeatClass());

        if (avail.getConfirmedAvailable() > 0) {
            String seat = avail.allocateSeats(1).get(0);
            toPromote.setStatus(BookingStatus.CONFIRMED);
            toPromote.setSeatNumber(seat);
            toPromote.setWaitlistNumber(0);
        } else if (avail.getRacAvailable() > 0) {
            toPromote.setStatus(BookingStatus.RAC);
            toPromote.setWaitlistNumber(0);
        }

        bookingRepository.save(toPromote);

        // Shift all remaining waitlist numbers up
        for (int i = 1; i < waitlisted.size(); i++) {
            Booking wl = waitlisted.get(i);
            wl.setWaitlistNumber(wl.getWaitlistNumber() - 1);
            bookingRepository.save(wl);
        }

        notificationService.sendSms(toPromote.getPassengers().get(0).getPhone(),
            "PNR " + toPromote.getPnr() + " promoted to " + toPromote.getStatus()
            + (toPromote.getSeatNumber() != null ? " Seat: " + toPromote.getSeatNumber() : ""));
    }
}

// --- Tatkal Queue Service ---
@Service
public class TatkalQueueService {

    private final RedisTemplate<String, String> redis;
    private static final int MAX_THROUGHPUT_PER_SEC = 500;

    public TatkalQueueResponse joinQueue(String userId, String trainNumber) {
        String queueKey = "tatkal:queue:" + trainNumber + ":" + LocalDate.now();
        long position = redis.opsForList().rightPush(queueKey, userId);
        redis.expire(queueKey, Duration.ofHours(2));

        long estimatedWaitSeconds = position / MAX_THROUGHPUT_PER_SEC;
        String token = UUID.randomUUID().toString();
        redis.opsForValue().set("tatkal:token:" + token, userId,
            Duration.ofMinutes(15));

        return new TatkalQueueResponse(token, position, estimatedWaitSeconds);
    }

    @Scheduled(fixedRate = 1000) // every second
    public void processQueue() {
        // Let MAX_THROUGHPUT_PER_SEC users through per second
        // across all train queues
    }
}
```

### Database Schema

```sql
-- PostgreSQL: trains master data
CREATE TABLE trains (
    train_id      BIGINT PRIMARY KEY,     -- e.g., 12301 (Rajdhani)
    train_name    VARCHAR(200) NOT NULL,
    train_type    VARCHAR(30) NOT NULL,    -- RAJDHANI, SHATABDI, SUPERFAST, EXPRESS, LOCAL
    source_stn    VARCHAR(10) REFERENCES stations(station_code),
    dest_stn      VARCHAR(10) REFERENCES stations(station_code),
    departure_time TIME NOT NULL,
    arrival_time  TIME NOT NULL,
    days_of_week  BIT(7) NOT NULL,        -- bit mask: Mon=1, Tue=2, ...
    total_distance_km INT,
    is_active     BOOLEAN DEFAULT TRUE
);

-- PostgreSQL: stations
CREATE TABLE stations (
    station_code  VARCHAR(10) PRIMARY KEY, -- e.g., 'NDLS', 'BCT'
    station_name  VARCHAR(200) NOT NULL,
    city          VARCHAR(100),
    state         VARCHAR(50),
    zone          VARCHAR(10),             -- NR, SR, ER, WR, etc.
    latitude      DOUBLE PRECISION,
    longitude     DOUBLE PRECISION
);

-- PostgreSQL: train routes (stops in order)
CREATE TABLE train_routes (
    train_id      BIGINT REFERENCES trains(train_id),
    stop_order    INT NOT NULL,
    station_code  VARCHAR(10) REFERENCES stations(station_code),
    arrival_time  TIME,
    departure_time TIME,
    halt_minutes  INT DEFAULT 2,
    distance_km   INT NOT NULL,
    PRIMARY KEY (train_id, stop_order)
);

-- PostgreSQL: coaches and seat inventory
CREATE TABLE coaches (
    coach_id      UUID PRIMARY KEY,
    train_id      BIGINT REFERENCES trains(train_id),
    coach_type    VARCHAR(10) NOT NULL,    -- 1A, 2A, 3A, SL, CC, 2S
    coach_number  VARCHAR(10) NOT NULL,    -- S1, S2, B1, A1
    total_seats   INT NOT NULL,
    UNIQUE(train_id, coach_number)
);

-- PostgreSQL: seat availability per date per segment
CREATE TABLE seat_availability (
    availability_id UUID PRIMARY KEY,
    train_id      BIGINT REFERENCES trains(train_id),
    coach_type    VARCHAR(10) NOT NULL,
    travel_date   DATE NOT NULL,
    from_stn      VARCHAR(10) REFERENCES stations(station_code),
    to_stn        VARCHAR(10) REFERENCES stations(station_code),
    total_seats   INT NOT NULL,
    booked_seats  INT DEFAULT 0,
    rac_limit     INT NOT NULL,
    rac_booked    INT DEFAULT 0,
    waitlist_count INT DEFAULT 0,
    version       INT DEFAULT 1,           -- optimistic locking
    UNIQUE(train_id, coach_type, travel_date, from_stn, to_stn)
);
CREATE INDEX idx_avail_search ON seat_availability(train_id, travel_date, coach_type);

-- PostgreSQL: bookings (PNR records)
CREATE TABLE bookings (
    pnr           VARCHAR(10) PRIMARY KEY, -- e.g., '4512345678'
    user_id       UUID REFERENCES users(user_id),
    train_id      BIGINT REFERENCES trains(train_id),
    travel_date   DATE NOT NULL,
    from_stn      VARCHAR(10) REFERENCES stations(station_code),
    to_stn        VARCHAR(10) REFERENCES stations(station_code),
    coach_type    VARCHAR(10) NOT NULL,
    booking_type  VARCHAR(10) NOT NULL,    -- GENERAL, TATKAL, PREMIUM_TATKAL
    status        VARCHAR(20) NOT NULL,    -- CONFIRMED, RAC, WAITLIST, CANCELLED
    total_fare    DECIMAL(10,2) NOT NULL,
    payment_id    UUID,
    booked_at     TIMESTAMP DEFAULT NOW(),
    cancelled_at  TIMESTAMP
);
CREATE INDEX idx_bookings_user ON bookings(user_id, booked_at DESC);
CREATE INDEX idx_bookings_train_date ON bookings(train_id, travel_date);

-- PostgreSQL: passengers per PNR
CREATE TABLE passengers (
    passenger_id  UUID PRIMARY KEY,
    pnr           VARCHAR(10) REFERENCES bookings(pnr),
    name          VARCHAR(100) NOT NULL,
    age           INT NOT NULL,
    gender        CHAR(1),                -- M, F, O
    seat_number   VARCHAR(10),            -- e.g., 'S1-32' (null for WL)
    berth_type    VARCHAR(10),            -- LOWER, MIDDLE, UPPER, SIDE_LOWER, SIDE_UPPER
    status        VARCHAR(20) NOT NULL,   -- CONFIRMED, RAC, WL-5, CANCELLED
    id_type       VARCHAR(20),            -- AADHAAR, PAN, PASSPORT
    id_number     VARCHAR(50)
);
CREATE INDEX idx_passengers_pnr ON passengers(pnr);

-- Redis: real-time availability cache
-- Key: "avail:{train_id}:{date}:{coach_type}" → Hash { total, booked, rac, wl }
-- Key: "tatkal_queue:{train_id}:{date}" → List (virtual queue tokens)
-- Key: "lock:seat:{train_id}:{date}:{coach}" → distributed lock for booking
```

### Capacity Estimation

```
Assumptions:
  2500 trains/day, 25M passengers/day
  Tatkal window: 10:00-10:30 AM → 500K bookings in 30 min

Normal QPS:
  25M / 86400 ≈ 290 bookings/sec

Tatkal peak:
  500K / 1800 sec ≈ 278 bookings/sec (sustained)
  Spike at 10:00:00 → 25,000+ requests/sec (need queue)

Storage (5 years):
  25M bookings/day × 365 × 5 × 2KB = ~91 TB
  Train schedules: ~500 MB (relatively small)

Cache (Redis):
  Availability for 2500 trains × 365 days × 5 classes = 4.5M keys
  Each key: ~200 bytes → ~900 MB (fits in single Redis node)
```

---

## Q12: Design a Bus Ticketing System (RedBus)

### Requirements

```
Functional:
  • Search buses by route, date, bus type (AC/Non-AC, Sleeper/Seater)
  • View seat map with availability (specific seat selection)
  • Book tickets with seat selection
  • Multi-operator aggregation (200+ operators)
  • Dynamic pricing based on demand
  • Boarding/dropping point selection
  • Cancellation with operator-specific refund policies
  • Live bus tracking (GPS)

Non-Functional:
  • Handle 5M+ bookings/day during festivals
  • Sub-second seat map loading
  • Real-time seat availability (no overselling)
  • 99.9% availability
```

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                  BUS TICKETING ARCHITECTURE                       │
│                                                                    │
│  ┌──────────┐       ┌──────────────┐                              │
│  │ Web/     │──────►│ API Gateway  │                              │
│  │ Mobile   │       │ + Rate Limit │                              │
│  └──────────┘       └──────┬───────┘                              │
│                            │                                      │
│  ┌─────────────────────────┼──────────────────────────┐           │
│  │                         │                          │           │
│  ▼                         ▼                          ▼           │
│ ┌──────────┐       ┌──────────────┐           ┌──────────┐       │
│ │ Search   │       │  Booking     │           │ Operator │       │
│ │ Service  │       │  Service     │           │ Service  │       │
│ │          │       │              │           │          │       │
│ │ • Routes │       │ • Seat Lock  │           │ • Mgmt   │       │
│ │ • Filters│       │ • Book      │           │ • Pricing│       │
│ │ • Sort   │       │ • Cancel    │           │ • Policy │       │
│ └────┬─────┘       └────┬────────┘           └────┬─────┘       │
│      │                  │                         │              │
│      ▼                  ▼                         ▼              │
│ ┌──────────┐      ┌──────────────┐        ┌──────────────┐      │
│ │Elastic-  │      │ PostgreSQL   │        │ Operator     │      │
│ │search    │      │ (bookings)   │        │ Inventory    │      │
│ │(routes,  │      └──────────────┘        │ APIs         │      │
│ │ buses)   │                              │(200+ systems)│      │
│ └──────────┘                              └──────────────┘      │
│                                                                  │
│  SEAT MAP & BOOKING FLOW:                                       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ Bus Layout (40-seater Volvo):                         │       │
│  │                                                        │       │
│  │  Lower Deck:        Upper Deck:                       │       │
│  │  [D] [1A][1B]  [1C] [U] [1D][1E]  [1F]             │       │
│  │      [2A][2B]  [2C]     [2D][2E]  [2F]             │       │
│  │      [3A][3B]  [3C]     [3D][3E]  [3F]             │       │
│  │      [4A][4B]  [4C]     [4D][4E]  [4F]             │       │
│  │      [5A][5B]  [5C]     [5D][5E]  [5F]             │       │
│  │                                                        │       │
│  │  ■ Booked  □ Available  ▨ Selected (held 8 min)      │       │
│  │                                                        │       │
│  │  Step 1: User selects seats 3A, 3B ─► Redis hold 8min│       │
│  │  Step 2: Fills passenger details                      │       │
│  │  Step 3: Payment (within 8 min)                       │       │
│  │  Step 4: Confirm → update operator inventory          │       │
│  │  If timeout → seats released automatically            │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  MULTI-OPERATOR AGGREGATION:                                    │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ Route: Bangalore → Chennai (350 km, ~6 hours)        │       │
│  │                                                        │       │
│  │  Operator    Type        Depart  Price  Available     │       │
│  │  ─────────── ─────────── ─────── ────── ──────────   │       │
│  │  VRL Travels AC Sleeper  22:00   ₹850   23/40 seats  │       │
│  │  SRS Travels AC Seater   22:30   ₹650   12/45 seats  │       │
│  │  Orange Bus  Non-AC Semi 23:00   ₹450   30/50 seats  │       │
│  │  KSRTC       AC Seater   23:30   ₹500   8/52 seats   │       │
│  │                                                        │       │
│  │  Each operator has their OWN inventory system.        │       │
│  │  RedBus aggregates via APIs + webhooks.               │       │
│  └──────────────────────────────────────────────────────┘       │
└──────────────────────────────────────────────────────────────────┘
```

### Approach Comparison — Inventory Management

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Real-Time API    │ Cached Inventory │ Hybrid (RedBus)  │
│                     │ (query operator) │ (periodic sync)  │ ✅               │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Accuracy            │ 100% real-time ✅│ Stale (5-15 min) │ Near real-time   │
│ Latency             │ 200-500ms ❌     │ 5ms (cache) ✅   │ 5ms + async sync │
│ Operator dependency │ High (if API     │ Low (cache works │ Balanced         │
│                     │ slow/down) ❌    │ during outage) ✅│                  │
│ Overbooking risk    │ None             │ Possible ❌      │ Low (verify at   │
│                     │                  │                  │ booking time)    │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Small operators  │ Read-heavy       │ Marketplace ✅   │
│                     │ low traffic      │ search pages     │ aggregators      │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class Bus {
    private String busId;
    private String operatorId;
    private String routeId;
    private BusType type; // AC_SLEEPER, AC_SEATER, NON_AC_SEATER
    private int totalSeats;
    private List<SeatLayout> seatLayout;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private List<BoardingPoint> boardingPoints;
    private List<DroppingPoint> droppingPoints;
}

public class SeatLayout {
    private String seatNumber; // "1A", "2B", "U3C" (upper deck)
    private SeatType seatType; // WINDOW, AISLE, MIDDLE
    private boolean isLowerDeck;
    private BigDecimal price;
}

public class BusBooking {
    private String bookingId;
    private String busId;
    private LocalDate travelDate;
    private List<String> seatNumbers;
    private List<Passenger> passengers;
    private String boardingPointId;
    private String droppingPointId;
    private BigDecimal totalFare;
    private BookingStatus status;
    private Instant bookedAt;
}

// --- Seat Selection and Locking ---
@Service
public class SeatLockService {

    private final RedisTemplate<String, String> redis;
    private static final Duration SEAT_HOLD_TTL = Duration.ofMinutes(8);

    public SeatLockResult lockSeats(String busId, LocalDate date,
                                     List<String> seatNumbers, String userId) {
        String inventoryKey = "bus:seats:" + busId + ":" + date;

        // Use Redis transaction to atomically check and lock seats
        List<Object> results = redis.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) {
                operations.multi();
                for (String seat : seatNumbers) {
                    String seatKey = inventoryKey + ":" + seat;
                    operations.opsForValue().setIfAbsent(seatKey, userId, SEAT_HOLD_TTL);
                }
                return operations.exec();
            }
        });

        // Check if all seats were successfully locked
        List<String> lockedSeats = new ArrayList<>();
        List<String> failedSeats = new ArrayList<>();

        for (int i = 0; i < seatNumbers.size(); i++) {
            if (Boolean.TRUE.equals(results.get(i))) {
                lockedSeats.add(seatNumbers.get(i));
            } else {
                failedSeats.add(seatNumbers.get(i));
            }
        }

        if (!failedSeats.isEmpty()) {
            // Rollback: release any seats we did lock
            for (String seat : lockedSeats) {
                redis.delete(inventoryKey + ":" + seat);
            }
            throw new SeatUnavailableException("Seats " + failedSeats + " already taken");
        }

        return new SeatLockResult(lockedSeats, SEAT_HOLD_TTL);
    }

    public void releaseSeats(String busId, LocalDate date, List<String> seatNumbers) {
        String inventoryKey = "bus:seats:" + busId + ":" + date;
        for (String seat : seatNumbers) {
            redis.delete(inventoryKey + ":" + seat);
        }
    }
}

// --- Bus Booking Service ---
@Service
public class BusBookingService {

    private final SeatLockService seatLockService;
    private final BookingRepository bookingRepository;
    private final OperatorGateway operatorGateway;
    private final PaymentService paymentService;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    @Transactional
    public BusBooking bookTicket(BusBookingRequest request) {
        // Step 1: Lock seats in Redis
        seatLockService.lockSeats(request.getBusId(), request.getTravelDate(),
            request.getSeatNumbers(), request.getUserId());

        try {
            // Step 2: Verify with operator's real-time inventory
            boolean operatorConfirmed = operatorGateway.verifyAndReserve(
                request.getBusId(), request.getTravelDate(),
                request.getSeatNumbers());

            if (!operatorConfirmed) {
                seatLockService.releaseSeats(request.getBusId(),
                    request.getTravelDate(), request.getSeatNumbers());
                throw new OperatorRejectedException("Operator inventory mismatch");
            }

            // Step 3: Process payment
            BigDecimal fare = calculateFare(request);
            PaymentResult payment = paymentService.charge(
                request.getUserId(), fare, request.getPaymentMethod());

            // Step 4: Create booking
            BusBooking booking = new BusBooking();
            booking.setBookingId(generateBookingId());
            booking.setBusId(request.getBusId());
            booking.setTravelDate(request.getTravelDate());
            booking.setSeatNumbers(request.getSeatNumbers());
            booking.setPassengers(request.getPassengers());
            booking.setTotalFare(fare);
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setBookedAt(Instant.now());
            bookingRepository.save(booking);

            // Step 5: Confirm with operator
            operatorGateway.confirmBooking(request.getBusId(),
                booking.getBookingId(), request.getSeatNumbers());

            kafkaTemplate.send("bus-booking-events", booking.getBookingId(),
                new BookingEvent("CONFIRMED", booking));

            return booking;

        } catch (Exception e) {
            seatLockService.releaseSeats(request.getBusId(),
                request.getTravelDate(), request.getSeatNumbers());
            throw e;
        }
    }
}

// --- Search Service with Dynamic Pricing ---
@Service
public class BusSearchService {

    private final ElasticsearchClient esClient;
    private final PricingEngine pricingEngine;

    public List<BusSearchResult> search(String source, String destination,
                                         LocalDate date, BusType type) {
        SearchResponse<Bus> response = esClient.search(s -> s
            .index("buses")
            .query(q -> q.bool(b -> b
                .must(m -> m.term(t -> t.field("route.source").value(source)))
                .must(m -> m.term(t -> t.field("route.destination").value(destination)))
                .must(m -> m.term(t -> t.field("runningDays").value(date.getDayOfWeek().name())))
                .filter(f -> type != null ? f.term(t -> t.field("type").value(type.name())) : f.matchAll(ma -> ma))
            ))
            .sort(sort -> sort.field(f -> f.field("departureTime").order(SortOrder.Asc))),
            Bus.class);

        return response.hits().hits().stream()
            .map(hit -> {
                Bus bus = hit.source();
                int available = getAvailableSeatCount(bus.getBusId(), date);
                BigDecimal dynamicPrice = pricingEngine.calculate(
                    bus, date, available);
                return new BusSearchResult(bus, available, dynamicPrice);
            })
            .collect(Collectors.toList());
    }
}
```

### Database Schema

```sql
-- PostgreSQL: bus operators
CREATE TABLE operators (
    operator_id   UUID PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    logo_url      TEXT,
    rating        DECIMAL(2,1) DEFAULT 0.0,
    review_count  INT DEFAULT 0,
    is_active     BOOLEAN DEFAULT TRUE,
    api_endpoint  VARCHAR(500),           -- operator inventory API URL
    api_key       VARCHAR(200),
    created_at    TIMESTAMP DEFAULT NOW()
);

-- PostgreSQL: buses
CREATE TABLE buses (
    bus_id        UUID PRIMARY KEY,
    operator_id   UUID REFERENCES operators(operator_id),
    bus_type      VARCHAR(30) NOT NULL,   -- AC_SLEEPER, NON_AC_SLEEPER, AC_SEATER, VOLVO_AC
    bus_name      VARCHAR(200),
    total_seats   INT NOT NULL,
    amenities     TEXT[],                 -- {'WiFi', 'Charging', 'Blanket', 'Water'}
    source_city   VARCHAR(100) NOT NULL,
    dest_city     VARCHAR(100) NOT NULL,
    via_cities    TEXT[],                 -- intermediate stops
    departure_time TIME NOT NULL,
    arrival_time  TIME NOT NULL,
    duration_min  INT,
    base_price    DECIMAL(10,2) NOT NULL,
    is_active     BOOLEAN DEFAULT TRUE
);
CREATE INDEX idx_buses_route ON buses(source_city, dest_city);
CREATE INDEX idx_buses_operator ON buses(operator_id);

-- PostgreSQL: seat layout (per bus)
CREATE TABLE seat_layout (
    seat_id       UUID PRIMARY KEY,
    bus_id        UUID REFERENCES buses(bus_id),
    seat_number   VARCHAR(10) NOT NULL,   -- '1A', '1B', 'L1', 'L2'
    seat_type     VARCHAR(20) NOT NULL,   -- SEATER, SLEEPER, SEMI_SLEEPER
    deck          VARCHAR(10),            -- LOWER, UPPER
    row_num       INT,
    col_num       INT,
    is_window     BOOLEAN DEFAULT FALSE,
    is_aisle      BOOLEAN DEFAULT FALSE,
    is_ladies_only BOOLEAN DEFAULT FALSE,
    price_tier    VARCHAR(10) DEFAULT 'STANDARD',  -- STANDARD, PREMIUM
    UNIQUE(bus_id, seat_number)
);
CREATE INDEX idx_layout_bus ON seat_layout(bus_id);

-- PostgreSQL: trip schedules (specific date instances)
CREATE TABLE trip_schedules (
    trip_id       UUID PRIMARY KEY,
    bus_id        UUID REFERENCES buses(bus_id),
    travel_date   DATE NOT NULL,
    available_seats INT NOT NULL,
    dynamic_price DECIMAL(10,2),
    status        VARCHAR(20) DEFAULT 'SCHEDULED',  -- SCHEDULED, IN_TRANSIT, COMPLETED, CANCELLED
    UNIQUE(bus_id, travel_date)
);
CREATE INDEX idx_trips_search ON trip_schedules(bus_id, travel_date);

-- PostgreSQL: bookings
CREATE TABLE bus_bookings (
    booking_id    UUID PRIMARY KEY,
    trip_id       UUID REFERENCES trip_schedules(trip_id),
    user_id       UUID REFERENCES users(user_id),
    operator_id   UUID REFERENCES operators(operator_id),
    status        VARCHAR(20) NOT NULL,   -- PENDING, CONFIRMED, CANCELLED, COMPLETED
    total_fare    DECIMAL(10,2) NOT NULL,
    passenger_count INT NOT NULL,
    contact_phone VARCHAR(20),
    contact_email VARCHAR(255),
    boarding_point VARCHAR(200),
    dropping_point VARCHAR(200),
    payment_id    UUID,
    booked_at     TIMESTAMP DEFAULT NOW(),
    cancelled_at  TIMESTAMP
);
CREATE INDEX idx_bus_bookings_user ON bus_bookings(user_id, booked_at DESC);
CREATE INDEX idx_bus_bookings_trip ON bus_bookings(trip_id);

-- PostgreSQL: booked seats per booking
CREATE TABLE booked_seats (
    booking_id    UUID REFERENCES bus_bookings(booking_id),
    seat_id       UUID REFERENCES seat_layout(seat_id),
    passenger_name VARCHAR(100) NOT NULL,
    passenger_age  INT,
    passenger_gender CHAR(1),
    PRIMARY KEY (booking_id, seat_id)
);

-- Redis: seat locks during checkout
-- Key: "seat_lock:{trip_id}:{seat_id}" → user_id (TTL: 10 min)
-- Key: "trip_avail:{trip_id}" → available seat count
```

### Capacity Estimation

```
Assumptions:
  5M bookings/day, 200+ operators, 50K buses/day
  Festival peak: 3x normal → 15M bookings/day

Search QPS:
  50M searches/day ≈ 580/sec (10:1 search:book ratio)
  Peak (festival evenings): 3000/sec

Booking QPS:
  5M/day ≈ 58/sec average, peak 500/sec

Seat lock (Redis):
  50K buses × 40 seats = 2M seat keys
  Each: ~100 bytes → 200 MB (fits easily)

Storage (5 years):
  5M/day × 365 × 5 × 1KB = ~9 TB bookings
  Route data: ~100 MB (all routes for India)
```

---

## Q13: Design a Movie Ticketing System (BookMyShow)

### Requirements

```
Functional:
  • Browse movies by city, genre, language
  • View showtimes across multiplexes/cinemas
  • Interactive seat map selection (Regular, Premium, Recliner)
  • Book tickets with seat selection
  • Apply promo codes / offers
  • F&B (food & beverage) add-ons
  • E-ticket with QR code
  • Cancellation/refund

Non-Functional:
  • Handle 10M+ bookings on blockbuster release day (e.g., Avengers)
  • Seat lock timeout: 8-10 minutes
  • No double-booking (strong consistency for seat allocation)
  • Low latency seat map rendering (< 200ms)
  • 99.95% availability
```

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                MOVIE TICKETING ARCHITECTURE                       │
│                                                                    │
│  ┌──────────┐       ┌──────────────┐                              │
│  │ Web/     │──────►│ API Gateway  │                              │
│  │ Mobile   │       │ + CDN        │                              │
│  └──────────┘       └──────┬───────┘                              │
│                            │                                      │
│  ┌─────────────────────────┼──────────────────────────┐           │
│  │                         │                          │           │
│  ▼                         ▼                          ▼           │
│ ┌──────────┐       ┌──────────────┐           ┌──────────┐       │
│ │ Movie    │       │  Booking     │           │ Cinema   │       │
│ │ Catalog  │       │  Service     │           │ Service  │       │
│ │ Service  │       │              │           │          │       │
│ │ • Movies │       │ • Seat Lock  │           │ • Halls  │       │
│ │ • Shows  │       │ • Book      │           │ • Layout │       │
│ │ • Reviews│       │ • F&B addons│           │ • Shows  │       │
│ └────┬─────┘       └────┬────────┘           └────┬─────┘       │
│      │                  │                         │              │
│      ▼                  ▼                         ▼              │
│ ┌──────────┐      ┌──────────────┐        ┌──────────────┐      │
│ │Elastic-  │      │ PostgreSQL   │        │ PostgreSQL   │      │
│ │search    │      │ (bookings)   │        │ (cinemas,    │      │
│ │(movies,  │      │              │        │  screens,    │      │
│ │ shows)   │      │ Redis (seat  │        │  showtimes)  │      │
│ └──────────┘      │  locks)      │        └──────────────┘      │
│                   └──────────────┘                               │
│                                                                  │
│  SEAT MAP — Interactive Theater Layout:                         │
│  ┌──────────────────────────────────────────────────────┐       │
│  │              ███ SCREEN ███                           │       │
│  │                                                        │       │
│  │  RECLINER (₹500):                                    │       │
│  │  [A1]■ [A2]□ [A3]□ [A4]■ [A5]□ [A6]□ [A7]■ [A8]□  │       │
│  │                                                        │       │
│  │  PREMIUM (₹300):                                     │       │
│  │  [B1]□ [B2]□ [B3]■ [B4]■ [B5]□ [B6]□ [B7]□ [B8]■  │       │
│  │  [C1]□ [C2]□ [C3]□ [C4]□ [C5]■ [C6]■ [C7]□ [C8]□  │       │
│  │  [D1]□ [D2]■ [D3]□ [D4]□ [D5]□ [D6]□ [D7]■ [D8]□  │       │
│  │                                                        │       │
│  │  REGULAR (₹150):                                     │       │
│  │  [E1]□ [E2]□ [E3]□ [E4]□ [E5]□ [E6]□ [E7]□ [E8]□  │       │
│  │  [F1]□ [F2]□ [F3]□ [F4]□ [F5]□ [F6]□ [F7]□ [F8]□  │       │
│  │  [G1]□ [G2]□ [G3]□ [G4]□ [G5]□ [G6]□ [G7]□ [G8]□  │       │
│  │                                                        │       │
│  │  ■ Booked  □ Available  ▨ Your selection              │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  BLOCKBUSTER RELEASE DAY FLOW:                                  │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ Movie: "Avengers: Secret Wars"                        │       │
│  │ Booking opens at 12:00 AM                             │       │
│  │                                                        │       │
│  │ 11:59 PM: 2 million users waiting on seat map page   │       │
│  │ 12:00 AM: Virtual queue activated                     │       │
│  │           Position shown: "You are #45,821"           │       │
│  │           Let 1000 users/sec into booking flow        │       │
│  │ 12:01 AM: Most shows in Mumbai already FULL           │       │
│  │ 12:05 AM: Surge pricing activated for remaining shows │       │
│  └──────────────────────────────────────────────────────┘       │
└──────────────────────────────────────────────────────────────────┘
```

### Approach Comparison — Seat Locking Strategy

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Optimistic Lock  │ Redis TTL Lock   │ Database Lock    │
│                     │ (check at book)  │ (BookMyShow) ✅  │ (SELECT FOR UPD) │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ How it works        │ No lock during   │ Lock seat in     │ DB-level row lock│
│                     │ selection, check │ Redis with 8-min │ when user selects│
│                     │ at payment time  │ TTL expiry       │ seat             │
│ UX                  │ "Sorry, taken"   │ Seat held while  │ Seat held while  │
│                     │ at payment ❌    │ you pay ✅       │ you pay ✅       │
│ Concurrency         │ High (no locks)  │ High (Redis) ✅  │ Low (DB locks) ❌│
│ Auto-release        │ N/A              │ TTL auto-expiry  │ Manual cleanup   │
│                     │                  │ ✅               │ needed ❌        │
│ Scalability         │ Best ✅          │ Excellent ✅     │ Poor ❌          │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ No lock overhead │ Fast, auto-expire│ Strong ACID      │
│                     │ Highest throughput│ Survives app     │ Simple (SQL)     │
│                     │                  │ crashes          │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Bad UX (reject   │ Redis cluster    │ Doesn't scale    │
│                     │ at last step)    │ needed           │ DB bottleneck    │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Low-contention   │ MovieTicketing ✅│ Small cinemas    │
│                     │ (few concurrent) │ High concurrency │ single-server    │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class Movie {
    private String movieId;
    private String title;
    private String language;
    private Genre genre;
    private int durationMinutes;
    private String posterUrl;
    private double rating;
    private LocalDate releaseDate;
}

public class Show {
    private String showId;
    private String movieId;
    private String cinemaId;
    private String screenId;
    private LocalDateTime showTime;
    private Map<SeatCategory, BigDecimal> pricing; // RECLINER → 500, PREMIUM → 300
}

public class Seat {
    private String seatId; // "A1", "B3"
    private SeatCategory category; // RECLINER, PREMIUM, REGULAR
    private int row;
    private int column;
}

public enum SeatCategory { RECLINER, PREMIUM, REGULAR }

public class MovieBooking {
    private String bookingId;
    private String showId;
    private String userId;
    private List<String> seatIds;
    private BigDecimal ticketAmount;
    private BigDecimal convenienceFee;
    private BigDecimal foodAmount;
    private List<FoodItem> foodItems;
    private BigDecimal totalAmount;
    private String promoCode;
    private BigDecimal discount;
    private BookingStatus status;
    private String qrCode;
    private Instant bookedAt;
}

// --- Movie Booking Service ---
@Service
public class MovieBookingService {

    private final SeatLockService seatLockService;
    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final PaymentService paymentService;
    private final PromoService promoService;
    private final QRCodeGenerator qrGenerator;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    private static final Duration SEAT_HOLD_TTL = Duration.ofMinutes(8);
    private static final BigDecimal CONVENIENCE_FEE = new BigDecimal("30.00");

    public SeatSelectionResult selectSeats(String showId, List<String> seatIds,
                                            String userId) {
        Show show = showRepository.findById(showId)
            .orElseThrow(() -> new NotFoundException("Show not found"));

        // Validate: adjacent seats only, max 10 per booking
        validateSeatSelection(seatIds, show);

        // Lock seats in Redis with TTL
        seatLockService.lockSeats(showId, seatIds, userId, SEAT_HOLD_TTL);

        BigDecimal subtotal = calculateSubtotal(show, seatIds);
        Instant expiresAt = Instant.now().plus(SEAT_HOLD_TTL);

        return new SeatSelectionResult(seatIds, subtotal,
            CONVENIENCE_FEE, expiresAt);
    }

    @Transactional
    public MovieBooking confirmBooking(ConfirmBookingRequest request) {
        // Verify seats are still locked by this user
        if (!seatLockService.verifyLock(request.getShowId(),
                request.getSeatIds(), request.getUserId())) {
            throw new SeatExpiredException("Seat hold expired. Please select again.");
        }

        Show show = showRepository.findById(request.getShowId()).orElseThrow();
        BigDecimal ticketAmount = calculateSubtotal(show, request.getSeatIds());
        BigDecimal foodAmount = calculateFoodTotal(request.getFoodItems());

        // Apply promo code
        BigDecimal discount = BigDecimal.ZERO;
        if (request.getPromoCode() != null) {
            discount = promoService.calculateDiscount(
                request.getPromoCode(), ticketAmount, request.getUserId());
        }

        BigDecimal total = ticketAmount.add(CONVENIENCE_FEE)
            .add(foodAmount).subtract(discount);

        // Process payment
        PaymentResult payment = paymentService.charge(
            request.getUserId(), total, request.getPaymentMethod());

        // Create booking
        MovieBooking booking = new MovieBooking();
        booking.setBookingId(generateBookingId());
        booking.setShowId(request.getShowId());
        booking.setUserId(request.getUserId());
        booking.setSeatIds(request.getSeatIds());
        booking.setTicketAmount(ticketAmount);
        booking.setConvenienceFee(CONVENIENCE_FEE);
        booking.setFoodAmount(foodAmount);
        booking.setFoodItems(request.getFoodItems());
        booking.setTotalAmount(total);
        booking.setDiscount(discount);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setQrCode(qrGenerator.generate(booking.getBookingId()));
        booking.setBookedAt(Instant.now());
        bookingRepository.save(booking);

        // Mark seats as permanently booked (remove TTL lock, add permanent record)
        seatLockService.confirmSeats(request.getShowId(), request.getSeatIds());

        kafkaTemplate.send("movie-booking-events", booking.getBookingId(),
            new BookingEvent("CONFIRMED", booking));

        return booking;
    }

    public SeatMapResponse getSeatMap(String showId) {
        Show show = showRepository.findById(showId).orElseThrow();
        List<Seat> allSeats = screenRepository.getSeatLayout(show.getScreenId());

        // Get booked and locked seats from Redis + DB
        Set<String> bookedSeats = bookingRepository.getBookedSeatIds(showId);
        Set<String> lockedSeats = seatLockService.getLockedSeats(showId);

        List<SeatStatus> seatStatuses = allSeats.stream()
            .map(seat -> {
                SeatState state = SeatState.AVAILABLE;
                if (bookedSeats.contains(seat.getSeatId())) state = SeatState.BOOKED;
                else if (lockedSeats.contains(seat.getSeatId())) state = SeatState.LOCKED;

                return new SeatStatus(seat, state,
                    show.getPricing().get(seat.getCategory()));
            })
            .collect(Collectors.toList());

        return new SeatMapResponse(show, seatStatuses);
    }
}
```

### Database Schema

```sql
-- PostgreSQL: movies
CREATE TABLE movies (
    movie_id      UUID PRIMARY KEY,
    title         VARCHAR(300) NOT NULL,
    language      VARCHAR(30),
    genre         VARCHAR(50)[],          -- {'ACTION', 'DRAMA', 'COMEDY'}
    duration_min  INT NOT NULL,
    rating        VARCHAR(10),            -- U, UA, A, S
    release_date  DATE,
    poster_url    TEXT,
    trailer_url   TEXT,
    description   TEXT,
    avg_rating    DECIMAL(2,1) DEFAULT 0.0,
    is_active     BOOLEAN DEFAULT TRUE
);

-- PostgreSQL: cinemas (multiplex)
CREATE TABLE cinemas (
    cinema_id     UUID PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    city          VARCHAR(100) NOT NULL,
    area          VARCHAR(100),
    address       TEXT,
    latitude      DOUBLE PRECISION,
    longitude     DOUBLE PRECISION,
    total_screens INT NOT NULL
);
CREATE INDEX idx_cinemas_city ON cinemas(city);

-- PostgreSQL: screens
CREATE TABLE screens (
    screen_id     UUID PRIMARY KEY,
    cinema_id     UUID REFERENCES cinemas(cinema_id),
    screen_name   VARCHAR(50) NOT NULL,   -- 'Screen 1', 'IMAX'
    screen_type   VARCHAR(30),            -- STANDARD, IMAX, 4DX, DOLBY_ATMOS
    total_seats   INT NOT NULL,
    UNIQUE(cinema_id, screen_name)
);

-- PostgreSQL: seat layout per screen
CREATE TABLE screen_seats (
    seat_id       UUID PRIMARY KEY,
    screen_id     UUID REFERENCES screens(screen_id),
    row_label     CHAR(2) NOT NULL,       -- 'A', 'B', ..., 'AA'
    seat_number   INT NOT NULL,
    category      VARCHAR(20) NOT NULL,   -- SILVER, GOLD, PLATINUM, RECLINER
    is_available  BOOLEAN DEFAULT TRUE,   -- structurally available (not broken)
    UNIQUE(screen_id, row_label, seat_number)
);
CREATE INDEX idx_seats_screen ON screen_seats(screen_id);

-- PostgreSQL: shows (movie showtimes)
CREATE TABLE shows (
    show_id       UUID PRIMARY KEY,
    movie_id      UUID REFERENCES movies(movie_id),
    screen_id     UUID REFERENCES screens(screen_id),
    show_date     DATE NOT NULL,
    start_time    TIME NOT NULL,
    end_time      TIME NOT NULL,
    language      VARCHAR(30),
    format        VARCHAR(20),            -- 2D, 3D, IMAX, 4DX
    status        VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, CANCELLED, HOUSEFULL
    UNIQUE(screen_id, show_date, start_time)
);
CREATE INDEX idx_shows_movie_date ON shows(movie_id, show_date);
CREATE INDEX idx_shows_screen_date ON shows(screen_id, show_date);

-- PostgreSQL: show pricing (per category per show)
CREATE TABLE show_pricing (
    show_id       UUID REFERENCES shows(show_id),
    category      VARCHAR(20) NOT NULL,
    price         DECIMAL(10,2) NOT NULL,
    convenience_fee DECIMAL(10,2) DEFAULT 0,
    PRIMARY KEY (show_id, category)
);

-- PostgreSQL: bookings
CREATE TABLE movie_bookings (
    booking_id    UUID PRIMARY KEY,
    show_id       UUID REFERENCES shows(show_id),
    user_id       UUID REFERENCES users(user_id),
    status        VARCHAR(20) NOT NULL,   -- PENDING, CONFIRMED, CANCELLED
    ticket_count  INT NOT NULL,
    base_amount   DECIMAL(10,2),
    convenience_fee DECIMAL(10,2),
    gst           DECIMAL(10,2),
    total_amount  DECIMAL(10,2) NOT NULL,
    qr_code       TEXT,                   -- QR data for entry gate
    payment_id    UUID,
    booked_at     TIMESTAMP DEFAULT NOW(),
    cancelled_at  TIMESTAMP
);
CREATE INDEX idx_movie_bookings_user ON movie_bookings(user_id, booked_at DESC);

-- PostgreSQL: booked seats per booking
CREATE TABLE movie_booked_seats (
    booking_id    UUID REFERENCES movie_bookings(booking_id),
    seat_id       UUID REFERENCES screen_seats(seat_id),
    show_id       UUID REFERENCES shows(show_id),
    PRIMARY KEY (booking_id, seat_id)
);
CREATE UNIQUE INDEX idx_show_seat ON movie_booked_seats(show_id, seat_id);

-- PostgreSQL: food & beverage orders
CREATE TABLE fnb_orders (
    order_id      UUID PRIMARY KEY,
    booking_id    UUID REFERENCES movie_bookings(booking_id),
    cinema_id     UUID REFERENCES cinemas(cinema_id),
    items         JSONB NOT NULL,         -- [{"name":"Popcorn L","qty":1,"price":350}]
    total_amount  DECIMAL(10,2) NOT NULL,
    status        VARCHAR(20) DEFAULT 'PENDING',
    created_at    TIMESTAMP DEFAULT NOW()
);

-- Redis: seat locks during checkout
-- Key: "seat_lock:{show_id}:{seat_id}" → user_id (TTL: 8 min)
-- Key: "show_booked:{show_id}" → Set of booked seat_ids
```

### Capacity Estimation

```
Assumptions:
  10,000 screens across India, avg 4 shows/day
  Average screen: 250 seats
  Blockbuster day: 10M+ booking attempts

Show inventory:
  10,000 × 4 = 40,000 shows/day
  40,000 × 250 = 10M seats/day

Normal QPS:
  2M bookings/day ≈ 23 bookings/sec
  Seat map views: 20M/day ≈ 230/sec

Blockbuster peak:
  10M attempts in 2 hours = 1,400 bookings/sec
  Seat map: 10,000 views/sec (heavy caching needed)

Seat lock (Redis):
  At any time: ~500K seats locked (being booked)
  Each: ~100 bytes → 50 MB (trivial for Redis)

Storage (5 years):
  2M/day × 365 × 5 × 1KB = ~3.6 TB bookings
```

---

## Q14: Design a Hotel Booking System (MakeMyTrip)

### Requirements

```
Functional:
  • Search hotels by city, dates, guests, star rating
  • View room types with photos, amenities, pricing
  • Real-time availability calendar
  • Book room(s) with date range selection
  • Dynamic pricing (weekday/weekend, season, demand)
  • Guest reviews and ratings
  • Cancellation with flexible/non-refundable policies
  • Multi-property comparison
  • Loyalty program and reward points

Non-Functional:
  • Handle 2M+ bookings/day (peak holiday season)
  • No overbooking (strong consistency)
  • Search latency < 500ms across 500K+ hotels
  • Support date-range queries efficiently
  • 99.9% availability
```

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                HOTEL BOOKING ARCHITECTURE                         │
│                                                                    │
│  ┌──────────┐       ┌──────────────┐                              │
│  │ Web/     │──────►│ API Gateway  │                              │
│  │ Mobile   │       │ + CDN        │                              │
│  └──────────┘       └──────┬───────┘                              │
│                            │                                      │
│  ┌─────────────────────────┼──────────────────────────┐           │
│  │                         │                          │           │
│  ▼                         ▼                          ▼           │
│ ┌──────────┐       ┌──────────────┐           ┌──────────┐       │
│ │ Search   │       │  Booking     │           │ Hotel    │       │
│ │ Service  │       │  Service     │           │ Service  │       │
│ │          │       │              │           │          │       │
│ │ • Hotels │       │ • Reserve    │           │ • Listing│       │
│ │ • Avail. │       │ • Confirm    │           │ • Rooms  │       │
│ │ • Filter │       │ • Cancel     │           │ • Photos │       │
│ │ • Sort   │       │ • Modify     │           │ • Reviews│       │
│ └────┬─────┘       └────┬────────┘           └────┬─────┘       │
│      │                  │                         │              │
│      ▼                  ▼                         ▼              │
│ ┌──────────┐      ┌──────────────┐        ┌──────────────┐      │
│ │Elastic-  │      │ PostgreSQL   │        │ PostgreSQL   │      │
│ │search    │      │ (bookings,   │        │ (hotels,     │      │
│ │(hotel    │      │  rooms)      │        │  rooms,      │      │
│ │ search)  │      │              │        │  reviews)    │      │
│ └──────────┘      │ Redis (rate  │        └──────────────┘      │
│                   │  + avail     │                               │
│                   │  cache)      │                               │
│                   └──────┬───────┘                               │
│                          │                                      │
│                          ▼                                      │
│                   ┌──────────────┐                               │
│                   │    Kafka     │                               │
│                   │              │───► Payment Service           │
│                   │              │───► Notification (email/SMS)  │
│                   │              │───► Hotel PMS Integration     │
│                   │              │───► Pricing Engine            │
│                   │              │───► Loyalty Points Service    │
│                   └──────────────┘                               │
│                                                                  │
│  AVAILABILITY CALENDAR (the hardest part):                      │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ Hotel: Taj Mumbai | Room: Deluxe Sea View            │       │
│  │ Total Inventory: 50 rooms                             │       │
│  │                                                        │       │
│  │  Date       │ Total │ Booked │ Available │ Price     │       │
│  │  ─────────── │ ───── │ ────── │ ───────── │ ──────── │       │
│  │  2026-03-15 │  50   │  48    │     2     │ ₹12,000  │       │
│  │  2026-03-16 │  50   │  45    │     5     │ ₹10,000  │       │
│  │  2026-03-17 │  50   │  30    │    20     │ ₹8,000   │       │
│  │  2026-03-18 │  50   │  50    │     0     │ SOLD OUT  │       │
│  │  2026-03-19 │  50   │  42    │     8     │ ₹11,000  │       │
│  │                                                        │       │
│  │  Booking Mar 15-18 (3 nights): Must have availability │       │
│  │  on ALL 3 nights. If Mar 18 is full → can't book.    │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  DYNAMIC PRICING:                                               │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ Base Price: ₹8,000/night                              │       │
│  │                                                        │       │
│  │ Modifiers:                                            │       │
│  │  Weekend (Fri-Sat):        +30%  = ₹10,400            │       │
│  │  Peak season (Dec-Jan):    +50%  = ₹12,000            │       │
│  │  High demand (>80% full):  +20%  = ₹9,600             │       │
│  │  Last minute (< 24 hrs):   -15%  = ₹6,800             │       │
│  │  Loyalty member:           -10%  = ₹7,200             │       │
│  │                                                        │       │
│  │  Final = base × (1 + season) × (1 + demand) × loyalty│       │
│  └──────────────────────────────────────────────────────┘       │
└──────────────────────────────────────────────────────────────────┘
```

### Approach Comparison — Availability Management

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Calendar Table   │ Booking-based    │ Hybrid           │
│                     │ (pre-computed)   │ (query on read)  │ (MakeMyTrip) ✅  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ How it works        │ One row per room │ Query bookings   │ Calendar cache + │
│                     │ per date with    │ table to compute │ booking-based    │
│                     │ availability     │ availability     │ verification     │
│ Read speed          │ O(1) per date ✅ │ O(N bookings) ❌ │ O(1) cache ✅    │
│ Write speed         │ Update calendar  │ Just insert      │ Insert + update  │
│                     │ + booking        │ booking row ✅   │ cache            │
│ Storage             │ High (365 rows   │ Low (only active │ Medium           │
│                     │ × N rooms) ❌    │ bookings) ✅     │                  │
│ Accuracy            │ Eventual (cache) │ Real-time ✅     │ Verify at book ✅│
│ Date-range query    │ Range scan ✅    │ Complex overlap  │ Range scan ✅    │
│                     │                  │ detection ❌     │                  │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Fastest reads    │ Simplest writes  │ Fast reads +     │
│                     │ Easy date-range  │ No calendar sync │ accurate at      │
│                     │                  │                  │ booking time     │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Calendar must    │ Slow for search  │ Two-step verify  │
│                     │ stay in sync     │ queries          │ complexity       │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Read-heavy       │ Low traffic      │ Scale booking ✅ │
│                     │ search pages     │ simple apps      │ platforms        │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class Hotel {
    private String hotelId;
    private String name;
    private String city;
    private String address;
    private int starRating;
    private double userRating;
    private List<String> amenities; // WiFi, Pool, Gym, Spa
    private List<RoomType> roomTypes;
    private List<String> imageUrls;
    private Map<String, String> policies; // cancellation, check-in/out times
}

public class RoomType {
    private String roomTypeId;
    private String hotelId;
    private String name; // "Deluxe Sea View", "Standard Twin"
    private int maxOccupancy;
    private int totalRooms;
    private BigDecimal basePrice;
    private List<String> amenities;
    private List<String> imageUrls;
}

public class RoomAvailability {
    private String roomTypeId;
    private LocalDate date;
    private int totalRooms;
    private int bookedRooms;
    private BigDecimal price; // dynamic price for this date
}

public class HotelBooking {
    private String bookingId;
    private String hotelId;
    private String roomTypeId;
    private String userId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int nights;
    private int rooms;
    private int guests;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BookingStatus status;
    private CancellationPolicy cancellationPolicy;
    private Instant bookedAt;
}

public enum CancellationPolicy { FREE_CANCELLATION, PARTIAL_REFUND, NON_REFUNDABLE }

// --- Hotel Search Service ---
@Service
public class HotelSearchService {

    private final ElasticsearchClient esClient;
    private final AvailabilityService availabilityService;
    private final PricingEngine pricingEngine;

    public HotelSearchResponse search(HotelSearchRequest request) {
        // Step 1: Search hotels by location, filters
        BoolQuery.Builder query = new BoolQuery.Builder()
            .must(m -> m.term(t -> t.field("city").value(request.getCity())))
            .filter(f -> f.range(r -> r.field("starRating")
                .gte(JsonData.of(request.getMinStars()))));

        if (request.getAmenities() != null) {
            for (String amenity : request.getAmenities()) {
                query.filter(f -> f.term(t -> t.field("amenities").value(amenity)));
            }
        }

        if (request.getMaxPrice() != null) {
            query.filter(f -> f.range(r -> r.field("basePrice")
                .lte(JsonData.of(request.getMaxPrice()))));
        }

        SearchResponse<Hotel> response = esClient.search(s -> s
            .index("hotels")
            .query(q -> q.bool(query.build()))
            .sort(buildSort(request.getSortBy()))
            .from(request.getPage() * request.getSize())
            .size(request.getSize()),
            Hotel.class);

        // Step 2: Check availability for each hotel across date range
        List<HotelSearchResult> results = response.hits().hits().stream()
            .map(hit -> {
                Hotel hotel = hit.source();
                List<RoomAvailabilityResult> available = availabilityService
                    .checkAvailability(hotel.getHotelId(), request.getCheckIn(),
                        request.getCheckOut(), request.getRooms());

                BigDecimal lowestPrice = available.stream()
                    .map(RoomAvailabilityResult::getTotalPrice)
                    .min(BigDecimal::compareTo).orElse(null);

                return new HotelSearchResult(hotel, available, lowestPrice);
            })
            .filter(r -> !r.getAvailableRooms().isEmpty()) // only show available
            .collect(Collectors.toList());

        return new HotelSearchResponse(results, response.hits().total().value());
    }
}

// --- Availability Service ---
@Service
public class AvailabilityService {

    private final RoomAvailabilityRepository availabilityRepository;
    private final PricingEngine pricingEngine;
    private final RedisTemplate<String, Integer> cache;

    public List<RoomAvailabilityResult> checkAvailability(String hotelId,
            LocalDate checkIn, LocalDate checkOut, int roomsNeeded) {

        List<RoomType> roomTypes = roomTypeRepository.findByHotelId(hotelId);
        List<RoomAvailabilityResult> results = new ArrayList<>();

        for (RoomType roomType : roomTypes) {
            // Check every date in the range
            boolean availableAllDates = true;
            BigDecimal totalPrice = BigDecimal.ZERO;
            int minAvailable = Integer.MAX_VALUE;

            for (LocalDate date = checkIn; date.isBefore(checkOut);
                 date = date.plusDays(1)) {

                String cacheKey = "avail:" + roomType.getRoomTypeId() + ":" + date;
                Integer available = cache.opsForValue().get(cacheKey);

                if (available == null) {
                    RoomAvailability avail = availabilityRepository
                        .findByRoomTypeIdAndDate(roomType.getRoomTypeId(), date);
                    available = avail.getTotalRooms() - avail.getBookedRooms();
                    cache.opsForValue().set(cacheKey, available, Duration.ofMinutes(5));
                }

                if (available < roomsNeeded) {
                    availableAllDates = false;
                    break;
                }
                minAvailable = Math.min(minAvailable, available);

                BigDecimal nightPrice = pricingEngine.getPrice(
                    roomType, date, available, roomType.getTotalRooms());
                totalPrice = totalPrice.add(nightPrice);
            }

            if (availableAllDates) {
                results.add(new RoomAvailabilityResult(roomType,
                    minAvailable, totalPrice));
            }
        }
        return results;
    }
}

// --- Hotel Booking Service ---
@Service
public class HotelBookingService {

    private final AvailabilityService availabilityService;
    private final RoomAvailabilityRepository availabilityRepository;
    private final BookingRepository bookingRepository;
    private final PaymentService paymentService;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    @Transactional
    public HotelBooking bookRoom(HotelBookingRequest request) {
        LocalDate checkIn = request.getCheckIn();
        LocalDate checkOut = request.getCheckOut();
        int nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);

        // Step 1: Verify availability with pessimistic lock on date range
        for (LocalDate date = checkIn; date.isBefore(checkOut);
             date = date.plusDays(1)) {

            int updated = availabilityRepository.decrementIfAvailable(
                request.getRoomTypeId(), date, request.getRooms());

            if (updated == 0) {
                // Rollback dates already decremented
                rollbackAvailability(request.getRoomTypeId(), checkIn, date,
                    request.getRooms());
                throw new NoAvailabilityException(
                    "Room not available on " + date);
            }
        }

        // Step 2: Calculate total with dynamic pricing
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (LocalDate date = checkIn; date.isBefore(checkOut);
             date = date.plusDays(1)) {
            totalAmount = totalAmount.add(
                pricingEngine.getPrice(request.getRoomTypeId(), date));
        }
        totalAmount = totalAmount.multiply(BigDecimal.valueOf(request.getRooms()));
        BigDecimal tax = totalAmount.multiply(new BigDecimal("0.18")); // 18% GST

        // Step 3: Process payment
        PaymentResult payment = paymentService.charge(
            request.getUserId(), totalAmount.add(tax),
            request.getPaymentMethod());

        // Step 4: Create booking
        HotelBooking booking = new HotelBooking();
        booking.setBookingId(generateBookingId());
        booking.setHotelId(request.getHotelId());
        booking.setRoomTypeId(request.getRoomTypeId());
        booking.setUserId(request.getUserId());
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setNights(nights);
        booking.setRooms(request.getRooms());
        booking.setTotalAmount(totalAmount);
        booking.setTaxAmount(tax);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookedAt(Instant.now());
        bookingRepository.save(booking);

        // Invalidate availability cache for all booked dates
        for (LocalDate date = checkIn; date.isBefore(checkOut);
             date = date.plusDays(1)) {
            cache.delete("avail:" + request.getRoomTypeId() + ":" + date);
        }

        kafkaTemplate.send("hotel-booking-events", booking.getBookingId(),
            new BookingEvent("CONFIRMED", booking));

        return booking;
    }

    private void rollbackAvailability(String roomTypeId, LocalDate from,
                                       LocalDate to, int rooms) {
        for (LocalDate date = from; date.isBefore(to); date = date.plusDays(1)) {
            availabilityRepository.incrementAvailability(roomTypeId, date, rooms);
        }
    }
}

// --- Dynamic Pricing Engine ---
@Service
public class PricingEngine {

    public BigDecimal getPrice(RoomType roomType, LocalDate date,
                                int available, int total) {
        BigDecimal base = roomType.getBasePrice();
        double multiplier = 1.0;

        // Weekend premium
        DayOfWeek day = date.getDayOfWeek();
        if (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY) {
            multiplier += 0.30;
        }

        // Seasonal pricing
        int month = date.getMonthValue();
        if (month == 12 || month == 1) multiplier += 0.50; // peak
        if (month == 7 || month == 8) multiplier += 0.20; // summer

        // Demand-based pricing
        double occupancy = 1.0 - ((double) available / total);
        if (occupancy > 0.9) multiplier += 0.40;      // almost full
        else if (occupancy > 0.8) multiplier += 0.20;  // filling up
        else if (occupancy < 0.3) multiplier -= 0.10;  // low demand discount

        // Last-minute discount
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), date);
        if (daysUntil <= 1 && occupancy < 0.7) multiplier -= 0.15;

        return base.multiply(BigDecimal.valueOf(multiplier))
            .setScale(0, RoundingMode.HALF_UP);
    }
}
```

### Database Schema

```sql
-- PostgreSQL: hotels
CREATE TABLE hotels (
    hotel_id      UUID PRIMARY KEY,
    name          VARCHAR(300) NOT NULL,
    chain         VARCHAR(100),           -- 'Marriott', 'OYO', 'Taj', null for independents
    star_rating   INT CHECK (star_rating BETWEEN 1 AND 5),
    city          VARCHAR(100) NOT NULL,
    area          VARCHAR(100),
    address       TEXT,
    latitude      DOUBLE PRECISION,
    longitude     DOUBLE PRECISION,
    description   TEXT,
    amenities     TEXT[],                 -- {'POOL', 'GYM', 'WIFI', 'SPA', 'PARKING'}
    check_in_time TIME DEFAULT '14:00',
    check_out_time TIME DEFAULT '11:00',
    avg_rating    DECIMAL(2,1) DEFAULT 0.0,
    review_count  INT DEFAULT 0,
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_hotels_city ON hotels(city);
CREATE INDEX idx_hotels_location ON hotels USING GIST(
    ST_MakePoint(longitude, latitude)::geography);

-- PostgreSQL: room types per hotel
CREATE TABLE room_types (
    room_type_id  UUID PRIMARY KEY,
    hotel_id      UUID REFERENCES hotels(hotel_id),
    name          VARCHAR(100) NOT NULL,  -- 'Deluxe King', 'Standard Twin', 'Suite'
    description   TEXT,
    max_occupancy INT NOT NULL,
    bed_type      VARCHAR(30),            -- KING, QUEEN, TWIN, DOUBLE
    total_rooms   INT NOT NULL,           -- total inventory of this type
    base_price    DECIMAL(12,2) NOT NULL, -- per night base price
    amenities     TEXT[],                 -- room-specific: {'AC', 'MINIBAR', 'BALCONY'}
    images        TEXT[],
    UNIQUE(hotel_id, name)
);

-- PostgreSQL: room availability calendar (per room type per date)
CREATE TABLE room_availability (
    availability_id UUID PRIMARY KEY,
    room_type_id  UUID REFERENCES room_types(room_type_id),
    date          DATE NOT NULL,
    total_rooms   INT NOT NULL,
    booked_rooms  INT DEFAULT 0,
    blocked_rooms INT DEFAULT 0,          -- maintenance, overbooking buffer
    price         DECIMAL(12,2) NOT NULL, -- dynamic price for this date
    min_nights    INT DEFAULT 1,
    version       INT DEFAULT 1,          -- optimistic locking
    UNIQUE(room_type_id, date)
);
CREATE INDEX idx_room_avail ON room_availability(room_type_id, date);

-- PostgreSQL: hotel bookings
CREATE TABLE hotel_bookings (
    booking_id    UUID PRIMARY KEY,
    hotel_id      UUID REFERENCES hotels(hotel_id),
    room_type_id  UUID REFERENCES room_types(room_type_id),
    user_id       UUID REFERENCES users(user_id),
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    num_rooms     INT NOT NULL DEFAULT 1,
    num_guests    INT NOT NULL,
    status        VARCHAR(20) NOT NULL,   -- PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED, NO_SHOW
    total_amount  DECIMAL(12,2) NOT NULL,
    tax_amount    DECIMAL(10,2),
    special_requests TEXT,
    guest_name    VARCHAR(200) NOT NULL,
    guest_phone   VARCHAR(20),
    guest_email   VARCHAR(255),
    payment_id    UUID,
    idempotency_key VARCHAR(100) UNIQUE,
    booked_at     TIMESTAMP DEFAULT NOW(),
    cancelled_at  TIMESTAMP,
    cancellation_fee DECIMAL(10,2)
);
CREATE INDEX idx_hotel_bookings_user ON hotel_bookings(user_id, booked_at DESC);
CREATE INDEX idx_hotel_bookings_hotel ON hotel_bookings(hotel_id, check_in_date);

-- PostgreSQL: reviews
CREATE TABLE hotel_reviews (
    review_id     UUID PRIMARY KEY,
    hotel_id      UUID REFERENCES hotels(hotel_id),
    booking_id    UUID REFERENCES hotel_bookings(booking_id),
    user_id       UUID REFERENCES users(user_id),
    rating        INT CHECK (rating BETWEEN 1 AND 5),
    title         VARCHAR(200),
    review_text   TEXT,
    pros          TEXT,
    cons          TEXT,
    images        TEXT[],
    is_verified   BOOLEAN DEFAULT TRUE,   -- stayed at hotel
    created_at    TIMESTAMP DEFAULT NOW(),
    UNIQUE(booking_id)                    -- one review per booking
);
CREATE INDEX idx_reviews_hotel ON hotel_reviews(hotel_id, created_at DESC);

-- Redis: availability cache
-- Key: "avail:{room_type_id}:{date}" → { total, booked, price } (TTL: 5 min)
-- Key: "hotel_search_cache:{city}:{check_in}:{check_out}:{hash}" → results (TTL: 2 min)
```

### Capacity Estimation

```
Assumptions:
  500K hotels, 50M room-nights/year
  2M bookings/day during peak season

Search QPS:
  20M searches/day ≈ 230/sec
  Peak (holiday planning): 1000/sec

Booking QPS:
  2M/day ≈ 23/sec average, peak 200/sec

Availability data:
  500K hotels × avg 5 room types × 365 days = 912M rows
  Each row: ~100 bytes → 91 GB (fits in PostgreSQL)

Cache (Redis):
  Hot dates (next 90 days): 912M × (90/365) ≈ 225M keys
  Each: ~50 bytes → 11 GB Redis cluster

Storage (5 years):
  50M bookings/year × 5 × 2KB = ~500 GB bookings
  Hotel content (photos): 500K × 50 images × 200KB = ~5 TB (S3)
```

---

## Q15: Design a Rate Limiting System

### Requirements

```
Functional:
  • Limit requests per user/IP/API key per time window
  • Support multiple rate limit tiers (free: 100/hr, premium: 10K/hr)
  • Return proper headers (X-RateLimit-Remaining, Retry-After)
  • Support different algorithms (token bucket, sliding window)
  • Configurable per-endpoint limits (/api/search: 50/min, /api/pay: 5/min)
  • Whitelist/blacklist certain clients

Non-Functional:
  • Ultra-low latency (< 1ms overhead per request)
  • Distributed across multiple API servers
  • Accurate counting (no significant over/under-counting)
  • Fault-tolerant (if rate limiter is down, allow traffic — fail-open)
  • Scale: 1M+ requests/sec across all services
```

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                  RATE LIMITING ARCHITECTURE                       │
│                                                                    │
│  Client                                                          │
│  ┌──────────┐                                                    │
│  │ Request  │                                                    │
│  │ with     │                                                    │
│  │ API Key  │                                                    │
│  └────┬─────┘                                                    │
│       │                                                          │
│       ▼                                                          │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │                    API GATEWAY                            │    │
│  │                                                            │    │
│  │  Step 1: Extract client identity                          │    │
│  │    → API key, user ID, IP address                         │    │
│  │                                                            │    │
│  │  Step 2: Check rate limit ──► Redis Cluster               │    │
│  │    ┌─────────────────────────────────────────────────┐    │    │
│  │    │ Key: "ratelimit:user_42:/api/search"            │    │    │
│  │    │ Value: { tokens: 47, last_refill: 1710000000 }  │    │    │
│  │    │                                                   │    │    │
│  │    │ tokens > 0?                                      │    │    │
│  │    │   YES → Decrement token, ALLOW request ✅        │    │    │
│  │    │   NO  → REJECT with 429 + Retry-After ❌         │    │    │
│  │    └─────────────────────────────────────────────────┘    │    │
│  │                                                            │    │
│  │  Step 3: Add response headers                             │    │
│  │    X-RateLimit-Limit: 100                                 │    │
│  │    X-RateLimit-Remaining: 47                              │    │
│  │    X-RateLimit-Reset: 1710003600                          │    │
│  │                                                            │    │
│  │  Step 4: Forward to backend (if allowed)                  │    │
│  └─────────────────────────┬────────────────────────────────┘    │
│                            │                                      │
│                            ▼                                      │
│                   ┌──────────────┐                                │
│                   │ Backend API  │                                │
│                   │ Service      │                                │
│                   └──────────────┘                                │
│                                                                    │
│  DISTRIBUTED RATE LIMITING (multiple API servers):               │
│  ┌──────────────────────────────────────────────────────┐       │
│  │                                                        │       │
│  │  API Server 1 ──┐                                    │       │
│  │  API Server 2 ──┼──► Redis Cluster (shared counter) │       │
│  │  API Server 3 ──┘    "ratelimit:user_42" → 47 tokens│       │
│  │                                                        │       │
│  │  All servers read/write the SAME counter in Redis.   │       │
│  │  Redis atomic operations (INCR, Lua scripts) prevent │       │
│  │  race conditions.                                     │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  ALGORITHM COMPARISON:                                          │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ TOKEN BUCKET (most popular — used by Stripe, GitHub): │       │
│  │                                                        │       │
│  │   Bucket capacity: 100 tokens                         │       │
│  │   Refill rate: 100 tokens per hour                    │       │
│  │                                                        │       │
│  │   ████████████████████ (100 tokens)                   │       │
│  │   ████████████████░░░░ (80 tokens — 20 requests made) │       │
│  │   ░░░░░░░░░░░░░░░░░░░░ (0 tokens — rate limited!)    │       │
│  │   ████░░░░░░░░░░░░░░░░ (4 tokens — refilled over time)│       │
│  │                                                        │       │
│  │ SLIDING WINDOW LOG (most accurate):                   │       │
│  │   Keep timestamps of all requests in last window      │       │
│  │   Count = timestamps in window ≤ limit?               │       │
│  │   [10:00:01, 10:00:05, 10:00:12, ...] → count = 3   │       │
│  │                                                        │       │
│  │ SLIDING WINDOW COUNTER (balanced):                    │       │
│  │   Weighted count from current + previous window       │       │
│  │   Current window: 30 req (60% through window)         │       │
│  │   Previous window: 80 req                             │       │
│  │   Estimate: 30 + 80 × 0.4 = 62 requests              │       │
│  │   Limit: 100 → ALLOW ✅                               │       │
│  └──────────────────────────────────────────────────────┘       │
└──────────────────────────────────────────────────────────────────┘
```

### Approach Comparison — Rate Limiting Algorithms

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ Token Bucket     │ Sliding Window   │ Fixed Window     │
│                     │                  │ Log              │ Counter          │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Burst handling      │ Allows controlled│ Strict — no      │ 2x burst at      │
│                     │ bursts ✅        │ bursts           │ boundary ❌      │
│ Memory usage        │ O(1) per user ✅ │ O(N) per user ❌ │ O(1) per user ✅ │
│ Accuracy            │ Good             │ Perfect ✅       │ Approximate      │
│ Implementation      │ Moderate         │ Complex          │ Simple ✅        │
│ Redis operations    │ 1 Lua script     │ ZADD + ZCOUNT    │ INCR + EXPIRE    │
│ Distributed         │ Lua atomic ✅    │ Sorted set ✅    │ INCR atomic ✅   │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Smooth rate      │ Most accurate    │ Simplest to      │
│                     │ Burst-friendly   │ No boundary      │ implement        │
│                     │ Low memory       │ issues           │ Lowest latency   │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ Slightly complex │ High memory for  │ 2x burst at      │
│                     │ Lua script needed│ high-volume users│ window edges     │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Used by             │ Stripe, AWS ✅   │ Accurate billing │ Simple APIs      │
│                     │ GitHub, Shopify  │ systems          │ (quick MVP)      │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Approach Comparison — Where to Place the Rate Limiter

```
┌─────────────────────┬──────────────────┬──────────────────┬──────────────────┐
│                     │ API Gateway      │ Middleware       │ Application      │
│                     │ (edge)           │ (sidecar/filter) │ Code (in-process)│
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Blocks traffic at   │ Edge (earliest)✅│ Before handler   │ Inside handler   │
│ Centralized config  │ Yes ✅           │ Per-service      │ Per-service      │
│ Request visibility  │ Limited (headers)│ Full context     │ Full context ✅  │
│ Latency overhead    │ Minimal ✅       │ Minimal          │ Minimal          │
│ Cross-service       │ Unified ✅       │ Needs shared     │ Needs shared     │
│                     │                  │ storage          │ storage          │
│ Per-endpoint control│ Route-based      │ Annotation-based │ Code-level ✅    │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ✅ Pros             │ Protects all     │ Service-specific │ Most flexible    │
│                     │ services at once │ rules, easy to   │ Business-aware   │
│                     │ Single config    │ add via filter   │ decisions        │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ ❌ Cons             │ No business      │ Each service     │ Scattered logic  │
│                     │ context          │ needs filter     │ Hard to manage   │
├─────────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Best for            │ Global defense ✅│ Microservices ✅ │ Complex business │
│                     │ DDoS, abuse      │ (Spring filter)  │ rules            │
└─────────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

### Low-Level Design — Java Code

```java
// --- Domain Models ---
public class RateLimitRule {
    private String ruleId;
    private String endpoint;       // "/api/search", "/api/payment", "*"
    private String clientType;     // "free", "premium", "enterprise"
    private int maxRequests;        // 100
    private Duration window;        // 1 HOUR
    private RateLimitAlgorithm algorithm; // TOKEN_BUCKET, SLIDING_WINDOW
}

public class RateLimitResult {
    private boolean allowed;
    private int remaining;
    private long resetAtEpochSeconds;
    private long retryAfterSeconds; // only if rejected
}

public enum RateLimitAlgorithm { TOKEN_BUCKET, SLIDING_WINDOW_LOG,
    SLIDING_WINDOW_COUNTER, FIXED_WINDOW }

// --- Token Bucket Implementation (Redis + Lua) ---
@Service
public class TokenBucketRateLimiter implements RateLimiter {

    private final RedisTemplate<String, String> redis;

    // Lua script executes atomically in Redis — no race conditions
    private static final String TOKEN_BUCKET_LUA = """
        local key = KEYS[1]
        local capacity = tonumber(ARGV[1])
        local refill_rate = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])
        local requested = tonumber(ARGV[4])
        
        local bucket = redis.call('HMGET', key, 'tokens', 'last_refill')
        local tokens = tonumber(bucket[1])
        local last_refill = tonumber(bucket[2])
        
        if tokens == nil then
            tokens = capacity
            last_refill = now
        end
        
        -- Refill tokens based on elapsed time
        local elapsed = now - last_refill
        local new_tokens = math.min(capacity, tokens + (elapsed * refill_rate))
        
        if new_tokens >= requested then
            new_tokens = new_tokens - requested
            redis.call('HMSET', key, 'tokens', new_tokens, 'last_refill', now)
            redis.call('EXPIRE', key, math.ceil(capacity / refill_rate) + 1)
            return {1, new_tokens}  -- allowed, remaining
        else
            redis.call('HMSET', key, 'tokens', new_tokens, 'last_refill', now)
            redis.call('EXPIRE', key, math.ceil(capacity / refill_rate) + 1)
            return {0, new_tokens}  -- rejected, remaining
        end
        """;

    private final RedisScript<List> tokenBucketScript;

    public TokenBucketRateLimiter(RedisTemplate<String, String> redis) {
        this.redis = redis;
        this.tokenBucketScript = RedisScript.of(TOKEN_BUCKET_LUA, List.class);
    }

    @Override
    public RateLimitResult tryAcquire(String clientId, RateLimitRule rule) {
        String key = "ratelimit:" + clientId + ":" + rule.getEndpoint();
        double refillRate = (double) rule.getMaxRequests() /
            rule.getWindow().toSeconds();

        List<Long> result = redis.execute(tokenBucketScript,
            List.of(key),
            String.valueOf(rule.getMaxRequests()),
            String.valueOf(refillRate),
            String.valueOf(Instant.now().getEpochSecond()),
            "1");

        boolean allowed = result.get(0) == 1;
        int remaining = result.get(1).intValue();
        long resetAt = Instant.now().plus(rule.getWindow()).getEpochSecond();

        RateLimitResult rateLimitResult = new RateLimitResult();
        rateLimitResult.setAllowed(allowed);
        rateLimitResult.setRemaining(remaining);
        rateLimitResult.setResetAtEpochSeconds(resetAt);
        if (!allowed) {
            long tokensNeeded = 1;
            rateLimitResult.setRetryAfterSeconds(
                (long) Math.ceil(tokensNeeded / refillRate));
        }
        return rateLimitResult;
    }
}

// --- Sliding Window Counter Implementation ---
@Service
public class SlidingWindowRateLimiter implements RateLimiter {

    private final RedisTemplate<String, String> redis;

    @Override
    public RateLimitResult tryAcquire(String clientId, RateLimitRule rule) {
        String key = "ratelimit:sw:" + clientId + ":" + rule.getEndpoint();
        long windowSizeMs = rule.getWindow().toMillis();
        long now = System.currentTimeMillis();
        long windowStart = now - windowSizeMs;

        // Use Redis sorted set: score = timestamp, member = unique request ID
        String member = now + ":" + UUID.randomUUID().toString().substring(0, 8);

        Long count = redis.execute(new SessionCallback<Long>() {
            @Override
            public Long execute(RedisOperations operations) {
                operations.multi();
                // Remove entries outside the window
                operations.opsForZSet().removeRangeByScore(key, 0, windowStart);
                // Count entries in window
                operations.opsForZSet().count(key, windowStart, now);
                // Add current request
                operations.opsForZSet().add(key, member, now);
                // Set expiry
                operations.expire(key, rule.getWindow().plusSeconds(10));

                List<Object> results = operations.exec();
                return (Long) results.get(1); // count result
            }
        });

        boolean allowed = count < rule.getMaxRequests();
        if (!allowed) {
            redis.opsForZSet().remove(key, member); // rollback add
        }

        RateLimitResult result = new RateLimitResult();
        result.setAllowed(allowed);
        result.setRemaining(Math.max(0, rule.getMaxRequests() - count.intValue() - 1));
        result.setResetAtEpochSeconds(
            Instant.ofEpochMilli(now).plus(rule.getWindow()).getEpochSecond());
        return result;
    }
}

// --- Rate Limit Filter (Spring Boot) ---
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;
    private final RateLimitRuleService ruleService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String clientId = extractClientId(request);
        String endpoint = request.getRequestURI();

        RateLimitRule rule = ruleService.getRule(clientId, endpoint);
        if (rule == null) {
            chain.doFilter(request, response); // no rule = no limit
            return;
        }

        RateLimitResult result = rateLimiter.tryAcquire(clientId, rule);

        // Always set rate limit headers
        response.setHeader("X-RateLimit-Limit",
            String.valueOf(rule.getMaxRequests()));
        response.setHeader("X-RateLimit-Remaining",
            String.valueOf(result.getRemaining()));
        response.setHeader("X-RateLimit-Reset",
            String.valueOf(result.getResetAtEpochSeconds()));

        if (result.isAllowed()) {
            chain.doFilter(request, response);
        } else {
            response.setHeader("Retry-After",
                String.valueOf(result.getRetryAfterSeconds()));
            response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS); // 429
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                  "error": "rate_limit_exceeded",
                  "message": "Too many requests. Please retry after %d seconds.",
                  "retry_after": %d
                }
                """.formatted(result.getRetryAfterSeconds(),
                              result.getRetryAfterSeconds()));
        }
    }

    private String extractClientId(HttpServletRequest request) {
        // Priority: API key → authenticated user → IP address
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null) return "apikey:" + apiKey;

        String userId = (String) request.getAttribute("userId");
        if (userId != null) return "user:" + userId;

        return "ip:" + request.getRemoteAddr();
    }
}

// --- Rate Limit Rule Service (tiered limits) ---
@Service
public class RateLimitRuleService {

    private final RateLimitRuleRepository ruleRepository;
    private final LoadingCache<String, RateLimitRule> ruleCache =
        CacheBuilder.newBuilder()
            .maximumSize(100_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build(new CacheLoader<>() {
                @Override
                public RateLimitRule load(String key) {
                    return loadRule(key);
                }
            });

    public RateLimitRule getRule(String clientId, String endpoint) {
        String tier = getClientTier(clientId); // "free", "premium", "enterprise"

        // Try exact endpoint match first, then wildcard
        RateLimitRule rule = ruleCache.getUnchecked(tier + ":" + endpoint);
        if (rule == null) {
            rule = ruleCache.getUnchecked(tier + ":*"); // default rule
        }
        return rule;
    }

    // Default tier limits
    // Free:       100 req/hour,   5 req/min per endpoint
    // Premium:    10,000 req/hour, 100 req/min per endpoint
    // Enterprise: 100,000 req/hour, 1000 req/min per endpoint
}
```

### Database Schema

```sql
-- PostgreSQL: rate limiting rules (config store)
CREATE TABLE rate_limit_rules (
    rule_id       UUID PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    description   TEXT,
    tier          VARCHAR(30) NOT NULL,   -- FREE, PREMIUM, ENTERPRISE, INTERNAL
    endpoint      VARCHAR(200),           -- '/api/v1/users' or '*' for global
    http_method   VARCHAR(10),            -- GET, POST, or '*' for all
    algorithm     VARCHAR(30) NOT NULL,   -- TOKEN_BUCKET, SLIDING_WINDOW_LOG, FIXED_WINDOW
    max_requests  INT NOT NULL,           -- limit count
    window_seconds INT NOT NULL,          -- time window (e.g., 3600 for hourly)
    burst_size    INT,                    -- for token bucket: max burst
    refill_rate   DOUBLE PRECISION,       -- for token bucket: tokens/sec
    penalty_action VARCHAR(20) DEFAULT 'REJECT', -- REJECT, THROTTLE, QUEUE
    retry_after_sec INT DEFAULT 60,
    is_active     BOOLEAN DEFAULT TRUE,
    priority      INT DEFAULT 0,          -- higher = evaluated first
    created_at    TIMESTAMP DEFAULT NOW(),
    updated_at    TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_rules_tier_endpoint ON rate_limit_rules(tier, endpoint);

-- PostgreSQL: API keys and client registry
CREATE TABLE api_clients (
    client_id     UUID PRIMARY KEY,
    api_key       VARCHAR(100) UNIQUE NOT NULL,
    client_name   VARCHAR(200) NOT NULL,
    tier          VARCHAR(30) NOT NULL DEFAULT 'FREE',
    owner_email   VARCHAR(255),
    is_active     BOOLEAN DEFAULT TRUE,
    rate_limit_override INT,              -- null = use tier default
    created_at    TIMESTAMP DEFAULT NOW(),
    expires_at    TIMESTAMP
);
CREATE INDEX idx_clients_api_key ON api_clients(api_key);

-- PostgreSQL: IP whitelist/blacklist
CREATE TABLE ip_rules (
    rule_id       UUID PRIMARY KEY,
    ip_address    INET NOT NULL,          -- supports IPv4, IPv6, CIDR notation
    rule_type     VARCHAR(10) NOT NULL,   -- WHITELIST, BLACKLIST
    reason        TEXT,
    expires_at    TIMESTAMP,              -- null = permanent
    created_by    UUID,
    created_at    TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_ip_rules_ip ON ip_rules(ip_address);

-- PostgreSQL: rate limit violations log (for analytics & abuse detection)
CREATE TABLE rate_limit_violations (
    violation_id  UUID PRIMARY KEY,
    client_id     UUID REFERENCES api_clients(client_id),
    ip_address    INET,
    endpoint      VARCHAR(200),
    rule_id       UUID REFERENCES rate_limit_rules(rule_id),
    limit_value   INT,
    current_count INT,
    action_taken  VARCHAR(20),            -- REJECTED, THROTTLED, QUEUED
    occurred_at   TIMESTAMP DEFAULT NOW()
);
-- Partitioned by month for efficient archiving
CREATE INDEX idx_violations_client ON rate_limit_violations(client_id, occurred_at DESC);
CREATE INDEX idx_violations_ip ON rate_limit_violations(ip_address, occurred_at DESC);

-- Redis: token bucket state
-- Key: "tb:{client_id}:{endpoint}" → Hash { tokens: 85.5, last_refill: 1710000000 }

-- Redis: sliding window log
-- Key: "swl:{client_id}:{endpoint}" → Sorted Set (score = timestamp_ms, member = request_id)

-- Redis: fixed window counter
-- Key: "fw:{client_id}:{endpoint}:{window_id}" → counter (TTL = window_seconds)
-- e.g., "fw:abc:api/users:1710000" → 47

-- Redis: client tier cache
-- Key: "client:{api_key}" → Hash { client_id, tier, override_limit } (TTL: 5 min)
```

### Capacity Estimation

```
Assumptions:
  1M requests/sec across all services
  100K unique clients/hour
  Average rule check: < 1ms

Redis operations:
  1M requests/sec → 1M Redis Lua script executions/sec
  Each: ~0.1ms → Redis handles this easily with cluster
  6-node Redis cluster: ~200K ops/sec per node ✅

Memory:
  100K active rate limit keys × 200 bytes = 20 MB
  Sliding window log (worst case): 100K × 1000 entries × 20B = 2 GB
  Token bucket: 100K × 50 bytes = 5 MB ✅ (much smaller)

Latency overhead:
  Redis round-trip: 0.1-0.5ms (same datacenter)
  Lua script execution: 0.05ms
  Total per-request overhead: < 1ms ✅

Rule storage:
  ~1000 rules × 500 bytes = 500 KB (in-memory cache)
```

---

## Q16: Design a Distributed Job Scheduler

> **Reference:** [Shreya Soni — Distributed Job Scheduler (LinkedIn)](https://www.linkedin.com/feed/update/urn:li:activity:7438497376904044544)

### Requirements

```
Functional:
  • Create, schedule, edit, and cancel jobs (one-time & recurring)
  • Execute jobs at the scheduled time (e.g., run a Python script)
  • Retry failed jobs with configurable retry count
  • Cancel running or future jobs
  • Search jobs by jobId, status, or userId
  • Store job artifacts/scripts in object storage

Non-Functional:
  • At-least-once execution guarantee
  • Scalable horizontally — handle millions of jobs
  • Fault-tolerant — detect and recover from executor crashes
  • Low scheduling latency (< 5 seconds from scheduled time)
  • 99.9% availability
  • Isolated execution — jobs shouldn't affect each other
```

### High-Level Architecture

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                     DISTRIBUTED JOB SCHEDULER — ARCHITECTURE                     │
│                                                                                  │
│  Client (User/API)                                                               │
│         │                                                                        │
│         ▼                                                                        │
│  ┌──────────────┐                                                                │
│  │ API Gateway   │  ← Auth, Rate Limiting, Routing                               │
│  └──────┬───────┘                                                                │
│         │                                                                        │
│    ┌────┴──────────────────┐                                                     │
│    │                       │                                                     │
│    ▼                       ▼                                                     │
│  ┌──────────────┐   ┌──────────────────┐                                         │
│  │ Job Service   │   │ Job Search       │                                         │
│  │              │   │ Service          │                                         │
│  │ • Create     │   │                  │                                         │
│  │ • Edit       │   │ • Search by      │                                         │
│  │ • Schedule   │   │   jobId, status  │                                         │
│  │ • Cancel     │   │ • Filter & page  │                                         │
│  └──────┬───────┘   └────────┬─────────┘                                         │
│         │                    │                                                   │
│         │ publish            │ read                                              │
│         ▼                    ▼                                                   │
│  ┌──────────────┐   ┌──────────────────┐                                         │
│  │    Kafka      │   │   Database       │                                         │
│  │ (Event Bus)   │   │  (Cassandra)     │                                         │
│  │              │   │                  │                                         │
│  │ Topics:      │   │ PK: user_id      │                                         │
│  │ • job-exec   │   │ CK: job_id       │                                         │
│  │ • job-retry  │   │                  │                                         │
│  │ • job-cancel │   │ Columns:          │                                         │
│  └──────┬───────┘   │ status, script,   │                                         │
│         │           │ retry_count,      │                                         │
│         │           │ scheduled_time,   │                                         │
│         │           │ modified_time     │                                         │
│         │           └──────────────────┘                                         │
│    ┌────┴────────────────────┐                                                   │
│    │                         │                                                   │
│    ▼                         ▼                                                   │
│  ┌──────────────┐   ┌──────────────────┐                                         │
│  │ Watcher       │   │ Job Consumer     │                                         │
│  │ Service       │   │ Service          │                                         │
│  │              │   │                  │                                         │
│  │ Poll every    │   │ Consumes Kafka   │                                         │
│  │ 10-20s for:  │   │ events to update │                                         │
│  │ • Upcoming   │   │ job status &     │                                         │
│  │   jobs       │   │ metadata in DB   │                                         │
│  │ • Stale      │   │                  │                                         │
│  │   running    │   │                  │                                         │
│  │   jobs       │   │                  │                                         │
│  └──────┬───────┘   └──────────────────┘                                         │
│         │                                                                        │
│         │ enqueue to Kafka                                                       │
│         ▼                                                                        │
│  ┌──────────────┐   ┌──────────────────┐   ┌──────────────────┐                   │
│  │ Executor      │   │   Docker         │   │   Object Store   │                   │
│  │ Service       │──►│   Container      │   │   (S3)           │                   │
│  │ (Multiple     │   │                  │   │                  │                   │
│  │  instances)   │   │ Runs job script  │   │ Job scripts &    │                   │
│  │              │   │ in isolation     │   │ artifacts        │                   │
│  │ Polls Redis  │   └──────────────────┘   └──────────────────┘                   │
│  │ for cancel   │                                                                │
│  │ signals      │   ┌──────────────────┐                                         │
│  │              │   │   Zookeeper       │                                         │
│  │ Reports to   │──►│                  │                                         │
│  │ Kafka on     │   │ Heartbeat &      │                                         │
│  │ success/fail │   │ coordination     │                                         │
│  └──────┬───────┘   └──────────────────┘                                         │
│         │                                                                        │
│         │ poll for cancel signals                                                │
│         ▼                                                                        │
│  ┌──────────────┐                                                                │
│  │   Redis       │                                                                │
│  │              │                                                                │
│  │ Cancel signal │                                                                │
│  │ store with    │                                                                │
│  │ TTL expiry    │                                                                │
│  └──────────────┘                                                                │
└──────────────────────────────────────────────────────────────────────────────────┘
```

### Service Interaction Diagram

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│              SERVICE INTERACTION — REQUEST & DATA FLOWS                           │
│                                                                                  │
│  ═══════════════════════════════════════════════════════════════════              │
│  FLOW 1: CREATE & SCHEDULE A JOB                                                │
│  ═══════════════════════════════════════════════════════════════════              │
│                                                                                  │
│  Client ──POST /jobs──► API Gateway ──► Job Service                              │
│                                            │                                     │
│                                            ├──► DB (INSERT job, status=SCHEDULED)│
│                                            │                                     │
│                                            └──► S3 (upload job script/artifact)  │
│                                                                                  │
│  ═══════════════════════════════════════════════════════════════════              │
│  FLOW 2: JOB EXECUTION (Happy Path)                                              │
│  ═══════════════════════════════════════════════════════════════════              │
│                                                                                  │
│  Watcher Service                                                                 │
│    │ (polls DB every 10-20s for jobs in [now, now+5min])                         │
│    │                                                                             │
│    ├──► DB: UPDATE status = QUEUED                                               │
│    │                                                                             │
│    └──► Kafka (job-exec topic): publish job event                                │
│              │                                                                   │
│              ▼                                                                   │
│         Executor Service (consumer group — multiple instances)                   │
│              │                                                                   │
│              ├──► S3: download job script                                         │
│              │                                                                   │
│              ├──► Docker: spin up container, execute script                       │
│              │                                                                   │
│              ├──► DB: UPDATE status = RUNNING, modified_time = now               │
│              │                                                                   │
│              ├──► Zookeeper: send heartbeat                                       │
│              │                                                                   │
│              └──► (on success) Kafka (job-status topic): publish SUCCESS          │
│                        │                                                         │
│                        ▼                                                         │
│                   Job Consumer Service                                           │
│                        │                                                         │
│                        └──► DB: UPDATE status = COMPLETED                        │
│                                                                                  │
│  ═══════════════════════════════════════════════════════════════════              │
│  FLOW 3: JOB FAILURE & RETRY                                                    │
│  ═══════════════════════════════════════════════════════════════════              │
│                                                                                  │
│  Executor Service                                                                │
│    │ (job execution fails inside Docker container)                               │
│    │                                                                             │
│    └──► Kafka (job-retry topic): publish FAILED event                            │
│              │                                                                   │
│              ▼                                                                   │
│         Job Consumer Service                                                     │
│              │                                                                   │
│              ├──► DB: INCREMENT retry_count, UPDATE status = RETRY_PENDING       │
│              │                                                                   │
│              └──► (if retry_count < max_retries)                                 │
│                     Kafka (job-exec topic): re-enqueue for execution              │
│                                                                                  │
│              └──► (if retry_count >= max_retries)                                │
│                     DB: UPDATE status = PERMANENTLY_FAILED                        │
│                     (move to Dead Letter Queue for inspection)                   │
│                                                                                  │
│  ═══════════════════════════════════════════════════════════════════              │
│  FLOW 4: EXECUTOR CRASH — FAILURE DETECTION                                     │
│  ═══════════════════════════════════════════════════════════════════              │
│                                                                                  │
│  Watcher Service                                                                 │
│    │ (polls DB every 10-20s)                                                     │
│    │                                                                             │
│    ├──► DETECT: status = RUNNING AND modified_time > 15s stale                   │
│    │                                                                             │
│    ├──► DB: UPDATE status = FAILED                                               │
│    │                                                                             │
│    └──► Kafka (job-retry topic): publish for retry                               │
│              │                                                                   │
│              ▼                                                                   │
│         (continues to FLOW 3 retry logic)                                        │
│                                                                                  │
│  ═══════════════════════════════════════════════════════════════════              │
│  FLOW 5: CANCEL JOB                                                             │
│  ═══════════════════════════════════════════════════════════════════              │
│                                                                                  │
│  Client ──DELETE /jobs/{id}──► API Gateway ──► Job Service                       │
│                                                    │                             │
│                                    ┌───────────────┤                             │
│                                    │               │                             │
│                              (if SCHEDULED)   (if RUNNING)                       │
│                                    │               │                             │
│                                    ▼               ▼                             │
│                              Kafka (cancel    Redis: SET                          │
│                              topic)           cancel:{jobId}                     │
│                                    │          with TTL                            │
│                                    ▼               │                             │
│                              Job Consumer          ▼                             │
│                              Service          Executor Service                   │
│                                    │          (polls Redis)                       │
│                                    ▼               │                             │
│                              DB: status =          ▼                             │
│                              CANCELLED        Kill Docker                         │
│                                               container                          │
│                                                    │                             │
│                                                    ▼                             │
│                                               DB: status =                       │
│                                               CANCELLED                          │
│                                                                                  │
│  ═══════════════════════════════════════════════════════════════════              │
│  FLOW 6: SEARCH JOBS                                                            │
│  ═══════════════════════════════════════════════════════════════════              │
│                                                                                  │
│  Client ──GET /jobs?status=RUNNING──► API Gateway ──► Job Search Service         │
│                                                            │                     │
│                                                            └──► DB (Cassandra)   │
│                                                                 query by         │
│                                                                 user_id +        │
│                                                                 filters          │
└──────────────────────────────────────────────────────────────────────────────────┘
```

### Component Deep Dive

#### API Gateway

```
Responsibilities:
  • Authentication & authorization (JWT / API keys)
  • Rate limiting (per-user, per-endpoint)
  • Request validation
  • Route to internal microservices (Job Service, Job Search Service)
  • TLS termination

Endpoints:
  POST   /api/v1/jobs              → Create/schedule a job
  PUT    /api/v1/jobs/{jobId}      → Edit a job
  DELETE /api/v1/jobs/{jobId}      → Cancel a job
  GET    /api/v1/jobs/{jobId}      → Get job details
  GET    /api/v1/jobs?status=X     → Search jobs
```

#### Job Service

```
Core operations:
  ┌─────────────────────────────────────────────────────────┐
  │                     JOB SERVICE                          │
  │                                                          │
  │  CREATE JOB:                                             │
  │    1. Validate request (cron expression, script path)    │
  │    2. Upload script to S3                                │
  │    3. Insert job record in Cassandra                     │
  │       (status = SCHEDULED, retry_count = 0)              │
  │    4. Return jobId to client                             │
  │                                                          │
  │  EDIT JOB:                                               │
  │    1. Validate job exists and is in SCHEDULED state      │
  │    2. Update fields in DB                                │
  │    3. If script changed, re-upload to S3                 │
  │                                                          │
  │  CANCEL JOB:                                             │
  │    1. Check current status                               │
  │    2. If SCHEDULED → publish cancel event to Kafka       │
  │    3. If RUNNING → write cancel signal to Redis          │
  │    4. If already COMPLETED/CANCELLED → return error      │
  └─────────────────────────────────────────────────────────┘
```

#### Watcher Service

```
  ┌─────────────────────────────────────────────────────────┐
  │                   WATCHER SERVICE                        │
  │                                                          │
  │  Poll Interval: 10-20 seconds                            │
  │                                                          │
  │  TASK 1 — Upcoming Job Detection:                        │
  │    Query: SELECT * FROM jobs                             │
  │            WHERE scheduled_time BETWEEN now AND now+5min │
  │              AND status = SCHEDULED                      │
  │    Action:                                               │
  │      • UPDATE status = QUEUED                            │
  │      • Publish to Kafka job-exec topic                   │
  │                                                          │
  │  TASK 2 — Stale Running Job Detection:                   │
  │    Query: SELECT * FROM jobs                             │
  │            WHERE status = RUNNING                        │
  │              AND modified_time < now - 15s               │
  │    Action:                                               │
  │      • UPDATE status = FAILED                            │
  │      • Publish to Kafka job-retry topic                  │
  │                                                          │
  │  Scaling: Use leader election (Zookeeper) to ensure      │
  │           only ONE watcher instance polls at a time      │
  └─────────────────────────────────────────────────────────┘
```

#### Executor Service

```
  ┌─────────────────────────────────────────────────────────┐
  │                  EXECUTOR SERVICE                        │
  │                                                          │
  │  Kafka Consumer Group: "executor-group"                  │
  │  (multiple instances for horizontal scaling)             │
  │                                                          │
  │  ON RECEIVING JOB EVENT:                                 │
  │    1. Download script from S3                            │
  │    2. Spin up Docker container                           │
  │    3. Mount script and execute                           │
  │    4. Periodically update modified_time in DB            │
  │       (heartbeat to prove "I'm still alive")            │
  │    5. Poll Redis for cancel signal (cancel:{jobId})      │
  │                                                          │
  │  ON SUCCESS:                                             │
  │    → Publish SUCCESS to Kafka job-status topic           │
  │    → Destroy Docker container                            │
  │                                                          │
  │  ON FAILURE:                                             │
  │    → Publish FAILED to Kafka job-retry topic             │
  │    → Destroy Docker container                            │
  │                                                          │
  │  ON CANCEL SIGNAL:                                       │
  │    → Kill Docker container                               │
  │    → Publish CANCELLED to Kafka job-status topic         │
  │                                                          │
  │  HEARTBEAT:                                              │
  │    → Send heartbeat to Zookeeper every 5s                │
  │    → If executor dies, Zookeeper detects and             │
  │      triggers rebalancing of Kafka consumer group        │
  └─────────────────────────────────────────────────────────┘
```

#### Kafka Topics & Event Schema

```
  ┌──────────────────────────────────────────────────────────┐
  │                    KAFKA TOPICS                            │
  │                                                            │
  │  Topic: job-exec                                           │
  │    Key: jobId                                              │
  │    Value: { jobId, userId, scriptPath, scheduledTime,      │
  │             maxRetries, timeout }                           │
  │    Producers: Watcher Service, Job Consumer (re-enqueue)   │
  │    Consumers: Executor Service (consumer group)            │
  │                                                            │
  │  Topic: job-retry                                          │
  │    Key: jobId                                              │
  │    Value: { jobId, retryCount, failureReason, timestamp }  │
  │    Producers: Executor Service, Watcher Service            │
  │    Consumers: Job Consumer Service                         │
  │                                                            │
  │  Topic: job-status                                         │
  │    Key: jobId                                              │
  │    Value: { jobId, status, executionTime, output }         │
  │    Producers: Executor Service                             │
  │    Consumers: Job Consumer Service                         │
  │                                                            │
  │  Topic: job-cancel                                         │
  │    Key: jobId                                              │
  │    Value: { jobId, cancelledBy, timestamp }                │
  │    Producers: Job Service                                  │
  │    Consumers: Job Consumer Service                         │
  │                                                            │
  │  DLQ: job-dlq                                              │
  │    Jobs that exceed max retries go here for manual          │
  │    inspection and replay.                                   │
  └──────────────────────────────────────────────────────────┘
```

#### Database Schema (Cassandra)

```
  ┌──────────────────────────────────────────────────────────┐
  │                 CASSANDRA DATA MODEL                       │
  │                                                            │
  │  Table: jobs                                               │
  │  ─────────────────────────────────────────────────         │
  │  Partition Key:  user_id (UUID)                            │
  │  Clustering Key: job_id (TIMEUUID, DESC)                   │
  │                                                            │
  │  Columns:                                                  │
  │  ┌────────────────────┬──────────────┬──────────────────┐  │
  │  │ Column             │ Type         │ Description       │  │
  │  ├────────────────────┼──────────────┼──────────────────┤  │
  │  │ user_id            │ UUID         │ Partition key     │  │
  │  │ job_id             │ TIMEUUID     │ Clustering key    │  │
  │  │ job_name           │ TEXT         │ Human-readable    │  │
  │  │ status             │ TEXT         │ SCHEDULED/QUEUED/ │  │
  │  │                    │              │ RUNNING/COMPLETED/│  │
  │  │                    │              │ FAILED/CANCELLED  │  │
  │  │ script_path        │ TEXT         │ S3 path           │  │
  │  │ scheduled_time     │ TIMESTAMP    │ When to execute   │  │
  │  │ cron_expression    │ TEXT         │ For recurring jobs│  │
  │  │ retry_count        │ INT          │ Current retries   │  │
  │  │ max_retries        │ INT          │ Max allowed       │  │
  │  │ timeout_seconds    │ INT          │ Execution timeout │  │
  │  │ created_time       │ TIMESTAMP    │ Creation time     │  │
  │  │ modified_time      │ TIMESTAMP    │ Last heartbeat    │  │
  │  │ execution_output   │ TEXT         │ Stdout/stderr     │  │
  │  └────────────────────┴──────────────┴──────────────────┘  │
  │                                                            │
  │  Secondary Index Table: jobs_by_status                     │
  │  ─────────────────────────────────────────────────         │
  │  PK: status    CK: scheduled_time (ASC)                    │
  │  (Materialized view for Watcher Service queries)           │
  │                                                            │
  │  Why Cassandra?                                            │
  │  • High write throughput for status updates                │
  │  • Scalable partitioning by user_id                        │
  │  • Tunable consistency (QUORUM for writes, ONE for reads)  │
  │  • Time-series friendly with TIMEUUID clustering           │
  └──────────────────────────────────────────────────────────┘
```

#### Redis — Cancellation Store

```
  ┌──────────────────────────────────────────────────────────┐
  │                  REDIS CANCEL STORE                        │
  │                                                            │
  │  Key Pattern:  cancel:{jobId}                              │
  │  Value:        { cancelledBy, timestamp }                  │
  │  TTL:          300 seconds (5 minutes)                     │
  │                                                            │
  │  SET cancel:abc-123 '{"by":"user1"}' EX 300                │
  │                                                            │
  │  Why Redis?                                                │
  │  • Sub-millisecond reads for polling from executors        │
  │  • TTL ensures stale cancel requests auto-expire           │
  │  • No persistent storage needed — ephemeral by nature      │
  │                                                            │
  │  Executor polling loop:                                    │
  │    while (jobRunning):                                     │
  │      if Redis.EXISTS("cancel:" + jobId):                   │
  │        killDockerContainer()                               │
  │        publishCancelledEvent()                             │
  │        break                                               │
  │      sleep(1s)                                             │
  └──────────────────────────────────────────────────────────┘
```

### Job State Machine

```
  ┌──────────────────────────────────────────────────────────────┐
  │                    JOB STATE MACHINE                          │
  │                                                               │
  │                    ┌───────────┐                              │
  │         ┌──────────│ SCHEDULED │◄──── Job Service creates    │
  │         │          └─────┬─────┘                              │
  │         │                │                                    │
  │         │   Watcher picks up (within 5 min window)           │
  │         │                │                                    │
  │         │                ▼                                    │
  │    Cancel (future)  ┌──────────┐                              │
  │         │           │  QUEUED  │──── Kafka job-exec topic     │
  │         │           └────┬─────┘                              │
  │         │                │                                    │
  │         │     Executor picks up from Kafka                   │
  │         │                │                                    │
  │         │                ▼                                    │
  │         │          ┌──────────┐                               │
  │         │          │ RUNNING  │──── Docker container active   │
  │         │          └──┬───┬───┘                               │
  │         │             │   │                                   │
  │         │      ┌──────┘   └──────────┐                       │
  │         │      │                     │                       │
  │         │   Success              Failure                     │
  │         │      │                     │                       │
  │         │      ▼                     ▼                       │
  │         │ ┌───────────┐    ┌──────────────────┐              │
  │         │ │ COMPLETED │    │  RETRY_PENDING   │              │
  │         │ └───────────┘    └────────┬─────────┘              │
  │         │                           │                        │
  │         │              retry_count < max?                    │
  │         │               ┌───────┴───────┐                   │
  │         │               │               │                   │
  │         │              YES              NO                  │
  │         │               │               │                   │
  │         │               ▼               ▼                   │
  │         │          ┌──────────┐  ┌──────────────────┐       │
  │         │          │  QUEUED  │  │ PERMANENTLY_FAILED│       │
  │         │          │ (re-enq) │  │ (→ DLQ)          │       │
  │         │          └──────────┘  └──────────────────┘       │
  │         │                                                    │
  │         ▼                                                    │
  │    ┌───────────┐                                             │
  │    │ CANCELLED │ ← via Kafka (future) or Redis (running)    │
  │    └───────────┘                                             │
  └──────────────────────────────────────────────────────────────┘
```

### Retry & Failure Strategy

```
  ┌──────────────────────────────────────────────────────────────┐
  │                  RETRY & FAILURE STRATEGY                     │
  │                                                               │
  │  Retry Policy:                                                │
  │  ┌──────────────────────────────────────────────────────────┐ │
  │  │ Approach: Exponential backoff with jitter                │ │
  │  │                                                          │ │
  │  │   Delay = min(base × 2^retryCount + random(0, jitter),  │ │
  │  │               maxDelay)                                  │ │
  │  │                                                          │ │
  │  │   Retry 1:  2s  + jitter                                │ │
  │  │   Retry 2:  4s  + jitter                                │ │
  │  │   Retry 3:  8s  + jitter                                │ │
  │  │   Retry 4: 16s  + jitter                                │ │
  │  │   Retry 5: 30s  (max delay cap)                         │ │
  │  └──────────────────────────────────────────────────────────┘ │
  │                                                               │
  │  Failure Detection — TWO mechanisms:                          │
  │                                                               │
  │  1. EXPLICIT FAILURE (executor reports)                       │
  │     Executor catches exception → publishes to job-retry topic │
  │     Fast: detected in < 1 second                              │
  │                                                               │
  │  2. IMPLICIT FAILURE (executor crash)                         │
  │     Executor process/container dies → cannot report failure   │
  │     Watcher polls DB for RUNNING jobs with stale modified_time│
  │     Threshold: modified_time > 15 seconds ago                 │
  │     Slower: detected within 10-35 seconds (poll interval)     │
  │                                                               │
  │  Dead Letter Queue (DLQ):                                     │
  │     Jobs exceeding max_retries are pushed to Kafka job-dlq    │
  │     topic for manual inspection, debugging, or replay         │
  └──────────────────────────────────────────────────────────────┘
```

### Approaches Comparison

| Approach | Scheduler | Pros | Cons |
|----------|-----------|------|------|
| **Approach 1: DB Polling (this design)** | Watcher polls DB every 10-20s | Simple, reliable, no message loss | Slight delay (up to poll interval), DB load |
| **Approach 2: Delay Queue (RabbitMQ/SQS)** | Message delayed delivery | Precise timing, no polling overhead | Queue depth issues, harder failure detection |
| **Approach 3: Time-wheel (Kafka + in-memory)** | Hierarchical timing wheel | O(1) insert/cancel, very low latency | Complex, state loss on crash, memory bound |
| **Approach 4: Cron-based (Kubernetes CronJob)** | K8s native scheduling | Zero infra to build, built-in retry | Limited to K8s, no fine-grained control |

### Scalability & Design Decisions

```
  ┌──────────────────────────────────────────────────────────────┐
  │              SCALABILITY & DESIGN DECISIONS                   │
  │                                                               │
  │  HORIZONTAL SCALING:                                          │
  │  ┌──────────────────────────────────────────────────────────┐ │
  │  │ Component         │ Scaling Strategy                     │ │
  │  ├───────────────────┼─────────────────────────────────────┤ │
  │  │ API Gateway       │ Multiple instances behind LB         │ │
  │  │ Job Service       │ Stateless → scale out freely         │ │
  │  │ Watcher Service   │ Leader election → 1 active, N standby│ │
  │  │ Executor Service  │ Kafka consumer group → auto-rebalance│ │
  │  │ Job Consumer      │ Kafka consumer group → auto-rebalance│ │
  │  │ Kafka             │ Add partitions + brokers             │ │
  │  │ Cassandra         │ Add nodes, vnodes handle rebalancing │ │
  │  │ Redis             │ Redis Cluster for HA                  │ │
  │  └──────────────────────────────────────────────────────────┘ │
  │                                                               │
  │  KEY DESIGN DECISIONS:                                        │
  │                                                               │
  │  1. Why Kafka over RabbitMQ?                                  │
  │     • Replay-ability: can reprocess events                    │
  │     • Consumer groups: natural load balancing for executors   │
  │     • Durability: persists messages to disk                   │
  │     • Throughput: handles millions of events/sec              │
  │                                                               │
  │  2. Why Docker for execution?                                 │
  │     • Isolation: one job can't crash another                  │
  │     • Consistency: same runtime everywhere                    │
  │     • Resource limits: CPU/memory capping per job             │
  │     • Security: sandboxed execution                           │
  │                                                               │
  │  3. Why Cassandra over PostgreSQL?                            │
  │     • Write-heavy workload (status updates every few seconds) │
  │     • Partition by user_id → queries scoped to single user    │
  │     • Linear scalability — add nodes, no resharding           │
  │     • AP system — availability over consistency for job status │
  │                                                               │
  │  4. Why Redis for cancellation (not DB)?                      │
  │     • Sub-ms reads needed for executor polling loop           │
  │     • Ephemeral data — TTL auto-cleanup                       │
  │     • No persistence overhead                                 │
  │                                                               │
  │  5. Why Zookeeper for coordination?                           │
  │     • Leader election for Watcher (avoid duplicate polling)   │
  │     • Executor health monitoring via ephemeral nodes          │
  │     • Kafka already depends on Zookeeper (or KRaft in newer)  │
  └──────────────────────────────────────────────────────────────┘
```

### Capacity Estimation

```
Assumptions:
  • 10M jobs scheduled per day
  • Average job execution: 30 seconds
  • Peak: 3x average load

Throughput:
  10M jobs/day = ~115 jobs/sec (average)
  Peak: ~350 jobs/sec
  Kafka can handle 100K+ messages/sec → no bottleneck

Executor capacity:
  If avg job = 30s, each executor handles ~2 jobs/min = 120 jobs/hr
  At peak 350 jobs/sec = 21,000 jobs/min
  Need: 21,000 / 2 = ~10,500 executor instances at peak
  With auto-scaling: 2,000 base + burst to 12,000

Storage (Cassandra):
  Each job record: ~1 KB
  10M jobs/day × 365 days × 1 KB = ~3.65 TB/year
  With RF=3: ~11 TB/year
  10-node cluster with 2 TB each → handles 2+ years

Kafka retention:
  Events: ~500 bytes each
  10M events/day × 500B = 5 GB/day
  7-day retention: 35 GB → trivial

Redis (cancel signals):
  Active running jobs at any time: ~10K
  Cancel signals: ~1% = 100 keys × 200 bytes = 20 KB
  Negligible memory usage

S3 (scripts & artifacts):
  Average script: 10 KB
  10M jobs × 10 KB = 100 GB/day (with dedup much less)
  Lifecycle policy: archive after 30 days
```

### Java LLD — Core Classes

```java
// === Job Entity ===
public class Job {
    private UUID jobId;
    private UUID userId;
    private String jobName;
    private JobStatus status;
    private String scriptPath;
    private Instant scheduledTime;
    private String cronExpression;
    private int retryCount;
    private int maxRetries;
    private int timeoutSeconds;
    private Instant createdTime;
    private Instant modifiedTime;
    private String executionOutput;
}

public enum JobStatus {
    SCHEDULED, QUEUED, RUNNING, COMPLETED,
    FAILED, RETRY_PENDING, PERMANENTLY_FAILED, CANCELLED
}

// === Job Service ===
public class JobService {
    private final JobRepository jobRepository;
    private final S3Client s3Client;
    private final KafkaProducer<String, JobEvent> kafkaProducer;
    private final RedisClient redisClient;

    public Job createJob(CreateJobRequest request) {
        String scriptPath = s3Client.upload(request.getScript());
        Job job = Job.builder()
            .jobId(UUID.randomUUID())
            .userId(request.getUserId())
            .jobName(request.getJobName())
            .status(JobStatus.SCHEDULED)
            .scriptPath(scriptPath)
            .scheduledTime(request.getScheduledTime())
            .cronExpression(request.getCronExpression())
            .maxRetries(request.getMaxRetries())
            .timeoutSeconds(request.getTimeoutSeconds())
            .retryCount(0)
            .createdTime(Instant.now())
            .modifiedTime(Instant.now())
            .build();
        jobRepository.save(job);
        return job;
    }

    public void cancelJob(UUID jobId) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new JobNotFoundException(jobId));

        switch (job.getStatus()) {
            case SCHEDULED:
            case QUEUED:
                kafkaProducer.send("job-cancel",
                    new CancelEvent(jobId, Instant.now()));
                break;
            case RUNNING:
                redisClient.setex("cancel:" + jobId,
                    300, "{\"timestamp\":\"" + Instant.now() + "\"}");
                break;
            default:
                throw new InvalidStateException(
                    "Cannot cancel job in " + job.getStatus() + " state");
        }
    }
}

// === Watcher Service ===
public class WatcherService implements Runnable {
    private final JobRepository jobRepository;
    private final KafkaProducer<String, JobEvent> kafkaProducer;

    @Override
    public void run() {
        while (true) {
            pollUpcomingJobs();
            detectStaleRunningJobs();
            sleep(Duration.ofSeconds(15));
        }
    }

    private void pollUpcomingJobs() {
        Instant now = Instant.now();
        Instant window = now.plus(Duration.ofMinutes(5));
        List<Job> upcoming = jobRepository
            .findByStatusAndScheduledTimeBetween(
                JobStatus.SCHEDULED, now, window);

        for (Job job : upcoming) {
            job.setStatus(JobStatus.QUEUED);
            job.setModifiedTime(Instant.now());
            jobRepository.save(job);
            kafkaProducer.send("job-exec",
                new ExecuteEvent(job.getJobId(), job.getScriptPath(),
                    job.getMaxRetries(), job.getTimeoutSeconds()));
        }
    }

    private void detectStaleRunningJobs() {
        Instant threshold = Instant.now().minus(Duration.ofSeconds(15));
        List<Job> stale = jobRepository
            .findByStatusAndModifiedTimeBefore(
                JobStatus.RUNNING, threshold);

        for (Job job : stale) {
            job.setStatus(JobStatus.FAILED);
            job.setModifiedTime(Instant.now());
            jobRepository.save(job);
            kafkaProducer.send("job-retry",
                new RetryEvent(job.getJobId(), job.getRetryCount(),
                    "Executor crash detected"));
        }
    }
}

// === Executor Service ===
public class ExecutorService {
    private final DockerClient dockerClient;
    private final S3Client s3Client;
    private final KafkaProducer<String, JobEvent> kafkaProducer;
    private final RedisClient redisClient;
    private final JobRepository jobRepository;

    @KafkaListener(topics = "job-exec", groupId = "executor-group")
    public void executeJob(ExecuteEvent event) {
        UUID jobId = event.getJobId();
        try {
            String script = s3Client.download(event.getScriptPath());
            String containerId = dockerClient.createContainer(script,
                event.getTimeoutSeconds());

            jobRepository.updateStatus(jobId, JobStatus.RUNNING);
            dockerClient.startContainer(containerId);

            CompletableFuture<?> heartbeat = startHeartbeat(jobId);
            CompletableFuture<?> cancelMonitor = monitorCancel(
                jobId, containerId);

            int exitCode = dockerClient.waitContainer(containerId);

            heartbeat.cancel(true);
            cancelMonitor.cancel(true);

            if (exitCode == 0) {
                kafkaProducer.send("job-status",
                    new StatusEvent(jobId, JobStatus.COMPLETED));
            } else {
                kafkaProducer.send("job-retry",
                    new RetryEvent(jobId, 0, "Exit code: " + exitCode));
            }
        } catch (Exception e) {
            kafkaProducer.send("job-retry",
                new RetryEvent(jobId, 0, e.getMessage()));
        }
    }

    private CompletableFuture<?> startHeartbeat(UUID jobId) {
        return CompletableFuture.runAsync(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                jobRepository.updateModifiedTime(jobId, Instant.now());
                sleep(Duration.ofSeconds(5));
            }
        });
    }

    private CompletableFuture<?> monitorCancel(
            UUID jobId, String containerId) {
        return CompletableFuture.runAsync(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (redisClient.exists("cancel:" + jobId)) {
                    dockerClient.killContainer(containerId);
                    kafkaProducer.send("job-status",
                        new StatusEvent(jobId, JobStatus.CANCELLED));
                    break;
                }
                sleep(Duration.ofSeconds(1));
            }
        });
    }
}

// === Job Consumer Service ===
public class JobConsumerService {
    private final JobRepository jobRepository;
    private final KafkaProducer<String, JobEvent> kafkaProducer;

    @KafkaListener(topics = "job-status", groupId = "consumer-group")
    public void handleStatusUpdate(StatusEvent event) {
        jobRepository.updateStatus(
            event.getJobId(), event.getStatus());
    }

    @KafkaListener(topics = "job-retry", groupId = "consumer-group")
    public void handleRetry(RetryEvent event) {
        Job job = jobRepository.findById(event.getJobId()).orElseThrow();
        int newRetryCount = job.getRetryCount() + 1;

        if (newRetryCount <= job.getMaxRetries()) {
            job.setRetryCount(newRetryCount);
            job.setStatus(JobStatus.RETRY_PENDING);
            jobRepository.save(job);

            long delay = calculateBackoff(newRetryCount);
            scheduledExecutor.schedule(() ->
                kafkaProducer.send("job-exec",
                    new ExecuteEvent(job.getJobId(),
                        job.getScriptPath(),
                        job.getMaxRetries(),
                        job.getTimeoutSeconds())),
                delay, TimeUnit.MILLISECONDS);
        } else {
            job.setStatus(JobStatus.PERMANENTLY_FAILED);
            jobRepository.save(job);
            kafkaProducer.send("job-dlq",
                new DlqEvent(job.getJobId(), event.getFailureReason()));
        }
    }

    @KafkaListener(topics = "job-cancel", groupId = "consumer-group")
    public void handleCancel(CancelEvent event) {
        jobRepository.updateStatus(
            event.getJobId(), JobStatus.CANCELLED);
    }

    private long calculateBackoff(int retryCount) {
        long base = 2000;
        long maxDelay = 30000;
        long delay = (long) (base * Math.pow(2, retryCount - 1));
        long jitter = ThreadLocalRandom.current().nextLong(0, 1000);
        return Math.min(delay + jitter, maxDelay);
    }
}
```

### Interview Tips

```
Key Talking Points:
  ✅ Exactly-once vs at-least-once: this design uses at-least-once with
     idempotent execution (prefer idempotent jobs)
  ✅ Leader election for Watcher prevents duplicate job enqueueing
  ✅ Two failure detection mechanisms (explicit + implicit) cover all cases
  ✅ DLQ prevents infinite retry loops
  ✅ Docker isolation prevents one bad job from taking down the executor
  ✅ Kafka consumer groups give natural load balancing and fault tolerance

Common Follow-Up Questions:
  Q: "How do you handle recurring/cron jobs?"
  A: After COMPLETED, Job Consumer checks cron_expression. If present,
     calculate next scheduled_time and INSERT a new SCHEDULED job.

  Q: "How do you guarantee exactly-once execution?"
  A: True exactly-once is very hard. Use at-least-once with idempotency
     keys. Each job has a unique execution_id; the job script should be
     idempotent or check the execution_id before performing side effects.

  Q: "What if Watcher goes down?"
  A: Zookeeper leader election promotes a standby Watcher. Jobs may be
     delayed by up to one poll interval (10-20s) during failover.

  Q: "How do you handle job dependencies (DAG)?"
  A: Add a depends_on field with a list of jobIds. Job Consumer checks
     if all dependencies are COMPLETED before enqueueing. This creates
     a DAG executor similar to Apache Airflow.

  Q: "What about job priority?"
  A: Use separate Kafka topics per priority level (high, medium, low).
     Executors consume from high-priority topic first. Or use a priority
     queue in front of Kafka.
```

---

## Interview Approach — How to Present These Designs

```
┌──────────────────────────────────────────────────────────────┐
│ FOR EVERY QUESTION, FOLLOW THIS 5-STEP STRUCTURE:            │
│                                                               │
│ Step 1: CLARIFY (2-3 min)                                    │
│   "What are the core features?"                              │
│   "How many users? Read/write ratio?"                        │
│   "Consistency vs availability preference?"                  │
│                                                               │
│ Step 2: ESTIMATE (2 min)                                     │
│   QPS, storage, bandwidth                                    │
│   "100M DAU × 10 requests = 1B/day ≈ 12K QPS"              │
│                                                               │
│ Step 3: HIGH-LEVEL DESIGN (5 min)                            │
│   Draw the main components (boxes and arrows)                │
│   Client → LB → API → Cache → DB                            │
│                                                               │
│ Step 4: DEEP DIVE (8-10 min)                                 │
│   Pick 2-3 components the interviewer cares about            │
│   Database schema, API design, algorithms                    │
│                                                               │
│ Step 5: WRAP UP (2 min)                                      │
│   Trade-offs you made and why                                │
│   What you'd improve with more time                          │
│   Monitoring and alerting strategy                           │
└──────────────────────────────────────────────────────────────┘
```

---

## Further Reading

| Topic | Document |
|-------|----------|
| [System Design Fundamentals](system-design-fundamentals.md) | Scalability, Availability, CAP, Latency, Throughput |
| [System Design 30 Concepts](system-design-30-concepts.md) | 30 building blocks from DNS to Idempotency |
| [System Design Handbook](system-design-handbook.md) | Architecture Patterns, Interview Framework |
| [Load Balancing](load-balancing.md) | Algorithms, Consistent Hashing, Health Checks |
| [Databases](databases.md) | SQL vs NoSQL, Replication, Sharding |
| [Caching Strategies](caching-strategies.md) | Write-Through/Back, LRU/LFU, Eviction |
| [Rate Limiting](rate-limiting.md) | Token Bucket, Sliding Window, Distributed |
| [Apache Kafka](kafka.md) | Topics, Partitions, Consumer Groups |
| [Senior Java Interview](senior-java-interview.md) | 20 Production-Grade Questions |

> **Total:** 16 system designs with multiple approaches, pros/cons comparison tables, Java LLD code, and capacity estimation for each.

# Master Apache Kafka — Deep Concepts with Real-Time Examples

A comprehensive guide to understanding Apache Kafka — from fundamentals to production architecture, with real-world scenarios from companies like Uber, Netflix, LinkedIn, and Walmart.

---

## 1. What is Kafka?

Apache Kafka is a **distributed event store and stream-processing platform**. Originally developed at LinkedIn to handle 1 trillion messages/day, it is now an open-source Apache project used by 80%+ of Fortune 100 companies.

**Think of Kafka as a commit log for your entire business.** Every event that happens — a user clicked a button, an order was placed, a sensor reported a temperature — gets written to Kafka as an immutable, ordered record.

### Key Characteristics

| Feature | Description |
|---------|-------------|
| **Distributed** | Runs as a cluster of one or more brokers across multiple servers |
| **Fault-tolerant** | Data is replicated across brokers; survives node failures |
| **Durable** | Messages are persisted to disk with configurable retention |
| **Scalable** | Handles millions of messages per second by adding partitions and brokers |
| **Real-time** | Supports both real-time streaming and batch processing |

### Real-Time Example: Why LinkedIn Built Kafka

**Problem:** LinkedIn had 100+ microservices, each with point-to-point connections for data sharing. With N services, they had N*(N-1) connections — an unmaintainable mess.

```
BEFORE Kafka (point-to-point):               AFTER Kafka (hub-and-spoke):

  User Service ←→ Search Service              User Service ──┐
       ↕              ↕                       Search Service ──┤
  Ad Service ←→ Analytics Service             Ad Service ──────┤──► KAFKA ──►  Any Consumer
       ↕              ↕                       Analytics ───────┤
  Email Service ←→ Recommendation             Email Service ──┘
                                              
  Connections: N*(N-1) = 30                   Connections: 2*N = 10
```

Every service just publishes to Kafka or consumes from Kafka. No direct coupling.

### Kafka vs Traditional Messaging

```
Traditional Queue:          Kafka:
┌──────────┐               ┌──────────────────────┐
│ Producer │──► Queue ──►  │ Producer ──► Topic    │
│          │   (deleted    │   (retained on disk)  │
│          │   after read) │   ▼         ▼         │
└──────────┘               │ Consumer  Consumer    │
                           │ Group A   Group B     │
                           └──────────────────────┘
```

- **Traditional queues** delete messages after consumption
- **Kafka** retains messages for a configurable period — multiple consumers can read independently

### Real-Time Example: Uber's Ride Matching

```
Rider opens app → "ride-request" topic
                        │
                        ├──► Matching Service (finds nearby drivers)
                        ├──► Pricing Service (calculates surge)
                        ├──► Analytics Service (tracks demand patterns)
                        └──► Fraud Service (detects fake requests)

All 4 services consume the SAME event independently.
With a traditional queue, only ONE could read each message.
```

---

## 2. What is a Message?

A **message** (also called a **record** or **event**) is the basic unit of data in Kafka. It represents something that happened in your system.

### Message Structure

```
┌─────────────────────────────────────────────┐
│                  Message                     │
├──────────┬──────────┬───────────┬───────────┤
│   Key    │  Value   │ Timestamp │  Headers  │
│ (bytes)  │ (bytes)  │  (long)   │ (key-val) │
├──────────┼──────────┼───────────┼───────────┤
│ "user-42"│ {"name": │ 17098...  │ source:   │
│          │  "Alice"}│           │ "web-app" │
└──────────┴──────────┴───────────┴───────────┘
```

| Field | Required | Purpose |
|-------|----------|---------|
| **Key** | Optional | Determines partition assignment; messages with the same key go to the same partition |
| **Value** | Yes | The actual data payload (JSON, Avro, Protobuf, plain text, etc.) |
| **Timestamp** | Auto | When the message was created or appended |
| **Headers** | Optional | Metadata key-value pairs (tracing IDs, source info) |

### Why Keys Matter — Real-Time Example: E-Commerce Orders

```
Scenario: An order goes through multiple state changes:
  ORDER_CREATED → PAYMENT_RECEIVED → SHIPPED → DELIVERED

Key = "order-12345"

Because the key is the same, ALL events for order-12345 go to the SAME partition.
This guarantees the events are consumed IN ORDER.

Without a key (round-robin): events might land on different partitions
  → Consumer could see SHIPPED before PAYMENT_RECEIVED! 
```

### Serialization Formats

| Format | Size | Schema | Human-Readable | Use Case |
|--------|------|--------|----------------|----------|
| **JSON** | Large | No | Yes | Prototyping, debugging |
| **Avro** | Small | Yes (Schema Registry) | No | Production data pipelines |
| **Protobuf** | Small | Yes (.proto files) | No | gRPC services |
| **String** | Varies | No | Yes | Logs, simple text |

### Real-Time Example: Schema Evolution with Avro

```
Version 1: { "name": "Alice", "email": "alice@test.com" }
Version 2: { "name": "Alice", "email": "alice@test.com", "phone": "555-0100" }

With Avro + Schema Registry:
  - Old consumers can still read new messages (ignore "phone")
  - New consumers can read old messages ("phone" defaults to null)
  - No breaking changes!

With raw JSON: each consumer must handle missing fields manually.
```

### Message Guarantees

| Guarantee | Meaning | Config | Real-Time Example |
|-----------|---------|--------|-------------------|
| **At-most-once** | May lose messages, never duplicate | `acks=0` | Ad impression tracking (losing a few is OK) |
| **At-least-once** | No loss, possible duplicates | `acks=all` | Order events (duplicate is safer than loss) |
| **Exactly-once** | No loss, no duplicates | idempotent + transactions | Financial transactions (must be precise) |

---

## 3. Topics & Partitions

### Topics — The Logical Organization

A **topic** is a named category or feed to which messages are published. Think of it as a database table or a folder in a filesystem.

**Naming convention best practices:**

```
<domain>.<entity>.<action>

Examples:
  ecommerce.order.created
  payment.transaction.completed
  user.profile.updated
  logistics.shipment.tracking
  iot.sensor.temperature-reading
```

### Real-Time Example: Netflix Topic Design

```
Netflix organizes topics by domain:

  streaming.play.started         → User pressed play
  streaming.play.heartbeat       → Every 10 sec during playback
  streaming.play.stopped         → User stopped/paused
  streaming.quality.change       → Bitrate changed (buffering)
  
  recommendation.view.event      → User browsed content
  recommendation.click.event     → User clicked a title
  
  billing.subscription.created   → New subscriber
  billing.payment.processed      → Monthly charge

Each topic can have different:
  - Partition count (high-traffic topics get more)
  - Retention period (billing = forever, heartbeats = 7 days)
  - Replication factor (billing = 3, heartbeats = 2)
```

### Partitions — The Unit of Parallelism

Each topic is divided into one or more **partitions** — ordered, immutable sequences of messages.

```
Topic: order-events (3 partitions)

Partition 0: [ msg0 | msg3 | msg6 | msg9  | ... ] → Broker 1
Partition 1: [ msg1 | msg4 | msg7 | msg10 | ... ] → Broker 2
Partition 2: [ msg2 | msg5 | msg8 | msg11 | ... ] → Broker 3
                ↑
            Offset (sequential ID within a partition)
```

### Key Concepts

| Concept | Description |
|---------|-------------|
| **Offset** | Sequential ID assigned to each message within a partition (0, 1, 2, ...) |
| **Ordering** | Guaranteed only **within a partition**, not across partitions |
| **Key-based routing** | Messages with the same key always go to the same partition → preserves order per key |
| **Replication factor** | Number of copies of each partition across brokers (typically 2–3) |
| **Leader / Follower** | Each partition has one leader (handles reads/writes) and N-1 followers (replicas) |

### Partition Assignment

```
Key: "user-42"  ──hash──► hash("user-42") % 3 = 1  ──► Partition 1
Key: "user-99"  ──hash──► hash("user-99") % 3 = 0  ──► Partition 0
Key: null       ──────────► Round-robin across partitions
```

### How Many Partitions? — Real-Time Decision Guide

```
Rule of thumb: partitions = max(T/P, T/C)
  T = target throughput (messages/sec)
  P = throughput a single producer partition can achieve (~100 MB/s)
  C = throughput a single consumer can achieve (~50 MB/s)

Real example at Walmart:
  - Black Friday: 500,000 orders/sec
  - Each consumer handles ~5,000 orders/sec
  - Partitions needed: 500,000 / 5,000 = 100 partitions
  - They use 128 partitions (power of 2, some headroom)
```

### Real-Time Example: Why Ordering Matters

```
Scenario: Bank account transactions for account "ACC-1001"

  Event 1: DEPOSIT  $1000  (balance should become $1000)
  Event 2: WITHDRAW $500   (balance should become $500)
  Event 3: WITHDRAW $300   (balance should become $200)

With key = "ACC-1001" → ALL go to Partition 0 → processed IN ORDER ✅

Without key (round-robin):
  Event 1 → Partition 0 (processed first)  → balance = $1000
  Event 3 → Partition 2 (processed second) → balance = $1000 - $300 = $700 ❌
  Event 2 → Partition 1 (processed third)  → balance = $700 - $500 = $200
  
  Intermediate state was WRONG. If the system crashed after Event 3,
  the account would show $700 instead of $200.
```

### Log Compaction vs Deletion

```
Retention = Delete (default):
  Keep all messages for N hours/days, then delete oldest segments.
  
  Timeline: [msg1][msg2][msg3][msg4][msg5] ──7 days──► [msg4][msg5]
  
  Use: Event logs, clickstreams, IoT data

Retention = Compact:
  Keep only the LATEST value per key. Earlier values are removed.
  
  Before compaction:
    key=A val=1, key=B val=2, key=A val=3, key=C val=4, key=B val=5
  
  After compaction:
    key=A val=3, key=C val=4, key=B val=5
  
  Use: Changelogs (current state of each entity), config updates
  
  Real example: Uber driver locations
    key="driver-42" → latest GPS coordinates only
    Don't need history of every GPS ping, just current location
```

---

## 4. Advantages of Kafka

### Why Choose Kafka?

| Advantage | Description |
|-----------|-------------|
| **Multiple Producers** | Many applications can write to the same topic simultaneously |
| **Multiple Consumers** | Independent consumer groups read the same data without interfering |
| **Disk-based Retention** | Messages are persisted to disk; configurable retention (hours, days, forever) |
| **Scalable** | Add partitions and brokers to scale horizontally |
| **High Throughput** | Millions of messages/sec via batching, compression, zero-copy |
| **Fault Tolerant** | Replication ensures no data loss when brokers fail |
| **Back-pressure Friendly** | Consumers read at their own pace; producers aren't blocked |
| **Replay Capability** | Consumers can re-read old messages by resetting offsets |

### How Kafka Achieves High Performance

```
1. SEQUENTIAL I/O (not random I/O)
   ┌──────────────────────────────────────────┐
   │ Disk: [...][msg1][msg2][msg3][msg4]...   │  ← append-only log
   └──────────────────────────────────────────┘
   Sequential writes to disk can be FASTER than random writes to RAM!
   
2. ZERO-COPY (sendfile system call)
   Traditional:  Disk → Kernel Buffer → User Buffer → Socket Buffer → NIC
   Kafka:        Disk → Kernel Buffer ─────────────────────────────► NIC
   Eliminates 2 copies and 2 context switches.
   
3. BATCHING
   Instead of sending 1000 messages individually:
   ┌────┐┌────┐┌────┐    ┌─────────────────────────┐
   │msg1││msg2││msg3│ →  │ batch: [msg1|msg2|msg3]  │ → 1 network call
   └────┘└────┘└────┘    └─────────────────────────┘
   
4. COMPRESSION (snappy, lz4, zstd)
   Batch of 1000 JSON messages: 500 KB
   After lz4 compression:       50 KB (90% reduction)
   
5. PAGE CACHE
   Kafka relies on the OS page cache instead of JVM heap.
   → No GC pauses, data stays in cache across restarts.
```

### Kafka vs Alternatives

| Feature | Kafka | RabbitMQ | AWS SQS | Redis Streams | Pulsar |
|---------|-------|----------|---------|---------------|--------|
| Throughput | Very High | Moderate | Moderate | High | Very High |
| Retention | Configurable | Until consumed | 14 days | Memory-limited | Configurable |
| Ordering | Per partition | Per queue | No guarantee | Per stream | Per partition |
| Replay | Yes | No | No | Yes | Yes |
| Consumer groups | Yes | Yes | Yes | Yes | Yes |
| Exactly-once | Yes | No | No | No | Yes |
| Multi-tenancy | Limited | Good | N/A | Limited | Built-in |
| Geo-replication | MirrorMaker | Federation | Cross-region | No | Built-in |

### Real-Time Example: LinkedIn's Scale

```
LinkedIn Kafka Stats (2024):
  - 7+ trillion messages per day
  - 100+ PB of data stored
  - 4,000+ Kafka brokers across data centers
  - Peak: 13 million messages/sec
  
Topics include:
  - Member activity (page views, clicks)
  - Ad impressions and clicks
  - Connection requests
  - Job application events
  - Message sends
  
Every single user interaction becomes a Kafka event.
```

---

## 5. Kafka Producer — Deep Dive

A **producer** is an application that publishes (writes) messages to Kafka topics.

### How Producers Work Internally

```
┌─────────────────────────────────────────────────────────────────┐
│                        PRODUCER                                 │
│                                                                 │
│  ┌─────────┐   ┌──────────────┐   ┌──────────────┐            │
│  │ Record  │──►│ Serializer   │──►│ Partitioner  │            │
│  │         │   │ Key + Value  │   │ (which       │            │
│  │ key     │   │ → bytes      │   │  partition?) │            │
│  │ value   │   └──────────────┘   └──────┬───────┘            │
│  │ topic   │                             │                     │
│  └─────────┘                             ▼                     │
│                              ┌──────────────────────┐          │
│                              │   Record Accumulator │          │
│                              │                      │          │
│                              │  Partition 0: [batch]│          │
│                              │  Partition 1: [batch]│──► Sender Thread ──► Broker
│                              │  Partition 2: [batch]│   (async)
│                              └──────────────────────┘          │
└─────────────────────────────────────────────────────────────────┘
```

### Producer Configuration

| Config | Default | Description |
|--------|---------|-------------|
| `bootstrap.servers` | — | Broker addresses (`host1:9092,host2:9092`) |
| `key.serializer` | — | How to serialize the key (e.g., `StringSerializer`) |
| `value.serializer` | — | How to serialize the value |
| `acks` | `1` | `0`=fire-and-forget, `1`=leader ack, `all`=all replicas ack |
| `retries` | `2147483647` | Number of retries on failure |
| `batch.size` | `16384` | Max bytes per batch |
| `linger.ms` | `0` | Time to wait before sending a batch |
| `compression.type` | `none` | `gzip`, `snappy`, `lz4`, `zstd` |
| `enable.idempotence` | `false` | Prevent duplicate messages on retry |
| `max.in.flight.requests` | `5` | Max unacknowledged requests per connection |
| `buffer.memory` | `33554432` | Total memory for buffering unsent records |

### acks Explained — Real-Time Example

```
Scenario: E-commerce order event produced to Kafka

acks=0 (Fire & Forget):
  Producer ──► Broker (don't wait)
  Speed: ⚡ Fastest
  Risk:  Message could be lost if broker crashes before writing to disk
  Use:   Ad click tracking (losing 1 in 100,000 is acceptable)

acks=1 (Leader Acknowledgment):
  Producer ──► Broker Leader ──► "OK, written to my log"
  Speed: ⚡ Fast
  Risk:  Leader crashes AFTER ack but BEFORE followers replicate
         → Message lost
  Use:   Session events, page views

acks=all (All In-Sync Replicas):
  Producer ──► Leader ──► Follower 1 ──► Follower 2 ──► "All confirmed"
  Speed: 🐢 Slowest
  Risk:  Virtually none (all copies must confirm)
  Use:   Payment events, financial transactions, order state changes
```

### Idempotent Producer — Solving Duplicates

```
Problem: Network timeout during ack
  Producer ──► Broker (writes msg) ──✕──► Producer (timeout, no ack received)
  Producer retries ──► Broker (writes msg AGAIN) → DUPLICATE!

Solution: enable.idempotence=true
  Each producer gets a Producer ID (PID) and sequence number per partition.
  
  Attempt 1: PID=5, seq=42 → Broker writes it
  Attempt 2: PID=5, seq=42 → Broker says "already have seq=42, skipping"
  
  Result: Exactly one copy in the log, even with retries.
```

### Producer Code Example (Java)

```java
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
props.put("acks", "all");
props.put("enable.idempotence", "true");

KafkaProducer<String, String> producer = new KafkaProducer<>(props);

// Synchronous send
ProducerRecord<String, String> record =
    new ProducerRecord<>("order-events", "order-123", "{\"status\":\"CREATED\"}");
producer.send(record).get();

// Asynchronous send with callback
producer.send(record, (metadata, exception) -> {
    if (exception == null) {
        System.out.println("Sent to partition " + metadata.partition()
            + " at offset " + metadata.offset());
    } else {
        exception.printStackTrace();
    }
});

producer.close();
```

### Real-Time Example: Uber's Trip Event Producer

```
When a rider requests a ride, Uber's producer sends:

Topic: "ride-requests"
Key:   "city-SFO"  (all San Francisco rides go to same partition for geo-ordering)
Value: {
  "ride_id": "R-98765",
  "rider_id": "U-12345",
  "pickup": {"lat": 37.7749, "lng": -122.4194},
  "dropoff": {"lat": 37.3382, "lng": -121.8863},
  "timestamp": 1710000000,
  "surge_multiplier": 1.5,
  "vehicle_type": "UberX"
}

Config:
  acks=all             (never lose a ride request)
  compression=lz4      (fast compression for high throughput)
  linger.ms=5          (batch for 5ms to improve throughput)
  batch.size=65536     (64KB batches)
  enable.idempotence=true
```

### Batching & Performance

```
Without batching:       With batching (linger.ms=5):
msg1 ──► network        msg1 ─┐
msg2 ──► network        msg2 ─┤──► batch ──► network (1 request)
msg3 ──► network        msg3 ─┘
(3 network calls)       (1 network call — 3x less overhead)

Performance impact at scale:
  10,000 msg/sec, no batching  → 10,000 network round trips/sec
  10,000 msg/sec, linger.ms=5 → ~200 batched requests/sec (50x reduction)
```

---

## 6. Kafka Consumer — Deep Dive

A **consumer** reads messages from Kafka topics. Consumers are organized into **consumer groups** for parallel processing.

### How Consumers Work Internally

```
┌────────────────────────────────────────────────────────────┐
│                        CONSUMER                             │
│                                                             │
│  poll() ──► Fetch Request ──► Broker ──► Fetch Response     │
│                                              │              │
│                                    ┌─────────▼──────────┐  │
│                                    │ Deserializer        │  │
│                                    │ bytes → Key, Value  │  │
│                                    └─────────┬──────────┘  │
│                                              │              │
│                                    ┌─────────▼──────────┐  │
│                                    │ ConsumerRecords     │  │
│                                    │ → process each      │  │
│                                    └─────────┬──────────┘  │
│                                              │              │
│                                    commitSync() / commitAsync()
└────────────────────────────────────────────────────────────┘
```

### Consumer Groups — The Key Concept

```
Topic: order-events (4 partitions)

Consumer Group "order-service":
  Consumer 1 ◄── Partition 0, Partition 1
  Consumer 2 ◄── Partition 2, Partition 3

Consumer Group "analytics":
  Consumer A ◄── Partition 0, Partition 1, Partition 2, Partition 3
```

### Consumer Group Rules

| Rule | Description |
|------|-------------|
| Each partition → **one consumer** per group | Ensures no duplicate processing within a group |
| More consumers than partitions → **idle consumers** | Extra consumers sit idle until a rebalance |
| Different groups → **independent** | Each group gets all messages independently |
| Consumer failure → **rebalance** | Partitions are reassigned to remaining consumers |

### Real-Time Example: Scaling Consumers

```
Scenario: E-commerce platform processing orders

Stage 1: Low traffic (100 orders/sec)
  Topic: orders (4 partitions)
  Consumer Group: order-processor
    Consumer 1 ← P0, P1, P2, P3  (handles all 100 orders/sec)

Stage 2: Growing traffic (400 orders/sec)
  Consumer 1 ← P0, P1  (handles 200 orders/sec)
  Consumer 2 ← P2, P3  (handles 200 orders/sec)

Stage 3: Black Friday (1600 orders/sec) — need more partitions!
  Topic: orders (16 partitions)  ← increased partitions
  Consumer 1  ← P0, P1, P2, P3
  Consumer 2  ← P4, P5, P6, P7
  Consumer 3  ← P8, P9, P10, P11
  Consumer 4  ← P12, P13, P14, P15
  (each handles 400 orders/sec)

Stage 4: Adding Consumer 5 with only 4 partitions?
  Consumer 5 ← (IDLE — no partition to assign!)
  Rule: consumers ≤ partitions
```

### Offset Management — Deep Explanation

```
Partition 0:  [ 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 ]
                              ▲           ▲
                         Committed    Current position
                          Offset      (Last polled)

On restart: Consumer resumes from committed offset (3)
            → Messages 3, 4, 5, 6, 7, 8 are re-delivered
```

### Auto-Commit vs Manual Commit — When It Matters

```
AUTO-COMMIT (enable.auto.commit=true):
  poll() → get 100 records → process 50 → auto-commit fires → CRASH!
  On restart: offset already committed at 100
  Result: 50 records LOST (never processed but offset moved forward)

MANUAL COMMIT:
  poll() → get 100 records → process all 100 → commitSync()
  If crash before commit: records 0-99 are re-delivered (at-least-once)
  If crash after commit: clean restart from 100

Real-time rule:
  - Auto-commit: OK for analytics, dashboards (approximate is fine)
  - Manual commit: Required for order processing, payments (can't lose data)
```

### Real-Time Example: Netflix Viewing History

```
Topic: "streaming.play.events" (64 partitions)

Consumer Group: "viewing-history-service"
  16 consumers, each handling 4 partitions

When user plays a movie:
  Key: "user-42"
  Value: { "title": "Stranger Things", "episode": "S4E1", 
           "action": "PLAY", "position_sec": 0, "device": "smart-tv" }

Every 10 seconds during playback:
  Key: "user-42"
  Value: { "title": "Stranger Things", "episode": "S4E1",
           "action": "HEARTBEAT", "position_sec": 600, "device": "smart-tv" }

The viewing-history consumer:
  1. Receives the event
  2. Updates the user's "Continue Watching" list
  3. Commits the offset
  4. If user opens Netflix on phone → sees "Resume at 10:00"
```

### Consumer Configuration

| Config | Default | Description |
|--------|---------|-------------|
| `bootstrap.servers` | — | Broker addresses |
| `group.id` | — | Consumer group name |
| `auto.offset.reset` | `latest` | `earliest` (from beginning) or `latest` (new messages only) |
| `enable.auto.commit` | `true` | Automatically commit offsets |
| `auto.commit.interval.ms` | `5000` | Auto-commit frequency |
| `max.poll.records` | `500` | Max records per poll() call |
| `session.timeout.ms` | `45000` | Time before consumer is considered dead |
| `max.poll.interval.ms` | `300000` | Max time between polls before rebalance |
| `fetch.min.bytes` | `1` | Min data per fetch (higher = fewer requests) |
| `fetch.max.wait.ms` | `500` | Max wait time for fetch.min.bytes |

### Consumer Code Example (Java)

```java
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("group.id", "order-service");
props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
props.put("auto.offset.reset", "earliest");
props.put("enable.auto.commit", "false");     // manual commit for safety

KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
consumer.subscribe(List.of("order-events"));

while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
    for (ConsumerRecord<String, String> record : records) {
        processOrder(record);    // business logic
    }
    consumer.commitSync();       // commit only after successful processing
}
```

### Rebalancing — What Happens When Consumers Change

```
Before: 3 consumers, 6 partitions
  C1 ← P0, P1    C2 ← P2, P3    C3 ← P4, P5

C3 crashes → Rebalance triggered!

During rebalance (seconds):
  ALL consumers stop processing (stop-the-world)
  Group Coordinator reassigns partitions

After: 2 consumers, 6 partitions
  C1 ← P0, P1, P2    C2 ← P3, P4, P5

Strategies:
  Eager (default):    Stop all → reassign all → resume
  Cooperative/Sticky: Only reassign affected partitions (less disruption)
```

---

## 7. Kafka Cluster — Deep Dive

A **Kafka cluster** consists of multiple **brokers** (servers) working together to store and serve data.

### Cluster Architecture

```
┌────────────────────────────────────────────────────────────┐
│                      Kafka Cluster                          │
│                                                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Broker 0 │  │ Broker 1 │  │ Broker 2 │  │ Broker 3 │  │
│  │          │  │          │  │          │  │          │  │
│  │ P0(L)    │  │ P0(F)    │  │ P1(F)    │  │ P2(F)    │  │
│  │ P1(L)    │  │ P2(L)    │  │ P0(F)    │  │ P1(F)    │  │
│  │ P3(F)    │  │ P3(L)    │  │ P2(F)    │  │ P3(F)    │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────┐      │
│  │              KRaft Controller Quorum              │      │
│  │  (metadata management, leader election)          │      │
│  └──────────────────────────────────────────────────┘      │
└────────────────────────────────────────────────────────────┘

L = Leader    F = Follower (replica)
```

### Broker Responsibilities

| Responsibility | Description |
|----------------|-------------|
| **Receive messages** | Accept writes from producers |
| **Store messages** | Persist to disk in partition log files |
| **Serve consumers** | Deliver messages to consumers on poll |
| **Replicate data** | Followers pull data from leaders |
| **Leader election** | If a leader fails, a follower is promoted |

### ZooKeeper vs KRaft

| Component | Role | Status |
|-----------|------|--------|
| **ZooKeeper** | Original metadata & coordination service | Deprecated (removed in Kafka 4.0) |
| **KRaft** (Kafka Raft) | Built-in consensus protocol for metadata | Recommended for all new clusters |

```
ZooKeeper mode:
  Kafka Brokers ←→ ZooKeeper Ensemble (separate cluster)
  Problem: Two systems to manage, ZK becomes bottleneck at scale

KRaft mode:
  Kafka Brokers include Controller nodes (no external dependency)
  Benefit: Simpler operations, faster metadata updates, scales to millions of partitions
```

### Replication — How Data Survives Failures

```
Topic: payments (replication-factor=3)

Partition 0:
  Broker 0 [LEADER]   →  [ msg0 | msg1 | msg2 | msg3 ]
  Broker 1 [FOLLOWER] →  [ msg0 | msg1 | msg2 | msg3 ]  (in-sync)
  Broker 2 [FOLLOWER] →  [ msg0 | msg1 | msg2 ]          (catching up)
                                                   ↑
                                              ISR (In-Sync Replicas)
                                              = {Broker 0, Broker 1}

If Broker 0 fails → Broker 1 becomes leader (no data loss)
```

### Real-Time Example: What Happens When a Broker Dies

```
Cluster: 3 brokers, topic "payments" with 6 partitions, replication=3

Normal state:
  Broker 0: P0(L) P1(F) P2(F) P3(L) P4(F) P5(F)
  Broker 1: P0(F) P1(L) P2(F) P3(F) P4(L) P5(F)
  Broker 2: P0(F) P1(F) P2(L) P3(F) P4(F) P5(L)

Broker 1 crashes at 3:00 AM!

Step 1: Controller detects failure (session.timeout = 10s)
Step 2: Leader election for P1 and P4 (Broker 1 was their leader)
  P1: Broker 0 or Broker 2 elected (whichever is most caught up)
  P4: Same process

Step 3: Updated state (all within seconds):
  Broker 0: P0(L) P1(L←promoted!) P2(F) P3(L) P4(F) P5(F)
  Broker 2: P0(F) P1(F)           P2(L) P3(F) P4(L←promoted!) P5(L)

Step 4: Producers and consumers automatically discover new leaders
  → Service continues with ZERO data loss and seconds of disruption
  
Step 5: When Broker 1 comes back online, it rejoins as follower
  → Catches up by replicating from current leaders
```

### Key Cluster Configs

| Config | Description | Recommended |
|--------|-------------|-------------|
| `num.partitions` | Default partitions per new topic | 6–12 |
| `default.replication.factor` | Default replication | 3 |
| `min.insync.replicas` | Min replicas that must ack a write | 2 |
| `log.retention.hours` | How long to keep messages | 168 (7 days) |
| `log.retention.bytes` | Max size per partition before deletion | -1 (unlimited) |
| `log.segment.bytes` | Size of each log segment file | 1 GB |
| `unclean.leader.election.enable` | Allow non-ISR replica as leader | false (data safety) |

### Real-Time Example: Production Cluster Sizing

```
Scenario: Fintech company processing card transactions

Requirements:
  - 50,000 transactions/sec
  - 99.99% availability
  - Zero data loss
  - 30-day retention
  - Average message size: 2 KB

Calculations:
  Throughput: 50K msg/sec × 2 KB = 100 MB/sec
  Daily storage: 100 MB/sec × 86400 sec = 8.6 TB/day
  30-day storage: 8.6 TB × 30 = 258 TB
  With replication=3: 258 TB × 3 = 774 TB
  
Cluster design:
  - 12 brokers (each with 6 × 12 TB disks)
  - 3 rack-aware zones (4 brokers per zone)
  - 64 partitions for the main topic
  - replication-factor=3, min.insync.replicas=2
  - acks=all, enable.idempotence=true
```

---

## 8. Kafka Use Cases — Real-Time Examples

### Use Case 1: Log Aggregation (Elasticsearch + Kibana)

```
Real-Time Example: Detecting a DDoS attack at Cloudflare

  ┌──────────────────────────────────────────────────┐
  │ 200+ Edge Servers worldwide                       │
  │                                                   │
  │  edge-server-1 ──┐                               │
  │  edge-server-2 ──┤                               │
  │  ...             ├──► Kafka topic: "access-logs" │
  │  edge-server-200──┘   (200 partitions)           │
  └──────────────┬───────────────────────────────────┘
                 │
      ┌──────────┼──────────────────┐
      ▼          ▼                  ▼
  ┌────────┐ ┌──────────┐   ┌──────────────┐
  │ Elastic│ │ Real-time │   │ Alert        │
  │ Search │ │ Dashboard │   │ System       │
  │ (store)│ │ (Grafana) │   │ (PagerDuty)  │
  └────────┘ └──────────┘   └──────────────┘

  At 2 AM: Anomaly detector sees 10x spike from IPs in a /24 range
  → Triggers automatic WAF rule
  → Team gets PagerDuty alert
  → All within 30 seconds of attack starting
```

### Use Case 2: Event-Driven Microservices (E-Commerce)

```
Real-Time Example: Placing an order on Amazon

Step 1: User clicks "Place Order"
  → Order Service produces: { order_id: "ORD-42", status: "CREATED", items: [...] }
  → Topic: "order-events"

Step 2: Payment Service (consumer group: "payment")
  → Consumes ORDER_CREATED
  → Charges credit card
  → Produces: { order_id: "ORD-42", status: "PAYMENT_SUCCESS" }
  → Topic: "payment-events"

Step 3: Inventory Service (consumer group: "inventory")
  → Consumes ORDER_CREATED
  → Reserves stock
  → If out of stock → "ITEM_UNAVAILABLE" event

Step 4: Notification Service (consumer group: "notifications")
  → Consumes PAYMENT_SUCCESS
  → Sends confirmation email + push notification

Step 5: Shipping Service (consumer group: "shipping")
  → Consumes PAYMENT_SUCCESS
  → Creates shipping label
  → Produces: { order_id: "ORD-42", tracking: "1Z999AA10" }

Step 6: Analytics Service (consumer group: "analytics")
  → Consumes ALL events
  → Updates real-time dashboard: orders/minute, revenue, conversion rate

Key insight: Each service is independent. If Notification Service is down,
orders still process. When it comes back, it catches up automatically!
```

### Use Case 3: Change Data Capture (CDC)

```
Real-Time Example: Keeping Elasticsearch in sync with MySQL

Problem: Users search for products, but search index is stale
  MySQL: product price changed to $29.99
  Elasticsearch: still shows $24.99 (updated daily via batch job)

Solution: CDC with Debezium + Kafka
  ┌──────────┐     ┌───────────┐     ┌──────────┐     ┌────────────┐
  │  MySQL   │──►  │ Debezium  │──►  │  Kafka   │──►  │ Elastic    │
  │ (source) │     │ reads     │     │  Topic   │     │ Search     │
  │          │     │ binlog    │     │          │     │ (updated   │
  │          │     │           │     │          │     │  in <1sec) │
  └──────────┘     └───────────┘     └──────────┘     └────────────┘

  When admin updates product price in MySQL:
    1. MySQL writes to binlog (binary log)
    2. Debezium reads binlog, produces Kafka event:
       { "op": "u", "before": {"price": 24.99}, "after": {"price": 29.99} }
    3. Elasticsearch consumer updates search index
    4. User sees correct price within 1 second
  
  Bonus consumers on the same topic:
    → Data warehouse (analytics)
    → Cache invalidation service
    → Price alert service (notifies users who set price watches)
```

### Use Case 4: Real-Time Fraud Detection

```
Real-Time Example: Credit card fraud detection at a bank

  ┌──────────────┐     ┌─────────────────────────┐
  │ POS Terminal │     │  Kafka Topic:            │
  │ / Mobile App │────►│  "card-transactions"     │
  │ / Website    │     │  (64 partitions)         │
  └──────────────┘     │  Key: card_number        │
                       └────────┬────────────────┘
                                │
               ┌────────────────┼────────────────┐
               ▼                ▼                ▼
       ┌──────────────┐ ┌──────────────┐ ┌────────────┐
       │ Rule Engine  │ │ ML Model     │ │ Velocity   │
       │              │ │ (real-time   │ │ Checker    │
       │ - Amount >   │ │  scoring)    │ │            │
       │   $5000?     │ │              │ │ - 5+ txns  │
       │ - Foreign    │ │ - Behavioral │ │   in 1 min?│
       │   country?   │ │   anomaly    │ │ - 3+ diff  │
       │ - New device?│ │   detection  │ │   countries│
       └──────┬───────┘ └──────┬───────┘ └─────┬──────┘
              │                │                │
              └────────────────┼────────────────┘
                               ▼
                    ┌────────────────────┐
                    │ Decision Service   │
                    │                    │
                    │ APPROVE / DECLINE  │
                    │ / REVIEW           │
                    │                    │
                    │ Latency: < 100ms   │
                    └────────────────────┘

  All of this happens between the moment you tap your card and
  the terminal shows "APPROVED" — under 100 milliseconds.
  
  Key = card_number ensures all transactions for the same card
  go to the same partition → velocity check sees them in order.
```

### Use Case 5: IoT Data Pipeline

```
Real-Time Example: Tesla Fleet Telemetry

  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
  │ Tesla Car 1 │ │ Tesla Car 2 │ │ Tesla Car N │
  │ (1000+      │ │             │ │             │
  │  sensors)   │ │             │ │             │
  └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
         │               │               │
    Every 100ms: battery, GPS, speed, brake, acceleration...
         │               │               │
         └───────────────┼───────────────┘
                         ▼
              ┌─────────────────────┐
              │ Kafka (100s of      │
              │ partitions per      │
              │ topic)              │
              │                     │
              │ vehicle.telemetry   │
              │ vehicle.location    │
              │ vehicle.diagnostics │
              │ vehicle.battery     │
              └─────────┬──────────┘
                        │
      ┌─────────────────┼─────────────────┐
      ▼                 ▼                  ▼
  ┌─────────┐   ┌──────────────┐   ┌──────────────┐
  │ Real-   │   │ Autopilot    │   │ Long-term    │
  │ time Map│   │ ML Training  │   │ Storage      │
  │ (fleet  │   │ (improve     │   │ (warranty    │
  │ tracking│   │  self-drive) │   │  analysis)   │
  └─────────┘   └──────────────┘   └──────────────┘

  Scale: 2 million+ cars × 1000 sensors × 10 readings/sec
       = 20 BILLION events per second
```

### Use Case 6: Real-Time Analytics

```
Real-Time Example: Spotify Wrapped (Year-in-Review)

  Every time you play a song:
    Topic: "streaming.play"
    Key: "user-42"
    Value: { "track": "Bohemian Rhapsody", "artist": "Queen",
             "genre": "Rock", "duration_ms": 354000,
             "skipped_at": null, "device": "mobile" }
  
  Multiple consumer groups process the same data:
  
  1. "recommendation-engine"
     → Updates your personalized playlists (Discover Weekly)
     
  2. "artist-analytics"
     → Real-time stream counts for artists
     → "Your song just hit 1 million plays!" notification
     
  3. "wrapped-aggregator"
     → Counts per genre, artist, total minutes
     → Powers the annual "Spotify Wrapped" feature
     → Uses Kafka Streams with state stores
     
  4. "royalty-calculator"
     → Calculates per-play royalties for record labels
     → Must be exactly-once (money involved!)
```

---

## 9. Kafka Streams & Kafka Connect

### Kafka Streams — Stream Processing Library

```
Kafka Streams is a Java library for building real-time applications
that transform, aggregate, and enrich data flowing through Kafka.

Traditional approach:
  Kafka → Consumer → Process → Producer → Kafka
  (lots of boilerplate)

Kafka Streams approach:
  StreamsBuilder builder = new StreamsBuilder();
  KStream<String, String> orders = builder.stream("orders");
  
  orders
    .filter((key, value) -> value.contains("PREMIUM"))
    .mapValues(value -> enrichWithCustomerData(value))
    .groupByKey()
    .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
    .count()
    .toStream()
    .to("premium-orders-per-5min");
```

### Real-Time Example: Fraud Score Aggregation

```
Input topic: "card-transactions"
Output topic: "fraud-scores"

KStream<String, Transaction> transactions = builder.stream("card-transactions");

// Count transactions per card in 1-minute windows
KTable<Windowed<String>, Long> txnCounts = transactions
    .groupByKey()
    .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1)))
    .count();

// Flag cards with > 5 transactions in 1 minute
txnCounts.toStream()
    .filter((windowedKey, count) -> count > 5)
    .mapValues((key, count) -> "SUSPICIOUS: " + count + " txns in 1 min")
    .to("fraud-alerts");
```

### Kafka Connect — Integration Framework

```
Kafka Connect moves data IN and OUT of Kafka without writing code.

Sources (data INTO Kafka):
  ┌──────────┐   ┌─────────────────┐   ┌───────┐
  │ MySQL    │──►│ JDBC Source      │──►│ Kafka │
  │ Postgres │──►│ Debezium CDC     │──►│       │
  │ MongoDB  │──►│ MongoDB Source   │──►│       │
  │ S3 Files │──►│ S3 Source        │──►│       │
  └──────────┘   └─────────────────┘   └───────┘

Sinks (data OUT of Kafka):
  ┌───────┐   ┌─────────────────┐   ┌──────────────┐
  │ Kafka │──►│ Elasticsearch   │──►│ Elasticsearch │
  │       │──►│ S3 Sink         │──►│ S3 / HDFS     │
  │       │──►│ JDBC Sink       │──►│ Database      │
  │       │──►│ Snowflake Sink  │──►│ Data Warehouse│
  └───────┘   └─────────────────┘   └──────────────┘

Configuration-only (no code):
{
  "connector.class": "io.debezium.connector.mysql.MySqlConnector",
  "database.hostname": "mysql-server",
  "database.port": "3306",
  "database.user": "debezium",
  "database.password": "***",
  "database.server.id": "1",
  "topic.prefix": "cdc",
  "table.include.list": "inventory.products,inventory.orders"
}
```

---

## 10. Kafka in Production — Best Practices

### Topic Design Patterns

```
Pattern 1: Event per entity type
  user.created, user.updated, user.deleted
  ✅ Clear, one topic per event type
  ❌ Many topics to manage

Pattern 2: All events per entity
  user.events (contains created, updated, deleted)
  ✅ Fewer topics, all user events in order
  ❌ Consumers must filter

Pattern 3: Domain events
  ecommerce.order.lifecycle (created → paid → shipped → delivered)
  ✅ Complete entity lifecycle in one topic
  ❌ Mixed event schemas

Recommendation: Pattern 2 or 3 for most use cases.
  Use Schema Registry with Avro for schema evolution.
```

### Monitoring — What to Watch

```
Critical Metrics:
┌──────────────────────────────────────────────────┐
│ Metric                    │ Alert When           │
├───────────────────────────┼──────────────────────┤
│ Consumer Lag              │ > 10,000 messages    │
│ Under-Replicated Partitions│ > 0                 │
│ ISR Shrink Rate           │ > 0/min              │
│ Request Latency (p99)     │ > 100ms              │
│ Disk Usage                │ > 80%                │
│ CPU Usage                 │ > 70%                │
│ Network I/O               │ Approaching NIC limit│
│ GC Pause Time             │ > 200ms              │
└───────────────────────────┴──────────────────────┘

Consumer Lag = Latest Offset - Consumer Committed Offset

  Partition 0: Latest=1000, Committed=950 → Lag = 50 ✅
  Partition 0: Latest=1000, Committed=200 → Lag = 800 ❌
  
  Lag growing = consumer too slow or stuck
  Lag shrinking = consumer catching up
  Lag stable near 0 = healthy
```

### Common Production Issues & Solutions

```
Issue 1: Consumer Lag keeps growing
  Cause: Consumer processing is too slow
  Fix: 
    - Add more partitions + consumers
    - Optimize processing logic (batch DB writes)
    - Use async processing (process in threads, commit periodically)

Issue 2: Rebalancing storm (frequent rebalances)
  Cause: Consumers taking too long to process a poll batch
  Fix:
    - Increase max.poll.interval.ms (default 5 min)
    - Decrease max.poll.records (process fewer per poll)
    - Use cooperative rebalancing (incremental, not stop-the-world)

Issue 3: Message ordering lost
  Cause: Messages for same entity going to different partitions
  Fix:
    - Always set a key (entity ID) on your messages
    - max.in.flight.requests.per.connection=1 for strict ordering with retries

Issue 4: Duplicate messages after restart
  Cause: At-least-once delivery (normal behavior)
  Fix:
    - Make consumers idempotent (use message ID as dedup key)
    - Or use exactly-once semantics (transactions + idempotent producer)
    
Issue 5: Disk full on brokers
  Cause: Retention set too high for available disk
  Fix:
    - Reduce log.retention.hours
    - Add more disks/brokers
    - Enable log.cleanup.policy=compact for changelog topics
```

---

## Quick Reference

### Kafka CLI Commands

```bash
# Create a topic
kafka-topics.sh --create --topic my-topic \
  --bootstrap-server localhost:9092 \
  --partitions 3 --replication-factor 2

# List topics
kafka-topics.sh --list --bootstrap-server localhost:9092

# Describe a topic
kafka-topics.sh --describe --topic my-topic \
  --bootstrap-server localhost:9092

# Produce messages
kafka-console-producer.sh --topic my-topic \
  --bootstrap-server localhost:9092

# Produce with key
kafka-console-producer.sh --topic my-topic \
  --bootstrap-server localhost:9092 \
  --property "parse.key=true" --property "key.separator=:"

# Consume messages (from beginning)
kafka-console-consumer.sh --topic my-topic \
  --bootstrap-server localhost:9092 \
  --from-beginning --group my-group

# View consumer group offsets
kafka-consumer-groups.sh --describe --group my-group \
  --bootstrap-server localhost:9092

# Reset offsets to earliest
kafka-consumer-groups.sh --group my-group --topic my-topic \
  --reset-offsets --to-earliest --execute \
  --bootstrap-server localhost:9092

# Delete a topic
kafka-topics.sh --delete --topic my-topic \
  --bootstrap-server localhost:9092

# Check broker configs
kafka-configs.sh --describe --entity-type brokers --entity-name 0 \
  --bootstrap-server localhost:9092
```

### Kafka with Docker (Quick Start)

```yaml
# docker-compose.yml (KRaft mode — no ZooKeeper)
version: '3'
services:
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@localhost:9093
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      CLUSTER_ID: 'MkU3OEVBNTcwNTJENDM2Qk'
```

### Spring Boot + Kafka (Quick Start)

```yaml
# application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
    consumer:
      group-id: my-service
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
```

```java
// Producer
@Service
public class OrderProducer {
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    public void sendOrder(OrderEvent event) {
        kafkaTemplate.send("order-events", event.getOrderId(), event);
    }
}

// Consumer
@Service
public class OrderConsumer {
    @KafkaListener(topics = "order-events", groupId = "order-service")
    public void handleOrder(OrderEvent event) {
        log.info("Processing order: {}", event.getOrderId());
        // business logic here
    }
}
```

---

## Interview Deep-Dive Questions

### Fundamentals

| Question | Answer |
|----------|--------|
| What is Kafka? | Distributed event streaming platform for high-throughput, fault-tolerant real-time data pipelines |
| How is ordering guaranteed? | Only within a single partition. Use message keys to ensure related events go to the same partition |
| What happens if a broker dies? | Controller detects failure, promotes an ISR follower to leader. Producers/consumers auto-discover new leader |
| How do consumer groups work? | Each partition is assigned to exactly one consumer per group. Different groups consume independently |
| What is ISR? | In-Sync Replicas — followers that have fully caught up with the leader's log |
| What is consumer lag? | Difference between the latest offset in a partition and the consumer's committed offset |
| What is log compaction? | Retains only the latest value per key. Older values for the same key are deleted during compaction |

### Producer Questions

| Question | Answer |
|----------|--------|
| `acks=all` vs `acks=1`? | `all` = wait for all ISR replicas (durable), `1` = wait for leader only (faster) |
| How to achieve exactly-once? | `enable.idempotence=true` + transactional API (`initTransactions`, `beginTransaction`, `commitTransaction`) |
| What if broker is slow to ack? | Producer's `buffer.memory` fills up → `send()` blocks for `max.block.ms`, then throws `TimeoutException` |
| How does partitioning work? | If key is set: `hash(key) % numPartitions`. If key is null: round-robin or sticky partitioner |

### Consumer Questions

| Question | Answer |
|----------|--------|
| What triggers a rebalance? | Consumer joins/leaves group, new partitions added, consumer fails heartbeat |
| auto.commit vs manual commit? | Auto: simpler but can lose data. Manual: safer, commit after processing |
| What is `max.poll.interval.ms`? | Max time between polls. If exceeded, consumer is evicted and rebalance triggers |
| How to handle poison pills? | Dead letter topic (DLT): send unparseable messages to a separate topic for investigation |

### Architecture Questions

| Question | Answer |
|----------|--------|
| Kafka vs RabbitMQ? | Kafka: log-based, replay, high throughput, ordering per partition. RabbitMQ: traditional queue, routing, lower latency for small messages |
| How to handle schema changes? | Schema Registry (Avro/Protobuf) with compatibility modes: BACKWARD, FORWARD, FULL |
| How to scale consumers? | Add partitions (can't decrease), then add consumers. Consumers ≤ partitions per group |
| How does Kafka achieve high throughput? | Sequential I/O, zero-copy, batching, compression, page cache, partitioning |
| What is MirrorMaker? | Tool for replicating data between Kafka clusters (cross-datacenter replication) |
| What is the difference between Kafka Streams and Flink? | Streams: library (embedded in app, Kafka-only). Flink: framework (separate cluster, multi-source) |

### Scenario-Based Questions

| Scenario | Solution |
|----------|----------|
| "We need to process 1M events/sec" | Multiple partitions (128+), multiple consumers, lz4 compression, batching, acks=1 if loss-tolerant |
| "Messages must never be lost" | `acks=all`, `min.insync.replicas=2`, `replication.factor=3`, manual offset commit after processing |
| "Consumer is too slow" | Increase partitions + consumers, reduce `max.poll.records`, async processing with internal queue |
| "We see duplicate messages" | Enable idempotent producer, make consumers idempotent (dedup by message ID in DB) |
| "Need to replay last 30 days" | Set `auto.offset.reset=earliest`, reset offsets with `kafka-consumer-groups.sh --reset-offsets --to-datetime` |
| "Data must stay in EU" | Separate Kafka clusters per region, use MirrorMaker 2 for selective replication |

---

## Resources

- [Full Interview Prep Kit for Data Engineers](https://lnkd.in/gNH-trFm)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Confluent Developer](https://developer.confluent.io/)
- [Kafka: The Definitive Guide (O'Reilly)](https://www.confluent.io/resources/kafka-the-definitive-guide-v2/)
- [Designing Data-Intensive Applications (Martin Kleppmann)](https://dataintensive.net/)

# System Design — 30 Key Concepts Roadmap

> Want to get comfortable with System Design in about a month? Focus on the right concepts — not random tutorials.
> The real foundation lies in understanding the building blocks first. Once you know the core concepts, most large-scale architectures start to make sense.

**Reference:** [Aishwarya Pani — 30-Concept System Design Roadmap](https://www.linkedin.com/feed/update/urn:li:activity:7435883597837873152) | Credit: Ashish Pratap Singh

---

## Roadmap Overview

```
WEEK 1: Networking & API Foundations (Concepts 1-9)
─────────────────────────────────────────────────────────────
  1. Client-Server        4. Proxy/Reverse Proxy    7. APIs
  2. IP Address           5. Latency                8. REST API
  3. DNS                  6. HTTP/HTTPS             9. GraphQL

WEEK 2: Database & Scaling (Concepts 10-18)
─────────────────────────────────────────────────────────────
  10. Databases          13. Horizontal Scaling     16. Replication
  11. SQL vs NoSQL       14. Load Balancers         17. Sharding
  12. Vertical Scaling   15. Database Indexing      18. Vertical Part.

WEEK 3: Performance & Consistency (Concepts 19-24)
─────────────────────────────────────────────────────────────
  19. Caching            21. CAP Theorem            23. CDN
  20. Denormalization    22. Blob Storage           24. WebSockets

WEEK 4: Architecture & Reliability (Concepts 25-30)
─────────────────────────────────────────────────────────────
  25. Webhooks           27. Message Queues         29. API Gateways
  26. Microservices      28. Rate Limiting          30. Idempotency
```

---

## 1. Client-Server Architecture

The foundation of the web. A **client** (browser, mobile app) sends requests to a **server** which processes them and returns responses. Almost every application you use daily — from Gmail to Netflix to Uber — follows this model. The client handles the user interface and user interaction, while the server handles data processing, storage, and business logic.

**Why it matters:** This is the fundamental mental model for understanding how all distributed systems communicate. Every system design answer begins with "a client sends a request to a server."

```
┌─────────────┐         request          ┌─────────────────┐
│   CLIENT    │ ────────────────────────► │     SERVER      │
│             │                          │                 │
│ • Browser   │ ◄──────────────────────── │ • Processes     │
│ • Mobile    │         response          │   business logic│
│ • Desktop   │                          │ • Accesses DB   │
│ • IoT       │                          │ • Returns data  │
└─────────────┘                          └─────────────────┘

THIN CLIENT (most logic on server):
  Browser ──► Server renders full HTML ──► Browser displays
  Example: Traditional server-side apps (PHP, Rails, JSP)

THICK CLIENT (logic on client):
  React App ──► Server returns JSON ──► Client renders UI
  Example: Single Page Applications (React, Angular, Vue)

THREE-TIER ARCHITECTURE:
┌──────────┐    ┌──────────────┐    ┌──────────────┐
│  Client  │───►│ Application  │───►│  Database    │
│ (browser)│    │  Server      │    │  Server      │
│          │◄───│  (API logic) │◄───│  (data store)│
└──────────┘    └──────────────┘    └──────────────┘
 Presentation       Business            Data
    Tier              Tier               Tier
```

### Real-Time Examples

```
EXAMPLE 1: UBER — Ride Request Flow
──────────────────────────────────────────────────────
  Rider's Phone (Client)                     Uber Servers
  ┌──────────────────┐                   ┌──────────────────┐
  │ 1. Open app      │ ── GPS coords ──►│ Location Service │
  │ 2. Enter dest.   │ ── "Book ride" ──►│ Matching Service │
  │ 3. See ETA       │ ◄── driver info ──│ Pricing Service  │
  │ 4. Track driver  │ ◄── live updates ─│ WebSocket Gateway│
  └──────────────────┘                   └──────────────────┘

  The phone is a "thick client" — it renders maps, animations,
  and handles GPS locally. But all pricing, matching, and
  payments happen on Uber's servers.

EXAMPLE 2: GMAIL — Email Check
──────────────────────────────────────────────────────
  Browser (Client)                        Google Servers
  ┌──────────────────┐                   ┌──────────────────┐
  │ You open Gmail   │ ── GET /inbox ───►│ Auth Service     │
  │                  │                    │ → verify token   │
  │                  │                    │ Mail Service     │
  │ See 3 new emails│ ◄── JSON data ────│ → query Bigtable │
  │ (rendered in JS) │                    │ → return emails  │
  └──────────────────┘                   └──────────────────┘

  Gmail is a thick SPA client (React-like). Server returns
  raw JSON, and Gmail's JavaScript renders the entire UI.

EXAMPLE 3: ATM MACHINE — Banking
──────────────────────────────────────────────────────
  ATM (Client)                           Bank Servers
  ┌──────────────────┐                   ┌──────────────────┐
  │ Insert card      │ ── card data ────►│ Auth Service     │
  │ Enter PIN        │ ── PIN check ────►│ → verify PIN     │
  │ "Withdraw $200"  │ ── debit req ────►│ Account Service  │
  │                  │                    │ → check balance  │
  │ Dispense cash    │ ◄── approved ─────│ → debit $200     │
  └──────────────────┘                   └──────────────────┘

  ATM is a "thin client" — it has no knowledge of your balance.
  Every operation is validated server-side for security.
```

---

## 2. IP Address

Every device on the internet has a unique **IP address** — its "home address" on the network. Just like every house needs a postal address for mail delivery, every device needs an IP address so data packets know where to go. Without IP addresses, your request to open YouTube would have no idea which server to reach or how to get the response back to your device.

**Why it matters:** Understanding IP addressing is essential for designing geo-distributed systems, configuring firewalls, setting up VPCs (Virtual Private Clouds), and debugging network issues.

```
IPv4: 192.168.1.100         (32 bits, ~4.3 billion addresses)
IPv6: 2001:0db8:85a3::8a2e  (128 bits, virtually unlimited)

HOW A REQUEST FINDS ITS DESTINATION:

Your laptop                                      Google Server
192.168.1.100                                    142.250.80.46
     │                                                │
     ├── Home Router (192.168.1.1)                    │
     │       │                                        │
     │       ├── ISP Router                           │
     │       │       │                                │
     │       │       ├── Internet Backbone ────────── │
     │       │       │   (routers hop-by-hop using    │
     │       │       │    routing tables to find       │
     │       │       │    shortest path)               │
     │       │       │                                │
     └───────┴───────┴────────────────────────────────┘

PRIVATE vs PUBLIC IP:
┌──────────────────────────────────────────────────────┐
│ Your Home Network (Private IPs — not internet-facing)│
│                                                       │
│  Laptop: 192.168.1.100                               │
│  Phone:  192.168.1.101     ┌────────────┐            │
│  TV:     192.168.1.102 ───►│   Router   │──► Internet│
│                             │ NAT: maps  │            │
│  All share ONE public IP:   │ private→   │            │
│  203.0.113.42              │ public IP  │            │
│                             └────────────┘            │
└──────────────────────────────────────────────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: AWS VPC — Private Network for Your App
──────────────────────────────────────────────────────
  ┌─── VPC: 10.0.0.0/16 (65,536 private IPs) ───────────────┐
  │                                                           │
  │  Public Subnet (10.0.1.0/24)    Private Subnet (10.0.2.0)│
  │  ┌───────────────────┐          ┌───────────────────┐    │
  │  │ Web Server        │          │ Database          │    │
  │  │ 10.0.1.15         │─────────►│ 10.0.2.50        │    │
  │  │ + Public IP:      │          │ NO public IP ✅   │    │
  │  │ 54.123.45.67      │          │ Unreachable from  │    │
  │  │ (internet-facing) │          │ internet directly │    │
  │  └───────────────────┘          └───────────────────┘    │
  │                                                           │
  │  Internet Gateway ──► only public subnet is reachable    │
  └───────────────────────────────────────────────────────────┘

  Netflix, Uber, Airbnb — all run inside AWS VPCs.
  Databases sit in private subnets (no public IP) for security.

EXAMPLE 2: NAT Gateway — How Your Office Shares One IP
──────────────────────────────────────────────────────
  An office with 500 employees:
  ┌────────────────────────────────────────────────┐
  │  Alice: 10.0.1.10 ──┐                          │
  │  Bob:   10.0.1.11 ──┤                          │
  │  Carol: 10.0.1.12 ──┼──► NAT Gateway ──► 203.0.113.5 ──► Internet
  │  ...                 │    (translates          │
  │  500 employees ──────┘     private→public)     │
  │                                                 │
  │  Google sees ALL 500 employees as 203.0.113.5  │
  │  NAT tracks which internal IP each response     │
  │  belongs to using port numbers                  │
  └────────────────────────────────────────────────┘

EXAMPLE 3: CDN — Anycast IP (One IP, Many Servers)
──────────────────────────────────────────────────────
  Cloudflare uses Anycast: same IP → nearest server

  DNS: example.com → 104.16.123.96

  User in Tokyo  ── 104.16.123.96 ──► Tokyo edge server    (5ms)
  User in London ── 104.16.123.96 ──► London edge server   (3ms)
  User in NYC    ── 104.16.123.96 ──► NYC edge server      (2ms)

  Same IP, different physical servers!
  BGP routing directs to geographically nearest one.
```

---

## 3. DNS (Domain Name System)

DNS translates human-readable domain names (`google.com`) into IP addresses (`142.250.80.46`). It's the internet's phone book.

```
You type: www.google.com
                │
                ▼
┌──────────────────────┐
│ 1. Browser Cache     │ ← checked first (instant if cached)
│    Found? → done!    │
└──────────┬───────────┘
           │ miss
           ▼
┌──────────────────────┐
│ 2. OS Cache          │ ← /etc/hosts or OS DNS cache
│    Found? → done!    │
└──────────┬───────────┘
           │ miss
           ▼
┌──────────────────────┐
│ 3. Recursive Resolver│ ← Your ISP's DNS server
│    (e.g., 8.8.8.8)  │    Checks its cache too
└──────────┬───────────┘
           │ miss — starts recursive lookup
           ▼
┌──────────────────────┐
│ 4. Root DNS Server   │ ← "I don't know google.com,
│    (13 worldwide)    │    but .com is handled by
│                      │    these TLD servers →"
└──────────┬───────────┘
           ▼
┌──────────────────────┐
│ 5. TLD Server (.com) │ ← "google.com is managed by
│                      │    ns1.google.com →"
└──────────┬───────────┘
           ▼
┌──────────────────────┐
│ 6. Authoritative DNS │ ← "google.com = 142.250.80.46"
│    (ns1.google.com)  │    Returns the actual IP!
└──────────┬───────────┘
           │
           ▼
Response: 142.250.80.46 (cached with TTL, e.g., 300 seconds)

DNS RECORD TYPES:
┌────────┬──────────────────────────────────────────┐
│ Type   │ Purpose                                  │
├────────┼──────────────────────────────────────────┤
│ A      │ Maps domain → IPv4 address               │
│ AAAA   │ Maps domain → IPv6 address               │
│ CNAME  │ Alias: blog.example.com → example.com    │
│ MX     │ Mail server for the domain               │
│ NS     │ Authoritative nameserver for domain       │
│ TXT    │ Arbitrary text (SPF, DKIM, verification) │
│ SRV    │ Service discovery (host + port)           │
└────────┴──────────────────────────────────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: NETFLIX — GeoDNS for Global Routing
──────────────────────────────────────────────────────
  netflix.com resolves to DIFFERENT IPs based on location:

  User in India  ──► DNS ──► 103.87.x.x  (Mumbai data center)
  User in USA    ──► DNS ──► 54.192.x.x  (Virginia data center)
  User in Europe ──► DNS ──► 52.84.x.x   (Frankfurt data center)

  Netflix uses AWS Route 53 with "Geolocation Routing Policy"
  to direct users to the nearest streaming servers, cutting
  latency by 100-200ms compared to serving from one location.

EXAMPLE 2: DNS FAILOVER — High Availability
──────────────────────────────────────────────────────
  api.myapp.com has TWO A records:
    Primary:   54.100.1.1  (US-East)
    Secondary: 54.200.2.2  (US-West)

  Normal:   DNS returns 54.100.1.1 (healthy, TTL=60s)
  Outage:   Health check detects US-East is down!
            DNS auto-switches to 54.200.2.2
  Recovery: Within 60 seconds (TTL), all traffic moves

  AWS Route 53, Cloudflare — offer built-in DNS failover.

EXAMPLE 3: BLUE-GREEN DEPLOYMENT via DNS
──────────────────────────────────────────────────────
  app.example.com ──► 10.0.1.100  (Blue = v1, current production)
                      10.0.2.100  (Green = v2, new version staging)

  Deploy v2 to Green. Test it. When ready:
    Change DNS: app.example.com → 10.0.2.100
    Green becomes production. Blue becomes standby.

  Rollback? Just switch DNS back to 10.0.1.100
  GitHub, Etsy, and many companies use this approach.

EXAMPLE 4: HOW SLACK USES CNAME FOR CUSTOM DOMAINS
──────────────────────────────────────────────────────
  Company sets up: slack.mycompany.com
    DNS: slack.mycompany.com → CNAME → mycompany.slack.com
                             → CNAME → slack-edge.com
                             → A     → 52.x.x.x (actual server)

  CNAME chain lets Slack change their infrastructure IPs
  without requiring customers to update DNS records.
```

---

## 4. Proxy / Reverse Proxy

```
═══════════════════════════════════════════════════════════
  FORWARD PROXY — sits in front of CLIENTS
═══════════════════════════════════════════════════════════

  ┌──────┐                                    ┌──────────┐
  │User A│──┐                                 │          │
  └──────┘  │    ┌───────────────┐            │  Server  │
            ├───►│ Forward Proxy │───────────►│          │
  ┌──────┐  │    │               │            │ Sees only│
  │User B│──┘    │ • Hides client│            │ proxy IP │
  └──────┘       │   identity    │            │          │
                 │ • Content     │            └──────────┘
                 │   filtering   │
                 │ • Caching     │
                 └───────────────┘

  Use cases:
  • Corporate network filtering (block social media)
  • Anonymity (VPN, Tor)
  • Geo-restriction bypass

═══════════════════════════════════════════════════════════
  REVERSE PROXY — sits in front of SERVERS
═══════════════════════════════════════════════════════════

  ┌──────┐       ┌───────────────┐            ┌──────────┐
  │      │       │ Reverse Proxy │       ┌───►│ Server A │
  │Client│──────►│               │───────┤    └──────────┘
  │      │       │ • SSL termin. │       │    ┌──────────┐
  │Sees  │       │ • Load balance│───────┼───►│ Server B │
  │only  │       │ • Caching     │       │    └──────────┘
  │proxy │       │ • Compression │       │    ┌──────────┐
  │IP    │       │ • Rate limit  │───────┘───►│ Server C │
  └──────┘       └───────────────┘            └──────────┘

  Use cases:
  • Load balancing (Nginx, HAProxy)
  • SSL/TLS termination (decrypt HTTPS at proxy)
  • Web Application Firewall (WAF)
  • Caching static content
  • API Gateway (Kong, AWS API Gateway)

  Examples: Nginx, Cloudflare, AWS ALB, Envoy, Traefik
```

### Real-Time Examples

```
EXAMPLE 1: CLOUDFLARE — Reverse Proxy for 25M+ Websites
──────────────────────────────────────────────────────
  Without Cloudflare:
    User ──────── 200ms ──────► Your Server (Virginia)
                                DDoS attack = server dies ❌

  With Cloudflare (reverse proxy):
    User ──5ms──► Cloudflare Edge ──────► Your Server
                  ┌───────────────────┐
                  │ • Blocks DDoS     │
                  │ • Caches static   │
                  │ • Terminates SSL  │
                  │ • WAF protection  │
                  │ • Bot detection   │
                  └───────────────────┘

  Shopify, Discord, and Canva all sit behind Cloudflare.
  Cloudflare absorbs ~165 billion cyber threats per day.

EXAMPLE 2: CORPORATE VPN — Forward Proxy
──────────────────────────────────────────────────────
  At a company office:
    Employee ──► Corporate Forward Proxy ──► Internet
                 │
                 ├── Block social media (facebook.com → denied)
                 ├── Block malware sites (blocklist)
                 ├── Log all URLs visited (compliance)
                 ├── Cache frequently visited pages
                 └── Scan downloads for viruses

  The proxy "masks" the employee's identity — external
  servers only see the proxy's IP, not the employee's.

EXAMPLE 3: NGINX — Reverse Proxy + Load Balancer
──────────────────────────────────────────────────────
  Real Nginx config for a production setup:

  upstream backend {
      server app-server-1:8080 weight=3;  ← gets 3x traffic
      server app-server-2:8080 weight=1;
      server app-server-3:8080 backup;    ← only if others fail
  }

  server {
      listen 443 ssl;
      location /api/ {
          proxy_pass http://backend;      ← reverse proxy
          proxy_set_header X-Real-IP $remote_addr;
      }
      location /static/ {
          root /var/www/;                 ← serve directly (no proxy)
          expires 30d;                    ← cache for 30 days
      }
  }

  Instagram, Pinterest, Dropbox — all use Nginx as reverse proxy.
```

---

## 5. Latency

The time it takes for a request to travel from source to destination and back. It's the delay the user *feels*.

```
REQUEST LIFECYCLE — WHERE LATENCY HIDES:

Client                                                    Server
  │                                                         │
  ├── DNS Lookup ──────── 1-50ms (cached: 0ms)             │
  ├── TCP Handshake ───── 10-150ms (3-way SYN/ACK)         │
  ├── TLS Handshake ───── 10-150ms (certificate exchange)   │
  ├── Request Transit ─── 1-150ms (speed of light!)         │
  │                       ┌─────────────────────────────────┤
  │                       │ Server Processing                │
  │                       │ ├── Parse request ──── 0.1ms    │
  │                       │ ├── Auth check ─────── 1-5ms    │
  │                       │ ├── DB query ──────── 1-100ms   │
  │                       │ ├── Business logic ── 1-50ms    │
  │                       │ └── Serialize resp ── 0.1-5ms   │
  │                       └─────────────────────────────────┤
  ├── Response Transit ── 1-150ms                           │
  ├── Client Rendering ── 10-500ms                          │
  │                                                         │

LATENCY vs BANDWIDTH:
  Latency   = how fast (delay per request)
  Bandwidth = how wide (data per second)

  ┌──────────────────────────────────────────────────────┐
  │  Think of a highway:                                  │
  │                                                       │
  │  Latency   = speed limit (how fast cars travel)      │
  │  Bandwidth = number of lanes (how many cars at once) │
  │                                                       │
  │  A 10-lane highway at 30mph has HIGH bandwidth       │
  │  but HIGH latency. You'd prefer 2 lanes at 200mph   │
  │  if you just need ONE car there fast.                │
  └──────────────────────────────────────────────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: AMAZON — Every 100ms of Latency Costs 1% Sales
──────────────────────────────────────────────────────
  Amazon discovered:
    100ms added latency = 1% drop in revenue
    At $500B annual revenue → 100ms costs $5B/year!

  How Amazon reduces latency:
    • CDN for product images → 5ms instead of 200ms
    • Redis cache for product data → 0.5ms instead of 50ms DB
    • Edge locations for API → serve from nearest region
    • Pre-computed recommendations → no real-time ML inference

EXAMPLE 2: GOOGLE SEARCH — Targeting < 200ms
──────────────────────────────────────────────────────
  When you search "weather today":

  Latency breakdown:
  ┌─────────────────────────────────────────────────────┐
  │ DNS lookup:          0ms  (cached)                  │
  │ TCP + TLS handshake: 10ms (HTTP/3 = 0-RTT!)        │
  │ Request to server:   5ms  (edge server nearby)      │
  │ Server processing:                                   │
  │   ├── Query parsing:     1ms                        │
  │   ├── Index lookup:      10ms (inverted index)      │
  │   ├── Ranking:           50ms (ML model scoring)    │
  │   ├── Snippet generation: 20ms                      │
  │   └── Ad auction:        30ms (real-time bidding)   │
  │ Response transfer:   10ms                            │
  │ Browser rendering:   50ms                            │
  │ ─────────────────────────────────────                │
  │ Total:               ~186ms ✅ (under 200ms goal)   │
  └─────────────────────────────────────────────────────┘

EXAMPLE 3: MULTIPLAYER GAMING — Latency = Life or Death
──────────────────────────────────────────────────────
  In Fortnite / Valorant / Call of Duty:

  < 20ms:   "Butter smooth" — pro players demand this
  20-50ms:  Playable for most gamers
  50-100ms: Noticeable lag, disadvantage in gunfights
  > 100ms:  "Teleporting" players, unplayable

  How game servers minimize latency:
  • Servers in 20+ regions worldwide
  • UDP instead of TCP (no retransmission delay)
  • Client-side prediction (assume movement, correct later)
  • Tick rate: 128Hz (update every 7.8ms) vs 64Hz (15.6ms)

LATENCY NUMBERS EVERY DEVELOPER SHOULD KNOW:
──────────────────────────────────────────────────────
  L1 cache reference:              0.5 ns
  L2 cache reference:              7   ns
  Main memory (RAM):               100 ns
  SSD random read:                  16  μs  (16,000 ns)
  HDD random read:                  2   ms  (2,000,000 ns)
  Round trip within same DC:        0.5 ms
  Round trip CA → Netherlands:      150 ms
  Round trip CA → Australia:        200 ms
```

---

## 6. HTTP / HTTPS

**HTTP** (HyperText Transfer Protocol) is how clients and servers communicate on the web. **HTTPS** adds encryption (TLS).

```
HTTP REQUEST ANATOMY:
┌──────────────────────────────────────────┐
│ POST /api/users HTTP/1.1                 │ ← method + path + version
│ Host: api.example.com                    │ ← required header
│ Content-Type: application/json           │ ← body format
│ Authorization: Bearer eyJhbG...          │ ← auth token
│                                          │
│ {                                        │ ← body
│   "name": "Alice",                       │
│   "email": "alice@example.com"           │
│ }                                        │
└──────────────────────────────────────────┘

HTTP RESPONSE:
┌──────────────────────────────────────────┐
│ HTTP/1.1 201 Created                     │ ← status code
│ Content-Type: application/json           │
│ Location: /api/users/42                  │
│                                          │
│ { "id": 42, "name": "Alice" }            │
└──────────────────────────────────────────┘

STATUS CODES:
┌───────┬──────────────────────────────────────────────┐
│ Range │ Meaning                                      │
├───────┼──────────────────────────────────────────────┤
│ 1xx   │ Informational (100 Continue)                 │
│ 2xx   │ Success (200 OK, 201 Created, 204 No Content)│
│ 3xx   │ Redirect (301 Permanent, 302 Temporary)      │
│ 4xx   │ Client Error (400 Bad, 401 Unauth, 404 Not)  │
│ 5xx   │ Server Error (500 Internal, 502 Bad Gateway)  │
└───────┴──────────────────────────────────────────────┘

HTTPS — TLS HANDSHAKE:
Client                                Server
  │                                      │
  ├── ClientHello (supported ciphers) ──►│
  │◄── ServerHello (chosen cipher) ──────┤
  │◄── Server Certificate (public key) ──┤
  ├── Key Exchange (pre-master secret) ──►│
  │                                      │
  │  Both derive session key             │
  │  All further data is ENCRYPTED 🔒   │
  │◄═══════ encrypted data ═════════════►│

HTTP VERSIONS:
┌─────────┬────────────────────────────────────────────┐
│ Version │ Key Feature                                │
├─────────┼────────────────────────────────────────────┤
│ HTTP/1.0│ New TCP connection per request (slow!)     │
│ HTTP/1.1│ Keep-alive connections, pipelining         │
│ HTTP/2  │ Multiplexing (many requests, one conn)     │
│ HTTP/3  │ QUIC (UDP-based), 0-RTT connection        │
└─────────┴────────────────────────────────────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: STRIPE PAYMENT API — HTTPS in Action
──────────────────────────────────────────────────────
  When you buy something online with a credit card:

  Browser ──HTTPS──► Stripe API
  POST https://api.stripe.com/v1/charges
  Authorization: Bearer sk_live_xxx        ← API key
  Content-Type: application/x-www-form-urlencoded

  amount=2000&currency=usd&source=tok_visa

  The ENTIRE request — including your credit card token — is
  encrypted via TLS. Even your ISP cannot read it.
  Without HTTPS, a hacker on public WiFi could steal the token.

EXAMPLE 2: HTTP/2 — WHY GOOGLE PUSHED FOR IT
──────────────────────────────────────────────────────
  Loading a web page with 50 resources (CSS, JS, images):

  HTTP/1.1 (old):
    Browser opens 6 TCP connections (browser limit)
    Downloads 6 files at a time → 9 rounds needed
    Head-of-line blocking: slow file blocks the connection

  HTTP/2 (modern):
    Browser opens 1 TCP connection
    All 50 files download in PARALLEL (multiplexing)
    Server can PUSH files before browser asks (server push)
    Headers compressed (HPACK) — 85% smaller

  Result: Pages load 15-50% faster.
  All major sites (Google, Facebook, Twitter) use HTTP/2.

EXAMPLE 3: HTTP/3 + QUIC — YOUTUBE ON BAD NETWORKS
──────────────────────────────────────────────────────
  Watching YouTube on a moving train (spotty network):

  HTTP/2 over TCP:
    Signal drops → TCP connection lost → 3-way handshake again
    (300ms+ pause). Head-of-line blocking across streams.

  HTTP/3 over QUIC (UDP):
    Signal drops → QUIC recovers instantly (connection migration)
    Each stream independent → no head-of-line blocking
    0-RTT resumption → reconnect with zero handshake delay

  Google reports HTTP/3 reduces buffering by 9% on YouTube
  and search latency by 2% even on good connections.

EXAMPLE 4: HSTS — FORCING HTTPS (Real Attack Prevention)
──────────────────────────────────────────────────────
  Without HSTS:
    User types "bank.com" → Browser tries HTTP first
    Attacker on same WiFi intercepts → redirects to fake site
    User enters password on fake site → credentials stolen!

  With HSTS (Strict-Transport-Security header):
    Browser ALWAYS uses HTTPS for bank.com (cached rule)
    Even if user types "http://bank.com" → upgraded to HTTPS
    No window for man-in-the-middle attack

  Banks, Gmail, GitHub — all use HSTS with preloading.
```

---

## 7. APIs (Application Programming Interface)

An API defines **how two software components communicate**. It's a contract.

```
API = a MENU at a restaurant

  ┌──────────────────────────────────────────────────┐
  │ You (client) don't go into the kitchen.          │
  │ You order from the MENU (API).                   │
  │ The kitchen (server) prepares and returns food.  │
  │                                                   │
  │ Client ──► API (menu) ──► Server (kitchen)       │
  │ Client ◄── Response ◄──── Server                 │
  └──────────────────────────────────────────────────┘

API STYLES COMPARISON:
┌─────────────┬──────────────┬───────────────┬──────────────┐
│             │ REST         │ GraphQL       │ gRPC         │
├─────────────┼──────────────┼───────────────┼──────────────┤
│ Protocol    │ HTTP/1.1     │ HTTP/1.1      │ HTTP/2       │
│ Format      │ JSON         │ JSON          │ Protobuf     │
│ Schema      │ OpenAPI/     │ Schema +      │ .proto files │
│             │ Swagger      │ type system   │              │
│ Caching     │ HTTP native  │ Custom        │ Custom       │
│ Best for    │ Public APIs  │ Flexible UIs  │ Internal     │
│             │ CRUD         │ mobile apps   │ microservices│
│ Latency     │ Medium       │ Medium        │ Lowest       │
└─────────────┴──────────────┴───────────────┴──────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: GOOGLE MAPS API — Third-Party Integration
──────────────────────────────────────────────────────
  Uber, DoorDash, Zomato — they don't build their own maps.
  They call Google Maps API:

  GET https://maps.googleapis.com/maps/api/directions/json
      ?origin=40.714,-74.006&destination=40.758,-73.985
      &key=YOUR_API_KEY

  Response: { routes: [{ legs: [{ distance: "5.2 km",
                                   duration: "18 min" }]}]}

  Google charges per API call:
  • Directions: $5 per 1000 requests
  • Geocoding:  $5 per 1000 requests
  Uber makes ~20M rides/day → millions of API calls to Google.

EXAMPLE 2: STRIPE API — Payment Processing
──────────────────────────────────────────────────────
  Every e-commerce site (Shopify, Lyft, DoorDash) uses Stripe:

  POST https://api.stripe.com/v1/payment_intents
  Body: { amount: 5000, currency: "usd", payment_method: "..." }

  Stripe handles PCI compliance, fraud detection, and
  50+ payment methods — your app just calls the API.

EXAMPLE 3: gRPC — Netflix Microservice Communication
──────────────────────────────────────────────────────
  Netflix has 1000+ microservices that talk to each other:

  REST (old approach):
    Recommendation Svc ── JSON over HTTP ──► User Profile Svc
    Serialization: 5ms, Transfer: 2ms, Deserialization: 5ms

  gRPC (current approach):
    Recommendation Svc ── Protobuf over HTTP/2 ──► User Profile
    Serialization: 0.5ms, Transfer: 1ms, Deserialization: 0.5ms
    10x faster! Binary format, strongly typed contracts.

  gRPC is used by Google, Netflix, Uber, Slack internally.
```

---

## 8. REST API

**RE**presentational **S**tate **T**ransfer — the most common API style. Resources are identified by URLs, manipulated with HTTP methods.

```
REST PRINCIPLES:
  1. Stateless    — each request contains ALL needed info
  2. Resource-based — URLs represent things (nouns), not actions
  3. HTTP methods  — verbs describe the action
  4. Uniform interface — consistent patterns everywhere

CRUD OPERATIONS MAPPED TO HTTP:
┌──────────┬────────────────────────┬─────────────────────────┐
│ Method   │ URL                    │ Action                  │
├──────────┼────────────────────────┼─────────────────────────┤
│ GET      │ /api/users             │ List all users          │
│ GET      │ /api/users/42          │ Get user 42             │
│ POST     │ /api/users             │ Create new user         │
│ PUT      │ /api/users/42          │ Replace user 42 (full)  │
│ PATCH    │ /api/users/42          │ Update user 42 (partial)│
│ DELETE   │ /api/users/42          │ Delete user 42          │
└──────────┴────────────────────────┴─────────────────────────┘

REST BEST PRACTICES:
  ✅ /api/users/42/orders          (nested resources)
  ✅ /api/orders?status=pending     (filtering)
  ✅ /api/orders?sort=date&limit=20 (pagination)
  ❌ /api/getUser                   (verb in URL — not RESTful)
  ❌ /api/deleteOrder/42            (use DELETE method instead)
```

### Real-Time Examples

```
EXAMPLE 1: TWITTER/X API — Real-World REST Design
──────────────────────────────────────────────────────
  GET  /2/tweets/123456         → Get a specific tweet
  POST /2/tweets                → Create a new tweet
       { "text": "Hello World!" }
  DELETE /2/tweets/123456       → Delete a tweet
  GET  /2/users/me/followers    → List your followers
       ?max_results=100&pagination_token=abc

  Pagination: cursor-based (not offset) for consistency
  Rate limit: 300 requests per 15-minute window
  Auth: OAuth 2.0 Bearer token

EXAMPLE 2: GITHUB API — Nested Resources
──────────────────────────────────────────────────────
  GET  /repos/facebook/react              → Get React repo
  GET  /repos/facebook/react/issues       → List all issues
  GET  /repos/facebook/react/issues/1234  → Get issue #1234
  POST /repos/facebook/react/issues       → Create new issue
  GET  /repos/facebook/react/pulls        → List pull requests

  Notice the hierarchy: /repos/{owner}/{repo}/{resource}
  Each level narrows the scope — this is RESTful design.

EXAMPLE 3: REST API VERSIONING — How Stripe Does It
──────────────────────────────────────────────────────
  Stripe supports API versioning to avoid breaking clients:

  Approach 1: URL versioning (most common)
    GET /v1/customers/42        ← Stripe uses this
    GET /v2/customers/42        ← newer version

  Approach 2: Header versioning
    GET /customers/42
    Stripe-Version: 2024-12-18  ← Stripe also supports this

  Approach 3: Query parameter
    GET /customers/42?version=2

  Stripe maintains 200+ API versions simultaneously!
  Old versions work forever — no breaking changes.
```

---

## 9. GraphQL

A query language for APIs where the **client specifies exactly what data it needs**.

```
THE PROBLEM GRAPHQL SOLVES:

  REST: 3 round trips to build a profile page
  ┌──────┐  GET /users/42              ┌──────┐
  │Client│─────────────────────────────►│Server│  Trip 1
  │      │  GET /users/42/posts        │      │  Trip 2
  │      │─────────────────────────────►│      │
  │      │  GET /users/42/followers    │      │  Trip 3
  │      │─────────────────────────────►│      │
  └──────┘                              └──────┘
  Over-fetching: each response has fields you don't need

  GraphQL: 1 round trip, exact data
  ┌──────┐  POST /graphql              ┌──────┐
  │Client│─────────────────────────────►│Server│  1 trip!
  │      │  query {                    │      │
  │      │    user(id: 42) {           │      │
  │      │      name                   │      │
  │      │      posts { title }        │      │
  │      │      followers { name }     │      │
  │      │    }                        │      │
  │      │  }                          │      │
  └──────┘                              └──────┘
  No over-fetching, no under-fetching!

REST vs GraphQL DECISION:
┌──────────────────────┬──────────────────────────────────┐
│ Choose REST when     │ Choose GraphQL when              │
├──────────────────────┼──────────────────────────────────┤
│ Simple CRUD ops      │ Complex, nested data needs       │
│ HTTP caching needed  │ Multiple client types (web/mobile)│
│ Public APIs          │ Rapidly changing frontend needs  │
│ Team is REST-familiar│ Need to reduce API round trips   │
│ Microservices (BFF)  │ Data graph with relationships    │
└──────────────────────┴──────────────────────────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: GITHUB API v4 — GraphQL in Production
──────────────────────────────────────────────────────
  GitHub switched from REST (v3) to GraphQL (v4):

  REST (old) — 3 API calls to show a repo page:
    GET /repos/facebook/react          → repo info
    GET /repos/facebook/react/issues   → issues list
    GET /repos/facebook/react/contributors → contributors
    Total: 3 round trips, lots of unused fields

  GraphQL (new) — 1 API call:
    query {
      repository(owner: "facebook", name: "react") {
        name
        stargazerCount
        issues(first: 10) { nodes { title, state } }
        mentionableUsers(first: 5) { nodes { login } }
      }
    }
    Total: 1 round trip, exact fields needed ✅

EXAMPLE 2: SHOPIFY — GraphQL for E-Commerce
──────────────────────────────────────────────────────
  A mobile app showing a product page needs:
    Product name, price, first 3 images, and 5 reviews

  REST: /products/123 returns ALL 50 fields + ALL images
        Wastes mobile bandwidth (critical on 3G/4G)

  GraphQL:
    query {
      product(id: "123") {
        title
        price
        images(first: 3) { url, alt }
        reviews(first: 5) { rating, comment }
      }
    }
    Only requested data transferred — saves 60-80% bandwidth

EXAMPLE 3: FACEBOOK — Where GraphQL Was Born
──────────────────────────────────────────────────────
  Facebook created GraphQL in 2012 because:

  Mobile News Feed needed data from 10+ backend services:
    User profile + Posts + Comments + Likes + Photos +
    Friends list + Ad data + Stories + Groups + Events

  REST approach: 10+ API calls → slow on mobile
  GraphQL: 1 query, exactly what the News Feed needs

  Today, Facebook's mobile app makes a single GraphQL query
  that fetches the entire News Feed in one round trip.
  This reduced data transfer by 50%+ on mobile devices.
```

---

## 10. Databases

Organized collections of structured data. The engine behind nearly every application.

```
DATABASE LANDSCAPE:

┌──────────────────────────────────────────────────────────────┐
│                        DATABASES                              │
│                                                                │
│   ┌────────────────────┐       ┌────────────────────────┐     │
│   │    RELATIONAL       │       │     NON-RELATIONAL     │     │
│   │    (SQL)            │       │     (NoSQL)            │     │
│   │                    │       │                        │     │
│   │ ┌────────────────┐ │       │ ┌──────────────────┐   │     │
│   │ │ PostgreSQL     │ │       │ │ Key-Value        │   │     │
│   │ │ MySQL          │ │       │ │ Redis, DynamoDB  │   │     │
│   │ │ SQL Server     │ │       │ └──────────────────┘   │     │
│   │ │ Oracle         │ │       │ ┌──────────────────┐   │     │
│   │ └────────────────┘ │       │ │ Document         │   │     │
│   │                    │       │ │ MongoDB, Couch   │   │     │
│   │ Tables + Rows      │       │ └──────────────────┘   │     │
│   │ ACID transactions  │       │ ┌──────────────────┐   │     │
│   │ SQL query language │       │ │ Wide-Column      │   │     │
│   │ Fixed schema       │       │ │ Cassandra, HBase │   │     │
│   │ JOINs across tables│       │ └──────────────────┘   │     │
│   └────────────────────┘       │ ┌──────────────────┐   │     │
│                                │ │ Graph            │   │     │
│                                │ │ Neo4j, Neptune   │   │     │
│                                │ └──────────────────┘   │     │
│                                └────────────────────────┘     │
└──────────────────────────────────────────────────────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: WHO USES WHAT DATABASE
──────────────────────────────────────────────────────
  ┌─────────────────┬──────────────────┬──────────────────┐
  │ Company         │ Database         │ Why              │
  ├─────────────────┼──────────────────┼──────────────────┤
  │ Instagram       │ PostgreSQL       │ User data, ACID  │
  │ Instagram       │ Cassandra        │ Feed, messages   │
  │ Uber            │ MySQL → PostgreSQL│ Trip data        │
  │ Uber            │ Redis            │ Driver locations  │
  │ Netflix         │ Cassandra        │ Viewing history  │
  │ Netflix         │ EVCache (Redis)  │ Session, caching │
  │ Twitter/X       │ MySQL (sharded)  │ Tweets, users    │
  │ Twitter/X       │ Manhattan (KV)   │ Timeline cache   │
  │ LinkedIn        │ Espresso (doc)   │ Profile data     │
  │ Airbnb          │ MySQL + DynamoDB │ Bookings, search │
  │ Spotify         │ Cassandra        │ Playlists        │
  │ Pinterest       │ HBase + MySQL    │ Pins + User data │
  └─────────────────┴──────────────────┴──────────────────┘

  Key insight: Most large companies use MULTIPLE databases.
  This is called "polyglot persistence."

EXAMPLE 2: WHEN TO USE EACH TYPE
──────────────────────────────────────────────────────
  Building an e-commerce platform? You might use:

  PostgreSQL: Orders, users, payments (need ACID transactions)
  Redis:      Shopping cart, session data (need speed)
  Elasticsearch: Product search (need full-text search)
  Cassandra:  Product reviews (write-heavy, eventual consistency OK)
  S3 + CDN:   Product images (not a DB, but stores data)

  Each database type excels at different access patterns.
  Wrong choice = performance nightmare.
```

---

## 11. SQL vs NoSQL

```
┌──────────────────────┬──────────────────────────────────┐
│ SQL (Relational)     │ NoSQL (Non-Relational)           │
├──────────────────────┼──────────────────────────────────┤
│ Fixed schema         │ Flexible/no schema               │
│ Tables + rows        │ Documents, KV pairs, graphs      │
│ ACID transactions    │ BASE (eventual consistency)      │
│ Vertical scaling     │ Horizontal scaling               │
│ Complex JOINs        │ Denormalized, embedded data      │
│ SQL query language   │ Varies per database              │
│                      │                                  │
│ Best for:            │ Best for:                        │
│ • Financial systems  │ • Social media, IoT              │
│ • Inventory/ERP      │ • Real-time analytics            │
│ • Complex relations  │ • Content management             │
│ • Strict consistency │ • High write throughput           │
├──────────────────────┼──────────────────────────────────┤
│ PostgreSQL, MySQL    │ MongoDB, Redis, Cassandra, Neo4j │
└──────────────────────┴──────────────────────────────────┘

WHEN TO CHOOSE:
  "Do I need complex JOINs and ACID?"  → SQL
  "Do I need flexible schema and scale?" → NoSQL
  "Can I tolerate eventual consistency?" → NoSQL
  "Is data highly relational?"           → SQL (or Graph NoSQL)
```

### Real-Time Examples

```
EXAMPLE 1: UBER — SQL for Financial Data, NoSQL for Location
──────────────────────────────────────────────────────
  SQL (PostgreSQL) for:
    • Payment transactions — ACID required (money must be exact)
    • Driver payouts — cannot lose or duplicate payments
    • Regulatory reporting — need complex JOINs and aggregates

  NoSQL (Redis + Cassandra) for:
    • Real-time driver location — 5M updates/sec (high write volume)
    • Trip history — append-only, partitioned by rider_id
    • ETA calculation cache — high read, tolerate stale data

  If Uber used SQL for location tracking:
    5M writes/sec → PostgreSQL would crumble ❌
  If Uber used NoSQL for payments:
    "Your $50 ride was charged $100" — unacceptable ❌

EXAMPLE 2: INSTAGRAM — PostgreSQL + Cassandra
──────────────────────────────────────────────────────
  PostgreSQL handles:
    • User profiles, follow relationships (relational data)
    • "Does user A follow user B?" — simple JOIN

  Cassandra handles:
    • News feed (billions of feed entries, write-heavy)
    • Direct messages (partitioned by chat_id + timestamp)
    • Write once, read many — Cassandra excels at this pattern

EXAMPLE 3: REAL DECISION FRAMEWORK
──────────────────────────────────────────────────────
  Answering these questions picks your database:

  ┌──────────────────────────────────────────────────────────┐
  │ Q: Do I need transactions across multiple tables?        │
  │    YES → SQL (PostgreSQL, MySQL)                         │
  │                                                           │
  │ Q: Is my write volume > 100K/sec per table?             │
  │    YES → NoSQL (Cassandra, DynamoDB)                     │
  │                                                           │
  │ Q: Is my data structure unpredictable/evolving?          │
  │    YES → Document DB (MongoDB)                           │
  │                                                           │
  │ Q: Do I need sub-millisecond reads?                      │
  │    YES → Key-Value (Redis, Memcached)                    │
  │                                                           │
  │ Q: Am I modeling complex relationships (social graph)?   │
  │    YES → Graph DB (Neo4j, Amazon Neptune)                │
  │                                                           │
  │ Q: Do I need full-text search?                           │
  │    YES → Search Engine (Elasticsearch, Solr)             │
  └──────────────────────────────────────────────────────────┘
```

---

## 12-13. Vertical Scaling vs Horizontal Scaling

```
═══════════════════════════════════════════════════════════
  VERTICAL SCALING (Scale UP) — Bigger machine
═══════════════════════════════════════════════════════════

  BEFORE             AFTER
  ┌──────────┐       ┌──────────────────┐
  │ 4 CPU    │       │ 64 CPU           │
  │ 8 GB RAM │  ──►  │ 512 GB RAM       │
  │ 256 GB   │       │ 4 TB SSD         │
  │ SSD      │       │                  │
  └──────────┘       └──────────────────┘
  $100/month          $5,000/month

  ✅ Simple (no code changes)   ❌ Hardware ceiling
  ✅ No distributed complexity  ❌ Single point of failure
  ✅ Strong consistency easy    ❌ Downtime during upgrade

═══════════════════════════════════════════════════════════
  HORIZONTAL SCALING (Scale OUT) — More machines
═══════════════════════════════════════════════════════════

  BEFORE             AFTER
  ┌──────────┐       ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐
  │ 4 CPU    │       │ 4CPU │ │ 4CPU │ │ 4CPU │ │ 4CPU │
  │ 8 GB RAM │  ──►  │ 8 GB │ │ 8 GB │ │ 8 GB │ │ 8 GB │
  │          │       └──────┘ └──────┘ └──────┘ └──────┘
  └──────────┘       $100 × 4 = $400/month
  $100/month         Behind a Load Balancer

  ✅ Virtually unlimited     ❌ Distributed complexity
  ✅ Fault tolerant           ❌ Data consistency harder
  ✅ Cost-effective           ❌ Code must be stateless
  ✅ Zero-downtime scaling    ❌ Network latency between nodes
```

### Real-Time Examples

```
EXAMPLE 1: STACK OVERFLOW — Vertical Scaling Success Story
──────────────────────────────────────────────────────
  Stack Overflow serves 1.3 billion page views/month with:
    • 9 web servers (IIS)
    • 4 SQL Servers (2 primary + 2 replicas)
    • 2 Redis servers
    • 2 Elasticsearch servers

  They chose VERTICAL scaling:
    SQL Server: 1.5 TB RAM, 384 cores
    Why? SQL JOINs are fast on one powerful machine.
    No distributed transaction complexity. Simple architecture.

  Stack Overflow proves: you don't ALWAYS need microservices
  and horizontal scaling. Right tool for the right job.

EXAMPLE 2: NETFLIX — Horizontal Scaling at Massive Scale
──────────────────────────────────────────────────────
  Netflix during peak hours (8 PM on a Friday):
    • 250+ million subscribers streaming simultaneously
    • Cannot be served by ONE machine, no matter how powerful

  Netflix's approach:
    Thousands of EC2 instances across 3 AWS regions
    Auto-scaling: 100 instances (3 AM) → 10,000 instances (8 PM)
    Stateless servers (session stored in Redis, not on server)

  Auto-scaling rule:
    IF avg_CPU > 70% for 5 min → add 50 more instances
    IF avg_CPU < 30% for 10 min → remove 30 instances
    Cost savings: ~40% vs always running peak capacity

EXAMPLE 3: WHEN TO CHOOSE WHICH
──────────────────────────────────────────────────────
  ┌──────────────────────────────────────────────────────────┐
  │  Start with vertical scaling until you hit limits:       │
  │                                                           │
  │  Stage 1: Single server (< 1000 users)                   │
  │    → Vertical: upgrade CPU, RAM                          │
  │                                                           │
  │  Stage 2: Growing pains (1K-100K users)                  │
  │    → Add read replicas (horizontal for reads)            │
  │    → Add Redis cache                                     │
  │    → Vertical scale the primary DB                       │
  │                                                           │
  │  Stage 3: Serious scale (100K-10M users)                 │
  │    → Horizontal: multiple app servers + load balancer    │
  │    → Database sharding                                   │
  │    → CDN for static content                              │
  │                                                           │
  │  Stage 4: Internet-scale (10M+ users)                    │
  │    → Full horizontal: auto-scaling, multi-region         │
  │    → Microservices architecture                          │
  │    → Polyglot persistence (multiple DBs)                 │
  └──────────────────────────────────────────────────────────┘
```

---

## 14. Load Balancers

Distribute incoming traffic across multiple servers so no single server is overwhelmed.

```
                    ┌────────────────┐
  Clients ─────────►│ Load Balancer  │
                    │                │
                    │ Algorithms:    │
                    │ • Round Robin  │
                    │ • Least Conn   │
                    │ • IP Hash      │
                    │ • Weighted     │
                    └───┬────┬────┬──┘
                        │    │    │
                   ┌────┘    │    └────┐
                   ▼         ▼         ▼
              ┌────────┐┌────────┐┌────────┐
              │Server 1││Server 2││Server 3│
              │  30%   ││  35%   ││  35%   │ ← balanced load
              └────────┘└────────┘└────────┘

LOAD BALANCING AT MULTIPLE LAYERS:
  DNS ──► Global LB (GeoDNS) ──► Regional LB ──► Servers
          route by geography     route by load    serve request
```

### Real-Time Examples

```
EXAMPLE 1: NETFLIX — Multi-Layer Load Balancing
──────────────────────────────────────────────────────
  User in India wants to watch a show:

  Layer 1: DNS (Route 53 GeoDNS)
    → Routes to Mumbai AWS region (closest)

  Layer 2: AWS ALB (Application Load Balancer)
    → Routes /api/* to API servers
    → Routes /stream/* to streaming servers
    → Routes /search/* to search servers
    → Health checks every 10 seconds

  Layer 3: Internal LB (Eureka service discovery)
    → Within "API servers," picks the least-loaded instance
    → If instance fails health check → removed in 30 seconds

  Result: Netflix handles 250M+ users with 99.99% uptime

EXAMPLE 2: HOW LOAD BALANCING ALGORITHMS WORK IN PRACTICE
──────────────────────────────────────────────────────
  Scenario: 3 servers, 100 incoming requests

  ROUND ROBIN:
    Request 1 → Server A, Request 2 → Server B,
    Request 3 → Server C, Request 4 → Server A ...
    Equal distribution but ignores server health/load.

  LEAST CONNECTIONS (Nginx, HAProxy):
    Server A: 15 active connections
    Server B: 5 active connections   ← next request goes here ✅
    Server C: 12 active connections

  IP HASH (sticky sessions):
    hash("203.0.113.42") % 3 = 1 → always goes to Server B
    User's session stays on same server (shopping cart stays!)

  WEIGHTED ROUND ROBIN:
    Server A (16 CPU): weight=4  → gets 4x more traffic
    Server B (4 CPU):  weight=1  → gets 1x traffic
    Useful during gradual rollouts (canary deployment).

EXAMPLE 3: HEALTH CHECKS — Detecting Dead Servers
──────────────────────────────────────────────────────
  AWS ALB pings each server every 30 seconds:

  GET /health → 200 OK       (server is healthy ✅)
  GET /health → 503 Error     (server is struggling ⚠️)
  GET /health → no response   (server is dead ❌)

  After 3 consecutive failures (unhealthy threshold):
    Server removed from rotation → no traffic sent
    Alert sent to on-call engineer
    Auto-scaling launches replacement instance
```

> See also: [Load Balancing Deep Dive](load-balancing.md)

---

## 15. Database Indexing

An index is a data structure that speeds up reads by avoiding full table scans — like a book's index.

```
WITHOUT INDEX — Full Table Scan:
┌──────┬──────────┬────────────┐
│ id   │ name     │ email      │  Scan ALL 10M rows
├──────┼──────────┼────────────┤  to find email =
│ 1    │ Alice    │ a@mail.com │  "z@mail.com"
│ 2    │ Bob      │ b@mail.com │
│ ...  │ ...      │ ...        │  Time: O(N) = SLOW
│ 10M  │ Zara     │ z@mail.com │ ← found at row 10M!
└──────┴──────────┴────────────┘

WITH INDEX on email — B-Tree Lookup:
┌──────────────────────────────────┐
│        B-Tree Index (email)       │
│                                    │
│              [m@...]               │
│             ╱      ╲              │
│        [d@...]    [t@...]         │
│        ╱    ╲      ╱    ╲         │
│    [a@..] [g@..][p@..] [z@..] ◄── found in 3 hops!
│                                    │
│  Time: O(log N) = FAST             │
│  10M rows → ~23 comparisons       │
│  (vs 10,000,000 without index)    │
└──────────────────────────────────┘

TRADE-OFF:
  ✅ Reads: 100-1000x faster
  ❌ Writes: slightly slower (must update index)
  ❌ Storage: index takes extra disk space
  Rule: index columns you frequently search, filter, or JOIN on
```

### Real-Time Examples

```
EXAMPLE 1: E-COMMERCE — Without vs With Index
──────────────────────────────────────────────────────
  Amazon products table: 100 million rows

  Query: "Show me all Nike running shoes under $100"
  SELECT * FROM products
  WHERE brand = 'Nike' AND category = 'running_shoes' AND price < 100;

  WITHOUT INDEX:
    Full table scan: checks all 100M rows → 45 seconds ❌
    User left the page after 3 seconds.

  WITH COMPOSITE INDEX on (brand, category, price):
    B-tree traversal: 3 hops → 0.005 seconds (5ms) ✅
    Result: 847 products returned instantly

EXAMPLE 2: INSTAGRAM — Index Types and When to Use Them
──────────────────────────────────────────────────────
  ┌─────────────────┬──────────────────┬──────────────────┐
  │ Index Type      │ Use Case         │ Real Example     │
  ├─────────────────┼──────────────────┼──────────────────┤
  │ B-Tree (default)│ Range queries,   │ "Posts after     │
  │                 │ sorting, =, <, > │ Jan 2024"        │
  ├─────────────────┼──────────────────┼──────────────────┤
  │ Hash Index      │ Exact match only │ "User with       │
  │                 │ (O(1) lookup)    │ email = X"       │
  ├─────────────────┼──────────────────┼──────────────────┤
  │ GIN (inverted)  │ Full-text search,│ "Posts containing │
  │                 │ JSONB, arrays    │ 'sunset'"        │
  ├─────────────────┼──────────────────┼──────────────────┤
  │ GiST            │ Geospatial,      │ "Restaurants     │
  │                 │ nearest neighbor │ within 5 km"     │
  ├─────────────────┼──────────────────┼──────────────────┤
  │ BRIN (block     │ Sorted data      │ "Logs from       │
  │ range)          │ (time-series)    │ March 2024"      │
  └─────────────────┴──────────────────┴──────────────────┘

EXAMPLE 3: SLACK — The Slow Query That Crashed Everything
──────────────────────────────────────────────────────
  Slack's messages table had billions of rows:

  Query: SELECT * FROM messages WHERE channel_id = 'C123'
         ORDER BY created_at DESC LIMIT 50;

  Problem: No index on (channel_id, created_at)
  Result:  Sequential scan → 30+ seconds per query
           With 10M active channels → database overloaded

  Fix: CREATE INDEX idx_messages_channel_time
       ON messages (channel_id, created_at DESC);

  After: Same query → 2ms ✅ (15,000x faster)
  Lesson: Always index your most frequent query patterns.
```

---

## 16. Replication

Copy data across multiple servers for **fault tolerance** and **read scaling**.

```
═══════════════════════════════════════════════════════════
  SINGLE-LEADER REPLICATION (most common)
═══════════════════════════════════════════════════════════

  Writes ──► ┌──────────┐ ── sync/async ──► ┌──────────┐
             │  Leader   │                   │ Follower │
             │ (primary) │ ── sync/async ──► │ (replica)│
  Reads ───► │          │                   └──────────┘
  Reads ────────────────────────────────────► ┌──────────┐
                                             │ Follower │
                                             │ (replica)│
                                             └──────────┘
  Writes go to leader only.
  Reads can go to any follower (read scaling).
  If leader dies → promote a follower.

═══════════════════════════════════════════════════════════
  MULTI-LEADER (e.g., CockroachDB, geo-distributed)
═══════════════════════════════════════════════════════════

  ┌──────────┐ ◄── sync ──► ┌──────────┐
  │ Leader A │              │ Leader B │
  │ (US)     │              │ (EU)     │
  └──────────┘              └──────────┘
  Both accept writes. Conflict resolution needed.

SYNC vs ASYNC REPLICATION:
┌──────────────┬────────────────────┬──────────────────────┐
│              │ Synchronous        │ Asynchronous         │
├──────────────┼────────────────────┼──────────────────────┤
│ Durability   │ Data safe on 2+    │ Data may be lost if  │
│              │ nodes before ACK   │ leader dies before   │
│              │                    │ replication           │
│ Latency      │ Higher (wait for   │ Lower (ACK           │
│              │ replica ACK)       │ immediately)          │
│ Consistency  │ Strong             │ Eventual             │
└──────────────┴────────────────────┴──────────────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: YOUTUBE — Read Replicas for Global Scale
──────────────────────────────────────────────────────
  YouTube gets 500+ hours of video uploaded per minute.
  Metadata (title, description, views) stored in MySQL.

  Without replication:
    Single DB in US → user in India queries → 200ms latency
    1 billion views/day → single DB overwhelmed

  With replication:
    Primary (US-West):  handles ALL writes (upload metadata)
    Replica (US-East):  handles reads for East Coast users
    Replica (EU):       handles reads for European users
    Replica (Asia):     handles reads for Asian users

    Write: "New video uploaded" → primary → async replicated
    Read:  "Show me trending videos" → nearest replica (5ms)

    Replication lag: typically 100-500ms (acceptable for
    "trending" page — a 500ms delay is invisible to users)

EXAMPLE 2: BANKING — Synchronous Replication (No Data Loss)
──────────────────────────────────────────────────────
  Bank transfer: Move $10,000 from Account A to Account B

  Primary DB (New York):
    1. Debit $10,000 from Account A
    2. Credit $10,000 to Account B
    3. WAIT for replica ACK before confirming ✅

  Replica DB (New Jersey — disaster recovery):
    Receives write → confirms to primary → primary tells user

  If primary dies BEFORE replica confirms:
    Synchronous: Transaction rolled back. No money lost. ✅
    Async would be: Money might vanish! ❌

  Trade-off: 10-50ms extra latency per write (acceptable for
  banking; unacceptable for social media likes).

EXAMPLE 3: REPLICATION LAG — THE FACEBOOK "LOST POST" BUG
──────────────────────────────────────────────────────
  User posts "Happy Birthday!" (write → primary)
  User immediately refreshes page (read → replica)
  Post not there yet! (replica hasn't caught up) 😱

  Solutions:
  1. Read-your-own-writes: After a write, route that
     user's reads to the primary for 5 seconds
  2. Monotonic reads: Pin user to same replica (sticky session)
  3. Causal consistency: Track "version" — if user wrote v5,
     don't serve data older than v5
```

---

## 17. Sharding

Split data across multiple databases. Each shard holds a **subset** of the data.

```
BEFORE SHARDING — Single DB:
┌────────────────────────────┐
│ Database (1 billion rows!) │ ← too big, too slow
│ All users: A through Z    │
└────────────────────────────┘

AFTER SHARDING — Distributed:
┌────────────────┐ ┌────────────────┐ ┌────────────────┐
│ Shard 1        │ │ Shard 2        │ │ Shard 3        │
│ Users A-I      │ │ Users J-R      │ │ Users S-Z      │
│ 333M rows      │ │ 333M rows      │ │ 333M rows      │
└────────────────┘ └────────────────┘ └────────────────┘
Each shard is fast and manageable!

SHARDING STRATEGIES:
┌─────────────────────────────────────────────────────────┐
│ 1. RANGE-BASED: shard by user_id ranges                 │
│    Shard 1: IDs 1-1M    Shard 2: IDs 1M-2M             │
│    ❌ Hot spots if some ranges are more active          │
│                                                          │
│ 2. HASH-BASED: shard = hash(user_id) % num_shards      │
│    Evenly distributed, but adding shards = rehash       │
│    ✅ Balanced load                                     │
│                                                          │
│ 3. DIRECTORY-BASED: lookup table maps key → shard       │
│    Most flexible but lookup table = SPOF                │
│                                                          │
│ 4. GEO-BASED: shard by geography                        │
│    US users → US shard, EU users → EU shard             │
│    ✅ Low latency for regional users                    │
└─────────────────────────────────────────────────────────┘

CHALLENGES:
  ❌ Cross-shard JOINs are expensive/impossible
  ❌ Rebalancing when adding new shards
  ❌ Distributed transactions
  ❌ Application routing complexity
```

### Real-Time Examples

```
EXAMPLE 1: INSTAGRAM — Sharding PostgreSQL by User ID
──────────────────────────────────────────────────────
  Instagram stores 2+ billion user accounts.
  Single PostgreSQL instance cannot hold this.

  Sharding strategy: user_id % num_shards

  shard_id = user_id % 64  (64 shards)

  Shard 0:  users 0, 64, 128, 192, ...
  Shard 1:  users 1, 65, 129, 193, ...
  ...
  Shard 63: users 63, 127, 191, 255, ...

  Each shard: ~31 million users → manageable!

  Challenge they faced:
    "Show Alice's feed" → her posts are on Shard 7
    "Show posts from people Alice follows" → followers are
    on 20 different shards! Cross-shard query needed.

    Solution: Pre-compute feed in Redis (fan-out on write)

EXAMPLE 2: DISCORD — Sharding by Guild (Server) ID
──────────────────────────────────────────────────────
  Discord has 200M+ monthly active users, 20M+ servers

  Shard key: guild_id (Discord server ID)
    All messages for a Discord server → same shard
    All members of a server → same shard
    All channels of a server → same shard

  Why guild_id and not user_id?
    Most queries are: "Get messages in channel X of server Y"
    If sharded by user_id → need to query ALL shards for
    messages in one channel = disaster

    Sharded by guild_id → one shard has ALL the data
    for that server → single-shard query ✅

EXAMPLE 3: TWITTER/X — Consistent Hashing for Sharding
──────────────────────────────────────────────────────
  Problem with hash(key) % N:
    Adding one shard (N → N+1) moves ~100% of keys!
    With 1 billion tweets → massive data migration

  Consistent Hashing:
    Adding one shard moves only ~1/N of keys ✅

  Before:  4 shards, 1B tweets = 250M per shard
  Add shard 5:
    Hash sharding: rehash ALL 1B tweets ❌ (hours of downtime)
    Consistent hashing: move only ~200M tweets ✅ (minimal)

  ┌──────────────────────────────────────────┐
  │  Hash Ring:                               │
  │           Shard A                         │
  │         ╱         ╲                       │
  │    Shard D    ●    Shard B               │
  │         ╲   (new)  ╱                     │
  │           Shard C                         │
  │                                           │
  │  Only keys between C and ● move to new   │
  │  shard. Everything else stays put.        │
  └──────────────────────────────────────────┘
```

---

## 18. Vertical Partitioning

Split a table's **columns** into separate tables or services (vs sharding which splits rows).

```
BEFORE — One wide table:
┌────────────────────────────────────────────────────────┐
│ users table                                            │
│ id │ name │ email │ avatar_blob │ bio │ preferences    │
│    │      │       │ (5MB each!) │     │ (JSON, 10KB)   │
└────────────────────────────────────────────────────────┘
  Every query loads ALL columns, even if you just need name!

AFTER — Vertically partitioned:
┌──────────────────────┐  ┌──────────────────────────┐
│ users_core           │  │ users_profile            │
│ id │ name │ email    │  │ id │ bio │ preferences   │
│    │      │          │  │    │     │               │
│ Fast queries!        │  │ Separate table/service   │
│ Small rows           │  │ Queried less often       │
└──────────────────────┘  └──────────────────────────┘
                          ┌──────────────────────────┐
                          │ users_media              │
                          │ id │ avatar_blob         │
                          │    │ (stored in S3/Blob) │
                          └──────────────────────────┘

USE CASES:
  • Separate hot (frequently accessed) from cold columns
  • Move BLOBs to object storage (S3)
  • Different access patterns = different databases
  • Microservices: each service owns its columns
```

### Real-Time Examples

```
EXAMPLE 1: LINKEDIN — Profile Data Split
──────────────────────────────────────────────────────
  LinkedIn profile has 50+ fields but different access patterns:

  BEFORE (one wide table):
    Every "Who viewed your profile" query loaded ALL 50 columns
    including resume_blob (5MB), endorsements JSON (100KB)
    Result: 500MB memory per query batch, slow reads

  AFTER (vertically partitioned):
    ┌──────────────────────┐   Hot data — queried 100x/day
    │ profile_core         │   per user
    │ id, name, headline,  │   PostgreSQL (fast SSD)
    │ photo_url, location  │
    └──────────────────────┘
    ┌──────────────────────┐   Warm data — queried 5x/day
    │ profile_experience   │   per user
    │ id, companies[],     │   PostgreSQL (standard)
    │ education[], skills[]│
    └──────────────────────┘
    ┌──────────────────────┐   Cold data — queried rarely
    │ profile_media        │
    │ id, resume_blob,     │   S3 (cheap storage)
    │ portfolio_files      │
    └──────────────────────┘

EXAMPLE 2: E-COMMERCE — Splitting Product Table
──────────────────────────────────────────────────────
  Product listing page needs: name, price, image_url, rating
  Product detail page needs: description, specs, reviews

  Split into:
  ┌────────────────────────────┐
  │ products_listing           │  ← Small rows, fits in cache
  │ id, name, price, image_url,│    Redis cache for all 10M rows
  │ rating, category_id       │    = ~5 GB (fits in memory!)
  └────────────────────────────┘
  ┌────────────────────────────┐
  │ products_detail            │  ← Large rows, queried only
  │ id, description (5KB),    │    when user clicks a product
  │ specs_json (2KB),         │    Not cached (too large)
  │ manufacturer_info         │
  └────────────────────────────┘

  Result: Listing page loads in 50ms (all from cache)
          Detail page loads in 200ms (DB query, acceptable)
```

---

## 19. Caching

Store frequently accessed data in a **fast, in-memory** layer to avoid expensive operations.

```
WITHOUT CACHE:
  Client ──► Server ──► Database (50ms)
  Every request hits the DB!

WITH CACHE:
  Client ──► Server ──► Cache (Redis, 0.5ms) ── HIT ──► return
                    │                         └─ MISS ─► DB (50ms)
                    │                                    ↓
                    │                              populate cache
                    └──────────────────────────────────────────►

CACHING STRATEGIES:
┌─────────────────┬────────────────────────────────────────┐
│ Cache-Aside     │ App checks cache → miss → query DB →   │
│ (Lazy Loading)  │ store in cache → return. Most common.  │
├─────────────────┼────────────────────────────────────────┤
│ Read-Through    │ Cache itself fetches from DB on miss.  │
│                 │ App only talks to cache.                │
├─────────────────┼────────────────────────────────────────┤
│ Write-Through   │ Write to cache AND DB simultaneously.  │
│                 │ Strong consistency, higher write latency│
├─────────────────┼────────────────────────────────────────┤
│ Write-Behind    │ Write to cache, async write to DB.     │
│ (Write-Back)    │ Fast writes, risk of data loss.        │
└─────────────────┴────────────────────────────────────────┘

EVICTION POLICIES:
  LRU  — evict Least Recently Used
  LFU  — evict Least Frequently Used
  FIFO — evict First In, First Out
  TTL  — evict after Time-To-Live expires
```

### Real-Time Examples

```
EXAMPLE 1: TWITTER/X — Cache Everything, Query Nothing
──────────────────────────────────────────────────────
  When you open Twitter, you see your timeline instantly.
  This is NOT a real-time database query.

  Twitter caches your timeline in Redis:
    Key:   "timeline:user_42"
    Value: [tweet_id_1, tweet_id_2, ... tweet_id_800]
    TTL:   24 hours

  When someone you follow tweets:
    Fan-out: push tweet_id into timeline caches of all followers
    (for users with < 10K followers)

  When you open the app:
    GET from Redis → 0.5ms → render tweets ✅
    No database query at all for most users.

  Twitter's Redis cluster: 1000+ nodes, 100+ TB of cached data

EXAMPLE 2: CACHE STAMPEDE — How Instagram Prevented It
──────────────────────────────────────────────────────
  Scenario: Celebrity's profile cached with TTL=5min.
  Beyoncé's profile expires → 50,000 users hit DB simultaneously!

  ┌─────────────────────────────────────────────┐
  │ 50K requests ──► Cache MISS ──► All hit DB  │
  │                                   DB: 💀     │
  │ This is called a "cache stampede"           │
  └─────────────────────────────────────────────┘

  Instagram's solution: LOCK + EARLY REFRESH

  1. First request detects MISS → acquires a lock
  2. Only THAT request queries DB and refreshes cache
  3. Other 49,999 requests wait or get slightly stale data
  4. Proactive refresh: when TTL < 20% remaining,
     background job refreshes cache BEFORE expiry

EXAMPLE 3: MULTI-LAYER CACHING — Netflix Architecture
──────────────────────────────────────────────────────
  Netflix uses 4 layers of caching:

  Layer 1: Browser/App cache (local device)
    → Cached thumbnails, UI assets, user preferences
    → Latency: 0ms

  Layer 2: CDN edge cache (CloudFront)
    → Cached video segments, images
    → Latency: 5-20ms

  Layer 3: Application cache (EVCache = Redis-based)
    → Cached user profiles, watch history, recommendations
    → Latency: 0.5-2ms
    → Cluster: 10,000+ nodes, 30+ million ops/sec

  Layer 4: Database (Cassandra, MySQL)
    → Source of truth, only hit on cache miss
    → Latency: 10-100ms

  Cache hit rate: > 95% → only 5% of requests reach the DB!
```

> See also: [Caching Strategies Deep Dive](caching-strategies.md)

---

## 20. Denormalization

Intentionally add **redundant data** to avoid expensive JOINs and speed up reads.

```
NORMALIZED (3NF — no redundancy):

  orders table                 users table
  ┌────┬─────────┬──────┐     ┌────┬──────────┐
  │ id │ user_id │ amt  │     │ id │ name     │
  │ 1  │ 42      │ $99  │     │ 42 │ Alice    │
  │ 2  │ 42      │ $50  │     └────┴──────────┘
  └────┴─────────┴──────┘

  Query: SELECT o.*, u.name FROM orders o JOIN users u ON ...
         JOIN = expensive at scale! ❌

DENORMALIZED (redundant but fast):

  orders table
  ┌────┬─────────┬──────┬──────────┐
  │ id │ user_id │ amt  │ user_name│  ← name duplicated
  │ 1  │ 42      │ $99  │ Alice    │     in every order
  │ 2  │ 42      │ $50  │ Alice    │
  └────┴─────────┴──────┴──────────┘

  Query: SELECT * FROM orders WHERE user_id = 42;
         No JOIN needed! ✅ Faster!

TRADE-OFF:
  ✅ Much faster reads (no JOINs)
  ❌ Harder writes (must update all copies when name changes)
  ❌ Risk of data inconsistency
  ❌ More storage
  Rule: Denormalize when read-heavy (read:write > 10:1)
```

### Real-Time Examples

```
EXAMPLE 1: AMAZON ORDER HISTORY — Why Denormalization is Essential
──────────────────────────────────────────────────────
  Normalized approach for "Show order history":
    SELECT o.*, p.name, p.image, a.street, a.city
    FROM orders o
    JOIN order_items oi ON o.id = oi.order_id
    JOIN products p ON oi.product_id = p.id
    JOIN addresses a ON o.address_id = a.id
    WHERE o.user_id = 42;

    4-table JOIN × 300M orders = query takes 5 seconds ❌

  Denormalized approach (what Amazon actually does):
    Each order stores a SNAPSHOT of the data at time of purchase:

    {
      "order_id": 12345,
      "user_id": 42,
      "items": [
        { "name": "iPhone 15", "price": 999, "image": "url..." }
      ],
      "shipping_address": { "street": "123 Main", "city": "NYC" }
    }

    No JOINs needed → query takes 2ms ✅
    Even if product name changes later, your order history
    shows what you ACTUALLY ordered (correct behavior!)

EXAMPLE 2: SOCIAL MEDIA — Embedding User Info in Posts
──────────────────────────────────────────────────────
  Normalized (3NF):
    post: { id, text, user_id }
    user: { id, name, avatar_url }
    To display a post → always JOIN users table

  Denormalized (Instagram/Twitter approach):
    post: { id, text, user_id, user_name, user_avatar_url }

  Why?
    News feed loads 50 posts → 50 JOINs to users table ❌
    With denormalization → 0 JOINs, single table scan ✅

  What if user changes their name?
    Async job updates all their posts (eventual consistency)
    Most users never change their name — this is rare.

EXAMPLE 3: SEARCH RESULTS — Google Denormalizes Everything
──────────────────────────────────────────────────────
  When Google crawls a page, it doesn't normalize the data:
    {
      "url": "https://...",
      "title": "Best Restaurants in NYC",
      "snippet": "Top 10 restaurants...",
      "domain": "yelp.com",
      "favicon_url": "https://yelp.com/favicon.ico",
      "page_rank": 0.87,
      "last_crawled": "2026-03-10"
    }

  All data needed for a search result is in ONE document.
  No JOINs at search time → Google returns results in 200ms.
  The cost? Crawling and indexing is expensive (write-heavy)
  But search is read-heavy (100,000:1 read:write ratio).
```

---

## 21. CAP Theorem

A distributed system can guarantee at most **two of three**: Consistency, Availability, Partition Tolerance.

```
              CONSISTENCY
              (all nodes see
               same data)
                  ▲
                 ╱ ╲
           CP   ╱   ╲   CA (only
        Spanner╱     ╲  single node)
        ZooKeeper     ╲
              ╱ PICK 2 ╲
             ╱           ╲
  AVAILABILITY ─────────── PARTITION TOLERANCE
  (always respond)         (survive network splits)
                  AP
              Cassandra
              DynamoDB

  Network partitions ARE inevitable → P is mandatory
  Real choice: CP (errors over stale data)
            or AP (stale data over errors)

  Banking ──► CP (wrong balance = catastrophic)
  Social  ──► AP (stale like count = acceptable)
```

### Real-Time Examples

```
EXAMPLE 1: BANKING APP — Choosing CP (Consistency + Partition Tolerance)
──────────────────────────────────────────────────────
  Scenario: You have $500 in your account.
  You withdraw $400 at ATM in NYC.
  Simultaneously, your spouse withdraws $300 at ATM in London.

  CP System (bank's choice):
    Network partition between NYC and London datacenters!
    NYC ATM: "Cannot process — unable to verify balance" ❌
    London ATM: Same error.
    Both withdrawals blocked until partition heals.

    Result: Inconvenient, but balance is NEVER wrong.
    No overdraft. No double-spending.

  If bank chose AP instead:
    NYC ATM: "Here's $400" ✅  (stale data: thinks balance = $500)
    London ATM: "Here's $300" ✅ (stale data: thinks balance = $500)
    Result: $700 withdrawn from a $500 account! 💀

EXAMPLE 2: SOCIAL MEDIA — Choosing AP (Availability + Partition Tolerance)
──────────────────────────────────────────────────────
  Scenario: Instagram post gets 1 million likes.

  User in India sees: 999,847 likes
  User in USA sees:   999,912 likes
  User in Europe sees: 999,790 likes

  All slightly different! (eventual consistency)
  Does anyone care? NO! ✅

  If Instagram chose CP instead:
    During network partition between US and India datacenters:
    Indian users: "Instagram is down" ❌
    1 billion users in Asia = major outage

  Instagram picks AP: always available, slightly stale like count.

EXAMPLE 3: REAL SYSTEMS AND THEIR CAP CHOICES
──────────────────────────────────────────────────────
  ┌─────────────────┬──────┬──────────────────────────────────┐
  │ System          │ CAP  │ Why                              │
  ├─────────────────┼──────┼──────────────────────────────────┤
  │ PostgreSQL      │ CP   │ ACID transactions, data accuracy │
  │ MongoDB         │ CP   │ Strong consistency (default)     │
  │ ZooKeeper       │ CP   │ Config must be consistent        │
  │ Google Spanner  │ CP   │ Globally consistent transactions │
  │ ─────────────── │ ──── │ ──────────────────────────────── │
  │ Cassandra       │ AP   │ Always writable, eventual sync   │
  │ DynamoDB        │ AP   │ Always available (by default)    │
  │ CouchDB         │ AP   │ Offline-first, sync later        │
  │ DNS             │ AP   │ Stale records OK (TTL-based)     │
  └─────────────────┴──────┴──────────────────────────────────┘

  Key insight: The choice isn't permanent!
  DynamoDB: AP by default, but supports "strongly consistent reads"
  MongoDB: CP by default, but supports "majority reads" (tunable)
```

> See also: [System Design Fundamentals — CAP Theorem](system-design-fundamentals.md#7-cap-theorem)

---

## 22. Blob Storage

**B**inary **L**arge **OB**ject storage for unstructured data: images, videos, backups, logs.

```
┌──────────────────────────────────────────────────────────┐
│             BLOB STORAGE ARCHITECTURE                     │
│                                                            │
│  App Server                                               │
│    │                                                       │
│    ├── Upload: PUT image.jpg ──────► ┌─────────────────┐ │
│    │                                 │ Blob Storage     │ │
│    │                                 │ (S3, Azure Blob, │ │
│    │                                 │  GCS)            │ │
│    │                                 │                  │ │
│    │   Returns: URL                  │ • Stores files   │ │
│    │   https://bucket.s3.aws.com/   │ • Replicates 3x  │ │
│    │       image.jpg                │ • 99.999999999%   │ │
│    │                                 │   durability     │ │
│    ├── Save URL in database          └─────────────────┘ │
│    │   ┌────────────────────────┐                        │
│    │   │ DB: id=42,             │  Store metadata in DB, │
│    │   │ avatar_url="https://.. │  store BLOB in object  │
│    │   └────────────────────────┘  storage. Never store  │
│    │                                BLOBs in your DB!    │
│    │                                                       │
│    ├── Serve: CDN → Blob URL ──► User downloads fast     │
└────┴──────────────────────────────────────────────────────┘

BLOB STORAGE vs FILE SYSTEM vs DATABASE:
┌────────────┬──────────────────┬─────────┬──────────────┐
│            │ Blob Storage     │ File Sys│ Database     │
├────────────┼──────────────────┼─────────┼──────────────┤
│ Scale      │ Petabytes        │ TB limit│ GB-TB        │
│ Cost       │ $0.02/GB/month   │ $$      │ $$$          │
│ Access     │ HTTP API (REST)  │ OS/NFS  │ SQL/query    │
│ Redundancy │ Built-in (3+ AZ)│ Manual  │ Replication  │
│ Best for   │ Images, video,   │ Logs,   │ Structured   │
│            │ backups, static  │ configs │ data (rows)  │
└────────────┴──────────────────┴─────────┴──────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: INSTAGRAM — 100+ Petabytes of Photos in S3
──────────────────────────────────────────────────────
  User uploads a photo:
  1. Client → Upload Service → S3 (original photo, e.g., 5MB)
  2. Image processing pipeline generates:
     ├── thumbnail   (150×150, 15KB)
     ├── small       (320×320, 50KB)
     ├── medium      (640×640, 150KB)
     └── large       (1080×1080, 300KB)
  3. All versions stored in S3:
     s3://instagram-photos/user_42/photo_123/original.jpg
     s3://instagram-photos/user_42/photo_123/thumb.jpg
     s3://instagram-photos/user_42/photo_123/medium.jpg
  4. URLs stored in PostgreSQL (NOT the actual files!)
  5. CDN (CloudFront) caches and serves the images

  Cost: S3 = $0.023/GB/month
        100 PB = ~$2.3M/month for storage alone!

EXAMPLE 2: PRE-SIGNED URLs — Secure Upload Without Your Server
──────────────────────────────────────────────────────
  Traditional (bad): Client → Your Server → S3 (server bottleneck)
  Pre-signed (good): Client → directly → S3

  Flow:
  1. Client asks your server: "I want to upload a 10MB photo"
  2. Server generates a pre-signed URL (valid for 15 min):
     https://bucket.s3.amazonaws.com/photo.jpg
       ?X-Amz-Credential=...&X-Amz-Signature=abc123
       &X-Amz-Expires=900

  3. Client uploads DIRECTLY to S3 using this URL
     → Your server never touches the file data
     → No bandwidth or CPU used on your server
     → S3 handles all the heavy lifting

  Dropbox, Slack, Discord — all use pre-signed URLs for uploads.

EXAMPLE 3: S3 STORAGE CLASSES — Cost Optimization
──────────────────────────────────────────────────────
  Netflix stores millions of video masters and transcoded files:

  ┌─────────────────────┬──────────┬──────────────────────┐
  │ Storage Class       │ $/GB/mo  │ Use Case             │
  ├─────────────────────┼──────────┼──────────────────────┤
  │ S3 Standard         │ $0.023   │ Active content       │
  │ S3 Infrequent (IA)  │ $0.0125  │ Older videos rarely  │
  │                     │          │ watched              │
  │ S3 Glacier Instant  │ $0.004   │ Backups, compliance  │
  │ S3 Glacier Deep     │ $0.00099 │ 7-year legal archive │
  └─────────────────────┴──────────┴──────────────────────┘

  Netflix auto-moves videos based on popularity:
    New release → Standard (heavily streamed)
    After 1 year → IA (still available, cheaper)
    After 3 years → Glacier (rarely watched, 95% cheaper)
```

---

## 23. CDN (Content Delivery Network)

A network of **edge servers** around the world that cache and serve content close to users.

```
WITHOUT CDN:
  User in Tokyo ───(200ms)──► Origin Server in Virginia
  Every request crosses the Pacific! Slow for images, JS, CSS.

WITH CDN:
  User in Tokyo ───(5ms)──► CDN Edge in Tokyo ── HIT ──► serve!
                                                └─ MISS ─► Origin
                                                           (200ms)
                                                           ↓
                                                     cache at edge

CDN ARCHITECTURE:
                    ┌─────────────────────┐
                    │   Origin Server     │
                    │   (Virginia, USA)    │
                    └──────────┬──────────┘
                               │
          ┌────────────────────┼────────────────────┐
          ▼                    ▼                    ▼
  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
  │ CDN Edge     │    │ CDN Edge     │    │ CDN Edge     │
  │ Tokyo        │    │ London       │    │ São Paulo    │
  │ Serves Asia  │    │ Serves EU    │    │ Serves LATAM │
  └──────────────┘    └──────────────┘    └──────────────┘
        ▲                    ▲                    ▲
     5ms latency          10ms               15ms
    Japanese users       UK users          Brazilian users

WHAT CDNs CACHE:
  ✅ Static: images, CSS, JS, fonts, videos
  ✅ API responses (with Cache-Control headers)
  ✅ Full HTML pages (static sites)
  ❌ Dynamic, personalized content (usually)

  Examples: Cloudflare, AWS CloudFront, Akamai, Fastly
```

### Real-Time Examples

```
EXAMPLE 1: NETFLIX — CDN Called Open Connect
──────────────────────────────────────────────────────
  Netflix built its OWN CDN (Open Connect):

  Problem: Streaming 4K video to 250M+ users worldwide.
  At peak, Netflix = 15% of ALL internet traffic globally!

  Solution: Netflix places physical servers (Open Connect
  Appliances) INSIDE ISP data centers:

  ┌──────────────────────────────────────────────────┐
  │  Jio's Data Center (India):                       │
  │                                                    │
  │  ┌────────────────────────┐                       │
  │  │ Netflix OCA Server     │                       │
  │  │ 100TB of popular       │                       │
  │  │ Indian content         │                       │
  │  │ "Sacred Games", etc.   │                       │
  │  └──────────┬─────────────┘                       │
  │             │ 1ms latency                         │
  │             ▼                                     │
  │  ┌──────────────────┐                             │
  │  │ Jio subscribers  │  ← content served from     │
  │  │ watching Netflix │    INSIDE their own ISP!    │
  │  └──────────────────┘                             │
  └──────────────────────────────────────────────────┘

  Result: Zero hop across the internet for popular content.
  Netflix pre-loads popular shows during off-peak hours.

EXAMPLE 2: SHOPIFY — CDN FOR E-COMMERCE PERFORMANCE
──────────────────────────────────────────────────────
  A Shopify store with 1000 product images:

  Without CDN:
    Customer in Australia → Shopify server in Canada
    Each product image: 200ms latency × 20 images on page
    Total: 4 seconds to load product grid ❌ (user leaves)

  With Shopify's CDN (Cloudflare + Fastly):
    Images cached at 200+ edge locations worldwide
    Customer in Australia → Sydney edge server → 10ms
    All 20 images: 200ms total ✅ (instant feel)

  Shopify reports: 1 second faster page load = 7% more conversions

EXAMPLE 3: CACHE INVALIDATION — The Hardest Problem
──────────────────────────────────────────────────────
  "There are only two hard things in computer science:
   cache invalidation and naming things." — Phil Karlton

  Problem: You update a product price from $99 to $79.
  CDN still serves the cached page with $99 for hours!

  Solutions:
  1. Short TTL: Cache for only 60 seconds (more origin hits)
  2. Versioned URLs: /style.v2.css → new URL = instant update ✅
  3. Purge API: Tell CDN "delete this cached page NOW"
     curl -X POST https://api.cloudflare.com/purge
       --data '{"files":["https://shop.com/product/123"]}'
  4. Stale-while-revalidate: Serve stale, fetch fresh in background

  Netflix uses TTL=0 for the catalog API (always fresh)
  but TTL=24h for video thumbnails (rarely change).
```

---

## 24. WebSockets

A protocol for **persistent, bidirectional** real-time communication between client and server.

```
HTTP (Request-Response):
  Client ── request ──►  Server     Client must poll
  Client ◄── response ── Server     for new data (wasteful)

WebSocket (Persistent, Full-Duplex):
  Client ══════════════ Server      Persistent connection!
         ◄── message ──             Server can push anytime
         ── message ──►             Client can send anytime
         ◄── message ──             No polling overhead

HOW WEBSOCKET CONNECTION STARTS:
  Client ── HTTP Upgrade Request ──► Server
            "Upgrade: websocket"
            "Connection: Upgrade"

  Server ── 101 Switching Protocols ──► Client
            Connection upgraded!

  ═══════ WebSocket frames (binary/text) ═══════

USE CASES:
┌─────────────────────┬────────────────────────────────┐
│ Use Case            │ Why WebSocket?                 │
├─────────────────────┼────────────────────────────────┤
│ Chat (Slack, Teams) │ Instant message delivery       │
│ Live scores         │ Real-time updates              │
│ Stock tickers       │ Price updates every millisecond│
│ Online gaming       │ Player position sync           │
│ Collaborative editing│ Google Docs cursors           │
│ Notifications       │ Push without polling           │
└─────────────────────┴────────────────────────────────┘

HTTP Polling vs SSE vs WebSocket:
┌─────────────┬──────────────────┬───────────┬──────────────┐
│             │ HTTP Polling     │ SSE       │ WebSocket    │
├─────────────┼──────────────────┼───────────┼──────────────┤
│ Direction   │ Client→Server    │ Server→   │ Bidirectional│
│             │ (repeated)       │ Client    │              │
│ Connection  │ New each time    │ Persistent│ Persistent   │
│ Overhead    │ High (headers)   │ Low       │ Lowest       │
│ Use for     │ Simple/legacy    │ News feeds│ Chat, gaming │
└─────────────┴──────────────────┴───────────┴──────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: SLACK — Millions of Concurrent WebSocket Connections
──────────────────────────────────────────────────────
  When you open Slack, your client establishes a WebSocket:

  wss://wss-primary.slack.com/websocket

  Through this ONE connection, Slack pushes:
    • New messages in any channel you're in
    • "Alice is typing..." indicators
    • Emoji reactions appearing in real-time
    • Channel join/leave notifications
    • Presence updates (online/offline dots)

  Slack's challenge: 10+ million concurrent WebSocket connections
  Solution: Gateway fleet of 100s of servers
    Each server handles ~100K connections
    Redis Pub/Sub routes messages to the right gateway

  If WebSocket disconnects (bad WiFi):
    Client auto-reconnects + fetches missed messages via REST API
    "Last message I saw was at timestamp X, give me everything after"

EXAMPLE 2: UBER — Real-Time Driver Tracking
──────────────────────────────────────────────────────
  After you book a ride, you see the car moving on the map.
  This is NOT polling. It's WebSocket + location streaming:

  Driver App:
    Every 4 seconds → send GPS coords via WebSocket
    { "lat": 40.7128, "lng": -74.0060, "heading": 270 }

  Uber's Server:
    Receives location → updates Redis geospatial index
    Routes location to rider's WebSocket connection

  Rider App:
    Receives driver location via WebSocket → animates car on map
    Smooth animation: client interpolates between 4-second updates

  Scale: 5 million active drivers × 1 update every 4 seconds
         = 1.25 million location messages per second through
         WebSocket infrastructure!

EXAMPLE 3: BINANCE — Crypto Trading WebSocket Streams
──────────────────────────────────────────────────────
  Stock/crypto trading needs sub-second price updates:

  REST approach (polling every second):
    GET /api/price?symbol=BTCUSDT  → 1 request/sec × 1M traders
    = 1 million HTTP requests/sec to check ONE price ❌

  WebSocket approach (Binance actually uses this):
    SUBSCRIBE to wss://stream.binance.com/ws/btcusdt@trade
    Server pushes every trade in real-time:
    { "price": "67542.30", "qty": "0.5", "time": 1710000000 }

    1 million traders receive the same price update
    via 1 million WebSocket connections
    but server only needs to COMPUTE the price ONCE
    and fan it out → much more efficient than 1M HTTP requests

  Binance handles 100+ million WebSocket messages per second.
```

---

## 25. Webhooks

A **server-to-server callback** — "don't call us, we'll call you." The server sends an HTTP POST to your URL when an event occurs.

```
POLLING (you keep asking):
  Your App ── "Any new payment?" ──► Stripe     every 5 seconds
  Your App ── "Any new payment?" ──► Stripe     99% of the time:
  Your App ── "Any new payment?" ──► Stripe     "Nope, nothing."
  Your App ── "Any new payment?" ──► Stripe     Wasteful! ❌

WEBHOOK (they tell you):
  Stripe ── "Payment received!" ──► Your App    Only when event
  POST https://yourapp.com/webhooks/stripe       happens! ✅
  {
    "event": "payment.completed",
    "data": { "amount": 9900, "customer": "cus_42" }
  }

WEBHOOK ARCHITECTURE:
  ┌──────────┐  event occurs   ┌───────────────────────┐
  │  Stripe  │────────────────►│ Your Webhook Endpoint │
  │          │  POST /webhooks │ /webhooks/stripe      │
  │  GitHub  │────────────────►│ /webhooks/github      │
  │          │  POST /webhooks │                       │
  └──────────┘                 │ • Verify signature    │
                               │ • Process event       │
                               │ • Return 200 OK       │
                               │ • (async processing)  │
                               └───────────────────────┘
  BEST PRACTICES:
  • Verify webhook signature (HMAC) — prevent spoofing
  • Respond 200 quickly, process async — prevent timeouts
  • Handle duplicates (idempotent) — webhooks may retry
  • Log all incoming webhooks for debugging
```

### Real-Time Examples

```
EXAMPLE 1: STRIPE — Payment Webhook (How Shopify Knows You Paid)
──────────────────────────────────────────────────────
  Customer buys shoes on Shopify for $120:

  1. Shopify → Stripe API: "Charge $120"
  2. Stripe processes payment (may take 2-30 seconds)
  3. Stripe sends webhook to Shopify:

     POST https://myshop.shopify.com/webhooks/stripe
     Stripe-Signature: t=1710000000,v1=abc123...
     {
       "type": "payment_intent.succeeded",
       "data": {
         "amount": 12000,
         "currency": "usd",
         "customer": "cus_abc123",
         "metadata": { "order_id": "ORD-456" }
       }
     }

  4. Shopify verifies signature (prevents fake webhooks)
  5. Shopify marks order as "Paid" → triggers fulfillment

  Without webhook: Shopify would poll Stripe every second
  asking "Is the payment done yet?" → 99% of calls wasted.

EXAMPLE 2: GITHUB — CI/CD Pipeline Triggered by Webhook
──────────────────────────────────────────────────────
  Developer pushes code to GitHub:

  GitHub sends webhook to Jenkins/CircleCI/GitHub Actions:

  POST https://ci.mycompany.com/webhooks/github
  {
    "event": "push",
    "ref": "refs/heads/main",
    "commits": [
      { "id": "abc123", "message": "Fix login bug" }
    ],
    "repository": { "full_name": "mycompany/backend" }
  }

  CI/CD pipeline automatically:
    1. Pulls latest code
    2. Runs tests
    3. Builds Docker image
    4. Deploys to staging
    5. Posts result back to GitHub PR as status check

  Every GitHub PR you see with "✅ All checks passed" is
  triggered by webhooks — no polling needed.

EXAMPLE 3: TWILIO — SMS Delivery Webhook
──────────────────────────────────────────────────────
  Your app sends an OTP SMS via Twilio:

  1. Your app → Twilio API: "Send OTP 123456 to +1-555-0123"
  2. Twilio sends the SMS (takes 1-30 seconds to deliver)
  3. Phone receives SMS → Twilio gets delivery confirmation
  4. Twilio sends webhook to your app:

     POST https://yourapp.com/webhooks/twilio
     {
       "MessageSid": "SM123",
       "MessageStatus": "delivered",
       "To": "+15550123",
       "SentTimestamp": "1710000000"
     }

  5. Your app updates: "OTP delivered to user's phone" ✅

  If SMS fails (invalid number):
     "MessageStatus": "undeliverable"
     Your app: "Please check your phone number" ⚠️

  Webhook retries: If your server is down, Twilio retries
  up to 3 times with exponential backoff (1s, 5s, 30s).
```

---

## 26. Microservices

An architecture where the application is split into **small, independent services**, each owning its data and deployable separately.

```
MONOLITH → MICROSERVICES:

  ┌────────────────────────────┐      ┌──────┐ ┌──────┐ ┌──────┐
  │ [Auth][Order][Pay][Ship]   │      │ Auth │ │Order │ │ Pay  │
  │      ONE deployment        │ ──►  │ Svc  │ │ Svc  │ │ Svc  │
  │      ONE database          │      │ ┌──┐ │ │ ┌──┐ │ │ ┌──┐ │
  │      ONE team              │      │ │DB│ │ │ │DB│ │ │ │DB│ │
  └────────────────────────────┘      │ └──┘ │ │ └──┘ │ │ └──┘ │
                                      └──────┘ └──────┘ └──────┘
                                      Independent teams & deploys

INTER-SERVICE COMMUNICATION:
  ┌──────────────────────────────────────────────────────┐
  │ SYNCHRONOUS (request-response):                      │
  │   REST: Order Svc ──GET /users/42──► Auth Svc       │
  │   gRPC: Order Svc ──binary RPC──► Shipping Svc     │
  │                                                      │
  │ ASYNCHRONOUS (event-driven):                        │
  │   Order Svc ──"OrderPlaced"──► Kafka ──► Pay Svc   │
  │                                       ──► Ship Svc │
  │   Loose coupling, better resilience                 │
  └──────────────────────────────────────────────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: AMAZON — From Monolith to 1000+ Microservices
──────────────────────────────────────────────────────
  In 2001, Amazon was a monolith. One codebase, one database.
  Deploy took hours. One bug could crash the entire website.

  Jeff Bezos's famous "API Mandate" (2002):
    "All teams will expose their data through service APIs.
    There will be no other form of inter-process communication.
    Anyone who doesn't do this will be fired."

  Today Amazon has 1000+ microservices:
    • Product Catalog Service
    • Recommendation Engine
    • Shopping Cart Service
    • Order Service
    • Payment Service
    • Shipping Service
    • Review Service
    • Search Service (each with its own database!)

  Each team owns their microservice end-to-end:
    Team writes code → tests → deploys → monitors → on-call
    Deploy independently: 150,000 deployments per DAY

EXAMPLE 2: UBER — Microservice Architecture
──────────────────────────────────────────────────────
  ┌──────────────────────────────────────────────────────┐
  │  When you request a ride, 10+ services activate:     │
  │                                                       │
  │  1. User Service       → authenticate your account   │
  │  2. Location Service   → get your GPS coordinates    │
  │  3. Pricing Service    → calculate fare + surge      │
  │  4. Matching Service   → find nearest available driver│
  │  5. Notification Svc   → push notification to driver │
  │  6. Payment Service    → pre-authorize your card     │
  │  7. ETA Service        → estimate arrival time       │
  │  8. Maps Service       → compute optimal route       │
  │  9. Rating Service     → load driver rating          │
  │ 10. Fraud Service      → check for suspicious activity│
  │                                                       │
  │  All in parallel, all in under 2 seconds!            │
  └──────────────────────────────────────────────────────┘

  If Pricing Service goes down:
    Other services still work → you can still book rides
    Pricing falls back to base rates (degraded but functional)

  In a monolith: pricing bug = entire Uber app crashes ❌

EXAMPLE 3: WHEN MICROSERVICES GO WRONG — Nano-Services Anti-Pattern
──────────────────────────────────────────────────────
  Common mistake: making services TOO small

  Bad: 50 microservices for a 5-person startup
    → Every "add to cart" → 12 network calls
    → Each call adds 5-10ms latency = 120ms just from network!
    → Team spends 80% time on infrastructure, 20% on features
    → Debugging a request across 12 services is a nightmare

  Right approach:
    Start with a MODULAR MONOLITH
    Split into microservices ONLY when:
      • Team size > 20 engineers (need independent deploys)
      • Different parts need different scaling (search vs orders)
      • Different parts need different tech stacks
      • Deployment coupling causes pain (can't ship independently)

  Shopify runs on a modular monolith serving $444B+ in GMV.
  It proves: monolith ≠ bad. Wrong architecture for your
  team size = bad.
```

> See also: [System Design Handbook — Architecture Patterns](system-design-handbook.md#4-architecture-patterns-monoliths-microservices-event-driven)

---

## 27. Message Queues

A buffer between producers and consumers that **decouples** services and handles traffic spikes.

```
WITHOUT QUEUE (tight coupling):
  Order Svc ──sync call──► Payment Svc    If Payment is slow,
                                          Order is slow too!
                                          If Payment is down,
                                          Order fails! ❌

WITH QUEUE (decoupled):
  Order Svc ──publish──► ┌───────────┐ ──consume──► Payment Svc
                         │  Message  │
                         │   Queue   │
                         │ (Kafka /  │
                         │  SQS /    │
                         │  RabbitMQ)│
                         └───────────┘
  Order returns immediately. Payment processes at its own pace.
  If Payment is down, messages queue up and are processed later. ✅

MESSAGE QUEUE PATTERNS:
┌─────────────────┬────────────────────────────────────────┐
│ Point-to-Point  │ One message → one consumer             │
│                 │ (task queue, job processing)            │
├─────────────────┼────────────────────────────────────────┤
│ Pub/Sub         │ One message → many consumers           │
│                 │ (event broadcasting, notifications)    │
├─────────────────┼────────────────────────────────────────┤
│ Fan-out         │ Same message to all queues/consumers   │
│                 │ (logging + analytics + audit)          │
└─────────────────┴────────────────────────────────────────┘

QUEUE PRODUCTS:
┌──────────────┬────────────────────────────────────────────┐
│ Kafka        │ High-throughput, durable, ordered, replay  │
│ RabbitMQ     │ Flexible routing, AMQP, push-based        │
│ AWS SQS      │ Managed, simple, at-least-once delivery   │
│ Redis Streams│ In-memory, fast, for simpler use cases    │
└──────────────┴────────────────────────────────────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: UBER — Order Processing with Kafka
──────────────────────────────────────────────────────
  When you book a ride, 6+ services need to react:

  Without queue:
    Ride Service calls Payment → then Notification → then ETA
    → then Analytics → then Fraud Check → one by one
    If Analytics service is slow → entire ride booking slows ❌

  With Kafka (what Uber uses):
    Ride Service publishes: "ride.requested" event
      ↓
    ┌──────────────── Kafka Topic: ride-events ─────────────┐
    │                                                        │
    │  Consumer 1: Payment Service    → pre-authorize card  │
    │  Consumer 2: Notification Svc   → push to driver      │
    │  Consumer 3: ETA Service        → calculate ETA       │
    │  Consumer 4: Analytics Service  → log metrics         │
    │  Consumer 5: Fraud Service      → check patterns      │
    │  Consumer 6: Pricing Service    → finalize fare       │
    │                                                        │
    │  ALL consume in PARALLEL — ride confirmed in < 2 sec  │
    │  If Analytics is slow → doesn't affect ride booking!  │
    └────────────────────────────────────────────────────────┘

  Uber's Kafka cluster: 1+ trillion messages/day

EXAMPLE 2: DOMINO'S — Order Status Updates
──────────────────────────────────────────────────────
  When you order pizza from Domino's:

  Order placed → "Prep started" → "In oven" → "Quality check"
  → "Out for delivery" → "Delivered"

  Each status change is an event on a message queue:

  Kitchen System → publishes "order.oven_entered"
                 → publishes "order.quality_passed"
  Delivery System → publishes "order.dispatched"
  Driver App → publishes "order.delivered"

  Consumer: Notification Service
    → Sends push notification for each event
    "Your pizza is in the oven! 🍕"
    "John is on his way with your order! 🚗"

  Without queue: Kitchen system must directly call
  notification system → tight coupling → if notification
  system is down, kitchen tracking breaks ❌

EXAMPLE 3: CHOOSING THE RIGHT QUEUE
──────────────────────────────────────────────────────
  ┌──────────────────────────────────────────────────────────┐
  │ Need message replay? (re-process old events)             │
  │   YES → Kafka ✅ (stores messages for days/weeks)        │
  │   NO  → SQS or RabbitMQ                                 │
  │                                                           │
  │ Need complex routing? (route by content/headers)         │
  │   YES → RabbitMQ ✅ (exchanges, bindings, routing keys)  │
  │   NO  → Kafka or SQS                                    │
  │                                                           │
  │ Need simplicity? (minimal ops, managed service)          │
  │   YES → AWS SQS ✅ (zero infrastructure to manage)      │
  │   NO  → Kafka or RabbitMQ                                │
  │                                                           │
  │ Need ordering? (messages processed in exact order)       │
  │   YES → Kafka ✅ (ordering per partition guaranteed)     │
  │   Partial → SQS FIFO (ordering per message group)        │
  │   NO  → SQS Standard, RabbitMQ                          │
  │                                                           │
  │ Need 1M+ messages/sec throughput?                        │
  │   YES → Kafka ✅ (designed for this)                     │
  │   NO  → Any of the above                                │
  └──────────────────────────────────────────────────────────┘

  LinkedIn (Kafka's creator): 7+ trillion messages/day
  Slack: SQS + Kafka (SQS for simple tasks, Kafka for events)
  Netflix: Kafka for ALL inter-service communication
```

> See also: [Apache Kafka Deep Dive](kafka.md)

---

## 28. Rate Limiting

Control how many requests a client can make in a time window to **protect your system** from abuse and overload.

```
WITHOUT RATE LIMITING:
  Malicious user sends 100,000 req/sec ──► Server crashes ❌

WITH RATE LIMITING:
  Malicious user sends 100,000 req/sec ──► Rate Limiter
    ├── Allow 100 req/sec ──► Server (healthy ✅)
    └── Reject 99,900 ──► 429 Too Many Requests

ALGORITHMS:
┌─────────────────┬──────────────────────────────────────────┐
│ Token Bucket    │ Tokens refill at steady rate. Each req   │
│                 │ consumes a token. No token = reject.     │
│                 │ Allows controlled bursts. Most popular.  │
├─────────────────┼──────────────────────────────────────────┤
│ Sliding Window  │ Count requests in a rolling time window. │
│                 │ Smooth, no boundary burst problem.       │
├─────────────────┼──────────────────────────────────────────┤
│ Fixed Window    │ Count per fixed interval (e.g., per min).│
│                 │ Simple but 2x burst at boundary.         │
├─────────────────┼──────────────────────────────────────────┤
│ Leaky Bucket    │ Requests processed at constant rate.     │
│                 │ Excess requests queue or are dropped.    │
└─────────────────┴──────────────────────────────────────────┘

HTTP RESPONSE WHEN LIMITED:
  HTTP/1.1 429 Too Many Requests
  Retry-After: 30
  X-RateLimit-Limit: 100
  X-RateLimit-Remaining: 0
  X-RateLimit-Reset: 1710000000
```

### Real-Time Examples

```
EXAMPLE 1: GITHUB API — Real Rate Limiting in Action
──────────────────────────────────────────────────────
  Try calling GitHub's API repeatedly:

  curl -H "Authorization: Bearer YOUR_TOKEN" \
       https://api.github.com/users/octocat

  Response headers:
    X-RateLimit-Limit:     5000      ← max requests per hour
    X-RateLimit-Remaining: 4998      ← requests left
    X-RateLimit-Reset:     1710003600 ← Unix timestamp when limit resets

  After 5000 requests in one hour:
    HTTP 403 Forbidden
    { "message": "API rate limit exceeded for user." }

  Why? Without limits, a bot could:
    • Scrape ALL public repos (millions of requests)
    • Overload GitHub's servers
    • Degrade experience for other users

EXAMPLE 2: CLOUDFLARE — DDoS Protection via Rate Limiting
──────────────────────────────────────────────────────
  A DDoS attack hits your website: 10 million requests/sec!

  Without rate limiting:
    All 10M requests reach your server → server crashes
    Your website goes down for all users ❌

  With Cloudflare rate limiting:
    Rule: "Max 100 requests per 10 seconds per IP"

    Legitimate user (2 req/sec):  ALLOWED ✅
    Bot farm (50,000 req/sec per IP): BLOCKED after 100 ❌

    Cloudflare handles the flood at their edge network.
    Your server sees only legitimate traffic.

  Real case: Cloudflare blocked a 71 million request/second
  DDoS attack in 2023 — the largest ever recorded.

EXAMPLE 3: TOKEN BUCKET — How Most Rate Limiters Work
──────────────────────────────────────────────────────
  Twitter/X API uses Token Bucket for tweet posting:
    Rate: 300 tweets per 3-hour window

  ┌───────────────────────────────────────────────┐
  │ BUCKET (capacity = 300 tokens)                 │
  │                                                 │
  │ ████████████████████  (300 tokens full)         │
  │                                                 │
  │ Refill rate: 100 tokens per hour                │
  │                                                 │
  │ User tweets: 1 token consumed                   │
  │ User tweets 50 times rapidly: 50 tokens gone    │
  │   → Still 250 tokens left (burst OK!)           │
  │                                                 │
  │ User scripts 300 tweets in 1 minute:            │
  │   → Bucket empty! Next tweet rejected. ❌       │
  │   → Must wait for refill (100 tokens/hour)      │
  │                                                 │
  │ Key: allows BURSTS but controls sustained rate  │
  └───────────────────────────────────────────────┘

  vs FIXED WINDOW (simpler but has edge problem):
    Window: 12:00-13:00 → 100 requests allowed
    User sends 100 at 12:59 + 100 at 13:00 = 200 in 1 minute!
    Token Bucket prevents this because tokens don't "reset."
```

> See also: [Rate Limiting Deep Dive](rate-limiting.md)

---

## 29. API Gateways

A **single entry point** for all API calls — handles cross-cutting concerns like auth, rate limiting, and routing.

```
WITHOUT API GATEWAY:
  Mobile ──► Auth Service       Each client must know
  Mobile ──► Order Service      every service URL,
  Mobile ──► Payment Service    handle auth, retries, etc.
  Web    ──► Auth Service       Duplicated logic! ❌
  Web    ──► Order Service

WITH API GATEWAY:
  Mobile ──┐                    ┌── Auth Service
  Web    ──┼──► ┌────────────┐  ├── Order Service
  IoT    ──┘    │ API Gateway│──┤── Payment Service
                │            │  ├── Shipping Service
                │ • Auth     │  └── Notification Svc
                │ • Rate limit│
                │ • Routing   │
                │ • Logging   │
                │ • SSL term. │
                │ • Transform │
                │ • Caching   │
                └────────────┘

API GATEWAY PRODUCTS:
┌──────────────────┬────────────────────────────────┐
│ Kong             │ Open-source, plugin ecosystem  │
│ AWS API Gateway  │ Managed, integrates with Lambda│
│ Nginx            │ High-performance, flexible     │
│ Envoy            │ Service mesh sidecar proxy     │
│ Spring Cloud GW  │ Java/Spring ecosystem          │
│ Azure API Mgmt   │ Full lifecycle management      │
└──────────────────┴────────────────────────────────┘

  API Gateway vs Load Balancer:
  ┌──────────────────────────────────────────────────────┐
  │ API Gateway:  application-level routing + features   │
  │               (auth, transform, rate limit, caching) │
  │                                                       │
  │ Load Balancer: traffic distribution to same service  │
  │                (health checks, least connections)     │
  │                                                       │
  │ In practice: Client → API Gateway → LB → Servers    │
  └──────────────────────────────────────────────────────┘
```

### Real-Time Examples

```
EXAMPLE 1: NETFLIX — Zuul API Gateway
──────────────────────────────────────────────────────
  Netflix built Zuul (open-source API gateway):

  Every Netflix request (250M+ users) passes through Zuul:

  Mobile App ──► Zuul Gateway ──► Backend Services
                 │
                 ├── Authentication: verify JWT token
                 ├── Rate limiting: 100 req/sec per user
                 ├── Request routing: /api/catalog → Catalog Service
                 │                    /api/streaming → Stream Service
                 ├── Canary testing: 5% of users → new version
                 ├── Load shedding: if backend overloaded → 503
                 ├── Request logging: trace ID for debugging
                 └── Response compression: gzip for mobile

  Zuul handles 100+ billion API requests per day.
  When Zuul detects a backend service failing:
    Automatically routes away from unhealthy instances
    Returns cached response or graceful degradation

EXAMPLE 2: AMAZON API GATEWAY — Serverless Backend
──────────────────────────────────────────────────────
  Build an API without managing ANY servers:

  Mobile App ──► AWS API Gateway ──► Lambda Function ──► DynamoDB
                 │
                 ├── Auto-scales from 0 to millions of requests
                 ├── $3.50 per million API calls (pay-per-use)
                 ├── Built-in throttling (10,000 req/sec default)
                 ├── API key management for external developers
                 ├── Request/response transformation
                 └── Swagger/OpenAPI documentation auto-generated

  Real example: A startup serving 1M API calls/day
    Cost: 1M × 30 days × $3.50/1M = $105/month
    No servers to manage, no scaling to configure.

EXAMPLE 3: BFF PATTERN — Backend for Frontend
──────────────────────────────────────────────────────
  Problem: Mobile and Web need different data formats

  Mobile app (small screen, limited bandwidth):
    Needs: { "name": "iPhone", "price": 999, "thumb_url": "..." }

  Web app (large screen, fast network):
    Needs: { "name": "iPhone", "price": 999, "images": [...],
             "specs": {...}, "reviews": [...], "related": [...] }

  Solution: Separate API Gateways per client type

  ┌─────────────┐     ┌──────────────────┐
  │ Mobile App  │────►│ Mobile BFF       │──► Product Service
  │             │     │ (lightweight API) │──► Image Service
  └─────────────┘     └──────────────────┘
  ┌─────────────┐     ┌──────────────────┐
  │ Web App     │────►│ Web BFF          │──► Product Service
  │             │     │ (rich API)       │──► Review Service
  └─────────────┘     └──────────────────┘──► Recommendation Svc

  Spotify, SoundCloud, Netflix — all use BFF pattern.
  Each client type gets an optimized API experience.
```

---

## 30. Idempotency

An operation is **idempotent** if doing it once or multiple times produces the **same result**. Critical for safe retries.

```
WHY IDEMPOTENCY MATTERS:

  Client ──"Pay $100"──► Server ── processes ── ✅ done
  Client ◄── response ── ... NETWORK ERROR! ❌ never received

  Client doesn't know if payment went through.
  What does client do? RETRY!

  WITHOUT IDEMPOTENCY:
    Retry ──"Pay $100"──► Server ── processes AGAIN ── ✅
    Customer charged $200! ❌❌

  WITH IDEMPOTENCY:
    Retry ──"Pay $100" (idempotency_key=abc123)──► Server
    Server: "I already processed abc123. Here's the result."
    Customer charged $100. ✅

HOW IT WORKS:
┌────────────────────────────────────────────────────────┐
│ 1. Client generates unique idempotency key             │
│                                                         │
│ 2. POST /payments                                       │
│    Idempotency-Key: abc-123-def                         │
│    { "amount": 100, "currency": "USD" }                 │
│                                                         │
│ 3. Server checks: has abc-123-def been processed?       │
│    ├── NO  → process payment, store key + result        │
│    └── YES → return stored result (no re-processing)    │
│                                                         │
│ 4. Same key, same result — no matter how many retries   │
└────────────────────────────────────────────────────────┘

IDEMPOTENT HTTP METHODS:
┌──────────┬─────────────┬──────────────────────────────┐
│ Method   │ Idempotent? │ Why                          │
├──────────┼─────────────┼──────────────────────────────┤
│ GET      │ ✅ Yes      │ Reading doesn't change state │
│ PUT      │ ✅ Yes      │ Same full replace = same     │
│ DELETE   │ ✅ Yes      │ Deleting twice = still gone  │
│ POST     │ ❌ No       │ Creates new resource each    │
│ PATCH    │ ❌ No*      │ Depends on implementation    │
└──────────┴─────────────┴──────────────────────────────┘
  * PATCH can be made idempotent with careful design

REAL-WORLD EXAMPLES:
  Stripe:  Idempotency-Key header on every payment API
  AWS:     ClientToken on CreateInstance (prevents dup VMs)
  Kafka:   enable.idempotence=true (dedup producer retries)
```

### Real-Time Examples

```
EXAMPLE 1: STRIPE — How $1 Trillion in Payments Stays Safe
──────────────────────────────────────────────────────
  Customer clicks "Pay $50" → network timeout → customer clicks again:

  WITHOUT idempotency:
    Request 1: POST /charges { amount: 5000 }  → charged $50 ✅
    Request 2: POST /charges { amount: 5000 }  → charged $50 AGAIN ❌
    Customer lost $100!

  WITH Stripe's idempotency:
    Request 1: POST /charges
               Idempotency-Key: "order-123-abc"
               { amount: 5000 }
               → Server: new key, process payment → $50 charged ✅
               → Store: "order-123-abc" → { status: "succeeded" }

    Request 2: POST /charges (SAME key due to retry)
               Idempotency-Key: "order-123-abc"
               → Server: "I already processed order-123-abc"
               → Return cached result → $50 (no new charge) ✅

  Stripe stores idempotency keys for 24 hours.
  After 24 hours, same key = treated as new request.

EXAMPLE 2: AMAZON ORDER PLACEMENT — Preventing Double Orders
──────────────────────────────────────────────────────
  Scenario: You click "Place Order" twice by accident.

  Amazon's approach:
  ┌────────────────────────────────────────────────────┐
  │ 1. Client generates order token: "tok_xyz789"      │
  │                                                     │
  │ 2. Request 1: POST /orders                         │
  │    X-Order-Token: tok_xyz789                       │
  │    { items: [...], address: {...} }                │
  │    → Server: new token → create order → return 201 │
  │    → INSERT into orders_idempotency (token, order_id)│
  │                                                     │
  │ 3. Request 2: POST /orders (accidental retry)      │
  │    X-Order-Token: tok_xyz789  (SAME token)         │
  │    → Server: token exists! → return existing order │
  │    → No new order created ✅                       │
  │                                                     │
  │ 4. New order: POST /orders                         │
  │    X-Order-Token: tok_abc456  (NEW token)          │
  │    → Server: new token → create new order ✅       │
  └────────────────────────────────────────────────────┘

EXAMPLE 3: KAFKA PRODUCER — Exactly-Once Delivery
──────────────────────────────────────────────────────
  Problem: Producer sends message → network error → retries
           Message might be written to Kafka TWICE!

  Kafka's idempotent producer:
    enable.idempotence=true

  How it works:
    Each producer gets a unique Producer ID (PID)
    Each message gets a sequence number: PID + seq

    Message 1: PID=42, seq=1 → Kafka writes → ACK sent
    Message 1: PID=42, seq=1 → retry (ACK was lost)
    Kafka: "I already have PID=42, seq=1 — skip!" ✅

    Message 2: PID=42, seq=2 → Kafka writes → new message ✅

  Without idempotency: "at-least-once" (may have duplicates)
  With idempotency: "exactly-once" (guaranteed unique) ✅

  LinkedIn processes 7+ trillion messages/day with
  Kafka idempotent producers — zero duplicates.
```

---

## 30-Day Study Plan

```
┌──────┬───────────────────────────────────────────────────┐
│ Day  │ Concepts to Study                                │
├──────┼───────────────────────────────────────────────────┤
│ 1-2  │ Client-Server, IP Address, DNS                   │
│ 3-4  │ Proxy/Reverse Proxy, Latency, HTTP/HTTPS         │
│ 5-6  │ APIs, REST API, GraphQL                          │
│ 7    │ Review Week 1 — draw diagrams from memory        │
├──────┼───────────────────────────────────────────────────┤
│ 8-9  │ Databases, SQL vs NoSQL                          │
│ 10   │ Vertical vs Horizontal Scaling                   │
│ 11   │ Load Balancers, Database Indexing                │
│ 12-13│ Replication, Sharding, Vertical Partitioning     │
│ 14   │ Review Week 2 — design a URL shortener           │
├──────┼───────────────────────────────────────────────────┤
│ 15-16│ Caching, Denormalization                         │
│ 17   │ CAP Theorem (deeply — practice trade-offs)       │
│ 18-19│ Blob Storage, CDN, WebSockets                    │
│ 20-21│ Webhooks, Microservices                          │
│ 22   │ Review Week 3 — design a chat system             │
├──────┼───────────────────────────────────────────────────┤
│ 23-24│ Message Queues, Rate Limiting                    │
│ 25-26│ API Gateways, Idempotency                       │
│ 27-28│ Practice: Design Instagram / Uber / Netflix      │
│ 29-30│ Review ALL concepts — whiteboard full systems    │
└──────┴───────────────────────────────────────────────────┘
```

---

## Further Reading

| Topic | Document |
|-------|----------|
| [System Design Fundamentals](system-design-fundamentals.md) | Deep dive: Scalability, Availability, Reliability, CAP, Latency, Throughput |
| [System Design Handbook](system-design-handbook.md) | Architecture Patterns, Interview Framework, 15-Concept Cheat Sheet |
| [Load Balancing](load-balancing.md) | Algorithms, Consistent Hashing, Health Checks, Real-World Architectures |
| [Databases](databases.md) | SQL vs NoSQL, Replication, Sharding, Indexing |
| [Caching Strategies](caching-strategies.md) | Write-Through/Back, LRU/LFU, Eviction Policies |
| [Rate Limiting](rate-limiting.md) | Token Bucket, Sliding Window, Distributed Rate Limiting |
| [Apache Kafka](kafka.md) | Topics, Partitions, Consumers, Exactly-Once Semantics |
| [Senior Java Interview](senior-java-interview.md) | 20 Production-Grade Questions with Diagrams |

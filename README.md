# Coding Interview Patterns - Java

A comprehensive Java project covering **20 essential coding interview patterns** + **7 core data structure & system design topics**, each with **30 examples** (10 Easy, 10 Medium, 10 Hard) — **810 total coding solutions**, plus **SOLID design principles** and **15 Gang-of-Four design patterns** with clear examples.

## Project Structure

```
coding-interview-patterns/
├── pom.xml
├── README.md
├── docs/
│   ├── system-design-fundamentals.md  # Scalability, Availability, CAP
│   ├── load-balancing.md              # LB Algorithms, Consistent Hashing
│   ├── databases.md                   # SQL vs NoSQL, Replication, Sharding
│   ├── caching-strategies.md           # Write-Through/Back, LRU, LFU, Eviction
│   ├── kafka.md                       # Master Kafka in 8 Steps
│   └── rate-limiting.md               # Rate Limiting Deep Dive
└── src/main/java/patterns/
    ├── prefixsum/               # Pattern 1:  Prefix Sum
    ├── twopointers/             # Pattern 2:  Two Pointers
    ├── slidingwindow/           # Pattern 3:  Sliding Window
    ├── fastslowpointers/        # Pattern 4:  Fast & Slow Pointers
    ├── linkedlistreversal/      # Pattern 5:  LinkedList In-place Reversal
    ├── frequencycounting/       # Pattern 6:  Frequency Counting
    ├── monotonicstack/          # Pattern 7:  Monotonic Stack
    ├── bitmanipulation/         # Pattern 8:  Bit Manipulation
    ├── topkelements/            # Pattern 9:  Top 'K' Elements
    ├── overlappingintervals/    # Pattern 10: Overlapping Intervals
    ├── binarysearch/            # Pattern 11: Binary Search Variants
    ├── binarytreetraversal/     # Pattern 12: Binary Tree Traversal
    ├── dfs/                     # Pattern 13: Depth-First Search (DFS)
    ├── bfs/                     # Pattern 14: Breadth-First Search (BFS)
    ├── shortestpath/            # Pattern 15: Shortest Path
    ├── matrixtraversal/         # Pattern 16: Matrix Traversal
    ├── backtracking/            # Pattern 17: Backtracking
    ├── trie/                    # Pattern 18: Prefix Search (Trie)
    ├── greedy/                  # Pattern 19: Greedy
    ├── dynamicprogramming/      # Pattern 20: Dynamic Programming
    ├── arrays/                  # Data Structure: Arrays
    ├── strings/                 # Data Structure: Strings
    ├── linkedlists/             # Data Structure: Linked Lists
    ├── stacks/                  # Data Structure: Stacks
    ├── queues/                  # Data Structure: Queues
    ├── cachingstrategies/       # System Design: Caching Strategies
    ├── ratelimiting/            # System Design: Rate Limiting
    ├── solid/                   # SOLID Design Principles
    └── designpatterns/          # Top 15 Design Patterns (GoF)
```

## Pattern Summary

| # | Pattern | When to Use | Key Idea | Time |
|---|---------|-------------|----------|------|
| 1 | **Prefix Sum** | Range sum queries, subarray sums | Pre-compute cumulative sums for O(1) range queries | O(n) |
| 2 | **Two Pointers** | Pair sums, palindromes, sorted arrays | Two references moving inward or at different speeds | O(n) |
| 3 | **Sliding Window** | Subarray/substring with constraint | Maintain a window that slides over data | O(n) |
| 4 | **Fast & Slow Pointers** | Cycle detection, middle of list | Two pointers at different speeds (tortoise & hare) | O(n) |
| 5 | **LinkedList Reversal** | Reverse list or sub-list | Manipulate prev/curr/next pointers in-place | O(n) |
| 6 | **Frequency Counting** | Anagrams, duplicates, majority element | Hash map to count occurrences | O(n) |
| 7 | **Monotonic Stack** | Next greater/smaller element | Stack maintaining increasing/decreasing order | O(n) |
| 8 | **Bit Manipulation** | Single number, power of 2, subsets | Bitwise operators (XOR, AND, shifts) | O(n) |
| 9 | **Top K Elements** | Kth largest, K most frequent | Heap (priority queue) of size K | O(n log k) |
| 10 | **Overlapping Intervals** | Merge intervals, meeting rooms | Sort by start, compare with previous end | O(n log n) |
| 11 | **Binary Search** | Sorted data, rotated arrays, answer space | Halve search space each iteration | O(log n) |
| 12 | **Binary Tree Traversal** | Tree operations, BST validation | Inorder / Preorder / Postorder / Level-order | O(n) |
| 13 | **DFS** | Paths, cycles, connected components | Explore depth-first with stack/recursion | O(V+E) |
| 14 | **BFS** | Shortest path (unweighted), level-order | Explore breadth-first with queue | O(V+E) |
| 15 | **Shortest Path** | Weighted graphs, network routing | Dijkstra / Bellman-Ford / Floyd-Warshall | O((V+E)logV) |
| 16 | **Matrix Traversal** | Grid problems, flood fill, spiral | DFS/BFS on 2D grid with direction arrays | O(m*n) |
| 17 | **Backtracking** | Permutations, combinations, N-Queens | Choose-Explore-Unchoose with pruning | O(2^n) |
| 18 | **Trie** | Prefix search, autocomplete, word search | Tree where each node is a character | O(L) |
| 19 | **Greedy** | Scheduling, jump games, stock trading | Make locally optimal choice at each step | O(n log n) |
| 20 | **Dynamic Programming** | Knapsack, LCS, counting paths | Store sub-problem solutions to avoid recomputation | O(n*m) |

## Examples Per Pattern

Each pattern file contains **30 examples** organized by difficulty:

| Difficulty | Count | Description |
|------------|-------|-------------|
| **Easy** | 10 | Fundamental application of the pattern |
| **Medium** | 10 | Requires combining the pattern with additional logic |
| **Hard** | 10 | Advanced problems often seen in top-tier coding interviews |

## Core Data Structure Topics (30 Examples Each)

| # | Topic | Key Techniques | Examples |
|---|-------|---------------|----------|
| 1 | **[Arrays](src/main/java/patterns/arrays/)** | Hashing, two pointers, sorting, prefix computation, in-place ops | 30 |
| 2 | **[Strings](src/main/java/patterns/strings/)** | Frequency count, sliding window, DP, pattern matching, backtracking | 30 |
| 3 | **[Linked Lists](src/main/java/patterns/linkedlists/)** | Dummy head, fast/slow pointers, in-place reversal, merge | 30 |
| 4 | **[Stacks](src/main/java/patterns/stacks/)** | Monotonic stack, expression parsing, matching, DFS simulation | 30 |
| 5 | **[Queues](src/main/java/patterns/queues/)** | BFS, deque, circular queue, priority queue, task scheduling | 30 |
| 6 | **[Caching Strategies](src/main/java/patterns/cachingstrategies/)** | LRU, LFU, FIFO, TTL, write-through/back, Bloom filter, CDN | 30 |
| 7 | **[Rate Limiting](src/main/java/patterns/ratelimiting/)** | Token bucket, leaky bucket, sliding window, adaptive, distributed | 30 |

### Run Data Structure & System Design Examples
```bash
cd src/main/java
javac patterns/arrays/ArraysPatterns.java
java patterns.arrays.ArraysPatterns

javac patterns/strings/StringsPatterns.java
java patterns.strings.StringsPatterns

javac patterns/linkedlists/LinkedListsPatterns.java
java patterns.linkedlists.LinkedListsPatterns

javac patterns/stacks/StackPatterns.java
java patterns.stacks.StackPatterns

javac patterns/queues/QueuePatterns.java
java patterns.queues.QueuePatterns

javac patterns/cachingstrategies/CachingStrategiesPatterns.java
java patterns.cachingstrategies.CachingStrategiesPatterns

javac patterns/ratelimiting/RateLimitingPatterns.java
java patterns.ratelimiting.RateLimitingPatterns
```

## SOLID Design Principles

The `solid/` package demonstrates all 5 SOLID principles with **BAD vs GOOD** code examples:

| Principle | Class | Key Rule |
|-----------|-------|----------|
| **S** — Single Responsibility | `SingleResponsibilityPrinciple` | A class should have only one reason to change |
| **O** — Open/Closed | `OpenClosedPrinciple` | Open for extension, closed for modification |
| **L** — Liskov Substitution | `LiskovSubstitutionPrinciple` | Subtypes must be substitutable for base types |
| **I** — Interface Segregation | `InterfaceSegregationPrinciple` | Don't force unused interface methods |
| **D** — Dependency Inversion | `DependencyInversionPrinciple` | Depend on abstractions, not concretions |

Each file shows a **violation** (BAD) and the **correct approach** (GOOD) with runnable `main()` methods.

## Top 15 Design Patterns (GoF)

The `designpatterns/` package implements 15 essential design patterns with real-world examples:

| # | Pattern | Category | Key Idea |
|---|---------|----------|----------|
| 1 | **Singleton** | Creational | One instance, global access |
| 2 | **Factory Method** | Creational | Delegate object creation to subclasses |
| 3 | **Builder** | Creational | Step-by-step complex object construction |
| 4 | **Adapter** | Structural | Convert incompatible interfaces |
| 5 | **Decorator** | Structural | Add responsibilities dynamically |
| 6 | **Facade** | Structural | Simplify complex subsystems |
| 7 | **Proxy** | Structural | Control access (lazy load, security, logging) |
| 8 | **Composite** | Structural | Tree structures (part-whole hierarchies) |
| 9 | **Observer** | Behavioral | Notify subscribers on state change |
| 10 | **Strategy** | Behavioral | Swap algorithms at runtime |
| 11 | **Command** | Behavioral | Encapsulate requests (undo/redo) |
| 12 | **Iterator** | Behavioral | Traverse collections uniformly |
| 13 | **State** | Behavioral | Behavior changes with internal state |
| 14 | **Template Method** | Behavioral | Algorithm skeleton with customizable steps |
| 15 | **Chain of Responsibility** | Behavioral | Pass requests along a handler chain |

Each file contains a brief description, real-world example, and runnable `main()` method.

## Technology Deep Dives

The `docs/` folder contains detailed reference guides for system design interview topics:

| Document | Topics Covered |
|----------|---------------|
| [**System Design Fundamentals**](docs/system-design-fundamentals.md) | Scalability, Availability, Reliability, Latency, Throughput, CAP Theorem, PACELC |
| [**Load Balancing**](docs/load-balancing.md) | Round Robin, Least Connections, Consistent Hashing, L4/L7, Health Checks, Real-World Architectures |
| [**Databases**](docs/databases.md) | SQL vs NoSQL, ACID vs BASE, Replication, Sharding, Indexing, Database Selection Guide |
| [**Caching Strategies**](docs/caching-strategies.md) | Write-Through, Write-Back, Write-Around, LRU, LFU, FIFO, Cache Stampede, Real-World Architectures |
| [**Kafka**](docs/kafka.md) | Messages, Topics, Partitions, Producers, Consumers, Clusters, Use Cases |
| [**Rate Limiting**](docs/rate-limiting.md) | 5 Core Algorithms, Types, Distributed Limiting, Architecture, Real-World Case Studies |
| [**Java Fundamentals**](docs/java-fundamentals.md) | If-Else, Switch, Ternary, Loops, Pattern Matching, Optional, Operators, Javadoc Conventions |
| [**Senior Java Interview**](docs/senior-java-interview.md) | 20 Real Production Questions — Multithreading, Kafka, Spring Transactions, AWS/Azure, System Design, Caching |
| [**System Design Handbook**](docs/system-design-handbook.md) | Load Balancers, Latency vs Throughput, CAP Trade-offs, Architecture Patterns (Monolith/Microservices/Event-Driven), Scalability/Reliability/Availability Checklists |
| [**System Design 30 Concepts**](docs/system-design-30-concepts.md) | Client-Server, DNS, Proxy, HTTP, REST, GraphQL, SQL/NoSQL, Scaling, Sharding, Caching, CDN, WebSockets, Webhooks, Microservices, Queues, Rate Limiting, Idempotency |
| [**System Design Interview Questions**](docs/system-design-interview-questions.md) | 15 Questions: URL Shortener, Chat App, Instagram, YouTube, Amazon, Uber, Google Drive, Web Crawler, Notification, Logging, IRCTC Train Ticketing, RedBus, BookMyShow, MakeMyTrip Hotel, Rate Limiting |

## How to Run

### Prerequisites
- Java 17+
- Maven 3.6+

### Compile
```bash
cd coding-interview-patterns
mvn compile
```

### Run any pattern
```bash
# Example: run the Two Pointers pattern
mvn exec:java -Dexec.mainClass="patterns.twopointers.TwoPointersPatterns"

# Example: run the Dynamic Programming pattern
mvn exec:java -Dexec.mainClass="patterns.dynamicprogramming.DynamicProgrammingPatterns"
```

### Run directly with javac
```bash
cd src/main/java
javac patterns/prefixsum/PrefixSumPatterns.java
java patterns.prefixsum.PrefixSumPatterns
```

### Run SOLID examples
```bash
cd src/main/java
javac patterns/solid/*.java
java patterns.solid.SingleResponsibilityPrinciple
java patterns.solid.OpenClosedPrinciple
java patterns.solid.LiskovSubstitutionPrinciple
java patterns.solid.InterfaceSegregationPrinciple
java patterns.solid.DependencyInversionPrinciple
```

### Run Design Pattern examples
```bash
cd src/main/java
javac patterns/designpatterns/*.java
java patterns.designpatterns.SingletonPattern
java patterns.designpatterns.FactoryMethodPattern
java patterns.designpatterns.BuilderPattern
java patterns.designpatterns.ObserverPattern
java patterns.designpatterns.StrategyPattern
java patterns.designpatterns.ChainOfResponsibilityPattern
# ... and 9 more
```

## Resources

1. [Prefix Sum](https://lnkd.in/gCHQegHZ)
2. [Two Pointers](https://lnkd.in/gxVkhHvi)
3. [Sliding Window](https://lnkd.in/g_AapGpf)
4. [Fast & Slow Pointers](https://lnkd.in/gnwzQn9f)
5. [LinkedList In-place Reversal](https://lnkd.in/gkRpq8hA)
6. [Frequency Counting](https://lnkd.in/gAefPAiH)
7. [Monotonic Stack](https://lnkd.in/gnx5JvUB)
8. [Bit Manipulation](https://lnkd.in/g6qnCCqq)
9. [Top 'K' Elements](https://lnkd.in/gwgjChNa)
10. [Overlapping Intervals](https://lnkd.in/g45zauYS)
11. [Binary Search Variants](https://lnkd.in/g93YZeEU)
12. [Binary Tree Traversal](https://lnkd.in/gS-FRRxR)
13. [Depth-First Search (DFS)](https://lnkd.in/gQUpRGdk)
14. [Breadth-First Search (BFS)](https://lnkd.in/gFnJTxfH)
15. [Shortest Path](https://lnkd.in/ggGCX35B)
16. [Matrix Traversal](https://lnkd.in/gHSCeH5H)
17. [Backtracking](https://lnkd.in/g5EfE_5W)
18. [Prefix Search (Trie)](https://lnkd.in/gjwmvjrj)
19. [Greedy](https://lnkd.in/gWfq8B25)
20. [Dynamic Programming Patterns](https://lnkd.in/gJZXfmHK)

### Design Patterns

1. [Singleton](https://lnkd.in/g3VrJz-k)
2. [Factory Method](https://lnkd.in/gA6Uew8n)
3. [Builder](https://lnkd.in/gdTr2BBF)
4. [Adapter](https://lnkd.in/g_yB_CZn)
5. [Decorator](https://lnkd.in/g9zWv66w)
6. [Facade](https://lnkd.in/gHzPeaKG)
7. [Proxy](https://lnkd.in/g2MF2hvS)
8. [Composite](https://lnkd.in/gHwStDc3)
9. [Observer](https://lnkd.in/g4S_eGjy)
10. [Strategy](https://lnkd.in/gSjXJ3Cq)
11. [Command](https://lnkd.in/gffxnxih)
12. [Iterator](https://lnkd.in/g7F_PmD9)
13. [State](https://lnkd.in/gmfFnubm)
14. [Template Method](https://lnkd.in/gshGDpKE)
15. [Chain of Responsibility](https://lnkd.in/gayT82-s)

Full list of patterns at [algomaster.io](https://algomaster.io)

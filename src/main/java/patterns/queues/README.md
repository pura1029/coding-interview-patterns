# Queues

## What is it?

A Queue follows **FIFO** (First In, First Out) — like a line at a coffee shop. The first person in line is the first to be served.

```
Enqueue (add):    Dequeue (remove):

  ┌───┬───┬───┐     ┌───┬───┬───┐
  │ C │ B │ A │ ──►  │ C │ B │   │ → A (removed)
  └───┴───┴───┘     └───┴───┴───┘
  back        front  back    front

  Enqueue D:         Dequeue:
  ┌───┬───┬───┬───┐  returns front element (A)
  │ D │ C │ B │ A │
  └───┴───┴───┴───┘

  Java: Queue<Integer> q = new LinkedList<>();
        q.offer(x)  → enqueue
        q.poll()     → dequeue
        q.peek()     → front element without removing
```

> **Real-world analogy:** A printer job queue. Documents are printed in the order they were submitted — first come, first served.

---

## Queue Variants

### 1. Standard Queue (FIFO)

```
BFS level-order traversal:
  Tree:      1
           /   \
          2     3
         / \
        4   5

  Queue: [1]
  Poll 1, add 2,3 → Queue: [2, 3]
  Poll 2, add 4,5 → Queue: [3, 4, 5]
  Poll 3           → Queue: [4, 5]
  Poll 4           → Queue: [5]
  Poll 5           → Queue: []

  Output: 1, 2, 3, 4, 5 (level order)
```

### 2. Deque (Double-Ended Queue)

```
Insert and remove from BOTH ends in O(1).

  addFirst ◄── ┌───┬───┬───┐ ──► addLast
  pollFirst ──► │ A │ B │ C │ ◄── pollLast
               └───┴───┴───┘

  Use case: Sliding Window Maximum (monotonic deque)
  
  nums = [1, 3, -1, -3, 5],  k=3
  
  Maintain DECREASING deque of indices:
  i=0: deque=[0(1)]
  i=1: 3>1 → remove 0, deque=[1(3)]
  i=2: deque=[1(3), 2(-1)]      → max = 3
  i=3: deque=[1(3), 3(-3)]      → max = 3 (remove 2, outdated)
  i=4: 5>all → clear, deque=[4(5)] → max = 5
```

### 3. Priority Queue (Heap)

```
Elements dequeued by PRIORITY, not insertion order.

  Min-Heap: smallest element at front
  Max-Heap: largest element at front

  Insert: 5, 2, 8, 1, 9
  
  Min-Heap state:    Max-Heap state:
       1                  9
      / \                / \
     2   8              5   8
    / \                / \
   5   9              1   2

  poll() from min-heap: 1, 2, 5, 8, 9 (ascending)
  poll() from max-heap: 9, 8, 5, 2, 1 (descending)

  Java: PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
```

### 4. Circular Queue

```
Fixed-size array with wrap-around using modular arithmetic.

  Capacity = 4:
  
  ┌───┬───┬───┬───┐
  │   │   │   │   │   front=0, rear=0
  └───┴───┴───┴───┘
  
  Enqueue A, B, C:
  ┌───┬───┬───┬───┐
  │ A │ B │ C │   │   front=0, rear=3
  └───┴───┴───┴───┘
  
  Dequeue A:
  ┌───┬───┬───┬───┐
  │   │ B │ C │   │   front=1, rear=3
  └───┴───┴───┴───┘
  
  Enqueue D, E:
  ┌───┬───┬───┬───┐
  │ E │ B │ C │ D │   front=1, rear=1 (wrapped around!)
  └───┴───┴───┴───┘
  
  rear = (rear + 1) % capacity → handles wrap-around
```

---

## Real-World Applications

| Domain          | Application                        | Queue Type        |
| --------------- | ---------------------------------- | ----------------- |
| **OS**          | Process scheduling (round-robin)   | Circular queue    |
| **Web servers** | Request handling                   | Standard queue    |
| **Messaging**   | Kafka, RabbitMQ, SQS              | Standard queue    |
| **Gaming**      | Event processing loop              | Priority queue    |
| **Networking**  | Packet buffering (routers)         | Circular queue    |
| **Caching**     | LRU eviction (deque)               | Deque             |

---

## When to Use

- **BFS traversal** — shortest path (unweighted), level-order
- **Task scheduling** — with cooldowns or priorities (heap)
- **Sliding window max/min** — monotonic deque
- **Stream processing** — hit counting, moving average
- **Multi-source BFS** — rotting oranges, walls & gates, distance maps

## Complexity

| Operation | Queue | Deque | Priority Queue |
|-----------|-------|-------|----------------|
| Enqueue / Offer | O(1) | O(1) | O(log n) |
| Dequeue / Poll | O(1) | O(1) | O(log n) |
| Peek | O(1) | O(1) | O(1) |
| Search | O(n) | O(n) | O(n) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Implement Queue using Stacks | Easy | Two stacks: in-stack and out-stack |
| 2 | Number of Recent Calls | Easy | Queue: evict pings outside 3000ms window |
| 3 | Moving Average from Data Stream | Easy | Queue with fixed window and running sum |
| 4 | Number of Students Unable to Eat | Easy | Queue rotation simulation |
| 5 | Time Needed to Buy Tickets | Easy | Calculate turns per person relative to k |
| 6 | Implement Stack using Queues | Easy | Rotate queue so newest is at front |
| 7 | First Unique Number in Stream | Easy | Queue + frequency map, skip non-unique |
| 8 | Reverse First K Elements of Queue | Easy | Stack for first k, then re-enqueue |
| 9 | Generate Binary Numbers 1 to N | Easy | BFS-style: append "0" and "1" to generate |
| 10 | Number of Islands (BFS) | Easy | BFS flood fill from each unvisited '1' |
| 11 | Design Circular Queue | Medium | Fixed array with head/tail mod arithmetic |
| 12 | Design Circular Deque | Medium | Fixed array with front/rear mod arithmetic |
| 13 | Rotting Oranges | Medium | Multi-source BFS from all rotten oranges |
| 14 | Walls and Gates | Medium | Multi-source BFS from all gates (0) |
| 15 | Open the Lock | Medium | BFS from "0000", skip deadends |
| 16 | Shortest Path in Binary Matrix | Medium | BFS on 8-directional grid |
| 17 | Task Scheduler | Medium | Greedy formula with max frequency count |
| 18 | Sliding Window Maximum (Deque) | Medium | Monotonic deque: maintain decreasing order |
| 19 | Kth Smallest in Sorted Matrix | Medium | Min-heap BFS expanding smallest neighbor |
| 20 | Design Hit Counter | Medium | Queue with timestamp eviction (> 300s) |
| 21 | Task Scheduler with Cooldown (Heap) | Hard | Max-heap + cooldown queue simulation |
| 22 | Shortest Subarray with Sum >= K | Hard | Monotonic deque on prefix sums |
| 23 | Jump Game IV | Hard | BFS with same-value adjacency grouping |
| 24 | Sliding Window Median | Hard | Two heaps (max + min) with lazy deletion |
| 25 | Word Ladder | Hard | BFS: one letter transformation per level |
| 26 | Shortest Path to Get All Keys | Hard | BFS with bitmask state (position + keys) |
| 27 | Min Cost Valid Parentheses | Hard | Greedy balance counter for unmatched brackets |
| 28 | Process Tasks Using Servers | Hard | Two priority queues: free servers + busy servers |
| 29 | Design Snake Game | Hard | Deque as snake body: addFirst/removeLast |
| 30 | Maximum Frequency Stack | Hard | Freq map + group-by-freq stacks |

## Key Insight

> Queues are the backbone of BFS — whenever you see "shortest path (unweighted)", "level-order", or "minimum steps", think queue. The **monotonic deque** extends this to sliding window problems. **Priority queues** (heaps) handle "next best" scheduling. Master **multi-source BFS** for problems where multiple starting points propagate simultaneously.

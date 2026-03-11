# Queues

## What is it?
A Queue (FIFO — First In, First Out) stores elements such that the first element added is the first to be removed. Queues are used for BFS traversal, task scheduling, sliding window problems, and message processing. Key variants include circular queue, priority queue (heap), and deque.

## When to Use
- BFS traversal (shortest path, level-order)
- Task scheduling with cooldowns or priorities
- Sliding window max/min (monotonic deque)
- Stream processing and hit counting
- Multi-source BFS (rotting oranges, walls & gates)

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
> Queues are the backbone of BFS — whenever you see "shortest path (unweighted)", "level-order", or "minimum steps", think queue. The **monotonic deque** extends this to sliding window problems. Priority queues (heaps) handle "next best" scheduling. Master multi-source BFS for problems where multiple starting points propagate simultaneously.

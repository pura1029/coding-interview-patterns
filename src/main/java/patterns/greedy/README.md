# Pattern 19: Greedy Algorithms

## What is it?

A **greedy algorithm** makes the **locally optimal choice** at each step, hoping that these local decisions lead to a **globally optimal solution**. It never reconsiders past choices — once a decision is made, it's final.

```
Greedy vs. Dynamic Programming:

Greedy:              DP (exhaustive):

     A                    A
    / \                  / \
   B   C   ← pick B    B   C   ← explore BOTH
  / \      (locally    / \ / \
 D   E      best)     D  E F  G  ← explore ALL
 ↓                     compare all paths
answer                 pick global best

Greedy: O(n) or O(n log n), ONE path
DP:     O(2^n) or O(n²), ALL paths

Greedy works when the "greedy choice property" holds:
  A locally optimal choice is part of a globally optimal solution.
```

> **Real-world analogy:** Making change with coins. To give 73 cents, you greedily pick the largest coin that fits: 50¢ → 20¢ → 2¢ → 1¢. This works with standard denominations because the greedy choice property holds. (It fails with denominations like {1, 3, 4} — greedy gives 4+1+1=3 coins for 6¢, but optimal is 3+3=2 coins.)

---

## The Two Properties for Greedy to Work

### 1. Greedy Choice Property

You can reach a global optimum by making a locally optimal choice — you don't need to consider future consequences.

```
Coin Change with {25¢, 10¢, 5¢, 1¢}: make 41¢

  Greedy: 25 + 10 + 5 + 1 = 4 coins ✅ (optimal)

  Why it works: using a smaller coin when a larger one fits
  can never produce fewer total coins with these denominations.

Coin Change with {1¢, 3¢, 4¢}: make 6¢

  Greedy: 4 + 1 + 1 = 3 coins ❌
  Optimal: 3 + 3 = 2 coins ✅

  Greedy fails: picking 4¢ blocks you from the 3+3 solution.
  → Use DP instead.
```

### 2. Optimal Substructure

An optimal solution to the problem contains optimal solutions to its subproblems.

```
Activity Selection:
  Activities sorted by end time: A(1-3), B(2-5), C(4-6), D(6-8)

  Pick A (ends earliest).
  Remaining subproblem: activities starting after 3 → C(4-6), D(6-8)
  Pick C (ends earliest).
  Remaining subproblem: activities starting after 6 → D(6-8)
  Pick D.

  Answer: {A, C, D} = 3 activities ✅ (optimal)

  Each subproblem (after removing the greedy choice) is itself
  solved optimally by the same greedy strategy.
```

---

## Greedy Strategy Categories

### Category 1: Sort + Scan

Sort the input by a specific criterion, then scan once making greedy choices.

```
Problem: Non-overlapping Intervals
  Input:  [[1,3], [2,4], [3,5], [0,2]]

  Step 1: Sort by END time → [[0,2], [1,3], [2,4], [3,5]]

  Step 2: Greedily keep intervals that don't overlap:
    [0,2] → take (first interval)
    [1,3] → skip (1 < 2, overlaps)
    [2,4] → take (2 >= 2, no overlap)
    [3,5] → take (3 >= 4? no, skip)  → actually 3 < 4, skip

  Result: remove 2 intervals, keep 2

  ┌──┐
  │  │  [0,2]  ← keep
  └──┘
   ┌───┐
   │   │  [1,3]  ← remove (overlaps [0,2])
   └───┘
     ┌───┐
     │   │  [2,4]  ← keep
     └───┘
      ┌───┐
      │   │  [3,5]  ← remove (overlaps [2,4])
      └───┘
  0  1  2  3  4  5
```

**Why sort by END time?** Choosing the interval that ends earliest leaves the most room for future intervals. This is the classic "activity selection" insight.

---

### Category 2: Greedy with Tracking (Single Pass)

Scan once, maintaining a running state.

```
Problem: Best Time to Buy and Sell Stock
  prices = [7, 1, 5, 3, 6, 4]

  Track: minPrice = ∞, maxProfit = 0

  Day 0: price=7  minPrice=7  profit=0   maxProfit=0
  Day 1: price=1  minPrice=1  profit=0   maxProfit=0
  Day 2: price=5  minPrice=1  profit=4   maxProfit=4  ← buy@1, sell@5
  Day 3: price=3  minPrice=1  profit=2   maxProfit=4
  Day 4: price=6  minPrice=1  profit=5   maxProfit=5  ← buy@1, sell@6 ✅
  Day 5: price=4  minPrice=1  profit=3   maxProfit=5

  Answer: 5

  Why greedy works: At each day, the optimal buy price is the minimum
  seen so far. We never need to reconsider past decisions.
```

```
Problem: Jump Game — Can you reach the last index?
  nums = [2, 3, 1, 1, 4]

  Track farthest reachable index:

  i=0: nums[0]=2 → farthest = max(0, 0+2) = 2
  i=1: nums[1]=3 → farthest = max(2, 1+3) = 4  ← reached end!
  i=2: nums[2]=1 → farthest = max(4, 2+1) = 4
  i=3: nums[3]=1 → farthest = max(4, 3+1) = 4
  i=4: done ✅

  [2] [3] [1] [1] [4]
   ├──►├──►         from index 0, can jump to 1 or 2
       ├──►├──►├──► from index 1, can jump to 2, 3, or 4 ✅
```

---

### Category 3: Greedy + Heap (Priority Queue)

Sort + greedy isn't enough when you need to dynamically pick the best among changing candidates.

```
Problem: IPO — Maximize Capital
  You have initial capital W. You can do at most k projects.
  Each project has a capital requirement and a profit.

  Projects: capital=[0, 1, 1, 2], profit=[1, 2, 3, 5], k=2, W=0

  Step 1: Sort by capital requirement.
  Step 2: For current capital W, add all affordable projects to max-heap.
  Step 3: Pick the highest-profit project from heap. W += profit.
  Step 4: Repeat k times.

  W=0: affordable=[P0(cap=0,prof=1)] → pick P0 → W=1
  W=1: affordable=[P1(cap=1,prof=2), P2(cap=1,prof=3)] → pick P2 → W=4

  Answer: W=4 after 2 projects

  ┌──────────────────────────────────────────────┐
  │ Sorted by capital: P0 P1 P2 P3               │
  │                     ↓                         │
  │ MaxHeap (profit):  [P0:1] → pick P0 (W=0→1) │
  │                     ↓  ↓                      │
  │ MaxHeap (profit):  [P2:3, P1:2] → pick P2    │
  │                     W = 1 → 4 ✅               │
  └──────────────────────────────────────────────┘
```

---

### Category 4: Two-Pass Greedy

When a single pass isn't enough — scan left-to-right, then right-to-left.

```
Problem: Candy Distribution
  ratings = [1, 0, 2]
  Rule: each child gets at least 1 candy;
        higher-rated child gets more than neighbor.

  Pass 1 (left → right): ensure right > left if rating higher
    candy = [1, 1, 1]
    ratings[1]=0 < ratings[0]=1 → skip
    ratings[2]=2 > ratings[1]=0 → candy[2] = candy[1] + 1 = 2
    candy = [1, 1, 2]

  Pass 2 (right → left): ensure left > right if rating higher
    ratings[1]=0 < ratings[2]=2 → skip
    ratings[0]=1 > ratings[1]=0 → candy[0] = max(1, candy[1]+1) = 2
    candy = [2, 1, 2]

  Answer: 2 + 1 + 2 = 5 candies

  ratings:  1    0    2
  candies: [2]  [1]  [2]
            ↑         ↑
          higher     higher
          than 0     than 0
```

---

## Classic Greedy Problems — Deep Dives

### Gas Station (Circular Route)

```
gas  = [1, 2, 3, 4, 5]    (fuel gained at each station)
cost = [3, 4, 5, 1, 2]    (fuel to reach next station)

net  = [-2, -2, -2, 3, 3]  (gas[i] - cost[i])

If total sum of net >= 0, a solution exists.
Sum = -2-2-2+3+3 = 0 ≥ 0 → solution exists.

Find start: track running surplus.
  i=0: surplus = -2          < 0 → can't start here
  i=1: surplus = -2 + -2 = -4 < 0 → can't start before i=2
  start = 2
  i=2: surplus = -2          < 0
  start = 3
  i=3: surplus = 3           ≥ 0
  i=4: surplus = 3 + 3 = 6   ≥ 0

  Answer: start at station 3

  Station:    0    1    2    3    4    0    1    2
  Gas:        1    2    3    4    5
  Cost:       3    4    5    1    2
             fail fail fail START──►──►──►──►──► ✅
                              tank: 3→6→4→0→1→0
```

**Real-world:** Delivery route planning. A delivery truck must complete a circular route visiting all warehouses. Each warehouse gives fuel (packages collected = revenue) and the road to the next costs fuel (distance = expense). The greedy approach finds the optimal starting warehouse.

---

### Task Scheduler

```
tasks = [A, A, A, B, B, B], cooldown n = 2

Most frequent task: A (count=3), B (count=3)
The most frequent task dictates idle time.

Arrange A with cooldown gaps of n=2:
  A _ _ A _ _ A

Fill gaps with B:
  A B _ A B _ A B

Total slots = (maxFreq - 1) × (n + 1) + numMaxFreqTasks
            = (3 - 1)       × (2 + 1)  + 2
            = 6 + 2 = 8

But len(tasks)=6, so answer = max(8, 6) = 8

  Time:  0  1  2  3  4  5  6  7
  Exec:  A  B  _  A  B  _  A  B
              idle       idle

  With more task types: A B C A B C A B → no idle needed!
```

**Real-world:** CPU process scheduling. The OS scheduler assigns processes to CPU cores with cooldown constraints — the same process can't run on back-to-back time slices to avoid cache thrashing.

---

### Partition Labels

```
s = "ababcbacadefegdehijhklij"

Step 1: Find last occurrence of each character.
  a:8  b:5  c:7  d:14  e:15  f:11  g:13  h:19  i:22  j:23  k:20  l:21

Step 2: Greedily extend partition until all chars are contained.

  i=0: 'a' last@8 → end=8
  i=1: 'b' last@5 → end=8
  ...
  i=8: 'a' last@8 → end=8, i==end → PARTITION [0..8] size=9
  i=9: 'd' last@14 → end=14
  ...
  i=15: 'e' last@15 → end=15, i==end → PARTITION [9..15] size=7
  i=16: 'h' last@19 → end=19
  ...
  i=23: 'j' last@23 → end=23, i==end → PARTITION [16..23] size=8

  Answer: [9, 7, 8]

  | a b a b c b a c a | d e f e g d e | h i j h k l i j |
  |←── partition 1 ──►|←── part 2  ──►|←── part 3    ──►|
  0                  8  9           15  16             23
```

**Real-world:** Log file splitting. A log parser must split a file into chunks such that each unique log source appears in exactly one chunk. The greedy approach ensures minimal chunks.

---

### Boats to Save People

```
people = [3, 5, 3, 4], limit = 5

Step 1: Sort → [3, 3, 4, 5]

Step 2: Two pointers — pair heaviest with lightest.

  L=0, R=3: people[L]+people[R] = 3+5 = 8 > 5 → heavy alone. boats=1, R--
  L=0, R=2: people[L]+people[R] = 3+4 = 7 > 5 → heavy alone. boats=2, R--
  L=0, R=1: people[L]+people[R] = 3+3 = 6 > 5 → heavy alone. boats=3, R--
  L=0, R=0: L==R → one person. boats=4

  Answer: 4 boats

  Optimal pairing attempt:
  [3, 3, 4, 5]
   └──┘  ←── can these pair? 3+3=6 > 5. No.
   Everyone goes alone.

  With limit=6: [3, 3, 4, 5]
   3+5=8>6 → 5 alone.  3+4=7>6 → 4 alone.  3+3=6≤6 → pair!
   Answer: 3 boats.
```

**Real-world:** Elevator scheduling. An elevator has a weight limit. People queue up, and the algorithm pairs the heaviest with the lightest to minimize trips.

---

### Remove K Digits (Monotonic Stack)

```
num = "1432219", k = 3 → find smallest number by removing 3 digits

Strategy: scan left to right. If current digit is smaller than the
previous one, removing the previous makes the number smaller.

  Stack: []
  '1' → push → [1]
  '4' → push → [1,4]
  '3' → 3 < 4 → pop 4 (k=2) → [1,3]
  '2' → 2 < 3 → pop 3 (k=1) → [1,2]
  '2' → push → [1,2,2]
  '1' → 1 < 2 → pop 2 (k=0) → [1,2,1]
  '9' → push → [1,2,1,9]

  Answer: "1219"

  1 4 3 2 2 1 9        Original
  1 _ 3 2 2 1 9        Remove 4 (4 > 3)
  1 _ _ 2 2 1 9        Remove 3 (3 > 2)
  1 _ _ 2 _ 1 9        Remove 2 (2 > 1)
  → "1219" ✅
```

---

## Greedy vs. Dynamic Programming — When Does Greedy Fail?

```
Problem                 Greedy Works?   Why / Why Not
─────────────────────────────────────────────────────────
Activity Selection      ✅ Yes          Greedy choice property holds
Fractional Knapsack     ✅ Yes          Can take fractions → ratio greedy
0/1 Knapsack            ❌ No           Can't take fractions → need DP
Coin Change (standard)  ✅ Yes          Standard denominations work
Coin Change (arbitrary) ❌ No           Greedy may miss optimal
Shortest Path (no neg)  ✅ Yes          Dijkstra = greedy
Shortest Path (neg wt)  ❌ No           Need Bellman-Ford (DP)
Huffman Coding          ✅ Yes          Merge two smallest = optimal
Job Scheduling          ✅ Yes          Sort by deadline, greedy
Matrix Chain Multiply   ❌ No           Subproblem overlap → DP
```

### Decision Tree: Greedy or DP?

```
Does the problem have optimal substructure?
  ├── No → Neither (try brute force or other)
  └── Yes
      ├── Does greedy choice property hold?
      │   ├── Yes → GREEDY ✅ (O(n) or O(n log n))
      │   └── No / Unsure
      │       ├── Are there overlapping subproblems?
      │       │   ├── Yes → DP ✅ (memoization/tabulation)
      │       │   └── No → Divide & Conquer
      │       └── Try greedy first, verify with counterexample
      └── Can you prove greedy works? (exchange argument)
          ├── Yes → GREEDY ✅
          └── No → DP is safer
```

---

## Proving Greedy Correctness — Exchange Argument

The standard technique to prove a greedy algorithm is correct:

```
Exchange Argument (Activity Selection):

1. Let G = greedy solution, O = any optimal solution.
2. If G = O, done.
3. If G ≠ O, find the first difference.
   - G picks activity A (ends earliest).
   - O picks activity B (ends later or same).
4. Show we can SWAP B for A in O without losing optimality:
   - A ends ≤ B, so A doesn't conflict with anything B was compatible with.
   - Replacing B with A gives a solution that's at least as good.
5. Repeat until O = G. ✅

This proves the greedy solution is optimal.
```

---

## Real-World Applications

| Domain              | Problem                         | Greedy Strategy                          |
| ------------------- | ------------------------------- | ---------------------------------------- |
| **Networking**      | Huffman Compression (ZIP, GZIP) | Merge two smallest frequency nodes       |
| **Navigation**      | Dijkstra's Shortest Path        | Always expand nearest unvisited node     |
| **Scheduling**      | CPU Job Scheduling              | Earliest deadline first                  |
| **Finance**         | Stock Trading (multiple txns)   | Collect all positive price deltas        |
| **Cloud**           | VM Bin Packing                  | First-fit decreasing                     |
| **Telecom**         | Minimum Spanning Tree (Kruskal) | Add cheapest edge that doesn't form cycle|
| **Data Storage**    | File Compression                | Huffman tree for variable-length encoding|
| **Load Balancing**  | Assign to least-loaded server   | Greedy assignment minimizes max load     |
| **Ride-sharing**    | Match rider to nearest driver   | Minimize pickup distance greedily        |

### Huffman Coding (Data Compression)

```
Characters: A(45%) B(13%) C(12%) D(16%) E(9%) F(5%)

Step 1: Put all in min-heap by frequency.
Step 2: Repeatedly merge two smallest:

  E(9) + F(5) = EF(14)
  C(12) + B(13) = CB(25)
  EF(14) + D(16) = EFD(30)
  CB(25) + EFD(30) = CBEFD(55)
  A(45) + CBEFD(55) = root(100)

Huffman Tree:
           (100)
          /     \
       A(45)   (55)
              /    \
           (25)    (30)
           / \     / \
        C(12) B(13) (14) D(16)
                   / \
                 E(9) F(5)

Codes: A=0, C=100, B=101, E=1100, F=1101, D=111

Fixed-length encoding: 3 bits × 100 chars = 300 bits
Huffman encoding: 1×45 + 3×13 + 3×12 + 3×16 + 4×9 + 4×5 = 224 bits
Compression: 25% smaller ✅
```

**Real-world:** Every ZIP file, GZIP, JPEG, MP3, and PNG uses Huffman coding. Your browser decompresses Huffman-encoded data thousands of times per page load.

---

## Common Greedy Patterns Summary

```
┌─────────────────────────────────────────────────────────┐
│              GREEDY ALGORITHM CHEAT SHEET                │
├──────────────────┬──────────────────────────────────────┤
│ SORT BY END TIME │ Interval scheduling, arrows, merge   │
│                  │ → Earliest end = most room left      │
├──────────────────┼──────────────────────────────────────┤
│ SORT BY START    │ Meeting rooms, overlapping intervals  │
│                  │ → Process in order, track conflicts   │
├──────────────────┼──────────────────────────────────────┤
│ SORT BY RATIO    │ Fractional knapsack, worker hiring    │
│                  │ → value/weight or wage/quality        │
├──────────────────┼──────────────────────────────────────┤
│ SORT BY DEADLINE │ Job scheduling, course selection      │
│                  │ → Process urgent first + heap         │
├──────────────────┼──────────────────────────────────────┤
│ TRACK MIN/MAX    │ Stock trading, jump game              │
│                  │ → Single pass, running state          │
├──────────────────┼──────────────────────────────────────┤
│ TWO-PASS         │ Candy, trapping rain water            │
│                  │ → left→right then right→left          │
├──────────────────┼──────────────────────────────────────┤
│ MONOTONIC STACK  │ Remove K digits, next greater element │
│                  │ → Maintain increasing/decreasing stack│
├──────────────────┼──────────────────────────────────────┤
│ TWO POINTERS     │ Boats, container with most water      │
│                  │ → Sort + pair from both ends          │
├──────────────────┼──────────────────────────────────────┤
│ HEAP + GREEDY    │ IPO, meeting rooms, task scheduler    │
│                  │ → Dynamically pick best candidate     │
└──────────────────┴──────────────────────────────────────┘
```

---

## When to Use

- **Interval scheduling** — activity selection, meeting rooms, non-overlapping intervals
- **Jump / reachability games** — can you reach the end? In how many jumps?
- **Stock trading** — buy/sell with one or multiple transactions
- **Huffman coding** — data compression, variable-length encoding
- **Minimum spanning tree** — Kruskal's (sort edges + union-find) and Prim's (min-heap)
- **Shortest paths** — Dijkstra's algorithm (no negative weights)
- **Scheduling** — CPU scheduling, task scheduler with cooldown
- **Assignment** — assign cookies, boats, workers to minimize/maximize
- **String manipulation** — remove K digits, reorganize string, partition labels

## Complexity

| Operation            | Time       | Space | Example                               |
| -------------------- | ---------- | ----- | ------------------------------------- |
| Sort + single scan   | O(n log n) | O(1)  | Non-overlapping intervals, gas station|
| Single pass (no sort)| O(n)       | O(1)  | Stock trading, jump game              |
| Sort + heap          | O(n log n) | O(n)  | IPO, course schedule, task scheduler  |
| Monotonic stack      | O(n)       | O(n)  | Remove K digits                       |
| Two pointers         | O(n)       | O(1)  | Boats to save people                  |
| Two-pass scan        | O(n)       | O(n)  | Candy distribution                    |

## Examples (30)

| #   | Problem                      | Difficulty | Key Idea                                  |
| --- | ---------------------------- | ---------- | ----------------------------------------- |
| 1   | Best Time to Buy/Sell Stock  | Easy       | Track min price, max profit               |
| 2   | Assign Cookies               | Easy       | Sort both, match smallest cookie to child |
| 3   | Lemonade Change              | Easy       | Greedy: prefer giving $10 change over $5s |
| 4   | Maximum Units on Truck       | Easy       | Sort by units/box descending              |
| 5   | Can Place Flowers            | Easy       | Greedily plant if neighbors empty         |
| 6   | Buy/Sell Stock II (multiple) | Easy       | Add all positive price differences        |
| 7   | Longest Palindrome (build)   | Easy       | Count pairs + one odd center              |
| 8   | Min Ops to Make Increasing   | Easy       | Increment each to be > previous           |
| 9   | Array Partition              | Easy       | Sort, sum even-indexed elements           |
| 10  | Walking Robot Simulation     | Easy       | Simulate moves, track max distance        |
| 11  | Jump Game                    | Medium     | Track farthest reachable index            |
| 12  | Jump Game II                 | Medium     | Greedy BFS levels for min jumps           |
| 13  | Gas Station                  | Medium     | Track surplus, find valid start           |
| 14  | Task Scheduler               | Medium     | Most frequent task determines idle slots  |
| 15  | Non-overlapping Intervals    | Medium     | Sort by end, keep non-overlapping         |
| 16  | Minimum Arrows               | Medium     | Sort by end, count groups                 |
| 17  | Partition Labels             | Medium     | Last occurrence defines partition end     |
| 18  | Boats to Save People         | Medium     | Pair heaviest with lightest               |
| 19  | Remove K Digits              | Medium     | Monotonic stack removes k digits          |
| 20  | Minimum Platforms            | Medium     | Sort arrivals/departures, sweep           |
| 21  | Candy Distribution           | Hard       | Two passes: left-to-right, right-to-left  |
| 22  | IPO (Maximize Capital)       | Hard       | Sort by capital, max-heap for profits     |
| 23  | Min Refueling Stops          | Hard       | Max-heap of passed station fuel           |
| 24  | Course Schedule III          | Hard       | Sort by deadline, max-heap for duration   |
| 25  | Maximum Performance of Team  | Hard       | Sort by efficiency, max-heap for speed    |
| 26  | Min Cost to Hire K Workers   | Hard       | Sort by wage/quality ratio                |
| 27  | Min Interval for Each Query  | Hard       | Sort + sweep with priority queue          |
| 28  | Patching Array               | Hard       | Greedily patch gaps in reachable sums     |
| 29  | Create Maximum Number        | Hard       | Merge two max subsequences of total k     |
| 30  | Reorganize String            | Hard       | Alternate most frequent characters        |

## Interview Tips

### Common Interview Questions About Greedy

| Question | Key Point |
|----------|-----------|
| "How do you know greedy works here?" | Prove greedy choice property — locally optimal leads to global optimum |
| "Can you prove it's optimal?" | Use exchange argument: swap any optimal solution's choice with greedy's and show it's no worse |
| "What if greedy doesn't work?" | Switch to DP — when choices interact (0/1 knapsack, coin change with arbitrary denominations) |
| "Greedy vs DP?" | Greedy: one path, O(n log n). DP: all paths, O(n²) or O(2^n). Greedy is faster when it works |
| "What's the sorting criterion?" | This IS the question — the right sort order is the entire algorithm |

### Red Flags That Greedy Won't Work

```
❌ "Find ALL possible solutions"      → Backtracking
❌ "Count the number of ways"          → DP
❌ "Maximize/minimize with constraints → DP (usually)
   that couple decisions"
❌ "0/1 choices (take or leave)"       → DP (knapsack-style)
❌ Counterexample exists               → Definitely not greedy

✅ "Find ONE optimal solution"         → Maybe greedy
✅ "Maximize/minimize with independent → Likely greedy
   choices"
✅ "Sort + process in order"           → Strong greedy signal
✅ "Interval / scheduling problem"     → Almost always greedy
```

## Key Insight

> **Sort by the right criterion** (deadline, end time, ratio, frequency), then **greedily pick the best available option**. The hardest part isn't coding — it's figuring out what to sort by and proving the greedy choice is safe. When in doubt, try greedy first, look for a counterexample. If none exists, prove with exchange argument. If greedy fails, switch to DP.


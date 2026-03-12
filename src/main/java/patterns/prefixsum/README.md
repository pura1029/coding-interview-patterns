# Pattern 1: Prefix Sum

## What is it?

Prefix sum **pre-computes cumulative sums** so that any subarray sum can be answered in O(1) after an O(n) build step.

```
Array:      [2, 4, 1, 3, 5]
Prefix:  [0, 2, 6, 7, 10, 15]
              ↑  ↑  ↑   ↑   ↑
              2  2+4 +1  +3  +5

Sum of subarray [1..3] (4 + 1 + 3) = prefix[4] - prefix[1] = 10 - 2 = 8 ✅

Without prefix sum: scan elements each time → O(n) per query
With prefix sum:    one subtraction → O(1) per query ← 1000x faster for 1000 queries
```

> **Real-world analogy:** A running total on your bank statement. To find how much you spent between March and July, you subtract the March balance from the July balance — you don't re-add every transaction.

---

## How It Works — Visualization

```
Index:     0    1    2    3    4
Array:   [ 2,   4,   1,   3,   5 ]

Build prefix sum:
  prefix[0] = 0                     (empty prefix)
  prefix[1] = 0 + 2 = 2
  prefix[2] = 2 + 4 = 6
  prefix[3] = 6 + 1 = 7
  prefix[4] = 7 + 3 = 10
  prefix[5] = 10 + 5 = 15

Prefix: [0, 2, 6, 7, 10, 15]

Query: sum(l..r) = prefix[r+1] - prefix[l]

  sum(0..4) = prefix[5] - prefix[0] = 15 - 0  = 15  (entire array)
  sum(1..3) = prefix[4] - prefix[1] = 10 - 2  = 8   (4+1+3)
  sum(2..2) = prefix[3] - prefix[2] = 7  - 6  = 1   (single element)
```

---

## Key Technique: Prefix Sum + HashMap

The most powerful combination: find subarrays with a target sum in O(n).

```
Problem: Subarray Sum Equals K
  nums = [1, 2, 3, -1, 2], k = 4

  Prefix sums: [0, 1, 3, 6, 5, 7]

  For each prefix[i], we need prefix[j] = prefix[i] - k (where j < i)
  If such j exists → subarray [j+1..i] has sum = k

  i=0: prefix=0, need 0-4=-4 → not in map.  map={0:1}
  i=1: prefix=1, need 1-4=-3 → not in map.  map={0:1, 1:1}
  i=2: prefix=3, need 3-4=-1 → not in map.  map={0:1, 1:1, 3:1}
  i=3: prefix=6, need 6-4=2  → not in map.  map={0:1, 1:1, 3:1, 6:1}
  i=4: prefix=5, need 5-4=1  → found! count++  map={..., 5:1}
  i=5: prefix=7, need 7-4=3  → found! count++  map={..., 7:1}

  Answer: 2 subarrays ([2,3,-1], [3,-1,2]) have sum = 4

  Visual:
  [1, 2, 3, -1, 2]
      └──────┘ = 4 ✅
         └──────┘ = 4 ✅
```

---

## 2D Prefix Sum (Range Sum in Matrix)

```
Matrix:          2D Prefix Sum:
  1  2  3        0  0  0  0
  4  5  6        0  1  3  6
  7  8  9        0  5  12 21
                 0  12 27 45

Build: prefix[i][j] = matrix[i-1][j-1]
       + prefix[i-1][j] + prefix[i][j-1] - prefix[i-1][j-1]

Query: sum of submatrix (r1,c1) to (r2,c2):
  = prefix[r2+1][c2+1] - prefix[r1][c2+1]
    - prefix[r2+1][c1] + prefix[r1][c1]

  ┌─────────────────────────────┐
  │            A (subtract)     │
  │    ┌───────┤                │
  │  B │ TARGET│                │
  │    └───────┘                │
  │  (subtract)                 │
  └─────────────────────────────┘

  Total - A - B + overlap(added back) = TARGET
  (Inclusion-exclusion principle)
```

---

## Difference Array (Reverse of Prefix Sum)

Apply range updates in O(1) each, then compute final array in O(n).

```
Problem: Apply +3 to range [1,4] and +5 to range [2,3]

  diff:  [0, 0, 0, 0, 0, 0]      (length n+1)
  
  +3 to [1,4]: diff[1] += 3, diff[5] -= 3
  diff:  [0, 3, 0, 0, 0, -3]
  
  +5 to [2,3]: diff[2] += 5, diff[4] -= 5
  diff:  [0, 3, 5, 0, -5, -3]
  
  Compute prefix sum of diff:
  result: [0, 3, 8, 8, 3, 0]  ✅

  Each range update = O(1) instead of O(n)!
  For m updates on n elements: O(m + n) instead of O(m × n)
```

**Real-world:** Airlines use this for booking. "Add 3 passengers from station 1 to 4" → increment at 1, decrement after 4. Compute prefix sum to get passenger count at each station.

---

## Real-World Applications

| Domain          | Application                      | How Prefix Sum Is Used                         |
| --------------- | -------------------------------- | ---------------------------------------------- |
| **Databases**   | Range aggregate queries          | Pre-computed cumulative sums for instant SUM()  |
| **Finance**     | Cumulative returns               | Running total for portfolio performance         |
| **Image Processing** | Integral images (Summed Area Table) | 2D prefix sum for fast box blur, face detection |
| **Gaming**      | Damage over area                 | 2D prefix sum for AoE damage calculation        |
| **Networking**  | Bandwidth usage per interval     | Difference array for overlapping time windows   |
| **Analytics**   | Cumulative user signups by date  | Prefix sum of daily counts                      |

---

## When to Use

- **Repeated range-sum queries** — build once, query unlimited times in O(1)
- **Subarray sum equals target** — prefix sum + hashmap for O(n)
- **Subarray divisibility checks** — prefix sum mod k + pigeonhole principle
- **2D region sum queries** — 2D prefix sum with inclusion-exclusion
- **Range updates** — difference array for efficient bulk operations
- **Product of array except self** — prefix/suffix product arrays

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Build prefix sum | O(n) | O(n) |
| Range query | O(1) | — |
| 2D build | O(m × n) | O(m × n) |
| 2D query | O(1) | — |
| Difference array update | O(1) per update | O(n) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Range Sum Query – Immutable | Easy | Build prefix[], answer in O(1) |
| 2 | Running Sum of 1D Array | Easy | Cumulative sum |
| 3 | Find Pivot Index | Easy | Left sum == total - left - current |
| 4 | Sum of All Odd Length Subarrays | Easy | Count element contribution |
| 5 | Number of Good Pairs | Easy | Frequency prefix counting |
| 6 | Left and Right Sum Differences | Easy | Prefix + suffix sums |
| 7 | Count Positive Prefix Sums | Easy | Running sum check |
| 8 | Max After Range Increments | Easy | Difference array technique |
| 9 | Min Start Value for Positive Sums | Easy | Track minimum prefix |
| 10 | K Length Apart Check | Easy | Gap tracking |
| 11 | Subarray Sum Equals K | Medium | Prefix sum + hashmap |
| 12 | Contiguous Array (Equal 0s and 1s) | Medium | Replace 0→-1, find sum=0 |
| 13 | Product of Array Except Self | Medium | Prefix/suffix products |
| 14 | Subarrays Divisible by K | Medium | Remainder prefix counting |
| 15 | Bounded Maximum Subarrays | Medium | AtMost technique |
| 16 | Binary Subarrays With Sum | Medium | Prefix sum counting |
| 17 | Maximum Size Subarray Sum=K | Medium | First occurrence hashmap |
| 18 | Range Addition (Difference Array) | Medium | Sweep line + prefix |
| 19 | Continuous Subarray Sum (multiple of k) | Medium | Remainder + index tracking |
| 20 | Count Nice Subarrays (k odd numbers) | Medium | AtMost sliding window |
| 21 | Max Sum After Removing One Element | Hard | Prefix/suffix max subarray |
| 22 | 2D Range Sum Query | Hard | 2D prefix inclusion-exclusion |
| 23 | Subarray Sum=K (Long overflow) | Hard | Long prefix sum handling |
| 24 | Max Sum of 3 Non-Overlapping Subarrays | Hard | Window sum + left/right best |
| 25 | Shortest Subarray with Sum≥K | Hard | Prefix sum + monotone deque |
| 26 | Count of Range Sum | Hard | Merge sort on prefix sums |
| 27 | Max Non-Overlapping Subarrays=Target | Hard | Greedy prefix reset |
| 28 | Make Sum Divisible by P | Hard | Remove shortest subarray |
| 29 | Submatrix Sum Target (2D) | Hard | Column compression + prefix |
| 30 | Min Operations to Reduce X to Zero | Hard | Longest subarray = total-x |

## Key Insight

> Build prefix sums once, answer unlimited range queries in O(1). The killer combination is **prefix sum + hashmap**: for every prefix[i], check if `prefix[i] - target` was seen before. This turns "find subarray with sum K" from O(n²) brute force into O(n). For 2D problems, use inclusion-exclusion. For range updates, flip the concept: use a difference array.

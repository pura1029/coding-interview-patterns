# Arrays

## What is it?

Arrays store elements **contiguously in memory** for O(1) random access by index. They are the most fundamental data structure — nearly every coding interview question involves arrays in some form.

```
Memory layout:
  Index:    0      1      2      3      4
  Value: [ 10  |  20  |  30  |  40  |  50  ]
  Addr:  0x100  0x104  0x108  0x112  0x116

  Access arr[3]: address = base + 3 × sizeof(int) = 0x100 + 12 = 0x112 → O(1)
  Insert at index 2: shift elements 2,3,4 right → O(n)
  Delete at index 1: shift elements 2,3,4 left → O(n)
```

> **Real-world analogy:** A row of lockers numbered 0 to n-1. You can open any locker instantly by number (O(1) access), but inserting a new locker in the middle requires shifting all subsequent lockers.

---

## Essential Array Techniques

### 1. Two Pointers

```
Two Sum (sorted array): find two numbers that sum to target=9
  [1, 2, 4, 6, 8, 10]
   L                R     1+10=11 > 9 → move R left
   L           R          1+8=9 ✅ FOUND

Container With Most Water:
  height = [1, 8, 6, 2, 5, 4, 8, 3, 7]

  L=0, R=8: area = min(1,7) × 8 = 8      move L (shorter side)
  L=1, R=8: area = min(8,7) × 7 = 49 ✅   move R
  L=1, R=7: area = min(8,3) × 6 = 18     move R
  ...
  Answer: 49

  |       |
  |   |   |       |
  |   | | |     | |
  |   | | | |   | |
  | | | | | | | | |
  1 8 6 2 5 4 8 3 7
  L──────────────►R
```

### 2. Kadane's Algorithm (Maximum Subarray)

```
nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]

  At each element: extend current subarray OR start fresh
  current = max(nums[i], current + nums[i])

  i=0: current = max(-2, -2) = -2    best = -2
  i=1: current = max(1, -2+1) = 1    best = 1
  i=2: current = max(-3, 1-3) = -2   best = 1
  i=3: current = max(4, -2+4) = 4    best = 4
  i=4: current = max(-1, 4-1) = 3    best = 4
  i=5: current = max(2, 3+2) = 5     best = 5
  i=6: current = max(1, 5+1) = 6     best = 6 ✅
  i=7: current = max(-5, 6-5) = 1    best = 6
  i=8: current = max(4, 1+4) = 5     best = 6

  Answer: 6 (subarray [4, -1, 2, 1])
```

### 3. Dutch National Flag (Three-Way Partition)

```
Sort Colors: arr = [2, 0, 2, 1, 1, 0]

  Three pointers: lo (0s boundary), mid (scanner), hi (2s boundary)

  [2, 0, 2, 1, 1, 0]     mid=0: arr[0]=2 → swap(mid,hi), hi--
   m              h
  [0, 0, 2, 1, 1, 2]     mid=0: arr[0]=0 → lo++, mid++
   m           h
  [0, 0, 2, 1, 1, 2]     mid=1: arr[1]=0 → lo++, mid++
      m        h
  [0, 0, 2, 1, 1, 2]     mid=2: arr[2]=2 → swap(mid,hi), hi--
         m     h
  [0, 0, 1, 1, 2, 2]     mid=2: arr[2]=1 → mid++
         m  h
  [0, 0, 1, 1, 2, 2]     mid=3: arr[3]=1 → mid++
            mh
  [0, 0, 1, 1, 2, 2]     mid > hi → DONE ✅
```

### 4. Cyclic Sort

```
First Missing Positive: place each n at index n-1

  [3, 4, -1, 1]
  
  i=0: arr[0]=3 → swap to index 2 → [-1, 4, 3, 1]
  i=0: arr[0]=-1 → skip (negative)
  i=1: arr[1]=4 → swap to index 3 → [-1, 1, 3, 4]
  i=1: arr[1]=1 → swap to index 0 → [1, -1, 3, 4]
  i=1: arr[1]=-1 → skip
  i=2: arr[2]=3 → already at index 2 ✅
  i=3: arr[3]=4 → already at index 3 ✅

  Scan: index 1 has -1 → first missing positive = 2 ✅
```

---

## Real-World Applications

| Domain          | Application                            | Technique                    |
| --------------- | -------------------------------------- | ---------------------------- |
| **Finance**     | Stock trading (buy low, sell high)      | Track min price, max profit  |
| **Databases**   | Sorted index scan                      | Binary search on sorted data |
| **Image**       | Pixel manipulation (2D array)          | Spiral traversal, rotation   |
| **Gaming**      | Collision detection arrays             | Sweep line, interval overlap |
| **Analytics**   | Running averages, cumulative sums      | Prefix sum, sliding window   |

---

## When to Use

- **Searching, sorting, partitioning** — two pointers, binary search, Dutch flag
- **Two-pointer and sliding window** — on sequential/sorted data
- **In-place transformations** — reverse, rotate (triple-reverse trick), cyclic sort
- **Prefix computation** — sum, product, XOR for range queries
- **Hash-based lookups** — complements, duplicates, frequency counting

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Access by index | O(1) | — |
| Search (unsorted) | O(n) | — |
| Search (sorted / binary) | O(log n) | — |
| Insert / Delete (middle) | O(n) | — |
| Hash-based lookup | O(1) avg | O(n) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Two Sum | Easy | Hash map for complement lookup |
| 2 | Best Time to Buy and Sell Stock | Easy | Track running min, compute max profit |
| 3 | Contains Duplicate | Easy | HashSet for O(1) duplicate check |
| 4 | Merge Sorted Array | Easy | Fill from back using three pointers |
| 5 | Maximum Subarray (Kadane's) | Easy | Running max of current vs. fresh start |
| 6 | Remove Duplicates from Sorted Array | Easy | Slow/fast pointer, overwrite in-place |
| 7 | Single Number | Easy | XOR all elements; duplicates cancel |
| 8 | Move Zeroes | Easy | Swap non-zero to write pointer |
| 9 | Plus One | Easy | Traverse from end, carry propagation |
| 10 | Missing Number | Easy | Sum formula: expected minus actual |
| 11 | Three Sum | Medium | Sort + two-pointer, skip duplicates |
| 12 | Container With Most Water | Medium | Opposite-end pointers, move shorter side |
| 13 | Product of Array Except Self | Medium | Left prefix pass + right suffix pass |
| 14 | Rotate Array | Medium | Triple-reverse trick |
| 15 | Sort Colors (Dutch National Flag) | Medium | Three-way partition with lo/mid/hi |
| 16 | Next Permutation | Medium | Find dip from right, swap + reverse suffix |
| 17 | Subarray Sum Equals K | Medium | Prefix sum with hash map counting |
| 18 | Spiral Matrix | Medium | Layer-by-layer peeling (top/right/bottom/left) |
| 19 | Find All Duplicates in Array | Medium | Mark visited by negating index |
| 20 | Kth Largest Element | Medium | Quickselect for average O(n) |
| 21 | Trapping Rain Water | Hard | Two-pointer with left/right max tracking |
| 22 | First Missing Positive | Hard | Cyclic sort placing n at index n-1 |
| 23 | Median of Two Sorted Arrays | Hard | Binary search on shorter array partition |
| 24 | Longest Consecutive Sequence | Hard | HashSet, start chain only when n-1 absent |
| 25 | Largest Rectangle in Histogram | Hard | Monotonic stack for nearest smaller bars |
| 26 | Sliding Window Maximum | Hard | Monotonic deque maintaining max candidates |
| 27 | Maximum Product Subarray | Hard | Track both max and min products (sign flip) |
| 28 | Count of Smaller Numbers After Self | Hard | Merge sort with index tracking |
| 29 | Shortest Unsorted Continuous Subarray | Hard | Two passes: find left/right boundaries |
| 30 | Candy Distribution | Hard | Two-pass: left-to-right rising, right-to-left falling |

## Key Insight

> Arrays are deceptively simple — the real challenge lies in choosing the right technique. **Two pointers** for sorted/partitioning problems, **prefix sum** for range queries, **hashing** for lookups, **cyclic sort** for missing/duplicate numbers, and **monotonic stack** for "nearest greater/smaller" problems. Master in-place operations to achieve O(1) space.

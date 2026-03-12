# Pattern 11: Binary Search Variants

## What is it?

Binary search **halves the search space** each iteration, achieving O(log n) time. It works on any **monotonic** function — not just sorted arrays.

```
Linear Search: check every element → O(n)
  [2, 5, 8, 12, 16, 23, 38, 56, 72, 91]
   ✓  ✓  ✓   ✓   ✓   ✓   ✓  FOUND!       7 comparisons

Binary Search: halve each time → O(log n)
  [2, 5, 8, 12, 16, 23, 38, 56, 72, 91]
                 ↑ mid=16  (too small)
                          [23, 38, 56, 72, 91]
                                ↑ mid=56  FOUND!     2 comparisons

For 1 billion elements:
  Linear:  1,000,000,000 comparisons
  Binary:  ~30 comparisons ← log₂(10⁹) ≈ 30
```

> **Real-world analogy:** Looking up a word in a dictionary. You don't read page by page — you open to the middle, decide if the word is before or after, and halve the remaining pages. Repeat until found.

---

## The Three Templates

### Template 1: Standard Binary Search (Exact Match)

```
function binarySearch(arr, target):
    lo = 0, hi = arr.length - 1
    while lo <= hi:
        mid = lo + (hi - lo) / 2
        if arr[mid] == target → return mid
        if arr[mid] < target  → lo = mid + 1
        else                  → hi = mid - 1
    return -1  (not found)

Example: find 23 in [2, 5, 8, 12, 16, 23, 38, 56, 72, 91]

  Step 1: lo=0, hi=9, mid=4 → arr[4]=16 < 23 → lo=5
  Step 2: lo=5, hi=9, mid=7 → arr[7]=56 > 23 → hi=6
  Step 3: lo=5, hi=6, mid=5 → arr[5]=23 = 23 → FOUND at index 5 ✅
```

### Template 2: Lower Bound (First Occurrence / Insert Position)

```
function lowerBound(arr, target):
    lo = 0, hi = arr.length
    while lo < hi:
        mid = lo + (hi - lo) / 2
        if arr[mid] < target → lo = mid + 1
        else                 → hi = mid
    return lo

Example: find first occurrence of 5 in [1, 3, 5, 5, 5, 7, 9]

  Step 1: lo=0, hi=7, mid=3 → arr[3]=5 ≥ 5 → hi=3
  Step 2: lo=0, hi=3, mid=1 → arr[1]=3 < 5  → lo=2
  Step 3: lo=2, hi=3, mid=2 → arr[2]=5 ≥ 5 → hi=2
  lo=hi=2 → first 5 is at index 2 ✅
```

### Template 3: Binary Search on Answer Space

```
function searchOnAnswer(lo, hi):
    while lo < hi:
        mid = lo + (hi - lo) / 2
        if feasible(mid) → hi = mid     (mid works, try smaller)
        else             → lo = mid + 1 (mid doesn't work, try bigger)
    return lo

The key insight: you're not searching an array — you're searching
a RANGE OF POSSIBLE ANSWERS and checking feasibility.

Example: Koko Eating Bananas
  piles = [3, 6, 7, 11], hours = 8
  What is the minimum eating speed k?

  lo=1, hi=11 (max pile)
  mid=6: hours needed = ⌈3/6⌉+⌈6/6⌉+⌈7/6⌉+⌈11/6⌉ = 1+1+2+2 = 6 ≤ 8 ✅ → hi=6
  mid=3: hours needed = ⌈3/3⌉+⌈6/3⌉+⌈7/3⌉+⌈11/3⌉ = 1+2+3+4 = 10 > 8 ❌ → lo=4
  mid=5: hours needed = ⌈3/5⌉+⌈6/5⌉+⌈7/5⌉+⌈11/5⌉ = 1+2+2+3 = 8 ≤ 8 ✅ → hi=5
  mid=4: hours needed = ⌈3/4⌉+⌈6/4⌉+⌈7/4⌉+⌈11/4⌉ = 1+2+2+3 = 8 ≤ 8 ✅ → hi=4
  lo=hi=4 → minimum speed = 4 ✅
```

---

## Classic Problems with Visualizations

### Search in Rotated Sorted Array

```
Original sorted: [0, 1, 2, 4, 5, 6, 7]
Rotated at k=4:  [4, 5, 6, 7, 0, 1, 2]

Find target = 0

Step 1: lo=0, hi=6, mid=3 → arr[3]=7
        Left half [4,5,6,7] is sorted (arr[lo]=4 ≤ arr[mid]=7)
        target=0 is NOT in [4,7] → search right: lo=4

Step 2: lo=4, hi=6, mid=5 → arr[5]=1
        Left half [0,1] is sorted (arr[lo]=0 ≤ arr[mid]=1)
        target=0 IS in [0,1] → search left: hi=5

Step 3: lo=4, hi=5, mid=4 → arr[4]=0 = target ✅

KEY INSIGHT: After rotation, at least ONE half is always sorted.
  Check if target is in the sorted half → search there.
  Otherwise → search the other half.
```

### Find Peak Element

```
arr = [1, 2, 3, 1]

A peak is an element greater than its neighbors.

    3
   / \
  2   1
 /
1

  mid=1 → arr[1]=2, arr[2]=3 → right neighbor is bigger → go right
  mid=2 → arr[2]=3, arr[3]=1 → left neighbor is smaller → PEAK at index 2 ✅

WHY BINARY SEARCH WORKS: if arr[mid] < arr[mid+1], there MUST be a peak
on the right side (values can't increase forever — they hit the boundary).
```

### Median of Two Sorted Arrays

```
A = [1, 3, 8, 9, 15]     (m=5)
B = [7, 11, 18, 19, 21]   (n=5)

Binary search on partition of shorter array.
Total elements = 10, so left partition gets 5 elements.

  Partition A at i=2: left_A = [1, 3]      right_A = [8, 9, 15]
  Partition B at j=3: left_B = [7, 11, 18] right_B = [19, 21]

  Check: max(left_A) ≤ min(right_B)?  3 ≤ 19 ✅
         max(left_B) ≤ min(right_A)?  18 ≤ 8  ❌ → too many from B

  Move A partition right (i=3):
  left_A = [1, 3, 8]      right_A = [9, 15]
  left_B = [7, 11]         right_B = [18, 19, 21]

  Check: max(left_A) ≤ min(right_B)?  8 ≤ 18 ✅
         max(left_B) ≤ min(right_A)?  11 ≤ 9  ❌ → still too many from B

  Move A partition right (i=4):
  left_A = [1, 3, 8, 9]   right_A = [15]
  left_B = [7]              right_B = [11, 18, 19, 21]

  Check: max(left_A) ≤ min(right_B)?  9 ≤ 11 ✅
         max(left_B) ≤ min(right_A)?  7 ≤ 15 ✅

  Median = (max(9,7) + min(15,11)) / 2 = (9+11)/2 = 10 ✅
```

---

## Binary Search on Answer — Pattern Recognition

```
┌─────────────────────────────────────────────────────────────┐
│  BINARY SEARCH ON ANSWER — Recognize This Pattern:          │
│                                                             │
│  "Find the MINIMUM/MAXIMUM value such that [condition]"     │
│                                                             │
│  Steps:                                                     │
│  1. Define search range: [lo, hi] = possible answer range   │
│  2. Write feasible(mid): can the condition be met with mid? │
│  3. Binary search: if feasible → try smaller/bigger         │
│                                                             │
│  Examples:                                                  │
│  ┌──────────────────────────────┬──────────────────────────┐│
│  │ Problem                      │ Search Space             ││
│  ├──────────────────────────────┼──────────────────────────┤│
│  │ Koko Eating Bananas          │ speed: [1, max(pile)]    ││
│  │ Capacity to Ship in D Days   │ capacity: [max, sum]     ││
│  │ Split Array Largest Sum      │ max_sum: [max, total]    ││
│  │ Aggressive Cows              │ distance: [1, max_pos]   ││
│  │ Min Speed to Arrive on Time  │ speed: [1, 10^7]        ││
│  │ Min Days for Bouquets        │ days: [1, max(bloom)]    ││
│  │ Sqrt(x)                      │ root: [0, x]             ││
│  └──────────────────────────────┴──────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

---

## Real-World Applications

| Domain            | Application                        | How Binary Search Is Used                        |
| ----------------- | ---------------------------------- | ------------------------------------------------ |
| **Databases**     | B-Tree index lookup                | Binary search within each tree node               |
| **Git**           | `git bisect`                       | Find the commit that introduced a bug             |
| **Gaming**        | Physics engine collision detection | Binary search on time to find exact contact point |
| **Networking**    | IP routing tables                  | Longest prefix match via binary search            |
| **ML/AI**         | Hyperparameter tuning              | Binary search on learning rate or threshold       |
| **Load Balancer** | Weighted server selection          | Binary search on cumulative weight array          |
| **File Systems**  | Directory lookup                   | B-tree search in inode tables                     |

### Git Bisect — Real Example

```
You know commit #100 is buggy, commit #1 was fine.

  git bisect start
  git bisect bad HEAD       (commit #100)
  git bisect good abc123    (commit #1)

  Git checks out commit #50 → you test → "bad"
  Git checks out commit #25 → you test → "good"
  Git checks out commit #37 → you test → "bad"
  Git checks out commit #31 → you test → "good"
  Git checks out commit #34 → you test → "bad"
  Git checks out commit #32 → you test → "good"
  Git checks out commit #33 → you test → "bad" ← BUG INTRODUCED HERE

  Only 7 tests instead of 100! That's log₂(100) ≈ 7 ✅
```

---

## Common Pitfalls

| Pitfall | Problem | Fix |
|---------|---------|-----|
| Integer overflow in `mid` | `(lo + hi) / 2` overflows for large values | Use `lo + (hi - lo) / 2` |
| Infinite loop | `lo = mid` when `mid = lo` loops forever | Ensure `lo = mid + 1` or `hi = mid - 1` |
| Off-by-one in bounds | Wrong `<=` vs `<` in while condition | Template 1: `lo <= hi`. Template 2: `lo < hi` |
| Wrong half selection in rotated | Not checking which half is sorted | Always compare `arr[lo]` vs `arr[mid]` first |

---

## When to Use

- **Sorted array search** — exact, first/last occurrence, insert position
- **Rotated sorted arrays** — identify sorted half, search accordingly
- **Binary search on answer** — minimize/maximize a value with feasibility check
- **Matrix search** — treat 2D sorted matrix as flattened 1D array
- **Peak finding** — move toward the higher neighbor
- **Kth element** — binary search on value, count elements ≤ mid

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Standard search | O(log n) | O(1) |
| Lower/upper bound | O(log n) | O(1) |
| Search on answer | O(n log range) | O(1) |
| 2D matrix search | O(log(m×n)) | O(1) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Binary Search (classic) | Easy | Compare mid, halve search space |
| 2 | First Bad Version | Easy | Find first true in boolean array |
| 3 | Search Insert Position | Easy | Lower bound insertion point |
| 4 | Count Negatives in Sorted Matrix | Easy | Step from bottom-left corner |
| 5 | Sqrt(x) | Easy | Binary search on answer [0, x] |
| 6 | Guess Number | Easy | Binary search with API feedback |
| 7 | Valid Perfect Square | Easy | Binary search mid*mid == n |
| 8 | Arrange Coins | Easy | Binary search on complete rows |
| 9 | Check If Double Exists | Easy | Sort + binary search for 2x |
| 10 | Intersection of Two Arrays | Easy | Sort + binary search lookups |
| 11 | First and Last Position | Medium | Two binary searches: lower + upper bound |
| 12 | Search in Rotated Sorted Array | Medium | Identify sorted half, search accordingly |
| 13 | Find Minimum in Rotated Array | Medium | Compare mid with right boundary |
| 14 | Find Peak Element | Medium | Move toward higher neighbor |
| 15 | Search a 2D Matrix | Medium | Treat as flattened sorted array |
| 16 | Koko Eating Bananas | Medium | Binary search on eating speed |
| 17 | Capacity to Ship in D Days | Medium | Binary search on capacity |
| 18 | Single Element in Sorted Array | Medium | Binary search on pair alignment |
| 19 | Time Based Key-Value Store | Medium | Binary search on timestamps |
| 20 | Min Days to Make Bouquets | Medium | Binary search on days |
| 21 | Median of Two Sorted Arrays | Hard | Binary search on partition position |
| 22 | Split Array Largest Sum | Hard | Binary search on max-sum, greedy check |
| 23 | Find in Mountain Array | Hard | Find peak, search both sides |
| 24 | Kth in Multiplication Table | Hard | Binary search on value, count ≤ x |
| 25 | Aggressive Cows (max min dist) | Hard | Binary search on distance |
| 26 | Nth Magical Number | Hard | Binary search with LCM counting |
| 27 | Russian Doll Envelopes | Hard | Sort + LIS with binary search |
| 28 | Min Speed to Arrive on Time | Hard | Binary search on speed |
| 29 | Count Smaller After Self (BIT) | Hard | Binary Indexed Tree for rank queries |
| 30 | Max Running Time of N Computers | Hard | Binary search on runtime |

## Key Insight

> "Binary search on answer": if you can verify a candidate answer in O(n), binary search finds the optimal in O(n log range). The hardest part is **recognizing** that binary search applies — look for "minimum X such that condition" or "maximum X such that condition". Define the search range, write a feasibility check, and binary search does the rest.

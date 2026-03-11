# Pattern 11: Binary Search Variants

## What is it?
Halves the search space each iteration for O(log n). Works on sorted data and monotonic predicates ("binary search on answer").

## When to Use
- Searching in sorted arrays (exact, first/last occurrence)
- Rotated sorted arrays
- Binary search on answer space (min/max feasibility)
- Matrix search (row-major sorted)

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Standard search | O(log n) | O(1) |
| Search on answer | O(n log range) | O(1) |

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
> "Binary search on answer": if you can verify a candidate answer in O(n), binary search finds the optimal in O(n log range).

# Arrays

## What is it?
Arrays store elements contiguously in memory for O(1) random access by index. They are the most fundamental data structure and underpin nearly every coding interview question — from basic traversal to complex partitioning and in-place manipulation.

## When to Use
- Searching, sorting, or partitioning elements
- Two-pointer and sliding window on sequential data
- In-place transformations (reverse, rotate, cyclic sort)
- Prefix computation (sum, product, XOR)
- Hash-based lookups for complements and duplicates

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
> Arrays are deceptively simple — the real challenge lies in choosing the right technique (hashing, sorting, two pointers, prefix computation, cyclic sort) and performing operations in-place to achieve optimal time and space complexity.

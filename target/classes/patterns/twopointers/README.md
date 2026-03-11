# Pattern 2: Two Pointers

## What is it?
Two references move through data (usually sorted) to solve problems in O(n) instead of O(n²). Common variants: opposite ends, same direction (fast/slow), or merge-style.

## When to Use
- Pair sum in sorted arrays, triplet/quadruplet sums
- Palindrome checks, string comparisons
- Merging sorted arrays, partitioning
- Container/trapping water problems

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Single pass | O(n) | O(1) |
| With sorting | O(n log n) | O(1) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Two Sum II – Sorted Array | Easy | Opposite-end pointers converge |
| 2 | Valid Palindrome | Easy | Compare chars from both ends |
| 3 | Remove Duplicates from Sorted Array | Easy | Slow pointer tracks unique position |
| 4 | Move Zeroes | Easy | Swap non-zeros to front |
| 5 | Squares of a Sorted Array | Easy | Merge from both ends |
| 6 | Reverse String | Easy | Swap opposite ends |
| 7 | Is Subsequence | Easy | Two pointers on both strings |
| 8 | Merge Sorted Array | Easy | Fill from the end |
| 9 | Intersection of Two Arrays II | Easy | Sort + two pointers |
| 10 | Valid Palindrome II | Easy | Skip one char and retry |
| 11 | Three Sum | Medium | Fix one, two-pointer on rest |
| 12 | Three Sum Closest | Medium | Track minimum difference |
| 13 | Container With Most Water | Medium | Move shorter height inward |
| 14 | Sort Colors (Dutch National Flag) | Medium | Three-way partition |
| 15 | Partition Labels | Medium | Track last occurrence of each char |
| 16 | Remove Duplicates II (allow 2) | Medium | Count duplicates with slow pointer |
| 17 | Longest Mountain in Array | Medium | Expand peak both directions |
| 18 | Boats to Save People | Medium | Pair lightest with heaviest |
| 19 | Four Sum | Medium | Two loops + two-pointer inner |
| 20 | Longest Word Through Deleting | Medium | Subsequence check per candidate |
| 21 | Trapping Rain Water | Hard | Track left/right max boundaries |
| 22 | Minimum Window Sort | Hard | Find unsorted subarray boundaries |
| 23 | Count Pairs with Diff ≤ Target | Hard | Sort + count valid pairs |
| 24 | Three Sum Smaller | Hard | Count all pairs below target |
| 25 | Backspace String Compare (O(1)) | Hard | Traverse from end with skip count |
| 26 | Min Diff K Scores | Hard | Sort + sliding window of size k |
| 27 | Smallest Range Covering K Lists | Hard | Merge + two-pointer on sorted events |
| 28 | Number of Subsequences Max-Min ≤ Target | Hard | Sort + count with powers |
| 29 | Maximum Erasure Value | Hard | Sliding window unique subarray |
| 30 | Count Subarrays with Score < K | Hard | Prefix sum + two-pointer |

## Key Insight
> Sort first (if needed), then use two pointers from opposite ends or same direction to eliminate O(n²) nested loops.

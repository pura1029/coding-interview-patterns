# Pattern 9: Top K Elements

## What is it?
Uses heaps (priority queues) to efficiently find top/bottom K elements in O(n log k) instead of O(n log n).

## When to Use
- Kth largest/smallest element
- K most/least frequent elements
- Merge K sorted lists/streams
- Median from data stream (two heaps)

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Find top K | O(n log k) | O(k) |
| Heap push/pop | O(log k) | - |
| Quick select (avg) | O(n) | O(1) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Kth Largest Element | Easy | Min-heap of size k |
| 2 | Last Stone Weight | Easy | Max-heap, smash two largest |
| 3 | Kth Largest in Stream | Easy | Maintain min-heap of size k |
| 4 | Sort by Increasing Frequency | Easy | Count + custom sort |
| 5 | Relative Ranks | Easy | Sort by score, assign ranks |
| 6 | K Closest Points to Origin | Easy | Min-heap by distance |
| 7 | K Pairs with Smallest Sums | Easy | Min-heap of pair sums |
| 8 | Maximum Product of Two Elements | Easy | Find two largest values |
| 9 | Find K Largest (sort) | Easy | Sort descending, take first k |
| 10 | Minimum Cost of Candies | Easy | Sort, skip every 3rd |
| 11 | Top K Frequent Elements | Medium | Bucket sort by frequency |
| 12 | Sort Characters By Frequency | Medium | Count + sort by frequency |
| 13 | Reorganize String | Medium | Greedy with max-heap |
| 14 | Task Scheduler | Medium | Max-freq determines idle slots |
| 15 | Kth Smallest in Sorted Matrix | Medium | Min-heap or binary search |
| 16 | Top K Frequent Words | Medium | Min-heap with custom comparator |
| 17 | Least Unique After K Removals | Medium | Remove least frequent first |
| 18 | Kth Smallest Pair Distance | Medium | Binary search on distance |
| 19 | Ugly Number II | Medium | Min-heap of multiples of 2,3,5 |
| 20 | Seat Reservation Manager | Medium | Min-heap of available seats |
| 21 | Find Median from Data Stream | Hard | Two heaps: max-heap + min-heap |
| 22 | Merge K Sorted Lists | Hard | Min-heap of list heads |
| 23 | Sliding Window Median | Hard | Two sorted sets with lazy deletion |
| 24 | IPO (Maximize Capital) | Hard | Max-heap profits, min-heap capitals |
| 25 | Smallest Range from K Lists | Hard | Min-heap + track global max |
| 26 | Trapping Rain Water II (3D) | Hard | BFS from border with min-heap |
| 27 | Maximum Frequency Stack | Hard | Stack per frequency level |
| 28 | Course Schedule III | Hard | Greedy sort by deadline + max-heap |
| 29 | K Closest (Quick Select) | Hard | Partition around kth distance |
| 30 | Min Cost to Hire K Workers | Hard | Sort by rate, max-heap for quality |

## Key Insight
> Use a min-heap of size K for "top K largest"; use max-heap of size K for "top K smallest". Two heaps for median.

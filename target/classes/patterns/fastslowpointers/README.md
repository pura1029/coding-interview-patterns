# Pattern 4: Fast & Slow Pointers (Floyd's Tortoise and Hare)

## What is it?
Two pointers at different speeds detect cycles, find midpoints, and solve linked list problems in O(n) time, O(1) space.

## When to Use
- Cycle detection in linked lists or sequences
- Finding middle of linked list
- Palindrome verification
- Finding duplicate numbers (cycle in value graph)

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Cycle detection | O(n) | O(1) |
| Find middle | O(n) | O(1) |
| Find cycle start | O(n) | O(1) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Happy Number | Easy | Detect cycle in digit-square sequence |
| 2 | Linked List Cycle Detection | Easy | Fast meets slow → cycle exists |
| 3 | Middle of Linked List | Easy | Slow at middle when fast reaches end |
| 4 | Palindrome Linked List | Easy | Find middle, reverse second half, compare |
| 5 | Remove Nth Node From End | Easy | Fast starts n ahead of slow |
| 6 | Intersection of Two Linked Lists | Easy | Equalize lengths, then advance together |
| 7 | Circular Array Loop | Easy | Detect cycle with consistent direction |
| 8 | Check If Double Exists | Easy | HashSet lookup for 2x or x/2 |
| 9 | Power of Two | Easy | Bit trick: n & (n-1) == 0 |
| 10 | Count Nodes in Complete Binary Tree | Easy | Binary search on last level |
| 11 | Find Cycle Start (Cycle II) | Medium | Reset one pointer to head after meeting |
| 12 | Reorder List | Medium | Find mid, reverse second, merge |
| 13 | Sort List (Merge Sort) | Medium | Split at mid, merge sorted halves |
| 14 | Add Two Numbers II | Medium | Reverse, add, reverse back |
| 15 | Odd Even Linked List | Medium | Separate odd/even indexed nodes |
| 16 | Delete Middle Node | Medium | Slow stops before middle |
| 17 | Rotate List by K | Medium | Find tail, connect, break at new tail |
| 18 | Swap Nodes in Pairs | Medium | Swap adjacent pairs recursively |
| 19 | Partition List | Medium | Two dummy lists: < x and >= x |
| 20 | Remove Duplicates from Sorted List II | Medium | Skip all nodes with duplicate values |
| 21 | Find Duplicate Number | Hard | Floyd's on index-value mapping |
| 22 | Cycle Entry + Length Detection | Hard | Count cycle length after meeting |
| 23 | Split Linked List into K Parts | Hard | Distribute extra nodes to first parts |
| 24 | Flatten Multilevel List | Hard | DFS-style flatten with child pointers |
| 25 | Random Node (Reservoir Sampling) | Hard | Equal probability with single pass |
| 26 | Copy List with Random Pointer | Hard | Interleave copies, set random, split |
| 27 | Merge K Sorted Lists | Hard | Divide and conquer merge |
| 28 | Reverse Alternating K-Groups | Hard | Reverse every other group of k |
| 29 | LRU Cache | Hard | Doubly linked list + hashmap |
| 30 | Detect and Remove Loop | Hard | Find meeting point, trace to loop start |

## Key Insight
> Slow moves 1 step, fast moves 2 steps. They meet inside a cycle; reset one to head to find cycle entry.

# Linked Lists

## What is it?
A linear data structure where each element (node) stores a value and a pointer to the next node. Unlike arrays, linked lists allow O(1) insertion/deletion at known positions but require O(n) traversal for random access. Key variants: singly-linked, doubly-linked, circular.

## When to Use
- Frequent insertion/deletion at head or in the middle
- Problems requiring in-place reversal or reordering
- Detecting cycles (fast/slow pointers)
- Merging or splitting ordered sequences
- Implementing stacks, queues, LRU caches

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Access by index | O(n) | — |
| Insert at head | O(1) | O(1) |
| Insert at position | O(n) | O(1) |
| Delete known node | O(1) | O(1) |
| Search | O(n) | — |
| Reverse | O(n) | O(1) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Reverse Linked List | Easy | Iterative prev/cur pointer swap |
| 2 | Merge Two Sorted Lists | Easy | Dummy head, compare and link smaller |
| 3 | Linked List Cycle | Easy | Fast/slow pointers meet if cycle exists |
| 4 | Middle of Linked List | Easy | Slow moves 1, fast moves 2 |
| 5 | Remove Nth Node From End | Easy | Two-pointer gap of n |
| 6 | Palindrome Linked List | Easy | Reverse second half, compare |
| 7 | Remove Duplicates from Sorted List | Easy | Skip consecutive equal nodes |
| 8 | Intersection of Two Linked Lists | Easy | Two-pass switchover aligns lengths |
| 9 | Delete Node (given only node) | Easy | Copy next value, delete next |
| 10 | Linked List Length | Easy | Simple traversal counter |
| 11 | Add Two Numbers | Medium | Digit-by-digit sum with carry |
| 12 | Odd Even Linked List | Medium | Separate odd/even indexed nodes, merge |
| 13 | Swap Nodes in Pairs | Medium | Rewire pairs with prev pointer |
| 14 | Linked List Cycle II | Medium | Floyd's: after meet, reset one to head |
| 15 | Rotate List | Medium | Form ring, break at len-k |
| 16 | Partition List | Medium | Two dummy lists: less / greater |
| 17 | Flatten Multilevel List (simplified) | Medium | Simplified: converts negative values to absolute |
| 18 | Sort List (Merge Sort) | Medium | Split at mid, recursively merge |
| 19 | Remove Duplicates II (all copies) | Medium | Prev pointer skips duplicate runs |
| 20 | Insertion Sort List | Medium | Build sorted list by inserting each node |
| 21 | Reverse Nodes in K-Group | Hard | Reverse k at a time, recurse on rest |
| 22 | Merge K Sorted Lists | Hard | Min-heap of list heads |
| 23 | Copy List with Random Pointer | Hard | Interleave copies, set random, separate |
| 24 | LRU Cache | Hard | LinkedHashMap with access-order eviction |
| 25 | Reverse Linked List II | Hard | Reverse sublist between positions |
| 26 | Reorder List | Hard | Split, reverse second half, interleave |
| 27 | Remove Zero Sum Consecutive Nodes | Hard | Prefix sum map to skip zero-sum ranges |
| 28 | Split Linked List in Parts | Hard | Distribute extra nodes to first parts |
| 29 | Swap Kth Node From Begin and End | Hard | Two-pass to find kth from each end |
| 30 | Maximum Twin Sum | Hard | Reverse second half, pair-sum with first |

## Key Insight
> Most linked list problems revolve around pointer manipulation. The three power techniques are: (1) dummy head to simplify edge cases, (2) fast/slow pointers for cycle and midpoint detection, and (3) in-place reversal to avoid extra space.

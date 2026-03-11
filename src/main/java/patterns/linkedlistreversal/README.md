# Pattern 5: Linked List In-Place Reversal

## What is it?
Reverse linked list nodes by re-wiring prev/curr/next pointers without extra data structures.

## When to Use
- Reverse entire list or sub-list between positions
- Reverse in groups of K
- Palindrome check on linked list
- Reorder or merge lists

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Full reversal | O(n) | O(1) |
| Partial reversal | O(n) | O(1) |
| K-group reversal | O(n) | O(1) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Reverse Linked List | Easy | Iterative prev/curr/next swap |
| 2 | Reverse List (Recursive) | Easy | Recurse to end, reverse on backtrack |
| 3 | Remove Duplicates from Sorted List | Easy | Skip consecutive equal nodes |
| 4 | Remove Linked List Elements | Easy | Dummy head, skip matching values |
| 5 | Merge Two Sorted Lists | Easy | Compare heads, append smaller |
| 6 | Binary Number in LL to Integer | Easy | Shift result left, add bit |
| 7 | Delete Node (given node only) | Easy | Copy next value, skip next node |
| 8 | Linked List Length | Easy | Count nodes in traversal |
| 9 | Get Kth Node from End | Easy | Two-pointer with k-gap |
| 10 | Insert at Position | Easy | Traverse to position, rewire |
| 11 | Reverse Between Positions | Medium | Locate start, reverse sub-list |
| 12 | Swap Nodes in Pairs | Medium | Swap adjacent pairs iteratively |
| 13 | Palindrome Linked List | Medium | Reverse second half, compare |
| 14 | Odd Even Linked List | Medium | Separate odd/even indexed nodes |
| 15 | Rotate List | Medium | Form cycle, break at new tail |
| 16 | Partition List | Medium | Two lists: less-than and greater-equal |
| 17 | Remove Duplicates II | Medium | Skip all copies of duplicate values |
| 18 | Add Two Numbers | Medium | Digit-by-digit addition with carry |
| 19 | Insertion Sort List | Medium | Insert each node into sorted portion |
| 20 | Next Greater Node | Medium | Stack to find next greater value |
| 21 | Reverse Nodes in K-Group | Hard | Reverse every k nodes, handle remainder |
| 22 | Merge K Sorted Lists | Hard | Divide and conquer or min-heap |
| 23 | Reverse Alternating K-Group | Hard | Reverse odd groups, skip even groups |
| 24 | Sort List (Merge Sort) | Hard | Split at mid, merge sorted halves |
| 25 | Reorder List | Hard | Find mid, reverse second half, interleave |
| 26 | Flatten Nested List Iterator | Hard | Recursively flatten nested structure |
| 27 | Reverse in Variable Groups | Hard | Different group sizes per iteration |
| 28 | Swap Kth Nodes | Hard | Find kth from start and end, swap values |
| 29 | Remove Zero Sum Consecutive | Hard | Prefix sum to detect zero-sum segments |
| 30 | Maximum Twin Sum | Hard | Reverse second half, sum with first half |

## Key Insight
> Track `prev`, `curr`, `next`. Reverse by setting `curr.next = prev`, then advance all three.

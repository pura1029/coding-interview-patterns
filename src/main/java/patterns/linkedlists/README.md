# Linked Lists

## What is it?

A linked list stores elements as **nodes** connected by pointers. Unlike arrays (contiguous memory), each node can be anywhere in memory — connected only by references.

```
Array:   [10 | 20 | 30 | 40]  ← contiguous in memory, O(1) access
          ↑ index 0

Linked List:
  head → [10|→] → [20|→] → [30|→] → [40|null]
         node1     node2     node3     node4

  Each node: { value, next pointer }
  Access node3: must traverse from head → O(n)
  Insert at head: create node, point to old head → O(1)
```

> **Real-world analogy:** A train. Each car (node) is connected to the next by a coupling (pointer). You can detach cars, insert new cars, or rearrange the order without moving other cars — but to reach car #5, you must walk through cars #1-4.

---

## Three Power Techniques

### 1. Dummy Head (Simplify Edge Cases)

```
Without dummy:                With dummy:
  Need special handling for     Dummy node → real nodes
  empty list, single node,     Always have a node to point to
  insert at head                No edge cases!

  dummy → [10|→] → [20|→] → [30|null]
    ↑ return dummy.next as the new head

  Merge Two Sorted Lists:
  list1: 1 → 3 → 5
  list2: 2 → 4 → 6

  dummy → ?
  Compare 1 vs 2 → link 1. dummy → 1 →
  Compare 3 vs 2 → link 2. dummy → 1 → 2 →
  Compare 3 vs 4 → link 3. dummy → 1 → 2 → 3 →
  ...
  Result: dummy → 1 → 2 → 3 → 4 → 5 → 6
```

### 2. Fast/Slow Pointers

```
Find middle:
  slow moves 1 step, fast moves 2 steps.
  When fast reaches end, slow is at middle.

  1 → 2 → 3 → 4 → 5
  s    f              Step 0
     s       f        Step 1
        s          f  Step 2: fast at end, slow at middle (3) ✅

Detect cycle:
  1 → 2 → 3 → 4 → 5
              ↑       |
              └───────┘  (cycle: 5 → 3)

  slow: 1→2→3→4→5→3→4
  fast: 1→3→5→4→3→5→4
  They MEET at 4 → cycle exists!

  Find cycle start: reset one pointer to head,
  both move 1 step. They meet at cycle entry (3).
```

### 3. In-Place Reversal

```
Reverse linked list:
  1 → 2 → 3 → 4 → null

  prev = null, curr = 1

  Step 1: next = 2, curr.next = prev
          null ← 1   2 → 3 → 4
          prev = 1, curr = 2

  Step 2: next = 3, curr.next = prev
          null ← 1 ← 2   3 → 4
          prev = 2, curr = 3

  Step 3: next = 4, curr.next = prev
          null ← 1 ← 2 ← 3   4
          prev = 3, curr = 4

  Step 4: next = null, curr.next = prev
          null ← 1 ← 2 ← 3 ← 4
          prev = 4, curr = null → DONE

  Result: 4 → 3 → 2 → 1 → null ✅
```

---

## Real-World Applications

| Domain        | Application                  | Why Linked List?                  |
| ------------- | ---------------------------- | --------------------------------- |
| **OS**        | Process scheduling queue     | O(1) insert/remove at ends        |
| **Browsers**  | Back/forward navigation      | Doubly-linked list of pages       |
| **Editors**   | Undo/redo history            | Doubly-linked command list        |
| **Caching**   | LRU Cache                    | Doubly-linked list + HashMap      |
| **Music**     | Playlist (next/prev song)    | Doubly-linked, circular variant   |

---

## When to Use

- **Frequent insertion/deletion** at head or in the middle (O(1) vs array's O(n))
- **In-place reversal or reordering** — pointer manipulation avoids extra space
- **Cycle detection** — fast/slow pointers in O(n) time, O(1) space
- **Merging or splitting** ordered sequences
- **Implementing data structures** — stacks, queues, LRU caches

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

> Most linked list problems revolve around **pointer manipulation**. The three power techniques are: (1) **dummy head** to eliminate edge cases, (2) **fast/slow pointers** for cycle detection and midpoint, and (3) **in-place reversal** to avoid extra space. Draw the pointers on paper before coding — pointer bugs are the #1 source of errors.

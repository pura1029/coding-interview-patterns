# Pattern 4: Fast & Slow Pointers (Floyd's Tortoise and Hare)

## What is it?

Two pointers move at **different speeds** through a sequence. The slow pointer moves 1 step, the fast pointer moves 2 steps. This technique detects cycles, finds midpoints, and solves linked list problems in O(n) time, O(1) space.

```
Finding middle of linked list:

  1 → 2 → 3 → 4 → 5 → null
  s                           Step 0
  f

  1 → 2 → 3 → 4 → 5 → null
      s                       Step 1
          f

  1 → 2 → 3 → 4 → 5 → null
          s                   Step 2: fast at end → slow at middle ✅
                    f
```

> **Real-world analogy:** Two runners on a circular track. The faster runner (2x speed) will eventually lap the slower runner — proving the track is circular (cycle detection). They meet at a specific point that reveals the start of the loop.

---

## The Three Key Applications

### 1. Cycle Detection

```
List with cycle:  1 → 2 → 3 → 4 → 5
                              ↑       |
                              └───────┘

  slow: 1 → 2 → 3 → 4 → 5 → 3 → 4
  fast: 1 → 3 → 5 → 4 → 3 → 5 → 4

  Step 0: s=1, f=1
  Step 1: s=2, f=3
  Step 2: s=3, f=5
  Step 3: s=4, f=4  ← MEET! Cycle exists ✅

  WHY THEY MUST MEET:
  Once both are in the cycle, fast gains 1 node per step.
  If cycle length is C, fast catches slow in at most C steps.
```

### 2. Find Cycle Entry Point

```
After detecting cycle (they meet at node M):

  1 → 2 → 3 → 4 → 5
              ↑       |
              └───────┘
  Meeting point = 4

  Reset slow to head. Both move 1 step at a time.
  slow: 1 → 2 → 3
  ptr:  4 → 5 → 3  ← MEET at 3 = cycle entry ✅

  WHY THIS WORKS (math):
  Let x = distance from head to cycle start
  Let y = distance from cycle start to meeting point
  Let C = cycle length

  slow traveled: x + y
  fast traveled: x + y + nC (for some n)
  fast = 2 × slow → x + y + nC = 2(x + y) → nC = x + y → x = nC - y

  So walking x from head = walking nC - y from meeting point
  = arriving at cycle start!
```

### 3. Find Duplicate Number (Floyd's on Array)

```
nums = [1, 3, 4, 2, 2]    (values 1-4, one duplicate)

Treat array as a linked list: index → nums[index]
  0 → 1 → 3 → 2 → 4 → 2 → 4 → 2 ...
                    ↑              |
                    └──────────────┘  (cycle because 2 appears twice)

  Phase 1: find meeting point
    slow = nums[0] = 1, fast = nums[nums[0]] = 3
    slow = nums[1] = 3, fast = nums[nums[3]] = 4
    slow = nums[3] = 2, fast = nums[nums[4]] = 4
    slow = nums[2] = 4, fast = nums[nums[2]] = 2
    slow = nums[4] = 2, fast = nums[nums[4]] = 2  MEET at 2

  Phase 2: find cycle entry
    slow = 0, ptr = 2
    slow = nums[0] = 1, ptr = nums[2] = 4
    slow = nums[1] = 3, ptr = nums[4] = 2
    slow = nums[3] = 2, ptr = nums[2] = 4 ... → entry = 2

  Duplicate = 2 ✅ (O(n) time, O(1) space, no modifying array)
```

---

## Happy Number (Cycle in Sequence)

```
Is 19 a happy number? (digit squares eventually reach 1)

  19 → 1²+9² = 82 → 8²+2² = 68 → 6²+8² = 100 → 1²+0²+0² = 1 ✅

Not happy (7 is happy, but 2 is not):
  2 → 4 → 16 → 37 → 58 → 89 → 145 → 42 → 20 → 4 → ... CYCLE! ❌

  Use fast/slow pointers on the digit-square sequence:
  slow = next(n), fast = next(next(n))
  If they meet at 1 → happy. If they meet elsewhere → cycle → not happy.
```

---

## When to Use

- **Cycle detection** in linked lists or sequences (O(n) time, O(1) space)
- **Finding middle** of linked list (slow at center when fast hits end)
- **Palindrome verification** — find mid, reverse second half, compare
- **Duplicate number** — Floyd's algorithm on index-value graph
- **Happy number** — cycle detection in digit-square sequence

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

> **Slow moves 1 step, fast moves 2 steps.** They meet inside a cycle (guaranteed by the pigeonhole principle — fast closes the gap by 1 each step). To find the cycle entry, reset one pointer to head and advance both at speed 1 — they meet at the entry point. This mathematical property enables O(1) space cycle detection.

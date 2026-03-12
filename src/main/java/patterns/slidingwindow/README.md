# Pattern 3: Sliding Window

## What is it?

Sliding window maintains a **dynamic window** (subarray/substring) over data, expanding and shrinking to find optimal solutions in O(n) — avoiding the O(n²) brute force of checking every subarray.

```
Brute Force (check every subarray):
  [a, b, c, d, e]
  [a]  [a,b]  [a,b,c]  [a,b,c,d]  [a,b,c,d,e]
       [b]    [b,c]     [b,c,d]    [b,c,d,e]
              [c]       [c,d]      [c,d,e]
                        [d]        [d,e]
                                   [e]
  = n(n+1)/2 subarrays → O(n²)

Sliding Window (two pointers):
  [a, b, c, d, e]
   L        R         expand R →
      L     R         shrink L → (when constraint violated)

  Each element enters and leaves the window at most once → O(n)
```

> **Real-world analogy:** Reading a book through a magnifying glass. You slide the glass along the text — you never go back to re-read. The glass is the "window" and the text is the array.

---

## Two Types of Sliding Window

### Type 1: Fixed-Size Window

Window size is fixed at k. Slide by adding one element on the right and removing one on the left.

```
Max Sum Subarray of Size 3:
  arr = [2, 1, 5, 1, 3, 2]    k = 3

  Window 1: [2, 1, 5]       sum = 8
  Window 2:    [1, 5, 1]    sum = 8-2+1 = 7
  Window 3:       [5, 1, 3] sum = 7-1+3 = 9 ← max
  Window 4:          [1, 3, 2] sum = 9-5+2 = 6

  ┌─────────┐
  │ 2  1  5 │ 1  3  2     sum=8
  └─────────┘
     ┌─────────┐
   2 │ 1  5  1 │ 3  2     sum=7  (subtract 2, add 1)
     └─────────┘
        ┌─────────┐
   2  1 │ 5  1  3 │ 2     sum=9  (subtract 1, add 3) ← MAX
        └─────────┘
           ┌─────────┐
   2  1  5 │ 1  3  2 │    sum=6  (subtract 5, add 2)
           └─────────┘

  Answer: 9
```

### Type 2: Variable-Size Window

Window expands and shrinks based on a constraint. Two sub-patterns:

```
LONGEST subarray/substring satisfying condition:
  Expand R always.
  Shrink L when constraint is VIOLATED.
  Track max window size.

SHORTEST subarray/substring satisfying condition:
  Expand R until constraint is SATISFIED.
  Shrink L while constraint is STILL satisfied.
  Track min window size.
```

---

## Classic Problems — Visualized

### Longest Substring Without Repeating Characters

```
s = "abcabcbb"

  Window    Chars in window     Action              Length
  ──────    ───────────────     ──────              ──────
  [a]       {a}                 expand R             1
  [a,b]     {a,b}              expand R             2
  [a,b,c]   {a,b,c}            expand R             3 ← max so far
  [a,b,c,a] {a,b,c} + a=dup!  shrink L past 'a'
  [b,c,a]   {b,c,a}            expand R             3
  [b,c,a,b] {b,c,a} + b=dup!  shrink L past 'b'
  [c,a,b]   {c,a,b}            expand R             3
  [c,a,b,c] {c,a,b} + c=dup!  shrink L past 'c'
  [a,b,c]   {a,b,c}            expand R             3
  [a,b,c,b] dup!              shrink L past 'b'
  [c,b]     {c,b}              expand R             2
  [c,b,b]   dup!              shrink L past 'b'
  [b]       {b}                 end                  1

  Answer: 3

  a  b  c  a  b  c  b  b
  L─────R                    window "abc" = 3 ✅
```

### Minimum Window Substring

```
s = "ADOBECODEBANC", t = "ABC"

Need shortest substring of s containing all chars of t.

  need: {A:1, B:1, C:1}    have: {}    matched = 0 (need 3)

  Expand R until all chars matched:
  A → have={A:1} matched=1
  D → skip
  O → skip
  B → have={A:1,B:1} matched=2
  E → skip
  C → have={A:1,B:1,C:1} matched=3 ✅ → window="ADOBEC" len=6

  Shrink L while still valid:
  Remove A → matched=2 ❌ → stop. Best = "ADOBEC" (6)

  Continue expanding R...
  ...eventually find "BANC" (4) ← shorter!

  A  D  O  B  E  C  O  D  E  B  A  N  C
  L──────────────R                         "ADOBEC" (6)
                              L────────R   "BANC" (4) ✅

  Answer: "BANC"
```

### Sliding Window Maximum (Monotonic Deque)

```
nums = [1, 3, -1, -3, 5, 3, 6, 7],  k = 3

Maintain a DECREASING deque of indices.
Front of deque = index of max in current window.

  Window [1,3,-1]:   deque=[3,-1] → max=3
  Window [3,-1,-3]:  deque=[3,-1,-3] → max=3
  Window [-1,-3,5]:  deque=[5] → max=5  (5 > -1 and -3, clear deque)
  Window [-3,5,3]:   deque=[5,3] → max=5
  Window [5,3,6]:    deque=[6] → max=6
  Window [3,6,7]:    deque=[7] → max=7

  Result: [3, 3, 5, 5, 6, 7]

  Why deque? Normal approach: scan k elements per window → O(nk).
  Monotonic deque: each element enters/exits deque once → O(n).
```

---

## The "At Most K" Trick

To count subarrays with **exactly K** distinct elements: `atMost(k) - atMost(k-1)`.

```
Problem: Subarrays with Exactly K=2 Different Integers
  arr = [1, 2, 1, 2, 3]

  atMost(2):  subarrays with ≤ 2 distinct = 12
  atMost(1):  subarrays with ≤ 1 distinct = 5
  exactly(2): 12 - 5 = 7 ✅

  Why? "At most K" is easy with sliding window (shrink when distinct > K).
  "Exactly K" is hard directly, but the subtraction trick works perfectly.
```

---

## When to Recognize Sliding Window

```
Signal words in the problem:
  ✅ "subarray" or "substring"
  ✅ "contiguous"
  ✅ "longest" / "shortest" / "minimum length"
  ✅ "at most K" / "exactly K"
  ✅ "window of size K"
  ✅ "consecutive"

NOT sliding window:
  ❌ "subsequence" (elements don't need to be contiguous)
  ❌ "combination" / "permutation" (backtracking)
  ❌ Need to look at all subarrays equally (prefix sum)
```

---

## Real-World Applications

| Domain          | Application                              | Window Type              |
| --------------- | ---------------------------------------- | ------------------------ |
| **Networking**  | TCP sliding window (flow control)        | Fixed window, adjustable |
| **Monitoring**  | Moving average of CPU/memory usage       | Fixed-size window        |
| **Finance**     | 30-day rolling average stock price       | Fixed-size window        |
| **Streaming**   | Count unique visitors in last 5 minutes  | Variable window          |
| **Security**    | Detect brute-force (5 failures in 1 min) | Fixed time window        |
| **Databases**   | Window functions (SUM OVER, RANK OVER)   | Fixed/variable           |

---

## When to Use

- **Fixed-size window aggregates** — max sum, average, count of size k
- **Longest/shortest substring** — with constraints on distinct chars, frequency
- **Anagram/permutation finding** — fixed window with frequency matching
- **At-most-K distinct** — variable window, shrink when exceeding k
- **Minimum size subarray** — variable window, shrink while condition met
- **Sliding window maximum/minimum** — monotonic deque

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Fixed window | O(n) | O(1) |
| Variable window | O(n) | O(k) where k = distinct elements |
| Monotonic deque | O(n) | O(k) where k = window size |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Max Sum Subarray of Size K | Easy | Fixed window, slide and update |
| 2 | Average of Subarrays of Size K | Easy | Running sum / k |
| 3 | Max Vowels in Substring of Size K | Easy | Track vowel count in window |
| 4 | Min Recolors for K Consecutive Black | Easy | Count whites in window |
| 5 | Contains Duplicate II | Easy | HashSet window of size k |
| 6 | Subarrays with Avg ≥ Threshold | Easy | Fixed window sum check |
| 7 | Diet Plan Performance | Easy | Sliding sum comparison |
| 8 | Defuse the Bomb | Easy | Circular window sum |
| 9 | Find K-Distant Indices | Easy | Mark indices within distance k |
| 10 | Grumpy Bookstore Owner | Easy | Max gain from k-window non-grumpy |
| 11 | Longest Substring Without Repeating | Medium | HashSet tracks window chars |
| 12 | Longest Repeating Character Replacement | Medium | Max freq char + k replacements |
| 13 | Fruit Into Baskets (at most 2 types) | Medium | At most 2 distinct in window |
| 14 | Max Consecutive Ones III | Medium | Flip at most k zeros |
| 15 | Permutation in String | Medium | Fixed window frequency match |
| 16 | Find All Anagrams | Medium | Sliding frequency comparison |
| 17 | Longest Substring K Distinct | Medium | Shrink when distinct > k |
| 18 | Minimum Size Subarray Sum | Medium | Shrink while sum ≥ target |
| 19 | Subarray Product Less Than K | Medium | Expand while product < k |
| 20 | K Radius Subarray Averages | Medium | Prefix sum for window avg |
| 21 | Minimum Window Substring | Hard | Two-pointer with frequency count |
| 22 | Sliding Window Maximum | Hard | Monotonic deque for max |
| 23 | Substring Concatenation of All Words | Hard | Fixed-size word window matching |
| 24 | Longest Substring Two Distinct | Hard | At most 2 distinct characters |
| 25 | Count Subarrays With Fixed Bounds | Hard | Track last positions of min/max |
| 26 | Minimum Window Subsequence | Hard | Two-pointer forward + backward |
| 27 | Max Value of Equation | Hard | Monotonic deque + constraint |
| 28 | Subarrays with K Different Integers | Hard | atMost(k) - atMost(k-1) |
| 29 | Min K Consecutive Bit Flips | Hard | Greedy flip with sliding window |
| 30 | Longest Substring At Least K Repeating | Hard | Divide-and-conquer or enumerate unique chars |

## Key Insight

> Expand right pointer to grow the window, shrink left pointer when constraint is violated. Each element enters and leaves at most once → O(n). The key decisions are: (1) **what to track** in the window (hashmap, counter, deque), (2) **when to shrink** (constraint violated), and (3) **when to record the answer** (valid window found).

# Pattern 20: Dynamic Programming

## What is it?

Dynamic programming (DP) breaks problems into **overlapping subproblems**, stores results to avoid recomputation. It converts exponential brute force into polynomial time.

```
Fibonacci WITHOUT DP (exponential):

             fib(5)
            /      \
        fib(4)     fib(3)      ← fib(3) computed TWICE
        /    \      /   \
    fib(3) fib(2) fib(2) fib(1)  ← fib(2) computed THREE times
    ...                           Total calls: O(2^n)

Fibonacci WITH DP (linear):

  dp[0] = 0, dp[1] = 1
  dp[2] = dp[0] + dp[1] = 1     ← computed ONCE, stored
  dp[3] = dp[1] + dp[2] = 2     ← reuses stored dp[2]
  dp[4] = dp[2] + dp[3] = 3
  dp[5] = dp[3] + dp[4] = 5     Total: O(n)
```

> **Real-world analogy:** Instead of re-deriving 7 × 8 = 56 every time, you memorize it. DP memorizes solutions to subproblems so you never solve the same thing twice.

---

## Two Approaches

### Top-Down (Memoization)

```
Start from the big problem, recurse down, cache results.

function fib(n, memo):
    if n in memo → return memo[n]
    memo[n] = fib(n-1, memo) + fib(n-2, memo)
    return memo[n]

  fib(5) → fib(4) → fib(3) → fib(2) → fib(1) → return 1
                                       → fib(0) → return 0
                              → return 1
                     → fib(2) → CACHED! return 1
            → return 3
           → fib(3) → CACHED! return 2
  → return 5
```

### Bottom-Up (Tabulation)

```
Start from smallest subproblems, build up.

  dp[0] = 0
  dp[1] = 1
  for i = 2 to n:
      dp[i] = dp[i-1] + dp[i-2]

  No recursion, no stack overflow, generally faster.
  Space optimization: only need dp[i-1] and dp[i-2] → O(1) space!
```

---

## DP Sub-Patterns

### 1D DP — Climbing Stairs / House Robber

```
Climbing Stairs: n=4, can climb 1 or 2 steps

  dp[i] = ways to reach step i
  dp[0]=1, dp[1]=1
  dp[2] = dp[1] + dp[0] = 2   (1+1 or 2)
  dp[3] = dp[2] + dp[1] = 3   (1+1+1, 1+2, 2+1)
  dp[4] = dp[3] + dp[2] = 5   (1+1+1+1, 1+1+2, 1+2+1, 2+1+1, 2+2)

House Robber: [2, 7, 9, 3, 1]

  dp[i] = max money robbing houses 0..i (can't rob adjacent)
  dp[0] = 2
  dp[1] = max(2, 7) = 7
  dp[2] = max(dp[1], dp[0]+9) = max(7, 11) = 11  ← rob 2 and 9
  dp[3] = max(dp[2], dp[1]+3) = max(11, 10) = 11
  dp[4] = max(dp[3], dp[2]+1) = max(11, 12) = 12 ← rob 2, 9, 1

  Answer: 12 (houses 0, 2, 4)
```

### 2D DP — Longest Common Subsequence

```
LCS of "ABCDE" and "ACE":

        ""  A  C  E
    ""   0  0  0  0
    A    0  1  1  1   ← A matches A
    B    0  1  1  1
    C    0  1  2  2   ← C matches C
    D    0  1  2  2
    E    0  1  2  3   ← E matches E

  dp[i][j] = chars match? dp[i-1][j-1]+1 : max(dp[i-1][j], dp[i][j-1])

  Answer: 3 ("ACE")
```

### Knapsack — 0/1 Knapsack

```
Items: weight=[1,2,3], value=[6,10,12], capacity=5

        cap: 0  1  2  3  4  5
  item 0:   0  6  6  6  6  6    ← item 0 (w=1, v=6)
  item 1:   0  6 10 16 16 16    ← item 1 (w=2, v=10)
  item 2:   0  6 10 16 18 22    ← item 2 (w=3, v=12)

  dp[i][w] = max(
    dp[i-1][w],              ← skip item i
    dp[i-1][w-weight[i]] + value[i]  ← take item i
  )

  Answer: 22 (items 1 and 2: weight=2+3=5, value=10+12=22)
```

---

## The 4-Step DP Framework

```
1. DEFINE STATE: what information do you need?
   "dp[i] = maximum profit considering items 0..i"

2. WRITE RECURRENCE: how does dp[i] relate to smaller states?
   "dp[i] = max(dp[i-1], dp[i-2] + value[i])"

3. BASE CASES: what are the trivial answers?
   "dp[0] = value[0], dp[1] = max(value[0], value[1])"

4. OPTIMIZE SPACE: do you need the full table?
   "Only need dp[i-1] and dp[i-2] → two variables"
```

---

## Real-World Applications

| Domain          | Application                        | DP Pattern                       |
| --------------- | ---------------------------------- | -------------------------------- |
| **Text Editors**| Spell check (edit distance)        | 2D DP on two strings             |
| **Finance**     | Portfolio optimization             | Knapsack (asset allocation)      |
| **Bioinformatics**| DNA sequence alignment           | LCS / edit distance              |
| **Routing**     | Shortest path (Bellman-Ford)       | 1D DP over edges                 |
| **Compression** | Optimal encoding                   | Huffman + DP                     |
| **Gaming**      | Pathfinding with costs             | 2D grid DP                       |

---

## Sub-patterns

- **1D DP**: Fibonacci-like, climbing stairs, house robber
- **2D DP**: Grid paths, LCS, edit distance
- **Knapsack**: 0/1 knapsack, subset sum, coin change
- **String DP**: Palindromes, regex matching, word break
- **Interval DP**: Burst balloons, matrix chain multiplication

## When to Use

- **Optimal substructure** + **overlapping subproblems**
- Counting paths, ways, or combinations
- Min/max optimization with choices
- String matching and transformation

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| 1D DP | O(n) | O(n) or O(1) |
| 2D DP | O(n × m) | O(n × m) or O(m) |
| Knapsack | O(n × W) | O(W) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Climbing Stairs | Easy | dp[i] = dp[i-1] + dp[i-2] |
| 2 | Fibonacci Number | Easy | Base case + recurrence |
| 3 | Min Cost Climbing Stairs | Easy | min(dp[i-1], dp[i-2]) + cost[i] |
| 4 | Maximum Subarray (Kadane's) | Easy | Extend or restart at each element |
| 5 | House Robber | Easy | Rob or skip: max(dp[i-1], dp[i-2]+val) |
| 6 | Best Time to Buy/Sell Stock | Easy | Track min price, max profit |
| 7 | Counting Bits | Easy | dp[i] = dp[i>>1] + (i&1) |
| 8 | Is Subsequence | Easy | Two-pointer or DP matching |
| 9 | Tribonacci Number | Easy | dp[i] = dp[i-1] + dp[i-2] + dp[i-3] |
| 10 | Pascal's Triangle | Easy | dp[r][c] = dp[r-1][c-1] + dp[r-1][c] |
| 11 | Longest Increasing Subsequence | Medium | DP or binary search patience sort |
| 12 | Coin Change | Medium | dp[amount] = min dp[amount-coin] + 1 |
| 13 | 0/1 Knapsack | Medium | Include/exclude item, 1D rolling array |
| 14 | Longest Common Subsequence | Medium | 2D DP: match or max(skip one) |
| 15 | Unique Paths | Medium | dp[i][j] = dp[i-1][j] + dp[i][j-1] |
| 16 | Word Break | Medium | dp[i] = any dp[j] && word[j..i] in dict |
| 17 | House Robber II (circular) | Medium | Two passes: skip first or last |
| 18 | Decode Ways | Medium | dp[i] based on 1-digit and 2-digit splits |
| 19 | Partition Equal Subset Sum | Medium | 0/1 knapsack for target = sum/2 |
| 20 | Longest Palindromic Substring | Medium | Expand around center or 2D DP |
| 21 | Edit Distance | Hard | dp[i][j] = min(insert, delete, replace) |
| 22 | Regular Expression Matching | Hard | DP on pattern with '.' and '*' |
| 23 | Longest Increasing Path in Matrix | Hard | DFS + memo on each cell |
| 24 | Burst Balloons | Hard | Interval DP: choose last balloon to pop |
| 25 | Min Cost to Cut a Stick | Hard | Interval DP on cut positions |
| 26 | Wildcard Matching | Hard | DP on '?' (any char) and '*' (any seq) |
| 27 | Interleaving String | Hard | 2D DP: s1[i] or s2[j] matches s3[i+j] |
| 28 | Distinct Subsequences | Hard | Count ways to form t from s |
| 29 | Maximum Profit in Job Scheduling | Hard | Sort by end, binary search + DP |
| 30 | Palindrome Partitioning II | Hard | Min cuts: precompute palindrome table |

## Key Insight

> **Define state clearly** (what changes), **write recurrence** (how states relate), **handle base cases**, then **optimize space** if only the previous row/state is needed. Start with brute-force recursion → add memoization → convert to tabulation. If you can solve it recursively and see overlapping subproblems, it's DP.

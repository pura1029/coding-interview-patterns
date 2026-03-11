# Pattern 20: Dynamic Programming

## What is it?
Break problems into overlapping subproblems, store results to avoid recomputation. Two approaches: Memoization (top-down) and Tabulation (bottom-up).

## Sub-patterns
- **1D DP**: Fibonacci-like, climbing stairs, house robber
- **2D DP**: Grid paths, LCS, edit distance
- **Knapsack**: 0/1 knapsack, subset sum, coin change
- **String DP**: Palindromes, regex matching, word break
- **Interval DP**: Burst balloons, matrix chain multiplication

## When to Use
- Optimal substructure + overlapping subproblems
- Counting paths, ways, or combinations
- Min/max optimization with choices
- String matching and transformation

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| 1D DP | O(n) | O(n) or O(1) |
| 2D DP | O(n × m) | O(n × m) |
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
> Define state clearly (what changes), write recurrence, optimize space if only previous row/state is needed. Start with brute-force recursion → add memoization → convert to tabulation.

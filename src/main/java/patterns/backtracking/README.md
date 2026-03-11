# Pattern 17: Backtracking

## What is it?
Systematically explores all possibilities by building solutions incrementally and abandoning invalid paths early (pruning).

## When to Use
- Permutations, combinations, subsets
- Constraint satisfaction (N-Queens, Sudoku)
- Word search, expression generation
- Partitioning problems

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Subsets / Combinations | O(2^n) | O(n) |
| Permutations | O(n!) | O(n) |
| N-Queens | O(n!) | O(n) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Generate Parentheses (simplified) | Easy | Track open/close count |
| 2 | Phone Number Letters (single digit) | Easy | Map digit to letters |
| 3 | Binary Watch | Easy | Count bits in hour+minute |
| 4 | Subsets | Easy | Include/exclude each element |
| 5 | Permutations | Easy | Swap or visited-array approach |
| 6 | Combinations C(n,k) | Easy | Choose k from n with start index |
| 7 | Combination Sum (unlimited) | Easy | Reuse same element, no going back |
| 8 | Path Sum II | Easy | Collect paths matching target sum |
| 9 | Count Max OR Subsets | Easy | Track max OR, count subsets achieving it |
| 10 | Jump Game (can reach 0) | Easy | Simple backtrack or greedy |
| 11 | Generate Parentheses | Medium | Track open/close, prune invalid |
| 12 | Letter Combinations | Medium | DFS through digit-to-char mapping |
| 13 | Word Search | Medium | DFS on grid with visited tracking |
| 14 | Subsets II (with dups) | Medium | Sort + skip consecutive duplicates |
| 15 | Combination Sum II (use once) | Medium | Sort + skip duplicates at same level |
| 16 | Permutations II (with dups) | Medium | Sort + skip same value at same position |
| 17 | Palindrome Partitioning | Medium | Try all cuts, check palindrome |
| 18 | Restore IP Addresses | Medium | Try 1-3 digit segments, validate |
| 19 | Beautiful Arrangement | Medium | Count valid permutations by constraint |
| 20 | Combination Sum III | Medium | Choose k numbers summing to n |
| 21 | N-Queens | Hard | Place queens row by row, check constraints |
| 22 | Sudoku Solver | Hard | Fill cells, validate row/col/box |
| 23 | Word Search II | Hard | Trie + DFS for multiple words |
| 24 | Expression Add Operators | Hard | Insert +,-,* between digits |
| 25 | Total N-Queens | Hard | Count all valid placements |
| 26 | Partition into K Equal Subsets | Hard | Backtrack assignment to k buckets |
| 27 | Remove Invalid Parentheses | Hard | BFS removing one bracket at a time |
| 28 | Factor Combinations | Hard | Enumerate factor decompositions |
| 29 | Robot Room Cleaner | Hard | DFS with relative directions |
| 30 | Word Squares | Hard | Trie-guided row-by-row construction |

## Key Insight
> Choose → Explore → Unchoose. Skip duplicate branches with `if(i>start && nums[i]==nums[i-1]) continue`.

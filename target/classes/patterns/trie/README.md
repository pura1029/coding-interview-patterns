# Pattern 18: Trie (Prefix Tree)

## What is it?
Tree-based data structure for efficient prefix matching, autocomplete, and string operations in O(L) per word.

## When to Use
- Prefix search, autocomplete suggestions
- Word dictionary with wildcard matching
- Word break, concatenated words
- Maximum XOR (bit-level Trie)

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Insert | O(L) | O(L) |
| Search / Prefix | O(L) | O(1) |
| Build Trie | O(N × L) | O(N × L) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Implement Trie | Easy | Array of 26 children per node |
| 2 | Longest Common Prefix | Easy | Traverse Trie until branch or end |
| 3 | Word Exists in Trie | Easy | Search to end, check isEnd flag |
| 4 | Prefix Exists Check | Easy | Search stops at any valid prefix |
| 5 | Count Words With Prefix | Easy | Count words below prefix node |
| 6 | Unique Morse Representations | Easy | Map words to morse, count unique |
| 7 | Count Words by Length | Easy | Group Trie words by depth |
| 8 | All Binary Codes of Size K | Easy | HashSet of all k-length substrings |
| 9 | Index Pairs of a String | Easy | Find all dictionary word occurrences |
| 10 | Sum of Prefix Scores | Easy | Count prefix visits during insertion |
| 11 | Add/Search Words (wildcards) | Medium | DFS with '.' branching to all children |
| 12 | Replace Words (roots) | Medium | Find shortest prefix in Trie |
| 13 | Map Sum Pairs | Medium | Store values at Trie leaves, sum subtree |
| 14 | Search Suggestions (autocomplete) | Medium | DFS from prefix node, collect words |
| 15 | Maximum XOR (Trie-based) | Medium | Bit-level Trie, greedily choose opposite |
| 16 | Magic Dictionary | Medium | Search with exactly one char different |
| 17 | Stream of Characters | Medium | Reverse Trie matching suffix |
| 18 | CamelCase Matching | Medium | Trie-like pattern matching |
| 19 | Group Shifted Strings | Medium | Normalize shift pattern as key |
| 20 | Count Distinct Substrings | Medium | Insert all suffixes, count nodes |
| 21 | Word Search II (Trie+DFS) | Hard | Build Trie from words, DFS on board |
| 22 | Word Break II | Hard | Trie + DFS + memoization for all splits |
| 23 | Concatenated Words | Hard | Check if word is concat of other Trie words |
| 24 | Longest Word Built Letter by Letter | Hard | All prefixes must exist in Trie |
| 25 | Word Break I | Hard | DP with Trie/set membership check |
| 26 | Palindrome Pairs | Hard | Trie of reversed words + suffix check |
| 27 | Count Prefix-Suffix Pairs | Hard | Check both prefix and suffix conditions |
| 28 | Design File System | Hard | Trie with path segments as keys |
| 29 | Stream Checker | Hard | Reverse suffix Trie for streaming match |
| 30 | Suffix Array / Automaton | Hard | Advanced string structure for pattern search |

## Key Insight
> Each node has 26 children (a-z). Insert O(L), search O(L). For Word Search II, build Trie from words and DFS on board.

# Strings

## What is it?
Strings are immutable character sequences. String problems test parsing, pattern matching, transformation, and dynamic programming skills. Key techniques include character frequency arrays, sliding window, two-pointer, backtracking, and DP on subsequences.

## When to Use
- Substring / subsequence matching and searching
- Anagram detection and grouping
- Palindrome checking and construction
- Pattern matching (regex, wildcard)
- String transformation (edit distance, decoding)

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Access by index | O(1) | — |
| Search (substring, naive) | O(m·n) | — |
| Search (KMP / Rabin-Karp) | O(m+n) | O(m) |
| Concatenation | O(n) | O(n) |
| Character frequency count | O(n) | O(1) (26 chars) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Valid Anagram | Easy | Character frequency count array |
| 2 | Reverse String | Easy | Two-pointer swap from both ends |
| 3 | Valid Palindrome | Easy | Skip non-alphanumeric, compare ends |
| 4 | Longest Common Prefix | Easy | Shrink prefix until all strings match |
| 5 | First Unique Character | Easy | Frequency count, return first with count 1 |
| 6 | Ransom Note | Easy | Magazine char counts must cover ransom |
| 7 | Is Subsequence | Easy | Two pointers: advance on match |
| 8 | Valid Parentheses (string) | Easy | Stack-based bracket matching |
| 9 | Implement strStr (indexOf) | Easy | Sliding window comparison |
| 10 | Count and Say | Easy | Run-length encoding iteration |
| 11 | Group Anagrams | Medium | Sorted-string key → hash map grouping |
| 12 | Longest Substring Without Repeating | Medium | Sliding window with char→index map |
| 13 | String to Integer (atoi) | Medium | Trim, parse sign, digit-by-digit with overflow |
| 14 | Longest Palindromic Substring | Medium | Expand around each center |
| 15 | Generate Parentheses | Medium | Backtracking: open < n, close < open |
| 16 | Letter Combinations of Phone Number | Medium | Backtracking over digit-to-letter mapping |
| 17 | Multiply Strings | Medium | Grade-school digit-by-digit multiplication |
| 18 | Zigzag Conversion | Medium | Distribute chars by bouncing row direction |
| 19 | Decode Ways | Medium | DP: single digit + two-digit valid decodings |
| 20 | Word Break | Medium | DP with dictionary set lookup |
| 21 | Minimum Window Substring | Hard | Sliding window with need/count arrays |
| 22 | Substring with Concatenation of All Words | Hard | Fixed-length sliding window + word frequency |
| 23 | Text Justification | Hard | Greedy line packing + space distribution |
| 24 | Longest Valid Parentheses | Hard | Stack of indices, compute length on pop |
| 25 | Regular Expression Matching | Hard | DP handling '.' and '*' operators |
| 26 | Wildcard Matching | Hard | DP handling '?' and '*' operators |
| 27 | Edit Distance | Hard | DP: insert/delete/replace operations |
| 28 | Palindrome Pairs | Hard | Hash map + partial reverse palindrome check |
| 29 | Min Moves to Make Palindrome | Hard | Greedy swap characters from edges inward |
| 30 | Distinct Subsequences | Hard | DP counting subsequence occurrences |

## Key Insight
> String problems are often array problems in disguise — but immutability and character-level operations add unique challenges. Master frequency arrays (O(1) space for lowercase), sliding window for substring problems, and DP for edit/match/subsequence problems.

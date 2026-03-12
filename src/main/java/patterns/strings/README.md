# Strings

## What is it?

Strings are **immutable character sequences**. In Java, strings are backed by `char[]` arrays but cannot be modified in-place — every operation creates a new string. String problems test parsing, pattern matching, transformation, and dynamic programming.

```
Java String internals:
  String s = "hello";

  Stack:  s → [ref] ──────► Heap: char[] {'h','e','l','l','o'}
  
  s.charAt(2) = 'l'       → O(1) random access
  s + " world"            → creates NEW string → O(n)
  s.substring(1,3) = "el" → O(n) in modern Java (copies chars)

  Immutable: s.replace('l','x') returns "hexxo" but s is still "hello"
  For mutation: use StringBuilder (O(1) amortized append)
```

> **Key insight:** Because strings are immutable, use `char[]` or `StringBuilder` for in-place operations. Use `int[26]` frequency arrays instead of `HashMap<Character, Integer>` for lowercase-only problems.

---

## Essential String Techniques

### 1. Frequency Array (Anagram / Character Counting)

```
Valid Anagram: is "anagram" an anagram of "nagaram"?

  Build frequency count for each string:
  "anagram": a=3, n=1, g=1, r=1, m=1
  "nagaram": n=1, a=3, g=1, r=1, m=1

  Same frequencies? → YES, it's an anagram ✅

  int[26] freq = new int[26];
  for (char c : s1) freq[c-'a']++;    ← increment for s1
  for (char c : s2) freq[c-'a']--;    ← decrement for s2
  All zeros? → anagram!
```

### 2. Expand Around Center (Palindromes)

```
Longest Palindromic Substring: s = "babad"

  For each center, expand outward while chars match:

  Center 'b'(0): "b" → can't expand
  Center 'a'(1): "a" → "bab" ✅ (len=3)
  Center 'b'(2): "b" → "aba" ✅ (len=3)
  Center 'a'(3): "a" → "bad"? No.
  Center 'd'(4): "d" → can't expand

  Also check EVEN-length centers (between chars):
  Between (0,1): "ba"? No.
  Between (1,2): "ab"? No.
  ...

  Answer: "bab" or "aba" (length 3)

  b  a  b  a  d
     ←─ a ─→         center at index 1
  ←── b a b ──→      expand: s[0]=b, s[2]=b ✅ → "bab"
```

### 3. Edit Distance (DP on Two Strings)

```
Edit Distance: "horse" → "ros" (min operations)

      ""  r  o  s
  ""   0  1  2  3
  h    1  1  2  3
  o    2  2  1  2
  r    3  2  2  2
  s    4  3  3  2
  e    5  4  4  3  ← answer

  dp[i][j] = min(
    dp[i-1][j] + 1,     ← delete from word1
    dp[i][j-1] + 1,     ← insert into word1
    dp[i-1][j-1] + cost ← replace (cost=0 if chars equal)
  )

  Operations: horse → rorse (replace h→r) → rose (delete r) → ros (delete e) = 3
```

---

## Pattern Recognition for String Problems

```
┌────────────────────────────────┬───────────────────────────────┐
│ Problem Type                   │ Technique                     │
├────────────────────────────────┼───────────────────────────────┤
│ Anagram / character count      │ Frequency array int[26]       │
│ Substring with constraint      │ Sliding window                │
│ Palindrome check / find        │ Two pointers / expand center  │
│ Pattern matching (., *)        │ 2D DP                         │
│ String transformation          │ Edit distance DP              │
│ Subsequence matching           │ Two pointers or DP            │
│ Generate valid strings         │ Backtracking                  │
│ Parentheses problems           │ Stack or counter              │
│ Grouping strings               │ Hash by sorted key / pattern  │
└────────────────────────────────┴───────────────────────────────┘
```

---

## Real-World Applications

| Domain       | Application                  | Technique                        |
| ------------ | ---------------------------- | -------------------------------- |
| **Search**   | Spell checker / autocorrect  | Edit distance ≤ 2                |
| **Security** | Password strength validation | Regex pattern matching           |
| **Compilers**| Lexical analysis / parsing   | State machine, substring matching|
| **DNA**      | Gene sequence alignment      | Edit distance, LCS              |
| **NLP**      | Tokenization, stemming       | Pattern matching, frequency      |

---

## When to Use

- **Substring / subsequence matching** — sliding window, two pointers, DP
- **Anagram detection and grouping** — frequency arrays, sorted key hashing
- **Palindrome checking and construction** — expand around center, two pointers
- **Pattern matching (regex, wildcard)** — 2D DP with '.' and '*' handling
- **String transformation** — edit distance DP (insert, delete, replace)
- **Parentheses** — stack for matching, counter for validity

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

> String problems are often array problems in disguise — but immutability and character-level operations add unique challenges. Master **frequency arrays** (O(1) space for lowercase), **sliding window** for substring problems, **expand around center** for palindromes, and **2D DP** for edit/match/subsequence problems. Always consider `StringBuilder` for mutation-heavy operations.

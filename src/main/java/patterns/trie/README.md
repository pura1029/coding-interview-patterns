# Pattern 18: Trie (Prefix Tree)

## What is it?

A Trie (pronounced "try") is a tree-based data structure that stores strings **character by character**. Each node represents a single character, and paths from root to leaf spell out words. Operations are O(L) where L is the word length — independent of how many words are stored.

```
Words: ["app", "apple", "apt", "bat", "bar"]

         (root)
        /      \
       a        b
       |        |
       p        a
      / \      / \
     p   t    t   r
     |   *    *   *
     l
     |
     e
     *

  * = isEnd (marks complete word)

  Search "app":  root→a→p→p → isEnd=true ✅
  Search "ap":   root→a→p   → isEnd=false ❌ (prefix, not word)
  Prefix "ap":   root→a→p   → node exists ✅ (prefix exists)
  Search "bat":  root→b→a→t → isEnd=true ✅
  Search "bad":  root→b→a→? → no 'd' child ❌
```

> **Real-world analogy:** A phone book organized not alphabetically by full name, but as a tree: first letter → second letter → third letter. To find "Smith", you go S → m → i → t → h. To find all names starting with "Sm", you just go S → m and list everything below.

---

## Trie Node Structure

```
class TrieNode:
    children = new TrieNode[26]    ← one slot per letter (a-z)
    isEnd = false                  ← marks a complete word
    count = 0                      ← (optional) count of words with this prefix

Insert "cat":
  root.children['c'-'a'] → create node
  c_node.children['a'-'a'] → create node
  a_node.children['t'-'a'] → create node, set isEnd=true

  Memory: each node = 26 pointers + 1 boolean
  Worst case: 26^L nodes for words of length L
  Practical: most slots are null → sparse tree
```

---

## Key Operations Visualized

### Insert and Search

```
Insert: "tea", "ten", "to", "inn", "in"

Step by step:
  "tea": root→t→e→a*
  "ten": root→t→e→n*     (reuses t→e path)
  "to":  root→t→o*
  "inn": root→i→n→n*
  "in":  root→i→n*        (marks existing 'n' node as end)

         (root)
        /      \
       t        i
      / \       |
     e   o*     n*
    / \         |
   a*  n*      n*

Search "ten": root→t→e→n → isEnd=true ✅
Search "te":  root→t→e   → isEnd=false ❌
startsWith "te": root→t→e → node exists ✅
```

### Autocomplete (Search Suggestions)

```
Trie contains: ["car", "card", "care", "careful", "cars", "cat"]

User types "car" → find all words below "car" node:

  root→c→a→r* ← collect "car"
              ├→d* ← collect "card"
              ├→e* ← collect "care"
              │  └→f→u→l* ← collect "careful"
              └→s* ← collect "cars"

  Suggestions: ["car", "card", "care", "careful", "cars"]

  Google search autocomplete works exactly like this (with ranking).
```

### Wildcard Search (Add/Search Words)

```
Search "c.t" where '.' matches any character:

  root→c→?→t

  At '.': branch to ALL children of 'c':
    c→a→t → isEnd? Check...  "cat" ✅
    c→o→t → isEnd? Check...  "cot" ✅
    c→u→t → isEnd? Check...  "cut" ✅

  Uses DFS when encountering '.', normal search otherwise.
```

---

## Advanced: Bit-Level Trie (Maximum XOR)

```
Problem: Find two numbers in array whose XOR is maximum.
  nums = [3, 10, 5, 25, 2, 8]

Build a Trie of binary representations (5 bits):
  3  = 00011
  10 = 01010
  5  = 00101
  25 = 11001
  2  = 00010
  8  = 01000

For each number, greedily choose the OPPOSITE bit at each level:
  For 25 (11001), want opposite = 00110 → closest is 00101 (5)
  XOR = 25 ^ 5 = 28

         (root)
        /      \
       0        1
      / \       |
     0   1      1
    / \  |      |
   0   1 0      0
   |   | |      |
   1   0 1      0
   |   | |      |
   0   0 0      1
   ↑       ↑    ↑
   2   5  10   25

  Answer: 28 (25 XOR 5)
```

---

## Word Search II (Trie + DFS on Grid)

```
Board:             Words: ["oath", "pea", "eat", "rain"]
  o  a  a  n
  e  t  a  e       Build Trie from words, then DFS on board:
  i  h  k  r
  i  f  l  v       Start DFS from each cell.
                   At each cell, check if current path
                   matches a Trie prefix. If not → prune.

  Finding "oath":
    (0,0)o → (1,0)e? No, try (0,1)a → (1,1)t → (2,1)h* → FOUND!
    o → a → t → h ✅

  Finding "eat":
    (1,0)e → (1,1)? No. Try (0,3)? No. Try (1,3)e → (2,3)? No.
    Eventually: e(1,0)→a(0,1)→t(1,1)* → FOUND!

  Without Trie: check each word against grid → O(words × 4^L)
  With Trie:    one DFS with pruning → much faster
```

---

## Real-World Applications

| Domain          | Application                          | How Trie Is Used                              |
| --------------- | ------------------------------------ | --------------------------------------------- |
| **Search**      | Google autocomplete                  | Prefix tree with frequency ranking            |
| **IDE**         | Code autocomplete (IntelliSense)     | Trie of method/variable names                 |
| **Spell Check** | Dictionary lookup                    | Word existence check in O(L)                  |
| **Networking**  | IP routing (longest prefix match)    | Bit-level Trie for IP address lookup          |
| **DNS**         | Domain name resolution               | Trie on domain segments (com→google→www)      |
| **Contacts**    | Phone contact search (T9 keyboard)   | Trie mapping digits to letters                |
| **NLP**         | Tokenizer (word segmentation)        | Word break using Trie dictionary              |

---

## Trie vs HashMap vs Sorted Array

```
┌────────────────────┬─────────────┬─────────────┬───────────────┐
│ Operation          │ Trie        │ HashMap     │ Sorted Array  │
├────────────────────┼─────────────┼─────────────┼───────────────┤
│ Exact search       │ O(L)        │ O(L) avg    │ O(L log N)    │
│ Prefix search      │ O(L)  ✅    │ O(N×L) ❌   │ O(L log N)    │
│ Autocomplete       │ O(L + k) ✅ │ O(N×L) ❌   │ O(L log N +k) │
│ Insert             │ O(L)        │ O(L) avg    │ O(N) ❌       │
│ Space              │ O(N×L×26)   │ O(N×L)      │ O(N×L)        │
│ Wildcard search    │ O(26^dots)  │ O(N) ❌     │ ❌            │
└────────────────────┴─────────────┴─────────────┴───────────────┘

Use Trie when: prefix operations are required
Use HashMap when: only exact match needed, space matters
```

---

## When to Use

- **Prefix search / autocomplete** — find all words starting with a prefix
- **Word dictionary with wildcards** — DFS branching at '.' characters
- **Word break / concatenated words** — check substrings against dictionary
- **Maximum XOR** — bit-level Trie, greedily choose opposite bits
- **Word search on grid** — Trie + DFS for multi-word search with pruning
- **IP routing** — longest prefix match for network routing

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Insert | O(L) | O(L) per word |
| Search / Prefix | O(L) | O(1) |
| Build Trie (N words) | O(N × L) | O(N × L × 26) worst case |
| Autocomplete (k results) | O(L + k) | O(1) |
| Wildcard search | O(26^dots × L) | O(L) stack |

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

> Each node has 26 children (a-z). Insert O(L), search O(L). The Trie's power is **prefix matching** — something HashMaps can't do efficiently. For Word Search II, build a Trie from the word list and DFS on the board, using the Trie to prune branches that don't match any word prefix. This turns an O(W × 4^L) problem into something practical.

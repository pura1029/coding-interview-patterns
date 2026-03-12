# Pattern 6: Frequency Counting (Hashing)

## What is it?

Frequency counting uses **hash maps** or **counting arrays** to track how often elements appear. This transforms O(n²) comparison problems into O(n) lookup problems.

```
Without hashing (brute force):        With hashing:
  For each pair (i,j):                  Count frequencies once → O(n)
    if a[i] + a[j] == target            Lookup complement in O(1)
  → O(n²)                              → O(n) total

Two Sum: nums = [2, 7, 11, 15], target = 9

  HashMap approach:
  i=0: need 9-2=7, map={} → not found, store {2:0}
  i=1: need 9-7=2, map={2:0} → FOUND! pair = (0,1) ✅
```

> **Real-world analogy:** Taking inventory. Instead of searching the warehouse for each item, you count everything once and write it on a list. Future lookups are instant.

---

## Key Techniques

### 1. Frequency Array (int[26] for lowercase)

```
Valid Anagram: is "listen" an anagram of "silent"?

  freq = new int[26]
  "listen": freq[l]++ freq[i]++ freq[s]++ freq[t]++ freq[e]++ freq[n]++
  "silent": freq[s]-- freq[i]-- freq[l]-- freq[e]-- freq[n]-- freq[t]--

  All zeros? → YES ✅

  freq array vs HashMap<Character, Integer>:
    int[26]  → fixed size, cache-friendly, no boxing → FASTER
    HashMap  → flexible, any character set → MORE GENERAL
```

### 2. Boyer-Moore Voting (Majority Element)

```
Find element appearing > n/2 times: [2, 2, 1, 1, 1, 2, 2]

  Candidate = 2, count = 1
  2: count++ → 2
  1: count-- → 1
  1: count-- → 0
  1: count=0 → candidate = 1, count = 1
  2: count-- → 0
  2: count=0 → candidate = 2, count = 1

  Candidate = 2 → verify: 2 appears 4/7 times > 3.5 ✅

  WHY IT WORKS: the majority element can "survive" all cancellations
  because it has more than half the votes.
  O(n) time, O(1) space — no map needed!
```

### 3. Bucket Sort by Frequency

```
Top K Frequent Elements: nums = [1,1,1,2,2,3], k=2

  Step 1: Count frequencies → {1:3, 2:2, 3:1}

  Step 2: Create buckets by frequency (index = frequency):
    bucket[1] = [3]       ← elements appearing 1 time
    bucket[2] = [2]       ← elements appearing 2 times
    bucket[3] = [1]       ← elements appearing 3 times

  Step 3: Walk buckets from right (highest freq):
    bucket[3] → [1], bucket[2] → [2]

  Answer: [1, 2] ✅ (O(n) — no sorting needed!)
```

---

## Real-World Applications

| Domain          | Application                    | Technique                     |
| --------------- | ------------------------------ | ----------------------------- |
| **Search**      | Word frequency in documents    | HashMap counting              |
| **Security**    | Password character requirements| Frequency array check         |
| **Analytics**   | Top viewed products            | Bucket sort by view count     |
| **Spam Filter** | Word occurrence patterns       | Frequency comparison          |
| **Databases**   | GROUP BY, COUNT operations     | Hash-based aggregation        |

---

## When to Use

- **Anagram detection and grouping** — character frequency comparison
- **Finding duplicates, missing elements** — HashSet membership, XOR
- **Majority element** — Boyer-Moore voting (O(1) space)
- **Frequency-based sorting** — bucket sort by count
- **Substring matching** — sliding window with character counts

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Count frequencies | O(n) | O(k) where k = distinct elements |
| Group by key | O(n) | O(n) |
| Frequency lookup | O(1) | — |
| Boyer-Moore | O(n) | O(1) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Valid Anagram | Easy | Compare character frequency arrays |
| 2 | First Unique Character | Easy | Count frequencies, find first with count 1 |
| 3 | Contains Duplicate | Easy | HashSet membership check |
| 4 | Majority Element (Boyer-Moore) | Easy | Cancel different elements |
| 5 | Ransom Note | Easy | Magazine must have enough of each char |
| 6 | Find the Difference | Easy | XOR all characters |
| 7 | Jewels and Stones | Easy | HashSet of jewels, count matches |
| 8 | Word Pattern | Easy | Bijection between words and chars |
| 9 | Isomorphic Strings | Easy | Two-way character mapping |
| 10 | Missing Number | Easy | XOR with indices or sum formula |
| 11 | Group Anagrams | Medium | Sort each word as key |
| 12 | Top K Frequent Elements | Medium | Bucket sort by frequency |
| 13 | Longest Consecutive Sequence | Medium | HashSet + expand from smallest |
| 14 | Sort Characters By Frequency | Medium | Count + sort by frequency |
| 15 | Find All Duplicates in Array | Medium | Mark visited by negating |
| 16 | Custom Sort String | Medium | Sort by order index map |
| 17 | Encode and Decode TinyURL | Medium | HashMap for id ↔ URL mapping |
| 18 | Subarray Sum Equals K | Medium | Prefix sum + frequency map |
| 19 | Min Steps to Make Anagram | Medium | Count character difference |
| 20 | Determine if Strings Are Close | Medium | Same char set + same sorted frequencies |
| 21 | Minimum Window Substring | Hard | Sliding window with need/have counts |
| 22 | Longest Substring K Distinct | Hard | Shrink when distinct > k |
| 23 | First Missing Positive | Hard | Cyclic sort to place i at index i-1 |
| 24 | Majority Element II (>n/3) | Hard | Boyer-Moore with two candidates |
| 25 | Substring Concatenation of All Words | Hard | Word-level sliding window |
| 26 | Count of Smaller After Self | Hard | Merge sort with index tracking |
| 27 | Smallest Sufficient Team | Hard | Bitmask DP on skill coverage |
| 28 | All O'one Data Structure | Hard | Doubly linked list + hashmap for O(1) |
| 29 | Rearrange String K Distance Apart | Hard | Greedy with max-heap + cooldown |
| 30 | Max Points on a Line | Hard | Slope frequency per point |

## Key Insight

> **Count frequencies first, then use the counts** to solve the problem. Use `int[26]` for lowercase letters (faster than HashMap). XOR is useful for "single unique" variants — all duplicates cancel out. Boyer-Moore voting finds majority elements in O(1) space. Bucket sort by frequency gives O(n) "Top K" without sorting.

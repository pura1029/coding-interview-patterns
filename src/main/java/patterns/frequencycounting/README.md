# Pattern 6: Frequency Counting (Hashing)

## What is it?
Uses hash maps or counting arrays to track element frequencies, enabling O(n) solutions for matching, grouping, and counting problems.

## When to Use
- Anagram detection and grouping
- Finding duplicates, missing elements
- Majority element, frequency-based sorting
- Substring matching with character counts

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Count frequencies | O(n) | O(k) |
| Group by key | O(n) | O(n) |
| Frequency lookup | O(1) | - |

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
> Count frequencies first, then use the counts to solve the problem. XOR is useful for "single unique" variants.

# Pattern 3: Sliding Window

## What is it?
Maintains a dynamic window over data, expanding/shrinking to find optimal subarrays or substrings in O(n).

## When to Use
- Fixed-size window aggregates (max sum, average)
- Variable-size: longest/shortest substring with constraints
- Anagram/permutation finding
- At-most-K distinct characters

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Fixed window | O(n) | O(1) |
| Variable window | O(n) | O(k) |

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
> Expand right pointer to grow the window, shrink left pointer when constraint is violated. Track window state with hashmap or counters.

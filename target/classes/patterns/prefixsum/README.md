# Pattern 1: Prefix Sum

## What is it?
Pre-computes cumulative sums so any subarray sum is O(1) after O(n) build. `prefix[i] = nums[0]+...+nums[i-1]`; `sum(l..r) = prefix[r+1] - prefix[l]`.

## When to Use
- Repeated range-sum queries
- Subarray sum equals target
- Subarray divisibility checks
- 2D region sum queries
- Difference arrays for range updates

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Build | O(n) | O(n) |
| Query | O(1) | - |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Range Sum Query – Immutable | Easy | Build prefix[], answer in O(1) |
| 2 | Running Sum of 1D Array | Easy | Cumulative sum |
| 3 | Find Pivot Index | Easy | Left sum == total - left - current |
| 4 | Sum of All Odd Length Subarrays | Easy | Count element contribution |
| 5 | Number of Good Pairs | Easy | Frequency prefix counting |
| 6 | Left and Right Sum Differences | Easy | Prefix + suffix sums |
| 7 | Count Positive Prefix Sums | Easy | Running sum check |
| 8 | Max After Range Increments | Easy | Difference array technique |
| 9 | Min Start Value for Positive Sums | Easy | Track minimum prefix |
| 10 | K Length Apart Check | Easy | Gap tracking |
| 11 | Subarray Sum Equals K | Medium | Prefix sum + hashmap |
| 12 | Contiguous Array (Equal 0s and 1s) | Medium | Replace 0→-1, find sum=0 |
| 13 | Product of Array Except Self | Medium | Prefix/suffix products |
| 14 | Subarrays Divisible by K | Medium | Remainder prefix counting |
| 15 | Bounded Maximum Subarrays | Medium | AtMost technique |
| 16 | Binary Subarrays With Sum | Medium | Prefix sum counting |
| 17 | Maximum Size Subarray Sum=K | Medium | First occurrence hashmap |
| 18 | Range Addition (Difference Array) | Medium | Sweep line + prefix |
| 19 | Continuous Subarray Sum (multiple of k) | Medium | Remainder + index tracking |
| 20 | Count Nice Subarrays (k odd numbers) | Medium | AtMost sliding window |
| 21 | Max Sum After Removing One Element | Hard | Prefix/suffix max subarray |
| 22 | 2D Range Sum Query | Hard | 2D prefix inclusion-exclusion |
| 23 | Subarray Sum=K (Long overflow) | Hard | Long prefix sum handling |
| 24 | Max Sum of 3 Non-Overlapping Subarrays | Hard | Window sum + left/right best |
| 25 | Shortest Subarray with Sum≥K | Hard | Prefix sum + monotone deque |
| 26 | Count of Range Sum | Hard | Merge sort on prefix sums |
| 27 | Max Non-Overlapping Subarrays=Target | Hard | Greedy prefix reset |
| 28 | Make Sum Divisible by P | Hard | Remove shortest subarray |
| 29 | Submatrix Sum Target (2D) | Hard | Column compression + prefix |
| 30 | Min Operations to Reduce X to Zero | Hard | Longest subarray = total-x |

## Key Insight
> Build prefix sums once, answer unlimited range queries in O(1). Combine with hashmaps for "count subarrays with property X".

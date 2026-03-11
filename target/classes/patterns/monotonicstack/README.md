# Pattern 7: Monotonic Stack

## What is it?
A stack that maintains increasing or decreasing order, solving "next greater/smaller element" problems in O(n).

## When to Use
- Next greater / smaller element queries
- Largest rectangle / trapping water
- Remove digits to form smallest number
- Subarray min/max aggregations

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Process all elements | O(n) | O(n) |
| Each element pushed/popped | O(1) amortized | - |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Daily Temperatures | Easy | Stack of indices, pop when warmer found |
| 2 | Next Greater Element I | Easy | Map next-greater via stack |
| 3 | Final Prices With Discount | Easy | Next smaller element = discount |
| 4 | Remove Outermost Parentheses | Easy | Track depth, skip outer layer |
| 5 | Make The String Great | Easy | Stack removes adjacent case-pair |
| 6 | Remove Adjacent Duplicates | Easy | Stack pops matching top |
| 7 | Backspace String Compare | Easy | Stack simulates backspace |
| 8 | Baseball Game | Easy | Stack-based score tracking |
| 9 | Valid Parentheses | Easy | Stack matches open/close brackets |
| 10 | Min Stack | Easy | Auxiliary stack tracks running min |
| 11 | Next Greater Element II (Circular) | Medium | Double traversal with modulo |
| 12 | Online Stock Span | Medium | Stack of (price, span) pairs |
| 13 | Remove K Digits | Medium | Maintain increasing stack, remove k |
| 14 | 132 Pattern | Medium | Reverse scan with max "2" tracking |
| 15 | Remove Duplicate Letters | Medium | Greedy stack + last-occurrence check |
| 16 | Asteroid Collision | Medium | Stack simulates collisions |
| 17 | Sum of Subarray Minimums | Medium | Contribution of each min via boundaries |
| 18 | Decode String | Medium | Stack of (string, count) pairs |
| 19 | Car Fleet | Medium | Sort by position, stack by arrival time |
| 20 | Evaluate Reverse Polish Notation | Medium | Stack-based expression evaluation |
| 21 | Largest Rectangle in Histogram | Hard | Stack finds left/right boundaries |
| 22 | Maximal Rectangle | Hard | Row-wise histogram + largest rectangle |
| 23 | Trapping Rain Water (stack) | Hard | Stack tracks valleys between bars |
| 24 | Sum of Subarray Ranges | Hard | Max-min contribution for all subarrays |
| 25 | Visible People in Queue | Hard | Decreasing stack from right |
| 26 | Maximum Width Ramp | Hard | Decreasing stack + right scan |
| 27 | Steps to Make Non-decreasing | Hard | Track rounds via stack simulation |
| 28 | Longest Valid Parentheses | Hard | Stack of indices for valid segments |
| 29 | Create Maximum Number | Hard | Merge two max subsequences |
| 30 | Min Cost Tree From Leaf Values | Hard | Greedy: remove smallest leaf first |

## Key Insight
> Pop elements that violate monotonicity; the popped element's answer is the current element.

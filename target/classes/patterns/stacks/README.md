# Stacks

## What is it?
A Stack (LIFO — Last In, First Out) stores elements such that the most recently added element is the first to be removed. Stacks are used for expression parsing, backtracking, undo/redo, DFS traversal, and monotonic pattern problems. They excel at "nearest" and "matching" problems.

## When to Use
- Parentheses / bracket matching and validation
- Expression evaluation (RPN, calculator)
- Monotonic stack (next greater/smaller, histogram)
- String manipulation (decode, simplify, remove duplicates)
- DFS simulation and backtracking

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Push | O(1) | O(1) |
| Pop | O(1) | O(1) |
| Peek / Top | O(1) | O(1) |
| Search | O(n) | — |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Valid Parentheses | Easy | Push expected closing, match on close |
| 2 | Implement Stack using Queues | Easy | Rotate queue after each push |
| 3 | Min Stack | Easy | Store (val, currentMin) pair |
| 4 | Baseball Game | Easy | Stack simulation of score operations |
| 5 | Next Greater Element I | Easy | Monotonic stack + hash map |
| 6 | Backspace String Compare | Easy | Build result with stack, pop on '#' |
| 7 | Remove All Adjacent Duplicates | Easy | Stack: pop if top == current char |
| 8 | Maximum Nesting Depth | Easy | Counter as virtual stack, track max |
| 9 | Make The String Great | Easy | Pop if same letter with different case |
| 10 | Crawler Log Folder | Easy | Counter tracks directory depth |
| 11 | Evaluate Reverse Polish Notation | Medium | Stack: push numbers, pop for operators |
| 12 | Daily Temperatures | Medium | Monotonic decreasing stack of indices |
| 13 | Decode String | Medium | Dual stacks: count stack + string stack |
| 14 | Simplify Path | Medium | Stack: push dirs, pop on ".." |
| 15 | Remove K Digits | Medium | Monotonic stack: drop digits that are too large |
| 16 | Asteroid Collision | Medium | Stack simulation: resolve collisions |
| 17 | Flatten Nested List Iterator | Medium | Stack of iterators for nested traversal |
| 18 | Online Stock Span | Medium | Monotonic stack tracking consecutive ≤ days |
| 19 | Car Fleet | Medium | Sort by position, stack by arrival time |
| 20 | Validate Stack Sequences | Medium | Simulate push/pop sequence |
| 21 | Largest Rectangle in Histogram | Hard | Monotonic stack to find nearest shorter bars |
| 22 | Maximal Rectangle | Hard | Row-by-row histogram + largest rectangle |
| 23 | Basic Calculator | Hard | Stack for nested parentheses with sign |
| 24 | Trapping Rain Water (stack-based) | Hard | Stack-based: pop and compute trapped water |
| 25 | Longest Valid Parentheses | Hard | Stack of indices, compute length on pop |
| 26 | Maximum Frequency Stack | Hard | Freq map + group-by-freq stacks |
| 27 | Visible People in Queue | Hard | Monotonic stack from right |
| 28 | Sum of Subarray Minimums | Hard | Contribution counting with monotonic stack |
| 29 | Remove Duplicate Letters | Hard | Monotonic stack with last-occurrence check |
| 30 | Max Stack | Hard | Dual stack: main + max-tracking with lazy pop |

## Key Insight
> Stacks shine in "matching" problems (brackets, expressions) and "nearest element" problems (next greater, histogram). The **monotonic stack** variant — maintaining elements in sorted order — is one of the most powerful interview techniques. Learn to recognize when the answer depends on "the nearest larger/smaller element."

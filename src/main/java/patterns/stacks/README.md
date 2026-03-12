# Stacks

## What is it?

A Stack follows **LIFO** (Last In, First Out) — like a stack of plates. You can only add/remove from the top.

```
Push:           Pop:            Peek:

  ┌───┐          ┌───┐
  │ C │ ← top    │   │ → C      Look at top
  ├───┤          ├───┤          without removing
  │ B │          │ B │ ← new top
  ├───┤          ├───┤          return B
  │ A │          │ A │
  └───┘          └───┘

  Java: Deque<Integer> stack = new ArrayDeque<>();
        stack.push(x)  → push
        stack.pop()    → pop
        stack.peek()   → top element without removing
```

> **Real-world analogy:** Browser back button. Each page you visit is pushed onto a stack. Clicking "back" pops the most recent page.

---

## Key Stack Patterns

### 1. Bracket Matching

```
Valid Parentheses: s = "({[]})"

  Scan left to right:
  '(' → push ')' (expected closing)   stack: [')']
  '{' → push '}'                      stack: [')', '}']
  '[' → push ']'                      stack: [')', '}', ']']
  ']' → pop → ']' matches ✅          stack: [')', '}']
  '}' → pop → '}' matches ✅          stack: [')']
  ')' → pop → ')' matches ✅          stack: []

  Stack empty at end → VALID ✅

  Invalid: "([)]"
  '(' → push ')'    stack: [')']
  '[' → push ']'    stack: [')', ']']
  ')' → pop → ']'   ≠ ')' → INVALID ❌
```

### 2. Monotonic Stack (Next Greater Element)

```
Find next greater element for each element:
  nums = [2, 1, 2, 4, 3]

  Maintain DECREASING stack (top is smallest):

  i=0: push 2.      stack: [2]
  i=1: 1 < 2, push. stack: [2, 1]
  i=2: 2 > 1 → pop 1, next_greater[1]=2.  stack: [2]
       2 = 2, push.  stack: [2, 2]
  i=3: 4 > 2 → pop 2, next_greater[2]=4.  stack: [2]
       4 > 2 → pop 2, next_greater[2]=4.  stack: []
       push 4.       stack: [4]
  i=4: 3 < 4, push. stack: [4, 3]
  Remaining: no next greater for 4 and 3 → -1

  Result: [4, 2, 4, -1, -1]
```

### 3. Largest Rectangle in Histogram

```
heights = [2, 1, 5, 6, 2, 3]

  Maintain INCREASING stack of indices.
  When a shorter bar is found, pop and calculate area.

  ┌───┐
  │   │
  │ 6 ├───┐
  │   │   │
  ├───┤ 5 │         ┌───┐
  │   │   │         │   │
  │   │   │   ┌───┐ │ 3 │
  │ 2 │   │   │ 2 │ │   │
  │   ├───┤   │   │ │   │
  │   │ 1 │   │   │ │   │
  └───┴───┴───┴───┴─┴───┘
    0   1   2   3   4   5

  At index 4 (height=2), bar 3 (height=6) is popped:
    area = 6 × 1 = 6
  Then bar 2 (height=5) is popped:
    area = 5 × 2 = 10 ← MAX ✅

  Answer: 10
```

---

## Real-World Applications

| Domain         | Application                          | Stack Pattern              |
| -------------- | ------------------------------------ | -------------------------- |
| **Compilers**  | Expression parsing, syntax checking  | Bracket matching           |
| **Browsers**   | Back/forward navigation              | Two stacks (back + forward)|
| **Editors**    | Undo/redo operations                 | Command pattern + stack    |
| **OS**         | Function call stack                  | Call frames pushed/popped  |
| **Calculators**| Postfix (RPN) evaluation             | Operand stack              |

---

## When to Use

- **Parentheses / bracket matching** — push expected closing, match on pop
- **Expression evaluation** — RPN, calculator (operand stack + operator stack)
- **Monotonic stack** — next greater/smaller, histogram, stock span
- **String manipulation** — decode, simplify path, remove duplicates
- **DFS simulation** — iterative DFS using explicit stack

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

> Stacks shine in **"matching"** problems (brackets, expressions) and **"nearest element"** problems (next greater, histogram). The **monotonic stack** — maintaining elements in sorted order — is one of the most powerful interview techniques. Learn to recognize when the answer depends on "the nearest larger/smaller element to the left/right."

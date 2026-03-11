package patterns.stacks;

import java.util.*;

/**
 * STACKS — 30 Essential Problems
 * A Stack (LIFO — Last In, First Out) is used for expression parsing, backtracking,
 * undo/redo, DFS traversal, and monotonic patterns. Mastering stack-based approaches
 * unlocks solutions for matching, nearest-element, and histogram-style problems.
 *
 * 10 Easy | 10 Medium | 10 Hard
 */
public class StackPatterns {

    // ======================= EASY 1: Valid Parentheses =======================
    /** Push expected closing bracket; on close, check top matches. O(n) time, O(n) space. */
    public static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '(') stack.push(')');
            else if (c == '{') stack.push('}');
            else if (c == '[') stack.push(']');
            else if (stack.isEmpty() || stack.pop() != c) return false;
        }
        return stack.isEmpty();
    }

    // ======================= EASY 2: Implement Stack using Queues =======================
    /** Single queue: after each push, rotate all elements so newest is at front. O(n) push, O(1) pop. */
    static class MyStack {
        Queue<Integer> q = new LinkedList<>();
        public void push(int x) { q.add(x); for (int i = 1; i < q.size(); i++) q.add(q.poll()); }
        public int pop() { return q.poll(); }
        public int top() { return q.peek(); }
        public boolean empty() { return q.isEmpty(); }
    }

    // ======================= EASY 3: Min Stack =======================
    /** Each entry stores (value, currentMin) pair for O(1) getMin. O(1) all operations. */
    static class MinStack {
        Deque<int[]> stack = new ArrayDeque<>();
        public void push(int val) { int min = stack.isEmpty() ? val : Math.min(val, stack.peek()[1]); stack.push(new int[]{val, min}); }
        public void pop() { stack.pop(); }
        public int top() { return stack.peek()[0]; }
        public int getMin() { return stack.peek()[1]; }
    }

    // ======================= EASY 4: Baseball Game =======================
    /** Stack simulation: +, D, C operate on top elements; sum remaining. O(n) time, O(n) space. */
    public static int calPoints(String[] operations) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String op : operations) {
            switch (op) {
                case "+": int a = stack.pop(), b = stack.peek(); stack.push(a); stack.push(a + b); break;
                case "D": stack.push(2 * stack.peek()); break;
                case "C": stack.pop(); break;
                default: stack.push(Integer.parseInt(op));
            }
        }
        int sum = 0; for (int v : stack) sum += v;
        return sum;
    }

    // ======================= EASY 5: Next Greater Element I =======================
    /** Monotonic stack on nums2 maps each value to its next greater; look up for nums1. O(m+n) time. */
    public static int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> map = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();
        for (int n : nums2) { while (!stack.isEmpty() && stack.peek() < n) map.put(stack.pop(), n); stack.push(n); }
        int[] res = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) res[i] = map.getOrDefault(nums1[i], -1);
        return res;
    }

    // ======================= EASY 6: Backspace String Compare =======================
    /** Build final string using stack: push chars, pop on '#'. Compare results. O(n) time, O(n) space. */
    public static boolean backspaceCompare(String s, String t) {
        return build(s).equals(build(t));
    }
    private static String build(String str) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : str.toCharArray()) { if (c == '#') { if (!stack.isEmpty()) stack.pop(); } else stack.push(c); }
        return stack.toString();
    }

    // ======================= EASY 7: Remove All Adjacent Duplicates =======================
    /** Stack: pop if top equals current char (adjacent duplicate), else push. O(n) time, O(n) space. */
    public static String removeDuplicates(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) { if (!stack.isEmpty() && stack.peek() == c) stack.pop(); else stack.push(c); }
        StringBuilder sb = new StringBuilder();
        for (char c : stack) sb.append(c);
        return sb.reverse().toString();
    }

    // ======================= EASY 8: Maximum Nesting Depth of Parentheses =======================
    /** Counter as virtual stack: increment on '(', decrement on ')'; track max depth. O(n) time, O(1) space. */
    public static int maxDepth(String s) {
        int depth = 0, max = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') max = Math.max(max, ++depth);
            else if (c == ')') depth--;
        }
        return max;
    }

    // ======================= EASY 9: Make The String Great =======================
    /** Stack: pop if top is the same letter with different case. O(n) time, O(n) space. */
    public static String makeGood(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (!stack.isEmpty() && Math.abs(stack.peek() - c) == 32) stack.pop();
            else stack.push(c);
        }
        StringBuilder sb = new StringBuilder();
        for (char c : stack) sb.append(c);
        return sb.reverse().toString();
    }

    // ======================= EASY 10: Crawler Log Folder =======================
    /** Counter as virtual stack: "../" decrements (min 0), "./" no-op, else increment. O(n) time, O(1) space. */
    public static int minOperations(String[] logs) {
        int depth = 0;
        for (String log : logs) {
            if (log.equals("../")) depth = Math.max(0, depth - 1);
            else if (!log.equals("./")) depth++;
        }
        return depth;
    }

    // ======================= MEDIUM 1: Evaluate Reverse Polish Notation =======================
    /** Stack: push numbers; on operator, pop two operands, compute, push result. O(n) time, O(n) space. */
    public static int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String t : tokens) {
            if ("+-*/".contains(t)) {
                int b = stack.pop(), a = stack.pop();
                switch (t) { case "+": stack.push(a + b); break; case "-": stack.push(a - b); break; case "*": stack.push(a * b); break; case "/": stack.push(a / b); break; }
            } else stack.push(Integer.parseInt(t));
        }
        return stack.pop();
    }

    // ======================= MEDIUM 2: Daily Temperatures =======================
    /** Monotonic decreasing stack of indices; pop and record gap when warmer day found. O(n) time, O(n) space. */
    public static int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[stack.peek()] < temperatures[i]) { int idx = stack.pop(); result[idx] = i - idx; }
            stack.push(i);
        }
        return result;
    }

    // ======================= MEDIUM 3: Decode String =======================
    /** Dual stacks: count stack and string stack; on ']' pop and repeat. O(n·max_k) time. */
    public static String decodeString(String s) {
        Deque<Integer> countStack = new ArrayDeque<>();
        Deque<StringBuilder> strStack = new ArrayDeque<>();
        StringBuilder cur = new StringBuilder();
        int k = 0;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) k = k * 10 + (c - '0');
            else if (c == '[') { countStack.push(k); strStack.push(cur); cur = new StringBuilder(); k = 0; }
            else if (c == ']') { StringBuilder prev = strStack.pop(); int cnt = countStack.pop(); for (int i = 0; i < cnt; i++) prev.append(cur); cur = prev; }
            else cur.append(c);
        }
        return cur.toString();
    }

    // ======================= MEDIUM 4: Simplify Path =======================
    /** Stack of directory names: push valid dirs, pop on ".."; join with "/". O(n) time. */
    public static String simplifyPath(String path) {
        Deque<String> stack = new ArrayDeque<>();
        for (String p : path.split("/")) {
            if (p.equals("..")) { if (!stack.isEmpty()) stack.pop(); }
            else if (!p.isEmpty() && !p.equals(".")) stack.push(p);
        }
        StringBuilder sb = new StringBuilder();
        for (String s : stack) sb.insert(0, "/" + s);
        return sb.length() == 0 ? "/" : sb.toString();
    }

    // ======================= MEDIUM 5: Remove K Digits =======================
    /** Monotonic increasing stack: pop larger digits to minimize result. O(n) time, O(n) space. */
    public static String removeKdigits(String num, int k) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : num.toCharArray()) {
            while (k > 0 && !stack.isEmpty() && stack.peek() > c) { stack.pop(); k--; }
            stack.push(c);
        }
        while (k-- > 0) stack.pop();
        StringBuilder sb = new StringBuilder();
        boolean leadingZero = true;
        for (Iterator<Character> it = stack.descendingIterator(); it.hasNext();) {
            char c = it.next();
            if (leadingZero && c == '0') continue;
            leadingZero = false;
            sb.append(c);
        }
        return sb.length() == 0 ? "0" : sb.toString();
    }

    // ======================= MEDIUM 6: Asteroid Collision =======================
    /** Stack simulation: negative asteroid collides with positive top; larger survives. O(n) time. */
    public static int[] asteroidCollision(int[] asteroids) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int a : asteroids) {
            boolean alive = true;
            while (alive && a < 0 && !stack.isEmpty() && stack.peek() > 0) {
                if (stack.peek() < -a) stack.pop();
                else if (stack.peek() == -a) { stack.pop(); alive = false; }
                else alive = false;
            }
            if (alive) stack.push(a);
        }
        int[] res = new int[stack.size()]; int i = stack.size() - 1;
        for (int v : stack) res[i--] = v;
        return res;
    }

    // ======================= MEDIUM 7: Flatten Nested List Iterator =======================
    /** Stack of iterators: when element is a list, push its iterator; collect integers. O(n) time. */
    public static List<Integer> flattenNestedList(List<Object> nestedList) {
        List<Integer> result = new ArrayList<>();
        Deque<Iterator<Object>> stack = new ArrayDeque<>();
        stack.push(nestedList.iterator());
        while (!stack.isEmpty()) {
            Iterator<Object> it = stack.peek();
            if (!it.hasNext()) { stack.pop(); continue; }
            Object next = it.next();
            if (next instanceof Integer) result.add((Integer) next);
            else { @SuppressWarnings("unchecked") List<Object> list = (List<Object>) next; stack.push(list.iterator()); }
        }
        return result;
    }

    // ======================= MEDIUM 8: Online Stock Span =======================
    /** Monotonic stack: pop and accumulate spans while top price <= current. O(1) amortized per call. */
    static class StockSpanner {
        Deque<int[]> stack = new ArrayDeque<>();
        public int next(int price) {
            int span = 1;
            while (!stack.isEmpty() && stack.peek()[0] <= price) span += stack.pop()[1];
            stack.push(new int[]{price, span});
            return span;
        }
    }

    // ======================= MEDIUM 9: Car Fleet =======================
    /** Sort by position descending; count fleets where arrival time exceeds previous fleet. O(n log n) time. */
    public static int carFleet(int target, int[] position, int[] speed) {
        int n = position.length;
        double[][] cars = new double[n][2];
        for (int i = 0; i < n; i++) cars[i] = new double[]{position[i], (double)(target - position[i]) / speed[i]};
        Arrays.sort(cars, (a, b) -> Double.compare(b[0], a[0]));
        int fleets = 0; double last = 0;
        for (double[] car : cars) { if (car[1] > last) { fleets++; last = car[1]; } }
        return fleets;
    }

    // ======================= MEDIUM 10: Validate Stack Sequences =======================
    /** Simulate push/pop: push from pushed[], pop whenever top matches popped[]. O(n) time, O(n) space. */
    public static boolean validateStackSequences(int[] pushed, int[] popped) {
        Deque<Integer> stack = new ArrayDeque<>();
        int j = 0;
        for (int val : pushed) {
            stack.push(val);
            while (!stack.isEmpty() && stack.peek() == popped[j]) { stack.pop(); j++; }
        }
        return stack.isEmpty();
    }

    // ======================= HARD 1: Largest Rectangle in Histogram =======================
    /** Monotonic stack: pop when current bar shorter; width between stack boundaries. O(n) time, O(n) space. */
    public static int largestRectangleArea(int[] heights) {
        Deque<Integer> stack = new ArrayDeque<>();
        int max = 0, n = heights.length;
        for (int i = 0; i <= n; i++) {
            int h = i == n ? 0 : heights[i];
            while (!stack.isEmpty() && h < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                max = Math.max(max, height * width);
            }
            stack.push(i);
        }
        return max;
    }

    // ======================= HARD 2: Maximal Rectangle =======================
    /** Build histogram row by row; apply largest rectangle in histogram per row. O(m·n) time. */
    public static int maximalRectangle(char[][] matrix) {
        if (matrix.length == 0) return 0;
        int cols = matrix[0].length, max = 0;
        int[] heights = new int[cols];
        for (char[] row : matrix) {
            for (int j = 0; j < cols; j++) heights[j] = row[j] == '1' ? heights[j] + 1 : 0;
            max = Math.max(max, largestRectangleArea(heights));
        }
        return max;
    }

    // ======================= HARD 3: Basic Calculator =======================
    /** Stack stores result and sign on '('; restore on ')'. Handles +, -, and nested parens. O(n) time. */
    public static int calculate(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        int result = 0, num = 0, sign = 1;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) num = num * 10 + (c - '0');
            else if (c == '+') { result += sign * num; num = 0; sign = 1; }
            else if (c == '-') { result += sign * num; num = 0; sign = -1; }
            else if (c == '(') { stack.push(result); stack.push(sign); result = 0; sign = 1; num = 0; }
            else if (c == ')') { result += sign * num; num = 0; result *= stack.pop(); result += stack.pop(); }
        }
        return result + sign * num;
    }

    // ======================= HARD 4: Trapping Rain Water (stack-based) =======================
    /** Stack-based approach: pop bottom, compute trapped water between boundaries. O(n) time, O(n) space. */
    public static int trap(int[] height) {
        Deque<Integer> stack = new ArrayDeque<>();
        int water = 0;
        for (int i = 0; i < height.length; i++) {
            while (!stack.isEmpty() && height[i] > height[stack.peek()]) {
                int bottom = height[stack.pop()];
                if (stack.isEmpty()) break;
                int width = i - stack.peek() - 1;
                int h = Math.min(height[i], height[stack.peek()]) - bottom;
                water += width * h;
            }
            stack.push(i);
        }
        return water;
    }

    // ======================= HARD 5: Longest Valid Parentheses =======================
    /** Stack of indices; push -1 as base; on ')' pop and compute length if stack non-empty. O(n) time. */
    public static int longestValidParentheses(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1);
        int max = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') stack.push(i);
            else { stack.pop(); if (stack.isEmpty()) stack.push(i); else max = Math.max(max, i - stack.peek()); }
        }
        return max;
    }

    // ======================= HARD 6: Maximum Frequency Stack =======================
    /** Freq map + stacks grouped by frequency; pop from highest frequency group. O(1) push/pop. */
    static class FreqStack {
        Map<Integer, Integer> freq = new HashMap<>();
        Map<Integer, Deque<Integer>> group = new HashMap<>();
        int maxFreq = 0;
        public void push(int val) {
            int f = freq.merge(val, 1, Integer::sum);
            maxFreq = Math.max(maxFreq, f);
            group.computeIfAbsent(f, k -> new ArrayDeque<>()).push(val);
        }
        public int pop() {
            int val = group.get(maxFreq).pop();
            if (group.get(maxFreq).isEmpty()) { group.remove(maxFreq); maxFreq--; }
            freq.merge(val, -1, Integer::sum);
            return val;
        }
    }

    // ======================= HARD 7: Number of Visible People in a Queue =======================
    /** Monotonic stack from right: pop shorter people (visible), count pops + peek if exists. O(n) time. */
    public static int[] canSeePersonsCount(int[] heights) {
        int n = heights.length;
        int[] res = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && heights[stack.peek()] < heights[i]) { stack.pop(); res[i]++; }
            if (!stack.isEmpty()) res[i]++;
            stack.push(i);
        }
        return res;
    }

    // ======================= HARD 8: Sum of Subarray Minimums =======================
    /** Monotonic stack: for each element, count subarrays where it is minimum (left × right). O(n) time. */
    public static int sumSubarrayMins(int[] arr) {
        long MOD = 1_000_000_007, sum = 0;
        Deque<Integer> stack = new ArrayDeque<>();
        int n = arr.length;
        for (int i = 0; i <= n; i++) {
            int cur = i == n ? 0 : arr[i];
            while (!stack.isEmpty() && arr[stack.peek()] >= cur) {
                int j = stack.pop();
                int left = stack.isEmpty() ? j + 1 : j - stack.peek();
                int right = i - j;
                sum = (sum + (long) arr[j] * left * right) % MOD;
            }
            stack.push(i);
        }
        return (int) sum;
    }

    // ======================= HARD 9: Remove Duplicate Letters (Smallest Subsequence) =======================
    /** Monotonic stack with last-occurrence check: remove larger char if it appears later. O(n) time. */
    public static String removeDuplicateLetters(String s) {
        int[] lastIdx = new int[26];
        boolean[] inStack = new boolean[26];
        for (int i = 0; i < s.length(); i++) lastIdx[s.charAt(i) - 'a'] = i;
        Deque<Character> stack = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (inStack[c - 'a']) continue;
            while (!stack.isEmpty() && stack.peek() > c && lastIdx[stack.peek() - 'a'] > i) inStack[stack.pop() - 'a'] = false;
            stack.push(c);
            inStack[c - 'a'] = true;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : stack) sb.append(c);
        return sb.reverse().toString();
    }

    // ======================= HARD 10: Max Stack (supports peekMax and popMax) =======================
    /** Two stacks: main stack and max-tracking stack. peekMax/popMax use lazy deletion. O(1) push/peek, O(n) popMax. */
    static class MaxStack {
        Deque<Integer> stack = new ArrayDeque<>();
        Deque<Integer> maxStack = new ArrayDeque<>();
        public void push(int x) { stack.push(x); maxStack.push(maxStack.isEmpty() ? x : Math.max(x, maxStack.peek())); }
        public int pop() { maxStack.pop(); return stack.pop(); }
        public int top() { return stack.peek(); }
        public int peekMax() { return maxStack.peek(); }
        public int popMax() {
            int max = peekMax();
            Deque<Integer> temp = new ArrayDeque<>();
            while (stack.peek() != max) { temp.push(pop()); }
            pop();
            while (!temp.isEmpty()) push(temp.pop());
            return max;
        }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== STACKS (30 Examples) ===\n");

        System.out.println("--- EASY ---");
        System.out.println("1. Valid Parens: " + isValid("()[]{}"));
        MyStack ms = new MyStack(); ms.push(1); ms.push(2);
        System.out.println("2. Stack via Queues: top=" + ms.top());
        MinStack minSt = new MinStack(); minSt.push(-2); minSt.push(0); minSt.push(-3);
        System.out.println("3. Min Stack: min=" + minSt.getMin());
        System.out.println("4. Baseball: " + calPoints(new String[]{"5", "2", "C", "D", "+"}));
        System.out.println("5. Next Greater I: " + Arrays.toString(nextGreaterElement(new int[]{4, 1, 2}, new int[]{1, 3, 4, 2})));
        System.out.println("6. Backspace: " + backspaceCompare("ab#c", "ad#c"));
        System.out.println("7. Remove Adj Dups: " + removeDuplicates("abbaca"));
        System.out.println("8. Max Depth: " + maxDepth("(1+(2*3)+((8)/4))+1"));
        System.out.println("9. Make Good: " + makeGood("leEeetcode"));
        System.out.println("10. Crawler Log: " + minOperations(new String[]{"d1/", "d2/", "../", "d21/", "./"}));

        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. RPN: " + evalRPN(new String[]{"2", "1", "+", "3", "*"}));
        System.out.println("12. Daily Temps: " + Arrays.toString(dailyTemperatures(new int[]{73, 74, 75, 71, 69, 72, 76, 73})));
        System.out.println("13. Decode String: " + decodeString("3[a]2[bc]"));
        System.out.println("14. Simplify Path: " + simplifyPath("/home//foo/"));
        System.out.println("15. Remove K Digits: " + removeKdigits("1432219", 3));
        System.out.println("16. Asteroids: " + Arrays.toString(asteroidCollision(new int[]{5, 10, -5})));
        System.out.println("17. Flatten Nested: (demo skipped)");
        StockSpanner sp = new StockSpanner();
        System.out.println("18. Stock Span: " + sp.next(100) + " " + sp.next(80) + " " + sp.next(60) + " " + sp.next(70) + " " + sp.next(60) + " " + sp.next(75) + " " + sp.next(85));
        System.out.println("19. Car Fleet: " + carFleet(12, new int[]{10, 8, 0, 5, 3}, new int[]{2, 4, 1, 1, 3}));
        System.out.println("20. Validate Stack Seq: " + validateStackSequences(new int[]{1, 2, 3, 4, 5}, new int[]{4, 5, 3, 2, 1}));

        System.out.println("\n--- HARD ---");
        System.out.println("21. Largest Rect: " + largestRectangleArea(new int[]{2, 1, 5, 6, 2, 3}));
        System.out.println("22. Maximal Rect: " + maximalRectangle(new char[][]{{'1', '0', '1', '0', '0'}, {'1', '0', '1', '1', '1'}, {'1', '1', '1', '1', '1'}, {'1', '0', '0', '1', '0'}}));
        System.out.println("23. Basic Calc: " + calculate("(1+(4+5+2)-3)+(6+8)"));
        System.out.println("24. Trap Water: " + trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}));
        System.out.println("25. Valid Parens Len: " + longestValidParentheses("(()"));
        FreqStack fs = new FreqStack(); fs.push(5); fs.push(7); fs.push(5); fs.push(7); fs.push(4); fs.push(5);
        System.out.println("26. Freq Stack pop: " + fs.pop() + " " + fs.pop());
        System.out.println("27. Visible People: " + Arrays.toString(canSeePersonsCount(new int[]{10, 6, 8, 5, 11, 9})));
        System.out.println("28. Sum Subarray Min: " + sumSubarrayMins(new int[]{3, 1, 2, 4}));
        System.out.println("29. Remove Dup Letters: " + removeDuplicateLetters("bcabc"));
        MaxStack maxSt = new MaxStack(); maxSt.push(5); maxSt.push(1); maxSt.push(5);
        System.out.println("30. Max Stack peekMax: " + maxSt.peekMax() + " popMax: " + maxSt.popMax());
    }
}

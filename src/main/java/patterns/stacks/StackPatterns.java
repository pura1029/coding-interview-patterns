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
    /**
     * Valid Parentheses — determine if a string of brackets is properly nested and matched.
     *
     * <p><b>Approach:</b> For each opening bracket, push its expected closing bracket.
     * For each closing bracket, pop and verify it matches. The stack must be empty at the end.
     *
     * <p><b>Example:</b> "()[]{}" → true; "(]" → false.
     *
     * @param s string containing '(', ')', '{', '}', '[', ']'
     * @return true if all brackets are valid and properly nested
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(n) — stack may hold up to n/2 elements.
     */
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
    /**
     * Implement Stack using Queues — simulate LIFO behavior using a single FIFO queue.
     *
     * <p><b>Approach:</b> After each push, rotate all existing elements behind the new one
     * so the newest element is always at the front of the queue.
     *
     * <p><b>Operations:</b> push O(n), pop O(1), top O(1), empty O(1).
     */
    static class MyStack {
        Queue<Integer> q = new LinkedList<>();
        public void push(int x) { q.add(x); for (int i = 1; i < q.size(); i++) q.add(q.poll()); }
        public int pop() { return q.poll(); }
        public int top() { return q.peek(); }
        public boolean empty() { return q.isEmpty(); }
    }

    // ======================= EASY 3: Min Stack =======================
    /**
     * Min Stack — design a stack that supports push, pop, top, and retrieving
     * the minimum element, all in O(1) time.
     *
     * <p><b>Approach:</b> Store (value, currentMin) pairs. Each entry remembers
     * the minimum at the time it was pushed, enabling O(1) getMin.
     *
     * <p><b>Operations:</b> All O(1) time, O(n) space.
     */
    static class MinStack {
        Deque<int[]> stack = new ArrayDeque<>();
        public void push(int val) { int min = stack.isEmpty() ? val : Math.min(val, stack.peek()[1]); stack.push(new int[]{val, min}); }
        public void pop() { stack.pop(); }
        public int top() { return stack.peek()[0]; }
        public int getMin() { return stack.peek()[1]; }
    }

    // ======================= EASY 4: Baseball Game =======================
    /**
     * Baseball Game — simulate a baseball scoring system with special operations.
     *
     * <p><b>Approach:</b> Stack simulation — push scores, "+" adds top two,
     * "D" doubles the top, "C" removes the top. Sum remaining elements.
     *
     * <p><b>Example:</b> ["5","2","C","D","+"] → 30.
     *
     * @param operations array of score operations
     * @return total sum of all scores after processing
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(n) — stack of scores.
     */
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
    /**
     * Next Greater Element I — for each element in nums1, find its next greater
     * element in nums2.
     *
     * <p><b>Approach:</b> Build a monotonic decreasing stack over nums2. When a larger
     * element is found, pop and map each popped value to the larger element.
     * Then look up the answer for each element in nums1.
     *
     * <p><b>Example:</b> nums1=[4,1,2], nums2=[1,3,4,2] → [-1,3,-1].
     *
     * @param nums1 subset of nums2 to query
     * @param nums2 full array to search in
     * @return array of next greater elements for each element in nums1 (-1 if none)
     *
     * <p><b>Time:</b> O(m + n) where m = nums1 length, n = nums2 length.
     * <br><b>Space:</b> O(n) — HashMap and stack.
     */
    public static int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> map = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();
        for (int n : nums2) { while (!stack.isEmpty() && stack.peek() < n) map.put(stack.pop(), n); stack.push(n); }
        int[] res = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) res[i] = map.getOrDefault(nums1[i], -1);
        return res;
    }

    // ======================= EASY 6: Backspace String Compare =======================
    /**
     * Backspace String Compare — determine if two strings are equal after applying
     * backspace ('#') operations.
     *
     * <p><b>Approach:</b> Build the final string using a stack: push regular characters,
     * pop on '#'. Compare the resulting strings.
     *
     * <p><b>Example:</b> s="ab#c", t="ad#c" → true (both become "ac").
     *
     * @param s first string with backspaces
     * @param t second string with backspaces
     * @return true if the resulting strings are equal
     *
     * <p><b>Time:</b> O(n + m) — process both strings.
     * <br><b>Space:</b> O(n + m) — stacks for both strings.
     */
    public static boolean backspaceCompare(String s, String t) {
        return build(s).equals(build(t));
    }
    private static String build(String str) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : str.toCharArray()) { if (c == '#') { if (!stack.isEmpty()) stack.pop(); } else stack.push(c); }
        return stack.toString();
    }

    // ======================= EASY 7: Remove All Adjacent Duplicates =======================
    /**
     * Remove All Adjacent Duplicates in String — repeatedly remove pairs of adjacent
     * equal characters until no more can be removed.
     *
     * <p><b>Approach:</b> Stack — if the top equals the current character, pop it
     * (they cancel). Otherwise push. Build result from remaining stack.
     *
     * <p><b>Example:</b> "abbaca" → "ca".
     *
     * @param s input string
     * @return the string after removing all adjacent duplicates
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(n) — stack stores characters.
     */
    public static String removeDuplicates(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) { if (!stack.isEmpty() && stack.peek() == c) stack.pop(); else stack.push(c); }
        StringBuilder sb = new StringBuilder();
        for (char c : stack) sb.append(c);
        return sb.reverse().toString();
    }

    // ======================= EASY 8: Maximum Nesting Depth of Parentheses =======================
    /**
     * Maximum Nesting Depth of Parentheses — find the deepest nesting level.
     *
     * <p><b>Approach:</b> Use a counter as a virtual stack: increment on '(',
     * decrement on ')'. Track the maximum depth reached.
     *
     * <p><b>Example:</b> "(1+(2*3)+((8)/4))+1" → 3.
     *
     * @param s string containing parentheses and other characters
     * @return the maximum nesting depth
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — counter variable only.
     */
    public static int maxDepth(String s) {
        int depth = 0, max = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') max = Math.max(max, ++depth);
            else if (c == ')') depth--;
        }
        return max;
    }

    // ======================= EASY 9: Make The String Great =======================
    /**
     * Make The String Great — remove adjacent characters that are the same letter
     * but different case (e.g., 'a' and 'A') until no more bad pairs exist.
     *
     * <p><b>Approach:</b> Stack — if the absolute difference between the top and
     * current character is 32 (ASCII distance between upper and lower), pop.
     * Otherwise push.
     *
     * <p><b>Example:</b> "leEeetcode" → "leetcode".
     *
     * @param s input string of English letters
     * @return the "great" string after all removals
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(n) — stack stores characters.
     */
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
    /**
     * Crawler Log Folder — find the minimum operations to return to the root folder
     * after executing a series of folder navigation commands.
     *
     * <p><b>Approach:</b> Counter as virtual stack depth: "../" decrements (min 0),
     * "./" is a no-op, anything else increments. Final counter = operations needed.
     *
     * <p><b>Example:</b> ["d1/","d2/","../","d21/","./"] → 2.
     *
     * @param logs array of folder navigation operations
     * @return minimum operations to return to root
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — single counter.
     */
    public static int minOperations(String[] logs) {
        int depth = 0;
        for (String log : logs) {
            if (log.equals("../")) depth = Math.max(0, depth - 1);
            else if (!log.equals("./")) depth++;
        }
        return depth;
    }

    // ======================= MEDIUM 1: Evaluate Reverse Polish Notation =======================
    /**
     * Evaluate Reverse Polish Notation — evaluate an arithmetic expression in
     * postfix (RPN) notation.
     *
     * <p><b>Approach:</b> Push numbers onto the stack. On an operator, pop two operands,
     * compute the result, and push it back. The final stack element is the answer.
     *
     * <p><b>Example:</b> ["2","1","+","3","*"] → 9.
     *
     * @param tokens array of numbers and operators (+, -, *, /)
     * @return the evaluation result
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(n) — stack stores operands.
     */
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
    /**
     * Daily Temperatures — for each day, find how many days until a warmer temperature.
     *
     * <p><b>Approach:</b> Monotonic decreasing stack of indices. When a warmer day is
     * found (current temp > stack top temp), pop and record the day gap.
     *
     * <p><b>Example:</b> [73,74,75,71,69,72,76,73] → [1,1,4,2,1,1,0,0].
     *
     * @param temperatures array of daily temperatures
     * @return array where result[i] = days until a warmer temperature (0 if none)
     *
     * <p><b>Time:</b> O(n) — each index is pushed and popped at most once.
     * <br><b>Space:</b> O(n) — stack and result array.
     */
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
    /**
     * Decode String — decode an encoded string like "3[a2[c]]" → "accaccacc".
     *
     * <p><b>Approach:</b> Two stacks: one for repeat counts, one for accumulated strings.
     * On '[', push current state. On ']', pop and repeat the enclosed string.
     * Digits accumulate into the count; letters accumulate into the current string.
     *
     * <p><b>Example:</b> "3[a]2[bc]" → "aaabcbc".
     *
     * @param s encoded string
     * @return decoded string
     *
     * <p><b>Time:</b> O(n · max_k) where max_k = maximum repeat count.
     * <br><b>Space:</b> O(n) — stacks for counts and partial strings.
     */
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
    /**
     * Simplify Path — simplify a Unix-style absolute file path.
     *
     * <p><b>Approach:</b> Split by '/' and use a stack of directory names.
     * ".." pops the top (go up), "." and empty are ignored, valid names are pushed.
     * Join remaining stack entries with '/'.
     *
     * <p><b>Example:</b> "/home//foo/" → "/home/foo".
     *
     * @param path the absolute file path
     * @return the simplified canonical path
     *
     * <p><b>Time:</b> O(n) — single pass through path components.
     * <br><b>Space:</b> O(n) — stack of directory names.
     */
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
    /**
     * Remove K Digits — remove k digits from a number string to make it the smallest possible.
     *
     * <p><b>Approach:</b> Monotonic increasing stack — whenever the current digit is
     * smaller than the stack top and we still have removals left, pop the larger digit.
     * Remove remaining digits from the end. Strip leading zeros.
     *
     * <p><b>Example:</b> num="1432219", k=3 → "1219".
     *
     * @param num the number as a string
     * @param k   number of digits to remove
     * @return the smallest possible number as a string
     *
     * <p><b>Time:</b> O(n) — each digit is pushed and popped at most once.
     * <br><b>Space:</b> O(n) — stack stores digits.
     */
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
    /**
     * Asteroid Collision — simulate asteroid collisions in a line. Positive = moving right,
     * negative = moving left. When they collide, the smaller one explodes; equal → both explode.
     *
     * <p><b>Approach:</b> Stack — push each asteroid. A negative asteroid collides with
     * positive ones on the stack top. Compare sizes: smaller explodes, equal → both explode,
     * larger survives.
     *
     * <p><b>Example:</b> [5,10,-5] → [5,10] (−5 is destroyed by 10).
     *
     * @param asteroids array of asteroid sizes (positive = right, negative = left)
     * @return the surviving asteroids after all collisions
     *
     * <p><b>Time:</b> O(n) — each asteroid is processed at most twice.
     * <br><b>Space:</b> O(n) — stack stores surviving asteroids.
     */
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
    /**
     * Flatten Nested List Iterator — flatten a nested list structure into a single list
     * of integers.
     *
     * <p><b>Approach:</b> Stack of iterators. Push the top-level iterator. When the next
     * element is a list, push its iterator. When it's an integer, collect it.
     *
     * @param nestedList a nested structure of integers and lists
     * @return flattened list of all integers
     *
     * <p><b>Time:</b> O(n) where n = total number of integers.
     * <br><b>Space:</b> O(d) where d = maximum nesting depth.
     */
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
    /**
     * Online Stock Span — compute the span of a stock's price for the current day.
     * The span is the number of consecutive days (including today) where the price
     * was less than or equal to today's price.
     *
     * <p><b>Approach:</b> Monotonic stack of (price, span) pairs. Pop and accumulate
     * spans while the stack top price ≤ current price.
     *
     * <p><b>Operations:</b> O(1) amortized per call.
     */
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
    /**
     * Car Fleet — determine how many car fleets will arrive at the destination.
     * Cars closer to the target that are slower block faster cars behind them.
     *
     * <p><b>Approach:</b> Sort cars by position (descending). Compute arrival time
     * for each car. A new fleet forms when a car's arrival time exceeds
     * the arrival time of the fleet ahead of it.
     *
     * <p><b>Example:</b> target=12, position=[10,8,0,5,3], speed=[2,4,1,1,3] → 3.
     *
     * @param target   destination position
     * @param position starting positions of each car
     * @param speed    speeds of each car
     * @return number of car fleets
     *
     * <p><b>Time:</b> O(n log n) — dominated by sorting.
     * <br><b>Space:</b> O(n) — array of (position, time) pairs.
     */
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
    /**
     * Validate Stack Sequences — determine if a given pushed/popped sequence is valid.
     *
     * <p><b>Approach:</b> Simulate: push elements from the pushed array. After each push,
     * pop as long as the stack top matches the next expected popped element.
     * Valid if the stack is empty at the end.
     *
     * <p><b>Example:</b> pushed=[1,2,3,4,5], popped=[4,5,3,2,1] → true.
     *
     * @param pushed the push sequence
     * @param popped the pop sequence
     * @return true if the pop sequence is valid given the push sequence
     *
     * <p><b>Time:</b> O(n) — each element is pushed and popped at most once.
     * <br><b>Space:</b> O(n) — simulation stack.
     */
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
    /**
     * Largest Rectangle in Histogram — find the largest rectangular area in a histogram.
     *
     * <p><b>Approach:</b> Monotonic increasing stack of indices. When a shorter bar appears,
     * pop and compute the rectangle: height = popped bar, width = distance between current
     * index and the new stack top. Sentinel height=0 flushes remaining bars.
     *
     * <p><b>Example:</b> [2,1,5,6,2,3] → 10.
     *
     * @param heights histogram bar heights
     * @return area of the largest rectangle
     *
     * <p><b>Time:</b> O(n) — each bar pushed and popped once.
     * <br><b>Space:</b> O(n) — stack.
     */
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
    /**
     * Maximal Rectangle — find the largest rectangle containing only 1s in a binary matrix.
     *
     * <p><b>Approach:</b> Build a histogram row by row (height[j] = consecutive 1s above).
     * Apply the "Largest Rectangle in Histogram" algorithm to each row's histogram.
     *
     * <p><b>Example:</b> [['1','0','1','0','0'],['1','0','1','1','1'],
     * ['1','1','1','1','1'],['1','0','0','1','0']] → 6.
     *
     * @param matrix binary matrix of '0' and '1' characters
     * @return area of the largest rectangle of 1s
     *
     * <p><b>Time:</b> O(m·n) — process each row's histogram in O(n).
     * <br><b>Space:</b> O(n) — histogram array and stack.
     */
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
    /**
     * Basic Calculator — evaluate a mathematical expression with +, −, and nested parentheses.
     *
     * <p><b>Approach:</b> Track running result and sign. On '(', save current result
     * and sign to the stack, then reset. On ')', apply the saved sign and add to
     * the saved result. Handles multi-digit numbers.
     *
     * <p><b>Example:</b> "(1+(4+5+2)-3)+(6+8)" → 23.
     *
     * @param s mathematical expression string
     * @return the evaluation result
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(n) — stack for nested parentheses.
     */
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
    /**
     * Trapping Rain Water (Stack-Based) — compute trapped water using a stack.
     *
     * <p><b>Approach:</b> Maintain a monotonic decreasing stack of indices.
     * When a taller bar is found, pop the bottom bar and compute the trapped water
     * between the new bar and the new stack top.
     *
     * <p><b>Example:</b> [0,1,0,2,1,0,1,3,2,1,2,1] → 6.
     *
     * @param height array of bar heights
     * @return total trapped water units
     *
     * <p><b>Time:</b> O(n) — each bar pushed and popped at most once.
     * <br><b>Space:</b> O(n) — stack.
     */
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
    /**
     * Longest Valid Parentheses — find the length of the longest valid parentheses substring.
     *
     * <p><b>Approach:</b> Stack of indices with -1 as a base marker. On '(' push index.
     * On ')' pop — if stack becomes empty, push current index as new base;
     * otherwise, valid length = current index − stack top.
     *
     * <p><b>Example:</b> "(()" → 2; ")()())" → 4.
     *
     * @param s string of '(' and ')'
     * @return length of the longest valid substring
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(n) — stack of indices.
     */
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
    /**
     * Maximum Frequency Stack — design a stack that pops the most frequent element.
     * If there's a tie, pop the most recently pushed among the most frequent.
     *
     * <p><b>Approach:</b> Frequency map + stacks grouped by frequency.
     * push() increments frequency and pushes to the corresponding group stack.
     * pop() pops from the highest frequency group stack.
     *
     * <p><b>Operations:</b> O(1) push and pop.
     */
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
    /**
     * Number of Visible People in a Queue — for each person, count how many people
     * to their right they can see (before being blocked by someone taller or equal).
     *
     * <p><b>Approach:</b> Monotonic stack from right to left. Pop shorter people (each is
     * visible), count the pops. If the stack is non-empty after popping, the top person
     * is also visible (but blocks further view).
     *
     * <p><b>Example:</b> [10,6,8,5,11,9] → [3,1,2,1,1,0].
     *
     * @param heights array of people's heights
     * @return array of visible person counts for each position
     *
     * <p><b>Time:</b> O(n) — each person pushed and popped at most once.
     * <br><b>Space:</b> O(n) — stack.
     */
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
    /**
     * Sum of Subarray Minimums — find the sum of the minimum element of every subarray.
     *
     * <p><b>Approach:</b> Monotonic stack — for each element, determine the number of
     * subarrays where it is the minimum by computing the distance to the previous
     * smaller (left) and next smaller-or-equal (right) elements.
     * Contribution = arr[j] × left × right.
     *
     * <p><b>Example:</b> [3,1,2,4] → 17.
     *
     * @param arr array of positive integers
     * @return sum of minimums of all subarrays, modulo 10^9 + 7
     *
     * <p><b>Time:</b> O(n) — each element pushed and popped once.
     * <br><b>Space:</b> O(n) — stack.
     */
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
    /**
     * Remove Duplicate Letters — remove duplicate letters so each letter appears once
     * and the result is the smallest lexicographic order possible.
     *
     * <p><b>Approach:</b> Monotonic increasing stack with last-occurrence tracking.
     * If the current character is smaller than the stack top and the top character
     * appears later in the string, pop it (we can use it later for a better position).
     * Skip characters already in the stack.
     *
     * <p><b>Example:</b> "bcabc" → "abc".
     *
     * @param s input string of lowercase letters
     * @return the smallest subsequence with unique characters
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — stack holds at most 26 characters.
     */
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
    /**
     * Max Stack — design a stack that supports push, pop, top, peekMax, and popMax.
     *
     * <p><b>Approach:</b> Two stacks — main stack and a max-tracking stack.
     * Each push records the running max. popMax finds the max, moves elements to
     * a temp stack, removes the max, and pushes elements back.
     *
     * <p><b>Operations:</b> push/pop/top/peekMax O(1), popMax O(n).
     */
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

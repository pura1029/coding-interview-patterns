package patterns.monotonicstack;

import java.util.*;

/**
 * PATTERN 7: MONOTONIC STACK
 *
 * Stack maintaining increasing/decreasing order for next greater/smaller element problems.
 *
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class MonotonicStackPatterns {

    // EASY 1: Daily Temperatures
    public static int[] dailyTemperatures(int[] t) { int n = t.length; int[] r = new int[n]; Deque<Integer> st = new ArrayDeque<>(); for (int i = 0; i < n; i++) { while (!st.isEmpty() && t[i] > t[st.peek()]) { int j = st.pop(); r[j] = i - j; } st.push(i); } return r; }

    // EASY 2: Next Greater Element I
    public static int[] nextGreaterElement(int[] nums1, int[] nums2) { Map<Integer, Integer> nge = new HashMap<>(); Deque<Integer> st = new ArrayDeque<>(); for (int n : nums2) { while (!st.isEmpty() && st.peek() < n) nge.put(st.pop(), n); st.push(n); } int[] r = new int[nums1.length]; for (int i = 0; i < nums1.length; i++) r[i] = nge.getOrDefault(nums1[i], -1); return r; }

    // EASY 3: Final Prices With Special Discount
    public static int[] finalPrices(int[] prices) { Deque<Integer> st = new ArrayDeque<>(); int[] r = Arrays.copyOf(prices, prices.length); for (int i = 0; i < prices.length; i++) { while (!st.isEmpty() && prices[st.peek()] >= prices[i]) r[st.pop()] -= prices[i]; st.push(i); } return r; }

    // EASY 4: Remove Outermost Parentheses
    public static String removeOuterParentheses(String s) { StringBuilder sb = new StringBuilder(); int depth = 0; for (char c : s.toCharArray()) { if (c == '(' && depth++ > 0) sb.append(c); if (c == ')' && --depth > 0) sb.append(c); } return sb.toString(); }

    // EASY 5: Make The String Great (remove adjacent pairs of same letter different case)
    public static String makeGood(String s) { Deque<Character> st = new ArrayDeque<>(); for (char c : s.toCharArray()) { if (!st.isEmpty() && Math.abs(st.peek() - c) == 32) st.pop(); else st.push(c); } StringBuilder sb = new StringBuilder(); for (char c : st) sb.append(c); return sb.reverse().toString(); }

    // EASY 6: Remove All Adjacent Duplicates In String
    public static String removeDuplicates(String s) { Deque<Character> st = new ArrayDeque<>(); for (char c : s.toCharArray()) { if (!st.isEmpty() && st.peek() == c) st.pop(); else st.push(c); } StringBuilder sb = new StringBuilder(); for (char c : st) sb.append(c); return sb.reverse().toString(); }

    // EASY 7: Backspace String Compare
    public static boolean backspaceCompare(String s, String t) { return processBackspace(s).equals(processBackspace(t)); }
    private static String processBackspace(String s) { Deque<Character> st = new ArrayDeque<>(); for (char c : s.toCharArray()) { if (c == '#') { if (!st.isEmpty()) st.pop(); } else st.push(c); } return st.toString(); }

    // EASY 8: Baseball Game
    public static int calPoints(String[] ops) { Deque<Integer> st = new ArrayDeque<>(); for (String op : ops) { switch (op) { case "+": int a = st.pop(), b = st.peek(); st.push(a); st.push(a + b); break; case "D": st.push(2 * st.peek()); break; case "C": st.pop(); break; default: st.push(Integer.parseInt(op)); } } int s = 0; for (int v : st) s += v; return s; }

    // EASY 9: Valid Parentheses
    public static boolean isValid(String s) { Deque<Character> st = new ArrayDeque<>(); for (char c : s.toCharArray()) { if (c == '(') st.push(')'); else if (c == '[') st.push(']'); else if (c == '{') st.push('}'); else if (st.isEmpty() || st.pop() != c) return false; } return st.isEmpty(); }

    // EASY 10: Min Stack
    static class MinStack { Deque<int[]> st = new ArrayDeque<>(); public void push(int v) { int m = st.isEmpty() ? v : Math.min(v, st.peek()[1]); st.push(new int[]{v, m}); } public void pop() { st.pop(); } public int top() { return st.peek()[0]; } public int getMin() { return st.peek()[1]; } }

    // MEDIUM 1: Next Greater Element II (Circular)
    public static int[] nextGreaterCircular(int[] nums) { int n = nums.length; int[] r = new int[n]; Arrays.fill(r, -1); Deque<Integer> st = new ArrayDeque<>(); for (int i = 0; i < 2 * n; i++) { while (!st.isEmpty() && nums[st.peek()] < nums[i % n]) r[st.pop()] = nums[i % n]; if (i < n) st.push(i); } return r; }

    // MEDIUM 2: Online Stock Span
    static class StockSpanner { Deque<int[]> st = new ArrayDeque<>(); public int next(int price) { int span = 1; while (!st.isEmpty() && st.peek()[0] <= price) span += st.pop()[1]; st.push(new int[]{price, span}); return span; } }

    // MEDIUM 3: Remove K Digits
    public static String removeKdigits(String num, int k) { Deque<Character> st = new ArrayDeque<>(); for (char c : num.toCharArray()) { while (k > 0 && !st.isEmpty() && st.peek() > c) { st.pop(); k--; } st.push(c); } while (k-- > 0) st.pop(); StringBuilder sb = new StringBuilder(); for (char c : st) sb.append(c); sb.reverse(); while (sb.length() > 0 && sb.charAt(0) == '0') sb.deleteCharAt(0); return sb.length() == 0 ? "0" : sb.toString(); }

    // MEDIUM 4: 132 Pattern
    public static boolean find132pattern(int[] nums) { int n = nums.length, third = Integer.MIN_VALUE; Deque<Integer> st = new ArrayDeque<>(); for (int i = n - 1; i >= 0; i--) { if (nums[i] < third) return true; while (!st.isEmpty() && st.peek() < nums[i]) third = st.pop(); st.push(nums[i]); } return false; }

    // MEDIUM 5: Remove Duplicate Letters (smallest lexicographic subsequence)
    public static String removeDuplicateLetters(String s) { int[] last = new int[26]; for (int i = 0; i < s.length(); i++) last[s.charAt(i)-'a'] = i; Deque<Character> st = new ArrayDeque<>(); boolean[] inStack = new boolean[26]; for (int i = 0; i < s.length(); i++) { char c = s.charAt(i); if (inStack[c-'a']) continue; while (!st.isEmpty() && st.peek() > c && last[st.peek()-'a'] > i) { inStack[st.pop()-'a'] = false; } st.push(c); inStack[c-'a'] = true; } StringBuilder sb = new StringBuilder(); for (char c : st) sb.append(c); return sb.reverse().toString(); }

    // MEDIUM 6: Asteroid Collision
    public static int[] asteroidCollision(int[] asteroids) { Deque<Integer> st = new ArrayDeque<>(); for (int a : asteroids) { boolean alive = true; while (alive && a < 0 && !st.isEmpty() && st.peek() > 0) { if (st.peek() < -a) st.pop(); else if (st.peek() == -a) { st.pop(); alive = false; } else alive = false; } if (alive) st.push(a); } int[] r = new int[st.size()]; for (int i = r.length - 1; i >= 0; i--) r[i] = st.pop(); return r; }

    // MEDIUM 7: Sum of Subarray Minimums
    public static int sumSubarrayMins(int[] arr) { int MOD = 1_000_000_007, n = arr.length; long sum = 0; Deque<Integer> st = new ArrayDeque<>(); for (int i = 0; i <= n; i++) { int cur = (i == n) ? 0 : arr[i]; while (!st.isEmpty() && arr[st.peek()] >= cur) { int j = st.pop(); int left = st.isEmpty() ? j + 1 : j - st.peek(); int right = i - j; sum = (sum + (long) arr[j] * left % MOD * right % MOD) % MOD; } st.push(i); } return (int) sum; }

    // MEDIUM 8: Decode String
    public static String decodeString(String s) { Deque<StringBuilder> stStr = new ArrayDeque<>(); Deque<Integer> stNum = new ArrayDeque<>(); StringBuilder cur = new StringBuilder(); int num = 0; for (char c : s.toCharArray()) { if (Character.isDigit(c)) num = num * 10 + (c - '0'); else if (c == '[') { stNum.push(num); stStr.push(cur); cur = new StringBuilder(); num = 0; } else if (c == ']') { int k = stNum.pop(); StringBuilder prev = stStr.pop(); for (int i = 0; i < k; i++) prev.append(cur); cur = prev; } else cur.append(c); } return cur.toString(); }

    // MEDIUM 9: Car Fleet
    public static int carFleet(int target, int[] position, int[] speed) { int n = position.length; int[][] cars = new int[n][2]; for (int i = 0; i < n; i++) cars[i] = new int[]{position[i], speed[i]}; Arrays.sort(cars, (a, b) -> b[0] - a[0]); int fleets = 0; double lastTime = 0; for (int[] c : cars) { double time = (double)(target - c[0]) / c[1]; if (time > lastTime) { fleets++; lastTime = time; } } return fleets; }

    // MEDIUM 10: Evaluate Reverse Polish Notation
    public static int evalRPN(String[] tokens) { Deque<Integer> st = new ArrayDeque<>(); for (String t : tokens) { switch (t) { case "+": st.push(st.pop() + st.pop()); break; case "-": int b = st.pop(), a = st.pop(); st.push(a - b); break; case "*": st.push(st.pop() * st.pop()); break; case "/": int d = st.pop(), c = st.pop(); st.push(c / d); break; default: st.push(Integer.parseInt(t)); } } return st.pop(); }

    // HARD 1: Largest Rectangle in Histogram
    public static int largestRectangleArea(int[] h) { int n = h.length, max = 0; Deque<Integer> st = new ArrayDeque<>(); for (int i = 0; i <= n; i++) { int cur = i == n ? 0 : h[i]; while (!st.isEmpty() && cur < h[st.peek()]) { int height = h[st.pop()]; int w = st.isEmpty() ? i : i - st.peek() - 1; max = Math.max(max, height * w); } st.push(i); } return max; }

    // HARD 2: Maximal Rectangle
    public static int maximalRectangle(char[][] matrix) { if (matrix.length == 0) return 0; int n = matrix[0].length, max = 0; int[] h = new int[n]; for (char[] row : matrix) { for (int j = 0; j < n; j++) h[j] = row[j] == '1' ? h[j] + 1 : 0; max = Math.max(max, largestRectangleArea(h)); } return max; }

    // HARD 3: Trapping Rain Water (using stack)
    public static int trap(int[] height) { Deque<Integer> st = new ArrayDeque<>(); int water = 0; for (int i = 0; i < height.length; i++) { while (!st.isEmpty() && height[i] > height[st.peek()]) { int bottom = height[st.pop()]; if (st.isEmpty()) break; int w = i - st.peek() - 1; int h = Math.min(height[i], height[st.peek()]) - bottom; water += w * h; } st.push(i); } return water; }

    // HARD 4: Sum of Subarray Ranges (max - min for all subarrays)
    public static long subArrayRanges(int[] nums) { int n = nums.length; long sum = 0; Deque<Integer> st = new ArrayDeque<>(); for (int i = 0; i <= n; i++) { while (!st.isEmpty() && (i == n || nums[st.peek()] >= nums[i])) { int j = st.pop(); int left = st.isEmpty() ? j + 1 : j - st.peek(); sum -= (long) nums[j] * left * (i - j); } st.push(i); } st.clear(); for (int i = 0; i <= n; i++) { while (!st.isEmpty() && (i == n || nums[st.peek()] <= nums[i])) { int j = st.pop(); int left = st.isEmpty() ? j + 1 : j - st.peek(); sum += (long) nums[j] * left * (i - j); } st.push(i); } return sum; }

    // HARD 5: Number of Visible People in a Queue
    public static int[] canSeePersonsCount(int[] heights) { int n = heights.length; int[] r = new int[n]; Deque<Integer> st = new ArrayDeque<>(); for (int i = n - 1; i >= 0; i--) { int cnt = 0; while (!st.isEmpty() && heights[st.peek()] < heights[i]) { st.pop(); cnt++; } if (!st.isEmpty()) cnt++; r[i] = cnt; st.push(i); } return r; }

    // HARD 6: Maximum Width Ramp (largest j-i where nums[i] <= nums[j])
    public static int maxWidthRamp(int[] nums) { int n = nums.length; Deque<Integer> st = new ArrayDeque<>(); for (int i = 0; i < n; i++) if (st.isEmpty() || nums[st.peek()] > nums[i]) st.push(i); int max = 0; for (int j = n - 1; j >= 0; j--) while (!st.isEmpty() && nums[st.peek()] <= nums[j]) max = Math.max(max, j - st.pop()); return max; }

    // HARD 7: Steps to Make Array Non-decreasing
    public static int totalSteps(int[] nums) { int n = nums.length, max = 0; Deque<int[]> st = new ArrayDeque<>(); for (int i = n - 1; i >= 0; i--) { int steps = 0; while (!st.isEmpty() && nums[i] > st.peek()[0]) steps = Math.max(steps + 1, st.pop()[1]); st.push(new int[]{nums[i], steps}); max = Math.max(max, steps); } return max; }

    // HARD 8: Longest Valid Parentheses
    public static int longestValidParentheses(String s) { Deque<Integer> st = new ArrayDeque<>(); st.push(-1); int max = 0; for (int i = 0; i < s.length(); i++) { if (s.charAt(i) == '(') st.push(i); else { st.pop(); if (st.isEmpty()) st.push(i); else max = Math.max(max, i - st.peek()); } } return max; }

    // HARD 9: Create Maximum Number (merge two arrays to form largest k digits)
    public static int[] maxNumber(int[] nums1, int[] nums2, int k) { int[] best = new int[k]; for (int i = Math.max(0, k - nums2.length); i <= Math.min(k, nums1.length); i++) { int[] a = maxSubseq(nums1, i), b = maxSubseq(nums2, k - i); int[] merged = merge(a, b); if (compare(merged, 0, best, 0) > 0) best = merged; } return best; }
    private static int[] maxSubseq(int[] nums, int k) { int[] r = new int[k]; int drop = nums.length - k, j = 0; for (int n : nums) { while (j > 0 && drop > 0 && r[j - 1] < n) { j--; drop--; } if (j < k) r[j++] = n; else drop--; } return r; }
    private static int[] merge(int[] a, int[] b) { int[] r = new int[a.length + b.length]; int i = 0, j = 0, k = 0; while (i < a.length && j < b.length) r[k++] = compare(a, i, b, j) >= 0 ? a[i++] : b[j++]; while (i < a.length) r[k++] = a[i++]; while (j < b.length) r[k++] = b[j++]; return r; }
    private static int compare(int[] a, int i, int[] b, int j) { while (i < a.length && j < b.length && a[i] == b[j]) { i++; j++; } if (i == a.length) return -1; if (j == b.length) return 1; return a[i] - b[j]; }

    // HARD 10: Minimum Cost Tree From Leaf Values
    public static int mctFromLeafValues(int[] arr) { Deque<Integer> st = new ArrayDeque<>(); st.push(Integer.MAX_VALUE); int cost = 0; for (int v : arr) { while (st.peek() <= v) cost += st.pop() * Math.min(st.peek(), v); st.push(v); } while (st.size() > 2) cost += st.pop() * st.peek(); return cost; }

    public static void main(String[] args) {
        System.out.println("=== MONOTONIC STACK PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        System.out.println("1. Daily Temps: " + Arrays.toString(dailyTemperatures(new int[]{73,74,75,71,69,72,76,73})));
        System.out.println("2. Next Greater I: " + Arrays.toString(nextGreaterElement(new int[]{4,1,2}, new int[]{1,3,4,2})));
        System.out.println("3. Final Prices: " + Arrays.toString(finalPrices(new int[]{8,4,6,2,3})));
        System.out.println("4. Remove Outer Parens: " + removeOuterParentheses("(()())(())"));
        System.out.println("5. Make Good: " + makeGood("leEeetcode"));
        System.out.println("6. Remove Adj Dups: " + removeDuplicates("abbaca"));
        System.out.println("7. Backspace Compare: " + backspaceCompare("ab#c", "ad#c"));
        System.out.println("8. Baseball Game: " + calPoints(new String[]{"5","2","C","D","+"}));
        System.out.println("9. Valid Parens: " + isValid("()[]{}"));
        MinStack ms = new MinStack(); ms.push(-2); ms.push(0); ms.push(-3);
        System.out.println("10. Min Stack: " + ms.getMin());
        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Next Greater Circ: " + Arrays.toString(nextGreaterCircular(new int[]{1,2,1})));
        StockSpanner sp = new StockSpanner();
        System.out.println("12. Stock Span: " + sp.next(100) + "," + sp.next(80) + "," + sp.next(60) + "," + sp.next(70) + "," + sp.next(60) + "," + sp.next(75) + "," + sp.next(85));
        System.out.println("13. Remove K Digits: " + removeKdigits("1432219", 3));
        System.out.println("14. 132 Pattern: " + find132pattern(new int[]{3,1,4,2}));
        System.out.println("15. Remove Dup Letters: " + removeDuplicateLetters("cbacdcbc"));
        System.out.println("16. Asteroid Collision: " + Arrays.toString(asteroidCollision(new int[]{5,10,-5})));
        System.out.println("17. Sum Subarray Mins: " + sumSubarrayMins(new int[]{3,1,2,4}));
        System.out.println("18. Decode String: " + decodeString("3[a2[c]]"));
        System.out.println("19. Car Fleet: " + carFleet(12, new int[]{10,8,0,5,3}, new int[]{2,4,1,1,3}));
        System.out.println("20. Eval RPN: " + evalRPN(new String[]{"2","1","+","3","*"}));
        System.out.println("\n--- HARD ---");
        System.out.println("21. Largest Rect: " + largestRectangleArea(new int[]{2,1,5,6,2,3}));
        System.out.println("22. Maximal Rect: " + maximalRectangle(new char[][]{{'1','0','1','0','0'},{'1','0','1','1','1'},{'1','1','1','1','1'},{'1','0','0','1','0'}}));
        System.out.println("23. Trap Water: " + trap(new int[]{0,1,0,2,1,0,1,3,2,1,2,1}));
        System.out.println("24. SubArr Ranges: " + subArrayRanges(new int[]{1,2,3}));
        System.out.println("25. Visible People: " + Arrays.toString(canSeePersonsCount(new int[]{10,6,8,5,11,9})));
        System.out.println("26. Max Width Ramp: " + maxWidthRamp(new int[]{6,0,8,2,1,5}));
        System.out.println("27. Total Steps: " + totalSteps(new int[]{5,3,4,4,7,3,6,11,8,5,11}));
        System.out.println("28. Longest Valid Parens: " + longestValidParentheses("(()"));
        System.out.println("29. Max Number: " + Arrays.toString(maxNumber(new int[]{3,4,6,5}, new int[]{9,1,2,5,8,3}, 5)));
        System.out.println("30. MCT Leaf: " + mctFromLeafValues(new int[]{6,2,4}));
    }
}

package patterns.arrays;

import java.util.*;

/**
 * ARRAYS — 30 Essential Problems
 * Arrays store elements contiguously in memory for O(1) random access.
 * Mastering in-place operations, two-pointer techniques, hashing, and sorting
 * on arrays is critical for coding interviews.
 *
 * 10 Easy | 10 Medium | 10 Hard
 */
public class ArraysPatterns {

    // ======================= EASY 1: Two Sum =======================
    /** Hash map stores seen values; for each num check if complement (target-num) exists. O(n) time, O(n) space. */
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) return new int[]{map.get(complement), i};
            map.put(nums[i], i);
        }
        return new int[]{};
    }

    // ======================= EASY 2: Best Time to Buy and Sell Stock =======================
    /** Track running minimum price; max profit = max(price - minSoFar). O(n) time, O(1) space. */
    public static int maxProfit(int[] prices) {
        int min = Integer.MAX_VALUE, maxProfit = 0;
        for (int p : prices) { min = Math.min(min, p); maxProfit = Math.max(maxProfit, p - min); }
        return maxProfit;
    }

    // ======================= EASY 3: Contains Duplicate =======================
    /** HashSet.add returns false if element already present. O(n) time, O(n) space. */
    public static boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int n : nums) if (!seen.add(n)) return true;
        return false;
    }

    // ======================= EASY 4: Merge Sorted Array =======================
    /** Fill nums1 from the back using three pointers (i, j, k). O(m+n) time, O(1) space. */
    public static void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1, j = n - 1, k = m + n - 1;
        while (j >= 0) {
            if (i >= 0 && nums1[i] > nums2[j]) nums1[k--] = nums1[i--];
            else nums1[k--] = nums2[j--];
        }
    }

    // ======================= EASY 5: Maximum Subarray (Kadane's) =======================
    /** Kadane's: at each index, decide to extend current subarray or start fresh. O(n) time, O(1) space. */
    public static int maxSubArray(int[] nums) {
        int max = nums[0], cur = nums[0];
        for (int i = 1; i < nums.length; i++) { cur = Math.max(nums[i], cur + nums[i]); max = Math.max(max, cur); }
        return max;
    }

    // ======================= EASY 6: Remove Duplicates from Sorted Array =======================
    /** Slow pointer marks write position; fast pointer scans ahead for new values. O(n) time, O(1) space. */
    public static int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;
        int slow = 0;
        for (int fast = 1; fast < nums.length; fast++) if (nums[fast] != nums[slow]) nums[++slow] = nums[fast];
        return slow + 1;
    }

    // ======================= EASY 7: Single Number =======================
    /** XOR all elements; duplicates cancel out, leaving the unique one. O(n) time, O(1) space. */
    public static int singleNumber(int[] nums) {
        int result = 0;
        for (int n : nums) result ^= n;
        return result;
    }

    // ======================= EASY 8: Move Zeroes =======================
    /** Snowball approach: track zero count, swap non-zero with first zero. O(n) time, O(1) space. */
    public static void moveZeroes(int[] nums) {
        int write = 0;
        for (int read = 0; read < nums.length; read++) {
            if (nums[read] != 0) { int t = nums[write]; nums[write] = nums[read]; nums[read] = t; write++; }
        }
    }

    // ======================= EASY 9: Plus One =======================
    /** Traverse from end; if digit < 9 increment and return, else set to 0 (carry). O(n) time, O(1) space. */
    public static int[] plusOne(int[] digits) {
        for (int i = digits.length - 1; i >= 0; i--) {
            if (digits[i] < 9) { digits[i]++; return digits; }
            digits[i] = 0;
        }
        int[] result = new int[digits.length + 1];
        result[0] = 1;
        return result;
    }

    // ======================= EASY 10: Missing Number =======================
    /** Sum formula: expected = n*(n+1)/2, subtract actual sum. O(n) time, O(1) space. */
    public static int missingNumber(int[] nums) {
        int n = nums.length, sum = n * (n + 1) / 2;
        for (int num : nums) sum -= num;
        return sum;
    }

    // ======================= MEDIUM 1: Three Sum =======================
    /** Sort array, fix one element, use two pointers for the pair; skip duplicates. O(n²) time, O(1) space. */
    public static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            int l = i + 1, r = nums.length - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (sum == 0) { result.add(Arrays.asList(nums[i], nums[l], nums[r])); while (l < r && nums[l] == nums[l + 1]) l++; while (l < r && nums[r] == nums[r - 1]) r--; l++; r--; }
                else if (sum < 0) l++; else r--;
            }
        }
        return result;
    }

    // ======================= MEDIUM 2: Container With Most Water =======================
    /** Two pointers at both ends; move the shorter side inward. O(n) time, O(1) space. */
    public static int maxArea(int[] height) {
        int l = 0, r = height.length - 1, max = 0;
        while (l < r) { max = Math.max(max, Math.min(height[l], height[r]) * (r - l)); if (height[l] < height[r]) l++; else r--; }
        return max;
    }

    // ======================= MEDIUM 3: Product of Array Except Self =======================
    /** Two passes: left-to-right prefix product, then right-to-left suffix product. O(n) time, O(1) extra space. */
    public static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        res[0] = 1;
        for (int i = 1; i < n; i++) res[i] = res[i - 1] * nums[i - 1];
        int right = 1;
        for (int i = n - 1; i >= 0; i--) { res[i] *= right; right *= nums[i]; }
        return res;
    }

    // ======================= MEDIUM 4: Rotate Array =======================
    /** Triple-reverse trick: reverse all, reverse first k, reverse rest. O(n) time, O(1) space. */
    public static void rotate(int[] nums, int k) {
        k %= nums.length;
        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }
    private static void reverse(int[] nums, int l, int r) { while (l < r) { int t = nums[l]; nums[l++] = nums[r]; nums[r--] = t; } }

    // ======================= MEDIUM 5: Sort Colors (Dutch National Flag) =======================
    /** Three-way partition: lo/mid/hi pointers separate 0s, 1s, and 2s in one pass. O(n) time, O(1) space. */
    public static void sortColors(int[] nums) {
        int lo = 0, mid = 0, hi = nums.length - 1;
        while (mid <= hi) {
            if (nums[mid] == 0) { int t = nums[lo]; nums[lo++] = nums[mid]; nums[mid++] = t; }
            else if (nums[mid] == 1) mid++;
            else { int t = nums[mid]; nums[mid] = nums[hi]; nums[hi--] = t; }
        }
    }

    // ======================= MEDIUM 6: Next Permutation =======================
    /** Find rightmost ascending pair, swap with next larger element, reverse suffix. O(n) time, O(1) space. */
    public static void nextPermutation(int[] nums) {
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) i--;
        if (i >= 0) { int j = nums.length - 1; while (nums[j] <= nums[i]) j--; int t = nums[i]; nums[i] = nums[j]; nums[j] = t; }
        reverse(nums, i + 1, nums.length - 1);
    }

    // ======================= MEDIUM 7: Subarray Sum Equals K =======================
    /** Prefix sum with hash map counting previous prefix sums. O(n) time, O(n) space. */
    public static int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);
        int sum = 0, count = 0;
        for (int n : nums) {
            sum += n;
            count += map.getOrDefault(sum - k, 0);
            map.merge(sum, 1, Integer::sum);
        }
        return count;
    }

    // ======================= MEDIUM 8: Spiral Matrix =======================
    /** Layer-by-layer peeling: top row, right col, bottom row, left col. O(m·n) time, O(1) extra space. */
    public static List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix.length == 0) return result;
        int top = 0, bottom = matrix.length - 1, left = 0, right = matrix[0].length - 1;
        while (top <= bottom && left <= right) {
            for (int j = left; j <= right; j++) result.add(matrix[top][j]); top++;
            for (int i = top; i <= bottom; i++) result.add(matrix[i][right]); right--;
            if (top <= bottom) { for (int j = right; j >= left; j--) result.add(matrix[bottom][j]); bottom--; }
            if (left <= right) { for (int i = bottom; i >= top; i--) result.add(matrix[i][left]); left++; }
        }
        return result;
    }

    // ======================= MEDIUM 9: Find All Duplicates in Array =======================
    /** Mark visited indices by negating; if already negative, it's a duplicate. O(n) time, O(1) space. */
    public static List<Integer> findDuplicates(int[] nums) {
        List<Integer> result = new ArrayList<>();
        for (int n : nums) {
            int idx = Math.abs(n) - 1;
            if (nums[idx] < 0) result.add(Math.abs(n));
            else nums[idx] = -nums[idx];
        }
        return result;
    }

    // ======================= MEDIUM 10: Kth Largest Element =======================
    /** Quickselect with random pivot for average O(n) time. Worst case O(n²). */
    public static int findKthLargest(int[] nums, int k) {
        int target = nums.length - k;
        return quickselect(nums, 0, nums.length - 1, target);
    }
    private static int quickselect(int[] nums, int lo, int hi, int k) {
        int pivot = nums[hi], p = lo;
        for (int i = lo; i < hi; i++) { if (nums[i] <= pivot) { int t = nums[p]; nums[p] = nums[i]; nums[i] = t; p++; } }
        int t = nums[p]; nums[p] = nums[hi]; nums[hi] = t;
        if (p == k) return nums[p];
        return p < k ? quickselect(nums, p + 1, hi, k) : quickselect(nums, lo, p - 1, k);
    }

    // ======================= HARD 1: Trapping Rain Water =======================
    /** Two pointers with running left-max and right-max; water at each bar = max - height. O(n) time, O(1) space. */
    public static int trap(int[] height) {
        int l = 0, r = height.length - 1, lMax = 0, rMax = 0, water = 0;
        while (l < r) {
            if (height[l] < height[r]) { lMax = Math.max(lMax, height[l]); water += lMax - height[l]; l++; }
            else { rMax = Math.max(rMax, height[r]); water += rMax - height[r]; r--; }
        }
        return water;
    }

    // ======================= HARD 2: First Missing Positive =======================
    /** Cyclic sort: place each value n at index n-1; first mismatch is the answer. O(n) time, O(1) space. */
    public static int firstMissingPositive(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; i++) while (nums[i] > 0 && nums[i] <= n && nums[nums[i] - 1] != nums[i]) { int t = nums[nums[i] - 1]; nums[nums[i] - 1] = nums[i]; nums[i] = t; }
        for (int i = 0; i < n; i++) if (nums[i] != i + 1) return i + 1;
        return n + 1;
    }

    // ======================= HARD 3: Median of Two Sorted Arrays =======================
    /** Binary search on the shorter array to find the correct partition point. O(log(min(m,n))) time, O(1) space. */
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) return findMedianSortedArrays(nums2, nums1);
        int m = nums1.length, n = nums2.length, lo = 0, hi = m;
        while (lo <= hi) {
            int i = (lo + hi) / 2, j = (m + n + 1) / 2 - i;
            int left1 = i == 0 ? Integer.MIN_VALUE : nums1[i - 1], right1 = i == m ? Integer.MAX_VALUE : nums1[i];
            int left2 = j == 0 ? Integer.MIN_VALUE : nums2[j - 1], right2 = j == n ? Integer.MAX_VALUE : nums2[j];
            if (left1 <= right2 && left2 <= right1) {
                if ((m + n) % 2 == 0) return (Math.max(left1, left2) + Math.min(right1, right2)) / 2.0;
                return Math.max(left1, left2);
            } else if (left1 > right2) hi = i - 1; else lo = i + 1;
        }
        return 0;
    }

    // ======================= HARD 4: Longest Consecutive Sequence =======================
    /** HashSet for O(1) lookup; only start chains where n-1 is absent. O(n) time, O(n) space. */
    public static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>(); for (int n : nums) set.add(n);
        int max = 0;
        for (int n : set) {
            if (!set.contains(n - 1)) { int len = 1; while (set.contains(n + len)) len++; max = Math.max(max, len); }
        }
        return max;
    }

    // ======================= HARD 5: Largest Rectangle in Histogram =======================
    /** Monotonic stack: pop when current bar is shorter; width = distance between stack boundaries. O(n) time. */
    public static int largestRectangleArea(int[] heights) {
        Deque<Integer> stack = new ArrayDeque<>();
        int max = 0, n = heights.length;
        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];
            while (!stack.isEmpty() && h < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                max = Math.max(max, height * width);
            }
            stack.push(i);
        }
        return max;
    }

    // ======================= HARD 6: Sliding Window Maximum =======================
    /** Monotonic deque stores indices in decreasing value order; front is always the window max. O(n) time. */
    public static int[] maxSlidingWindow(int[] nums, int k) {
        Deque<Integer> dq = new ArrayDeque<>();
        int[] result = new int[nums.length - k + 1];
        for (int i = 0; i < nums.length; i++) {
            while (!dq.isEmpty() && dq.peekFirst() < i - k + 1) dq.pollFirst();
            while (!dq.isEmpty() && nums[dq.peekLast()] < nums[i]) dq.pollLast();
            dq.offerLast(i);
            if (i >= k - 1) result[i - k + 1] = nums[dq.peekFirst()];
        }
        return result;
    }

    // ======================= HARD 7: Maximum Product Subarray =======================
    /** Track both max and min products (negatives flip sign). O(n) time, O(1) space. */
    public static int maxProduct(int[] nums) {
        int max = nums[0], curMax = nums[0], curMin = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int a = curMax * nums[i], b = curMin * nums[i];
            curMax = Math.max(nums[i], Math.max(a, b));
            curMin = Math.min(nums[i], Math.min(a, b));
            max = Math.max(max, curMax);
        }
        return max;
    }

    // ======================= HARD 8: Count of Smaller Numbers After Self =======================
    /** Merge sort with index tracking; count right-side elements that move left during merge. O(n log n) time. */
    public static List<Integer> countSmaller(int[] nums) {
        int n = nums.length;
        int[] result = new int[n], indices = new int[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        mergeSort(nums, indices, result, 0, n - 1);
        List<Integer> list = new ArrayList<>();
        for (int r : result) list.add(r);
        return list;
    }
    private static void mergeSort(int[] nums, int[] indices, int[] result, int lo, int hi) {
        if (lo >= hi) return;
        int mid = (lo + hi) / 2;
        mergeSort(nums, indices, result, lo, mid);
        mergeSort(nums, indices, result, mid + 1, hi);
        int[] temp = new int[hi - lo + 1];
        int i = lo, j = mid + 1, k = 0, rightCount = 0;
        while (i <= mid && j <= hi) {
            if (nums[indices[j]] < nums[indices[i]]) { rightCount++; temp[k++] = indices[j++]; }
            else { result[indices[i]] += rightCount; temp[k++] = indices[i++]; }
        }
        while (i <= mid) { result[indices[i]] += rightCount; temp[k++] = indices[i++]; }
        while (j <= hi) temp[k++] = indices[j++];
        System.arraycopy(temp, 0, indices, lo, temp.length);
    }

    // ======================= HARD 9: Shortest Unsorted Continuous Subarray =======================
    /** Two passes: find right boundary from left, left boundary from right. O(n) time, O(1) space. */
    public static int findUnsortedSubarray(int[] nums) {
        int n = nums.length, max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        int left = -1, right = -1;
        for (int i = 0; i < n; i++) {
            if (nums[i] < max) right = i; else max = nums[i];
        }
        for (int i = n - 1; i >= 0; i--) {
            if (nums[i] > min) left = i; else min = nums[i];
        }
        return right == -1 ? 0 : right - left + 1;
    }

    // ======================= HARD 10: Candy Distribution =======================
    /** Two passes: left-to-right for rising, right-to-left for falling; take max at each position. O(n) time. */
    public static int candy(int[] ratings) {
        int n = ratings.length;
        int[] candies = new int[n];
        Arrays.fill(candies, 1);
        for (int i = 1; i < n; i++) if (ratings[i] > ratings[i - 1]) candies[i] = candies[i - 1] + 1;
        for (int i = n - 2; i >= 0; i--) if (ratings[i] > ratings[i + 1]) candies[i] = Math.max(candies[i], candies[i + 1] + 1);
        int sum = 0; for (int c : candies) sum += c;
        return sum;
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== ARRAYS (30 Examples) ===\n");

        System.out.println("--- EASY ---");
        System.out.println("1. Two Sum: " + Arrays.toString(twoSum(new int[]{2, 7, 11, 15}, 9)));
        System.out.println("2. Max Profit: " + maxProfit(new int[]{7, 1, 5, 3, 6, 4}));
        System.out.println("3. Contains Dup: " + containsDuplicate(new int[]{1, 2, 3, 1}));
        int[] m4 = {1, 2, 3, 0, 0, 0}; merge(m4, 3, new int[]{2, 5, 6}, 3);
        System.out.println("4. Merge Sorted: " + Arrays.toString(m4));
        System.out.println("5. Max Subarray: " + maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));
        System.out.println("6. Remove Dups: " + removeDuplicates(new int[]{0, 0, 1, 1, 1, 2, 2, 3, 3, 4}));
        System.out.println("7. Single Number: " + singleNumber(new int[]{4, 1, 2, 1, 2}));
        int[] mz = {0, 1, 0, 3, 12}; moveZeroes(mz);
        System.out.println("8. Move Zeroes: " + Arrays.toString(mz));
        System.out.println("9. Plus One: " + Arrays.toString(plusOne(new int[]{9, 9, 9})));
        System.out.println("10. Missing Number: " + missingNumber(new int[]{3, 0, 1}));

        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Three Sum: " + threeSum(new int[]{-1, 0, 1, 2, -1, -4}));
        System.out.println("12. Max Area: " + maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}));
        System.out.println("13. Product Except Self: " + Arrays.toString(productExceptSelf(new int[]{1, 2, 3, 4})));
        int[] r14 = {1, 2, 3, 4, 5, 6, 7}; rotate(r14, 3);
        System.out.println("14. Rotate: " + Arrays.toString(r14));
        int[] c15 = {2, 0, 2, 1, 1, 0}; sortColors(c15);
        System.out.println("15. Sort Colors: " + Arrays.toString(c15));
        int[] np = {1, 2, 3}; nextPermutation(np);
        System.out.println("16. Next Perm: " + Arrays.toString(np));
        System.out.println("17. Subarray Sum K: " + subarraySum(new int[]{1, 1, 1}, 2));
        System.out.println("18. Spiral Matrix: " + spiralOrder(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}));
        System.out.println("19. Find Dups: " + findDuplicates(new int[]{4, 3, 2, 7, 8, 2, 3, 1}));
        System.out.println("20. Kth Largest: " + findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2));

        System.out.println("\n--- HARD ---");
        System.out.println("21. Trap Water: " + trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}));
        System.out.println("22. First Missing +: " + firstMissingPositive(new int[]{3, 4, -1, 1}));
        System.out.println("23. Median 2 Arrays: " + findMedianSortedArrays(new int[]{1, 3}, new int[]{2}));
        System.out.println("24. Longest Consec: " + longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}));
        System.out.println("25. Largest Rect: " + largestRectangleArea(new int[]{2, 1, 5, 6, 2, 3}));
        System.out.println("26. Sliding Max: " + Arrays.toString(maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)));
        System.out.println("27. Max Product: " + maxProduct(new int[]{2, 3, -2, 4}));
        System.out.println("28. Count Smaller: " + countSmaller(new int[]{5, 2, 6, 1}));
        System.out.println("29. Unsorted Sub: " + findUnsortedSubarray(new int[]{2, 6, 4, 8, 10, 9, 15}));
        System.out.println("30. Candy: " + candy(new int[]{1, 0, 2}));
    }
}

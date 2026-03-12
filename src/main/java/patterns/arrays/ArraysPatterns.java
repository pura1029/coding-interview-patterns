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
    /**
     * Two Sum — find two indices whose values add up to target.
     *
     * <p><b>Approach:</b> Hash map stores each seen value mapped to its index.
     * For every element, check if the complement (target - nums[i]) exists in the map.
     *
     * <p><b>Example:</b> nums=[2,7,11,15], target=9 → [0,1] because 2+7=9.
     *
     * @param nums   array of integers
     * @param target the desired sum
     * @return indices of the two numbers that add up to target
     *
     * <p><b>Time:</b> O(n) — single pass with O(1) hash lookups.
     * <br><b>Space:</b> O(n) — hash map stores at most n entries.
     */
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
    /**
     * Best Time to Buy and Sell Stock — maximize profit from a single buy and sell.
     *
     * <p><b>Approach:</b> Maintain a running minimum price seen so far.
     * At each price, the potential profit is price − minSoFar; track the global max.
     *
     * <p><b>Example:</b> prices=[7,1,5,3,6,4] → 5 (buy at 1, sell at 6).
     *
     * @param prices array of stock prices on each day
     * @return maximum achievable profit (0 if no profit is possible)
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — two tracking variables.
     */
    public static int maxProfit(int[] prices) {
        int min = Integer.MAX_VALUE, maxProfit = 0;
        for (int p : prices) { min = Math.min(min, p); maxProfit = Math.max(maxProfit, p - min); }
        return maxProfit;
    }

    // ======================= EASY 3: Contains Duplicate =======================
    /**
     * Contains Duplicate — determine if any value appears at least twice.
     *
     * <p><b>Approach:</b> Insert elements into a HashSet; {@code add()} returns false
     * if the element already exists, immediately indicating a duplicate.
     *
     * <p><b>Example:</b> [1,2,3,1] → true (1 appears twice).
     *
     * @param nums array of integers
     * @return true if any element is repeated
     *
     * <p><b>Time:</b> O(n) — single pass with O(1) set operations.
     * <br><b>Space:</b> O(n) — set stores up to n elements.
     */
    public static boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int n : nums) if (!seen.add(n)) return true;
        return false;
    }

    // ======================= EASY 4: Merge Sorted Array =======================
    /**
     * Merge Sorted Array — merge nums2 into nums1 (which has trailing space) in-place.
     *
     * <p><b>Approach:</b> Three pointers start from the end: compare the largest
     * remaining elements from each array and place at the back of nums1.
     *
     * <p><b>Example:</b> nums1=[1,2,3,0,0,0], m=3, nums2=[2,5,6], n=3 → [1,2,2,3,5,6].
     *
     * @param nums1 first sorted array (length m+n, with trailing zeros)
     * @param m     number of valid elements in nums1
     * @param nums2 second sorted array
     * @param n     number of elements in nums2
     *
     * <p><b>Time:</b> O(m+n) — each element is placed exactly once.
     * <br><b>Space:</b> O(1) — in-place merge.
     */
    public static void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1, j = n - 1, k = m + n - 1;
        while (j >= 0) {
            if (i >= 0 && nums1[i] > nums2[j]) nums1[k--] = nums1[i--];
            else nums1[k--] = nums2[j--];
        }
    }

    // ======================= EASY 5: Maximum Subarray (Kadane's) =======================
    /**
     * Maximum Subarray — find the contiguous subarray with the largest sum.
     *
     * <p><b>Approach (Kadane's Algorithm):</b> At each index, decide whether to
     * extend the current subarray (cur + nums[i]) or start a new one (nums[i]).
     * Track the global maximum across all decisions.
     *
     * <p><b>Example:</b> [-2,1,-3,4,-1,2,1,-5,4] → 6 (subarray [4,-1,2,1]).
     *
     * @param nums array of integers (at least one element)
     * @return the maximum subarray sum
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — two tracking variables.
     */
    public static int maxSubArray(int[] nums) {
        int max = nums[0], cur = nums[0];
        for (int i = 1; i < nums.length; i++) { cur = Math.max(nums[i], cur + nums[i]); max = Math.max(max, cur); }
        return max;
    }

    // ======================= EASY 6: Remove Duplicates from Sorted Array =======================
    /**
     * Remove Duplicates from Sorted Array — remove duplicates in-place, return new length.
     *
     * <p><b>Approach:</b> Two pointers — slow marks the write position, fast scans ahead.
     * When fast finds a new unique value, write it at slow+1.
     *
     * <p><b>Example:</b> [0,0,1,1,1,2,2,3,3,4] → returns 5, array becomes [0,1,2,3,4,...].
     *
     * @param nums sorted array of integers
     * @return the count of unique elements
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — in-place modification.
     */
    public static int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;
        int slow = 0;
        for (int fast = 1; fast < nums.length; fast++) if (nums[fast] != nums[slow]) nums[++slow] = nums[fast];
        return slow + 1;
    }

    // ======================= EASY 7: Single Number =======================
    /**
     * Single Number — every element appears twice except one; find it.
     *
     * <p><b>Approach:</b> XOR all elements. Since a ⊕ a = 0 and a ⊕ 0 = a,
     * all pairs cancel out, leaving only the unique element.
     *
     * <p><b>Example:</b> [4,1,2,1,2] → 4.
     *
     * @param nums array where every element appears twice except one
     * @return the single element that has no duplicate
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — one variable.
     */
    public static int singleNumber(int[] nums) {
        int result = 0;
        for (int n : nums) result ^= n;
        return result;
    }

    // ======================= EASY 8: Move Zeroes =======================
    /**
     * Move Zeroes — move all 0s to the end while preserving order of non-zero elements.
     *
     * <p><b>Approach:</b> Two pointers — write pointer tracks the next position for
     * a non-zero element. Swap non-zeros forward as they are found.
     *
     * <p><b>Example:</b> [0,1,0,3,12] → [1,3,12,0,0].
     *
     * @param nums array of integers (modified in-place)
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — in-place swaps.
     */
    public static void moveZeroes(int[] nums) {
        int write = 0;
        for (int read = 0; read < nums.length; read++) {
            if (nums[read] != 0) { int t = nums[write]; nums[write] = nums[read]; nums[read] = t; write++; }
        }
    }

    // ======================= EASY 9: Plus One =======================
    /**
     * Plus One — increment a large integer represented as an array of digits.
     *
     * <p><b>Approach:</b> Traverse from the last digit. If digit &lt; 9, increment
     * and return immediately (no carry). Otherwise set to 0 and continue.
     * If all digits overflow, prepend a 1.
     *
     * <p><b>Example:</b> [9,9,9] → [1,0,0,0].
     *
     * @param digits array representing a non-negative integer
     * @return the digits array after adding one
     *
     * <p><b>Time:</b> O(n) — worst case traverses all digits.
     * <br><b>Space:</b> O(1) — unless overflow creates a new array of size n+1.
     */
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
    /**
     * Missing Number — find the missing number in [0..n] from an array of n elements.
     *
     * <p><b>Approach:</b> Gauss formula: expected sum = n*(n+1)/2.
     * Subtract every element to find the missing one.
     *
     * <p><b>Example:</b> [3,0,1] → 2.
     *
     * @param nums array containing n distinct numbers from 0 to n
     * @return the missing number
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — arithmetic only.
     */
    public static int missingNumber(int[] nums) {
        int n = nums.length, sum = n * (n + 1) / 2;
        for (int num : nums) sum -= num;
        return sum;
    }

    // ======================= MEDIUM 1: Three Sum =======================
    /**
     * Three Sum — find all unique triplets that sum to zero.
     *
     * <p><b>Approach:</b> Sort the array. Fix one element (i), then use two pointers
     * (left, right) to find pairs that complement it. Skip duplicates at all levels.
     *
     * <p><b>Example:</b> [-1,0,1,2,-1,-4] → [[-1,-1,2],[-1,0,1]].
     *
     * @param nums array of integers
     * @return list of all unique triplets summing to zero
     *
     * <p><b>Time:</b> O(n²) — sorting O(n log n) + nested two-pointer O(n²).
     * <br><b>Space:</b> O(1) — ignoring the output list.
     */
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
    /**
     * Container With Most Water — find two lines forming a container that holds the most water.
     *
     * <p><b>Approach:</b> Two pointers at both ends. Area = min(height[l], height[r]) × (r−l).
     * Move the pointer with the shorter height inward to potentially find a taller line.
     *
     * <p><b>Example:</b> [1,8,6,2,5,4,8,3,7] → 49.
     *
     * @param height array of non-negative integers representing line heights
     * @return maximum area of water the container can hold
     *
     * <p><b>Time:</b> O(n) — single pass with two pointers.
     * <br><b>Space:</b> O(1) — constant extra variables.
     */
    public static int maxArea(int[] height) {
        int l = 0, r = height.length - 1, max = 0;
        while (l < r) { max = Math.max(max, Math.min(height[l], height[r]) * (r - l)); if (height[l] < height[r]) l++; else r--; }
        return max;
    }

    // ======================= MEDIUM 3: Product of Array Except Self =======================
    /**
     * Product of Array Except Self — return an array where each element is the product
     * of all other elements, without using division.
     *
     * <p><b>Approach:</b> Two passes: left-to-right accumulates prefix products,
     * right-to-left accumulates suffix products into the same result array.
     *
     * <p><b>Example:</b> [1,2,3,4] → [24,12,8,6].
     *
     * @param nums array of integers (length ≥ 2)
     * @return product array where res[i] = product of all elements except nums[i]
     *
     * <p><b>Time:</b> O(n) — two linear passes.
     * <br><b>Space:</b> O(1) extra — result array is not counted as extra space.
     */
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
    /**
     * Rotate Array — rotate the array to the right by k steps.
     *
     * <p><b>Approach (Triple Reverse):</b> Reverse the entire array, then reverse the
     * first k elements, then reverse the remaining. This achieves in-place rotation.
     *
     * <p><b>Example:</b> [1,2,3,4,5,6,7], k=3 → [5,6,7,1,2,3,4].
     *
     * @param nums array of integers (modified in-place)
     * @param k    number of positions to rotate right
     *
     * <p><b>Time:</b> O(n) — each element is reversed at most twice.
     * <br><b>Space:</b> O(1) — in-place.
     */
    public static void rotate(int[] nums, int k) {
        k %= nums.length;
        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }
    private static void reverse(int[] nums, int l, int r) { while (l < r) { int t = nums[l]; nums[l++] = nums[r]; nums[r--] = t; } }

    // ======================= MEDIUM 5: Sort Colors (Dutch National Flag) =======================
    /**
     * Sort Colors (Dutch National Flag) — sort an array of 0s, 1s, and 2s in-place.
     *
     * <p><b>Approach:</b> Three-way partition with lo, mid, hi pointers.
     * 0 → swap to lo region, 1 → keep in mid, 2 → swap to hi region. Single pass.
     *
     * <p><b>Example:</b> [2,0,2,1,1,0] → [0,0,1,1,2,2].
     *
     * @param nums array containing only 0, 1, and 2
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — in-place swaps.
     */
    public static void sortColors(int[] nums) {
        int lo = 0, mid = 0, hi = nums.length - 1;
        while (mid <= hi) {
            if (nums[mid] == 0) { int t = nums[lo]; nums[lo++] = nums[mid]; nums[mid++] = t; }
            else if (nums[mid] == 1) mid++;
            else { int t = nums[mid]; nums[mid] = nums[hi]; nums[hi--] = t; }
        }
    }

    // ======================= MEDIUM 6: Next Permutation =======================
    /**
     * Next Permutation — rearrange numbers into the lexicographically next greater permutation.
     *
     * <p><b>Approach:</b>
     * <ol>
     *   <li>Find the rightmost ascending pair (nums[i] &lt; nums[i+1]).</li>
     *   <li>Swap nums[i] with the smallest element greater than it in the suffix.</li>
     *   <li>Reverse the suffix after position i.</li>
     * </ol>
     *
     * <p><b>Example:</b> [1,2,3] → [1,3,2].
     *
     * @param nums array of integers (modified in-place)
     *
     * <p><b>Time:</b> O(n) — at most three linear scans.
     * <br><b>Space:</b> O(1) — in-place.
     */
    public static void nextPermutation(int[] nums) {
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) i--;
        if (i >= 0) { int j = nums.length - 1; while (nums[j] <= nums[i]) j--; int t = nums[i]; nums[i] = nums[j]; nums[j] = t; }
        reverse(nums, i + 1, nums.length - 1);
    }

    // ======================= MEDIUM 7: Subarray Sum Equals K =======================
    /**
     * Subarray Sum Equals K — count the number of contiguous subarrays that sum to k.
     *
     * <p><b>Approach:</b> Maintain a running prefix sum. Use a hash map to count
     * how many times each prefix sum has occurred. For each index, the number of
     * valid subarrays ending here is map[prefixSum − k].
     *
     * <p><b>Example:</b> [1,1,1], k=2 → 2 (subarrays [1,1] at indices 0-1 and 1-2).
     *
     * @param nums array of integers (may contain negatives)
     * @param k    target sum
     * @return count of subarrays with sum equal to k
     *
     * <p><b>Time:</b> O(n) — single pass with O(1) hash operations.
     * <br><b>Space:</b> O(n) — hash map of prefix sums.
     */
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
    /**
     * Spiral Matrix — return all elements of a matrix in spiral (clockwise) order.
     *
     * <p><b>Approach:</b> Layer-by-layer peeling with four boundaries (top, bottom,
     * left, right). Traverse top row → right column → bottom row → left column,
     * then shrink boundaries inward.
     *
     * <p><b>Example:</b> [[1,2,3],[4,5,6],[7,8,9]] → [1,2,3,6,9,8,7,4,5].
     *
     * @param matrix 2D integer matrix (m × n)
     * @return elements in spiral order
     *
     * <p><b>Time:</b> O(m·n) — visits each element once.
     * <br><b>Space:</b> O(1) extra — excluding the output list.
     */
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
    /**
     * Find All Duplicates in Array — elements are in range [1, n] and each appears
     * once or twice; find all that appear twice.
     *
     * <p><b>Approach:</b> Use the array itself as a hash map by negating the value
     * at index (|num| − 1). If the value at that index is already negative,
     * the number has been seen before (duplicate).
     *
     * <p><b>Example:</b> [4,3,2,7,8,2,3,1] → [2,3].
     *
     * @param nums array of integers in range [1, n]
     * @return list of duplicates
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — uses input array as hash map.
     */
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
    /**
     * Kth Largest Element — find the kth largest element in an unsorted array.
     *
     * <p><b>Approach (Quickselect):</b> Partition the array around a pivot.
     * If the pivot lands at position (n−k), it's the answer. Otherwise recurse
     * into the partition containing the target index.
     *
     * <p><b>Example:</b> [3,2,1,5,6,4], k=2 → 5.
     *
     * @param nums array of integers
     * @param k    the rank (1-based) of the desired element
     * @return the kth largest element
     *
     * <p><b>Time:</b> O(n) average, O(n²) worst case.
     * <br><b>Space:</b> O(1) — in-place partitioning (ignoring recursion stack).
     */
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
    /**
     * Trapping Rain Water — compute how much water can be trapped between bars.
     *
     * <p><b>Approach:</b> Two pointers from both ends with running left-max and right-max.
     * Water at each bar = min(leftMax, rightMax) − height. Always process the shorter side.
     *
     * <p><b>Example:</b> [0,1,0,2,1,0,1,3,2,1,2,1] → 6.
     *
     * @param height array of non-negative integers representing bar heights
     * @return total units of water trapped
     *
     * <p><b>Time:</b> O(n) — single pass with two pointers.
     * <br><b>Space:</b> O(1) — constant extra variables.
     */
    public static int trap(int[] height) {
        int l = 0, r = height.length - 1, lMax = 0, rMax = 0, water = 0;
        while (l < r) {
            if (height[l] < height[r]) { lMax = Math.max(lMax, height[l]); water += lMax - height[l]; l++; }
            else { rMax = Math.max(rMax, height[r]); water += rMax - height[r]; r--; }
        }
        return water;
    }

    // ======================= HARD 2: First Missing Positive =======================
    /**
     * First Missing Positive — find the smallest missing positive integer.
     *
     * <p><b>Approach (Cyclic Sort):</b> Place each value n at index n−1.
     * After sorting, the first index where nums[i] ≠ i+1 is the answer.
     *
     * <p><b>Example:</b> [3,4,-1,1] → 2.
     *
     * @param nums array of integers (may contain negatives and duplicates)
     * @return the smallest positive integer not present
     *
     * <p><b>Time:</b> O(n) — each element is swapped at most once.
     * <br><b>Space:</b> O(1) — in-place.
     */
    public static int firstMissingPositive(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; i++) while (nums[i] > 0 && nums[i] <= n && nums[nums[i] - 1] != nums[i]) { int t = nums[nums[i] - 1]; nums[nums[i] - 1] = nums[i]; nums[i] = t; }
        for (int i = 0; i < n; i++) if (nums[i] != i + 1) return i + 1;
        return n + 1;
    }

    // ======================= HARD 3: Median of Two Sorted Arrays =======================
    /**
     * Median of Two Sorted Arrays — find the median of two sorted arrays in logarithmic time.
     *
     * <p><b>Approach:</b> Binary search on the shorter array to find the correct partition
     * point. The partition divides both arrays such that all left elements ≤ all right elements.
     *
     * <p><b>Example:</b> nums1=[1,3], nums2=[2] → 2.0.
     *
     * @param nums1 first sorted array
     * @param nums2 second sorted array
     * @return the median of the merged array
     *
     * <p><b>Time:</b> O(log(min(m, n))) — binary search on shorter array.
     * <br><b>Space:</b> O(1) — constant variables.
     */
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
    /**
     * Longest Consecutive Sequence — find the length of the longest consecutive element sequence.
     *
     * <p><b>Approach:</b> Insert all elements into a HashSet. For each element,
     * only start counting a chain if (n−1) is not in the set (ensuring we start
     * at the beginning of a sequence). Extend while (n+len) exists.
     *
     * <p><b>Example:</b> [100,4,200,1,3,2] → 4 (sequence [1,2,3,4]).
     *
     * @param nums unsorted array of integers
     * @return length of the longest consecutive sequence
     *
     * <p><b>Time:</b> O(n) — each element is visited at most twice.
     * <br><b>Space:</b> O(n) — HashSet stores all elements.
     */
    public static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>(); for (int n : nums) set.add(n);
        int max = 0;
        for (int n : set) {
            if (!set.contains(n - 1)) { int len = 1; while (set.contains(n + len)) len++; max = Math.max(max, len); }
        }
        return max;
    }

    // ======================= HARD 5: Largest Rectangle in Histogram =======================
    /**
     * Largest Rectangle in Histogram — find the area of the largest rectangle
     * that can be formed within a histogram.
     *
     * <p><b>Approach:</b> Monotonic increasing stack of indices. When a shorter bar
     * is encountered, pop and compute the rectangle width using the distance between
     * stack boundaries. Append a sentinel height=0 to flush the stack.
     *
     * <p><b>Example:</b> [2,1,5,6,2,3] → 10 (bars 5 and 6, width=2).
     *
     * @param heights array of non-negative integers representing histogram bar heights
     * @return area of the largest rectangle
     *
     * <p><b>Time:</b> O(n) — each bar is pushed and popped at most once.
     * <br><b>Space:</b> O(n) — stack stores indices.
     */
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
    /**
     * Sliding Window Maximum — find the maximum in each window of size k.
     *
     * <p><b>Approach:</b> Monotonic decreasing deque stores indices. The front is
     * always the window maximum. Remove front if out-of-window, remove back if
     * smaller than current element.
     *
     * <p><b>Example:</b> [1,3,-1,-3,5,3,6,7], k=3 → [3,3,5,5,6,7].
     *
     * @param nums array of integers
     * @param k    sliding window size
     * @return array of maximums for each window position
     *
     * <p><b>Time:</b> O(n) — each element is enqueued and dequeued at most once.
     * <br><b>Space:</b> O(k) — deque stores at most k indices.
     */
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
    /**
     * Maximum Product Subarray — find the contiguous subarray with the largest product.
     *
     * <p><b>Approach:</b> Track both the running maximum and minimum products,
     * because a negative number can flip the minimum into the maximum.
     * At each step, consider extending with curMax*num, curMin*num, or starting fresh with num.
     *
     * <p><b>Example:</b> [2,3,-2,4] → 6 (subarray [2,3]).
     *
     * @param nums array of integers (at least one element)
     * @return the maximum product of any contiguous subarray
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — three tracking variables.
     */
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
    /**
     * Count of Smaller Numbers After Self — for each element, count how many
     * elements to its right are smaller.
     *
     * <p><b>Approach:</b> Modified merge sort with index tracking. During the merge step,
     * when a right-side element is placed before a left-side element, increment
     * the count for that left-side element.
     *
     * <p><b>Example:</b> [5,2,6,1] → [2,1,1,0].
     *
     * @param nums array of integers
     * @return list of counts for each position
     *
     * <p><b>Time:</b> O(n log n) — merge sort.
     * <br><b>Space:</b> O(n) — auxiliary arrays for merge sort.
     */
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
    /**
     * Shortest Unsorted Continuous Subarray — find the shortest subarray such that
     * sorting only that subarray makes the entire array sorted.
     *
     * <p><b>Approach:</b> Two passes: scan left-to-right tracking the running max —
     * the last position where an element is smaller than max is the right boundary.
     * Scan right-to-left tracking the running min — the last position where an element
     * is larger than min is the left boundary.
     *
     * <p><b>Example:</b> [2,6,4,8,10,9,15] → 5 (subarray [6,4,8,10,9]).
     *
     * @param nums array of integers
     * @return length of the shortest subarray to sort
     *
     * <p><b>Time:</b> O(n) — two linear passes.
     * <br><b>Space:</b> O(1) — constant variables.
     */
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
    /**
     * Candy Distribution — give each child at least one candy; children with a higher
     * rating than their neighbor must get more candies. Find the minimum total candies.
     *
     * <p><b>Approach:</b> Two passes: left-to-right ensures each child with a higher
     * rating than its left neighbor gets more candy. Right-to-left ensures the same
     * for the right neighbor, taking the max at each position.
     *
     * <p><b>Example:</b> [1,0,2] → 5 (candies: [2,1,2]).
     *
     * @param ratings array of children's ratings
     * @return minimum total number of candies needed
     *
     * <p><b>Time:</b> O(n) — two linear passes.
     * <br><b>Space:</b> O(n) — auxiliary candies array.
     */
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

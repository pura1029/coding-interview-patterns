package patterns.prefixsum;

import java.util.*;

/**
 * PATTERN 1: PREFIX SUM
 *
 * Pre-computes cumulative sums so any subarray sum is O(1) after O(n) build.
 * prefix[i] = nums[0] + ... + nums[i-1]; sum(l..r) = prefix[r+1] - prefix[l].
 *
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class PrefixSumPatterns {

    // ======================= EASY 1 =======================
    /**
     * Range Sum Query – Immutable
     *
     * <p><b>Approach:</b> Range Sum Query – Immutable
     */
    static class RangeSumQuery {
        private final int[] prefix;
        public RangeSumQuery(int[] nums) {
            prefix = new int[nums.length + 1];
            for (int i = 0; i < nums.length; i++) prefix[i + 1] = prefix[i] + nums[i];
        }
        public int sumRange(int left, int right) {
            return prefix[right + 1] - prefix[left];
        }
    }

    // ======================= EASY 2 =======================
    /**
     * Running Sum of 1D Array
     *
     * <p><b>Approach:</b> Running Sum of 1D Array. result[i] = sum(nums[0]. nums[i]).
     *
     * @param nums the nums parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static int[] runningSum(int[] nums) {
        int[] result = new int[nums.length];
        result[0] = nums[0];
        for (int i = 1; i < nums.length; i++) result[i] = result[i - 1] + nums[i];
        return result;
    }

    // ======================= EASY 3 =======================
    /**
     * Find Pivot Index
     *
     * <p><b>Approach:</b> Find Pivot Index. leftSum == totalSum - leftSum - nums[i].
     *
     * @param nums the nums parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int pivotIndex(int[] nums) {
        int total = 0;
        for (int n : nums) total += n;
        int leftSum = 0;
        for (int i = 0; i < nums.length; i++) {
            if (leftSum == total - leftSum - nums[i]) return i;
            leftSum += nums[i];
        }
        return -1;
    }

    // ======================= EASY 4 =======================
    /**
     * Sum of All Odd Length Subarrays
     *
     * <p><b>Approach:</b> Sum of All Odd Length Subarrays. Count how many odd-length subarrays include each element.
     *
     * @param arr the arr parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     */
    public static int sumOddLengthSubarrays(int[] arr) {
        int n = arr.length, result = 0;
        for (int i = 0; i < n; i++) {
            int timesInOdd = ((i + 1) * (n - i) + 1) / 2;
            result += timesInOdd * arr[i];
        }
        return result;
    }

    // ======================= EASY 5 =======================
    /**
     * Number of Good Pairs
     *
     * <p><b>Approach:</b> Number of Good Pairs. For each value, pairs = count so far. Frequency counting.
     *
     * @param nums the nums parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int numGoodPairs(int[] nums) {
        int[] count = new int[101];
        int result = 0;
        for (int n : nums) {
            result += count[n];
            count[n]++;
        }
        return result;
    }

    // ======================= EASY 6 =======================
    /**
     * Left and Right Sum Differences
     *
     * <p><b>Approach:</b> Left and Right Sum Differences. Single pass: rightSum = total - leftSum - nums[i].
     *
     * @param nums the nums parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     */
    public static int[] leftRightDifference(int[] nums) {
        int n = nums.length;
        int total = 0;
        for (int x : nums) total += x;
        int[] answer = new int[n];
        int leftSum = 0;
        for (int i = 0; i < n; i++) {
            int rightSum = total - leftSum - nums[i];
            answer[i] = Math.abs(leftSum - rightSum);
            leftSum += nums[i];
        }
        return answer;
    }

    // ======================= EASY 7 =======================
    /**
     * Count Positive Prefix Sums
     *
     * <p><b>Approach:</b> Count Positive Prefix Sums. Count indices where running sum > 0.
     *
     * @param nums the nums parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int countPositivePrefixSums(int[] nums) {
        int sum = 0, count = 0;
        for (int n : nums) {
            sum += n;
            if (sum > 0) count++;
        }
        return count;
    }

    // ======================= EASY 8 =======================
    /**
     * Max After Range Increments
     *
     * <p><b>Approach:</b> Max After Range Increments. Difference array for range updates, then prefix sum.
     *
     * @param n the n parameter
     * @param queries the queries parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n + q) time.
     */
    public static int maxAfterRangeIncrements(int n, int[][] queries) {
        int[] diff = new int[n + 1];
        for (int[] q : queries) {
            diff[q[0]] += 1;
            if (q[1] + 1 < n) diff[q[1] + 1] -= 1;
        }
        int max = 0, sum = 0;
        for (int i = 0; i < n; i++) {
            sum += diff[i];
            max = Math.max(max, sum);
        }
        return max;
    }

    // ======================= EASY 9 =======================
    /**
     * Min Start Value for Positive Step Sum
     *
     * <p><b>Approach:</b> Min Start Value for Positive Step Sum. startValue = 1 - min(prefixSum).
     *
     * @param nums the nums parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int minStartValue(int[] nums) {
        int minPrefix = 0, sum = 0;
        for (int n : nums) {
            sum += n;
            minPrefix = Math.min(minPrefix, sum);
        }
        return 1 - minPrefix;
    }

    // ======================= EASY 10 =======================
    /**
     * Check If All 1's Are K Places Apart
     *
     * <p><b>Approach:</b> Check If All 1's Are K Places Apart. Track last 1's index; verify gap >= k.
     *
     * @param nums the nums parameter
     * @param k the k parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static boolean kLengthApart(int[] nums, int k) {
        int lastOne = -k - 1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 1) {
                if (i - lastOne - 1 < k) return false;
                lastOne = i;
            }
        }
        return true;
    }

    // ======================= MEDIUM 1 =======================
    /**
     * Subarray Sum Equals K
     *
     * <p><b>Approach:</b> Subarray Sum Equals K. Prefix sum + hashmap: count occurrences of (sum - k).
     *
     * @param nums the nums parameter
     * @param k the k parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static int subarraySumEqualsK(int[] nums, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1);
        int sum = 0, count = 0;
        for (int num : nums) {
            sum += num;
            count += prefixCount.getOrDefault(sum - k, 0);
            prefixCount.merge(sum, 1, Integer::sum);
        }
        return count;
    }

    // ======================= MEDIUM 2 =======================
    /**
     * Contiguous Array
     *
     * <p><b>Approach:</b> Contiguous Array. Replace 0→-1; find longest subarray with sum=0 via first-occurrence map.
     *
     * @param nums the nums parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     */
    public static int findMaxLength(int[] nums) {
        Map<Integer, Integer> firstOccurrence = new HashMap<>();
        firstOccurrence.put(0, -1);
        int sum = 0, maxLen = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += (nums[i] == 0) ? -1 : 1;
            if (firstOccurrence.containsKey(sum)) {
                maxLen = Math.max(maxLen, i - firstOccurrence.get(sum));
            } else {
                firstOccurrence.put(sum, i);
            }
        }
        return maxLen;
    }

    // ======================= MEDIUM 3 =======================
    /**
     * Product of Array Except Self
     *
     * <p><b>Approach:</b> Product of Array Except Self. Left prefix product pass + right suffix product pass.
     *
     * @param nums the nums parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     */
    public static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        result[0] = 1;
        for (int i = 1; i < n; i++) result[i] = result[i - 1] * nums[i - 1];
        int right = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= right;
            right *= nums[i];
        }
        return result;
    }

    // ======================= MEDIUM 4 =======================
    /**
     * Subarrays Divisible by K
     *
     * <p><b>Approach:</b> Subarrays Divisible by K. Same prefix-sum trick with remainder counting.
     *
     * @param nums the nums parameter
     * @param k the k parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(k) space.
     */
    public static int subarraysDivByK(int[] nums, int k) {
        Map<Integer, Integer> remainderCount = new HashMap<>();
        remainderCount.put(0, 1);
        int sum = 0, count = 0;
        for (int num : nums) {
            sum += num;
            int rem = ((sum % k) + k) % k;
            count += remainderCount.getOrDefault(rem, 0);
            remainderCount.merge(rem, 1, Integer::sum);
        }
        return count;
    }

    // ======================= MEDIUM 5 =======================
    /**
     * Subarrays with Bounded Max
     *
     * <p><b>Approach:</b> Subarrays with Bounded Max. countAtMost(right) - countAtMost(left-1).
     *
     * @param nums the nums parameter
     * @param left the left parameter
     * @param right the right parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int numSubarrayBoundedMax(int[] nums, int left, int right) {
        return countAtMost(nums, right) - countAtMost(nums, left - 1);
    }

    private static int countAtMost(int[] nums, int bound) {
        int count = 0, windowLen = 0;
        for (int num : nums) {
            windowLen = (num <= bound) ? windowLen + 1 : 0;
            count += windowLen;
        }
        return count;
    }

    // ======================= MEDIUM 6 =======================
    /**
     * Binary Subarrays With Sum
     *
     * <p><b>Approach:</b> Binary Subarrays With Sum. Prefix sum hashmap counts subarrays summing to goal.
     *
     * @param nums the nums parameter
     * @param goal the goal parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     */
    public static int numSubarraysWithSum(int[] nums, int goal) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1);
        int sum = 0, count = 0;
        for (int num : nums) {
            sum += num;
            count += prefixCount.getOrDefault(sum - goal, 0);
            prefixCount.merge(sum, 1, Integer::sum);
        }
        return count;
    }

    // ======================= MEDIUM 7 =======================
    /**
     * Max Size Subarray Sum Equals K
     *
     * <p><b>Approach:</b> Max Size Subarray Sum Equals K. Track first occurrence of each prefix sum.
     *
     * @param nums the nums parameter
     * @param k the k parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static int maxSubArrayLen(int[] nums, int k) {
        Map<Integer, Integer> firstIndex = new HashMap<>();
        firstIndex.put(0, -1);
        int sum = 0, maxLen = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            if (firstIndex.containsKey(sum - k)) {
                maxLen = Math.max(maxLen, i - firstIndex.get(sum - k));
            }
            firstIndex.putIfAbsent(sum, i);
        }
        return maxLen;
    }

    // ======================= MEDIUM 8 =======================
    /**
     * Range Addition
     *
     * <p><b>Approach:</b> Range Addition
     *
     * @param length the length parameter
     * @param updates the updates parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n + q) time.
     */
    public static int[] rangeAddition(int length, int[][] updates) {
        int[] diff = new int[length];
        for (int[] u : updates) {
            diff[u[0]] += u[2];
            if (u[1] + 1 < length) diff[u[1] + 1] -= u[2];
        }
        for (int i = 1; i < length; i++) diff[i] += diff[i - 1];
        return diff;
    }

    // ======================= MEDIUM 9 =======================
    /**
     * Continuous Subarray Sum
     *
     * <p><b>Approach:</b> Continuous Subarray Sum. Prefix sum remainder map; same remainder at distance >= 2 means multiple of k.
     *
     * @param nums the nums parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static boolean checkSubarraySum(int[] nums, int k) {
        Map<Integer, Integer> remainderIndex = new HashMap<>();
        remainderIndex.put(0, -1);
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            int rem = (k == 0) ? sum : ((sum % k) + k) % k;
            if (remainderIndex.containsKey(rem)) {
                if (i - remainderIndex.get(rem) >= 2) return true;
            } else {
                remainderIndex.put(rem, i);
            }
        }
        return false;
    }

    // ======================= MEDIUM 10 =======================
    /**
     * Nice Subarrays (exactly k odds)
     *
     * <p><b>Approach:</b> Nice Subarrays (exactly k odds). atMost(k) - atMost(k-1) sliding window trick.
     *
     * @param nums the nums parameter
     * @param k the k parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     */
    public static int numberOfSubarrays(int[] nums, int k) {
        return atMostKOdd(nums, k) - atMostKOdd(nums, k - 1);
    }

    private static int atMostKOdd(int[] nums, int k) {
        int left = 0, count = 0, oddCount = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] % 2 == 1) oddCount++;
            while (oddCount > k) {
                if (nums[left] % 2 == 1) oddCount--;
                left++;
            }
            count += right - left + 1;
        }
        return count;
    }

    // ======================= HARD 1 =======================
    /**
     * Max Subarray Sum After Removing One Element
     *
     * <p><b>Approach:</b> Max Subarray Sum After Removing One Element. Forward and backward max subarrays; bridge at each i.
     *
     * @param nums the nums parameter
     * @return the computed result
     */
    public static int maxSumAfterRemoval(int[] nums) {
        int n = nums.length;
        int[] maxEndingHere = new int[n];
        int[] maxStartingHere = new int[n];
        maxEndingHere[0] = nums[0];
        for (int i = 1; i < n; i++)
            maxEndingHere[i] = Math.max(nums[i], maxEndingHere[i - 1] + nums[i]);
        maxStartingHere[n - 1] = nums[n - 1];
        for (int i = n - 2; i >= 0; i--)
            maxStartingHere[i] = Math.max(nums[i], maxStartingHere[i + 1] + nums[i]);
        int result = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) result = Math.max(result, maxEndingHere[i]);
        for (int i = 1; i < n - 1; i++)
            result = Math.max(result, maxEndingHere[i - 1] + maxStartingHere[i + 1]);
        return result;
    }

    // ======================= HARD 2 =======================
    /**
     * 2D Range Sum Query
     *
     * <p><b>Approach:</b> 2D Range Sum Query
     */
    static class RangeSum2D {
        private final int[][] prefix;
        public RangeSum2D(int[][] matrix) {
            int m = matrix.length, n = matrix[0].length;
            prefix = new int[m + 1][n + 1];
            for (int i = 1; i <= m; i++)
                for (int j = 1; j <= n; j++)
                    prefix[i][j] = matrix[i - 1][j - 1] + prefix[i - 1][j] + prefix[i][j - 1] - prefix[i - 1][j - 1];
        }
        public int sumRegion(int r1, int c1, int r2, int c2) {
            return prefix[r2 + 1][c2 + 1] - prefix[r1][c2 + 1] - prefix[r2 + 1][c1] + prefix[r1][c1];
        }
    }

    // ======================= HARD 3 =======================
    /**
     * Subarray Sum Equals K (Long)
     *
     * <p><b>Approach:</b> Subarray Sum Equals K (Long). Same prefix-sum hashmap approach but with long to handle large sums.
     *
     * @param nums the nums parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static long subarraySumEqualsKLong(int[] nums, int k) {
        Map<Long, Long> prefixCount = new HashMap<>();
        prefixCount.put(0L, 1L);
        long sum = 0, count = 0;
        for (int num : nums) {
            sum += num;
            count += prefixCount.getOrDefault(sum - k, 0L);
            prefixCount.merge(sum, 1L, Long::sum);
        }
        return count;
    }

    // ======================= HARD 4 =======================
    /**
     * Max Sum of 3 Non-Overlapping Subarrays
     *
     * <p><b>Approach:</b> Max Sum of 3 Non-Overlapping Subarrays. Window sums + left/right best indices.
     *
     * @param nums the nums parameter
     * @param k the k parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static int[] maxSumOfThreeSubarrays(int[] nums, int k) {
        int n = nums.length;
        int[] windowSum = new int[n - k + 1];
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum += nums[i];
            if (i >= k) sum -= nums[i - k];
            if (i >= k - 1) windowSum[i - k + 1] = sum;
        }
        int[] left = new int[windowSum.length];
        int best = 0;
        for (int i = 0; i < windowSum.length; i++) {
            if (windowSum[i] > windowSum[best]) best = i;
            left[i] = best;
        }
        int[] right = new int[windowSum.length];
        best = windowSum.length - 1;
        for (int i = windowSum.length - 1; i >= 0; i--) {
            if (windowSum[i] >= windowSum[best]) best = i;
            right[i] = best;
        }
        int[] result = new int[]{-1, -1, -1};
        for (int mid = k; mid < windowSum.length - k; mid++) {
            int l = left[mid - k], r = right[mid + k];
            if (result[0] == -1 || windowSum[l] + windowSum[mid] + windowSum[r] >
                    windowSum[result[0]] + windowSum[result[1]] + windowSum[result[2]]) {
                result = new int[]{l, mid, r};
            }
        }
        return result;
    }

    // ======================= HARD 5 =======================
    /**
     * Shortest Subarray with Sum >= K
     *
     * <p><b>Approach:</b> Shortest Subarray with Sum >= K. Prefix sum + monotonic deque. Handles negatives.
     *
     * @param nums the nums parameter
     * @param k the k parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     */
    public static int shortestSubarrayWithSumK(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];
        Deque<Integer> deque = new ArrayDeque<>();
        int minLen = n + 1;
        for (int i = 0; i <= n; i++) {
            while (!deque.isEmpty() && prefix[i] - prefix[deque.peekFirst()] >= k) {
                minLen = Math.min(minLen, i - deque.pollFirst());
            }
            while (!deque.isEmpty() && prefix[i] <= prefix[deque.peekLast()]) {
                deque.pollLast();
            }
            deque.offerLast(i);
        }
        return minLen <= n ? minLen : -1;
    }

    // ======================= HARD 6 =======================
    /**
     * Count of Range Sum
     *
     * <p><b>Approach:</b> Count of Range Sum. Merge sort on prefix sums to count pairs in [lower, upper].
     *
     * @param nums the nums parameter
     * @param lower the lower parameter
     * @param upper the upper parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n log n) time.
     */
    public static int countRangeSum(int[] nums, int lower, int upper) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];
        return mergeSortCount(prefix, 0, n + 1, lower, upper);
    }

    private static int mergeSortCount(long[] arr, int lo, int hi, int lower, int upper) {
        if (hi - lo <= 1) return 0;
        int mid = lo + (hi - lo) / 2;
        int count = mergeSortCount(arr, lo, mid, lower, upper) + mergeSortCount(arr, mid, hi, lower, upper);
        int j1 = mid, j2 = mid;
        for (int i = lo; i < mid; i++) {
            while (j1 < hi && arr[j1] - arr[i] < lower) j1++;
            while (j2 < hi && arr[j2] - arr[i] <= upper) j2++;
            count += j2 - j1;
        }
        long[] sorted = new long[hi - lo];
        int p1 = lo, p2 = mid, idx = 0;
        while (p1 < mid && p2 < hi) sorted[idx++] = arr[p1] <= arr[p2] ? arr[p1++] : arr[p2++];
        while (p1 < mid) sorted[idx++] = arr[p1++];
        while (p2 < hi) sorted[idx++] = arr[p2++];
        System.arraycopy(sorted, 0, arr, lo, hi - lo);
        return count;
    }

    // ======================= HARD 7 =======================
    /**
     * Max Non-Overlapping Subarrays with Sum = Target
     *
     * <p><b>Approach:</b> Max Non-Overlapping Subarrays with Sum = Target. Greedy: reset prefix set after each match.
     *
     * @param nums the nums parameter
     * @param target the target parameter
     * @return the computed result
     */
    public static int maxNonOverlapping(int[] nums, int target) {
        Set<Integer> prefixSet = new HashSet<>();
        prefixSet.add(0);
        int sum = 0, count = 0;
        for (int num : nums) {
            sum += num;
            if (prefixSet.contains(sum - target)) {
                count++;
                prefixSet.clear();
                prefixSet.add(0);
                sum = 0;
            } else {
                prefixSet.add(sum);
            }
        }
        return count;
    }

    // ======================= HARD 8 =======================
    /**
     * Make Sum Divisible by P
     *
     * <p><b>Approach:</b> Make Sum Divisible by P. Remove shortest subarray so remaining sum % p == 0. Prefix remainder map.
     *
     * @param nums the nums parameter
     * @param p the p parameter
     * @return the computed result
     */
    public static int minSubarray(int[] nums, int p) {
        long totalSum = 0;
        for (int n : nums) totalSum += n;
        int target = (int) (totalSum % p);
        if (target == 0) return 0;
        Map<Integer, Integer> lastIndex = new HashMap<>();
        lastIndex.put(0, -1);
        int sum = 0, minLen = nums.length;
        for (int i = 0; i < nums.length; i++) {
            sum = ((sum + nums[i]) % p + p) % p;
            int need = ((sum - target) % p + p) % p;
            if (lastIndex.containsKey(need)) {
                minLen = Math.min(minLen, i - lastIndex.get(need));
            }
            lastIndex.put(sum, i);
        }
        return minLen < nums.length ? minLen : -1;
    }

    // ======================= HARD 9 =======================
    /**
     * Submatrices That Sum to Target
     *
     * <p><b>Approach:</b> Submatrices That Sum to Target. Fix row range, reduce to 1D subarray sum equals k.
     *
     * @param matrix the matrix parameter
     * @param target the target parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(m²·n) time.
     */
    public static int numSubmatrixSumTarget(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length, count = 0;
        for (int r1 = 0; r1 < m; r1++) {
            int[] colSum = new int[n];
            for (int r2 = r1; r2 < m; r2++) {
                Map<Integer, Integer> prefixCount = new HashMap<>();
                prefixCount.put(0, 1);
                int sum = 0;
                for (int c = 0; c < n; c++) {
                    colSum[c] += matrix[r2][c];
                    sum += colSum[c];
                    count += prefixCount.getOrDefault(sum - target, 0);
                    prefixCount.merge(sum, 1, Integer::sum);
                }
            }
        }
        return count;
    }

    // ======================= HARD 10 =======================
    /**
     * Min Operations to Reduce X to Zero
     *
     * <p><b>Approach:</b> Min Operations to Reduce X to Zero. Find longest subarray with sum = total - x.
     *
     * @param nums the nums parameter
     * @param x the x parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static int minOperations(int[] nums, int x) {
        int total = 0;
        for (int n : nums) total += n;
        int target = total - x;
        if (target < 0) return -1;
        if (target == 0) return nums.length;
        Map<Integer, Integer> firstIndex = new HashMap<>();
        firstIndex.put(0, -1);
        int sum = 0, maxLen = -1;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            if (firstIndex.containsKey(sum - target)) {
                maxLen = Math.max(maxLen, i - firstIndex.get(sum - target));
            }
            firstIndex.putIfAbsent(sum, i);
        }
        return maxLen == -1 ? -1 : nums.length - maxLen;
    }

    public static void main(String[] args) {
        System.out.println("=== PREFIX SUM PATTERN (30 Examples) ===\n");

        // EASY
        System.out.println("--- EASY ---");
        // new RangeSumQuery(new int[]{...}) → creates object with prefix sum array in constructor; sumRange returns prefix[right+1] - prefix[left]
        RangeSumQuery rsq = new RangeSumQuery(new int[]{-2, 0, 3, -5, 2, -1});
        // Range Sum Query: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("1. Range Sum Query: sumRange(0,2) = " + rsq.sumRange(0, 2));
        // new int[]{...} → input; creates new int[] result; for-loop: result[i] = result[i-1] + nums[i] — running sum accumulation
        System.out.println("2. Running Sum: " + Arrays.toString(runningSum(new int[]{1, 2, 3, 4})));
        // computes total sum first via for-loop; second for-loop with if (leftSum == total - leftSum - nums[i]) return index — balance check
        System.out.println("3. Pivot Index: " + pivotIndex(new int[]{1, 7, 3, 6, 5, 6}));
        // creates new int[] prefix sum; nested for-loops with odd-length stride; sum = prefix[j+1] - prefix[i] — prefix sum range query
        System.out.println("4. Sum Odd Length Subarrays: " + sumOddLengthSubarrays(new int[]{1, 4, 2, 5, 3}));
        // new int[]{...} → creates array literal; for-loop with if (condition) count/accumulate
        System.out.println("5. Good Pairs: " + numGoodPairs(new int[]{1, 2, 3, 1, 1, 3}));
        // new int[]{...} → creates array literal
        System.out.println("6. Left Right Diff: " + Arrays.toString(leftRightDifference(new int[]{10, 4, 8, 3})));
        // new int[]{...} → creates array literal; for-loop with if (condition) count/accumulate
        System.out.println("7. Positive Prefix Sums: " + countPositivePrefixSums(new int[]{1, -2, 3, -1, 5}));
        // new int[]{...} → creates array literal; tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("8. Max After Range Increments: " + maxAfterRangeIncrements(5, new int[][]{{0, 2}, {1, 4}, {2, 3}}));
        // for-loop accumulates prefix sum; Math.min tracks minimum prefix; return 1 - minPrefix if negative, else 1 — ensures sum >= 1
        System.out.println("9. Min Start Value: " + minStartValue(new int[]{-3, 2, -3, 4, 2}));
        // new int[]{...} → creates array literal
        System.out.println("10. K Length Apart: " + kLengthApart(new int[]{1, 0, 0, 0, 1, 0, 0, 1}, 2));

        // MEDIUM
        System.out.println("\n--- MEDIUM ---");
        // new int[]{...} → creates array literal; for-loop with if (condition) count/accumulate
        System.out.println("11. Subarray Sum=K: " + subarraySumEqualsK(new int[]{1, 1, 1}, 2));
        // new int[]{...} → creates array literal; for-loop or binary search with if-else to locate target; tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("12. Contiguous Array: " + findMaxLength(new int[]{0, 1, 0, 1, 0, 1, 1}));
        // creates new int[] result; two for-loops: left pass multiplies prefix, right pass multiplies suffix — no division needed
        System.out.println("13. Product Except Self: " + Arrays.toString(productExceptSelf(new int[]{1, 2, 3, 4})));
        // new int[]{...} → creates array literal
        System.out.println("14. Subarrays Div by K: " + subarraysDivByK(new int[]{4, 5, 0, -2, -3, 1}, 5));
        // new int[]{...} → creates array literal; for-loop with if (condition) count/accumulate; tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("15. Bounded Max Subarrays: " + numSubarrayBoundedMax(new int[]{2, 1, 4, 3}, 2, 3));
        // creates new HashMap<>(); for-loop with prefix sum; if (map.containsKey(sum-goal)) add count — same as subarraySum pattern
        System.out.println("16. Binary Subarrays Sum: " + numSubarraysWithSum(new int[]{1, 0, 1, 0, 1}, 2));
        // new int[]{...} → creates array literal; tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("17. Max SubArray Len=K: " + maxSubArrayLen(new int[]{1, -1, 5, -2, 3}, 3));
        // new int[]{...} → creates array literal
        System.out.println("18. Range Addition: " + Arrays.toString(rangeAddition(5, new int[][]{{1, 3, 2}, {2, 4, 3}, {0, 2, -2}})));
        // creates new HashMap<Integer,Integer>(); for-loop with if (map.containsKey(sum%k) && i - map.get >= 2) — modular prefix sum
        System.out.println("19. Continuous Subarray Sum: " + checkSubarraySum(new int[]{23, 2, 4, 6, 7}, 6));
        // new int[]{...} → creates array literal; for-loop with if (condition) count/accumulate
        System.out.println("20. Nice Subarrays: " + numberOfSubarrays(new int[]{1, 1, 2, 1, 1}, 3));

        // HARD
        System.out.println("\n--- HARD ---");
        // new int[]{...} → creates array literal; for-loop with if (condition) count/accumulate; tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("21. Max Sum After Removal: " + maxSumAfterRemoval(new int[]{1, -2, 0, 3}));
        // new RangeSum2D() → creates object; new int[]{...} → creates array literal
        RangeSum2D rs2d = new RangeSum2D(new int[][]{{3, 0, 1, 4, 2}, {5, 6, 3, 2, 1}, {1, 2, 0, 1, 5}, {4, 1, 0, 1, 7}, {1, 0, 3, 0, 5}});
        // 2D Range Sum: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("22. 2D Range Sum: sumRegion(2,1,4,3) = " + rs2d.sumRegion(2, 1, 4, 3));
        // new int[]{...} → creates array literal; for-loop with if (condition) count/accumulate
        System.out.println("23. Subarray Sum=K (Long): " + subarraySumEqualsKLong(new int[]{1, 1, 1, 1, 1}, 2));
        // new int[]{...} → creates array literal; for-loop with if (condition) count/accumulate; tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("24. Max Sum 3 Subarrays: " + Arrays.toString(maxSumOfThreeSubarrays(new int[]{1, 2, 1, 2, 6, 7, 5, 1}, 2)));
        // new int[]{...} → creates array literal; for-loop with if (condition) count/accumulate; tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("25. Shortest Subarray Sum>=K: " + shortestSubarrayWithSumK(new int[]{2, -1, 2}, 3));
        // new int[]{...} → creates array literal; for-loop with if (condition) count/accumulate
        System.out.println("26. Count Range Sum: " + countRangeSum(new int[]{-2, 5, -1}, -2, 2));
        // new int[]{...} → creates array literal; tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("27. Max Non-Overlapping: " + maxNonOverlapping(new int[]{-1, 3, 5, 1, 4, 2, -9}, 6));
        // new int[]{...} → creates array literal; tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("28. Min Subarray (div by p): " + minSubarray(new int[]{3, 1, 4, 2}, 6));
        // 2D prefix sum: nested for-loops over row pairs; within each pair: HashMap prefix sum for column sums with if (map.containsKey(sum-target))
        System.out.println("29. Submatrix Sum Target: " + numSubmatrixSumTarget(new int[][]{{0, 1, 0}, {1, 1, 1}, {0, 1, 0}}, 0));
        // new int[]{...} → creates array literal; tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("30. Min Ops Reduce X: " + minOperations(new int[]{1, 1, 4, 2, 3}, 5));
    }
}

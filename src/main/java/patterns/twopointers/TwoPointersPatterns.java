package patterns.twopointers;

import java.util.*;

/**
 * PATTERN 2: TWO POINTERS
 *
 * Uses two references moving through a data structure (usually sorted array/string)
 * to solve problems in O(n) instead of O(n²).
 *
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class TwoPointersPatterns {

    // ======================= EASY 1 =======================
    // Two Sum II – Sorted Array
    public static int[] twoSumSorted(int[] numbers, int target) {
        int l = 0, r = numbers.length - 1;
        while (l < r) {
            int sum = numbers[l] + numbers[r];
            if (sum == target) return new int[]{l + 1, r + 1};
            else if (sum < target) l++;
            else r--;
        }
        return new int[]{-1, -1};
    }

    // ======================= EASY 2 =======================
    // Valid Palindrome
    public static boolean isPalindrome(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) {
            while (l < r && !Character.isLetterOrDigit(s.charAt(l))) l++;
            while (l < r && !Character.isLetterOrDigit(s.charAt(r))) r--;
            if (Character.toLowerCase(s.charAt(l)) != Character.toLowerCase(s.charAt(r))) return false;
            l++; r--;
        }
        return true;
    }

    // ======================= EASY 3 =======================
    // Remove Duplicates from Sorted Array
    public static int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;
        int slow = 0;
        for (int fast = 1; fast < nums.length; fast++) {
            if (nums[fast] != nums[slow]) nums[++slow] = nums[fast];
        }
        return slow + 1;
    }

    // ======================= EASY 4 =======================
    // Move Zeroes to end
    public static void moveZeroes(int[] nums) {
        int slow = 0;
        for (int fast = 0; fast < nums.length; fast++) {
            if (nums[fast] != 0) {
                int tmp = nums[slow]; nums[slow] = nums[fast]; nums[fast] = tmp;
                slow++;
            }
        }
    }

    // ======================= EASY 5 =======================
    // Squares of a Sorted Array
    public static int[] sortedSquares(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        int l = 0, r = n - 1, pos = n - 1;
        while (l <= r) {
            if (Math.abs(nums[l]) > Math.abs(nums[r])) {
                result[pos--] = nums[l] * nums[l]; l++;
            } else {
                result[pos--] = nums[r] * nums[r]; r--;
            }
        }
        return result;
    }

    // ======================= EASY 6 =======================
    // Reverse String in-place
    public static void reverseString(char[] s) {
        int l = 0, r = s.length - 1;
        while (l < r) { char t = s[l]; s[l++] = s[r]; s[r--] = t; }
    }

    // ======================= EASY 7 =======================
    // Is Subsequence
    public static boolean isSubsequence(String s, String t) {
        int i = 0, j = 0;
        while (i < s.length() && j < t.length()) {
            if (s.charAt(i) == t.charAt(j)) i++;
            j++;
        }
        return i == s.length();
    }

    // ======================= EASY 8 =======================
    // Merge Sorted Array in-place
    public static void merge(int[] nums1, int m, int[] nums2, int n) {
        int p = m + n - 1, p1 = m - 1, p2 = n - 1;
        while (p2 >= 0) {
            if (p1 >= 0 && nums1[p1] > nums2[p2]) nums1[p--] = nums1[p1--];
            else nums1[p--] = nums2[p2--];
        }
    }

    // ======================= EASY 9 =======================
    // Intersection of Two Arrays II
    public static int[] intersect(int[] nums1, int[] nums2) {
        Arrays.sort(nums1); Arrays.sort(nums2);
        List<Integer> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < nums1.length && j < nums2.length) {
            if (nums1[i] == nums2[j]) { result.add(nums1[i]); i++; j++; }
            else if (nums1[i] < nums2[j]) i++;
            else j++;
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    // ======================= EASY 10 =======================
    // Valid Palindrome II (can remove at most one char)
    public static boolean validPalindromeII(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) {
            if (s.charAt(l) != s.charAt(r))
                return isPalin(s, l + 1, r) || isPalin(s, l, r - 1);
            l++; r--;
        }
        return true;
    }
    private static boolean isPalin(String s, int l, int r) {
        while (l < r) { if (s.charAt(l++) != s.charAt(r--)) return false; }
        return true;
    }

    // ======================= MEDIUM 1 =======================
    // Three Sum (find all triplets summing to 0)
    public static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            int l = i + 1, r = nums.length - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[l], nums[r]));
                    while (l < r && nums[l] == nums[l + 1]) l++;
                    while (l < r && nums[r] == nums[r - 1]) r--;
                    l++; r--;
                } else if (sum < 0) l++; else r--;
            }
        }
        return result;
    }

    // ======================= MEDIUM 2 =======================
    // Three Sum Closest
    public static int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int closest = nums[0] + nums[1] + nums[2];
        for (int i = 0; i < nums.length - 2; i++) {
            int l = i + 1, r = nums.length - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (Math.abs(sum - target) < Math.abs(closest - target)) closest = sum;
                if (sum < target) l++;
                else if (sum > target) r--;
                else return sum;
            }
        }
        return closest;
    }

    // ======================= MEDIUM 3 =======================
    // Container With Most Water
    public static int maxArea(int[] height) {
        int l = 0, r = height.length - 1, max = 0;
        while (l < r) {
            max = Math.max(max, Math.min(height[l], height[r]) * (r - l));
            if (height[l] < height[r]) l++; else r--;
        }
        return max;
    }

    // ======================= MEDIUM 4 =======================
    // Sort Colors (Dutch National Flag)
    public static void sortColors(int[] nums) {
        int lo = 0, mid = 0, hi = nums.length - 1;
        while (mid <= hi) {
            if (nums[mid] == 0) { swap(nums, lo++, mid++); }
            else if (nums[mid] == 1) { mid++; }
            else { swap(nums, mid, hi--); }
        }
    }
    private static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    // ======================= MEDIUM 5 =======================
    // Partition Labels
    public static List<Integer> partitionLabels(String s) {
        int[] lastIndex = new int[26];
        for (int i = 0; i < s.length(); i++) lastIndex[s.charAt(i) - 'a'] = i;
        List<Integer> result = new ArrayList<>();
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, lastIndex[s.charAt(i) - 'a']);
            if (i == end) { result.add(end - start + 1); start = end + 1; }
        }
        return result;
    }

    // ======================= MEDIUM 6 =======================
    // Remove Duplicates from Sorted Array II (allow at most 2)
    public static int removeDuplicatesII(int[] nums) {
        if (nums.length <= 2) return nums.length;
        int slow = 2;
        for (int fast = 2; fast < nums.length; fast++) {
            if (nums[fast] != nums[slow - 2]) nums[slow++] = nums[fast];
        }
        return slow;
    }

    // ======================= MEDIUM 7 =======================
    // Longest Mountain in Array
    public static int longestMountain(int[] arr) {
        int n = arr.length, max = 0;
        for (int i = 1; i < n - 1; ) {
            if (arr[i - 1] < arr[i] && arr[i] > arr[i + 1]) {
                int l = i, r = i;
                while (l > 0 && arr[l - 1] < arr[l]) l--;
                while (r < n - 1 && arr[r] > arr[r + 1]) r++;
                max = Math.max(max, r - l + 1);
                i = r + 1;
            } else i++;
        }
        return max;
    }

    // ======================= MEDIUM 8 =======================
    // Boats to Save People (pair heaviest with lightest)
    public static int numRescueBoats(int[] people, int limit) {
        Arrays.sort(people);
        int l = 0, r = people.length - 1, boats = 0;
        while (l <= r) {
            if (people[l] + people[r] <= limit) l++;
            r--;
            boats++;
        }
        return boats;
    }

    // ======================= MEDIUM 9 =======================
    // Four Sum (find all quadruplets summing to target)
    public static List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length - 3; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            for (int j = i + 1; j < nums.length - 2; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) continue;
                int l = j + 1, r = nums.length - 1;
                while (l < r) {
                    long sum = (long) nums[i] + nums[j] + nums[l] + nums[r];
                    if (sum == target) {
                        result.add(Arrays.asList(nums[i], nums[j], nums[l], nums[r]));
                        while (l < r && nums[l] == nums[l + 1]) l++;
                        while (l < r && nums[r] == nums[r - 1]) r--;
                        l++; r--;
                    } else if (sum < target) l++; else r--;
                }
            }
        }
        return result;
    }

    // ======================= MEDIUM 10 =======================
    // Longest Word Through Deleting (longest subsequence from dict)
    public static String findLongestWord(String s, List<String> dictionary) {
        String result = "";
        for (String word : dictionary) {
            if (isSubsequence(word, s)) {
                if (word.length() > result.length() ||
                    (word.length() == result.length() && word.compareTo(result) < 0)) {
                    result = word;
                }
            }
        }
        return result;
    }

    // ======================= HARD 1 =======================
    // Trapping Rain Water
    public static int trapRainWater(int[] height) {
        int l = 0, r = height.length - 1, lMax = 0, rMax = 0, water = 0;
        while (l < r) {
            if (height[l] < height[r]) { lMax = Math.max(lMax, height[l]); water += lMax - height[l]; l++; }
            else { rMax = Math.max(rMax, height[r]); water += rMax - height[r]; r--; }
        }
        return water;
    }

    // ======================= HARD 2 =======================
    // Minimum Window Sort (shortest subarray to sort to make whole array sorted)
    public static int findUnsortedSubarray(int[] nums) {
        int n = nums.length, l = -1, r = -1;
        int max = nums[0], min = nums[n - 1];
        for (int i = 1; i < n; i++) {
            max = Math.max(max, nums[i]);
            if (nums[i] < max) r = i;
        }
        for (int i = n - 2; i >= 0; i--) {
            min = Math.min(min, nums[i]);
            if (nums[i] > min) l = i;
        }
        return (l == -1) ? 0 : r - l + 1;
    }

    // ======================= HARD 3 =======================
    // Count Pairs with Absolute Difference <= Target (sorted)
    public static int countPairsWithDiff(int[] nums, int target) {
        Arrays.sort(nums);
        int count = 0, l = 0;
        for (int r = 1; r < nums.length; r++) {
            while (nums[r] - nums[l] > target) l++;
            count += r - l;
        }
        return count;
    }

    // ======================= HARD 4 =======================
    // Three Sum Smaller (count triplets with sum < target)
    public static int threeSumSmaller(int[] nums, int target) {
        Arrays.sort(nums);
        int count = 0;
        for (int i = 0; i < nums.length - 2; i++) {
            int l = i + 1, r = nums.length - 1;
            while (l < r) {
                if (nums[i] + nums[l] + nums[r] < target) { count += r - l; l++; }
                else r--;
            }
        }
        return count;
    }

    // ======================= HARD 5 =======================
    // Backspace String Compare (O(1) space, traverse from end)
    public static boolean backspaceCompare(String s, String t) {
        int i = s.length() - 1, j = t.length() - 1;
        int skipS = 0, skipT = 0;
        while (i >= 0 || j >= 0) {
            while (i >= 0) {
                if (s.charAt(i) == '#') { skipS++; i--; }
                else if (skipS > 0) { skipS--; i--; }
                else break;
            }
            while (j >= 0) {
                if (t.charAt(j) == '#') { skipT++; j--; }
                else if (skipT > 0) { skipT--; j--; }
                else break;
            }
            if (i >= 0 && j >= 0 && s.charAt(i) != t.charAt(j)) return false;
            if ((i >= 0) != (j >= 0)) return false;
            i--; j--;
        }
        return true;
    }

    // ======================= HARD 6 =======================
    // Minimum Difference Between Highest and Lowest of K Scores
    public static int minimumDifference(int[] nums, int k) {
        Arrays.sort(nums);
        int minDiff = Integer.MAX_VALUE;
        for (int i = 0; i + k - 1 < nums.length; i++) {
            minDiff = Math.min(minDiff, nums[i + k - 1] - nums[i]);
        }
        return minDiff;
    }

    // ======================= HARD 7 =======================
    // Smallest Range Covering Elements from K Lists
    public static int[] smallestRange(List<List<Integer>> nums) {
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < nums.size(); i++) {
            pq.offer(new int[]{nums.get(i).get(0), i, 0});
            max = Math.max(max, nums.get(i).get(0));
        }
        int[] result = {0, Integer.MAX_VALUE};
        while (pq.size() == nums.size()) {
            int[] curr = pq.poll();
            if (max - curr[0] < result[1] - result[0]) { result[0] = curr[0]; result[1] = max; }
            if (curr[2] + 1 < nums.get(curr[1]).size()) {
                int next = nums.get(curr[1]).get(curr[2] + 1);
                pq.offer(new int[]{next, curr[1], curr[2] + 1});
                max = Math.max(max, next);
            }
        }
        return result;
    }

    // ======================= HARD 8 =======================
    // Number of Subsequences with Max-Min <= Target
    public static int numSubseq(int[] nums, int target) {
        int MOD = 1_000_000_007;
        Arrays.sort(nums);
        int n = nums.length;
        int[] pow2 = new int[n];
        pow2[0] = 1;
        for (int i = 1; i < n; i++) pow2[i] = (pow2[i - 1] * 2) % MOD;
        int l = 0, r = n - 1, count = 0;
        while (l <= r) {
            if (nums[l] + nums[r] <= target) { count = (count + pow2[r - l]) % MOD; l++; }
            else r--;
        }
        return count;
    }

    // ======================= HARD 9 =======================
    // Maximum Erasure Value (longest subarray with unique elements, max sum)
    public static int maximumUniqueSubarray(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        int l = 0, sum = 0, max = 0;
        for (int r = 0; r < nums.length; r++) {
            while (seen.contains(nums[r])) { seen.remove(nums[l]); sum -= nums[l]; l++; }
            seen.add(nums[r]);
            sum += nums[r];
            max = Math.max(max, sum);
        }
        return max;
    }

    // ======================= HARD 10 =======================
    // Count Subarrays with Score Less Than K
    // Score = sum * length. Find count of subarrays with score < k.
    public static long countSubarraysScoreLessThanK(int[] nums, long k) {
        long sum = 0, count = 0;
        int l = 0;
        for (int r = 0; r < nums.length; r++) {
            sum += nums[r];
            while (sum * (r - l + 1) >= k) sum -= nums[l++];
            count += r - l + 1;
        }
        return count;
    }

    public static void main(String[] args) {
        System.out.println("=== TWO POINTERS PATTERN (30 Examples) ===\n");

        System.out.println("--- EASY ---");
        System.out.println("1. Two Sum Sorted: " + Arrays.toString(twoSumSorted(new int[]{2, 7, 11, 15}, 9)));
        System.out.println("2. Valid Palindrome: " + isPalindrome("A man, a plan, a canal: Panama"));
        System.out.println("3. Remove Duplicates: " + removeDuplicates(new int[]{1, 1, 2, 3, 3}));
        int[] z = {0, 1, 0, 3, 12}; moveZeroes(z);
        System.out.println("4. Move Zeroes: " + Arrays.toString(z));
        System.out.println("5. Sorted Squares: " + Arrays.toString(sortedSquares(new int[]{-4, -1, 0, 3, 10})));
        char[] rs = "hello".toCharArray(); reverseString(rs);
        System.out.println("6. Reverse String: " + new String(rs));
        System.out.println("7. Is Subsequence: " + isSubsequence("ace", "abcde"));
        int[] m1 = {1, 2, 3, 0, 0, 0}; merge(m1, 3, new int[]{2, 5, 6}, 3);
        System.out.println("8. Merge Sorted: " + Arrays.toString(m1));
        System.out.println("9. Intersect: " + Arrays.toString(intersect(new int[]{1, 2, 2, 1}, new int[]{2, 2})));
        System.out.println("10. Valid Palindrome II: " + validPalindromeII("abca"));

        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Three Sum: " + threeSum(new int[]{-1, 0, 1, 2, -1, -4}));
        System.out.println("12. Three Sum Closest: " + threeSumClosest(new int[]{-1, 2, 1, -4}, 1));
        System.out.println("13. Container Most Water: " + maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}));
        int[] sc = {2, 0, 2, 1, 1, 0}; sortColors(sc);
        System.out.println("14. Sort Colors: " + Arrays.toString(sc));
        System.out.println("15. Partition Labels: " + partitionLabels("ababcbacadefegdehijhklij"));
        System.out.println("16. Remove Dup II: " + removeDuplicatesII(new int[]{1, 1, 1, 2, 2, 3}));
        System.out.println("17. Longest Mountain: " + longestMountain(new int[]{2, 1, 4, 7, 3, 2, 5}));
        System.out.println("18. Rescue Boats: " + numRescueBoats(new int[]{3, 2, 2, 1}, 3));
        System.out.println("19. Four Sum: " + fourSum(new int[]{1, 0, -1, 0, -2, 2}, 0));
        System.out.println("20. Longest Word: " + findLongestWord("abpcplea", Arrays.asList("ale", "apple", "monkey", "plea")));

        System.out.println("\n--- HARD ---");
        System.out.println("21. Trap Rain Water: " + trapRainWater(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}));
        System.out.println("22. Unsorted Subarray: " + findUnsortedSubarray(new int[]{2, 6, 4, 8, 10, 9, 15}));
        System.out.println("23. Pairs Diff<=Target: " + countPairsWithDiff(new int[]{1, 3, 5, 7}, 3));
        System.out.println("24. Three Sum Smaller: " + threeSumSmaller(new int[]{-2, 0, 1, 3}, 2));
        System.out.println("25. Backspace Compare: " + backspaceCompare("ab#c", "ad#c"));
        System.out.println("26. Min Diff K Scores: " + minimumDifference(new int[]{9, 4, 1, 7}, 2));
        List<List<Integer>> lists = Arrays.asList(Arrays.asList(4,10,15,24,26), Arrays.asList(0,9,12,20), Arrays.asList(5,18,22,30));
        System.out.println("27. Smallest Range: " + Arrays.toString(smallestRange(lists)));
        System.out.println("28. Num Subseq: " + numSubseq(new int[]{3, 5, 6, 7}, 9));
        System.out.println("29. Max Erasure Value: " + maximumUniqueSubarray(new int[]{4, 2, 4, 5, 6}));
        System.out.println("30. Subarrays Score<K: " + countSubarraysScoreLessThanK(new int[]{2, 1, 4, 3, 5}, 10));
    }
}

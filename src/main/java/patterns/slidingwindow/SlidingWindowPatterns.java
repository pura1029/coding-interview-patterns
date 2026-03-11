package patterns.slidingwindow;

import java.util.*;

/**
 * PATTERN 3: SLIDING WINDOW
 *
 * Maintains a window of elements sliding over data for O(n) subarray/substring problems.
 *
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class SlidingWindowPatterns {

    // ======================= EASY 1: Max Sum Subarray of Size K =======================
    public static int maxSumSubarray(int[] nums, int k) {
        int sum = 0, max;
        for (int i = 0; i < k; i++) sum += nums[i];
        max = sum;
        for (int i = k; i < nums.length; i++) { sum += nums[i] - nums[i - k]; max = Math.max(max, sum); }
        return max;
    }

    // ======================= EASY 2: Average of Subarrays of Size K =======================
    public static double[] averagesOfSubarrays(int[] nums, int k) {
        double[] result = new double[nums.length - k + 1];
        double sum = 0;
        for (int i = 0; i < k; i++) sum += nums[i];
        result[0] = sum / k;
        for (int i = k; i < nums.length; i++) { sum += nums[i] - nums[i - k]; result[i - k + 1] = sum / k; }
        return result;
    }

    // ======================= EASY 3: Maximum Number of Vowels in Substring of Size K =======================
    public static int maxVowels(String s, int k) {
        int count = 0;
        for (int i = 0; i < k; i++) if (isVowel(s.charAt(i))) count++;
        int max = count;
        for (int i = k; i < s.length(); i++) {
            if (isVowel(s.charAt(i))) count++;
            if (isVowel(s.charAt(i - k))) count--;
            max = Math.max(max, count);
        }
        return max;
    }
    private static boolean isVowel(char c) { return "aeiou".indexOf(c) >= 0; }

    // ======================= EASY 4: Minimum Recolors to Get K Consecutive Black Blocks =======================
    public static int minimumRecolors(String blocks, int k) {
        int whites = 0;
        for (int i = 0; i < k; i++) if (blocks.charAt(i) == 'W') whites++;
        int min = whites;
        for (int i = k; i < blocks.length(); i++) {
            if (blocks.charAt(i) == 'W') whites++;
            if (blocks.charAt(i - k) == 'W') whites--;
            min = Math.min(min, whites);
        }
        return min;
    }

    // ======================= EASY 5: Contains Duplicate II =======================
    public static boolean containsNearbyDuplicate(int[] nums, int k) {
        Set<Integer> window = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            if (i > k) window.remove(nums[i - k - 1]);
            if (!window.add(nums[i])) return true;
        }
        return false;
    }

    // ======================= EASY 6: Number of Sub-arrays of Size K with Avg >= Threshold =======================
    public static int numOfSubarrays(int[] arr, int k, int threshold) {
        int sum = 0, count = 0;
        for (int i = 0; i < k; i++) sum += arr[i];
        if (sum / k >= threshold) count++;
        for (int i = k; i < arr.length; i++) {
            sum += arr[i] - arr[i - k];
            if (sum / k >= threshold) count++;
        }
        return count;
    }

    // ======================= EASY 7: Diet Plan Performance =======================
    public static int dietPlanPerformance(int[] calories, int k, int lower, int upper) {
        int sum = 0, points = 0;
        for (int i = 0; i < k; i++) sum += calories[i];
        if (sum < lower) points--; else if (sum > upper) points++;
        for (int i = k; i < calories.length; i++) {
            sum += calories[i] - calories[i - k];
            if (sum < lower) points--; else if (sum > upper) points++;
        }
        return points;
    }

    // ======================= EASY 8: Defuse the Bomb =======================
    public static int[] decrypt(int[] code, int k) {
        int n = code.length;
        int[] result = new int[n];
        if (k == 0) return result;
        int start = k > 0 ? 1 : k, end = k > 0 ? k : -1;
        int sum = 0;
        for (int i = start; i <= end; i++) sum += code[((i % n) + n) % n];
        for (int i = 0; i < n; i++) {
            result[i] = sum;
            sum -= code[((i + start) % n + n) % n];
            sum += code[((i + end + 1) % n + n) % n];
        }
        return result;
    }

    // ======================= EASY 9: Find All K-Distant Indices =======================
    public static List<Integer> findKDistantIndices(int[] nums, int key, int k) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            for (int j = Math.max(0, i - k); j <= Math.min(nums.length - 1, i + k); j++) {
                if (nums[j] == key) { result.add(i); break; }
            }
        }
        return result;
    }

    // ======================= EASY 10: Grumpy Bookstore Owner =======================
    public static int maxSatisfied(int[] customers, int[] grumpy, int minutes) {
        int baseSatisfied = 0;
        for (int i = 0; i < customers.length; i++) if (grumpy[i] == 0) baseSatisfied += customers[i];
        int extra = 0;
        for (int i = 0; i < minutes; i++) if (grumpy[i] == 1) extra += customers[i];
        int maxExtra = extra;
        for (int i = minutes; i < customers.length; i++) {
            if (grumpy[i] == 1) extra += customers[i];
            if (grumpy[i - minutes] == 1) extra -= customers[i - minutes];
            maxExtra = Math.max(maxExtra, extra);
        }
        return baseSatisfied + maxExtra;
    }

    // ======================= MEDIUM 1: Longest Substring Without Repeating =======================
    public static int longestWithoutRepeats(String s) {
        Set<Character> seen = new HashSet<>();
        int l = 0, max = 0;
        for (int r = 0; r < s.length(); r++) {
            while (seen.contains(s.charAt(r))) seen.remove(s.charAt(l++));
            seen.add(s.charAt(r));
            max = Math.max(max, r - l + 1);
        }
        return max;
    }

    // ======================= MEDIUM 2: Longest Repeating Character Replacement =======================
    public static int characterReplacement(String s, int k) {
        int[] count = new int[26];
        int l = 0, maxCount = 0, max = 0;
        for (int r = 0; r < s.length(); r++) {
            maxCount = Math.max(maxCount, ++count[s.charAt(r) - 'A']);
            while (r - l + 1 - maxCount > k) count[s.charAt(l++) - 'A']--;
            max = Math.max(max, r - l + 1);
        }
        return max;
    }

    // ======================= MEDIUM 3: Fruit Into Baskets (at most 2 types) =======================
    public static int totalFruit(int[] fruits) {
        Map<Integer, Integer> basket = new HashMap<>();
        int l = 0, max = 0;
        for (int r = 0; r < fruits.length; r++) {
            basket.merge(fruits[r], 1, Integer::sum);
            while (basket.size() > 2) {
                basket.merge(fruits[l], -1, Integer::sum);
                if (basket.get(fruits[l]) == 0) basket.remove(fruits[l]);
                l++;
            }
            max = Math.max(max, r - l + 1);
        }
        return max;
    }

    // ======================= MEDIUM 4: Max Consecutive Ones III (flip at most k) =======================
    public static int longestOnes(int[] nums, int k) {
        int l = 0, zeros = 0, max = 0;
        for (int r = 0; r < nums.length; r++) {
            if (nums[r] == 0) zeros++;
            while (zeros > k) { if (nums[l++] == 0) zeros--; }
            max = Math.max(max, r - l + 1);
        }
        return max;
    }

    // ======================= MEDIUM 5: Permutation in String =======================
    public static boolean checkInclusion(String s1, String s2) {
        if (s1.length() > s2.length()) return false;
        int[] count = new int[26];
        for (char c : s1.toCharArray()) count[c - 'a']++;
        int l = 0, matched = 0;
        for (int r = 0; r < s2.length(); r++) {
            if (--count[s2.charAt(r) - 'a'] >= 0) matched++;
            if (r >= s1.length() && ++count[s2.charAt(l++) - 'a'] > 0) matched--;
            if (matched == s1.length()) return true;
        }
        return false;
    }

    // ======================= MEDIUM 6: Find All Anagrams in a String =======================
    public static List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) return result;
        int[] count = new int[26];
        for (char c : p.toCharArray()) count[c - 'a']++;
        int l = 0, matched = 0;
        for (int r = 0; r < s.length(); r++) {
            if (--count[s.charAt(r) - 'a'] >= 0) matched++;
            if (r >= p.length() && ++count[s.charAt(l++) - 'a'] > 0) matched--;
            if (matched == p.length()) result.add(l);
        }
        return result;
    }

    // ======================= MEDIUM 7: Longest Substring with At Most K Distinct =======================
    public static int lengthOfLongestSubstringKDistinct(String s, int k) {
        Map<Character, Integer> freq = new HashMap<>();
        int l = 0, max = 0;
        for (int r = 0; r < s.length(); r++) {
            freq.merge(s.charAt(r), 1, Integer::sum);
            while (freq.size() > k) {
                freq.merge(s.charAt(l), -1, Integer::sum);
                if (freq.get(s.charAt(l)) == 0) freq.remove(s.charAt(l));
                l++;
            }
            max = Math.max(max, r - l + 1);
        }
        return max;
    }

    // ======================= MEDIUM 8: Minimum Size Subarray Sum =======================
    public static int minSubArrayLen(int target, int[] nums) {
        int l = 0, sum = 0, min = Integer.MAX_VALUE;
        for (int r = 0; r < nums.length; r++) {
            sum += nums[r];
            while (sum >= target) { min = Math.min(min, r - l + 1); sum -= nums[l++]; }
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }

    // ======================= MEDIUM 9: Subarray Product Less Than K =======================
    public static int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1) return 0;
        int l = 0, product = 1, count = 0;
        for (int r = 0; r < nums.length; r++) {
            product *= nums[r];
            while (product >= k) product /= nums[l++];
            count += r - l + 1;
        }
        return count;
    }

    // ======================= MEDIUM 10: K Radius Subarray Averages =======================
    public static int[] getAverages(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        if (2 * k + 1 > n) return result;
        long sum = 0;
        for (int i = 0; i < 2 * k + 1; i++) sum += nums[i];
        result[k] = (int) (sum / (2 * k + 1));
        for (int i = k + 1; i + k < n; i++) {
            sum += nums[i + k] - nums[i - k - 1];
            result[i] = (int) (sum / (2 * k + 1));
        }
        return result;
    }

    // ======================= HARD 1: Minimum Window Substring =======================
    public static String minWindowSubstring(String s, String t) {
        if (s.length() < t.length()) return "";
        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);
        int required = need.size(), formed = 0, l = 0, minLen = Integer.MAX_VALUE, minStart = 0;
        Map<Character, Integer> window = new HashMap<>();
        for (int r = 0; r < s.length(); r++) {
            window.merge(s.charAt(r), 1, Integer::sum);
            if (need.containsKey(s.charAt(r)) && window.get(s.charAt(r)).intValue() == need.get(s.charAt(r)).intValue()) formed++;
            while (formed == required) {
                if (r - l + 1 < minLen) { minLen = r - l + 1; minStart = l; }
                window.merge(s.charAt(l), -1, Integer::sum);
                if (need.containsKey(s.charAt(l)) && window.get(s.charAt(l)) < need.get(s.charAt(l))) formed--;
                l++;
            }
        }
        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }

    // ======================= HARD 2: Sliding Window Maximum =======================
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

    // ======================= HARD 3: Substring with Concatenation of All Words =======================
    public static List<Integer> findSubstring(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (words.length == 0) return result;
        int wLen = words[0].length(), totalLen = wLen * words.length;
        Map<String, Integer> wordCount = new HashMap<>();
        for (String w : words) wordCount.merge(w, 1, Integer::sum);
        for (int i = 0; i < wLen; i++) {
            Map<String, Integer> window = new HashMap<>();
            int l = i, count = 0;
            for (int r = i; r + wLen <= s.length(); r += wLen) {
                String word = s.substring(r, r + wLen);
                window.merge(word, 1, Integer::sum);
                count++;
                while (window.getOrDefault(word, 0) > wordCount.getOrDefault(word, 0)) {
                    String leftWord = s.substring(l, l + wLen);
                    window.merge(leftWord, -1, Integer::sum);
                    count--;
                    l += wLen;
                }
                if (count == words.length) result.add(l);
            }
        }
        return result;
    }

    // ======================= HARD 4: Longest Substring with At Most Two Distinct =======================
    public static int lengthOfLongestSubstringTwoDistinct(String s) {
        return lengthOfLongestSubstringKDistinct(s, 2);
    }

    // ======================= HARD 5: Count Subarrays With Fixed Bounds =======================
    public static long countSubarrays(int[] nums, int minK, int maxK) {
        long count = 0;
        int lastMin = -1, lastMax = -1, lastBad = -1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] < minK || nums[i] > maxK) lastBad = i;
            if (nums[i] == minK) lastMin = i;
            if (nums[i] == maxK) lastMax = i;
            count += Math.max(0, Math.min(lastMin, lastMax) - lastBad);
        }
        return count;
    }

    // ======================= HARD 6: Minimum Window Subsequence =======================
    public static String minWindowSubsequence(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int minLen = Integer.MAX_VALUE, minStart = 0;
        int j = 0;
        for (int i = 0; i < m; i++) {
            if (s1.charAt(i) == s2.charAt(j)) j++;
            if (j == n) {
                int end = i;
                j--;
                while (j >= 0) { if (s1.charAt(i) == s2.charAt(j)) j--; i--; }
                i++;
                if (end - i + 1 < minLen) { minLen = end - i + 1; minStart = i; }
                j = 0;
            }
        }
        return minLen == Integer.MAX_VALUE ? "" : s1.substring(minStart, minStart + minLen);
    }

    // ======================= HARD 7: Max Value of Equation (y_j + y_i + |x_j - x_i|) =======================
    public static int findMaxValueOfEquation(int[][] points, int k) {
        Deque<int[]> dq = new ArrayDeque<>();
        int max = Integer.MIN_VALUE;
        for (int[] p : points) {
            while (!dq.isEmpty() && p[0] - dq.peekFirst()[0] > k) dq.pollFirst();
            if (!dq.isEmpty()) max = Math.max(max, p[1] + p[0] + dq.peekFirst()[1] - dq.peekFirst()[0]);
            while (!dq.isEmpty() && dq.peekLast()[1] - dq.peekLast()[0] <= p[1] - p[0]) dq.pollLast();
            dq.offerLast(p);
        }
        return max;
    }

    // ======================= HARD 8: Subarrays with K Different Integers =======================
    public static int subarraysWithKDistinct(int[] nums, int k) {
        return atMostKDistinct(nums, k) - atMostKDistinct(nums, k - 1);
    }
    private static int atMostKDistinct(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        int l = 0, count = 0;
        for (int r = 0; r < nums.length; r++) {
            freq.merge(nums[r], 1, Integer::sum);
            while (freq.size() > k) {
                freq.merge(nums[l], -1, Integer::sum);
                if (freq.get(nums[l]) == 0) freq.remove(nums[l]);
                l++;
            }
            count += r - l + 1;
        }
        return count;
    }

    // ======================= HARD 9: Minimum Number of K Consecutive Bit Flips =======================
    public static int minKBitFlips(int[] nums, int k) {
        int n = nums.length, flips = 0, flipCount = 0;
        int[] isFlipped = new int[n];
        for (int i = 0; i < n; i++) {
            if (i >= k) flipCount -= isFlipped[i - k];
            if ((nums[i] + flipCount) % 2 == 0) {
                if (i + k > n) return -1;
                isFlipped[i] = 1;
                flipCount++;
                flips++;
            }
        }
        return flips;
    }

    // ======================= HARD 10: Longest Substring with At Least K Repeating =======================
    public static int longestSubstringKRepeating(String s, int k) {
        int max = 0;
        for (int uniqueTarget = 1; uniqueTarget <= 26; uniqueTarget++) {
            int[] count = new int[26];
            int l = 0, unique = 0, atLeastK = 0;
            for (int r = 0; r < s.length(); r++) {
                int idx = s.charAt(r) - 'a';
                if (count[idx]++ == 0) unique++;
                if (count[idx] == k) atLeastK++;
                while (unique > uniqueTarget) {
                    int lidx = s.charAt(l) - 'a';
                    if (count[lidx] == k) atLeastK--;
                    if (--count[lidx] == 0) unique--;
                    l++;
                }
                if (unique == uniqueTarget && unique == atLeastK) max = Math.max(max, r - l + 1);
            }
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println("=== SLIDING WINDOW PATTERN (30 Examples) ===\n");

        System.out.println("--- EASY ---");
        System.out.println("1. Max Sum K: " + maxSumSubarray(new int[]{2, 1, 5, 1, 3, 2}, 3));
        System.out.println("2. Avg Subarrays: " + Arrays.toString(averagesOfSubarrays(new int[]{1, 3, 2, 6, -1, 4, 1, 8, 2}, 5)));
        System.out.println("3. Max Vowels: " + maxVowels("abciiidef", 3));
        System.out.println("4. Min Recolors: " + minimumRecolors("WBBWWBBWBW", 7));
        System.out.println("5. Contains Dup II: " + containsNearbyDuplicate(new int[]{1, 2, 3, 1}, 3));
        System.out.println("6. Subarrays Avg>=Th: " + numOfSubarrays(new int[]{2, 2, 2, 2, 5, 5, 5, 8}, 3, 4));
        System.out.println("7. Diet Plan: " + dietPlanPerformance(new int[]{1, 2, 3, 4, 5}, 1, 3, 3));
        System.out.println("8. Decrypt: " + Arrays.toString(decrypt(new int[]{5, 7, 1, 4}, 3)));
        System.out.println("9. K-Distant: " + findKDistantIndices(new int[]{3, 4, 9, 1, 3, 9, 5}, 9, 1));
        System.out.println("10. Grumpy Owner: " + maxSatisfied(new int[]{1, 0, 1, 2, 1, 1, 7, 5}, new int[]{0, 1, 0, 1, 0, 1, 0, 1}, 3));

        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Longest No Repeat: " + longestWithoutRepeats("abcabcbb"));
        System.out.println("12. Char Replacement: " + characterReplacement("AABABBA", 1));
        System.out.println("13. Total Fruit: " + totalFruit(new int[]{1, 2, 1, 2, 3}));
        System.out.println("14. Longest Ones: " + longestOnes(new int[]{1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0}, 2));
        System.out.println("15. Permutation In String: " + checkInclusion("ab", "eidbaooo"));
        System.out.println("16. Find Anagrams: " + findAnagrams("cbaebabacd", "abc"));
        System.out.println("17. K Distinct: " + lengthOfLongestSubstringKDistinct("eceba", 2));
        System.out.println("18. Min Size Subarray: " + minSubArrayLen(7, new int[]{2, 3, 1, 2, 4, 3}));
        System.out.println("19. Product < K: " + numSubarrayProductLessThanK(new int[]{10, 5, 2, 6}, 100));
        System.out.println("20. K Radius Avg: " + Arrays.toString(getAverages(new int[]{7, 4, 3, 9, 1, 8, 5, 2, 6}, 3)));

        System.out.println("\n--- HARD ---");
        System.out.println("21. Min Window Sub: " + minWindowSubstring("ADOBECODEBANC", "ABC"));
        System.out.println("22. Sliding Max: " + Arrays.toString(maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)));
        System.out.println("23. Concat Words: " + findSubstring("barfoothefoobarman", new String[]{"foo", "bar"}));
        System.out.println("24. Two Distinct: " + lengthOfLongestSubstringTwoDistinct("eceba"));
        System.out.println("25. Fixed Bounds: " + countSubarrays(new int[]{1, 3, 5, 2, 7, 5}, 1, 5));
        System.out.println("26. Min Window Subseq: " + minWindowSubsequence("abcdebdde", "bde"));
        System.out.println("27. Max Equation: " + findMaxValueOfEquation(new int[][]{{1, 3}, {2, 0}, {5, 10}, {6, -10}}, 1));
        System.out.println("28. K Distinct Ints: " + subarraysWithKDistinct(new int[]{1, 2, 1, 2, 3}, 2));
        System.out.println("29. Min K Bit Flips: " + minKBitFlips(new int[]{0, 0, 0, 1, 0, 1, 1, 0}, 3));
        System.out.println("30. Longest K Repeat: " + longestSubstringKRepeating("aaabb", 3));
    }
}

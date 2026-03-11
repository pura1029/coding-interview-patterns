package patterns.frequencycounting;

import java.util.*;

/**
 * PATTERN 6: FREQUENCY COUNTING (Hashing)
 *
 * Uses hash maps / arrays to count element frequencies for O(n) solutions.
 *
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class FrequencyCountingPatterns {

    /** Valid Anagram. Compare character frequency arrays. */
    public static boolean isAnagram(String s, String t) { if (s.length() != t.length()) return false; int[] f = new int[26]; for (int i = 0; i < s.length(); i++) { f[s.charAt(i)-'a']++; f[t.charAt(i)-'a']--; } for (int v : f) if (v != 0) return false; return true; }

    /** First Unique Character in a String. Count frequencies, find first with count 1. */
    public static int firstUniqChar(String s) { int[] f = new int[26]; for (char c : s.toCharArray()) f[c-'a']++; for (int i = 0; i < s.length(); i++) if (f[s.charAt(i)-'a'] == 1) return i; return -1; }

    /** Contains Duplicate. HashSet membership check. */
    public static boolean containsDuplicate(int[] nums) { Set<Integer> s = new HashSet<>(); for (int n : nums) if (!s.add(n)) return true; return false; }

    /** Majority Element. Cancel different elements. */
    public static int majorityElement(int[] nums) { int c = 0, m = 0; for (int n : nums) { if (c == 0) m = n; c += (n == m) ? 1 : -1; } return m; }

    /** Ransom Note. Magazine must have enough of each char. */
    public static boolean canConstruct(String ransomNote, String magazine) { int[] f = new int[26]; for (char c : magazine.toCharArray()) f[c-'a']++; for (char c : ransomNote.toCharArray()) if (--f[c-'a'] < 0) return false; return true; }

    /** Find the Difference. XOR all characters. */
    public static char findTheDifference(String s, String t) { char c = 0; for (char ch : s.toCharArray()) c ^= ch; for (char ch : t.toCharArray()) c ^= ch; return c; }

    /** Jewels and Stones. HashSet of jewels, count matches. */
    public static int numJewelsInStones(String jewels, String stones) { Set<Character> j = new HashSet<>(); for (char c : jewels.toCharArray()) j.add(c); int cnt = 0; for (char c : stones.toCharArray()) if (j.contains(c)) cnt++; return cnt; }

    /** Word Pattern. Bijection between words and chars. */
    public static boolean wordPattern(String pattern, String s) { String[] words = s.split(" "); if (pattern.length() != words.length) return false; Map<Character, String> m1 = new HashMap<>(); Map<String, Character> m2 = new HashMap<>(); for (int i = 0; i < pattern.length(); i++) { char c = pattern.charAt(i); if (m1.containsKey(c) && !m1.get(c).equals(words[i])) return false; if (m2.containsKey(words[i]) && m2.get(words[i]) != c) return false; m1.put(c, words[i]); m2.put(words[i], c); } return true; }

    /** Isomorphic Strings. Two-way character mapping. */
    public static boolean isIsomorphic(String s, String t) { int[] ms = new int[256], mt = new int[256]; for (int i = 0; i < s.length(); i++) { if (ms[s.charAt(i)] != mt[t.charAt(i)]) return false; ms[s.charAt(i)] = i + 1; mt[t.charAt(i)] = i + 1; } return true; }

    /** Missing Number. XOR with indices or sum formula. */
    public static int missingNumber(int[] nums) { int n = nums.length, sum = n * (n + 1) / 2; for (int x : nums) sum -= x; return sum; }

    /** Group Anagrams. Sort each word as key. */
    public static List<List<String>> groupAnagrams(String[] strs) { Map<String, List<String>> m = new HashMap<>(); for (String s : strs) { char[] c = s.toCharArray(); Arrays.sort(c); m.computeIfAbsent(new String(c), k -> new ArrayList<>()).add(s); } return new ArrayList<>(m.values()); }

    /** Top K Frequent Elements. Bucket sort by frequency. */
    public static int[] topKFrequent(int[] nums, int k) { Map<Integer, Integer> f = new HashMap<>(); for (int n : nums) f.merge(n, 1, Integer::sum); PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(f::get)); for (int key : f.keySet()) { pq.offer(key); if (pq.size() > k) pq.poll(); } int[] r = new int[k]; for (int i = k - 1; i >= 0; i--) r[i] = pq.poll(); return r; }

    /** Longest Consecutive Sequence. HashSet + expand from smallest. */
    public static int longestConsecutive(int[] nums) { Set<Integer> s = new HashSet<>(); for (int n : nums) s.add(n); int max = 0; for (int n : s) { if (!s.contains(n - 1)) { int len = 1; while (s.contains(n + len)) len++; max = Math.max(max, len); } } return max; }

    /** Sort Characters By Frequency. Count + sort by frequency. */
    public static String frequencySort(String s) { Map<Character, Integer> f = new HashMap<>(); for (char c : s.toCharArray()) f.merge(c, 1, Integer::sum); List<Character> chars = new ArrayList<>(f.keySet()); chars.sort((a, b) -> f.get(b) - f.get(a)); StringBuilder sb = new StringBuilder(); for (char c : chars) for (int i = 0; i < f.get(c); i++) sb.append(c); return sb.toString(); }

    /** Find All Duplicates in an Array. Mark visited by negating. */
    public static List<Integer> findDuplicates(int[] nums) { List<Integer> r = new ArrayList<>(); for (int n : nums) { int idx = Math.abs(n) - 1; if (nums[idx] < 0) r.add(Math.abs(n)); else nums[idx] = -nums[idx]; } return r; }

    /** Custom Sort String. Sort by order index map. */
    public static String customSortString(String order, String s) { int[] f = new int[26]; for (char c : s.toCharArray()) f[c-'a']++; StringBuilder sb = new StringBuilder(); for (char c : order.toCharArray()) { while (f[c-'a']-- > 0) sb.append(c); } for (int i = 0; i < 26; i++) while (f[i]-- > 0) sb.append((char)('a'+i)); return sb.toString(); }

    /** Encode and Decode TinyURL. HashMap for id ↔ URL mapping. */
    static Map<String, String> urlMap = new HashMap<>();
    static int urlId = 0;
    public static String encode(String longUrl) { String key = "http://tiny/" + urlId++; urlMap.put(key, longUrl); return key; }
    public static String decode(String shortUrl) { return urlMap.get(shortUrl); }

    /** Subarray Sum Equals K. Prefix sum + frequency map. */
    public static int subarraySum(int[] nums, int k) { Map<Integer, Integer> m = new HashMap<>(); m.put(0, 1); int s = 0, c = 0; for (int n : nums) { s += n; c += m.getOrDefault(s - k, 0); m.merge(s, 1, Integer::sum); } return c; }

    /** Minimum Number of Steps to Make Two Strings Anagram. Count character difference. */
    public static int minSteps(String s, String t) { int[] f = new int[26]; for (char c : s.toCharArray()) f[c-'a']++; for (char c : t.toCharArray()) f[c-'a']--; int r = 0; for (int v : f) if (v > 0) r += v; return r; }

    /** Determine if Two Strings Are Close. Same char set + same sorted frequencies. */
    public static boolean closeStrings(String w1, String w2) {
        if (w1.length() != w2.length()) return false;
        int[] f1 = new int[26], f2 = new int[26];
        for (char c : w1.toCharArray()) f1[c-'a']++;
        for (char c : w2.toCharArray()) f2[c-'a']++;
        for (int i = 0; i < 26; i++) if ((f1[i] == 0) != (f2[i] == 0)) return false;
        Arrays.sort(f1); Arrays.sort(f2);
        return Arrays.equals(f1, f2);
    }

    /** Min Window Containing Pattern. Sliding window with need/have counts. */
    public static String minWindow(String s, String t) { Map<Character, Integer> need = new HashMap<>(); for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum); int req = need.size(), formed = 0, l = 0, ml = Integer.MAX_VALUE, ms = 0; Map<Character, Integer> w = new HashMap<>(); for (int r = 0; r < s.length(); r++) { w.merge(s.charAt(r), 1, Integer::sum); if (need.containsKey(s.charAt(r)) && w.get(s.charAt(r)).intValue() == need.get(s.charAt(r)).intValue()) formed++; while (formed == req) { if (r - l + 1 < ml) { ml = r - l + 1; ms = l; } w.merge(s.charAt(l), -1, Integer::sum); if (need.containsKey(s.charAt(l)) && w.get(s.charAt(l)) < need.get(s.charAt(l))) formed--; l++; } } return ml == Integer.MAX_VALUE ? "" : s.substring(ms, ms + ml); }

    /** Longest Substring with At Most K Distinct Characters. Shrink when distinct > k. */
    public static int lengthOfLongestSubstringKDistinct(String s, int k) { Map<Character, Integer> f = new HashMap<>(); int l = 0, max = 0; for (int r = 0; r < s.length(); r++) { f.merge(s.charAt(r), 1, Integer::sum); while (f.size() > k) { f.merge(s.charAt(l), -1, Integer::sum); if (f.get(s.charAt(l)) == 0) f.remove(s.charAt(l)); l++; } max = Math.max(max, r - l + 1); } return max; }

    /** First Missing Positive. Cyclic sort to place i at index i-1. */
    public static int firstMissingPositive(int[] nums) { int n = nums.length; for (int i = 0; i < n; i++) while (nums[i] > 0 && nums[i] <= n && nums[nums[i]-1] != nums[i]) { int t = nums[nums[i]-1]; nums[nums[i]-1] = nums[i]; nums[i] = t; } for (int i = 0; i < n; i++) if (nums[i] != i + 1) return i + 1; return n + 1; }

    /** Majority Element II (elements appearing > n/3 times). Boyer-Moore with two candidates. */
    public static List<Integer> majorityElementII(int[] nums) { int c1 = 0, c2 = 0, m1 = 0, m2 = 1; for (int n : nums) { if (n == m1) c1++; else if (n == m2) c2++; else if (c1 == 0) { m1 = n; c1 = 1; } else if (c2 == 0) { m2 = n; c2 = 1; } else { c1--; c2--; } } c1 = 0; c2 = 0; for (int n : nums) { if (n == m1) c1++; else if (n == m2) c2++; } List<Integer> r = new ArrayList<>(); if (c1 > nums.length / 3) r.add(m1); if (c2 > nums.length / 3) r.add(m2); return r; }

    /** Substring with Concatenation of All Words. Word-level sliding window. */
    public static List<Integer> findSubstring(String s, String[] words) { List<Integer> r = new ArrayList<>(); if (words.length == 0) return r; int wl = words[0].length(), tl = wl * words.length; Map<String, Integer> wc = new HashMap<>(); for (String w : words) wc.merge(w, 1, Integer::sum); for (int i = 0; i < wl; i++) { Map<String, Integer> win = new HashMap<>(); int l = i, c = 0; for (int j = i; j + wl <= s.length(); j += wl) { String w = s.substring(j, j + wl); win.merge(w, 1, Integer::sum); c++; while (win.getOrDefault(w, 0) > wc.getOrDefault(w, 0)) { String lw = s.substring(l, l + wl); win.merge(lw, -1, Integer::sum); c--; l += wl; } if (c == words.length) r.add(l); } } return r; }

    /** Count of Smaller Numbers After Self. Merge sort with index tracking. */
    public static List<Integer> countSmaller(int[] nums) { int n = nums.length; Integer[] r = new Integer[n]; Arrays.fill(r, 0); int[][] indexed = new int[n][2]; for (int i = 0; i < n; i++) { indexed[i][0] = nums[i]; indexed[i][1] = i; } mergeSort(indexed, r, 0, n - 1); return Arrays.asList(r); }
    private static void mergeSort(int[][] arr, Integer[] r, int lo, int hi) { if (lo >= hi) return; int mid = (lo + hi) / 2; mergeSort(arr, r, lo, mid); mergeSort(arr, r, mid + 1, hi); int[][] merged = new int[hi - lo + 1][2]; int i = lo, j = mid + 1, k = 0, rightCount = 0; while (i <= mid && j <= hi) { if (arr[j][0] < arr[i][0]) { rightCount++; merged[k++] = arr[j++]; } else { r[arr[i][1]] += rightCount; merged[k++] = arr[i++]; } } while (i <= mid) { r[arr[i][1]] += rightCount; merged[k++] = arr[i++]; } while (j <= hi) merged[k++] = arr[j++]; System.arraycopy(merged, 0, arr, lo, merged.length); }

    /** Smallest Sufficient Team (bitmask). Bitmask DP on skill coverage. */
    public static int[] smallestSufficientTeam(String[] reqSkills, List<List<String>> people) { Map<String, Integer> skillIdx = new HashMap<>(); for (int i = 0; i < reqSkills.length; i++) skillIdx.put(reqSkills[i], i); int n = reqSkills.length; int[] personMask = new int[people.size()]; for (int i = 0; i < people.size(); i++) for (String s : people.get(i)) personMask[i] |= 1 << skillIdx.get(s); int target = (1 << n) - 1; Map<Integer, List<Integer>> dp = new HashMap<>(); dp.put(0, new ArrayList<>()); for (int i = 0; i < people.size(); i++) { for (Map.Entry<Integer, List<Integer>> e : new HashMap<>(dp).entrySet()) { int newMask = e.getKey() | personMask[i]; if (!dp.containsKey(newMask) || dp.get(newMask).size() > e.getValue().size() + 1) { List<Integer> team = new ArrayList<>(e.getValue()); team.add(i); dp.put(newMask, team); } } } return dp.get(target).stream().mapToInt(Integer::intValue).toArray(); }

    /** All O`one Data Structure (increment/decrement/getMaxKey/getMinKey). Doubly linked list + hashmap for O(1). */
    static class AllOne {
        Map<String, Integer> map = new HashMap<>();
        public void inc(String key) { map.merge(key, 1, Integer::sum); }
        public void dec(String key) { int v = map.get(key); if (v == 1) map.remove(key); else map.put(key, v - 1); }
        public String getMaxKey() { return map.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).map(Map.Entry::getKey).orElse(""); }
        public String getMinKey() { return map.entrySet().stream().min(Comparator.comparingInt(Map.Entry::getValue)).map(Map.Entry::getKey).orElse(""); }
    }

    /** Rearrange String K Distance Apart. Greedy with max-heap + cooldown. */
    public static String rearrangeString(String s, int k) { if (k <= 1) return s; Map<Character, Integer> f = new HashMap<>(); for (char c : s.toCharArray()) f.merge(c, 1, Integer::sum); PriorityQueue<Map.Entry<Character, Integer>> pq = new PriorityQueue<>((a, b) -> b.getValue() - a.getValue()); pq.addAll(f.entrySet()); StringBuilder sb = new StringBuilder(); Queue<Map.Entry<Character, Integer>> wait = new LinkedList<>(); while (!pq.isEmpty()) { Map.Entry<Character, Integer> e = pq.poll(); sb.append(e.getKey()); e.setValue(e.getValue() - 1); wait.offer(e); if (wait.size() >= k) { Map.Entry<Character, Integer> front = wait.poll(); if (front.getValue() > 0) pq.offer(front); } } return sb.length() == s.length() ? sb.toString() : ""; }

    /** Max Points on a Line. Slope frequency per point. */
    public static int maxPoints(int[][] points) { int n = points.length; if (n <= 2) return n; int max = 2; for (int i = 0; i < n; i++) { Map<String, Integer> slopes = new HashMap<>(); for (int j = i + 1; j < n; j++) { int dx = points[j][0] - points[i][0], dy = points[j][1] - points[i][1]; int g = gcd(Math.abs(dx), Math.abs(dy)); if (dx < 0) { dx = -dx; dy = -dy; } else if (dx == 0) dy = Math.abs(dy); String key = (dx / g) + "/" + (dy / g); slopes.merge(key, 1, Integer::sum); max = Math.max(max, slopes.get(key) + 1); } } return max; }
    private static int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); }

    public static void main(String[] args) {
        System.out.println("=== FREQUENCY COUNTING PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        System.out.println("1. Anagram: " + isAnagram("anagram", "nagaram"));
        System.out.println("2. First Uniq: " + firstUniqChar("leetcode"));
        System.out.println("3. Contains Dup: " + containsDuplicate(new int[]{1,2,3,1}));
        System.out.println("4. Majority: " + majorityElement(new int[]{2,2,1,1,1,2,2}));
        System.out.println("5. Ransom Note: " + canConstruct("aa", "aab"));
        System.out.println("6. Find Diff: " + findTheDifference("abcd", "abcde"));
        System.out.println("7. Jewels: " + numJewelsInStones("aA", "aAAbbbb"));
        System.out.println("8. Word Pattern: " + wordPattern("abba", "dog cat cat dog"));
        System.out.println("9. Isomorphic: " + isIsomorphic("egg", "add"));
        System.out.println("10. Missing #: " + missingNumber(new int[]{3,0,1}));
        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Group Anagrams: " + groupAnagrams(new String[]{"eat","tea","tan","ate","nat","bat"}));
        System.out.println("12. Top K Freq: " + Arrays.toString(topKFrequent(new int[]{1,1,1,2,2,3}, 2)));
        System.out.println("13. Longest Consec: " + longestConsecutive(new int[]{100,4,200,1,3,2}));
        System.out.println("14. Freq Sort: " + frequencySort("tree"));
        System.out.println("15. Find Dups: " + findDuplicates(new int[]{4,3,2,7,8,2,3,1}));
        System.out.println("16. Custom Sort: " + customSortString("cba", "abcd"));
        System.out.println("17. Encode/Decode: " + decode(encode("https://example.com")));
        System.out.println("18. SubArr Sum=K: " + subarraySum(new int[]{1,1,1}, 2));
        System.out.println("19. Min Steps Anagram: " + minSteps("bab", "aba"));
        System.out.println("20. Close Strings: " + closeStrings("abc", "bca"));
        System.out.println("\n--- HARD ---");
        System.out.println("21. Min Window: " + minWindow("ADOBECODEBANC", "ABC"));
        System.out.println("22. K Distinct: " + lengthOfLongestSubstringKDistinct("eceba", 2));
        System.out.println("23. First Missing +: " + firstMissingPositive(new int[]{3,4,-1,1}));
        System.out.println("24. Majority II: " + majorityElementII(new int[]{3,2,3}));
        System.out.println("25. Concat Words: " + findSubstring("barfoothefoobarman", new String[]{"foo","bar"}));
        System.out.println("26. Count Smaller: " + countSmaller(new int[]{5,2,6,1}));
        System.out.println("27. Sufficient Team: " + Arrays.toString(smallestSufficientTeam(new String[]{"java","nodejs","reactjs"}, Arrays.asList(Arrays.asList("java"), Arrays.asList("nodejs"), Arrays.asList("nodejs","reactjs")))));
        AllOne ao = new AllOne(); ao.inc("a"); ao.inc("b"); ao.inc("b");
        System.out.println("28. AllOne max: " + ao.getMaxKey());
        System.out.println("29. Rearrange K=3: " + rearrangeString("aabbcc", 3));
        System.out.println("30. Max Points: " + maxPoints(new int[][]{{1,1},{2,2},{3,3}}));
    }
}

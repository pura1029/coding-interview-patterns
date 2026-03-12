package patterns.strings;

import java.util.*;

/**
 * STRINGS — 30 Essential Problems
 * Strings are immutable character sequences. Key techniques include hashing,
 * sliding window, two-pointer, character frequency arrays, and dynamic programming.
 * String problems test parsing, pattern matching, and transformation skills.
 *
 * 10 Easy | 10 Medium | 10 Hard
 */
public class StringsPatterns {

    // ======================= EASY 1: Valid Anagram =======================
    /**
     * Valid Anagram — determine if two strings are anagrams of each other.
     *
     * <p><b>Approach:</b> Use a frequency array of size 26. Increment for chars in s,
     * decrement for chars in t. If all counts are zero, they are anagrams.
     *
     * <p><b>Example:</b> s="anagram", t="nagaram" → true.
     *
     * @param s first string
     * @param t second string
     * @return true if s and t are anagrams
     *
     * <p><b>Time:</b> O(n) — single pass through both strings.
     * <br><b>Space:</b> O(1) — fixed-size frequency array (26 lowercase letters).
     */
    public static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] count = new int[26];
        for (int i = 0; i < s.length(); i++) { count[s.charAt(i) - 'a']++; count[t.charAt(i) - 'a']--; }
        for (int c : count) if (c != 0) return false;
        return true;
    }

    // ======================= EASY 2: Reverse String =======================
    /**
     * Reverse String — reverse a character array in-place.
     *
     * <p><b>Approach:</b> Two pointers from both ends swap characters and move inward.
     *
     * <p><b>Example:</b> ['h','e','l','l','o'] → ['o','l','l','e','h'].
     *
     * @param s character array to reverse (modified in-place)
     *
     * <p><b>Time:</b> O(n) — n/2 swaps.
     * <br><b>Space:</b> O(1) — in-place.
     */
    public static void reverseString(char[] s) {
        int l = 0, r = s.length - 1;
        while (l < r) { char t = s[l]; s[l++] = s[r]; s[r--] = t; }
    }

    // ======================= EASY 3: Valid Palindrome =======================
    /**
     * Valid Palindrome — determine if a string is a palindrome considering only
     * alphanumeric characters and ignoring case.
     *
     * <p><b>Approach:</b> Two pointers skip non-alphanumeric characters and compare
     * lowercased characters from both ends.
     *
     * <p><b>Example:</b> "A man, a plan, a canal: Panama" → true.
     *
     * @param s input string
     * @return true if s is a valid palindrome
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — no extra data structures.
     */
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

    // ======================= EASY 4: Longest Common Prefix =======================
    /**
     * Longest Common Prefix — find the longest common prefix among an array of strings.
     *
     * <p><b>Approach:</b> Start with the first string as the prefix. Shrink it character
     * by character from the end until every other string starts with it.
     *
     * <p><b>Example:</b> ["flower","flow","flight"] → "fl".
     *
     * @param strs array of strings
     * @return the longest common prefix (empty string if none)
     *
     * <p><b>Time:</b> O(S) where S = sum of all character comparisons.
     * <br><b>Space:</b> O(1) — uses substring of existing string.
     */
    public static String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) return "";
        String prefix = strs[0];
        for (int i = 1; i < strs.length; i++) {
            while (strs[i].indexOf(prefix) != 0) { prefix = prefix.substring(0, prefix.length() - 1); if (prefix.isEmpty()) return ""; }
        }
        return prefix;
    }

    // ======================= EASY 5: First Unique Character =======================
    /**
     * First Unique Character in a String — find the first non-repeating character.
     *
     * <p><b>Approach:</b> Count character frequencies in a first pass. In a second pass,
     * return the first index with a count of 1.
     *
     * <p><b>Example:</b> "leetcode" → 0 (character 'l').
     *
     * @param s input string (lowercase English letters)
     * @return index of first unique character, or -1 if none exists
     *
     * <p><b>Time:</b> O(n) — two passes.
     * <br><b>Space:</b> O(1) — fixed-size frequency array.
     */
    public static int firstUniqChar(String s) {
        int[] count = new int[26];
        for (char c : s.toCharArray()) count[c - 'a']++;
        for (int i = 0; i < s.length(); i++) if (count[s.charAt(i) - 'a'] == 1) return i;
        return -1;
    }

    // ======================= EASY 6: Ransom Note =======================
    /**
     * Ransom Note — determine if a ransom note can be constructed from magazine letters.
     *
     * <p><b>Approach:</b> Count all magazine characters. For each ransom note character,
     * decrement the count. If any count goes negative, the note cannot be formed.
     *
     * <p><b>Example:</b> ransomNote="aa", magazine="aab" → true.
     *
     * @param ransomNote the note to construct
     * @param magazine   the source of available characters
     * @return true if ransomNote can be constructed from magazine
     *
     * <p><b>Time:</b> O(m + n) where m = magazine length, n = note length.
     * <br><b>Space:</b> O(1) — fixed-size frequency array.
     */
    public static boolean canConstruct(String ransomNote, String magazine) {
        int[] count = new int[26];
        for (char c : magazine.toCharArray()) count[c - 'a']++;
        for (char c : ransomNote.toCharArray()) if (--count[c - 'a'] < 0) return false;
        return true;
    }

    // ======================= EASY 7: Is Subsequence =======================
    /**
     * Is Subsequence — determine if s is a subsequence of t.
     *
     * <p><b>Approach:</b> Two pointers — advance the s-pointer only when the current
     * character matches. If the s-pointer reaches the end, s is a subsequence.
     *
     * <p><b>Example:</b> s="ace", t="abcde" → true.
     *
     * @param s the potential subsequence
     * @param t the source string
     * @return true if s is a subsequence of t
     *
     * <p><b>Time:</b> O(n) where n = t.length().
     * <br><b>Space:</b> O(1) — two pointer variables.
     */
    public static boolean isSubsequence(String s, String t) {
        int i = 0;
        for (int j = 0; j < t.length() && i < s.length(); j++) {
            if (s.charAt(i) == t.charAt(j)) i++;
        }
        return i == s.length();
    }

    // ======================= EASY 8: Valid Parentheses (string) =======================
    /**
     * Valid Parentheses — determine if a string of brackets is properly nested and matched.
     *
     * <p><b>Approach:</b> Push the expected closing bracket for each opening bracket.
     * On a closing bracket, check if it matches the top of the stack.
     *
     * <p><b>Example:</b> "()[]{}" → true; "(]" → false.
     *
     * @param s string containing characters '(', ')', '{', '}', '[', ']'
     * @return true if the brackets are valid
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(n) — stack may hold up to n/2 elements.
     */
    public static boolean isValidParentheses(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '(') stack.push(')');
            else if (c == '{') stack.push('}');
            else if (c == '[') stack.push(']');
            else if (stack.isEmpty() || stack.pop() != c) return false;
        }
        return stack.isEmpty();
    }

    // ======================= EASY 9: Implement strStr (indexOf) =======================
    /**
     * Implement strStr — find the first occurrence of needle in haystack.
     *
     * <p><b>Approach:</b> Slide a window of needle's length across haystack and
     * compare substrings at each position.
     *
     * <p><b>Example:</b> haystack="hello", needle="ll" → 2.
     *
     * @param haystack the string to search in
     * @param needle   the pattern to find
     * @return index of first occurrence, or -1 if not found
     *
     * <p><b>Time:</b> O(m·n) where m = haystack length, n = needle length.
     * <br><b>Space:</b> O(1) — no extra structures (ignoring substring creation).
     */
    public static int strStr(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        for (int i = 0; i <= haystack.length() - needle.length(); i++) {
            if (haystack.substring(i, i + needle.length()).equals(needle)) return i;
        }
        return -1;
    }

    // ======================= EASY 10: Count and Say =======================
    /**
     * Count and Say — generate the nth term of the look-and-say sequence.
     *
     * <p><b>Approach:</b> Iteratively build each term by run-length encoding the
     * previous term. Group consecutive identical digits and encode as
     * (count)(digit).
     *
     * <p><b>Example:</b> n=4 → "1211" (1→"11"→"21"→"1211").
     *
     * @param n the term number (1-indexed)
     * @return the nth term of the count-and-say sequence
     *
     * <p><b>Time:</b> O(2^n) — each term can roughly double in length.
     * <br><b>Space:</b> O(2^n) — stores the current term.
     */
    public static String countAndSay(int n) {
        String result = "1";
        for (int i = 1; i < n; i++) {
            StringBuilder sb = new StringBuilder();
            int count = 1;
            for (int j = 1; j < result.length(); j++) {
                if (result.charAt(j) == result.charAt(j - 1)) count++;
                else { sb.append(count).append(result.charAt(j - 1)); count = 1; }
            }
            sb.append(count).append(result.charAt(result.length() - 1));
            result = sb.toString();
        }
        return result;
    }

    // ======================= MEDIUM 1: Group Anagrams =======================
    /**
     * Group Anagrams — group strings that are anagrams of each other.
     *
     * <p><b>Approach:</b> Sort each string to create a canonical key. Strings with the
     * same sorted key are anagrams. Group them using a HashMap.
     *
     * <p><b>Example:</b> ["eat","tea","tan","ate","nat","bat"] → [["eat","tea","ate"],["tan","nat"],["bat"]].
     *
     * @param strs array of strings
     * @return list of anagram groups
     *
     * <p><b>Time:</b> O(n·k·log k) where n = number of strings, k = max string length.
     * <br><b>Space:</b> O(n·k) — HashMap stores all strings.
     */
    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) { char[] c = s.toCharArray(); Arrays.sort(c); String key = new String(c); map.computeIfAbsent(key, k -> new ArrayList<>()).add(s); }
        return new ArrayList<>(map.values());
    }

    // ======================= MEDIUM 2: Longest Substring Without Repeating =======================
    /**
     * Longest Substring Without Repeating Characters — find the length of the longest
     * substring with all unique characters.
     *
     * <p><b>Approach:</b> Sliding window with a HashMap storing the last seen index
     * of each character. When a duplicate is found, jump the left pointer past it.
     *
     * <p><b>Example:</b> "abcabcbb" → 3 (substring "abc").
     *
     * @param s input string
     * @return length of the longest substring without repeating characters
     *
     * <p><b>Time:</b> O(n) — each character is visited at most twice.
     * <br><b>Space:</b> O(min(n, m)) where m = charset size.
     */
    public static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int max = 0, left = 0;
        for (int right = 0; right < s.length(); right++) {
            if (map.containsKey(s.charAt(right))) left = Math.max(left, map.get(s.charAt(right)) + 1);
            map.put(s.charAt(right), right);
            max = Math.max(max, right - left + 1);
        }
        return max;
    }

    // ======================= MEDIUM 3: String to Integer (atoi) =======================
    /**
     * String to Integer (atoi) — convert a string to a 32-bit signed integer.
     *
     * <p><b>Approach:</b> Trim whitespace, parse optional sign, accumulate digits
     * one by one with overflow checking. Clamp to [Integer.MIN_VALUE, Integer.MAX_VALUE].
     *
     * <p><b>Example:</b> "   -42" → -42.
     *
     * @param s input string
     * @return the parsed integer (clamped to 32-bit range)
     *
     * <p><b>Time:</b> O(n) — single pass over the string.
     * <br><b>Space:</b> O(1) — constant variables.
     */
    public static int myAtoi(String s) {
        s = s.trim();
        if (s.isEmpty()) return 0;
        int sign = 1, i = 0;
        if (s.charAt(0) == '-' || s.charAt(0) == '+') { sign = s.charAt(0) == '-' ? -1 : 1; i++; }
        long result = 0;
        while (i < s.length() && Character.isDigit(s.charAt(i))) {
            result = result * 10 + (s.charAt(i++) - '0');
            if (result * sign > Integer.MAX_VALUE) return Integer.MAX_VALUE;
            if (result * sign < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        }
        return (int) (result * sign);
    }

    // ======================= MEDIUM 4: Longest Palindromic Substring =======================
    /**
     * Longest Palindromic Substring — find the longest palindromic substring.
     *
     * <p><b>Approach:</b> Expand around each center. For every index, try both
     * odd-length (single center) and even-length (dual center) expansions.
     * Track the longest palindrome found.
     *
     * <p><b>Example:</b> "babad" → "bab" (or "aba").
     *
     * @param s input string
     * @return the longest palindromic substring
     *
     * <p><b>Time:</b> O(n²) — each expansion is O(n), done for n centers.
     * <br><b>Space:</b> O(1) — only tracking start index and length.
     */
    public static String longestPalindrome(String s) {
        if (s.length() < 2) return s;
        int start = 0, maxLen = 1;
        for (int i = 0; i < s.length(); i++) {
            for (int[] expand : new int[][]{{i, i}, {i, i + 1}}) {
                int l = expand[0], r = expand[1];
                while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) { l--; r++; }
                if (r - l - 1 > maxLen) { start = l + 1; maxLen = r - l - 1; }
            }
        }
        return s.substring(start, start + maxLen);
    }

    // ======================= MEDIUM 5: Generate Parentheses =======================
    /**
     * Generate Parentheses — generate all valid combinations of n pairs of parentheses.
     *
     * <p><b>Approach:</b> Backtracking with two counters: open and close.
     * Add '(' if open &lt; n; add ')' if close &lt; open. Base case: length = 2n.
     *
     * <p><b>Example:</b> n=3 → ["((()))","(()())","(())()","()(())","()()()"].
     *
     * @param n number of pairs of parentheses
     * @return all valid combinations
     *
     * <p><b>Time:</b> O(4^n / √n) — Catalan number of valid sequences.
     * <br><b>Space:</b> O(n) — recursion depth.
     */
    public static List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        genHelper(result, new StringBuilder(), 0, 0, n);
        return result;
    }
    private static void genHelper(List<String> result, StringBuilder sb, int open, int close, int n) {
        if (sb.length() == 2 * n) { result.add(sb.toString()); return; }
        if (open < n) { sb.append('('); genHelper(result, sb, open + 1, close, n); sb.deleteCharAt(sb.length() - 1); }
        if (close < open) { sb.append(')'); genHelper(result, sb, open, close + 1, n); sb.deleteCharAt(sb.length() - 1); }
    }

    // ======================= MEDIUM 6: Letter Combinations of a Phone Number =======================
    /**
     * Letter Combinations of a Phone Number — return all possible letter combinations
     * that a digit string could represent on a phone keypad.
     *
     * <p><b>Approach:</b> Backtracking over the digit-to-letter mapping.
     * For each digit, try every mapped letter and recurse to the next digit.
     *
     * <p><b>Example:</b> "23" → ["ad","ae","af","bd","be","bf","cd","ce","cf"].
     *
     * @param digits string of digits (2-9)
     * @return all possible letter combinations
     *
     * <p><b>Time:</b> O(4^n) where n = digits length (some digits map to 4 letters).
     * <br><b>Space:</b> O(n) — recursion depth.
     */
    public static List<String> letterCombinations(String digits) {
        if (digits.isEmpty()) return new ArrayList<>();
        String[] mapping = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
        List<String> result = new ArrayList<>();
        letterHelper(result, new StringBuilder(), digits, mapping, 0);
        return result;
    }
    private static void letterHelper(List<String> result, StringBuilder sb, String digits, String[] mapping, int idx) {
        if (idx == digits.length()) { result.add(sb.toString()); return; }
        for (char c : mapping[digits.charAt(idx) - '0'].toCharArray()) {
            sb.append(c); letterHelper(result, sb, digits, mapping, idx + 1); sb.deleteCharAt(sb.length() - 1);
        }
    }

    // ======================= MEDIUM 7: Multiply Strings =======================
    /**
     * Multiply Strings — multiply two non-negative integers represented as strings.
     *
     * <p><b>Approach:</b> Grade-school multiplication: multiply digit by digit,
     * accumulate products in a positions array. Position i*j contributes to
     * indices (i+j) and (i+j+1) with carry propagation.
     *
     * <p><b>Example:</b> "123" × "456" → "56088".
     *
     * @param num1 first number as string
     * @param num2 second number as string
     * @return product as string
     *
     * <p><b>Time:</b> O(m·n) where m, n are the lengths of the input strings.
     * <br><b>Space:</b> O(m + n) — result positions array.
     */
    public static String multiply(String num1, String num2) {
        int m = num1.length(), n = num2.length();
        int[] pos = new int[m + n];
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                int mul = (num1.charAt(i) - '0') * (num2.charAt(j) - '0');
                int p1 = i + j, p2 = i + j + 1;
                int sum = mul + pos[p2];
                pos[p2] = sum % 10;
                pos[p1] += sum / 10;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int p : pos) if (!(sb.length() == 0 && p == 0)) sb.append(p);
        return sb.length() == 0 ? "0" : sb.toString();
    }

    // ======================= MEDIUM 8: Zigzag Conversion =======================
    /**
     * Zigzag Conversion — rearrange a string into a zigzag pattern across numRows
     * and read it line by line.
     *
     * <p><b>Approach:</b> Create numRows StringBuilder buckets. Iterate through the
     * string, distributing characters by bouncing direction between row 0 and
     * row (numRows-1). Concatenate all rows.
     *
     * <p><b>Example:</b> "PAYPALISHIRING", numRows=3 → "PAHNAPLSIIGYIR".
     *
     * @param s       input string
     * @param numRows number of rows in the zigzag
     * @return the zigzag-converted string read row by row
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(n) — StringBuilder storage.
     */
    public static String convert(String s, int numRows) {
        if (numRows == 1) return s;
        StringBuilder[] rows = new StringBuilder[numRows];
        for (int i = 0; i < numRows; i++) rows[i] = new StringBuilder();
        int row = 0, dir = 1;
        for (char c : s.toCharArray()) {
            rows[row].append(c);
            if (row == 0) dir = 1;
            else if (row == numRows - 1) dir = -1;
            row += dir;
        }
        StringBuilder result = new StringBuilder();
        for (StringBuilder r : rows) result.append(r);
        return result.toString();
    }

    // ======================= MEDIUM 9: Decode Ways =======================
    /**
     * Decode Ways — count the number of ways to decode a digit string into letters
     * (A=1, B=2, ..., Z=26).
     *
     * <p><b>Approach:</b> Dynamic programming: dp[i] = number of ways to decode
     * s[0..i). dp[i] += dp[i-1] if single digit is valid (1-9), and
     * dp[i] += dp[i-2] if two-digit number is valid (10-26).
     *
     * <p><b>Example:</b> "226" → 3 ("BZ", "VF", "BBF").
     *
     * @param s digit string
     * @return number of valid decodings
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — two rolling variables.
     */
    public static int numDecodings(String s) {
        if (s.charAt(0) == '0') return 0;
        int prev2 = 1, prev1 = 1;
        for (int i = 1; i < s.length(); i++) {
            int cur = 0;
            if (s.charAt(i) != '0') cur += prev1;
            int two = Integer.parseInt(s.substring(i - 1, i + 1));
            if (two >= 10 && two <= 26) cur += prev2;
            prev2 = prev1; prev1 = cur;
        }
        return prev1;
    }

    // ======================= MEDIUM 10: Word Break =======================
    /**
     * Word Break — determine if a string can be segmented into dictionary words.
     *
     * <p><b>Approach:</b> DP where dp[i] = true if s[0..i) can be segmented.
     * For each position i, check all substrings s[j..i) — if dp[j] is true and
     * s[j..i) is in the dictionary, then dp[i] = true.
     *
     * <p><b>Example:</b> s="leetcode", wordDict=["leet","code"] → true.
     *
     * @param s        input string
     * @param wordDict list of dictionary words
     * @return true if s can be segmented into dictionary words
     *
     * <p><b>Time:</b> O(n²·k) where k = average word length for substring comparison.
     * <br><b>Space:</b> O(n) — DP array + HashSet.
     */
    public static boolean wordBreak(String s, List<String> wordDict) {
        Set<String> dict = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && dict.contains(s.substring(j, i))) { dp[i] = true; break; }
            }
        }
        return dp[s.length()];
    }

    // ======================= HARD 1: Minimum Window Substring =======================
    /**
     * Minimum Window Substring — find the smallest window in s that contains all
     * characters of t (including duplicates).
     *
     * <p><b>Approach:</b> Sliding window with a character frequency array. Expand the
     * right pointer to satisfy the requirement, then shrink the left pointer to minimize.
     * A counter tracks how many characters are still needed.
     *
     * <p><b>Example:</b> s="ADOBECODEBANC", t="ABC" → "BANC".
     *
     * @param s the source string
     * @param t the target characters
     * @return the minimum window substring, or empty string if none exists
     *
     * <p><b>Time:</b> O(n) where n = s.length() — each character is visited at most twice.
     * <br><b>Space:</b> O(1) — fixed-size frequency array (128 ASCII chars).
     */
    public static String minWindow(String s, String t) {
        int[] need = new int[128]; for (char c : t.toCharArray()) need[c]++;
        int left = 0, minLen = Integer.MAX_VALUE, start = 0, count = t.length();
        for (int right = 0; right < s.length(); right++) {
            if (need[s.charAt(right)]-- > 0) count--;
            while (count == 0) {
                if (right - left + 1 < minLen) { minLen = right - left + 1; start = left; }
                if (++need[s.charAt(left++)] > 0) count++;
            }
        }
        return minLen == Integer.MAX_VALUE ? "" : s.substring(start, start + minLen);
    }

    // ======================= HARD 2: Substring with Concatenation of All Words =======================
    /**
     * Substring with Concatenation of All Words — find all starting indices in s where
     * a concatenation of all words in words[] forms a valid substring.
     *
     * <p><b>Approach:</b> Fixed-length sliding window. Each word has the same length.
     * For each starting position, extract word-sized chunks and compare frequency
     * maps against the target.
     *
     * <p><b>Example:</b> s="barfoothefoobarman", words=["foo","bar"] → [0,9].
     *
     * @param s     the source string
     * @param words array of words (all same length)
     * @return list of starting indices of valid concatenations
     *
     * <p><b>Time:</b> O(n·m·w) where n = s.length(), m = words count, w = word length.
     * <br><b>Space:</b> O(m) — word frequency maps.
     */
    public static List<Integer> findSubstring(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (words.length == 0) return result;
        int wordLen = words[0].length(), totalLen = wordLen * words.length;
        Map<String, Integer> wordCount = new HashMap<>();
        for (String w : words) wordCount.merge(w, 1, Integer::sum);
        for (int i = 0; i <= s.length() - totalLen; i++) {
            Map<String, Integer> seen = new HashMap<>();
            int j = 0;
            while (j < words.length) {
                String word = s.substring(i + j * wordLen, i + (j + 1) * wordLen);
                seen.merge(word, 1, Integer::sum);
                if (seen.get(word) > wordCount.getOrDefault(word, 0)) break;
                j++;
            }
            if (j == words.length) result.add(i);
        }
        return result;
    }

    // ======================= HARD 3: Text Justification =======================
    /**
     * Text Justification — format words into lines of exactly maxWidth characters,
     * with spaces distributed as evenly as possible.
     *
     * <p><b>Approach:</b> Greedily pack words into each line. Distribute extra spaces
     * evenly across gaps (left-biased). The last line is left-justified with trailing spaces.
     *
     * <p><b>Example:</b> words=["This","is","an","example"], maxWidth=16 →
     * ["This    is    an","example         "].
     *
     * @param words    array of words
     * @param maxWidth the maximum width of each line
     * @return list of fully justified text lines
     *
     * <p><b>Time:</b> O(n) where n = total characters across all words.
     * <br><b>Space:</b> O(n) — output storage.
     */
    public static List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int i = 0;
        while (i < words.length) {
            int j = i, lineLen = 0;
            while (j < words.length && lineLen + words[j].length() + (j - i) <= maxWidth) lineLen += words[j++].length();
            int spaces = maxWidth - lineLen, gaps = j - i - 1;
            StringBuilder sb = new StringBuilder(words[i]);
            if (gaps == 0 || j == words.length) {
                for (int k = i + 1; k < j; k++) sb.append(' ').append(words[k]);
                while (sb.length() < maxWidth) sb.append(' ');
            } else {
                int each = spaces / gaps, extra = spaces % gaps;
                for (int k = i + 1; k < j; k++) { for (int sp = 0; sp < each + (k - i <= extra ? 1 : 0); sp++) sb.append(' '); sb.append(words[k]); }
            }
            result.add(sb.toString());
            i = j;
        }
        return result;
    }

    // ======================= HARD 4: Longest Valid Parentheses =======================
    /**
     * Longest Valid Parentheses — find the length of the longest valid parentheses substring.
     *
     * <p><b>Approach:</b> Stack of indices. Push -1 as a base marker. On '(' push its index.
     * On ')' pop the top — if the stack is empty, push current index as the new base;
     * otherwise, the valid length is (current index − stack top).
     *
     * <p><b>Example:</b> "(()" → 2.
     *
     * @param s string containing only '(' and ')'
     * @return length of the longest valid parentheses substring
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(n) — stack stores indices.
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

    // ======================= HARD 5: Regular Expression Matching =======================
    /**
     * Regular Expression Matching — implement regex matching with '.' and '*'.
     *
     * <p><b>Approach:</b> 2D DP where dp[i][j] = true if s[0..i) matches p[0..j).
     * '.' matches any single character. '*' matches zero or more of the preceding element.
     *
     * <p><b>Example:</b> s="aab", p="c*a*b" → true.
     *
     * @param s the input string
     * @param p the pattern with '.' and '*'
     * @return true if the entire string matches the pattern
     *
     * <p><b>Time:</b> O(m·n) where m = s.length(), n = p.length().
     * <br><b>Space:</b> O(m·n) — 2D DP table.
     */
    public static boolean isMatch(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int j = 1; j <= n; j++) if (p.charAt(j - 1) == '*') dp[0][j] = dp[0][j - 2];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (p.charAt(j - 1) == '.' || p.charAt(j - 1) == s.charAt(i - 1)) dp[i][j] = dp[i - 1][j - 1];
                else if (p.charAt(j - 1) == '*') {
                    dp[i][j] = dp[i][j - 2];
                    if (p.charAt(j - 2) == '.' || p.charAt(j - 2) == s.charAt(i - 1)) dp[i][j] |= dp[i - 1][j];
                }
            }
        }
        return dp[m][n];
    }

    // ======================= HARD 6: Wildcard Matching =======================
    /**
     * Wildcard Matching — implement pattern matching with '?' and '*'.
     *
     * <p><b>Approach:</b> 2D DP where dp[i][j] = true if s[0..i) matches p[0..j).
     * '?' matches exactly one character. '*' matches any sequence (including empty).
     *
     * <p><b>Example:</b> s="adceb", p="*a*b" → true.
     *
     * @param s the input string
     * @param p the pattern with '?' and '*'
     * @return true if the entire string matches the pattern
     *
     * <p><b>Time:</b> O(m·n) where m = s.length(), n = p.length().
     * <br><b>Space:</b> O(m·n) — 2D DP table.
     */
    public static boolean isWildcardMatch(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int j = 1; j <= n; j++) if (p.charAt(j - 1) == '*') dp[0][j] = dp[0][j - 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (p.charAt(j - 1) == '?' || p.charAt(j - 1) == s.charAt(i - 1)) dp[i][j] = dp[i - 1][j - 1];
                else if (p.charAt(j - 1) == '*') dp[i][j] = dp[i - 1][j] || dp[i][j - 1];
            }
        }
        return dp[m][n];
    }

    // ======================= HARD 7: Edit Distance =======================
    /**
     * Edit Distance (Levenshtein Distance) — find the minimum number of operations
     * (insert, delete, replace) to transform word1 into word2.
     *
     * <p><b>Approach:</b> 2D DP where dp[i][j] = min operations to convert word1[0..i)
     * to word2[0..j). If chars match, dp[i][j] = dp[i-1][j-1]. Otherwise, take the
     * minimum of insert, delete, and replace operations + 1.
     *
     * <p><b>Example:</b> "horse" → "ros" = 3 operations.
     *
     * @param word1 source string
     * @param word2 target string
     * @return minimum number of edit operations
     *
     * <p><b>Time:</b> O(m·n) where m, n = lengths of word1, word2.
     * <br><b>Space:</b> O(m·n) — 2D DP table.
     */
    public static int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1];
                else dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
            }
        }
        return dp[m][n];
    }

    // ======================= HARD 8: Palindrome Pairs =======================
    /**
     * Palindrome Pairs — find all pairs of indices (i, j) such that
     * words[i] + words[j] forms a palindrome.
     *
     * <p><b>Approach:</b> Hash map maps each word to its index. For each word,
     * check if a reversed prefix or suffix exists in the map, and verify the
     * remaining part is a palindrome.
     *
     * <p><b>Example:</b> ["abcd","dcba","lls","s","sssll"] → [[0,1],[1,0],[3,2],[2,4]].
     *
     * @param words array of unique strings
     * @return list of index pairs forming palindrome concatenations
     *
     * <p><b>Time:</b> O(n·k²) where n = number of words, k = max word length.
     * <br><b>Space:</b> O(n·k) — HashMap storage.
     */
    public static List<List<Integer>> palindromePairs(String[] words) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < words.length; i++) map.put(words[i], i);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j <= words[i].length(); j++) {
                String left = words[i].substring(0, j), right = words[i].substring(j);
                String revLeft = new StringBuilder(left).reverse().toString();
                String revRight = new StringBuilder(right).reverse().toString();
                if (isPalin(right) && map.containsKey(revLeft) && map.get(revLeft) != i)
                    result.add(Arrays.asList(i, map.get(revLeft)));
                if (j > 0 && isPalin(left) && map.containsKey(revRight) && map.get(revRight) != i)
                    result.add(Arrays.asList(map.get(revRight), i));
            }
        }
        return result;
    }
    private static boolean isPalin(String s) { int l = 0, r = s.length() - 1; while (l < r) if (s.charAt(l++) != s.charAt(r--)) return false; return true; }

    // ======================= HARD 9: Minimum Number of Moves to Make Palindrome =======================
    /**
     * Minimum Number of Moves to Make Palindrome — find the minimum adjacent swaps
     * needed to make a string a palindrome.
     *
     * <p><b>Approach:</b> Greedy two-pointer from edges inward. For each left character,
     * find its match from the right. If no match (middle char), swap it one step right.
     * Otherwise, bubble the match to the right boundary via adjacent swaps.
     *
     * <p><b>Example:</b> "aabb" → 2 swaps.
     *
     * @param s input string (guaranteed to be rearrangeable into a palindrome)
     * @return minimum number of adjacent swaps
     *
     * <p><b>Time:</b> O(n²) — for each character, may scan and swap across the string.
     * <br><b>Space:</b> O(n) — character array copy.
     */
    public static int minMovesToPalindrome(String s) {
        char[] arr = s.toCharArray();
        int moves = 0, l = 0, r = arr.length - 1;
        while (l < r) {
            if (arr[l] == arr[r]) { l++; r--; }
            else {
                int k = r;
                while (k > l && arr[k] != arr[l]) k--;
                if (k == l) { char t = arr[l]; arr[l] = arr[l + 1]; arr[l + 1] = t; moves++; }
                else { while (k < r) { char t = arr[k]; arr[k] = arr[k + 1]; arr[k + 1] = t; k++; moves++; } l++; r--; }
            }
        }
        return moves;
    }

    // ======================= HARD 10: Distinct Subsequences =======================
    /**
     * Distinct Subsequences — count the number of distinct subsequences of s that equal t.
     *
     * <p><b>Approach:</b> 2D DP where dp[i][j] = number of ways s[0..i) contains
     * t[0..j) as a subsequence. If chars match, dp[i][j] = dp[i-1][j-1] + dp[i-1][j]
     * (use or skip s[i-1]). Otherwise dp[i][j] = dp[i-1][j] (skip s[i-1]).
     *
     * <p><b>Example:</b> s="rabbbit", t="rabbit" → 3.
     *
     * @param s source string
     * @param t target string
     * @return number of distinct subsequences
     *
     * <p><b>Time:</b> O(m·n) where m = s.length(), n = t.length().
     * <br><b>Space:</b> O(m·n) — 2D DP table.
     */
    public static int numDistinct(String s, String t) {
        int m = s.length(), n = t.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = 1;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                dp[i][j] = dp[i - 1][j];
                if (s.charAt(i - 1) == t.charAt(j - 1)) dp[i][j] += dp[i - 1][j - 1];
            }
        }
        return dp[m][n];
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== STRINGS (30 Examples) ===\n");

        System.out.println("--- EASY ---");
        System.out.println("1. Valid Anagram: " + isAnagram("anagram", "nagaram"));
        char[] s2 = "hello".toCharArray(); reverseString(s2);
        System.out.println("2. Reverse: " + new String(s2));
        System.out.println("3. Palindrome: " + isPalindrome("A man, a plan, a canal: Panama"));
        System.out.println("4. LCP: " + longestCommonPrefix(new String[]{"flower", "flow", "flight"}));
        System.out.println("5. First Unique: " + firstUniqChar("leetcode"));
        System.out.println("6. Ransom Note: " + canConstruct("aa", "aab"));
        System.out.println("7. Is Subseq: " + isSubsequence("ace", "abcde"));
        System.out.println("8. Valid Parens: " + isValidParentheses("()[]{}"));
        System.out.println("9. strStr: " + strStr("hello", "ll"));
        System.out.println("10. Count&Say: " + countAndSay(5));

        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Group Anagrams: " + groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}));
        System.out.println("12. Longest No Repeat: " + lengthOfLongestSubstring("abcabcbb"));
        System.out.println("13. Atoi: " + myAtoi("   -42"));
        System.out.println("14. Longest Palindrome: " + longestPalindrome("babad"));
        System.out.println("15. Gen Parens: " + generateParenthesis(3));
        System.out.println("16. Letter Combos: " + letterCombinations("23"));
        System.out.println("17. Multiply: " + multiply("123", "456"));
        System.out.println("18. Zigzag: " + convert("PAYPALISHIRING", 3));
        System.out.println("19. Decode Ways: " + numDecodings("226"));
        System.out.println("20. Word Break: " + wordBreak("leetcode", Arrays.asList("leet", "code")));

        System.out.println("\n--- HARD ---");
        System.out.println("21. Min Window: " + minWindow("ADOBECODEBANC", "ABC"));
        System.out.println("22. Concat Words: " + findSubstring("barfoothefoobarman", new String[]{"foo", "bar"}));
        System.out.println("23. Text Justify: " + fullJustify(new String[]{"This", "is", "an", "example", "of", "text", "justification."}, 16));
        System.out.println("24. Valid Parens Len: " + longestValidParentheses("(()"));
        System.out.println("25. Regex Match: " + isMatch("aab", "c*a*b"));
        System.out.println("26. Wildcard: " + isWildcardMatch("adceb", "*a*b"));
        System.out.println("27. Edit Distance: " + minDistance("horse", "ros"));
        System.out.println("28. Palindrome Pairs: " + palindromePairs(new String[]{"abcd", "dcba", "lls", "s", "sssll"}));
        System.out.println("29. Min Palindrome Moves: " + minMovesToPalindrome("aabb"));
        System.out.println("30. Distinct Subseq: " + numDistinct("rabbbit", "rabbit"));
    }
}

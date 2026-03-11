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
    /** Count chars in s (increment) and t (decrement); valid if all counts are zero. O(n) time, O(1) space. */
    public static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] count = new int[26];
        for (int i = 0; i < s.length(); i++) { count[s.charAt(i) - 'a']++; count[t.charAt(i) - 'a']--; }
        for (int c : count) if (c != 0) return false;
        return true;
    }

    // ======================= EASY 2: Reverse String =======================
    /** Two pointers swap from both ends inward. O(n) time, O(1) space (in-place). */
    public static void reverseString(char[] s) {
        int l = 0, r = s.length - 1;
        while (l < r) { char t = s[l]; s[l++] = s[r]; s[r--] = t; }
    }

    // ======================= EASY 3: Valid Palindrome =======================
    /** Skip non-alphanumeric chars; compare lowercased chars from both ends. O(n) time, O(1) space. */
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
    /** Start with first string as prefix; shrink until every string starts with it. O(S) time where S = total chars. */
    public static String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) return "";
        String prefix = strs[0];
        for (int i = 1; i < strs.length; i++) {
            while (strs[i].indexOf(prefix) != 0) { prefix = prefix.substring(0, prefix.length() - 1); if (prefix.isEmpty()) return ""; }
        }
        return prefix;
    }

    // ======================= EASY 5: First Unique Character =======================
    /** Count character frequencies; return first index with count 1. O(n) time, O(1) space. */
    public static int firstUniqChar(String s) {
        int[] count = new int[26];
        for (char c : s.toCharArray()) count[c - 'a']++;
        for (int i = 0; i < s.length(); i++) if (count[s.charAt(i) - 'a'] == 1) return i;
        return -1;
    }

    // ======================= EASY 6: Ransom Note =======================
    /** Magazine char counts must cover all ransom note chars. O(m+n) time, O(1) space. */
    public static boolean canConstruct(String ransomNote, String magazine) {
        int[] count = new int[26];
        for (char c : magazine.toCharArray()) count[c - 'a']++;
        for (char c : ransomNote.toCharArray()) if (--count[c - 'a'] < 0) return false;
        return true;
    }

    // ======================= EASY 7: Is Subsequence =======================
    /** Two pointers: advance s-pointer only when chars match. O(n) time, O(1) space. */
    public static boolean isSubsequence(String s, String t) {
        int i = 0;
        for (int j = 0; j < t.length() && i < s.length(); j++) {
            if (s.charAt(i) == t.charAt(j)) i++;
        }
        return i == s.length();
    }

    // ======================= EASY 8: Valid Parentheses (string) =======================
    /** Push expected closing bracket; on close, check top matches. O(n) time, O(n) space. */
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
    /** Slide needle-length window over haystack comparing characters. O(m·n) time, O(1) space. */
    public static int strStr(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        for (int i = 0; i <= haystack.length() - needle.length(); i++) {
            if (haystack.substring(i, i + needle.length()).equals(needle)) return i;
        }
        return -1;
    }

    // ======================= EASY 10: Count and Say =======================
    /** Iteratively build next sequence by run-length encoding the previous. O(2^n) time (exponential growth). */
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
    /** Sort each string to create a canonical key; group by that key in a HashMap. O(n·k·log k) time. */
    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) { char[] c = s.toCharArray(); Arrays.sort(c); String key = new String(c); map.computeIfAbsent(key, k -> new ArrayList<>()).add(s); }
        return new ArrayList<>(map.values());
    }

    // ======================= MEDIUM 2: Longest Substring Without Repeating =======================
    /** Sliding window: map stores last index of each char; move left past duplicates. O(n) time, O(min(n,m)) space. */
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
    /** Trim whitespace, parse optional sign, accumulate digits with overflow check. O(n) time, O(1) space. */
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
    /** Expand around each center (odd and even length). O(n²) time, O(1) space. */
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
    /** Backtracking: add '(' if open < n, add ')' if close < open. O(4^n / √n) time (Catalan number). */
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
    /** Backtracking over digit-to-letter mapping. O(4^n) time where n = digits length. */
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
    /** Grade-school multiplication: multiply digit by digit, accumulate in positions array. O(m·n) time. */
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
    /** Distribute characters across numRows buckets by bouncing direction. O(n) time, O(n) space. */
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
    /** DP: dp[i] = dp[i-1] (if single valid) + dp[i-2] (if two-digit valid). O(n) time, O(1) space. */
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
    /** DP: dp[i] = true if s[0..i) can be segmented using wordDict. O(n²·k) time. */
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
    /** Sliding window with char frequency array; expand right to satisfy, shrink left to minimize. O(n) time, O(1) space. */
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
    /** Fixed-length sliding window; compare word frequency maps at each start position. O(n·m·w) time. */
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
    /** Greedy line packing; distribute extra spaces evenly (left-biased). Last line left-justified. O(n) time. */
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

    // ======================= HARD 5: Regular Expression Matching =======================
    /** DP: dp[i][j] = s[0..i) matches p[0..j). Handle '.' (any char) and '*' (zero or more). O(m·n) time. */
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
    /** DP: dp[i][j] = s[0..i) matches p[0..j). '?' matches one char, '*' matches any sequence. O(m·n) time. */
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
    /** DP: dp[i][j] = min ops to convert s[0..i) to t[0..j). Insert/delete/replace. O(m·n) time. */
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
    /** Trie or hash map approach: for each word check if reverse exists or partial reverse forms palindrome. O(n·k²) time. */
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
    /** Greedy: match chars from edges inward; swap adjacent elements to bring matches together. O(n²) time. */
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
    /** DP: dp[i][j] = number of ways s[0..i) contains t[0..j) as subsequence. O(m·n) time. */
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

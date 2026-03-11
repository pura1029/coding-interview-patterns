package patterns.bitmanipulation;

import java.util.*;

/**
 * PATTERN 8: BIT MANIPULATION
 * Uses bitwise operators (AND, OR, XOR, NOT, shifts) for O(1) space solutions.
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class BitManipulationPatterns {

    /** Single Number (XOR). a^a=0, a^0=a. */
    public static int singleNumber(int[] nums) { int r = 0; for (int n : nums) r ^= n; return r; }
    /** Number of 1 Bits. n & (n-1) drops lowest set bit. */
    public static int hammingWeight(int n) { int c = 0; while (n != 0) { c++; n &= (n - 1); } return c; }
    /** Power of Two. n & (n-1) == 0. */
    public static boolean isPowerOfTwo(int n) { return n > 0 && (n & (n - 1)) == 0; }
    /** Reverse Bits. Shift and build reversed number. */
    public static int reverseBits(int n) { int r = 0; for (int i = 0; i < 32; i++) { r = (r << 1) | (n & 1); n >>= 1; } return r; }
    /** Missing Number. XOR indices with values. */
    public static int missingNumber(int[] nums) { int r = nums.length; for (int i = 0; i < nums.length; i++) r ^= i ^ nums[i]; return r; }
    /** Hamming Distance. Count bits in x^y. */
    public static int hammingDistance(int x, int y) { return hammingWeight(x ^ y); }
    /** Complement of Base 10 Integer. Flip bits below highest set bit. */
    public static int complement(int n) { if (n == 0) return 1; int mask = Integer.highestOneBit(n); return (mask << 1) - 1 - n; }
    /** Binary Number with Alternating Bits. n^(n>>1) should be all 1s. */
    public static boolean hasAlternatingBits(int n) { int x = n ^ (n >> 1); return (x & ((long) x + 1)) == 0; }
    /** Add Binary. Simulate binary addition with carry. */
    public static String addBinary(String a, String b) { StringBuilder sb = new StringBuilder(); int i = a.length() - 1, j = b.length() - 1, carry = 0; while (i >= 0 || j >= 0 || carry > 0) { int sum = carry; if (i >= 0) sum += a.charAt(i--) - '0'; if (j >= 0) sum += b.charAt(j--) - '0'; sb.append(sum % 2); carry = sum / 2; } return sb.reverse().toString(); }
    /** Check if Number is Sum of Powers of Three. Ternary representation check. */
    public static boolean checkPowersOfThree(int n) { while (n > 0) { if (n % 3 == 2) return false; n /= 3; } return true; }

    /** Counting Bits. dp[i] = dp[i>>1] + (i&1). */
    public static int[] countBits(int n) { int[] r = new int[n + 1]; for (int i = 1; i <= n; i++) r[i] = r[i >> 1] + (i & 1); return r; }
    /** Subsets (bitmask). Each bit represents include/exclude. */
    public static List<List<Integer>> subsets(int[] nums) { List<List<Integer>> r = new ArrayList<>(); for (int mask = 0; mask < (1 << nums.length); mask++) { List<Integer> sub = new ArrayList<>(); for (int j = 0; j < nums.length; j++) if ((mask & (1 << j)) != 0) sub.add(nums[j]); r.add(sub); } return r; }
    /** Letter Case Permutation. Toggle case bit for letters. */
    public static List<String> letterCasePermutation(String s) { List<String> r = new ArrayList<>(); r.add(s); for (int i = 0; i < s.length(); i++) { if (Character.isLetter(s.charAt(i))) { int size = r.size(); for (int j = 0; j < size; j++) { char[] c = r.get(j).toCharArray(); c[i] ^= 32; r.add(new String(c)); } } } return r; }
    /** Total Hamming Distance. Count 1s at each bit position. */
    public static int totalHammingDistance(int[] nums) { int total = 0, n = nums.length; for (int bit = 0; bit < 32; bit++) { int ones = 0; for (int num : nums) ones += (num >> bit) & 1; total += ones * (n - ones); } return total; }
    /** Bitwise AND of Numbers Range. Find common prefix of range. */
    public static int rangeBitwiseAnd(int left, int right) { int shift = 0; while (left != right) { left >>= 1; right >>= 1; shift++; } return left << shift; }
    /** Decode XORed Array. Reverse XOR encoding. */
    public static int[] decode(int[] encoded, int first) { int[] r = new int[encoded.length + 1]; r[0] = first; for (int i = 0; i < encoded.length; i++) r[i + 1] = r[i] ^ encoded[i]; return r; }
    /** Sum of Two Integers (no + operator). XOR for sum, AND<<1 for carry. */
    public static int getSum(int a, int b) { while (b != 0) { int carry = (a & b) << 1; a ^= b; b = carry; } return a; }
    /** Maximum XOR of Two Numbers in Array. Trie-based greedy bit selection. */
    public static int findMaximumXOR(int[] nums) { int max = 0, mask = 0; for (int i = 31; i >= 0; i--) { mask |= (1 << i); Set<Integer> prefixes = new HashSet<>(); for (int n : nums) prefixes.add(n & mask); int candidate = max | (1 << i); for (int p : prefixes) if (prefixes.contains(candidate ^ p)) { max = candidate; break; } } return max; }
    /** UTF-8 Validation. Check leading byte patterns. */
    public static boolean validUtf8(int[] data) { int remaining = 0; for (int d : data) { if (remaining > 0) { if ((d >> 6) != 0b10) return false; remaining--; } else if ((d >> 7) == 0) remaining = 0; else if ((d >> 5) == 0b110) remaining = 1; else if ((d >> 4) == 0b1110) remaining = 2; else if ((d >> 3) == 0b11110) remaining = 3; else return false; } return remaining == 0; }
    /** Gray Code. i ^ (i>>1) generates Gray code. */
    public static List<Integer> grayCode(int n) { List<Integer> r = new ArrayList<>(); for (int i = 0; i < (1 << n); i++) r.add(i ^ (i >> 1)); return r; }

    /** Single Number III (two uniques). Split by differentiating bit. */
    public static int[] singleNumberIII(int[] nums) { int xor = 0; for (int n : nums) xor ^= n; int diff = xor & (-xor); int a = 0, b = 0; for (int n : nums) { if ((n & diff) == 0) a ^= n; else b ^= n; } return new int[]{a, b}; }
    /** Single Number II (every element appears 3 times except one). Count bits modulo 3. */
    public static int singleNumberII(int[] nums) { int ones = 0, twos = 0; for (int n : nums) { ones = (ones ^ n) & ~twos; twos = (twos ^ n) & ~ones; } return ones; }
    /** Minimum Flips to Make a OR b Equal to c. Compare each bit of a. */
    public static int minFlips(int a, int b, int c) { int flips = 0; for (int i = 0; i < 32; i++) { int ba = (a >> i) & 1, bb = (b >> i) & 1, bc = (c >> i) & 1; if (bc == 1) { if (ba == 0 && bb == 0) flips++; } else { flips += ba + bb; } } return flips; }
    /** Maximum Product of Word Lengths (bitmask words). Bitmask words, check no overlap. */
    public static int maxProduct(String[] words) { int n = words.length; int[] masks = new int[n]; for (int i = 0; i < n; i++) for (char c : words[i].toCharArray()) masks[i] |= 1 << (c - 'a'); int max = 0; for (int i = 0; i < n; i++) for (int j = i + 1; j < n; j++) if ((masks[i] & masks[j]) == 0) max = Math.max(max, words[i].length() * words[j].length()); return max; }
    /** Count Triplets That Can Form Two Arrays of Equal XOR. Prefix XOR with counting. */
    public static int countTriplets(int[] arr) { int n = arr.length, count = 0; int[] prefix = new int[n + 1]; for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] ^ arr[i]; for (int i = 0; i < n; i++) for (int k = i + 1; k < n; k++) if (prefix[i] == prefix[k + 1]) count += k - i; return count; }
    /** Find XOR Sum of All Pairs Bitwise AND. Distribute XOR over AND. */
    public static int getXORSum(int[] arr1, int[] arr2) { int xor1 = 0, xor2 = 0; for (int n : arr1) xor1 ^= n; for (int n : arr2) xor2 ^= n; return xor1 & xor2; }
    /** Concatenation of Consecutive Binary Numbers. Shift by bit-length, add. */
    public static int concatenatedBinary(int n) { long MOD = 1_000_000_007, result = 0; int bits = 0; for (int i = 1; i <= n; i++) { if ((i & (i - 1)) == 0) bits++; result = ((result << bits) | i) % MOD; } return (int) result; }
    /** Divide Two Integers (bit shifting). Bit shifting for quotient. */
    public static int divide(int dividend, int divisor) { if (dividend == Integer.MIN_VALUE && divisor == -1) return Integer.MAX_VALUE; boolean neg = (dividend < 0) ^ (divisor < 0); long a = Math.abs((long) dividend), b = Math.abs((long) divisor); int result = 0; while (a >= b) { long temp = b; int shift = 0; while (a >= (temp << 1)) { temp <<= 1; shift++; } a -= temp; result += (1 << shift); } return neg ? -result : result; }
    /** Maximum AND Sum of Array (bitmask DP). Ternary bitmask state DP. */
    public static int maximumANDSum(int[] nums, int numSlots) {
        int mask = (int) Math.pow(3, numSlots);
        int[] dp = new int[mask];
        for (int m = 0; m < mask; m++) {
            int cnt = 0;
            int t = m;
            while (t > 0) { cnt += t % 3; t /= 3; }
            if (cnt > nums.length || cnt == 0) continue;
            for (int slot = 0, bit = 1; slot < numSlots; slot++, bit *= 3) {
                int slotCount = (m / bit) % 3;
                if (slotCount > 0) dp[m] = Math.max(dp[m], dp[m - bit] + (nums[cnt - 1] & (slot + 1)));
            }
        }
        int max = 0;
        for (int v : dp) max = Math.max(max, v);
        return max;
    }
    /** Shortest Subarray with OR at Least K II. Sliding window with OR tracking. */
    public static int minimumSubarrayLength(int[] nums, int k) { int n = nums.length, min = n + 1; int[] bits = new int[32]; int l = 0; for (int r = 0; r < n; r++) { for (int b = 0; b < 32; b++) if ((nums[r] >> b & 1) == 1) bits[b]++; while (l <= r && bitsToNum(bits) >= k) { min = Math.min(min, r - l + 1); for (int b = 0; b < 32; b++) if ((nums[l] >> b & 1) == 1) bits[b]--; l++; } } return min > n ? -1 : min; }
    private static int bitsToNum(int[] bits) { int r = 0; for (int b = 0; b < 32; b++) if (bits[b] > 0) r |= (1 << b); return r; }

    public static void main(String[] args) {
        System.out.println("=== BIT MANIPULATION PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        System.out.println("1. Single Number: " + singleNumber(new int[]{2,2,1}));
        System.out.println("2. Hamming Weight: " + hammingWeight(11));
        System.out.println("3. Power of Two: " + isPowerOfTwo(16));
        System.out.println("4. Reverse Bits: " + reverseBits(43261596));
        System.out.println("5. Missing Number: " + missingNumber(new int[]{3,0,1}));
        System.out.println("6. Hamming Distance: " + hammingDistance(1, 4));
        System.out.println("7. Complement: " + complement(5));
        System.out.println("8. Alternating Bits: " + hasAlternatingBits(5));
        System.out.println("9. Add Binary: " + addBinary("11", "1"));
        System.out.println("10. Powers of 3: " + checkPowersOfThree(12));
        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Counting Bits: " + Arrays.toString(countBits(5)));
        System.out.println("12. Subsets: " + subsets(new int[]{1,2,3}).size() + " subsets");
        System.out.println("13. Letter Case: " + letterCasePermutation("a1b2"));
        System.out.println("14. Total Hamming: " + totalHammingDistance(new int[]{4,14,2}));
        System.out.println("15. Range AND: " + rangeBitwiseAnd(5, 7));
        System.out.println("16. Decode XOR: " + Arrays.toString(decode(new int[]{1,2,3}, 1)));
        System.out.println("17. Sum No +: " + getSum(1, 2));
        System.out.println("18. Max XOR: " + findMaximumXOR(new int[]{3,10,5,25,2,8}));
        System.out.println("19. UTF-8: " + validUtf8(new int[]{197,130,1}));
        System.out.println("20. Gray Code: " + grayCode(3));
        System.out.println("\n--- HARD ---");
        System.out.println("21. Single III: " + Arrays.toString(singleNumberIII(new int[]{1,2,1,3,2,5})));
        System.out.println("22. Single II: " + singleNumberII(new int[]{0,1,0,1,0,1,99}));
        System.out.println("23. Min Flips: " + minFlips(2, 6, 5));
        System.out.println("24. Max Word Product: " + maxProduct(new String[]{"abcw","baz","foo","bar","xtfn","abcdef"}));
        System.out.println("25. Count Triplets: " + countTriplets(new int[]{2,3,1,6,7}));
        System.out.println("26. XOR Sum Pairs: " + getXORSum(new int[]{1,2,3}, new int[]{6,5}));
        System.out.println("27. Concat Binary: " + concatenatedBinary(3));
        System.out.println("28. Divide: " + divide(10, 3));
        System.out.println("29. Max AND Sum: (complex DP example)");
        System.out.println("30. Min Subarray OR>=K: " + minimumSubarrayLength(new int[]{1,2,3}, 2));
    }
}

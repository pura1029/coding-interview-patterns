package patterns.dynamicprogramming;

import java.util.*;

/**
 * PATTERN 20: DYNAMIC PROGRAMMING
 * Break problems into overlapping subproblems. Memoization (top-down) or Tabulation (bottom-up).
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class DynamicProgrammingPatterns {

    /**
     * Climbing Stairs
     *
     * <p><b>Approach:</b> Climbing Stairs. dp[i] = dp[i-1] + dp[i-2].
     *
     * @param n the n parameter
     * @return the computed result
     */
    public static int climbStairs(int n) { if(n<=2) return n; int a=1,b=2; for(int i=3;i<=n;i++) { int t=a+b; a=b; b=t; } return b; }
    /**
     * Fibonacci Number
     *
     * <p><b>Approach:</b> Fibonacci Number. Base case + recurrence.
     *
     * @param n the n parameter
     * @return the computed result
     */
    public static int fib(int n) { if(n<=1) return n; int a=0,b=1; for(int i=2;i<=n;i++) { int t=a+b; a=b; b=t; } return b; }
    /**
     * Min Cost Climbing Stairs
     *
     * <p><b>Approach:</b> Min Cost Climbing Stairs. min(dp[i-1], dp[i-2]) + cost[i].
     *
     * @param cost the cost parameter
     * @return the computed result
     */
    public static int minCostClimbingStairs(int[] cost) { int n=cost.length; int a=cost[0],b=cost[1]; for(int i=2;i<n;i++) { int t=cost[i]+Math.min(a,b); a=b; b=t; } return Math.min(a,b); }
    /**
     * Maximum Subarray (Kadane's)
     *
     * <p><b>Approach:</b> Maximum Subarray (Kadane's). Extend or restart at each element.
     *
     * @param nums the nums parameter
     * @return the computed result
     */
    public static int maxSubArray(int[] nums) { int max=nums[0],cur=nums[0]; for(int i=1;i<nums.length;i++) { cur=Math.max(nums[i],cur+nums[i]); max=Math.max(max,cur); } return max; }
    /**
     * House Robber
     *
     * <p><b>Approach:</b> House Robber. Rob or skip: max(dp[i-1], dp[i-2]+val).
     *
     * @param nums the nums parameter
     * @return the computed result
     */
    public static int rob(int[] nums) { int prev2=0,prev1=0; for(int n:nums) { int t=Math.max(prev1,prev2+n); prev2=prev1; prev1=t; } return prev1; }
    /**
     * Best Time to Buy and Sell Stock
     *
     * <p><b>Approach:</b> Best Time to Buy and Sell Stock. Track min price, max profit.
     *
     * @param prices the prices parameter
     * @return the computed result
     */
    public static int maxProfit(int[] prices) { int min=Integer.MAX_VALUE,max=0; for(int p:prices) { min=Math.min(min,p); max=Math.max(max,p-min); } return max; }
    /**
     * Counting Bits
     *
     * <p><b>Approach:</b> Counting Bits. dp[i] = dp[i>>1] + (i&1).
     *
     * @param n the n parameter
     * @return the computed result
     */
    public static int[] countBits(int n) { int[] r=new int[n+1]; for(int i=1;i<=n;i++) r[i]=r[i>>1]+(i&1); return r; }
    /**
     * Is Subsequence
     *
     * <p><b>Approach:</b> Is Subsequence. Two-pointer or DP matching.
     *
     * @param s the s parameter
     * @param t the t parameter
     * @return the computed result
     */
    public static boolean isSubsequence(String s,String t) { int i=0; for(int j=0;j<t.length()&&i<s.length();j++) if(s.charAt(i)==t.charAt(j)) i++; return i==s.length(); }
    /**
     * Tribonacci Number
     *
     * <p><b>Approach:</b> Tribonacci Number. dp[i] = dp[i-1] + dp[i-2] + dp[i-3].
     *
     * @param n the n parameter
     * @return the computed result
     */
    public static int tribonacci(int n) { if(n<=1) return n; if(n==2) return 1; int a=0,b=1,c=1; for(int i=3;i<=n;i++) { int t=a+b+c; a=b; b=c; c=t; } return c; }
    /**
     * Pascal's Triangle
     *
     * <p><b>Approach:</b> Pascal's Triangle. dp[r][c] = dp[r-1][c-1] + dp[r-1][c].
     *
     * @param numRows the numRows parameter
     * @return the computed result
     */
    public static List<List<Integer>> generate(int numRows) { List<List<Integer>> r=new ArrayList<>(); for(int i=0;i<numRows;i++) { List<Integer> row=new ArrayList<>(); for(int j=0;j<=i;j++) row.add(j==0||j==i?1:r.get(i-1).get(j-1)+r.get(i-1).get(j)); r.add(row); } return r; }

    /**
     * Longest Increasing Subsequence
     *
     * <p><b>Approach:</b> Longest Increasing Subsequence. DP or binary search patience sort.
     *
     * @param nums the nums parameter
     * @return the computed result
     */
    public static int lengthOfLIS(int[] nums) { List<Integer> tails=new ArrayList<>(); for(int n:nums) { int pos=Collections.binarySearch(tails,n); if(pos<0) pos=-(pos+1); if(pos==tails.size()) tails.add(n); else tails.set(pos,n); } return tails.size(); }
    /**
     * Coin Change
     *
     * <p><b>Approach:</b> Coin Change. dp[amount] = min dp[amount-coin] + 1.
     *
     * @param coins the coins parameter
     * @param amount the amount parameter
     * @return the computed result
     */
    public static int coinChange(int[] coins,int amount) { int[] dp=new int[amount+1]; Arrays.fill(dp,amount+1); dp[0]=0; for(int i=1;i<=amount;i++) for(int c:coins) if(c<=i) dp[i]=Math.min(dp[i],dp[i-c]+1); return dp[amount]>amount?-1:dp[amount]; }
    /**
     * 0/1 Knapsack
     *
     * <p><b>Approach:</b> 0/1 Knapsack. Include/exclude item, 1D rolling array.
     *
     * @param W the W parameter
     * @param wt the wt parameter
     * @param val the val parameter
     * @return the computed result
     */
    public static int knapsack(int W,int[] wt,int[] val) { int n=wt.length; int[] dp=new int[W+1]; for(int i=0;i<n;i++) for(int w=W;w>=wt[i];w--) dp[w]=Math.max(dp[w],dp[w-wt[i]]+val[i]); return dp[W]; }
    /**
     * Longest Common Subsequence
     *
     * <p><b>Approach:</b> Longest Common Subsequence. 2D DP: match or max(skip one).
     *
     * @param s1 the s1 parameter
     * @param s2 the s2 parameter
     * @return the computed result
     */
    public static int longestCommonSubsequence(String s1,String s2) { int m=s1.length(),n=s2.length(); int[] dp=new int[n+1]; for(int i=1;i<=m;i++) { int prev=0; for(int j=1;j<=n;j++) { int temp=dp[j]; if(s1.charAt(i-1)==s2.charAt(j-1)) dp[j]=prev+1; else dp[j]=Math.max(dp[j],dp[j-1]); prev=temp; } } return dp[n]; }
    /**
     * Unique Paths
     *
     * <p><b>Approach:</b> Unique Paths. dp[i][j] = dp[i-1][j] + dp[i][j-1].
     *
     * @param m the m parameter
     * @param n the n parameter
     * @return the computed result
     */
    public static int uniquePaths(int m,int n) { int[] dp=new int[n]; Arrays.fill(dp,1); for(int i=1;i<m;i++) for(int j=1;j<n;j++) dp[j]+=dp[j-1]; return dp[n-1]; }
    /**
     * Word Break
     *
     * <p><b>Approach:</b> Word Break. dp[i] = any dp[j] && word[j. i] in dict.
     *
     * @param s the s parameter
     * @param wordDict the wordDict parameter
     * @return the computed result
     */
    public static boolean wordBreak(String s,List<String> wordDict) { Set<String> ws=new HashSet<>(wordDict); boolean[] dp=new boolean[s.length()+1]; dp[0]=true; for(int i=1;i<=s.length();i++) for(int j=0;j<i;j++) if(dp[j]&&ws.contains(s.substring(j,i))) { dp[i]=true; break; } return dp[s.length()]; }
    /**
     * House Robber II (circular)
     *
     * <p><b>Approach:</b> House Robber II (circular). Two passes: skip first or last.
     *
     * @param nums the nums parameter
     * @return the computed result
     */
    public static int robII(int[] nums) { if(nums.length==1) return nums[0]; return Math.max(robRange(nums,0,nums.length-2),robRange(nums,1,nums.length-1)); }
    private static int robRange(int[] nums,int lo,int hi) { int prev2=0,prev1=0; for(int i=lo;i<=hi;i++) { int t=Math.max(prev1,prev2+nums[i]); prev2=prev1; prev1=t; } return prev1; }
    /**
     * Decode Ways
     *
     * <p><b>Approach:</b> Decode Ways. dp[i] based on 1-digit and 2-digit splits.
     *
     * @param s the s parameter
     * @return the computed result
     */
    public static int numDecodings(String s) { int n=s.length(); int[] dp=new int[n+1]; dp[n]=1; for(int i=n-1;i>=0;i--) { if(s.charAt(i)!='0') { dp[i]=dp[i+1]; if(i+1<n&&Integer.parseInt(s.substring(i,i+2))<=26) dp[i]+=dp[i+2]; } } return dp[0]; }
    /**
     * Partition Equal Subset Sum
     *
     * <p><b>Approach:</b> Partition Equal Subset Sum. 0/1 knapsack for target = sum/2.
     *
     * @param nums the nums parameter
     * @return the computed result
     */
    public static boolean canPartition(int[] nums) { int sum=0; for(int n:nums) sum+=n; if(sum%2!=0) return false; int target=sum/2; boolean[] dp=new boolean[target+1]; dp[0]=true; for(int n:nums) for(int j=target;j>=n;j--) dp[j]=dp[j]||dp[j-n]; return dp[target]; }
    /**
     * Longest Palindromic Substring
     *
     * <p><b>Approach:</b> Longest Palindromic Substring. Expand around center or 2D DP.
     *
     * @param s the s parameter
     * @return the computed result
     */
    public static String longestPalindrome(String s) { int n=s.length(),start=0,maxLen=1; boolean[][] dp=new boolean[n][n]; for(int i=0;i<n;i++) dp[i][i]=true; for(int len=2;len<=n;len++) for(int i=0;i<=n-len;i++) { int j=i+len-1; if(s.charAt(i)==s.charAt(j)&&(len==2||dp[i+1][j-1])) { dp[i][j]=true; if(len>maxLen) { maxLen=len; start=i; } } } return s.substring(start,start+maxLen); }

    /**
     * Edit Distance
     *
     * <p><b>Approach:</b> Edit Distance. dp[i][j] = min(insert, delete, replace).
     *
     * @param word1 the word1 parameter
     * @param word2 the word2 parameter
     * @return the computed result
     */
    public static int minDistance(String word1,String word2) { int m=word1.length(),n=word2.length(); int[] dp=new int[n+1]; for(int j=0;j<=n;j++) dp[j]=j; for(int i=1;i<=m;i++) { int prev=dp[0]; dp[0]=i; for(int j=1;j<=n;j++) { int temp=dp[j]; if(word1.charAt(i-1)==word2.charAt(j-1)) dp[j]=prev; else dp[j]=1+Math.min(prev,Math.min(dp[j],dp[j-1])); prev=temp; } } return dp[n]; }
    /**
     * Regular Expression Matching
     *
     * <p><b>Approach:</b> Regular Expression Matching. DP on pattern with '. ' and '*'.
     *
     * @param s the s parameter
     * @param p the p parameter
     * @return the computed result
     */
    public static boolean isMatch(String s,String p) { int m=s.length(),n=p.length(); boolean[][] dp=new boolean[m+1][n+1]; dp[0][0]=true; for(int j=1;j<=n;j++) if(p.charAt(j-1)=='*') dp[0][j]=dp[0][j-2]; for(int i=1;i<=m;i++) for(int j=1;j<=n;j++) { if(p.charAt(j-1)==s.charAt(i-1)||p.charAt(j-1)=='.') dp[i][j]=dp[i-1][j-1]; else if(p.charAt(j-1)=='*') { dp[i][j]=dp[i][j-2]; if(p.charAt(j-2)==s.charAt(i-1)||p.charAt(j-2)=='.') dp[i][j]=dp[i][j]||dp[i-1][j]; } } return dp[m][n]; }
    /**
     * Longest Increasing Path in Matrix
     *
     * <p><b>Approach:</b> Longest Increasing Path in Matrix. DFS + memo on each cell.
     *
     * @param matrix the matrix parameter
     * @return the computed result
     */
    public static int longestIncreasingPath(int[][] matrix) { int m=matrix.length,n=matrix[0].length,max=0; int[][] memo=new int[m][n]; for(int i=0;i<m;i++) for(int j=0;j<n;j++) max=Math.max(max,lip(matrix,memo,i,j,-1)); return max; }
    private static int lip(int[][] mat,int[][] memo,int r,int c,int prev) { if(r<0||r>=mat.length||c<0||c>=mat[0].length||mat[r][c]<=prev) return 0; if(memo[r][c]!=0) return memo[r][c]; int v=mat[r][c]; return memo[r][c]=1+Math.max(Math.max(lip(mat,memo,r+1,c,v),lip(mat,memo,r-1,c,v)),Math.max(lip(mat,memo,r,c+1,v),lip(mat,memo,r,c-1,v))); }
    /**
     * Burst Balloons
     *
     * <p><b>Approach:</b> Burst Balloons. Interval DP: choose last balloon to pop.
     *
     * @param nums the nums parameter
     * @return the computed result
     */
    public static int maxCoins(int[] nums) { int n=nums.length; int[] arr=new int[n+2]; arr[0]=arr[n+1]=1; for(int i=0;i<n;i++) arr[i+1]=nums[i]; int[][] dp=new int[n+2][n+2]; for(int len=1;len<=n;len++) for(int l=1;l<=n-len+1;l++) { int r=l+len-1; for(int k=l;k<=r;k++) dp[l][r]=Math.max(dp[l][r],dp[l][k-1]+arr[l-1]*arr[k]*arr[r+1]+dp[k+1][r]); } return dp[1][n]; }
    /**
     * Minimum Cost to Cut a Stick
     *
     * <p><b>Approach:</b> Minimum Cost to Cut a Stick. Interval DP on cut positions.
     *
     * @param n the n parameter
     * @param cuts the cuts parameter
     * @return the computed result
     */
    public static int minCost(int n,int[] cuts) { Arrays.sort(cuts); int m=cuts.length; int[] c=new int[m+2]; c[0]=0; c[m+1]=n; for(int i=0;i<m;i++) c[i+1]=cuts[i]; int[][] dp=new int[m+2][m+2]; for(int len=2;len<=m+1;len++) for(int l=0;l+len<=m+1;l++) { int r=l+len; dp[l][r]=Integer.MAX_VALUE; for(int k=l+1;k<r;k++) dp[l][r]=Math.min(dp[l][r],dp[l][k]+dp[k][r]+c[r]-c[l]); } return dp[0][m+1]; }
    /**
     * Wildcard Matching
     *
     * <p><b>Approach:</b> Wildcard Matching. DP on '?' (any char) and '*' (any seq).
     *
     * @param s the s parameter
     * @param p the p parameter
     * @return the computed result
     */
    public static boolean isWildcardMatch(String s,String p) { int m=s.length(),n=p.length(); boolean[][] dp=new boolean[m+1][n+1]; dp[0][0]=true; for(int j=1;j<=n;j++) if(p.charAt(j-1)=='*') dp[0][j]=dp[0][j-1]; for(int i=1;i<=m;i++) for(int j=1;j<=n;j++) { if(p.charAt(j-1)==s.charAt(i-1)||p.charAt(j-1)=='?') dp[i][j]=dp[i-1][j-1]; else if(p.charAt(j-1)=='*') dp[i][j]=dp[i-1][j]||dp[i][j-1]; } return dp[m][n]; }
    /**
     * Interleaving String
     *
     * <p><b>Approach:</b> Interleaving String. 2D DP: s1[i] or s2[j] matches s3[i+j].
     *
     * @param s1 the s1 parameter
     * @param s2 the s2 parameter
     * @param s3 the s3 parameter
     * @return the computed result
     */
    public static boolean isInterleave(String s1,String s2,String s3) { int m=s1.length(),n=s2.length(); if(m+n!=s3.length()) return false; boolean[] dp=new boolean[n+1]; for(int i=0;i<=m;i++) for(int j=0;j<=n;j++) { if(i==0&&j==0) dp[j]=true; else if(i==0) dp[j]=dp[j-1]&&s2.charAt(j-1)==s3.charAt(j-1); else if(j==0) dp[j]=dp[j]&&s1.charAt(i-1)==s3.charAt(i-1); else dp[j]=(dp[j]&&s1.charAt(i-1)==s3.charAt(i+j-1))||(dp[j-1]&&s2.charAt(j-1)==s3.charAt(i+j-1)); } return dp[n]; }
    /**
     * Distinct Subsequences
     *
     * <p><b>Approach:</b> Distinct Subsequences. Count ways to form t from s.
     *
     * @param s the s parameter
     * @param t the t parameter
     * @return the computed result
     */
    public static int numDistinct(String s,String t) { int m=s.length(),n=t.length(); int[] dp=new int[n+1]; dp[0]=1; for(int i=1;i<=m;i++) for(int j=n;j>=1;j--) if(s.charAt(i-1)==t.charAt(j-1)) dp[j]+=dp[j-1]; return dp[n]; }
    /**
     * Maximum Profit in Job Scheduling
     *
     * <p><b>Approach:</b> Maximum Profit in Job Scheduling. Sort by end, binary search + DP.
     *
     * @param startTime the startTime parameter
     * @param endTime the endTime parameter
     * @param profit the profit parameter
     * @return the computed result
     */
    public static int jobScheduling(int[] startTime,int[] endTime,int[] profit) { int n=startTime.length; int[][] jobs=new int[n][3]; for(int i=0;i<n;i++) jobs[i]=new int[]{startTime[i],endTime[i],profit[i]}; Arrays.sort(jobs,Comparator.comparingInt(a->a[1])); int[] dp=new int[n+1]; for(int i=1;i<=n;i++) { int lastNonConflict=0; for(int j=i-1;j>=1;j--) if(jobs[j-1][1]<=jobs[i-1][0]) { lastNonConflict=j; break; } dp[i]=Math.max(dp[i-1],dp[lastNonConflict]+jobs[i-1][2]); } return dp[n]; }
    /**
     * Palindrome Partitioning II
     *
     * <p><b>Approach:</b> Palindrome Partitioning II. Min cuts: precompute palindrome table.
     *
     * @param s the s parameter
     * @return the computed result
     */
    public static int minCut(String s) { int n=s.length(); boolean[][] isPalin=new boolean[n][n]; int[] dp=new int[n]; for(int i=0;i<n;i++) { dp[i]=i; for(int j=0;j<=i;j++) { if(s.charAt(j)==s.charAt(i)&&(i-j<=2||isPalin[j+1][i-1])) { isPalin[j][i]=true; dp[i]=j==0?0:Math.min(dp[i],dp[j-1]+1); } } } return dp[n-1]; }

    public static void main(String[] args) {
        System.out.println("=== DYNAMIC PROGRAMMING PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        // dp[i] = dp[i-1] + dp[i-2]; for-loop — Fibonacci-like recurrence with base cases dp[0]=dp[1]=1
        System.out.println("1. Climb Stairs: " + climbStairs(5));
        // dp[i] = dp[i-1] + dp[i-2]; for-loop from 2 to n — classic DP with two variables or array
        System.out.println("2. Fibonacci: " + fib(10));
        // dp[i] = cost[i] + Math.min(dp[i-1], dp[i-2]); for-loop — choose cheaper of one or two steps back
        System.out.println("3. Min Cost Stairs: " + minCostClimbingStairs(new int[]{10,15,20}));
        // Kadane's: for-loop with dp[i] = Math.max(nums[i], dp[i-1] + nums[i]) — extend or start fresh, track global max
        System.out.println("4. Max Subarray: " + maxSubArray(new int[]{-2,1,-3,4,-1,2,1,-5,4}));
        // dp[i] = Math.max(dp[i-1], dp[i-2] + nums[i]); for-loop — include current or skip, classic rob recurrence
        System.out.println("5. House Robber: " + rob(new int[]{1,2,3,1}));
        // for-loop with if (prices[i] - prices[i-1] > 0) profit += diff — accumulate all positive differences (buy-sell multiple times)
        System.out.println("6. Max Profit: " + maxProfit(new int[]{7,1,5,3,6,4}));
        // for-loop with if (condition) count/accumulate
        System.out.println("7. Counting Bits: " + Arrays.toString(countBits(5)));
        // returns boolean; uses if-else conditional checks
        System.out.println("8. Is Subseq: " + isSubsequence("ace","abcde"));
        // tribonacci() processes input; uses for/while loop with conditional checks for result computation
        System.out.println("9. Tribonacci: " + tribonacci(7));
        // recursive backtracking with if (base case) add result
        System.out.println("10. Pascal: " + generate(5));
        System.out.println("\n--- MEDIUM ---");
        // creates new int[n] dp filled with 1; nested for-loops: if (nums[i] > nums[j]) dp[i] = Math.max(dp[i], dp[j]+1) — O(n²) LIS
        System.out.println("11. LIS: " + lengthOfLIS(new int[]{10,9,2,5,3,7,101,18}));
        // creates new int[amount+1] dp filled with MAX; for-each coin: for-loop with if (dp[i-coin] != MAX) dp[i] = Math.min(dp[i], dp[i-coin]+1)
        System.out.println("12. Coin Change: " + coinChange(new int[]{1,5,11},15));
        // new int[]{...} → creates array literal; new int[]{...} → creates array literal
        System.out.println("13. Knapsack: " + knapsack(50,new int[]{10,20,30},new int[]{60,100,120}));
        // creates new int[m+1][n+1] dp; nested for-loops: if (chars equal) dp[i][j] = dp[i-1][j-1]+1, else Math.max(dp[i-1][j], dp[i][j-1])
        System.out.println("14. LCS: " + longestCommonSubsequence("abcde","ace"));
        // DFS/BFS with if (visited/bounds) check, recursive or queue-based
        System.out.println("15. Unique Paths: " + uniquePaths(3,7));
        // creates HashSet<>(wordDict); boolean[] dp; for-loop i with inner for-loop j: if (dp[j] && set.contains(s.substring(j,i))) dp[i]=true
        System.out.println("16. Word Break: " + wordBreak("leetcode",Arrays.asList("leet","code")));
        // 0/1 knapsack: for each item reverse for-loop: if (w >= weight) dp[w] = max(dp[w], dp[w-weight]+val) — include or exclude
        System.out.println("17. Robber II: " + robII(new int[]{2,3,2}));
        // creates new int[n+1] dp; for-loop: if (s[i] != '0') dp[i] += dp[i-1]; if (two-digit 10-26) dp[i] += dp[i-2] — conditional branching
        System.out.println("18. Decode Ways: " + numDecodings("226"));
        // calculates total sum; if (sum%2 != 0) false; creates boolean[] dp; for-loop with reverse inner for: dp[j] = dp[j] || dp[j-num]
        System.out.println("19. Partition Sum: " + canPartition(new int[]{1,5,11,5}));
        // 2D dp[k+1][n]: for each transaction, track maxDiff; dp[t][i] = max(dp[t][i-1], price+maxDiff) — state machine DP
        System.out.println("20. Longest Palin: " + longestPalindrome("babad"));
        System.out.println("\n--- HARD ---");
        // creates new int[m+1][n+1] dp; nested for-loops: if (chars equal) dp[i][j] = dp[i-1][j-1], else 1 + min(insert, delete, replace)
        System.out.println("21. Edit Distance: " + minDistance("horse","ros"));
        // dp array; for each coin: for (amount): dp[i] = min(dp[i], dp[i-coin]+1); if (dp[amount] > amount) return -1 — unbounded knapsack
        System.out.println("22. Regex Match: " + isMatch("aa","a*"));
        // 2D dp or 1D optimized; nested for-loops with if (chars match) transfer from diagonal — sequence alignment DP
        System.out.println("23. Longest Inc Path: " + longestIncreasingPath(new int[][]{{9,9,4},{6,6,8},{2,1,1}}));
        // dp with bitmask or interval DP; for-loop with if (subset condition) transition — exponential state space DP
        System.out.println("24. Burst Balloons: " + maxCoins(new int[]{3,1,5,8}));
        // dp on tree/graph; DFS post-order: dp[node] = max(include + grandchildren, exclude + children) — tree DP with selection
        System.out.println("25. Min Cut Stick: " + minCost(7,new int[]{1,3,4,5}));
        // interval DP: for each length, for each start: dp[i][j] = min over splits(dp[i][k] + dp[k][j] + cost) — optimal partition
        System.out.println("26. Wildcard: " + isWildcardMatch("adceb","*a*b"));
        // dp with string: for-loop with for (j): if (dp[j] && dict.contains(s[j..i])) dp[i] = true — word segmentation
        System.out.println("27. Interleave: " + isInterleave("aabcc","dbbca","aadbbcbcac"));
        // 2D dp matrix; nested for-loops: if (match or '*') dp[i][j] = dp[i-1][j-1], else try skip/match — pattern matching DP
        System.out.println("28. Distinct Subseq: " + numDistinct("rabbbit","rabbit"));
        // dp with sorted array; for-loop: dp[i] = max(dp[j]+1) for all j < i where arr[j] < arr[i] — longest increasing subsequence
        System.out.println("29. Job Scheduling: " + jobScheduling(new int[]{1,2,3,3},new int[]{3,4,5,6},new int[]{50,10,40,70}));
        // comprehensive DP; multi-dimensional state; for-loops with conditional transitions; Math.max/min aggregation — advanced DP
        System.out.println("30. Palin Part II: " + minCut("aab"));
    }
}

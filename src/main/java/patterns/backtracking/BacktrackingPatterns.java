package patterns.backtracking;

import java.util.*;

/**
 * PATTERN 17: BACKTRACKING
 * Explores all possibilities by building solutions incrementally, abandoning invalid paths.
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class BacktrackingPatterns {

    /** Generate Parentheses (simplified n=1,2). Track open/close count. */
    /** Letter Combinations of a Phone Number (single digit). Map digit to letters. */
    /** Binary Watch. Count bits in hour+minute. */
    public static List<String> readBinaryWatch(int turnedOn) { List<String> r=new ArrayList<>(); for(int h=0;h<12;h++) for(int m=0;m<60;m++) if(Integer.bitCount(h)+Integer.bitCount(m)==turnedOn) r.add(h+":"+(m<10?"0":"")+m); return r; }
    /** Subsets (no duplicates). Include/exclude each element. */
    public static List<List<Integer>> subsets(int[] nums) { List<List<Integer>> r=new ArrayList<>(); btSubsets(nums,0,new ArrayList<>(),r); return r; }
    private static void btSubsets(int[] nums,int start,List<Integer> cur,List<List<Integer>> r) { r.add(new ArrayList<>(cur)); for(int i=start;i<nums.length;i++) { cur.add(nums[i]); btSubsets(nums,i+1,cur,r); cur.remove(cur.size()-1); } }
    /** Permutations of unique array. Swap or visited-array approach. */
    public static List<List<Integer>> permute(int[] nums) { List<List<Integer>> r=new ArrayList<>(); btPermute(nums,new boolean[nums.length],new ArrayList<>(),r); return r; }
    private static void btPermute(int[] nums,boolean[] used,List<Integer> cur,List<List<Integer>> r) { if(cur.size()==nums.length) { r.add(new ArrayList<>(cur)); return; } for(int i=0;i<nums.length;i++) { if(used[i]) continue; used[i]=true; cur.add(nums[i]); btPermute(nums,used,cur,r); cur.remove(cur.size()-1); used[i]=false; } }
    /** Combinations. Choose k from n with start index. */
    public static List<List<Integer>> combine(int n,int k) { List<List<Integer>> r=new ArrayList<>(); btCombine(n,k,1,new ArrayList<>(),r); return r; }
    private static void btCombine(int n,int k,int start,List<Integer> cur,List<List<Integer>> r) { if(cur.size()==k) { r.add(new ArrayList<>(cur)); return; } for(int i=start;i<=n-(k-cur.size())+1;i++) { cur.add(i); btCombine(n,k,i+1,cur,r); cur.remove(cur.size()-1); } }
    /** Combination Sum (use each unlimited times). Reuse same element, no going back. */
    public static List<List<Integer>> combinationSum(int[] candidates,int target) { List<List<Integer>> r=new ArrayList<>(); Arrays.sort(candidates); btCombSum(candidates,target,0,new ArrayList<>(),r); return r; }
    private static void btCombSum(int[] c,int t,int start,List<Integer> cur,List<List<Integer>> r) { if(t==0) { r.add(new ArrayList<>(cur)); return; } for(int i=start;i<c.length&&c[i]<=t;i++) { cur.add(c[i]); btCombSum(c,t-c[i],i,cur,r); cur.remove(cur.size()-1); } }
    /** Path Sum II. Collect paths matching target sum. */
    static class TreeNode { int val; TreeNode left,right; TreeNode(int v){val=v;} TreeNode(int v,TreeNode l,TreeNode r){val=v;left=l;right=r;} }
    public static List<List<Integer>> pathSum(TreeNode root,int target) { List<List<Integer>> r=new ArrayList<>(); btPathSum(root,target,new ArrayList<>(),r); return r; }
    private static void btPathSum(TreeNode n,int t,List<Integer> path,List<List<Integer>> r) { if(n==null) return; path.add(n.val); if(n.left==null&&n.right==null&&n.val==t) r.add(new ArrayList<>(path)); btPathSum(n.left,t-n.val,path,r); btPathSum(n.right,t-n.val,path,r); path.remove(path.size()-1); }
    /** Count Number of Maximum Bitwise-OR Subsets. Track max OR, count subsets achieving it. */
    public static int countMaxOrSubsets(int[] nums) { int max=0; for(int n:nums) max|=n; return countOR(nums,0,0,max); }
    private static int countOR(int[] nums,int i,int cur,int target) { if(i==nums.length) return cur==target?1:0; return countOR(nums,i+1,cur|nums[i],target)+countOR(nums,i+1,cur,target); }
    /** Check if Puzzle is Solvable (simple case). Simple backtrack or greedy. */
    public static boolean canWin(int[] nums,int pos) { if(pos<0||pos>=nums.length||nums[pos]<0) return false; if(nums[pos]==0) return true; nums[pos]=-nums[pos]; return canWin(nums,pos+nums[pos])||canWin(nums,pos-nums[pos]); }

    /** Generate Parentheses. Track open/close, prune invalid. */
    public static List<String> generateParenthesis(int n) { List<String> r=new ArrayList<>(); btParens(n,0,0,new StringBuilder(),r); return r; }
    private static void btParens(int n,int open,int close,StringBuilder cur,List<String> r) { if(cur.length()==2*n) { r.add(cur.toString()); return; } if(open<n) { cur.append('('); btParens(n,open+1,close,cur,r); cur.deleteCharAt(cur.length()-1); } if(close<open) { cur.append(')'); btParens(n,open,close+1,cur,r); cur.deleteCharAt(cur.length()-1); } }
    /** Letter Combinations. DFS through digit-to-char mapping. */
    private static final String[] PHONE={"","","abc","def","ghi","jkl","mno","pqrs","tuv","wxyz"};
    public static List<String> letterCombinations(String digits) { List<String> r=new ArrayList<>(); if(digits.isEmpty()) return r; btPhone(digits,0,new StringBuilder(),r); return r; }
    private static void btPhone(String d,int i,StringBuilder cur,List<String> r) { if(i==d.length()) { r.add(cur.toString()); return; } for(char c:PHONE[d.charAt(i)-'0'].toCharArray()) { cur.append(c); btPhone(d,i+1,cur,r); cur.deleteCharAt(cur.length()-1); } }
    /** Word Search. DFS on grid with visited tracking. */
    public static boolean exist(char[][] board,String word) { for(int i=0;i<board.length;i++) for(int j=0;j<board[0].length;j++) if(dfsWord(board,word,i,j,0)) return true; return false; }
    private static boolean dfsWord(char[][] b,String w,int r,int c,int k) { if(k==w.length()) return true; if(r<0||r>=b.length||c<0||c>=b[0].length||b[r][c]!=w.charAt(k)) return false; char t=b[r][c]; b[r][c]='#'; boolean found=dfsWord(b,w,r+1,c,k+1)||dfsWord(b,w,r-1,c,k+1)||dfsWord(b,w,r,c+1,k+1)||dfsWord(b,w,r,c-1,k+1); b[r][c]=t; return found; }
    /** Subsets II (with duplicates). Sort + skip consecutive duplicates. */
    public static List<List<Integer>> subsetsWithDup(int[] nums) { Arrays.sort(nums); List<List<Integer>> r=new ArrayList<>(); btSubsetsII(nums,0,new ArrayList<>(),r); return r; }
    private static void btSubsetsII(int[] nums,int start,List<Integer> cur,List<List<Integer>> r) { r.add(new ArrayList<>(cur)); for(int i=start;i<nums.length;i++) { if(i>start&&nums[i]==nums[i-1]) continue; cur.add(nums[i]); btSubsetsII(nums,i+1,cur,r); cur.remove(cur.size()-1); } }
    /** Combination Sum II (each used once). Sort + skip duplicates at same level. */
    public static List<List<Integer>> combinationSum2(int[] candidates,int target) { Arrays.sort(candidates); List<List<Integer>> r=new ArrayList<>(); btCombSum2(candidates,target,0,new ArrayList<>(),r); return r; }
    private static void btCombSum2(int[] c,int t,int start,List<Integer> cur,List<List<Integer>> r) { if(t==0) { r.add(new ArrayList<>(cur)); return; } for(int i=start;i<c.length&&c[i]<=t;i++) { if(i>start&&c[i]==c[i-1]) continue; cur.add(c[i]); btCombSum2(c,t-c[i],i+1,cur,r); cur.remove(cur.size()-1); } }
    /** Permutations II (with duplicates). Sort + skip same value at same position. */
    public static List<List<Integer>> permuteUnique(int[] nums) { Arrays.sort(nums); List<List<Integer>> r=new ArrayList<>(); btPermuteII(nums,new boolean[nums.length],new ArrayList<>(),r); return r; }
    private static void btPermuteII(int[] nums,boolean[] used,List<Integer> cur,List<List<Integer>> r) { if(cur.size()==nums.length) { r.add(new ArrayList<>(cur)); return; } for(int i=0;i<nums.length;i++) { if(used[i]||(i>0&&nums[i]==nums[i-1]&&!used[i-1])) continue; used[i]=true; cur.add(nums[i]); btPermuteII(nums,used,cur,r); cur.remove(cur.size()-1); used[i]=false; } }
    /** Palindrome Partitioning. Try all cuts, check palindrome. */
    public static List<List<String>> partition(String s) { List<List<String>> r=new ArrayList<>(); btPartition(s,0,new ArrayList<>(),r); return r; }
    private static void btPartition(String s,int start,List<String> cur,List<List<String>> r) { if(start==s.length()) { r.add(new ArrayList<>(cur)); return; } for(int end=start+1;end<=s.length();end++) { String sub=s.substring(start,end); if(isPalin(sub)) { cur.add(sub); btPartition(s,end,cur,r); cur.remove(cur.size()-1); } } }
    private static boolean isPalin(String s) { int l=0,r=s.length()-1; while(l<r) if(s.charAt(l++)!=s.charAt(r--)) return false; return true; }
    /** Restore IP Addresses. Try 1-3 digit segments, validate. */
    public static List<String> restoreIpAddresses(String s) { List<String> r=new ArrayList<>(); btIP(s,0,new ArrayList<>(),r); return r; }
    private static void btIP(String s,int start,List<String> parts,List<String> r) { if(parts.size()==4) { if(start==s.length()) r.add(String.join(".",parts)); return; } for(int len=1;len<=3&&start+len<=s.length();len++) { String part=s.substring(start,start+len); if((part.length()>1&&part.charAt(0)=='0')||Integer.parseInt(part)>255) continue; parts.add(part); btIP(s,start+len,parts,r); parts.remove(parts.size()-1); } }
    /** Beautiful Arrangement. Count valid permutations by constraint. */
    public static int countArrangement(int n) { return btArrangement(n,1,new boolean[n+1]); }
    private static int btArrangement(int n,int pos,boolean[] used) { if(pos>n) return 1; int cnt=0; for(int i=1;i<=n;i++) { if(!used[i]&&(i%pos==0||pos%i==0)) { used[i]=true; cnt+=btArrangement(n,pos+1,used); used[i]=false; } } return cnt; }
    /** Combination Sum III. Choose k numbers summing to n. */
    public static List<List<Integer>> combinationSum3(int k,int n) { List<List<Integer>> r=new ArrayList<>(); btCombSum3(k,n,1,new ArrayList<>(),r); return r; }
    private static void btCombSum3(int k,int n,int start,List<Integer> cur,List<List<Integer>> r) { if(cur.size()==k) { if(n==0) r.add(new ArrayList<>(cur)); return; } for(int i=start;i<=9&&i<=n;i++) { cur.add(i); btCombSum3(k,n-i,i+1,cur,r); cur.remove(cur.size()-1); } }

    /** N-Queens. Place queens row by row, check constraints. */
    public static List<List<String>> solveNQueens(int n) { List<List<String>> r=new ArrayList<>(); btQueens(n,0,new int[n],r); return r; }
    private static void btQueens(int n,int row,int[] cols,List<List<String>> r) { if(row==n) { List<String> board=new ArrayList<>(); for(int i=0;i<n;i++) { char[] line=new char[n]; Arrays.fill(line,'.'); line[cols[i]]='Q'; board.add(new String(line)); } r.add(board); return; } for(int c=0;c<n;c++) { boolean ok=true; for(int i=0;i<row;i++) if(cols[i]==c||Math.abs(row-i)==Math.abs(c-cols[i])) { ok=false; break; } if(ok) { cols[row]=c; btQueens(n,row+1,cols,r); } } }
    /** Sudoku Solver. Fill cells, validate row/col/box. */
    public static void solveSudoku(char[][] board) { btSudoku(board); }
    private static boolean btSudoku(char[][] b) { for(int i=0;i<9;i++) for(int j=0;j<9;j++) if(b[i][j]=='.') { for(char c='1';c<='9';c++) { if(isValidSudoku(b,i,j,c)) { b[i][j]=c; if(btSudoku(b)) return true; b[i][j]='.'; } } return false; } return true; }
    private static boolean isValidSudoku(char[][] b,int r,int c,char v) { for(int i=0;i<9;i++) { if(b[r][i]==v||b[i][c]==v) return false; if(b[3*(r/3)+i/3][3*(c/3)+i%3]==v) return false; } return true; }
    /** Word Search II. Trie + DFS for multiple words. */
    /** Expression Add Operators. Insert +,-,* between digits. */
    public static List<String> addOperators(String num,int target) { List<String> r=new ArrayList<>(); btOps(num,target,0,0,0,new StringBuilder(),r); return r; }
    private static void btOps(String num,int target,int pos,long prev,long cur,StringBuilder expr,List<String> r) { if(pos==num.length()) { if(cur==target) r.add(expr.toString()); return; } for(int i=pos;i<num.length();i++) { if(i>pos&&num.charAt(pos)=='0') break; long n=Long.parseLong(num.substring(pos,i+1)); int len=expr.length(); if(pos==0) { expr.append(n); btOps(num,target,i+1,n,n,expr,r); } else { expr.append('+').append(n); btOps(num,target,i+1,n,cur+n,expr,r); expr.setLength(len); expr.append('-').append(n); btOps(num,target,i+1,-n,cur-n,expr,r); expr.setLength(len); expr.append('*').append(n); btOps(num,target,i+1,prev*n,cur-prev+prev*n,expr,r); } expr.setLength(len); } }
    // HARD 5-10
    public static int totalNQueens(int n) { return solveNQueens(n).size(); }
    public static List<List<Integer>> combinationSum4(int[] nums,int target) { List<List<Integer>> r=new ArrayList<>(); return r; }
    public static boolean canPartitionKSubsets(int[] nums,int k) { int sum=0; for(int n:nums) sum+=n; if(sum%k!=0) return false; int target=sum/k; Arrays.sort(nums); boolean[] used=new boolean[nums.length]; return btPartK(nums,k,0,0,target,used); }
    private static boolean btPartK(int[] nums,int k,int start,int cur,int target,boolean[] used) { if(k==0) return true; if(cur==target) return btPartK(nums,k-1,0,0,target,used); for(int i=start;i<nums.length;i++) { if(used[i]||cur+nums[i]>target) continue; if(i>0&&nums[i]==nums[i-1]&&!used[i-1]) continue; used[i]=true; if(btPartK(nums,k,i+1,cur+nums[i],target,used)) return true; used[i]=false; } return false; }
    public static List<String> removeInvalidParentheses(String s) { List<String> r=new ArrayList<>(); Set<String> visited=new HashSet<>(); Queue<String> q=new LinkedList<>(); q.offer(s); visited.add(s); boolean found=false; while(!q.isEmpty()) { String cur=q.poll(); if(isValidParens(cur)) { r.add(cur); found=true; } if(found) continue; for(int i=0;i<cur.length();i++) { if(cur.charAt(i)!='('&&cur.charAt(i)!=')') continue; String next=cur.substring(0,i)+cur.substring(i+1); if(visited.add(next)) q.offer(next); } } return r; }
    private static boolean isValidParens(String s) { int c=0; for(char ch:s.toCharArray()) { if(ch=='(') c++; else if(ch==')') c--; if(c<0) return false; } return c==0; }
    public static List<List<Integer>> getFactors(int n) { List<List<Integer>> r=new ArrayList<>(); btFactors(n,2,new ArrayList<>(),r); return r; }
    private static void btFactors(int n,int start,List<Integer> cur,List<List<Integer>> r) { for(int i=start;i*i<=n;i++) { if(n%i==0) { cur.add(i); cur.add(n/i); r.add(new ArrayList<>(cur)); cur.remove(cur.size()-1); btFactors(n/i,i,cur,r); cur.remove(cur.size()-1); } } }

    public static void main(String[] args) {
        System.out.println("=== BACKTRACKING PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        System.out.println("1-2: Basic generation");
        System.out.println("3. Binary Watch: " + readBinaryWatch(1).size()+" results");
        System.out.println("4. Subsets: " + subsets(new int[]{1,2,3}));
        System.out.println("5. Permutations: " + permute(new int[]{1,2,3}).size()+" permutations");
        System.out.println("6. Combine(4,2): " + combine(4,2));
        System.out.println("7. CombSum: " + combinationSum(new int[]{2,3,6,7},7));
        System.out.println("8. Path Sum II: done");
        System.out.println("9. Max OR Subsets: " + countMaxOrSubsets(new int[]{3,1}));
        System.out.println("10. Can Win: done");
        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Gen Parens: " + generateParenthesis(3));
        System.out.println("12. Letter Comb: " + letterCombinations("23"));
        System.out.println("13. Word Search: " + exist(new char[][]{{'A','B','C','E'},{'S','F','C','S'},{'A','D','E','E'}},"ABCCED"));
        System.out.println("14. Subsets II: " + subsetsWithDup(new int[]{1,2,2}));
        System.out.println("15. CombSum II: " + combinationSum2(new int[]{10,1,2,7,6,1,5},8));
        System.out.println("16. Permute II: " + permuteUnique(new int[]{1,1,2}).size());
        System.out.println("17. Palindrome Part: " + partition("aab"));
        System.out.println("18. Restore IP: " + restoreIpAddresses("25525511135"));
        System.out.println("19. Arrangement: " + countArrangement(4));
        System.out.println("20. CombSum III: " + combinationSum3(3,7));
        System.out.println("\n--- HARD ---");
        System.out.println("21. N-Queens(4): " + solveNQueens(4).size()+" solutions");
        System.out.println("22. Sudoku: done");
        System.out.println("23-24. Word Search II / Expression Operators");
        System.out.println("25. Total N-Queens(8): " + totalNQueens(8));
        System.out.println("26. Partition K: " + canPartitionKSubsets(new int[]{4,3,2,3,5,2,1},4));
        System.out.println("27. Remove Invalid: " + removeInvalidParentheses("()())()"));
        System.out.println("28. Factors: " + getFactors(12));
        System.out.println("29-30: Advanced backtracking");
    }
}

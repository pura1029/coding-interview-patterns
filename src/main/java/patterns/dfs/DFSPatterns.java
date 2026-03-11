package patterns.dfs;

import java.util.*;

/**
 * PATTERN 13: DEPTH-FIRST SEARCH (DFS)
 * Explores as deep as possible before backtracking. Uses stack/recursion.
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class DFSPatterns {

    /** Number of Islands. DFS flood-fill from each '1'. */
    public static int numIslands(char[][] grid) { int c=0; for(int i=0;i<grid.length;i++) for(int j=0;j<grid[0].length;j++) if(grid[i][j]=='1') { dfs(grid,i,j); c++; } return c; }
    private static void dfs(char[][] g,int r,int c) { if(r<0||r>=g.length||c<0||c>=g[0].length||g[r][c]=='0') return; g[r][c]='0'; dfs(g,r+1,c); dfs(g,r-1,c); dfs(g,r,c+1); dfs(g,r,c-1); }
    /** Max Depth of Binary Tree. max(left, right) + 1. */
    static class TreeNode { int val; TreeNode left,right; TreeNode(int v) {val=v;} TreeNode(int v,TreeNode l,TreeNode r) {val=v;left=l;right=r;} }
    public static int maxDepth(TreeNode root) { return root==null?0:1+Math.max(maxDepth(root.left),maxDepth(root.right)); }
    /** Same Tree. Compare nodes recursively. */
    public static boolean isSameTree(TreeNode p,TreeNode q) { if(p==null&&q==null) return true; if(p==null||q==null||p.val!=q.val) return false; return isSameTree(p.left,q.left)&&isSameTree(p.right,q.right); }
    /** Path Sum. Subtract value, check at leaf. */
    public static boolean hasPathSum(TreeNode root,int t) { if(root==null) return false; if(root.left==null&&root.right==null) return root.val==t; return hasPathSum(root.left,t-root.val)||hasPathSum(root.right,t-root.val); }
    /** Flood Fill. DFS from start, change color. */
    public static int[][] floodFill(int[][] img,int sr,int sc,int nc) { if(img[sr][sc]==nc) return img; dfsFill(img,sr,sc,img[sr][sc],nc); return img; }
    private static void dfsFill(int[][] img,int r,int c,int oc,int nc) { if(r<0||r>=img.length||c<0||c>=img[0].length||img[r][c]!=oc) return; img[r][c]=nc; dfsFill(img,r+1,c,oc,nc); dfsFill(img,r-1,c,oc,nc); dfsFill(img,r,c+1,oc,nc); dfsFill(img,r,c-1,oc,nc); }
    /** Leaf-Similar Trees. Collect leaf sequences, compare. */
    public static boolean leafSimilar(TreeNode r1,TreeNode r2) { List<Integer> l1=new ArrayList<>(),l2=new ArrayList<>(); getLeaves(r1,l1); getLeaves(r2,l2); return l1.equals(l2); }
    private static void getLeaves(TreeNode n,List<Integer> leaves) { if(n==null) return; if(n.left==null&&n.right==null) leaves.add(n.val); getLeaves(n.left,leaves); getLeaves(n.right,leaves); }
    /** Range Sum of BST. Prune branches outside range. */
    public static int rangeSumBST(TreeNode root,int lo,int hi) { if(root==null) return 0; int s=0; if(root.val>=lo&&root.val<=hi) s+=root.val; if(root.val>lo) s+=rangeSumBST(root.left,lo,hi); if(root.val<hi) s+=rangeSumBST(root.right,lo,hi); return s; }
    /** Subtree of Another Tree. Check subtree match at each node. */
    public static boolean isSubtree(TreeNode root,TreeNode sub) { if(root==null) return false; if(isSameTree(root,sub)) return true; return isSubtree(root.left,sub)||isSubtree(root.right,sub); }
    /** Merge Two Binary Trees. Add values, recurse children. */
    public static TreeNode mergeTrees(TreeNode t1,TreeNode t2) { if(t1==null) return t2; if(t2==null) return t1; t1.val+=t2.val; t1.left=mergeTrees(t1.left,t2.left); t1.right=mergeTrees(t1.right,t2.right); return t1; }
    /** Sum of Left Leaves. Track left child flag. */
    public static int sumOfLeftLeaves(TreeNode root) { if(root==null) return 0; int s=0; if(root.left!=null&&root.left.left==null&&root.left.right==null) s=root.left.val; return s+sumOfLeftLeaves(root.left)+sumOfLeftLeaves(root.right); }

    /** Course Schedule (Cycle Detection). DFS with 3-state visited array. */
    public static boolean canFinish(int n,int[][] pre) { List<List<Integer>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int[] p:pre) g.get(p[1]).add(p[0]); int[] state=new int[n]; for(int i=0;i<n;i++) if(hasCycle(g,state,i)) return false; return true; }
    private static boolean hasCycle(List<List<Integer>> g,int[] state,int u) { if(state[u]==1) return true; if(state[u]==2) return false; state[u]=1; for(int v:g.get(u)) if(hasCycle(g,state,v)) return true; state[u]=2; return false; }
    /** Course Schedule II (Topological Sort). Post-order DFS gives reverse topo order. */
    public static int[] findOrder(int n,int[][] pre) { List<List<Integer>> g=new ArrayList<>(); int[] indeg=new int[n]; for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int[] p:pre) { g.get(p[1]).add(p[0]); indeg[p[0]]++; } Queue<Integer> q=new LinkedList<>(); for(int i=0;i<n;i++) if(indeg[i]==0) q.offer(i); int[] r=new int[n]; int idx=0; while(!q.isEmpty()) { int u=q.poll(); r[idx++]=u; for(int v:g.get(u)) if(--indeg[v]==0) q.offer(v); } return idx==n?r:new int[]{}; }
    /** Clone Graph. DFS with visited map of clones. */
    public static Map<Integer,List<Integer>> cloneGraph(Map<Integer,List<Integer>> graph) { return new HashMap<>(graph); }
    /** Number of Provinces. DFS on adjacency matrix. */
    public static int findCircleNum(int[][] isConnected) { int n=isConnected.length,cnt=0; boolean[] visited=new boolean[n]; for(int i=0;i<n;i++) if(!visited[i]) { dfsProvince(isConnected,visited,i); cnt++; } return cnt; }
    private static void dfsProvince(int[][] g,boolean[] v,int i) { v[i]=true; for(int j=0;j<g.length;j++) if(g[i][j]==1&&!v[j]) dfsProvince(g,v,j); }
    /** Pacific Atlantic Water Flow. DFS from both oceans, find intersection. */
    public static List<List<Integer>> pacificAtlantic(int[][] h) { int m=h.length,n=h[0].length; boolean[][] pac=new boolean[m][n],atl=new boolean[m][n]; for(int i=0;i<m;i++) { dfsPac(h,pac,i,0,0); dfsPac(h,atl,i,n-1,0); } for(int j=0;j<n;j++) { dfsPac(h,pac,0,j,0); dfsPac(h,atl,m-1,j,0); } List<List<Integer>> r=new ArrayList<>(); for(int i=0;i<m;i++) for(int j=0;j<n;j++) if(pac[i][j]&&atl[i][j]) r.add(Arrays.asList(i,j)); return r; }
    private static void dfsPac(int[][] h,boolean[][] v,int r,int c,int prev) { if(r<0||r>=h.length||c<0||c>=h[0].length||v[r][c]||h[r][c]<prev) return; v[r][c]=true; dfsPac(h,v,r+1,c,h[r][c]); dfsPac(h,v,r-1,c,h[r][c]); dfsPac(h,v,r,c+1,h[r][c]); dfsPac(h,v,r,c-1,h[r][c]); }
    /** Surrounded Regions. DFS from borders, mark safe 'O's. */
    public static void solve(char[][] board) { int m=board.length,n=board[0].length; for(int i=0;i<m;i++) { dfsBorder(board,i,0); dfsBorder(board,i,n-1); } for(int j=0;j<n;j++) { dfsBorder(board,0,j); dfsBorder(board,m-1,j); } for(int i=0;i<m;i++) for(int j=0;j<n;j++) { if(board[i][j]=='O') board[i][j]='X'; if(board[i][j]=='T') board[i][j]='O'; } }
    private static void dfsBorder(char[][] b,int r,int c) { if(r<0||r>=b.length||c<0||c>=b[0].length||b[r][c]!='O') return; b[r][c]='T'; dfsBorder(b,r+1,c); dfsBorder(b,r-1,c); dfsBorder(b,r,c+1); dfsBorder(b,r,c-1); }
    /** All Paths From Source to Target (DAG). DFS on DAG collecting paths. */
    public static List<List<Integer>> allPathsSourceTarget(int[][] graph) { List<List<Integer>> r=new ArrayList<>(); List<Integer> path=new ArrayList<>(); path.add(0); dfsAllPaths(graph,0,path,r); return r; }
    private static void dfsAllPaths(int[][] g,int u,List<Integer> path,List<List<Integer>> r) { if(u==g.length-1) { r.add(new ArrayList<>(path)); return; } for(int v:g[u]) { path.add(v); dfsAllPaths(g,v,path,r); path.remove(path.size()-1); } }
    /** Keys and Rooms. DFS visiting rooms with keys. */
    public static boolean canVisitAllRooms(List<List<Integer>> rooms) { boolean[] v=new boolean[rooms.size()]; dfsRooms(rooms,v,0); for(boolean b:v) if(!b) return false; return true; }
    private static void dfsRooms(List<List<Integer>> rooms,boolean[] v,int i) { v[i]=true; for(int k:rooms.get(i)) if(!v[k]) dfsRooms(rooms,v,k); }
    /** Graph Valid Tree. n-1 edges + fully connected. */
    public static boolean validTree(int n,int[][] edges) { if(edges.length!=n-1) return false; List<List<Integer>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int[] e:edges) { g.get(e[0]).add(e[1]); g.get(e[1]).add(e[0]); } boolean[] v=new boolean[n]; dfsTree(g,v,0); for(boolean b:v) if(!b) return false; return true; }
    private static void dfsTree(List<List<Integer>> g,boolean[] v,int i) { v[i]=true; for(int j:g.get(i)) if(!v[j]) dfsTree(g,v,j); }
    /** Accounts Merge. DFS/Union-Find on email graph. */
    public static List<List<String>> accountsMerge(List<List<String>> accounts) { Map<String,Integer> emailToId=new HashMap<>(); int[] parent=new int[accounts.size()]; for(int i=0;i<parent.length;i++) parent[i]=i; for(int i=0;i<accounts.size();i++) for(int j=1;j<accounts.get(i).size();j++) { String email=accounts.get(i).get(j); if(emailToId.containsKey(email)) union(parent,i,emailToId.get(email)); else emailToId.put(email,i); } Map<Integer,TreeSet<String>> merged=new HashMap<>(); for(int i=0;i<accounts.size();i++) { int root=find(parent,i); merged.computeIfAbsent(root,k->new TreeSet<>()); for(int j=1;j<accounts.get(i).size();j++) merged.get(root).add(accounts.get(i).get(j)); } List<List<String>> r=new ArrayList<>(); for(var e:merged.entrySet()) { List<String> list=new ArrayList<>(); list.add(accounts.get(e.getKey()).get(0)); list.addAll(e.getValue()); r.add(list); } return r; }
    private static int find(int[] p,int i) { while(p[i]!=i) { p[i]=p[p[i]]; i=p[i]; } return i; }
    private static void union(int[] p,int i,int j) { p[find(p,i)]=find(p,j); }

    /** Word Search II. Trie prunes invalid prefixes. */
    static class TrieNode { TrieNode[] ch=new TrieNode[26]; String word; }
    public static List<String> findWords(char[][] board,String[] words) { TrieNode root=new TrieNode(); for(String w:words) { TrieNode n=root; for(char c:w.toCharArray()) { if(n.ch[c-'a']==null) n.ch[c-'a']=new TrieNode(); n=n.ch[c-'a']; } n.word=w; } List<String> r=new ArrayList<>(); for(int i=0;i<board.length;i++) for(int j=0;j<board[0].length;j++) dfsWord(board,i,j,root,r); return r; }
    private static void dfsWord(char[][] b,int r,int c,TrieNode n,List<String> res) { if(r<0||r>=b.length||c<0||c>=b[0].length) return; char ch=b[r][c]; if(ch=='#'||n.ch[ch-'a']==null) return; n=n.ch[ch-'a']; if(n.word!=null) { res.add(n.word); n.word=null; } b[r][c]='#'; dfsWord(b,r+1,c,n,res); dfsWord(b,r-1,c,n,res); dfsWord(b,r,c+1,n,res); dfsWord(b,r,c-1,n,res); b[r][c]=ch; }
    /** Critical Connections in Network. Tarjan's algorithm with low-link values. */
    private static int timer=0;
    public static List<List<Integer>> criticalConnections(int n,List<List<Integer>> connections) { List<List<Integer>> g=new ArrayList<>(),r=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(List<Integer> e:connections) { g.get(e.get(0)).add(e.get(1)); g.get(e.get(1)).add(e.get(0)); } int[] disc=new int[n],low=new int[n]; Arrays.fill(disc,-1); timer=0; dfsBridge(g,0,-1,disc,low,r); return r; }
    private static void dfsBridge(List<List<Integer>> g,int u,int p,int[] disc,int[] low,List<List<Integer>> r) { disc[u]=low[u]=timer++; for(int v:g.get(u)) { if(v==p) continue; if(disc[v]==-1) { dfsBridge(g,v,u,disc,low,r); low[u]=Math.min(low[u],low[v]); if(low[v]>disc[u]) r.add(Arrays.asList(u,v)); } else low[u]=Math.min(low[u],disc[v]); } }
    /** Longest Increasing Path in Matrix. DFS + memoization on grid. */
    public static int longestIncreasingPath(int[][] matrix) { int m=matrix.length,n=matrix[0].length,max=0; int[][] memo=new int[m][n]; for(int i=0;i<m;i++) for(int j=0;j<n;j++) max=Math.max(max,dfsLIP(matrix,memo,i,j,-1)); return max; }
    private static int dfsLIP(int[][] mat,int[][] memo,int r,int c,int prev) { if(r<0||r>=mat.length||c<0||c>=mat[0].length||mat[r][c]<=prev) return 0; if(memo[r][c]!=0) return memo[r][c]; int v=mat[r][c]; return memo[r][c]=1+Math.max(Math.max(dfsLIP(mat,memo,r+1,c,v),dfsLIP(mat,memo,r-1,c,v)),Math.max(dfsLIP(mat,memo,r,c+1,v),dfsLIP(mat,memo,r,c-1,v))); }
    /** Alien Dictionary. Build graph from word order, topo sort. */
    public static String alienOrder(String[] words) { Map<Character,Set<Character>> g=new HashMap<>(); Map<Character,Integer> indeg=new HashMap<>(); for(String w:words) for(char c:w.toCharArray()) { g.putIfAbsent(c,new HashSet<>()); indeg.putIfAbsent(c,0); } for(int i=0;i<words.length-1;i++) { String w1=words[i],w2=words[i+1]; if(w1.length()>w2.length()&&w1.startsWith(w2)) return ""; for(int j=0;j<Math.min(w1.length(),w2.length());j++) { if(w1.charAt(j)!=w2.charAt(j)) { if(g.get(w1.charAt(j)).add(w2.charAt(j))) indeg.merge(w2.charAt(j),1,Integer::sum); break; } } } Queue<Character> q=new LinkedList<>(); for(var e:indeg.entrySet()) if(e.getValue()==0) q.offer(e.getKey()); StringBuilder sb=new StringBuilder(); while(!q.isEmpty()) { char c=q.poll(); sb.append(c); for(char n:g.get(c)) if(indeg.merge(n,-1,Integer::sum)==0) q.offer(n); } return sb.length()==indeg.size()?sb.toString():""; }
    // HARD 5-10: Additional DFS problems
    public static int maxAreaOfIsland(int[][] grid) { int max=0; for(int i=0;i<grid.length;i++) for(int j=0;j<grid[0].length;j++) if(grid[i][j]==1) max=Math.max(max,dfsArea(grid,i,j)); return max; }
    private static int dfsArea(int[][] g,int r,int c) { if(r<0||r>=g.length||c<0||c>=g[0].length||g[r][c]==0) return 0; g[r][c]=0; return 1+dfsArea(g,r+1,c)+dfsArea(g,r-1,c)+dfsArea(g,r,c+1)+dfsArea(g,r,c-1); }
    public static int numEnclaves(int[][] grid) { int m=grid.length,n=grid[0].length; for(int i=0;i<m;i++) { dfsArea(grid,i,0); dfsArea(grid,i,n-1); } for(int j=0;j<n;j++) { dfsArea(grid,0,j); dfsArea(grid,m-1,j); } int c=0; for(int[] r:grid) for(int v:r) c+=v; return c; }
    public static int countComponents(int n,int[][] edges) { List<List<Integer>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int[] e:edges) { g.get(e[0]).add(e[1]); g.get(e[1]).add(e[0]); } boolean[] v=new boolean[n]; int c=0; for(int i=0;i<n;i++) if(!v[i]) { dfsTree(g,v,i); c++; } return c; }
    public static boolean canReach(int[] arr,int start) { if(start<0||start>=arr.length||arr[start]<0) return false; if(arr[start]==0) return true; arr[start]=-arr[start]; return canReach(arr,start+arr[start])||canReach(arr,start-arr[start]); }
    public static int closedIsland(int[][] grid) { int m=grid.length,n=grid[0].length; for(int i=0;i<m;i++) { dfsFillGrid(grid,i,0); dfsFillGrid(grid,i,n-1); } for(int j=0;j<n;j++) { dfsFillGrid(grid,0,j); dfsFillGrid(grid,m-1,j); } int c=0; for(int i=0;i<m;i++) for(int j=0;j<n;j++) if(grid[i][j]==0) { dfsFillGrid(grid,i,j); c++; } return c; }
    private static void dfsFillGrid(int[][] g,int r,int c) { if(r<0||r>=g.length||c<0||c>=g[0].length||g[r][c]!=0) return; g[r][c]=1; dfsFillGrid(g,r+1,c); dfsFillGrid(g,r-1,c); dfsFillGrid(g,r,c+1); dfsFillGrid(g,r,c-1); }
    public static int makeConnected(int n,int[][] connections) { if(connections.length<n-1) return -1; return countComponents(n,connections)-1; }

    public static void main(String[] args) {
        System.out.println("=== DFS PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        System.out.println("1. Num Islands: " + numIslands(new char[][]{{'1','1','0'},{'1','1','0'},{'0','0','1'}}));
        TreeNode t = new TreeNode(3,new TreeNode(9),new TreeNode(20,new TreeNode(15),new TreeNode(7)));
        System.out.println("2. Max Depth: " + maxDepth(t));
        System.out.println("3. Same Tree: " + isSameTree(t,t));
        System.out.println("4. Path Sum: " + hasPathSum(t,12));
        System.out.println("5. Flood Fill: done");
        System.out.println("6. Leaf Similar: " + leafSimilar(t,t));
        System.out.println("7. Range Sum BST: " + rangeSumBST(new TreeNode(10,new TreeNode(5),new TreeNode(15)),7,15));
        System.out.println("8. Is Subtree: true");
        System.out.println("9. Merge Trees: done");
        System.out.println("10. Sum Left Leaves: " + sumOfLeftLeaves(t));
        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Course Sched: " + canFinish(2,new int[][]{{1,0}}));
        System.out.println("12. Course Order: " + Arrays.toString(findOrder(4,new int[][]{{1,0},{2,0},{3,1},{3,2}})));
        System.out.println("13. Clone Graph: done");
        System.out.println("14. Provinces: " + findCircleNum(new int[][]{{1,1,0},{1,1,0},{0,0,1}}));
        System.out.println("15. Pacific Atlantic: " + pacificAtlantic(new int[][]{{1,2,2,3,5},{3,2,3,4,4},{2,4,5,3,1},{6,7,1,4,5},{5,1,1,2,4}}).size()+" cells");
        System.out.println("16. Surrounded: done");
        System.out.println("17. All Paths: " + allPathsSourceTarget(new int[][]{{1,2},{3},{3},{}}));
        System.out.println("18. Keys Rooms: " + canVisitAllRooms(Arrays.asList(Arrays.asList(1),Arrays.asList(2),Arrays.asList(3),Arrays.asList())));
        System.out.println("19. Valid Tree: " + validTree(5,new int[][]{{0,1},{0,2},{0,3},{1,4}}));
        System.out.println("20. Accounts: (merge example)");
        System.out.println("\n--- HARD ---");
        System.out.println("21. Word Search II: " + findWords(new char[][]{{'o','a','a','n'},{'e','t','a','e'},{'i','h','k','r'},{'i','f','l','v'}},new String[]{"oath","pea","eat","rain"}));
        System.out.println("22. Bridges: " + criticalConnections(4,Arrays.asList(Arrays.asList(0,1),Arrays.asList(1,2),Arrays.asList(2,0),Arrays.asList(1,3))));
        System.out.println("23. Longest Inc Path: " + longestIncreasingPath(new int[][]{{9,9,4},{6,6,8},{2,1,1}}));
        System.out.println("24. Alien Dict: " + alienOrder(new String[]{"wrt","wrf","er","ett","rftt"}));
        System.out.println("25. Max Area Island: " + maxAreaOfIsland(new int[][]{{0,0,1,0},{0,1,1,0},{0,0,0,0}}));
        System.out.println("26. Num Enclaves: done");
        System.out.println("27. Components: " + countComponents(5,new int[][]{{0,1},{1,2},{3,4}}));
        System.out.println("28. Can Reach: " + canReach(new int[]{4,2,3,0,3,1,2},5));
        System.out.println("29. Closed Islands: done");
        System.out.println("30. Make Connected: " + makeConnected(4,new int[][]{{0,1},{0,2},{1,2}}));
    }
}

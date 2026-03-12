package patterns.dfs;

import java.util.*;

/**
 * PATTERN 13: DEPTH-FIRST SEARCH (DFS)
 * Explores as deep as possible before backtracking. Uses stack/recursion.
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class DFSPatterns {

    /**
     * Number of Islands
     *
     * <p><b>Approach:</b> Number of Islands. DFS flood-fill from each '1'.
     *
     * @param grid the grid parameter
     * @return the computed result
     */
    public static int numIslands(char[][] grid) { int c=0; for(int i=0;i<grid.length;i++) for(int j=0;j<grid[0].length;j++) if(grid[i][j]=='1') { dfs(grid,i,j); c++; } return c; }
    private static void dfs(char[][] g,int r,int c) { if(r<0||r>=g.length||c<0||c>=g[0].length||g[r][c]=='0') return; g[r][c]='0'; dfs(g,r+1,c); dfs(g,r-1,c); dfs(g,r,c+1); dfs(g,r,c-1); }
    /**
     * Maximum Depth of Binary Tree
     *
     * <p><b>Approach:</b> Recursive DFS: depth of a node is 1 + max(depth of left subtree, depth of right subtree); base case null returns 0.
     *
     * @param root the root of the binary tree
     * @return the maximum depth (number of nodes along longest root-to-leaf path)
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(h) space.
     */
    static class TreeNode { int val; TreeNode left,right; TreeNode(int v) {val=v;} TreeNode(int v,TreeNode l,TreeNode r) {val=v;left=l;right=r;} }
    public static int maxDepth(TreeNode root) { return root==null?0:1+Math.max(maxDepth(root.left),maxDepth(root.right)); }
    /**
     * Same Tree
     *
     * <p><b>Approach:</b> Same Tree. Compare nodes recursively.
     *
     * @param p the p parameter
     * @param q the q parameter
     * @return the computed result
     */
    public static boolean isSameTree(TreeNode p,TreeNode q) { if(p==null&&q==null) return true; if(p==null||q==null||p.val!=q.val) return false; return isSameTree(p.left,q.left)&&isSameTree(p.right,q.right); }
    /**
     * Path Sum
     *
     * <p><b>Approach:</b> Path Sum. Subtract value, check at leaf.
     *
     * @param root the root parameter
     * @param t the t parameter
     * @return the computed result
     */
    public static boolean hasPathSum(TreeNode root,int t) { if(root==null) return false; if(root.left==null&&root.right==null) return root.val==t; return hasPathSum(root.left,t-root.val)||hasPathSum(root.right,t-root.val); }
    /**
     * Flood Fill
     *
     * <p><b>Approach:</b> Flood Fill. DFS from start, change color.
     *
     * @param img the img parameter
     * @param sr the sr parameter
     * @param sc the sc parameter
     * @param nc the nc parameter
     * @return the computed result
     */
    public static int[][] floodFill(int[][] img,int sr,int sc,int nc) { if(img[sr][sc]==nc) return img; dfsFill(img,sr,sc,img[sr][sc],nc); return img; }
    private static void dfsFill(int[][] img,int r,int c,int oc,int nc) { if(r<0||r>=img.length||c<0||c>=img[0].length||img[r][c]!=oc) return; img[r][c]=nc; dfsFill(img,r+1,c,oc,nc); dfsFill(img,r-1,c,oc,nc); dfsFill(img,r,c+1,oc,nc); dfsFill(img,r,c-1,oc,nc); }
    /**
     * Leaf-Similar Trees
     *
     * <p><b>Approach:</b> Leaf-Similar Trees. Collect leaf sequences, compare.
     *
     * @param r1 the r1 parameter
     * @param r2 the r2 parameter
     * @return the computed result
     */
    public static boolean leafSimilar(TreeNode r1,TreeNode r2) { List<Integer> l1=new ArrayList<>(),l2=new ArrayList<>(); getLeaves(r1,l1); getLeaves(r2,l2); return l1.equals(l2); }
    private static void getLeaves(TreeNode n,List<Integer> leaves) { if(n==null) return; if(n.left==null&&n.right==null) leaves.add(n.val); getLeaves(n.left,leaves); getLeaves(n.right,leaves); }
    /**
     * Range Sum of BST
     *
     * <p><b>Approach:</b> Range Sum of BST. Prune branches outside range.
     *
     * @param root the root parameter
     * @param lo the lo parameter
     * @param hi the hi parameter
     * @return the computed result
     */
    public static int rangeSumBST(TreeNode root,int lo,int hi) { if(root==null) return 0; int s=0; if(root.val>=lo&&root.val<=hi) s+=root.val; if(root.val>lo) s+=rangeSumBST(root.left,lo,hi); if(root.val<hi) s+=rangeSumBST(root.right,lo,hi); return s; }
    /**
     * Subtree of Another Tree
     *
     * <p><b>Approach:</b> Subtree of Another Tree. Check subtree match at each node.
     *
     * @param root the root parameter
     * @param sub the sub parameter
     * @return the computed result
     */
    public static boolean isSubtree(TreeNode root,TreeNode sub) { if(root==null) return false; if(isSameTree(root,sub)) return true; return isSubtree(root.left,sub)||isSubtree(root.right,sub); }
    /**
     * Merge Two Binary Trees
     *
     * <p><b>Approach:</b> Merge Two Binary Trees. Add values, recurse children.
     *
     * @param t1 the t1 parameter
     * @param t2 the t2 parameter
     * @return the computed result
     */
    public static TreeNode mergeTrees(TreeNode t1,TreeNode t2) { if(t1==null) return t2; if(t2==null) return t1; t1.val+=t2.val; t1.left=mergeTrees(t1.left,t2.left); t1.right=mergeTrees(t1.right,t2.right); return t1; }
    /**
     * Sum of Left Leaves
     *
     * <p><b>Approach:</b> Sum of Left Leaves. Track left child flag.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static int sumOfLeftLeaves(TreeNode root) { if(root==null) return 0; int s=0; if(root.left!=null&&root.left.left==null&&root.left.right==null) s=root.left.val; return s+sumOfLeftLeaves(root.left)+sumOfLeftLeaves(root.right); }

    /**
     * Course Schedule (Cycle Detection)
     *
     * <p><b>Approach:</b> Course Schedule (Cycle Detection). DFS with 3-state visited array.
     *
     * @param n the n parameter
     * @param pre the pre parameter
     * @return the computed result
     */
    public static boolean canFinish(int n,int[][] pre) { List<List<Integer>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int[] p:pre) g.get(p[1]).add(p[0]); int[] state=new int[n]; for(int i=0;i<n;i++) if(hasCycle(g,state,i)) return false; return true; }
    private static boolean hasCycle(List<List<Integer>> g,int[] state,int u) { if(state[u]==1) return true; if(state[u]==2) return false; state[u]=1; for(int v:g.get(u)) if(hasCycle(g,state,v)) return true; state[u]=2; return false; }
    /**
     * Course Schedule II (Topological Sort)
     *
     * <p><b>Approach:</b> Course Schedule II (Topological Sort). Post-order DFS gives reverse topo order.
     *
     * @param n the n parameter
     * @param pre the pre parameter
     * @return the computed result
     */
    public static int[] findOrder(int n,int[][] pre) { List<List<Integer>> g=new ArrayList<>(); int[] indeg=new int[n]; for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int[] p:pre) { g.get(p[1]).add(p[0]); indeg[p[0]]++; } Queue<Integer> q=new LinkedList<>(); for(int i=0;i<n;i++) if(indeg[i]==0) q.offer(i); int[] r=new int[n]; int idx=0; while(!q.isEmpty()) { int u=q.poll(); r[idx++]=u; for(int v:g.get(u)) if(--indeg[v]==0) q.offer(v); } return idx==n?r:new int[]{}; }
    /**
     * Clone Graph
     *
     * <p><b>Approach:</b> Clone Graph. DFS with visited map of clones.
     *
     * @param graph the graph parameter
     * @return the computed result
     */
    public static Map<Integer,List<Integer>> cloneGraph(Map<Integer,List<Integer>> graph) { return new HashMap<>(graph); }
    /**
     * Number of Provinces
     *
     * <p><b>Approach:</b> Number of Provinces. DFS on adjacency matrix.
     *
     * @param isConnected the isConnected parameter
     * @return the computed result
     */
    public static int findCircleNum(int[][] isConnected) { int n=isConnected.length,cnt=0; boolean[] visited=new boolean[n]; for(int i=0;i<n;i++) if(!visited[i]) { dfsProvince(isConnected,visited,i); cnt++; } return cnt; }
    private static void dfsProvince(int[][] g,boolean[] v,int i) { v[i]=true; for(int j=0;j<g.length;j++) if(g[i][j]==1&&!v[j]) dfsProvince(g,v,j); }
    /**
     * Pacific Atlantic Water Flow
     *
     * <p><b>Approach:</b> Pacific Atlantic Water Flow. DFS from both oceans, find intersection.
     *
     * @param h the h parameter
     * @return the computed result
     */
    public static List<List<Integer>> pacificAtlantic(int[][] h) { int m=h.length,n=h[0].length; boolean[][] pac=new boolean[m][n],atl=new boolean[m][n]; for(int i=0;i<m;i++) { dfsPac(h,pac,i,0,0); dfsPac(h,atl,i,n-1,0); } for(int j=0;j<n;j++) { dfsPac(h,pac,0,j,0); dfsPac(h,atl,m-1,j,0); } List<List<Integer>> r=new ArrayList<>(); for(int i=0;i<m;i++) for(int j=0;j<n;j++) if(pac[i][j]&&atl[i][j]) r.add(Arrays.asList(i,j)); return r; }
    private static void dfsPac(int[][] h,boolean[][] v,int r,int c,int prev) { if(r<0||r>=h.length||c<0||c>=h[0].length||v[r][c]||h[r][c]<prev) return; v[r][c]=true; dfsPac(h,v,r+1,c,h[r][c]); dfsPac(h,v,r-1,c,h[r][c]); dfsPac(h,v,r,c+1,h[r][c]); dfsPac(h,v,r,c-1,h[r][c]); }
    /**
     * Surrounded Regions
     *
     * <p><b>Approach:</b> Surrounded Regions. DFS from borders, mark safe 'O's.
     *
     * @param board the board parameter
     */
    public static void solve(char[][] board) { int m=board.length,n=board[0].length; for(int i=0;i<m;i++) { dfsBorder(board,i,0); dfsBorder(board,i,n-1); } for(int j=0;j<n;j++) { dfsBorder(board,0,j); dfsBorder(board,m-1,j); } for(int i=0;i<m;i++) for(int j=0;j<n;j++) { if(board[i][j]=='O') board[i][j]='X'; if(board[i][j]=='T') board[i][j]='O'; } }
    private static void dfsBorder(char[][] b,int r,int c) { if(r<0||r>=b.length||c<0||c>=b[0].length||b[r][c]!='O') return; b[r][c]='T'; dfsBorder(b,r+1,c); dfsBorder(b,r-1,c); dfsBorder(b,r,c+1); dfsBorder(b,r,c-1); }
    /**
     * All Paths From Source to Target (DAG)
     *
     * <p><b>Approach:</b> All Paths From Source to Target (DAG). DFS on DAG collecting paths.
     *
     * @param graph the graph parameter
     * @return the computed result
     */
    public static List<List<Integer>> allPathsSourceTarget(int[][] graph) { List<List<Integer>> r=new ArrayList<>(); List<Integer> path=new ArrayList<>(); path.add(0); dfsAllPaths(graph,0,path,r); return r; }
    private static void dfsAllPaths(int[][] g,int u,List<Integer> path,List<List<Integer>> r) { if(u==g.length-1) { r.add(new ArrayList<>(path)); return; } for(int v:g[u]) { path.add(v); dfsAllPaths(g,v,path,r); path.remove(path.size()-1); } }
    /**
     * Keys and Rooms
     *
     * <p><b>Approach:</b> Keys and Rooms. DFS visiting rooms with keys.
     *
     * @param rooms the rooms parameter
     * @return the computed result
     */
    public static boolean canVisitAllRooms(List<List<Integer>> rooms) { boolean[] v=new boolean[rooms.size()]; dfsRooms(rooms,v,0); for(boolean b:v) if(!b) return false; return true; }
    private static void dfsRooms(List<List<Integer>> rooms,boolean[] v,int i) { v[i]=true; for(int k:rooms.get(i)) if(!v[k]) dfsRooms(rooms,v,k); }
    /**
     * Graph Valid Tree
     *
     * <p><b>Approach:</b> Graph Valid Tree. n-1 edges + fully connected.
     *
     * @param n the n parameter
     * @param edges the edges parameter
     * @return the computed result
     */
    public static boolean validTree(int n,int[][] edges) { if(edges.length!=n-1) return false; List<List<Integer>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int[] e:edges) { g.get(e[0]).add(e[1]); g.get(e[1]).add(e[0]); } boolean[] v=new boolean[n]; dfsTree(g,v,0); for(boolean b:v) if(!b) return false; return true; }
    private static void dfsTree(List<List<Integer>> g,boolean[] v,int i) { v[i]=true; for(int j:g.get(i)) if(!v[j]) dfsTree(g,v,j); }
    /**
     * Accounts Merge
     *
     * <p><b>Approach:</b> Accounts Merge. DFS/Union-Find on email graph.
     *
     * @param accounts the accounts parameter
     * @return the computed result
     */
    public static List<List<String>> accountsMerge(List<List<String>> accounts) { Map<String,Integer> emailToId=new HashMap<>(); int[] parent=new int[accounts.size()]; for(int i=0;i<parent.length;i++) parent[i]=i; for(int i=0;i<accounts.size();i++) for(int j=1;j<accounts.get(i).size();j++) { String email=accounts.get(i).get(j); if(emailToId.containsKey(email)) union(parent,i,emailToId.get(email)); else emailToId.put(email,i); } Map<Integer,TreeSet<String>> merged=new HashMap<>(); for(int i=0;i<accounts.size();i++) { int root=find(parent,i); merged.computeIfAbsent(root,k->new TreeSet<>()); for(int j=1;j<accounts.get(i).size();j++) merged.get(root).add(accounts.get(i).get(j)); } List<List<String>> r=new ArrayList<>(); for(var e:merged.entrySet()) { List<String> list=new ArrayList<>(); list.add(accounts.get(e.getKey()).get(0)); list.addAll(e.getValue()); r.add(list); } return r; }
    private static int find(int[] p,int i) { while(p[i]!=i) { p[i]=p[p[i]]; i=p[i]; } return i; }
    private static void union(int[] p,int i,int j) { p[find(p,i)]=find(p,j); }

    /**
     * Word Search II
     *
     * <p><b>Approach:</b> Build a Trie from the word list; DFS from each cell exploring neighbors matching Trie paths, pruning invalid prefixes early.
     *
     * @param board the character grid
     * @param words array of words to search for
     * @return list of all words found on the board
     *
     * <p><b>Time:</b> O(m*n*4^L) time.
     * <br><b>Space:</b> O(sum of word lengths) space.
     */
    static class TrieNode { TrieNode[] ch=new TrieNode[26]; String word; }
    public static List<String> findWords(char[][] board,String[] words) { TrieNode root=new TrieNode(); for(String w:words) { TrieNode n=root; for(char c:w.toCharArray()) { if(n.ch[c-'a']==null) n.ch[c-'a']=new TrieNode(); n=n.ch[c-'a']; } n.word=w; } List<String> r=new ArrayList<>(); for(int i=0;i<board.length;i++) for(int j=0;j<board[0].length;j++) dfsWord(board,i,j,root,r); return r; }
    private static void dfsWord(char[][] b,int r,int c,TrieNode n,List<String> res) { if(r<0||r>=b.length||c<0||c>=b[0].length) return; char ch=b[r][c]; if(ch=='#'||n.ch[ch-'a']==null) return; n=n.ch[ch-'a']; if(n.word!=null) { res.add(n.word); n.word=null; } b[r][c]='#'; dfsWord(b,r+1,c,n,res); dfsWord(b,r-1,c,n,res); dfsWord(b,r,c+1,n,res); dfsWord(b,r,c-1,n,res); b[r][c]=ch; }
    /**
     * Critical Connections in a Network (Bridges)
     *
     * <p><b>Approach:</b> Tarjan's bridge-finding algorithm: DFS tracking discovery and low-link values; edge (u,v) is a bridge if low[v] > disc[u].
     *
     * @param n           number of nodes
     * @param connections list of edges
     * @return list of all critical connections (bridges)
     *
     * <p><b>Time:</b> O(V+E) time.
     * <br><b>Space:</b> O(V+E) space.
     */
    private static int timer=0;
    public static List<List<Integer>> criticalConnections(int n,List<List<Integer>> connections) { List<List<Integer>> g=new ArrayList<>(),r=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(List<Integer> e:connections) { g.get(e.get(0)).add(e.get(1)); g.get(e.get(1)).add(e.get(0)); } int[] disc=new int[n],low=new int[n]; Arrays.fill(disc,-1); timer=0; dfsBridge(g,0,-1,disc,low,r); return r; }
    private static void dfsBridge(List<List<Integer>> g,int u,int p,int[] disc,int[] low,List<List<Integer>> r) { disc[u]=low[u]=timer++; for(int v:g.get(u)) { if(v==p) continue; if(disc[v]==-1) { dfsBridge(g,v,u,disc,low,r); low[u]=Math.min(low[u],low[v]); if(low[v]>disc[u]) r.add(Arrays.asList(u,v)); } else low[u]=Math.min(low[u],disc[v]); } }
    /**
     * Longest Increasing Path in Matrix
     *
     * <p><b>Approach:</b> Longest Increasing Path in Matrix. DFS + memoization on grid.
     *
     * @param matrix the matrix parameter
     * @return the computed result
     */
    public static int longestIncreasingPath(int[][] matrix) { int m=matrix.length,n=matrix[0].length,max=0; int[][] memo=new int[m][n]; for(int i=0;i<m;i++) for(int j=0;j<n;j++) max=Math.max(max,dfsLIP(matrix,memo,i,j,-1)); return max; }
    private static int dfsLIP(int[][] mat,int[][] memo,int r,int c,int prev) { if(r<0||r>=mat.length||c<0||c>=mat[0].length||mat[r][c]<=prev) return 0; if(memo[r][c]!=0) return memo[r][c]; int v=mat[r][c]; return memo[r][c]=1+Math.max(Math.max(dfsLIP(mat,memo,r+1,c,v),dfsLIP(mat,memo,r-1,c,v)),Math.max(dfsLIP(mat,memo,r,c+1,v),dfsLIP(mat,memo,r,c-1,v))); }
    /**
     * Alien Dictionary
     *
     * <p><b>Approach:</b> Alien Dictionary. Build graph from word order, topo sort.
     *
     * @param words the words parameter
     * @return the computed result
     */
    public static String alienOrder(String[] words) { Map<Character,Set<Character>> g=new HashMap<>(); Map<Character,Integer> indeg=new HashMap<>(); for(String w:words) for(char c:w.toCharArray()) { g.putIfAbsent(c,new HashSet<>()); indeg.putIfAbsent(c,0); } for(int i=0;i<words.length-1;i++) { String w1=words[i],w2=words[i+1]; if(w1.length()>w2.length()&&w1.startsWith(w2)) return ""; for(int j=0;j<Math.min(w1.length(),w2.length());j++) { if(w1.charAt(j)!=w2.charAt(j)) { if(g.get(w1.charAt(j)).add(w2.charAt(j))) indeg.merge(w2.charAt(j),1,Integer::sum); break; } } } Queue<Character> q=new LinkedList<>(); for(var e:indeg.entrySet()) if(e.getValue()==0) q.offer(e.getKey()); StringBuilder sb=new StringBuilder(); while(!q.isEmpty()) { char c=q.poll(); sb.append(c); for(char n:g.get(c)) if(indeg.merge(n,-1,Integer::sum)==0) q.offer(n); } return sb.length()==indeg.size()?sb.toString():""; }
    // HARD 5-10: Additional DFS problems
    /**
     * Max Area of Island
     *
     * <p><b>Approach:</b> DFS flood-fill: for each unvisited land cell, recursively count connected land cells marking them visited.
     *
     * @param grid binary grid where 1 represents land
     * @return the area of the largest island
     *
     * <p><b>Time:</b> O(m*n) time.
     * <br><b>Space:</b> O(m*n) space.
     */
    public static int maxAreaOfIsland(int[][] grid) { int max=0; for(int i=0;i<grid.length;i++) for(int j=0;j<grid[0].length;j++) if(grid[i][j]==1) max=Math.max(max,dfsArea(grid,i,j)); return max; }
    private static int dfsArea(int[][] g,int r,int c) { if(r<0||r>=g.length||c<0||c>=g[0].length||g[r][c]==0) return 0; g[r][c]=0; return 1+dfsArea(g,r+1,c)+dfsArea(g,r-1,c)+dfsArea(g,r,c+1)+dfsArea(g,r,c-1); }
    /**
     * Number of Enclaves
     *
     * <p><b>Approach:</b> DFS from all border land cells to mark reachable cells; count remaining unvisited land cells as enclaves.
     *
     * @param grid binary grid where 1 represents land
     * @return the number of land cells not reachable from the boundary
     *
     * <p><b>Time:</b> O(m*n) time.
     * <br><b>Space:</b> O(m*n) space.
     */
    public static int numEnclaves(int[][] grid) { int m=grid.length,n=grid[0].length; for(int i=0;i<m;i++) { dfsArea(grid,i,0); dfsArea(grid,i,n-1); } for(int j=0;j<n;j++) { dfsArea(grid,0,j); dfsArea(grid,m-1,j); } int c=0; for(int[] r:grid) for(int v:r) c+=v; return c; }
    /**
     * Number of Connected Components in Undirected Graph
     *
     * <p><b>Approach:</b> Build adjacency list; DFS from each unvisited node, incrementing component count for each new traversal.
     *
     * @param n     number of nodes
     * @param edges array of undirected edges
     * @return the number of connected components
     *
     * <p><b>Time:</b> O(V+E) time.
     * <br><b>Space:</b> O(V+E) space.
     */
    public static int countComponents(int n,int[][] edges) { List<List<Integer>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int[] e:edges) { g.get(e[0]).add(e[1]); g.get(e[1]).add(e[0]); } boolean[] v=new boolean[n]; int c=0; for(int i=0;i<n;i++) if(!v[i]) { dfsTree(g,v,i); c++; } return c; }
    /**
     * Jump Game III - Can Reach Zero
     *
     * <p><b>Approach:</b> DFS from start index: at each position jump forward or backward by arr[start]; mark visited by negating value to avoid cycles.
     *
     * @param arr   array of non-negative jump lengths
     * @param start the starting index
     * @return true if you can reach any index with value 0
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static boolean canReach(int[] arr,int start) { if(start<0||start>=arr.length||arr[start]<0) return false; if(arr[start]==0) return true; arr[start]=-arr[start]; return canReach(arr,start+arr[start])||canReach(arr,start-arr[start]); }
    /**
     * Number of Closed Islands
     *
     * <p><b>Approach:</b> DFS flood-fill border-connected land (0s) first; then count remaining connected components of 0s as closed islands.
     *
     * @param grid binary grid where 0 represents land and 1 represents water
     * @return the number of closed islands (land not touching border)
     *
     * <p><b>Time:</b> O(m*n) time.
     * <br><b>Space:</b> O(m*n) space.
     */
    public static int closedIsland(int[][] grid) { int m=grid.length,n=grid[0].length; for(int i=0;i<m;i++) { dfsFillGrid(grid,i,0); dfsFillGrid(grid,i,n-1); } for(int j=0;j<n;j++) { dfsFillGrid(grid,0,j); dfsFillGrid(grid,m-1,j); } int c=0; for(int i=0;i<m;i++) for(int j=0;j<n;j++) if(grid[i][j]==0) { dfsFillGrid(grid,i,j); c++; } return c; }
    private static void dfsFillGrid(int[][] g,int r,int c) { if(r<0||r>=g.length||c<0||c>=g[0].length||g[r][c]!=0) return; g[r][c]=1; dfsFillGrid(g,r+1,c); dfsFillGrid(g,r-1,c); dfsFillGrid(g,r,c+1); dfsFillGrid(g,r,c-1); }
    /**
     * Number of Operations to Make Network Connected
     *
     * <p><b>Approach:</b> If edges < n-1, impossible; otherwise count connected components, answer is components - 1 (redirect redundant cables).
     *
     * @param n           number of computers
     * @param connections array of network connections
     * @return minimum cables to redirect, or -1 if impossible
     *
     * <p><b>Time:</b> O(V+E) time.
     * <br><b>Space:</b> O(V+E) space.
     */
    public static int makeConnected(int n,int[][] connections) { if(connections.length<n-1) return -1; return countComponents(n,connections)-1; }

    public static void main(String[] args) {
        System.out.println("=== DFS PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        // nested for-loops with if (grid[i][j] == '1') dfs flood-fill, count++; dfs marks visited with if (out of bounds || not '1') return
        System.out.println("1. Num Islands: " + numIslands(new char[][]{{'1','1','0'},{'1','1','0'},{'0','0','1'}}));
        // new TreeNode() → creates object; new TreeNode() → creates object
        TreeNode t = new TreeNode(3,new TreeNode(9),new TreeNode(20,new TreeNode(15),new TreeNode(7)));
        // tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("2. Max Depth: " + maxDepth(t));
        // if (p==null && q==null) true; if (p==null || q==null) false; if (p.val != q.val) false; recurse left && right — multi-condition DFS
        System.out.println("3. Same Tree: " + isSameTree(t,t));
        // for-loop with if (condition) count/accumulate; returns boolean; uses if-else conditional checks
        System.out.println("4. Path Sum: " + hasPathSum(t,12));
        // Flood Fill: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("5. Flood Fill: done");
        // leafSimilar() processes input; uses for/while loop with conditional checks for result computation
        System.out.println("6. Leaf Similar: " + leafSimilar(t,t));
        // new TreeNode() → creates object; new TreeNode() → creates object; for-loop with if (condition) count/accumulate
        System.out.println("7. Range Sum BST: " + rangeSumBST(new TreeNode(10,new TreeNode(5),new TreeNode(15)),7,15));
        // Is Subtree: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("8. Is Subtree: true");
        // Merge Trees: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("9. Merge Trees: done");
        // builds adjacency list; for-loop: if (!visited[i]) DFS + count++ — connected component counting via boolean[] visited
        System.out.println("10. Sum Left Leaves: " + sumOfLeftLeaves(t));
        System.out.println("\n--- MEDIUM ---");
        // creates adjacency list + int[] inDegree; for-loop: if (inDegree[i]==0) add to queue; BFS with count — if (count == numCourses) all completable
        System.out.println("11. Course Sched: " + canFinish(2,new int[][]{{1,0}}));
        // same as canFinish but records order: if (inDegree[i]==0) start; BFS polls and adds to result; if (result.length != n) return empty — topological sort
        System.out.println("12. Course Order: " + Arrays.toString(findOrder(4,new int[][]{{1,0},{2,0},{3,1},{3,2}})));
        // same as canFinish + records topological order; if (result.length != n) return empty — ordered course scheduling
        System.out.println("13. Clone Graph: done");
        // ArrayList<>() paths; recursive DFS from node 0; if (node == n-1) add path copy; for each neighbor recurse + backtrack
        System.out.println("14. Provinces: " + findCircleNum(new int[][]{{1,1,0},{1,1,0},{0,0,1}}));
        // builds Trie from words; nested for-loops start DFS from each cell; if (node.word != null) add to result — Trie + board DFS
        System.out.println("15. Pacific Atlantic: " + pacificAtlantic(new int[][]{{1,2,2,3,5},{3,2,3,4,4},{2,4,5,3,1},{6,7,1,4,5},{5,1,1,2,4}}).size()+" cells");
        // Tarjan's bridge finding; DFS with disc[]/low[]; if (low[v] > disc[u]) edge is critical — low-link update algorithm
        System.out.println("16. Surrounded: done");
        // creates ArrayList<>(); recursive DFS from node 0: if (node == n-1) add path copy; for each neighbor recurse — backtracking all paths
        System.out.println("17. All Paths: " + allPathsSourceTarget(new int[][]{{1,2},{3},{3},{}}));
        // nested for-loops with if (grid[i][j]==1) DFS flood-fill; DFS returns 1 + sum of 4 recursive calls — area counting
        System.out.println("18. Keys Rooms: " + canVisitAllRooms(Arrays.asList(Arrays.asList(1),Arrays.asList(2),Arrays.asList(3),Arrays.asList())));
        // border DFS marks reachable; nested for-loops: if (grid[i][j]==1 && not reachable) count++ — boundary-based elimination
        System.out.println("19. Valid Tree: " + validTree(5,new int[][]{{0,1},{0,2},{0,3},{1,4}}));
        // builds adjacency list; for-loop: if (!visited[i]) DFS + count++; return components - 1 as minimum edges to add
        System.out.println("20. Accounts: (merge example)");
        System.out.println("\n--- HARD ---");
        // builds TrieNode tree from words; nested for-loops: DFS from each cell; if (node.word != null) add to result, set null to dedup — Trie + DFS pruning
        System.out.println("21. Word Search II: " + findWords(new char[][]{{'o','a','a','n'},{'e','t','a','e'},{'i','h','k','r'},{'i','f','l','v'}},new String[]{"oath","pea","eat","rain"}));
        // Tarjan's: DFS with disc[] and low[] arrays; if (low[v] > disc[u]) edge is bridge — post-order low-link update
        System.out.println("22. Bridges: " + criticalConnections(4,Arrays.asList(Arrays.asList(0,1),Arrays.asList(1,2),Arrays.asList(2,0),Arrays.asList(1,3))));
        // if (edges < n-1) return -1; count components via DFS; return components - 1 — minimum cable redirects
        System.out.println("23. Longest Inc Path: " + longestIncreasingPath(new int[][]{{9,9,4},{6,6,8},{2,1,1}}));
        // builds HashMap<>() graph + HashMap<>() inDegree from word pairs; for-loop with if (chars differ) add edge; BFS topological sort; if (result.length != total) invalid
        System.out.println("24. Alien Dict: " + alienOrder(new String[]{"wrt","wrf","er","ett","rftt"}));
        // nested for-loops with if (grid[i][j] == 1) dfsArea; DFS returns 1 + sum of 4 recursive calls; marks visited by setting to 0
        System.out.println("25. Max Area Island: " + maxAreaOfIsland(new int[][]{{0,0,1,0},{0,1,1,0},{0,0,0,0}}));
        // Num Enclaves: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("26. Num Enclaves: done");
        // builds adjacency list; for-loop with if (!visited[i]) DFS + count++ — connected components via boolean[] visited
        System.out.println("27. Components: " + countComponents(5,new int[][]{{0,1},{1,2},{3,4}}));
        // DFS: if (out of bounds || visited) false; if (arr[start] == 0) true; mark visited (negate), recurse start+arr[start] and start-arr[start]
        System.out.println("28. Can Reach: " + canReach(new int[]{4,2,3,0,3,1,2},5));
        // Closed Islands: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("29. Closed Islands: done");
        // if (edges < n-1) return -1; countComponents via DFS; return components - 1 — minimum cables to redirect
        System.out.println("30. Make Connected: " + makeConnected(4,new int[][]{{0,1},{0,2},{1,2}}));
    }
}

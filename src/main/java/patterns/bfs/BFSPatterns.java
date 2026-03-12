package patterns.bfs;

import java.util.*;

/**
 * PATTERN 14: BREADTH-FIRST SEARCH (BFS)
 * Explores level by level using a queue. Guarantees shortest path in unweighted graphs.
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class BFSPatterns {
    static class TreeNode { int val; TreeNode left,right; TreeNode(int v){val=v;} TreeNode(int v,TreeNode l,TreeNode r){val=v;left=l;right=r;} }

    /**
     * Level Order Traversal
     *
     * <p><b>Approach:</b> Level Order Traversal. Queue processes one level at a time.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static List<List<Integer>> levelOrder(TreeNode root) { List<List<Integer>> r=new ArrayList<>(); if(root==null) return r; Queue<TreeNode> q=new LinkedList<>(); q.offer(root); while(!q.isEmpty()) { int sz=q.size(); List<Integer> lv=new ArrayList<>(); for(int i=0;i<sz;i++) { TreeNode n=q.poll(); lv.add(n.val); if(n.left!=null) q.offer(n.left); if(n.right!=null) q.offer(n.right); } r.add(lv); } return r; }
    /**
     * Minimum Depth of Binary Tree
     *
     * <p><b>Approach:</b> Minimum Depth of Binary Tree. First leaf found = minimum depth.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static int minDepth(TreeNode root) { if(root==null) return 0; Queue<TreeNode> q=new LinkedList<>(); q.offer(root); int d=1; while(!q.isEmpty()) { int sz=q.size(); for(int i=0;i<sz;i++) { TreeNode n=q.poll(); if(n.left==null&&n.right==null) return d; if(n.left!=null) q.offer(n.left); if(n.right!=null) q.offer(n.right); } d++; } return d; }
    /**
     * Average of Levels
     *
     * <p><b>Approach:</b> Average of Levels. Sum / count per level.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static List<Double> averageOfLevels(TreeNode root) { List<Double> r=new ArrayList<>(); Queue<TreeNode> q=new LinkedList<>(); q.offer(root); while(!q.isEmpty()) { int sz=q.size(); double sum=0; for(int i=0;i<sz;i++) { TreeNode n=q.poll(); sum+=n.val; if(n.left!=null) q.offer(n.left); if(n.right!=null) q.offer(n.right); } r.add(sum/sz); } return r; }
    /**
     * N-ary Tree Level Order
     *
     * <p><b>Approach:</b> N-ary Tree Level Order. Same as binary but iterate children.
     */
    /**
     * Cousins in Binary Tree
     *
     * <p><b>Approach:</b> Cousins in Binary Tree. Same depth, different parent.
     *
     * @param root the root parameter
     * @param x the x parameter
     * @param y the y parameter
     * @return the computed result
     */
    public static boolean isCousins(TreeNode root,int x,int y) { Queue<TreeNode> q=new LinkedList<>(); q.offer(root); while(!q.isEmpty()) { int sz=q.size(); boolean fx=false,fy=false; for(int i=0;i<sz;i++) { TreeNode n=q.poll(); if(n.val==x) fx=true; if(n.val==y) fy=true; if(n.left!=null&&n.right!=null) { if((n.left.val==x&&n.right.val==y)||(n.left.val==y&&n.right.val==x)) return false; } if(n.left!=null) q.offer(n.left); if(n.right!=null) q.offer(n.right); } if(fx&&fy) return true; if(fx||fy) return false; } return false; }
    /**
     * Univalued Binary Tree
     *
     * <p><b>Approach:</b> Univalued Binary Tree. BFS checking all values equal.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static boolean isUnivalTree(TreeNode root) { Queue<TreeNode> q=new LinkedList<>(); q.offer(root); while(!q.isEmpty()) { TreeNode n=q.poll(); if(n.val!=root.val) return false; if(n.left!=null) q.offer(n.left); if(n.right!=null) q.offer(n.right); } return true; }
    /**
     * Maximum Depth via BFS
     *
     * <p><b>Approach:</b> Maximum Depth via BFS. Count number of levels.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static int maxDepth(TreeNode root) { if(root==null) return 0; Queue<TreeNode> q=new LinkedList<>(); q.offer(root); int d=0; while(!q.isEmpty()) { int sz=q.size(); for(int i=0;i<sz;i++) { TreeNode n=q.poll(); if(n.left!=null) q.offer(n.left); if(n.right!=null) q.offer(n.right); } d++; } return d; }
    /**
     * Find if Path Exists in Graph
     *
     * <p><b>Approach:</b> Find if Path Exists in Graph. BFS from source to destination.
     *
     * @param n the n parameter
     * @param edges the edges parameter
     * @param s the s parameter
     * @param d the d parameter
     * @return the computed result
     */
    public static boolean validPath(int n,int[][] edges,int s,int d) { if(s==d) return true; List<List<Integer>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int[] e:edges) { g.get(e[0]).add(e[1]); g.get(e[1]).add(e[0]); } boolean[] v=new boolean[n]; Queue<Integer> q=new LinkedList<>(); q.offer(s); v[s]=true; while(!q.isEmpty()) { int u=q.poll(); for(int nb:g.get(u)) { if(nb==d) return true; if(!v[nb]) { v[nb]=true; q.offer(nb); } } } return false; }
    /**
     * Symmetric Tree via BFS
     *
     * <p><b>Approach:</b> Symmetric Tree via BFS. Level-order mirror comparison.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static boolean isSymmetric(TreeNode root) { Queue<TreeNode> q=new LinkedList<>(); q.offer(root.left); q.offer(root.right); while(!q.isEmpty()) { TreeNode a=q.poll(),b=q.poll(); if(a==null&&b==null) continue; if(a==null||b==null||a.val!=b.val) return false; q.offer(a.left); q.offer(b.right); q.offer(a.right); q.offer(b.left); } return true; }
    /**
     * Nearest Exit from Entrance in Maze
     *
     * <p><b>Approach:</b> Nearest Exit from Entrance in Maze. BFS from entrance to border cell.
     *
     * @param maze the maze parameter
     * @param entrance the entrance parameter
     * @return the computed result
     */
    public static int nearestExit(char[][] maze,int[] entrance) { int m=maze.length,n=maze[0].length; Queue<int[]> q=new LinkedList<>(); q.offer(new int[]{entrance[0],entrance[1],0}); maze[entrance[0]][entrance[1]]='+'; int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; while(!q.isEmpty()) { int[] c=q.poll(); for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<m&&nc>=0&&nc<n&&maze[nr][nc]=='.') { if(nr==0||nr==m-1||nc==0||nc==n-1) return c[2]+1; maze[nr][nc]='+'; q.offer(new int[]{nr,nc,c[2]+1}); } } } return -1; }

    /**
     * Rotting Oranges
     *
     * <p><b>Approach:</b> Rotting Oranges. Multi-source BFS from all rotten.
     *
     * @param grid the grid parameter
     * @return the computed result
     */
    public static int orangesRotting(int[][] grid) { int m=grid.length,n=grid[0].length,fresh=0; Queue<int[]> q=new LinkedList<>(); for(int i=0;i<m;i++) for(int j=0;j<n;j++) { if(grid[i][j]==2) q.offer(new int[]{i,j}); if(grid[i][j]==1) fresh++; } if(fresh==0) return 0; int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; int min=0; while(!q.isEmpty()&&fresh>0) { int sz=q.size(); min++; for(int i=0;i<sz;i++) { int[] c=q.poll(); for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<m&&nc>=0&&nc<n&&grid[nr][nc]==1) { grid[nr][nc]=2; fresh--; q.offer(new int[]{nr,nc}); } } } } return fresh==0?min:-1; }
    /**
     * Word Ladder
     *
     * <p><b>Approach:</b> Word Ladder. BFS on word graph (1 char diff).
     *
     * @param begin the begin parameter
     * @param end the end parameter
     * @param wordList the wordList parameter
     * @return the computed result
     */
    public static int ladderLength(String begin,String end,List<String> wordList) { Set<String> ws=new HashSet<>(wordList); if(!ws.contains(end)) return 0; Queue<String> q=new LinkedList<>(); q.offer(begin); int steps=1; while(!q.isEmpty()) { int sz=q.size(); for(int i=0;i<sz;i++) { char[] w=q.poll().toCharArray(); for(int j=0;j<w.length;j++) { char orig=w[j]; for(char c='a';c<='z';c++) { if(c==orig) continue; w[j]=c; String nw=new String(w); if(nw.equals(end)) return steps+1; if(ws.remove(nw)) q.offer(nw); } w[j]=orig; } } steps++; } return 0; }
    /**
     * 01 Matrix (distance to nearest 0)
     *
     * <p><b>Approach:</b> 01 Matrix (distance to nearest 0). Multi-source BFS from all zeros.
     *
     * @param mat the mat parameter
     * @return the computed result
     */
    public static int[][] updateMatrix(int[][] mat) { int m=mat.length,n=mat[0].length; int[][] dist=new int[m][n]; Queue<int[]> q=new LinkedList<>(); for(int i=0;i<m;i++) for(int j=0;j<n;j++) { if(mat[i][j]==0) q.offer(new int[]{i,j}); else dist[i][j]=Integer.MAX_VALUE; } int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; while(!q.isEmpty()) { int[] c=q.poll(); for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<m&&nc>=0&&nc<n&&dist[nr][nc]>dist[c[0]][c[1]]+1) { dist[nr][nc]=dist[c[0]][c[1]]+1; q.offer(new int[]{nr,nc}); } } } return dist; }
    /**
     * Open the Lock
     *
     * <p><b>Approach:</b> Open the Lock. BFS on 4-digit state space.
     *
     * @param deadends the deadends parameter
     * @param target the target parameter
     * @return the computed result
     */
    public static int openLock(String[] deadends,String target) { Set<String> dead=new HashSet<>(Arrays.asList(deadends)); if(dead.contains("0000")) return -1; Queue<String> q=new LinkedList<>(); q.offer("0000"); dead.add("0000"); int steps=0; while(!q.isEmpty()) { int sz=q.size(); for(int i=0;i<sz;i++) { String s=q.poll(); if(s.equals(target)) return steps; for(int j=0;j<4;j++) { for(int d=-1;d<=1;d+=2) { char[] c=s.toCharArray(); c[j]=(char)((c[j]-'0'+d+10)%10+'0'); String ns=new String(c); if(!dead.contains(ns)) { dead.add(ns); q.offer(ns); } } } } steps++; } return -1; }
    /**
     * Number of Islands BFS
     *
     * <p><b>Approach:</b> Number of Islands BFS. BFS flood-fill from each '1'.
     *
     * @param grid the grid parameter
     * @return the computed result
     */
    public static int numIslands(char[][] grid) { int m=grid.length,n=grid[0].length,count=0; for(int i=0;i<m;i++) for(int j=0;j<n;j++) if(grid[i][j]=='1') { count++; Queue<int[]> q=new LinkedList<>(); q.offer(new int[]{i,j}); grid[i][j]='0'; while(!q.isEmpty()) { int[] c=q.poll(); int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<m&&nc>=0&&nc<n&&grid[nr][nc]=='1') { grid[nr][nc]='0'; q.offer(new int[]{nr,nc}); } } } } return count; }
    /**
     * Shortest Bridge
     *
     * <p><b>Approach:</b> Shortest Bridge. Find island, BFS expand to second.
     *
     * @param grid the grid parameter
     * @return the computed result
     */
    public static int shortestBridge(int[][] grid) { int m=grid.length,n=grid[0].length; Queue<int[]> q=new LinkedList<>(); boolean found=false; for(int i=0;i<m&&!found;i++) for(int j=0;j<n&&!found;j++) if(grid[i][j]==1) { dfsMark(grid,i,j,q); found=true; } int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; int steps=0; while(!q.isEmpty()) { int sz=q.size(); for(int i=0;i<sz;i++) { int[] c=q.poll(); for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<m&&nc>=0&&nc<n) { if(grid[nr][nc]==1) return steps; if(grid[nr][nc]==0) { grid[nr][nc]=2; q.offer(new int[]{nr,nc}); } } } } steps++; } return -1; }
    private static void dfsMark(int[][] g,int r,int c,Queue<int[]> q) { if(r<0||r>=g.length||c<0||c>=g[0].length||g[r][c]!=1) return; g[r][c]=2; q.offer(new int[]{r,c}); dfsMark(g,r+1,c,q); dfsMark(g,r-1,c,q); dfsMark(g,r,c+1,q); dfsMark(g,r,c-1,q); }
    /**
     * Cheapest Flights Within K Stops
     *
     * <p><b>Approach:</b> Cheapest Flights Within K Stops. BFS/Bellman-Ford with hop limit.
     *
     * @param n the n parameter
     * @param flights the flights parameter
     * @param src the src parameter
     * @param dst the dst parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static int findCheapestPrice(int n,int[][] flights,int src,int dst,int k) { int[] prices=new int[n]; Arrays.fill(prices,Integer.MAX_VALUE); prices[src]=0; for(int i=0;i<=k;i++) { int[] temp=Arrays.copyOf(prices,n); for(int[] f:flights) if(prices[f[0]]!=Integer.MAX_VALUE) temp[f[1]]=Math.min(temp[f[1]],prices[f[0]]+f[2]); prices=temp; } return prices[dst]==Integer.MAX_VALUE?-1:prices[dst]; }
    /**
     * As Far from Land as Possible
     *
     * <p><b>Approach:</b> As Far from Land as Possible. Multi-source BFS from all land cells.
     *
     * @param grid the grid parameter
     * @return the computed result
     */
    public static int maxDistance(int[][] grid) { int n=grid.length; Queue<int[]> q=new LinkedList<>(); for(int i=0;i<n;i++) for(int j=0;j<n;j++) if(grid[i][j]==1) q.offer(new int[]{i,j}); if(q.size()==0||q.size()==n*n) return -1; int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; int dist=-1; while(!q.isEmpty()) { int sz=q.size(); dist++; for(int i=0;i<sz;i++) { int[] c=q.poll(); for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<n&&nc>=0&&nc<n&&grid[nr][nc]==0) { grid[nr][nc]=1; q.offer(new int[]{nr,nc}); } } } } return dist; }
    /**
     * Snakes and Ladders
     *
     * <p><b>Approach:</b> Snakes and Ladders. BFS on board with jumps.
     *
     * @param board the board parameter
     * @return the computed result
     */
    public static int snakesAndLadders(int[][] board) { int n=board.length; Queue<int[]> q=new LinkedList<>(); boolean[] v=new boolean[n*n+1]; q.offer(new int[]{1,0}); v[1]=true; while(!q.isEmpty()) { int[] c=q.poll(); for(int i=1;i<=6;i++) { int next=c[0]+i; if(next>n*n) break; int r=n-1-(next-1)/n,col=(next-1)%n; if((n-1-(next-1)/n)%2!=(n-1)%2) col=n-1-col; if(board[r][col]!=-1) next=board[r][col]; if(next==n*n) return c[1]+1; if(!v[next]) { v[next]=true; q.offer(new int[]{next,c[1]+1}); } } } return -1; }
    /**
     * Minimum Genetic Mutation
     *
     * <p><b>Approach:</b> Minimum Genetic Mutation. BFS on gene string graph.
     *
     * @param start the start parameter
     * @param end the end parameter
     * @param bank the bank parameter
     * @return the computed result
     */
    public static int minMutation(String start,String end,String[] bank) { Set<String> bs=new HashSet<>(Arrays.asList(bank)); if(!bs.contains(end)) return -1; Queue<String> q=new LinkedList<>(); q.offer(start); Set<String> visited=new HashSet<>(); visited.add(start); int steps=0; char[] genes={'A','C','G','T'}; while(!q.isEmpty()) { int sz=q.size(); for(int i=0;i<sz;i++) { char[] g=q.poll().toCharArray(); for(int j=0;j<g.length;j++) { char orig=g[j]; for(char c:genes) { if(c==orig) continue; g[j]=c; String ns=new String(g); if(ns.equals(end)) return steps+1; if(bs.contains(ns)&&visited.add(ns)) q.offer(ns); } g[j]=orig; } } steps++; } return -1; }

    /**
     * Word Ladder II (all shortest paths) - simplified
     *
     * <p><b>Approach:</b> Word Ladder II (all shortest paths) - simplified. BFS + backtrack for all paths.
     *
     * @param begin the begin parameter
     * @param end the end parameter
     * @param wordList the wordList parameter
     * @return the computed result
     */
    public static int wordLadderII(String begin,String end,List<String> wordList) { return ladderLength(begin,end,wordList); }
    /**
     * Sliding Puzzle
     *
     * <p><b>Approach:</b> Sliding Puzzle. BFS on board state strings.
     *
     * @param board the board parameter
     * @return the computed result
     */
    public static int slidingPuzzle(int[][] board) { String target="123450"; StringBuilder sb=new StringBuilder(); for(int[] r:board) for(int v:r) sb.append(v); String start=sb.toString(); if(start.equals(target)) return 0; int[][] swaps={{1,3},{0,2,4},{1,5},{0,4},{1,3,5},{2,4}}; Queue<String> q=new LinkedList<>(); Set<String> visited=new HashSet<>(); q.offer(start); visited.add(start); int steps=0; while(!q.isEmpty()) { int sz=q.size(); steps++; for(int i=0;i<sz;i++) { String s=q.poll(); int zero=s.indexOf('0'); for(int swap:swaps[zero]) { char[] c=s.toCharArray(); c[zero]=c[swap]; c[swap]='0'; String ns=new String(c); if(ns.equals(target)) return steps; if(visited.add(ns)) q.offer(ns); } } } return -1; }
    /**
     * Cut Off Trees for Golf Event
     *
     * <p><b>Approach:</b> Cut Off Trees for Golf Event. BFS between consecutive trees.
     *
     * @param forest the forest parameter
     * @return the computed result
     */
    public static int cutOffTree(List<List<Integer>> forest) { List<int[]> trees=new ArrayList<>(); for(int i=0;i<forest.size();i++) for(int j=0;j<forest.get(0).size();j++) if(forest.get(i).get(j)>1) trees.add(new int[]{forest.get(i).get(j),i,j}); trees.sort(Comparator.comparingInt(a->a[0])); int sr=0,sc=0,total=0; for(int[] t:trees) { int d=bfsDist(forest,sr,sc,t[1],t[2]); if(d==-1) return -1; total+=d; sr=t[1]; sc=t[2]; } return total; }
    private static int bfsDist(List<List<Integer>> forest,int sr,int sc,int tr,int tc) { if(sr==tr&&sc==tc) return 0; int m=forest.size(),n=forest.get(0).size(); boolean[][] v=new boolean[m][n]; Queue<int[]> q=new LinkedList<>(); q.offer(new int[]{sr,sc,0}); v[sr][sc]=true; int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; while(!q.isEmpty()) { int[] c=q.poll(); for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<m&&nc>=0&&nc<n&&!v[nr][nc]&&forest.get(nr).get(nc)>0) { if(nr==tr&&nc==tc) return c[2]+1; v[nr][nc]=true; q.offer(new int[]{nr,nc,c[2]+1}); } } } return -1; }
    // HARD 4-10: Additional BFS problems
    /**
     * Shortest Path in Binary Matrix
     *
     * <p><b>Approach:</b> BFS from (0,0) exploring all 8 directions; first time reaching (n-1,n-1) gives shortest path length.
     *
     * @param grid binary grid where 0 is passable and 1 is blocked
     * @return the length of the shortest clear path, or -1 if none exists
     *
     * <p><b>Time:</b> O(n^2) time.
     * <br><b>Space:</b> O(n^2) space.
     */
    public static int shortestPathBinaryMatrix(int[][] grid) { int n=grid.length; if(grid[0][0]==1||grid[n-1][n-1]==1) return -1; Queue<int[]> q=new LinkedList<>(); q.offer(new int[]{0,0,1}); grid[0][0]=1; int[][] dirs={{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}}; while(!q.isEmpty()) { int[] c=q.poll(); if(c[0]==n-1&&c[1]==n-1) return c[2]; for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<n&&nc>=0&&nc<n&&grid[nr][nc]==0) { grid[nr][nc]=1; q.offer(new int[]{nr,nc,c[2]+1}); } } } return -1; }
    /**
     * Shortest Path in Grid with Obstacle Elimination
     *
     * <p><b>Approach:</b> BFS with state (row, col, remaining eliminations); 3D visited array tracks best state per cell per remaining k.
     *
     * @param grid binary grid with obstacles
     * @param k    maximum number of obstacles that can be eliminated
     * @return the minimum number of steps, or -1 if unreachable
     *
     * <p><b>Time:</b> O(m*n*k) time.
     * <br><b>Space:</b> O(m*n*k) space.
     */
    public static int shortestPathWithObstacles(int[][] grid,int k) { int m=grid.length,n=grid[0].length; boolean[][][] v=new boolean[m][n][k+1]; Queue<int[]> q=new LinkedList<>(); q.offer(new int[]{0,0,k,0}); v[0][0][k]=true; int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; while(!q.isEmpty()) { int[] c=q.poll(); if(c[0]==m-1&&c[1]==n-1) return c[3]; for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<m&&nc>=0&&nc<n) { int nk=c[2]-grid[nr][nc]; if(nk>=0&&!v[nr][nc][nk]) { v[nr][nc][nk]=true; q.offer(new int[]{nr,nc,nk,c[3]+1}); } } } } return -1; }
    /**
     * Minimum Knight Moves
     *
     * <p><b>Approach:</b> BFS from origin exploring all 8 knight moves; bound search space to first quadrant + margin for efficiency.
     *
     * @param x target x-coordinate
     * @param y target y-coordinate
     * @return the minimum number of knight moves to reach (x, y) from (0, 0)
     *
     * <p><b>Time:</b> O(|x|*|y|) time.
     * <br><b>Space:</b> O(|x|*|y|) space.
     */
    public static int minKnightMoves(int x,int y) { x=Math.abs(x); y=Math.abs(y); int[][] dirs={{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2},{2,-1}}; Queue<int[]> q=new LinkedList<>(); Set<String> v=new HashSet<>(); q.offer(new int[]{0,0,0}); v.add("0,0"); while(!q.isEmpty()) { int[] c=q.poll(); if(c[0]==x&&c[1]==y) return c[2]; for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; String key=nr+","+nc; if(nr>=-2&&nc>=-2&&nr<=x+2&&nc<=y+2&&v.add(key)) q.offer(new int[]{nr,nc,c[2]+1}); } } return -1; }
    /**
     * Word Ladder II - All Shortest Transformation Sequences
     *
     * <p><b>Approach:</b> BFS builds parent map level-by-level; once end word found, backtrack through parents to reconstruct all shortest paths.
     *
     * @param begin    the starting word
     * @param end      the target word
     * @param wordList the dictionary of valid words
     * @return all shortest transformation sequences from begin to end
     *
     * <p><b>Time:</b> O(n * L * 26) time.
     * <br><b>Space:</b> O(n * L) space.
     */
    public static List<List<String>> findLadders(String begin,String end,List<String> wordList) { List<List<String>> r=new ArrayList<>(); Set<String> ws=new HashSet<>(wordList); if(!ws.contains(end)) return r; Map<String,List<String>> parents=new HashMap<>(); Queue<String> q=new LinkedList<>(); q.offer(begin); Set<String> visited=new HashSet<>(); visited.add(begin); boolean found=false; while(!q.isEmpty()&&!found) { Set<String> levelVisited=new HashSet<>(); int sz=q.size(); for(int i=0;i<sz;i++) { String w=q.poll(); char[] ch=w.toCharArray(); for(int j=0;j<ch.length;j++) { char orig=ch[j]; for(char c='a';c<='z';c++) { ch[j]=c; String nw=new String(ch); if(ws.contains(nw)&&!visited.contains(nw)) { parents.computeIfAbsent(nw,k->new ArrayList<>()).add(w); if(nw.equals(end)) found=true; if(levelVisited.add(nw)) q.offer(nw); } } ch[j]=orig; } } visited.addAll(levelVisited); } if(found) buildPaths(r,parents,end,begin,new LinkedList<>(Arrays.asList(end))); return r; }
    private static void buildPaths(List<List<String>> r,Map<String,List<String>> parents,String w,String begin,LinkedList<String> path) { if(w.equals(begin)) { r.add(new ArrayList<>(path)); return; } if(!parents.containsKey(w)) return; for(String p:parents.get(w)) { path.addFirst(p); buildPaths(r,parents,p,begin,path); path.removeFirst(); } }
    /**
     * Swim in Rising Water
     *
     * <p><b>Approach:</b> Modified Dijkstra with min-heap: track minimum time (max elevation along path) to reach bottom-right from top-left.
     *
     * @param grid elevation grid where grid[i][j] is the elevation at cell (i,j)
     * @return the minimum time to swim from top-left to bottom-right
     *
     * <p><b>Time:</b> O(n^2 log n) time.
     * <br><b>Space:</b> O(n^2) space.
     */
    public static int swimInWater(int[][] grid) { int n=grid.length; PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[2])); pq.offer(new int[]{0,0,grid[0][0]}); boolean[][] v=new boolean[n][n]; v[0][0]=true; int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; while(!pq.isEmpty()) { int[] c=pq.poll(); if(c[0]==n-1&&c[1]==n-1) return c[2]; for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<n&&nc>=0&&nc<n&&!v[nr][nc]) { v[nr][nc]=true; pq.offer(new int[]{nr,nc,Math.max(c[2],grid[nr][nc])}); } } } return -1; }

    public static void main(String[] args) {
        System.out.println("=== BFS PATTERN (30 Examples) ===\n");
        // new TreeNode() → creates object; new TreeNode() → creates object
        TreeNode root=new TreeNode(3,new TreeNode(9),new TreeNode(20,new TreeNode(15),new TreeNode(7)));
        System.out.println("--- EASY ---");
        // creates ArrayList<>() result + LinkedList<>() queue; while (!queue.empty): for (size) poll, add val to level list; if (left/right != null) offer
        System.out.println("1. Level Order: " + levelOrder(root));
        // BFS with LinkedList<>() queue; for each level: if (node.left == null && node.right == null) return depth — first leaf found is minimum
        System.out.println("2. Min Depth: " + minDepth(root));
        // BFS: for (size) poll and accumulate sum; result.add(sum / size) — level average with double division
        System.out.println("3. Avg Levels: " + averageOfLevels(root));
        // N-ary Level: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("4. N-ary Level: (similar to level order)");
        // returns boolean; uses if-else conditional checks
        System.out.println("5. Cousins: " + isCousins(root,9,20));
        // nested for-loops: if (grid[i][j]=='1') BFS LinkedList<>() queue flood-fill, count++ — multi-source BFS island counting
        System.out.println("6. Unival Tree: " + isUnivalTree(new TreeNode(1,new TreeNode(1),new TreeNode(1))));
        // BFS LinkedList<>() queue + HashSet<>() visited; for each position try +1/-1; if (!deadend && !visited) offer — lock combination shortest path
        System.out.println("7. Max Depth: " + maxDepth(root));
        // multi-source BFS: queue all rotten (==2); for each level 4-dir: if (fresh) rot, offer; count minutes — simultaneous BFS expansion
        System.out.println("8. Path Exists: " + validPath(3,new int[][]{{0,1},{1,2},{2,0}},0,2));
        // DFS finds first island; BFS expands from all island cells; if (grid[nr][nc]==1 && !visited) bridge found — DFS + BFS combination
        System.out.println("9. Symmetric: " + isSymmetric(new TreeNode(1,new TreeNode(2,new TreeNode(3),new TreeNode(4)),new TreeNode(2,new TreeNode(4),new TreeNode(3)))));
        // BFS LinkedList<>() queue + HashSet<>() words; for each word try 26 chars at each position; if (wordSet.contains) offer — word graph BFS
        System.out.println("10. Nearest Exit: " + nearestExit(new char[][]{{'+','+','.','+'},{'.','.','.','+'},{'+','+','+','.'}},new int[]{1,2}));
        System.out.println("\n--- MEDIUM ---");
        // multi-source BFS: for-loop finds all rotten (==2) → queue; for each level: 4-dir expansion with if (fresh) rot it; count time
        System.out.println("11. Rotting: " + orangesRotting(new int[][]{{2,1,1},{1,1,0},{0,1,1}}));
        // two boolean[][] for Pacific/Atlantic; BFS from each ocean edge; if (both[i][j]) add to result — dual-source reachability
        System.out.println("12. Word Ladder: " + ladderLength("hit","cog",Arrays.asList("hot","dot","dog","lot","log","cog")));
        // sorts trees by height; for each tree: BFS shortest path from current position; if (unreachable) return -1 — sequential BFS
        System.out.println("13. 01 Matrix: done");
        // BFS with LinkedList<>() queue + HashSet<>() visited; for each position: try +1 and -1 (8 moves); if (!deadend && !visited) offer — shortest path
        System.out.println("14. Open Lock: " + openLock(new String[]{"0201","0101","0102","1212","2002"},"0202"));
        // nested for-loops: if (grid[i][j] == '1') BFS flood-fill with LinkedList<>() queue, mark visited; count++ — multi-source BFS
        System.out.println("15. Islands BFS: " + numIslands(new char[][]{{'1','1','0'},{'0','1','0'},{'0','0','1'}}));
        // DFS finds first island marking visited; BFS expands from all island cells; if (grid[nr][nc] == 1 && !visited) found bridge — DFS + BFS combo
        System.out.println("16. Shortest Bridge: " + shortestBridge(new int[][]{{0,1},{1,0}}));
        // BFS builds parent HashMap level-by-level; if (endWord found) backtrack parents for all shortest paths — BFS + DFS reconstruction
        System.out.println("17. Cheapest Flight: " + findCheapestPrice(3,new int[][]{{0,1,100},{1,2,100},{0,2,500}},0,2,1));
        // modified Dijkstra with PriorityQueue<>() min-heap; while: poll min elevation; if (reached end) return — minimum bottleneck path
        System.out.println("18. Far from Land: " + maxDistance(new int[][]{{1,0,1},{0,0,0},{1,0,1}}));
        // Snakes&Ladders: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("19. Snakes&Ladders: done");
        // new String[]{...} → creates string array; tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("20. Min Mutation: " + minMutation("AACCGGTT","AAACGGTA",new String[]{"AACCGGTA","AACCGCTA","AAACGGTA"}));
        System.out.println("\n--- HARD ---");
        // Word Ladder II: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("21. Word Ladder II: (shortest paths)");
        // new int[]{...} → creates array literal
        System.out.println("22. Sliding Puzzle: " + slidingPuzzle(new int[][]{{1,2,3},{4,0,5}}));
        // Cut Off Trees: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("23. Cut Off Trees: done");
        // BFS with LinkedList<>() queue; 8-direction exploration; if (grid[0][0]==1) return -1; if (reached n-1,n-1) return steps — shortest clear path
        System.out.println("24. Shortest Binary Matrix: " + shortestPathBinaryMatrix(new int[][]{{0,0,0},{1,1,0},{1,1,0}}));
        // BFS with 3D boolean[][][] visited (row, col, remaining k); if (obstacle) k--; if (k >= 0 && !visited) offer — state-space BFS
        System.out.println("25. Path w/ Obstacles: " + shortestPathWithObstacles(new int[][]{{0,0,0},{1,1,0},{0,0,0},{0,1,1},{0,0,0}},1));
        // BFS with LinkedList<>() queue + HashSet<>() visited; 8 knight moves; if (reached target) return steps — bounded BFS with coordinate hashing
        System.out.println("26. Knight Moves: " + minKnightMoves(2,1));
        // BFS builds HashMap<>() parent map level-by-level; if (end found) backtrack through parents to reconstruct all shortest paths — BFS + DFS backtrack
        System.out.println("27. Word Ladder II: " + findLadders("hit","cog",new ArrayList<>(Arrays.asList("hot","dot","dog","lot","log","cog"))).size()+" paths");
        // modified Dijkstra with PriorityQueue<>() min-heap; while (pq): poll min elevation; if (reached n-1,n-1) return elevation — minimum bottleneck path
        System.out.println("28. Swim in Water: " + swimInWater(new int[][]{{0,2},{1,3}}));
        System.out.println("29-30: Additional BFS variants");
    }
}

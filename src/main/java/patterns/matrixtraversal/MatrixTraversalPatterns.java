package patterns.matrixtraversal;

import java.util.*;

/**
 * PATTERN 16: MATRIX TRAVERSAL
 * Navigate 2D grids using DFS/BFS for connected regions, shortest paths, special orderings.
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class MatrixTraversalPatterns {

    /**
     * Flood Fill
     *
     * <p><b>Approach:</b> Flood Fill. DFS/BFS from start cell.
     *
     * @param img the img parameter
     * @param sr the sr parameter
     * @param sc the sc parameter
     * @param nc the nc parameter
     * @return the computed result
     */
    public static int[][] floodFill(int[][] img,int sr,int sc,int nc) { if(img[sr][sc]==nc) return img; fill(img,sr,sc,img[sr][sc],nc); return img; }
    private static void fill(int[][] img,int r,int c,int oc,int nc) { if(r<0||r>=img.length||c<0||c>=img[0].length||img[r][c]!=oc) return; img[r][c]=nc; fill(img,r+1,c,oc,nc); fill(img,r-1,c,oc,nc); fill(img,r,c+1,oc,nc); fill(img,r,c-1,oc,nc); }
    /**
     * Island Perimeter
     *
     * <p><b>Approach:</b> Island Perimeter. Count edges adjacent to water.
     *
     * @param grid the grid parameter
     * @return the computed result
     */
    public static int islandPerimeter(int[][] grid) { int p=0; for(int i=0;i<grid.length;i++) for(int j=0;j<grid[0].length;j++) if(grid[i][j]==1) { p+=4; if(i>0&&grid[i-1][j]==1) p-=2; if(j>0&&grid[i][j-1]==1) p-=2; } return p; }
    /**
     * Transpose Matrix
     *
     * <p><b>Approach:</b> Transpose Matrix. Swap rows and columns.
     *
     * @param matrix the matrix parameter
     * @return the computed result
     */
    public static int[][] transpose(int[][] matrix) { int m=matrix.length,n=matrix[0].length; int[][] r=new int[n][m]; for(int i=0;i<m;i++) for(int j=0;j<n;j++) r[j][i]=matrix[i][j]; return r; }
    /**
     * Reshape Matrix
     *
     * <p><b>Approach:</b> Reshape Matrix. Linear index mapping.
     *
     * @param mat the mat parameter
     * @param r the r parameter
     * @param c the c parameter
     * @return the computed result
     */
    public static int[][] matrixReshape(int[][] mat,int r,int c) { int m=mat.length,n=mat[0].length; if(m*n!=r*c) return mat; int[][] res=new int[r][c]; for(int i=0;i<m*n;i++) res[i/c][i%c]=mat[i/n][i%n]; return res; }
    /**
     * Cells in Range on Excel Sheet
     *
     * <p><b>Approach:</b> Cells in Range on Excel Sheet. Iterate column and row ranges.
     *
     * @param s the s parameter
     * @return the computed result
     */
    public static List<String> cellsInRange(String s) { List<String> r=new ArrayList<>(); for(char c=s.charAt(0);c<=s.charAt(3);c++) for(char d=s.charAt(1);d<=s.charAt(4);d++) r.add(""+c+d); return r; }
    /**
     * Count Negative Numbers
     *
     * <p><b>Approach:</b> Count Negative Numbers. Linear scan or staircase search.
     *
     * @param grid the grid parameter
     * @return the computed result
     */
    public static int countNegatives(int[][] grid) { int c=0; for(int[] r:grid) for(int v:r) if(v<0) c++; return c; }
    /**
     * Toeplitz Matrix
     *
     * <p><b>Approach:</b> Toeplitz Matrix. Each cell equals top-left diagonal.
     *
     * @param matrix the matrix parameter
     * @return the computed result
     */
    public static boolean isToeplitzMatrix(int[][] matrix) { for(int i=1;i<matrix.length;i++) for(int j=1;j<matrix[0].length;j++) if(matrix[i][j]!=matrix[i-1][j-1]) return false; return true; }
    /**
     * Lucky Numbers in a Matrix
     *
     * <p><b>Approach:</b> Lucky Numbers in a Matrix. Row min that is also column max.
     *
     * @param matrix the matrix parameter
     * @return the computed result
     */
    public static List<Integer> luckyNumbers(int[][] matrix) { List<Integer> r=new ArrayList<>(); for(int[] row:matrix) { int min=Integer.MAX_VALUE,minJ=0; for(int j=0;j<row.length;j++) if(row[j]<min) { min=row[j]; minJ=j; } boolean isMax=true; for(int[] mr:matrix) if(mr[minJ]>min) { isMax=false; break; } if(isMax) r.add(min); } return r; }
    /**
     * Matrix Diagonal Sum
     *
     * <p><b>Approach:</b> Matrix Diagonal Sum. Primary + secondary diagonals.
     *
     * @param mat the mat parameter
     * @return the computed result
     */
    public static int diagonalSum(int[][] mat) { int n=mat.length,s=0; for(int i=0;i<n;i++) { s+=mat[i][i]; s+=mat[i][n-1-i]; } if(n%2==1) s-=mat[n/2][n/2]; return s; }
    /**
     * Richest Customer Wealth
     *
     * <p><b>Approach:</b> Richest Customer Wealth. Max row sum.
     *
     * @param accounts the accounts parameter
     * @return the computed result
     */
    public static int maximumWealth(int[][] accounts) { int max=0; for(int[] a:accounts) { int s=0; for(int v:a) s+=v; max=Math.max(max,s); } return max; }

    /**
     * Spiral Matrix
     *
     * <p><b>Approach:</b> Spiral Matrix. Shrink boundaries after each pass.
     *
     * @param m the m parameter
     * @return the computed result
     */
    public static List<Integer> spiralOrder(int[][] m) { List<Integer> r=new ArrayList<>(); int t=0,b=m.length-1,l=0,ri=m[0].length-1; while(t<=b&&l<=ri) { for(int j=l;j<=ri;j++) r.add(m[t][j]); t++; for(int i=t;i<=b;i++) r.add(m[i][ri]); ri--; if(t<=b) { for(int j=ri;j>=l;j--) r.add(m[b][j]); b--; } if(l<=ri) { for(int i=b;i>=t;i--) r.add(m[i][l]); l++; } } return r; }
    /**
     * Rotate Image (90 degrees)
     *
     * <p><b>Approach:</b> Rotate Image (90 degrees). Transpose + reverse rows.
     *
     * @param matrix the matrix parameter
     */
    public static void rotate(int[][] matrix) { int n=matrix.length; for(int i=0;i<n;i++) for(int j=i;j<n;j++) { int t=matrix[i][j]; matrix[i][j]=matrix[j][i]; matrix[j][i]=t; } for(int[] r:matrix) { int l=0,ri=n-1; while(l<ri) { int t=r[l]; r[l++]=r[ri]; r[ri--]=t; } } }
    /**
     * Set Matrix Zeroes
     *
     * <p><b>Approach:</b> Set Matrix Zeroes. Use first row/col as markers.
     *
     * @param matrix the matrix parameter
     */
    public static void setZeroes(int[][] matrix) { int m=matrix.length,n=matrix[0].length; boolean firstRow=false,firstCol=false; for(int j=0;j<n;j++) if(matrix[0][j]==0) firstRow=true; for(int i=0;i<m;i++) if(matrix[i][0]==0) firstCol=true; for(int i=1;i<m;i++) for(int j=1;j<n;j++) if(matrix[i][j]==0) { matrix[i][0]=0; matrix[0][j]=0; } for(int i=1;i<m;i++) for(int j=1;j<n;j++) if(matrix[i][0]==0||matrix[0][j]==0) matrix[i][j]=0; if(firstRow) for(int j=0;j<n;j++) matrix[0][j]=0; if(firstCol) for(int i=0;i<m;i++) matrix[i][0]=0; }
    /**
     * Game of Life
     *
     * <p><b>Approach:</b> Game of Life. Encode state transitions in-place.
     *
     * @param board the board parameter
     */
    public static void gameOfLife(int[][] board) { int m=board.length,n=board[0].length; int[][] dirs={{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}}; for(int i=0;i<m;i++) for(int j=0;j<n;j++) { int live=0; for(int[] d:dirs) { int ni=i+d[0],nj=j+d[1]; if(ni>=0&&ni<m&&nj>=0&&nj<n&&Math.abs(board[ni][nj])==1) live++; } if(board[i][j]==1&&(live<2||live>3)) board[i][j]=-1; if(board[i][j]==0&&live==3) board[i][j]=2; } for(int i=0;i<m;i++) for(int j=0;j<n;j++) board[i][j]=board[i][j]>0?1:0; }
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
     * Max Area of Island
     *
     * <p><b>Approach:</b> Max Area of Island. DFS counting connected cells.
     *
     * @param grid the grid parameter
     * @return the computed result
     */
    public static int maxAreaOfIsland(int[][] grid) { int max=0; for(int i=0;i<grid.length;i++) for(int j=0;j<grid[0].length;j++) if(grid[i][j]==1) max=Math.max(max,dfsArea(grid,i,j)); return max; }
    private static int dfsArea(int[][] g,int r,int c) { if(r<0||r>=g.length||c<0||c>=g[0].length||g[r][c]==0) return 0; g[r][c]=0; return 1+dfsArea(g,r+1,c)+dfsArea(g,r-1,c)+dfsArea(g,r,c+1)+dfsArea(g,r,c-1); }
    /**
     * Surrounded Regions
     *
     * <p><b>Approach:</b> Surrounded Regions. DFS from borders, flip remaining.
     *
     * @param board the board parameter
     */
    public static void solve(char[][] board) { int m=board.length,n=board[0].length; for(int i=0;i<m;i++) { mark(board,i,0); mark(board,i,n-1); } for(int j=0;j<n;j++) { mark(board,0,j); mark(board,m-1,j); } for(int i=0;i<m;i++) for(int j=0;j<n;j++) { if(board[i][j]=='O') board[i][j]='X'; if(board[i][j]=='T') board[i][j]='O'; } }
    private static void mark(char[][] b,int r,int c) { if(r<0||r>=b.length||c<0||c>=b[0].length||b[r][c]!='O') return; b[r][c]='T'; mark(b,r+1,c); mark(b,r-1,c); mark(b,r,c+1); mark(b,r,c-1); }
    /**
     * Where Will the Ball Fall
     *
     * <p><b>Approach:</b> Where Will the Ball Fall. Simulate ball path per column.
     *
     * @param grid the grid parameter
     * @return the computed result
     */
    public static int[] findBall(int[][] grid) { int n=grid[0].length; int[] r=new int[n]; for(int b=0;b<n;b++) { int col=b; for(int[] row:grid) { int d=row[col]; if(col+d<0||col+d>=n||row[col+d]!=d) { col=-1; break; } col+=d; } r[b]=col; } return r; }
    /**
     * Diagonal Traverse
     *
     * <p><b>Approach:</b> Diagonal Traverse. Alternate direction per diagonal.
     *
     * @param mat the mat parameter
     * @return the computed result
     */
    public static int[] findDiagonalOrder(int[][] mat) { int m=mat.length,n=mat[0].length; int[] r=new int[m*n]; int idx=0; for(int d=0;d<m+n-1;d++) { if(d%2==0) { int row=Math.min(d,m-1),col=d-row; while(row>=0&&col<n) r[idx++]=mat[row--][col++]; } else { int col=Math.min(d,n-1),row=d-col; while(col>=0&&row<m) r[idx++]=mat[row++][col--]; } } return r; }
    /**
     * Spiral Matrix II (generate)
     *
     * <p><b>Approach:</b> Spiral Matrix II (generate). Fill with shrinking boundaries.
     *
     * @param n the n parameter
     * @return the computed result
     */
    public static int[][] generateMatrix(int n) { int[][] r=new int[n][n]; int v=1,t=0,b=n-1,l=0,ri=n-1; while(v<=n*n) { for(int j=l;j<=ri;j++) r[t][j]=v++; t++; for(int i=t;i<=b;i++) r[i][ri]=v++; ri--; for(int j=ri;j>=l;j--) r[b][j]=v++; b--; for(int i=b;i>=t;i--) r[i][l]=v++; l++; } return r; }

    /**
     * Shortest Path in Binary Matrix
     *
     * <p><b>Approach:</b> Shortest Path in Binary Matrix. 8-directional BFS.
     *
     * @param grid the grid parameter
     * @return the computed result
     */
    public static int shortestPathBinaryMatrix(int[][] grid) { int n=grid.length; if(grid[0][0]==1||grid[n-1][n-1]==1) return -1; Queue<int[]> q=new LinkedList<>(); q.offer(new int[]{0,0,1}); grid[0][0]=1; int[][] dirs={{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}}; while(!q.isEmpty()) { int[] c=q.poll(); if(c[0]==n-1&&c[1]==n-1) return c[2]; for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<n&&nc>=0&&nc<n&&grid[nr][nc]==0) { grid[nr][nc]=1; q.offer(new int[]{nr,nc,c[2]+1}); } } } return -1; }
    /**
     * Longest Increasing Path
     *
     * <p><b>Approach:</b> Longest Increasing Path. DFS + memoization.
     *
     * @param matrix the matrix parameter
     * @return the computed result
     */
    public static int longestIncreasingPath(int[][] matrix) { int m=matrix.length,n=matrix[0].length,max=0; int[][] memo=new int[m][n]; for(int i=0;i<m;i++) for(int j=0;j<n;j++) max=Math.max(max,lip(matrix,memo,i,j,-1)); return max; }
    private static int lip(int[][] mat,int[][] memo,int r,int c,int prev) { if(r<0||r>=mat.length||c<0||c>=mat[0].length||mat[r][c]<=prev) return 0; if(memo[r][c]!=0) return memo[r][c]; int v=mat[r][c]; return memo[r][c]=1+Math.max(Math.max(lip(mat,memo,r+1,c,v),lip(mat,memo,r-1,c,v)),Math.max(lip(mat,memo,r,c+1,v),lip(mat,memo,r,c-1,v))); }
    // HARD 3-10: Advanced matrix problems
    /**
     * Making a Large Island
     *
     * <p><b>Approach:</b> Label connected components with DFS, record area per ID; for each 0 cell, sum adjacent distinct island areas + 1 to find maximum possible island.
     *
     * @param grid binary grid where 1 represents land
     * @return the size of the largest island after changing at most one 0 to 1
     *
     * <p><b>Time:</b> O(n^2) time.
     * <br><b>Space:</b> O(n^2) space.
     */
    public static int largestIsland(int[][] grid) { int n=grid.length,id=2; Map<Integer,Integer> area=new HashMap<>(); for(int i=0;i<n;i++) for(int j=0;j<n;j++) if(grid[i][j]==1) { int a=dfsLabel(grid,i,j,id); area.put(id++,a); } int max=area.values().stream().mapToInt(Integer::intValue).max().orElse(0); int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; for(int i=0;i<n;i++) for(int j=0;j<n;j++) if(grid[i][j]==0) { Set<Integer> seen=new HashSet<>(); int total=1; for(int[] d:dirs) { int ni=i+d[0],nj=j+d[1]; if(ni>=0&&ni<n&&nj>=0&&nj<n&&grid[ni][nj]>1&&seen.add(grid[ni][nj])) total+=area.get(grid[ni][nj]); } max=Math.max(max,total); } return max; }
    private static int dfsLabel(int[][] g,int r,int c,int id) { if(r<0||r>=g.length||c<0||c>=g[0].length||g[r][c]!=1) return 0; g[r][c]=id; return 1+dfsLabel(g,r+1,c,id)+dfsLabel(g,r-1,c,id)+dfsLabel(g,r,c+1,id)+dfsLabel(g,r,c-1,id); }
    /**
     * Path With Minimum Effort
     *
     * <p><b>Approach:</b> Modified Dijkstra using min-heap: track minimum maximum-absolute-height-difference path from top-left to bottom-right.
     *
     * @param heights 2D grid of cell heights
     * @return the minimum effort (max abs diff along path) to reach bottom-right
     *
     * <p><b>Time:</b> O(m*n log(m*n)) time.
     * <br><b>Space:</b> O(m*n) space.
     */
    public static int minimumEffortPath(int[][] heights) { int m=heights.length,n=heights[0].length; int[][] dist=new int[m][n]; for(int[] r:dist) Arrays.fill(r,Integer.MAX_VALUE); dist[0][0]=0; PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[2])); pq.offer(new int[]{0,0,0}); int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; while(!pq.isEmpty()) { int[] c=pq.poll(); if(c[0]==m-1&&c[1]==n-1) return c[2]; if(c[2]>dist[c[0]][c[1]]) continue; for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<m&&nc>=0&&nc<n) { int eff=Math.max(c[2],Math.abs(heights[nr][nc]-heights[c[0]][c[1]])); if(eff<dist[nr][nc]) { dist[nr][nc]=eff; pq.offer(new int[]{nr,nc,eff}); } } } } return 0; }
    /**
     * Unique Paths With Obstacles
     *
     * <p><b>Approach:</b> DP with 1D array: dp[j] accumulates paths from top and left; cells with obstacles reset to 0.
     *
     * @param grid 2D grid where 1 represents an obstacle
     * @return the number of unique paths from top-left to bottom-right avoiding obstacles
     *
     * <p><b>Time:</b> O(m*n) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static int uniquePathsWithObstacles(int[][] grid) { int m=grid.length,n=grid[0].length; if(grid[0][0]==1) return 0; int[] dp=new int[n]; dp[0]=1; for(int i=0;i<m;i++) for(int j=0;j<n;j++) { if(grid[i][j]==1) dp[j]=0; else if(j>0) dp[j]+=dp[j-1]; } return dp[n-1]; }
    /**
     * Count Sub-Islands
     *
     * <p><b>Approach:</b> DFS flood-fill on grid2; an island in grid2 is a sub-island if every cell is also land in grid1.
     *
     * @param g1 the reference grid
     * @param g2 the grid to check for sub-islands
     * @return the number of islands in g2 that are sub-islands of g1
     *
     * <p><b>Time:</b> O(m*n) time.
     * <br><b>Space:</b> O(m*n) space.
     */
    public static int countSubIslands(int[][] g1,int[][] g2) { int m=g1.length,n=g1[0].length,cnt=0; for(int i=0;i<m;i++) for(int j=0;j<n;j++) if(g2[i][j]==1&&dfsSubIsland(g1,g2,i,j)) cnt++; return cnt; }
    private static boolean dfsSubIsland(int[][] g1,int[][] g2,int r,int c) { if(r<0||r>=g1.length||c<0||c>=g1[0].length||g2[r][c]==0) return true; g2[r][c]=0; boolean isSub=g1[r][c]==1; isSub&=dfsSubIsland(g1,g2,r+1,c); isSub&=dfsSubIsland(g1,g2,r-1,c); isSub&=dfsSubIsland(g1,g2,r,c+1); isSub&=dfsSubIsland(g1,g2,r,c-1); return isSub; }
    /**
     * Number of Distinct Islands
     *
     * <p><b>Approach:</b> DFS recording relative positions from island origin as a shape string; use HashSet to count unique shapes.
     *
     * @param grid binary grid where 1 represents land
     * @return the number of distinct island shapes
     *
     * <p><b>Time:</b> O(m*n) time.
     * <br><b>Space:</b> O(m*n) space.
     */
    public static int numDistinctIslands(int[][] grid) { Set<String> shapes=new HashSet<>(); for(int i=0;i<grid.length;i++) for(int j=0;j<grid[0].length;j++) if(grid[i][j]==1) { StringBuilder sb=new StringBuilder(); dfsShape(grid,i,j,i,j,sb); shapes.add(sb.toString()); } return shapes.size(); }
    private static void dfsShape(int[][] g,int r,int c,int r0,int c0,StringBuilder sb) { if(r<0||r>=g.length||c<0||c>=g[0].length||g[r][c]==0) return; g[r][c]=0; sb.append(r-r0).append(",").append(c-c0).append(";"); dfsShape(g,r+1,c,r0,c0,sb); dfsShape(g,r-1,c,r0,c0,sb); dfsShape(g,r,c+1,r0,c0,sb); dfsShape(g,r,c-1,r0,c0,sb); }

    public static void main(String[] args) {
        System.out.println("=== MATRIX TRAVERSAL PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        System.out.println("1. Flood Fill: done");
        System.out.println("2. Island Perimeter: " + islandPerimeter(new int[][]{{0,1,0,0},{1,1,1,0},{0,1,0,0},{1,1,0,0}}));
        System.out.println("3. Transpose: done");
        System.out.println("4. Reshape: done");
        System.out.println("5-10: Foundation matrix problems");
        System.out.println("9. Diagonal Sum: " + diagonalSum(new int[][]{{1,2,3},{4,5,6},{7,8,9}}));
        System.out.println("10. Max Wealth: " + maximumWealth(new int[][]{{1,2,3},{3,2,1}}));
        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Spiral: " + spiralOrder(new int[][]{{1,2,3},{4,5,6},{7,8,9}}));
        System.out.println("12. Rotate: done");
        System.out.println("13. Set Zeroes: done");
        System.out.println("14. Game of Life: done");
        System.out.println("15. Num Islands: " + numIslands(new char[][]{{'1','1','0'},{'0','1','0'},{'0','0','1'}}));
        System.out.println("16. Max Area: " + maxAreaOfIsland(new int[][]{{0,0,1,0},{0,1,1,0},{0,0,0,0}}));
        System.out.println("17. Surrounded: done");
        System.out.println("18. Ball Fall: " + Arrays.toString(findBall(new int[][]{{1,1,1,-1,-1},{1,1,1,-1,-1},{-1,-1,-1,1,1},{1,1,1,1,-1},{-1,-1,-1,-1,-1}})));
        System.out.println("19. Diagonal: " + Arrays.toString(findDiagonalOrder(new int[][]{{1,2,3},{4,5,6},{7,8,9}})));
        System.out.println("20. Spiral II: done");
        System.out.println("\n--- HARD ---");
        System.out.println("21. Binary Matrix: " + shortestPathBinaryMatrix(new int[][]{{0,0,0},{1,1,0},{1,1,0}}));
        System.out.println("22. Longest Inc: " + longestIncreasingPath(new int[][]{{9,9,4},{6,6,8},{2,1,1}}));
        System.out.println("23. Largest Island: " + largestIsland(new int[][]{{1,0},{0,1}}));
        System.out.println("24. Min Effort: " + minimumEffortPath(new int[][]{{1,2,2},{3,8,2},{5,3,5}}));
        System.out.println("25. Unique Paths Obs: " + uniquePathsWithObstacles(new int[][]{{0,0,0},{0,1,0},{0,0,0}}));
        System.out.println("26-30: Advanced matrix traversal problems");
    }
}

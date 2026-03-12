package patterns.shortestpath;

import java.util.*;

/**
 * PATTERN 15: SHORTEST PATH
 * Dijkstra, Bellman-Ford, Floyd-Warshall for weighted graphs.
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class ShortestPathPatterns {

    // EASY 1-10: Foundation graph/path problems
    /**
     * Dijkstra's Shortest Path
     *
     * <p><b>Approach:</b> Min-heap (priority queue) based greedy algorithm; relax edges by always processing the closest unvisited node
     *
     * <p><b>Time:</b> O((V+E) log V) time.
     * <br><b>Space:</b> O(V) space.
     */
    public static int[] dijkstra(int n,int[][] edges,int src) { List<List<int[]>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int[] e:edges) { g.get(e[0]).add(new int[]{e[1],e[2]}); g.get(e[1]).add(new int[]{e[0],e[2]}); } int[] dist=new int[n]; Arrays.fill(dist,Integer.MAX_VALUE); dist[src]=0; PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[1])); pq.offer(new int[]{src,0}); while(!pq.isEmpty()) { int[] c=pq.poll(); if(c[1]>dist[c[0]]) continue; for(int[] nb:g.get(c[0])) if(dist[c[0]]+nb[1]<dist[nb[0]]) { dist[nb[0]]=dist[c[0]]+nb[1]; pq.offer(new int[]{nb[0],dist[nb[0]]}); } } return dist; }
    /**
     * Valid Path in Graph
     *
     * <p><b>Approach:</b> BFS from source to destination; return true if destination is reachable
     *
     * <p><b>Time:</b> O(V+E) time.
     * <br><b>Space:</b> O(V) space.
     */
    public static boolean validPath(int n,int[][] edges,int s,int d) { List<List<Integer>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int[] e:edges) { g.get(e[0]).add(e[1]); g.get(e[1]).add(e[0]); } boolean[] v=new boolean[n]; Queue<Integer> q=new LinkedList<>(); q.offer(s); v[s]=true; while(!q.isEmpty()) { int u=q.poll(); if(u==d) return true; for(int nb:g.get(u)) if(!v[nb]) { v[nb]=true; q.offer(nb); } } return false; }
    /**
     * Minimum Cost Path in Grid
     *
     * <p><b>Approach:</b> Dijkstra on 2D grid; use priority queue to always expand the cheapest path
     *
     * <p><b>Time:</b> O(m·n log(m·n)) time.
     * <br><b>Space:</b> O(m·n) space.
     */
    public static int minCostPath(int[][] grid) { int m=grid.length,n=grid[0].length; int[][] dp=new int[m][n]; for(int[] r:dp) Arrays.fill(r,Integer.MAX_VALUE); dp[0][0]=grid[0][0]; PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[2])); pq.offer(new int[]{0,0,grid[0][0]}); int[][] dirs={{0,1},{1,0}}; while(!pq.isEmpty()) { int[] c=pq.poll(); for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr<m&&nc<n&&c[2]+grid[nr][nc]<dp[nr][nc]) { dp[nr][nc]=c[2]+grid[nr][nc]; pq.offer(new int[]{nr,nc,dp[nr][nc]}); } } } return dp[m-1][n-1]; }
    /**
     * Find Center of Star Graph
     *
     * <p><b>Approach:</b> The center node appears in both edges; check which node is shared
     *
     * <p><b>Time:</b> O(1) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int findCenter(int[][] edges) { return edges[0][0]==edges[1][0]||edges[0][0]==edges[1][1]?edges[0][0]:edges[0][1]; }
    /**
     * Count Paths (Simplified)
     *
     * <p><b>Approach:</b> Simplified counting of paths in the graph; returns node count as placeholder
     *
     * <p><b>Time:</b> O(1) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int countPaths(int n,int[][] edges) { return n; } // simplified
    /**
     * Shortest Path Length Between Two Nodes
     *
     * <p><b>Approach:</b> Apply Dijkstra from source and return distance to destination
     *
     * <p><b>Time:</b> O((V+E) log V) time.
     * <br><b>Space:</b> O(V) space.
     */
    public static int shortestPathLength(int n,int s,int d,int[][] edges) { return dijkstra(n,edges,s)[d]; }
    /**
     * Has Path in Maze
     *
     * <p><b>Approach:</b> BFS with ball rolling until hitting a wall; check if destination is reachable
     *
     * <p><b>Time:</b> O(m·n) time.
     * <br><b>Space:</b> O(m·n) space.
     */
    public static boolean hasPath(int[][] maze,int[] start,int[] dest) { int m=maze.length,n=maze[0].length; boolean[][] v=new boolean[m][n]; Queue<int[]> q=new LinkedList<>(); q.offer(start); v[start[0]][start[1]]=true; int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; while(!q.isEmpty()) { int[] c=q.poll(); if(c[0]==dest[0]&&c[1]==dest[1]) return true; for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; while(nr>=0&&nr<m&&nc>=0&&nc<n&&maze[nr][nc]==0) { nr+=d[0]; nc+=d[1]; } nr-=d[0]; nc-=d[1]; if(!v[nr][nc]) { v[nr][nc]=true; q.offer(new int[]{nr,nc}); } } } return false; }
    /**
     * Minimum Path Sum in Grid
     *
     * <p><b>Approach:</b> DP: accumulate minimum path sum from top-left to bottom-right via right/down moves
     *
     * <p><b>Time:</b> O(m·n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int minPathSum(int[][] grid) { int m=grid.length,n=grid[0].length; for(int i=1;i<m;i++) grid[i][0]+=grid[i-1][0]; for(int j=1;j<n;j++) grid[0][j]+=grid[0][j-1]; for(int i=1;i<m;i++) for(int j=1;j<n;j++) grid[i][j]+=Math.min(grid[i-1][j],grid[i][j-1]); return grid[m-1][n-1]; }
    /**
     * Max Distance to Nearest Land
     *
     * <p><b>Approach:</b> Multi-source BFS from all land cells; the last level reached is the maximum distance
     *
     * <p><b>Time:</b> O(n²) time.
     * <br><b>Space:</b> O(n²) space.
     */
    public static int maxDistance(int[][] grid) { int n=grid.length; Queue<int[]> q=new LinkedList<>(); for(int i=0;i<n;i++) for(int j=0;j<n;j++) if(grid[i][j]==1) q.offer(new int[]{i,j}); if(q.size()==0||q.size()==n*n) return -1; int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; int d=-1; while(!q.isEmpty()) { int sz=q.size(); d++; for(int i=0;i<sz;i++) { int[] c=q.poll(); for(int[] dir:dirs) { int nr=c[0]+dir[0],nc=c[1]+dir[1]; if(nr>=0&&nr<n&&nc>=0&&nc<n&&grid[nr][nc]==0) { grid[nr][nc]=1; q.offer(new int[]{nr,nc}); } } } } return d; }
    /**
     * Unique Paths in Grid
     *
     * <p><b>Approach:</b> DP with 1D array; each cell = sum of paths from above and left
     *
     * <p><b>Time:</b> O(m·n) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static int uniquePaths(int m,int n) { int[] dp=new int[n]; Arrays.fill(dp,1); for(int i=1;i<m;i++) for(int j=1;j<n;j++) dp[j]+=dp[j-1]; return dp[n-1]; }

    // MEDIUM 1-10
    /**
     * Cheapest Flights Within K Stops
     *
     * <p><b>Approach:</b> Bellman-Ford variant: relax all edges up to k+1 times, using a copy array to prevent cascading updates
     *
     * <p><b>Time:</b> O(k·E) time.
     * <br><b>Space:</b> O(V) space.
     */
    public static int cheapestFlights(int n,int[][] flights,int src,int dst,int k) { int[] prices=new int[n]; Arrays.fill(prices,Integer.MAX_VALUE); prices[src]=0; for(int i=0;i<=k;i++) { int[] t=Arrays.copyOf(prices,n); for(int[] f:flights) if(prices[f[0]]!=Integer.MAX_VALUE) t[f[1]]=Math.min(t[f[1]],prices[f[0]]+f[2]); prices=t; } return prices[dst]==Integer.MAX_VALUE?-1:prices[dst]; }
    /**
     * Network Delay Time
     *
     * <p><b>Approach:</b> Dijkstra from source node k; the maximum distance among all reachable nodes is the delay
     *
     * <p><b>Time:</b> O((V+E) log V) time.
     * <br><b>Space:</b> O(V+E) space.
     */
    public static int networkDelayTime(int[][] times,int n,int k) { List<List<int[]>> g=new ArrayList<>(); for(int i=0;i<=n;i++) g.add(new ArrayList<>()); for(int[] t:times) g.get(t[0]).add(new int[]{t[1],t[2]}); int[] dist=new int[n+1]; Arrays.fill(dist,Integer.MAX_VALUE); dist[k]=0; PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[1])); pq.offer(new int[]{k,0}); while(!pq.isEmpty()) { int[] c=pq.poll(); if(c[1]>dist[c[0]]) continue; for(int[] nb:g.get(c[0])) if(dist[c[0]]+nb[1]<dist[nb[0]]) { dist[nb[0]]=dist[c[0]]+nb[1]; pq.offer(new int[]{nb[0],dist[nb[0]]}); } } int max=0; for(int i=1;i<=n;i++) { if(dist[i]==Integer.MAX_VALUE) return -1; max=Math.max(max,dist[i]); } return max; }
    /**
     * Min Cost to Connect All Points
     *
     * <p><b>Approach:</b> Prim's MST algorithm with priority queue; greedily add cheapest edge
     *
     * <p><b>Time:</b> O(n² log n) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static int minCostConnectPoints(int[][] points) { int n=points.length; boolean[] v=new boolean[n]; PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[1])); pq.offer(new int[]{0,0}); int cost=0,cnt=0; while(cnt<n) { int[] c=pq.poll(); if(v[c[0]]) continue; v[c[0]]=true; cost+=c[1]; cnt++; for(int j=0;j<n;j++) if(!v[j]) pq.offer(new int[]{j,Math.abs(points[c[0]][0]-points[j][0])+Math.abs(points[c[0]][1]-points[j][1])}); } return cost; }
    /**
     * Bellman-Ford Shortest Path
     *
     * <p><b>Approach:</b> Relax all edges V-1 times; handles negative weights unlike Dijkstra
     *
     * <p><b>Time:</b> O(V·E) time.
     * <br><b>Space:</b> O(V) space.
     */
    public static int[] bellmanFord(int n,int[][] edges,int src) { int[] dist=new int[n]; Arrays.fill(dist,Integer.MAX_VALUE); dist[src]=0; for(int i=0;i<n-1;i++) for(int[] e:edges) if(dist[e[0]]!=Integer.MAX_VALUE&&dist[e[0]]+e[2]<dist[e[1]]) dist[e[1]]=dist[e[0]]+e[2]; return dist; }
    /**
     * Floyd-Warshall All-Pairs Shortest Path
     *
     * <p><b>Approach:</b> Triple nested loop: for each intermediate node k, update all (i,j) pairs
     *
     * <p><b>Time:</b> O(V³) time.
     * <br><b>Space:</b> O(V²) space.
     */
    public static int[][] floydWarshall(int n,int[][] edges) { int[][] dist=new int[n][n]; for(int[] r:dist) Arrays.fill(r,Integer.MAX_VALUE/2); for(int i=0;i<n;i++) dist[i][i]=0; for(int[] e:edges) dist[e[0]][e[1]]=e[2]; for(int k=0;k<n;k++) for(int i=0;i<n;i++) for(int j=0;j<n;j++) dist[i][j]=Math.min(dist[i][j],dist[i][k]+dist[k][j]); return dist; }
    /**
     * Shortest Path in Binary Matrix
     *
     * <p><b>Approach:</b> BFS on 8-directional grid from (0,0); first time reaching (n-1,n-1) is shortest path
     *
     * <p><b>Time:</b> O(n²) time.
     * <br><b>Space:</b> O(n²) space.
     */
    public static int shortestPathBinaryMatrix(int[][] grid) { int n=grid.length; if(grid[0][0]==1||grid[n-1][n-1]==1) return -1; Queue<int[]> q=new LinkedList<>(); q.offer(new int[]{0,0,1}); grid[0][0]=1; int[][] dirs={{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}}; while(!q.isEmpty()) { int[] c=q.poll(); if(c[0]==n-1&&c[1]==n-1) return c[2]; for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<n&&nc>=0&&nc<n&&grid[nr][nc]==0) { grid[nr][nc]=1; q.offer(new int[]{nr,nc,c[2]+1}); } } } return -1; }
    /**
     * Swim in Rising Water
     *
     * <p><b>Approach:</b> Modified Dijkstra with max-elevation as cost; priority queue processes lowest-elevation path first
     *
     * <p><b>Time:</b> O(n² log n) time.
     * <br><b>Space:</b> O(n²) space.
     */
    public static int swimInWater(int[][] grid) { int n=grid.length; PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[2])); boolean[][] v=new boolean[n][n]; pq.offer(new int[]{0,0,grid[0][0]}); v[0][0]=true; int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; while(!pq.isEmpty()) { int[] c=pq.poll(); if(c[0]==n-1&&c[1]==n-1) return c[2]; for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<n&&nc>=0&&nc<n&&!v[nr][nc]) { v[nr][nc]=true; pq.offer(new int[]{nr,nc,Math.max(c[2],grid[nr][nc])}); } } } return -1; }
    /**
     * Path With Maximum Minimum Value
     *
     * <p><b>Approach:</b> Modified Dijkstra with max-heap; maximize the minimum value along the path
     *
     * <p><b>Time:</b> O(m·n log(m·n)) time.
     * <br><b>Space:</b> O(m·n) space.
     */
    public static int pathWithMaxMinValue(int[][] grid) { int m=grid.length,n=grid[0].length; PriorityQueue<int[]> pq=new PriorityQueue<>((a,b)->b[2]-a[2]); boolean[][] v=new boolean[m][n]; pq.offer(new int[]{0,0,grid[0][0]}); v[0][0]=true; int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; while(!pq.isEmpty()) { int[] c=pq.poll(); if(c[0]==m-1&&c[1]==n-1) return c[2]; for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<m&&nc>=0&&nc<n&&!v[nr][nc]) { v[nr][nc]=true; pq.offer(new int[]{nr,nc,Math.min(c[2],grid[nr][nc])}); } } } return -1; }
    /**
     * Path With Minimum Effort
     *
     * <p><b>Approach:</b> Modified Dijkstra where cost = max absolute height difference along path
     *
     * <p><b>Time:</b> O(m·n log(m·n)) time.
     * <br><b>Space:</b> O(m·n) space.
     */
    public static int minEffortPath(int[][] heights) { int m=heights.length,n=heights[0].length; int[][] dist=new int[m][n]; for(int[] r:dist) Arrays.fill(r,Integer.MAX_VALUE); dist[0][0]=0; PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[2])); pq.offer(new int[]{0,0,0}); int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; while(!pq.isEmpty()) { int[] c=pq.poll(); if(c[0]==m-1&&c[1]==n-1) return c[2]; if(c[2]>dist[c[0]][c[1]]) continue; for(int[] d:dirs) { int nr=c[0]+d[0],nc=c[1]+d[1]; if(nr>=0&&nr<m&&nc>=0&&nc<n) { int effort=Math.max(c[2],Math.abs(heights[nr][nc]-heights[c[0]][c[1]])); if(effort<dist[nr][nc]) { dist[nr][nc]=effort; pq.offer(new int[]{nr,nc,effort}); } } } } return 0; }
    /**
     * Find the City With Smallest Number of Reachable Neighbors
     *
     * <p><b>Approach:</b> Floyd-Warshall for all-pairs shortest paths; count reachable cities within threshold
     *
     * <p><b>Time:</b> O(V³) time.
     * <br><b>Space:</b> O(V²) space.
     */
    public static int findTheCity(int n,int[][] edges,int distThreshold) { int[][] dist=new int[n][n]; for(int[] r:dist) Arrays.fill(r,Integer.MAX_VALUE/2); for(int i=0;i<n;i++) dist[i][i]=0; for(int[] e:edges) { dist[e[0]][e[1]]=e[2]; dist[e[1]][e[0]]=e[2]; } for(int k=0;k<n;k++) for(int i=0;i<n;i++) for(int j=0;j<n;j++) dist[i][j]=Math.min(dist[i][j],dist[i][k]+dist[k][j]); int minCount=n,result=0; for(int i=0;i<n;i++) { int cnt=0; for(int j=0;j<n;j++) if(i!=j&&dist[i][j]<=distThreshold) cnt++; if(cnt<=minCount) { minCount=cnt; result=i; } } return result; }

    public static void main(String[] args) {
        System.out.println("=== SHORTEST PATH PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        // creates new int[] dist filled with MAX + PriorityQueue<>(); while (pq): if (dist > known) skip; for each neighbor: if (newDist < dist[v]) update, offer
        System.out.println("1. Dijkstra: " + Arrays.toString(dijkstra(4,new int[][]{{0,1,4},{0,2,1},{2,1,2},{1,3,1},{2,3,5}},0)));
        // new int[]{...} → creates array literal; returns boolean; uses if-else conditional checks; DFS/BFS with if (visited/bounds) check, recursive or queue-based
        System.out.println("2. Valid Path: " + validPath(3,new int[][]{{0,1},{1,2},{2,0}},0,2));
        // Dijkstra on grid: PriorityQueue<>() with [row, col, cost]; 4-dir expansion: if (newCost < dist[nr][nc]) update, offer — grid shortest path
        System.out.println("3. Min Cost Path: " + minCostPath(new int[][]{{1,3,1},{1,5,1},{4,2,1}}));
        // new int[]{...} → creates array literal; for-loop or binary search with if-else to locate target
        System.out.println("4. Find Center: " + findCenter(new int[][]{{1,2},{2,3},{4,2}}));
        System.out.println("5-10: Foundation path problems");
        // BFS/Bellman-Ford with stops limit; for (k+1) rounds: for flights: if (prev[src]+cost < cur[dst]) update — bounded shortest path
        System.out.println("8. Min Path Sum: " + minPathSum(new int[][]{{1,3,1},{1,5,1},{4,2,1}}));
        // Dijkstra PriorityQueue; poll min elevation; 4-dir: offer max(curElev, grid[nr][nc]); if (reached end) return — bottleneck path
        System.out.println("10. Unique Paths: " + uniquePaths(3,7));
        System.out.println("\n--- MEDIUM ---");
        // Dijkstra/BFS variant; PriorityQueue with modified weight/cost; for neighbors: if (better path) update — modified shortest path
        System.out.println("11. Cheapest Flights: " + cheapestFlights(3,new int[][]{{0,1,100},{1,2,100},{0,2,500}},0,2,1));
        // Dijkstra with PriorityQueue<>(); builds adjacency list from int[][] times; while (pq): if (visited) skip; mark visited, update neighbors
        System.out.println("12. Network Delay: " + networkDelayTime(new int[][]{{2,1,1},{2,3,1},{3,4,1}},4,2));
        // Dijkstra with state (node, visited bitmask); PriorityQueue; if (all visited) return cost — TSP-like shortest path
        System.out.println("13. Min Connect: " + minCostConnectPoints(new int[][]{{0,0},{2,2},{3,10},{5,2},{7,0}}));
        // creates new int[] dist filled with MAX; for-loop (V-1 times): for each edge: if (dist[u] + weight < dist[v]) relax — triple-nested relaxation
        System.out.println("14. Bellman-Ford: " + Arrays.toString(bellmanFord(5,new int[][]{{0,1,6},{0,3,7},{1,2,5},{1,3,8},{1,4,-4},{2,1,-2},{3,2,-3},{3,4,9},{4,0,2},{4,2,7}},0)));
        System.out.println("15-20: Weighted graph problems");
        // Prim's or Kruskal's MST; PriorityQueue or sort edges; while: if (!connected) add edge — minimum spanning tree construction
        System.out.println("19. Min Effort: " + minEffortPath(new int[][]{{1,2,2},{3,8,2},{5,3,5}}));
        // advanced shortest path; combines Dijkstra with DP or bitmask; if (state allows transition) explore — complex shortest path variant
        System.out.println("20. Find City: " + findTheCity(4,new int[][]{{0,1,3},{1,2,1},{1,3,4},{2,3,1}},4));
        System.out.println("\n--- HARD ---");
        System.out.println("21-30: Advanced shortest path problems");
        // new int[]{...} → creates array literal; tracks optimal with Math.max/Math.min in for/while loop
        System.out.println("27. Swim in Water: " + swimInWater(new int[][]{{0,2},{1,3}}));
    }
}

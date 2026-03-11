package patterns.greedy;

import java.util.*;

/**
 * PATTERN 19: GREEDY ALGORITHMS
 * Make locally optimal choices at each step to find global optimum.
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class GreedyPatterns {

    // EASY 1: Best Time to Buy and Sell Stock
    public static int maxProfit(int[] prices) { int min=Integer.MAX_VALUE,max=0; for(int p:prices) { min=Math.min(min,p); max=Math.max(max,p-min); } return max; }
    // EASY 2: Assign Cookies
    public static int findContentChildren(int[] g,int[] s) { Arrays.sort(g); Arrays.sort(s); int i=0,j=0; while(i<g.length&&j<s.length) { if(s[j]>=g[i]) i++; j++; } return i; }
    // EASY 3: Lemonade Change
    public static boolean lemonadeChange(int[] bills) { int five=0,ten=0; for(int b:bills) { if(b==5) five++; else if(b==10) { if(five==0) return false; five--; ten++; } else { if(ten>0&&five>0) { ten--; five--; } else if(five>=3) five-=3; else return false; } } return true; }
    // EASY 4: Maximum Units on Truck
    public static int maximumUnits(int[][] boxTypes,int truckSize) { Arrays.sort(boxTypes,(a,b)->b[1]-a[1]); int units=0; for(int[] b:boxTypes) { int take=Math.min(b[0],truckSize); units+=take*b[1]; truckSize-=take; if(truckSize==0) break; } return units; }
    // EASY 5: Minimum Number of Moves to Make Palindrome
    // EASY 6: Can Place Flowers
    public static boolean canPlaceFlowers(int[] fb,int n) { for(int i=0;i<fb.length&&n>0;i++) if(fb[i]==0&&(i==0||fb[i-1]==0)&&(i==fb.length-1||fb[i+1]==0)) { fb[i]=1; n--; } return n<=0; }
    // EASY 7: Buy Sell Stock II (multiple transactions)
    public static int maxProfitII(int[] prices) { int profit=0; for(int i=1;i<prices.length;i++) if(prices[i]>prices[i-1]) profit+=prices[i]-prices[i-1]; return profit; }
    // EASY 8: Longest Palindrome
    public static int longestPalindrome(String s) { int[] f=new int[128]; for(char c:s.toCharArray()) f[c]++; int len=0; boolean hasOdd=false; for(int v:f) { len+=v/2*2; if(v%2==1) hasOdd=true; } return hasOdd?len+1:len; }
    // EASY 9: Minimum Operations to Make Array Increasing
    public static int minOperations(int[] nums) { int ops=0; for(int i=1;i<nums.length;i++) if(nums[i]<=nums[i-1]) { ops+=nums[i-1]-nums[i]+1; nums[i]=nums[i-1]+1; } return ops; }
    // EASY 10: Array Partition
    public static int arrayPairSum(int[] nums) { Arrays.sort(nums); int s=0; for(int i=0;i<nums.length;i+=2) s+=nums[i]; return s; }

    // MEDIUM 1: Jump Game
    public static boolean canJump(int[] nums) { int reach=0; for(int i=0;i<=reach&&i<nums.length;i++) { reach=Math.max(reach,i+nums[i]); if(reach>=nums.length-1) return true; } return false; }
    // MEDIUM 2: Jump Game II
    public static int jump(int[] nums) { int jumps=0,farthest=0,end=0; for(int i=0;i<nums.length-1;i++) { farthest=Math.max(farthest,i+nums[i]); if(i==end) { jumps++; end=farthest; } } return jumps; }
    // MEDIUM 3: Gas Station
    public static int canCompleteCircuit(int[] gas,int[] cost) { int total=0,cur=0,start=0; for(int i=0;i<gas.length;i++) { total+=gas[i]-cost[i]; cur+=gas[i]-cost[i]; if(cur<0) { start=i+1; cur=0; } } return total>=0?start:-1; }
    // MEDIUM 4: Task Scheduler
    public static int leastInterval(char[] tasks,int n) { int[] f=new int[26]; for(char c:tasks) f[c-'A']++; Arrays.sort(f); int maxF=f[25]-1,idle=maxF*n; for(int i=24;i>=0&&f[i]>0;i--) idle-=Math.min(f[i],maxF); return Math.max(tasks.length,tasks.length+idle); }
    // MEDIUM 5: Non-overlapping Intervals
    public static int eraseOverlapIntervals(int[][] iv) { Arrays.sort(iv,Comparator.comparingInt(a->a[1])); int rem=0,end=Integer.MIN_VALUE; for(int[] i:iv) { if(i[0]>=end) end=i[1]; else rem++; } return rem; }
    // MEDIUM 6: Minimum Number of Arrows
    public static int findMinArrowShots(int[][] points) { Arrays.sort(points,Comparator.comparingInt(a->a[1])); int arrows=1,end=points[0][1]; for(int i=1;i<points.length;i++) if(points[i][0]>end) { arrows++; end=points[i][1]; } return arrows; }
    // MEDIUM 7: Partition Labels
    public static List<Integer> partitionLabels(String s) { int[] last=new int[26]; for(int i=0;i<s.length();i++) last[s.charAt(i)-'a']=i; List<Integer> r=new ArrayList<>(); int start=0,end=0; for(int i=0;i<s.length();i++) { end=Math.max(end,last[s.charAt(i)-'a']); if(i==end) { r.add(end-start+1); start=end+1; } } return r; }
    // MEDIUM 8: Boats to Save People
    public static int numRescueBoats(int[] people,int limit) { Arrays.sort(people); int l=0,r=people.length-1,boats=0; while(l<=r) { if(people[l]+people[r]<=limit) l++; r--; boats++; } return boats; }
    // MEDIUM 9: Remove K Digits
    public static String removeKdigits(String num,int k) { Deque<Character> st=new ArrayDeque<>(); for(char c:num.toCharArray()) { while(k>0&&!st.isEmpty()&&st.peek()>c) { st.pop(); k--; } st.push(c); } while(k-->0) st.pop(); StringBuilder sb=new StringBuilder(); for(char c:st) sb.append(c); sb.reverse(); while(sb.length()>0&&sb.charAt(0)=='0') sb.deleteCharAt(0); return sb.length()==0?"0":sb.toString(); }
    // MEDIUM 10: Minimum Platforms (train station)
    public static int minPlatforms(int[] arrival,int[] departure) { Arrays.sort(arrival); Arrays.sort(departure); int plat=0,max=0,i=0,j=0; while(i<arrival.length) { if(arrival[i]<=departure[j]) { plat++; i++; } else { plat--; j++; } max=Math.max(max,plat); } return max; }

    // HARD 1: Candy Distribution
    public static int candy(int[] ratings) { int n=ratings.length; int[] candies=new int[n]; Arrays.fill(candies,1); for(int i=1;i<n;i++) if(ratings[i]>ratings[i-1]) candies[i]=candies[i-1]+1; for(int i=n-2;i>=0;i--) if(ratings[i]>ratings[i+1]) candies[i]=Math.max(candies[i],candies[i+1]+1); int sum=0; for(int c:candies) sum+=c; return sum; }
    // HARD 2: IPO (maximize capital)
    public static int findMaximizedCapital(int k,int w,int[] profits,int[] capital) { int n=profits.length; int[][] proj=new int[n][2]; for(int i=0;i<n;i++) proj[i]=new int[]{capital[i],profits[i]}; Arrays.sort(proj,Comparator.comparingInt(a->a[0])); PriorityQueue<Integer> pq=new PriorityQueue<>(Collections.reverseOrder()); int i=0; while(k-->0) { while(i<n&&proj[i][0]<=w) pq.offer(proj[i++][1]); if(pq.isEmpty()) break; w+=pq.poll(); } return w; }
    // HARD 3: Minimum Number of Refueling Stops
    public static int minRefuelStops(int target,int startFuel,int[][] stations) { PriorityQueue<Integer> pq=new PriorityQueue<>(Collections.reverseOrder()); int fuel=startFuel,stops=0,i=0; while(fuel<target) { while(i<stations.length&&stations[i][0]<=fuel) pq.offer(stations[i++][1]); if(pq.isEmpty()) return -1; fuel+=pq.poll(); stops++; } return stops; }
    // HARD 4: Course Schedule III
    public static int scheduleCourse(int[][] courses) { Arrays.sort(courses,Comparator.comparingInt(a->a[1])); PriorityQueue<Integer> pq=new PriorityQueue<>(Collections.reverseOrder()); int time=0; for(int[] c:courses) { time+=c[0]; pq.offer(c[0]); if(time>c[1]) time-=pq.poll(); } return pq.size(); }
    // HARD 5: Maximum Performance of Team
    public static int maxPerformance(int n,int[] speed,int[] efficiency,int k) { int[][] eng=new int[n][2]; for(int i=0;i<n;i++) eng[i]=new int[]{efficiency[i],speed[i]}; Arrays.sort(eng,(a,b)->b[0]-a[0]); PriorityQueue<Integer> pq=new PriorityQueue<>(); long sumS=0,max=0; for(int[] e:eng) { sumS+=e[1]; pq.offer(e[1]); if(pq.size()>k) sumS-=pq.poll(); max=Math.max(max,sumS*e[0]); } return (int)(max%1_000_000_007); }
    // HARD 6: Minimum Cost to Hire K Workers
    public static double mincostToHireWorkers(int[] quality,int[] wage,int k) { int n=quality.length; int[][] workers=new int[n][2]; for(int i=0;i<n;i++) workers[i]=new int[]{quality[i],wage[i]}; Arrays.sort(workers,(a,b)->Double.compare((double)a[1]/a[0],(double)b[1]/b[0])); PriorityQueue<Integer> pq=new PriorityQueue<>(Collections.reverseOrder()); int sumQ=0; double min=Double.MAX_VALUE; for(int[] w:workers) { sumQ+=w[0]; pq.offer(w[0]); if(pq.size()>k) sumQ-=pq.poll(); if(pq.size()==k) min=Math.min(min,sumQ*((double)w[1]/w[0])); } return min; }
    // HARD 7: Minimum Interval to Include Each Query
    public static int[] minInterval(int[][] intervals,int[] queries) { Arrays.sort(intervals,Comparator.comparingInt(a->a[0])); int[][] sortedQ=new int[queries.length][2]; for(int i=0;i<queries.length;i++) sortedQ[i]=new int[]{queries[i],i}; Arrays.sort(sortedQ,Comparator.comparingInt(a->a[0])); PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[0])); int[] ans=new int[queries.length]; Arrays.fill(ans,-1); int j=0; for(int[] q:sortedQ) { while(j<intervals.length&&intervals[j][0]<=q[0]) { pq.offer(new int[]{intervals[j][1]-intervals[j][0]+1,intervals[j][1]}); j++; } while(!pq.isEmpty()&&pq.peek()[1]<q[0]) pq.poll(); if(!pq.isEmpty()) ans[q[1]]=pq.peek()[0]; } return ans; }
    // HARD 8: Patching Array
    public static int minPatches(int[] nums,int n) { long miss=1; int patches=0,i=0; while(miss<=n) { if(i<nums.length&&nums[i]<=miss) { miss+=nums[i++]; } else { miss+=miss; patches++; } } return patches; }
    // HARD 9: Create Maximum Number
    public static int[] maxNumber(int[] nums1,int[] nums2,int k) { int[] best=new int[k]; for(int i=Math.max(0,k-nums2.length);i<=Math.min(k,nums1.length);i++) { int[] a=maxSubseq(nums1,i),b=maxSubseq(nums2,k-i); int[] merged=merge(a,b); if(compare(merged,0,best,0)>0) best=merged; } return best; }
    private static int[] maxSubseq(int[] nums,int k) { int[] r=new int[k]; int drop=nums.length-k,j=0; for(int n:nums) { while(j>0&&drop>0&&r[j-1]<n) { j--; drop--; } if(j<k) r[j++]=n; else drop--; } return r; }
    private static int[] merge(int[] a,int[] b) { int[] r=new int[a.length+b.length]; int i=0,j=0,k=0; while(i<a.length&&j<b.length) r[k++]=compare(a,i,b,j)>=0?a[i++]:b[j++]; while(i<a.length) r[k++]=a[i++]; while(j<b.length) r[k++]=b[j++]; return r; }
    private static int compare(int[] a,int i,int[] b,int j) { while(i<a.length&&j<b.length&&a[i]==b[j]) { i++; j++; } if(i==a.length) return -1; if(j==b.length) return 1; return a[i]-b[j]; }
    // HARD 10: Reorganize String
    public static String reorganizeString(String s) { int[] f=new int[26]; for(char c:s.toCharArray()) f[c-'a']++; PriorityQueue<int[]> pq=new PriorityQueue<>((a,b)->b[1]-a[1]); for(int i=0;i<26;i++) if(f[i]>0) pq.offer(new int[]{i,f[i]}); StringBuilder sb=new StringBuilder(); int[] prev=null; while(!pq.isEmpty()) { int[] cur=pq.poll(); sb.append((char)(cur[0]+'a')); cur[1]--; if(prev!=null&&prev[1]>0) pq.offer(prev); prev=cur; } return sb.length()==s.length()?sb.toString():""; }

    public static void main(String[] args) {
        System.out.println("=== GREEDY PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        System.out.println("1. Max Profit: " + maxProfit(new int[]{7,1,5,3,6,4}));
        System.out.println("2. Assign Cookies: " + findContentChildren(new int[]{1,2,3},new int[]{1,1}));
        System.out.println("3. Lemonade: " + lemonadeChange(new int[]{5,5,5,10,20}));
        System.out.println("4. Max Units: " + maximumUnits(new int[][]{{1,3},{2,2},{3,1}},4));
        System.out.println("5-6: Flower/palindrome");
        System.out.println("7. Max Profit II: " + maxProfitII(new int[]{7,1,5,3,6,4}));
        System.out.println("8. Longest Palin: " + longestPalindrome("abccccdd"));
        System.out.println("9. Min Ops Inc: " + minOperations(new int[]{1,1,1}));
        System.out.println("10. Array Pair: " + arrayPairSum(new int[]{1,4,3,2}));
        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Jump Game: " + canJump(new int[]{2,3,1,1,4}));
        System.out.println("12. Jump II: " + jump(new int[]{2,3,1,1,4}));
        System.out.println("13. Gas Station: " + canCompleteCircuit(new int[]{1,2,3,4,5},new int[]{3,4,5,1,2}));
        System.out.println("14. Task Sched: " + leastInterval(new char[]{'A','A','A','B','B','B'},2));
        System.out.println("15. Erase Overlap: " + eraseOverlapIntervals(new int[][]{{1,2},{2,3},{3,4},{1,3}}));
        System.out.println("16. Min Arrows: " + findMinArrowShots(new int[][]{{10,16},{2,8},{1,6},{7,12}}));
        System.out.println("17. Partition: " + partitionLabels("ababcbacadefegdehijhklij"));
        System.out.println("18. Boats: " + numRescueBoats(new int[]{3,2,2,1},3));
        System.out.println("19. Remove K: " + removeKdigits("1432219",3));
        System.out.println("20. Min Platforms: " + minPlatforms(new int[]{900,940,950,1100,1500,1800},new int[]{910,1200,1120,1130,1900,2000}));
        System.out.println("\n--- HARD ---");
        System.out.println("21. Candy: " + candy(new int[]{1,0,2}));
        System.out.println("22. IPO: " + findMaximizedCapital(2,0,new int[]{1,2,3},new int[]{0,1,1}));
        System.out.println("23. Refuel: " + minRefuelStops(100,10,new int[][]{{10,60},{20,30},{30,30},{60,40}}));
        System.out.println("24. Course III: " + scheduleCourse(new int[][]{{100,200},{200,1300},{1000,1250},{2000,3200}}));
        System.out.println("25. Max Perf: " + maxPerformance(6,new int[]{2,10,3,1,5,8},new int[]{5,4,3,9,7,2},2));
        System.out.println("26. Hire K: " + mincostToHireWorkers(new int[]{10,20,5},new int[]{70,50,30},2));
        System.out.println("27. Min Interval: " + Arrays.toString(minInterval(new int[][]{{1,4},{2,4},{3,6},{4,4}},new int[]{2,3,4,5})));
        System.out.println("28. Patches: " + minPatches(new int[]{1,3},6));
        System.out.println("29. Max Number: " + Arrays.toString(maxNumber(new int[]{3,4,6,5},new int[]{9,1,2,5,8,3},5)));
        System.out.println("30. Reorganize: " + reorganizeString("aab"));
    }
}

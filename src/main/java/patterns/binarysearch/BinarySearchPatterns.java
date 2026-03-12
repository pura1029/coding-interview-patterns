package patterns.binarysearch;

import java.util.*;

/**
 * PATTERN 11: BINARY SEARCH VARIANTS
 * Halves search space each iteration for O(log n). Works on sorted data and monotonic predicates.
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class BinarySearchPatterns {

    /**
     * Binary Search (classic)
     *
     * <p><b>Approach:</b> Binary Search (classic). Compare mid, halve search space.
     *
     * @param nums the nums parameter
     * @param target the target parameter
     * @return the computed result
     */
    public static int search(int[] nums, int target) { int lo=0,hi=nums.length-1; while(lo<=hi) { int m=(lo+hi)/2; if(nums[m]==target) return m; if(nums[m]<target) lo=m+1; else hi=m-1; } return -1; }
    /**
     * First Bad Version
     *
     * <p><b>Approach:</b> First Bad Version. Find first true in boolean array.
     *
     * @param n the n parameter
     * @param bad the bad parameter
     * @return the computed result
     */
    public static int firstBadVersion(int n, int bad) { int lo=1,hi=n; while(lo<hi) { int m=lo+(hi-lo)/2; if(m>=bad) hi=m; else lo=m+1; } return lo; }
    /**
     * Search Insert Position
     *
     * <p><b>Approach:</b> Search Insert Position. Lower bound insertion point.
     *
     * @param nums the nums parameter
     * @param target the target parameter
     * @return the computed result
     */
    public static int searchInsert(int[] nums, int target) { int lo=0,hi=nums.length; while(lo<hi) { int m=(lo+hi)/2; if(nums[m]<target) lo=m+1; else hi=m; } return lo; }
    /**
     * Count Negative Numbers in Sorted Matrix
     *
     * <p><b>Approach:</b> Count Negative Numbers in Sorted Matrix. Step from bottom-left corner.
     *
     * @param grid the grid parameter
     * @return the computed result
     */
    public static int countNegatives(int[][] grid) { int c=0; for(int[] row:grid) { int lo=0,hi=row.length; while(lo<hi) { int m=(lo+hi)/2; if(row[m]<0) hi=m; else lo=m+1; } c+=row.length-lo; } return c; }
    /**
     * Sqrt(x)
     *
     * <p><b>Approach:</b> Sqrt(x). Binary search on answer [0, x].
     *
     * @param x the x parameter
     * @return the computed result
     */
    public static int mySqrt(int x) { int lo=0,hi=x; while(lo<=hi) { long m=(lo+hi)/2; if(m*m<=x && (m+1)*(m+1)>x) return (int)m; if(m*m<x) lo=(int)m+1; else hi=(int)m-1; } return lo; }
    /**
     * Guess Number Higher or Lower
     *
     * <p><b>Approach:</b> Guess Number Higher or Lower. Binary search with API feedback.
     *
     * @param n the n parameter
     * @param pick the pick parameter
     * @return the computed result
     */
    public static int guessNumber(int n, int pick) { int lo=1,hi=n; while(lo<=hi) { int m=lo+(hi-lo)/2; if(m==pick) return m; if(m<pick) lo=m+1; else hi=m-1; } return -1; }
    /**
     * Valid Perfect Square
     *
     * <p><b>Approach:</b> Valid Perfect Square. Binary search mid*mid == n.
     *
     * @param num the num parameter
     * @return the computed result
     */
    public static boolean isPerfectSquare(int num) { long lo=1,hi=num; while(lo<=hi) { long m=(lo+hi)/2; if(m*m==num) return true; if(m*m<num) lo=m+1; else hi=m-1; } return false; }
    /**
     * Arrange Coins
     *
     * <p><b>Approach:</b> Arrange Coins. Binary search on complete rows.
     *
     * @param n the n parameter
     * @return the computed result
     */
    public static int arrangeCoins(int n) { long lo=0,hi=n; while(lo<=hi) { long m=(lo+hi)/2; if(m*(m+1)/2<=n) lo=m+1; else hi=m-1; } return (int)(lo-1); }
    /**
     * Check If N and Its Double Exist
     *
     * <p><b>Approach:</b> Check If N and Its Double Exist. Sort + binary search for 2x.
     *
     * @param arr the arr parameter
     * @return the computed result
     */
    public static boolean checkIfExist(int[] arr) { Set<Integer> s = new HashSet<>(); for(int n:arr) { if(s.contains(2*n)||(n%2==0&&s.contains(n/2))) return true; s.add(n); } return true; }
    /**
     * Intersection of Two Arrays
     *
     * <p><b>Approach:</b> Intersection of Two Arrays. Sort + binary search lookups.
     *
     * @param n1 the n1 parameter
     * @param n2 the n2 parameter
     * @return the computed result
     */
    public static int[] intersection(int[] n1, int[] n2) { Set<Integer> s1=new HashSet<>(),r=new HashSet<>(); for(int n:n1) s1.add(n); for(int n:n2) if(s1.contains(n)) r.add(n); return r.stream().mapToInt(Integer::intValue).toArray(); }

    /**
     * First and Last Position
     *
     * <p><b>Approach:</b> First and Last Position. Two binary searches: lower + upper bound.
     *
     * @param nums the nums parameter
     * @param t the t parameter
     * @return the computed result
     */
    public static int[] searchRange(int[] nums, int t) { return new int[]{bound(nums,t,true),bound(nums,t,false)}; }
    private static int bound(int[] nums, int t, boolean first) { int lo=0,hi=nums.length-1,r=-1; while(lo<=hi) { int m=(lo+hi)/2; if(nums[m]==t) { r=m; if(first) hi=m-1; else lo=m+1; } else if(nums[m]<t) lo=m+1; else hi=m-1; } return r; }
    /**
     * Search in Rotated Sorted Array
     *
     * <p><b>Approach:</b> Search in Rotated Sorted Array. Identify sorted half, search accordingly.
     *
     * @param nums the nums parameter
     * @param t the t parameter
     * @return the computed result
     */
    public static int searchRotated(int[] nums, int t) { int lo=0,hi=nums.length-1; while(lo<=hi) { int m=(lo+hi)/2; if(nums[m]==t) return m; if(nums[lo]<=nums[m]) { if(t>=nums[lo]&&t<nums[m]) hi=m-1; else lo=m+1; } else { if(t>nums[m]&&t<=nums[hi]) lo=m+1; else hi=m-1; } } return -1; }
    /**
     * Find Minimum in Rotated Sorted Array
     *
     * <p><b>Approach:</b> Find Minimum in Rotated Sorted Array. Compare mid with right boundary.
     *
     * @param nums the nums parameter
     * @return the computed result
     */
    public static int findMin(int[] nums) { int lo=0,hi=nums.length-1; while(lo<hi) { int m=(lo+hi)/2; if(nums[m]>nums[hi]) lo=m+1; else hi=m; } return nums[lo]; }
    /**
     * Find Peak Element
     *
     * <p><b>Approach:</b> Find Peak Element. Move toward higher neighbor.
     *
     * @param nums the nums parameter
     * @return the computed result
     */
    public static int findPeakElement(int[] nums) { int lo=0,hi=nums.length-1; while(lo<hi) { int m=(lo+hi)/2; if(nums[m]<nums[m+1]) lo=m+1; else hi=m; } return lo; }
    /**
     * Search a 2D Matrix
     *
     * <p><b>Approach:</b> Search a 2D Matrix. Treat as flattened sorted array.
     *
     * @param matrix the matrix parameter
     * @param t the t parameter
     * @return the computed result
     */
    public static boolean searchMatrix(int[][] matrix, int t) { int m=matrix.length,n=matrix[0].length,lo=0,hi=m*n-1; while(lo<=hi) { int mid=(lo+hi)/2; int v=matrix[mid/n][mid%n]; if(v==t) return true; if(v<t) lo=mid+1; else hi=mid-1; } return false; }
    /**
     * Koko Eating Bananas
     *
     * <p><b>Approach:</b> Koko Eating Bananas. Binary search on eating speed.
     *
     * @param piles the piles parameter
     * @param h the h parameter
     * @return the computed result
     */
    public static int minEatingSpeed(int[] piles, int h) { int lo=1,hi=Arrays.stream(piles).max().getAsInt(); while(lo<hi) { int m=(lo+hi)/2; int hours=0; for(int p:piles) hours+=(p+m-1)/m; if(hours<=h) hi=m; else lo=m+1; } return lo; }
    /**
     * Capacity To Ship Packages Within D Days
     *
     * <p><b>Approach:</b> Capacity To Ship Packages Within D Days. Binary search on capacity.
     *
     * @param weights the weights parameter
     * @param days the days parameter
     * @return the computed result
     */
    public static int shipWithinDays(int[] weights, int days) { int lo=Arrays.stream(weights).max().getAsInt(),hi=Arrays.stream(weights).sum(); while(lo<hi) { int m=(lo+hi)/2,d=1,cur=0; for(int w:weights) { if(cur+w>m) { d++; cur=0; } cur+=w; } if(d<=days) hi=m; else lo=m+1; } return lo; }
    /**
     * Single Element in a Sorted Array
     *
     * <p><b>Approach:</b> Single Element in a Sorted Array. Binary search on pair alignment.
     *
     * @param nums the nums parameter
     * @return the computed result
     */
    public static int singleNonDuplicate(int[] nums) { int lo=0,hi=nums.length-1; while(lo<hi) { int m=(lo+hi)/2; if(m%2==1) m--; if(nums[m]==nums[m+1]) lo=m+2; else hi=m; } return nums[lo]; }
    /**
     * Time Based Key-Value Store
     *
     * <p><b>Approach:</b> Time Based Key-Value Store. Binary search on timestamps.
     */
    static class TimeMap { Map<String,List<int[]>> map=new HashMap<>(); Map<String,List<String>> vals=new HashMap<>(); void set(String k, String v, int t) { map.computeIfAbsent(k,x->new ArrayList<>()).add(new int[]{t}); vals.computeIfAbsent(k,x->new ArrayList<>()).add(v); } String get(String k, int t) { if(!map.containsKey(k)) return ""; List<int[]> times=map.get(k); int lo=0,hi=times.size()-1,r=-1; while(lo<=hi) { int m=(lo+hi)/2; if(times.get(m)[0]<=t) { r=m; lo=m+1; } else hi=m-1; } return r>=0?vals.get(k).get(r):""; } }
    /**
     * Minimum Number of Days to Make m Bouquets
     *
     * <p><b>Approach:</b> Minimum Number of Days to Make m Bouquets. Binary search on days.
     *
     * @param bloom the bloom parameter
     * @param m the m parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static int minDays(int[] bloom, int m, int k) { if((long)m*k>bloom.length) return -1; int lo=1,hi=Arrays.stream(bloom).max().getAsInt(); while(lo<hi) { int mid=(lo+hi)/2,bouquets=0,flowers=0; for(int b:bloom) { if(b<=mid) flowers++; else flowers=0; if(flowers==k) { bouquets++; flowers=0; } } if(bouquets>=m) hi=mid; else lo=mid+1; } return lo; }

    /**
     * Median of Two Sorted Arrays
     *
     * <p><b>Approach:</b> Median of Two Sorted Arrays. Binary search on partition position.
     *
     * @param a the a parameter
     * @param b the b parameter
     * @return the computed result
     */
    public static double findMedianSortedArrays(int[] a, int[] b) { if(a.length>b.length) return findMedianSortedArrays(b,a); int m=a.length,n=b.length,lo=0,hi=m; while(lo<=hi) { int i=lo+(hi-lo)/2,j=(m+n+1)/2-i; int l1=i==0?Integer.MIN_VALUE:a[i-1],r1=i==m?Integer.MAX_VALUE:a[i]; int l2=j==0?Integer.MIN_VALUE:b[j-1],r2=j==n?Integer.MAX_VALUE:b[j]; if(l1<=r2&&l2<=r1) return(m+n)%2==0?(Math.max(l1,l2)+Math.min(r1,r2))/2.0:Math.max(l1,l2); if(l1>r2) hi=i-1; else lo=i+1; } throw new IllegalArgumentException(); }
    /**
     * Split Array Largest Sum
     *
     * <p><b>Approach:</b> Split Array Largest Sum. Binary search on max-sum, greedy check.
     *
     * @param nums the nums parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static int splitArray(int[] nums, int k) { int lo=Arrays.stream(nums).max().getAsInt(),hi=Arrays.stream(nums).sum(); while(lo<hi) { int m=(lo+hi)/2,parts=1,sum=0; for(int n:nums) { if(sum+n>m) { parts++; sum=0; } sum+=n; } if(parts<=k) hi=m; else lo=m+1; } return lo; }
    /**
     * Find in Mountain Array
     *
     * <p><b>Approach:</b> Find in Mountain Array. Find peak, search both sides.
     *
     * @param arr the arr parameter
     * @param target the target parameter
     * @return the computed result
     */
    public static int findInMountainArray(int[] arr, int target) { int peak=0; int lo=0,hi=arr.length-1; while(lo<hi) { int m=(lo+hi)/2; if(arr[m]<arr[m+1]) lo=m+1; else hi=m; } peak=lo; lo=0; hi=peak; while(lo<=hi) { int m=(lo+hi)/2; if(arr[m]==target) return m; if(arr[m]<target) lo=m+1; else hi=m-1; } lo=peak; hi=arr.length-1; while(lo<=hi) { int m=(lo+hi)/2; if(arr[m]==target) return m; if(arr[m]>target) lo=m+1; else hi=m-1; } return -1; }
    /**
     * Kth Smallest Number in Multiplication Table
     *
     * <p><b>Approach:</b> Kth Smallest Number in Multiplication Table. Binary search on value, count ≤ x.
     *
     * @param m the m parameter
     * @param n the n parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static int findKthNumber(int m, int n, int k) { int lo=1,hi=m*n; while(lo<hi) { int mid=(lo+hi)/2,cnt=0; for(int i=1;i<=m;i++) cnt+=Math.min(mid/i,n); if(cnt>=k) hi=mid; else lo=mid+1; } return lo; }
    /**
     * Aggressive Cows / Magnetic Balls (max min distance)
     *
     * <p><b>Approach:</b> Aggressive Cows / Magnetic Balls (max min distance). Binary search on distance.
     *
     * @param positions the positions parameter
     * @param m the m parameter
     * @return the computed result
     */
    public static int maxMinDistance(int[] positions, int m) { Arrays.sort(positions); int lo=1,hi=positions[positions.length-1]-positions[0]; while(lo<hi) { int mid=lo+(hi-lo+1)/2; int cnt=1,last=positions[0]; for(int i=1;i<positions.length;i++) { if(positions[i]-last>=mid) { cnt++; last=positions[i]; } } if(cnt>=m) lo=mid; else hi=mid-1; } return lo; }
    /**
     * Nth Magical Number
     *
     * <p><b>Approach:</b> Nth Magical Number. Binary search with LCM counting.
     *
     * @param n the n parameter
     * @param a the a parameter
     * @param b the b parameter
     * @return the computed result
     */
    public static int nthMagicalNumber(int n, int a, int b) { long MOD=1_000_000_007,lcm=(long)a/gcd(a,b)*b; long lo=1,hi=(long)n*Math.min(a,b); while(lo<hi) { long m=(lo+hi)/2; if(m/a+m/b-m/lcm>=n) hi=m; else lo=m+1; } return (int)(lo%MOD); }
    private static int gcd(int a,int b) { return b==0?a:gcd(b,a%b); }
    /**
     * Russian Doll Envelopes
     *
     * <p><b>Approach:</b> Russian Doll Envelopes. Sort + LIS with binary search.
     *
     * @param envelopes the envelopes parameter
     * @return the computed result
     */
    public static int maxEnvelopes(int[][] envelopes) { Arrays.sort(envelopes,(a,b)->a[0]!=b[0]?a[0]-b[0]:b[1]-a[1]); List<Integer> dp=new ArrayList<>(); for(int[] e:envelopes) { int pos=Collections.binarySearch(dp,e[1]); if(pos<0) pos=-(pos+1); if(pos==dp.size()) dp.add(e[1]); else dp.set(pos,e[1]); } return dp.size(); }
    /**
     * Minimum Speed to Arrive on Time
     *
     * <p><b>Approach:</b> Minimum Speed to Arrive on Time. Binary search on speed.
     *
     * @param dist the dist parameter
     * @param hour the hour parameter
     * @return the computed result
     */
    public static int minSpeedOnTime(int[] dist, double hour) { int lo=1,hi=10_000_000; while(lo<hi) { int m=(lo+hi)/2; double time=0; for(int i=0;i<dist.length-1;i++) time+=Math.ceil((double)dist[i]/m); time+=(double)dist[dist.length-1]/m; if(time<=hour) hi=m; else lo=m+1; } return lo>10_000_000?-1:lo; }
    /**
     * Count of Smaller Numbers After Self (using BIT)
     *
     * <p><b>Approach:</b> Count of Smaller Numbers After Self (using BIT). Binary Indexed Tree for rank queries.
     *
     * @param nums the nums parameter
     * @return the computed result
     */
    public static List<Integer> countSmaller(int[] nums) { int offset=10001,size=2*10002; int[] bit=new int[size+1]; Integer[] r=new Integer[nums.length]; for(int i=nums.length-1;i>=0;i--) { int idx=nums[i]+offset; r[i]=query(bit,idx-1); update(bit,idx,size); } return Arrays.asList(r); }
    private static void update(int[] bit,int i,int n) { for(i++;i<=n;i+=i&(-i)) bit[i]++; }
    private static int query(int[] bit,int i) { int s=0; for(i++;i>0;i-=i&(-i)) s+=bit[i]; return s; }
    /**
     * Maximum Running Time of N Computers
     *
     * <p><b>Approach:</b> Maximum Running Time of N Computers. Binary search on runtime.
     *
     * @param n the n parameter
     * @param batteries the batteries parameter
     * @return the computed result
     */
    public static long maxRunTime(int n, int[] batteries) { long lo=1,hi=0; for(int b:batteries) hi+=b; while(lo<hi) { long m=lo+(hi-lo+1)/2,total=0; for(int b:batteries) total+=Math.min(b,m); if(total>=m*n) lo=m; else hi=m-1; } return lo; }

    public static void main(String[] args) {
        System.out.println("=== BINARY SEARCH PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        // new int[]{...} → creates array literal; for-loop or binary search with if-else to locate target
        System.out.println("1. Search: " + search(new int[]{-1,0,3,5,9,12}, 9));
        // firstBadVersion() processes input; uses for/while loop with conditional checks for result computation
        System.out.println("2. First Bad: " + firstBadVersion(5, 4));
        // while (lo <= hi): if (nums[mid] == target) return mid, else if (< target) lo=mid+1, else hi=mid-1; return lo as insert position
        System.out.println("3. Insert Pos: " + searchInsert(new int[]{1,3,5,6}, 5));
        // new int[]{...} → creates array literal; for-loop with if (condition) count/accumulate
        System.out.println("4. Count Neg: " + countNegatives(new int[][]{{4,3,2,-1},{3,2,1,-1},{1,1,-1,-2},{-1,-1,-2,-3}}));
        // mySqrt() processes input; uses for/while loop with conditional checks for result computation
        System.out.println("5. Sqrt: " + mySqrt(8));
        // for-loop with if (condition) count/accumulate
        System.out.println("6. Guess: " + guessNumber(10, 6));
        // returns boolean; uses if-else conditional checks
        System.out.println("7. Perfect Sq: " + isPerfectSquare(16));
        // arrangeCoins() processes input; uses for/while loop with conditional checks for result computation
        System.out.println("8. Coins: " + arrangeCoins(8));
        // Double: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("9. Double: (check exists)");
        // new int[]{...} → creates array literal; new int[]{...} → creates array literal
        System.out.println("10. Intersect: " + Arrays.toString(intersection(new int[]{1,2,2,1}, new int[]{2,2})));
        System.out.println("\n--- MEDIUM ---");
        // calls findFirst + findLast, each uses while (lo <= hi): findFirst if (nums[mid] >= target) hi=mid-1, findLast if (nums[mid] <= target) lo=mid+1
        System.out.println("11. Range: " + Arrays.toString(searchRange(new int[]{5,7,7,8,8,10}, 8)));
        // while (lo <= hi): determine sorted half with if (nums[lo] <= nums[mid]), then if target in that half adjust bounds — two-level if-else
        System.out.println("12. Rotated: " + searchRotated(new int[]{4,5,6,7,0,1,2}, 0));
        // while (lo < hi): if (nums[mid] > nums[hi]) lo=mid+1, else hi=mid — same as minRotatedSorted with duplicates handling
        System.out.println("13. Find Min: " + findMin(new int[]{3,4,5,1,2}));
        // new int[]{...} → creates array literal; for-loop or binary search with if-else to locate target
        System.out.println("14. Peak: " + findPeakElement(new int[]{1,2,3,1}));
        // new int[][]{{...}} → 2D sorted matrix; treats as 1D: while (lo <= hi), row=mid/cols, col=mid%cols, if (val == target) return true
        System.out.println("15. 2D Matrix: " + searchMatrix(new int[][]{{1,3,5,7},{10,11,16,20},{23,30,34,60}}, 3));
        // binary search on answer: while (lo < hi), for-loop counts hours at speed mid; if (hours <= h) hi=mid, else lo=mid+1
        System.out.println("16. Koko: " + minEatingSpeed(new int[]{3,6,7,11}, 8));
        // binary search on capacity: while (lo <= hi), for-loop simulates loading; if (days <= d) hi=mid-1, else lo=mid+1 — search on answer
        System.out.println("17. Ship: " + shipWithinDays(new int[]{1,2,3,4,5,6,7,8,9,10}, 5));
        // binary search on answer; while (lo < hi): for-loop counts feasible with mid; if (feasible >= k) hi=mid, else lo=mid+1 — kth smallest pair distance
        System.out.println("18. Single: " + singleNonDuplicate(new int[]{1,1,2,3,3,4,4,8,8}));
        // binary search on difficulty threshold; for-loop checks if (can achieve); while (lo <= hi) narrows answer — search on answer pattern
        System.out.println("19. TimeMap: (key-value store)");
        // binary search + greedy validation; while (lo <= hi): simulate with for-loop; if (valid) update answer — parametric search
        System.out.println("20. Min Days: " + minDays(new int[]{1,10,3,10,2}, 3, 1));
        System.out.println("\n--- HARD ---");
        // two sorted arrays; binary search on shorter array: while (lo <= hi), partitions both arrays, if (conditions met) compute median, else adjust
        System.out.println("21. Median: " + findMedianSortedArrays(new int[]{1,3}, new int[]{2}));
        // binary search on max sum: while (lo <= hi), greedy for-loop splits; if (pieces <= m) hi=mid-1, else lo=mid+1 — minimize the maximum
        System.out.println("22. Split Arr: " + splitArray(new int[]{7,2,5,10,8}, 2));
        // binary search on answer (max min distance); while (lo <= hi): greedy for-loop places elements; if (placed >= m) lo=mid+1 — aggressive cows pattern
        System.out.println("23. Mountain: " + findInMountainArray(new int[]{1,2,3,4,5,3,1}, 3));
        // binary search on time; while (lo <= hi): for-loop sums floor(time/speed); if (total >= n) hi=mid-1 — search on answer
        System.out.println("24. Kth Mul: " + findKthNumber(3, 3, 5));
        // binary search + sliding window; for each window: binary search finds threshold; if (operations <= k) window is valid — combined techniques
        System.out.println("25. Max Min Dist: " + maxMinDistance(new int[]{1,2,8,4,9}, 3));
        // binary search on result; while (lo <= hi): for-loop validates with greedy; if (feasible) update best — minimization via binary search
        System.out.println("26. Nth Magical: " + nthMagicalNumber(4, 2, 3));
        // nested binary search or merge sort approach; for each element: binary search counts smaller elements in remaining — O(n log n) inversion count
        System.out.println("27. Envelopes: " + maxEnvelopes(new int[][]{{5,4},{6,4},{6,7},{2,3}}));
        // binary search + matrix staircase; while (lo < hi): for-loop counts elements <= mid per row; if (count <= k) lo=mid+1 — sorted matrix search
        System.out.println("28. Min Speed: " + minSpeedOnTime(new int[]{1,3,2}, 6));
        // binary search on answer range; while (lo <= hi): simulation/validation with for-loop; if (valid) narrow range — search on answer
        System.out.println("29. Count Smaller: " + countSmaller(new int[]{5,2,6,1}));
        // binary search on answer; while (lo <= hi): greedy validation with for-loop; if (condition met) update bounds — parametric binary search
        System.out.println("30. Max Runtime: " + maxRunTime(2, new int[]{3,3,3}));
    }
}

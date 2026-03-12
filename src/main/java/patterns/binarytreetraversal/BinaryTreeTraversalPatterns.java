package patterns.binarytreetraversal;

import java.util.*;

/**
 * PATTERN 12: BINARY TREE TRAVERSAL
 * Visit every node in specific order: Inorder, Preorder, Postorder, Level-order.
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class BinaryTreeTraversalPatterns {
    static class TreeNode { int val; TreeNode left, right; TreeNode(int v) { val=v; } TreeNode(int v,TreeNode l,TreeNode r) { val=v; left=l; right=r; } }

    /**
     * Inorder Traversal
     *
     * <p><b>Approach:</b> Inorder Traversal. Left → Root → Right (sorted for BST).
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static List<Integer> inorderTraversal(TreeNode root) { List<Integer> r=new ArrayList<>(); Deque<TreeNode> st=new ArrayDeque<>(); TreeNode c=root; while(c!=null||!st.isEmpty()) { while(c!=null) { st.push(c); c=c.left; } c=st.pop(); r.add(c.val); c=c.right; } return r; }
    /**
     * Preorder Traversal
     *
     * <p><b>Approach:</b> Preorder Traversal. Root → Left → Right.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static List<Integer> preorderTraversal(TreeNode root) { List<Integer> r=new ArrayList<>(); if(root==null) return r; Deque<TreeNode> st=new ArrayDeque<>(); st.push(root); while(!st.isEmpty()) { TreeNode n=st.pop(); r.add(n.val); if(n.right!=null) st.push(n.right); if(n.left!=null) st.push(n.left); } return r; }
    /**
     * Postorder Traversal
     *
     * <p><b>Approach:</b> Postorder Traversal. Left → Right → Root.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static List<Integer> postorderTraversal(TreeNode root) { LinkedList<Integer> r=new LinkedList<>(); if(root==null) return r; Deque<TreeNode> st=new ArrayDeque<>(); st.push(root); while(!st.isEmpty()) { TreeNode n=st.pop(); r.addFirst(n.val); if(n.left!=null) st.push(n.left); if(n.right!=null) st.push(n.right); } return r; }
    /**
     * Maximum Depth
     *
     * <p><b>Approach:</b> Maximum Depth. max(left, right) + 1.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static int maxDepth(TreeNode root) { if(root==null) return 0; return 1+Math.max(maxDepth(root.left), maxDepth(root.right)); }
    /**
     * Symmetric Tree
     *
     * <p><b>Approach:</b> Symmetric Tree. Mirror comparison of left/right.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static boolean isSymmetric(TreeNode root) { return isMirror(root, root); }
    private static boolean isMirror(TreeNode a, TreeNode b) { if(a==null&&b==null) return true; if(a==null||b==null) return false; return a.val==b.val&&isMirror(a.left,b.right)&&isMirror(a.right,b.left); }
    /**
     * Invert Binary Tree
     *
     * <p><b>Approach:</b> Invert Binary Tree. Swap left and right recursively.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static TreeNode invertTree(TreeNode root) { if(root==null) return null; TreeNode t=root.left; root.left=invertTree(root.right); root.right=invertTree(t); return root; }
    /**
     * Same Tree
     *
     * <p><b>Approach:</b> Same Tree. Compare structure and values.
     *
     * @param p the p parameter
     * @param q the q parameter
     * @return the computed result
     */
    public static boolean isSameTree(TreeNode p, TreeNode q) { if(p==null&&q==null) return true; if(p==null||q==null||p.val!=q.val) return false; return isSameTree(p.left,q.left)&&isSameTree(p.right,q.right); }
    /**
     * Minimum Depth
     *
     * <p><b>Approach:</b> Minimum Depth. Shortest root-to-leaf path.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static int minDepth(TreeNode root) { if(root==null) return 0; if(root.left==null) return 1+minDepth(root.right); if(root.right==null) return 1+minDepth(root.left); return 1+Math.min(minDepth(root.left),minDepth(root.right)); }
    /**
     * Path Sum
     *
     * <p><b>Approach:</b> Path Sum. Subtract node value, check leaf.
     *
     * @param root the root parameter
     * @param target the target parameter
     * @return the computed result
     */
    public static boolean hasPathSum(TreeNode root, int target) { if(root==null) return false; if(root.left==null&&root.right==null) return root.val==target; return hasPathSum(root.left,target-root.val)||hasPathSum(root.right,target-root.val); }
    /**
     * Count Complete Tree Nodes
     *
     * <p><b>Approach:</b> Count Complete Tree Nodes. Binary search on last level.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static int countNodes(TreeNode root) { if(root==null) return 0; return 1+countNodes(root.left)+countNodes(root.right); }

    /**
     * Level Order Traversal
     *
     * <p><b>Approach:</b> Level Order Traversal. BFS with level grouping.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static List<List<Integer>> levelOrder(TreeNode root) { List<List<Integer>> r=new ArrayList<>(); if(root==null) return r; Queue<TreeNode> q=new LinkedList<>(); q.offer(root); while(!q.isEmpty()) { int sz=q.size(); List<Integer> lv=new ArrayList<>(); for(int i=0;i<sz;i++) { TreeNode n=q.poll(); lv.add(n.val); if(n.left!=null) q.offer(n.left); if(n.right!=null) q.offer(n.right); } r.add(lv); } return r; }
    /**
     * Zigzag Level Order
     *
     * <p><b>Approach:</b> Zigzag Level Order. Alternate left-right per level.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static List<List<Integer>> zigzagLevelOrder(TreeNode root) { List<List<Integer>> r=new ArrayList<>(); if(root==null) return r; Queue<TreeNode> q=new LinkedList<>(); q.offer(root); boolean ltr=true; while(!q.isEmpty()) { int sz=q.size(); LinkedList<Integer> lv=new LinkedList<>(); for(int i=0;i<sz;i++) { TreeNode n=q.poll(); if(ltr) lv.addLast(n.val); else lv.addFirst(n.val); if(n.left!=null) q.offer(n.left); if(n.right!=null) q.offer(n.right); } r.add(lv); ltr=!ltr; } return r; }
    /**
     * Validate BST
     *
     * <p><b>Approach:</b> Validate BST. Inorder must be strictly increasing.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static boolean isValidBST(TreeNode root) { return validateBST(root, Long.MIN_VALUE, Long.MAX_VALUE); }
    private static boolean validateBST(TreeNode n, long lo, long hi) { if(n==null) return true; if(n.val<=lo||n.val>=hi) return false; return validateBST(n.left,lo,n.val)&&validateBST(n.right,n.val,hi); }
    /**
     * Kth Smallest Element in BST
     *
     * <p><b>Approach:</b> Kth Smallest Element in BST. Inorder traversal, count to k.
     *
     * @param root the root parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static int kthSmallest(TreeNode root, int k) { Deque<TreeNode> st=new ArrayDeque<>(); TreeNode c=root; while(true) { while(c!=null) { st.push(c); c=c.left; } c=st.pop(); if(--k==0) return c.val; c=c.right; } }
    /**
     * Binary Tree Right Side View
     *
     * <p><b>Approach:</b> Binary Tree Right Side View. Last node at each BFS level.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static List<Integer> rightSideView(TreeNode root) { List<Integer> r=new ArrayList<>(); if(root==null) return r; Queue<TreeNode> q=new LinkedList<>(); q.offer(root); while(!q.isEmpty()) { int sz=q.size(); for(int i=0;i<sz;i++) { TreeNode n=q.poll(); if(i==sz-1) r.add(n.val); if(n.left!=null) q.offer(n.left); if(n.right!=null) q.offer(n.right); } } return r; }
    /**
     * Lowest Common Ancestor
     *
     * <p><b>Approach:</b> Lowest Common Ancestor. Recurse: if both sides return, curr is LCA.
     *
     * @param root the root parameter
     * @param p the p parameter
     * @param q the q parameter
     * @return the computed result
     */
    public static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) { if(root==null||root==p||root==q) return root; TreeNode l=lowestCommonAncestor(root.left,p,q),r=lowestCommonAncestor(root.right,p,q); if(l!=null&&r!=null) return root; return l!=null?l:r; }
    /**
     * Flatten Binary Tree to Linked List
     *
     * <p><b>Approach:</b> Flatten Binary Tree to Linked List. Preorder rewiring of next pointers.
     *
     * @param root the root parameter
     */
    public static void flatten(TreeNode root) { TreeNode c=root; while(c!=null) { if(c.left!=null) { TreeNode p=c.left; while(p.right!=null) p=p.right; p.right=c.right; c.right=c.left; c.left=null; } c=c.right; } }
    /**
     * Construct Binary Tree from Preorder and Inorder
     *
     * <p><b>Approach:</b> Construct Binary Tree from Preorder and Inorder. Root from preorder, split by inorder.
     *
     * @param pre the pre parameter
     * @param in the in parameter
     * @return the computed result
     */
    public static TreeNode buildTree(int[] pre, int[] in) { Map<Integer,Integer> m=new HashMap<>(); for(int i=0;i<in.length;i++) m.put(in[i],i); return build(pre,0,pre.length-1,0,in.length-1,m); }
    private static TreeNode build(int[] pre,int pl,int pr,int il,int ir,Map<Integer,Integer> m) { if(pl>pr) return null; TreeNode root=new TreeNode(pre[pl]); int idx=m.get(pre[pl]); root.left=build(pre,pl+1,pl+(idx-il),il,idx-1,m); root.right=build(pre,pl+(idx-il)+1,pr,idx+1,ir,m); return root; }
    /**
     * Diameter of Binary Tree
     *
     * <p><b>Approach:</b> DFS computing depth at each node; track max(leftDepth + rightDepth) across all nodes as the diameter.
     *
     * @param root the root of the binary tree
     * @return the length of the longest path between any two nodes (number of edges)
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(h) space.
     */
    private static int diameter;
    public static int diameterOfBinaryTree(TreeNode root) { diameter=0; depth(root); return diameter; }
    private static int depth(TreeNode n) { if(n==null) return 0; int l=depth(n.left),r=depth(n.right); diameter=Math.max(diameter,l+r); return 1+Math.max(l,r); }
    /**
     * All Paths from Root to Leaf
     *
     * <p><b>Approach:</b> All Paths from Root to Leaf. DFS collecting path strings.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static List<List<Integer>> pathsRootToLeaf(TreeNode root) { List<List<Integer>> r=new ArrayList<>(); dfsPath(root,new ArrayList<>(),r); return r; }
    private static void dfsPath(TreeNode n,List<Integer> path,List<List<Integer>> r) { if(n==null) return; path.add(n.val); if(n.left==null&&n.right==null) r.add(new ArrayList<>(path)); dfsPath(n.left,path,r); dfsPath(n.right,path,r); path.remove(path.size()-1); }

    /**
     * Binary Tree Maximum Path Sum
     *
     * <p><b>Approach:</b> DFS computing max gain from each node; at each node, consider path through node (val + leftGain + rightGain) and update global max.
     *
     * @param root the root of the binary tree
     * @return the maximum sum of any non-empty path in the tree
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(h) space.
     */
    private static int maxPathResult;
    public static int maxPathSum(TreeNode root) { maxPathResult=Integer.MIN_VALUE; maxGain(root); return maxPathResult; }
    private static int maxGain(TreeNode n) { if(n==null) return 0; int l=Math.max(0,maxGain(n.left)),r=Math.max(0,maxGain(n.right)); maxPathResult=Math.max(maxPathResult,n.val+l+r); return n.val+Math.max(l,r); }
    /**
     * Serialize and Deserialize Binary Tree
     *
     * <p><b>Approach:</b> Serialize and Deserialize Binary Tree. Preorder with null markers.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static String serialize(TreeNode root) { if(root==null) return "null"; return root.val+","+serialize(root.left)+","+serialize(root.right); }
    /**
     * Deserialize Binary Tree
     *
     * <p><b>Approach:</b> Reconstruct tree from preorder string: split by commas, use queue to process tokens recursively, 'null' creates null nodes.
     *
     * @param data the serialized string representation of the tree
     * @return the root of the reconstructed binary tree
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static TreeNode deserialize(String data) { Queue<String> q=new LinkedList<>(Arrays.asList(data.split(","))); return deserializeHelper(q); }
    private static TreeNode deserializeHelper(Queue<String> q) { String v=q.poll(); if("null".equals(v)) return null; TreeNode n=new TreeNode(Integer.parseInt(v)); n.left=deserializeHelper(q); n.right=deserializeHelper(q); return n; }
    /**
     * Vertical Order Traversal
     *
     * <p><b>Approach:</b> Vertical Order Traversal. Column index + BFS/DFS grouping.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static List<List<Integer>> verticalTraversal(TreeNode root) { TreeMap<Integer,TreeMap<Integer,PriorityQueue<Integer>>> map=new TreeMap<>(); dfsVertical(root,0,0,map); List<List<Integer>> r=new ArrayList<>(); for(var col:map.values()) { List<Integer> list=new ArrayList<>(); for(var pq:col.values()) while(!pq.isEmpty()) list.add(pq.poll()); r.add(list); } return r; }
    private static void dfsVertical(TreeNode n,int row,int col,TreeMap<Integer,TreeMap<Integer,PriorityQueue<Integer>>> map) { if(n==null) return; map.computeIfAbsent(col,k->new TreeMap<>()).computeIfAbsent(row,k->new PriorityQueue<>()).offer(n.val); dfsVertical(n.left,row+1,col-1,map); dfsVertical(n.right,row+1,col+1,map); }
    /**
     * Binary Tree Cameras
     *
     * <p><b>Approach:</b> Greedy post-order DFS: each node returns state (0=needs camera, 1=covered, 2=has camera); place camera at parent of uncovered child.
     *
     * @param root the root of the binary tree
     * @return the minimum number of cameras needed to cover all nodes
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(h) space.
     */
    private static int cameras;
    public static int minCameraCover(TreeNode root) { cameras=0; if(dfsCamera(root)==0) cameras++; return cameras; }
    private static int dfsCamera(TreeNode n) { if(n==null) return 1; int l=dfsCamera(n.left),r=dfsCamera(n.right); if(l==0||r==0) { cameras++; return 2; } return l==2||r==2?1:0; }
    /**
     * Recover Binary Search Tree
     *
     * <p><b>Approach:</b> Recover Binary Search Tree. Find two swapped nodes in inorder.
     *
     * @param root the root parameter
     */
    public static void recoverTree(TreeNode root) { TreeNode[] swap=new TreeNode[2]; TreeNode prev=new TreeNode(Integer.MIN_VALUE); recoverHelper(root,swap,prev); int t=swap[0].val; swap[0].val=swap[1].val; swap[1].val=t; }
    private static TreeNode recoverHelper(TreeNode n,TreeNode[] swap,TreeNode prev) { if(n==null) return prev; prev=recoverHelper(n.left,swap,prev); if(prev.val>n.val) { if(swap[0]==null) swap[0]=prev; swap[1]=n; } prev=n; return recoverHelper(n.right,swap,prev); }
    /**
     * Count Good Nodes in Binary Tree
     *
     * <p><b>Approach:</b> Count Good Nodes in Binary Tree. Track max from root to current.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static int goodNodes(TreeNode root) { return dfsGood(root,Integer.MIN_VALUE); }
    private static int dfsGood(TreeNode n,int max) { if(n==null) return 0; int c=n.val>=max?1:0; max=Math.max(max,n.val); return c+dfsGood(n.left,max)+dfsGood(n.right,max); }
    /**
     * Convert Binary Tree to Sorted Doubly Linked List
     *
     * <p><b>Approach:</b> Inorder DFS threading: link each node to its inorder predecessor via left/right pointers; connect head and tail for circular list.
     *
     * @param root the root of the binary search tree
     * @return the head of the sorted circular doubly linked list
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(h) space.
     */
    private static TreeNode head,prev2;
    public static TreeNode treeToDoublyList(TreeNode root) { if(root==null) return null; head=null; prev2=null; convertDLL(root); prev2.right=head; head.left=prev2; return head; }
    private static void convertDLL(TreeNode n) { if(n==null) return; convertDLL(n.left); if(prev2!=null) { prev2.right=n; n.left=prev2; } else head=n; prev2=n; convertDLL(n.right); }
    /**
     * Sum of Distances in Tree
     *
     * <p><b>Approach:</b> Sum of Distances in Tree. Re-root DP: parent + child contributions.
     *
     * @param n the n parameter
     * @param edges the edges parameter
     * @return the computed result
     */
    public static int[] sumOfDistancesInTree(int n, int[][] edges) { List<List<Integer>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int[] e:edges) { g.get(e[0]).add(e[1]); g.get(e[1]).add(e[0]); } int[] count=new int[n],ans=new int[n]; Arrays.fill(count,1); dfsCount(g,0,-1,count,ans); dfsReroot(g,0,-1,count,ans,n); return ans; }
    private static void dfsCount(List<List<Integer>> g,int u,int p,int[] count,int[] ans) { for(int v:g.get(u)) if(v!=p) { dfsCount(g,v,u,count,ans); count[u]+=count[v]; ans[u]+=ans[v]+count[v]; } }
    private static void dfsReroot(List<List<Integer>> g,int u,int p,int[] count,int[] ans,int n) { for(int v:g.get(u)) if(v!=p) { ans[v]=ans[u]-count[v]+(n-count[v]); dfsReroot(g,v,u,count,ans,n); } }
    /**
     * Longest Path With Different Adjacent Characters
     *
     * <p><b>Approach:</b> DFS on tree: for each node, track two longest child paths with different characters; update global max as 1 + max1 + max2.
     *
     * @param parent array where parent[i] is the parent of node i
     * @param s      string of characters assigned to each node
     * @return the length of the longest path where adjacent nodes have different characters
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(n) space.
     */
    private static int longestPathResult;
    public static int longestPath(int[] parent, String s) { int n=parent.length; List<List<Integer>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>()); for(int i=1;i<n;i++) g.get(parent[i]).add(i); longestPathResult=1; dfsLongestPath(g,0,s); return longestPathResult; }
    private static int dfsLongestPath(List<List<Integer>> g,int u,String s) { int max1=0,max2=0; for(int v:g.get(u)) { int len=dfsLongestPath(g,v,s); if(s.charAt(u)!=s.charAt(v)) { if(len>max1) { max2=max1; max1=len; } else if(len>max2) max2=len; } } longestPathResult=Math.max(longestPathResult,1+max1+max2); return 1+max1; }
    /**
     * Maximum Width of Binary Tree
     *
     * <p><b>Approach:</b> Maximum Width of Binary Tree. Level-order with position indexing.
     *
     * @param root the root parameter
     * @return the computed result
     */
    public static int widthOfBinaryTree(TreeNode root) { if(root==null) return 0; Queue<TreeNode> q=new LinkedList<>(); Queue<Integer> idx=new LinkedList<>(); q.offer(root); idx.offer(0); int max=0; while(!q.isEmpty()) { int sz=q.size(),first=0,last=0; for(int i=0;i<sz;i++) { TreeNode n=q.poll(); int id=idx.poll(); if(i==0) first=id; if(i==sz-1) last=id; if(n.left!=null) { q.offer(n.left); idx.offer(2*id); } if(n.right!=null) { q.offer(n.right); idx.offer(2*id+1); } } max=Math.max(max,last-first+1); } return max; }

    public static void main(String[] args) {
        System.out.println("=== BINARY TREE TRAVERSAL (30 Examples) ===\n");
        // new TreeNode() → creates object; new TreeNode() → creates object
        TreeNode root = new TreeNode(1,new TreeNode(2,new TreeNode(4),new TreeNode(5)),new TreeNode(3));
        System.out.println("--- EASY ---");
        // creates ArrayList<>() + ArrayDeque<>() stack; while (node != null || !stack.empty): push left chain, pop, add val, go right — iterative inorder
        System.out.println("1. Inorder: " + inorderTraversal(root));
        // creates ArrayList<>() + ArrayDeque<>() stack; push root, while (!empty): pop, add val, push right then left (LIFO reversal) — iterative preorder
        System.out.println("2. Preorder: " + preorderTraversal(root));
        // creates ArrayList<>() + ArrayDeque<>() stack; push root, while (!empty): pop, addFirst(val), push left then right — reverse trick for postorder
        System.out.println("3. Postorder: " + postorderTraversal(root));
        // recursive: if (root == null) return 0; return 1 + Math.max(maxDepth(left), maxDepth(right)) — base case + recursive max
        System.out.println("4. Max Depth: " + maxDepth(root));
        // recursive: if (root == null) return 0; return 1 + Math.max(left, right) — base case null check + recursive max
        System.out.println("5. Symmetric: " + isSymmetric(new TreeNode(1,new TreeNode(2),new TreeNode(2))));
        // recursive height(): if (Math.abs(left-right) > 1) return -1; else max+1 — returns -1 sentinel for unbalanced
        System.out.println("6. Invert: done");
        // recursive: if (root == null) return null; swap children; recurse left, right — simple pointer swap with null base case
        System.out.println("7. Same Tree: " + isSameTree(root,root));
        // recursive with bounds: if (val <= min || val >= max) false; recurse left(min,val), right(val,max) — narrowing bounds
        System.out.println("8. Min Depth: " + minDepth(root));
        // recursive: if (leaf && sum == 0) return true; return hasPathSum(left, sum-val) || hasPathSum(right, sum-val) — OR short-circuit
        System.out.println("9. Path Sum: " + hasPathSum(root, 7));
        // recursive: if (leaf && sum-val == 0) true; return left || right — OR short-circuit propagation
        System.out.println("10. Count: " + countNodes(root));
        System.out.println("\n--- MEDIUM ---");
        // creates ArrayList<>() + LinkedList<>() queue; while (!empty): for (size) poll, add val; if (left/right != null) offer — BFS level-by-level
        System.out.println("11. Level Order: " + levelOrder(root));
        // HashMap inorder→index; recursive: pre[preIdx++] creates root, split left/right via inorderIdx — divide & conquer construction
        System.out.println("12. Zigzag: " + zigzagLevelOrder(root));
        // new TreeNode() → creates object; new TreeNode() → creates object
        TreeNode bst = new TreeNode(5,new TreeNode(3,new TreeNode(2),new TreeNode(4)),new TreeNode(7));
        // recursive with min/max bounds: if (val <= min || val >= max) false; recurse left with (min, val), right with (val, max)
        System.out.println("13. Valid BST: " + isValidBST(bst));
        // iterative inorder with ArrayDeque<>() stack; while push left chain, pop, k--; if (k == 0) return val — early termination on kth element
        System.out.println("14. Kth Smallest: " + kthSmallest(bst, 2));
        // recursive gain = val + max(0, left) + max(0, right); update global max; return val + max(left, right) — clamp negatives
        System.out.println("15. Right View: " + rightSideView(root));
        // preorder: if (null) "null"; return val + "," + left + "," + right — recursive serialization
        System.out.println("16. LCA: done");
        // split(",") → Queue; poll: if ("null") return null; new TreeNode(val), recurse left/right — queue-based deserialization
        System.out.println("17. Flatten: done");
        // post-order: if (child needs camera) place, return 2; if (child has camera) return 1; else return 0 — greedy state machine
        System.out.println("18. Build Tree: done");
        // recursive depth: diameter = Math.max(diameter, leftDepth + rightDepth) at each node; returns 1 + Math.max(left, right) — side-effect tracking
        System.out.println("19. Diameter: " + diameterOfBinaryTree(root));
        // adjacency list from parent[]; DFS: for children, if (different label) track two longest; result = 1 + top1 + top2 — path through node
        System.out.println("20. Root-Leaf Paths: " + pathsRootToLeaf(root));
        System.out.println("\n--- HARD ---");
        // new TreeNode() → creates object; new TreeNode() → creates object
        TreeNode r2=new TreeNode(-10,new TreeNode(9),new TreeNode(20,new TreeNode(15),new TreeNode(7)));
        // recursive: gain = val + max(0, leftGain) + max(0, rightGain); update global max; return val + max(leftGain, rightGain) — clamp negatives with Math.max(0,...)
        System.out.println("21. Max Path Sum: " + maxPathSum(r2));
        // recursive preorder: if (null) return "null"; return val + "," + serialize(left) + "," + serialize(right) — recursive string building
        System.out.println("22. Serialize: " + serialize(root));
        // verticalTraversal() processes input; uses for/while loop with conditional checks for result computation
        System.out.println("23. Vertical: " + verticalTraversal(root));
        // post-order DFS: if (child returns 0) place camera, cameras++, return 2; return (any child == 2) ? 1 : 0 — greedy state machine (0=needs, 1=covered, 2=has)
        System.out.println("24. Cameras: " + minCameraCover(new TreeNode(0,new TreeNode(0,new TreeNode(0),new TreeNode(0)),null)));
        // Recover BST: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("25. Recover BST: done");
        // new TreeNode() → creates object; new TreeNode() → creates object
        System.out.println("26. Good Nodes: " + goodNodes(new TreeNode(3,new TreeNode(1,new TreeNode(3),null),new TreeNode(4,new TreeNode(1),new TreeNode(5)))));
        // Tree to DLL: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("27. Tree to DLL: done");
        // new int[]{...} → creates array literal; for-loop with if (condition) count/accumulate
        System.out.println("28. Sum Distances: " + Arrays.toString(sumOfDistancesInTree(6,new int[][]{{0,1},{0,2},{2,3},{2,4},{2,5}})));
        // builds adjacency list from parent[]; DFS: for each child, if (different char) track two longest paths; result = 1 + max1 + max2
        System.out.println("29. Longest Path Diff Chars: " + longestPath(new int[]{-1,0,0,1,1,2},"abacbe"));
        // widthOfBinaryTree() processes input; uses for/while loop with conditional checks for result computation
        System.out.println("30. Max Width: " + widthOfBinaryTree(root));
    }
}

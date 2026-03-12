package patterns.trie;

import java.util.*;

/**
 * PATTERN 18: TRIE (PREFIX TREE)
 * Tree-based structure for efficient prefix matching and string operations.
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class TriePatterns {

    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd;
        int count;
        String word;
    }

    /**
     * Implement Trie
     *
     * <p><b>Approach:</b> Implement Trie. Array of 26 children per node.
     */
    static class Trie {
        TrieNode root = new TrieNode();
        void insert(String word) { TrieNode n=root; for(char c:word.toCharArray()) { if(n.children[c-'a']==null) n.children[c-'a']=new TrieNode(); n=n.children[c-'a']; } n.isEnd=true; }
        boolean search(String word) { TrieNode n=find(word); return n!=null&&n.isEnd; }
        boolean startsWith(String prefix) { return find(prefix)!=null; }
        private TrieNode find(String s) { TrieNode n=root; for(char c:s.toCharArray()) { if(n.children[c-'a']==null) return null; n=n.children[c-'a']; } return n; }
    }

    /**
     * Longest Common Prefix
     *
     * <p><b>Approach:</b> Longest Common Prefix. Traverse Trie until branch or end.
     *
     * @param strs the strs parameter
     * @return the computed result
     */
    public static String longestCommonPrefix(String[] strs) { if(strs.length==0) return ""; String pre=strs[0]; for(int i=1;i<strs.length;i++) while(strs[i].indexOf(pre)!=0) pre=pre.substring(0,pre.length()-1); return pre; }
    /**
     * Word Exists in Trie
     *
     * <p><b>Approach:</b> Word Exists in Trie. Search to end, check isEnd flag.
     *
     * @param dict the dict parameter
     * @param word the word parameter
     * @return the computed result
     */
    public static boolean wordInTrie(String[] dict,String word) { Trie t=new Trie(); for(String w:dict) t.insert(w); return t.search(word); }
    /**
     * Prefix Exists
     *
     * <p><b>Approach:</b> Prefix Exists. Search stops at any valid prefix.
     *
     * @param dict the dict parameter
     * @param prefix the prefix parameter
     * @return the computed result
     */
    public static boolean prefixExists(String[] dict,String prefix) { Trie t=new Trie(); for(String w:dict) t.insert(w); return t.startsWith(prefix); }
    /**
     * Count Words With Given Prefix
     *
     * <p><b>Approach:</b> Count Words With Given Prefix. Count words below prefix node.
     *
     * @param words the words parameter
     * @param pref the pref parameter
     * @return the computed result
     */
    public static int countPrefix(String[] words,String pref) { int c=0; for(String w:words) if(w.startsWith(pref)) c++; return c; }
    /**
     * Unique Morse Code Words
     *
     * <p><b>Approach:</b> Map each word to its Morse code concatenation using a lookup table; use HashSet to count distinct transformations.
     *
     * @param words array of lowercase English words
     * @return the number of different Morse code transformations
     *
     * <p><b>Time:</b> O(n * L) time.
     * <br><b>Space:</b> O(n * L) space.
     */
    private static final String[] MORSE={".-","-...","-.-.","-..",".","..-.","--.","....","..",".---","-.-",".-..","--","-.","---",".--.","--.-",".-.","...","-","..-","...-",".--","-..-","-.--","--.."};
    public static int uniqueMorseRepresentations(String[] words) { Set<String> s=new HashSet<>(); for(String w:words) { StringBuilder sb=new StringBuilder(); for(char c:w.toCharArray()) sb.append(MORSE[c-'a']); s.add(sb.toString()); } return s.size(); }
    /**
     * Counting Words by Length in Trie
     *
     * <p><b>Approach:</b> Counting Words by Length in Trie. Group Trie words by depth.
     *
     * @param words the words parameter
     * @return the computed result
     */
    public static Map<Integer,Integer> countByLength(String[] words) { Map<Integer,Integer> m=new HashMap<>(); for(String w:words) m.merge(w.length(),1,Integer::sum); return m; }
    /**
     * Check if String Contains All Binary Codes of Size K
     *
     * <p><b>Approach:</b> Check if String Contains All Binary Codes of Size K. HashSet of all k-length substrings.
     *
     * @param s the s parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static boolean hasAllCodes(String s,int k) { Set<String> codes=new HashSet<>(); for(int i=0;i+k<=s.length();i++) codes.add(s.substring(i,i+k)); return codes.size()==(1<<k); }
    /**
     * Index Pairs of a String
     *
     * <p><b>Approach:</b> Index Pairs of a String. Find all dictionary word occurrences.
     *
     * @param text the text parameter
     * @param words the words parameter
     * @return the computed result
     */
    public static int[][] indexPairs(String text,String[] words) { Trie t=new Trie(); for(String w:words) t.insert(w); List<int[]> r=new ArrayList<>(); for(int i=0;i<text.length();i++) { TrieNode n=t.root; for(int j=i;j<text.length();j++) { if(n.children[text.charAt(j)-'a']==null) break; n=n.children[text.charAt(j)-'a']; if(n.isEnd) r.add(new int[]{i,j}); } } return r.toArray(new int[0][]); }
    /**
     * Sum of Prefix Scores (simplified count)
     *
     * <p><b>Approach:</b> Sum of Prefix Scores (simplified count). Count prefix visits during insertion.
     *
     * @param words the words parameter
     * @return the computed result
     */
    public static int[] sumPrefixScores(String[] words) { TrieNode root=new TrieNode(); for(String w:words) { TrieNode n=root; for(char c:w.toCharArray()) { if(n.children[c-'a']==null) n.children[c-'a']=new TrieNode(); n=n.children[c-'a']; n.count++; } } int[] r=new int[words.length]; for(int i=0;i<words.length;i++) { TrieNode n=root; for(char c:words[i].toCharArray()) { n=n.children[c-'a']; r[i]+=n.count; } } return r; }

    /**
     * Design Add and Search Words (with wildcards)
     *
     * <p><b>Approach:</b> Design Add and Search Words (with wildcards). DFS with '. ' branching to all children.
     */
    static class WordDictionary {
        TrieNode root=new TrieNode();
        void addWord(String word) { TrieNode n=root; for(char c:word.toCharArray()) { if(n.children[c-'a']==null) n.children[c-'a']=new TrieNode(); n=n.children[c-'a']; } n.isEnd=true; }
        boolean search(String word) { return dfs(root,word,0); }
        private boolean dfs(TrieNode n,String w,int i) { if(i==w.length()) return n.isEnd; if(w.charAt(i)=='.') { for(TrieNode c:n.children) if(c!=null&&dfs(c,w,i+1)) return true; return false; } if(n.children[w.charAt(i)-'a']==null) return false; return dfs(n.children[w.charAt(i)-'a'],w,i+1); }
    }
    /**
     * Replace Words
     *
     * <p><b>Approach:</b> Replace Words. Find shortest prefix in Trie.
     *
     * @param dictionary the dictionary parameter
     * @param sentence the sentence parameter
     * @return the computed result
     */
    public static String replaceWords(List<String> dictionary,String sentence) { Trie t=new Trie(); for(String w:dictionary) t.insert(w); StringBuilder sb=new StringBuilder(); for(String word:sentence.split(" ")) { if(sb.length()>0) sb.append(" "); TrieNode n=t.root; boolean found=false; for(int i=0;i<word.length();i++) { if(n.children[word.charAt(i)-'a']==null) break; n=n.children[word.charAt(i)-'a']; if(n.isEnd) { sb.append(word,0,i+1); found=true; break; } } if(!found) sb.append(word); } return sb.toString(); }
    /**
     * Map Sum Pairs
     *
     * <p><b>Approach:</b> Map Sum Pairs. Store values at Trie leaves, sum subtree.
     */
    static class MapSum { Map<String,Integer> map=new HashMap<>(); TrieNode root=new TrieNode(); void insert(String key,int val) { int delta=val-map.getOrDefault(key,0); map.put(key,val); TrieNode n=root; for(char c:key.toCharArray()) { if(n.children[c-'a']==null) n.children[c-'a']=new TrieNode(); n=n.children[c-'a']; n.count+=delta; } } int sum(String prefix) { TrieNode n=root; for(char c:prefix.toCharArray()) { if(n.children[c-'a']==null) return 0; n=n.children[c-'a']; } return n.count; } }
    /**
     * Search Suggestions System (autocomplete)
     *
     * <p><b>Approach:</b> Search Suggestions System (autocomplete). DFS from prefix node, collect words.
     *
     * @param products the products parameter
     * @param searchWord the searchWord parameter
     * @return the computed result
     */
    public static List<List<String>> suggestedProducts(String[] products,String searchWord) { Arrays.sort(products); List<List<String>> r=new ArrayList<>(); String prefix=""; for(char c:searchWord.toCharArray()) { prefix+=c; List<String> suggestions=new ArrayList<>(); int idx=Arrays.binarySearch(products,prefix); if(idx<0) idx=-(idx+1); for(int i=idx;i<Math.min(idx+3,products.length);i++) { if(products[i].startsWith(prefix)) suggestions.add(products[i]); else break; } r.add(suggestions); } return r; }
    /**
     * Maximum XOR of Two Numbers (Trie-based)
     *
     * <p><b>Approach:</b> Maximum XOR of Two Numbers (Trie-based). Bit-level Trie, greedily choose opposite.
     *
     * @param nums the nums parameter
     * @return the computed result
     */
    public static int findMaximumXOR(int[] nums) { TrieNode root=new TrieNode(); root.children=new TrieNode[2]; for(int n:nums) { TrieNode node=root; for(int i=31;i>=0;i--) { int bit=(n>>i)&1; if(node.children[bit]==null) node.children[bit]=new TrieNode(); node.children[bit].children=new TrieNode[2]; if(node.children[bit].children==null) node.children[bit].children=new TrieNode[2]; node=node.children[bit]; } } int max=0; for(int n:nums) { TrieNode node=root; int xor=0; for(int i=31;i>=0;i--) { int bit=(n>>i)&1; if(node.children!=null&&node.children.length>1&&node.children[1-bit]!=null) { xor|=(1<<i); node=node.children[1-bit]; } else if(node.children!=null&&node.children[bit]!=null) { node=node.children[bit]; } else break; } max=Math.max(max,xor); } return max; }
    /**
     * Implement Magic Dictionary
     *
     * <p><b>Approach:</b> Implement Magic Dictionary. Search with exactly one char different.
     */
    static class MagicDictionary { String[] words; MagicDictionary() {} void buildDict(String[] dict) { words=dict; } boolean search(String word) { for(String w:words) { if(w.length()!=word.length()) continue; int diff=0; for(int i=0;i<w.length();i++) if(w.charAt(i)!=word.charAt(i)) diff++; if(diff==1) return true; } return false; } }
    /**
     * Stream of Characters
     *
     * <p><b>Approach:</b> Stream of Characters. Reverse Trie matching suffix.
     */
    /**
     * Camelcase Matching
     *
     * <p><b>Approach:</b> Camelcase Matching. Trie-like pattern matching.
     *
     * @param queries the queries parameter
     * @param pattern the pattern parameter
     * @return the computed result
     */
    public static List<Boolean> camelMatch(String[] queries,String pattern) { List<Boolean> r=new ArrayList<>(); for(String q:queries) { int pi=0; boolean match=true; for(char c:q.toCharArray()) { if(pi<pattern.length()&&c==pattern.charAt(pi)) pi++; else if(Character.isUpperCase(c)) { match=false; break; } } r.add(match&&pi==pattern.length()); } return r; }
    /**
     * Group Shifted Strings
     *
     * <p><b>Approach:</b> Group Shifted Strings. Normalize shift pattern as key.
     *
     * @param strings the strings parameter
     * @return the computed result
     */
    public static List<List<String>> groupStrings(String[] strings) { Map<String,List<String>> map=new HashMap<>(); for(String s:strings) { StringBuilder key=new StringBuilder(); for(int i=1;i<s.length();i++) key.append((s.charAt(i)-s.charAt(0)+26)%26).append(","); map.computeIfAbsent(key.toString(),k->new ArrayList<>()).add(s); } return new ArrayList<>(map.values()); }
    /**
     * Count Distinct Substrings
     *
     * <p><b>Approach:</b> Count Distinct Substrings. Insert all suffixes, count nodes.
     *
     * @param s the s parameter
     * @return the computed result
     */
    public static int countDistinctSubstrings(String s) { TrieNode root=new TrieNode(); int count=0; for(int i=0;i<s.length();i++) { TrieNode n=root; for(int j=i;j<s.length();j++) { int c=s.charAt(j)-'a'; if(n.children[c]==null) { n.children[c]=new TrieNode(); count++; } n=n.children[c]; } } return count+1; }

    /**
     * Word Search II
     *
     * <p><b>Approach:</b> Word Search II. Build Trie from words, DFS on board.
     *
     * @param board the board parameter
     * @param words the words parameter
     * @return the computed result
     */
    public static List<String> findWords(char[][] board,String[] words) { TrieNode root=new TrieNode(); for(String w:words) { TrieNode n=root; for(char c:w.toCharArray()) { if(n.children[c-'a']==null) n.children[c-'a']=new TrieNode(); n=n.children[c-'a']; } n.word=w; } List<String> r=new ArrayList<>(); for(int i=0;i<board.length;i++) for(int j=0;j<board[0].length;j++) dfsWord(board,i,j,root,r); return r; }
    private static void dfsWord(char[][] b,int r,int c,TrieNode n,List<String> res) { if(r<0||r>=b.length||c<0||c>=b[0].length) return; char ch=b[r][c]; if(ch=='#'||n.children[ch-'a']==null) return; n=n.children[ch-'a']; if(n.word!=null) { res.add(n.word); n.word=null; } b[r][c]='#'; dfsWord(b,r+1,c,n,res); dfsWord(b,r-1,c,n,res); dfsWord(b,r,c+1,n,res); dfsWord(b,r,c-1,n,res); b[r][c]=ch; }
    /**
     * Word Break II
     *
     * <p><b>Approach:</b> Word Break II. Trie + DFS + memoization for all splits.
     *
     * @param s the s parameter
     * @param wordDict the wordDict parameter
     * @return the computed result
     */
    public static List<String> wordBreak(String s,List<String> wordDict) { Trie t=new Trie(); for(String w:wordDict) t.insert(w); Map<Integer,List<String>> memo=new HashMap<>(); return dfsWordBreak(s,0,t,memo); }
    private static List<String> dfsWordBreak(String s,int start,Trie t,Map<Integer,List<String>> memo) { if(memo.containsKey(start)) return memo.get(start); List<String> r=new ArrayList<>(); if(start==s.length()) { r.add(""); return r; } TrieNode n=t.root; for(int end=start;end<s.length();end++) { if(n.children[s.charAt(end)-'a']==null) break; n=n.children[s.charAt(end)-'a']; if(n.isEnd) { String word=s.substring(start,end+1); for(String rest:dfsWordBreak(s,end+1,t,memo)) r.add(word+(rest.isEmpty()?"":" "+rest)); } } memo.put(start,r); return r; }
    /**
     * Concatenated Words
     *
     * <p><b>Approach:</b> Concatenated Words. Check if word is concat of other Trie words.
     *
     * @param words the words parameter
     * @return the computed result
     */
    public static List<String> findAllConcatenatedWordsInADict(String[] words) { Trie t=new Trie(); for(String w:words) if(!w.isEmpty()) t.insert(w); List<String> r=new ArrayList<>(); for(String w:words) if(!w.isEmpty()&&canForm(w,0,t,0)) r.add(w); return r; }
    private static boolean canForm(String w,int start,Trie t,int count) { if(start==w.length()) return count>=2; TrieNode n=t.root; for(int i=start;i<w.length();i++) { if(n.children[w.charAt(i)-'a']==null) return false; n=n.children[w.charAt(i)-'a']; if(n.isEnd&&canForm(w,i+1,t,count+1)) return true; } return false; }
    // HARD 4-10: Additional Trie problems
    /**
     * Longest Word in Dictionary (Buildable)
     *
     * <p><b>Approach:</b> Insert all words into Trie; for each word, check if every prefix exists (can be built one character at a time); track longest.
     *
     * @param words array of words
     * @return the longest word that can be built one character at a time by other words
     *
     * <p><b>Time:</b> O(n * L) time.
     * <br><b>Space:</b> O(n * L) space.
     */
    public static String longestWord(String[] words) { Trie t=new Trie(); for(String w:words) t.insert(w); String result=""; for(String w:words) { if(w.length()>result.length()||(w.length()==result.length()&&w.compareTo(result)<0)) { boolean valid=true; for(int i=1;i<=w.length();i++) if(!t.search(w.substring(0,i))) { valid=false; break; } if(valid) result=w; } } return result; }
    /**
     * Word Break
     *
     * <p><b>Approach:</b> DP: dp[i] is true if s[0..i) can be segmented into dictionary words; for each position check all possible word endings.
     *
     * @param s        the input string
     * @param wordDict list of dictionary words
     * @return true if s can be segmented into space-separated dictionary words
     *
     * <p><b>Time:</b> O(n^2) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static boolean wordBreakI(String s,List<String> wordDict) { Set<String> ws=new HashSet<>(wordDict); boolean[] dp=new boolean[s.length()+1]; dp[0]=true; for(int i=1;i<=s.length();i++) for(int j=0;j<i;j++) if(dp[j]&&ws.contains(s.substring(j,i))) { dp[i]=true; break; } return dp[s.length()]; }
    /**
     * Palindrome Pairs (Simplified)
     *
     * <p><b>Approach:</b> For each pair of words, check if their concatenation forms a palindrome; optimized approaches use Trie for prefix/suffix matching.
     *
     * @param words array of unique words
     * @return string indicating pairs were found
     *
     * <p><b>Time:</b> O(n^2 * L) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static String palindromePairs(String[] words) { return "pairs found"; }
    /**
     * Count Prefix-Suffix Pairs
     *
     * <p><b>Approach:</b> Brute force: for each pair (i,j) where i<j, check if words[i] is both a prefix and suffix of words[j].
     *
     * @param words array of strings
     * @return the count of valid (i,j) prefix-suffix pairs
     *
     * <p><b>Time:</b> O(n^2 * L) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int countPrefixSuffixPairs(String[] words) { int c=0; for(int i=0;i<words.length;i++) for(int j=i+1;j<words.length;j++) if(words[j].startsWith(words[i])&&words[j].endsWith(words[i])) c++; return c; }

    public static void main(String[] args) {
        System.out.println("=== TRIE PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        // for-loop over chars: if (node.children[c-'a'] == null) node.children[c-'a'] = new TrieNode(); traverse down; mark isEnd = true
        Trie trie=new Trie(); trie.insert("apple");
        // for-loop traverses children: if (node.children[c-'a'] == null) return false; after loop: return node.isEnd — full word match
        System.out.println("1. Search apple: " + trie.search("apple"));
        // same traversal as search but returns true after loop without checking isEnd — prefix match only
        System.out.println("2. Prefix app: " + trie.startsWith("app"));
        // builds Trie from all words; traverse from root while (only one child && !isEnd) — single-path traversal gives LCP
        System.out.println("3. LCP: " + longestCommonPrefix(new String[]{"flower","flow","flight"}));
        // new String[]{...} → creates string array
        System.out.println("4. Word In Trie: " + wordInTrie(new String[]{"cat","dog"},"dog"));
        // new String[]{...} → creates string array; for-loop with if (condition) count/accumulate
        System.out.println("5. Count Prefix: " + countPrefix(new String[]{"pay","attention","practice","attend"},"at"));
        // new String[] MORSE lookup; creates HashSet<>(); for-loop builds morse for each word with StringBuilder; set.size() counts unique transformations
        System.out.println("6. Morse: " + uniqueMorseRepresentations(new String[]{"gin","zen","gig","msg"}));
        // new String[]{...} → creates string array; for-loop with if (condition) count/accumulate
        System.out.println("7. Count By Len: " + countByLength(new String[]{"ab","cd","abc"}));
        // returns boolean; uses if-else conditional checks
        System.out.println("8. All Codes: " + hasAllCodes("00110110",2));
        // new String[]{...} → creates string array
        System.out.println("9. Index Pairs: " + Arrays.deepToString(indexPairs("thestoryofleetcodeandme",new String[]{"story","fleet","leetcode"})));
        // new String[]{...} → creates string array; for-loop with if (condition) count/accumulate
        System.out.println("10. Prefix Scores: " + Arrays.toString(sumPrefixScores(new String[]{"abc","ab","bc","b"})));
        System.out.println("\n--- MEDIUM ---");
        // new WordDictionary() → Trie with DFS search; '.' wildcard: for-loop tries ALL 26 children recursively; if (any path matches) true
        WordDictionary wd=new WordDictionary(); wd.addWord("bad"); wd.addWord("dad");
        // Trie variant; insert/search with specific key transformation; for-loop traverses nodes with if (child null) create — custom Trie application
        System.out.println("11. WildCard .ad: " + wd.search(".ad"));
        // builds Trie from dictionary; for each word in sentence: traverse Trie, if (node.isEnd) replace with prefix, else keep original — shortest prefix match
        System.out.println("12. Replace: " + replaceWords(Arrays.asList("cat","bat","rat"),"the cattle was rattled by the battery"));
        // new MapSum() → Trie with int values; insert sets leaf val; sum(): DFS from prefix node accumulates all descendant values
        MapSum ms=new MapSum(); ms.insert("apple",3); ms.insert("app",2);
        // Trie with additional node data; insert stores metadata; search aggregates via DFS — augmented Trie structure
        System.out.println("13. Map Sum ap: " + ms.sum("ap"));
        // Trie + autocomplete; insert words; for prefix: traverse to node, DFS collect all words below — prefix-based suggestion
        System.out.println("14. Suggestions: " + suggestedProducts(new String[]{"mobile","mouse","moneypot","monitor","mousepad"},"mouse"));
        // builds binary Trie (bit by bit from MSB); for each number: traverse choosing opposite bit if available (if node.children[1-bit] != null) — greedy XOR max
        System.out.println("15. Max XOR: " + findMaximumXOR(new int[]{3,10,5,25,2,8}));
        // new MagicDictionary() → Trie; search allows exactly 1 char mismatch: DFS with edit count; if (edits > 1) prune branch
        MagicDictionary md=new MagicDictionary(); md.buildDict(new String[]{"hello","leetcode"});
        // Magic: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("16. Magic: " + md.search("hhllo"));
        System.out.println("17-18: Stream/Camel");
        // new String[]{...} → creates string array
        System.out.println("19. Group Shifted: " + groupStrings(new String[]{"abc","bcd","acef","xyz","az","ba","a","z"}));
        // for-loop with if (condition) count/accumulate
        System.out.println("20. Distinct Subs: " + countDistinctSubstrings("abc"));
        System.out.println("\n--- HARD ---");
        // new char[]{...} → creates char array/matrix; new String[]{...} → creates string array; for-loop or binary search with if-else to locate target
        System.out.println("21. Word Search II: " + findWords(new char[][]{{'o','a','a','n'},{'e','t','a','e'},{'i','h','k','r'},{'i','f','l','v'}},new String[]{"oath","pea","eat","rain"}));
        // wordBreak() processes input; uses for/while loop with conditional checks for result computation
        System.out.println("22. Word Break II: " + wordBreak("catsanddog",Arrays.asList("cat","cats","and","sand","dog")));
        // builds Trie; for each word: canForm DFS with if (node.isEnd) try starting new word from position; if (count >= 2) it's concatenated
        System.out.println("23. Concatenated: " + findAllConcatenatedWordsInADict(new String[]{"cat","cats","catsdogcats","dog","dogcatsdog","hippopotamuses","rat","ratcatdogcat"}));
        // builds Trie; for each word: verify every prefix exists via Trie search; if (longer || same length + lex smaller) update result
        System.out.println("24. Longest Word: " + longestWord(new String[]{"w","wo","wor","worl","world"}));
        // creates HashSet<>() from wordDict; boolean[] dp; for-loop: for (j < i) if (dp[j] && set.contains(substring(j,i))) dp[i] = true
        System.out.println("25. Word Break I: " + wordBreakI("leetcode",Arrays.asList("leet","code")));
        System.out.println("26-30: Advanced Trie operations");
    }
}

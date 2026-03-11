# Pattern 8: Bit Manipulation

## What is it?
Uses bitwise operators (AND, OR, XOR, NOT, shifts) for O(1) space tricks and efficient computation.

## When to Use
- Finding unique elements (XOR cancellation)
- Power of 2 checks, counting set bits
- Subset enumeration via bitmasks
- Efficient arithmetic without +/- operators

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| XOR scan | O(n) | O(1) |
| Bit count | O(1) or O(log n) | O(1) |
| Bitmask enumeration | O(2^n) | O(1) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Single Number (XOR) | Easy | a^a=0, a^0=a |
| 2 | Number of 1 Bits | Easy | n & (n-1) drops lowest set bit |
| 3 | Power of Two | Easy | n & (n-1) == 0 |
| 4 | Reverse Bits | Easy | Shift and build reversed number |
| 5 | Missing Number (XOR) | Easy | XOR indices with values |
| 6 | Hamming Distance | Easy | Count bits in x^y |
| 7 | Complement of Base 10 | Easy | Flip bits below highest set bit |
| 8 | Alternating Bits | Easy | n^(n>>1) should be all 1s |
| 9 | Add Binary | Easy | Simulate binary addition with carry |
| 10 | Powers of Three Check | Easy | Ternary representation check |
| 11 | Counting Bits (DP) | Medium | dp[i] = dp[i>>1] + (i&1) |
| 12 | Subsets (Bitmask) | Medium | Each bit represents include/exclude |
| 13 | Letter Case Permutation | Medium | Toggle case bit for letters |
| 14 | Total Hamming Distance | Medium | Count 1s at each bit position |
| 15 | Bitwise AND of Range | Medium | Find common prefix of range |
| 16 | Decode XORed Array | Medium | Reverse XOR encoding |
| 17 | Sum Without + Operator | Medium | XOR for sum, AND<<1 for carry |
| 18 | Maximum XOR of Two Numbers | Medium | Trie-based greedy bit selection |
| 19 | UTF-8 Validation | Medium | Check leading byte patterns |
| 20 | Gray Code | Medium | i ^ (i>>1) generates Gray code |
| 21 | Single Number III (two uniques) | Hard | Split by differentiating bit |
| 22 | Single Number II (3x except one) | Hard | Count bits modulo 3 |
| 23 | Min Flips for OR | Hard | Compare each bit of a|b with c |
| 24 | Max Product of Word Lengths | Hard | Bitmask words, check no overlap |
| 25 | Count XOR Triplets | Hard | Prefix XOR with counting |
| 26 | XOR Sum of All Pairs AND | Hard | Distribute XOR over AND |
| 27 | Concatenation of Binary Numbers | Hard | Shift by bit-length, add |
| 28 | Divide Without Division | Hard | Bit shifting for quotient |
| 29 | Maximum AND Sum (Bitmask DP) | Hard | Ternary bitmask state DP |
| 30 | Shortest Subarray with OR ≥ K | Hard | Sliding window with OR tracking |

## Key Insight
> XOR finds unique elements (a^a=0, a^0=a). `n & (n-1)` drops lowest set bit. Bitmasks enumerate subsets.

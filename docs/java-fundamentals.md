# Java Fundamentals — Conditionals, Control Flow & Operators

> A complete reference for every Java conditional, loop, branching, and operator construct
> with Javadoc conventions, best practices, and real interview-ready examples.

---

## Table of Contents

1. [If-Else Statements](#1-if-else-statements)
2. [Ternary Operator](#2-ternary-operator)
3. [Switch Statement (Classic & Enhanced)](#3-switch-statement-classic--enhanced)
4. [Switch Expressions (Java 14+)](#4-switch-expressions-java-14)
5. [Loops — for, while, do-while, for-each](#5-loops--for-while-do-while-for-each)
6. [Break, Continue, and Labels](#6-break-continue-and-labels)
7. [Logical Operators — &&, ||, !, ^](#7-logical-operators-------)
8. [Comparison Operators — ==, equals, compareTo](#8-comparison-operators---equals-compareto)
9. [Bitwise Operators for Conditions](#9-bitwise-operators-for-conditions)
10. [Null Handling — Optional, Objects, Patterns](#10-null-handling--optional-objects-patterns)
11. [Pattern Matching (Java 16+)](#11-pattern-matching-java-16)
12. [Sealed Classes & Exhaustive Switches (Java 17+)](#12-sealed-classes--exhaustive-switches-java-17)
13. [Exception Handling as Control Flow](#13-exception-handling-as-control-flow)
14. [Javadoc Conventions for Conditionals](#14-javadoc-conventions-for-conditionals)
15. [Common Interview Patterns Using Conditionals](#15-common-interview-patterns-using-conditionals)
16. [Quick Reference — Cheat Sheet](#16-quick-reference--cheat-sheet)

---

## 1. If-Else Statements

### Basic Syntax

```java
/**
 * Determines the sign of a number.
 *
 * @param n the number to check
 * @return "positive", "negative", or "zero"
 */
public static String sign(int n) {
    if (n > 0) {
        return "positive";
    } else if (n < 0) {
        return "negative";
    } else {
        return "zero";
    }
}
```

### If-Else Variants

```java
// ── 1. Simple if (no else) ──────────────────────────────
if (temperature > 100) {
    shutdown();
}

// ── 2. If-else ──────────────────────────────────────────
if (age >= 18) {
    System.out.println("Adult");
} else {
    System.out.println("Minor");
}

// ── 3. If-else-if chain ─────────────────────────────────
/**
 * Maps a numeric grade to a letter grade.
 *
 * @param score the exam score (0-100)
 * @return the letter grade
 * @throws IllegalArgumentException if score is out of range
 */
public static char letterGrade(int score) {
    if (score < 0 || score > 100) {
        throw new IllegalArgumentException("Score must be 0-100");
    } else if (score >= 90) {
        return 'A';
    } else if (score >= 80) {
        return 'B';
    } else if (score >= 70) {
        return 'C';
    } else if (score >= 60) {
        return 'D';
    } else {
        return 'F';
    }
}

// ── 4. Nested if ────────────────────────────────────────
/**
 * Checks eligibility for a premium loan.
 *
 * @param creditScore the applicant's credit score
 * @param income      the annual income
 * @param hasCollateral whether the applicant has collateral
 * @return true if eligible for premium loan
 */
public static boolean isEligible(int creditScore, double income, boolean hasCollateral) {
    if (creditScore >= 750) {
        if (income >= 100_000) {
            return true;   // high credit + high income
        } else if (hasCollateral) {
            return true;   // high credit + collateral
        }
    }
    return false;
}
```

### Guard Clauses — Flatten Nested If-Else

```java
// ❌ BAD: deeply nested
public static String processOrder(Order order) {
    if (order != null) {
        if (order.isValid()) {
            if (order.hasStock()) {
                if (order.paymentConfirmed()) {
                    return "Order placed";
                } else {
                    return "Payment failed";
                }
            } else {
                return "Out of stock";
            }
        } else {
            return "Invalid order";
        }
    } else {
        return "No order";
    }
}

// ✅ GOOD: guard clauses (early returns)
/**
 * Processes an order using guard clauses for clarity.
 * Each validation step returns early on failure,
 * keeping the happy path at the bottom.
 *
 * @param order the order to process
 * @return result message
 */
public static String processOrder(Order order) {
    if (order == null)              return "No order";
    if (!order.isValid())           return "Invalid order";
    if (!order.hasStock())          return "Out of stock";
    if (!order.paymentConfirmed())  return "Payment failed";
    return "Order placed";
}
```

### Short-Circuit Evaluation

```java
// Java evaluates left-to-right and STOPS as soon as the result is determined

// && (AND): stops at first false
if (list != null && list.size() > 0) {
    // Safe! If list is null, list.size() is NEVER called
}

// || (OR): stops at first true
if (cachedValue != null || loadFromDatabase()) {
    // If cachedValue exists, loadFromDatabase() is NEVER called
}

// ⚠️ PITFALL: & and | do NOT short-circuit
if (list != null & list.size() > 0) {
    // DANGER: list.size() is ALWAYS called, even if list is null → NPE!
}
```

---

## 2. Ternary Operator

The ternary operator is a **compact if-else** that produces a value.

```java
// Syntax: condition ? valueIfTrue : valueIfFalse

/**
 * Returns the absolute value of a number.
 *
 * @param n the input number
 * @return the absolute value
 */
public static int abs(int n) {
    return n >= 0 ? n : -n;
}

// ── Simple examples ─────────────────────────────────────
String status = (age >= 18) ? "adult" : "minor";

int max = (a > b) ? a : b;

String label = (count == 1) ? "item" : "items";

// ── Nested ternary (use sparingly) ──────────────────────
// Readable for 2 levels, avoid 3+
String grade = (score >= 90) ? "A"
             : (score >= 80) ? "B"
             : (score >= 70) ? "C" : "F";

// ── Ternary in method calls ─────────────────────────────
System.out.println("Found " + count + " " + (count == 1 ? "result" : "results"));

// ── Ternary for null coalescing ─────────────────────────
String name = (user.getName() != null) ? user.getName() : "Anonymous";
// Java 9+: better with Optional
String name = Optional.ofNullable(user.getName()).orElse("Anonymous");
```

### When to Use Ternary vs If-Else

```java
// ✅ GOOD: simple value assignment
int min = (a < b) ? a : b;

// ❌ BAD: side effects in ternary
(isValid) ? save(order) : logError(order);  // Use if-else instead

// ❌ BAD: deeply nested ternary
String x = a ? b ? "1" : c ? "2" : "3" : "4";  // Unreadable!
```

---

## 3. Switch Statement (Classic & Enhanced)

### Classic Switch (all Java versions)

```java
/**
 * Maps a day number (1-7) to its name.
 *
 * @param day the day number (1 = Monday)
 * @return the day name
 * @throws IllegalArgumentException if day is invalid
 */
public static String dayName(int day) {
    switch (day) {
        case 1:  return "Monday";
        case 2:  return "Tuesday";
        case 3:  return "Wednesday";
        case 4:  return "Thursday";
        case 5:  return "Friday";
        case 6:  return "Saturday";
        case 7:  return "Sunday";
        default: throw new IllegalArgumentException("Invalid day: " + day);
    }
}
```

### Fall-Through Behavior (Classic)

```java
// ⚠️ Without break, execution FALLS THROUGH to the next case

/**
 * Returns the number of days in a month (non-leap year).
 * Uses fall-through to group months with the same day count.
 *
 * @param month the month number (1-12)
 * @return number of days
 */
public static int daysInMonth(int month) {
    switch (month) {
        case 2:
            return 28;
        case 4: case 6: case 9: case 11:       // fall-through grouping
            return 30;
        case 1: case 3: case 5: case 7:
        case 8: case 10: case 12:               // fall-through grouping
            return 31;
        default:
            throw new IllegalArgumentException("Invalid month: " + month);
    }
}
```

### Switch on Strings and Enums

```java
// ── Switch on String (Java 7+) ──────────────────────────
/**
 * Parses an HTTP method string into a numeric code.
 *
 * @param method the HTTP method (case-sensitive)
 * @return numeric code for the method
 */
public static int httpMethodCode(String method) {
    switch (method) {
        case "GET":    return 1;
        case "POST":   return 2;
        case "PUT":    return 3;
        case "DELETE": return 4;
        case "PATCH":  return 5;
        default:       return -1;
    }
}

// ── Switch on Enum ──────────────────────────────────────
enum TrafficLight { RED, YELLOW, GREEN }

/**
 * Determines the action for a traffic light state.
 *
 * @param light the current traffic light color
 * @return the action to take
 */
public static String action(TrafficLight light) {
    switch (light) {
        case RED:    return "Stop";
        case YELLOW: return "Caution";
        case GREEN:  return "Go";
        default:     throw new AssertionError("Unknown light: " + light);
    }
}
```

### Switch Allowed Types

```java
// Types allowed in switch expression:
//   byte, short, char, int         (primitives)
//   Byte, Short, Character, Integer (wrappers)
//   String                          (Java 7+)
//   Enum                            (Java 5+)
//   Pattern matching                (Java 21+ preview)

// ❌ NOT allowed: long, float, double, boolean, Object
```

---

## 4. Switch Expressions (Java 14+)

Java 14 introduced **switch expressions** with arrow syntax — no fall-through, no break needed, and the switch itself produces a value.

```java
/**
 * Maps a day number to its type using a switch expression.
 *
 * @param day the day number (1 = Monday)
 * @return "Weekday" or "Weekend"
 */
public static String dayType(int day) {
    return switch (day) {
        case 1, 2, 3, 4, 5 -> "Weekday";      // comma-separated cases
        case 6, 7           -> "Weekend";
        default             -> throw new IllegalArgumentException("Invalid: " + day);
    };  // note: semicolon after the switch expression
}
```

### Arrow Syntax vs Colon Syntax

```java
// ── Arrow syntax (→): no fall-through, returns value ────
String result = switch (status) {
    case "ACTIVE"   -> "User is active";
    case "INACTIVE" -> "User is inactive";
    case "BANNED"   -> "User is banned";
    default         -> "Unknown status";
};

// ── Block body with yield (for multi-line) ──────────────
/**
 * Calculates shipping cost based on tier.
 * Premium gets free shipping; standard pays by weight.
 *
 * @param tier   the customer tier
 * @param weight the package weight in kg
 * @return shipping cost in dollars
 */
public static double shippingCost(String tier, double weight) {
    return switch (tier) {
        case "PREMIUM" -> 0.0;
        case "STANDARD" -> {
            double base = 5.0;
            double perKg = 1.5;
            yield base + (weight * perKg);    // yield returns value from block
        }
        case "ECONOMY" -> {
            yield 3.0 + (weight * 2.0);
        }
        default -> throw new IllegalArgumentException("Unknown tier: " + tier);
    };
}
```

### Exhaustiveness

```java
// Switch expressions MUST be exhaustive (all cases covered)

enum Season { SPRING, SUMMER, FALL, WINTER }

/**
 * Returns the typical temperature for a season.
 * No default needed — enum covers all cases.
 */
public static int avgTemp(Season season) {
    return switch (season) {
        case SPRING -> 65;
        case SUMMER -> 85;
        case FALL   -> 55;
        case WINTER -> 35;
        // No default needed! Compiler verifies all enum values are handled.
        // If you add a new enum value, this won't compile until you add a case.
    };
}
```

---

## 5. Loops — for, while, do-while, for-each

### For Loop

```java
/**
 * Computes the sum of array elements.
 * Uses a classic for-loop with index access.
 *
 * @param arr the input array
 * @return sum of all elements
 */
public static int sum(int[] arr) {
    int total = 0;
    for (int i = 0; i < arr.length; i++) {
        total += arr[i];
    }
    return total;
}

// ── Two-pointer for loop ────────────────────────────────
/**
 * Checks if a string is a palindrome using two pointers.
 */
public static boolean isPalindrome(String s) {
    for (int l = 0, r = s.length() - 1; l < r; l++, r--) {
        if (s.charAt(l) != s.charAt(r)) return false;
    }
    return true;
}

// ── Infinite for loop ───────────────────────────────────
for (;;) {
    // runs forever until break
    if (condition) break;
}
```

### Enhanced For-Each Loop (Java 5+)

```java
/**
 * Finds the maximum value in a list.
 *
 * @param nums the list of integers
 * @return the maximum value
 */
public static int findMax(List<Integer> nums) {
    int max = Integer.MIN_VALUE;
    for (int n : nums) {
        max = Math.max(max, n);
    }
    return max;
}

// Works with arrays, Iterable, Collections
for (String word : words) { ... }
for (Map.Entry<K, V> entry : map.entrySet()) { ... }
for (char c : str.toCharArray()) { ... }

// ⚠️ Cannot modify collection during for-each (ConcurrentModificationException)
// ⚠️ No index access — use traditional for loop if you need the index
```

### While Loop

```java
/**
 * Reverses a linked list iteratively.
 * Uses a while loop since termination depends on node traversal, not a counter.
 *
 * @param head the head of the linked list
 * @return the new head (previously the tail)
 */
public static ListNode reverse(ListNode head) {
    ListNode prev = null, cur = head;
    while (cur != null) {
        ListNode next = cur.next;
        cur.next = prev;
        prev = cur;
        cur = next;
    }
    return prev;
}

// ── While with sentinel ─────────────────────────────────
/**
 * Binary search using while loop.
 */
public static int binarySearch(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (arr[mid] == target)      return mid;
        else if (arr[mid] < target)  lo = mid + 1;
        else                         hi = mid - 1;
    }
    return -1;
}
```

### Do-While Loop

```java
/**
 * Reads user input until a valid number is entered.
 * Do-while guarantees the prompt appears at least once.
 */
public static int readValidNumber(Scanner scanner) {
    int number;
    do {
        System.out.print("Enter a positive number: ");
        number = scanner.nextInt();
    } while (number <= 0);
    return number;
}

// ── Do-while for digit extraction ───────────────────────
/**
 * Counts the digits in a number.
 * Uses do-while so that input 0 correctly returns 1.
 */
public static int digitCount(int n) {
    int count = 0;
    do {
        count++;
        n /= 10;
    } while (n != 0);
    return count;
}
```

### Loop Selection Guide

```
┌────────────────────────────────────────────────────────────┐
│  WHICH LOOP TO USE?                                        │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  Know the iteration count?                                 │
│  ├── Yes → for (int i = 0; i < n; i++)                     │
│  └── No                                                    │
│      ├── Need index access?                                │
│      │   ├── Yes → for (int i = ...)                       │
│      │   └── No  → for (T item : collection)               │
│      ├── Condition checked BEFORE first iteration?         │
│      │   └── while (condition) { ... }                     │
│      └── Must execute AT LEAST once?                       │
│          └── do { ... } while (condition);                  │
│                                                            │
│  Iterating a collection without modification?              │
│  └── for-each (cleanest, preferred)                        │
│                                                            │
│  Need to traverse nodes/pointers?                          │
│  └── while (node != null)                                  │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

---

## 6. Break, Continue, and Labels

```java
// ── break: exit the innermost loop ──────────────────────
/**
 * Finds the first negative number in an array.
 */
public static int firstNegative(int[] arr) {
    int result = -1;
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] < 0) {
            result = arr[i];
            break;         // stop searching
        }
    }
    return result;
}

// ── continue: skip current iteration ────────────────────
/**
 * Sums only the positive numbers in an array.
 */
public static int sumPositive(int[] arr) {
    int sum = 0;
    for (int n : arr) {
        if (n <= 0) continue;  // skip non-positive
        sum += n;
    }
    return sum;
}

// ── Labeled break: exit outer loop ──────────────────────
/**
 * Searches for a target in a 2D matrix.
 * Uses a labeled break to exit both loops when found.
 *
 * @return the coordinates [row, col], or null if not found
 */
public static int[] search2D(int[][] matrix, int target) {
    int[] result = null;
    outer:                                // label
    for (int i = 0; i < matrix.length; i++) {
        for (int j = 0; j < matrix[i].length; j++) {
            if (matrix[i][j] == target) {
                result = new int[]{i, j};
                break outer;              // exits BOTH loops
            }
        }
    }
    return result;
}

// ── Labeled continue ────────────────────────────────────
/**
 * Prints pairs where inner value > outer value.
 */
outer:
for (int i = 0; i < 5; i++) {
    for (int j = 0; j < 5; j++) {
        if (j <= i) continue outer;    // skip to next i
        System.out.println(i + "," + j);
    }
}
```

---

## 7. Logical Operators — &&, ||, !, ^

```java
// ── && (AND): both must be true ─────────────────────────
if (age >= 18 && hasLicense) {
    // Can drive
}

// ── || (OR): at least one must be true ──────────────────
if (isAdmin || isOwner) {
    // Has access
}

// ── ! (NOT): inverts boolean ────────────────────────────
if (!list.isEmpty()) {
    process(list);
}

// ── ^ (XOR): exactly one must be true ───────────────────
if (hasPassport ^ hasDrivingLicense) {
    // Exactly one form of ID (not both, not neither)
}

// ── Combining operators (precedence: ! > && > ||) ───────
/**
 * Checks if a user can access premium content.
 * Must be (subscribed AND not expired) OR be an admin.
 *
 * @return true if access is granted
 */
public static boolean canAccess(User user) {
    return (user.isSubscribed() && !user.isExpired()) || user.isAdmin();
}

// ── De Morgan's Laws (useful for simplification) ────────
// !(A && B)  ==  !A || !B
// !(A || B)  ==  !A && !B

// Example: "not (logged in AND verified)" == "not logged in OR not verified"
if (!(loggedIn && verified)) { ... }
// is the same as:
if (!loggedIn || !verified) { ... }
```

---

## 8. Comparison Operators — ==, equals, compareTo

```java
// ── Primitives: use == ──────────────────────────────────
int a = 5, b = 5;
if (a == b) { }      // true — compares VALUES

// ── Objects: use .equals() ──────────────────────────────
String s1 = new String("hello");
String s2 = new String("hello");

if (s1 == s2) { }          // FALSE — compares REFERENCES (different objects)
if (s1.equals(s2)) { }     // TRUE  — compares CONTENT

// ── String interning (pool) ─────────────────────────────
String s3 = "hello";
String s4 = "hello";
if (s3 == s4) { }          // TRUE — same interned reference (string pool)
// ⚠️ Never rely on == for Strings; always use .equals()

// ── Integer caching (-128 to 127) ───────────────────────
Integer x = 127, y = 127;
if (x == y) { }            // TRUE — cached range

Integer p = 128, q = 128;
if (p == q) { }            // FALSE — outside cached range!
if (p.equals(q)) { }       // TRUE — always use .equals() for wrapper types

// ── Null-safe comparison ────────────────────────────────
// ❌ "hello".equals(possiblyNull)   → fine if "hello" is the constant
// ❌ possiblyNull.equals("hello")   → NPE if null!
// ✅ Objects.equals(a, b)           → null-safe (Java 7+)
if (Objects.equals(name, "admin")) { }

// ── compareTo (for ordering) ────────────────────────────
/**
 * Compares two strings lexicographically.
 *
 * @return negative if a < b, zero if equal, positive if a > b
 */
int cmp = "apple".compareTo("banana");  // negative (a < b)

// Comparable interface
if (date1.compareTo(date2) < 0) {
    // date1 is before date2
}

// ── instanceof (type checking) ──────────────────────────
if (obj instanceof String) {
    String s = (String) obj;       // explicit cast (pre-Java 16)
}

// Java 16+ pattern matching:
if (obj instanceof String s) {
    // s is already cast — no explicit cast needed
    System.out.println(s.length());
}
```

---

## 9. Bitwise Operators for Conditions

```java
// ── Check if number is even/odd ─────────────────────────
/**
 * Checks parity using bitwise AND (faster than modulo).
 * The least significant bit is 0 for even, 1 for odd.
 */
public static boolean isEven(int n) {
    return (n & 1) == 0;
}

// ── Check if power of 2 ────────────────────────────────
/**
 * A power of 2 has exactly one bit set: n & (n-1) clears it.
 */
public static boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (n - 1)) == 0;
}

// ── Swap without temp variable ──────────────────────────
a ^= b;   // a = a XOR b
b ^= a;   // b = b XOR a (original a)
a ^= b;   // a = a XOR b (original b)

// ── Bit flags for permissions ───────────────────────────
static final int READ    = 1;   // 001
static final int WRITE   = 2;   // 010
static final int EXECUTE = 4;   // 100

int permission = READ | WRITE;          // 011 (read + write)
boolean canRead = (permission & READ) != 0;   // true
boolean canExec = (permission & EXECUTE) != 0; // false

// ── Conditional set/clear bits ──────────────────────────
permission |= EXECUTE;    // SET the execute bit     → 111
permission &= ~WRITE;     // CLEAR the write bit     → 101
permission ^= READ;       // TOGGLE the read bit     → 100
```

---

## 10. Null Handling — Optional, Objects, Patterns

```java
// ── Traditional null check ──────────────────────────────
if (user != null && user.getAddress() != null && user.getAddress().getCity() != null) {
    return user.getAddress().getCity();
}
return "Unknown";

// ── Optional (Java 8+) — better null handling ───────────
/**
 * Gets the user's city, returning "Unknown" if any part of the chain is absent.
 * Uses Optional to avoid nested null checks.
 *
 * @param user the user (may be null)
 * @return the city name or "Unknown"
 */
public static String getCity(User user) {
    return Optional.ofNullable(user)
            .map(User::getAddress)
            .map(Address::getCity)
            .orElse("Unknown");
}

// ── Optional methods ────────────────────────────────────
Optional<String> opt = Optional.ofNullable(getValue());

opt.isPresent();                    // true if value exists
opt.isEmpty();                      // true if no value (Java 11+)
opt.get();                          // get value (throws if empty!)
opt.orElse("default");              // value or default
opt.orElseGet(() -> compute());     // value or lazy default
opt.orElseThrow();                  // value or NoSuchElementException
opt.ifPresent(v -> use(v));         // run action if present
opt.ifPresentOrElse(                // action or fallback (Java 9+)
    v -> use(v),
    () -> handleMissing()
);
opt.map(String::toUpperCase);       // transform if present
opt.filter(s -> s.length() > 3);   // filter if present
opt.flatMap(this::findUser);        // chain Optionals

// ── Objects utility methods (Java 7+) ───────────────────
Objects.equals(a, b);               // null-safe equals
Objects.hash(a, b, c);              // null-safe hashCode
Objects.toString(obj, "default");   // null-safe toString
Objects.requireNonNull(param);      // throw NPE if null (fail-fast)
Objects.requireNonNull(param, "param must not be null");

// ── Null Object Pattern ─────────────────────────────────
/**
 * Instead of returning null, return a "no-op" implementation.
 */
interface Logger {
    void log(String msg);
}

class ConsoleLogger implements Logger {
    public void log(String msg) { System.out.println(msg); }
}

class NullLogger implements Logger {
    public void log(String msg) { /* do nothing */ }
}

Logger logger = isDebug ? new ConsoleLogger() : new NullLogger();
logger.log("safe to call — never null");
```

---

## 11. Pattern Matching (Java 16+)

### instanceof Pattern Matching

```java
// ── Before Java 16 ──────────────────────────────────────
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}

// ── Java 16+: pattern variable ──────────────────────────
/**
 * Formats any object to a display string.
 * Uses pattern matching to avoid explicit casts.
 *
 * @param obj the object to format
 * @return formatted string representation
 */
public static String format(Object obj) {
    if (obj instanceof Integer i) {
        return "Integer: " + i;
    } else if (obj instanceof String s && !s.isEmpty()) {
        return "String: " + s.toUpperCase();
    } else if (obj instanceof double[] arr) {
        return "Array of length " + arr.length;
    } else if (obj instanceof List<?> list && list.size() > 0) {
        return "List with " + list.size() + " elements";
    }
    return "Unknown: " + obj;
}
```

### Switch Pattern Matching (Java 21+)

```java
/**
 * Formats a shape's area using switch pattern matching.
 * Each case destructures the object type and extracts fields.
 *
 * @param shape the shape to describe
 * @return a description of the shape
 */
public static String describe(Object shape) {
    return switch (shape) {
        case Circle c   -> "Circle with radius " + c.radius();
        case Rectangle r when r.width() == r.height()
                        -> "Square with side " + r.width();
        case Rectangle r -> "Rectangle " + r.width() + "×" + r.height();
        case null       -> "No shape";
        default         -> "Unknown shape";
    };
}
```

---

## 12. Sealed Classes & Exhaustive Switches (Java 17+)

```java
/**
 * Sealed interface: only permitted subtypes can implement it.
 * This enables exhaustive switch without default.
 */
public sealed interface Shape permits Circle, Rectangle, Triangle {
    double area();
}

public record Circle(double radius) implements Shape {
    public double area() { return Math.PI * radius * radius; }
}

public record Rectangle(double width, double height) implements Shape {
    public double area() { return width * height; }
}

public record Triangle(double base, double height) implements Shape {
    public double area() { return 0.5 * base * height; }
}

/**
 * Computes area description. The compiler guarantees all Shape subtypes are handled.
 * Adding a new Shape subtype will cause a compile error here until a case is added.
 *
 * @param shape the shape to describe
 * @return description with computed area
 */
public static String describeArea(Shape shape) {
    return switch (shape) {
        case Circle c    -> "Circle area: " + String.format("%.2f", c.area());
        case Rectangle r -> "Rectangle area: " + String.format("%.2f", r.area());
        case Triangle t  -> "Triangle area: " + String.format("%.2f", t.area());
        // No default needed — sealed ensures exhaustiveness
    };
}
```

---

## 13. Exception Handling as Control Flow

```java
// ── try-catch-finally ───────────────────────────────────
/**
 * Parses an integer from a string, returning a default on failure.
 *
 * @param s          the string to parse
 * @param defaultVal the fallback value if parsing fails
 * @return the parsed integer or the default value
 */
public static int parseInt(String s, int defaultVal) {
    try {
        return Integer.parseInt(s);
    } catch (NumberFormatException e) {
        return defaultVal;
    }
}

// ── Multi-catch (Java 7+) ───────────────────────────────
try {
    riskyOperation();
} catch (IOException | SQLException e) {
    log(e.getMessage());
}

// ── try-with-resources (Java 7+) ────────────────────────
/**
 * Reads the first line of a file.
 * The BufferedReader is automatically closed, even on exception.
 *
 * @param path the file path
 * @return the first line, or null if file is empty
 * @throws IOException if the file cannot be read
 */
public static String firstLine(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        return br.readLine();
    }   // br.close() called automatically
}

// ── Custom exception hierarchy ──────────────────────────
/**
 * Thrown when a business rule is violated.
 * Checked exceptions for recoverable errors.
 */
public class InsufficientFundsException extends Exception {
    private final double deficit;

    public InsufficientFundsException(double deficit) {
        super("Insufficient funds: short by $" + deficit);
        this.deficit = deficit;
    }

    public double getDeficit() { return deficit; }
}

/**
 * Withdraws money from an account.
 *
 * @param amount the amount to withdraw
 * @throws InsufficientFundsException if balance is too low
 */
public void withdraw(double amount) throws InsufficientFundsException {
    if (amount > balance) {
        throw new InsufficientFundsException(amount - balance);
    }
    balance -= amount;
}
```

---

## 14. Javadoc Conventions for Conditionals

### How to Document Methods with Conditions

```java
/**
 * Validates and processes user registration.
 *
 * <p>Validation rules:
 * <ul>
 *   <li>Username must be 3-20 alphanumeric characters</li>
 *   <li>Email must contain '@' and a valid domain</li>
 *   <li>Password must be at least 8 characters with one digit</li>
 * </ul>
 *
 * @param username the desired username (3-20 alphanumeric chars)
 * @param email    the user's email address
 * @param password the password (min 8 chars, at least 1 digit)
 * @return the created User object
 * @throws IllegalArgumentException if any field fails validation
 * @throws DuplicateUserException   if username already exists
 * @see User#isValid()
 * @since 2.0
 */
public User register(String username, String email, String password) { ... }
```

### Javadoc Tags Reference

```java
/**
 * Brief one-line summary (mandatory).
 *
 * <p>Extended description with details (optional).
 * Can include HTML: <b>bold</b>, <code>code</code>,
 * <pre>code blocks</pre>.
 *
 * @param  paramName  description of parameter
 * @return            description of return value
 * @throws ExcType    when this exception is thrown
 * @see               related class or method
 * @since             version when this was introduced
 * @deprecated        why deprecated and what to use instead
 *
 * {@code inlineCode}          → renders as monospace
 * {@link ClassName#method()}  → clickable link
 * {@literal <not html>}       → escape HTML
 */
```

### Documenting Conditional Behavior

```java
/**
 * Finds an element in a sorted array using binary search.
 *
 * <p><b>Precondition:</b> The array must be sorted in ascending order.
 * Behavior is undefined for unsorted arrays.
 *
 * <p><b>Postcondition:</b>
 * <ul>
 *   <li>If found: returns the index of the element (0-based)</li>
 *   <li>If not found: returns {@code -(insertion point) - 1}</li>
 * </ul>
 *
 * <p><b>Edge cases:</b>
 * <ul>
 *   <li>Empty array: returns -1</li>
 *   <li>Null array: throws NullPointerException</li>
 *   <li>Duplicate elements: returns any matching index (not necessarily first)</li>
 * </ul>
 *
 * @param arr    a sorted array of integers (must not be null)
 * @param target the value to search for
 * @return the index of target, or a negative value if not found
 * @throws NullPointerException if arr is null
 */
public static int binarySearch(int[] arr, int target) { ... }
```

---

## 15. Common Interview Patterns Using Conditionals

### Two-Pointer with Conditions

```java
/**
 * Determines if a sorted array contains two numbers that sum to target.
 * Uses two pointers from both ends; moves left pointer if sum is too small,
 * right pointer if too large.
 *
 * @param nums   sorted array of integers
 * @param target the desired sum
 * @return true if a pair with the given sum exists
 */
public static boolean twoSumSorted(int[] nums, int target) {
    int l = 0, r = nums.length - 1;
    while (l < r) {
        int sum = nums[l] + nums[r];
        if (sum == target)  return true;
        else if (sum < target) l++;
        else                   r--;
    }
    return false;
}
```

### Binary Search Boundary Conditions

```java
/**
 * Finds the leftmost (first) position of target in a sorted array.
 * Key insight: on match, don't return — shrink right boundary to find earlier occurrence.
 *
 * @param nums   sorted array
 * @param target the value to find
 * @return the first index of target, or -1 if not found
 */
public static int leftBound(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1, result = -1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) {
            result = mid;
            hi = mid - 1;      // keep searching left
        } else if (nums[mid] < target) {
            lo = mid + 1;
        } else {
            hi = mid - 1;
        }
    }
    return result;
}
```

### DFS/Backtracking with Pruning Conditions

```java
/**
 * Generates all valid combinations of n pairs of parentheses.
 * Pruning conditions:
 *   - Can add '(' if open count < n
 *   - Can add ')' only if close count < open count
 *
 * @param n number of pairs
 * @return all valid combinations
 */
public static List<String> generateParenthesis(int n) {
    List<String> result = new ArrayList<>();
    backtrack(result, new StringBuilder(), 0, 0, n);
    return result;
}

private static void backtrack(List<String> result, StringBuilder sb,
                               int open, int close, int n) {
    if (sb.length() == 2 * n) {
        result.add(sb.toString());
        return;
    }
    if (open < n) {
        sb.append('(');
        backtrack(result, sb, open + 1, close, n);
        sb.deleteCharAt(sb.length() - 1);
    }
    if (close < open) {
        sb.append(')');
        backtrack(result, sb, open, close + 1, n);
        sb.deleteCharAt(sb.length() - 1);
    }
}
```

### DP State Transition Conditions

```java
/**
 * Computes the minimum cost to climb stairs.
 * At each step, choose the cheaper of:
 *   - Coming from 1 step below: dp[i-1] + cost[i-1]
 *   - Coming from 2 steps below: dp[i-2] + cost[i-2]
 *
 * @param cost the cost at each step
 * @return minimum cost to reach the top
 */
public static int minCostClimbingStairs(int[] cost) {
    int n = cost.length;
    int prev2 = 0, prev1 = 0;
    for (int i = 2; i <= n; i++) {
        int cur = Math.min(prev1 + cost[i - 1], prev2 + cost[i - 2]);
        prev2 = prev1;
        prev1 = cur;
    }
    return prev1;
}
```

---

## 16. Quick Reference — Cheat Sheet

### Conditional Constructs Summary

| Construct | When to Use | Key Syntax |
|-----------|------------|------------|
| `if-else` | General branching, complex conditions | `if (cond) { } else if { } else { }` |
| `ternary` | Simple inline value selection | `cond ? valTrue : valFalse` |
| `switch` (classic) | Multiple discrete values, fall-through needed | `switch (x) { case 1: ...; break; }` |
| `switch` (expression) | Multiple values returning a result (Java 14+) | `var r = switch (x) { case 1 -> ...; };` |
| `for` | Known iteration count, index access needed | `for (int i = 0; i < n; i++)` |
| `for-each` | Iterating collections without index | `for (T item : collection)` |
| `while` | Unknown iterations, condition before body | `while (cond) { }` |
| `do-while` | Must execute at least once | `do { } while (cond);` |
| `try-catch` | Exception-based control flow | `try { } catch (E e) { }` |
| `Optional` | Null avoidance, functional chaining | `Optional.ofNullable(x).map(...).orElse(...)` |
| `pattern match` | Type-safe instanceof + cast (Java 16+) | `if (obj instanceof String s)` |

### Operator Precedence (highest to lowest)

```
┌──────────────────────────────────────────┐
│  1. ()                     Parentheses   │
│  2. ++ --  !  ~            Unary         │
│  3. *  /  %                Multiplicative│
│  4. +  -                   Additive      │
│  5. <<  >>  >>>            Shift         │
│  6. <  <=  >  >=          Relational    │
│  7. ==  !=                 Equality      │
│  8. &                      Bitwise AND   │
│  9. ^                      Bitwise XOR   │
│ 10. |                      Bitwise OR    │
│ 11. &&                     Logical AND   │
│ 12. ||                     Logical OR    │
│ 13. ?:                     Ternary       │
│ 14. =  +=  -=  *=  ...    Assignment    │
└──────────────────────────────────────────┘
```

### Common Pitfalls

```java
// ❌ Assignment instead of comparison
if (x = 5) { }         // Compile error (not boolean)
if (flag = true) { }   // Compiles but ALWAYS true! Use: if (flag)

// ❌ Floating point comparison
if (0.1 + 0.2 == 0.3) { }   // FALSE! (floating point imprecision)
if (Math.abs(a - b) < 1e-9) { }  // ✅ Correct approach

// ❌ String comparison with ==
if (str == "hello") { }       // Compares REFERENCES
if (str.equals("hello")) { }  // ✅ Compares CONTENT

// ❌ Integer wrapper comparison with ==
if (Integer.valueOf(200) == Integer.valueOf(200)) { }   // FALSE!
if (Integer.valueOf(200).equals(200)) { }               // ✅ TRUE

// ❌ Missing break in switch (unintentional fall-through)
switch (x) {
    case 1: doA();     // falls through to case 2!
    case 2: doB();     // ⚠️ also runs for case 1
}

// ❌ Null dereference without check
user.getName().length();              // NPE if user or getName() is null
Optional.ofNullable(user)             // ✅ Safe chain
    .map(User::getName)
    .map(String::length)
    .orElse(0);
```

### Best Practices

1. **Prefer guard clauses** over deeply nested if-else chains
2. **Use switch expressions** (Java 14+) over switch statements for cleaner code
3. **Always use `.equals()`** for object comparison, never `==`
4. **Short-circuit with `&&` and `||`**, not `&` and `|` for boolean logic
5. **Use `Optional`** instead of returning or passing null
6. **Use `Objects.requireNonNull()`** at method entry for fail-fast validation
7. **Prefer for-each** over indexed for-loops when you don't need the index
8. **Use pattern matching** (Java 16+) to combine instanceof and cast
9. **Add Javadoc** for any method with non-obvious conditional behavior
10. **Keep conditional complexity low** — extract complex conditions into named boolean methods

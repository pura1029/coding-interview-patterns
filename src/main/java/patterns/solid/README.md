# SOLID Principles — Explained with Clear Java Examples

The **SOLID** principles are five design guidelines introduced by **Robert C. Martin (Uncle Bob)** that make object-oriented code easier to **change**, **test**, and **extend**. They are the foundation of clean architecture and appear in virtually every senior-level software engineering interview.

---

## Why SOLID Matters

Without SOLID, codebases degrade over time into what Uncle Bob calls **"design rot"**:

| Symptom | What Happens | Which Principle Prevents It |
|---------|-------------|---------------------------|
| **Rigidity** | A small change requires editing many files | SRP, OCP |
| **Fragility** | Fixing one thing breaks something unrelated | SRP, LSP |
| **Immobility** | Can't reuse a module in another project because it drags too many dependencies | DIP, ISP |
| **Viscosity** | Doing things the wrong way is easier than doing them the right way | OCP, DIP |
| **Needless Complexity** | Abstractions that nobody asked for | ISP, SRP |

```
Without SOLID                          With SOLID
┌─────────────────────┐          ┌─────────┐  ┌─────────┐  ┌─────────┐
│   God Class          │          │  Auth    │  │ Profile │  │  Email  │
│  - auth()            │    →     │ Service  │  │ Service │  │ Service │
│  - profile()         │          └─────────┘  └─────────┘  └─────────┘
│  - email()           │               │             │             │
│  - log()             │               ▼             ▼             ▼
│  - validate()        │          (each can change, test, deploy independently)
└─────────────────────┘
```

---

## Overview

| Principle | Letter | Rule | Key Question | Real-World Analogy |
|-----------|--------|------|--------------|-------------------|
| **Single Responsibility** | S | A class should have only one reason to change | Does this class do more than one job? | A chef cooks, a waiter serves — one role each |
| **Open/Closed** | O | Open for extension, closed for modification | Can I add behavior without editing existing code? | USB ports — plug in new devices without rewiring the motherboard |
| **Liskov Substitution** | L | Subtypes must be substitutable for base types | Will a subclass break callers of the parent? | Any ATM card works in any ATM — no surprises |
| **Interface Segregation** | I | Don't force unused interface methods | Is any class forced to implement methods it doesn't need? | Restaurant menu — you order only what you want |
| **Dependency Inversion** | D | Depend on abstractions, not concretions | Does high-level code depend on low-level details? | Wall outlet — any appliance plugs in via a standard interface |

---

## S — Single Responsibility Principle (SRP)

> A class should have only **one reason to change**.  
> *— Robert C. Martin*

### The Concept

A "reason to change" means a **stakeholder** or **business concern**. If a class handles authentication AND email sending, then changes requested by the security team (auth logic) and the marketing team (email templates) both force edits to the same class. This coupling means a security fix could accidentally break email formatting.

### Real-World Analogy

In a restaurant, the **chef** cooks, the **waiter** serves, and the **cashier** handles payments. If one person did all three jobs, a mistake in cooking could delay payments. Separation of concerns makes the restaurant (system) more resilient.

### What Happens When You Violate SRP

```
┌────────────────────────────────────┐
│         UserGodClass               │
│  ┌──────────┐  ┌──────────┐       │
│  │ Auth     │  │ Profile  │       │  ← 3 reasons to change
│  │ Logic    │  │ Display  │       │  ← Auth team, UI team,
│  └──────────┘  └──────────┘       │     and Ops team all
│  ┌──────────┐                     │     edit the SAME file
│  │ Email    │                     │
│  │ Sending  │                     │
│  └──────────┘                     │
└────────────────────────────────────┘
   Problem: Change email template → retest auth + profile too
```

**Consequences:**
- Merge conflicts — multiple teams edit the same file
- Cascading test failures — unrelated changes break unrelated tests
- Harder to understand — new developers must comprehend the whole class to change one part
- Can't reuse one concern independently (e.g., reuse `EmailService` in another project)

### The Fix

```
┌──────────┐     ┌──────────────┐     ┌──────────────┐
│ UserAuth │     │ UserProfile  │     │ EmailService │
│ ─────────│     │ ────────────│     │ ────────────│
│ login()  │     │ getInfo()   │     │ send()      │
│ logout() │     │ update()    │     │ format()    │
└──────────┘     └──────────────┘     └──────────────┘
     ↑                  ↑                    ↑
 Security team      UI team             Ops team
 changes this      changes this        changes this
```

```java
// BAD: Three responsibilities in one class
class User {
    boolean authenticate(String pw) { ... }
    String getProfile() { ... }
    void sendEmail(String subject) { ... }
}

// GOOD: One responsibility per class
class UserAuth    { boolean authenticate(User u, String pw) { ... } }
class UserProfile { String getProfile(User u) { ... } }
class EmailService { void sendEmail(String to, String subj) { ... } }
```

### How to Identify SRP Violations

Ask these questions:
1. **Can you describe the class in one sentence without "and"?** If not, it has multiple responsibilities.
2. **Who requests changes to this class?** If multiple teams/stakeholders, split it.
3. **What reasons could force this class to change?** Each reason = one responsibility.

### Common SRP Violations in Production

| Violation | Responsibilities Mixed | Fix |
|-----------|----------------------|-----|
| `UserController` handles validation, business logic, and DB queries | 3 concerns | Controller → Service → Repository layers |
| `ReportGenerator` fetches data, formats HTML, and sends emails | 3 concerns | `DataFetcher`, `HtmlFormatter`, `EmailSender` |
| `OrderService` calculates tax, applies discounts, and processes payment | 3 concerns | `TaxCalculator`, `DiscountEngine`, `PaymentProcessor` |

**Run:** `java patterns.solid.SingleResponsibilityPrinciple`

---

## O — Open/Closed Principle (OCP)

> Classes should be **open for extension** but **closed for modification**.  
> *— Bertrand Meyer (1988), popularized by Robert C. Martin*

### The Concept

Once a class is written, tested, and deployed, you should be able to **add new behavior** without **changing its source code**. This is achieved through **abstraction** — define a contract (interface or abstract class), and new behavior is added by creating new implementations, not editing existing ones.

### Real-World Analogy

A **USB port** on your laptop is closed for modification (you can't rewire it) but open for extension (you can plug in a mouse, keyboard, webcam, or a device that doesn't exist yet). The laptop doesn't need to be redesigned for each new device.

### What Happens When You Violate OCP

```
// Every new shape requires editing this method
double calculateArea(Object shape) {
    if (shape instanceof Rectangle r) return r.w * r.h;
    else if (shape instanceof Circle c) return PI * c.r * c.r;
    // NEW SHAPE? Must add another else-if here!
    // → Existing code is modified
    // → Must retest ALL existing branches
    // → Risk of breaking Rectangle/Circle logic
}
```

**Consequences:**
- Adding a new feature (Triangle) risks breaking existing features (Rectangle, Circle)
- Growing if-else / switch chains that become unreadable
- Every addition requires retesting the entire method
- Violates SRP — the calculator knows about every shape type

### The Fix — Polymorphism

```
             ┌───────────┐
             │  Shape     │ ← interface (contract)
             │  area()    │
             └─────┬─────┘
          ┌────────┼──────────┐
          ▼        ▼          ▼
    ┌──────────┐ ┌────────┐ ┌──────────┐
    │Rectangle │ │Circle  │ │Triangle  │  ← new shapes added
    │area()=w*h│ │area()  │ │area()    │    WITHOUT changing
    └──────────┘ │=πr²    │ │=½bh      │    existing code
                 └────────┘ └──────────┘
```

```java
// GOOD: Add new shapes without modifying existing code
interface Shape { double area(); }
class Rectangle implements Shape { double area() { return w * h; } }
class Circle    implements Shape { double area() { return PI * r * r; } }
class Triangle  implements Shape { double area() { return 0.5 * b * h; } }

// AreaCalculator NEVER changes — it works with the Shape interface
class AreaCalculator {
    double totalArea(List<Shape> shapes) {
        return shapes.stream().mapToDouble(Shape::area).sum();
    }
}
```

### Techniques to Achieve OCP

| Technique | How | Example |
|-----------|-----|---------|
| **Interface / Abstract class** | Define contract, add new implementations | `Shape` → `Rectangle`, `Circle` |
| **Strategy Pattern** | Inject behavior via interface | `SortStrategy` → `QuickSort`, `MergeSort` |
| **Decorator Pattern** | Wrap existing behavior with new behavior | `BufferedInputStream(new FileInputStream(...))` |
| **Template Method** | Base class defines skeleton, subclasses override steps | `AbstractController.handle()` |
| **Plugin architecture** | Load new modules at runtime | VSCode extensions, Jenkins plugins |

### OCP in Frameworks You Use Daily

| Framework | OCP in Action |
|-----------|--------------|
| **Spring** | Add new `@Controller` classes — framework doesn't change |
| **JUnit** | Add new `@Test` methods — test runner doesn't change |
| **Servlet API** | Add new `HttpServlet` implementations — container doesn't change |
| **JDBC** | Add new `Driver` implementations — `DriverManager` doesn't change |

**Run:** `java patterns.solid.OpenClosedPrinciple`

---

## L — Liskov Substitution Principle (LSP)

> If S is a subtype of T, then objects of type T may be replaced with objects of type S **without altering the correctness** of the program.  
> *— Barbara Liskov (1987)*

### The Concept

This is the formal way of saying: **a subclass should never surprise the caller**. If a method accepts a `Bird`, it should work correctly whether it receives an `Eagle`, a `Sparrow`, or a `Penguin`. If `Penguin.fly()` throws an exception, it violates LSP because the caller expected all birds to fly.

### Real-World Analogy

An **ATM card** works at any ATM machine. If your bank issued a "special" card that caused ATMs to crash, that card violates the substitution principle — it can't be used wherever a normal card is expected.

### What Happens When You Violate LSP

```
List<Bird> birds = List.of(new Eagle(), new Sparrow(), new Penguin());

for (Bird b : birds) {
    b.fly();    // 💥 Penguin throws UnsupportedOperationException!
}
```

**Consequences:**
- Callers must add `instanceof` checks to protect against broken subtypes
- Polymorphism becomes unreliable — you can't trust the base type contract
- Defensive code spreads like a virus: `if (bird instanceof Penguin) { skip; }`
- Defeats the purpose of inheritance

### Rules for LSP Compliance

LSP is more than "don't throw exceptions." A subclass must obey all of these:

| Rule | Meaning | Violation Example |
|------|---------|-------------------|
| **Preconditions cannot be strengthened** | Subclass can't demand more than parent | Parent accepts any `int`; child rejects negatives |
| **Postconditions cannot be weakened** | Subclass must deliver at least what parent promises | Parent guarantees sorted output; child returns unsorted |
| **Invariants must be preserved** | Subclass can't break parent's guarantees | `Rectangle` guarantees `area = w × h`; `Square.setWidth()` silently also sets height, breaking callers |
| **No new exceptions** | Subclass shouldn't throw exceptions parent doesn't | `Penguin.fly()` throws `UnsupportedOperationException` |
| **History constraint** | Subclass can't change state in ways parent doesn't allow | Immutable parent; mutable subclass |

### The Classic Rectangle–Square Problem

```
class Rectangle {
    void setWidth(int w)  { this.width = w; }
    void setHeight(int h) { this.height = h; }
    int area() { return width * height; }
}

class Square extends Rectangle {
    void setWidth(int w)  { this.width = w; this.height = w; }   // VIOLATION!
    void setHeight(int h) { this.height = h; this.width = h; }  // VIOLATION!
}

// Caller expects Rectangle behavior:
Rectangle r = new Square();
r.setWidth(5);
r.setHeight(10);
r.area();  // Expected: 50, Got: 100 — LSP violated!
```

**Fix:** Don't make `Square` extend `Rectangle`. Use a `Shape` interface instead.

### The Fix — Proper Hierarchy

```
                 ┌─────────┐
                 │  Bird    │ ← base: name(), eat()
                 └────┬────┘
            ┌─────────┴─────────┐
      ┌───────────┐       ┌────────────┐
      │FlyingBird │       │SwimmingBird│
      │  fly()    │       │  swim()    │
      └─────┬─────┘       └─────┬──────┘
            │                   │
      ┌─────┴─────┐      ┌─────┴──────┐
      │  Eagle    │      │  Penguin   │
      └───────────┘      └────────────┘
      Eagle.fly() ✓       Penguin.swim() ✓
      (no surprise)       (no surprise)
```

```java
interface Bird       { String eat(); }
interface FlyingBird extends Bird { String fly(); }
interface SwimmingBird extends Bird { String swim(); }

class Eagle   implements FlyingBird   { ... }
class Penguin implements SwimmingBird { ... }
```

### How to Detect LSP Violations

1. **Grep for `instanceof`** — if callers check the concrete type, the abstraction is leaking
2. **Grep for `UnsupportedOperationException`** — a subclass that can't fulfill the contract
3. **Look at overridden methods** — does the override change the meaning or throw?
4. **Write a test using only the base type** — if it fails for any subtype, LSP is violated

**Run:** `java patterns.solid.LiskovSubstitutionPrinciple`

---

## I — Interface Segregation Principle (ISP)

> No client should be forced to depend on methods it does not use.  
> *— Robert C. Martin*

### The Concept

A **fat interface** is one that has too many methods. When a class implements a fat interface, it's forced to provide implementations for methods that are irrelevant to it — usually as empty methods or `throw new UnsupportedOperationException()`. The fix is to split the fat interface into smaller, focused ones. Each client implements only the interfaces it needs.

### Real-World Analogy

At a **restaurant**, you get a menu with just food items. You don't get the chef's cookbook, the supplier's contact list, and the accounting spreadsheet. Each stakeholder gets only the "interface" they need.

### What Happens When You Violate ISP

```
interface Machine {
    void print(String doc);
    void scan(String doc);
    void fax(String doc);
    void staple(String doc);
    void collate(String doc);
}

class SimplePrinter implements Machine {
    void print(String doc) { /* works */ }
    void scan(String doc)  { throw new UnsupportedOperationException(); }  // FORCED
    void fax(String doc)   { throw new UnsupportedOperationException(); }  // FORCED
    void staple(String doc){ throw new UnsupportedOperationException(); }  // FORCED
    void collate(String doc){ throw new UnsupportedOperationException(); } // FORCED
}
```

**Consequences:**
- `SimplePrinter` is coupled to `scan`, `fax`, `staple`, `collate` even though it only prints
- If `Machine` adds a new method `photocopy()`, EVERY implementor must be updated — even those that can't photocopy
- Violates LSP — callers of `Machine` can't safely call `scan()` on a `SimplePrinter`
- Testing burden — you must test/mock methods that the class doesn't actually use

### The Fix — Split the Interface

```
    Fat Interface                    Segregated Interfaces
┌──────────────────┐          ┌──────────┐ ┌──────────┐ ┌────────┐
│    Machine       │          │Printable │ │Scannable │ │Faxable │
│  print()         │    →     │ print()  │ │ scan()   │ │ fax()  │
│  scan()          │          └────┬─────┘ └────┬─────┘ └───┬────┘
│  fax()           │               │            │           │
└──────────────────┘               ▼            ▼           ▼
                            SimplePrinter   Scanner     AllInOne
                            (Printable)   (Scannable)  (all three)
```

```java
interface Printable { void print(String doc); }
interface Scannable { void scan(String doc); }
interface Faxable   { void fax(String doc); }

class SimplePrinter implements Printable { ... }
class AllInOne      implements Printable, Scannable, Faxable { ... }
```

### ISP in the Java Standard Library

| Fat Interface (Avoid) | Segregated Alternative (Preferred) |
|----------------------|-----------------------------------|
| `java.util.Iterator` (has `remove()` that most don't use) | Since Java 8: `remove()` is a default method — optional to override |
| `java.sql.ResultSet` (150+ methods) | Hard to fix — but shows why ISP matters |
| `javax.servlet.Servlet` (5 methods) | `HttpServlet` — you only override `doGet`/`doPost` |

### How ISP Relates to SRP

- **SRP** is about classes: one class, one responsibility
- **ISP** is about interfaces: one interface, one role

They work together: if an interface serves multiple roles (ISP violation), any class implementing it will likely have multiple responsibilities (SRP violation).

### Signs of ISP Violations

1. Interfaces with 5+ methods where most implementors leave some as no-ops
2. `UnsupportedOperationException` in implementations
3. "Adapter" classes that exist only to provide empty default implementations
4. Implementors that don't use most of the interface's methods

**Run:** `java patterns.solid.InterfaceSegregationPrinciple`

---

## D — Dependency Inversion Principle (DIP)

> A. High-level modules should not depend on low-level modules. **Both should depend on abstractions.**  
> B. Abstractions should not depend on details. **Details should depend on abstractions.**  
> *— Robert C. Martin*

### The Concept

"Inversion" refers to inverting the traditional dependency direction. In naive code, high-level business logic (`OrderService`) directly depends on low-level infrastructure (`StripePayment`, `MySQLDatabase`). DIP says: introduce an **abstraction** (interface) between them. The high-level module depends on the interface, and the low-level module implements it.

### Real-World Analogy

A **wall electrical outlet** is an abstraction. Your laptop, phone charger, and toaster all depend on the outlet standard — not on the power plant. The power company can switch from coal to solar without changing your appliances. Both sides depend on the outlet interface.

### What Happens When You Violate DIP

```
                    TIGHT COUPLING (Bad)
┌──────────────┐         ┌────────────────┐
│ OrderService │────────▶│ StripePayment  │  ← concrete dependency
│ (high-level) │         │ (low-level)    │
└──────────────┘         └────────────────┘

Problem: To switch to PayPal, you must EDIT OrderService.
Problem: To unit test OrderService, you must connect to Stripe.
Problem: OrderService can't exist without Stripe on the classpath.
```

**Consequences:**
- **Can't swap implementations** — switching payment providers requires editing business logic
- **Can't unit test** — testing `OrderService` requires a real Stripe connection
- **Deployment coupling** — changing the payment library forces redeployment of `OrderService`
- **Reuse blocked** — `OrderService` can't be used in a project without Stripe

### The Fix — Depend on Abstractions

```
                    DEPENDENCY INVERSION (Good)
┌──────────────┐         ┌─────────────────┐
│ OrderService │────────▶│ PaymentGateway  │  ← abstraction (interface)
│ (high-level) │         │ processPayment()│
└──────────────┘         └────────┬────────┘
                          ┌───────┴────────┐
                    ┌─────┴─────┐    ┌─────┴─────┐    ┌──────────┐
                    │  Stripe   │    │  PayPal   │    │  Crypto  │
                    │ Payment   │    │ Payment   │    │ Payment  │
                    └───────────┘    └───────────┘    └──────────┘

OrderService never changes when you add a new payment provider.
Unit tests inject a MockPaymentGateway — no real API calls.
```

```java
interface PaymentGateway { void processPayment(double amount); }
class StripePayment implements PaymentGateway { ... }
class PayPalPayment implements PaymentGateway { ... }

class OrderService {
    private final PaymentGateway gateway;
    OrderService(PaymentGateway gw) { this.gateway = gw; }  // injected
    void placeOrder(String item, double price) { gateway.processPayment(price); }
}
```

### DIP Enables These Practices

| Practice | How DIP Enables It |
|----------|-------------------|
| **Unit Testing / Mocking** | Inject `MockPaymentGateway` instead of real Stripe |
| **Dependency Injection (Spring, Guice)** | Container wires implementations to interfaces at runtime |
| **Plugin Architecture** | Load new implementations without recompiling the core |
| **Hexagonal Architecture** | Core business logic has zero knowledge of infrastructure |
| **Microservices** | Services communicate via contracts (APIs), not shared code |

### DIP vs. Dependency Injection (DI)

These are related but **not the same**:

| | DIP (Principle) | DI (Technique) |
|-|----------------|-----------------|
| **What** | Design rule: depend on abstractions | Mechanism: pass dependencies from outside |
| **Why** | Decoupling, testability, flexibility | Implement DIP in practice |
| **How** | Define interfaces between layers | Constructor injection, setter injection, framework autowiring |
| **Without the other?** | DIP without DI: manual factory methods | DI without DIP: injecting concrete classes (still coupled) |

### Layers and the Dependency Rule

```
Traditional (wrong direction):           Inverted (correct direction):

┌─────────────────┐                    ┌─────────────────┐
│   UI Layer       │                    │   UI Layer       │
└────────┬────────┘                    └────────┬────────┘
         │ depends on                           │ depends on
         ▼                                      ▼
┌─────────────────┐                    ┌─────────────────┐
│ Business Logic   │                    │  «interface»    │
└────────┬────────┘                    │  ServicePort    │
         │ depends on                  └────────┬────────┘
         ▼                                      ▲ implements
┌─────────────────┐                    ┌────────┴────────┐
│   Database       │                    │ Business Logic   │
└─────────────────┘                    └────────┬────────┘
                                                │ depends on
                                                ▼
                                       ┌─────────────────┐
                                       │  «interface»    │
                                       │ RepositoryPort  │
                                       └────────┬────────┘
                                                ▲ implements
                                       ┌────────┴────────┐
                                       │   Database       │
                                       └─────────────────┘
```

**Run:** `java patterns.solid.DependencyInversionPrinciple`

---

## How They Work Together

| Principle | Helps With | Relates To |
|-----------|-----------|------------|
| **SRP** | Smaller classes → easier to understand and test | ISP (interface version of SRP) |
| **OCP** | New features don't break existing code | LSP (safe substitution enables OCP) |
| **LSP** | Polymorphism works safely — no surprises | OCP (subtypes extend without breaking) |
| **ISP** | Lean interfaces → no wasted implementation effort | SRP (applied to interfaces) |
| **DIP** | Loose coupling → easy to swap, mock, and extend | OCP (abstractions enable extension) |

### The SOLID Chain Reaction

When you apply all five principles together, a positive chain reaction happens:

```
SRP → small, focused classes
  └─▶ OCP → each class has a clear extension point (interface)
       └─▶ LSP → subtypes honor the interface contract
            └─▶ ISP → interfaces are small enough to implement fully
                 └─▶ DIP → everything depends on these clean abstractions
                      └─▶ Result: code that is easy to change, test, and extend
```

---

## SOLID vs. Over-Engineering

A common objection: *"Applying SOLID everywhere leads to too many classes and interfaces."* This is valid — SOLID is a set of **guidelines**, not laws.

| Situation | Apply SOLID? | Why |
|-----------|-------------|-----|
| Small script / prototype | Lightly or skip | Will be thrown away; speed matters more |
| Core domain / business logic | Strongly | This code changes the most and has the highest cost of bugs |
| Stable utility code (math, string helpers) | Lightly | Rarely changes — extraction overhead isn't worth it |
| Code with 3+ implementations or likely future variation | Strongly | OCP and DIP pay off quickly |
| Team of 1 on a personal project | Lightly | Less coordination overhead |
| Team of 10+ on a long-lived product | Strongly | Merge conflicts, testing, onboarding all benefit |

### Rules of Thumb

1. **Don't pre-optimize** — apply SOLID when you feel the pain of not having it (second time you modify a class for a different reason → extract)
2. **SRP and DIP give the most bang for the buck** — start there
3. **If you have only one implementation**, you don't need an interface yet (wait until you have two)
4. **If a class has < 50 lines and one reason to change**, it's fine — don't split further

---

## SOLID in Interviews

### Common Interview Questions

| Question | What They're Testing |
|----------|---------------------|
| "Design a notification system (email, SMS, push)" | OCP + DIP — interface for notifiers, inject implementations |
| "What's wrong with this code?" (God class shown) | SRP — identify multiple responsibilities |
| "Can Square extend Rectangle?" | LSP — explain the substitution violation |
| "How would you make this testable?" | DIP — introduce interfaces, inject mocks |
| "This interface has 12 methods..." | ISP — split into role-based interfaces |

### How to Talk About SOLID in Interviews

1. **Name the principle** — shows you know the vocabulary
2. **Explain the violation** — identify specifically what's wrong
3. **Propose the fix** — show the refactored design
4. **Mention the benefit** — testability, extensibility, or maintainability

---

## How to Run

```bash
cd src/main/java
javac patterns/solid/*.java

java patterns.solid.SingleResponsibilityPrinciple
java patterns.solid.OpenClosedPrinciple
java patterns.solid.LiskovSubstitutionPrinciple
java patterns.solid.InterfaceSegregationPrinciple
java patterns.solid.DependencyInversionPrinciple
```

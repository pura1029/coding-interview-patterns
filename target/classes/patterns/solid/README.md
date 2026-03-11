# SOLID Principles — Explained with Clear Java Examples

The **SOLID** principles are five design guidelines that make object-oriented code easier to **change**, **test**, and **extend**.

## Overview

| Principle | Letter | Rule | Key Question |
|-----------|--------|------|--------------|
| **Single Responsibility** | S | A class should have only one reason to change | Does this class do more than one job? |
| **Open/Closed** | O | Open for extension, closed for modification | Can I add behavior without editing existing code? |
| **Liskov Substitution** | L | Subtypes must be substitutable for base types | Will a subclass break callers of the parent? |
| **Interface Segregation** | I | Don't force unused interface methods | Is any class forced to implement methods it doesn't need? |
| **Dependency Inversion** | D | Depend on abstractions, not concretions | Does high-level code depend on low-level details? |

---

## S — Single Responsibility Principle (SRP)

> A class should have only **one reason to change**.

**Bad:** A monolithic `User` class handles authentication, profile display, and email sending.

**Good:** Split into `UserAuth`, `UserProfile`, and `EmailService` — each with a single job.

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

**Run:** `java patterns.solid.SingleResponsibilityPrinciple`

---

## O — Open/Closed Principle (OCP)

> Classes should be **open for extension** but **closed for modification**.

**Bad:** An `AreaCalculator` with if-else chains that must be edited for every new shape.

**Good:** A `Shape` interface with `area()`. New shapes just implement the interface.

```java
// GOOD: Add new shapes without modifying existing code
interface Shape { double area(); }
class Rectangle implements Shape { double area() { return w * h; } }
class Circle    implements Shape { double area() { return PI * r * r; } }
class Triangle  implements Shape { double area() { return 0.5 * b * h; } }
```

**Run:** `java patterns.solid.OpenClosedPrinciple`

---

## L — Liskov Substitution Principle (LSP)

> Subtypes must be **substitutable** for their base types without breaking behavior.

**Bad:** `Penguin extends Bird` but throws an exception on `fly()`.

**Good:** Separate `FlyingBird` and `SwimmingBird` interfaces. Penguin is a `SwimmingBird`.

```java
// GOOD: Penguin is never expected to fly
interface Bird       { String eat(); }
interface FlyingBird extends Bird { String fly(); }
interface SwimmingBird extends Bird { String swim(); }

class Eagle   implements FlyingBird   { ... }
class Penguin implements SwimmingBird { ... }
```

**Run:** `java patterns.solid.LiskovSubstitutionPrinciple`

---

## I — Interface Segregation Principle (ISP)

> Don't force classes to implement **interfaces they don't use**.

**Bad:** A fat `Machine` interface forces `SimplePrinter` to implement `scan()` and `fax()`.

**Good:** Split into `Printable`, `Scannable`, `Faxable`. Each class picks what it supports.

```java
// GOOD: Small, focused interfaces
interface Printable { void print(String doc); }
interface Scannable { void scan(String doc); }
interface Faxable   { void fax(String doc); }

class SimplePrinter implements Printable { ... }
class AllInOne      implements Printable, Scannable, Faxable { ... }
```

**Run:** `java patterns.solid.InterfaceSegregationPrinciple`

---

## D — Dependency Inversion Principle (DIP)

> High-level modules should not depend on low-level modules. **Both should depend on abstractions.**

**Bad:** `OrderService` directly creates `StripePayment` — tightly coupled.

**Good:** `OrderService` depends on a `PaymentGateway` interface. Inject any implementation.

```java
// GOOD: Depend on abstraction, inject implementation
interface PaymentGateway { void processPayment(double amount); }
class StripePayment implements PaymentGateway { ... }
class PayPalPayment implements PaymentGateway { ... }

class OrderService {
    private final PaymentGateway gateway;
    OrderService(PaymentGateway gw) { this.gateway = gw; }
    void placeOrder(String item, double price) { gateway.processPayment(price); }
}
```

**Run:** `java patterns.solid.DependencyInversionPrinciple`

---

## How They Work Together

| Principle | Helps With |
|-----------|------------|
| SRP | Smaller classes → easier to understand and test |
| OCP | New features don't break existing code |
| LSP | Polymorphism works safely — no surprises |
| ISP | Lean interfaces → no wasted implementation effort |
| DIP | Loose coupling → easy to swap, mock, and extend |

> The real power of SOLID is not in following each principle in isolation. It's in how they **work together** to make your code easier to change, test, and extend.

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

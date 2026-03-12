# Top 15 Design Patterns — Java Examples

Design patterns are proven solutions to common software design problems. They are categorized into three types: **Creational**, **Structural**, and **Behavioral**.

```
The Three Categories:

  CREATIONAL                  STRUCTURAL                  BEHAVIORAL
  (How objects are made)      (How objects compose)        (How objects communicate)

  ┌──────────────┐           ┌──────────────┐            ┌────────────────────┐
  │ Singleton    │           │ Adapter      │            │ Observer           │
  │ Factory      │           │ Decorator    │            │ Strategy           │
  │ Builder      │           │ Facade       │            │ Command            │
  └──────────────┘           │ Proxy        │            │ Iterator           │
                             │ Composite    │            │ State              │
                             └──────────────┘            │ Template Method    │
                                                         │ Chain of Resp.     │
                                                         └────────────────────┘
```

> **When to use which?** Need ONE instance → Singleton. Need to create different types → Factory. Need to wrap/extend → Decorator. Need to simplify → Facade. Need to react to changes → Observer. Need to swap behavior → Strategy.

## Overview

| # | Pattern | Category | Intent | Real-World Analogy |
|---|---------|----------|--------|--------------------|
| 1 | **Singleton** | Creational | Ensure only one instance exists | Database connection pool |
| 2 | **Factory Method** | Creational | Delegate object creation to subclasses | Notification service (Email/SMS/Push) |
| 3 | **Builder** | Creational | Build complex objects step by step | Constructing a User profile |
| 4 | **Adapter** | Structural | Convert one interface to another | Legacy payment API wrapper |
| 5 | **Decorator** | Structural | Add responsibilities dynamically | Coffee with optional toppings |
| 6 | **Facade** | Structural | Simplify a complex subsystem | One-click order placement |
| 7 | **Proxy** | Structural | Control access to an object | Lazy loading, access control, logging |
| 8 | **Composite** | Structural | Tree structures (part-whole) | File system (files + directories) |
| 9 | **Observer** | Behavioral | Notify dependents of state changes | Event bus, stock price alerts |
| 10 | **Strategy** | Behavioral | Swap algorithms at runtime | Sorting strategies, discount rules |
| 11 | **Command** | Behavioral | Encapsulate requests as objects | Undo/redo in text editors |
| 12 | **Iterator** | Behavioral | Traverse collections uniformly | BST iterator, Fibonacci sequence |
| 13 | **State** | Behavioral | Change behavior based on state | Order lifecycle (New→Paid→Shipped→Delivered) |
| 14 | **Template Method** | Behavioral | Define algorithm skeleton, vary steps | Data processors (CSV/JSON/XML) |
| 15 | **Chain of Responsibility** | Behavioral | Pass request along a handler chain | Support escalation, middleware pipeline |

---

## Pattern Visualizations

### Creational Patterns — How Objects Are Made

```
Singleton:    Only ONE instance ever
              ┌──────────────────┐
  get() ────► │  Instance (1)    │ ◄──── get()
              │  (shared state)  │
  get() ────► │                  │ ◄──── get()
              └──────────────────┘

Factory:      Create different types from ONE method
              ┌──────────────────┐
  "email" ──► │  Factory.create()│ ──► EmailNotification
  "sms"   ──► │                  │ ──► SMSNotification
  "push"  ──► │                  │ ──► PushNotification
              └──────────────────┘

Builder:      Build complex objects step by step
              User.builder("Alice", "email")
                  .phone("+1-555")      ← optional
                  .company("TechCorp") ← optional
                  .age(28)             ← optional
                  .build()             ← final object
```

### Structural Patterns — How Objects Compose

```
Adapter:      Convert incompatible interface
  Client ──► [Adapter] ──► LegacySystem
              (translates calls)

Decorator:    Stack behaviors like layers
  request ──► [Logging] ──► [Auth] ──► [Compress] ──► Core
              Each layer adds behavior without modifying the core.

Facade:       Simplify complex subsystem
  Client ──► [OrderFacade.placeOrder()]
              ├── inventory.checkStock()
              ├── payment.processPayment()
              └── shipping.shipOrder()

Proxy:        Control access
  Client ──► [Proxy] ──(check permissions)──► RealObject
              "You can READ but not DROP TABLE"

Composite:    Tree structure (files + folders)
  Directory "project"
  ├── File "README.md" (1KB)
  ├── Directory "src"
  │   ├── File "Main.java" (15KB)
  │   └── File "Utils.java" (8KB)
  └── Directory "test"
      └── File "MainTest.java" (10KB)
  project.getSize() = recursively sum all children
```

### Behavioral Patterns — How Objects Communicate

```
Observer:     Publish-subscribe
  EventBus ──► [EmailAlert]      "ORDER_PLACED"
           ──► [SlackNotifier]   "ORDER_PLACED"
           ──► [SMSAlert]        "ORDER_PLACED"
  One event → all subscribers notified.

Strategy:     Swap algorithms at runtime
  sorter.setStrategy(new BubbleSort())  → sort with bubble
  sorter.setStrategy(new QuickSort())   → sort with quick
  Same interface, different implementations.

State:        Behavior changes with state
  Order: NEW ──► PAID ──► SHIPPED ──► DELIVERED
         │       │        │           │
         cancel  refund   track       review
  Each state has different allowed actions.

Chain of Responsibility:
  Request ──► [Bot] ──► [Level1] ──► [Level2] ──► [Manager]
              "Can I     "Can I       "Can I       "I'll
               handle?"   handle?"     handle?"     handle it"
  First handler that can handle it processes the request.
```

---

## Creational Patterns

### 1. Singleton
> Ensures a class has only **one instance** with a global access point.

```java
static class LazySingleton {
    private static volatile LazySingleton instance;
    private LazySingleton() {}
    public static LazySingleton getInstance() {
        if (instance == null) {
            synchronized (LazySingleton.class) {
                if (instance == null) instance = new LazySingleton();
            }
        }
        return instance;
    }
}
```

**Run:** `java patterns.designpatterns.SingletonPattern`

### 2. Factory Method
> Creates objects without specifying the exact class — **delegates to subclasses**.

```java
interface Notification { void send(String message); }
class EmailNotification implements Notification { ... }
class SMSNotification implements Notification { ... }

Notification n = NotificationFactory.create("email");
n.send("Welcome!");
```

**Run:** `java patterns.designpatterns.FactoryMethodPattern`

### 3. Builder
> Constructs complex objects **step by step** with a fluent API.

```java
User user = new User.Builder("Alice", "alice@email.com")
    .phone("+1-555-0123")
    .company("TechCorp")
    .age(28)
    .build();
```

**Run:** `java patterns.designpatterns.BuilderPattern`

---

## Structural Patterns

### 4. Adapter
> Converts an incompatible interface into one the client expects — **a wrapper/bridge**.

```java
class PayPalAdapter implements PaymentProcessor {
    private LegacyPayPal legacyPayPal = new LegacyPayPal();
    void pay(double amount, String currency) {
        legacyPayPal.makePayment((int)(amount * 100));
    }
}
```

**Run:** `java patterns.designpatterns.AdapterPattern`

### 5. Decorator
> Attaches additional behavior **dynamically** without subclassing.

```java
Coffee latte = new WhipDecorator(
    new VanillaDecorator(
        new MilkDecorator(
            new SimpleCoffee())));
// "Simple Coffee + Milk + Vanilla + Whip" → $3.85
```

**Run:** `java patterns.designpatterns.DecoratorPattern`

### 6. Facade
> **Simplifies** a complex subsystem behind a single, easy-to-use interface.

```java
class OrderFacade {
    void placeOrder(String item, double price, String address) {
        inventory.checkStock(item);
        payment.processPayment(price);
        shipping.shipOrder(item, address);
    }
}
```

**Run:** `java patterns.designpatterns.FacadePattern`

### 7. Proxy
> Controls **access** to another object (lazy loading, security, logging).

```java
class SecureDatabaseProxy implements Database {
    void query(String sql) {
        if (sql.startsWith("DROP") && !"ADMIN".equals(role))
            throw new SecurityException("Access denied");
        realDatabase.query(sql);
    }
}
```

**Run:** `java patterns.designpatterns.ProxyPattern`

### 8. Composite
> Composes objects into **tree structures**; treats individuals and groups uniformly.

```java
Directory root = new Directory("project");
root.add(new File("README.md", 1));
root.add(new Directory("src").add(new File("Main.java", 15)));
root.display("");  // Recursively prints the tree
```

**Run:** `java patterns.designpatterns.CompositePattern`

---

## Behavioral Patterns

### 9. Observer
> When one object changes, all **subscribers are notified** automatically.

```java
EventBus bus = new EventBus();
bus.subscribe(new EmailAlert());
bus.subscribe(new SlackNotifier());
bus.publish("ORDER_PLACED", "Order #12345");
```

**Run:** `java patterns.designpatterns.ObserverPattern`

### 10. Strategy
> Defines a family of algorithms and makes them **interchangeable at runtime**.

```java
Sorter sorter = new Sorter(new BubbleSort());
sorter.sort(data);
sorter.setStrategy(new QuickSort());
sorter.sort(data);
```

**Run:** `java patterns.designpatterns.StrategyPattern`

### 11. Command
> Encapsulates requests as objects — enables **undo/redo, queuing, macros**.

```java
CommandManager mgr = new CommandManager();
mgr.execute(new InsertCommand(editor, "Hello"));
mgr.execute(new InsertCommand(editor, " World"));
mgr.undo();  // "Hello"
mgr.redo();  // "Hello World"
```

**Run:** `java patterns.designpatterns.CommandPattern`

### 12. Iterator
> Provides **sequential access** to elements without exposing internals.

```java
for (int n : new NumberRange(1, 20, 3)) { ... }
BSTIterator it = new BSTIterator(root);
while (it.hasNext()) System.out.print(it.next());
```

**Run:** `java patterns.designpatterns.IteratorPattern`

### 13. State
> Object **changes behavior** when its internal state changes.

```java
Order order = new Order("001");  // NEW
order.next();  // → PAID
order.next();  // → SHIPPED
order.next();  // → DELIVERED
```

**Run:** `java patterns.designpatterns.StatePattern`

### 14. Template Method
> Defines algorithm **skeleton in base class**; subclasses override specific steps.

```java
abstract class DataProcessor {
    final void process(String data) {
        parse(data) → validate() → transform() → output();
    }
    abstract String parse(String raw);
    abstract String validate(String parsed);
    abstract String transform(String validated);
}
```

**Run:** `java patterns.designpatterns.TemplateMethodPattern`

### 15. Chain of Responsibility
> Passes requests along a **chain of handlers** until one handles it.

```java
bot.setNext(level1).setNext(level2).setNext(manager);
bot.handle("System down", severity: 8);
// Bot → L1 → L2 → Manager handles it
```

**Run:** `java patterns.designpatterns.ChainOfResponsibilityPattern`

---

## How to Run All

```bash
cd src/main/java
javac patterns/designpatterns/*.java

java patterns.designpatterns.SingletonPattern
java patterns.designpatterns.FactoryMethodPattern
java patterns.designpatterns.BuilderPattern
java patterns.designpatterns.AdapterPattern
java patterns.designpatterns.DecoratorPattern
java patterns.designpatterns.FacadePattern
java patterns.designpatterns.ProxyPattern
java patterns.designpatterns.CompositePattern
java patterns.designpatterns.ObserverPattern
java patterns.designpatterns.StrategyPattern
java patterns.designpatterns.CommandPattern
java patterns.designpatterns.IteratorPattern
java patterns.designpatterns.StatePattern
java patterns.designpatterns.TemplateMethodPattern
java patterns.designpatterns.ChainOfResponsibilityPattern
```

## Quick Decision Guide

```
Need exactly ONE instance?              → Singleton
Need to create objects without knowing
  the exact class?                      → Factory Method
Need to build complex objects with
  many optional parameters?             → Builder
Need to use a class with an
  incompatible interface?               → Adapter
Need to add behavior dynamically
  without subclassing?                  → Decorator
Need to simplify a complex subsystem?   → Facade
Need to control access, add logging,
  or lazy-load?                         → Proxy
Need uniform handling of tree
  structures (part-whole)?              → Composite
Need multiple objects to react to
  state changes?                        → Observer
Need to swap algorithms at runtime?     → Strategy
Need undo/redo, queuing, or
  logging of operations?                → Command
Need to traverse a collection
  without exposing internals?           → Iterator
Need object behavior to change
  based on internal state?              → State
Need a fixed algorithm with
  customizable steps?                   → Template Method
Need a pipeline of handlers where
  each can process or pass along?       → Chain of Responsibility
```

## Real-World Usage in Popular Frameworks

| Pattern | Used In | How |
|---------|---------|-----|
| **Singleton** | Spring Beans, `Runtime.getRuntime()` | Default bean scope, single JVM instance |
| **Factory** | Spring `BeanFactory`, JDBC `DriverManager` | Create objects from configuration |
| **Builder** | Lombok `@Builder`, StringBuilder | Fluent API for complex construction |
| **Adapter** | Java IO (InputStreamReader) | Adapts byte stream to character stream |
| **Decorator** | Java IO (BufferedReader wraps Reader) | Stack buffering, compression, encryption |
| **Facade** | Spring Boot auto-configuration | Simplifies complex framework setup |
| **Proxy** | Spring AOP, JDK Dynamic Proxy | Transactions, security, caching |
| **Observer** | Java Swing listeners, Spring Events | UI events, application events |
| **Strategy** | `Comparator`, `Runnable` | Swap comparison/execution logic |
| **Iterator** | Java `Iterable`/`Iterator` | For-each loops on any collection |
| **State** | TCP connection states, order workflows | State-dependent behavior |
| **Template** | `HttpServlet.doGet()`, JUnit lifecycle | Override specific steps of a process |

## Resources

1. [Singleton](https://lnkd.in/g3VrJz-k)
2. [Factory Method](https://lnkd.in/gA6Uew8n)
3. [Builder](https://lnkd.in/gdTr2BBF)
4. [Adapter](https://lnkd.in/g_yB_CZn)
5. [Decorator](https://lnkd.in/g9zWv66w)
6. [Facade](https://lnkd.in/gHzPeaKG)
7. [Proxy](https://lnkd.in/g2MF2hvS)
8. [Composite](https://lnkd.in/gHwStDc3)
9. [Observer](https://lnkd.in/g4S_eGjy)
10. [Strategy](https://lnkd.in/gSjXJ3Cq)
11. [Command](https://lnkd.in/gffxnxih)
12. [Iterator](https://lnkd.in/g7F_PmD9)
13. [State](https://lnkd.in/gmfFnubm)
14. [Template Method](https://lnkd.in/gshGDpKE)
15. [Chain of Responsibility](https://lnkd.in/gayT82-s)

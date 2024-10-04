# Report: Fuzzy Logic System with Scoped Variables and Logic Gates

## 1. Introduction

### Background

Domain-Specific Languages (DSLs) are specialized programming languages tailored to a specific application domain. They provide a higher-level abstraction, allowing users to express concepts and operations in terms that are familiar within that domain. This leads to increased productivity, readability, and maintainability when compared to using general-purpose programming languages.

In the realm of fuzzy logic systems, DSLs can simplify the process of defining and manipulating fuzzy variables, sets, and logic gates. Fuzzy logic extends classical logic by allowing truth values to range between 0 and 1, representing degrees of truth. This is particularly useful in systems where binary true/false logic is insufficient, such as control systems, decision-making processes, and artificial intelligence applications.

### Purpose of the DSL

The **Fuzzy Logic System with Scoped Variables and Logic Gates** is a DSL implemented in Scala. Its primary purpose is to enable users to define and evaluate expressions involving fuzzy sets, fuzzy logic gates, and mathematical operations within a statically scoped environment. By supporting variable assignment, scoping, and testing logic gates with inputs, the DSL addresses specific challenges in building complex logic-based systems that require modularity and reusability.

### Language Overview

The DSL is designed with several key goals:

- **Expressiveness**: Provide intuitive constructs for defining fuzzy values, sets, variables, and logic gates.
- **Modularity**: Support statically scoped environments to allow controlled visibility and reuse of variables and logic gates.
- **Type Safety**: Ensure operations are type-checked to prevent runtime errors.
- **Performance**: Utilize Scala's efficient functional programming features for performant evaluation.
- **Domain-Specific Features**: Include operations and constructs specific to fuzzy logic, such as fuzzy addition, multiplication, logical `AND`, `OR`, and `XOR` operations.

---

## 2. Language Design

### Syntax

The DSL uses Scala's case classes and pattern matching to represent fuzzy expressions and operations. It adopts a declarative style, allowing users to define complex expressions in a readable and maintainable manner. The syntax supports variable assignments, scoping, logic gate definitions, and testing gates with specific inputs.

#### Examples

1. **Defining and Assigning a Fuzzy Value to a Variable**

   ```scala
   val assignVar = Assign(FuzzyVar("X"), FuzzyVal(0.9))
   ```

2. **Defining a Fuzzy Set**

   ```scala
   val fuzzySet = FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.3)), ("C", FuzzyVal(0.4))))
   ```

3. **Defining a Logic Gate with an Expression**

   ```scala
   val logicGateExpr = Assign(FuzzyGate("logicGate1"), FuzzyAdd(FuzzyVal(0.5), FuzzyVal(0.3)))
   ```

4. **Creating a Scoped Variable Assignment**

   ```scala
   val scopeExpr = Scope("gateScope", Assign(FuzzyVar("A"), FuzzyVal(0.2)))
   ```

### Types

The DSL defines several types to represent different fuzzy logic constructs:

- **FuzzyVal**: Represents a single fuzzy value between 0.0 and 1.0.
- **FuzzySet**: Represents a set of labeled fuzzy values.
- **FuzzyVar**: A variable that can hold a `FuzzyVal` or `FuzzySet`.
- **FuzzyGate**: Represents a logic gate assigned to an expression.
- **LogicGate**: Used to reference a defined logic gate in expressions.
- **Assign**: Represents the assignment of an expression to a variable or gate.
- **Scope**: Defines a new scope for variable assignments.
- **TestGate**: Used to test a logic gate with specific inputs.

### Evaluation Model

Expressions in the DSL are evaluated recursively, respecting the statically scoped environment. The evaluation model involves:

- **Recursive Evaluation**: Expressions are evaluated from the innermost parts outward.
- **Scope Management**: Variables and gates are resolved based on their scope.
- **Type Checking**: Ensures operations are performed on compatible types.
- **Operation Semantics**: Mathematical and logical operations follow fuzzy logic rules.

---

## 3. Semantics

### Static Semantics

#### Type Checking

- **Type Enforcement**: Operations expect operands of specific types (e.g., `FuzzyAdd` expects `FuzzyVal` or `FuzzySet`).
- **Error Handling**: If operands are of incompatible types, the evaluator throws an exception.

#### Scoping and Variable Declarations

- **Statically Scoped Environment**: Variables are resolved based on the lexical scope where they are defined.
- **Variable Assignment**: Variables are assigned using the `Assign` construct within a scope.
- **Scope Rules**: Variables defined in a scope are not accessible outside that scope unless passed explicitly.

#### Type Resolution

- **No Type Inference**: The DSL relies on explicit type definitions through constructors.
- **Type Matching**: During evaluation, the types of operands are matched against expected types for operations.

### Dynamic Semantics

#### Expression Evaluation

- **Fuzzy Values and Sets**: Evaluated directly to their defined values.
- **Operations**: Applied recursively, combining fuzzy values according to fuzzy logic rules.
- **Variables and Gates**: Resolved in the current environment and evaluated accordingly.

#### Order of Execution

- **Deterministic Evaluation**: Expressions are evaluated in a predictable order, respecting the structure of the expression tree.
- **Scoped Execution**: New scopes create isolated environments for variable assignments.

#### Control Structures

- **No Traditional Control Structures**: The DSL does not include loops or conditionals. Control flow is managed through expression composition and scoping.

#### Examples

1. **Simple Fuzzy Addition**

   ```scala
   val expr = FuzzyAdd(FuzzyVal(0.5), FuzzyVal(0.3))
   val result = evaluate(expr, env, env) // FuzzyVal(0.8)
   ```

2. **Variable Usage in Expressions**

   ```scala
   val assignX = Assign(FuzzyVar("X"), FuzzyVal(0.6))
   evaluate(assignX, env, env)
   val expr = FuzzyMult(FuzzyVar("X"), FuzzyVal(0.5))
   val result = evaluate(expr, env, env) // FuzzyVal(0.3)
   ```

3. **Scoped Variable Assignment**

   ```scala
   val scopeExpr = Scope("Scope1", Assign(FuzzyVar("A"), FuzzyVal(0.5)))
   evaluate(scopeExpr, env, env)
   // Variable "A" is not accessible outside "Scope1"
   ```

4. **Logic Gate Evaluation with Inputs**

   ```scala
   val gateExpr = Assign(FuzzyGate("gate1"), FuzzyAnd(FuzzyVar("A"), FuzzyVar("B")))
   evaluate(gateExpr, env, env)
   val testGate = TestGate("gate1", FuzzyVal(0.7))
   val result = evaluate(testGate, env, env) 
   ```

---

## 4. Implementation

### Architecture

The system's architecture comprises several key components, structured to facilitate modularity and maintainability.

#### Core Components

1. **Expression Definitions (`FuzzyExpressions.scala`)**

    - Defines all expression types using Scala case classes.
    - Includes constructs like `FuzzyVal`, `FuzzySet`, `FuzzyAdd`, `FuzzyMult`, `Assign`, `Scope`, `LogicGate`, etc.

2. **Evaluator (`FuzzyEvaluator.scala`)**

    - Implements the `evaluate` function that recursively evaluates expressions.
    - Handles variable resolution, scope management, and operation execution.

3. **Environment and Scoping (`EnvironmentScopes.scala`)**

    - Defines the `Environment` class representing statically scoped environments.
    - Manages variable and gate bindings, with support for nested scopes.

4. **Mathematical Operations (`FuzzyMath.scala`)**

    - Implements the fuzzy logic operations (addition, multiplication, `AND`, `OR`, `XOR`, etc.).
    - Ensures operations adhere to fuzzy logic rules (e.g., capping addition at 1.0).

5. **Testing (`src/test/scala`)**

    - Contains test cases validating the functionality of expressions, scoping, variable assignments, and logic gates.

### Tools and Libraries

- **Scala**: Chosen for its functional programming features and strong type system, facilitating the implementation of the DSL.
- **sbt (Simple Build Tool)**: Used for building and managing the Scala project.
- **ScalaTest**: A testing framework for writing and running automated tests.

---

## 5. Type System

### Type Definitions

- **Basic Types**:

    - **FuzzyVal**: Represents a fuzzy value between 0.0 and 1.0.
    - **FuzzySet**: Represents a set of labeled fuzzy values.

- **Composite Types**:

    - **FuzzyVar**: A variable that can hold a `FuzzyVal` or `FuzzySet`.
    - **FuzzyGate**: A logic gate assigned an expression.

### Type Interactions

- **Operations on FuzzyVal and FuzzySet**:
    - Operation is defined for combinations of `FuzzyVal` and `FuzzySet` (AlphaCut).
     
- **No Implicit Type Conversion**:

    - The DSL does not perform implicit type coercion.
    - Types must match the expected operands for each operation.

### Type Safety

- **Compile-Time Checks**:

    - Scala's type system catches some type mismatches during compilation.

- **Runtime Checks**:

    - The evaluator performs type checking during evaluation.
    - Throws exceptions for invalid operations or undefined variables.

### Error Handling

- **Undefined Variables**:

    - Accessing a variable not defined in the current or parent scopes results in an exception.

- **Invalid Operations**:

    - Attempting operations with incompatible types raises an error.

- **Missing Inputs for Logic Gates**:

    - Testing a gate without providing necessary inputs results in an exception.

---

## Examples and Use Cases

### Variable Assignment and Fetching

Assign a variable and retrieve its value:

```scala
val assignVar = Assign(FuzzyVar("X"), FuzzyVal(0.9))
evaluate(assignVar, env, env)

val fetchVar = FuzzyVar("X")
val result = evaluate(fetchVar, env, env) // Result: FuzzyVal(0.9)
```

### Logic Gate Assignment and Testing

Define a logic gate and test it with inputs:

```scala
val logicGateExpr = Assign(FuzzyGate("gate1"), FuzzyAdd(FuzzyVar("A"), FuzzyVar("B")))
evaluate(logicGateExpr, env, env)

val testGateExpr = TestGate("gate1", FuzzyVal(0.5)) 
val result = evaluate(testGateExpr, env, env) // Result: FuzzyVal(0.9)
```

### Scoped Variables within Logic Gates

Variables within logic gates are scoped, preventing conflicts:

```scala
val logicGate1 = Assign(FuzzyGate("gate1"), FuzzyMult(FuzzyVar("A"), FuzzyVar("B")))
val scopeGate1 = Scope("gate1", Assign(FuzzyVar("A"), FuzzyVal(0.2)))
val scopeGate2 = Scope("gate1", Assign(FuzzyVar("B"), FuzzyVal(0.5)))
evaluate(scopeGate1, env, env)
evaluate(scopeGate2, env, env)
evaluate(logicGate1, env, env)
val result = evaluate(LogicGate("gate1"), env, env) // Result: FuzzyVal(0.1)
```

### Composite Logic Gates

Compose logic gates to build complex expressions:

```scala
val logicGate1 = Assign(FuzzyGate("gate1"), FuzzyAnd(FuzzyVar("X"), FuzzyVar("Y")))
val logicGate2 = Assign(FuzzyGate("gate2"), FuzzyOr(LogicGate("gate1"), FuzzyVar("Z")))
val scopeX = Scope("gate1", Assign(FuzzyVar("X"), FuzzyVal(0.6)))
val scopeY = Scope("gate1", Assign(FuzzyVar("Y"), FuzzyVal(0.7)))
val scopeZ = Scope("gate2", Assign(FuzzyVar("Z"), FuzzyVal(0.8)))
evaluate(scopeX, env, env)
evaluate(scopeY, env, env)
evaluate(scopeZ, env, env)
evaluate(logicGate1, env, env)
evaluate(logicGate2, env, env)

val testGate = TestGate("gate2", FuzzyVal(0.5))
val result = evaluate(testGate, env, env) // Result: FuzzyVal(0.5)
```

---

## References

- [Fuzzy Logic Principles](https://en.wikipedia.org/wiki/Fuzzy_logic)
- [Scala Programming Language](https://www.scala-lang.org/)
---

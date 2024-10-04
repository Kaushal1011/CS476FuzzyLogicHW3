### README

# Fuzzy Logic System with Scoped Variables and Logic Gates

This project implements a **Fuzzy Logic System** in **Scala** using the concepts of fuzzy mathematics, variable scoping, and logic gates. It is designed from the perspective of programming language design, focusing on statically scoped environments. The system allows users to define and evaluate expressions involving fuzzy sets, fuzzy logic gates, and mathematical operations. It supports variable assignment, scoping, and testing logic gates with inputs, making it versatile for building logic-based systems.

## [Report](REPORT.md)

The detailed report provides an in-depth analysis of the DSL, including its purpose, design, syntax, types, and features. It covers the motivation behind the DSL, its language overview, design considerations.

## Project Overview

This system introduces a statically scoped environment for fuzzy expressions. Variables defined within certain scopes retain their bindings and can be referenced according to scope rules. This is analogous to scoping mechanisms in modern programming languages, providing users with control over variable assignments and function-like constructs (logic gates).

### Key Features:
- **Fuzzy Values & Fuzzy Sets**: Supports operations on single values (e.g., `FuzzyVal(0.5)`) and sets of values (e.g., `FuzzySet(List("A" -> FuzzyVal(0.5), "B" -> FuzzyVal(0.3)))`).
- **Mathematical Operations**: Performs operations like addition, multiplication, and logical operations (`AND`, `OR`, `XOR`) on fuzzy values and sets.
- **Statically Scoped Variables**: Assign variables within a specific scope, allowing controlled visibility and use of variables across different scopes.
- **Logic Gates as Function Constructs**: Logic gates behave like functions, evaluating fuzzy expressions based on inputs and scoped variables.
- **Testable Gates**: Test gates by providing specific inputs and evaluating their results based on scoped variables and fuzzy logic.

## Prerequisites

- **Scala**: Ensure you have Scala installed.
- **sbt**: The Simple Build Tool for Scala projects.

## Getting Started

### Clone the Repository

```sh
https://github.com/Kaushal1011/CS476FuzzyLogicHW1
cd CS476FuzzyLogicHW1
```

### Build the Project

```sh
sbt compile
```

### Run the Project

```sh
sbt run
```

### Run the Tests

```sh
sbt test
```

## Project Structure

- `src/main/scala`: Contains the main source code.
- `src/test/scala`: Contains test cases for different components of the system.
- `FuzzyEvaluator.scala`: Core evaluation logic for fuzzy expressions and gates.
- `FuzzyExpressions.scala`: Defines fuzzy expression types (values, sets, operations).
- `EnvironmentScopes.scala`: Implements the statically scoped environment.
- `FuzzyMath.scala`: Implements the mathematical operations on fuzzy values and sets.

## Language Tokens (Expressions)

The system defines a set of language tokens (or expressions) for operations and scoping constructs.

### Fuzzy Values and Sets

- **FuzzyVal**: Represents a fuzzy value, e.g., `FuzzyVal(0.7)`.
- **FuzzySet**: Represents a set of fuzzy values associated with labels, e.g., `FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3))))`.

### Basic Arithmetic & Logic Operations

- **FuzzyAdd**: Adds two fuzzy values or sets, capping the result at 1.0.
- **FuzzyMult**: Multiplies two fuzzy values or sets.
- **FuzzyAnd**: Computes the minimum of two fuzzy values.
- **FuzzyOr**: Computes the maximum of two fuzzy values.
- **FuzzyXor**: Computes the difference between the maximum and minimum of two fuzzy values.

### Advanced Operations

- **FuzzyAlphaCut**: Extracts elements from a fuzzy set with membership values greater than or equal to a specific threshold (alpha).
- **FuzzyUnion**: Computes the union (maximum membership) of two fuzzy sets.
- **FuzzyIntersection**: Computes the intersection (minimum membership) of two fuzzy sets.

## Variable Assignment and Scoping

### Assign

- **Assign**: Assigns a fuzzy value to a variable. In statically scoped environments, variables maintain their values within their scope.

```scala
val assignVar = Assign(FuzzyVar("X"), FuzzyVal(0.9))
val result = eval(assignVar, env, env)
```

### Scope

- **Scope**: Defines a new scope for a variable assignment. Variables assigned within a scope remain local to that scope and do not affect parent scopes.

```scala
val scopeExpr = Scope("gateScope", Assign(FuzzyVar("A"), FuzzyVal(0.2)))
```

### Example:

```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
val assignVar = Assign(FuzzyVar("X"), FuzzyVal(0.9))
val resultVarAssign = eval(assignVar, commonEnv, commonEnv)
val scopeExpr = Scope("Scope1", Assign(FuzzyVar("A"), FuzzyVal(0.5)))
eval(scopeExpr, commonEnv, commonEnv)
```

## Logic Gates

### LogicGate

A **LogicGate** is a function construct that can be assigned to expressions and evaluated using the scoped environment. Logic gates allow the composition of fuzzy logic expressions.

### FuzzyGate Assignment

Logic gates are assigned fuzzy expressions and can reference variables within their scope. A gate acts like a function that can evaluate expressions based on the current variable assignments.

```scala
val logicGateExpr = Assign(FuzzyGate("logicGate1"), FuzzyAdd(FuzzyVal(0.5), FuzzyVal(0.3)))
val resultGate = eval(logicGateExpr, commonEnv, commonEnv)
```

### Composite Gates

Logic gates can be composed together. For example, a composite gate might involve operations between two logic gates or between a logic gate and a fuzzy variable.

```scala
val compositeGateExpr = Assign(FuzzyGate("compositeGate"), FuzzyXor(LogicGate("logicGate1"), FuzzyVar("C")))
val resultCompositeGate = eval(compositeGateExpr, commonEnv, commonEnv)
```

### Testing Logic Gates

You can test logic gates with specific inputs using the **TestGate** expression.

```scala
val testGateExpr = TestGate("logicGate1", FuzzyVal(0.5))
val resultTestGate = eval(testGateExpr, commonEnv, commonEnv)
```

## Examples

### Variable Assignment and Fetching

```scala
val assignVar = Assign(FuzzyVar("X"), FuzzyVal(0.9))
eval(assignVar, commonEnv, commonEnv)

val fetchVar = FuzzyVar("X")
val resultVar = eval(fetchVar, commonEnv, commonEnv)
println(s"Variable X: $resultVar")
```

### Logic Gate Assignment

```scala
val logicGateExpr = Assign(FuzzyGate("gate1"), FuzzyAdd(FuzzyVal(0.2), FuzzyVal(0.3)))
val resultGate = eval(logicGateExpr, commonEnv, commonEnv)
println(s"LogicGate gate1: $resultGate")
```

### Composite Logic Gate

```scala
val compositeGateExpr = Assign(FuzzyGate("compositeGate"), FuzzyXor(LogicGate("gate1"), FuzzyVal(0.5)))
val resultCompositeGate = eval(compositeGateExpr, commonEnv, commonEnv)
println(s"Composite Gate: $resultCompositeGate")
```

## Logic Gates as Functions

Logic gates in this system behave as reusable functions. You can assign an expression to a gate, and this gate can be referenced or composed with other gates or variables in different scopes.

### Scoping within Logic Gates

Variables within logic gates are scoped. For example, variables `A` and `B` assigned in one gate will not conflict with variables in another gate, unless intentionally shared through the parent scope.

```scala
val logicGate1 = Assign(FuzzyGate("gate1"), FuzzyMult(FuzzyVar("A"), FuzzyVar("B")))
val scopeGate1 = Scope("gate1", Assign(FuzzyVar("A"), FuzzyVal(0.2)))
val scopeGate2 = Scope("gate1", Assign(FuzzyVar("B"), FuzzyVal(0.5)))
val result = eval(LogicGate("gate1"), commonEnv, commonEnv)
println(s"Gate1 result: $result")
```

Here are the key features demonstrated in the system with brief explanations and corresponding code snippets from the test files:

1. **Statically Scoped Environment**
    - Variables are looked up in the current scope or parent scope.
    - **Example**:
   ```scala
   val assignVar = Assign(FuzzyGate("Gate1"), FuzzyAnd(FuzzyMult(FuzzyAdd(FuzzyVal(0.1), FuzzyVar("X")), Assign(FuzzyVar("X"), FuzzyVal(0.3))), FuzzyVar("X")))
   val parentX = Assign(FuzzyVar("X"), FuzzyVal(0.1))
   ```
    - This ensures that variables like `X` in different scopes are resolved properly based on static scoping rules.

2. **Nesting of Assignments**
    - Allows variable assignments inside complex expressions.
    - **Example**:
   ```scala
   val assignVar = Assign(FuzzyGate("Gate1"), FuzzyMult(FuzzyAdd(FuzzyVal(0.5), Assign(FuzzyVar("X"), FuzzyVal(0.3))),FuzzyVar("X")))
   ```
    - This demonstrates how assignments like `FuzzyVar("X")` can be nested within other operations, providing modular and dynamic assignments.

3. **Logic Gates as First-Class Constructs**
    - Logic gates are treated as reusable functional constructs.
    - **Example**:
   ```scala
   val logicGateExpr = Assign(FuzzyGate("logicGate1"), FuzzyAdd(FuzzyMult(FuzzyVar("A", FuzzyVal(0.1)), FuzzyVar("B", FuzzyVal(0.2))), FuzzyVal(0.1)))
   ```
    - Gates like `logicGate1` can be reused across multiple evaluations with different inputs, allowing modular design.

4. **Composite Logic Gates**
    - Logic gates can be combined into larger, more complex gates.
    - **Example**:
   ```scala
   val compositeGateExpr = Assign(FuzzyGate("compositeGate"), FuzzyXor(LogicGate("logicGate1"), FuzzyVar("C")))
   ```
    - This combines logic gate `logicGate1` and a variable `C` into a composite gate, demonstrating modularity.

5. **Error Handling for Missing Inputs**
    - Throws an exception if a required variable is not defined.
    - **Example**:
   ```scala
   assertThrows[Exception] {
     val testCompositeGate = TestGate("compositeGate", FuzzyVar("A", FuzzyVal(0.5)))
   }
   ```
    - This ensures that logic gates are not evaluated with missing inputs, promoting robustness in the system.


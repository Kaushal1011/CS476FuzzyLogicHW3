# Fuzzy Logic System with Classes, Macros, Scoped Variables, and Partial Evaluation

In this version of Fuzzy Logic Programming Language created using Scala, we have added support

- Partial Evaluation for Fuzzy Logic Expressions (Math, Logic, Union and Intersection) etc.
- Partial Evaluation for Fuzzy Logic Gates and Test Gate Expressions.
- Partial Evaluation for Let Macros and Nested Macro Definitions.
- IfTrue Conditional Expressions with Partial Evaluation.
- Partial Evaluation support using associativity and commutativity of operations.
- Partial Evaluation for Method Invocation on Classes.
- Test Coverage for Partial Evaluation of Fuzzy Logic Expressions and Gates.

Previously, The Fuzzy Logic System was implemented with support for:

- Fuzzy Logic Expressions (Math, Logic, Union and Intersection) etc.
- Fuzzy Logic Gates and Test Gate Expressions. (Analogous to procedural programming)
- Let Macros and Nested Macro Definitions.
- Classes, Inheritance and Method Invocation
- NonFuzzy Operations supporting all possibilities from Scala Types.
- Test Coverage for Fuzzy Logic Expressions and Gates.

---

## Getting Started

### Prerequisites

- **Scala**: Ensure Scala is installed.
- **sbt**: The Simple Build Tool for managing Scala projects.

### Setup Instructions

1. **Clone the Repository**
   ```sh
   git clone https://github.com/Kaushal1011/CS476FuzzyLogicHW3
   cd CS476FuzzyLogicHW3
   ```

2. **Build the Project**
   ```sh
   sbt compile
   ```

3. **Run the Project**
   ```sh
   sbt run
   ```

4. **Run Tests**
   ```sh
   sbt test
   ```

---
## Expanded Test Coverage

### Categories of Tests

1. **Classes and Methods**:
  - Test inheritance, method invocation, and partial evaluation.
  - Location: [`src/test/scala/PartialEvalInvokeMethod.scala`](src/test/scala/PartialEvalInvokeMethod.scala)

2. **Macros**:
  - Validate nested macros and reuse.
  - Location: [`src/test/scala/PartialEvalLetMacro.scala`](src/test/scala/PartialEvalLetMacro.scala)

3. **Logic Gates**:
  - Define, evaluate, and test gates with partial inputs.
  - Location: [`src/test/scala/PartialEvalFuzzyGateTests.scala`](src/test/scala/PartialEvalFuzzyGateTests.scala)

4. **Arithmetic and Logical Operations**:
  - Test associativity, commutativity, and logical gates.
  - Location: [`src/test/scala/PartialEvalMath.scala`](src/test/scala/PartialEvalMath.scala)

5. **Conditional Expressions**:
  - Evaluate conditions with partial or fully defined branches.
  - Location: [`src/test/scala/PartialEvalIfTrue.scala`](src/test/scala/PartialEvalIfTrue.scala)

---


## Explanation of Features with Examples and Implementation Details

- Implementaion of Partial Evaluation can be found in [`src/main/scala/FuzzyEvaluator.scala`](src/main/scala/FuzzyEvaluator.scala).

### Operation Associativity and Commutativity

```scala
expr match {
  case FuzzyAdd(x1, x2) =>
    val left = eval(x1, env, root)
    val right = eval(x2, env, root)
    (left, right) match {
      case (FuzzyVal(i1), FuzzyVal(i2)) => Add(FuzzyVal(i1), FuzzyVal(i2))
      case (FuzzySet(elems1), FuzzySet(elems2)) => Add(FuzzySet(elems1), FuzzySet(elems2))
      case (FuzzyVal(0.0), r) => r // Identity for addition
      case (l, FuzzyVal(0.0)) => l
      // Associativity and Commutativity: Group constants
      case (FuzzyVal(i1), FuzzyAdd(FuzzyVal(i2), r)) => FuzzyAdd(Add(FuzzyVal(i1), FuzzyVal(i2)), r)
      case (FuzzyVal(i1), FuzzyAdd(l, FuzzyVal(i2))) => FuzzyAdd(l, Add(FuzzyVal(i1), FuzzyVal(i2)))
      case (FuzzyAdd(FuzzyVal(i1), r), FuzzyVal(i2)) => FuzzyAdd(Add(FuzzyVal(i1), FuzzyVal(i2)), r)
      case (FuzzyAdd(l, FuzzyVal(i1)), FuzzyVal(i2)) => FuzzyAdd(l, Add(FuzzyVal(i1), FuzzyVal(i2)))
      case (FuzzySet(elems1), FuzzyAdd(FuzzySet(elems2), r)) => FuzzyAdd(Add(FuzzySet(elems1), FuzzySet(elems2)), r)
      case (FuzzySet(elems1), FuzzyAdd(l, FuzzySet(elems2))) => FuzzyAdd(l, Add(FuzzySet(elems1), FuzzySet(elems2)))
      case (FuzzyAdd(FuzzySet(elems1), r), FuzzySet(elems2)) => FuzzyAdd(Add(FuzzySet(elems1), FuzzySet(elems2)), r)
      case (FuzzyAdd(l, FuzzySet(elems1)), FuzzySet(elems2)) => FuzzyAdd(l, Add(FuzzySet(elems1), FuzzySet(elems2)))
      //          case (FuzzyVal(i1), FuzzyAdd(l,FuzzyVal(i2))) =>  FuzzyAdd(l,Add(FuzzyVal(i1), FuzzyVal(i2)))
      case _ => FuzzyAdd(left, right)
    }
}
```

### Assign and Other Expressions

```scala
//        FPPE: Partially Safe, Error condition: Invalid operation (Error can be generated for non-compatible types)
expr match {
  case Assign(FuzzyVar((name, value)), expr) =>
    val evaluatedExpr = eval(expr, env, root)
    env.setVariable(name, evaluatedExpr)
    evaluatedExpr

  //        FPPE: Partially Safe, Error condition: Invalid operation (Error can be generated for non-compatible types)
  case Assign(FuzzyVar(name: String), expr) =>
    val evaluatedExpr = eval(expr, env, root)
    env.setVariable(name, evaluatedExpr)
    //        if isFullyEvaluated(evaluatedExpr) then evaluatedExpr else expr
    evaluatedExpr
}
```        

### Invoke Method on Class

- Refer to [`src/main/scala/FuzzyEvaluator.scala`](src/main/scala/FuzzyEvaluator.scala) for the implementation of method invocation on classes.

### IfTrue Conditional Expressions and Other Logical Operations

- Refer to [`src/main/scala/FuzzyEvaluator.scala`](src/main/scala/FuzzyEvaluator.scala) for the implementation of `IfTrue` conditional expressions.


### Partial Evaluation

#### Invocation of Methods on Classes

---

**1. Partial and Full Evaluation of a Simple Method**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

// Define a method that performs addition and multiplication
val methodDef: MethodDef = MethodDef(
  "compute",
  List(Parameter("x", ParamType("FuzzyVal"))),
  List(
    Assign(FuzzyVar("result"), FuzzyAdd(FuzzyVar("x"), FuzzyVar("h"))),
    FuzzyMult(FuzzyVar("result"), FuzzyVal(2.0))
  )
)
val classDef = CreateClass("MyClass", None, List.empty, List(methodDef))
eval(classDef, commonEnv, commonEnv)

// Create an instance and invoke the method
val createInstanceExpr = CreateInstance("MyClass", "instance1")
eval(createInstanceExpr, commonEnv, commonEnv)

val invokeExpr = InvokeMethod("instance1", "compute", List(("x", FuzzyVal(0.5))))
val resultInvoke = eval(invokeExpr, commonEnv, commonEnv)
// Output: PartiallyEvaluatedMethod(List(FuzzyAdd(FuzzyVal(0.5), FuzzyVar("h")), FuzzyMult(...)), ...)

// Define `h` and re-evaluate
commonEnv.setVariable("h", FuzzyVal(0.2))
val resultInvokeFull = eval(resultInvoke, commonEnv, commonEnv)
// Output: FuzzyVal(1.4)
```

**Explanation:**
- The `compute` method depends on `h`, which is initially undefined.
- When invoked, the method remains partially evaluated until `h` is defined.
- After defining `h`, the body resolves fully, returning `1.4`.

---

**2. Nested Method Invocation**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

// Define the first method
val methodDef: MethodDef = MethodDef(
  "compute",
  List(Parameter("x", ParamType("FuzzyVal"))),
  List(
    Assign(FuzzyVar("result"), FuzzyAdd(FuzzyVar("x"), FuzzyVar("h"))),
    FuzzyMult(FuzzyVar("result"), FuzzyVal(2.0))
  )
)
val classDef = CreateClass("MyClass", None, List.empty, List(methodDef))
eval(classDef, commonEnv, commonEnv)

// Define the second method that calls the first
val methodDef2: MethodDef = MethodDef(
  "compute2",
  List(Parameter("x", ParamType("FuzzyVal"))),
  List(
    InvokeMethod("instance1", "compute", List(("x", FuzzyVar("x"))))
  )
)
val classDef2 = CreateClass("MyClass2", None, List.empty, List(methodDef2))
eval(classDef2, commonEnv, commonEnv)

// Create instances
eval(CreateInstance("MyClass", "instance1"), commonEnv, commonEnv)
eval(CreateInstance("MyClass2", "instance2"), commonEnv, commonEnv)

// Invoke the nested method
val invokeExpr = InvokeMethod("instance2", "compute2", List(("x", FuzzyVal(0.5))))
val resultInvoke = eval(invokeExpr, commonEnv, commonEnv)
// Output: PartiallyEvaluatedMethod(List(...nested partially evaluated method body...), ...)

// Define `h` and re-evaluate
commonEnv.setVariable("h", FuzzyVal(0.2))
val resultInvokeFull = eval(resultInvoke, commonEnv, commonEnv)
// Output: FuzzyVal(1.4)
```

**Explanation:**
- The `compute2` method calls `compute` on `instance1`, creating a nested partial evaluation.
- The inner method remains partially evaluated until its dependencies (`h`) are resolved.
- After defining `h`, both the inner and outer methods fully resolve, returning `1.4`.

---

#### IfTrue Conditional Expressions

---

**1. Partial Evaluation of Condition, Full Evaluation of `ThenExecute`**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

val ifTrueExpr = IfTrue(
  condition = FuzzyAdd(FuzzyVal(0.5), FuzzyVar("condVar")), // Partially evaluated condition
  thenBranch = ThenExecute(List(
    Assign(FuzzyVar("x"), FuzzyVal(10))
  )),
  elseBranch = ElseRun(List(
    Assign(FuzzyVar("x"), FuzzyVal(0))
  ))
)

val resultPartialCondition = eval(ifTrueExpr, commonEnv, commonEnv)
// Output: IfTrue(FuzzyAdd(FuzzyVal(0.5), FuzzyVar("condVar")), ThenExecute(...), ElseRun(...))

commonEnv.setVariable("condVar", FuzzyVal(0.5))
val resultConditionFull = eval(ifTrueExpr, commonEnv, commonEnv)
// Output: FuzzyVal(1.0)
assert(commonEnv.lookup("x") == Some(FuzzyVal(10)))
```

**Explanation:**
- The condition (`0.5 + condVar`) is partially evaluated until `condVar` is defined.
- Once the condition resolves to `FuzzyVal(1.0)`, the `ThenExecute` branch is fully evaluated, assigning `x = 10`.

---

**2. Full Evaluation of Condition, Partial Evaluation of `ThenExecute`**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

val ifTrueExpr = IfTrue(
  condition = FuzzyVal(1.0), // Fully evaluated condition
  thenBranch = ThenExecute(List(
    Assign(FuzzyVar("y"), FuzzyVar("undefinedVar")) // Partially evaluated
  )),
  elseBranch = ElseRun(List(
    Assign(FuzzyVar("y"), FuzzyVal(0))
  ))
)

val resultPartialThenBranch = eval(ifTrueExpr, commonEnv, commonEnv)
// Output: IfTrue(FuzzyVal(1.0), ThenExecute(List(FuzzyVar("undefinedVar"))), ElseRun(...))

commonEnv.setVariable("undefinedVar", FuzzyVal(20))
val resultThenBranchFull = eval(resultPartialThenBranch, commonEnv, commonEnv)
// Output: FuzzyVal(1.0)
assert(commonEnv.lookup("y") == Some(FuzzyVar("undefinedVar")))
```

**Explanation:**
- The condition is immediately resolved to `true`, but the `ThenExecute` branch remains partially evaluated due to `undefinedVar`.
- Once `undefinedVar` is defined, the branch is fully evaluated.

---

**3. Full Evaluation of Condition, Partial Evaluation of `ElseRun`**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

val ifTrueExpr = IfTrue(
  condition = FuzzyVal(0.0), // Fully evaluated false condition
  thenBranch = ThenExecute(List(
    Assign(FuzzyVar("z"), FuzzyVal(10))
  )),
  elseBranch = ElseRun(List(
    Assign(FuzzyVar("z"), FuzzyVar("undefinedVar")) // Partially evaluated
  ))
)

val resultPartialElseBranch = eval(ifTrueExpr, commonEnv, commonEnv)
// Output: IfTrue(FuzzyVal(0.0), ThenExecute(...), ElseRun(List(FuzzyVar("undefinedVar"))))

commonEnv.setVariable("undefinedVar", FuzzyVal(5))
val resultElseBranchFull = eval(resultPartialElseBranch, commonEnv, commonEnv)
// Output: FuzzyVal(1.0)
assert(commonEnv.lookup("z") == Some(FuzzyVar("undefinedVar")))
```

**Explanation:**
- The condition evaluates to `false`, so the `ElseRun` branch is selected.
- When `undefinedVar` is defined, the branch fully resolves, assigning `z = undefinedVar`.

---

**4. Partial Evaluation of Condition and Branches**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

val ifTrueExpr = IfTrue(
  condition = FuzzyAdd(FuzzyVal(0.3), FuzzyVar("condVar")), // Partially evaluated condition
  thenBranch = ThenExecute(List(
    Assign(FuzzyVar("result"), FuzzyAdd(FuzzyVar("a"), FuzzyVar("b"))) // Partially evaluated
  )),
  elseBranch = ElseRun(List(
    Assign(FuzzyVar("result"), FuzzyVal(0))
  ))
)

val resultPartial = eval(ifTrueExpr, commonEnv, commonEnv)
// Output: IfTrue(FuzzyAdd(FuzzyVal(0.3), FuzzyVar("condVar")), ThenExecute(...), ElseRun(...))

commonEnv.setVariable("condVar", FuzzyVal(0.7))
commonEnv.setVariable("a", FuzzyVal(2))
commonEnv.setVariable("b", FuzzyVal(3))
val resultFull = eval(ifTrueExpr, commonEnv, commonEnv)
// Output: FuzzyVal(1.0)
assert(commonEnv.lookup("result") == Some(FuzzyVal(1.0)))
```

**Explanation:**
- Both the condition and the `ThenExecute` branch are partially evaluated.
- As the variables `condVar`, `a`, and `b` are defined, the expression fully resolves.

---


#### Math Operations

---

**1. Basic Partial Evaluation: Addition**

**Example:**
```scala
val expr = FuzzyAdd(FuzzyAdd(FuzzyVal(0.5), FuzzyVal(0.1)), FuzzyVar("x"))
val resultPartial = eval(expr, commonEnv, commonEnv)
// Output: FuzzyAdd(FuzzyVal(0.6), FuzzyVar("x"))

commonEnv.setVariable("x", FuzzyVal(0.3))
val resultFull = eval(expr, commonEnv, commonEnv)
// Output: FuzzyVal(0.8999999999999999)
```

**Explanation:**
- Initially, the two constants `0.5` and `0.1` are added, resulting in `FuzzyVal(0.6)`.
- The variable `x` remains unresolved, so the expression is partially evaluated.
- Once `x` is defined, the expression fully resolves to a concrete value.

---

**2. Basic Partial Evaluation: Multiplication**

**Example:**
```scala
val expr = FuzzyMult(FuzzyVal(0.5), FuzzyVar("y"))
val resultPartial = eval(expr, commonEnv, commonEnv)
// Output: FuzzyMult(FuzzyVal(0.5), FuzzyVar("y"))

commonEnv.setVariable("y", FuzzyVal(0.6))
val resultFull = eval(expr, commonEnv, commonEnv)
// Output: FuzzyVal(0.3)
```

**Explanation:**
- The `FuzzyVal(0.5)` is multiplied by an unresolved variable `y`, so the expression remains partially evaluated.
- After defining `y`, the multiplication is completed, resulting in `FuzzyVal(0.3)`.

---

**3. Associativity and Commutativity in Addition**

**Example:**
```scala
val expr = FuzzyAdd(FuzzyVal(0.2), FuzzyAdd(FuzzyVal(0.3), FuzzyVar("z")))
val resultPartial = eval(expr, commonEnv, commonEnv)
// Output: FuzzyAdd(FuzzyVal(0.5), FuzzyVar("z"))

commonEnv.setVariable("z", FuzzyVal(0.4))
val resultFull = eval(expr, commonEnv, commonEnv)
// Output: FuzzyVal(0.8999999999999999)
```

**Explanation:**
- The constants `0.2` and `0.3` are grouped and simplified to `FuzzyVal(0.5)`, showcasing the **associative property**.
- The unresolved variable `z` is retained in symbolic form until it is defined.

---

**4. Nested Additions**

**Example:**
```scala
val expr = FuzzyAdd(FuzzyAdd(FuzzyVal(0.2), FuzzyVar("a")), FuzzyVar("b"))
val resultPartial = eval(expr, commonEnv, commonEnv)
// Output: FuzzyAdd(FuzzyAdd(FuzzyVal(0.2), FuzzyVar("a")), FuzzyVar("b"))

commonEnv.setVariable("a", FuzzyVal(0.3))
val resultIntermediate = eval(expr, commonEnv, commonEnv)
// Output: FuzzyAdd(FuzzyVal(0.5), FuzzyVar("b"))

commonEnv.setVariable("b", FuzzyVal(0.4))
val resultFull = eval(expr, commonEnv, commonEnv)
// Output: FuzzyVal(0.9)
```

**Explanation:**
- The expression is partially evaluated in stages:
  1. Simplify constants (`0.2 + 0.3`).
  2. Resolve the next variable `b` once it is defined.

---

**5. Logical Operations: AND**

**Example:**
```scala
val expr = FuzzyAnd(FuzzyVal(0.7), FuzzyVar("x"))
val resultPartial = eval(expr, commonEnv, commonEnv)
// Output: FuzzyAnd(FuzzyVal(0.7), FuzzyVar("x"))

commonEnv.setVariable("x", FuzzyVal(0.9))
val resultFull = eval(expr, commonEnv, commonEnv)
// Output: FuzzyVal(0.7)
```

**Explanation:**
- The partial evaluation retains the unresolved variable in the `FuzzyAnd` expression.
- Once `x` is defined, the logical AND is resolved, returning the minimum of the two values (`0.7`).

---

**6. Nested Logical Operations: OR**

**Example:**
```scala
val expr = FuzzyOr(FuzzyOr(FuzzyVal(0.2), FuzzyVar("x")), FuzzyVar("y"))
val resultPartial = eval(expr, commonEnv, commonEnv)
// Output: FuzzyOr(FuzzyOr(FuzzyVal(0.2), FuzzyVar("x")), FuzzyVar("y"))

commonEnv.setVariable("x", FuzzyVal(0.5))
val resultIntermediate = eval(expr, commonEnv, commonEnv)
// Output: FuzzyOr(FuzzyVal(0.5), FuzzyVar("y"))

commonEnv.setVariable("y", FuzzyVal(0.7))
val resultFull = eval(expr, commonEnv, commonEnv)
// Output: FuzzyVal(0.7)
```

**Explanation:**
- Logical operations like `FuzzyOr` are partially evaluated until all variables are resolved.
- The maximum value among the operands is returned once the expression is fully resolved.

---

#### Fuzzy Logic Gates

---
**1. Fuzzy Gate Creation and Partial Evaluation**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

// Create a FuzzyGate
val gateExpr = Assign(FuzzyGate("ANDGate"), FuzzyAnd(FuzzyVar("p"), FuzzyVar("q")))
val resultGate = eval(gateExpr, commonEnv, commonEnv)
// Output: FuzzyAnd(FuzzyVar("p"), FuzzyVar("q"))

// Test the gate without defining variables
val testGateExpr = TestGate("ANDGate", FuzzyVal(0.8))
val resultTestGate = eval(testGateExpr, commonEnv, commonEnv)
// Output: FuzzyAnd(FuzzyAnd(FuzzyVar("p"), FuzzyVar("q")), FuzzyVal(0.8))
```

**Explanation:**
- The `ANDGate` is created with unresolved variables `p` and `q`.
- When tested, the gate remains partially evaluated, combining the unresolved expression with the test input (`FuzzyVal(0.8)`).

---

**2. Full Evaluation of Fuzzy Gates**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

// Create a FuzzyGate
val gateExpr = Assign(FuzzyGate("ANDGate"), FuzzyAnd(FuzzyVar("p"), FuzzyVar("q")))
eval(gateExpr, commonEnv, commonEnv)

// Define variables `p` and `q`
commonEnv.setVariable("p", FuzzyVal(0.7))
commonEnv.setVariable("q", FuzzyVal(0.9))

// Test the gate after variables are defined
val testGateExpr = TestGate("ANDGate", FuzzyVal(0.8))
val resultTestGateFull = eval(testGateExpr, commonEnv, commonEnv)
// Output: FuzzyVal(0.7)
```

**Explanation:**
- When the variables `p` and `q` are defined, the gate is fully resolved.
- The `AND` operation computes the minimum of the inputs (`0.7` and `0.9`), resulting in `0.7`.

---

**3. Fuzzy Gates with Partially Defined Inputs**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

// Create a FuzzyGate
val gateExpr = Assign(FuzzyGate("ORGate"), FuzzyOr(FuzzyVar("x"), FuzzyVar("y")))
eval(gateExpr, commonEnv, commonEnv)

// Test the gate with partially defined inputs
val testGateExpr = TestGate("ORGate", FuzzyVal(0.5))
val resultTestGate = eval(testGateExpr, commonEnv, commonEnv)
// Output: FuzzyAnd(FuzzyOr(FuzzyVar("x"), FuzzyVar("y")), FuzzyVal(0.5))

// Define `x` and test again
commonEnv.setVariable("x", FuzzyVal(0.4))
val resultPartial = eval(testGateExpr, commonEnv, commonEnv)
// Output: FuzzyAnd(FuzzyOr(FuzzyVal(0.4), FuzzyVar("y")), FuzzyVal(0.5))

// Define `y` and test again
commonEnv.setVariable("y", FuzzyVal(0.6))
val resultFull = eval(testGateExpr, commonEnv, commonEnv)
// Output: FuzzyVal(0.5)
```

**Explanation:**
- The `ORGate` is partially evaluated with unresolved variables `x` and `y`.
- After defining `x`, the expression simplifies further.
- When both `x` and `y` are defined, the gate fully resolves, returning the maximum of the inputs (`0.6`) combined with the test value (`0.5`).

---

**4. Nested Fuzzy Gates**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

// Create nested FuzzyGates
val gateExpr1 = Assign(FuzzyGate("Gate1"), FuzzyAnd(FuzzyVar("a"), FuzzyVar("b")))
val gateExpr2 = Assign(FuzzyGate("Gate2"), FuzzyOr(FuzzyGate("Gate1"), FuzzyVar("c")))
eval(gateExpr1, commonEnv, commonEnv)
eval(gateExpr2, commonEnv, commonEnv)

// Test Gate2 without defining variables
val testGateExpr = TestGate("Gate2", FuzzyVal(0.5))
val resultTestGate = eval(testGateExpr, commonEnv, commonEnv)
// Output: FuzzyAnd(FuzzyOr(FuzzyAnd(FuzzyVar("a"), FuzzyVar("b")), FuzzyVar("c")), FuzzyVal(0.5))

// Define variables `a`, `b`, and `c`
commonEnv.setVariable("a", FuzzyVal(0.8))
commonEnv.setVariable("b", FuzzyVal(0.6))
commonEnv.setVariable("c", FuzzyVal(0.7))

// Test Gate2 after defining variables
val resultTestGateFull = eval(testGateExpr, commonEnv, commonEnv)
// Output: FuzzyVal(0.5)
```

**Explanation:**
- `Gate1` and `Gate2` are nested gates. Initially, both remain partially evaluated.
- As the variables `a`, `b`, and `c` are defined, the gates resolve progressively.
- The nested structure ensures that evaluation flows correctly across gates.

---

### Let Macros and Nested Macro Definitions

---

**1. Partial Evaluation of Let with Undefined Variables**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

val letExpr = Let(
  List(
    Assign(FuzzyVar("d"), FuzzyAdd(FuzzyVal(0.1), FuzzyVar("e"))),
    Assign(FuzzyVar("f"), FuzzyMult(FuzzyVar("d"), FuzzyVal(0.3)))
  ),
  FuzzyAdd(FuzzyVar("f"), FuzzyVal(0.3))
)

val resultPartial = eval(letExpr, commonEnv, commonEnv)
// Output: FuzzyAdd(FuzzyMult(FuzzyAdd(FuzzyVal(0.1), FuzzyVar("e")), FuzzyVal(0.3)), FuzzyVal(0.3))

commonEnv.setVariable("e", FuzzyVal(0.4))
val resultFull = eval(letExpr, commonEnv, commonEnv)
// Output: FuzzyVal(0.44999999999999996)
```

**Explanation:**
- The `Let` expression defines variables `d` and `f` locally.
- When `e` is undefined, the evaluation remains partial, retaining the symbolic representation.
- After defining `e`, the expression resolves completely.

---

**2. Nested Let Assignments**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

val letExpr = Let(
  List(
    Assign(FuzzyVar("a"), FuzzyVal(0.2)),
    Assign(FuzzyVar("b"), FuzzyAdd(FuzzyVar("a"), FuzzyVar("c")))
  ),
  FuzzyMult(FuzzyVar("b"), FuzzyVal(0.5))
)

val resultPartial = eval(letExpr, commonEnv, commonEnv)
// Output: FuzzyMult(FuzzyAdd(FuzzyVal(0.2), FuzzyVar("c")), FuzzyVal(0.5))

commonEnv.setVariable("c", FuzzyVal(0.3))
val resultFull = eval(letExpr, commonEnv, commonEnv)
// Output: FuzzyVal(0.25)
```

**Explanation:**
- Nested assignments (`b` depends on `a` and `c`) allow incremental resolution.
- When `c` is undefined, the evaluation is deferred. Defining `c` completes the computation.

---

**3. Macro Definition and Use**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

val macroDefinition = FuzzyAdd(FuzzyVal(0.2), FuzzyVar("g"))
val macroExpr = DefineMacro("myMacro", macroDefinition)
eval(macroExpr, commonEnv, commonEnv)

val macroUsage = Macro("myMacro")
val resultPartial = eval(macroUsage, commonEnv, commonEnv)
// Output: FuzzyAdd(FuzzyVal(0.2), FuzzyVar("g"))

commonEnv.setVariable("g", FuzzyVal(0.8))
val resultFull = eval(macroUsage, commonEnv, commonEnv)
// Output: FuzzyVal(1.0)
```

**Explanation:**
- The macro `myMacro` encapsulates a reusable expression.
- When `g` is undefined, the macro evaluates partially. Defining `g` resolves the macro completely.

---

**4. Nested Macros**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

val macro1Definition = FuzzyAdd(FuzzyVar("x"), FuzzyVal(0.1))
val macro2Definition = FuzzyMult(Macro("macro1"), FuzzyVar("y"))

eval(DefineMacro("macro1", macro1Definition), commonEnv, commonEnv)
eval(DefineMacro("macro2", macro2Definition), commonEnv, commonEnv)

val macroUsage = Macro("macro2")
val resultPartial = eval(macroUsage, commonEnv, commonEnv)
// Output: FuzzyMult(FuzzyAdd(FuzzyVar("x"), FuzzyVal(0.1)), FuzzyVar("y"))

commonEnv.setVariable("x", FuzzyVal(0.5))
val resultIntermediate = eval(macroUsage, commonEnv, commonEnv)
// Output: FuzzyMult(FuzzyVal(0.6), FuzzyVar("y"))

commonEnv.setVariable("y", FuzzyVal(0.4))
val resultFull = eval(macroUsage, commonEnv, commonEnv)
// Output: FuzzyVal(0.24)
```

**Explanation:**
- The nested macro `macro2` references `macro1`, demonstrating hierarchical resolution.
- Partially resolved macros retain symbolic representations until all variables are defined.

---

**5. Let with Macro References**

**Example:**
```scala
val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

val macroDefinition = FuzzyAdd(FuzzyVal(0.3), FuzzyVar("a"))
eval(DefineMacro("macro1", macroDefinition), commonEnv, commonEnv)

val letExpr = Let(
  List(
    Assign(FuzzyVar("b"), Macro("macro1")),
    Assign(FuzzyVar("c"), FuzzyMult(FuzzyVar("b"), FuzzyVar("d")))
  ),
  FuzzyAdd(FuzzyVar("c"), FuzzyVal(0.1))
)

val resultPartial = eval(letExpr, commonEnv, commonEnv)
// Output: FuzzyAdd(FuzzyMult(FuzzyAdd(FuzzyVal(0.3), FuzzyVar("a")), FuzzyVar("d")), FuzzyVal(0.1))

commonEnv.setVariable("a", FuzzyVal(0.2))
commonEnv.setVariable("d", FuzzyVal(0.5))
val resultFull = eval(letExpr, commonEnv, commonEnv)
// Output: FuzzyVal(0.35)
```

**Explanation:**
- The macro `macro1` is used as part of the `Let` expression.
- Both the macro and the variables in the `Let` scope are resolved incrementally.

---





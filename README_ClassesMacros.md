# Fuzzy Logic System with Classes, Macros, and Scoped Variables

This project builds a **Fuzzy Logic System** in **Scala** that supports classes, macros, and scoped variables for defining and evaluating fuzzy logic expressions. It allows the creation of classes with methods that include both fuzzy and non-fuzzy operations, as well as macros that can be reused in expressions. The system also includes statically scoped variables and logic gates, supporting complex logic-based structures and encapsulated class methods.

## [Detailed Report Fuzzy Logic](REPORT.md)

## [Old Readme](README_FuzzyLogic.md)

## Project Overview

This system is built with modularity in mind, providing powerful tools to manage variables, classes, instances, methods, and macros within scoped environments. It supports fuzzy and non-fuzzy operations, including arithmetic and logical gates, with statically scoped variables that maintain context-specific assignments.

## Prerequisites

- **Scala**: Ensure you have Scala installed.
- **sbt**: The Simple Build Tool for Scala projects.

## Getting Started

### Clone the Repository

```sh
git clone https://github.com/Kaushal1011/CS476FuzzyLogicHW1
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


### Key Features

- **Class Definitions**: Define classes with variables and methods that support fuzzy and non-fuzzy operations. Classes can also inherit from other classes, extending functionality.

- **Method Invocation**: Invoke methods on class instances, including inherited methods from parent classes. This allows for method reuse and polymorphism.

- **Fuzzy Logic Operations**: Perform fuzzy operations like addition, multiplication, AND, OR, and XOR within methods, enabling complex decision-making logic in class definitions.

- **Macros**: Define and use reusable expressions with macros, which can be incorporated into other expressions and reused across methods.

- **Scoped Variables**: Use `Let` constructs to create local scopes for variables, preserving isolation and ensuring controlled access to variables.

- **Non-Fuzzy Operations**: Support non-fuzzy arithmetic and assignment within methods, with customizable non-fuzzy operations such as integer addition and string concatenation.

- **Environment Management**: Manage classes, instances, methods, and macros within dynamically scoped environments, tracking variable states and evaluating expressions according to static scoping rules.

## New Features and Expanded Test Coverage

The following sections highlight the key features demonstrated by new test cases, illustrating how the system supports classes, macros, and logic gates.

### Class Definitions and Inheritance

Classes can be defined with variables and methods. The system supports inheritance, allowing derived classes to extend base class functionality.

- **Example**:
  ```scala
  val baseClass = CreateClass(
    "Base",
    None,
    List(ClassVar("v1", VarType("Integer"))),
    List(MethodDef(
      "m1",
      List(Parameter("p1", ParamType("Integer"))),
      List(NonFuzzyAssign("v1", NonFuzzyType(10)))
    ))
  )

  val derivedClass = CreateClass(
    "Derived",
    Some("Base"),
    List(ClassVar("v2", VarType("String"))),
    List(MethodDef("m2", List(Parameter("p2", ParamType("String"))), List(NonFuzzyAssign("v2", NonFuzzyType("hello")))))
  )
  ```

### Method Invocation

Methods within classes can be invoked on instances, supporting both fuzzy and non-fuzzy operations. Inherited methods from a base class can also be called on derived instances.

- **Example**:
  ```scala
  val createInstanceExpr = CreateInstance("Derived")
  eval(createInstanceExpr, commonEnv, commonEnv)

  val invokeMethodExpr = InvokeMethod("Derived", "m1", List(("p1", NonFuzzyType(5))))
  val methodResult = eval(invokeMethodExpr, commonEnv, commonEnv)
  assert(methodResult == NonFuzzyType(10))
  ```

### Fuzzy Logic in Methods

The system supports fuzzy operations like addition, multiplication, AND, OR, and XOR. These operations are evaluated within the methods of a class and provide flexibility in decision-making.

- **Example**:
  ```scala
  val invokeMethodExpr = InvokeMethod("Derived", "m2", List(("p2", NonFuzzyType("world"))))
  val methodResult = eval(invokeMethodExpr, commonEnv, commonEnv)
  ```

### Macros for Reusable Expressions

Macros can be defined for fuzzy expressions and then reused within other expressions or methods. This modularity allows complex expressions to be defined once and used multiple times.

- **Example**:
  ```scala
  val defineMacroExpr = DefineMacro("macroExample", FuzzyAdd(FuzzyVal(0.3), FuzzyVal(0.2)))
  eval(defineMacroExpr, commonEnv, commonEnv)

  val useMacroExpr = FuzzyAdd(Macro("macroExample"), FuzzyVal(0.5))
  val result = eval(useMacroExpr, commonEnv, commonEnv)
  assert(result == FuzzyVal(1.0)) // 0.3 + 0.2 + 0.5, capped at 1.0
  ```

### Scoped Variables with `Let` Constructs

Variables can be assigned within a `Let` scope, providing a localized environment for evaluations. These scoped variables are used in nested expressions without affecting the outer scope.

- **Example**:
  ```scala
  val letExpr = Let(
    List(
      Assign(FuzzyVar("temp"), FuzzyAdd(FuzzyVal(0.6), FuzzyVal(0.4))),
      Assign(FuzzyVar("result"), FuzzyMult(Macro("macroExample"), FuzzyVar("temp")))
    ),
    FuzzyAdd(FuzzyVar("result"), FuzzyVal(0.1))
  )
  ```

### Non-Fuzzy Operations

The system also supports non-fuzzy operations such as integer addition and string concatenation, allowing for flexible handling of non-fuzzy types within classes and expressions.

- **Example**:
  ```scala
  val addition = (args: Seq[Any]) => args(0).asInstanceOf[Integer] + args(1).asInstanceOf[Integer]
  val assignVar = NonFuzzyAssign("X", NonFuzzyOperation(List(NonFuzzyType(3), NonFuzzyType(3)), addition))
  ```


## Tests For Classes, Methods, Macros, and Let Scopes

- [**Class and Method Tests**](src/test/scala/FuzzyClassTests.scala): Test cases for class definitions
- [**Macro Tests**](src/test/scala/MacroTests.scala): Test cases for macro definitions and usage
- [**Non-Fuzzy Operation Tests**](src/test/scala/NonFuzzyTests.scala): Test cases for non-fuzzy operations


import FuzzyExpressions.FuzzyExpression._
import FuzzyExpressions.FuzzyExpression
import FuzzyEvaluator._
import EnvironmentScopes.Environment
import scala.collection.mutable

import FuzzyExpressions.FuzzyExpression._
import FuzzyExpressions.FuzzyExpression
import EnvironmentScopes.Environment
import FuzzyEvaluator._
import scala.collection.mutable

@main
def main(): Unit = {
  // Create a common environment
  val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

  // Define a Base class with a method and fuzzy logic
  val baseClass = CreateClass(
    "Base",
    None,
    List(ClassVar("v1", VarType("Integer"))), // Declare an integer variable v1
    List(MethodDef(
      "m1",
      List(Parameter("p1", ParamType("Integer"))), // Single parameter method
      List(
        NonFuzzyAssign("v1", NonFuzzyType(10)), // Assign to non-fuzzy var
        FuzzyAdd(FuzzyVal(0.5), FuzzyVal(0.7)) // Perform a fuzzy addition
      )
    ))
  )
  eval(baseClass, commonEnv, commonEnv)
  println("Base class created.")

  // Define a macro and evaluate it
  val defineMacroExpr = DefineMacro("macroExample", FuzzyAdd(FuzzyVal(0.3), FuzzyVal(0.2)))
  eval(defineMacroExpr, commonEnv, commonEnv)
  println("Macro 'macroExample' defined.")

  // Use the macro in a fuzzy expression
  val useMacroExpr = FuzzyAdd(Macro("macroExample"), FuzzyVal(0.5))
  println(useMacroExpr)

  val macroResult = eval(useMacroExpr, commonEnv, commonEnv)
  println(s"Result of using 'macroExample' macro in expression: $macroResult")

  val addition = (args: Seq[Any]) => args(0).asInstanceOf[Integer] + args(1).asInstanceOf[Integer]

  // Define a Derived class extending Base with an additional method
  val derivedClass = CreateClass(
    "Derived",
    Some("Base"), // Extends Base class
    List(ClassVar("v2", VarType("String"))), // Add string variable v2
    List(
      MethodDef(
        "m2",
        List(Parameter("p2", ParamType("String"))),
        List(
          NonFuzzyAssign("v2", NonFuzzyType("hello")), // Assign a non-fuzzy string
          FuzzyMult(FuzzyVal(2.0), FuzzyVal(3.0)) // Perform a fuzzy multiplication
        )
      ),
      MethodDef(
        "m3",
        List(Parameter("p3", ParamType("Integer"))), // Second method with an int parameter
        List(
          FuzzyAnd(FuzzyVal(0.8), FuzzyVal(0.9)), // Perform a fuzzy AND
          NonFuzzyAssign("v1", NonFuzzyType(50)) // Override variable v1 in derived class
        )
      ),
      MethodDef(
        "m5",
        List(Parameter("p5", ParamType("Integer"))),
        List(
          NonFuzzyAssign("v1", NonFuzzyType(100)),
          NonFuzzyAssign("v2", NonFuzzyOperation(List(NonFuzzyType(10), NonFuzzyVar("p5")), addition))
        )
      )
    )
  )
  eval(derivedClass, commonEnv, commonEnv)
  println("Derived class created.")


  // Create a let expression with local variables and evaluate it
  val letExpr = Let(
    List(
      Assign(FuzzyVar("temp"), FuzzyAdd(FuzzyVal(0.6), FuzzyVal(0.4))),
      Assign(FuzzyVar("result"), FuzzyMult(Macro("macroExample"), FuzzyVar("temp")))
    ),
    FuzzyAdd(FuzzyVar("result"), FuzzyVal(0.1))
  )
  val letResult = eval(letExpr, commonEnv, commonEnv)
  println(s"Result of let expression: $letResult")

  // Create an instance of the Derived class
  val createInstanceExpr = CreateInstance("Derived")
  val instanceResult = eval(createInstanceExpr, commonEnv, commonEnv)
  println(s"Created instance of Derived: $instanceResult")

  // Invoke a method (m2) on the Derived class instance
  val invokeMethodExpr = InvokeMethod("Derived", "m2", List(("p2", NonFuzzyType("world"))))
  val methodResult = eval(invokeMethodExpr, commonEnv, commonEnv)
  println(s"Result of invoking m2: $methodResult")

  // Invoke the second method (m3) to test fuzzy logic and variable assignment
  val invokeMethodExpr2 = InvokeMethod("Derived", "m3", List(("p3", NonFuzzyType(42))))
  val methodResult2 = eval(invokeMethodExpr2, commonEnv, commonEnv)
  println(s"Result of invoking m3: $methodResult2")

  // Call the inherited method m1 on the derived instance
  val invokeBaseMethodExpr = InvokeMethod("Derived", "m1", List(("p1", NonFuzzyType(5))))
  val baseMethodResult = eval(invokeBaseMethodExpr, commonEnv, commonEnv)
  println(s"Result of invoking m1 from base class on derived instance: $baseMethodResult")

  // Call the method m5 on the derived instance
  val invokeMethodExpr5 = InvokeMethod("Derived", "m5", List(("p5", NonFuzzyType(35))))
  val methodResult5 = eval(invokeMethodExpr5, commonEnv, commonEnv)
  println(s"Result of invoking m5: $methodResult5")

  // Print the final environment to check all classes, instances, variables, and macros
  commonEnv.printEnvironment()
}

import org.scalatest.funsuite.AnyFunSuite
import FuzzyExpressions.FuzzyExpression._
import FuzzyEvaluator._
import EnvironmentScopes.Environment
import scala.collection.mutable


class NonFuzzyTests extends AnyFunSuite {

  test("NonFuzzy Assign Test") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val assignVar = NonFuzzyAssign("X", NonFuzzyType(0.3))
    val resultVarAssign = eval(assignVar, commonEnv, commonEnv)
    val evalX = eval(NonFuzzyVar("X"), commonEnv, commonEnv)
    assert(evalX == NonFuzzyType(0.3))
  }

  test("NonFuzzy Operation Test") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val addition = (args: Seq[Any]) => args(0).asInstanceOf[Integer] + args(1).asInstanceOf[Integer]
    val assignVar = NonFuzzyAssign("X", NonFuzzyOperation(List(NonFuzzyType(3), NonFuzzyType(3)), addition))
    val resultVarAssign = eval(assignVar, commonEnv, commonEnv)
    assert(resultVarAssign == NonFuzzyType(6))
    val evalX = eval(NonFuzzyVar("X"), commonEnv, commonEnv)
//    println(evalX)
    assert(evalX == NonFuzzyType(6))

  }

  test("Non Fuzzy Operations Test in Class Addition") {
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
//    println("Base class created.")

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
//    println("Derived class created.")

    val createInstanceExpr = CreateInstance("Derived", "Derived")
    val instanceResult = eval(createInstanceExpr, commonEnv, commonEnv)

    val invokeMethodExpr5 = InvokeMethod("Derived", "m5", List(("p5", NonFuzzyType(35))))
    val methodResult5 = eval(invokeMethodExpr5, commonEnv, commonEnv)
//    println(s"Result of invoking m5: $methodResult5")

//    commonEnv.printEnvironment( )

    assert(methodResult5 == NonFuzzyType(45))

  }

  test("Non Fuzzy Operation String Concat"){
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    val concat = (args: Seq[Any]) => args(0).asInstanceOf[String] + args(1).asInstanceOf[String]

    val assignVar = NonFuzzyAssign("X", NonFuzzyOperation(List(NonFuzzyType("Hello"), NonFuzzyType("World")), concat))

    val result = eval(assignVar, commonEnv, commonEnv)

    assert(result == NonFuzzyType("HelloWorld"))

    val evalX = eval(NonFuzzyVar("X"), commonEnv, commonEnv)

    assert(evalX == NonFuzzyType("HelloWorld"))
  }
}

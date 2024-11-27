import org.scalatest.funsuite.AnyFunSuite
import FuzzyExpressions.FuzzyExpression._
import FuzzyEvaluator._
import EnvironmentScopes.Environment
import scala.collection.mutable

class FuzzyClassTests extends AnyFunSuite {

  test("Base class creation with method and variable initialization") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val baseClass = CreateClass(
      "Base",
      None,
      List(ClassVar("v1", VarType("Integer"))),
      List(MethodDef(
        "m1",
        List(Parameter("p1", ParamType("Integer"))),
        List(
          NonFuzzyAssign("v1", NonFuzzyType(10)),
          FuzzyAdd(FuzzyVal(0.5), FuzzyVal(0.7))
        )
      ))
    )

    eval(baseClass, commonEnv, commonEnv)
  }

  test("Derived class creation with inheritance from Base class") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Define the Base class first
    val baseClass = CreateClass(
      "Base",
      None,
      List(ClassVar("v1", VarType("Integer"))),
      List(MethodDef(
        "m1",
        List(Parameter("p1", ParamType("Integer"))),
        List(
          NonFuzzyAssign("v1", NonFuzzyType(10)),
          FuzzyAdd(FuzzyVal(0.5), FuzzyVal(0.7))
        )
      ))
    )
    eval(baseClass, commonEnv, commonEnv)

    // Define the Derived class that extends Base
    val derivedClass = CreateClass(
      "Derived",
      Some("Base"),
      List(ClassVar("v2", VarType("String"))),
      List(
        MethodDef(
          "m2",
          List(Parameter("p2", ParamType("String"))),
          List(
            NonFuzzyAssign("v2", NonFuzzyType("hello")),
            FuzzyMult(FuzzyVal(2.0), FuzzyVal(3.0))
          )
        )
      )
    )
    eval(derivedClass, commonEnv, commonEnv)
  }

  test("Invoke method m1 from Base class in a Derived class instance") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Define and evaluate the Base and Derived classes
    val baseClass = CreateClass(
      "Base",
      None,
      List(ClassVar("v1", VarType("Integer"))),
      List(MethodDef("m1", List(Parameter("p1", ParamType("Integer"))), List(NonFuzzyAssign("v1", NonFuzzyType(10)))))
    )
    eval(baseClass, commonEnv, commonEnv)

    val derivedClass = CreateClass(
      "Derived",
      Some("Base"),
      List(ClassVar("v2", VarType("String"))),
      List(MethodDef("m2", List(Parameter("p2", ParamType("String"))), List(NonFuzzyAssign("v2", NonFuzzyType("hello")))))
    )
    eval(derivedClass, commonEnv, commonEnv)

    // Create an instance of Derived and invoke method m1
    val createInstanceExpr = CreateInstance("Derived", "Derived")
    eval(createInstanceExpr, commonEnv, commonEnv)

    val invokeMethodExpr = InvokeMethod("Derived", "m1", List(("p1", NonFuzzyType(5))))
    val methodResult = eval(invokeMethodExpr, commonEnv, commonEnv)

    assert(methodResult == NonFuzzyType(10))

//    commonEnv.printEnvironment()

    val methodScope = commonEnv.findScope("MethodScope-m1").get

    assert(methodScope.lookup("p1").contains(NonFuzzyType(5)))

    assert(methodScope.lookup("v1").contains(NonFuzzyType(10)))

  }

  test("Variable assignment in Derived class method") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Define and evaluate the Derived class
    val derivedClass = CreateClass(
      "Derived",
      None,
      List(ClassVar("v1", VarType("Integer")), ClassVar("v2", VarType("String"))),
      List(
        MethodDef(
          "m3",
          List(Parameter("p3", ParamType("Integer"))),
          List(
            NonFuzzyAssign("v1", NonFuzzyType(100)),
            NonFuzzyAssign("v2", NonFuzzyType("test"))
          )
        )
      )
    )
    eval(derivedClass, commonEnv, commonEnv)

    // Create an instance of Derived and invoke method m3
    val createInstanceExpr = CreateInstance("Derived","Derived")
    eval(createInstanceExpr, commonEnv, commonEnv)

    val invokeMethodExpr = InvokeMethod("Derived", "m3", List(("p3", NonFuzzyType(42))))
    eval(invokeMethodExpr, commonEnv, commonEnv)
  }

  test("Fuzzy addition in Derived class method") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Define and evaluate the Derived class with a fuzzy operation in method
    val derivedClass = CreateClass(
      "Derived",
      None,
      List(ClassVar("v1", VarType("Integer"))),
      List(
        MethodDef(
          "m4",
          List(Parameter("p4", ParamType("Integer"))),
          List(FuzzyAdd(FuzzyVal(0.6), FuzzyVal(0.4)))
        )
      )
    )
    eval(derivedClass, commonEnv, commonEnv)

    // Create an instance of Derived and invoke method m4
    val createInstanceExpr = CreateInstance("Derived", "Derived")
    eval(createInstanceExpr, commonEnv, commonEnv)

    val invokeMethodExpr = InvokeMethod("Derived", "m4", List(("p4", NonFuzzyType(10))))
    val methodResult = eval(invokeMethodExpr, commonEnv, commonEnv)

    assert(methodResult == FuzzyVal(1.0))

    val methodScope = commonEnv.findScope("MethodScope-m4").get

    assert(methodScope.lookup("p4").contains(NonFuzzyType(10)))
  }
}

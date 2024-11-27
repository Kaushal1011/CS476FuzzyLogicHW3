import org.scalatest.funsuite.AnyFunSuite
import FuzzyExpressions.FuzzyExpression._
import FuzzyEvaluator._
import EnvironmentScopes.Environment
import scala.collection.mutable

class PartialEvalInvokeMethod extends AnyFunSuite {


  test("Invoke a method that only evaluates partially then fully evaluate it"){
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    val methodDef: MethodDef = MethodDef(
      "compute",
      List(Parameter("x", ParamType("FuzzyVal"))),
      List(
        Assign(FuzzyVar("result"), FuzzyAdd(FuzzyVar("x"), FuzzyVar("h"))),
        FuzzyMult(FuzzyVar("result"), FuzzyVal(2.0))
      )
    )
    val classDef = CreateClass("MyClass", None, List.empty, List(methodDef))

    val runClass = eval(classDef, commonEnv, commonEnv)


    val createInstanceExpr = CreateInstance("MyClass", "instance1")
    eval(createInstanceExpr, commonEnv, commonEnv)

    val invokeExpr = InvokeMethod("instance1", "compute", List(("x", FuzzyVal(0.5))))
    val resultInvoke = eval(invokeExpr, commonEnv, commonEnv)
//    println(s"Result of partial evaluation of InvokeMethod: $resultInvoke")
    
    val bodyPartial = resultInvoke.asInstanceOf[PartiallyEvaluatedMethod].body
    assert(bodyPartial== List(FuzzyAdd(FuzzyVal(0.5),FuzzyVar("h")), FuzzyMult(FuzzyAdd(FuzzyVal(0.5),FuzzyVar("h")),FuzzyVal(2.0))))
    // Now define 'h' and re-evaluate
    commonEnv.setVariable("h", FuzzyVal(0.2))
    val resultInvoke_full = eval(resultInvoke, commonEnv, commonEnv)
//    println(s"Result after defining 'h': $resultInvoke_full\n")

    assert(resultInvoke_full == FuzzyVal(1.4))

  }


}

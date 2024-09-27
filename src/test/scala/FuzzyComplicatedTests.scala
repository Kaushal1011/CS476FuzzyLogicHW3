import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.funsuite.AnyFunSuite
import FuzzyExpressions.FuzzyExpression.*
import FuzzyEvaluator.*
import EnvironmentScopes.Environment
import FuzzyExpressions.FuzzyExpression

import scala.collection.mutable

class FuzzyComplicatedTests extends AnyFunSuite {
  test("Nesting Assigns Test"){
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val assignVar = Assign(FuzzyGate("Gate1"), FuzzyMult(FuzzyAdd(FuzzyVal(0.5), Assign(FuzzyVar("X"), FuzzyVal(0.3))),FuzzyVar("X")))
    val resultVarAssign = eval(assignVar, commonEnv,commonEnv)
    val evalGate = eval(LogicGate("Gate1"), commonEnv, commonEnv)
    assert(evalGate == FuzzyVal(0.24))
//    commonEnv.printEnvironment()
  }

  test("Nesting Assigns Tests with Variable in Parent Scope and Fetching after Assign"){
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val assignVar = Assign(FuzzyGate("Gate1"), FuzzyAnd(FuzzyMult(FuzzyAdd(FuzzyVal(0.1), FuzzyVar("X")), Assign(FuzzyVar("X"), FuzzyVal(0.3))), FuzzyVar("X")))
    // the first occurence of X is in the parent scope and the second is in the child scope, third is scoped
    val parentX = Assign(FuzzyVar("X"), FuzzyVal(0.1))
    val evalX = eval(parentX, commonEnv, commonEnv)
//    (0.1 + 0.1 )*0.3 And 0.3 = 0.06 And 0.3 = 0.06
    val resultVarAssign = eval(assignVar, commonEnv, commonEnv)
    val evalGate = eval(LogicGate("Gate1"), commonEnv, commonEnv)
//    println(evalGate)
    assert(evalGate.asInstanceOf[FuzzyVal].i > 0.0598)
    assert(evalGate == FuzzyVal(0.06))
  }


}

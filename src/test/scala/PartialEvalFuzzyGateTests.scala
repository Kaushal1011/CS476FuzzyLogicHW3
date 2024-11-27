import org.scalatest.funsuite.AnyFunSuite
import FuzzyExpressions.FuzzyExpression._
import FuzzyEvaluator._
import EnvironmentScopes.Environment
import scala.collection.mutable

class PartialEvalFuzzyGateTests extends AnyFunSuite {

  test("FuzzyGate creation and partial evaluation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Create a FuzzyGate
    val gateExpr = Assign(FuzzyGate("ANDGate"), FuzzyAnd(FuzzyVar("p"), FuzzyVar("q")))
    val resultGate = eval(gateExpr, commonEnv, commonEnv)

    // Assert that the gate was created but not fully evaluated
    assert(resultGate == FuzzyAnd(FuzzyVar("p"), FuzzyVar("q")))

    // Test the gate without defining variables `p` and `q`
    val testGateExpr = TestGate("ANDGate", FuzzyVal(0.8))
    val resultTestGate = eval(testGateExpr, commonEnv, commonEnv)

    // Assert that the test gate remains partially evaluated
    assert(resultTestGate == FuzzyAnd(FuzzyAnd(FuzzyVar("p"), FuzzyVar("q")), FuzzyVal(0.8)))
  }

  test("FuzzyGate evaluation after defining variables") {
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

    // Assert that the gate evaluation is now fully resolved
    assert(resultTestGateFull == FuzzyVal(0.7)) // The minimum of 0.7 and 0.9 is 0.7
  }

  test("FuzzyGate with partially defined inputs") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Create a FuzzyGate
    val gateExpr = Assign(FuzzyGate("ORGate"), FuzzyOr(FuzzyVar("x"), FuzzyVar("y")))
    eval(gateExpr, commonEnv, commonEnv)

    // Test the gate with partially defined inputs
    val testGateExpr = TestGate("ORGate", FuzzyVal(0.5))
    val resultTestGate = eval(testGateExpr, commonEnv, commonEnv)

    // Assert that the result remains partially evaluated
    assert(resultTestGate == FuzzyAnd(FuzzyOr(FuzzyVar("x"), FuzzyVar("y")), FuzzyVal(0.5)))

    // Define `x` and test again
    commonEnv.setVariable("x", FuzzyVal(0.4))
    val resultPartial = eval(testGateExpr, commonEnv, commonEnv)
    assert(resultPartial == FuzzyAnd(FuzzyOr(FuzzyVal(0.4), FuzzyVar("y")), FuzzyVal(0.5)))

    // Define `y` and test again
    commonEnv.setVariable("y", FuzzyVal(0.6))
    val resultFull = eval(testGateExpr, commonEnv, commonEnv)
    assert(resultFull == FuzzyVal(0.5)) // The result of OR(0.4, 0.6) is 0.6 and 0.5 is 0.5
  }

  test("Nested FuzzyGates evaluation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Create nested FuzzyGates
    val gateExpr1 = Assign(FuzzyGate("Gate1"), FuzzyAnd(FuzzyVar("a"), FuzzyVar("b")))
    val gateExpr2 = Assign(FuzzyGate("Gate2"), FuzzyOr(FuzzyGate("Gate1"), FuzzyVar("c")))

    eval(gateExpr1, commonEnv, commonEnv)
    eval(gateExpr2, commonEnv, commonEnv)

    // Test Gate2 without defining any variables
    val testGateExpr = TestGate("Gate2", FuzzyVal(0.5))
    val resultTestGate = eval(testGateExpr, commonEnv, commonEnv)

    // Assert that Gate2 remains partially evaluated
    assert(resultTestGate == FuzzyAnd(FuzzyOr(FuzzyAnd(FuzzyVar("a"), FuzzyVar("b")), FuzzyVar("c")), FuzzyVal(0.5)))

    // Define variables `a`, `b`, and `c`
    commonEnv.setVariable("a", FuzzyVal(0.8))
    commonEnv.setVariable("b", FuzzyVal(0.6))
    commonEnv.setVariable("c", FuzzyVal(0.7))

    // Test Gate2 after defining variables
    val resultTestGateFull = eval(testGateExpr, commonEnv, commonEnv)

    // Assert that Gate2 is fully resolved
    assert(resultTestGateFull == FuzzyVal(0.5)) // OR(AND(0.8, 0.6), 0.7) And 0.5 = 0.5
  }
}

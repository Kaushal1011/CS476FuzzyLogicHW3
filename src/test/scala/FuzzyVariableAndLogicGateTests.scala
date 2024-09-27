import org.scalatest.funsuite.AnyFunSuite
import FuzzyExpressions.FuzzyExpression._
import FuzzyEvaluator._
import EnvironmentScopes.Environment
import scala.collection.mutable

class FuzzyVariableAndLogicGateTests extends AnyFunSuite {

  // Test for variable assignment and fetching
  test("Fuzzy Variable Assignment and Fetch: Should assign and fetch variable X") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Assign variable X
    val assignVar = Assign(FuzzyVar("X"), FuzzyVal(0.9))
    val resultVarAssign = eval(assignVar, commonEnv, commonEnv)
    assert(resultVarAssign == FuzzyVal(0.9))

    // Fetch variable X
    val fetchVar = FuzzyVar("X")
    val resultVarFetch = eval(fetchVar, commonEnv, commonEnv)
    assert(resultVarFetch == FuzzyVal(0.9))
  }

  // Test for scoping of variables inside logic gates
  test("Fuzzy Variable Scoping within LogicGate: Should assign scoped variables A and B") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Assign logic gate expression
    val logicGateExpr = Assign(FuzzyGate("logicGate1"), FuzzyAdd(FuzzyMult(FuzzyVar("A", FuzzyVal(0.1)), FuzzyVar("B", FuzzyVal(0.2))), FuzzyVal(0.1)))
    eval(logicGateExpr, commonEnv, commonEnv)

    // Assign values to A and B within the logic gate's scope
    val scopeExprA = Scope("logicGate1", Assign(FuzzyVar("A"), FuzzyVal(0.2)))
    val scopeExprB = Scope("logicGate1", Assign(FuzzyVar("B"), FuzzyVal(0.3)))
    val scopeResultA = eval(scopeExprA, commonEnv, commonEnv)
    val scopeResultB = eval(scopeExprB, commonEnv, commonEnv)

    assert(scopeResultA == FuzzyVal(0.2))
    assert(scopeResultB == FuzzyVal(0.3))
  }

  // Test for evaluating logic gate with scoped variables
  test("Fuzzy LogicGate Evaluation: Should evaluate logic gate with scoped variables A and B") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Assign logic gate expression
    val logicGateExpr = Assign(FuzzyGate("logicGate1"), FuzzyAdd(FuzzyMult(FuzzyVar("A", FuzzyVal(0.1)), FuzzyVar("B", FuzzyVal(0.2))), FuzzyVal(0.1)))
    eval(logicGateExpr, commonEnv, commonEnv)

    // Assign values to A and B within the logic gate's scope
    val scopeExprA = Scope("logicGate1", Assign(FuzzyVar("A"), FuzzyVal(0.2)))
    val scopeExprB = Scope("logicGate1", Assign(FuzzyVar("B"), FuzzyVal(0.3)))
    eval(scopeExprA, commonEnv, commonEnv)
    eval(scopeExprB, commonEnv, commonEnv)

    // Evaluate the logic gate
    val evalLogicGate = eval(LogicGate("logicGate1"), commonEnv, commonEnv)
    assert(evalLogicGate == FuzzyVal(0.16)) // 0.2 * 0.3 + 0.1 = 0.16
  }

  // Test for composite logic gate (logic gate within a logic gate)
  test("Fuzzy Composite LogicGate Evaluation: Should evaluate composite gate with variable C") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Assign logic gate expression
    val logicGateExpr = Assign(FuzzyGate("logicGate1"), FuzzyAdd(FuzzyMult(FuzzyVar("A", FuzzyVal(0.1)), FuzzyVar("B", FuzzyVal(0.2))), FuzzyVal(0.1)))
    eval(logicGateExpr, commonEnv, commonEnv)

    // Assign composite gate expression (XOR of logic gate1 and C)
    val compositeGateExpr = Assign(FuzzyGate("compositeGate"), FuzzyXor(LogicGate("logicGate1"), FuzzyVar("C")))
    eval(compositeGateExpr, commonEnv, commonEnv)

    // Assign variable C and evaluate the composite gate
    val assignC = Assign(FuzzyVar("C"), FuzzyVal(0.3))
    eval(assignC, commonEnv, commonEnv)

    // Evaluate the composite logic gate
    val evalCompositeGate = eval(LogicGate("compositeGate"), commonEnv, commonEnv)
    assert(evalCompositeGate == FuzzyVal(0.18))
  }

  // Test for testing logic gate with specific inputs
  test("Fuzzy TestGate: Should test logic gate with specific input") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Assign logic gate expression
    val logicGateExpr = Assign(FuzzyGate("logicGate1"), FuzzyAdd(FuzzyMult(FuzzyVar("A", FuzzyVal(0.6)), FuzzyVar("B", FuzzyVal(0.5))), FuzzyVal(0.2)))
    eval(logicGateExpr, commonEnv, commonEnv)

    // Test logic gate1 with a specific input
    val testGateExpr = TestGate("logicGate1", FuzzyVal(0.2))
    val testGateResult = eval(testGateExpr, commonEnv, commonEnv)

    assert(testGateResult == FuzzyVal(0.2)) // AND of (0.2) and (0.5) = min(0.2, 0.5)
  }

  // Test for error handling when logic gate input is missing
  test("Fuzzy CompositeGate Error Handling: Should throw error if variable C is not defined") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Assign composite gate expression (XOR of logic gate1 and C)
    val compositeGateExpr = Assign(FuzzyGate("compositeGate"), FuzzyXor(LogicGate("logicGate1"), FuzzyVar("C")))
    eval(compositeGateExpr, commonEnv, commonEnv)

    // Try testing the composite gate without defining C
    assertThrows[Exception] {
      val testCompositeGate = TestGate("compositeGate", FuzzyVar("A", FuzzyVal(0.5)))
      eval(testCompositeGate, commonEnv, commonEnv)
    }
  }
}

import FuzzyExpressions.FuzzyExpression._
import FuzzyExpressions.FuzzyExpression
import FuzzyEvaluator._
import EnvironmentScopes.Environment
import scala.collection.mutable

@main
def main(): Unit = {

  // Create a common environment
  val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

  // Fuzzy Addition on Val
  val expr = FuzzyAdd(FuzzyVal(0.5), FuzzyVal(0.3))
  val result = eval(expr, commonEnv,commonEnv)
  println(s"FuzzyAdd result: $result")

  // Fuzzy Addition on Set
  val expr2 = FuzzyAdd(
    FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))),
    FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.1)), ("C", FuzzyVal(0.4))))
  )
  val result2 = eval(expr2, commonEnv,commonEnv)
  println(s"FuzzyAdd on Set result: $result2")

  // Fuzzy Multiplication on Val
  val expr3 = FuzzyMult(FuzzyVal(0.5), FuzzyVal(0.3))
  val result3 = eval(expr3, commonEnv,commonEnv)
  println(s"FuzzyMult result: $result3")

  // Fuzzy Multiplication on Set
  val expr4 = FuzzyMult(
    FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))),
    FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.1)), ("C", FuzzyVal(0.4))))
  )
  val result4 = eval(expr4, commonEnv,commonEnv)
  println(s"FuzzyMult on Set result: $result4")

  // Fuzzy Alpha Cut
  val expr5 = FuzzyAlphaCut(
    FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))),
    FuzzyVal(0.35)
  )
  val result5 = eval(expr5, commonEnv,commonEnv)
  println(s"FuzzyAlphaCut result: $result5")


  // Fuzzy Variable Assignment Test
  val assignVar = Assign(FuzzyVar("X"), FuzzyVal(0.9))
  val resultVarAssign = eval(assignVar, commonEnv,commonEnv)
  println(s"Assigned variable X: $resultVarAssign")

  // Fuzzy Variable Fetch Test
  val fetchVar = FuzzyVar("X")
  val resultVarFetch = eval(fetchVar, commonEnv,commonEnv)
  println(s"Fetched variable X: $resultVarFetch")

  // Logic Gate Assignment Test
  val logicGateExpr = Assign(FuzzyGate("logicGate1"), FuzzyAdd(FuzzyMult(FuzzyVar("A",FuzzyVal(0.1)), FuzzyVar("B",FuzzyVal(0.2))), FuzzyVal(0.1)))
  val logicGateCreate = eval(logicGateExpr, commonEnv,commonEnv)
  commonEnv.printEnvironment()
  val logicGateResult = eval(LogicGate("logicGate1"), commonEnv,commonEnv)
  println(s"Assigned Logic Gate logicGate1: $logicGateResult")

  // Assign values to variables within the scope of the logic gate
  val scopeExprA = Scope("logicGate1", Assign(FuzzyVar("A"), FuzzyVal(0.2)))
  val scopeExprB = Scope("logicGate1", Assign(FuzzyVar("B"), FuzzyVal(0.3)))
  val scopeResultA = eval(scopeExprA, commonEnv,commonEnv)
  val scopeResultB = eval(scopeExprB, commonEnv,commonEnv)
  println(s"Scoped variable A: $scopeResultA")
  println(s"Scoped variable B: $scopeResultB")

  println(commonEnv.printEnvironment())
  // eval logic gate with scoped variables
  val evalLogicGate = eval(LogicGate("logicGate1"), commonEnv,commonEnv)

  println(s"Evaluated Logic Gate logicGate1: $evalLogicGate")

  // Test Gate Evaluation
  val testGateExpr = TestGate("logicGate1", FuzzyVal(0.5))
  val testGateResult = eval(testGateExpr, commonEnv,commonEnv)
  println(s"TestGate logicGate1 with input A=0.5: $testGateResult")

  // Composite Logic Gate Test
  val compositeGateExpr = Assign(FuzzyGate("compositeGate"), FuzzyXor(LogicGate("logicGate1"), FuzzyVar("C")))
  val compositeGateResult = eval(compositeGateExpr, commonEnv,commonEnv)
  println(s"Assigned Composite Gate compositeGate: $compositeGateResult")

  commonEnv.printEnvironment()

  val assignC = Assign(FuzzyVar("C"), FuzzyVal(0.3))
  val resultC = eval(assignC, commonEnv,commonEnv)

  val evalCompositeGate = eval(LogicGate("compositeGate"), commonEnv,commonEnv)
  println(s"Evaluated Composite Gate compositeGate: $evalCompositeGate")

  // Trying to test the composite gate without defining C
  try {
    val testCompositeGate = TestGate("compositeGate", FuzzyVal(0.2))
    val testCompositeGateResult = eval(testCompositeGate, commonEnv,commonEnv)
    println(s"TestCompositeGate result: $testCompositeGateResult")
  } catch {
    case e: Exception => println(s"Error during composite gate test: ${e.getMessage}")
  }
}

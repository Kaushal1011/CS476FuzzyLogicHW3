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

  // -----------------------
  // Partial Evaluation Tests
  // -----------------------

  // 1. Partial evaluation of FuzzyAdd with undefined variables
  println("---- Testing FuzzyAdd Partial Evaluation ----")
  val expr1 = FuzzyAdd(FuzzyVal(0.5), FuzzyVar("x"))
  val result1 = eval(expr1, commonEnv, commonEnv)
  println(s"Result of partial evaluation of FuzzyAdd: $result1")

  // Now define 'x' and re-evaluate
  commonEnv.setVariable("x", FuzzyVal(0.3))
  val result1_full = eval(expr1, commonEnv, commonEnv)
  println(s"Result after defining 'x': $result1_full\n")

  // 2. Partial evaluation of FuzzyMult with undefined variables
  println("---- Testing FuzzyMult Partial Evaluation ----")
  val expr2 = FuzzyMult(FuzzyVal(0.5), FuzzyVar("y"))
  val result2 = eval(expr2, commonEnv, commonEnv)
  println(s"Result of partial evaluation of FuzzyMult: $result2")

  // Now define 'y' and re-evaluate
  commonEnv.setVariable("y", FuzzyVal(0.6))
  val result2_full = eval(expr2, commonEnv, commonEnv)
  println(s"Result after defining 'y': $result2_full\n")

  // 3. Testing associative and commutative simplification
  println("---- Testing Associative Simplification ----")
  val expr3 = FuzzyAdd(FuzzyVal(0.2), FuzzyAdd(FuzzyVal(0.3), FuzzyVar("z")))
  val result3 = eval(expr3, commonEnv, commonEnv)
  println(s"Result of partial evaluation with associativity: $result3")

  // Now define 'z' and re-evaluate
  commonEnv.setVariable("z", FuzzyVal(0.4))
  val result3_full = eval(expr3, commonEnv, commonEnv)
  println(s"Result after defining 'z': $result3_full\n")

  // 4. Partial evaluation in logical operations
  println("---- Testing FuzzyAnd Partial Evaluation ----")
  val expr4 = FuzzyAnd(FuzzyVal(0.7), FuzzyVar("a"))
  val result4 = eval(expr4, commonEnv, commonEnv)
  println(s"Result of partial evaluation of FuzzyAnd: $result4")

  // Now define 'a' and re-evaluate
  commonEnv.setVariable("a", FuzzyVal(0.9))
  val result4_full = eval(expr4, commonEnv, commonEnv)
  println(s"Result after defining 'a': $result4_full\n")

  // 5. Partial evaluation in assignments
  println("---- Testing Assignment Partial Evaluation ----")
  val assignExpr = Assign(FuzzyVar("b"), FuzzyAdd(FuzzyVal(0.2), FuzzyVar("c")))
  val resultAssign = eval(assignExpr, commonEnv, commonEnv)
  println(s"Result of partial evaluation in assignment: $resultAssign")

  // Now define 'c' and re-evaluate 'b'
  commonEnv.setVariable("c", FuzzyVal(0.5))
  val bValue = eval(FuzzyVar("b"), commonEnv, commonEnv)
  println(s"Value of 'b' after defining 'c': $bValue\n")

  // -----------------------
  // Partial Evaluation for Let
  // -----------------------
  println("---- Testing Let Partial Evaluation ----")
  val letExpr = Let(
    List(
      Assign(FuzzyVar("d"), FuzzyAdd(FuzzyVal(0.1), FuzzyVar("e"))),
      Assign(FuzzyVar("f"), FuzzyMult(FuzzyVar("d"), FuzzyVal(2.0)))
    ),
    FuzzyAdd(FuzzyVar("f"), FuzzyVal(0.3))
  )
  val resultLet = eval(letExpr, commonEnv, commonEnv)
  println(s"Result of partial evaluation of Let: $resultLet")

  // Now define 'e' and re-evaluate
  commonEnv.setVariable("e", FuzzyVal(0.4))
  val resultLet_full = eval(letExpr, commonEnv, commonEnv)
  println(s"Result after defining 'e': $resultLet_full\n")

  // -----------------------
  // Partial Evaluation for Macro
  // -----------------------
  println("---- Testing Macro Partial Evaluation ----")

  // Define the macro and re-evaluate
  val macroDefinition = FuzzyAdd(FuzzyVal(0.2), FuzzyVar("g"))

  val macroExpr = DefineMacro(
    "myMacro",
    macroDefinition
  )

  val resultMacro_defined = eval(macroExpr, commonEnv, commonEnv)
  println(s"Result after defining Macro 'myMacro': $resultMacro_defined")

  // Define 'g' and re-evaluate
  commonEnv.setVariable("g", FuzzyVal(0.8))
  val resultMacro_full = eval(resultMacro_defined, commonEnv, commonEnv)
  println(s"Result after defining 'g': $resultMacro_full\n")

  // -----------------------
  // Partial Evaluation in FuzzyGate and TestGate
  // -----------------------
  println("---- Testing FuzzyGate and TestGate Partial Evaluation ----")
  val gateExpr = Assign(FuzzyGate("ANDGate"), FuzzyAnd(FuzzyVar("p"), FuzzyVar("q")))
  val resultGate = eval(gateExpr, commonEnv, commonEnv)
  println(s"Result of partial evaluation of FuzzyGate assignment: $resultGate")

  // Now test the gate without defining 'p' and 'q'
  val testGateExpr = TestGate("ANDGate", FuzzyVal(0.8))
  val resultTestGate = eval(testGateExpr, commonEnv, commonEnv)
  println(s"Result of partial evaluation of TestGate: $resultTestGate")

  // Define 'p' and 'q' and re-evaluate
  commonEnv.setVariable("p", FuzzyVal(0.7))
  commonEnv.setVariable("q", FuzzyVal(0.9))
  val resultTestGate_full = eval(testGateExpr, commonEnv, commonEnv)
  println(s"Result after defining 'p' and 'q': $resultTestGate_full\n")
}

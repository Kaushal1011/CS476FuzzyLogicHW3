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

  // Partial evaluation of FuzzyAdd with undefined variables
  val expr1 = FuzzyAdd(FuzzyVal(0.5), FuzzyVar("x"))
  val result1 = eval(expr1, commonEnv, commonEnv)
  println(s"Result of partial evaluation of FuzzyAdd: $result1")

  // Now define 'x' and re-evaluate
  commonEnv.setVariable("x", FuzzyVal(0.3))
  val result1_full = eval(expr1, commonEnv, commonEnv)
  println(s"Result after defining 'x': $result1_full\n")

  // Partial evaluation of FuzzyMult with undefined variables
  val expr2 = FuzzyMult(FuzzyVal(0.5), FuzzyVar("y"))
  val result2 = eval(expr2, commonEnv, commonEnv)
  println(s"Result of partial evaluation of FuzzyMult: $result2")

  // Now define 'y' and re-evaluate
  commonEnv.setVariable("y", FuzzyVal(0.6))
  val result2_full = eval(expr2, commonEnv, commonEnv)
  println(s"Result after defining 'y': $result2_full\n")

  // Testing associative and commutative simplification
  val expr3 = FuzzyAdd(FuzzyVal(0.2), FuzzyAdd(FuzzyVal(0.3), FuzzyVar("z")))
  val result3 = eval(expr3, commonEnv, commonEnv)
  println(s"Result of partial evaluation with associativity: $result3")

  // Now define 'z' and re-evaluate
  commonEnv.setVariable("z", FuzzyVal(0.4))
  val result3_full = eval(expr3, commonEnv, commonEnv)
  println(s"Result after defining 'z': $result3_full\n")

  // Partial evaluation in logical operations
  val expr4 = FuzzyAnd(FuzzyVal(0.7), FuzzyVar("a"))
  val result4 = eval(expr4, commonEnv, commonEnv)
  println(s"Result of partial evaluation of FuzzyAnd: $result4")

  // Now define 'a' and re-evaluate
  commonEnv.setVariable("a", FuzzyVal(0.9))
  val result4_full = eval(expr4, commonEnv, commonEnv)
  println(s"Result after defining 'a': $result4_full\n")

  // Partial evaluation in assignments
  val assignExpr = Assign(FuzzyVar("b"), FuzzyAdd(FuzzyVal(0.2), FuzzyVar("c")))
  val resultAssign = eval(assignExpr, commonEnv, commonEnv)
  println(s"Result of partial evaluation in assignment: $resultAssign")

  // Now define 'c' and re-evaluate 'b'
  commonEnv.setVariable("c", FuzzyVal(0.5))
  val bValue = eval(FuzzyVar("b"), commonEnv, commonEnv)
  println(s"Value of 'b' after defining 'c': $bValue\n")
}

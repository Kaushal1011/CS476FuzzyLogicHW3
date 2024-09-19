import FuzzyExpressions.FuzzyExpression._
import FuzzyExpressions.FuzzyExpression
import FuzzyEvaluator._

@main
def main(): Unit = {

  // Fuzzy Addition on Val
  val expr = FuzzyAdd(FuzzyVal(0.5), FuzzyVal(0.3))
  val result = eval(expr)
  println(result)

  // Fuzzy Addition on Set
  val expr2 = FuzzyAdd(FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))), FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.1)), ("C", FuzzyVal(0.4)))))
  val result2 = eval(expr2)
  println(result2)

  // Fuzzy Multiplication on Val
  val expr3 = FuzzyMult(FuzzyVal(0.5), FuzzyVal(0.3))
  val result3 = eval(expr3)
  println(result3)

  // Fuzzy Multiplication on Set
  val expr4 = FuzzyMult(FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))), FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.1)), ("C", FuzzyVal(0.4)))))
  val result4 = eval(expr4)
  println(result4)

  // Fuzzy Alpha Cut
  val expr5 = FuzzyAlphaCut(FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))), FuzzyVal(0.35))
  val result5 = eval(expr5)
  println(result5)
}
import org.scalatest.funsuite.AnyFunSuite
import FuzzyExpressions.FuzzyExpression._
import FuzzyEvaluator._
import EnvironmentScopes.Environment
import scala.collection.mutable

class FuzzyMathSetTests extends AnyFunSuite {

  // Test for addition on FuzzySet
  test("Fuzzy Add on FuzzySet: Should add fuzzy values in sets, cap at 1") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyAdd(
      FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))),
      FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.3)), ("C", FuzzyVal(0.4))))
    )
    val result = eval(expr, commonEnv, commonEnv)

    assert(result == FuzzySet(List(("A", FuzzyVal(0.7)), ("B", FuzzyVal(0.6)), ("C", FuzzyVal(0.4)))))
  }

  // Test for multiplication on FuzzySet
  test("Fuzzy Mult on FuzzySet: Should multiply fuzzy values in sets") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyMult(
      FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))),
      FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.6)), ("C", FuzzyVal(0.4))))
    )
    val result = eval(expr, commonEnv, commonEnv)

    assert(result == FuzzySet(List(("A", FuzzyVal(0.1)), ("B", FuzzyVal(0.18)), ("C", FuzzyVal(0.4)))))
  }

  // Test for XOR on FuzzySet
  test("Fuzzy Xor on FuzzySet: Should perform XOR on fuzzy sets (max - min)") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyXor(
      FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))),
      FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.6)), ("C", FuzzyVal(0.4))))
    )
    val result = eval(expr, commonEnv, commonEnv)

    assert(result == FuzzySet(List(("A", FuzzyVal(0.3)), ("B", FuzzyVal(0.3)), ("C", FuzzyVal(0.4)))))
  }

  // Test for AND on FuzzySet
  test("Fuzzy And on FuzzySet: Should calculate AND (minimum of values) for sets") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyAnd(
      FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))),
      FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.6)), ("C", FuzzyVal(0.4))))
    )
    val result = eval(expr, commonEnv, commonEnv)

    assert(result == FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.3)), ("C", FuzzyVal(0.4)))))
  }

  // Test for OR on FuzzySet
  test("Fuzzy Or on FuzzySet: Should calculate OR (maximum of values) for sets") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyOr(
      FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))),
      FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.6)), ("C", FuzzyVal(0.4))))
    )
    val result = eval(expr, commonEnv, commonEnv)

    assert(result == FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.6)), ("C", FuzzyVal(0.4)))))
  }

  // Test for Alpha Cut on FuzzySet
  test("Fuzzy AlphaCut on FuzzySet: Should extract elements with membership values >= alpha") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyAlphaCut(
      FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)), ("C", FuzzyVal(0.8)))),
      FuzzyVal(0.4)
    )
    val result = eval(expr, commonEnv, commonEnv)

    assert(result == FuzzySet(List(("A", FuzzyVal(0.5)), ("C", FuzzyVal(0.8)))))
  }

  // Test for Union on FuzzySet
  test("Fuzzy Union on FuzzySet: Should take the union (maximum membership) of fuzzy sets") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyUnion(
      FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))),
      FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.6)), ("C", FuzzyVal(0.4))))
    )
    val result = eval(expr, commonEnv, commonEnv)

    assert(result == FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.6)), ("C", FuzzyVal(0.4)))))
  }

  // Test for Intersection on FuzzySet
  test("Fuzzy Intersection on FuzzySet: Should take the intersection (minimum membership) of fuzzy sets") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyIntersection(
      FuzzySet(List(("A", FuzzyVal(0.5)), ("B", FuzzyVal(0.3)))),
      FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.6)), ("C", FuzzyVal(0.4))))
    )
    val result = eval(expr, commonEnv, commonEnv)

    assert(result == FuzzySet(List(("A", FuzzyVal(0.2)), ("B", FuzzyVal(0.3)), ("C", FuzzyVal(0.4)))))
  }
}

import org.scalatest.funsuite.AnyFunSuite
import FuzzyExpressions.FuzzyExpression._
import FuzzyEvaluator._
import EnvironmentScopes.Environment
import scala.collection.mutable

class FuzzyMathValTests extends AnyFunSuite {

  // Test for Fuzzy Addition on FuzzyVal
  test("Fuzzy Add on FuzzyVal: Should add values and cap at 1") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyAdd(FuzzyVal(0.5), FuzzyVal(0.3))
    val result = eval(expr, commonEnv, commonEnv)
    assert(result == FuzzyVal(0.8))

    val exprOverflow = FuzzyAdd(FuzzyVal(0.7), FuzzyVal(0.6))
    val resultOverflow = eval(exprOverflow, commonEnv, commonEnv)
    assert(resultOverflow == FuzzyVal(1.0)) // Should cap at 1
  }

  // Test for Fuzzy Multiplication on FuzzyVal
  test("Fuzzy Mult on FuzzyVal: Should multiply values correctly") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyMult(FuzzyVal(0.5), FuzzyVal(0.3))
    val result = eval(expr, commonEnv, commonEnv)
    assert(result == FuzzyVal(0.15))

    val exprZero = FuzzyMult(FuzzyVal(0.0), FuzzyVal(0.3))
    val resultZero = eval(exprZero, commonEnv, commonEnv)
    assert(resultZero == FuzzyVal(0.0)) // Multiply by 0
  }

  // Test for Fuzzy XOR on FuzzyVal
  test("Fuzzy Xor on FuzzyVal: Should calculate XOR (difference between max and min)") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyXor(FuzzyVal(0.5), FuzzyVal(0.3))
    val result = eval(expr, commonEnv, commonEnv)
    assert(result == FuzzyVal(0.2))

    val exprEqual = FuzzyXor(FuzzyVal(0.7), FuzzyVal(0.7))
    val resultEqual = eval(exprEqual, commonEnv, commonEnv)
    assert(resultEqual == FuzzyVal(0.0)) // When values are the same
  }

  // Test for Fuzzy AND on FuzzyVal
  test("Fuzzy And on FuzzyVal: Should calculate AND (minimum of values)") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyAnd(FuzzyVal(0.5), FuzzyVal(0.3))
    val result = eval(expr, commonEnv, commonEnv)
    assert(result == FuzzyVal(0.3))

    val exprSame = FuzzyAnd(FuzzyVal(0.7), FuzzyVal(0.7))
    val resultSame = eval(exprSame, commonEnv, commonEnv)
    assert(resultSame == FuzzyVal(0.7))
  }

  // Test for Fuzzy OR on FuzzyVal
  test("Fuzzy Or on FuzzyVal: Should calculate OR (maximum of values)") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyOr(FuzzyVal(0.5), FuzzyVal(0.3))
    val result = eval(expr, commonEnv, commonEnv)
    assert(result == FuzzyVal(0.5))

    val exprSame = FuzzyOr(FuzzyVal(0.7), FuzzyVal(0.7))
    val resultSame = eval(exprSame, commonEnv, commonEnv)
    assert(resultSame == FuzzyVal(0.7))
  }

  // Test for Fuzzy NOT on FuzzyVal
  test("Fuzzy Not on FuzzyVal: Should negate the value") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyNot(FuzzyVal(0.5))
    val result = eval(expr, commonEnv, commonEnv)
    assert(result == FuzzyVal(0.5))

    val exprMax = FuzzyNot(FuzzyVal(1.0))
    val resultMax = eval(exprMax, commonEnv, commonEnv)
    assert(resultMax == FuzzyVal(0.0)) // NOT of 1 should be 0

    val exprMin = FuzzyNot(FuzzyVal(0.0))
    val resultMin = eval(exprMin, commonEnv, commonEnv)
    assert(resultMin == FuzzyVal(1.0)) // NOT of 0 should be 1
  }

  // Test for Fuzzy NAND on FuzzyVal
  test("Fuzzy Nand on FuzzyVal: Should calculate NAND (NOT AND)") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyNand(FuzzyVal(0.5), FuzzyVal(0.3))
    val result = eval(expr, commonEnv, commonEnv)
    assert(result == FuzzyVal(0.7)) // NAND is NOT AND

    val exprSame = FuzzyNand(FuzzyVal(0.8), FuzzyVal(0.4))
    val resultSame = eval(exprSame, commonEnv, commonEnv)
    assert(resultSame == FuzzyVal(0.6))
  }

  // Test for Fuzzy NOR on FuzzyVal
  test("Fuzzy Nor on FuzzyVal: Should calculate NOR (NOT OR)") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr = FuzzyNor(FuzzyVal(0.5), FuzzyVal(0.3))
    val result = eval(expr, commonEnv, commonEnv)
    assert(result == FuzzyVal(0.5)) // NOR is NOT OR

    val exprSame = FuzzyNor(FuzzyVal(0.5), FuzzyVal(0.5))
    val resultSame = eval(exprSame, commonEnv, commonEnv)
    assert(resultSame == FuzzyVal(0.5)) // When values are the same
  }
}

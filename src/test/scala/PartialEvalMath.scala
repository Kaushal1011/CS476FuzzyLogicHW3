import org.scalatest.funsuite.AnyFunSuite
import FuzzyExpressions.FuzzyExpression.{ParamType, *}
import FuzzyEvaluator.*
import EnvironmentScopes.Environment

import scala.collection.immutable.List
import scala.collection.mutable

class PartialEvalMath extends AnyFunSuite {

  test("Basic Partial Evaluation for Math Add") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
//    println("---- Testing FuzzyAdd Partial Evaluation ----")
    val expr1 = FuzzyAdd(FuzzyAdd(FuzzyVal(0.5), FuzzyVal(0.1)), FuzzyVar("x"))
    val result1 = eval(expr1, commonEnv, commonEnv)
//    println(s"Result of partial evaluation of FuzzyAdd: $result1")

    assert(result1 == FuzzyAdd(FuzzyVal(0.6),FuzzyVar("x")))

    commonEnv.setVariable("x", FuzzyVal(0.3))
    val result1_full = eval(expr1, commonEnv, commonEnv)
//    println(s"Result after defining 'x': $result1_full\n")

    assert(result1_full == FuzzyVal(0.8999999999999999))
  }

  test("Basic Partial Evaluation for Math Multiplication"){
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr2 = FuzzyMult(FuzzyVal(0.5), FuzzyVar("y"))
    val result2 = eval(expr2, commonEnv, commonEnv)
//    println(s"Result of partial evaluation of FuzzyMult: $result2")

    assert(result2 == FuzzyMult(FuzzyVal(0.5),FuzzyVar("y")))

    // Now define 'y' and re-evaluate
    commonEnv.setVariable("y", FuzzyVal(0.6))
    val result2_full = eval(expr2, commonEnv, commonEnv)
//    println(s"Result after defining 'y': $result2_full\n")

    assert(result2_full == FuzzyVal(0.3))
  }

  test("Associative and Commutative Property of Math Ops: Add "){
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr3 = FuzzyAdd(FuzzyVal(0.2), FuzzyAdd(FuzzyVal(0.3), FuzzyVar("z")))
    val result3 = eval(expr3, commonEnv, commonEnv)
//    println(s"Result of partial evaluation with associativity: $result3")
    assert(result3 == FuzzyAdd(FuzzyVal(0.5),FuzzyVar("z")))
    // Now define 'z' and re-evaluate
    commonEnv.setVariable("z", FuzzyVal(0.4))
    val result3_full = eval(expr3, commonEnv, commonEnv)
//    println(s"Result after defining 'z': $result3_full\n")
    assert(result3_full == FuzzyVal(0.8999999999999999))

  }

  // Additional tests for addition
  test("Nested Additions with Partial Evaluation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr1 = FuzzyAdd(FuzzyAdd(FuzzyVal(0.2), FuzzyVar("a")), FuzzyVar("b"))
    val result1 = eval(expr1, commonEnv, commonEnv)

    assert(result1 == FuzzyAdd(FuzzyAdd(FuzzyVal(0.2), FuzzyVar("a")), FuzzyVar("b")))

    commonEnv.setVariable("a", FuzzyVal(0.3))
    val result2 = eval(expr1, commonEnv, commonEnv)
    assert(result2 == FuzzyAdd(FuzzyVal(0.5), FuzzyVar("b")))

    commonEnv.setVariable("b", FuzzyVal(0.4))
    val result3 = eval(expr1, commonEnv, commonEnv)
    assert(result3 == FuzzyVal(0.9))
  }

  // Additional tests for multiplication
  test("Complex Multiplication with Variables") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr1 = FuzzyMult(FuzzyMult(FuzzyVal(0.5), FuzzyVar("x")), FuzzyVar("y"))
    val result1 = eval(expr1, commonEnv, commonEnv)

    assert(result1 == FuzzyMult(FuzzyMult(FuzzyVal(0.5), FuzzyVar("x")), FuzzyVar("y")))

    commonEnv.setVariable("x", FuzzyVal(0.8))
    val result2 = eval(expr1, commonEnv, commonEnv)
    assert(result2 == FuzzyMult(FuzzyVal(0.4), FuzzyVar("y")))

    commonEnv.setVariable("y", FuzzyVal(0.6))
    val result3 = eval(expr1, commonEnv, commonEnv)
    assert(result3 == FuzzyVal(0.24))
  }

  // Logical AND tests
  test("Basic AND Operation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr1 = FuzzyAnd(FuzzyVal(0.7), FuzzyVar("x"))
    val result1 = eval(expr1, commonEnv, commonEnv)

    assert(result1 == FuzzyAnd(FuzzyVal(0.7), FuzzyVar("x")))

    commonEnv.setVariable("x", FuzzyVal(0.9))
    val result2 = eval(expr1, commonEnv, commonEnv)
    assert(result2 == FuzzyVal(0.7))
  }

  test("Nested AND Operations") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr1 = FuzzyAnd(FuzzyAnd(FuzzyVal(0.5), FuzzyVar("x")), FuzzyVar("y"))
    val result1 = eval(expr1, commonEnv, commonEnv)

    assert(result1 == FuzzyAnd(FuzzyAnd(FuzzyVal(0.5), FuzzyVar("x")), FuzzyVar("y")))

    commonEnv.setVariable("x", FuzzyVal(0.4))
    val result2 = eval(expr1, commonEnv, commonEnv)
    assert(result2 == FuzzyAnd(FuzzyVal(0.4), FuzzyVar("y")))

    commonEnv.setVariable("y", FuzzyVal(0.6))
    val result3 = eval(expr1, commonEnv, commonEnv)
    assert(result3 == FuzzyVal(0.4))
  }

  // Logical OR tests
  test("Basic OR Operation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr1 = FuzzyOr(FuzzyVal(0.3), FuzzyVar("x"))
    val result1 = eval(expr1, commonEnv, commonEnv)

    assert(result1 == FuzzyOr(FuzzyVal(0.3), FuzzyVar("x")))

    commonEnv.setVariable("x", FuzzyVal(0.8))
    val result2 = eval(expr1, commonEnv, commonEnv)
    assert(result2 == FuzzyVal(0.8))
  }

  test("Nested OR Operations") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr1 = FuzzyOr(FuzzyOr(FuzzyVal(0.2), FuzzyVar("x")), FuzzyVar("y"))
    val result1 = eval(expr1, commonEnv, commonEnv)

    assert(result1 == FuzzyOr(FuzzyOr(FuzzyVal(0.2), FuzzyVar("x")), FuzzyVar("y")))

    commonEnv.setVariable("x", FuzzyVal(0.5))
    val result2 = eval(expr1, commonEnv, commonEnv)
    assert(result2 == FuzzyOr(FuzzyVal(0.5), FuzzyVar("y")))

    commonEnv.setVariable("y", FuzzyVal(0.7))
    val result3 = eval(expr1, commonEnv, commonEnv)
    assert(result3 == FuzzyVal(0.7))
  }

  // Logical NOT tests
  test("Basic NOT Operation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr1 = FuzzyNot(FuzzyVar("x"))
    val result1 = eval(expr1, commonEnv, commonEnv)

    assert(result1 == FuzzyNot(FuzzyVar("x")))

    commonEnv.setVariable("x", FuzzyVal(0.6))
    val result2 = eval(expr1, commonEnv, commonEnv)
    assert(result2 == FuzzyVal(0.4))
  }

  test("Nested NOT Operations") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val expr1 = FuzzyNot(FuzzyNot(FuzzyVar("x")))
    val result1 = eval(expr1, commonEnv, commonEnv)

    assert(result1 == FuzzyNot(FuzzyNot(FuzzyVar("x"))))

    commonEnv.setVariable("x", FuzzyVal(0.7))
    val result2 = eval(expr1, commonEnv, commonEnv)
    assert(result2 == FuzzyVal(0.7))
  }

}

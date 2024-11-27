import org.scalatest.funsuite.AnyFunSuite
import FuzzyExpressions.FuzzyExpression._
import FuzzyEvaluator._
import EnvironmentScopes.Environment
import scala.collection.mutable

class PartialEvalIfTrue extends AnyFunSuite {

  test("Condition partial evaluation, Then branch full evaluation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    val ifTrueExpr = IfTrue(
      condition = FuzzyAdd(FuzzyVal(0.5), FuzzyVar("condVar")), // Partially evaluated condition
      thenBranch = ThenExecute(List(
        Assign(FuzzyVar("x"), FuzzyVal(10))
      )),
      elseBranch = ElseRun(List(
        Assign(FuzzyVar("x"), FuzzyVal(0))
      ))
    )

    val resultPartialCondition = eval(ifTrueExpr, commonEnv, commonEnv)
    assert(resultPartialCondition == IfTrue(
      condition = FuzzyAdd(FuzzyVal(0.5), FuzzyVar("condVar")),
      thenBranch = ThenExecute(List(
        Assign(FuzzyVar("x"), FuzzyVal(10))
      )),
      elseBranch = ElseRun(List(
        Assign(FuzzyVar("x"), FuzzyVal(0))
      ))
    ))

    // Define `condVar` to fully evaluate condition
    commonEnv.setVariable("condVar", FuzzyVal(0.5))
    val resultConditionFull = eval(ifTrueExpr, commonEnv, commonEnv)
    assert(resultConditionFull == FuzzyVal(1.0)) // Then branch is executed
    assert(commonEnv.lookup("x") == Some(FuzzyVal(10)))
  }

  test("Condition full evaluation, Then branch partial evaluation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    val ifTrueExpr = IfTrue(
      condition = FuzzyVal(1.0), // Fully evaluated condition
      thenBranch = ThenExecute(List(
        Assign(FuzzyVar("y"), FuzzyVar("undefinedVar")) // Partially evaluated
      )),
      elseBranch = ElseRun(List(
        Assign(FuzzyVar("y"), FuzzyVal(0))
      ))
    )

    val resultPartialThenBranch = eval(ifTrueExpr, commonEnv, commonEnv)
    assert(resultPartialThenBranch == IfTrue(
      condition = FuzzyVal(1.0),
      thenBranch = ThenExecute(List(
        FuzzyVar("undefinedVar") // Partially evaluated
      )),
      elseBranch = ElseRun(List(
        Assign(FuzzyVar("y"), FuzzyVal(0))
      ))
    ))

    // Define `undefinedVar` to fully evaluate Then branch
    commonEnv.setVariable("undefinedVar", FuzzyVal(20))
    val resultThenBranchFull = eval(resultPartialThenBranch, commonEnv, commonEnv)
    assert(resultThenBranchFull == FuzzyVal(1.0)) // Then branch is now fully evaluated
    assert(commonEnv.lookup("y") == Some(FuzzyVar("undefinedVar")))
    assert(commonEnv.lookup("undefinedVar") == Some(FuzzyVal(20)))

  }

  test("Condition full evaluation, Else branch partial evaluation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    val ifTrueExpr = IfTrue(
      condition = FuzzyVal(0.0), // Fully evaluated false condition
      thenBranch = ThenExecute(List(
        Assign(FuzzyVar("z"), FuzzyVal(10))
      )),
      elseBranch = ElseRun(List(
        Assign(FuzzyVar("z"), FuzzyVar("undefinedVar")) // Partially evaluated
      ))
    )

    val resultPartialElseBranch = eval(ifTrueExpr, commonEnv, commonEnv)
    assert(resultPartialElseBranch == IfTrue(
      condition = FuzzyVal(0.0),
      thenBranch = ThenExecute(List(
        Assign(FuzzyVar("z"), FuzzyVal(10))
      )),
      elseBranch = ElseRun(List(
        FuzzyVar("undefinedVar")
      ))
    ))

    // Define `undefinedVar` to fully evaluate Else branch
    commonEnv.setVariable("undefinedVar", FuzzyVal(5))
    val resultElseBranchFull = eval(resultPartialElseBranch, commonEnv, commonEnv)
    assert(resultElseBranchFull == FuzzyVal(1.0)) // Else branch is now fully evaluated
    assert(commonEnv.lookup("z") == Some(FuzzyVar("undefinedVar")))
    assert(commonEnv.lookup("undefinedVar") == Some(FuzzyVal(5)))
  }

  test("Condition partial evaluation, Then branch partial evaluation, full evaluation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    val ifTrueExpr = IfTrue(
      condition = FuzzyAdd(FuzzyVal(0.3), FuzzyVar("condVar")), // Partially evaluated condition
      thenBranch = ThenExecute(List(
        Assign(FuzzyVar("result"), FuzzyAdd(FuzzyVar("a"), FuzzyVar("b"))) // Partially evaluated
      )),
      elseBranch = ElseRun(List(
        Assign(FuzzyVar("result"), FuzzyVal(0))
      ))
    )

    val resultPartial = eval(ifTrueExpr, commonEnv, commonEnv)
    assert(resultPartial == IfTrue(
      condition = FuzzyAdd(FuzzyVal(0.3), FuzzyVar("condVar")),
      thenBranch = ThenExecute(List(
        Assign(FuzzyVar("result"), FuzzyAdd(FuzzyVar("a"), FuzzyVar("b")))
      )),
      elseBranch = ElseRun(List(
        Assign(FuzzyVar("result"), FuzzyVal(0))
      ))
    ))

    // Define `condVar`, `a`, and `b` to fully evaluate
    commonEnv.setVariable("condVar", FuzzyVal(0.7)) // Condition resolves to FuzzyVal(1.0)
    commonEnv.setVariable("a", FuzzyVal(2))
    commonEnv.setVariable("b", FuzzyVal(3))
    val resultFull = eval(ifTrueExpr, commonEnv, commonEnv)
    assert(resultFull == FuzzyVal(1.0)) // Then branch is fully evaluated
    assert(commonEnv.lookup("result") == Some(FuzzyVal(1.0))) // Add maxes out at 1.0
  }

  test("Condition partial evaluation, Else branch partial evaluation, full evaluation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    val ifTrueExpr = IfTrue(
      condition = FuzzyAdd(FuzzyVal(0.2), FuzzyVar("condVar")), // Partially evaluated condition
      thenBranch = ThenExecute(List(
        Assign(FuzzyVar("result"), FuzzyVal(1))
      )),
      elseBranch = ElseRun(List(
        Assign(FuzzyVar("result"), FuzzyAdd(FuzzyVar("x"), FuzzyVar("y"))) // Partially evaluated
      ))
    )

    val resultPartial = eval(ifTrueExpr, commonEnv, commonEnv)
    assert(resultPartial == IfTrue(
      condition = FuzzyAdd(FuzzyVal(0.2), FuzzyVar("condVar")),
      thenBranch = ThenExecute(List(
        Assign(FuzzyVar("result"), FuzzyVal(1))
      )),
      elseBranch = ElseRun(List(
        Assign(FuzzyVar("result"), FuzzyAdd(FuzzyVar("x"), FuzzyVar("y")))
      ))
    ))

    // Define `condVar`, `x`, and `y` to fully evaluate
    commonEnv.setVariable("condVar", FuzzyVal(0.3)) // Condition resolves to FuzzyVal(0.5)
    commonEnv.setVariable("x", FuzzyVal(4))
    commonEnv.setVariable("y", FuzzyVal(6))
    val resultFull = eval(ifTrueExpr, commonEnv, commonEnv)
    assert(resultFull == FuzzyVal(1.0)) // Else branch is fully evaluated
    assert(commonEnv.lookup("result") == Some(FuzzyVal(1.0))) // 4 + 6 = 10 // Add maxes out at 1.0
  }
}

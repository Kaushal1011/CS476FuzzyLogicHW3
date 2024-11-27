import org.scalatest.funsuite.AnyFunSuite
import FuzzyExpressions.FuzzyExpression._
import FuzzyEvaluator._
import EnvironmentScopes.Environment
import scala.collection.mutable

class PartialEvalLetMacro extends AnyFunSuite {

  // -----------------------
  // Partial Evaluation for Let
  // -----------------------

  test("Partial Evaluation for Let with Undefined Variables") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    val letExpr = Let(
      List(
        Assign(FuzzyVar("d"), FuzzyAdd(FuzzyVal(0.1), FuzzyVar("e"))),
        Assign(FuzzyVar("f"), FuzzyMult(FuzzyVar("d"), FuzzyVal(0.3)))
      ),
      FuzzyAdd(FuzzyVar("f"), FuzzyVal(0.3))
    )

    val resultLet = eval(letExpr, commonEnv, commonEnv)
    assert(resultLet == FuzzyAdd(FuzzyMult(FuzzyAdd(FuzzyVal(0.1), FuzzyVar("e")), FuzzyVal(0.3)), FuzzyVal(0.3)))

    commonEnv.setVariable("e", FuzzyVal(0.4))
    val resultLetFull = eval(letExpr, commonEnv, commonEnv)
    assert(resultLetFull == FuzzyVal(0.44999999999999996))
  }

  test("Let with Nested Assignments") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    val letExpr = Let(
      List(
        Assign(FuzzyVar("a"), FuzzyVal(0.2)),
        Assign(FuzzyVar("b"), FuzzyAdd(FuzzyVar("a"), FuzzyVar("c")))
      ),
      FuzzyMult(FuzzyVar("b"), FuzzyVal(0.5))
    )

    val resultLet = eval(letExpr, commonEnv, commonEnv)
    assert(resultLet == FuzzyMult(FuzzyAdd(FuzzyVal(0.2), FuzzyVar("c")), FuzzyVal(0.5)))

    commonEnv.setVariable("c", FuzzyVal(0.3))
    val resultLetFull = eval(letExpr, commonEnv, commonEnv)
    assert(resultLetFull == FuzzyVal(0.25))
  }

  // -----------------------
  // Partial Evaluation for Macro
  // -----------------------

  test("Partial Evaluation for Macro Definition and Use") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    val macroDefinition = FuzzyAdd(FuzzyVal(0.2), FuzzyVar("g"))
    val macroExpr = DefineMacro("myMacro", macroDefinition)
    val resultMacroDefined = eval(macroExpr, commonEnv, commonEnv)
    assert(resultMacroDefined == FuzzyAdd(FuzzyVal(0.2), FuzzyVar("g")))

    val macroUsage = Macro("myMacro")
    val resultMacroUsage = eval(macroUsage, commonEnv, commonEnv)
    assert(resultMacroUsage == FuzzyAdd(FuzzyVal(0.2), FuzzyVar("g")))

    commonEnv.setVariable("g", FuzzyVal(0.8))
    val resultMacroFull = eval(macroUsage, commonEnv, commonEnv)
    assert(resultMacroFull == FuzzyVal(1.0))
  }

  test("Nested Macros with Partial Evaluation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    val macro1Definition = FuzzyAdd(FuzzyVar("x"), FuzzyVal(0.1))
    val macro1Expr = DefineMacro("macro1", macro1Definition)
    eval(macro1Expr, commonEnv, commonEnv)

    val macro2Definition = FuzzyMult(Macro("macro1"), FuzzyVar("y"))
    val macro2Expr = DefineMacro("macro2", macro2Definition)
    eval(macro2Expr, commonEnv, commonEnv)

    val macroUsage = Macro("macro2")
    val resultMacroUsage = eval(macroUsage, commonEnv, commonEnv)
    assert(resultMacroUsage == FuzzyMult(FuzzyAdd(FuzzyVar("x"), FuzzyVal(0.1)), FuzzyVar("y")))

    commonEnv.setVariable("x", FuzzyVal(0.5))
    val resultPartial = eval(macroUsage, commonEnv, commonEnv)
    assert(resultPartial == FuzzyMult(FuzzyVal(0.6), FuzzyVar("y")))

    commonEnv.setVariable("y", FuzzyVal(0.4))
    val resultFull = eval(macroUsage, commonEnv, commonEnv)
    assert(resultFull == FuzzyVal(0.24))
  }

  test("Let with Macro Reference and Partial Evaluation") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Define a macro
    val macroDefinition = FuzzyAdd(FuzzyVal(0.3), FuzzyVar("a"))
    val macroExpr = DefineMacro("macro1", macroDefinition)
    eval(macroExpr, commonEnv, commonEnv)

    // Use the macro inside a Let expression
    val letExpr = Let(
      List(
        Assign(FuzzyVar("b"), Macro("macro1")),
        Assign(FuzzyVar("c"), FuzzyMult(FuzzyVar("b"), FuzzyVar("d")))
      ),
      FuzzyAdd(FuzzyVar("c"), FuzzyVal(0.1))
    )

    // Partial evaluation without defining `a` and `d`
    val resultPartial = eval(letExpr, commonEnv, commonEnv)
    assert(resultPartial == FuzzyAdd(FuzzyMult(FuzzyAdd(FuzzyVal(0.3), FuzzyVar("a")), FuzzyVar("d")), FuzzyVal(0.1)))

    // Define `a` and `d`, then evaluate fully
    commonEnv.setVariable("a", FuzzyVal(0.2))
    commonEnv.setVariable("d", FuzzyVal(0.5))
    val resultFull = eval(letExpr, commonEnv, commonEnv)
    assert(resultFull == FuzzyVal(0.35))
  }

}

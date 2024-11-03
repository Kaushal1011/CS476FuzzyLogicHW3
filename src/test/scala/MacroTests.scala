import org.scalatest.funsuite.AnyFunSuite
import FuzzyExpressions.FuzzyExpression._
import FuzzyEvaluator._
import EnvironmentScopes.Environment
import scala.collection.mutable

class MacroTests extends AnyFunSuite {

  test("Macro Definition: Define a simple macro with fuzzy addition") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val defineMacroExpr = DefineMacro("macroExample", FuzzyAdd(FuzzyVal(0.3), FuzzyVal(0.2)))
    eval(defineMacroExpr, commonEnv, commonEnv)

  }

  test("Macro Usage: Use a defined macro in a fuzzy expression") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val defineMacroExpr = DefineMacro("macroExample", FuzzyAdd(FuzzyVal(0.3), FuzzyVal(0.2)))
    eval(defineMacroExpr, commonEnv, commonEnv)

    val useMacroExpr = FuzzyAdd(Macro("macroExample"), FuzzyVal(0.5))
    val result = eval(useMacroExpr, commonEnv, commonEnv)

    assert(result == FuzzyVal(1.0)) // 0.3 + 0.2 + 0.5 = 1.0, capped at 1
  }

  test("Macro Re-evaluation: Ensure macro yields consistent results") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)
    val defineMacroExpr = DefineMacro("macroExample", FuzzyAdd(FuzzyVal(0.4), FuzzyVal(0.2)))
    eval(defineMacroExpr, commonEnv, commonEnv)

    val firstUse = FuzzyAdd(Macro("macroExample"), FuzzyVal(0.2))
    val firstResult = eval(firstUse, commonEnv, commonEnv)
    assert(firstResult.asInstanceOf[FuzzyVal].i > 0.7)

    val secondUse = FuzzyAdd(Macro("macroExample"), FuzzyVal(0.1))
    val secondResult = eval(secondUse, commonEnv, commonEnv)
    assert(secondResult.asInstanceOf[FuzzyVal].i > 0.6)

  }

  test("Nested Macro Test: Use a macro inside another macro") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    // Define base macros
    val defineMacro1 = DefineMacro("macroA", FuzzyAdd(FuzzyVal(0.2), FuzzyVal(0.3)))
    eval(defineMacro1, commonEnv, commonEnv)

    val defineMacro2 = DefineMacro("macroB", FuzzyAdd(Macro("macroA"), FuzzyVal(0.1)))
    eval(defineMacro2, commonEnv, commonEnv)

    // Use the nested macro
    val result = eval(Macro("macroB"), commonEnv, commonEnv)

    assert(result == FuzzyVal(0.6)) // 0.2 + 0.3 + 0.1 = 0.6
  }


  test("Undefined Macro Usage: Attempt to use an undefined macro") {
    val commonEnv = new Environment(Some("GlobalScope"), mutable.Map.empty)

    val useUndefinedMacro = Macro("nonExistentMacro")

    intercept[Exception] {
      eval(useUndefinedMacro, commonEnv, commonEnv)
    }
  }
}

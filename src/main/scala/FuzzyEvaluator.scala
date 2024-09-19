import FuzzyExpressions.FuzzyExpression._
import FuzzyExpressions.FuzzyExpression
import FuzzyMath._

object FuzzyEvaluator:

  def eval(expr: FuzzyExpression, env: Map[String, Map[String, FuzzyExpression]] = Map(), scope: String = ""): FuzzyExpression =
    expr match
      case FuzzyVal(i) => FuzzyVal(i)
      case FuzzySet(elems) => FuzzySet(elems)
      case FuzzyVar(v) =>
        v match
          case (name, value) =>
            // Check scope starting from current to parent if not defined return eval(value)
            throw new Exception("Not Implemented")
          case (name) =>
            // Check scope starting from current to parent if not defined throw exception
            throw new Exception("Not Implemented")

      case FuzzyAdd(x1, x2) => Add(eval(x1, env, scope), eval(x2, env, scope))
      case FuzzyMult(x1, x2) => Mult(eval(x1, env, scope), eval(x2, env, scope))
      case FuzzyNot(x) => Not(eval(x, env, scope))

      case FuzzyAnd(x1, x2) => And(eval(x1, env, scope), eval(x2, env, scope))
      case FuzzyOr(x1, x2) => Or(eval(x1, env, scope), eval(x2, env, scope))
      case FuzzyXor(x1, x2) => Xor(eval(x1, env, scope), eval(x2, env, scope))
      case FuzzyNand(x1, x2) => Nand(eval(x1, env, scope), eval(x2, env, scope))
      case FuzzyNor(x1, x2) => Nor(eval(x1, env, scope), eval(x2, env, scope))

      case FuzzyAlphaCut(set, cut) => AlphaCut(eval(set, env, scope).asInstanceOf[FuzzySet], cut.asInstanceOf[FuzzyVal])
      case FuzzyUnion(set1, set2) => Union(eval(set1, env, scope).asInstanceOf[FuzzySet], eval(set2, env, scope).asInstanceOf[FuzzySet])
      case FuzzyIntersection(set1, set2) => Intersection(eval(set1, env, scope).asInstanceOf[FuzzySet], eval(set2, env, scope).asInstanceOf[FuzzySet])

      case Assign(s, e) => throw new Exception("Not Implemented")

      case Scope(s, e) => throw new Exception("Not Implemented")

      case FuzzyGate(s) => throw new Exception("Not Implemented")

      case TestGate(s, e) => throw new Exception("Not Implemented")

      case _ => throw new Exception("Invalid Fuzzy Expression")

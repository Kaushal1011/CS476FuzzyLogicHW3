import FuzzyExpressions.FuzzyExpression._
import FuzzyExpressions.FuzzyExpression
import FuzzyMath._
import EnvironmentScopes.Environment

object FuzzyEvaluator:

  def eval(expr: FuzzyExpression, env: Environment, root: Environment): FuzzyExpression =
    expr match
      case FuzzyVal(i) => FuzzyVal(i) // Evaluates to itself
      case FuzzySet(elems) => FuzzySet(elems) // Evaluates to itself

      // Evaluate FuzzyVar, lookup from the environment or throw an error if not found
      case FuzzyVar(v) =>
        v match
          case (name: String, value: FuzzyExpression) =>
            env.lookup(name) match
              case Some(foundVar) => eval(foundVar, env, root) // Evaluate the found variable
              case None => eval(value, env, root) // Default to evaluating the value if not found
          case name: String =>
            env.lookup(name) match
              case Some(foundVar) => eval(foundVar, env, root) // Evaluate the found variable
              case None => throw new Exception(s"Variable $name not defined in scope ${env.name}")

      // Arithmetic operations
      case FuzzyAdd(x1, x2) => Add(eval(x1, env, root), eval(x2, env, root))
      case FuzzyMult(x1, x2) => Mult(eval(x1, env, root), eval(x2, env, root))
      case FuzzyNot(x) => Not(eval(x, env, root))

      // Logic operations
      case FuzzyAnd(x1, x2) => And(eval(x1, env, root), eval(x2, env, root))
      case FuzzyOr(x1, x2) => Or(eval(x1, env,root), eval(x2, env, root))
      case FuzzyXor(x1, x2) => Xor(eval(x1, env,root), eval(x2, env,root))
      case FuzzyNand(x1, x2) => Nand(eval(x1, env,root), eval(x2, env,root))
      case FuzzyNor(x1, x2) => Nor(eval(x1, env,root), eval(x2, env,root))

      // Set operations
      case FuzzyAlphaCut(set, cut) =>
        AlphaCut(eval(set, env,root).asInstanceOf[FuzzySet], eval(cut, env,root).asInstanceOf[FuzzyVal])
      case FuzzyUnion(set1, set2) =>
        Union(eval(set1, env,root).asInstanceOf[FuzzySet], eval(set2, env,root).asInstanceOf[FuzzySet])
      case FuzzyIntersection(set1, set2) =>
        Intersection(eval(set1, env,root).asInstanceOf[FuzzySet], eval(set2, env,root).asInstanceOf[FuzzySet])

      // Handle Assign to update environment with variables or gates
      case Assign(FuzzyGate(gateName), expr) =>
        env.setVariable(gateName, expr) // Store gate in parent environment so that composition can be done
        // Create a new scope for the gate if it doesn't exist
        val ns = env.getOrCreateChild(Some(gateName))
        ns.setVariable(gateName, expr) // Store gate in environment sort of identity if needed
        expr

      case Assign(FuzzyVar((name, value)), expr) =>
        val evaluatedExpr = eval(expr, env,root)
        env.setVariable(name, evaluatedExpr) // Set variable in current environment
        evaluatedExpr

      case Assign(FuzzyVar(name: String), expr) =>
        val evaluatedExpr = eval(expr, env,root)
        env.setVariable(name, evaluatedExpr) // Set variable in current environment
        evaluatedExpr

      // Handle Scopes, creating a new environment for the scope
      case Scope(s, e) =>
        env.findScope(s) match
          case Some(scopeEnv) => eval(e, scopeEnv,root)
          case None =>
            val newScope = env.createChild(Some(s))
            eval(e, newScope,root)

      // Handle FuzzyGate (storing a gate in the environment)
      case FuzzyGate(gateName) =>
        env.lookup(gateName) match
          case Some(gate) => gate // Return the found gate
          case None => throw new Exception(s"Gate $gateName not defined")

      // Handle LogicGate, fetch the expression of the gate and evaluate it
      case LogicGate(gateName) =>
        val lgScope = root.findScope(gateName).getOrElse(throw new Exception(s"Scope $gateName not found"))
        env.lookup(gateName) match
          case Some(expr) => eval(expr, lgScope,root)
          case _ => throw new Exception(s"Logic gate $gateName not defined")

      // Handle TestGate to evaluate a gate with specific inputs
      case TestGate(gateName, input) =>
        val gateEnv = env.findScope(gateName).getOrElse(throw new Exception(s"Scope $gateName not found"))
        gateEnv.lookup(gateName) match
          case Some(expr) =>
            val x: FuzzyExpression = eval(expr, gateEnv,root)
            val y: FuzzyExpression = eval(input, env,root)
            eval(FuzzyAnd(x, y), env,root) // Evaluate the gate with the input
          case _ => throw new Exception(s"Logic gate $gateName not defined")

      // Default case for invalid expressions
      case _ => throw new Exception("Invalid Fuzzy Expression")

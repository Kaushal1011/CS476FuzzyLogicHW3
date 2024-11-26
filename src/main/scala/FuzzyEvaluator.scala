import FuzzyExpressions.FuzzyExpression.{NonFuzzyOperation, *}
import FuzzyExpressions.FuzzyExpression
import FuzzyMath.*
import EnvironmentScopes.Environment

import scala.collection.mutable

object FuzzyEvaluator:

  private def isFullyEvaluated(expr: FuzzyExpression): Boolean = expr match {
    case FuzzyVal(_) => true
    case NonFuzzyType(_) => true
    case FuzzySet(elems) => elems.forall { case (_, v) => isFullyEvaluated(v) }
    case ThenExecute(exprs) => exprs.forall(isFullyEvaluated)
    case ElseRun(exprs) => exprs.forall(isFullyEvaluated)
    case _ => false
  }


//  Forward pass for eval checking partial eval and error conditions through comment labels
  def eval(expr: FuzzyExpression, env: Environment, root: Environment): FuzzyExpression =
    expr match
//  FPPE:    Partially Safe, No error conditions
      case FuzzyVal(i) => FuzzyVal(i) // Evaluates to itself
      case FuzzySet(elems) => FuzzySet(elems) // Evaluates to itself

      // Evaluate FuzzyVar, lookup from the environment or throw an error if not found
//    FPPE: Partially Safe (in case of var not found evaluates without it), Error condition: Variable not found
      case FuzzyVar(v) =>
        v match
          case (name: String, value: FuzzyExpression) =>
            env.lookup(name) match
              case Some(foundVar) => eval(foundVar, env, root) // Evaluate the found variable
              case None =>
                println(s"Warning: Variable $name not defined in scope ${env.name}")
                eval(value, env, root) // Default to evaluating the value if not found

          case name: String =>
            env.lookup(name) match
              case Some(foundVar) => eval(foundVar, env, root) // Evaluate the found variable
              case None =>
                println(s"Warning: Variable $name not defined in scope ${env.name}")
                FuzzyVar(name) // Return the variable name if not found for later evaluation
//                throw new Exception(s"Variable $name not defined in scope ${env.name}")

      // NonFuzzyVar logic: Fetch value from environment or error if not found
//      FPPE: Partially Safe (in case of var not found evaluates without it), Error condition: Variable not found
      case NonFuzzyVar(v) =>
        v match
          case (name: String, value: NonFuzzyType[?]) =>
            env.lookup(name) match
              case Some(foundVar) => eval(foundVar, env, root) // Evaluate the found variable
              case None =>
                println(s"Warning: Non-fuzzy variable $name not defined in scope ${env.name}")
                NonFuzzyType(value) // Return the non-fuzzy value if not found
          case name: String =>
            env.lookup(name) match
              case Some(foundVar) => eval(foundVar, env, root) // Evaluate the found variable
              case None =>
                println(s"Warning: Non-fuzzy variable $name not defined in scope ${env.name}")
                NonFuzzyVar(name) // Return the variable name if not found for later evaluation
//                throw new Exception(s"Non-fuzzy variable $name not defined in scope ${env.name}")

      // Arithmetic operations for Fuzzy types
//      FPPE: Partially Safe, Supports Associativity and Commutativity, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case FuzzyAdd(x1, x2) =>
        val left = eval(x1, env, root)
        val right = eval(x2, env, root)
        (left, right) match
          case (FuzzyVal(i1), FuzzyVal(i2)) => Add(FuzzyVal(i1), FuzzyVal(i2))
          case (FuzzySet(elems1), FuzzySet(elems2)) => Add(FuzzySet(elems1), FuzzySet(elems2))
          case (FuzzyVal(0.0), r) => r // Identity for addition
          case (l, FuzzyVal(0.0)) => l
          // Associativity and Commutativity: Group constants
          case (FuzzyVal(i1), FuzzyAdd(FuzzyVal(i2), r)) =>  FuzzyAdd(Add(FuzzyVal(i1), FuzzyVal(i2)), r)
          case (FuzzyVal(i1), FuzzyAdd(l,FuzzyVal(i2))) =>  FuzzyAdd(l,Add(FuzzyVal(i1), FuzzyVal(i2)))
          case (FuzzyAdd(FuzzyVal(i1), r), FuzzyVal(i2)) =>  FuzzyAdd(Add(FuzzyVal(i1), FuzzyVal(i2)),r)
          case (FuzzyAdd(l, FuzzyVal(i1)), FuzzyVal(i2)) =>  FuzzyAdd(l,Add(FuzzyVal(i1), FuzzyVal(i2)))
          case (FuzzySet(elems1), FuzzyAdd(FuzzySet(elems2), r)) =>  FuzzyAdd(Add(FuzzySet(elems1), FuzzySet(elems2)), r)
          case (FuzzySet(elems1), FuzzyAdd(l,FuzzySet(elems2))) =>  FuzzyAdd(l,Add(FuzzySet(elems1), FuzzySet(elems2)))
          case (FuzzyAdd(FuzzySet(elems1), r), FuzzySet(elems2)) =>  FuzzyAdd(Add(FuzzySet(elems1), FuzzySet(elems2)),r)
          case (FuzzyAdd(l, FuzzySet(elems1)), FuzzySet(elems2)) =>  FuzzyAdd(l,Add(FuzzySet(elems1), FuzzySet(elems2)))
          //          case (FuzzyVal(i1), FuzzyAdd(l,FuzzyVal(i2))) =>  FuzzyAdd(l,Add(FuzzyVal(i1), FuzzyVal(i2)))
          case _ => FuzzyAdd(left, right)

//      FPPE: Partially Safe, Supports Associativity and Commutativity, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case FuzzyMult(x1, x2) =>
        val left = eval(x1, env, root)
        val right = eval(x2, env, root)
        (left, right) match
          case (FuzzyVal(i1), FuzzyVal(i2)) => Mult(FuzzyVal(i1), FuzzyVal(i2))
          case (FuzzySet(elems1), FuzzySet(elems2)) => Mult(FuzzySet(elems1), FuzzySet(elems2))
          case (FuzzyVal(1.0), r) => r // Identity for multiplication
          case (l, FuzzyVal(1.0)) => l
          // Associativity and Commutativity: Group constants
          case (FuzzyVal(i1), FuzzyMult(FuzzyVal(i2), r)) =>  FuzzyMult(Mult(FuzzyVal(i1), FuzzyVal(i2)), r)
          case (FuzzyVal(i1), FuzzyMult(l,FuzzyVal(i2))) =>  FuzzyMult(l,Mult(FuzzyVal(i1), FuzzyVal(i2)))
          case (FuzzyMult(FuzzyVal(i1), r), FuzzyVal(i2)) =>  FuzzyMult(Mult(FuzzyVal(i1), FuzzyVal(i2)),r)
          case (FuzzyMult(l, FuzzyVal(i1)), FuzzyVal(i2)) =>  FuzzyMult(l,Mult(FuzzyVal(i1), FuzzyVal(i2)))
          case (FuzzySet(elems1), FuzzyMult(FuzzySet(elems2), r)) =>  FuzzyMult(Mult(FuzzySet(elems1), FuzzySet(elems2)), r)
          case (FuzzySet(elems1), FuzzyMult(l,FuzzySet(elems2))) =>  FuzzyMult(l,Mult(FuzzySet(elems1), FuzzySet(elems2)))
          case (FuzzyMult(FuzzySet(elems1), r), FuzzySet(elems2)) =>  FuzzyMult(Mult(FuzzySet(elems1), FuzzySet(elems2)),r)
          case (FuzzyMult(l, FuzzySet(elems1)), FuzzySet(elems2)) =>  FuzzyMult(l,Mult(FuzzySet(elems1), FuzzySet(elems2)))
          case _ => FuzzyMult(left, right)

//        FPPE: Partially Safe, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case FuzzyNot(x) =>
        val expr = eval(x, env, root)
        expr match
          case FuzzyVal(i) => Not(FuzzyVal(i))
          case FuzzySet(elems) => Not(FuzzySet(elems))
          case _ => FuzzyNot(expr)
//            throw new Exception("Invalid fuzzy negation on non-compatible types")

      // Logic operations for fuzzy types
//      FPPE: Partially Safe, Supports Associativity and Commutativity, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case FuzzyAnd(x1, x2) =>
        val left = eval(x1, env, root)
        val right = eval(x2, env, root)
        (left, right) match
          case (FuzzyVal(i1), FuzzyVal(i2)) => And(FuzzyVal(i1), FuzzyVal(i2))
          case (FuzzySet(elems1), FuzzySet(elems2)) => And(FuzzySet(elems1), FuzzySet(elems2))
          case (FuzzyVal(1.0), r) => r // Identity for AND
          case (l, FuzzyVal(1.0)) => l
          // Associativity and Commutativity: Group constants
          case (FuzzyVal(i1), FuzzyAnd(FuzzyVal(i2), r)) =>  FuzzyAnd(And(FuzzyVal(i1), FuzzyVal(i2)), r)
          case (FuzzyVal(i1), FuzzyAnd(l,FuzzyVal(i2))) =>  FuzzyAnd(l,And(FuzzyVal(i1), FuzzyVal(i2)))
          case (FuzzyAnd(FuzzyVal(i1), r), FuzzyVal(i2)) =>  FuzzyAnd(And(FuzzyVal(i1), FuzzyVal(i2)),r)
          case (FuzzyAnd(l, FuzzyVal(i1)), FuzzyVal(i2)) =>  FuzzyAnd(l,And(FuzzyVal(i1), FuzzyVal(i2)))
          case (FuzzySet(elems1), FuzzyAnd(FuzzySet(elems2), r)) =>  FuzzyAnd(And(FuzzySet(elems1), FuzzySet(elems2)), r)
          case (FuzzySet(elems1), FuzzyAnd(l,FuzzySet(elems2))) =>  FuzzyAnd(l,And(FuzzySet(elems1), FuzzySet(elems2)))
          case (FuzzyAnd(FuzzySet(elems1), r), FuzzySet(elems2)) =>  FuzzyAnd(And(FuzzySet(elems1), FuzzySet(elems2)),r)
          case (FuzzyAnd(l, FuzzySet(elems1)), FuzzySet(elems2)) =>  FuzzyAnd(l,And(FuzzySet(elems1), FuzzySet(elems2)))
          case _ => FuzzyAnd(left, right)

//        FPPE: Partially Safe, Supports Associativity and Commutativity, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case FuzzyOr(x1, x2) =>
        val left = eval(x1, env, root)
        val right = eval(x2, env, root)
        (left, right) match
          case (FuzzyVal(i1), FuzzyVal(i2)) => Or(FuzzyVal(i1), FuzzyVal(i2))
          case (FuzzySet(elems1), FuzzySet(elems2)) => Or(FuzzySet(elems1), FuzzySet(elems2))
          case (FuzzyVal(0.0), r) => r // Identity for OR
          case (l, FuzzyVal(0.0)) => l
          // Associativity and Commutativity: Group constants
          case (FuzzyVal(i1), FuzzyOr(FuzzyVal(i2), r)) =>  FuzzyOr(Or(FuzzyVal(i1), FuzzyVal(i2)), r)
          case (FuzzyVal(i1), FuzzyOr(l,FuzzyVal(i2))) =>  FuzzyOr(l,Or(FuzzyVal(i1), FuzzyVal(i2)))
          case (FuzzyOr(FuzzyVal(i1), r), FuzzyVal(i2)) =>  FuzzyOr(Or(FuzzyVal(i1), FuzzyVal(i2)),r)
          case (FuzzyOr(l, FuzzyVal(i1)), FuzzyVal(i2)) =>  FuzzyOr(l,Or(FuzzyVal(i1), FuzzyVal(i2)))
          case (FuzzySet(elems1), FuzzyOr(FuzzySet(elems2), r)) =>  FuzzyOr(Or(FuzzySet(elems1), FuzzySet(elems2)), r)
          case (FuzzySet(elems1), FuzzyOr(l,FuzzySet(elems2))) =>  FuzzyOr(l,Or(FuzzySet(elems1), FuzzySet(elems2)))
          case (FuzzyOr(FuzzySet(elems1), r), FuzzySet(elems2)) =>  FuzzyOr(Or(FuzzySet(elems1), FuzzySet(elems2)),r)
          case (FuzzyOr(l, FuzzySet(elems1)), FuzzySet(elems2)) =>  FuzzyOr(l,Or(FuzzySet(elems1), FuzzySet(elems2)))
          case _ => FuzzyOr(left, right)

//        FPPE: Partially Safe, Supports Associativity and Commutativity, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case FuzzyXor(x1, x2) =>
        val left = eval(x1, env, root)
        val right = eval(x2, env, root)
        (left, right) match
          case (FuzzyVal(i1), FuzzyVal(i2)) => Xor(FuzzyVal(i1), FuzzyVal(i2))
          case (FuzzySet(elems1), FuzzySet(elems2)) => Xor(FuzzySet(elems1), FuzzySet(elems2))

          case (FuzzyVal(0.0), r) => r // Identity for XOR
          case (l, FuzzyVal(0.0)) => l
          // Associativity and Commutativity: Group constants
          case (FuzzyVal(i1), FuzzyXor(FuzzyVal(i2), r)) =>  FuzzyXor(Xor(FuzzyVal(i1), FuzzyVal(i2)), r)
          case (FuzzyVal(i1), FuzzyXor(l,FuzzyVal(i2))) =>  FuzzyXor(l,Xor(FuzzyVal(i1), FuzzyVal(i2)))
          case (FuzzyXor(FuzzyVal(i1), r), FuzzyVal(i2)) =>  FuzzyXor(Xor(FuzzyVal(i1), FuzzyVal(i2)),r)
          case (FuzzyXor(l, FuzzyVal(i1)), FuzzyVal(i2)) =>  FuzzyXor(l,Xor(FuzzyVal(i1), FuzzyVal(i2)))
          case (FuzzySet(elems1), FuzzyXor(FuzzySet(elems2), r)) =>  FuzzyXor(Xor(FuzzySet(elems1), FuzzySet(elems2)), r)
          case (FuzzySet(elems1), FuzzyXor(l,FuzzySet(elems2))) =>  FuzzyXor(l,Xor(FuzzySet(elems1), FuzzySet(elems2)))
          case (FuzzyXor(FuzzySet(elems1), r), FuzzySet(elems2)) =>  FuzzyXor(Xor(FuzzySet(elems1), FuzzySet(elems2)),r)
          case (FuzzyXor(l, FuzzySet(elems1)), FuzzySet(elems2)) =>  FuzzyXor(l,Xor(FuzzySet(elems1), FuzzySet(elems2)))
          case _ => FuzzyXor(left, right)

//        FPPE: Partially Safe, Supports Associativity and Commutativity, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case FuzzyNand(x1, x2) =>
        val left = eval(x1, env, root)
        val right = eval(x2, env, root)
        val andEval = eval(FuzzyAnd(left, right), env, root)
        (andEval) match
          case FuzzyVal(i) => Not(FuzzyVal(i))
          case FuzzySet(elems) => Not(FuzzySet(elems))
          case _ => FuzzyNand(left, right)

//          FPPE: Partially Safe, Supports Associativity and Commutativity, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case FuzzyNor(x1, x2) =>
        val left = eval(x1, env, root)
        val right = eval(x2, env, root)
        val orEval = eval(FuzzyOr(left, right), env, root)
        (orEval) match
          case FuzzyVal(i) => Not(FuzzyVal(i))
          case FuzzySet(elems) => Not(FuzzySet(elems))
          case _ => FuzzyNor(left, right)

      // Non-fuzzy operations using underlying Scala logic
      case NonFuzzyAssign(name: String, value: NonFuzzyType[?]) =>
        try
          env.setVariable(name, value)
          eval(value, env, root)
        catch
          case e: Exception => throw new Exception(s"Error in assigning NonFuzzyType: ${e.getMessage}")

      case NonFuzzyAssign(name: String, value: NonFuzzyOperation) =>
        try
          println(s"Assigning $name to $value")
          env.setVariable(name, eval(value, env, root))
          eval(value, env, root)
        catch
          case e: Exception => throw new Exception(s"Error in assigning NonFuzzyOperation: ${e.getMessage}")

      // Evaluates to itself
//      FPPE: Partially Safe, No error conditions
      case NonFuzzyType(value) => NonFuzzyType(value)

      // Set operations for fuzzy types
//      FPPE: Partially Safe, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case FuzzyAlphaCut(set, cut) =>
        val setEval = eval(set, env, root)
        val cutEval = eval(cut, env, root)
        (setEval, cutEval) match
          case (FuzzySet(elems), FuzzyVal(c)) => AlphaCut(FuzzySet(elems), FuzzyVal(c))
          case _ => FuzzyAlphaCut(setEval, cutEval)
//            throw new Exception("Invalid fuzzy alpha cut operation")\
//      FPPE: Partially Safe, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case FuzzyUnion(set1, set2) =>
        val left = eval(set1, env, root)
        val right = eval(set2, env,root)
        (left, right) match
          case (FuzzySet(elems1), FuzzySet(elems2)) => Union(FuzzySet(elems1), FuzzySet(elems2))
          case _ => FuzzyUnion(left, right)

//      FPPE: Partially Safe, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case FuzzyIntersection(set1, set2) =>
        val left = eval(set1, env, root)
        val right = eval(set2, env,root)
        (left, right) match
          case (FuzzySet(elems1), FuzzySet(elems2)) => Intersection(FuzzySet(elems1), FuzzySet(elems2))
          case _ => FuzzyIntersection(left, right)

      // Handling non-fuzzy logic gate assignments and scopes
      case Assign(FuzzyGate(gateName), expr) =>
        env.setVariable(gateName, expr)
        val ns = env.getOrCreateChild(Some(gateName))
        ns.setVariable(gateName, expr)
//         return partially evaluated expression
        eval(expr, env, root)

//        FPPE: Partially Safe, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case Assign(FuzzyVar((name, value)), expr) =>
        val evaluatedExpr = eval(expr, env, root)
        env.setVariable(name, evaluatedExpr)
        evaluatedExpr

//        FPPE: Partially Safe, Error condition: Invalid operation (Error can be generated for non-compatible types)
      case Assign(FuzzyVar(name: String), expr) =>
        val evaluatedExpr = eval(expr, env, root)
        env.setVariable(name, evaluatedExpr)
        evaluatedExpr

      case Scope(s, e) =>
        env.findScope(s) match
          case Some(scopeEnv) => eval(e, scopeEnv, root)
          case None =>
            val newScope = env.createChild(Some(s))
            eval(e, newScope, root)

      // Handle FuzzyGate (storing a gate in the environment)
      case FuzzyGate(gateName) =>
        env.lookup(gateName) match
          case Some(gate) => gate
          case None => throw new Exception(s"Gate $gateName not defined")

      // Handle LogicGate, fetch the expression of the gate and evaluate it
      case LogicGate(gateName) =>
        val lgScope = root.findScope(gateName).getOrElse(throw new Exception(s"Scope $gateName not found"))
        env.lookup(gateName) match
          case Some(expr) => eval(expr, lgScope, root)
          case _ => throw new Exception(s"Logic gate $gateName not defined")

      // Handle TestGate to evaluate a gate with specific inputs
      case TestGate(gateName, input) =>
        val gateEnv = env.findScope(gateName).getOrElse(throw new Exception(s"Scope $gateName not found"))
        gateEnv.lookup(gateName) match
          case Some(expr) =>
            val x: FuzzyExpression = eval(expr, gateEnv, root)
            val y: FuzzyExpression = eval(input, env, root)
            eval(FuzzyAnd(x, y), env, root) // Evaluate the gate with the input
          case _ => throw new Exception(s"Logic gate $gateName not defined")

      // Handle CreateClass to define a class
      case CreateClass(name, extendsClass, vars, methods) =>
        val parentClassDef = extendsClass.flatMap(env.classes.get)
        val classDef: ClassDef = ClassDef(name, parentClassDef, vars, methods)
        env.defineClass(classDef)
        classDef

      // Handle CreateInstance to create a class instance
      case CreateInstance(className) =>
        val instance = env.createInstance(className)
        FuzzyVal(instance.hashCode().toDouble) // Representing instance as a value (hash code)

      case InvokeMethod(instanceName, methodName, args) =>
        // Evaluate arguments as much as possible
        val evaluatedArgs = args.map { case (argName, argExpr) => (argName, eval(argExpr, env, root)) }

        env.instances.get(instanceName) match
          case Some(instance) =>
            val classDef = instance.classDef

            // Find the method in the class or its parent classes
            def findMethod(classDef: ClassDef, methodName: String): Option[MethodDef] =
              classDef.methods.find(_.methodName == methodName)
                .orElse(classDef.extendsClass.flatMap(findMethod(_, methodName)))

            findMethod(classDef, methodName) match
              case Some(methodDef) =>
                // Create a new environment for the method scope
                val methodEnv = env.createChild(Some(s"MethodScope-$methodName"))

                // Bind method parameters in the new environment
                methodDef.parameters.zip(evaluatedArgs).foreach { case (param, (argName, argValue)) =>
                  methodEnv.setVariable(param.name, argValue)
                }

                // Evaluate all statements in the method body one by one
                val evaluatedBody = methodDef.body match
                  case singleExpr: FuzzyExpression =>
                    List(eval(singleExpr, methodEnv, root))
                  case exprList: List[FuzzyExpression] =>
                    exprList.map(expr => eval(expr, methodEnv, root))

                // Check if all expressions are fully evaluated
                val allFullyEvaluated = evaluatedBody.forall(isFullyEvaluated)

                if (allFullyEvaluated) {
                  // Return the last result
                  evaluatedBody.last
                } else {
                  // Return the list of partially evaluated expressions
                  PartiallyEvaluatedMethod(evaluatedBody)
                }
              case None =>
                // Method not found, return partially evaluated InvokeMethod
                InvokeMethod(instanceName, methodName, evaluatedArgs)
          case None =>
            // Instance not found, return partially evaluated InvokeMethod
            InvokeMethod(instanceName, methodName, evaluatedArgs)

      case Macro(name) =>
        eval(env.lookupMacro(name).getOrElse(throw new Exception(s"Macro $name not defined")), env, root)
      // Handle Let construct for local variable assignments
  // Define a macro in the environment
      case DefineMacro(name, macroExpr) =>
        env.defineMacro(name, macroExpr)
        macroExpr


      case Let(assignments, inExpr) =>
        // Create a new environment scope for Let
        val letEnv = env.createChild(Some("LetScope"))

        // Evaluate each assignment and add it to the Let environment
        val evaluatedAssignments = assignments.map {
          case Assign(FuzzyVar(name: String), value) =>
            val evaluatedValue = eval(value, letEnv, root)
            letEnv.setVariable(name, evaluatedValue)
            Assign(FuzzyVar(name), evaluatedValue)
          case other =>
            // Cannot evaluate assignment, keep it as is
            other
        }

        // Evaluate the expression within the Let scope
        val evaluatedInExpr = eval(inExpr, letEnv, root)

        // Return the result if fully evaluated; otherwise, return the partially evaluated Let expression
        if (evaluatedAssignments.forall(_.isInstanceOf[Assign]) && !evaluatedInExpr.isInstanceOf[Let]) {
          evaluatedInExpr
        } else {
          Let(evaluatedAssignments, evaluatedInExpr)
        }

    // Evaluate IfTrue construct
      case IfTrue(condition, ThenExecute(thenBranch), ElseRun(elseBranch)) =>
        // Evaluate the condition
        val condEval = eval(condition, env, root)
        condEval match
          case FuzzyVal(c) if c >= 1.0 =>
            // Fully evaluate Then branch
            val evaluatedThen = thenBranch.map(e => eval(e, env, root))
            if (evaluatedThen.forall(isFullyEvaluated)) {
              FuzzyVal(1.0) // Return 1.0 if all Then branch expressions are fully evaluated
            } else {
              // Return partially evaluated IfTrue with remaining Then branch
              IfTrue(condEval, ThenExecute(evaluatedThen), ElseRun(elseBranch))
            }
          case FuzzyVal(c) if c < 1.0 =>
            // Fully evaluate Else branch
            val evaluatedElse = elseBranch.map(e => eval(e, env, root))
            if (evaluatedElse.forall(isFullyEvaluated)) {
              FuzzyVal(1.0) // Return 0.0 if all Else branch expressions are fully evaluated
            } else {
              // Return partially evaluated IfTrue with remaining Else branch
              IfTrue(condEval, ThenExecute(thenBranch), ElseRun(evaluatedElse))
            }
          case NonFuzzyType(true) =>
            // Fully evaluate Then branch
            val evaluatedThen = thenBranch.map(e => eval(e, env, root))
            if (evaluatedThen.forall(isFullyEvaluated)) {
              FuzzyVal(1.0) // Return 1.0 if all Then branch expressions are fully evaluated
            } else {
              // Return partially evaluated IfTrue with remaining Then branch
              IfTrue(condEval, ThenExecute(evaluatedThen), ElseRun(elseBranch))
            }
          case NonFuzzyType(false) =>
            // Fully evaluate Else branch
            val evaluatedElse = elseBranch.map(e => eval(e, env, root))
            if (evaluatedElse.forall(isFullyEvaluated)) {
              FuzzyVal(1.0) // Return 0.0 if all Else branch expressions are fully evaluated
            } else {
              // Return partially evaluated IfTrue with remaining Else branch
              IfTrue(condEval, ThenExecute(thenBranch), ElseRun(evaluatedElse))
            }
          case _ =>
            // Return partially evaluated IfTrue if the condition isn't fully evaluated
            IfTrue(condEval, ThenExecute(thenBranch), ElseRun(elseBranch))

      // Evaluate ThenExecute construct
      case ThenExecute(exprList) =>
        val evaluatedExprs = exprList.map(e => eval(e, env, root))
        if (evaluatedExprs.forall(isFullyEvaluated)) {
          FuzzyVal(1.0) // Return 1.0 if all expressions are fully evaluated
        } else {
          // Return partially evaluated ThenExecute
          ThenExecute(evaluatedExprs)
        }

      // Evaluate ElseRun construct
      case ElseRun(exprList) =>
        val evaluatedExprs = exprList.map(e => eval(e, env, root))
        if (evaluatedExprs.forall(isFullyEvaluated)) {
          FuzzyVal(1.0) // Return 1.0 if all expressions are fully evaluated
        } else {
          // Return partially evaluated ElseRun
          ElseRun(evaluatedExprs)
        }

      case NonFuzzyOperation(p, fun) =>
        val args = p.map {
          case fuzzyExpr: FuzzyExpression =>
            eval(fuzzyExpr, env, root) match {
              case evaluatedResult: NonFuzzyType[_] => // Handle specific case on eval result if it's of DesiredType
                // Process evaluatedResult as needed
                evaluatedResult.value
              case fz: FuzzyVal =>
                fz.i
              case fsSet: FuzzySet =>
                fsSet.elems.map { case (name, FuzzyVal(v)) => (v) }
              case _ => throw new Exception("Invalid Fuzzy Expression")
            }
          case nonFuzzyValue => nonFuzzyValue // Non-fuzzy values remain as they are
        }
        NonFuzzyType(fun.apply(args))

      case _ => throw new Exception("Invalid Fuzzy Expression")

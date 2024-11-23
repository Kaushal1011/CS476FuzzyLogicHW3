import FuzzyExpressions.FuzzyExpression.{NonFuzzyOperation, *}
import FuzzyExpressions.FuzzyExpression
import FuzzyMath.*
import EnvironmentScopes.Environment

import scala.collection.mutable

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
              case None => FuzzyVar(name) // Return the variable name if not found for later evaluation
//                throw new Exception(s"Variable $name not defined in scope ${env.name}")

      // NonFuzzyVar logic: Fetch value from environment or error if not found
      case NonFuzzyVar(v) =>
        v match
          case (name: String, value: NonFuzzyType[?]) =>
            env.lookup(name) match
              case Some(foundVar) => eval(foundVar, env, root) // Evaluate the found variable
              case None => NonFuzzyType(value) // Return the non-fuzzy value if not found
          case name: String =>
            env.lookup(name) match
              case Some(foundVar) => eval(foundVar, env, root) // Evaluate the found variable
              case None => NonFuzzyVar(name) // Return the variable name if not found for later evaluation
//                throw new Exception(s"Non-fuzzy variable $name not defined in scope ${env.name}")

      // Arithmetic operations for Fuzzy types
      case FuzzyAdd(x1, x2) =>
        val left = eval(x1, env, root)
        val right = eval(x2, env, root)
        (left, right) match
          case (FuzzyVal(i1), FuzzyVal(i2)) => Add(FuzzyVal(i1), FuzzyVal(i2))
          case (FuzzyVal(0.0), r) => r // Identity for addition
          case (l, FuzzyVal(0.0)) => l
          // Associativity and Commutativity: Group constants
          case (FuzzyVal(i1), FuzzyAdd(FuzzyVal(i2), r)) =>  FuzzyAdd(Add(FuzzyVal(i1), FuzzyVal(i2)), r)
          case _ => FuzzyAdd(left, right)
          
      case FuzzyMult(x1, x2) =>
        val left = eval(x1, env, root)
        val right = eval(x2, env, root)
        (left, right) match
          case (FuzzyVal(i1), FuzzyVal(i2)) => Mult(FuzzyVal(i1), FuzzyVal(i2))
          case (FuzzyVal(1.0), r) => r // Identity for multiplication
          case (l, FuzzyVal(1.0)) => l
          // Associativity and Commutativity: Group constants
          case (FuzzyVal(i1), FuzzyMult(FuzzyVal(i2), r)) =>  FuzzyMult(Mult(FuzzyVal(i1), FuzzyVal(i2)), r)
          case _ => FuzzyMult(left, right)

      case FuzzyNot(x) =>
        val expr = eval(x, env, root)
        expr match
          case FuzzyVal(i) => Not(FuzzyVal(i))
          case FuzzySet(elems) => Not(FuzzySet(elems))
          case _ => throw new Exception("Invalid fuzzy negation on non-compatible types")

      // Logic operations for fuzzy types
      case FuzzyAnd(x1, x2) =>
        val left = eval(x1, env, root)
        val right = eval(x2, env, root)
        (left, right) match
          case (FuzzyVal(i1), FuzzyVal(i2)) => And(FuzzyVal(i1), FuzzyVal(i2))
          case (FuzzyVal(1.0), r) => r // Identity for AND
          case (l, FuzzyVal(1.0)) => l
          // Associativity and Commutativity: Group constants
          case (FuzzyVal(i1), FuzzyAnd(FuzzyVal(i2), r)) =>  FuzzyAnd(And(FuzzyVal(i1), FuzzyVal(i2)), r)
          case _ => FuzzyAnd(left, right)

      case FuzzyOr(x1, x2) =>
        val left = eval(x1, env, root)
        val right = eval(x2, env, root)
        (left, right) match
          case (FuzzyVal(i1), FuzzyVal(i2)) => Or(FuzzyVal(i1), FuzzyVal(i2))
          case (FuzzyVal(0.0), r) => r // Identity for OR
          case (l, FuzzyVal(0.0)) => l
          // Associativity and Commutativity: Group constants
          case (FuzzyVal(i1), FuzzyOr(FuzzyVal(i2), r)) =>  FuzzyOr(Or(FuzzyVal(i1), FuzzyVal(i2)), r)
          case _ => FuzzyOr(left, right)

      case FuzzyXor(x1, x2) =>
        val left = eval(x1, env, root)
        val right = eval(x2, env, root)
        (left, right) match
          case (FuzzyVal(i1), FuzzyVal(i2)) => Xor(FuzzyVal(i1), FuzzyVal(i2))
          case (FuzzyVal(0.0), r) => r // Identity for XOR
          case (l, FuzzyVal(0.0)) => l
          // Associativity and Commutativity: Group constants
          case (FuzzyVal(i1), FuzzyXor(FuzzyVal(i2), r)) =>  FuzzyXor(Xor(FuzzyVal(i1), FuzzyVal(i2)), r)
          case _ => FuzzyXor(left, right)

      case FuzzyNand(x1, x2) => Not(FuzzyAnd(x1, x2))
      case FuzzyNor(x1, x2) => Not(FuzzyOr(x1, x2))

      // Non-fuzzy operations using underlying Scala logic
      case NonFuzzyAssign(name: String, value: NonFuzzyType[?]) =>
        try
          env.setVariable(name, value)
          eval(value, env, root)
        catch
          case e: Exception => throw new Exception(s"Error in assigning NonFuzzyType: ${e.getMessage}")

      case NonFuzzyAssign(name: String, value: NonFuzzyOperation) =>
        try
          env.setVariable(name, eval(value, env, root))
          eval(value, env, root)
        catch
          case e: Exception => throw new Exception(s"Error in assigning NonFuzzyOperation: ${e.getMessage}")

      // Evaluates to itself
      case NonFuzzyType(value) => NonFuzzyType(value)

      // Set operations for fuzzy types
      case FuzzyAlphaCut(set, cut) =>
        AlphaCut(eval(set, env, root).asInstanceOf[FuzzySet], eval(cut, env, root).asInstanceOf[FuzzyVal])
      case FuzzyUnion(set1, set2) =>
        Union(eval(set1, env, root).asInstanceOf[FuzzySet], eval(set2, env, root).asInstanceOf[FuzzySet])
      case FuzzyIntersection(set1, set2) =>
        Intersection(eval(set1, env, root).asInstanceOf[FuzzySet], eval(set2, env, root).asInstanceOf[FuzzySet])

      // Handling non-fuzzy logic gate assignments and scopes
      case Assign(FuzzyGate(gateName), expr) =>
        env.setVariable(gateName, expr)
        val ns = env.getOrCreateChild(Some(gateName))
        ns.setVariable(gateName, expr)
        expr

      case Assign(FuzzyVar((name, value)), expr) =>
        val evaluatedExpr = eval(expr, env, root)
        env.setVariable(name, evaluatedExpr)
        evaluatedExpr

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

      // Handle InvokeMethod to call a method on an instance
      case InvokeMethod(instanceName, methodName, args) =>
        val instance = env.instances.getOrElse(instanceName, throw new Exception(s"Instance $instanceName not found"))
        val classDef = instance.classDef

        // Find the method in the class or its parent classes
        def findMethod(classDef: ClassDef, methodName: String): Option[MethodDef] =
          classDef.methods.find(_.methodName == methodName)
            .orElse(classDef.extendsClass.flatMap(findMethod(_, methodName)))

        val methodDef = findMethod(classDef, methodName).getOrElse(throw new Exception(s"Method $methodName not found in class ${classDef.name}"))

        // Create a new environment for the method scope
        val methodEnv = env.createChild(Some(s"MethodScope-$methodName"))

        // Bind method parameters in the new environment
        methodDef.parameters.zip(args).foreach { case (param, (argName, argExpr)) =>
          val evaluatedArg = eval(argExpr, env, root)
          val paramType = param.paramType.name
          println(evaluatedArg.getClass.getSimpleName)
          if evaluatedArg.getClass.getSimpleName == "NonFuzzyType" then
            if paramType!="Any" && evaluatedArg.asInstanceOf[NonFuzzyType[?]].value.getClass.getSimpleName != paramType then
              throw new Exception(s"Invalid argument type for parameter ${param.name}, expected $paramType got ${evaluatedArg.asInstanceOf[NonFuzzyType[?]].value.getClass.getSimpleName}")
          else if paramType!="Any" && evaluatedArg.getClass.getSimpleName != paramType then
            throw new Exception(s"Invalid argument type for parameter ${param.name}, expected $paramType")

          methodEnv.setVariable(param.name, evaluatedArg)
        }

        // Evaluate all statements in the method body and return the result of the last one
        methodDef.body match
          case singleExpr: FuzzyExpression => eval(singleExpr, methodEnv, root)
          case exprList: List[FuzzyExpression] =>
            val invoked = exprList.map(eval(_, methodEnv, root))
//            println(exprList)
//            println(s"Method $methodName invoked with result: $invoked")
            invoked.last

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
        assignments.foreach {
          case Assign(FuzzyVar(name: String), value) =>
            letEnv.setVariable(name, eval(value, letEnv, root))
          case _ => throw new Exception("Invalid assignment in Let construct")
        }
        // Evaluate the expression within the Let scope
        eval(inExpr, letEnv, root)


      case _ => throw new Exception("Invalid Fuzzy Expression")

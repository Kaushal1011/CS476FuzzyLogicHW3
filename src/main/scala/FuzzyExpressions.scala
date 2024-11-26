import FuzzyExpressions.FuzzyExpression.ThenExecute

import scala.collection.mutable
object FuzzyExpressions:

    enum FuzzyExpression:

        case FuzzyNone

        case FuzzyVal(i: Double)
        case FuzzySet(elems: List[(String, FuzzyVal)])
        case FuzzyVar(v: (String, FuzzyExpression) | (String))

        // Basic Fuzzy Operations
        case FuzzyAdd(x1: FuzzyExpression, x2: FuzzyExpression)
        case FuzzyMult(x1: FuzzyExpression, x2: FuzzyExpression)
        case FuzzyNot(x: FuzzyExpression)

        // Logic gates
        case FuzzyAnd(x1: FuzzyExpression, x2: FuzzyExpression)
        case FuzzyOr(x1: FuzzyExpression, x2: FuzzyExpression)
        case FuzzyXor(x1: FuzzyExpression, x2: FuzzyExpression)
        case FuzzyNand(x1: FuzzyExpression, x2: FuzzyExpression)
        case FuzzyNor(x1: FuzzyExpression, x2: FuzzyExpression)

        // Set operations
        case FuzzyAlphaCut(set: FuzzySet|FuzzyExpression, cut: FuzzyVal|FuzzyExpression)
        case FuzzyUnion(set1: FuzzySet|FuzzyExpression, set2: FuzzySet|FuzzyExpression)
        case FuzzyIntersection(set1: FuzzySet|FuzzyExpression, set2: FuzzySet|FuzzyExpression)

        // Assignment and Scoping
        case Assign(s: FuzzyExpression, e: FuzzyExpression)
        case Scope(s: String, e: FuzzyExpression)
        case FuzzyGate(s: String)
        case LogicGate(s: String)

        // Test Gate with an Expected Value
        case TestGate(s: String, e: FuzzyExpression)

        // Class and instance definitions for the extended DSL
        case ClassDef(name: String, extendsClass: Option[ClassDef] = None,
                            variables: List[ClassVar] = List(), methods: List[MethodDef] = List())

        case ClassVar(name: String, varType: VarType)

        case MethodDef(methodName: String, parameters: List[Parameter], body: FuzzyExpression | List[FuzzyExpression])

        case VarType(name: String)

        case Parameter(name: String, paramType: ParamType)

        case ParamType(name: String)

        case ClassInstance(classDef: ClassDef, variables: mutable.Map[String, FuzzyExpression])

        case CreateClass(name: String, extendsClass: Option[String], vars: List[ClassVar], methods: List[MethodDef])

        case CreateInstance(className: String)

        case InvokeMethod(instanceName: String, methodName: String, args: List[(String, FuzzyExpression)])

        case NonFuzzyType[A](value: A)
        
        case NonFuzzyAssign(name: String, value: NonFuzzyType[?] | NonFuzzyOperation)
        
        case NonFuzzyVar(v: (String,NonFuzzyType[?]) | (String))

        case NonFuzzyOperation(p: List[Any], fun: Seq[Any] => Any)
//            def applyOperation(): NonFuzzyType[Any] =
//                val args = p.map(_.value)
//                NonFuzzyType(fun(args))

        case Macro(name: String) // Macro case for macro substitution
        case Let(assignments: List[Assign|FuzzyExpression], inExpr: FuzzyExpression) // Let-in construct
        case DefineMacro(name: String, expr: FuzzyExpression) // Defining a macro
        case PartiallyEvaluatedMethod(body: List[FuzzyExpression])

        // Conditional Expressions
        case IfTrue(condition: FuzzyExpression, thenBranch: FuzzyExpression, elseBranch: FuzzyExpression)
        case ThenExecute(expr: List[FuzzyExpression])
        case ElseRun(expr: List[FuzzyExpression])

//    object ConditionalSyntax:
//
//        implicit class ConditionalOps(condition: FuzzyExpression):
//            def IfTrue(thenBranch: => FuzzyExpression): ThenBranch =
//                ThenBranch(condition, ThenExecute(thenBranch))
//
//        case class ThenBranch(condition: FuzzyExpression, thenBranch: ThenExecute):
//            def ElseRun(elseBranch: => FuzzyExpression): FuzzyExpression.IfTrue =
//                FuzzyExpression.IfTrue(condition, thenBranch, ElseRun(elseBranch))

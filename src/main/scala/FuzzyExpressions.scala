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
        case FuzzyAlphaCut(set: FuzzySet, cut: FuzzyVal)
        case FuzzyUnion(set1: FuzzySet, set2: FuzzySet)
        case FuzzyIntersection(set1: FuzzySet, set2: FuzzySet)

        // Assignment and Scoping
        case Assign(s: FuzzyExpression, e: FuzzyExpression)
        case Scope(s: String, e: FuzzyExpression)
        case FuzzyGate(s: String)
        case LogicGate(s: String)

        // Test Gate with an Expected Value
        case TestGate(s: String, e: FuzzyExpression)

        // Control Flow
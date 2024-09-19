import FuzzyExpressions.FuzzyExpression._
import FuzzyExpressions.FuzzyExpression

object FuzzyMath:

  // Add two fuzzy values or sets
  def Add(x1: FuzzyExpression, x2: FuzzyExpression): FuzzyExpression = (x1, x2) match
    case (FuzzyVal(v1), FuzzyVal(v2)) => FuzzyVal(math.min(1, v1 + v2))
    case (FuzzySet(elems1), FuzzySet(elems2)) =>
      val combinedElems = combineSets(elems1, elems2, (v1, v2) => math.min(1, v1 + v2))
      FuzzySet(combinedElems)
    case _ => throw new Exception("Invalid fuzzy addition between non-compatible types")

  // Multiply two fuzzy values or sets
  def Mult(x1: FuzzyExpression, x2: FuzzyExpression): FuzzyExpression = (x1, x2) match
    case (FuzzyVal(v1), FuzzyVal(v2)) => FuzzyVal(v1 * v2)
    case (FuzzySet(elems1), FuzzySet(elems2)) =>
      val combinedElems = combineSets(elems1, elems2, (v1, v2) => v1 * v2)
      FuzzySet(combinedElems)
    case _ => throw new Exception("Invalid fuzzy multiplication between non-compatible types")

  // Fuzzy XOR (difference between max and min of values)
  def Xor(x1: FuzzyExpression, x2: FuzzyExpression): FuzzyExpression = (x1, x2) match
    case (FuzzyVal(v1), FuzzyVal(v2)) => FuzzyVal(math.max(v1, v2) - math.min(v1, v2))
    case (FuzzySet(elems1), FuzzySet(elems2)) =>
      val combinedElems = combineSets(elems1, elems2, (v1, v2) => math.max(v1, v2) - math.min(v1, v2))
      FuzzySet(combinedElems)
    case _ => throw new Exception("Invalid fuzzy XOR operation")



  // Fuzzy AND (minimum of values)
  def And(x1: FuzzyExpression, x2: FuzzyExpression): FuzzyExpression = (x1, x2) match
    case (FuzzyVal(v1), FuzzyVal(v2)) => FuzzyVal(math.min(v1, v2))
    case (FuzzySet(elems1), FuzzySet(elems2)) =>
      val combinedElems = combineSets(elems1, elems2, (v1, v2) => math.min(v1, v2))
      FuzzySet(combinedElems)
    case _ => throw new Exception("Invalid fuzzy AND operation")

  // Fuzzy Not operation
  def Not(x: FuzzyExpression): FuzzyExpression = x match
    case FuzzyVal(v) => FuzzyVal(1 - v)
    case FuzzySet(elems) =>
      val negatedElems: List[(String, FuzzyExpressions.FuzzyExpression.FuzzyVal)] = elems.map { case (name, FuzzyVal(v)) => (name, FuzzyVal(1 - v)) }
      FuzzySet(negatedElems)
    case _ => throw new Exception("Invalid fuzzy negation on non-compatible types")

  // Fuzzy NAND (NOT AND)
  def Nand(x1: FuzzyExpression, x2: FuzzyExpression): FuzzyExpression = Not(And(x1, x2))

  // Fuzzy NOR (NOT OR)
  def Nor(x1: FuzzyExpression, x2: FuzzyExpression): FuzzyExpression = Not(Or(x1, x2))

  // Fuzzy OR (maximum of values)
  def Or(x1: FuzzyExpression, x2: FuzzyExpression): FuzzyExpression = (x1, x2) match
    case (FuzzyVal(v1), FuzzyVal(v2)) => FuzzyVal(math.max(v1, v2))
    case (FuzzySet(elems1), FuzzySet(elems2)) =>
      val combinedElems = combineSets(elems1, elems2, (v1, v2) => math.max(v1, v2))
      FuzzySet(combinedElems)
    case _ => throw new Exception("Invalid fuzzy OR operation")

  // Alpha Cut (for extracting elements with membership values >= alpha)
  def AlphaCut(set: FuzzySet, cut: FuzzyVal): FuzzySet =
    FuzzySet(set.elems.filter { case (_, FuzzyVal(v)) => v >= cut.i })

  // Fuzzy Set Union (maximum of membership values for common elements)
  def Union(set1: FuzzySet, set2: FuzzySet): FuzzySet =
    FuzzySet(combineSets(set1.elems, set2.elems, (v1, v2) => math.max(v1, v2)))

  // Fuzzy Set Intersection (minimum of membership values for common elements)
  def Intersection(set1: FuzzySet, set2: FuzzySet): FuzzySet =
    FuzzySet(combineSets(set1.elems, set2.elems, (v1, v2) => math.min(v1, v2)))

  // Helper function to combine two fuzzy sets based on element name, preserving unmatched elements
  private def combineSets(
                           elems1: List[(String, FuzzyVal)],
                           elems2: List[(String, FuzzyVal)],
                           op: (Double, Double) => Double): List[(String, FuzzyVal)] =

    val map1 = elems1.toMap
    val map2 = elems2.toMap

    // Merge the sets by applying the operation on common elements and preserving unique elements
    val allKeys = map1.keySet ++ map2.keySet

    allKeys.toList.map { key =>
      (map1.get(key), map2.get(key)) match
        case (Some(FuzzyVal(v1)), Some(FuzzyVal(v2))) => (key, FuzzyVal(op(v1, v2)))
        case (Some(FuzzyVal(v1)), None) => (key, FuzzyVal(v1)) // Element only in set1
        case (None, Some(FuzzyVal(v2))) => (key, FuzzyVal(v2)) // Element only in set2
        case _ => throw new Exception("Invalid fuzzy set operation")
    }
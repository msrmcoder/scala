package basicscala.lectures.part4pm

import basicscala.exercise.{Cons, Empty, MyList}

object AllThePatterns extends App {

  // 1. Constants
  val x: Any = "Scala"
  val constant = x match {
    case 1 => "A number"
    case "Scala" => "This scala"
    case true => "The truth"
    case AllThePatterns => "A singleton object"
  }

  // 2. match anything
  // 2.1 wildcard
  val matchWildcard = x match {
    case _ =>
  }

  // 2.2. variable
  val matchAVariable = x match {
    case something => s"I've found $something"
  }

  // 3. Tuples
  val aTuple = (1, 2)
  val matchATuple = aTuple match {
    case (1, 1) => "Tuple(1, 1)"
    case (1, 2) => "Tuple(1, 2)"
    case (something, 2) => s"Tuple($something, 2)"
  }

  val nestedTuple = (1, (2, 3))
  val matchNestedTuple = nestedTuple match {
    case (_, (1, v)) => s"NestedTuple of value contains $v"
  }

  // 4. case classes - Constructor pattern
  val aList: MyList[Int] = Cons(1, Cons(2, Empty))
  val matchAList = aList match {
    case Empty =>
    case Cons(head, tail) =>
    case Cons(head, Cons(subhead, subTail)) => // Nested matching
  }

  // 5. list pattern
  val standardList = List(1,2,3,4)
  val matchStandardList = standardList match {
    case List(1, _, _, _) => // constructor extractor pattern
    case List(1, _*) => // list of arbitrary length (kinda varargs)
    case 1 :: List(_) => // infix pattern
    case List(1, 2, 3) :+ 42 => // infix pattern
  }

  // 6. type specifiers
  val unknown: Any = 2
  val matchUnknown = unknown match {
    case list: List[Int] => // explicit type specifier
    case _ =>
  }

  // 7. name binding
  val nameBinding = aList match {
    case nonEmptyList @ Cons(_, _) => // name binding => use the name later(here)
    case Cons(head, rest @ Cons(2, _)) => // name binding inside the nested patterns
  }

  // 8. multi-patterns
  val matchMultiPattern = aList match {
    case Empty | Cons(0, _) => // compound pattern (multi-pattern)
  }

  // 9. if guards
  val secondElementSpecial = aList match {
    case Cons(_, Cons(head, _)) if head % 2 == 0 => // predicate added as part of case
  }

  // Exercise
  // JVM trick question - type erasure
  val listOfInts = List(1, 2, 3, 4)
  val numberMatch = listOfInts match {
    case listOfString: List[String] => s"list of strings" // this is matched because of JVM backward compatibility
    case listOfInteger: List[Int] => s"list of integers"
    case _ => ""
  }
  println(numberMatch)
}

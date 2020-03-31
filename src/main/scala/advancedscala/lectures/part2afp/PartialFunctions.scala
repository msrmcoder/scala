package advancedscala.lectures.part2afp

object PartialFunctions extends App {
  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] === Int => Int
  // restrict function
  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  // proper function, can't be assigned to partial function
  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
    // MatchedError
  }
  // {1, 2, 5} => Int accepts only part of Int domain values
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // partial function values
  println(aPartialFunction(2))
  //  println(aPartialFunction(7)) // crashed due to MatchError

  // PF utilities
  println(aPartialFunction.isDefinedAt(7))
  // lift
  val lifted = aPartialFunction.lift
  println(lifted(2))
  println(lifted(7))

  val chainedPF = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }
  println(chainedPF(2))
  println(chainedPF(45))
  //  println(chainedPF(10))

  // PF extend to normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOFs accept partial functions as well
  val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
  println(aMappedList)

  /*
    Note: PF can only have ONE parameter type
   */

  /*
    Exercise.
    1. construct a PF instance (anonymous class)
    2. dumb chatbot as a PF
   */
  val aManualFussyFunction = new PartialFunction[Int, Int] {
    def isDefinedAt(x: Int): Boolean =
      x == 1 || x == 2 || x == 5

    def apply(x: Int): Int = x match {
      case 1 => 42
      case 2 => 65
      case 5 => 999
    }

  }
  println(aManualFussyFunction(5))

  val chatbot: PartialFunction[String, String] = {
    case "hello" => "Hi, my name is HAL9000"
    case "goodbye" => "Once you start talking to me, there is no return, human!"
    case "Call mom" => "Unable to find your phone without your credit card"
  }
  //  scala.io.Source.stdin.getLines().foreach(line => println("chatbot says: " + chatbot(line)))
  // can be written as
  scala.io.Source.stdin.getLines().map(chatbot).foreach(println)

}

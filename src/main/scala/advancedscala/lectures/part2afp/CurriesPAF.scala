package advancedscala.lectures.part2afp

object CurriesPAF extends App {

  // curry = a function returns an another function
  val adder: Int => Int = x => x + 1 // simple adder
  val superAdder: Int => Int => Int = y => x => x + y // eq to val superAdder: Int => (Int => Int) = y => x => x + y
  val add3 = superAdder(3) // Int => Int = y => 3 + y
  println(add3(2)) // 5
  println(superAdder(3)(2)) // multiple parameter list = curried function

  // bit more complex curried function
  val twoNumAdder: (Int, Int) => Int = (x, y) => x + y
  val algebraicFn: (Int, Int) => (Int, Int) => Int = (x, y) => (a, b) => (x + y) / (a + b)

  // METHOD
  def curriedAdder(x: Int)(y: Int): Int = x + y // curried method
  // val add4 = curriedAdder(4) // throws compiler error

  // FUNCTION
  val add4: Int => Int = curriedAdder(4)

  // lifting = ETA-Expansion
  // methods can't used as HOF whereas function does
  // function != method (JVM limitation)
  def inc(x: Int): Int = x + 1

  List(1, 2, 3).map(inc)
  // ETA-EXPANSION compiler converts into lambda
  // List(1,2,3).map(x => inc(x)) // ETA-EXPANSION

  // Partial function applications
  val add5 = curriedAdder(5) _ // tells compiler to apply ETA-EXPANSION i.e Int => Int

  // Exercise:
  val simpleAddFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int): Int = x + y

  def curriedAddMethod(x: Int)(y: Int): Int = x + y

  // val add7: Int => Int = x => x + 7
  // as many different implementation of add7 using the above
  // be creative!

  val simpleAddFunction7 = simpleAddFunction(7, _)
  println(simpleAddFunction7(2))

  val simpleAddMethod7 = simpleAddMethod(7, _)
  println(simpleAddMethod7(2))

  val curriedAddMethod7 = curriedAddMethod(7) _
  println(curriedAddMethod7(2))

  // Answers
  val add7 = (x: Int) => simpleAddFunction(7, x)
  val add7_2 = simpleAddFunction.curried(7)
  val dad7_3 = simpleAddFunction(7, _: Int)
  val add7_4 = curriedAddMethod(7) _ // PAF
  val add7_5 = curriedAddMethod(7)(_) // PAF = alternative
  val add7_6 = simpleAddFunction(7, _: Int) // alternative syntax for turning methods into function values
  // y => simpleAddFunction(7, y)

  // underscores are powerful
  def concatenator(a: String, b: String, c: String): String = a + b + c

  val insertName = concatenator("Hello, I'm ", _: String, " how are you?") // compiler does x: String => concatenator(hello, x, howareyou)
  println(insertName("Sriram"))

  val fillInTheBlanks = concatenator("Hello, ", _: String, _: String) // (x, y) => concatenator("Hello, ", x, y)
  println(fillInTheBlanks("Sriram", " Scala is awesome!"))

  // Exercise
  /*
    1. Process list of numbers and return their string representation with different formats
        Use the %4.2f, %8.6g and %14.12f with a curried formatter function.
   */
  println("%4.2f".format(Math.PI))


  // Exercise: 1: Answer
  def curriedFormatter(s: String)(n: Double) = s format n
  val standardFormat = curriedFormatter("%4.2f") _ // lift
  val preciseFormat = curriedFormatter("%8.6f") _
  val morePreciseFormat = curriedFormatter("%14.12f") _

  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)
  println(numbers.map(standardFormat))
  println(numbers.map(preciseFormat))
  println(numbers.map(morePreciseFormat))

  /*
    2. difference between
        - functions vs methods
        - parameters: by-name vs 0-lambda
   */
  def byName(n: => Int): Int = n + 1
  def byFunction(f: () => Int): Int = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  /*
    calling byName and byFunction
     - int
     - method
     - parenMethod
     - lambda
     - PAF
   */

  // Exercise: 2 Answer
  println("...By Name...")
  println("calling with int...")
  println(byName(4))
  println("calling with method...")
  println(byName(method))
  println("calling with parenMethod...")
  println(byName(parenMethod()))
  println(byName(parenMethod))
  println("calling with lambda...")
  // println(byName(() => 5)) // not ok
  println(byName( (() => 5)())) // ok because, creating and invoking lambda

  // byFunction(4) // not ok
  // byFunction(method) // not ok!!.. parameterless method not accepted.. compiler don't do eta-expansion
  byFunction(parenMethod) // ok. eta-expansion applied
  byFunction(() => 4)
  byFunction(parenMethod _) // ok
}

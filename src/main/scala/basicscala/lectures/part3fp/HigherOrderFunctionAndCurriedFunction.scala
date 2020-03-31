package basicscala.lectures.part3fp

import scala.annotation.tailrec

object HigherOrderFunctionAndCurriedFunction extends App {
  // Higher order function - takes function, returns function within a function
  val superFunction: (Int, (String, (Int => Boolean)) => Int) => (Int => Int) = null

  // filter, map, flatMap are all higher order function on the examples we have seen in the previous tutorial

  // function that applies an another function n times over a value x
  // nTimes(f, n, x)
  // nTimes(f, 3, x) = f(f(f(x)))
  // nTimes(f, 3, x)
  //  = nTimes(f, 2, f(x))
  //  = nTimes(f, 1, f(f(x)))
  //  = nTimes(f, 0, f(f(f(x))))
  @tailrec
  def nTimes(f: Int => Int, n: Int, x: Int): Int =
    if (n <= 0) x
    else nTimes(f, n - 1, f(x))

  val plusOne: Int => Int = x => x + 1
  println(nTimes(plusOne, 10, 1))

  // ntb(f, n) = x => f(f(f...(x))
  // ntb(f, 3) = x => f(f(f(x)))
  // val plusOne3 = ntb(plusOne, 3)
  // plusOne3(1) = 4
  def nTimesBetter(f: Int => Int, n: Int): (Int => Int) =
    if (n <= 0) (x: Int) => x
    else (x: Int) => nTimesBetter(f, n-1)(f(x))

  val plusTen = nTimesBetter(plusOne, 10)
  println(plusTen(1))

  // Curried function
  val superAdder: Int => (Int => Int) = (x: Int) => (y: Int) => x + y
  val add3 = superAdder(3)
  println(add3(10))
  println(superAdder(3)(10))

  // function with multiple parameter list
  def curriedFormatter(c: String)(n: Double): String = c.format(n)

  val standardFormat: (Double => String) = curriedFormatter("%4.2f")
  val preciseFormat: (Double => String) = curriedFormatter("%10.8f")

  println(standardFormat(Math.PI))
  println(preciseFormat(Math.PI))

}

package basicscala.exercise

object ExceptionWorkout extends App {
//  val outOfMemoryError = throw new OutOfMemoryError

  def createStackOverflowError(): Unit = {
    createStackOverflowError()
    println("getting back stack frames")
  }
//  createStackOverflowError()

  class MathCalculationException extends Exception
  class OverflowException extends Exception
  class UnderflowException extends Exception

  class PocketCalculator() {
    def add(x: Int, y: Int): Int =
      if(x + y < Int.MaxValue) throw new OverflowException
      else x + y

    def sub(x: Int, y: Int): Int =
      if(x - y > Int.MinValue) throw new UnderflowException
      else x - y

    def mul(x: Int, y: Int): Int = x * y
    def div(x: Int, y: Int): Int = {
      try {
        x / y
      } catch {
        case e: ArithmeticException => throw new MathCalculationException
      }
    }
  }

  val calc = new PocketCalculator
  println(calc.add(8, 2))
  println(calc.sub(8, 2))
  println(calc.mul(8, 2))
  println(calc.div(8, 2))
  try {
    println(calc.div(3, 0))
  } catch {
    case e: Exception => println("Exception")
  }

//  println(calc.add(Int.MaxValue, 8))

  println(calc.sub(Int.MinValue, 3))

}

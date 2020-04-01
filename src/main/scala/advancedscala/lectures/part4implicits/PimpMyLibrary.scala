package advancedscala.lectures.part4implicits

object PimpMyLibrary extends App {
  // 2.isPrime - Int doesn't have isPrime but through pimping it is possible in Scala

  // rule: implicit class must take exactly one argument
  // implicit class RichInt(value: Int) {
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0
    def sqrt: Double = Math.sqrt(value)
    def times(function: () => Unit): Unit = {
      def timesAux(n: Int): Unit =
        if(n <= 0) ()
        else {
          function()
          timesAux(n - 1)
        }
      timesAux(value)
    }

    def *[T](list: List[T]): List[T] = {
      def concatenate(n: Int): List[T] =
        if(n <= 0) List()
        else list ++ concatenate(n - 1)
      concatenate(value)
    }
  }

  implicit class RicherInt(richInt: RichInt) {
    def isOdd: Boolean = richInt.value % 2 != 0
  }

  new RichInt(4).isEven
  // (or)
  4.isEven

  import scala.concurrent.duration._
  2.seconds // implicit class types

  1 to 10 // implicit class concept

  // compiler doesn't do multiple implicit searches
  // 42.isOdd // compiler won't check int -> richInt -> richerInt just because of implicit

  /*
    Exercise:-
      Enrich String class
       - asInt
       - encrypt
          "John" => Lqjp

      Keep enriching Int class
       - times(function)
         3.times(() => ...)
       - *
         3 * List(1,2) => List(1,2,1,2,1,2)
   */
  implicit class RichString(value: String) {
    def asInt: Int = Integer.valueOf(value) // java.lang.Integer -> Int

    def encrypt(distance: Int): String =
      value.map(c => (c + distance).toChar)
  }
  println("3".asInt + 4)
  println("John".encrypt(2))

  3.times(() => println("Hello"))
  println(3 * List(1, 2))

  // "3" / 4
  implicit def stringToInt(value: String): Int = Integer.valueOf(value)
  println("3" / 4) // automatically converts to int

  // equivalent to implicit class RichAltInt(value: Int)
  class RichAltInt(value: Int)
  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

  // danger zone
  implicit def intToBoolean(value: Int): Boolean = value == 1

  // c style coding
  // if(1) do something
  // else do something else

  val aCondition = 1
  if(aCondition) println("Hey, C style if")
  else println("I'm not C")

  println(if(3) "OK" else "Something wrong")
}

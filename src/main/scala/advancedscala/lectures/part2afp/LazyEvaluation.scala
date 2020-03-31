package advancedscala.lectures.part2afp

object LazyEvaluation extends App {

  // LAZY delays evaluation of values
  lazy val x: Int = {
    println("hello")
    42
  }
  println(x)
  println(x)

  // examples of implications
  // example #1: side effects
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }
  def simpleCondition: Boolean = false
  lazy val lazyCondition = sideEffectCondition
  println(if(simpleCondition && lazyCondition) "yes" else "no")

  // in conjunction with call by name
  // example #2: call by need using lazy
  // def byNameMethod(n: => Int) = n + n + n + 1 can be rewritten as follows using lazy
  def byNameMethod(n: => Int) = {
    lazy val t = n
    println("waiting")
    t + t + t + 1
  }
  def retrieveMagicValue = {
    // side effects or a long computation
    Thread.sleep(1000)
    42
  }
  println(byNameMethod(retrieveMagicValue))

  // example #3: lazy vals on filters
  def lessThan30(n: Int) = {
    println(s"$n is less than 30?")
    n < 30
  }
  def greaterThan20(n: Int) = {
    println(s"$n is greater than 20")
    n > 20
  }
  val numbers = List(2, 25, 45, 23, 5)
  val lt30 = numbers.filter(lessThan30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  println()
  val lt30lazy = numbers.withFilter(lessThan30)
  val gt20lazy = lt30lazy.withFilter(greaterThan20)
  println(gt20lazy)

  // for-comprehension uses withFilter
  for {
    i <- List(1,2,3) if i % 2 == 0
  } yield i + 1
  List(1,2,3).withFilter(_ % 2 == 0).map(_ + 1)

  /*
    Exercise: Implement a lazily evaluated, singly linked STREAM of elements.
    naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite!)
    naturals.take(100).foreach(println) takes first 100 elements (infinite -> finite numbers)
    naturals.foreach(println) // will crash -  infinite!
    naturals.map(_ % 2 == 0) // stream of all even numbers (potentially infinite!)
   */
  abstract class MyStream[+A] {
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]

    def #::[B >: A](element: B): MyStream[B] // prepend a to Stream
    def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] // concatenates two streams

    def foreach(f: A => Unit): Unit
    def map[B >: A](f: A => B): MyStream[B]
    def flatMap[B >: A](f: A => MyStream[B]): MyStream[B]
    def filter(f: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A] // takes first n elements out of this stream
    def takeAsList(n: Int): List[A]
  }

  object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] = ???
  }

}

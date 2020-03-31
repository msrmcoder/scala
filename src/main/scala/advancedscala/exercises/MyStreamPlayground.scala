package advancedscala.exercises

import scala.annotation.tailrec

abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] // prepend a to Stream
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concatenates two streams

  def foreach(f: A => Unit): Unit
  def map[B >: A](f: A => B): MyStream[B]
  def flatMap[B >: A](f: A => MyStream[B]): MyStream[B]
  def filter(f: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes first n elements out of this stream
  def takeAsList(n: Int): List[A] = take(n).toList()

  /*
    [1 2 3].toList([])
     => [2 3].toList([1])
     => [3].toList([2 1])
     => [].toList([3 2 1])
     = [1 2 3]
   */
  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if(isEmpty) acc.reverse
    else tail.toList(head :: acc)
}

object EmptyStream extends MyStream[Nothing] {
  def isEmpty: Boolean = true
  def head: Nothing = throw new NoSuchElementException
  def tail: MyStream[Nothing] = throw new NoSuchElementException

  def #::[B >: Nothing](element: B): MyStream[B] = new Cons(element, this)
  def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  def foreach(f: Nothing => Unit): Unit = this
  def map[B >: Nothing](f: Nothing => B): MyStream[B] = this
  def flatMap[B >: Nothing](f: Nothing => MyStream[B]): MyStream[B] = this
  def filter(f: Nothing => Boolean): MyStream[Nothing] = this

  def take(n: Int): MyStream[Nothing] = this
}

class Cons[A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
  def isEmpty: Boolean = false
  override val head: A = hd
  override lazy val tail: MyStream[A] = tl

  def #::[B >: A](element: B): MyStream[B] = new Cons(element, this)
  /*
    [1,2,3] ++ [4,5]
     => new Cons(1, [2,3] ++ [4,5])
     => new Cons(1, new Cons(2, [3] ++ [4,5]))
     => new Cons(1, new Cons(2, new Cons(3, [] ++ [4,5])))
     => new Cons(1, new Cons(2, new Cons(3, new Cons(4, new Cons(5, EmptyStream)))))
     = [1,2,3,4,5]
   */
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons(head, tail ++ anotherStream)

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  /*
    s = new Cons(1, ?)
    mapped = s.map(_ * 2) = new Cons(2, s.tail.map(_ * 2)) // will not evaluate
    ... mapped.tail // evaluated only when this line of code found
   */
  def map[B >: A](f: A => B): MyStream[B] = new Cons(f(head), tail.map(f)) // preserves lazy eval!
  def flatMap[B >: A](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f) // preserves lazy eval
  def filter(f: A => Boolean): MyStream[A] =
    if(f(head)) new Cons(head, tail.filter(f))
    else tail.filter(f)

  def take(n: Int): MyStream[A] =
    if (n <= 0) EmptyStream
    else if (n == 1) new Cons(head, EmptyStream)
    else new Cons(head, tail.take(n - 1))
}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] =
    new Cons(start, MyStream.from(generator(start))(generator))
}

object MyStreamPlayground extends App {
  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals // equivalent to naturals.#::(0)
  println(startFrom0.head)

  startFrom0.take(10000).foreach(println)

  // map, flatMap
  println(startFrom0.map(_ * 2).take(100).toList())
  println(startFrom0.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList())

  // filtering
  // println(startFrom0.filter(_ < 10).toList()) // StackOverflowError filtering infinite numbers leads to infinite
  println(startFrom0.filter(_ < 10).take(10).toList())
  // println(startFrom0.filter(_ < 10).take(11).toList()) // leads to StackOverflowError
  println(startFrom0.filter(_ < 10).take(10).take(20).toList())

  /*
    Exercises on streams
    1. stream of Fibonacci numbers
    2. stream of prime numbers with  Eratosthenes' sieve
       [2 3 4 ....]
       filter out all numbers divisible by 2
       [2 3 5 7 9 11 ...]
       filter out all numbers divisible by 3
       [2 3 5 7 11 13 17 ...]
       filter out all numbers divisible by 5
         ...
   */
  // Exercise #1: Fibonacci
  /*
    [ first, [ ...
    [ first, fibo(second, first + second)
   */
  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] =
    new Cons(first, fibonacci(second, first + second))

  println(fibonacci(1, 1).take(100).toList())

  // Exercise #2: Eratosthenes sieve
  /*
    [2 3 4 5 6 7 8 9 10 11 12 ...
    => [2 3 5 7 9 11 13 ... =  [2 eratosthenes applied to numbers filtered by n % 2 != 0
    => [2 3 eratosthenes applied to [5 7 9 11 ...] filtered by n % 3 != 0
    => [2 3 5 eratosthenes applied to [7 9 11 ...] filtered by n % 5 != 0
   */
  def eratosthenes(numbers: MyStream[Int]): MyStream[Int] =
    if(numbers.isEmpty) numbers
    else new Cons(numbers.head, eratosthenes(numbers.tail.filter(_ % numbers.head != 0)))

  println(eratosthenes(MyStream.from(2)(_ + 1)).take(100).toList())
}


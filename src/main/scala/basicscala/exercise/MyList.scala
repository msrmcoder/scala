package basicscala.exercise

abstract class MyList[+A] {
  def head: A
  def tail: MyList[A]
  def isEmpty: Boolean
  def add[B >: A](element: B): MyList[B]
  def printElements: String
  override def toString: String = "[" + printElements + "]"
  def ++[B >: A](list: MyList[B]): MyList[B]

  def map[B](transformer: A => B): MyList[B]
  def flatMap[B](transformer: A => MyList[B]): MyList[B]
  def filter(predicate: A => Boolean): MyList[A]

  def foreach(consumer: A => Unit)
  def sort(sorter: (Int, Int) => Int): MyList[A]
  def zipWith[B >: A](list: MyList[B], fn: (A, A) => B): MyList[B]
}

case object Empty extends MyList[Nothing] {
  def head: Nothing = throw new NoSuchElementException
  def tail: MyList[Nothing] = throw new NoSuchElementException
  def isEmpty: Boolean = true
  def add[B >: Nothing](element: B): MyList[B] = new Cons(element, Empty)
  def printElements: String = ""
  def map[B](transformer: Nothing => B): MyList[B] = Empty
  def flatMap[B](transformer: Nothing => MyList[B]): MyList[B] = Empty
  def filter(predicate: Nothing => Boolean): MyList[Nothing] = Empty
  def ++[B >: Nothing](list: MyList[B]): MyList[B] = list
  def foreach(consumer: Nothing => Unit) = Empty
  def sort(sorter: (Int, Int) => Int): MyList[Nothing] = Empty
  def zipWith[B >: Nothing](list: MyList[B], fn: (Nothing, Nothing) => B): MyList[B] = list
}

case class Cons[+A](h: A, t: MyList[A]) extends MyList[A] {
  def head: A = h
  def tail: MyList[A] = t
  def isEmpty: Boolean = false
  def add[B >: A](element: B): MyList[B] = Cons(element, this)
  def printElements: String =
    if (tail.isEmpty) s"$h"
    else s"$h ${tail.printElements}"

  /*
    [1, 2, 3].filter(n % 2 == 0)
      = [2, 3].filter(n % 2 == 0)
      = Cons(2, [3].filter(n % 2 == 0)
      = Cons(2, Empty.filter(n % 2 == 0)
      = Cons(2, Empty)
   */
  def filter(predicate: A => Boolean): MyList[A] =
    if(predicate(h)) Cons(h, t.filter(predicate))
    else t.filter(predicate)

  /*
    [4, 2, 3].map(n * 2)
     = Cons(8, [2, 3].map(n * 2))
     = Cons(8, Cons(4, [3].map(n * 2)))
     = Cons(8, Cons(4, Cons(6, Empty.map(n * 2))))
     = Cons(8, Cons(4, Cons(6, Empty)))
   */
  def map[B](transformer: A => B): MyList[B] = Cons(transformer(h), t.map(transformer))

  /*
    [1, 2] ++ [3, 4, 5]
      = Cons(1, [2] ++ [3, 4, 5])
      = Cons(1, Cons(2, Empty ++ [3, 4, 5]))
      = Cons(1, Cons(2, [3, 4, 5])) // bcoz, Empty's ++ return list
      = Cons(1, Cons(2, Cons(3, Cons(4, Cons(5, Empty))))) // Actually final list as is
   */
  override def ++[B >: A](list: MyList[B]): MyList[B] = Cons(h, t ++ list)

  /*
    [1, 2].flatMap(n => [n, n+1])
      = [1, 2] ++ [2].flatMap(n => [n, n+1])
      = [1, 2] ++ [2, 3] ++ Empty.flatMap(n => [n, n+1])
      = [1, 2] ++ [2, 3] ++ Empty
      = [1, 2, 2, 3]
   */
  override def flatMap[B](transformer: A => MyList[B]): MyList[B] =
    transformer(h) ++ t.flatMap(transformer)

  def foreach(consumer: A => Unit) = {
    consumer(h)
    if(!t.isEmpty) t.foreach(consumer)
  }

  def sort(sorter: (Int, Int) => Int): MyList[A] = Empty

  def zipWith[B >: A](list: MyList[B], fn: (A, A) => B): MyList[B] = {
    Empty
  }

}

object ListTest extends  App {
  val list = Cons(1, Cons(2, Cons(3, Cons(4, Empty))))
  val list2 = Cons(6, Cons(7, Empty))
  println(list.head)
  println(list.tail.head)

  println(list.add(5).head)
  println(list.isEmpty)
  println(list.toString)

  println()
  println("map...")
  println(list)
  val func: (Int) => Int = a => a * 2
  println(list.map(func))
  println(list.map(_ * 2))

  println()
  println("filter...")
  println(list)
  println(list.filter(_ % 2 == 0))

  println()
  println("++...")
  println(list ++ list2)

  println()
  println("flatMap...")
  println(list)
  println(list.flatMap(new ((Int) => MyList[Int]) {
    override def apply(element: Int): MyList[Int] = Cons(element, Cons(element + 1, Empty))
  }))
  // shortened above statement
  val flatMapFunc: (Int) => MyList[Int] = (element: Int) => Cons(element, Cons(element + 1, Empty))
  println(list.flatMap(flatMapFunc))
  // Again, more elegant expression... They all 're the same
  println(list.flatMap(e => Cons(e, Cons(e + 1, Empty))))

  println()
  println("foreach...")
  println(list)
  list.foreach(x => println(x))

  println("for comprehension...")
  val scalaList = List(3, 2, 1, 5, 8)
  for(x <- scalaList if x % 2 == 0) println(x)
  for(x <- list) println(x)
}


package basicscala.exercise

object MyListByFPTest extends  App {
  abstract class MyList[+A] {
    def head: A
    def tail: MyList[A]
    def isEmpty: Boolean
    def add[B >: A](element: B): MyList[B]
    def printElements: String
    override def toString: String = "[" + printElements + "]"

    def map[B](transformer: MyTransformer[A, B]): MyList[B]
    def flatMap[B](transformer: MyTransformer[A, MyList[B]]): MyList[B]
    def filter(p: MyPredicate[A]): MyList[A]

    def ++[B >: A](list: MyList[B]): MyList[B]

  }

  trait MyPredicate[-T] {
    def test(element: T): Boolean
  }

  trait MyTransformer[-A, B] {
    def transform(element: A): B
  }

  case object Empty extends MyList[Nothing] {
    def head: Nothing = throw new NoSuchElementException
    def tail: MyList[Nothing] = throw new NoSuchElementException
    def isEmpty: Boolean = true
    def add[B >: Nothing](element: B): MyList[B] = new Cons(element, Empty)
    def printElements: String = ""
    def map[B](transformer: MyTransformer[Nothing, B]): MyList[B] = Empty
    def flatMap[B](transformer: MyTransformer[Nothing, MyList[B]]): MyList[B] = Empty
    def filter(p: MyPredicate[Nothing]): MyList[Nothing] = Empty
    def ++[B >: Nothing](list: MyList[B]): MyList[B] = list
  }

  case class Cons[+A](h: A, t: MyList[A]) extends MyList[A] {
    def head: A = h
    def tail: MyList[A] = t
    def isEmpty: Boolean = false
    def add[B >: A](element: B): MyList[B] = new Cons(element, this)
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
    def filter(predicate: MyPredicate[A]): MyList[A] =
      if(predicate.test(h)) Cons(h, t.filter(predicate))
      else t.filter(predicate)

    /*
      [4, 2, 3].map(n * 2)
       = Cons(8, [2, 3].map(n * 2))
       = Cons(8, Cons(4, [3].map(n * 2)))
       = Cons(8, Cons(4, Cons(6, Empty.map(n * 2))))
       = Cons(8, Cons(4, Cons(6, Empty)))
     */
    def map[B](transformer: MyTransformer[A, B]): MyList[B] =
      Cons(transformer.transform(h), t.map(transformer))

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
    override def flatMap[B](transformer: MyTransformer[A, MyList[B]]): MyList[B] =
      transformer.transform(h) ++ t.flatMap(transformer)
  }

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
  val func: MyTransformer[Int, Int] = a => a * 2
  println(list.map(func))
  println(list.map(a => a * 2))

  println()
  println("filter...")
  println(list)
  println(list.filter(n => n % 2 == 0))

  println()
  println("++...")
  println(list ++ list2)

  println()
  println("flatMap...")
  println(list)
  println(list.flatMap(new MyTransformer[Int, MyList[Int]] {
    override def transform(element: Int): MyList[Int] = new Cons(element, new Cons(element + 1, Empty))
  }))
}


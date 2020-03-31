package advancedscala.exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
  /*
    Exercise - Implement a functional set
   */
  def apply(a: A): Boolean =
    contains(a)

  def contains(a: A): Boolean
  def +(a: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A] // union

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(f: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  /*
    Exercise:
      1. remove element from set
      2. intersection with another set
      3. difference with another set
   */
  def -(a: A): MySet[A]
  def &(anotherSet: MySet[A]): MySet[A] // intersection
  def --(anotherSet: MySet[A]): MySet[A] // difference

  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {
  def contains(a: A): Boolean = false
  def +(a: A): MySet[A] = new NonEmptySet[A](a, this)
  def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  def map[B](f: A => B): MySet[B] = new EmptySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
  def filter(f: A => Boolean): MySet[A] = this
  def foreach(f: A => Unit): Unit = ()

  def -(a: A): MySet[A] = this
  def &(anotherSet: MySet[A]): MySet[A] = this
  def --(anotherSet: MySet[A]): MySet[A] = this

  def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
}

// { x in A | property(x) }
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  def contains(a: A): Boolean = property(a)

  // { x in A | property(x) } + a = { x in A | property(x) || a == x }
  def +(a: A): MySet[A] = new PropertyBasedSet[A](x => property(x) || x == a)

  // { x in A | property(x) } ++ set = { x in A | property(x) || set contains x }
  def ++(anotherSet: MySet[A]): MySet[A] = new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  // all integers => naturals.map(_ % 3) = [0,1,2] infinite numbers becomes suddenly finite numbers set hence we're not
  // sure how it works other scenarios so unable to implement map, flatMap, filter
  def map[B](f: A => B): MySet[B] = politelyFail
  def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
  def foreach(f: A => Unit): Unit = politelyFail

  def filter(f: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && f(x))
  def -(a: A): MySet[A] = filter(x => x != a)
  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)
  def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new UnsupportedOperationException("Unable to perform when property of the set is undetermined")
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  def contains(a: A): Boolean =
    head == a || tail.contains(a)

  def +(a: A): MySet[A] =
    if(this contains a) this
    else new NonEmptySet[A](a, this)

  /*
    [1, 2, 3] ++ [4, 5, 3]
      => [2, 3] ++ [4, 5, 3] + 1
      => [3] ++ [4, 5, 3] + 1 + 2
      => [] ++ [4, 5, 3] + 1 + 2 + 3
      => [4, 5, 3] + 1 + 2 + 3
      => [4, 5, 3, 1, 2]
   */
  def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  def map[B](f: A => B): MySet[B] =
    (tail map f) + f(head)

  def flatMap[B](f: A => MySet[B]): MySet[B] =
    (tail flatMap f) ++ f(head)

  def filter(f: A => Boolean): MySet[A] = {
    val filteredTail = tail filter f
    if(f(head)) filteredTail + head
    else filteredTail
  }

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }

  /*
    [1,2,3] - 2
    => [2,3] - 2 + 1
    => [3] + 1 = [3, 1]
   */
  def -(a: A): MySet[A] =
    if(a == head) tail
    else tail - a + head

  /*
    this = [1,2,3]
    anotherSet = [3,4,5]
    [1,2,3] & [3,4,5]
    =>
   */
  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet) // filter(x => anotherSet.contains(x)) - anotherSet is a
  // functional so can be reduced as above
  def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet) // filter(x => !anotherSet(x))

  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))
}

object MySet {
  /*
    val s = MySet(1,2,3)
      => buildSet(seq(1,2,3), [])
      => buildSet(seq(2,3), [] + 1)
      => buildSet(seq(3), [1] + 2)
      => buildSet(seq(), [1, 2] + 3)
      => buildSet(seq(), [1,2,3]) = [1,2,3]
   */
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if(valSeq == Seq.empty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    buildSet(values.toSeq, new EmptySet[A])
  }
}

object MySetPlayground extends App {
  def prettyPrint[A](x: A): Unit = print(x + "\t")

  val s = MySet(1, 2, 3, 4)
  println("calling foreach...")
  s.foreach(prettyPrint)

  println("\n\nadding element...")
  s + 5 foreach prettyPrint

  println("\n\nadding duplicates...")
  s + 5 + 3 foreach prettyPrint

  println("\n\nmap fn. boost by 10x...")
  s + 5 + 7 map(_ * 10) foreach prettyPrint

  println("\n\nflatMap fn. add element and its boosted by 10x...")
  s + 5 + 7 flatMap(x => MySet(x, x * 10)) foreach prettyPrint

  println("\n\nfiltering odd element...")
  s + 5 + 7 foreach prettyPrint
  println()
  s + 5 + 7 filter(_ % 2 == 1) foreach prettyPrint

  println("\n\nUnary operations...")
  println("All elements of set...")
  println(s)
  val negative = !s
  println("Negation of set i.e checks 5...")
  println("1 should not be present: " + negative(1))
  println("5 should be present: " + negative(5))
  val negativeEven = negative.filter(_ % 2 == 0)
  println("5 should not be present: " + negativeEven(5))
  println("7 should not be present: " + negativeEven(7))
  println("8 should be present: " + negativeEven(8))
  val negativeEven5 = negativeEven + 5
  println("5 should be present: " + negativeEven5(5))

//  negative.foreach(println) // throws Exception
}
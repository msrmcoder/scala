package advancedscala.lectures.part5ts

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object HigherKindedTypes extends App {

  trait AHigherKindedType[F[_]]

  trait MyList[A] {
    def flatMap[B](f: A => B): MyList[B]
  }

  trait MyOption[A] {
    def flatMap[B](f: A => B): MyOption[B]
  }

  trait MyFuture[A] {
    def flatMap[B](f: A => B): MyFuture[B]
  }

  // combine/multiply
//  def multiply[A, B](listA: List[A], listB: List[B]): List[(A, B)] =
//    for {
//      a <- listA
//      b <- listB
//    } yield (a, b)
//
//  def multiply[A, B](listA: Option[A], listB: Option[B]): Option[(A, B)] =
//    for {
//      a <- listA
//      b <- listB
//    } yield (a, b)
//
//  def multiply[A, B](listA: Future[A], listB: Future[B]): Future[(A, B)] =
//    for {
//      a <- listA
//      b <- listB
//    } yield (a, b)

  // use HKT
  trait Monad[F[_], A] {
    def flatMap[B](f: A => F[B]): F[B]
    def map[B](f: A => B): F[B]
  }
  implicit class MonadList[A](list: List[A]) extends Monad[List, A] {
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)
    override def map[B](f: A => B): List[B] = list.map(f)
  }
  implicit class MonadOption[A](option: Option[A]) extends Monad[Option, A] {
    override def flatMap[B](f: A => Option[B]): Option[B] = option.flatMap(f)
    override def map[B](f: A => B): Option[B] = option.map(f)
  }

  def multiply[F[_], A, B](implicit ma: Monad[F, A], mb: Monad[F, B]): F[(A, B)] =
    for {
      a <- ma
      b <- mb
    } yield (a, b)

  val monadList = new MonadList(List(1, 2, 3))
  monadList.flatMap(x => List(x, x + 1)) // List[Int]
  // MonadList[List, Int] => List[Int]
  monadList.map(_ * 2)
  // MonadList[List, Int] => List[Int]

  /*
   List(1, 2) * List("a", "b") => List((1, "a'), (2, "a"), (1, "b"), (2, "b"))
   */

  // after converting to implicit

//  println(multiply(new MonadList(List(1, 2)), new MonadList(List("a", "b"))))
//  println(multiply(new MonadOption(Some(2)), new MonadOption(Some("a"))))

  println(multiply(List(1, 2), List("a", "b")))
  println(multiply(Some(2), Some("a")))

  // auto coverts types which eliminates all wrapper types we created for List, Option => MonadList, MonadOption

}

package advancedscala.exercises

import advancedscala.lectures.part4implicits.TypeClasses.User

object EqualityPlayground extends App {

  // Exercise: Equality
  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }
  implicit object NameEquality extends Equal[User] {
    def apply(a: User, b: User): Boolean = a.name == b.name
  }
  object FullEquality extends Equal[User] {
    def apply(a: User, b: User): Boolean = a.email == b.email && a.name == b.name
  }
  /*
  Exercise - implement the TC pattern for the Equality tc.
 */
  // companion of Equal type class
  object Equal {
    implicit def apply[T](a: T, b: T)(implicit equalizer: Equal[T]) =
      equalizer(a, b)
  }

  val john = User("John", 23, "john@gmail.com")
  val anotherJohn = User("John", 45, "anotherJohn@gmail.com")

  println(NameEquality(anotherJohn, john))
  println(Equal(anotherJohn, john)(FullEquality))

  println(Equal(anotherJohn, john)(NameEquality))
  println(Equal(anotherJohn, john)) // call NameEquality as it is implicit AD-HOC polymorphism


  /*
    Exercise:- improve the Equal TC with an implicit conversion class
     ===(another: T)
     !==(another: T)
   */
  implicit class TypeSafeEqual[T](value: T) {
    def ===(another: T)(implicit equalizer: Equal[T]): Boolean = equalizer(value, another)
    def !==(another: T)(implicit equalizer: Equal[T]): Boolean = !equalizer(value, another)
  }

  println()
  println(john === anotherJohn)
  println(john !== anotherJohn)

  // println(john === anotherJohn(FullEquality))

  // TYPE SAFETY
  println(john == 42) // obviously wrong. but compiler can't find this error
  // println(john === 42) // TYPE SAFETY, compiler checks error

}

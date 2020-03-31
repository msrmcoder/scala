package advancedscala.lectures.part1as

object AdvancedPatternMatching extends App {
  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"The only element is $head")
    case _ =>
  }

  /*
    - constants
    - wildcards
    - tuples
    - some special magic like above
   */

  class Person(val name: String, val age: Int)
  object Person {
//    def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))
    def unapply(person: Person): Option[(String, Int)] =
      if(person.age < 21) None
      else Some((person.name, person.age))

    def unapply(age: Int): Option[String] =
      Some(if(age < 21) "minor" else "major")
  }

  val bob: Person = new Person("Bob", 25)
//  val bob: Person = new Person("Bob", 12)
  val greeting = bob match {
    case Person(name, age) => s"Hi, my name is $name and $age years old"
    case Person(_, age) if age < 21 => "Hey, I'm less than 21" // it won't work as unapply returns None
  }
  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }
  println(legalStatus)


  /*
    Exercise: Come up with elegant custom pattern match
   */
  val n: Int = 45
  val mathProperty = n match {
    case x if x < 10 => "single digit message"
    case x if x % 2 == 0 => "an even number message"
      // .. condition 1..n goes on and on and on..
    case _ => "no property message"
  }
  println(mathProperty)

  /*
    Note: in practice, if we need unapply singleton object just for condition checks, then naming convention
    must be small letter
   */

  object even {
    def unapply(arg: Int): Option[Boolean] = {
      if (arg % 2 == 0) Some(true)
      else None
    }
  }
  object singleDigit {
    def unapply(arg: Int): Option[Boolean] = {
      if (arg > -10 && arg < 10) Some(true)
      else None
    }
  }
  val anotherNumber: Int = 4
  val mathProperty1 = anotherNumber match {
    case singleDigit(_) => "another single digit"
    case even(_) => "an another even number"
    // .. condition 1..n goes on and on and on..
    case _ => "another no property"
  }
  println(mathProperty1)

  // Another cleaner way to rewrite the above code as follows
  object cleanerEven {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }
  object cleanerSingleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }
  val cleanNumber = 53
  val cleanMathProperty = cleanNumber match {
    case cleanerSingleDigit() => "clean single digit"
    case cleanerEven() => "clean even number"
    case _ => "clean no property"
  }
  println(cleanMathProperty)


  // infix patterns
  case class Or[A, B](a: A, b: B)
  val or: Or[Int, String] = Or(2, "two")
  val humanDescription = or match {
//    case Or(number, string) => s"$number is written as $string" // equivalent to
    case number Or string => s"$number is written as $string"
  }
  println(humanDescription)

  // decomposing sequence
  val vararg = numbers match {
    case List(1, _*) => "Starting with 1"
  }
  // Lets create a custom one
  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }
  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]
  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if(list == Empty) Some(Seq.empty)
//      else unapplySeq(list.tail).map(list.head +: _)
      else unapplySeq(list.tail).map(elem => list.head +: elem)
  }
  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  println(myList)
  val decomposed = myList match {
    case MyList(1, 2, _*) => "starting with 1, 2"
    case _ => "something else"
  }
  println(decomposed)

  // custom return types for unapply
  // implementing these two methods: isEmpty: Boolean, get: something
  // used for pattern matching value destructing
  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }
  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty: Boolean = false
      def get: String = person.name
    }
  }

  println(bob match {
    case PersonWrapper(name) => s"This person's name is $name"
    case _ => "An alien"
  })

}

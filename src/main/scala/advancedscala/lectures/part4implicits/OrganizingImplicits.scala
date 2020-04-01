package advancedscala.lectures.part4implicits

object OrganizingImplicits extends App {

  // scala.Predef - Ordering used from here
  // reverseOrdering takes precedence over scala.Predef hence, list printed in reverse
  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan((x, y) => x > y)
  // implicit def reverseOrdering: Ordering[Int] = Ordering.fromLessThan((x, y) => x > y)
  // implicit def reverseOrdering(): Ordering[Int] = Ordering.fromLessThan((x, y) => x > y) // won't apply
  // implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)
  // Now, won't work as compiler confused due to too many implicits to be used on the same
  // search scope
  println(List(3, 1, 5, 9, 7, 2).sorted)

  /*
    Implicits (used as implicit parameter)
      - var/val
      - object
      - accessor methods = def with no parentheses
   */

  // Exercise
  case class Person(name: String, age: Int)
  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )
  //  object Person {
  //    implicit val alphabetingOrdering = Ordering.fromLessThan[Person](_.name < _.name)
  //  }
  //  implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
  //  println(persons.sorted)

  /*
    Implicit scopes
     - normal scope = LOCAL SCOPE
     - imported scope
     - companions of all types involved in method signature
        example: def sorted[B >: A](implicit ord: Ordering[B]): List[B]
        - List
        - Ordering
        - all types involved = A or any super type
   */
  object AlphabetingOrdering {
    implicit val alphabetingOrdering = Ordering.fromLessThan[Person](_.name < _.name)
  }
  object AgeOrdering {
    implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
  }
  import AgeOrdering._
  println(persons.sorted)

  /*
    Exercise.
      Defining ordering based on below criteria
      - totalPrice = most used (50%)
      - by unit count = 25%
      - by unit price = 25%
   */
  case class Purchase(nUnits: Int, unitPrice: Double)

  val purchases = List(
    Purchase(10, 25.5),
    Purchase(15, 12.42),
    Purchase(5, 205.25)
  )

  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((p1, p2) => p1.nUnits * p1.unitPrice < p2.nUnits * p2.unitPrice)
  }
  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits < _.nUnits)
  }
  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }

  println(purchases.sorted)
}

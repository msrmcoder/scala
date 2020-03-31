package basicscala.lectures.part2oop

import javax.naming.ldap.PagedResultsControl

object Objects extends App {

  // object can't take parameters
  object Person {
    // "static"/"class" - level functionality
    val N_EYES = 2
    def canFly: Boolean = false

    // Factory method
    def apply(mother: Person, father: Person) = new Person("Bobbie")
  }

  // class takes parameters
  class Person(name: String) {
    // instance-level functionality
  }

  // Companions if class/instance level on same scope

  println(Person.N_EYES)
  println(Person.canFly)

  // object is Singleton
  val mary = Person
  val john = Person
  println(mary == john)

  val rose = new Person("Rose")
  val david = new Person("David")
  println(rose == david)


  val bobbie = Person(rose, david)
  println(bobbie)
}

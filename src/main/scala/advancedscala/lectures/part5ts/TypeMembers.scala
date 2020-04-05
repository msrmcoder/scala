package advancedscala.lectures.part5ts

object TypeMembers extends App {

  class Animal
  class Cat extends Animal
  class Dog extends Animal

  class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal // abstract upper bounded Animal - means anything extends Animal
    type SuperBoundedAnimal >: Dog <: Animal // abstract lower bound to Dog and upper bound to Animal
    type AnimalC = Cat // alias
  }

  val ac = new AnimalCollection

  val dog: ac.AnimalType =  ???
  // val newDog: ac.BoundedAnimal = new Dog // Error
  val pup: ac.SuperBoundedAnimal = new Dog

  val cat: ac.AnimalC = new Cat

  type catAlias = Cat
  val anotherCat: catAlias = new Cat

  trait MyList {
    type T
    def add(element: T): MyList
  }

  class NonEmptyList extends MyList {
    type T = Int
    def add(element: Int): MyList = ???
  }

  // .type
  type CatsType = cat.type
  // val newCat: CatsType = cat
  // new CatsType


  /*
    Exercise: enforce a type to be applicable to SOME TYPES only
   */
  // Locked - API got from other developers
  trait MList {
    type T
    def head: T
    def tail: MList
  }

  trait ApplicableToNumber {
    type T <: AnyVal
  }

  // Not good design, create a custom list and restrict only numeric list to be created

  // It won't work as it derived from ApplicableToNumber
//  class CustomList(hd: String, tl: CustomList) extends MList with ApplicableToNumber {
//    type T = String
//    def head = hd
//    def tail = tl
//  }

  // OK
  class IntList(hd: Int, tl: IntList) extends MList with ApplicableToNumber {
    type T = Int
    def head = hd
    def tail = tl
  }

  // Number
  // type member and type member constraints (bounds)

}

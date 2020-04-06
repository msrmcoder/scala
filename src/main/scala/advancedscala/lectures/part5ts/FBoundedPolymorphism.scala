package advancedscala.lectures.part5ts

object FBoundedPolymorphism extends App {

  // F-Bounded Polymorphism

//  trait Animal {
//    def breed: List[Animal]
//  }
//  class Dog extends Animal {
//    def breed: List[Animal] = ??? // List[Dog]
//  }
//  class Cat extends Animal {
//    def breed: List[Animal] = ??? // List[Cat]
//  }

  /*
    Want to find a way to enforce return type of breed method from List[Animal] to List[Cat | Dog | etc.] as per subtype
    of Animal
   */

  // Solution - 1 Manually taking care

  // No enforcement at compiler level, so it may leads to human mistake when defining a new type
//  trait Animal {
//    def breed: List[Animal]
//  }
//  class Dog extends Animal {
//    def breed: List[Dog] = ???
//  }
//  class Cat extends Animal {
//    def breed: List[Cat] = ??? // return type can be as List[Dog], still it valid and compiles
//  }

  // Solution - 2 FBP

//  trait Animal[A <: Animal[A]] { // F-Bounded Polymorphism
//    def breed: List[Animal[A]]
//  }
//  class Dog extends Animal[Dog] {
//    def breed: List[Animal[Dog]] = ???
//  }
//  class Cat extends Animal[Cat] {
//    def breed: List[Animal[Cat]] = ???
//  }

//  trait Entity[E <: Entity[E]] // ORM
//
//  class Crocodile extends Animal[Dog] { // still compiles and not ok, we expect return type must be of List[Crocodile]
//    def breed: List[Animal[Dog]] = ???
//  }

  // Solution 4 - FBP + self-types
//  trait Animal[A <: Animal[A]] { self: A =>
//    def breed: List[Animal[A]]
//  }
//  class Dog extends Animal[Dog] {
//    def breed: List[Animal[Dog]] = ???
//  }
//  class Cat extends Animal[Cat] {
//    def breed: List[Animal[Cat]] = ???
//  }

//  class Crocodile extends Animal[Dog] { // compiler not happy, we restricted the type.
//    def breed: List[Animal[Dog]] = ???
//  }

  // FBP + SelfType = has limitation, look out below example
//  trait Fish extends Animal[Fish]
//  class Shark extends Fish {
//    override def breed: List[Animal[Fish]] = List(new Cod) // wrong
//  }
//  class Cod extends Fish {
//    override def breed: List[Animal[Fish]] = ???
//  }

  // Exercise

  // Solution 5 - type classes!

//  trait Animal
//  trait CanBreed[A] {
//    def breed(a: A): List[A]
//  }
//  class Dog extends Animal
//  object Dog {
//    implicit object DogsCanBreed extends CanBreed[Dog] {
//      def breed(dog: Dog): List[Dog] = ???
//    }
//  }
//
//  implicit class CanBreedOps[A](animal: A) {
//    def breed(implicit canBreed: CanBreed[A]): List[A] =
//      canBreed.breed(animal)
//  }
//
//  val dog = new Dog
//  dog.breed
//
//  class Cat extends Animal
//  object Cat {
//    implicit object CatsCanBreed extends CanBreed[Dog] {
//      override def breed(a: Dog): List[Dog] = ???
//    }
//  }
//
//  val cat = new Cat
//  cat.breed // No implicit found error

  // Solution - 6

  trait Animal[A] {
    def breed(a: A): List[A]
  }
  class Dog
  implicit object Dog extends Animal[Dog] {
    override def breed(a: Dog): List[Dog] = ???
  }
  implicit class AnimalOps[A](animal: A) {
    def breed(implicit animalInstance: Animal[A]): List[A] = animalInstance.breed(animal)
  }
  val dog = new Dog
  dog.breed

  class Cat
  object Cat extends Animal[Dog] {
    def breed(a: Dog): List[Dog] = ???
  }

  val cat = new Cat
  // cat.breed // get compiler error because of the wrong implementation of companion object of Cat

}

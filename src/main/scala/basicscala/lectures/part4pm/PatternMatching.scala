package basicscala.lectures.part4pm

import scala.util.Random

object PatternMatching extends App {

  val random = new Random
  val number = random.nextInt(10)

  val description = number match {
    case 1 => "One"
    case 2 => "Two"
    case 3 => "Three"
    case _ => "Something else"
  }
  println(number)
  println(description)

  case class Person(name: String, age: Int)
  val bob = Person("Bob", 20)
  val greetings = bob match {
    case Person(n, a) if a < 21 => s"I'm $n and I can't drink in the US"
    case Person(n, a) => s"Hi, I'm $n and I'm' $a years old"
    case _ => "I don't know who I am"
  }
  println(greetings)
  /*
   1. cases are matched in order
   2. what if no cases matched? - MatchError
   3. type of pattern match expression? - unified type of all the types in all the cases
   */

  // PM on sealed hierarchy
  sealed class Animal
  case class Dog(breed: String) extends Animal
  case class Cat(greet: String) extends Animal
  val animal:Animal = Dog("Terra Nova")
  animal match {
    case Dog(someBreed) => println(s"Match dog of $someBreed breed")
  }



}

package advancedscala.lectures.part1as

import scala.annotation.tailrec

object Recap extends App {

  val aCondition: Boolean = false
  val aConditionalVal = if(aCondition) 42 else 1
  // type inferred by compiler
  // expression over statement

  val codeBlock = {
    if(aCondition) 23
    42 // will be returned
  }

  def aMethod = 1 + 2

  @tailrec
  def factorial(n: Int, acc: Int = 1): Int = {
    if(n <= 0) acc
    else factorial(n -1, n * acc)
  }

  println(factorial(5))

  // oop
  trait Animal
  class Dog extends Animal
  val dog: Animal = new Dog

  // case classes - already companion object


}

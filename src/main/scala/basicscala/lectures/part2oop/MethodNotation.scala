package basicscala.lectures.part2oop

import scala.language.postfixOps

object MethodNotation extends  App {

  val joe = new Person("Joe", 12)
  println(joe.age)

  val john = new Person("John", 14)

  // infix notation - if a method takes just one parameter,
  // we can write without . and parentheses
  joe greet "Welcome to Scala"

  // invoke unary operator
  println(+joe)

  println(john.+(joe))
  println(john + joe) // equivalent


  // 1. Infix
  (joe + "Phew").print

  // 2. Prefix - unary
  val newJoe = +joe
  newJoe.print

  // 3. Postfix
  val mary = new Person("Mary", 20)
  mary learns "Scala"
  mary learnScala

  // 4. apply()
  mary(5)

  class Person(val name: String, val age: Int=0) {
    def greet(message: String) = println(s"Hello $name, $message")
    def +(person: Person) = s"$name welcomes ${person.name}"
    def +(nickname: String) = new Person(s"$name ($nickname)", age)
    def unary_+ : Person = new Person(name, age + 1)
    def print = println(s"My name is $name and I'm $age year old!")
    def learns(subject: String) = println(s"$name learns $subject")
    def learnScala = this learns "Scala"
    def apply(times: Int) = println(s"$name watched movie $times times")
  }
}


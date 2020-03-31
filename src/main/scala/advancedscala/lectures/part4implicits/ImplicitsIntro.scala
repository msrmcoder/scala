package advancedscala.lectures.part4implicits

object ImplicitsIntro extends App {
  val pair = "Sriram" -> "555"
  val intPair = 2 -> 6 // -> is implicits

  case class Person(name: String) {
    def greet: String = s"Hi, my name is $name!"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)

  // greet is not part of "String" class but still works of Scala's powerful feature called "Implicits"
  println("Peter".greet) // compiler converts to println(fromStringToPerson("Peter").greet)

  // if compiler see one more implicit type with same method, it won't work
  //  class A {
  //    def greet: Int = 42
  //  }
  //  implicit def fromStringToA(str: String): A = new A

  // implicit parameter
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount = 10

   println(increment(5))

}

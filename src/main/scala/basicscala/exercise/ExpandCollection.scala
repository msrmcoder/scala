package basicscala.exercise

object ExpandCollection extends App {
  trait MyPredicate[T] {
    def test(t: T): Boolean
  }

  val nameTest = new MyPredicate[String] {
    def test(str: String): Boolean = str.toLowerCase.startsWith("s")
  }

  val evenNumberTest = new MyPredicate[Int] {
    def test(number: Int): Boolean = number % 2 == 0
  }
  println(nameTest.test("Sriram"))
  println(nameTest.test("Brathean"))
  println(evenNumberTest.test(14))


}

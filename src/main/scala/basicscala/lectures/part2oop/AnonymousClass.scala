package basicscala.lectures.part2oop

object AnonymousClass extends App {

  abstract class Animal {
    def eat: Unit
  }

  val animal: Animal = new Animal {
    override def eat: Unit = println("ahahah")
  }

  animal.eat
}

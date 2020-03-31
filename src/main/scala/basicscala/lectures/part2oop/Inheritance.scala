package basicscala.lectures.part2oop

object Inheritance extends App {

  // Single class inheritance
  sealed class Animal {
    val creatureType = "wild"
    protected def eat = println("eating")
  }

  class Cat extends Animal {
    def crunch =  {
      eat
      println("crunch crunch")
    }
  }

  // protected - accessible only in subclass
  // private - not accessible outside of class

  val cat = new Cat
  cat.crunch

  // Constructors
  class Person(name: String, age: Int) {
    def this(name: String) = this(name, 0)
  }
  class Adult(name: String, age: Int, idCard: String) extends Person(name)

  // Overriding
  class Dog(override val creatureType: String) extends Animal {
    override def eat = println("Dog eats")
  }

  val dog = new Dog("K9 ")
  dog.eat
  println(dog.creatureType)


  // super keyword

  // override vs overload

  // preventing override
  // final - on members
  // final - on entire class
  // sealed - on the file when types are known and
  // want to restrict to extend outside of the file

}

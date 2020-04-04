package advancedscala.lectures.part5ts

object RockingInheritance extends App {

  // convenience
  trait Writer[T] {
    def write(value: T): Unit
  }
  trait Closeable {
    def close(status: Int): Unit
  }
  trait GenericStream[T] {
    // some methods
    def foreach(f: T => Unit): Unit
  }

  // 1. mixin
  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // 2. diamond problem
  trait Animal { def name: String }
  trait Tiger extends Animal { override def name: String = "tiger" }
  trait Lion extends Animal { override def name: String = "lion" }
  class Mutant extends Lion with Tiger
  val m = new Mutant
  println(m.name)

  /*
    Mutant extends Animal with { override def name: String = "lion" }
    extends Animal with { override def name: String = "tiger" }

    Last override gets picked
   */

  // 3. the super problem + type linearization
  trait Cold {
    def print = println("cold")
  }
  trait Green extends Cold {
    override def print = {
      println("green")
      super.print
    }
  }
  trait Blue extends Cold {
    override def print = {
      println("blue")
      super.print
    }
  }
  class Red {
    def print = println("red")
  }

  class White extends Red with Green with Blue {
    override def print = {
      println("white")
      super.print
    }
  }
  val color = new White
  color.print

  /*
  Explanation: who's my super?

    Inheritance hierarchy | Compiler expansion
                          |
               Cold       | Cold  = AnyRef with <Cold>
                |         |
             ___+___      | Green = Cold with <Green>
             |     |      |       = AnyRef with <Cold> with <Green>
             |     |      |
     Red   Green  Blue    | Blue  = Cold with <Blue>
       |     |     |      |       = AnyRef with <Cold> with <Blue>
       |     |     |      |
       +-----+-----+      | Red   = AnyRef with <Red>
             |            |
           White          | White = Red with Green with Blue with <White>
                          |       = AnyRef with <Red>
                          |         with (AnyRef with <Cold> with <Green>)
                          |         with (AnyRef with <Cold> with <Blue>)
                          |         with AnyRef with <White>
                          |
                          | Note: When compiler see any reference second time, just skips it, hence as follows
                          |
                          | White = AnyRef with <Red> with <Cold> with <Green> with <Blue> with <White>
                          |     This expression is called type linearization, Now, at runtime, super.print calls from
                          | left to right (White -> AnyRef). That skips the call of Red.super.print method invocation
                          | This is a "Super problem" which resolved by type linearization
                          | output:
                          |  white
                          |  blue
                          |  green
                          |  cold
   */

}

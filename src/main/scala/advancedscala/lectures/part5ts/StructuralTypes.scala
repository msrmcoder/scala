package advancedscala.lectures.part5ts

object StructuralTypes extends App {

  // structural type
  type JavaCloseable = java.io.Closeable

  class HipsterCloseable {
    def close(): Unit = println("yeah yeah, I'm closing")
    def closeSilently(): Unit = println("not making a sound")
  }

  // Usage 1:-
  // def close(closeable: JavaCloseable OR HipsterCloseable) // how to do this??

  type UnifiedCloseable = {
    def close(): Unit
  } // STRUCTURAL TYPE

  // Now we can any type which has close()
  def closeQuietly(unifiedCloseable: UnifiedCloseable): Unit = unifiedCloseable.close()

  closeQuietly(new JavaCloseable {
    override def close(): Unit = ???
  })
  closeQuietly(new HipsterCloseable)

  // Usage 2:- Type refinement
  type AdvancedCloseable = JavaCloseable {
    def closeSilently(): Unit
  }
  class AdvancedJavaCloseable extends AdvancedCloseable {
    def close(): Unit = println("Java closes")
    def closeSilently(): Unit = println("Java closes silently")
  }
  def closeShh(advancedJavaCloseable: AdvancedJavaCloseable): Unit = advancedJavaCloseable.closeSilently()
  closeShh(new AdvancedJavaCloseable)
  // closeShh(new HipsterCloseable) // Error: Type mismatch

  // using structural types as standalone types
  def close(closeable: {def close(): Unit}): Unit = closeable.close()

  // type-checking => duck typing

  type SoundMaker = {
    def makeSound()
  }
  class Dog {
    def makeSound(): Unit = println("bark!")
  }
  class Car {
    def makeSound(): Unit = println("vrooom!")
  }
  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car

  // static duck typing
  // CAVEAT: based on reflection

  /*
    Exercise: 1. Cons Based List
   */
  trait CBL[+T] {
    def head: T
    def tail: CBL[T]
  }
  class Human {
    def head: Brain = new Brain
  }
  class Brain {
    override def toString: String = "BRAINZ!"
  }

  def f[T](somethingWithHead: { def head: T }): Unit = somethingWithHead.head

  /*
    f is compatible with CBL and with a Human? Yes.
   */
  case object CBNil extends CBL[Nothing] {
    def head: Nothing = ???
    def tail: CBL[Nothing] = this
  }
  case class CBCons[T](override val head: T, override val tail: CBL[T]) extends CBL[T]
  f(new CBCons(2, CBNil))
  f(new Human) // ?? T = Brain!

  // 2.
  object HeadEqualizer {
    type Headable[T] = {
      def head: T
    }

    def ===[T](a: Headable[T], b: Headable[T]): Boolean = a.head == b.head
  }

  /*
    === is compatible with CBL and with a Human? Yes.
   */
  val brainzList = CBCons(new Brain, CBNil)
  HeadEqualizer.===(brainzList, new Human)

  val stringsList = CBCons("Hello", CBNil)
  // problem
  HeadEqualizer.===(stringsList, new Human) // not type safe
  // because Scala compiler uses reflection erasure which removes type
  // safe [T] from ==== method definition



}

package advancedscala.lectures.part5ts

object PathDependentTypes extends App {

  class Outer {
    class Inner
    object InnerObject
    type InnerType // generic type

    def print(i: Inner) = println(i)

    def printGeneral(i: Outer#Inner) = println(i)

  }

  def defMethod: Int = {
    class HelperClass
    type HelperType = String // generic type can't be created inside method,
    42
  }

  // per-instance
  val o = new Outer
  val inner = new o.Inner //  o.Inner is a TYPE

  val oo = new Outer
  // val i: oo.Inner = new o.Inner// Error: it is not allowed
  // o.Inner is different from oo.Inner

  o.print(inner)

  // oo.print(inner) // Error: it is not allowed

  // path-dependent types

  // Outer#Inner

  o.printGeneral(inner)
  oo.printGeneral(inner)

  /*
    Exercise:
      DB keyed by Int or String, but maybe other types need to develop

      Hint:
       - use path-dependent
       - abstract typ members and/or type aliases
   */

  trait ItemLike {
    type Key
  }

  trait Item[T] extends ItemLike {
    type Key = T
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  get[IntItem](42) // OK
  get[StringItem]("hello") // OK
  // get[IntItem]("42") // Not OK

































}

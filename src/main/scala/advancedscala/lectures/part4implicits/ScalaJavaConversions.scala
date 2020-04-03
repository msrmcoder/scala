package advancedscala.lectures.part4implicits

import java.{util => ju }

object ScalaJavaConversions extends App {

  // scala to java and vice versa is frequent need for developers

  import collection.JavaConverters._

  val javaSet: ju.Set[Int] = new ju.HashSet[Int]
  (1 to 5).foreach(javaSet.add)
  println(javaSet)

  val scalaSet = javaSet.asScala
  println(scalaSet)

  /*
    Iterator
    Iterable
    ju.List - collection.mutable.Buffer
    ju.Set - collection.mutable.Set
    ju.Map - collection.mutable.Map
   */

  import collection.mutable._

  val scalaNumberBuffer = ArrayBuffer[Int](1, 2, 3)
  val javaNumberBuffer = scalaNumberBuffer.asJava

  println(javaNumberBuffer.asScala eq scalaNumberBuffer)

  // not always true for all java <-> scala conversion
  // e.g
  val numbers = List(1, 2, 3)
  val juNumbers = numbers.asJava
  val backToScala = juNumbers.asScala

  // FALSE! - bcoz, numbers (immutable) and backToScala (mutable) as it is converted
  // from juNumbers (in Java, List is mutable)
  println(numbers eq backToScala) // false
  println(numbers == backToScala) // true

  // However, try adding element to juNumbers throws error at runtime
  // juNumbers.add(7)

  /*
    Exercise:
      create a Scala-Java Optional-Option
        .asScala
   */
  class ToScala[T](value: => T) {
    def asScala: T = value
  }
  implicit def asScalaOptional[T](o: ju.Optional[T]): ToScala[Option[T]] = new ToScala[Option[T]](
    if(o.isPresent) Some(o.get()) else None
  )

  val juOptional: ju.Optional[Int] = ju.Optional.of(3)
  val scalaOption = juOptional.asScala
  println(juOptional)
  println(scalaOption)

}

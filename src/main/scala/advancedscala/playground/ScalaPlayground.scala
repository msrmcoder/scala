package advancedscala.playground

import scala.collection.parallel.CollectionConverters._

object ScalaPlayground extends App {
  println("Hello, Scala")
  val list: List[Int] = List(1, 2, 3, 4, 5)
  println(list)
  println(list.par)
}

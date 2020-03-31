package advancedscala.lectures.part3concurrency

import scala.collection.parallel.CollectionConverters._

object ParallelUtils extends App {

  val parList = List(1, 2, 3)

  val parVector = ParVector[Int](1, 2, 3) // par is used to make parallel
}

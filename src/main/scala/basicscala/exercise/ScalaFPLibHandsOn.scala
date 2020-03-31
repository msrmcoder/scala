package basicscala.exercise

object ScalaFPLibHandsOn extends App {

  val ints = List(1, 2, 3, 4)
  val chars = List('a', 'b', 'c', 'd')
  chars.flatMap(x => ints.map(y => x.toString + y)).foreach(println)
}

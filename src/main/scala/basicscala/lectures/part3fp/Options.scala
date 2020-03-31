package basicscala.lectures.part3fp

import scala.util.Random

object Options extends App {
  def unsafeMethod: String = null

  def backMethod: String = "A valid result"

  val result = unsafeMethod
  if (result != null)
    println(result.toUpperCase)

  val resultOption: Option[String] = Option(unsafeMethod)
  resultOption.map(str => println(str))
    .orElse(Option(backMethod)
      .map(str => println(str))
    )

  val config: Map[String, String] = Map(
    "host" -> "174.21.23.1",
    "port" -> "80"
  )

  class Connection {
    def connect: String = "Connected"
  }

  object Connection {
    val random = new Random
    def apply(host: String, port: String): Option[Connection] = {
      if(random.nextBoolean()) Some(new Connection)
      else None
    }
  }

  val connection = config.get("host").flatMap(h => config.get("port").flatMap(p => Connection(h, p)))
  val connectionStatus: Option[String] = connection.map(c => c.connect)
  println(connectionStatus)
  connectionStatus.foreach(println)

  // we can do this in chained method
  config.get("host")
    .flatMap(h => config.get("port")
      .flatMap(p => Connection(h, p))
      .map(c => c.connect))
    .foreach(println)

  // for-comprehensions
  val forConnectionStatus = for {
    h <- config.get("host")
    p <- config.get("port")
    c <- Connection(h, p)
  } yield c.connect
  forConnectionStatus.foreach(println)
}

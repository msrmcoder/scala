package basicscala.lectures.part3fp

import scala.util.{Failure, Random, Success, Try}

object HandlingFailures extends App {

  val aSuccess = Success(4)
  val aFailure = Failure(new RuntimeException("Super Failure!!!"))
  println(aSuccess)
  println(aFailure)

  def unsafeMethod: String = throw new RuntimeException("I can't give you a string")

  def backupMethod: String = "A valid result"

  val value = Try(unsafeMethod)
  println(value)

  // syntax sugar
  val anotherPotentialFailure = Try {
    // code that might throw
  }

  // utilities
  println(value.isSuccess)
  val fallbackTry = Try(unsafeMethod).orElse(Try(backupMethod))
  println(fallbackTry)

  // if you design API
  def betterUnsafeMethod: Try[String] = Failure(new RuntimeException)
  def betterBackupMethod: Try[String] = Success("A valid result")
  val betterFallbackTry = betterUnsafeMethod orElse betterBackupMethod

  println(aSuccess.map(_ * 2))
  println(aSuccess.flatMap(x => Success(x * 10)))
  println(aSuccess.filter(_ > 10))

  // for-comprehension can be used as Try has filter, map, flatMap

  // Problem
  val (host, port) = ("localhost", 8080)
  def renderHTML(page: String) = println(page)

  class Connection {
    def get(url: String): String = {
      val random = new Random(System.nanoTime())
      if(random.nextBoolean()) "<html>...</html"
      else throw new RuntimeException("Connection interrupted")
    }

    def getSafe(url: String): Try[String] = Try(get(url))
  }

  object HttpService {
    def getConnection(host: String, port: Int) = {
      val random = new Random(System.nanoTime())
      if(random.nextBoolean()) new Connection
      else throw new RuntimeException("Someone else took the port")
    }

    def getSafeConnection(host: String, port: Int): Try[Connection] = Try(getConnection(host, port))
  }

  println("Rendering page (using Try)...")
  Try(HttpService.getConnection(host, port))
    .map(c => Try(c.get("index.html")))
    .flatMap(page => page.map(renderHTML))

  println("Rendering page (API provided Try)...")
  val possibleConnection = HttpService.getSafeConnection(host, port)
  val page = possibleConnection.flatMap(connection => connection.getSafe("/index.html"))
  page.foreach(renderHTML)

  println("Rendering page (for-comprehension)...")
  // for-comprehend version
  for {
    connection <- HttpService.getSafeConnection(host, port)
    page <- connection.getSafe("/index.html")
  } yield renderHTML(page)

}

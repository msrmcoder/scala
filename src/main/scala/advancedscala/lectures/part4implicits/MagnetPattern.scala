package advancedscala.lectures.part4implicits

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

object MagnetPattern extends App {

  class P2PRequest
  class P2PResponse
  class Serializer[T]

  // method overloading
  trait Actor {
    def receive(statusCode: Int): Int
    def receive(request: P2PRequest): Int
    def receive(response: P2PResponse): Int
    def receive[T : Serializer](message: T): Int
    def receive[T : Serializer](message: T, statusCode: Int): Int
    def receive(future: Future[P2PRequest]): Int
    // def receive(future: Future[P2PResponse]): Int // TYPE Erasure
    // lots of overload
  }

  // above designing of API makes lot of problems
  /*
    Problems:
      1. Type erasure
      2. lifting doesn't work for all overloads

           val receiveFV = receive _ // ?!

      3. code duplication
      4. type inference - default arguments

           actor.receive(?!) // compiler can't infer which overloaded method to be invoked
   */

  /*
    apply magnet pattern
     - magnet defines the overload contract
     - overloaded functionality can be defined as implicit type instances
     -
  */
  trait MessageMagnet[R] {
    def apply(): R
  }

  def receive[R](magnet: MessageMagnet[R]): R = magnet()

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    def apply(): Int = {
      // logic for handling a P2PRequest
      println("Handling P2P request")
      42
    }
  }
  implicit class FromP2PResponse(response: P2PResponse) extends MessageMagnet[Int] {
    def apply(): Int = {
      // logic for handling a P2PResponse
      println("Handling P2P response")
      24
    }
  }
  receive(new P2PRequest) // automatically type inferred picked right implementation
  receive(new P2PResponse)

  // 1. no more type erasure problem
  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply(): Int = 2
  }
  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = 3
  }

  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))

  // 2. lifting
  trait MathLib {
    def add1(n: Int): Int = n + 1
    def add1(s: String): Int = s.toInt + 1
    // lot more overloads
  }

  trait AddMagnet {
    def apply(): Int
  }

  def add1(magnet: AddMagnet): Int = magnet()
  implicit class AddInt(n: Int) extends AddMagnet {
    def apply(): Int = n + 1
  }
  implicit class AddString(s: String) extends AddMagnet {
    def apply(): Int = s.toInt + 1
  }
  val addFV = add1 _
  println(addFV(1))  // lifting happens automatically
  println(addFV("3"))

  val receiveFV = receive _

  // There is a catch, AddMagnet is not defined as Type
  // because, MessageMagnet result type is unknown defined as [R]
  // which prevents to write "lift" expression like "add1 _" for "receive _"
  // Pattern: addFV: MagnetPattern.AddMagnet => Int
  // Pattern: receiveFV: MagnetPattern.MessageMagnet[Nothing] => Nothing


  /*
    Drawbacks of magnet pattern:
      1. verbose
      2. hard to read
      3. you can't name or place default arguments
         e.g: receive() // not possible, it must take arg
      4. call by name doesn't work correctly
   */

  // 4. call by name doesn't work correctly
  class Handler {
    def handle(s: => String) = {
      println(s)
      println(s)
    }
    // other overloads
  }

  // magnet pattern
  trait HandleMagnet {
    def apply(): Unit
  }
  def handle(magnet: HandleMagnet): Unit = magnet()
  implicit class StringHandle(s: => String) extends HandleMagnet {
    def apply(): Unit = {
      println(s)
      println(s)
    }
  }
  def sideEffectString(): String = {
    println("Hello, Scala")
    "hahaha"
  }

  // handle(sideEffectString())

  // Hello Scala prints only once, side effects not working properly with magnet
  handle {
    println("Hello, Scala")
    "hahaha"
  }

}

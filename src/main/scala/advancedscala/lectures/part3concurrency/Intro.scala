package advancedscala.lectures.part3concurrency

import java.util.concurrent.{Executor, Executors}

object Intro extends App {
  private val runnable = new Runnable() {
    override def run(): Unit = println("Running in parallel")
  }
  // JVM threads
  val aThread = new Thread(runnable)
  //  aThread.start() // parallel execution
  //  runnable.run() // no parallel execution - just a method invocation
  //  aThread.join()
  println("Intro thread")

  val threadHello = new Thread(() => (1 to 5).foreach(x => println("Hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(x => println("Good bye")))
  //  threadHello.start()
  //  threadGoodbye.start()

  // Executors
  val pool = Executors.newFixedThreadPool(10)
  //  pool.execute(() => println("Something in thread pool"))

  //  pool.execute(() => {
  //    Thread.sleep(1000)
  //    println("done after 1 seconds")
  //  })
  //
  //  pool.execute(() => {
  //    Thread.sleep(1000)
  //    println("almost done")
  //    Thread.sleep(2000)
  //    println("done after 2 seconds")
  //  })

  pool.shutdown()

  //  pool.shutdownNow() // force shutdown even if threads are running inside JVM
  println(pool.isShutdown)

  // race condition #1
  def runInParallel = {
    var x = 0

    val t1 = new Thread(() => {
      x = 1
    })
    val t2 = new Thread(() => {
      x = 2
    })
    //    t1.start()
    //    t2.start()
    //    println(x)
  }

  for (_ <- 1 to 10000) runInParallel

  // race condition #2
  class BankAccount(@volatile var amount: Int) {
    override def toString: String = amount.toString
  }

  def buy(account: BankAccount, thing: String, price: Int) = {
    account.amount -= price
    //    println(s"I've bought $thing")
    //    println(s"My account bal is $account")
  }

//  for (_ <- 1 to 1000) {
//    val account = new BankAccount(50000)
//    val t1 = new Thread(() => buy(account, "Shoe", 3000))
//    val t2 = new Thread(() => buy(account, "iPhone", 4000))
//
//    t1.start()
//    t2.start()
//    Thread.sleep(10)
//    if (account.amount != 43000) println("AHA: " + account)
//    //    println()
//  }

  // solution
  // option #1: synchronized()
  def buySafe(account: BankAccount, thing: String, price: Int) = {
    account.synchronized {
      account.amount -= price
//      println(s"I've bought $thing")
//      println(s"My account bal is $account")
    }
  }

//  for (_ <- 1 to 1000) {
//    val account = new BankAccount(50000)
//    val t1 = new Thread(() => buySafe(account, "Shoe", 3000))
//    val t2 = new Thread(() => buySafe(account, "iPhone", 4000))
//
//    t1.start()
//    t2.start()
//    Thread.sleep(10)
//    if (account.amount != 43000) println("AHA: " + account)
//    //    println()
//  }

  // option #2: @volatile
  // adding @volatile annotation on amount variable in BankAccount class
  // it is much useful compared to synchronized which allows to have a code block
  
  // Exercise:
  /*
      1. construct 50 "inception" threads
          Thread1 -> Thread2 -> Thread3 -> ...
          each thread println("Hello from thread #1")
          in REVERSE order
   */
  def inceptionThread(maxThread: Int, i: Int = 1): Thread = new Thread(() => {
    if(i < maxThread) {
      val newThread = inceptionThread(maxThread, i + 1)
      newThread.start()
      newThread.join()
    }
    println(s"Hello from thread $i")
  })
//  inceptionThread(50).start()

  // Exercise: #2
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())
  threads.foreach(_.join())
  println(x)
  // q1: What is the biggest possible value for x ? 100
  // q2: What is the smallest possible value for x ? 1

}

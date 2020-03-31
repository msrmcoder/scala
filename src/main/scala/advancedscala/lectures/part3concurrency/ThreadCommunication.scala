package advancedscala.lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

  /*
    classical producer-consumer problem
    producer -> [ x ] <- consumer in parallel
   */

  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    def set(newValue: Int): Unit = value = newValue

    def get: Int = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      while (container.isEmpty) {
        println("[consumer] actively waiting...")
      }
      println("[consumer] I have consumed a value " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing...")
      Thread.sleep(500)
      val value = 42
      println("[producer] I have produced, after long work, the value " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  // naiveProdCons()

  // wait and notify
  def smartProdCons: Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      container.synchronized {
        container.wait
        println("[consumer] I have consumed a value " + container.get)
      }
    })

    val producer = new Thread(() => {
      println("[producer] computing...")
      container.synchronized {
        Thread.sleep(500)
        val value = 42
        println("[producer] I have produced, after long time, the value " + value)
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  // smartProdCons


  /*
    Level 2: Indefinitely - producer -> [ ? ? ? ] <- consumer
    producer must wait if the buffer is full
    consumer must wait if the buffer is empty
   */
  def complexProdCons: Unit = {
    val capacity: Int = 3
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val rand = new Random

    val consumer = new Thread(() => {
      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println(s"[consumer] buffer is empty, waiting...")
            buffer.wait
          }

          println("[consumer] consumed " + buffer.dequeue())

          // hey producer, there is some empty space, are you lazy?!
          buffer.notify()
        }

        Thread.sleep(rand.nextInt(500))
      }
    })


    val producer = new Thread(() => {
      var i = 0
      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("[producer] buffer is full, waiting...")
            buffer.wait
          }

          println("[producer] produced " + i)
          buffer.enqueue(i)

          i += 1

          // hey consumer, food is ready
          buffer.notify()
        }

        Thread.sleep(rand.nextInt(500))
      }
    })

    consumer.start()
    producer.start()
  }

  // complexProdCons


  /*
    Level 3: Multiple producers consumers indefinitely
    p1, p2, p3 -> [ ? ? ? ] <- c1, c2, c3
   */
  def multiProdCons(p: Int, c: Int, capacity: Int): Unit = {
    val rand = new Random
    val buffer: mutable.Queue[Int] = new mutable.Queue

    def consumer(id: Int) = new Thread(() => {
      val name = Thread.currentThread().getName
      while (true) {
        buffer.synchronized {
          while (buffer.isEmpty) {
            println(s"[consumer-$name] buffer is empty, waiting...")
            buffer.wait()
          }
          println(s"[consumer-$name] consumed ${buffer.dequeue()}")

          buffer.notify()
        }

        Thread.sleep(rand.nextInt(500))
      }
    }, id.toString)

    def producer(id: Int) = new Thread(() => {
      var i = 0
      val name = Thread.currentThread().getName
      while(true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"[producer-$name] buffer is full, waiting...")
            buffer.wait
          }
          println(s"[producer-$name] produced $i")
          buffer.enqueue(i)
          i += 1

          buffer.notify()
        }
        Thread.sleep(rand.nextInt(500))
      }
    }, id.toString)

    (1 to c).map(consumer).foreach(_.start())
    (1 to p).map(producer).foreach(_.start())
  }
  // multiProdCons(4, 6, 10)

  /*
     Exercise:
     1. think of an example where notifyAll acts in a different way than notify?
     2. create a deadlock (two or more threads waiting for each other)
     3. create a livelock (two threads running long time by not allowing other threads)
   */
  // 1. Notify All
  def testNotifyAll(): Unit = {
    val bell = new Object
    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized {
        println(s"[thread $i] waiting...")
        bell.wait
        println(s"[thread $i] hooray!")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      bell.synchronized {
        println("[announcer] ring the bell!!!")
        bell.notify() // comment this and uncomment below to see the difference
        // bell.notifyAll
      }
    }).start()
  }
  // testNotifyAll()

  // 2. deadlock
  case class Friend(name: String) {
    def bow(other: Friend): Unit = {
      this.synchronized {
        println(s"$this: I'm bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen")
      }
    }

    def rise(other: Friend): Unit = {
      this.synchronized {
        println(s"$this: I'm raising to my friend $other")
      }
    }

    var side = "right"
    def switchSide(): Unit = {
      if (side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend): Unit = {
      while (this.side == other.side) {
        println(s"$this: Oh, but please, $other feel free to pass...")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("Sam")
  val peter = Friend("Peter")

  // 2. deadlock
  // new Thread(() => sam.bow(peter)).start()
  // new Thread(() => peter.bow(sam)).start()

  // 3. livelock // yielding execution to each other
  new Thread(() => sam.pass(peter)).start()
  new Thread(() => peter.pass(sam)).start()

}

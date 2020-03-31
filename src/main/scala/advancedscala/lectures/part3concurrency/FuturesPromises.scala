package advancedscala.lectures.part3concurrency

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Random, Success, Try}

object FuturesPromises extends App {
  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife // calculate meaning of life on ANOTHER thread
  } // (global) is passed by compiler

  println(aFuture.value) // Option[Try[Int]]

  println("waiting on the future")
  // this onComplete block may be executed by same future thread or completely by a new thread
  aFuture.onComplete(x => x match {
    case Success(meaningOfLife) => println(s"the meaning of the life is $meaningOfLife")
    case Failure(e) => println(s"I have failed $e")
  })

  Thread.sleep(3000)

  // Social Network
  case class Profile(id: String, name: String) {
    def poke(otherProfile: Profile) =
      println(s"${this.name} pokes ${otherProfile.name}")
  }

  object SocialNetwork {

    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id-0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val rand = new Random

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      // fetching from DB
      Thread.sleep(300)
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(400)
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  // client: mark to poke bill
  // too wordy!!! so best choice is functional composition
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  //  mark.onComplete {
  //    case Success(markProfile) => {
  //      val bill = SocialNetwork.fetchBestFriend(markProfile)
  //      bill.onComplete {
  //        case Success(billProfile) => markProfile.poke(billProfile)
  //        case Failure(e) => e.printStackTrace()
  //      }
  //    }
  //    case Failure(e) => e.printStackTrace()
  //  }

  // functional composition of futures
  // filter, map, flatMap
  val nameOnTheWall = mark.map(profile => profile.name)
  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

  // for-comprehension - more cleaner way writing asynchronous code
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("fb.id.0-dummy", "Forever alone!")
  }

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unknown id").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))

  // online banking app
  case class User(name: String)

  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Rock the JVM banking"

    def fetchUser(name: String): Future[User] = Future {
      // simulating fetching from DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchant: String, amount: Double): Future[Transaction] = Future {
      // simulating fetching from DB
      Thread.sleep(1000)
      Transaction(user.name, merchant, amount, "SUCCESS")
    }

    def purchase(username: String, merchant: String, item: String, cost: Double): String = {
      // fetch user
      // create transaction
      // WAIT transaction to finish
      val transactionStatus = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchant, cost)
      } yield transaction.status
      Await.result(transactionStatus, 2.seconds) // implicit conversion -> pimp my library
    }

  }

  println(BankingApp.purchase("Sriram", "Apple", "iPhone", 3000))

  // promises
  // Futures - read only purpose
  val promise = Promise[Int]() // "controller" over a future
  val future = promise.future

  // thread - 1 "consumer"
  future.onComplete {
    case Success(r) => println(s"[consumer] I've received value $r")
  }

  // thread - 2 "producer"
  val producer = new Thread(() => {
    println("[producer] I'm crunching number...")
    Thread.sleep(500)
    // "fulfilling" the promise
    promise.success(42)
    println("[producer] done!!!")
  })
  producer.start()

  Thread.sleep(1_000)

  /*
  Exercises:-
    1. fulfill a future IMMEDIATELY with a value
    2. inSequence(fa, fb)
    3. first(fa, fb) => new future with first value of two futures
    4. last(fa, fb) => new future with last value of two futures
    5. retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
   */

  // 1 - fulfill immediately
  def fulfillImmediately[T](value: T): Future[T] = Future(value)

  // 2 - inSeq
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] =
    first.flatMap(_ => second)

  // 3 - first out of two futures
  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A]
    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)
    promise.future
  }

  // 4 - last out of the two futures
  def last[A](fa: Future[A], fb: Future[A]): Future[A] = {
    // 1 promise try complete a future
    // 2 promise check result of first promise and update last computation result into it
    val bothPromise = Promise[A]
    val lastPromise = Promise[A]

    def checkAndComplete(result: Try[A]) =
      if (!bothPromise.tryComplete(result))
        lastPromise.complete(result)

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future
  }

  val fast = Future {
    Thread.sleep(100)
    42
  }
  val slow = Future {
    Thread.sleep(200)
    45
  }
  first(fast, slow).foreach(x => println(s"FIRST: $x"))
  last(fast, slow).foreach(x => println(s"LAST: $x"))

  Thread.sleep(1000)

  // 4 - retry until
  def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] =
    action()
    .filter(condition)
    .recoverWith {
      case _ => retryUntil(action, condition)
    }

  val rand = new Random
  val action = () => Future {
    Thread.sleep(200)
    val value = rand.nextInt(100)
    println("generated value " + value)
    value
  }
  retryUntil(action, (x: Int) => x < 10).foreach(x => println(s"settled at $x"))
  Thread.sleep(10000)
}

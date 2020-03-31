package advancedscala.lectures.part2afp

object Monad extends App {
  /*
    Monad is mathematical concept which satisfies three principles
     - left-identity
     - right-identity
     - associativity

    In Scala, any type has just unit/flatMap
    Ex: Try, Success, Fail
   */
  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }
  object Attempt {
    def apply[A](a: A): Attempt[A] = {
      try {
        Success(a)
      } catch {
        case e: Throwable => Fail(e)
      }
    }
  }

  case class Success[+A](a: A) extends Attempt[A] {
    override def flatMap[B](f: A => Attempt[B]): Attempt[B] = {
      try {
        f(a)
      } catch {
        case e: Throwable => Fail(e)
      }
    }
  }

  case class Fail(e: Throwable) extends Attempt[Nothing] {
    override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  /*
    Proof:
    left-identity

    unit.flatMap(f) = f(x)
    Attempt(x).flatMap(f) = f(x) // success
    Success(x).flatMap(f) = f(x) // proved.
   */

  /*
    Exercise:
    1) implement a Lazy[T] monad = computation which will only be executed when it's needed.
       unit/apply
       flatMap

    2) Monad = unit + flatMap
       Monad = unit + map + flatten

       Monad[T] {
         def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)

         def map[B](f: T => B): Monad[B] = ???
         def flatten(m: Monad[Monad[T]]): Monad[T] = ???
       }
       Note: keep List in mind
   */

  // Ex #1: Lazy
  class Lazy[+A](value: => A) {
    private lazy val internalValue = value
    def use: A = internalValue
    // def flatMap[B](f: A => Lazy[B]): Lazy[B] = f(value)
    def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(internalValue)
  }
  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value)
  }

  val lazyInstance = Lazy {
    println("Today I don't feel like doing anything")
    42
  }
//  println(lazyInstance.use)
  val flatMappedInstance = lazyInstance.flatMap(x => Lazy {
    x * 10
  })

  val flatMappedInstance2 = lazyInstance.flatMap(x => Lazy {
    x * 10
  })
  flatMappedInstance.use
  flatMappedInstance2.use
}

package basicscala.exercise

object MaybeTest extends App {

  trait Maybe[+T] {
    def filter(f: T => Boolean): Maybe[T]
    def map[B](f: T => B): Maybe[B]
    def flatMap[B](f: T => List[Maybe[B]]): List[Maybe[B]]
  }

}

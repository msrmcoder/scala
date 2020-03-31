package basicscala.lectures.part3fp

object AnonymousFunctions extends App {
  // It creates function in normal way
  val doubler: Function1[Int, Int] = new Function1[Int, Int] {
    def apply(x: Int): Int = x * 2
  }
  println(doubler(3))

  // same can be written as follows
  // anonymous function or Lambda
  // (x: Int) => x * 2 is lambda
  val doubler2 = (x: Int) => x * 2
  println(doubler2(3))

  // function type can be defined either form below
//  val doubler3: Function1[Int, Int] = ???
//  val doubler4: Int => Int = ???

  // Multiple parameters
  val adder: (Int, Int) => Int = (x, y) => x + y
  println(adder(4, 4))

  // No parameters
  val justDoSomething: () => Int = () => 3
  // careful
  println(justDoSomething) // function itself
  println(justDoSomething()) // calling function

  // curly braces with lambda
  val stingToInt = { (str: String) =>
    str.toInt
  }

  // MORE syntactic sugar
  val niceIncrementer: Int => Int = _ + 1 // equivalent to x => x + 1
  val niceAdder: (Int, Int) => Int = _ + _ // equivalent to (x, y) => x + y

  val superAdder = (x: Int) => (y: Int) => x + y
  println(superAdder(3)(8))

}

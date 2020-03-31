package basicscala.exercise

object WhatsAFunction extends App {

  // 1. 1 arg input -> output function
  trait MyFunction[A, R] {
    def doFunc(a: A): R
  }
  val fun: MyFunction[Int, String] = new MyFunction[Int, String] {
    override def doFunc(a: Int): String = a.toString
  }
  println(fun.doFunc(5))

  // 2. 2 arg input -> output function
  trait MyFunction2[A, B, R] {
    def apply(a: A, b: B): R
  }
  val adderFunc: MyFunction2[String, String, Int] = new MyFunction2[String, String, Int] {
    def apply(a: String, b: String): Int = a.toInt + b.toInt
  }
  println(adderFunc.apply("5", "3"))
  // omit function name to invoke as apply is something special in Scala (No explicit invocation required)
  println(adderFunc("5", "3"))

  // 3. Scala in-built Function1..Function22
  private val inbuiltFunc: Function2[String, String, Int] = new Function2[String, String, Int] {
    def apply(n1: String, n2: String): Int = n1.toInt + n2.toInt
  }
  println(inbuiltFunc("23", "10"))


  // 4. Using Scala syntactic sugar for functional types definition
  val simpleFunc: (String, String) => Int = new Function2[String, String, Int] {
    def apply(n1: String, n2: String): Int = n1.toInt + n2.toInt
  }
  println(simpleFunc("12", "23"))

  // 5. More simplified expression (types inferred from left side of the declaration)
  val elegantFunc: (String, String) => Int = (a, b) => a.toInt + b.toInt
  println(elegantFunc("4", "5"))
  // or
  // return type of function inferred
  val elegantFunc2 = (a: String, b: String) => a.toInt + b.toInt
  println(elegantFunc2("2", "1"))

  // Exercises
  /*
    1. a function which takes 2 strings and concatenates them
    2. transform the MyPredicate and MyTransformer into function types
    3. define a function which takes an int and returns another function which takes an int and returns an int
        - what's the type of this function
        - how to do it
   */
  // Ex 1
  val stringConcat = (a: String, b: String) => a + b
  println(stringConcat("Hello", "Scala"))
  println(((a: String, b: String) => a + b)("Hi", "Scala"))

  // Ex 2 - MyList modified

  // Ex 3 Function[Int, Function[Int, Int]]
  val superAdder: Function[Int, Function[Int, Int]] = new Function[Int, Function[Int, Int]] {
    override def apply(x: Int): Function1[Int, Int] = new Function1[Int, Int] {
      override def apply(y: Int): Int = x + y
    }
  }

  val adder3 = superAdder(3)
  println(adder3(4))
  println(superAdder.apply(3).apply(4))
  println(superAdder(3)(4)) // curried function
}

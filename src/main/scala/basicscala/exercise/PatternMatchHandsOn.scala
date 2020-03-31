package basicscala.exercise

object PatternMatchHandsOn extends App {

  trait Expr
  case class Number(n: Int) extends Expr
  case class Sum(e1: Expr, e2: Expr) extends Expr
  case class Prod(e1: Expr, e2: Expr) extends Expr

  // takes expr => human readable form
  // Sum(Number(1), Number(2)) => 1 + 2
  // Sum(Number(1), Number(2), Number(3)) => 1 + 2 + 3
  // Prod(Sum(Number(1), Number(2)), Number(3)) => (1 + 2) * 3
  // Sum(Prod(Number(2), Number(3)), Number(1)) => 2 * 3 + 1

  def show(e: Expr): String = e match {
    case Number(n) => s"$n"
    case Sum(e1, e2) => show(e1) + " + " + show(e2)
    case Prod(e1, e2) => {
      def maybeShowParentheses(e: Expr): String = e match {
        case Prod(_, _) => show(e)
        case Number(_) => show(e)
        case _ => "(" + show(e) + ")"
      }
      maybeShowParentheses(e1) + " * " +  maybeShowParentheses(e2)
    }
  }

  println(show(Sum(Number(1), Number(2)))) // 1 + 2
  println(show(Sum(Sum(Number(1), Number(2)), Number(3)))) // 1 + 2 + 3
  println(show(Prod(Sum(Number(1), Number(2)), Number(3)))) // (1 + 2) * 3
  println(show(Prod(Number(4), Sum(Number(1), Prod(Number(2), Number(3)))))) // 4 * (1 + 2 * 3)
  println(show(Prod(Sum(Number(4), Number(1)), Prod(Number(2), Number(3))))) // (4 + 1) * 2 * 3
  println(show(Prod(Sum(Number(4), Number(1)), Sum(Number(2), Number(3))))) // (4 + 1) * (2 + 3)
  println(show(Sum(Prod(Number(2), Number(3)), Number(1)))) // 2 * 3 + 1

}

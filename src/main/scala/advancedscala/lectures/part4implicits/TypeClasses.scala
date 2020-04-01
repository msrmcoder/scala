package advancedscala.lectures.part4implicits

object TypeClasses extends App {
  // design approach 1:-
  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    def toHtml: String = s"<div>$name ($age yo) <a href=$email/> </div>"
  }

  User("John", 23, "john@gmail.com").toHtml
  /*
    1. works only for the types we write
    2. ONE implementation out of quite a number
   */

  // option 2 - pattern matching
  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(n, a, e) =>
      // case java.util.Date => // compiler throws error
      case _ =>
    }
  }

  /*
    Disadvantage:
      - lost type safety
      - need to modify this code every time
      - still ONE implementation
   */

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
  }
  val john = User("John", 23, "john@gmail.com")
  println(UserSerializer.serialize(john))

  // 1 - we can define serializers for other types
  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString} </div>"
  }

  // 2 - we can define multiple serializer
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name} </div>"
  }

  // part 2
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer;
  }
  implicit object IntSerializer extends HTMLSerializer[Int] {
    def serialize(value: Int): String = s"<div style='color: blue'>$value</div>"
  }
  println(HTMLSerializer.serialize(42)(IntSerializer)) // if IntSerializer not defined as implicit
  println(HTMLSerializer.serialize(42)) // if IntSerializer defined as implicit, compiler infers

  println(HTMLSerializer.serialize(john)) // compiler infers serializer type automatically
  println(HTMLSerializer.serialize(john)(PartialUserSerializer))

  println(HTMLSerializer[Int].serialize(42)) // provides ability to access all methods of HTMLSerializer through implicits


}

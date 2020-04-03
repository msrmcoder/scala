package advancedscala.lectures.part4implicits

object TypeClasses extends App {
  // design approach 1:- without type
  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    def toHtml: String = s"<div>$name ($age yo) <a href=$email/> </div>"
  }

  println(User("John", 23, "john@gmail.com").toHtml)
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
  // design approach 2:- with type
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

  // part 3
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  println(john.toHTML(UserSerializer)) // compiler println(new HTMLEnrichment[User](john).toHTML(UserSerializer))
  println(john.toHTML)

  /*
    - extend functionality to new types (anyType toHTML)
   */
  println(2.toHTML)
  println(john.toHTML(PartialUserSerializer))

  /*
    // This pattern is super expressive
    - type class itself HTMLSerializer[T] {...}
    - type class instance (some of which are implicit) UserSerializer, IntSerializer, etc.,
    - conversion with implicit classes -- HTMLEnrichment
   */

  // context bounds
  println("Context bounds")
  def htmlBoilerplate[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
    s"<html><body>${value.toHTML(serializer)}</body></html>"

  println(htmlBoilerplate(john))
  println(htmlBoilerplate(john)(PartialUserSerializer))

  // implicit UserSerializer used automatically
  // adv: context bound makes the method name shortened
  // disAdv: can't use serializer by name inside htmlSugar
  def htmlSugar[T: HTMLSerializer](value: T): String = {
    s"<html><body>${value.toHTML}</body></html>"
  }
  println(htmlSugar(john))
  println(htmlSugar(john)(PartialUserSerializer))

  // overcome the disAdv using implicitly
  def htmlSugarNamed[T: HTMLSerializer](value: T): String = {
    val serializer = implicitly[HTMLSerializer[T]]
    // use APIs of serializer here if required
    s"<html><body>${value.toHTML(serializer)}</body></html>"
  }
  println(htmlSugarNamed(john))
  println(htmlSugarNamed(john)(PartialUserSerializer))

  // implicitly
  case class Permission(mask: String)
  implicit val defaultPermission = Permission("0744")

  // some other part of the code
  val standardPerms = implicitly[Permission]
}

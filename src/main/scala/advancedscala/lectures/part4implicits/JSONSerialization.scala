package advancedscala.lectures.part4implicits

import java.util.Date

object JSONSerialization extends App {
  /*
    Social network app - users, post, feed
    Serialize data to JSON format to pass to other systems
   */

  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  /*
    1. intermediate data types - Int, String, List, Date
    2. type classes for intermediate data type
    3. serialize to JSON
   */
  // data type
  sealed trait JSONValue {
    def stringify: String
  }
  // intermediate data types
  final case class JSONString(value: String) extends JSONValue {
    def stringify: String = "\"" + value + "\""
  }
  final case class JSONNumber(value: Int) extends JSONValue {
    def stringify: String = value.toString
  }
  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    def stringify: String = values.map(_.stringify).mkString("[", ",", "]")
  }
  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    /*
      Sample JSON structure:-
      {
        "name": "john",
        "age" : 32,
        "email" : "john@gmail.com",
        "posts" : [ ... ]
        "post": {
          "content": "Scala rocks"
        }
      }
     */

    def stringify: String = values.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }.mkString("{", ",", "}")
  }

  val json = JSONObject(Map(
    "name" -> JSONString("john"),
    "age" -> JSONNumber(32),
    "posts" -> JSONArray(List( JSONString("Scala rocks"), JSONNumber(12)))
  ))
  println(json.stringify)

  /*
    2.1. Type class
    2.2. Type class instances (implicit)
    2.3. pimp library to use type class instances (enrichment)
   */

  // 2.1
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }
  // 2.2
  // existing data types
  implicit object StringConverter extends JSONConverter[String] {
    def convert(value: String): JSONValue = JSONString(value)
  }
  implicit object NumberConverter extends JSONConverter[Int] {
    def convert(value: Int): JSONValue = JSONNumber(value)
  }
  //2.3
  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue =
      converter.convert(value)
  }
  // custom data types
  implicit object UserConverter extends JSONConverter[User] {
    def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email)
    ))
  }
  implicit object PostConverter extends JSONConverter[Post] {
    def convert(post: Post): JSONValue = JSONObject(Map(
      "content" -> JSONString(post.content),
      "created" -> JSONString(post.createdAt.toString)
    ))
  }
  implicit object FeedConverter extends JSONConverter[Feed] {
    def convert(feed: Feed): JSONObject = JSONObject(Map(
      // "user" -> UserConverter.convert(feed.user), // can be written as below
      // "posts" -> JSONArray(feed.posts.map(post => PostConverter.convert(post))) // can be written as below
      "user" -> feed.user.toJSON,
      "posts" -> JSONArray(feed.posts.map(_.toJSON))
    ))
  }

  val now = new Date(System.currentTimeMillis())
  val john = User("John", 32, "john@gmail.com")
  val feed = Feed(john, List(
    Post("Scala rocks", now),
    Post("It is cute little converter", now)
  ))

  println(feed.toJSON.stringify)
}

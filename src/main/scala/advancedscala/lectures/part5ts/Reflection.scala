package advancedscala.lectures.part5ts

object Reflection extends App {

  // reflection + macros + quasiquotes => METAPROGRAMMING

  case class Person(name: String) {
    def sayMyName(): Unit = println(s"Hi, my name is $name")
  }

  // Scenario - 1
  // 0. import
  import scala.reflect.runtime.{universe => ru}
  // 1 - instantiate mirror
  val m = ru.runtimeMirror(getClass.getClassLoader)
  // 2 - create a class object by name = "description about class"
  val clazz = m.staticClass("advancedscala.lectures.part5ts.Reflection.Person")
  // 3 - create a reflected mirror = "can do things like invoke constructor, method, etc"
  val classMirror = m.reflectClass(clazz)
  // 4 - get the constructor
  val constructor = clazz.primaryConstructor.asMethod
  // 5 - reflect the constructor
  val constructorMirror = classMirror.reflectConstructor(constructor)
  // 6 - invoke constructor
  val instance = constructorMirror.apply("John")
  println(instance)

  // Scenario - 2
  val mary = Person("Mary")
  val methodName = "sayMyName"
  // 1 - mirror
  // 2 - reflect the method
  val reflected = m.reflect(mary)
  // 3 - method symbol
  val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod
  // 4 - reflect the method
  val method = reflected.reflectMethod(methodSymbol)
  // 5 - invoke
  method.apply()
}

package advancedscala.lectures.part5ts

object SelfTypes extends App {

  // requiring a type to be mixed in
  trait Instrumentalist {
    def play(): Unit
  }
  trait Singer { self: Instrumentalist => // SELF TYPE whoever implements Singer must implement Instrumentalist as well
    // rest of the API
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist {
    override def sing(): Unit = ???
    override def play(): Unit = ???
  }

//  class Vocalist extends Singer {
//    override def sing(): Unit = ???
//  }

  val jamesHetfield = new Singer with Instrumentalist {
    override def sing(): Unit = ???
    override def play(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = println("(guider solo)")
  }

  val ericClapton = new Guitarist with Singer { // OK
    override def sing(): Unit = ???
  }

  // vs inheritance
  class A
  class B extends A // B IS AN A

  trait T
  trait S { self: T => } // S REQUIRES a T
  // CAKE PATTERN => "dependency injection"

  // DI
  class Component {
    // API
  }
  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(component: Component)

  // Cake pattern
  trait ScalaComponent {
    def action(x: Int): String
  }
  trait ScalaDependentComponent { self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + " rocks!"
  }
  trait ScalaApplication { self: ScalaDependentComponent => }

  // layer 1 - small components
  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent

  // layer 2 - compose
  trait Profile extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats

  // layer 3 - app
  trait AnalyticsApp extends ScalaApplication with Analytics

  // cyclical dependencies
  // class X extends Y
  // class Y extends X
  trait X { y: Y => }
  trait Y { x: X => } // no cyclic dependencies

}

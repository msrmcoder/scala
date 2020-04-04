package advancedscala.lectures.part5ts

object variance extends App {
  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // what is variance?
  // "inheritance" - type substitution of generics
  class Cage[T] // Generic type

  // It is possible because Cat extends Animal
  // animal: Animal = new Cat

  // if the same above intuitive is applicable for Cage[Cat] extends Cage[Animal]??

  // if Yes, solution is Covariance
  class CCage[+T] // covariance
  val ccage: CCage[Animal] = new CCage[Cat]

  // if No, Invariance
  class ICage[T]
  // val cage: Cage[Animal] = new Cage[Cat] ??
  // error type mismatch because they are contradict to each other,
  // val value: Int = "Hello" // ie. equivalent to

  // opposite of both - Contravariance
  class XCage[-T]
  val xcage: XCage[Cat] = new XCage[Animal]

  // Type Positions
  // Point: 1.0
  class InvariantCage[T](val animal: T) // OK, invariant accepts child classes of Animal
  val myCage: InvariantCage[Animal] = new InvariantCage[Animal](new Dog)

  // Point: 1.1
  class CovariantCage[+T](val animal: T) // Covariant position
  val myCCage: CovariantCage[Animal] = new CovariantCage[Cat](new Cat) // OK
  // val myCCage: CovariantCage[Animal] = new CovariantCage[Cat](new Dog) // Compiler not happy

  // Point: 1.2
  /*
  class ContravariantCage[-T](val animal: T) // Not possible! Compiler error

  if compiler allows above code, there may be a chance to
  code like below is wrong

   val myXCage: ContravariantCage[Cat] = new ContravariantCage[Animal](new Dog)
   */

  // VARIABLE POSITION - "var" is not allowed in  Covariant and Contravariant types

  // Point: 1.3
  // class CovariantVariableCage[+T](var animal: T) // types of vars are in CONTRAVARIANT POSITION
  /*
   if allowed, might occur the below
    val myCcage: CCage[Animal] = new CCage[Cat](new Cat)
    myCcage.animal = new Dog
  */
  // Point: 1.4
  /*
  class ContravariantVariableCage[-T](var animal: T)

  if compiler allows, chance to write below code which is completely wrong
  so, it restricted

  val ccvcage: ContravariantVariableCage[Cat] = new ContravariantVariableCage[Animal](new Crocodile)
   */
  // Point: 1.5
  class InvariantVariableCage[T](var animal: T) // OK

  // --------------------------------------------------------------------

  // Point: 2.0
  /*trait AnotherCovariantCage[+T] {
    def addAnimal(animal: T) // contravariant position
  }*/
  /*
    val ccage: CCage[Animal] = new CCage[Cat]
    ccage.addAnimal(new Dog) // which is wrong, compiler restricts
   */

  // Point: 2.1
  class AnotherContraVariantCage[-T] {
    def addAnimal(animal: T) = true
  }
  val acc: AnotherContraVariantCage[Cat] = new AnotherContraVariantCage[Animal]
  acc.addAnimal(new Cat)
  // acc.addAnimal(new Dog) // Error
  class Kitty extends Cat
  acc.addAnimal(new Kitty) // Cool!

  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B] // widening the type
  }
  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty) // type at Kitty
  val moreAnimals = animals.add(new Cat) // type widens to Cat
  val evenMoreAnimals = moreAnimals.add(new Dog) // type widens to Animal

  // Method arguments are in contravariant position.

  // return types
  class PetShop[-T] {
    // def get(isItPuppy: Boolean): T // method return type is covariant position
    /*
      val catShop = new PetShop[Animal] {
        def get(isItPuppy: Boolean): Animal = new Cat
      }

      val dogShop: PetShop[Dog] = catShop
      dogShop.get(true) // EVIL CAT!
     */
    def get[S <: T](isItPuppy: Boolean, defaultAnimal: S): S = defaultAnimal // any subtype of T
  }

  val shop: PetShop[Dog] = new PetShop[Animal]
  // val evilCat = shop.get(true, new Cat)
  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)

  /*
    Big rule:
     - method arguments are in CONTRAVARIANT position
     - return types are COVARIANT position
   */

}

package advancedscala.exercises

object ParkingApp extends App {

  /*
    Exercise:-
      1. Invariant, covariant, contravariant
         Parking[T](things: List[T]) {
           park(vehicle: T)
           impound(vehicles: List[T])
           checkVehicles(conditions: String): List[T]
         }

       2. Used someone else's API: IList[T]

       3. Parking = monad!
            - flatMap
   */

  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle

  class IList[T]

  // Ex: 1
  val bike = new Bike
  val car = new Car
  val vehicle = new Vehicle

  class IParking[T](things: List[T]) {
    def park(vehicle: T) = println("i-parked")
    def impound(vehicles: List[T]) = println("i-impounded")
    def checkVehicles(conditions: String): List[T] = things
  }
  val list = List(bike, car)
  val iParking: IParking[Vehicle] = new IParking[Vehicle](list)
  iParking.park(new Vehicle)
  iParking.park(new Car)
  iParking.impound(List(new Bike, new Car))
  println("i-checked" + iParking.checkVehicles("lights,registrationPlats"))

  // covariant, contravariant are not possible
  // val cParking: Parking[Vehicle] = new Parking[Car](List(new Car))
  // val xParking: Parking[Car] = new Parking[Vehicle](List(new Car))

  // by definition Animal -> Dog = Covariant, Operation act on its specific type or its parent
  class CParking[+T](things: List[T]){
    def park[U >: T](vehicle: U) = println("c-park")
    def impound[U >: T](vehicles: List[U]) = println("c-impounded")
    def checkVehicles(conditions: String): List[T] = things
  }
  // val cParking: CParking[Vehicle] = new CParking[Vehicle](list)
  val cParking: CParking[Vehicle] = new CParking[Car](List(new Car))
  // val cParking: CParking[Car] = new CParking[Vehicle](List()) // compiler error
  cParking.park(new Vehicle)
  cParking.park(new Car)
  cParking.park(new Bike)
  cParking.impound(list)
  println("c-checked" + cParking.checkVehicles("lights"))

  class XParking[-T](things: List[T]) {
    def park(vehicle: T) = println("x-parked")
    def impound(vehicles: List[T]) = println("x-impounded")
    def checkVehicles[L <: T](conditions: String): List[L] = List()
  }
  val xParking: XParking[Car] = new XParking[Vehicle](list)
  // val xParking: XParking[Vehicle] = new XParking[Vehicle](list)
  // val xParking: XParking[Vehicle] = new XParking[Car](list) // Error
  xParking.park(new Car)
  // xParking.park(new Vehicle) // Error
  // xParking.park(new Bike) // Error
  xParking.impound(List(new Car))
  // xParking.impound(List(new Car, new Bike)) // Error
  println("x-checked" + xParking.checkVehicles("horns"))

  // Answer
  // invariant
  class IIParking[T](vehicles: List[T]) {
    def park(vehicle: T): IIParking[T] = ???
    def impound(vehicles: List[T]): IIParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => S): IIParking[S] = ???
  }

  // covariant
  class CCParking[+T](vehicles: List[T]) {
    def park[U >: T](vehicle: U): CCParking[U] = ???
    def impound[U >: T](vehicles: List[U]): CCParking[U] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => S): CCParking[S] = ???
  }

  // contravariant
  class XXParking[-T](vehicles: List[T]) {
    def park(vehicle: T): XXParking[T] = ???
    def impound(vehicles: List[T]): XXParking[T] = ???
    def checkVehicles[L <: T](conditions: String): List[L] = ???

    // def flatMap[S](f: T => S): XXParking[S] = ???
    // def flatMap[R <: T, S](f: R => S): XXParking[S] = ???
    def flatMap[R <: T, S](f: Function1[R, S]): XXParking[S] = ???
  }

  /*
    Rule of thumb:
      - Covariant is useful to keep collection of things
      - Contravariant is useful for performing group of actions
   */
  // Ex: 2
  // covariant
  class CCParking2[+T](vehicles: IList[T]) {
    def park[U >: T](vehicle: U): CCParking2[U] = ???
    def impound[U >: T](vehicles: IList[U]): CCParking2[U] = ???
    def checkVehicles[U >: T](conditions: String): IList[U] = ???
  }

  // contravariant
  class XXParking2[-T](vehicles: IList[T]) {
    def park(vehicle: T): XXParking2[T] = ???
    def impound[L <: T](vehicles: IList[L]): XXParking2[L] = ???
    def checkVehicles[L <: T](conditions: String): IList[L] = ???
  }

  // Ex: 3
  // adding flatMap
}

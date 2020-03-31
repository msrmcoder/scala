package advancedscala.lectures.part3concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.{ForkJoinTaskSupport, Task, TaskSupport}
import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {

  // 1. Parallel collection

  val parList = List(1, 2, 3).par // .par makes the collection to parallel

  val aParVector = ParVector[Int](1, 2, 3) // par is used to make parallel

  /*
    Seq
    Vector
    Array
    Set - Hash, Trie
    Map - Hash, Trie
   */

  def measure[T](operation: => T): Long = {
    val t0 = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - t0
  }

  val list: List[Int] = (1 to 10_000_000).toList
  val serialList = measure {
    list.map(_ + 1)
  }
  // println("Serial time: " + serialList)

  val parallelTime = measure {
    list.par.map(_ + 1)
  }
  // println("Parallel time: " + parallelTime)

  /*
     Map-reduce model
      - split the elements into chunks - Splitter
      - operation
      - recombine - Combiner
   */

  // map, flatMap, filter, foreach - safe operation in parallel
  // reduce, fold - not safe

  // fold, reduce - non-associative operations
  println(List(1, 2, 3).reduce(_ - _))
  println(List(1, 2, 3).par.reduce(_ - _))

  // synchronization
  var sum = 0
  List(1, 2, 3).par.foreach(sum += _)
  println(sum) // race condition!

  // configuration
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))
  /*
    alternatives
     - ThreadPoolTaskSupport - deprecated
     - ExecutionContextTaskSupport(EC) - used if Future
   */

  // custom one - ThreadManager
  aParVector.tasksupport = new TaskSupport {
    override def execute[R, Tp](fjtask: Task[R, Tp]): () => R = ???
    override val environment: AnyRef = ???
    override def executeAndWaitResult[R, Tp](task: Task[R, Tp]): R = ???
    override def parallelismLevel: Int = ???
  }

  // 2. Atomic ops and references
  val atomic = new AtomicReference[Int](2)

  atomic.get() // thread-safe read
  atomic.set(4) // thread-safe write

  // if(x == 4) x = 42 equivalent is
  // just do reference equality no deep comparision of reference
  atomic.compareAndSet(4, 42) // thread-safe combo

  atomic.updateAndGet(x => x + 1) // thread-safe function run
  atomic.getAndUpdate(_ + 1) // same but get the value and update it
  atomic.accumulateAndGet(12, _ + _)
  atomic.getAndAccumulate(12, (x, y) => x + y)
}

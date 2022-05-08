package brilliant.futures

/**
* In this section we will learn about the support that the Scala standard
* library provides for concurrent and asynchronous programming with its `Future`
* data type as well as related data types such as `Promise`.
* 
* To develop a basis for the materials in this section we will beginning by
* reviewing some basics about concurrency on the JVM.
* 
* The fundamental unit of concurrency on the JVM is the `Thread`. A thread
* represents one logical chain of execution. Everything on a thread happens
* linearly one after another. While you may not have realized it, everything
* we have done so far as occurred on a single thread.
* 
* In the program below, `2` will always be printed to the console because the
* line `x += 1` appears before `println(x)`. This may seem obvious but this type
* of reasoning is only correct when we are talking about a single thread.
*/
object SingleThreaded extends App {

  var x = 1
  x += 1
  println(x)
}

/**
  * In contrast, in the example below either `1` or `2` may be printed to the
  * console. The result here is indeterminate for two reasons.
  * 
  * First, unless synchronization is enforced through other operators,
  * activities on different threads may appear in any order relative to each
  * other. In this case that means that `x += 1` may be executed before or
  * after `println(x)`.
  * 
  * Second, changes to a variable made by one thread are not guaranteed to be
  * "visible" to other threads absent other tools such as volatile or atomic
  * variables.
  * 
  * If we include a `sleep` for a second after we fork updating the variable we
  * will probably see the update value printed, but there is actually no
  * guarantee that this will occur no matter how long we wait.
  */
object MultiThreaded extends App {

  def fork(task: => Any): Thread = {
    val thread = new Thread { override def run(): Unit = task }
    thread.start()
    thread
  }

  /**
    * For illustration only. Don't do this at home!
    */
  def sleep(millis: Long): Unit =
    Thread.sleep(millis)

  var x = 1
  fork(x += 1)
  println(x)
}

/**
  * Threads have several properties that are important to keep in mind and in
  * fact have driven much of concurrent programming on the JVM. Understanding
  * these features will help you understand the "why" of `Future` as well as
  * other frameworks for asynchronous and concurrent programming that came
  * after it.
  * 
  * 1. Threads are heavyweight. Threads have significant system level resources
  * assocaited with them so we do not want to make any more threads than
  * necessary. Switching between threads also has significant performance costs.
  * Optimal performance is typically achieved by having a number of threads
  * equal to operating system cores.
  * 
  * 2. There is no way to wait for a thread to complete execution without
  * blcoking the current thread to wait for its result. In combination with the
  * property above that threads are heavyweight this leads to a strong principle
  * that we should never block threads.
  * 
  * In the example above, we can restore the desired sequencing between updating
  * the variable and printing it by using the `join` operator on `Thread`.
  * However, this blocks the main thread from doing anything else, which means
  * we are potentially wasting system resources.
  */
object ThreadBlocking extends App {

  def fork(task: => Any): Thread = {
    val thread = new Thread { override def run(): Unit = task }
    thread.start()
    thread
  }

  var x = 1
  val thread = fork(x += 1)
  thread.join()
  println(x)
}

/**
  * The most basic solution to address that is callbacks. Instead of waiting
  * for the thread to complete and getting its value, which we can't do without
  * blocking, we register a callback that the thread will run when it is done
  * describing the logic that want it to do.
  * 
  * In the example below we correctly enforce the sequentiality between updating
  * the variable and reading it and we do not block any threads.
  */
object Callbacks extends App {

  def fork(task: => Any): Thread = {
    val thread = new Thread { override def run(): Unit = task }
    thread.start()
    thread
  }

  var x = 1

  def asyncIncrement(callback: Int => Unit): Unit =
    fork {
      x += 1
      callback(x)
    }

  asyncIncrement { n => println(n) }
}

/**
  * Unfortunately, this callback style of programming can lead to programs
  * that are extremely hard to read, write, or reason about when we introduce
  * any level of complexity.
  * 
  * Try to increment the variable five times and then print out the final
  * value using callbacks.
  */
object CallbackHell {

  def fork(task: => Any): Thread = {
    val thread = new Thread { override def run(): Unit = task }
    thread.start()
    thread
  }

  var x = 1

  def asyncIncrement(callback: Int => Unit): Unit =
    fork {
      x += 1
      callback(x)
    }

  asyncIncrement { n => println(n) }
}

/**
  * Data types like future essentially wrap this underlying callback based API
  * with a more declarative one providing operators such as `map` and `flatMap`.
  * This allows us to describe our concurrency logic in a higher level, more
  * compositional style, but under the hood everything is implemented in terms
  * of call backs and as we will see below there is not actually any rocket
  * science going on here.
  */
final case class Async[+A](register: (A => Unit) => Unit) { self =>
  def map[B](f: A => B): Async[B] =
    Async { cb =>
      register(a => cb(f(a)))
    }

  def flatMap[B](f: A => Async[B]): Async[B] =
    Async { cb =>
      register { a =>
        f(a).register(cb)
      }
    }

  /**
    * As an exercise, implement the `zipWithPar` operator. Make sure that your
    * implementation runs both `self` and `that` in parallel rather than one
    * after the other.
    */
  def zipWithPar[B, C](that: Async[B])(f: (A, B) => C): Async[C] =
    ???

  def runAsync(cb: A => Unit): Unit =
    register(cb)

  def runSync: A = {
    val latch = new java.util.concurrent.CountDownLatch(1)
    var result = null.asInstanceOf[A]
    runAsync { a =>
      result = a
      latch.countDown()
    }
    latch.await() // blocking
    result
  }
}

object Async {

  def succeed[A](a: A): Async[A] =
    Async(cb => cb(a))

  def async[A](a: => A): Async[A] =
    Async { cb =>
      val thread = new Thread { override def run(): Unit = cb(a) }
      thread.start()
    }
}

object AsyncExample extends App {

  def asyncIncrement(a: Int): Async[Int] =
    Async.async {
      Thread.sleep(1000)
      a + 1
    }

  val asyncSum = for {
    x <- asyncIncrement(0)
    y <- asyncIncrement(x)
    z <- asyncIncrement(y)
  } yield z

  asyncSum.runAsync(n => println(n))
}

/**
  * As an exercise, update the implementation of `Async` above to support error
  * handling.
  */
final case class Async2[+E, +A](register: (Either[E, A] => Unit) => Unit)
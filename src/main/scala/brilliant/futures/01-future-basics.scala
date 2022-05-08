package brilliant.futures

import scala.concurrent._

import FutureExtensions._

/**
  * You can think of the `Future` data type as being very similar to the `Async`
  * data type we implemented in the previous section. We will see that `Future`
  * adds some complexity but ultimately there is not that much going on "under
  * the hood" so if you understood the material so far you are in a good
  * position to understand it and in fact to reimplement it yourself if
  * necessary.
  * 
  * `Future` adds three main changes to the `Async` data type that we
  * implemented in the previous section.
  * 
  * First, it builds in an error type of `Throwable`. This is actually a
  * specialization of the `Async` data type with error handling that you may
  * have implemented yourself at the end of the last section that fixes the
  * error type to `Throwable`.
  * 
  * Second, almost all operators on `Future` require an `ExecutionContext`,
  * which you can think of as a thread pool. You may recall that in our
  * simplified implementation of `Async` before we just created a new thread
  * ourselves in the `async` constructor. You can think of that as essentially
  * hard coding a particular thread pool implementation and `Future` as
  * allowing the thread pool to instead be specified as a parameter to each
  * operator.
  * 
  * Finally, `Future` has a more "eager" evaluation model than the `Async` data
  * type we implemented. Recall that in the `async` constructor we did not start
  * the thread until the callback was registered. This resulted in a "lazy"
  * evaluation model where no work is done until we "run" a computation using
  * `runAsync`. In contrast, `Future` has an "eager" evaluation model where we
  * kick off the thread even before the callback is registered. As we will see,
  * this can make it significantly harder to reason about futures than our
  * `Async` data type.
  */
object FutureBasics {
  import scala.concurrent.ExecutionContext.Implicits.global

  /**
    * Use the `Future.successful` constructor to lift an existing value into a
    * `Future`.
    */
  lazy val successful: Future[Int] =
    ???

  /**
    * Use the `Future.failed` constructor to construct a future that has
    * already failed with the specified `Throwable`.
    */
  lazy val failed: Future[Nothing] =
    ???

  /**
    * Use the `fromTry` constructor to construct a `Future` from an existing
    * `scala.util.Try` value.
    */
  lazy val fromTry: Future[Int] =
    ???

  /**
    * Use the `Future.apply` constructor to construct a `Future` from a value
    * that may potentially be computed asynchronously. Explain the difference
    * between this constructor and the constructors you have previously
    * implemented in this section.
    */
  lazy val apply: Future[Int] =
    ???

  /**
    * Use the `map` operator on `Future` to transform the successful result of
    * the `successful` future you defined above.
    */
  lazy val map: Future[Int] =
    ???

  /**
    * Use the `map` operator on `Future` to transform the failed result of the
    * `failed` future you defined above. Note the result.
    */
  lazy val map2: Future[Int] =
    ???

  def doubleIt(n: Int): Future[Int] =
    Future {
      println("Doing some expensive computation")
      Thread.sleep(1000)
      println("Done with expensive computation")
      n * 2
    }

  /**
    * Use `flatMap` to asynchronously wait for the result of the `apply`
    * future you defined above and then further process its result using the
    * `doubleIt` operator defined above.
    */
  lazy val flatMap: Future[Int] =
    ???
}

/**
  * Scala `Future` does not have explicit parallelism operators but instead
  * relies on the "eagerness" of `Future` to create parallelism. To see this,
  * try running the two snippets below by replacing `future1` with `future2` in
  * the last line of this object. Report your findings.
  */
object Parallelism extends App {

  val ec = scala.concurrent.ExecutionContext.global

  def asyncTwo(implicit ec: ExecutionContext): Future[Int] =
    Future {
      println("Two sleeping")
      Thread.sleep(2000)
      println("Two waking up")
      2
    }

  def asyncThree(implicit ec: ExecutionContext): Future[Int] =
    Future {
      println("Three sleeping")
      Thread.sleep(3000)
      println("Three waking up")
      3
    }

  def future1(implicit ec: ExecutionContext): Future[Int] =
    for {
      two <- asyncTwo
      three <- asyncThree
    } yield two + three

  def future2(implicit ec: ExecutionContext): Future[Int] = {
    val asyncTwo1 = asyncTwo
    val asyncThree1 = asyncThree
    for {
      two <- asyncTwo1
      three <- asyncThree1
    } yield two + three
  }

  future1(ec).await(ec)
}

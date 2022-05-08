package brilliant.futures

/**
  * Because of the model of "implicit" concurrency built into `Future` it
  * has relatively few concurrency operators. Normally we control whether
  * we want to do something sequentially or in parallel just be when we
  * evaluate our futures, as we saw above.
  * 
  * However, with `Future`, `Promise`, and possibly atomic variables we can
  * implement a variety of concurrency operators ourselves.
  */
object Concurrency {
  import scala.concurrent._

  val ec = scala.concurrent.ExecutionContext.global

  def successfulFuture(n: Int)(implicit ec: ExecutionContext): Future[Int] =
    Future {
      Thread.sleep(n * 1000)
      n
    }

  def failedFuture(n: Int)(implicit ec: ExecutionContext): Future[Int] =
    Future {
      Thread.sleep(n * 1000)
      throw new Exception("Uh oh!")
    }

  /**
    * Use the `firstCompletedOf` operator on the `Future` companion object
    * to return the result of the first to be completed, whether by success or
    * failure.
    * 
    * Describe why this operator is fundamentally resource unsafe.
    */
  def raceFirst[A](left: Future[A], right: Future[A]): Future[A] =
    ???

  /**
    * Implement an operator that returns the first future to be successfully
    * completed. If the first future to complete execution fails the operator
    * should wait for the second future to complete and return the result of
    * that future if it is a success. If both futures fail you may return either
    * failure at your discretion or combine them.
    */
  def race[A](left: Future[A], right: Future[A]): Future[A] =
    ???
}
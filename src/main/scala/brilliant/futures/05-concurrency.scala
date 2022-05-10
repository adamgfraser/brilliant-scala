package brilliant.futures

import scala.util._

import FutureExtensions._

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

  def zip[A, B](left: Future[A], right: Future[B]): Future[(A, B)] = ???

  def raceEither[A, B](left: Future[A], right: Future[B]): Future[Either[A, B]] = ???

  def successfulFuture(n: Int)(implicit ec: ExecutionContext): Future[Int] =
    Future {
      println(s"Future $n starting")
      Thread.sleep(n * 1000)
      println(s"Future $n done")
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
  def raceFirst[A](left: Future[A], right: Future[A])(implicit ec: ExecutionContext): Future[A] =
    Future.firstCompletedOf(List(left, right))

  /**
    * Implement an operator that returns the first future to be successfully
    * completed. If the first future to complete execution fails the operator
    * should wait for the second future to complete and return the result of
    * that future if it is a success. If both futures fail you may return either
    * failure at your discretion or combine them.
    */
  def race[A](left: Future[A], right: Future[A])(implicit ec: ExecutionContext): Future[A] =
    raceWith(left, right)(
      {
        case (Success(a), future) => Future.successful(a)
        case (Failure(t1), future) => future.transform(_.fold(t2 => Failure({t2.addSuppressed(t1); t2}), a => Success(a)))
      },
      ???
    )

  /**
    * Implement `raceWith`, which is the most powerful operator for running
    * two workflows concurrently. Use this to implement a version of `zipPar`
    * that supports early termination.
    */
  def raceWith[A, B, C](left: Future[A], right: Future[B])(
    leftWinner: (Try[A], Future[B]) => Future[C],
    rightWinner: (Try[B], Future[A]) => Future[C]
  )(implicit ec: ExecutionContext): Future[C] = {
    val promise = Promise[() => Future[C]]
    left.onComplete(tryA => promise.trySuccess(() => leftWinner(tryA, right)))
    right.onComplete(tryB => promise.trySuccess(() => rightWinner(tryB, left)))
    promise.future.flatMap(f => f())
  }

  /**
    * Run `left` and `right` in parallel and collect the results of both when
    * both are done, but if either one fails then return immediately with that
    * failure.
    */
  def zipPar[A, B](left: Future[A], right: Future[B])(implicit ec: ExecutionContext): Future[(A, B)] =
    raceWith(left, right)(
      {
        case (Success(a), future) => future.map(b => (a, b))
        case (Failure(t), future) => Future.failed(t)
      },
      {
        case (Success(b), future) => future.map(a => (a, b))
        case (Failure(t), future) => Future.failed(t)
      }
    )

  def zipPar[A, B, C](futureA: Future[A], futureB: Future[B], futureC: Future[C]): Future[(A, B, C)] =
    ???

  // Future.sequence

  def collectAllPar[A](futures: List[Future[A]])(implicit ec: ExecutionContext): Future[List[A]] =
    futures.foldLeft(Future.successful(List.empty[A])) { (future, next) =>
      zipPar(next, future).map { case (a, as) => a :: as }
    }.map(_.reverse)

  // val p = Promise[List[A]]()
  // futures.foreach(_.onComplete(_.failed.foreach(p.tryFailure)))
  // Future.sequence(future).foreach(p.trySuccess)
  // p.future
}

object ConcurrencyExample extends App {
  import scala.concurrent._

  import Concurrency._

  implicit val ec = scala.concurrent.ExecutionContext.global

  println(raceFirst(successfulFuture(2), successfulFuture(3))(ec).await(ec))

  // Future 3 starting
  // Future 2 starting
  // Future 2 done
  // 2
  // [success] Total time: 4 s, completed May 10, 2022, 12:48:12 AM
  // Future 3 done
}
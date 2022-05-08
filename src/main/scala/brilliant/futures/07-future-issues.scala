package brilliant.futures

/**
  * While `Future` is a giant step up from programming directly in terms of
  * threads or callbacks, there has been almost no innovation in `Future` for
  * years.
  * 
  * In this section we will look at some of the pitfalls of future to be aware
  * of.
  */
object FutureIssues

/**
  * Scala's `Future` relies on a combination of "lazy" and "eager"
  * evaluation models. The `Future.apply` constructor has be to lazy or we
  * wouldn't run computations asynchronously at all. However, in the `Future`
  * API we have to eagerly run futures if we want to introduce any actual
  * concurrency or parallelism to our programs beyond just sequencing
  * computations on asynchronous values.
  * 
  * This can result in a confusing evalution model where seemingful
  * insignificant changes can dramatically impact the semantics of a program.
  * It can also make it harder to define operators for futures.
  */
object EvaluationModel {
  import scala.concurrent._
  import scala.util._

  implicit val ec: ExecutionContext =
    scala.concurrent.ExecutionContext.global

  val future: Future[Int] =
    Future {
      val n = scala.util.Random.nextInt(100)
      if (n % 2 == 0) n
      else throw new Exception("Boom!")
    }

  /**
    * Explain why this operator will not have the desired semantics.
    */
  def retry[A](future: Future[A]): Future[A] =
    future.transformWith {
      case Failure(t) => retry(future)
      case Success(a) => Future.successful(a)
    }
}

/**
  * Another issue with `Future` is that it does not support interruption. In
  * the `race` exercise we did before we were not doing anything with the result
  * of the loser but the loser continued executing. In our simple example this
  * did not matter, but imagine the loser was opening files and processing
  * their results, but then we determined we didn't need the results anymore
  * (e.g. reuest timed out, user navigated away). We have now created a
  * resource leak. `Future` does not give us any way to deal with this.
  */
object ResourceSafety
package brilliant.futures

/**
  * We said before that we never want to block a thread if we can possibly
  * avoid it. The `Future` API helps us avoid that, but there are still a couple
  * of guidelines that we need to follow.
  */
object Blocking {
  import scala.concurrent._
  import scala.concurrent.duration.Duration

  implicit val ec: ExecutionContext =
    scala.concurrent.ExecutionContext.global

  /**
    * If we really want to block for the result of a `Future` we can use the
    * `Await.result` operator, which will either return the successful result
    * of the computation or throw an `Exception`.
    * 
    * The first guideline we should have about this operator is to avoid using
    * it at all whenever possible. Ideally we would call this operator at most
    * once in our entire program. Instead of blocking for a result and doing
    * something with it use `map` or `flatMap` to describe what further
    * prcessing you want to do with the result when it is available.
    */
  def blockForResult[A](future: Future[A], duration: Duration): A =
    Await.result(future, duration)

  lazy val someFuture: Future[Int] =
    ???

  def someTransformation(n: Int): String =
    ???

  /**
    * In this example by calling `blockForResult` we are defeating the
    * purpose of using `Future` in the first place, which was to avoid wasting
    * system resources by blocking threads waiting for results to be completed.
    */
  object Bad {
    val result = blockForResult(someFuture, Duration.Inf)
    val transformed = someTransformation(result)
    println(transformed)
  }

  /**
    * In this example we are just creating a data pipeline that will
    * asynchronously run each step when the result of the previous step is
    * available. We may need to block once at the very end of our program to
    * prevent our application from completing until our logic has actually
    * run.
    */
  object Good {
    someFuture
      .map(someTransformation)
      .foreach(println)
  }

  /**
    * If you do need to block for a result, it is recommended to use a timeout
    * rather than waiting forever as in these simple examples so failure can
    * be signaled versus the application suspended indefinitely.
    */
  object Timeouts

  // ZIO 1.0 number of operating system cores for the core thread pool

  /**
    * Sometimes we need to work with third party APIs that block internally.
    * If we use the `Future.apply` constructor we are running the computation
    * on another thread, but it is still one of the threads on our thread pool.
    * Scala futures use a fork join thread pool that is relatively resilient to
    * blocking work (at significant cost to performance due to excessive context
    * switching) but we can help it out by using the `blocking` operator to give
    * it a "hint" that a certain operation is blocking
    */
  object BlockingAPIs {
    import scala.concurrent._

    object SomeThirdPartyAPI {

      /**
        * Loads some data from database and blocks until it is available.
        * Unfortunately the library we are using does not expose a non-blocking
        * API.
        */
      def blockingCall: String =
        ???
    }

    object MyWrapper {

      /**
        * This is still going to block some thread, but we are giving the
        * implementation more information to manage the fact that a thrad will
        * be "out of commission" while it is blocking for the result.
        */
      def managedBlockingCall: Future[String] =
        Future {
          blocking {
            SomeThirdPartyAPI.blockingCall
          }
        }
    }
  }
}
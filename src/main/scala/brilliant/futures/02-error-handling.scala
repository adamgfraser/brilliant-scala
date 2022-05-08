package brilliant.futures

/**
  * As we have discussed, `Future` is like the version of our `Async` data
  * type with error handling except that the error type is fixed to
  * `Throwable`.
  * 
  * This means that in addition to the basic operators such as `map`, `flatMap`,
  * and `zip` there are specialized operators for transforming and potentially
  * recovering from errors.
  */
object ErrorHandling {
  import scala.concurrent._

  val failedFuture: Future[Nothing] =
    Future.failed[Nothing](new Throwable("Oh no!"))

  val successfulFuture: Future[Int] =
    Future.successful(42)

  /**
    * Use the `fallbackTo` constructor to recover from `failedFuture` and
    * return the result of `successfulFuture` instead.
    */
  val recoveredFuture: Future[Int] =
    ???

  /**
    * The `transform` operator allows you to map both the error and success
    * types of the `Future`. Try using it to convert the successful value to 
    * an `Int` and to convert the error to a `NoSuchElementException`. Note
    * your observations about how this transformation is reflected in the type
    * signature.
    */
  def transformedFuture(in: Future[Int])(implicit ec: ExecutionContext): Future[String] =
    ???

  /**
    * There is also a variant of `transform` called `transformWith1` that allows
    * specifiying a function from `Try` to `Future` that lets you handle both
    * the failure and success possibilites and run a new computation for each.
    * Perform a workflow of your choice for the successful and failed values
    * of the input `Future`.
    * 
    * Note how much information you have about the failure possibilities in
    * your implementation.
    */
  def transformedFuture2(in: Future[Int])(implicit ec: ExecutionContext): Future[String] =
    ???
}
package brilliant.futures

/**
  * The `Promise` data type is extremely useful for interaction with other
  * asynchronous data types or with callback based APIs. We can translate any
  * callback based API into a `Future` based one by creating a `Promise`,
  * registering a callback that will complete the `Promise`, and then waiting
  * for the result of the `Promise`.
  */
object Interop {
  import scala.concurrent._
  import scala.util._

  type ??? = Nothing

  def async[A](register: (A => Unit) => Unit)(implicit ec: ExecutionContext): Future[A] = {
    val promise = Promise[A]()
    register(a => promise.success(a))
    promise.future
  }

  /**
    * Implement the `asyncEither` constructor, which wraps a callback based API
    * that allows us to call back with either a success or failure in a
    * `Future`. Then reimplement `async` in terms of `asyncTry` to reduce code
    * duplication.
    */
  def asyncTry[A](register: (Try[A] => Unit) => Unit)(implicit ec: ExecutionContext): Future[A] =
    ???

  /**
    * Pick another asynchronous data type of your choice. Implement a
    * constructor to convert values of that data type into a `Future`. Note
    * whether your implementation ever blocks a thread.
    */
  def from[A](in: ???): Future[A] =
    ???
}
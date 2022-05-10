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

  trait User

  trait NonblockingDatabase {
    def loadUser[A](id: Int)(cb: (User => A) => Unit): Unit =
      ???
  }

  trait NonblockingDatabaseFuture {
    def loadUser[A](id: Int): Future[A]
  }

  /**
    * Implement the `asyncEither` constructor, which wraps a callback based API
    * that allows us to call back with either a success or failure in a
    * `Future`. Then reimplement `async` in terms of `asyncTry` to reduce code
    * duplication.
    */
  def asyncTry[A](register: (Try[A] => Unit) => Unit)(implicit ec: ExecutionContext): Future[A] =
    ???

  // libraryDependencies += "com.google.guava" % "guava" % "31.1-jre"

  import com.google.common.util.concurrent.ListenableFuture
  import java.util.concurrent.Executor

  // import scala.util.control.NonFatal

  /** 
    * Pick another asynchronous data type of your choice. Implement a
    * construc whether your implementation ever blocks a thread.
    */
  def fromFutureGuava[A](future: ListenableFuture[A])(implicit ec: ExecutionContext): Future[A] = {
    val promise = Promise[A]()
    future.addListener(
      new Runnable {
        def run(): Unit =
          promise.complete(Try(future.get()))
      },
      executionContextAsExectutor(ec)
    )
    promise.future
  }

  private def executionContextAsExectutor(ec: ExecutionContext): Executor =
    new Executor {
      def execute(command: Runnable): Unit = ec.execute(command)
    }
}
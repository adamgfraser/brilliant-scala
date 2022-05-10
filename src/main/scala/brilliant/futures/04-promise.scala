package brilliant.futures

/**
  * The other key data type in the Scala standard library regarding concurrency
  * is `Promise`. While `Future` represents a (probably) "in flight",
  * computation, you can think of a `Promise` as like an empty "box" that we
  * can fill exactly once with a value and which we can asynchronously wait for
  * a value from.
  * 
  * Let's start developing our intuition for `Promise` by implementing our own
  * version using the `Async` data type we defined previousy.
  */
object Promises {

  final case class Async[+A](register: (A => Unit) => Unit)

  object Async {

    def succeed[A](a: A): Async[A] =
      Async(cb => cb(a))

    def async[A](register: (A => Unit) => Unit): Async[A] =
      Async(register)
  }

  sealed trait Promise[A] {
    def await: Async[A]
    def succeed(a: A): Boolean
  }

  object Promise {

    private sealed trait State[A]

    private object State {
      final case class Empty[A](callbacks: List[A => Unit]) extends State[A]
      final case class Full[A](value: A) extends State[A]
    }

    def make[A]: Promise[A] =
      new Promise[A] {
        val state = new java.util.concurrent.atomic.AtomicReference[State[A]]

        def await: Async[A] =
          Async { callback =>
            var loop = true
            while (loop) {
              val currentState = state.get
              currentState match {
                case State.Empty(callbacks) =>
                  val updatedState = State.Empty(callback :: callbacks)
                  loop = !state.compareAndSet(currentState, updatedState)
                case State.Full(value) =>
                  loop = false
                  callback(value)
              }
            }
        }

        def succeed(a: A): Boolean = {
          var loop = true
          var result = false
          while (loop) {
            val currentState = state.get
            currentState match {
              case State.Empty(callbacks) =>
                result = true
                callbacks.foreach(callback => callback(a))
              case State.Full(value) =>
                loop = false
            }
          }
          result
        }
      }
  }
}

/**
  * As an exercise, update your implementation of the `Async` data type that
  * supported error handling to include an implementation of `Promise` that
  * allows the promise to be completed with either a successful value or an
  * error. Failing the promise with an error should signal failure to all
  * workflows that are waiting on that promise.
  */
object Promises2

/**
  * As we saw above there is some work in implementing this `Promise` data type
  * so it is nice that the Scala standard library provides an implementation
  * that we can use. However, hopefully you can see from the above that again
  * there is no "magic" here and in fact there is remarkably little going on
  * in most of these operators.
  * 
  * The `Promise` data type in the Scala standard library can be thought of as
  * the `Promise` data we implemented above with an error type fixed to
  * `Throwable`.
  * 
  * In this section you will get practice working with the different
  * constructors for promises. Essentially all of these either create an "empty"
  * box or create a box that is already filled with some existing value that is
  * either a success, a failure, or some other data type that gets translated
  * into these.
  */
object PromiseConstructors {
  import scala.concurrent._

  /**
    * Create a `Promise` that still has to be completed using the `apply`
    * constructor on `Promise`.
    */
  lazy val empty: Promise[Int] =
    ???

  /**
    * Create a `Promise` that is done with an existing value of your choice
    * using the `successful` constructor on `Promise`.
    */
  lazy val alreadyDone: Promise[Int] =
    ???

  /**
    * Create a `Promise` that is already failed with a failure of your choice
    * using the `failed` constructor on `Promise`. Note what information the
    * type signature gives you regarding the ways that this `Promise` could be
    * failed.
    */
  lazy val alreadyFailed: Promise[Int] =
    ???

  /**
    * Create a `Promise` that is either successful or failed using the `fromTry`
    * constructor on `Promise`.
    */
  lazy val alreadySomething: Promise[Int] =
    ???
}

/**
  * As we saw in our introduction to promises above, there are only two
  * fundamental operators on promises (1) completing them with a result and
  * (2) waiting for a result.
  * 
  * For the Scala Promise there is really only one way to wait for the result
  * and there are a variety of ways to complete the promise with either a
  * success or a failure.
  */
object PromiseOperators {
  import scala.concurrent._

  lazy val emptyBox: Promise[Int] =
    Promise[Int]()

  lazy val anotherBox: Promise[Int] =
    ???

  /**
    * Create a `Future` to describe the result of asynchronously waiting for
    * a `Promise` to be completed using the `future` operator on `Promise`.
    */
  lazy val waitForTheBox: Future[Int] =
    ???

  /**
    * Create an operator that will complete the `Future` with a successful
    * value using the `succeed` operator on `Promise`.
    */
  def fillTheBox(n: Int): Unit =
    ???

  /**
    * Create another operator that will fail the box with an error of your
    * choice.
    */
  def failTheBox(t: Throwable): Unit =
    ???

  /**
    * As we saw above, a `Promise` can only ever be completed once, so 
    * subsequent attempts to complete it will just discard the new value.
    * Sometimes this is fine but in other cases we need to take special
    * action depending on whether we were the first to complete a `Promise` or
    * it was already completed. The `try` variants of these operators return a
    * `Boolean` that is `true` if we were the ones to complete the `Promise`
    * and `false` otherwise.
    * 
    * Use one of these operators to implement a method that also returns
    * whether we were the ones to fill the box.
    */
  def fillTheBoxFirst(n: Int): Boolean =
    ???
}
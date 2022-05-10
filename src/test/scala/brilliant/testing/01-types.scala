package brilliant.testing

import zio.test._

import java.time.Instant
import scala.collection.immutable.SortedMap

/**
 * While Scala's type system can't replace the need for testing, we can make
 * some states unrepresentable and so reduce the number of things we need to
 * test. This can help us allocate our "testing budget" of time we have to
 * spend writing tests to the higher value tests versus verifying basic
 * invariants.
 * 
 * In the example below, update the signature of load actions to allow you to
 * delete as many of these tests as possible or if you cannot delete them
 * reduce their scope.
 */
object Types extends ZIOSpecDefault {

  def spec = suite("loadActions")(
    // test("all instants have at least one associated action") {
    //   ???
    // },
    // test("instants are distinct") {
    //   ???
    // },
    test("instants are sorted") {
      ???
    }
  )
}

object TypesExample {

  /**
    * A sealed trait representing some action that our system should perform.
    */
  trait Action

  /**
   * Loads all the actions we are supposed to run. Each action has a time
   * associated with it that is the time we are supposed to run it. There may
   * be multiple actions that are supposed to run at the same time so we return
   * a list of actions associated with each time. A given point of time should
   * only occur once in the list and the list should be sorted in ascending
   * order.
   */
  def loadActions: SortedMap[Instant, ::[Action]] =
    ???

  loadActions.get(Instant.now) match {
    case Some(actions) =>
      actions match {
        case first :: rest => ???
      }
    case None => ???
  }

  object Example {

    sealed trait List[+A]

    object List {
      final case class ::[A](head: A, tail: List[A]) extends List[A]
      case object Nil extends List[Nothing]
    }
  }
}
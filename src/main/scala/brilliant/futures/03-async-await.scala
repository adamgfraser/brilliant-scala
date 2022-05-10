package brilliant.futures

import FutureExtensions._
import scala.concurrent.ExecutionContext

import scala.concurrent._
import scala.async.Async._

/**
  * While the for comprehension syntax is a dramatic improvement from the
  * callback based style of writing asynchronous code, it is still less
  * accessible than normal imperative style code. We have to learn this new
  * for comprehension syntax, which can create a barrier to usability,
  * particularly for developers from other programming languages.
  * 
  * Furthermore, as we saw in the section on future basics, futures execution
  * model creates situations where it can be somewhat unclear when evaluation
  * happens and the actual "work" is not done in the for comprehension. This
  * can be confusing, especially for developers used to working with a more
  * "functional" style of programming.
  * 
  * The async await functionality tries to address both of these concerns. Note
  * that this is not part of the Scala standard library and needs to be added
  * as a separate module. See the build file for this project for an example
  * of this.
  */
object AsyncAwait extends App {

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

  /**
    * Recall than this method will run `asyncTwo` and `asyncThree` sequentially,
    * because we are running each of the futures within the for comprehension
    * and lines of a for comprehension are always evaluated sequentially.
    */
  def future1(implicit ec: ExecutionContext): Future[Int] =
    for {
      two   <- asyncTwo
      three <- asyncThree
    } yield two + three

  /**
    * In contrast, this method will run `asyncTwo` and `asyncThree` in parallel.
    * Now we are "kicking off" the two asynchronous computations on the first
    * two lines before the for comprehension with no sequencing between them.
    * The for comprehension is just waiting for the result of one and then
    * waiting for the result of the other.
    */
  def future2(implicit ec: ExecutionContext): Future[Int] = {
    lazy val asyncTwo1   = asyncTwo
    lazy val asyncThree1 = asyncThree
    for {
      three <- asyncThree1
      two   <- asyncTwo1
    } yield two + three
  }

  /**
    * Let's rewrite the example above to use the async await syntax. Our
    * approach here is we still reference each future we want to "kick off",
    * here assigning them to the values `two` and `three`. Then instead of
    * using a for comprehension to collection the results we use the `await`
    * operator.
    * 
    * Note that this doesn't actually block any threads/ The `await` operator
    * can only be used inside an `async` block. The macro implementation of
    * `async` will automatically rewrite all uses of `await` to a for
    * comprehension style. So this will be rewritten back to `future2`
    * internally.
    */
  def future3(implicit ec: ExecutionContext): Future[Int] =
    async {
      val two = asyncTwo
      val three = asyncThree
      await(two) + await(three)
    }

  /**
    * Note that we still have the same issue as we did before with potentially
    * seemingly insignificant changes changing the execution model. Compared to
    * the parallel implementation above, this implement just inlines the
    * definition of `two` and `three` which would always be a value preserving
    * refactoring in purely functional code. However, this changes the program
    * from being executed from in parallel to sequential here.
    */
  def future4(implicit ec: ExecutionContext): Future[Int] =
    async {
      await(asyncTwo) + await(asyncThree)
    }

  future4(ec).await(ec)
}

object Practice {

  trait User

  /**
    * Throws an exception if user does not have appropriate permissions
    */
  def checkPermissions(implicit ec: ExecutionContext): Future[Unit] =
    ???

  /**
    * Asynchronously looks up user in database.
    */
  def getUser(id: Int)(implicit ec: ExecutionContext): Future[User] =
    ???

  /**
    * Returns a new `User` with some data updated.
    */
  def updateUser(user: User): User =
    ???

  /**
    * Saves a user to the database asynchronously.
    */
  def saveUser(user: User)(implicit ec: ExecutionContext): Future[Unit] =
    ???

  /**
    * Constructs an event describing an update to a user and streams it out.
    */
  def streamUserUpdate(oldUser: User, newUser: User)(implicit ec: ExecutionContext): Future[Unit] =
    ???

  /**
    * Using the async await syntax, write a program that will load a user,
    * update the user's information, and then in parallel save the updated
    * information to the database and stream out the update.
    */
  def myProgram1(implicit ec: ExecutionContext): Future[Unit] =
    ???

  /**
    * Assume we have now concluded that we can only stream out the update
    * after we have successfully saved the updated user to the database. Revise
    * your implementation to reflect this new requirement.
    */
  def myProgram2(implicit ec: ExecutionContext): Future[Unit] =
    ???
}
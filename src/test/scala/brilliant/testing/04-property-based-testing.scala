package brilliant.testing

import zio.test._

/**
  * Another technique when writing tests is to use property based tests. The
  * test framework will generate a variety of values of your data type for
  * you and make some assertion on each one.
  * 
  * 1. Using the data modeling techniques we have described, create a
  * representation of `ScheduledActions`, which should be an ordered collection
  * of points in time along with actions that are supposed to occur at each
  * point in time. Use whatever tools you think are appropriate to enforce as
  * many invariants as possible with the type signature and to prevent invalid
  * instances of data from being created.
  * 
  * 2. Write a constructor that allows a user to create a `ScheduledActions`
  * from an `Iterable[Instant, Action)]`. The constructor should ensure that
  * only valid values of `ScheduledActions` can be created.
  * 
  * 3. Write a property based test to verify that for any input, the
  * `ScheduledActions` created by your constructor are sorted and always
  * contain exactly the actions that were in the original input.
  */
object Properties extends ZIOSpecDefault {

  def spec = suite("Properties")(
    test("greet") {
      ???
    }
  )
}

object PropertiesExample {

  trait Action

  type ??? = Nothing

  type ScheduledActions = ???
}
package brilliant.testing

import zio.test._

/**
  * Another technique we can use to make it easier to write tests that is also
  * just a good practice in general is to refactor our code into smaller
  * building blocks that we can piece together. This way we can test each of
  * these components individually. This is particularly valuable if we can
  * extract pure data transformations out of our side effecting code.
  * 
  * Make the code below more testable by refactoring the `greet` operator into
  * two operators that do I/O and one pure data transformation. Then write a
  * test for the pure data transformation.
  * 
  * Note your findings regarding the testability of your revised code versus
  * the original.
  */
object Composition extends ZIOSpecDefault {

  def spec = suite("Composition")(
    test("greet") {
      ???
    }
  )
}

object CompositionExample {

  def greet: Unit = {
    val name = scala.io.StdIn.readLine("What is your name? ")
    println(s"Hello, $name!")
  }
}
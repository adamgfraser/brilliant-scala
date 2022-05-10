package brilliant.testing

import zio.test._

/**
  * Another technique that is important to write testable code is to not to
  * hard code our dependencies. In the previous example, our implementation
  * hard coded a dependency on the live console. Instead, ideally we should
  * reflect all dependencies as parameters. Frameworks like ZIO with its
  * environment type can help us clean this up so we don't have to pass these
  * dependencies around everywhere explicitly but we can still do this in our
  * own code without a framework like that.
  * 
  * Refactor the `Greet` method into a `GreeterService`. Create a
  * `ConsoleGreeter` implementation of the `GreeterService` that takes a
  * `Console` service as a constructor parameter. Then define a `Live` and
  * `Test` implementation of the `Console` service. Use your `Test`
  * implementation to write a test for the `greet` method.
  */
object Dependencies extends ZIOSpecDefault {

  def spec = suite("Composition")(
    test("greet") {
      ???
    }
  )
}

object DependenciesExample {

  def greet: Unit = {
    val name = scala.io.StdIn.readLine("What is your name? ")
    println(s"Hello, $name!")
  }
}
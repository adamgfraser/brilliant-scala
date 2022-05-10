package brilliant.testing

import zio.test._

import java.util.concurrent.atomic.AtomicReference

/** Another technique that is important to write testable code is to not to hard
  * code our dependencies. In the previous example, our implementation hard
  * coded a dependency on the live console. Instead, ideally we should reflect
  * all dependencies as parameters. Frameworks like ZIO with its environment
  * type can help us clean this up so we don't have to pass these dependencies
  * around everywhere explicitly but we can still do this in our own code
  * without a framework like that.
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

  trait Greeter {
    def greet(): Unit
  }

  object Greeter {

    final case class ConsoleGreeter(console: Console) extends Greeter {
      def greet(): Unit = {
        val prompt = "What is your name? "
        val input = getInput(prompt)
        val output = transformInput(input)
        writeOutput(output)
      }

      private def getInput(prompt: String): String = {
        console.readLine(prompt)
      }

      private def transformInput(input: String): String = {
        s"Hello, $input!"
      }

      private def writeOutput(output: String): Unit = {
        console.printLine(output)
      }
    }

    val live = ConsoleGreeter(Console.Live)

    def makeTest(input: AtomicReference[Vector[String]], output: AtomicReference[Vector[String]]): Greeter =
      ConsoleGreeter(Console.Test(input, output))
  }

  trait Console {
    def printLine(message: String): Unit
    def readLine(prompt: String): String
  }

  object Console {

    case object Live extends Console {
      def printLine(message: String): Unit = {
        println(message)
      }
      def readLine(prompt: String): String =
        scala.io.StdIn.readLine(prompt)
    }

    final case class Test(input: AtomicReference[Vector[String]], output: AtomicReference[Vector[String]]) extends Console {
      def printLine(message: String): Unit =
        output.updateAndGet(_ :+ message)
      def readLine(prompt: String): String =
        input.getAndUpdate(_.tail).headOption.getOrElse(throw new NoSuchElementException("there is no more input left to read!"))
    }
  }

  def greet: Unit = {
    val name = scala.io.StdIn.readLine("What is your name? ")
    println(s"Hello, $name!")
  }
}

package brilliant.testing

import zio.test._

/**
  * This section will start our discussion of testing. Unless some of the other
  * material we have covered where I have shared with you my perspective on the
  * "right answer", these materials will focus on some best practices that will
  * hopefully help you in writing testable code.
  * 
  * We will be using ZIO Test here but as we will see the syntax is similar to
  * other testing frameworks and none of the concepts we talk about will be
  * specific to ZIO Test.
  * 
  * We will also see that many of these ideas are not about how we write tests
  * but how we write code so that we can write tests for it.
  */
object Practices extends ZIOSpecDefault {

  def spec = suite("Practices")(
    test("some test") {
      assertTrue(1 + 1 == 2)
    }
  )
}
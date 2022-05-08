package brilliant.futures

import scala.concurrent._

object FutureExtensions {

  implicit final class FutureExtension[A](private val self: Future[A]) extends AnyVal {
    def debug(implicit ec: ExecutionContext): Future[A] =
      self.transform { result =>
        println(result)
        result
      }

    def await(implicit ec: ExecutionContext): A =
      scala.concurrent.Await.result(self, scala.concurrent.duration.Duration.Inf)
  }
}
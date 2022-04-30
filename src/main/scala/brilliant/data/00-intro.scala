package brilliant.data

import scala.annotation.tailrec

object intro {

  /** Scala is an extremely flexible language. This can be a great asset because
    * we can use the right tool for the job in different situations. But it can
    * also be a challenge because there can be many different ways to do things.
    * Each way can be fine on its own, but if we want to be productive on teams
    * we generally all need to "speak the same language".
    *
    * In this section we'll look at a variety of ways of tackling a simple
    * problem of adding a collection of integers to get a sense of the
    * alternatives and the trade-offs involved
    */
  object Addition {

    /** On one extreme of "Java without the semicolons" we can just iterate over
      * the elements of the collection in a loop.
      */
    def sumImperative(as: Iterable[Int]): Int = {
      val iterator = as.iterator
      var sum = 0
      while (iterator.hasNext) {
        sum += iterator.next()
      }
      sum
    }

    /** We can mechanically convert any `while` loop into a tail recursive
      * function. This is a purely functional equivalent of the version above.
      */
    def sumFunctional1(as: List[Int]): Int = {

      @tailrec
      def loop(as: List[Int], sum: Int): Int =
        as match {
          case a :: as => loop(as, sum + a)
          case Nil     => sum
        }

      loop(as, 0)
    }

    /** The `foldLeft` method handles the recursion for us and lets us write
      * this in a very straightforward way.
      */
    def sumFunctional2(as: List[Int]): Int =
      as.foldLeft(0)(_ + _)

    trait Identity[A] {
      def combine(left: A, right: A): A
      def identity: A
    }

    object Identity {

      implicit val intAddition: Identity[Int] =
        new Identity[Int] {
          def combine(left: Int, right: Int): Int =
            left + right
          def identity: Int =
            0
        }
    }

    trait ForEach[Collection[+Element]] {
      def foldMap[A, B: Identity](as: Collection[A])(f: A => B): B
    }

    object ForEach {

      implicit val listForEach: ForEach[List] =
        new ForEach[List] {
          def foldMap[A, B](as: List[A])(f: A => B)(implicit
              identity: Identity[B]
          ): B =
            as.foldLeft(identity.identity)((b, a) => identity.combine(b, f(a)))
        }

      implicit class ForEachSyntax[Collection[+Element], A](as: Collection[A]) {
        def foldMap[B: Identity](f: A => B)(implicit
            forEach: ForEach[Collection]
        ): B =
          forEach.foldMap(as)(f)
      }
    }

    import ForEach._

    /** Here with the `Identity` and `ForEach` functional abstractions we are
      * generalizing over both the collection type and the combining operation.
      * The collection doesn't have to be a `List` but can be anything that we
      * can do something "for each" element of. And the combining operation
      * doesn't have to be addition but can be anything that is associative and
      * has an identity element.
      */
    def sumAbstract(as: List[Int]): Int =
      as.foldMap(a => a)

    trait Covariant[F[+_]] {
      def map[A, B](f: A => B): F[A] => F[B]
    }

    object Covariant {

      implicit val optionCovariant: Covariant[Option] =
        new Covariant[Option] {
          def map[A, B](f: A => B): Option[A] => Option[B] =
            _ map f
        }

      implicit class CovariantSyntax[F[+_], A](as: F[A]) {
        def map[B](f: A => B)(implicit covariant: Covariant[F]): F[B] =
          covariant.map(f)(as)
      }
    }

    import Covariant._

    final case class Recursive[Case[+_]](caseValue: Case[Recursive[Case]]) {
      def fold[Z](f: Case[Z] => Z)(implicit covariant: Covariant[Case]): Z =
        f(caseValue.map(_.fold(f)))
    }

    sealed trait ListCase[+A, +B]

    object ListCase {

      final case class Cons[+A, +B](head: A, tail: B) extends ListCase[A, B]
      case object Nil extends ListCase[Nothing, Nothing]

      implicit def listCaseCovariant[A]: Covariant[
        ({
          type ListCasePartiallyApplied[+B] = ListCase[A, B]
        })#ListCasePartiallyApplied
      ] =
        new Covariant[
          ({
            type ListCasePartiallyApplied[+B] = ListCase[A, B]
          })#ListCasePartiallyApplied
        ] {
          def map[B, C](f: B => C): ListCase[A, B] => ListCase[A, C] =
            _ match {
              case Cons(head, tail) => Cons(head, f(tail))
              case Nil              => Nil
            }
        }
    }

    type RecursiveList[A] = Recursive[
      ({
        type ListCasePartiallyApplied[+B] = ListCase[A, B]
      })#ListCasePartiallyApplied
    ]

    /** Now we are further generalizing by separating the recursive structure of
      * the list from the different cases of the list. This reflects a deep
      * understanding of recursive data structures as "fixed" points of simpler,
      * non-recursive data structure and allows for very elegant implementation
      * of some operators.
      */
    def sumRecursive(as: RecursiveList[Int]): Int =
      as.fold[Int] {
        case ListCase.Cons(head, tail) => head + tail
        case ListCase.Nil              => 0
      }
  }
}

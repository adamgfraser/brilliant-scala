package brilliant.data

/**
 * There are several types in Scala that have special meaning.
 */
object special_types {

  // Cardinality
  // "Size" of different data types

  // Any type as describing some set of values
  // That set can have some size

  // Boolean - 2 possible values Set(true, false)
  // Unit - 1 (())
  // Int - 32 bit signed integer

  // Int - set of all 32 bit signed integers Int.+, Int.max

  // Any - top type
  //   Type that includes all possible values
  //   Values that have no capabilities
  //   Have no information associated with them

  trait Animal extends Any

  trait Cat extends Animal

  // Nothing - bottom type
  //  Type that includes no values, empty set
  //  Value that has every possible capability


  // val x: Int = throw new Exception

  // type OptionUnit = Option[Unit]
  // val first = None
  // val second = Some(())
  // val optionUnitValues: Set[OptionUnit] = Set(first, second)
  // type OptionNothing = Option[Nothing]
  // val third = None

  final case class WorkflowState(interruptible: Boolean, interrupting: Boolean)

  // 2 * 2

  // interruptible and interrupting
  // interruptible and not interrupting
  // not interruptible and interrupting
  // not interruptible and not interrupting

  // combinatorics - multiple the size of the two sets

  sealed trait MaybeWorkflowState

  object MaybeWorkflowState {
    case object None extends MaybeWorkflowState
    case class SomeState(state: WorkflowState) extends MaybeWorkflowState
  }

  // 1 + (2 * 2)

  // plus

  // 4 possibilities


  // A * B
  final case class MyTuple[A, B](a: A, b: B)

  // E + A
  // type MyEither[+E, +A] = Either[E, A]

  // 2 * 2
  final case class WorkflowState2(interruptible: InterruptibleStatus, interrupting: InterruptingStatus)

  sealed trait InterruptibleStatus

  case object InterruptibleStatus {
    case object Interruptible extends InterruptibleStatus
    case object NotInterruptible extends InterruptibleStatus
  }

  sealed trait InterruptingStatus

  case object InterruptingStatus {
    case object Interrupting extends InterruptingStatus
    case object NotInterrupting extends InterruptingStatus
  }


  // 1 + 1 + 1 + 1

  sealed trait WorkflowState3

  object WorkflowState3 {
    case object InterruptibleAndNotInterrupting extends WorkflowState3
    case object InterruptibleAndInterrupting extends WorkflowState3
    case object NotInterruptibleAndInterrupting extends WorkflowState3
    case object NotInterruptibleAndNotInterrupting extends WorkflowState3
  }

  // Three possible results from pulling from a stream
  // Got a value of type A and if you pull again there will potentially be more values
  // Got an error of type E and you're done
  // Got an end of stream signal

  // E + A
  trait IO[+E, +A]

  // A + E + 1

  type Pull[+E, +A] = IO[Option[E], A]

  // A
  // val x: Either[Nothing, Int] = ???

  sealed trait MyEither[+E, +A]

  object MyEither {
    case class Left[E](value: E) extends MyEither[E, Nothing]
    case class Right[A](value: A) extends MyEither[Nothing, A]
  }

  // A + 0 ===> A

  def eliminateEither[A](either: Either[Nothing, A]): A =
    either match {
      case Left(e) => e
      case Right(a) => a
    }

  val myStrangeComputation: Either[UserValidationError, Nothing] =
    ???

  // 0 + 0 == 0
  val myEvenStrangeComputation: Either[Nothing, Nothing] =
    ???

  val myComputation: Either[String, Int] = ???
  val myComputation2: Either[Throwable, Int] = ???
  val myComputation3: Either[Nothing, Int] = ???

  val myComputationWithDefault: Either[Nothing, Int] =
    myComputation match {
      case Left(_) => Right(0)
      case Right(n) => Right(n)
    }

  // Throwable can take on an infinite number of values

  sealed trait UserValidationError

  object UserValidationError {
    final case class UserNotFound(userId: Int) extends UserValidationError
    final case class InvalidEmail(userInput: String, validationErrorMessage: String) extends UserValidationError
  }

  final case class Indexed[+A](value: A, index: Long)

  // 2^64 * 0 == 0
  type IndexedNothing = Indexed[Nothing]

  trait Equivalence[From, To] {
    def from(from: From): To
    def to(to: To): From
  }

  // Either[A, B]
//  Either[B, A]

  // map
  // flatMap
  // orElse

  // Either[A, B]
  // Either[B, A]

  // Int | String
  // String | Int

  // val xxx: Either[Int, String] = ???
  // val yyy: Either[String, Int] = xxx

  def swap[A, B](either: Either[A, B]): Either[B, A] =
    either match {
      case Left(a) => Right(a)
      case Right(b) => Left(b)
    }

    // (String, Int)

    // (Int, String)

  val booleanInterruptStatusEquivalence: Equivalence[Boolean, InterruptibleStatus] =
    new Equivalence[Boolean, InterruptibleStatus] {
      def from(from: Boolean): InterruptibleStatus =
        if (from) InterruptibleStatus.Interruptible
        else InterruptibleStatus.NotInterruptible
      def to(to: InterruptibleStatus): Boolean =
        to match {
          case InterruptibleStatus.Interruptible => true
          case InterruptibleStatus.NotInterruptible => false
        }
    }

  def fromBooleanWhereTrueIsInterruptible(boolean: Boolean) = ???
  def fromBooleanWhereFalseIsInterruptible(boolean: Boolean) = ???

  // Boolean


  // 2 + 2

  /**
   * EXERCISE 1
   *
   * Find a type existing in the Scala standard library, which we will call `One`, which has a
   * single "inhabitant" (i.e. there exists a single unique value that has this type).
   */
  type One = Unit

  /**
   * EXERCISE 2
   *
   * Find a type existing in the Scala standard library, which we will call `Zero`, which has no
   * "inhabitants" (i.e. there exists no values of this type).
   */
  type Zero = Nothing

  /**
   * EXERCISE 3
   *
   * Scala allows you to treat `Nothing` as any other type. To demonstrate this to yourself,
   * change the return type of this function to whatever type you like, then try to explain why
   * this rule in the Scala compiler will not lead to any crashes of your application.
   */
  def nothingIsAnything(value: Nothing): Nothing = value
}

/**
 * Data types that are formed from case classes (products) and enums (sums) are sometimes called
 * _algebraic data types_. The word _algebraic_ here refers to the _algebraic laws_ that are
 * satisfied by type-level operators that compose types.
 */
object algebra {

  /**
   * EXERCISE 3
   *
   * If we use `*` to denote product composition of types, then as with ordinary multiplication on
   * numbers, we should have that `A * B` is the same as `B * A`.
   *
   * Although the tuples (A, B) and (B, A) are not exactly the same, they are equivalent because
   * they store the same amount of information. For example, (Boolean, String) stores the same
   * amount of information as (String, Boolean). In math and in functional programming, this
   * equivalence is called an "isomorphism", and it can be regarded as a weaker but more useful
   * definition of equality.
   */
  def toBA[A, B](ab: (A, B)): (B, A) = TODO
  def toAB[A, B](ba: (B, A)): (A, B) = TODO

  def roundtripAB[A, B](t: (A, B)): (A, B) = toAB(toBA(t))
  def roundtripBA[A, B](t: (B, A)): (B, A) = toBA(toAB(t))

  /**
   * EXERCISE 4
   *
   * If we use `+` to sum product composition of types, then as with ordinary addition on
   * numbers, we should have that `A + B` is the same as `B + A`.
   *
   * Although the eithers Either[A, B] and Either[B, A] are not exactly the s;ame, they are
   * isomorphic, as with tuples.
   */
  def toBA[A, B](ab: Either[A, B]): Either[B, A] = TODO
  def toAB[A, B](ba: Either[B, A]): Either[A, B] = TODO

  def roundtripAB[A, B](t: Either[A, B]): Either[A, B] = toAB(toBA(t))
  def roundtripBA[A, B](t: Either[B, A]): Either[B, A] = toBA(toAB(t))

  /**
   * EXERCISE 5
   *
   * As with multiplication of numbers, we also have `A * 1` is the same as `A`.
   */
  def withUnit[A](v: A): (A, Unit)    = TODO
  def withoutUnit[A](v: (A, Unit)): A = TODO

  def roundtripUnit1[A](v: A): A                 = withoutUnit(withUnit(v))
  def roundtripUnit2[A](t: (A, Unit)): (A, Unit) = withUnit(withoutUnit(t))

  /**
   * EXERCISE 6
   *
   * As with multiplication of numbers, we also have `A + 0` is the same as `A`.
   */
  def withNothing[A](v: A): Either[A, Nothing]    = TODO
  def withoutNothing[A](v: Either[A, Nothing]): A = TODO

  def roundtripNothing1[A](v: A): A                                   = withoutNothing(withNothing(v))
  def roundtripNothing2[A](t: Either[A, Nothing]): Either[A, Nothing] = withNothing(withoutNothing(t))

  /**
   * EXERCISE 7
   *
   * As with multiplication of numbers, we also have `A * 0` is the same as `0`.
   */
  def withValue[A](v: Nothing): (A, Nothing)    = TODO
  def withoutValue[A](v: (A, Nothing)): Nothing = TODO

  def roundtripValue1(v: Nothing): Nothing              = withoutValue(withValue(v))
  def roundtripValue2[A](t: (A, Nothing)): (A, Nothing) = withValue(withoutValue(t))

  /**
   * EXERCISE 8
   *
   * Algebraic data types follow the distributive property, such that `A * (B + C) = A * B + A * C`.
   */
  def distribute[A, B, C](tuple: (A, Either[B, C])): Either[(A, B), (A, C)] = TODO
  def factor[A, B, C](either: Either[(A, B), (A, C)]): (A, Either[B, C])    = TODO

  def roundtripDist1[A, B, C](t: (A, Either[B, C])): (A, Either[B, C])           = factor(distribute(t))
  def roundtripDist2[A, B, C](e: Either[(A, B), (A, C)]): Either[(A, B), (A, C)] = distribute(factor(e))
}

/**
 * The algebraic notation introduced previously is quite flexible and can model all data types,
 * including those that are generic and recursive.
 */
object algebra_of_types {
  final case class Identity[A](value: A)

  /**
   * EXERCISE 1
   *
   * All functional data types can be written using sums and products, and therefore have an
   * algebraic definition. For polymorphic data types, the algebraic definition may refer to the
   * polymorphic types. For example, the algebraic definition of the identity type shown above is
   * `A`.
   *
   * Create a polymorphic data type whose algebraic definition is `A * B`. Hint: You can use
   * `Tuple2` or create your own version of this data type.
   */
  type ATimesB

  /**
   * EXERCISE 2
   *
   * Create a polymorphic data type whose algebraic definition is `A + B`. Hint: You can use
   * `Either` or create your own version of this data type.
   */
  type APlusB

  /**
   * EXERCISE 3
   *
   * The algebraic definition of recursive polymorphic data types can be expressed using infinite
   * polynomial series. For example, the type of `List[A]` can be defined as
   * `1 + A + A * A + A * A * A + ...`, or, by using exponentiation, `1 + A + A^2 + A^3 + ...`.
   *
   * Find the algebraic definition for the following type `Tree`.
   */
  sealed trait Tree[+A]
  object Tree {
    final case class Leaf[+A](value: A)                      extends Tree[A]
    final case class Fork[+A](left: Tree[A], right: Tree[A]) extends Tree[A]
  }
}

/**
 * Different types can be equivalent, and looking at their algebraic definitions can make this
 * equivalence easier to see.
 */
object algebraic_equivalence {

  /**
   * EXERCISE 1
   *
   * The type `Either[String, Option[Int]]` can be difficult to work with, if we are concerned
   * about manipulating the `Int`. Fortunately, this type is equivalent to another one, which is
   * easier to work with. Find some type `Answer1` such that `Either[Answer1, Int]` is equivalent
   * to the first type.
   *
   * Write out the algebraic definitions of both types, and show they are equivalent.
   */
  type ComplexEither = Either[String, Option[Int]]
  type Answer1
  type SimplerEither = Either[Answer1, Int]

  /**
   * EXERCISE 2
   *
   * The type `Option[A]` is actually unnecessary, because it is equivalent to `Either[Answer2, A]`,
   * for some type `Answer2`. Find what this type is and write your answer below.
   *
   * Write out the algebraic definitions of both `Option` and your new type, and show they are
   * equivalent.
   */
  type Answer2
  type NewOption[+A] = Either[Answer2, A]

  /**
   * EXERCISE 3
   *
   * The type `Try[A]` is also unnecessary. Find a type `Answer3` such that `Either[Answer3, A]` is
   * equivalent to `Try[A]`.
   *
   * Write out the algebraic definitions of both `Try` and your new type, and show they are
   * equivalent.
   */
  type Answer3
  type NewTry[+A] = Either[Answer3, A]

}

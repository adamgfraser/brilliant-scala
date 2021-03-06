package brilliant.data

import javax.swing.event.DocumentEvent.EventType

/** Sometimes we don't want to take the time to model data precisely. For
  * example, we might want to model an email address with a string, even though
  * most strings are not valid email addresses.
  *
  * In such cases, we can save time by using a smart constructor, which lets us
  * ensure we model only valid data, but without complicated data types.
  */
object smart_constructors {

  // final case class Email(value: String)

  

  sealed abstract case class Email private (value: String)

  trait ValidationError

  object ValidationError {
    final case class Runtime(t: Throwable) extends ValidationError
    case object Empty extends ValidationError
  }

  object Email {

    // def apply(value: String): Option[Email] =
    //   fromString(value)
    def fromString(email: String): Option[Email] =
      if (email.matches("""/\w+@\w+\.com""")) Some(new Email(email) {})
      else None
    def fromStringEither(email: String): Either[ValidationError, Email] =
      ???
    def fromStringThrow(email: String): Email =
      if (email.matches("""/\w+@\w+\.com""")) new Email(email) {}
      else throw new Exception("not a valid email")
  }

  sealed abstract case class User private (
      name: Name,
      email: Email,
      address: Address
  )

  object User {

    /**
     * Makes a user with no validation. The caller is responsible for ensuring
     * that the data is valid. Be very careful about using this.
     */
    def unsafeMake(name: Name, email: Email, address: Address): User =
      new User(name, email, address) {}

    // implicit val nameDecoder: JSONDecoder[Name] =
    //   JSONDecoder[String].map(User.unsafeMake(_))

    def make(
        name: String,
        email: String,
        address: String
    ): Either[ValidationError, User] =
    for {
      name    <- Name.fromString(name) // Option[Name] === Either[Unit, Name]
      email   <- Email.fromStringEither(email) // Try[Name] === Either[Throwable, Email]
      address <- Address.fromString(address) // Either[ValidationError, Address]
    } yield new User(name, email, address) {}
  }

  trait Name

  object Name {
    def fromString(string: String): Either[ValidationError, Name] =
      ???
  }

  trait Address

  object Address {
    def fromString(string: String): Either[ValidationError, Address] =
      ???
  }

  // Option is an error type where the error contains no information
  // Either is an error type where the error contains a type that we choose
  // Try is an error type where the error type is fixed to Throwable (Either[Throwable, A])
  // Validation is an error type where the error is custom but where we can potentially have multiple errors

  trait Validation[+E, +A] { self =>
    import Validation._

    def zipWith[E1 >: E, B, C](that: Validation[E1, B])(f: (A, B) => C): Validation[E1, C] =
      self match {
        case Failure(e) => that match {
          case Failure(e1) => Failure(e ++ e1)
          case Success(b)  => Failure(e)
        }
        case Success(a) => that match {
          case Failure(e)  => Failure(e)
          case Success(b)  => Success(f(a, b))
        }
      }
  }

  object Example {

    def validateName(name: String): Validation[ValidationError, Name] =
      ???

    def validateEmail(email: String): Validation[ValidationError, Email] =
      ???

    validateName(???).zipWith(validateEmail(???))((name, email) => ???)
  }

  object Validation {
    final case class Success[+A](value: A) extends Validation[Nothing, A]
    final case class Failure[+E](errors: Vector[E]) extends Validation[E, Nothing]
  }

  // val x = Email("adam@example.com")

  /** EXERCISE 1
    *
    * Create a smart constructor for `NonNegative` which ensures the integer is
    * always non-negative.
    */
  sealed abstract case class NonNegative private (value: Int)

  /** EXERCISE 2
    *
    * Create a smart constructor for `Age` that ensures the integer is between 0
    * and 120.
    */
  sealed abstract case class Age private (value: Int)

  import scala.util.{Success, Failure, Try}

  object Age {
    def fromInt(i: Int): Try[Age] = {
      if (i < 0) Failure(new IllegalArgumentException("age cannot be negative!"))
      else if(i > 120) Failure(new IllegalArgumentException(s"age[$i] is too high (> 120)"))
      else Success(new Age(i) {})
    }
  }

  /** EXERCISE 3
    *
    * Create a smart constructor for password that ensures some security
    * considerations are met.
    */
  sealed abstract case class Password private (value: String)
}

object applied_smart_constructors {

  /** EXERCISE 1
    *
    * Identify the weaknesses in this data type, and use smart constructors (and
    * possibly other techniques) to correct them.
    */
  final case class BankAccount(
      id: String,
      name: String,
      balance: Double,
      opened: java.time.Instant
  )

  /** EXERCISE 2
    *
    * Identify the weaknesses in this data type, and use smart constructors (and
    * possibly other techniques) to correct them.
    */
  final case class Person(age: Int, name: String, salary: Double)

  /** EXERCISE 3
    *
    * Identify the weaknesses in this data type, and use smart constructors (and
    * possibly other techniques) to correct them.
    */
  // final case class SecurityEvent(
  //     machine: Machine,
  //     timestamp: Timestamp,
  //     eventType: EventType
  // )

//   sealed trait EventType {
//     def toInt: Int =
//       this match {
//         case EventType.PortScanning    => 0
//         case EventType.DenialOfService => 1
//         case EventType.InvalidLogin    => 2
//       }
//   }

//   object EventType {
//     case object PortScanning extends EventType
//     case object DenialOfService extends EventType
//     case object InvalidLogin extends EventType

//     def fromInt(int: Int): Option[EventType] =
//       int match {
//         case 0 => Some(PortScanning)
//         case 1 => Some(DenialOfService)
//         case 2 => Some(InvalidLogin)
//         case _ => None
//       }

//     def unsafeFromInt(int: Int): EventType =
//       fromInt(int).getOrElse(throw new IllegalArgumentException(s"invalid event type: $int"))
//   }



//   sealed abstract case class Machine private (value: String)

//   object Machine {
//     def fromString(string: String): Option[Machine] =
//       ???
//   }

//   sealed abstract case class Timestamp private (value: String)

//   object Timestamp {
//     def fromString(string: String): Option[Timestamp] =
//       ???
//   }

//   sealed trait EventType
// object EventType{
//   case object PortScanning extends EventType
//   case object DenialOfService extends EventType
//   case object InvalidLogin extends EventType
// }
// sealed abstract case class SecurityEvent private(machine: SecurityEvent.IPv4,
//                                                  timestamp: java.time.Instant,
//                                                  eventType: EventType)
// object SecurityEvent {
//   private[this] val Machine = raw"(\d+)\.(\d+)\.(\d+)\.(\d+)".r
//   type IPv4 = (Int, Int, Int, Int)
//   def mkSecEvent(machine: String, timestamp: java.time.Instant, event: EventType): Option[SecurityEvent] =
//     machine match {
//       case Machine(n1, n2, n3, n4) =>
//         val ip: IP = (java.lang.Integer.parseInt(n1),
//           java.lang.Integer.parseInt(n2),
//           java.lang.Integer.parseInt(n3),
//           java.lang.Integer.parseInt(n4))
//         Some(new SecurityEvent(ip, timestamp, event) {})
//       case _ => None
//     }
// }

// type Machine = String   //Not sure I actually want a wrapper here, but gives clarity in code
//   /**
//    * EXERCISE 3
//    *
//    * Identify the weaknesses in this data type, and use smart constructors (and possibly other
//    * techniques) to correct them.
//    */
//   final case class SecurityEvent(machine: Machine,
//                                  timestamp: Instant,
//                                  eventType: EventType.type)

//   object EventType extends  Enumeration {
//     val PORTS_CANNING,DENIAL_OF_SERVICE,INVALID_LOGIN = Value
//   }
}

package brilliant.data

import scala.annotation.tailrec

/** The following exercises test your ability to model various entities using
  * case classes.
  */
object product_modeling {

  /** EXERCISE 1
    *
    * Using a case class, create a model of a product, which has a name,
    * description, and a price.
    */
  final case class Product()

  /** EXERCISE 2
    *
    * Using a case class, create a model of a a user profile, which has a
    * picture URL, and text- based location (indicating the geographic area
    * where the user is from).
    */
  final case class UserProfile()

  /** EXERCISE 3
    *
    * Using a case class, create a model of an item that can be posted on
    * LinkedIn's feed. This item contains a subject and some text.
    */
  final case class FeedItem()

  /** EXERCISE 4
    *
    * Using a case class, create a model of an event, which has an event id, a
    * timestamp, and a map of properties (String/String).
    */
  final case class Event()
}

/** The following exercises test your ability to model various entities using
  * enums.
  */
object sum_modeling {

  /** EXERCISE 1
    *
    * Using an enum, create a model of a color, which could be `Red`, `Green`,
    * `Blue`, or `Custom`, and if `Custom`, then it should store `red`, `green`,
    * and `blue` components individually, as an integer (`Int`) value.
    */
  sealed trait Color
  object Color {
    case object Red extends Color
  }

  /** EXERCISE 2
    *
    * Using an enum, create a model of an web event, which could be either a
    * page load for a certain URL, a click on a particular button, or a click to
    * a specific URL.
    */
  sealed trait WebEvent
  object WebEvent {
    final case class PageLoad(url: String) extends WebEvent
  }

  /** EXERCISE 3
    *
    * Using an enum, create a model of an age bracket, which could be baby,
    * child, young adult, teenager, adult, mature adult, or senior adult.
    */
  sealed trait AgeBracket
  object AgeBracket {
    case object Child extends AgeBracket
  }

  /** EXERCISE 4
    *
    * Using an enum, create a model of a step in a JSON pipeline, which could be
    * transform, aggregate, or save to file. aggregate.
    */
  type Json
  sealed trait JsonPipelineStep
  object JsonPipeline {
    final case class Transform(fn: Json => Json) extends JsonPipelineStep
  }
}

/** The following exercises test your ability to model various entities using
  * both case classes and enums.
  */
object mixed_modeling {

  // Example of domains to model
  //  AST - expression nodes
  //  ??? - modeling companies and evidence
  //        sum types and product types

  // claim has three parts
  // some concept you are talking about - string
  // some relation
  // some other concept you are talking about

  // "X refers to Y"
  // every claim has evidence supporting it

  // Talking about companies specifically

  // Claim("The ball", "is", "red")
  final case class Claim[Sub](
      subject: Sub,
      relation: Relation,
      referrent: Concept
  )
  final case class SupportedClaim[Sub](claim: Claim[Sub], evidence: Evidence)

  type CompanyClaim = Claim[Concept.Company]
  type SupportedCompanyClaim = SupportedClaim[Concept.Company]

  def doSomethingWithConceptClaim(claim: Claim[Concept]): Unit = ???

  sealed trait Concept

  object Concept {
    final case class Company(ticker: String) extends Concept
    final case class Other(label: String) extends Concept
  }

  // product type -- "string" so simple type
  // sum type

  trait Relation {
    ???
  }

  final case class Relation1(label: String)
  sealed trait Relation2
  object Relation2 {
    final case object Is
    final case object GreaterThan
  }

  trait Evidence
  object Evidence {
    final case class Single(evidence: String) extends Evidence
    final case class Both(left: Evidence, right: Evidence) extends Evidence
  }
  trait Company

  /** EXERCISE 1
    *
    * Using only case classes and enums, create a model of an order for an
    * e-commerce platform, which would consist of a number of items, each with a
    * certain price, and an overall price, including shipping and handling
    * charges.
    */
  // final case class Item(name: String, price: Double)

  // final case class Order(items: Map[Item, Int]) {

  //   def combine(that: Order): Order =
  //     ???

  //   def totalPrice =
  //     items.foldLeft(0.0) { case (sum, (Item(_, p), q)) =>
  //       sum + (q * p)
  //     }
  // }

  // val order1 = Order(Map(Item("shoes", 100.0) -> 1, Item("shirt", 50.0) -> 1))

  import java.util.UUID

  case class Item(id: UUID, desc: String, price: Double)

  sealed trait OrderItem {
    def price: Double
  }

  case class OrderItemOrd(item: Item, quantity: Int) extends OrderItem {
    override def price: Double = item.price * quantity
  }

  case class BulkItem(item: Item, quantity: Double) extends OrderItem {
    override def price: Double = item.price * quantity
  }

  case class QuantifiedItem(item: Item, quantity: Quantity)

  sealed trait Quantity

  object Quantity {
    final case class Continuous(value: Double) extends Quantity
    final case class Discrete(value: Int) extends Quantity
  }

  import java.util.UUID
  
  case class Order(id: UUID, items: Set[OrderItem], overhead: Double) {
    @transient
    lazy val finalPrice = items.view.map(it => it.price).sum + overhead
  }

  object AnotherExample {

      trait Quantity {
        def unit: String
      }

      object Quantity {
        final case class IntQuantity(value: Int) extends Quantity {
          def unit = "Unit"
        }
        final case class DoubleQuantity(value: Double) extends Quantity {
          def unit = "KG"
        }
      }

      trait Item {
        def quantity: Quantity
      }

      object Item {
        final case class Shirt(name: String, sku: String, price: Double) extends Item {
          def quantity: Quantity.IntQuantity = ???
        }
      }

      final case class Totals()

      final case class Order(items: List[Item], totals: Totals)
  }

  // shipping and handling
  // quantity in item???
  // order???

  /** EXERCISE 2
    *
    * Using only case classes and enums, create a model of an `Email`, which
    * contains a subject, a body, a recipient, and a from address.
    */
  type Email = TODO

  /** EXERCISE 3
    *
    * Using only case classes and enums, create a model of a page layout for a
    * content-management system, which could consist of predefined elements,
    * such as a news feed, a photo gallery, and other elements, arranged in some
    * well-defined way relative to each other.
    */
  type PageLayout = TODO

  /** EXERCISE 4
    *
    * Using only case classes and enums, create a model of a rule that describes
    * the conditions for triggering an email to be sent to a shopper on an
    * e-commerce website.
    */
  type EmailTriggerRule = TODO
}

object basic_dm_graduation {
  sealed trait Command
  object Command {
    case object Look extends Command
    case object Quit extends Command
    final case class LookAt(what: String) extends Command
    final case class Go(where: String) extends Command
    final case class Take(item: String) extends Command
    final case class Drop(item: String) extends Command
    final case class Fight(who: String) extends Command

    def fromString(string: String): Option[Command] =
      string.trim.toLowerCase.split("\\s+").toList match {
        case "go" :: where :: Nil          => Some(Go(where))
        case "look" :: Nil                 => Some(Look)
        case "look" :: "at" :: what :: Nil => Some(LookAt(what))
        case "take" :: item :: Nil         => Some(Take(item))
        case "drop" :: item :: Nil         => Some(Drop(item))
        case "fight" :: who :: Nil         => Some(Fight(who))
        case ("quit" | "exit") :: Nil      => Some(Quit)
        case _                             => None
      }
  }

  /** EXERCISE
    *
    * Using case classes and sealed traits (and whatever other data types you
    * like), design a game world that can be used to play a simple text-based
    * role playing game.
    *
    * The data type should model the player, non-player characters, and items
    * available to pick up or drop in the game world.
    */
  final case class State()

  def describe(state: State): String =
    "You are playing this game."

  def process(state: State, command: Command): (String, Option[State]) =
    if (command == Command.Quit) ("You quitted", None)
    else (s"You did: ${command}, which had no effect.", Some(state))

  def main(args: Array[String]): Unit = {
    @tailrec
    def loop(state: State): Unit = {
      println(describe(state))

      val line = scala.io.StdIn.readLine()

      Command.fromString(line) match {
        case None =>
          println("Unrecognized command")
          loop(state)

        case Some(command) =>
          process(state, command) match {
            case (output, next) =>
              println(output)
              next match {
                case Some(value) => loop(value)
                case None        => println("Goodbye!")
              }
          }
      }
    }

    loop(State())
  }
}

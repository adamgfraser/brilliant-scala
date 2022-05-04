package brilliant.data

/**
 * Scala data types constructed from enums and case classes may be *recursive*: that is, a top-
 * level definition may contain references to values of the same type.
 */
object recursive extends App {

  sealed trait Expr

  object Expr {
    final case class Literal(value: Int) extends Expr
    final case class Sum(left: Expr, right: Expr) extends Expr
  }

  import Expr._

  Sum(Sum(Literal(1), Literal(2)), Sum(Literal(3), Literal(4)))

  /**
   * EXERCISE 1
   *
   * Create a recursive data type that models a user of a social network, who has friends; and
   * whose friends may have other friends, and so forth.
   */
  final case class User(userId: Long, friends: List[User])

  def x: Int = 1
  val y: Int = 2
  lazy val z: Int = 3
  lazy val zz: Int = z
  val zzz = zz

  def add(x: Int, y: Int): Int =
    ???

  def map[A, B](as: List[A])(f: A => B): List[B] =
    ???

  // thunk = () => A

//  def ifThenElseTwice[A](condition: Boolean, ifTrue: => A, ifFalse: => A): A =
//    if (condition) {
//     val memoizedIfTrue = ifTrue
//     memoizedIfTrue
//     memoizedIfTrue
//    } else {
//     ifFalse
//    }

  // val result = ifThenElseExplicit(true, 1, throw new NoSuchElementException)

  // val result2 = if (true) 1 else throw new NoSuchElementException

  // def myFunction(int: => Int): Int = {
  //   lazy val memoizedInt = int
  //   val x = int()
  //   val y = int()
  //   x + y
  // }

  def myInt: Int = {
    println("evaluating myInt")
    2
  }

  // val result3 = myFunction(myInt)

  /**
   * EXERCISE 2
   *
   * Create a recursive data type that models numeric operations on integers, including addition,
   * multiplication, and subtraction.
   */
  sealed trait NumericExpression
  object NumericExpression {
    final case class Literal(value: Int) extends NumericExpression
  }

  /**
   * EXERCISE 3
   *
   * Create a `EmailTrigger` recursive data type which models the conditions in which to trigger
   * sending an email. Include common triggers like on purchase, on shopping cart abandonment, etc.
   */
  sealed trait EmailTrigger
  object EmailTrigger {
    case object OnPurchase                                         extends EmailTrigger
    final case class Both(left: EmailTrigger, right: EmailTrigger) extends EmailTrigger
  }
}

/**
 * As Scala is an eager programming language, in which expressions are evaluated eagerly, generally
 * from left to right, top to bottom, the tree-like data structures created with case classes and
 * enumerations do not contain cycles. However, with some work, you can model cycles. Cycles are
 * usually for fully general-purpose graphs.
 */
object cyclically_recursive extends App {
  final case class Snake(food: () => Snake)

  /**
   * EXERCISE 1
   *
   * Create a snake that is eating its own tail. In order to do this, you will have to use a
   * `lazy val`.
   */
  lazy val snake: Snake = Snake(() => snake)

  println(snake)

  // Snake(null)

  /**
   * EXERCISE 2
   *
   * Create two employees "Tim" and "Tom" who are each other's coworkers. You will have to change
   * the `coworker` field from `Employee` to `() => Employee` (`Function0`), also called a "thunk",
   * and you will have to use a `lazy val` to define the employees.
   */
  final case class Employee(name: String, coworker: () => Employee)

  lazy val tim: Employee = Employee("Tim", () => tom)
  lazy val tom: Employee = Employee("Tom", () => tim)

  println(tim)
  println(tim.coworker())

  /**
   * EXERCISE 3
   *
   * Develop a List-like recursive structure that is sufficiently lazy, it can be appended to
   * itself.
   */
  // sealed trait LazyList[+A] extends Iterable[A]
  // object LazyList {
  //   def apply[A](el: A): LazyList[A] = ???

  //   // The syntax `=>` means a "lazy parameter". Such parameters are evaluated wherever they are
  //   // referenced "by name".
  //   def concat[A](left: => LazyList[A], right: => LazyList[A]): LazyList[A] = ???
  // }

  // sealed trait LazyList[+A] extends Iterable[A]

  // object LazyList {
  //   def apply[A](el: A): LazyList[A] = new LazyList[A] {
  //     override def iterator: Iterator[A] = Iterator.single(el)
  //   }
  //   def concat[A](left: => LazyList[A], right: => LazyList[A]): LazyList[A] = new LazyList[A] {
  //     override def iterator: Iterator[A] = new Iterator[A] {
  //       private[this] lazy val l = left.iterator
  //       private[this] lazy val r = right.iterator
        
  //       override def hasNext: Boolean = l.hasNext || r.hasNext
  //       override def next(): A = if (l.hasNext) l.next() else r.next()
  //     }
  //   }
  // }

  // lazy val infiniteList: LazyList[Int] = LazyList.concat(LazyList(1), infiniteList)

  // println(infiniteList.take(10).toList)

  // sealed trait LazyList[+A] { self =>
  //   import LazyList._

  //   def headOption: Option[A] =
  //     this match {
  //       case Nil => None
  //       case Cons(h, _) => Some(h())
  //     }

  //   def concat[A1 >: A](that: => LazyList[A1]): LazyList[A1] =
  //     self match {
  //       case Nil => that
  //       case Cons(h, t) => Cons(h, () => t().concat(that))
  //     }
  // }

  // object LazyList {

  //   def apply[A](a: => A): LazyList[A] =
  //     Cons(() => a, () => Nil)

  //   final case class Cons[+A](head: () => A, tail: () => LazyList[A]) extends LazyList[A]
  //   case object Nil extends LazyList[Nothing]
  // }

  // lazy val infiniteList: LazyList[Int] = LazyList(1).concat(infiniteList)

  // println(infiniteList.headOption)

  // sealed trait LazyList[+A] extends Iterable[A]
  // object LazyList {
  //   case class Single[A](el: A) extends LazyList[A] {
  //     override def iterator: Iterator[A] = Iterator(el)
  //   }
  //   case class Concat[A](left: () => LazyList[A],right: () => LazyList[A]) extends LazyList[A] {
  //     override def iterator: Iterator[A] = left().iterator ++ right().iterator
  //   }
  //   case class Map[A, B](value: LazyList[A], f: A => B) extends LazyList[B] {
  //     override def iterator: Iterator[B] = value.iterator.map(f)
  //   }
  //   def apply[A](el: A): LazyList[A] = Single(el)

  //   // The syntax => means a "lazy parameter". Such parameters are evaluated wherever they are
  //   // referenced "by name".
  //   def concat[A](left: => LazyList[A], right: => LazyList[A]): LazyList[A] = Concat(() => left, () => right)
  // }
}

package scala.lab04

object Sequences:
  
  enum Sequence[E]:
    case Cons(head: E, tail: Sequence[E])
    case Nil()

  object Sequence:

    def of[A](n: Int, a: A): Sequence[A] =
      if (n == 0) then Nil[A]() else Cons(a, of(n - 1, a))

    extension (s: Sequence[Int])
      def sum: Int = s match
        case Cons(h, t) => h + t.sum
        case _          => 0

    extension [A](s: Sequence[A])

      def map[B](mapper: A => B): Sequence[B] = s match
        case Cons(h, t) => Cons(mapper(h), t.map(mapper))
        case Nil()      => Nil()

      def filter(pred: A => Boolean): Sequence[A] = s match
        case Cons(h, t) if pred(h) => Cons(h, t.filter(pred))
        case Cons(_, t)            => t.filter(pred)
        case Nil()                 => Nil()

      def concat(other: Sequence[A]): Sequence[A] = s match
        case Cons(head, tail) => Cons(head, tail.concat(other))
        case Nil() => other

      def flatMap[B](mapper: A => Sequence[B]): Sequence[B] = s match
        case Cons(head, tail) => mapper(head).concat(tail.flatMap(mapper))
        case Nil() => Nil()

@main def trySequences() =
  import Sequences.*
  import Sequence.*
  
  val seq = Cons(10, Cons(20, Cons(30, Nil())))
  println(Sequence.sum(seq))
  println(seq.filter(_ >= 20).map(_ + 1).sum)
  println(sum(map(filter(seq)(_ >= 20))(_ + 1)))
  println(seq.flatMap(x => Cons(x, Cons(x + 1, Nil()))))
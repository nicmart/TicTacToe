package tictactoe.typeclasses.instances

import cats.Monad
import tictactoe.typeclasses.{MakeRef, MonadE, URef}

object EitherInstances {
  class EitherRef[T](var t: T) extends URef[Either, T] {
    override def get: Either[Nothing, T] = Right(t)
    override def update(f: T => T): Either[Nothing, Unit] = Right({ t = f(t); () })
  }

  object MakeEitherRef extends MakeRef[Either] {
    override def make[T](t: T): Either[Nothing, URef[Either, T]] = Right(new EitherRef(t))
  }

  implicit val me = new EitherMonadE

  class EitherMonadE extends MonadE[Either] {
    override def monadT[E]: Monad[Either[E, ?]] = new Monad[Either[E, ?]] {
      override def flatMap[A, B](fa: Either[E, A])(f: A => Either[E, B]): Either[E, B] =
        fa.flatMap(f)
      override def pure[A](x: A): Either[E, A] =
        Right(x)
      override def tailRecM[A, B](a: A)(f: A => Either[E, Either[A, B]]): Either[E, B] =
        f(a).flatMap {
          case Left(l)  => tailRecM(l)(f)
          case Right(r) => Right(r)
        }
    }

    override def monadE[T]: Monad[Either[?, T]] = new Monad[Either[?, T]] {
      override def flatMap[A, B](fa: Either[A, T])(f: A => Either[B, T]): Either[B, T] =
        fa.left.flatMap(f)
      override def pure[A](x: A): Either[A, T] = Left(x)

      override def tailRecM[A, B](a: A)(f: A => Either[Either[A, B], T]): Either[B, T] =
        f(a).left.flatMap {
          case Left(l)  => tailRecM(l)(f)
          case Right(r) => Left(r)
        }
    }
  }
}

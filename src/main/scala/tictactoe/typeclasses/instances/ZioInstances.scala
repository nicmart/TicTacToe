package tictactoe.typeclasses.instances

import cats.Monad
import cats.effect.Sync
import scalaz.zio.{IO, Ref, UIO}
import tictactoe.typeclasses.{MakeRef, MonadE, URef}

object ZioInstances {
  class ZIORef[T](zioRef: Ref[T]) extends URef[IO, T] {
    override def get: IO[Nothing, T] = zioRef.get
    override def update(f: T => T): IO[Nothing, Unit] = zioRef.update(f).unit
  }

  object MakeZioRef extends MakeRef[IO] {
    def make[T](t: T): UIO[ZIORef[T]] = Ref.make(t).map(new ZIORef(_))
  }

  implicit def sync[T]: Sync[IO[Throwable, ?]] = scalaz.zio.interop.catz.taskConcurrentInstances

  implicit val me: MonadE[IO] = new MonadE[IO] {

    override def monadT[E]: Monad[IO[E, ?]] = new Monad[IO[E, ?]] {
      override final def pure[A](a: A): IO[E, A] = IO.succeed(a)
      override final def map[A, B](fa: IO[E, A])(f: A => B): IO[E, B] = fa.map(f)
      override final def flatMap[A, B](fa: IO[E, A])(f: A => IO[E, B]): IO[E, B] = fa.flatMap(f)
      override final def tailRecM[A, B](a: A)(f: A => IO[E, Either[A, B]]): IO[E, B] =
        IO.suspend(f(a)).flatMap {
          case Left(l)  => tailRecM(l)(f)
          case Right(r) => IO.succeed(r)
        }
    }

    override def monadE[T]: Monad[IO[?, T]] = new Monad[IO[?, T]] {
      override def pure[A](x: A): IO[A, T] = IO.fail(x)
      override def flatMap[A, B](fa: IO[A, T])(f: A => IO[B, T]): IO[B, T] = fa.catchAll(f)
      override def tailRecM[A, B](a: A)(f: A => IO[Either[A, B], T]): IO[B, T] =
        IO.suspend(f(a)).catchAll {
          case Left(l)  => tailRecM(l)(f)
          case Right(r) => IO.fail(r)
        }
    }
  }
}

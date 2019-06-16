package tictactoe.typeclasses

import cats.Monad

import scala.util.Try

trait MonadE[F[+_, +_]] {
  def monadT[E]: Monad[F[E, ?]]
  def monadE[T]: Monad[F[?, T]]
  final def throwE[E, T](e: E): F[E, T] = monadE[T].pure(e)
  final def handleError[E, E2, T](f: F[E, T], handle: E => F[E2, T]): F[E2, T] =
    monadE[T].flatMap(f)(handle)

  final def fromEither[E, T](either: Either[E, T]): F[E, T] =
    either.fold(throwE[E, T], monadT[E].pure[T])

  final def fromTry[T](t: Try[T]): F[Throwable, T] =
    fromEither(t.toEither)

  final def pure[T](t: T): F[Nothing, T] = monadT[Nothing].pure(t)
  final def unit: F[Nothing, Unit] = pure(())
}

object MonadE {
  def apply[F[+_, +_]](implicit me: MonadE[F]): MonadE[F] = me

  implicit def monad[F[+_, +_], E](implicit monadE: MonadE[F]): Monad[F[E, ?]] = monadE.monadT

  implicit def ops[F[+_, +_]: MonadE, E, T](ft: F[E, T]): MonadEOps[F, E, T] =
    new MonadEOps[F, E, T](ft)

  final class MonadEOps[F[+_, +_], E, T](private val ft: F[E, T]) {
    def flatMap[E2 >: E, T2](f: T => F[E2, T2])(implicit ME: MonadE[F]): F[E2, T2] =
      ME.monadT[E2].flatMap(ft)(f)

    def map[T2](f: T => T2)(implicit ME: MonadE[F]): F[E, T2] =
      flatMap(t => ME.monadT[E].pure(f(t)))

    def handleError[E2 <: E](f: E => F[E2, T])(implicit ME: MonadE[F]): F[E2, T] =
      ME.handleError(ft, f)

    def mapError[E2](f: E => E2)(implicit ME: MonadE[F]): F[E2, T] =
      ME.monadE[T].map(ft)(f)
  }
}

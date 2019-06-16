package tictactoe.typeclasses

import cats.{Functor, Monad}

import scala.util.Try

trait MonadE[F[+_, +_]] {
  def monad[E]: Monad[F[E, ?]]
  def functor[T]: Functor[F[?, T]]
  def throwE[E, T](e: E): F[E, T]
  def handleError[E, E2 <: E, T](f: F[E, T], handle: E => F[E2, T]): F[E2, T]
}

object MonadE {
  def apply[F[+_, +_]](implicit me: MonadE[F]): MonadE[F] = me

  implicit def monad[F[+_, +_], E](implicit monadE: MonadE[F]): Monad[F[E, ?]] = monadE.monad

  implicit def ops[F[+_, +_]: MonadE, E, T](ft: F[E, T]): MonadEOps[F, E, T] =
    new MonadEOps[F, E, T](ft)

  final class MonadEOps[F[+_, +_], E, T](private val ft: F[E, T]) {
    def flatMap[E2 >: E, T2](f: T => F[E2, T2])(implicit ME: MonadE[F]): F[E2, T2] =
      ME.monad[E2].flatMap(ft)(f)

    def map[T2](f: T => T2)(implicit ME: MonadE[F]): F[E, T2] = flatMap(t => ME.monad[E].pure(f(t)))

    def handleError[E2 <: E](f: E => F[E2, T])(implicit ME: MonadE[F]): F[E2, T] =
      ME.handleError(ft, f)

    def mapError[E2](f: E => E2)(implicit ME: MonadE[F]): F[E2, T] =
      ME.functor[T].map(ft)(f)
  }
}

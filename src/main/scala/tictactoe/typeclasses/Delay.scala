package tictactoe.typeclasses

trait Delay[F[_, _]] {
  def delay[T](t: => T): F[Throwable, T]
  def delayTotal[T](t: => T): F[Nothing, T]
}

object Delay {
  def apply[F[_, _]](implicit d: Delay[F]): Delay[F] = d
}

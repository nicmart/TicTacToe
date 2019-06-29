package tictactoe.typeclasses

trait URef[F[_, _], T] {
  def get: F[Nothing, T]
  def update(f: T => T): F[Nothing, Unit]

  final def set(t: T): F[Nothing, Unit] = update(_ => t)
}

trait MakeRef[F[_, _]] {
  def make[T](t: T): F[Nothing, URef[F, T]]
}

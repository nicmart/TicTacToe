package tictactoe.console

trait Console[F[_, _]] {
  def read: F[Throwable, String]
  def put(string: String): F[Nothing, Unit]
}

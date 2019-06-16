package tictactoe.random

trait Random[F[_, _]] {
  def nextInt(max: Int): F[Nothing, Int]
}

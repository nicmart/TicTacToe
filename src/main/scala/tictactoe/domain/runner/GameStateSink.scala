package tictactoe.domain.runner

trait GameStateSink[F[_, _], S] {
  def update(f: S => S): F[Nothing, Unit]
}

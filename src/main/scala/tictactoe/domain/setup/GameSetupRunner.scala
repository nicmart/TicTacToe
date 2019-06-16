package tictactoe.domain.setup

trait GameSetupRunner[F[_, _]] {
  def runSetup: F[Nothing, GameSetup]
}

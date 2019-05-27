package tictactoe.domain.model

sealed trait GameState
object GameState {
  final case object InProgress extends GameState
  final case class Finished(result: Result) extends GameState

  sealed trait Result
  object Result {
    final case class Winner(winner: Mark) extends Result
    final case object Draw extends Result
  }
}

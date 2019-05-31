package tictactoe.domain.game.model

sealed abstract class State(val inProgress: Boolean)

object State {
  final case class InProgress(currentPlayer: Player) extends State(true)
  final case class Finished(result: Result) extends State(false)

  sealed trait Result
  object Result {
    final case class Winner(winner: Player) extends Result
    final case object Draw extends Result
  }
}

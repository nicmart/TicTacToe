package tictactoe.domain.model

sealed trait State
object State {
  final case object InProgress extends State
  final case class Finished(result: Result) extends State

  sealed trait Result
  object Result {
    final case class Winner(winner: Player) extends Result
    final case object Draw extends Result
  }
}

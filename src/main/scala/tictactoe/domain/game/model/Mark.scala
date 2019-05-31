package tictactoe.domain.game.model

sealed trait Mark {
  def switch: Mark = this match {
    case Mark.X => Mark.O
    case Mark.O => Mark.X
  }
}
object Mark {
  final case object X extends Mark
  final case object O extends Mark
}

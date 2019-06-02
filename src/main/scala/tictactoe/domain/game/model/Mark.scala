package tictactoe.domain.game.model

sealed trait Mark {
  def switch: Mark = fold(Mark.O, Mark.X)

  def fold[T](ifX: => T, ifO: => T): T = this match {
    case Mark.X => ifX
    case Mark.O => ifO
  }
}
object Mark {
  final case object X extends Mark
  final case object O extends Mark
}

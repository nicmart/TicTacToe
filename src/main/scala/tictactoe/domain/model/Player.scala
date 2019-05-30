package tictactoe.domain.model

final case class Player(mark: Mark) {
  def switch: Player = Player(mark.switch)
}

object Player {
  val playerX: Player = Player(Mark.X)
  val playerO: Player = Player(Mark.O)
}

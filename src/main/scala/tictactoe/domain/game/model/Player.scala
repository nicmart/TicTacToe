package tictactoe.domain.game.model

import tictactoe.domain.game.model.Player.{Player1, Player2}

sealed abstract class Player(val mark: Mark) {
  def switch: Player = fold(Player2, Player1)

  def fold[T](ifPlayerOne: => T, ifPlayer2: => T): T = this match {
    case Player1 => ifPlayerOne
    case Player2 => ifPlayer2
  }
}

object Player {
  final case object Player1 extends Player(Mark.X)
  final case object Player2 extends Player(Mark.O)
}

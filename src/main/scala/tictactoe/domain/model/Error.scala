package tictactoe.domain.model

import tictactoe.domain.model.Board.Cell

sealed trait Error
object Error {
  case class IllegalMove(game: Game, move: Cell) extends Error
  case object InvalidXCoordinate extends Error
  case object InvalidYCoordinate extends Error
}

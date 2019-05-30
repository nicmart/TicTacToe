package tictactoe.domain.model

import tictactoe.domain.model.Board.Cell

sealed trait Error
object Error {
  case object GameHasAlreadyEnded extends Error
  case class CellOccupied(game: StandardGame, move: Cell) extends Error
  case object InvalidXCoordinate extends Error
  case object InvalidYCoordinate extends Error
}

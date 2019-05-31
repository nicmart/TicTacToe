package tictactoe.domain.model

import tictactoe.domain.Game
import tictactoe.domain.model.Board.Cell

sealed trait Error
object Error {
  case object GameHasAlreadyEnded extends Error
  case class CellOccupied(game: Game, move: Cell) extends Error
  case object InvalidXCoordinate extends Error
  case object InvalidYCoordinate extends Error
}

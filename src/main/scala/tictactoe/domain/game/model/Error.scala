package tictactoe.domain.game.model

import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell

sealed trait Error
object Error {
  case object GameHasAlreadyEnded extends Error
  case class CellOccupied(game: Game, move: Cell) extends Error
  case object InvalidXCoordinate extends Error
  case object InvalidYCoordinate extends Error
}

package tictactoe.domain.game

import tictactoe.domain.game.model.State.InProgress
import tictactoe.domain.game.model._

trait Game {
  def board: Board
  def state: State
  def makeMove(cell: Board.Cell): Either[Error, Game]
  def availableMoves: List[Board.Cell]

  final def size: Int = board.size.value
  final def inProgress: Boolean = state.inProgress
  final def currentPlayer: Either[Error, Player] =
    state match {
      case InProgress(currentPlayer) => Right(currentPlayer)
      case _                         => Left(Error.GameHasAlreadyEnded)
    }
}

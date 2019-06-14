package tictactoe.domain.game

import tictactoe.domain.game.model._

trait Game {
  def board: Board
  def state: State
  def currentPlayer: Player
  def makeMove(cell: Board.Cell): Either[Error, Game]
  def availableMoves: List[Board.Cell]

  final def size: Int = board.size.value
  final def inProgress: Boolean = state.inProgress
}

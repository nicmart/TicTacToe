package tictactoe.domain

import tictactoe.domain.model.{Board, Error, State}

trait Game {
  def board: Board
  def state: State
  def availableMoves: List[Board.Cell]
  def makeMove(cell: Board.Cell): Either[Error, Game]

  final def size: Int = board.size.value
  final def inProgress: Boolean = state.inProgress
}

package tictactoe.domain.model

import tictactoe.domain.model.Board.Cell

trait Rules {
  def checkIfLegalMove(game: Game, cell: Cell): Either[Error, Unit]
  def availableMoves(game: Game): List[Cell]
}

object Rules extends Rules {
  override def availableMoves(game: Game): List[Cell] = game.board.emptyCells.toList
  override def checkIfLegalMove(game: Game, cell: Cell): Either[Error, Unit] =
    Either.cond(game.board.markAt(cell).isEmpty, (), Error.IllegalMove(game, cell))
}

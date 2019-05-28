package tictactoe.domain.model

import tictactoe.domain.model.Board.Cell
import tictactoe.domain.model.GameState.InProgress
import tictactoe.domain.model.GameState.Result.{Draw, Winner}

trait Rules {
  def checkIfLegalMove(game: Game, cell: Cell): Either[Error, Unit]
  def availableMoves(game: Game): List[Cell]
  def state(game: Game): GameState
}

object Rules extends Rules {
  override def availableMoves(game: Game): List[Cell] =
    if (state(game) == InProgress)
      game.board.emptyCells.toList
    else Nil

  override def checkIfLegalMove(game: Game, cell: Cell): Either[Error, Unit] =
    Either.cond(game.board.markAt(cell).isEmpty, (), Error.CellOccupied(game, cell))

  override def state(game: Game): GameState =
    game.board.allLines.flatMap(winner) match {
      case Nil if game.board.emptyCells.nonEmpty => GameState.InProgress
      case Nil                                   => GameState.Finished(Draw)
      case mark :: _                             => GameState.Finished(Winner(mark))
    }

  private def winner(line: Line): Option[Mark] =
    if (line.cellsStates.forall(state => state.contains(Mark.O))) Some(Mark.O)
    else if (line.cellsStates.forall(state => state.contains(Mark.X))) Some(Mark.X)
    else None
}

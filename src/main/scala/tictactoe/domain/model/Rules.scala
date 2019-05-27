package tictactoe.domain.model

import tictactoe.domain.model.Board.Cell
import tictactoe.domain.model.Error.GameHasEnded
import tictactoe.domain.model.GameState.InProgress
import tictactoe.domain.model.GameState.Result.{Draw, Winner}

trait Rules {
  def checkIfLegalMove(game: Game, cell: Cell): Either[Error, Unit]
  def checkIfGameInProgress(game: Game): Either[Error, Unit] =
    Either.cond(state(game) == InProgress, (), GameHasEnded)
  def availableMoves(game: Game): List[Cell]
  def state(game: Game): GameState
}

object Rules extends Rules {
  override def availableMoves(game: Game): List[Cell] = game.board.emptyCells.toList
  override def checkIfLegalMove(game: Game, cell: Cell): Either[Error, Unit] =
    Either.cond(game.board.markAt(cell).isEmpty, (), Error.IllegalMove(game, cell))
  override def state(game: Game): GameState =
    game.board.allLines.flatMap(winner) match {
      case Nil if game.availableMoves.nonEmpty => GameState.InProgress
      case Nil                                 => GameState.Finished(Draw)
      case mark :: _                           => GameState.Finished(Winner(mark))
    }

  private def winner(line: Line): Option[Mark] =
    if (line.cellsStates.forall(state => state.contains(Mark.O))) Some(Mark.O)
    else if (line.cellsStates.forall(state => state.contains(Mark.X))) Some(Mark.X)
    else None
}

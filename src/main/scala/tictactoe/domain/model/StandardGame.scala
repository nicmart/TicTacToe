package tictactoe.domain.model

import tictactoe.domain.model.Board.Cell
import tictactoe.domain.model.State.Result.{Draw, Winner}

sealed abstract case class StandardGame(board: Board, currentPlayer: Player) extends Game {
  override val state: State = board.allLines.flatMap(winner) match {
    case Nil if board.emptyCells.nonEmpty => State.InProgress
    case Nil                              => State.Finished(Draw)
    case mark :: _                        => State.Finished(Winner(Player(mark)))
  }

  override def availableMoves: List[Cell] =
    if (inProgress) board.emptyCells.toList else Nil

  override def makeMove(cell: Board.Cell): Either[Error, StandardGame] =
    for {
      _ <- Either.cond(inProgress, (), Error.GameHasAlreadyEnded)
      _ <- checkIfLegalMove(cell)
      newBoard <- board.withMark(currentPlayer.mark, cell)
    } yield new StandardGame(newBoard, currentPlayer.switch) {}

  private def checkIfLegalMove(cell: Cell): Either[Error, Unit] =
    Either.cond(board.markAt(cell).isEmpty, (), Error.CellOccupied(this, cell))

  private def winner(line: Line): Option[Mark] =
    if (line.cellsStates.forall(state => state.contains(Mark.O))) Some(Mark.O)
    else if (line.cellsStates.forall(state => state.contains(Mark.X))) Some(Mark.X)
    else None
}

object StandardGame {
  def newGame(size: Board.Size, currentPlayer: Player): StandardGame =
    new StandardGame(Board.emptyBoard(size), currentPlayer) {}
}

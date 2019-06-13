package tictactoe.domain.game.model

import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.State.InProgress
import tictactoe.domain.game.model.State.Result.{Draw, Winner}

sealed abstract case class StandardGame(board: Board, state: State, winningLineSize: Int)
    extends Game {

  override def availableMoves: List[Cell] =
    if (inProgress) board.emptyCells.toList else Nil

  override def makeMove(cell: Board.Cell): Either[Error, Game] =
    for {
      currentPlayer <- checkIfGameInProgress
      _ <- checkIfLegalMove(cell)
      newBoard <- board.withMark(currentPlayer.mark, cell)
      newState = nextGameState(newBoard, currentPlayer, cell, currentPlayer.mark)
    } yield new StandardGame(newBoard, newState, winningLineSize) {}

  private def checkIfGameInProgress: Either[Error, Player] =
    state match {
      case InProgress(currentPlayer) => Right(currentPlayer)
      case _                         => Left(Error.GameHasAlreadyEnded)
    }

  private def checkIfLegalMove(cell: Cell): Either[Error, Unit] =
    for {
      _ <- board.validateCell(cell)
      _ <- Either.cond(board.markAt(cell).isEmpty, (), Error.CellOccupied(this, cell))
    } yield ()

  private def nextGameState(
      newBoard: Board,
      currentPlayer: Player,
      lastMove: Cell,
      lastMoveMark: Mark
  ): State =
    hasWinner(newBoard, lastMove, lastMoveMark) match {
      case true                                  => State.Finished(Winner(currentPlayer))
      case false if newBoard.emptyCells.nonEmpty => State.InProgress(currentPlayer.switch)
      case false                                 => State.Finished(Draw)
    }

  private def connectedCells(newBoard: Board, from: Cell, mark: Mark)(direction: Cell): Int =
    board.markAt(from + direction) match {
      case Some(m) if m == mark =>
        1 + connectedCells(newBoard, from + direction, mark)(direction)
      case _ => 0
    }

  private def hasWinner(newBoard: Board, lastMove: Cell, lastMoveMark: Mark): Boolean = {
    val conn = connectedCells(newBoard, lastMove, lastMoveMark) _
    conn(Cell(1, 0)) + conn(Cell(-1, 0)) + 1 == winningLineSize ||
    conn(Cell(0, 1)) + conn(Cell(0, -1)) + 1 == winningLineSize ||
    conn(Cell(1, 1)) + conn(Cell(-1, -1)) + 1 == winningLineSize ||
    conn(Cell(1, -1)) + conn(Cell(-1, 1)) + 1 == winningLineSize
  }
}

object StandardGame {
  // Without loss of generality we can assume that player one is always X
  def newGame(size: Board.Size, winningLineSize: Int): StandardGame =
    new StandardGame(Board.emptyBoard(size), State.InProgress(Player.Player1), winningLineSize) {}
}

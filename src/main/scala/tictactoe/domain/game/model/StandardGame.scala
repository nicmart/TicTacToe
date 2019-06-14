package tictactoe.domain.game.model

import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.StandardGame.StateTransition
import tictactoe.domain.game.model.State.InProgress
import tictactoe.domain.game.model.State.Result.{Draw, Winner}

sealed abstract case class StandardGame(board: Board, state: State, winningLineSize: Int)
    extends Game {

  private val stateTransition = new StateTransition(board, winningLineSize)

  override def availableMoves: List[Cell] =
    if (inProgress) board.emptyCells.toList else Nil

  override def makeMove(cell: Board.Cell): Either[Error, Game] =
    for {
      currentPlayer <- checkIfGameInProgress
      _ <- checkIfLegalMove(cell)
      newBoard <- board.withMark(currentPlayer.mark, cell)
      newState = stateTransition.nextState(currentPlayer, cell)
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
}

object StandardGame {
  // Without loss of generality we can assume that player one is always X
  def newGame(size: Board.Size, winningLineSize: Int): StandardGame =
    new StandardGame(Board.emptyBoard(size), State.InProgress(Player.Player1), winningLineSize) {}

  /**
    * Just group some functions together to calculate the State Transition of a Standard Game
    */
  private class StateTransition(board: Board, winningLineSize: Int) {
    def nextState(currentPlayer: Player, lastCell: Cell): State =
      hasWinner(currentPlayer, lastCell) match {
        case true                               => State.Finished(Winner(currentPlayer))
        case false if board.emptyCells.nonEmpty => State.InProgress(currentPlayer.switch)
        case false                              => State.Finished(Draw)
      }

    private def connectedCells(from: Cell, mark: Mark, direction: Cell): Int =
      board.markAt(from + direction) match {
        case Some(m) if m == mark =>
          1 + connectedCells(from + direction, mark, direction)
        case _ => 0
      }

    private def hasWinner(currentPlayer: Player, lastMove: Cell): Boolean = {
      isOnWinningLine(currentPlayer.mark, lastMove, Cell(1, 0)) ||
      isOnWinningLine(currentPlayer.mark, lastMove, Cell(0, 1)) ||
      isOnWinningLine(currentPlayer.mark, lastMove, Cell(1, 1)) ||
      isOnWinningLine(currentPlayer.mark, lastMove, Cell(1, -1))
    }

    private def isOnWinningLine(mark: Mark, cell: Cell, direction: Cell): Boolean =
      connectedCells(cell, mark, direction) + connectedCells(cell, mark, -direction) + 1 == winningLineSize
  }
}

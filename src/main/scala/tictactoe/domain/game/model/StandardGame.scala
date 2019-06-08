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
      newState = nextGameState(newBoard, currentPlayer)
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

  // TODO can we generalise, finding the first line that contain all elements of the same kind?
  private def winner(newBoard: Board)(line: Line): Option[Mark] =
    if (line.cellStates(newBoard).forall(state => state.contains(Mark.O))) Some(Mark.O)
    else if (line.cellStates(newBoard).forall(state => state.contains(Mark.X))) Some(Mark.X)
    else None

  private def nextGameState(newBoard: Board, currentPlayer: Player): State =
    hasWinner(newBoard) match {
      case true                                  => State.Finished(Winner(currentPlayer))
      case false if newBoard.emptyCells.nonEmpty => State.InProgress(currentPlayer.switch)
      case false                                 => State.Finished(Draw)
    }

  private def hasWinner(newBoard: Board): Boolean =
    Line.linesOfBoard(newBoard.size.value, winningLineSize).flatMap(winner(newBoard)).nonEmpty
}

object StandardGame {
  // Without loss of generality we can assume that player one is always X
  def newGame(size: Board.Size, winningLineSize: Int): StandardGame =
    new StandardGame(Board.emptyBoard(size), State.InProgress(Player.Player1), winningLineSize) {}
}

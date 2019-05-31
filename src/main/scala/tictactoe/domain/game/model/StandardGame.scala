package tictactoe.domain.game.model

import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.State.InProgress
import tictactoe.domain.game.model.State.Result.{Draw, Winner}

sealed abstract case class StandardGame(board: Board, state: State) extends Game {

  override def availableMoves: List[Cell] =
    if (inProgress) board.emptyCells.toList else Nil

  override def makeMove(cell: Board.Cell): Either[Error, Game] =
    for {
      currentPlayer <- checkIfGameInProgress
      _ <- checkIfLegalMove(cell)
      newBoard <- board.withMark(currentPlayer.mark, cell)
      newState = nextGameState(newBoard, currentPlayer)
    } yield new StandardGame(newBoard, newState) {}

  private def checkIfGameInProgress: Either[Error, Player] =
    state match {
      case InProgress(currentPlayer) => Right(currentPlayer)
      case _                         => Left(Error.GameHasAlreadyEnded)
    }

  private def checkIfLegalMove(cell: Cell): Either[Error, Unit] =
    Either.cond(board.markAt(cell).isEmpty, (), Error.CellOccupied(this, cell))

  private def winner(line: Line): Option[Mark] =
    if (line.cellsStates.forall(state => state.contains(Mark.O))) Some(Mark.O)
    else if (line.cellsStates.forall(state => state.contains(Mark.X))) Some(Mark.X)
    else None

  private def nextGameState(board: Board, currentPlayer: Player): State =
    board.allLines.flatMap(winner) match {
      case Nil if board.emptyCells.nonEmpty => State.InProgress(currentPlayer.switch)
      case Nil                              => State.Finished(Draw)
      case _                                => State.Finished(Winner(currentPlayer))
    }
}

object StandardGame {
  // Without loss of generality we can assume that player one is always X
  def newGame(size: Board.Size): StandardGame =
    new StandardGame(Board.emptyBoard(size), State.InProgress(Player(Mark.X))) {}
}

package tictactoe.domain

import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model._

trait CommonOps extends EitherOps {
  implicit class BoardOps(board: Board) {
    def withCells(cellsStates: Seq[(Cell, Mark)]): Board =
      cellsStates.foldLeft(board) {
        case (board, (cell, mark)) => board.withMark(mark, cell).right.get
      }
  }

  implicit class GameOps(game: Game) {
    def withMoves(moves: Seq[Cell]): Game =
      moves.foldLeft(game) {
        case (currGame, cell) => currGame.makeMove(cell).getRight
      }

    def playMovesAndGetHistory(moves: Seq[Cell]): List[Game] = moves.foldLeft(List(game)) {
      (games, move) =>
        games :+ games.last.makeMove(move).getRight
    }

    def unsafeCurrentPlayer: Player = game.currentPlayer.getRight
  }

  implicit class Moves(moves: List[Cell]) {
    def intersperseWithInvalidAndErrors: List[Either[Error.UnexpectedError, Cell]] = moves.flatMap(
      legalMove =>
        List(
          Right(legalMove),
          Left(Error.UnexpectedError(s"Generated an error between moves $legalMove")),
          Right(legalMove),
          Right(Cell(-1, 1))
        )
    )
  }
}

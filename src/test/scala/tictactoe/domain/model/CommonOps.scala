package tictactoe.domain.model

import tictactoe.domain.model.Board.Cell
import tictactoe.domain.model.GameState.InProgress

trait CommonOps extends RightOps {
  implicit class BoardOps(board: Board) {
    def withCells(cellsStates: Seq[(Cell, Mark)]): Board =
      cellsStates.foldLeft(board) {
        case (board, (cell, mark)) => board.withMark(mark, cell).right.get
      }

    def line(line: Line): Line = line match {
      case Line.Horizontal(y, _)  => board.horizontalLines(y)
      case Line.Vertical(x, _)    => board.verticalLines(x)
      case Line.FirstDiagonal(_)  => board.firstDiagonalLine
      case Line.SecondDiagonal(_) => board.secondDiagonalLine
    }
  }

  implicit class GameOps(game: Game) {
    def withMoves(moves: Seq[Cell]): Game =
      moves.foldLeft(game) {
        case (currGame, cell) => currGame.makeMove(cell).getRight
      }
    def inProgress: Boolean = Rules.state(game) == InProgress
  }
}

package tictactoe.domain.model

import tictactoe.domain.model.Board.Cell

trait CommonOps extends EitherOps {
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

  implicit class GameOps(game: StandardGame) {
    def withMoves(moves: Seq[Cell]): StandardGame =
      moves.foldLeft(game) {
        case (currGame, cell) => currGame.makeMove(cell).getRight
      }
  }
}

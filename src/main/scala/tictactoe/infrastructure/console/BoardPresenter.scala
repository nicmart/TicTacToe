package tictactoe.infrastructure.console

import tictactoe.domain.game.model.{Board, Line, Mark}
import tictactoe.domain.game.model.Board.Cell

class BoardPresenter {
  def render(board: Board): BoardViewModel =
    BoardViewModel(board.horizontalLines.map(renderLine(board.size.value)))
      .getOrElse(BoardViewModel.empty(board.size.value, "?"))

  private def renderLine(boardSize: Int)(line: Line.Horizontal): List[String] =
    line.cells.zip(line.cellsStates).map((renderCell(boardSize) _).tupled)

  private def renderCell(boardSize: Int)(cell: Cell, state: Option[Mark]): String =
    state match {
      case None       => cellNumber(boardSize, cell).toString
      case Some(mark) => mark.toString
    }

  private def cellNumber(boardSize: Int, cell: Cell): Int = 1 + boardSize * cell.y + cell.x
}

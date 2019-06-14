package tictactoe.stringview

import tictactoe.domain.game.model.Board
import tictactoe.stringpresenter.BoardStringView
import tictactoe.underware.SizedString

class SimpleBoardStringView(cellView: CellView, cellSize: Int) extends BoardStringView {

  override def render(board: Board): String =
    board.allCellsByRow
      .map(cellView.renderCells)
      .map(cellsLine)
      .mkString(dividerLine(board.size.value))

  private def cellsLine(line: Seq[SizedString]): String =
    line.map(_.center(cellSize)).mkString("|")

  private def dividerLine(boardSize: Int): String =
    List.fill(boardSize)("-" * cellSize).mkString("\n", "|", "\n")
}

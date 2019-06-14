package tictactoe.stringview

import tictactoe.domain.game.model.Board
import tictactoe.stringpresenter.BoardStringView
import tictactoe.underware.SizedString

class BeautifulBoardStringView(cellView: CellView, cellSize: Int) extends BoardStringView {

  override def render(board: Board): String =
    board.allCellsByRow
      .map(cellView.renderCells)
      .map(cellsLine)
      .mkString(
        topLine(board.size.value),
        dividerLine(board.size.value),
        bottomLine(board.size.value)
      )

  private def topLine(boardSize: Int): String =
    List.fill(boardSize)("─" * cellSize).mkString("┌", "┬", "┐\n")

  private def cellsLine(line: Seq[SizedString]): String =
    line.map(_.center(cellSize)).mkString("│", "│", "│")

  private def dividerLine(boardSize: Int): String =
    List.fill(boardSize)("─" * cellSize).mkString("\n├", "┼", "┤\n")

  private def bottomLine(boardSize: Int): String =
    List.fill(boardSize)("─" * cellSize).mkString("\n└", "┴", "┘")
}

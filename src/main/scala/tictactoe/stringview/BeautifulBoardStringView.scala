package tictactoe.stringview

import tictactoe.console.BoardStringView
import tictactoe.stringpresenter.BoardStringViewModel
import tictactoe.underware.SizedString

class BeautifulBoardStringView(cellSize: Int) extends BoardStringView {
  override def render(board: BoardStringViewModel): String =
    board.rows
      .map(cellsLine)
      .mkString(
        topLine(board.size),
        dividerLine(board.size),
        bottomLine(board.size)
      )

  private def topLine(boardSize: Int): String =
    List.fill(boardSize)("─" * cellSize).mkString("┌", "┬", "┐\n")

  private def cellsLine(line: List[SizedString]): String =
    line.map(_.center(cellSize)).mkString("│", "│", "│")

  private def dividerLine(boardSize: Int): String =
    List.fill(boardSize)("─" * cellSize).mkString("\n├", "┼", "┤\n")

  private def bottomLine(boardSize: Int): String =
    List.fill(boardSize)("─" * cellSize).mkString("\n└", "┴", "┘")
}

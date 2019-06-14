package tictactoe.stringview

import tictactoe.console.BoardStringView
import tictactoe.stringpresenter.BoardStringViewModel
import tictactoe.underware.SizedString

class SimpleBoardStringView(cellSize: Int) extends BoardStringView {
  override def render(board: BoardStringViewModel): String =
    board.rows.map(cellsLine).mkString(dividerLine(board.size))

  private def cellsLine(line: List[SizedString]): String =
    line.map(_.center(cellSize)).mkString("|")

  private def dividerLine(boardSize: Int): String =
    List.fill(boardSize)("-" * cellSize).mkString("\n", "|", "\n")
}

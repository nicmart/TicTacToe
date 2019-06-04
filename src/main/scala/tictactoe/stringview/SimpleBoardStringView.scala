package tictactoe.stringview

import tictactoe.consolegameevents.BoardStringView
import tictactoe.stringpresenter.BoardStringViewModel
import tictactoe.underware.StringUnderware._

class SimpleBoardStringView(cellSize: Int) extends BoardStringView {
  override def render(board: BoardStringViewModel): String =
    board.rows.map(cellsLine).mkString(dividerLine(board.size))

  private def cellsLine(line: List[String]): String =
    line.map(_.center(cellSize)).mkString("|")

  private def dividerLine(boardSize: Int): String =
    List.fill(boardSize)("-" * cellSize).mkString("\n", "|", "\n")
}

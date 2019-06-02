package tictactoe.infrastructure.console

class SimpleBoardView extends BoardView {
  val cellSize = 3
  override def render(board: BoardViewModel): String =
    board.rows.map(renderLine).mkString(dividerHorizontalLine(board.size))

  private def renderLine(line: List[String]): String =
    line.map(addPadding).mkString("|")

  private def dividerHorizontalLine(boardSize: Int): String =
    "\n" + List.fill(boardSize)("---").mkString("|") + "\n"

  private def addPadding(string: String): String =
    string.length match {
      case 1 => s" $string "
      case 2 => s" $string"
      case 3 => string
      case _ => " " * cellSize
    }
}

package tictactoe.simplestringview

import java.text.BreakIterator

import tictactoe.consolegameevents.BoardStringView
import tictactoe.stringpresenter.BoardStringViewModel

class SimpleBoardStringView extends BoardStringView {
  val cellSize = 3
  override def render(board: BoardStringViewModel): String =
    board.rows.map(renderLine).mkString(dividerHorizontalLine(board.size))

  private def renderLine(line: List[String]): String =
    line.map(addPadding).mkString("|")

  private def dividerHorizontalLine(boardSize: Int): String =
    "\n" + List.fill(boardSize)("---").mkString("|") + "\n"

  private def addPadding(string: String): String =
    unicodeStringLength(string) match {
      case 1 => s" $string "
      case 2 => s" $string"
      case 3 => string
      case _ => " " * cellSize
    }

  // see https://stackoverflow.com/questions/6828076/how-to-correctly-compute-the-length-of-a-string-in-java
  private def unicodeStringLength(
      text: String,
      locale: java.util.Locale = java.util.Locale.ENGLISH
  ) = {
    val charIterator = java.text.BreakIterator.getCharacterInstance(locale)
    charIterator.setText(text)

    var result = 0
    while (charIterator.next() != BreakIterator.DONE) result += 1
    result
  }
}

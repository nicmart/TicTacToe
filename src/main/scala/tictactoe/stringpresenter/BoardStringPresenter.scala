package tictactoe.stringpresenter

import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Board, Mark}

class BoardStringPresenter(renderMark: Mark => String) {
  def render(board: Board): BoardStringViewModel =
    BoardStringViewModel(board.allCellsByRow.map(renderLine).toList)
      .getOrElse(BoardStringViewModel.empty(board.size.value, "?"))

  private def renderLine(line: Seq[(Cell, Option[Mark])]): List[String] =
    line.map((renderCell(line.size) _).tupled).toList

  private def renderCell(boardSize: Int)(cell: Cell, state: Option[Mark]): String =
    state match {
      case None       => cellNumber(boardSize, cell).toString
      case Some(mark) => renderMark(mark)
    }

  private def cellNumber(boardSize: Int, cell: Cell): Int = 1 + boardSize * cell.y + cell.x
}

object BoardStringPresenter {
  def defaultMarkRendering(mark: Mark): String = mark.fold("X", "O")
  def coolMarkRendering(mark: Mark): String = mark.fold("🍆", "🍅")
}

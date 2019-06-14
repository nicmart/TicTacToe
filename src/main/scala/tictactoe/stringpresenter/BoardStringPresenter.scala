package tictactoe.stringpresenter

import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Board, Mark}
import tictactoe.underware.SizedString

class BoardStringPresenter(renderMark: Mark => SizedString, renderEmpty: String => SizedString) {
  def render(board: Board): BoardStringViewModel =
    BoardStringViewModel(board.allCellsByRow.map(renderLine).toList)
      .getOrElse(BoardStringViewModel.empty(board.size.value, SizedString("?")))

  private def renderLine(line: Seq[(Cell, Option[Mark])]): List[SizedString] =
    line.map((renderCell(line.size) _).tupled).toList

  private def renderCell(boardSize: Int)(cell: Cell, state: Option[Mark]): SizedString =
    state match {
      case None       => renderEmpty(cellNumber(boardSize, cell).toString)
      case Some(mark) => renderMark(mark)
    }

  private def cellNumber(boardSize: Int, cell: Cell): Int = 1 + boardSize * cell.y + cell.x
}

object BoardStringPresenter {
  def defaultMarkRendering(mark: Mark): SizedString = mark.fold(SizedString("X"), SizedString("O"))
}

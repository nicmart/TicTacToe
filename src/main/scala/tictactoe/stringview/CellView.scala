package tictactoe.stringview

import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.Mark
import tictactoe.underware.SizedString

class CellView(renderMark: Mark => SizedString, renderEmpty: String => SizedString) {

  def renderCells(line: Seq[(Cell, Option[Mark])]): Seq[SizedString] =
    line.map { case (cell, state) => renderCell(line.size)(cell, state) }

  def renderCell(boardSize: Int)(cell: Cell, state: Option[Mark]): SizedString =
    state match {
      case None       => renderEmpty(cellNumber(boardSize, cell).toString)
      case Some(mark) => renderMark(mark)
    }

  private def cellNumber(boardSize: Int, cell: Cell): Int = 1 + boardSize * cell.y + cell.x
}

object CellView {
  def defaultMarkRendering(mark: Mark): SizedString = mark.fold(SizedString("X"), SizedString("O"))
}

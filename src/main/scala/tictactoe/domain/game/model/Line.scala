package tictactoe.domain.game.model

import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.Line.Direction
import tictactoe.domain.game.model.Line.Direction.{East, North, NorthEast, SouthEast}

final case class Line(from: Cell, direction: Direction, size: Int) {
  def cells: Seq[Cell] = (1 until size).scanLeft(from) { (previous, _) =>
    direction.moveCell(previous)
  }
  def cellStates(board: Board): Seq[Option[Mark]] = cells.map(board.markAt)
}

object Line {
  sealed abstract class Direction(val x: Int, val y: Int) {
    def moveCell(cell: Cell): Cell = Cell(cell.x + x, cell.y + y)
  }

  object Direction {
    final case object North extends Direction(0, 1)
    final case object NorthEast extends Direction(1, 1)
    final case object East extends Direction(1, 0)
    final case object SouthEast extends Direction(1, -1)
    final case object South extends Direction(0, -1)
    final case object SouthWest extends Direction(-1, -1)
    final case object West extends Direction(-1, 0)
    final case object NorthWest extends Direction(-1, 1)
  }

  def linesOfBoard(boardSize: Int, lineSize: Int): Seq[Line] = {
    val horizontalLines = for {
      y <- 0 until boardSize
      x <- 0 to boardSize - lineSize
    } yield Line(Cell(x, y), East, lineSize)

    val verticalLines = horizontalLines.map(
      line => line.copy(from = Cell(line.from.y, line.from.x), direction = North)
    )

    val westCells = (0 until boardSize).map(y => Cell(0, y))
    val southCells = (1 until boardSize).map(x => Cell(x, 0))
    val northCells = (1 until boardSize).map(x => Cell(x, boardSize - 1))

    val northEastDiagonals = for {
      cell <- westCells ++ southCells
      diagonalSize = boardSize - cell.x - cell.y
      offset <- 0 to diagonalSize - lineSize
      from = Cell(cell.x + offset, cell.y + offset)
    } yield Line(from, NorthEast, lineSize)

    val southEastDiagonals = for {
      cell <- westCells ++ northCells
      diagonalSize = cell.y + 1 - cell.x
      offset <- 0 to diagonalSize - lineSize
      from = Cell(cell.x + offset, cell.y - offset)
    } yield Line(from, SouthEast, lineSize)

    horizontalLines ++ verticalLines ++ northEastDiagonals ++ southEastDiagonals
  }

}

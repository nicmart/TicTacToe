package tictactoe.domain.model

import tictactoe.domain.model.Board.{Size, _}

sealed abstract case class Board(size: Size, private val cells: Vector[Vector[Option[Mark]]]) {

  def withMark(mark: Mark, cell: Cell): Either[Error, Board] =
    validateCell(cell).map { cell =>
      new Board(size, cells.updated(cell.y, cells(cell.y).updated(cell.x, Some(mark)))) {}
    }

  def markAt(cell: Cell): Option[Mark] = cells(cell.y)(cell.x)

  def horizontalLines: List[Line.Horizontal] =
    (0 until size.value).map(y => Line.Horizontal(y, cells(y))).toList

  def verticalLines: List[Line.Vertical] = {
    def verticalLine(x: Int): Line.Vertical =
      Line.Vertical(x, (0 until size.value).map(y => markAt(Cell(x, y))).toVector)

    (0 until size.value).map(verticalLine).toList
  }

  def firstDiagonalLine: Line.FirstDiagonal =
    Line.FirstDiagonal((0 until size.value).map(x => markAt(Cell(x, x))).toVector)

  def secondDiagonalLine: Line.SecondDiagonal =
    Line.SecondDiagonal((0 until size.value).map(x => markAt(Cell(size.value - 1 - x, x))).toVector)

  def allLines: List[Line] =
    firstDiagonalLine :: secondDiagonalLine :: horizontalLines ::: verticalLines

  private def validateCell(cell: Cell): Either[Error, Cell] =
    for {
      _ <- validateCoordinate(cell.x, Error.InvalidXCoordinate)
      _ <- validateCoordinate(cell.y, Error.InvalidYCoordinate)
    } yield cell

  private def validateCoordinate(coordinate: Int, error: Error): Either[Error, Int] =
    Either.cond(0 <= coordinate && coordinate < size.value, coordinate, error)
}

object Board {
  def emptyBoard(size: Size): Board = new Board(size, Vector.fill(size.value, size.value)(None)) {}

  case class Size(value: Int)
  case class Cell(x: Int, y: Int)
  case class CellWithState(cell: Cell, state: Option[Mark])
}

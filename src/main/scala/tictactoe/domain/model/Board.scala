package tictactoe.domain.model

import tictactoe.domain.model.Board.{Size, _}

sealed abstract case class Board(size: Size, private val cells: Vector[Vector[Option[Mark]]]) {

  def withMark(mark: Mark, cell: Cell): Either[Error, Board] =
    validateCell(cell).map { cell =>
      new Board(size, cells.updated(cell.x, cells(cell.x).updated(cell.y, Some(mark)))) {}
    }

  def markAt(cell: Cell): Option[Mark] = cells(cell.x)(cell.y)

  def horizontalLines: List[List[Option[Mark]]] =
    cells.toList.map(_.toList)

  def verticalLines: List[List[Option[Mark]]] = {
    def verticalLine(column: Int): List[Option[Mark]] =
      (0 until size.value).map(line => markAt(Cell(line, column))).toList

    (0 until size.value).map(verticalLine).toList
  }

  def diagonalLines: List[List[Option[Mark]]] = ???

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
}

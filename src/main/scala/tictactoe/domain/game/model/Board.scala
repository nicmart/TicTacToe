package tictactoe.domain.game.model

import tictactoe.domain.game.model.Board.{Size, _}

sealed abstract case class Board(size: Size, private val cells: Vector[Vector[Option[Mark]]]) {

  def withMark(mark: Mark, cell: Cell): Either[Error, Board] =
    validateCell(cell).map { cell =>
      new Board(size, cells.updated(cell.y, cells(cell.y).updated(cell.x, Some(mark)))) {}
    }

  def validateCell(cell: Cell): Either[Error, Cell] =
    for {
      _ <- validateCoordinate(cell.x, Error.InvalidXCoordinate)
      _ <- validateCoordinate(cell.y, Error.InvalidYCoordinate)
    } yield cell

  def markAt(cell: Cell): Option[Mark] =
    validateCell(cell).toOption.flatMap(_ => cells(cell.y)(cell.x))

  def emptyCells: Seq[Cell] =
    for {
      x <- 0 until size.value
      y <- 0 until size.value
      cell = Cell(x, y)
      state = markAt(Cell(x, y))
      cell <- state.fold(Option(cell))(_ => None)
    } yield cell

  def allCellsByRow: Seq[Seq[(Cell, Option[Mark])]] =
    for {
      y <- 0 until size.value
      line = (0 until size.value).map(x => Cell(x, y) -> cells(y)(x))
    } yield line

  private def validateCoordinate(coordinate: Int, error: Error): Either[Error, Int] =
    Either.cond(0 <= coordinate && coordinate < size.value, coordinate, error)
}

object Board {
  def emptyBoard(size: Size): Board = new Board(size, Vector.fill(size.value, size.value)(None)) {}

  case class Size(value: Int)
  case class Cell(x: Int, y: Int) {
    def +(other: Cell): Cell = Cell(x + other.x, y + other.y)
    def unary_-(): Cell = Cell(-x, -y)
  }
  case class CellWithState(cell: Cell, state: Option[Mark])
}

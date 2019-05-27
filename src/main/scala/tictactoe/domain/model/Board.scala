package tictactoe.domain.model

import tictactoe.domain.model.Board.{Size, _}

sealed abstract case class Board(size: Size) {

  def withMark(mark: Mark, cell: Cell): Either[Error, Board] = ???
  def markAt(cell: Cell): Option[Mark] = ???

  def horizontalLines: List[Option[Mark]] = ???
  def verticalLines: List[Option[Mark]] = ???
  def diagonalLines: List[Option[Mark]] = ???

  private def validateCell(cell: Cell): Either[Error, Cell] =
    for {
      _ <- validateCoordinate(cell.x, Error.InvalidXCoordinate)
      _ <- validateCoordinate(cell.y, Error.InvalidYCoordinate)
    } yield cell

  private def validateCoordinate(coordinate: Int, error: Error): Either[Error, Int] =
    Either.cond(0 <= coordinate && coordinate < size.value, coordinate, error)
}

object Board {
  def apply(size: Size): Board = new Board(size) {}
  case class Size(value: Int)
  case class Cell(x: Int, y: Int)
}

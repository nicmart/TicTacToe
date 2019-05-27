package tictactoe.domain.model

import tictactoe.domain.model.Board.Cell

sealed abstract class Line {
  def cellsStates: Vector[Option[Mark]]
  def size: Int = cellsStates.size

  def cells: List[Cell] = this match {
    case Line.Horizontal(y, _)  => (0 until size).map(x => Cell(x, y)).toList
    case Line.Vertical(x, _)    => (0 until size).map(y => Cell(x, y)).toList
    case Line.FirstDiagonal(_)  => (0 until size).map(y => Cell(y, y)).toList
    case Line.SecondDiagonal(_) => (0 until size).map(y => Cell(size - 1 - y, y)).toList
  }

  def cellsWithMarks: List[(Cell, Mark)] =
    cells
      .zip(cellsStates)
      .flatMap { case (cell, status) => status.map(mark => cell -> mark) }
}

object Line {
  final case class Horizontal(y: Int, cellsStates: Vector[Option[Mark]]) extends Line
  final case class Vertical(x: Int, cellsStates: Vector[Option[Mark]]) extends Line
  final case class FirstDiagonal(cellsStates: Vector[Option[Mark]]) extends Line
  final case class SecondDiagonal(cellsStates: Vector[Option[Mark]]) extends Line
}

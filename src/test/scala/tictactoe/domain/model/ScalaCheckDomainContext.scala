package tictactoe.domain.model

import org.scalacheck.{Arbitrary, Gen}
import Arbitrary.arbitrary

object ScalaCheckDomainContext {
  implicit val arbMark: Arbitrary[Mark] = Arbitrary(Gen.oneOf(Mark.X, Mark.O))
  implicit val arbBoardSize: Arbitrary[Board.Size] = Arbitrary(Gen.chooseNum(1, 100).map(Board.Size))

  val genEmptyBoard: Gen[Board] =
    arbBoardSize.arbitrary.map(Board.emptyBoard)

  val genNewGame: Gen[Game] =
    for {
      boardSize <- arbitrary[Board.Size]
      mark <- arbitrary[Mark]
    } yield Game.newGame(boardSize, mark)

  val genGame: Gen[Game] =
    for {
      newGame <- genNewGame
      size = newGame.board.size.value
      numOfMoves <- Gen.choose(0, size)
      moves <- Gen.listOfN(numOfMoves, genValidCell(size))
    } yield moves.foldLeft(newGame) { (game, cell) =>
      game.makeMove(cell).right.get
    }

  val genCellValue: Gen[Option[Mark]] =
    Gen.oneOf(Some(Mark.X), Some(Mark.O), None)

  def genLine(size: Int): Gen[Line] =
    Gen.frequency(
      size -> genHorizontalLine(size),
      size -> genVerticalLine(size),
      1 -> genFirstDiagonalLine(size),
      1 -> genSecondDiagonalLine(size)
    )

  def genHorizontalLine(size: Int): Gen[Line.Horizontal] =
    for {
      y <- genValidCellCoordinate(size)
      cellsStates <- genLineOfCellValues(size)
    } yield Line.Horizontal(y, cellsStates)

  def genVerticalLine(size: Int): Gen[Line.Vertical] =
    for {
      x <- genValidCellCoordinate(size)
      cellsStates <- genLineOfCellValues(size)
    } yield Line.Vertical(x, cellsStates)

  def genFirstDiagonalLine(size: Int): Gen[Line.FirstDiagonal] =
    genLineOfCellValues(size).map(Line.FirstDiagonal)

  def genSecondDiagonalLine(size: Int): Gen[Line.SecondDiagonal] =
    genLineOfCellValues(size).map(Line.SecondDiagonal)

  def genLineOfCellValues(size: Int): Gen[Vector[Option[Mark]]] =
    Gen.listOfN(size, genCellValue).map(_.toVector)

  def genValidCell(size: Int): Gen[Board.Cell] =
    for {
      x <- genValidCellCoordinate(size)
      y <- genValidCellCoordinate(size)
    } yield Board.Cell(x, y)

  def genValidCellCoordinate(size: Int): Gen[Int] =
    Gen.choose(0, size - 1)
}

package tictactoe.domain.model

import org.scalacheck.{Arbitrary, Gen}

object ScalaCheckDomainContext {
  implicit val arbMark: Arbitrary[Mark] = Arbitrary(Gen.oneOf(Mark.X, Mark.O))
  implicit val arbBoardSize: Arbitrary[Board.Size] = Arbitrary(Gen.chooseNum(1, 100).map(Board.Size))

  val genEmptyBoard: Gen[Board] =
    arbBoardSize.arbitrary.map(Board.emptyBoard)

  val genCellValue: Gen[Option[Mark]] =
    Gen.oneOf(Some(Mark.X), Some(Mark.O), None)

  def genLineOfCellValues(size: Int): Gen[Vector[Option[Mark]]] =
    Gen.listOfN(size, genCellValue).map(_.toVector)

  def genValidCell(board: Board): Gen[Board.Cell] =
    for {
      x <- genValidCellCoordinate(board)
      y <- genValidCellCoordinate(board)
    } yield Board.Cell(x, y)

  def genValidCellCoordinate(board: Board): Gen[Int] =
    Gen.choose(0, board.size.value - 1)
}

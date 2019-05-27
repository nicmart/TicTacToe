package tictactoe.domain.model

import org.scalacheck.{Arbitrary, Gen}
import Arbitrary.arbitrary
import tictactoe.domain.model.Board.Cell

object ScalaCheckDomainContext extends RightOps with CommonOps {
  implicit val arbMark: Arbitrary[Mark] = Arbitrary(Gen.oneOf(Mark.X, Mark.O))
  implicit val arbBoardSize: Arbitrary[Board.Size] = Arbitrary(Gen.chooseNum(1, 10).map(Board.Size))

  val genEmptyBoard: Gen[Board] =
    arbBoardSize.arbitrary.map(Board.emptyBoard)

  val genNewGame: Gen[Game] =
    for {
      boardSize <- arbitrary[Board.Size]
      mark <- arbitrary[Mark]
    } yield Game.newGame(boardSize, mark, Rules)

  val genInProgressGame: Gen[Game] =
    genNewGame.flatMap(genFutureGame(1))

  val genGameWithAvailableMove: Gen[(Game, Cell)] =
    for {
      game <- genInProgressGame
      move <- genAvailableMove(game)
    } yield (game, move)

  def genHistoryOfMovesWhereCurrentPlayerWins(size: Int): Gen[List[Cell]] =
    for {
      line <- genLine(size)
      moves <- intersperseWithOpponentMoves(size, line.cells)
    } yield moves

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

  private def genFutureGame(minAvailableCells: Int)(current: Game): Gen[Game] =
    if (current.availableMoves.size <= minAvailableCells) Gen.const(current)
    else
      Gen.frequency(
        1 -> Gen.const(current),
        current.availableMoves.size - minAvailableCells -> genNextGame(current).flatMap(
          genFutureGame(minAvailableCells)
        )
      )

  private def genAvailableMove(game: Game): Gen[Cell] =
    Gen.oneOf(game.board.emptyCells)

  private def genNextGame(current: Game): Gen[Game] =
    for {
      move <- genAvailableMove(current)
    } yield current.makeMove(move).getRight

  private def intersperseWithOpponentMoves(size: Int, moves: List[Cell]): Gen[List[Cell]] =
    for {
      board <- Gen.const(Board.emptyBoard(Board.Size(size)))
      movesWithMarks = moves.map(cell => cell -> Mark.X)
      boardWithMoves = board.withCells(movesWithMarks)
      opponentMoves <- Gen.pick(moves.size - 1, boardWithMoves.emptyCells)
    } yield moves.zip(opponentMoves).flatMap { case (m1, m2) => List(m1, m2) } ::: List(moves.last)
}

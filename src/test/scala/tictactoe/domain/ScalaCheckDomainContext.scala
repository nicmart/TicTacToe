package tictactoe.domain

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model._

object ScalaCheckDomainContext extends EitherOps with CommonOps {
  implicit val arbMark: Arbitrary[Mark] = Arbitrary(Gen.oneOf(Mark.X, Mark.O))
  implicit val arbPlayer: Arbitrary[Player] = Arbitrary(arbitrary[Mark].map(Player.apply))
  implicit val arbBoardSize: Arbitrary[Board.Size] = Arbitrary(Gen.chooseNum(1, 2).map(Board.Size))

  val genEmptyBoard: Gen[Board] =
    arbBoardSize.arbitrary.map(Board.emptyBoard)

  def genNewGameOfSize(size: Int): Gen[Game] =
    Gen.const(StandardGame.newGame(Board.Size(size)))

  val genNewGame: Gen[Game] =
    arbitrary[Board.Size].flatMap(size => genNewGameOfSize(size.value))

  val genInProgressGame: Gen[Game] =
    genNewGame.flatMap(genInProgressGameFrom)

  val genGameWithAvailableMove: Gen[(Game, Cell)] =
    for {
      game <- genInProgressGame
      move <- genAvailableMove(game)
    } yield (game, move)

  val genInProgressGameWithMoveThatWillNotEndTheGame: Gen[(Game, Cell)] =
    for {
      size <- Gen.choose(2, 10)
      emptyGame <- genNewGameOfSize(size)
      move <- genAvailableMove(emptyGame)
    } yield (emptyGame, move)

  def genSequenceOfLegalMoves(game: Game): Gen[List[Cell]] =
    if (game.inProgress) {
      for {
        move <- genAvailableMove(game)
        nextGame = game.makeMove(move).getRight
        nextMoves <- genSequenceOfLegalMoves(nextGame)
      } yield move :: nextMoves
    } else Gen.const(Nil)

  def genHistoryOfMovesWhereCurrentPlayerWins(size: Int): Gen[List[Cell]] =
    for {
      line <- genLine(size)
      moves <- intersperseWithOpponentMoves(size, line.cells)
    } yield moves

  val genWonGame: Gen[Game] = for {
    emptyGame <- genNewGame
    winningMoves <- genHistoryOfMovesWhereCurrentPlayerWins(emptyGame.size)
    finishedGame = emptyGame.withMoves(winningMoves)
  } yield finishedGame

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

  def genValidCells(size: Int): Gen[Set[Board.Cell]] =
    Gen
      .choose(0, size)
      .flatMap(numberOfCells => Gen.listOfN(numberOfCells, genValidCell(size)))
      .map(_.toSet)

  def genValidCellCoordinate(size: Int): Gen[Int] =
    Gen.choose(0, size - 1)

  private def genInProgressGameFrom(current: Game): Gen[Game] =
    if (current.availableMoves.size <= 1) Gen.const(current)
    else
      Gen.frequency(
        1 -> Gen.const(current),
        current.availableMoves.size - 1 ->
          genNextGame(current).flatMap(
            nextGame =>
              if (nextGame.inProgress) genInProgressGameFrom(nextGame) else Gen.const(current)
          )
      )

  def genAvailableMove(game: Game): Gen[Cell] =
    Gen.oneOf(game.availableMoves)

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

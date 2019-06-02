package tictactoe.domain.runner

import scalaz.zio.{DefaultRuntime, IO, Ref}
import tictactoe.domain.CommonTest
import tictactoe.domain.ScalaCheckDomainContext._
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Player}
import tictactoe.domain.game.{Game, model}

class GameRunnerTest extends CommonTest {
  "A Game Runner" - {
    "should pass to the presenter the current game" in {
      forAll(genNewGame) { game =>
        forAll(genSequenceOfLegalMoves(game)) { moves =>
          val program = for {
            runnerWithHistory <- runnerWithHistory(game, moves)
            (runner, history) = runnerWithHistory
            _ <- runner.runGame
            games <- history.get
          } yield games

          val expectedGameHistory = game.playMovesAndGetHistory(moves)
          val actualGameHistory = runtime.unsafeRun(program)

          actualGameHistory shouldBe expectedGameHistory
        }
      }
    }
  }

  private def runnerWithHistory(
      game: Game,
      moves: List[Cell]
  ): IO[model.Error, (GameRunner, Ref[List[Game]])] = {
    val groupedMoves = moves.grouped(2).toList
    val player1Moves = groupedMoves.flatMap(_.headOption)
    val player2Moves = groupedMoves.flatMap(_.drop(1).headOption)

    for {
      player1Moves <- playerSource(player1Moves.intersperseWithInvalidAndErrors)
      player2Moves <- playerSource(player2Moves.intersperseWithInvalidAndErrors)
      presenterAndHistory <- buildPresenter
      (presenter, historyRef) = presenterAndHistory
      runner = GameRunner(game, presenter, player1Moves, player2Moves)
    } yield (runner, historyRef)
  }

  private def playerSource(moves: List[Either[Error, Cell]]): IO[Error, MovesSource] =
    Ref.make(moves).map(FakeMovesSource)

  private val buildPresenter: IO[Error, (GameEvents, Ref[List[Game]])] =
    Ref.make[List[Game]](Nil).map { ref =>
      (FakeGameEvents(ref), ref)
    }

  lazy val runtime = new DefaultRuntime {}
}

case class FakeMovesSource(movesRef: Ref[List[Either[Error, Cell]]]) extends MovesSource {
  override def askMove(game: Game): IO[Error, Cell] =
    for {
      remainingMoves <- movesRef.get
      move <- remainingMoves match {
        case Nil          => IO.fail(Error.UnexpectedError("No more moves"))
        case head :: tail => movesRef.set(tail).flatMap(_ => IO.fromEither(head))
      }
    } yield move
}

case class FakeGameEvents(historyRef: Ref[List[Game]]) extends GameEvents {
  override def playerHasToChooseMove(player: Player): IO[Error, Unit] = IO.unit
  override def playerHasChosenIllegalMove(move: Cell, error: Error): IO[Error, Unit] = IO.unit
  override def playerHasChosenInvalidMove(error: Error): IO[Error, Unit] = IO.unit
  override def gameHasBeenUpdated(game: Game): IO[Error, Unit] =
    historyRef.update(games => games :+ game).unit
  override def gameHasEnded(game: Game): IO[Error, Unit] = IO.unit
  override def gameIsAboutToStart(game: Game): IO[Error, Unit] = IO.unit
}

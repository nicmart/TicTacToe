package tictactoe.domain.runner

import scalaz.zio._
import tictactoe.domain.CommonTest
import tictactoe.domain.ScalaCheckDomainContext._
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Player}

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
      initialGame: Game,
      moves: List[Cell]
  ): ZIO[Any, Error, (GameRunner[List[Game]], Ref[List[Game]])] = {
    val groupedMoves = moves.grouped(2).toList
    val player1Moves = groupedMoves.flatMap(_.headOption)
    val player2Moves = groupedMoves.flatMap(_.drop(1).headOption)

    for {
      player1Moves <- playerSource(player1Moves.intersperseWithInvalidAndErrors)
      player2Moves <- playerSource(player2Moves.intersperseWithInvalidAndErrors)
      sink <- buildSink
      gameRef <- Ref.make(initialGame)
      runner = GameRunner(gameRef, player1Moves, player2Moves, FakeGameEvents, sink)
    } yield (runner, sink.ref)
  }

  private def playerSource(moves: List[Either[Error, Cell]]): IO[Error, MovesSource] =
    Ref.make(moves).map(FakeMovesSource)

  private val buildSink: UIO[FakeGameStateSink[List[Game]]] =
    Ref.make[List[Game]](Nil).map(FakeGameStateSink.apply)

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

case class FakeGameStateSink[S](ref: Ref[S]) extends GameStateSink[S] {
  def update(f: S => S): UIO[Unit] = ref.update(f).unit
}

object FakeGameEvents extends GameEvents[List[Game]] {

  override def gameStarted(game: Game)(state: List[Game]): List[Game] = update(state, game)
  override def gameEnded(game: Game)(state: List[Game]): List[Game] = update(state, game)
  override def playerMoveRequested(
      game: Game,
      player: Player
  )(state: List[Game]): List[Game] = update(state, game)

  override def playerMoved(
      game: Game,
      player: Player,
      move: Cell
  )(state: List[Game]): List[Game] = update(state, game)

  override def playerChoseInvalidMove(
      game: Game,
      error: Error
  )(state: List[Game]): List[Game] = update(state, game)

  override def playerChoseIllegalMove(
      game: Game,
      move: Cell,
      error: Error
  )(state: List[Game]): List[Game] = update(state, game)

  private def update(history: List[Game], game: Game): List[Game] =
    if (history.lastOption.contains(game)) history
    else history :+ game
}

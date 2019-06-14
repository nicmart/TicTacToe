package tictactoe.domain.runner

import scalaz.zio._
import tictactoe.domain.CommonTest
import tictactoe.domain.ScalaCheckDomainContext._
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Player}
import tictactoe.domain.runner.GameRunner.{HasGameRef, HasStateRef}

class GameRunnerTest extends CommonTest {
  "A Game Runner" - {
    "should pass to the presenter the current game" in {
      forAll(genNewGame) { game =>
        forAll(genSequenceOfLegalMoves(game)) { moves =>
          val program = for {
            runnerWithHistory <- runnerWithHistory(moves)
            (runner, history) = runnerWithHistory
            _ <- runner.runGame.provideSomeM(initialState(game))
            games <- history.get
          } yield games

          val expectedGameHistory = game.playMovesAndGetHistory(moves)
          val actualGameHistory = runtime.unsafeRun(program)

          actualGameHistory shouldBe expectedGameHistory
        }
      }
    }
  }

  private def initialState(game: Game): UIO[GameRunner.State[List[Game]]] =
    for {
      gameRef <- Ref.make(game)
      stateRef <- Ref.make[List[Game]](Nil)
    } yield new HasStateRef[List[Game]] with HasGameRef {
      override def state: Ref[List[Game]] = stateRef
      override def game: Ref[Game] = gameRef
    }

  private def runnerWithHistory(
      moves: List[Cell]
  ): ZIO[Any, Error, (GameRunner[List[Game]], Ref[List[Game]])] = {
    val groupedMoves = moves.grouped(2).toList
    val player1Moves = groupedMoves.flatMap(_.headOption)
    val player2Moves = groupedMoves.flatMap(_.drop(1).headOption)

    for {
      player1Moves <- playerSource(player1Moves.intersperseWithInvalidAndErrors)
      player2Moves <- playerSource(player2Moves.intersperseWithInvalidAndErrors)
      sink <- buildSink
      runner = GameRunner(player1Moves, player2Moves, FakeGameEventTransition$, sink)
    } yield (runner, sink.ref)
  }

  private def playerSource(moves: List[Either[Error, Cell]]): IO[Error, MovesSource] =
    Ref.make(moves).map(FakeMovesSource)

  private val buildSink: ZIO[Any, Nothing, FakeGameStateSink[List[Game]]] =
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
  override def use(state: S): UIO[Unit] = ref.set(state)
}

object FakeGameEventTransition$ extends GameEvents[List[Game]] {

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

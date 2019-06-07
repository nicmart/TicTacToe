package tictactoe.domain.runner

import scalaz.zio._
import tictactoe.domain.CommonTest
import tictactoe.domain.ScalaCheckDomainContext._
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.Error
import tictactoe.domain.game.{Game, model}

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

  private def initialState(game: Game): UIO[GameRunner.State[Game]] = Ref.make(game).map { ref =>
    new ObserverState[Game] with GameState {
      override def state: Ref[Game] = ref
      override def gameState: Ref[Game] = ref
    }
  }

  private def runnerWithHistory(
      moves: List[Cell]
  ): IO[model.Error, (GameRunner[Game], Ref[List[Game]])] = {
    val groupedMoves = moves.grouped(2).toList
    val player1Moves = groupedMoves.flatMap(_.headOption)
    val player2Moves = groupedMoves.flatMap(_.drop(1).headOption)

    for {
      player1Moves <- playerSource(player1Moves.intersperseWithInvalidAndErrors)
      player2Moves <- playerSource(player2Moves.intersperseWithInvalidAndErrors)
      observerAndHistory <- buildObserver
      (observer, historyRef) = observerAndHistory
      runner = GameRunner(player1Moves, player2Moves, observer)
    } yield (runner, historyRef)
  }

  private def playerSource(moves: List[Either[Error, Cell]]): IO[Error, MovesSource] =
    Ref.make(moves).map(FakeMovesSource)

  private val buildObserver: IO[Error, (GameStateObserver[Game], Ref[List[Game]])] =
    Ref.make[List[Game]](Nil).map { ref =>
      (FakeGameObserver(ref), ref)
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

case class FakeGameObserver(historyRef: Ref[List[Game]]) extends GameStateObserver[Game] {
  override def receive(event: GameEvent): ZIO[ObserverState[Game], Nothing, Unit] =
    ZIO
      .environment[ObserverState[Game]]
      .flatMap(_.state.get)
      .flatMap(game => updateHistory(game, historyRef))

  private def updateHistory(newGame: Game, historyRef: Ref[List[Game]]) =
    for {
      history <- historyRef.get
      _ <- if (history.lastOption.contains(newGame)) ZIO.unit
      else historyRef.set(history :+ newGame).unit
    } yield ()
}

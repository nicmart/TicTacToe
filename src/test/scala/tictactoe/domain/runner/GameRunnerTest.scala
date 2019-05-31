package tictactoe.domain.runner

import scalaz.zio.{DefaultRuntime, IO, Ref}
import tictactoe.domain.CommonTest
import tictactoe.domain.ScalaCheckDomainContext._
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.{Game, model}
import tictactoe.domain.presenter.GamePresenter

class GameRunnerTest extends CommonTest {
  "A Game Runner" - {
    "should pass to the presenter the current game" in {
      forAll(genNewGame) { game =>
        forAll(genSequenceOfLegalMoves(game)) { moves =>
          val program = for {
            runnerWithHistory <- runnerWithHistory(game, moves)
            (runner, history) = runnerWithHistory
            _ <- runner.loop.either
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
      player1Moves <- playerMoves(player1Moves)
      player2Moves <- playerMoves(player2Moves)
      presenterAndHistory <- buildPresenter
      (presenter, historyRef) = presenterAndHistory
      runner = GameRunner(game, presenter, player1Moves, player2Moves)
    } yield (runner, historyRef)
  }

  private def playerMoves(moves: List[Cell]): IO[model.Error, MovesSource] =
    Ref.make(moves).map { ref =>
      MovesSource { _ =>
        for {
          remainingMoves <- ref.get
          move <- remainingMoves match {
            case Nil          => IO.fail(model.Error.UnexpectedError("No more moves"))
            case head :: tail => ref.set(tail).map(_ => head)
          }
        } yield move
      }
    }

  val buildPresenter: IO[model.Error, (GamePresenter, Ref[List[Game]])] =
    Ref.make[List[Game]](Nil).map { ref =>
      (GamePresenter { game =>
        ref.update(games => games :+ game).unit
      }, ref)
    }

  lazy val runtime = new DefaultRuntime {}
}

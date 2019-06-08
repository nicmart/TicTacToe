package tictactoe.app

import scalaz.zio.{App, Ref, UIO, ZIO}
import tictactoe.console.{ConsoleMovesSource, _}
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, StandardGame}
import tictactoe.domain.runner.GameRunner
import tictactoe.domain.runner.GameRunner.{HasGameRef, HasStateRef}
import tictactoe.rudegamestrings.RudeGameStrings
import tictactoe.stringpresenter.GameStringViewModel.Message
import tictactoe.stringpresenter.{BoardStringPresenter, GameStringPresenter, GameStringViewModel}
import tictactoe.stringview.{BeautifulBoardStringView, StandardGameStringView}

object ConsoleApp extends App {
  val runner: GameRunner[GameStringViewModel] =
    GameRunner(
      new ConsoleMovesSource,
      new ConsoleMovesSource,
      new GameStringPresenter(
        new BoardStringPresenter(_.fold("🖕", "🧠")),
        RudeGameStrings
      ),
      new ConsoleGameStateSink(new StandardGameStringView(new BeautifulBoardStringView(3)))
    )

  def run(args: List[String]): ZIO[ConsoleApp.Environment, Nothing, Int] =
    runner.runGame.provideSomeM(initialState).either.const(0)

  private def initialGamRefe: UIO[Ref[Game]] =
    Ref.make(StandardGame.newGame(Board.Size(6), 4))

  private def initialViewRef: UIO[Ref[GameStringViewModel]] =
    Ref.make(Message(""))

  private def initialState: UIO[GameRunner.State[GameStringViewModel]] =
    initialGamRefe.zipWith(initialViewRef) { (gameRef, viewRef) =>
      new HasStateRef[GameStringViewModel] with HasGameRef {
        override def game: Ref[Game] = gameRef
        override def state: Ref[GameStringViewModel] = viewRef
      }
    }
}

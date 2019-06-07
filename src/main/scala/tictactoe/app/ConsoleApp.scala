package tictactoe.app

import scalaz.zio.{App, Ref, UIO}
import tictactoe.console.{ConsoleMovesSource, _}
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, StandardGame}
import tictactoe.domain.runner.{GameRunner, GameState, ObserverState}
import tictactoe.rudegamestrings.RudeGameStrings
import tictactoe.stringpresenter.GameRunStateStringViewModel.Message
import tictactoe.stringpresenter.{
  BoardStringPresenter,
  GameRunStateStringPresenter,
  GameRunStateStringViewModel
}
import tictactoe.stringview.{BeautifulBoardStringView, StandardGameRunStateStringView}

object ConsoleApp extends App {
  val runner: GameRunner[GameRunStateStringViewModel] =
    GameRunner(
      new ConsoleMovesSource,
      new ConsoleMovesSource,
      new ConsoleGameStateObserver(
        new GameRunStateStringPresenter(
          new BoardStringPresenter(_.fold("🖕", "🧠")),
          RudeGameStrings
        ),
        new StandardGameRunStateStringView(new BeautifulBoardStringView(3))
      )
    )

  def run(args: List[String]) =
    runner.runGame.provideSomeM(initialState).either.const(0)

  private def initialGamRefe: UIO[Ref[Game]] =
    Ref.make(StandardGame.newGame(Board.Size(2)))

  private def initialViewRef: UIO[Ref[GameRunStateStringViewModel]] =
    Ref.make(Message(""))

  private def initialState: UIO[GameRunner.State[GameRunStateStringViewModel]] =
    initialGamRefe.zipWith(initialViewRef) { (gameRef, viewRef) =>
      new ObserverState[GameRunStateStringViewModel] with GameState {
        override def gameState: Ref[Game] = gameRef
        override def state: Ref[GameRunStateStringViewModel] = viewRef
      }
    }
}

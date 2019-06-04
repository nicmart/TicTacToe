package tictactoe.app

import scalaz.zio.{App, Ref, UIO}
import tictactoe.consolegameevents._
import tictactoe.consolemovessource.ConsoleMovesSource
import tictactoe.domain.game.model.{Board, StandardGame}
import tictactoe.domain.runner.{GameEvent, GameRunState, GameRunner, WithGameRunState}
import tictactoe.rudegamestrings.RudeGameStrings
import tictactoe.stringpresenter.{BoardStringPresenter, GameRunStateStringPresenter}
import tictactoe.stringview.{BeautifulBoardStringView, StandardGameRunStateStringView}

object ConsoleApp extends App {
  val runner: GameRunner =
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
    runner.runGame.provideSomeM(newGame).either.const(0)

  private def newGame: UIO[WithGameRunState] =
    Ref.make(GameRunState(StandardGame.newGame(Board.Size(2)), GameEvent.GameIsAboutToStart)).map {
      ref =>
        new WithGameRunState {
          override def gameState: Ref[GameRunState] = ref
        }
    }
}

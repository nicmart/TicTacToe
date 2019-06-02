package tictactoe.app

import scalaz.zio.App
import tictactoe.consolegameevents._
import tictactoe.consolemovessource.ConsoleMovesSource
import tictactoe.domain.game.model.{Board, StandardGame}
import tictactoe.domain.runner.GameRunner
import tictactoe.rudegamestrings.RudeConsoleGameStrings
import tictactoe.simplestringview.SimpleBoardStringView
import tictactoe.stringpresenter.BoardStringPresenter

object ConsoleApp extends App {
  val runner: GameRunner =
    GameRunner(
      StandardGame.newGame(Board.Size(3)),
      new ConsoleGameEvents(
        new BoardStringPresenter(_.fold("🖕", "🧠")),
        new SimpleBoardStringView,
        RudeConsoleGameStrings
      ),
      new ConsoleMovesSource,
      new ConsoleMovesSource
    )

  def run(args: List[String]) =
    runner.runGame.either.const(0)
}
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
import tictactoe.underware.AnsiCodes._

object ConsoleApp extends App {
  val runner: GameRunner[GameStringViewModel] =
    GameRunner(
      new ConsoleMovesSource,
      new ConsoleMovesSource,
      new GameStringPresenter(
        new BoardStringPresenter(
          _.fold(coloriseString(brightRed)("X"), coloriseString(brightBlue)("O")),
          coloriseString(color(238))
        ),
        RudeGameStrings
      ),
      new ConsoleGameStateSink(new StandardGameStringView(new BeautifulBoardStringView(3)))
    )

  def run(args: List[String]): ZIO[ConsoleApp.Environment, Nothing, Int] =
    runner.runGame.provideSomeM(initialState).either.const(0)

  private def initialGamRef: UIO[Ref[Game]] =
    Ref.make(StandardGame.newGame(Board.Size(20), 3))

  private def initialViewRef: UIO[Ref[GameStringViewModel]] =
    Ref.make(Message(""))

  private def initialState: UIO[GameRunner.State[GameStringViewModel]] =
    initialGamRef.zipWith(initialViewRef) { (gameRef, viewRef) =>
      new HasStateRef[GameStringViewModel] with HasGameRef {
        override def game: Ref[Game] = gameRef
        override def state: Ref[GameStringViewModel] = viewRef
      }
    }
}

package tictactoe.app

import scalaz.zio.{App, Ref, UIO, ZIO}
import tictactoe.console._
import tictactoe.domain.runner.GameRunner.HasStateRef
import tictactoe.domain.setup.GameManager
import tictactoe.domain.setup.standard.StandardGameSetupRunner
import tictactoe.rudegamestrings.RudeGameStrings
import tictactoe.stringpresenter.GameStringViewModel.NormalScreen
import tictactoe.stringpresenter.{GameStringViewModel, StringSetupEvents}
import tictactoe.stringview.{BeautifulBoardStringView, StandardGameStringView}
import tictactoe.underware.AnsiCodes._

object ConsoleApp extends App {
  val manager = new GameManager[GameStringViewModel](
    new StandardGameSetupRunner(
      new ConsoleGameSetupSettingSource,
      new StringSetupEvents(RudeGameStrings),
      new ConsoleGameStateSink(
        new StandardGameStringView(new BeautifulBoardStringView(3))
      ),
      maxGameSize = 25
    ),
    new ConsoleGameBuilder(
      ConsoleGameConfig(
        coloriseString(brightRed)("X"),
        coloriseString(brightBlue)("O"),
        coloriseString(color(238)),
        RudeGameStrings
      )
    )
  )

  def run(args: List[String]): ZIO[ConsoleApp.Environment, Nothing, Int] =
    manager.run.provideSomeM(initialViewRef).const(0)

  def initialViewRef: UIO[HasStateRef[GameStringViewModel]] =
    Ref
      .make[GameStringViewModel](
        NormalScreen(
          s"Welcome to TicTacToe!",
          Vector.empty
        )
      )
      .map { ref =>
        new HasStateRef[GameStringViewModel] {
          override def state: Ref[GameStringViewModel] = ref
        }
      }
}

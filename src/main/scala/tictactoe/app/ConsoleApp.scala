package tictactoe.app

import scalaz.zio.{App, IO, UIO, ZIO}
import tictactoe.console._
import tictactoe.domain.setup.GameManager
import tictactoe.domain.setup.standard.StandardGameSetupRunner
import tictactoe.rudegamestrings.RudeGameStrings
import tictactoe.stringpresenter.GameStringViewModel.NormalScreen
import tictactoe.stringpresenter.{GameStringViewModel, StringSetupEvents}
import tictactoe.stringview.{BeautifulBoardStringView, StandardGameStringView}
import tictactoe.typeclasses.instances.ZioInstances._
import tictactoe.underware.AnsiCodes._

object ConsoleApp extends App {
  def run(args: List[String]): ZIO[ConsoleApp.Environment, Nothing, Int] =
    manager.flatMap(_.run).const(0)

  private def manager: UIO[GameManager[IO, GameStringViewModel]] = sink.map { sink =>
    new GameManager[IO, GameStringViewModel](
      new StandardGameSetupRunner[IO, GameStringViewModel](
        new ConsoleGameSetupSettingSource,
        new StringSetupEvents(RudeGameStrings),
        sink,
        maxGameSize = 25
      ),
      new ConsoleGameBuilder(
        ConsoleGameConfig(
          coloriseString(brightRed)("X"),
          coloriseString(brightBlue)("O"),
          coloriseString(color(238)),
          RudeGameStrings
        ),
        sink,
        MakeZioRef
      )
    )
  }

  private def sink: UIO[ConsoleGameStateSink[IO]] =
    MakeZioRef.make(initialScreen).map { screen =>
      new ConsoleGameStateSink(
        new StandardGameStringView(
          new BeautifulBoardStringView(3)
        ),
        screen
      )
    }

  private def initialScreen: GameStringViewModel =
    NormalScreen(
      s"Welcome to TicTacToe!",
      Vector.empty
    )
}

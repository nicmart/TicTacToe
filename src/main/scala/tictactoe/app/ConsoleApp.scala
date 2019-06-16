package tictactoe.app

import tictactoe.console._
import tictactoe.domain.setup.GameManager
import tictactoe.domain.setup.standard.StandardGameSetupRunner
import tictactoe.rudegamestrings.RudeGameStrings
import tictactoe.stringpresenter.GameStringViewModel.NormalScreen
import tictactoe.stringpresenter.{GameStringViewModel, StringSetupEvents}
import tictactoe.stringview.{BeautifulBoardStringView, StandardGameStringView}
import tictactoe.typeclasses.MonadE._
import tictactoe.typeclasses.{MakeRef, MonadE}
import tictactoe.underware.AnsiCodes._

class ConsoleApp[F[+_, +_]: MonadE](console: Console[F], makeRef: MakeRef[F]) {

  def manager: F[Nothing, GameManager[F, GameStringViewModel]] = sink.map { sink =>
    new GameManager[F, GameStringViewModel](
      new StandardGameSetupRunner[F, GameStringViewModel](
        new ConsoleGameSetupSettingSource(console),
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
        console,
        sink,
        makeRef
      )
    )
  }

  private def sink: F[Nothing, ConsoleGameStateSink[F]] =
    makeRef.make(initialScreen).map { screen =>
      new ConsoleGameStateSink(
        new StandardGameStringView(
          new BeautifulBoardStringView(3)
        ),
        screen,
        console
      )
    }

  private def initialScreen: GameStringViewModel =
    NormalScreen(
      s"Welcome to TicTacToe!",
      Vector.empty
    )
}

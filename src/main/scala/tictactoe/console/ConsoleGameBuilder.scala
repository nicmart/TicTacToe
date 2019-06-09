package tictactoe.console

import scalaz.zio.{Ref, UIO}
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, StandardGame}
import tictactoe.domain.runner.GameRunner
import tictactoe.domain.runner.GameRunner.{HasGameRef, HasStateRef}
import tictactoe.domain.setup.{GameBuilder, GameSetup}
import tictactoe.rudegamestrings.RudeGameStrings
import tictactoe.stringpresenter.GameStringViewModel.NormalScreen
import tictactoe.stringpresenter.{BoardStringPresenter, GameStringPresenter, GameStringViewModel}
import tictactoe.stringview.{BeautifulBoardStringView, StandardGameStringView}
import tictactoe.underware.AnsiCodes.{brightBlue, brightRed, color, coloriseString}

// Shoudln't be here, just to do some initial plumbing
class ConsoleGameBuilder extends GameBuilder[GameStringViewModel] {

  override def runner(setup: GameSetup): GameRunner[GameStringViewModel] =
    new GameRunner[GameStringViewModel](
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

  override def initialState(setup: GameSetup): UIO[GameRunner.State[GameStringViewModel]] =
    initialGamRef(setup).zipWith(initialViewRef(setup)) { (gameRef, viewRef) =>
      new HasStateRef[GameStringViewModel] with HasGameRef {
        override def game: Ref[Game] = gameRef
        override def state: Ref[GameStringViewModel] = viewRef
      }
    }

  private def initialGamRef(setup: GameSetup): UIO[Ref[Game]] =
    Ref.make(StandardGame.newGame(Board.Size(setup.gameSize), setup.winningLineLength))

  private def initialViewRef(setup: GameSetup): UIO[Ref[GameStringViewModel]] =
    Ref.make(
      NormalScreen(
        s"Welcome to TicTacToe! This is a ${setup.gameSize}x${setup.gameSize} game, ${setup.winningLineLength} in a row to win",
        Vector.empty
      )
    )
}

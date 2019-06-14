package tictactoe.console

import scalaz.zio.{Ref, UIO, ZIO}
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, StandardGame}
import tictactoe.domain.runner.GameRunner
import tictactoe.domain.runner.GameRunner.{HasGameRef, HasStateRef}
import tictactoe.domain.setup.{GameBuilder, GameSetup}
import tictactoe.rudegamestrings.RudeGameStrings
import tictactoe.stringpresenter.{StringGameEvents, GameStringViewModel}
import tictactoe.stringview.{BeautifulBoardStringView, CellView, StandardGameStringView}
import tictactoe.underware.AnsiCodes.{brightBlue, brightRed, color, coloriseString}

class ConsoleGameBuilder extends GameBuilder[GameStringViewModel] {

  override def runner(setup: GameSetup): GameRunner[GameStringViewModel] =
    new GameRunner[GameStringViewModel](
      new ConsoleMovesSource,
      new ConsoleMovesSource,
      new StringGameEvents(
        new BeautifulBoardStringView(
          new CellView(
            _.fold(coloriseString(brightRed)("X"), coloriseString(brightBlue)("O")),
            coloriseString(color(238))
          ),
          3
        ),
        RudeGameStrings
      ),
      new ConsoleGameStateSink(StandardGameStringView)
    )

  override def initialState(
      setup: GameSetup
  ): ZIO[HasStateRef[GameStringViewModel], Nothing, GameRunner.State[GameStringViewModel]] =
    for {
      stateRef <- ZIO.access[HasStateRef[GameStringViewModel]](_.state)
      gameRef <- initialGamRef(setup)
    } yield new HasStateRef[GameStringViewModel] with HasGameRef {
      override def game: Ref[Game] = gameRef
      override def state: Ref[GameStringViewModel] = stateRef
    }

  private def initialGamRef(setup: GameSetup): UIO[Ref[Game]] =
    Ref.make(StandardGame.newGame(Board.Size(setup.gameSize), setup.winningLineLength))
}

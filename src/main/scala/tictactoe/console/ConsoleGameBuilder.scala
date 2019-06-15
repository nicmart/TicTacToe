package tictactoe.console

import scalaz.zio.{Ref, UIO}
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, StandardGame}
import tictactoe.domain.runner.{GameRunner, GameStateSink}
import tictactoe.domain.setup.{GameBuilder, GameSetup}
import tictactoe.stringpresenter.{BoardStringPresenter, GameStringViewModel, StringGameEvents}

class ConsoleGameBuilder(config: ConsoleGameConfig, stateSink: GameStateSink[GameStringViewModel])
    extends GameBuilder[GameStringViewModel] {

  override def runner(setup: GameSetup): UIO[GameRunner[GameStringViewModel]] =
    initialGamRef(setup).map { gameRef =>
      new GameRunner[GameStringViewModel](
        gameRef,
        new ConsoleMovesSource,
        new ConsoleMovesSource,
        new StringGameEvents(
          new BoardStringPresenter(
            _.fold(config.player1Mark, config.player2Mark),
            config.emptyCell
          ),
          config.strings
        ),
        stateSink
      )
    }

  private def initialGamRef(setup: GameSetup): UIO[Ref[Game]] =
    Ref.make(StandardGame.newGame(Board.Size(setup.gameSize), setup.winningLineLength))
}

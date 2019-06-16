package tictactoe.console

import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, StandardGame}
import tictactoe.domain.runner.{GameRunner, GameStateSink}
import tictactoe.domain.setup.{GameBuilder, GameSetup}
import tictactoe.stringpresenter.{BoardStringPresenter, GameStringViewModel, StringGameEvents}
import tictactoe.typeclasses.MonadE._
import tictactoe.typeclasses.{MakeRef, MonadE, URef}

class ConsoleGameBuilder[F[+_, +_]: MonadE](
    config: ConsoleGameConfig,
    console: Console[F],
    stateSink: GameStateSink[F, GameStringViewModel],
    makeRef: MakeRef[F]
) extends GameBuilder[F, GameStringViewModel] {

  override def runner(setup: GameSetup): F[Nothing, GameRunner[F, GameStringViewModel]] =
    initialGamRef(setup).map { gameRef =>
      new GameRunner[F, GameStringViewModel](
        gameRef,
        new ConsoleMovesSource(console),
        new ConsoleMovesSource(console),
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

  private def initialGamRef(setup: GameSetup): F[Nothing, URef[F, Game]] =
    makeRef.make(StandardGame.newGame(Board.Size(setup.gameSize), setup.winningLineLength))
}

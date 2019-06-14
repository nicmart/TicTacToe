package tictactoe.console

import scalaz.zio.{Ref, UIO, ZIO}
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, StandardGame}
import tictactoe.domain.runner.GameRunner
import tictactoe.domain.runner.GameRunner.{HasGameRef, HasStateRef}
import tictactoe.domain.setup.{GameBuilder, GameSetup}
import tictactoe.stringpresenter.{BoardStringPresenter, StringGameEvents, GameStringViewModel}
import tictactoe.stringview.{BeautifulBoardStringView, StandardGameStringView}

class ConsoleGameBuilder(config: ConsoleGameConfig) extends GameBuilder[GameStringViewModel] {

  override def runner(setup: GameSetup): UIO[GameRunner[GameStringViewModel]] =
    UIO.succeed(
      new GameRunner[GameStringViewModel](
        new ConsoleMovesSource,
        new ConsoleMovesSource,
        new StringGameEvents(
          new BoardStringPresenter(
            _.fold(config.player1Mark, config.player2Mark),
            config.emptyCell
          ),
          config.strings
        ),
        new ConsoleGameStateSink(
          new StandardGameStringView(
            new BeautifulBoardStringView(3)
          )
        )
      )
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

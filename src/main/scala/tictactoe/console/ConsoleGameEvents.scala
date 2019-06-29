package tictactoe.console

import tictactoe.domain.runner.{GameEvents, GameStateSink}
import tictactoe.stringpresenter.GameStringViewModel

object ConsoleGameEvents {
  def apply[F[+_, +_], T](
      events: GameEvents[GameStringViewModel => GameStringViewModel],
      sink: GameStateSink[F, GameStringViewModel]
  ): GameEvents[F[Nothing, Unit]] =
    new GameEvents.Mapped(events, sink.update)
}

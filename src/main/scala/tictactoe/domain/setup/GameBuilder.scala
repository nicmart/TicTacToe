package tictactoe.domain.setup

import scalaz.zio.ZIO
import tictactoe.domain.runner.GameRunner
import tictactoe.domain.runner.GameRunner.HasStateRef

trait GameBuilder[S] {
  def runner(setup: GameSetup): GameRunner[S]
  def initialState(setup: GameSetup): ZIO[HasStateRef[S], Nothing, GameRunner.State[S]]
}

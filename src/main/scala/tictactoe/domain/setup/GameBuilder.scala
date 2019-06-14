package tictactoe.domain.setup

import scalaz.zio.{UIO, ZIO}
import tictactoe.domain.runner.GameRunner
import tictactoe.domain.runner.GameRunner.HasStateRef

trait GameBuilder[S] {
  def runner(setup: GameSetup): UIO[GameRunner[S]]
  def initialState(setup: GameSetup): ZIO[HasStateRef[S], Nothing, GameRunner.State[S]]
}

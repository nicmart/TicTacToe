package tictactoe.domain.setup

import scalaz.zio.UIO
import tictactoe.domain.runner.GameRunner

trait GameBuilder[S] {
  def runner(setup: GameSetup): GameRunner[S]
  def initialState(setup: GameSetup): UIO[GameRunner.State[S]]
}

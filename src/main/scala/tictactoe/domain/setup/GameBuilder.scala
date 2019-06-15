package tictactoe.domain.setup

import scalaz.zio.UIO
import tictactoe.domain.runner.GameRunner

trait GameBuilder[S] {
  def runner(setup: GameSetup): UIO[GameRunner[S]]
}

package tictactoe.domain.setup

import scalaz.zio.ZIO
import tictactoe.domain.runner.GameRunner.HasStateRef

trait GameSetupRunner[S] {
  def runSetup: ZIO[HasStateRef[S], Nothing, GameSetup]
}

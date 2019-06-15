package tictactoe.domain.setup

import scalaz.zio.UIO

trait GameSetupRunner[S] {
  def runSetup: UIO[GameSetup]
}

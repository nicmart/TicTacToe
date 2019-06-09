package tictactoe.domain.setup

import scalaz.zio.IO

trait SetupSource {
  def askSetup: IO[SetupSource.Error, GameSetup]
  def askToContinue: IO[SetupSource.Error, Boolean]
}

object SetupSource {
  sealed trait Error
}

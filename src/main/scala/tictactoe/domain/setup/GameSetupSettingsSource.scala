package tictactoe.domain.setup

import scalaz.zio.IO

trait GameSetupSettingsSource {
  def askGameSize: IO[GameSetupSettingsSource.Error, Int]
  def askWinningGameLength(gameSize: Int): IO[GameSetupSettingsSource.Error, Int]
}

object GameSetupSettingsSource {
  sealed trait Error
  object Error {
    case object InvalidSetting extends Error
  }
}

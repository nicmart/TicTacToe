package tictactoe.domain.setup

trait GameSetupSettingsSource[F[_, _]] {
  def askGameSize: F[GameSetupSettingsSource.Error, Int]
  def askWinningGameLength: F[GameSetupSettingsSource.Error, Int]
}

object GameSetupSettingsSource {
  sealed trait Error
  object Error {
    case object InvalidSetting extends Error
  }
}

package tictactoe.console

import tictactoe.domain.setup.GameSetupSettingsSource
import tictactoe.domain.setup.GameSetupSettingsSource.Error.InvalidSetting
import tictactoe.typeclasses.MonadE
import tictactoe.typeclasses.MonadE._
import scala.util.Try

class ConsoleGameSetupSettingSource[F[+_, +_]: MonadE](console: Console[F])
    extends GameSetupSettingsSource[F] {

  override def askGameSize: F[GameSetupSettingsSource.Error, Int] =
    askInt

  override def askWinningGameLength: F[GameSetupSettingsSource.Error, Int] =
    askInt

  private def askInt: F[GameSetupSettingsSource.Error, Int] =
    for {
      input <- console.read.mapError(_ => InvalidSetting)
      length <- MonadE[F].fromEither(Try(input.toInt).toEither).mapError(_ => InvalidSetting)
    } yield length
}

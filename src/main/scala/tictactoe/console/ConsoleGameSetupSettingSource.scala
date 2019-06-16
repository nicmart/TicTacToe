package tictactoe.console

import tictactoe.domain.setup.GameSetupSettingsSource
import tictactoe.domain.setup.GameSetupSettingsSource.Error.InvalidSetting
import tictactoe.typeclasses.{Delay, MonadE}
import MonadE._

import scala.io.StdIn
import scala.util.Try

class ConsoleGameSetupSettingSource[F[+_, +_]: MonadE: Delay] extends GameSetupSettingsSource[F] {

  override def askGameSize: F[GameSetupSettingsSource.Error, Int] =
    askInt

  override def askWinningGameLength: F[GameSetupSettingsSource.Error, Int] =
    askInt

  private def askInt: F[GameSetupSettingsSource.Error, Int] =
    for {
      input <- readLn
      length <- MonadE[F].fromEither(Try(input.toInt).toEither).mapError(_ => InvalidSetting)
    } yield length

  private def readLn: F[Nothing, String] =
    Delay[F].delayTotal(StdIn.readLine().trim)
}

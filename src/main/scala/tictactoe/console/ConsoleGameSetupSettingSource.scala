package tictactoe.console

import scalaz.zio.{IO, UIO, ZIO}
import tictactoe.domain.setup.GameSetupSettingsSource
import tictactoe.domain.setup.GameSetupSettingsSource.Error.InvalidSetting

import scala.io.StdIn
import scala.util.Try

class ConsoleGameSetupSettingSource extends GameSetupSettingsSource {

  override def askGameSize: IO[GameSetupSettingsSource.Error, Int] =
    for {
      input <- readLn
      size <- IO.fromEither(Try(input.toInt).toEither).mapError(_ => InvalidSetting)
      validSize <- if (size >= 1 && size <= 25) IO.succeed(size) else IO.fail(InvalidSetting)
    } yield validSize

  override def askWinningGameLength(gameSize: Int): IO[GameSetupSettingsSource.Error, Int] =
    for {
      input <- readLn
      length <- IO.fromEither(Try(input.toInt).toEither).mapError(_ => InvalidSetting)
      validSize <- if (length >= 1 && length <= gameSize) IO.succeed(length)
      else IO.fail(InvalidSetting)
    } yield validSize

  private def readLn: UIO[String] =
    ZIO.effectTotal(StdIn.readLine().trim)
}

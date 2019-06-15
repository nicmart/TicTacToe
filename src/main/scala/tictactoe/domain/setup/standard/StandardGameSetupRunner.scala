package tictactoe.domain.setup.standard

import scalaz.zio.{IO, UIO}
import tictactoe.domain.runner.GameStateSink
import tictactoe.domain.setup._

class StandardGameSetupRunner[S](
    source: GameSetupSettingsSource,
    setupEvents: SetupEvents[S],
    sink: GameStateSink[S],
    maxGameSize: Int
) extends GameSetupRunner[S] {

  override def runSetup: UIO[GameSetup] =
    for {
      gameSize <- askGameSizeUntilValid
      winningLength <- askWinningLengthUntilValid(gameSize)
      setup = GameSetup(gameSize, winningLength)
      _ <- sink.update(setupEvents.setupCompleted(setup))
    } yield setup

  private def askGameSizeUntilValid: UIO[Int] =
    for {
      _ <- sink.update(setupEvents.gameSizeRequested(maxGameSize))
      size <- source.askGameSize.flatMap(verifyGameSize).catchAll(_ => catchInvalidGameSize)
    } yield size

  private def verifyGameSize(size: Int): IO[GameSetupSettingsSource.Error, Int] =
    IO.fromEither(
      Either
        .cond(size >= 1 && size <= maxGameSize, size, GameSetupSettingsSource.Error.InvalidSetting)
    )

  private def catchInvalidGameSize: UIO[Int] =
    sink.update(setupEvents.userChoseInvalidOption) *> askGameSizeUntilValid

  private def askWinningLengthUntilValid(gameSize: Int): UIO[Int] =
    for {
      _ <- sink.update(setupEvents.winningLengthRequested(gameSize))
      size <- source
        .askWinningGameLength(gameSize)
        .flatMap(verifyWinningLength(gameSize))
        .catchAll(_ => catchInvalidWinningLength(gameSize))
    } yield size

  private def catchInvalidWinningLength(
      gameSize: Int
  ): UIO[Int] =
    sink.update(setupEvents.userChoseInvalidOption) *> askWinningLengthUntilValid(gameSize)

  private def verifyWinningLength(
      gameSize: Int
  )(length: Int): IO[GameSetupSettingsSource.Error, Int] =
    IO.fromEither(
      Either.cond(
        length >= 1 && length <= gameSize,
        length,
        GameSetupSettingsSource.Error.InvalidSetting
      )
    )
}

package tictactoe.domain.setup.standard

import tictactoe.domain.runner.GameStateSink
import tictactoe.domain.setup._
import tictactoe.typeclasses.MonadE
import tictactoe.typeclasses.MonadE._

class StandardGameSetupRunner[F[+_, +_]: MonadE, S](
    source: GameSetupSettingsSource[F],
    setupEvents: SetupEvents[S],
    sink: GameStateSink[F, S],
    maxGameSize: Int
) extends GameSetupRunner[F] {

  override def runSetup: F[Nothing, GameSetup] =
    for {
      gameSize <- askGameSizeUntilValid
      winningLength <- askWinningLengthUntilValid(gameSize)
      setup = GameSetup(gameSize, winningLength)
      _ <- sink.update(setupEvents.setupCompleted(setup))
    } yield setup

  private def askGameSizeUntilValid: F[Nothing, Int] =
    for {
      _ <- sink.update(setupEvents.gameSizeRequested(maxGameSize))
      size <- source.askGameSize.flatMap(verifyGameSize).handleError(_ => catchInvalidGameSize)
    } yield size

  private def verifyGameSize(size: Int): F[GameSetupSettingsSource.Error, Int] =
    MonadE[F].fromEither(
      Either
        .cond(size >= 1 && size <= maxGameSize, size, GameSetupSettingsSource.Error.InvalidSetting)
    )

  private def catchInvalidGameSize: F[Nothing, Int] =
    sink.update(setupEvents.userChoseInvalidOption).flatMap(_ => askGameSizeUntilValid)

  private def askWinningLengthUntilValid(gameSize: Int): F[Nothing, Int] =
    for {
      _ <- sink.update(setupEvents.winningLengthRequested(gameSize))
      size <- source.askWinningGameLength
        .flatMap(verifyWinningLength(gameSize))
        .handleError(_ => catchInvalidWinningLength(gameSize))
    } yield size

  private def catchInvalidWinningLength(
      gameSize: Int
  ): F[Nothing, Int] =
    sink
      .update(setupEvents.userChoseInvalidOption)
      .flatMap(_ => askWinningLengthUntilValid(gameSize))

  private def verifyWinningLength(
      gameSize: Int
  )(length: Int): F[GameSetupSettingsSource.Error, Int] =
    MonadE[F].fromEither(
      Either.cond(
        length >= 1 && length <= gameSize,
        length,
        GameSetupSettingsSource.Error.InvalidSetting
      )
    )
}

package tictactoe.domain.setup.standard

import scalaz.zio.{IO, ZIO}
import tictactoe.domain.runner.GameRunner.HasStateRef
import tictactoe.domain.runner.GameStateSink
import tictactoe.domain.setup._

class StandardGameSetupRunner[S](
    source: GameSetupSettingsSource,
    setupEvents: SetupEvents[S],
    sink: GameStateSink[S],
    maxGameSize: Int
) extends GameSetupRunner[S] {

  override def runSetup: ZIO[HasStateRef[S], Nothing, GameSetup] =
    for {
      gameSize <- askGameSizeUntilValid
      winningLength <- askWinningLengthUntilValid(gameSize)
      setup = GameSetup(gameSize, winningLength)
      _ <- notify(setupEvents.setupCompleted(setup))
    } yield setup

  private def askGameSizeUntilValid: ZIO[HasStateRef[S], Nothing, Int] =
    for {
      _ <- notify(setupEvents.gameSizeRequested(maxGameSize))
      size <- source.askGameSize.flatMap(verifyGameSize).catchAll(_ => catchInvalidGameSize)
    } yield size

  private def verifyGameSize(size: Int): IO[GameSetupSettingsSource.Error, Int] =
    IO.fromEither(
      Either
        .cond(size >= 1 && size <= maxGameSize, size, GameSetupSettingsSource.Error.InvalidSetting)
    )

  private def catchInvalidGameSize: ZIO[HasStateRef[S], Nothing, Int] =
    notify(setupEvents.userChoseInvalidOption) *> askGameSizeUntilValid

  private def askWinningLengthUntilValid(gameSize: Int): ZIO[HasStateRef[S], Nothing, Int] =
    for {
      _ <- notify(setupEvents.winningLengthRequested(gameSize))
      size <- source
        .askWinningGameLength(gameSize)
        .flatMap(verifyWinningLength(gameSize))
        .catchAll(_ => catchInvalidWinningLength(gameSize))
    } yield size

  private def catchInvalidWinningLength(
      gameSize: Int
  ): ZIO[HasStateRef[S], Nothing, Int] =
    notify(setupEvents.userChoseInvalidOption) *> askWinningLengthUntilValid(gameSize)

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

  private def notify(state: S => S): ZIO[HasStateRef[S], Nothing, Unit] =
    for {
      currentState <- ZIO.accessM[HasStateRef[S]](_.state.get)
      nextState = state(currentState)
      _ <- ZIO.accessM[HasStateRef[S]](_.state.set(nextState))
      _ <- sink.use(nextState)
    } yield ()
}

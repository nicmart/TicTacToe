package tictactoe.domain.setup.standard

import scalaz.zio.ZIO
import tictactoe.domain.runner.GameRunner.HasStateRef
import tictactoe.domain.runner.GameStateSink
import tictactoe.domain.setup.SetupEvent.{
  GameSizeRequested,
  SetupCompleted,
  UserChoseInvalidOption,
  WinningLengthRequested
}
import tictactoe.domain.setup._

class StandardGameSetupRunner[S](
    source: GameSetupSettingsSource,
    setupEventStateTransition: SetupEventStateTransition[S],
    sink: GameStateSink[S]
) extends GameSetupRunner[S] {

  override def runSetup: ZIO[HasStateRef[S], Nothing, GameSetup] =
    for {
      gameSize <- askGameSizeUntilValid
      winningLength <- askWinningLengthUntilValid(gameSize)
      setup = GameSetup(gameSize, winningLength)
      _ <- notify(SetupCompleted(setup))
    } yield setup

  private def askGameSizeUntilValid: ZIO[HasStateRef[S], Nothing, Int] =
    for {
      _ <- notify(GameSizeRequested)
      size <- source.askGameSize.catchAll(_ => catchInvalidGameSize)
    } yield size

  private def catchInvalidGameSize: ZIO[HasStateRef[S], Nothing, Int] =
    notify(UserChoseInvalidOption) *> askGameSizeUntilValid

  private def askWinningLengthUntilValid(gameSize: Int): ZIO[HasStateRef[S], Nothing, Int] =
    for {
      _ <- notify(WinningLengthRequested)
      size <- source
        .askWinningGameLength(gameSize)
        .catchAll(_ => catchInvalidWinningLength(gameSize))
    } yield size

  private def catchInvalidWinningLength(
      gameSize: Int
  ): ZIO[HasStateRef[S], Nothing, Int] =
    notify(UserChoseInvalidOption) *> askWinningLengthUntilValid(gameSize)

  private def notify(event: SetupEvent): ZIO[HasStateRef[S], Nothing, Unit] =
    for {
      currentState <- ZIO.accessM[HasStateRef[S]](_.state.get)
      nextState = setupEventStateTransition.receive(currentState, event)
      _ <- ZIO.accessM[HasStateRef[S]](_.state.set(nextState))
      _ <- sink.use(nextState)
    } yield ()
}
package tictactoe.domain.setup

sealed trait SetupEvent

object SetupEvent {
  final case object GameSizeRequested extends SetupEvent
  final case object WinningLengthRequested extends SetupEvent
  final case object UserChoseInvalidOption extends SetupEvent
  final case class SetupCompleted(gameSetup: GameSetup) extends SetupEvent
}

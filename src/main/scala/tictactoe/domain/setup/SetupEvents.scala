package tictactoe.domain.setup

trait SetupEvents[S] {
  def gameSizeRequested(state: S): S
  def winningLengthRequested(state: S): S
  def userChoseInvalidOption(state: S): S
  def setupCompleted(gameSetup: GameSetup)(state: S): S
}

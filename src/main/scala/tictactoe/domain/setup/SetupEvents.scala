package tictactoe.domain.setup

trait SetupEvents[S] {
  def gameSizeRequested(maxSize: Int)(state: S): S
  def winningLengthRequested(gameSize: Int)(state: S): S
  def userChoseInvalidOption(state: S): S
  def setupCompleted(gameSetup: GameSetup)(state: S): S
}

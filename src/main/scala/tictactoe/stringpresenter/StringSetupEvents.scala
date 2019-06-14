package tictactoe.stringpresenter

import tictactoe.domain.setup.{GameSetup, SetupEvents}

class StringSetupEvents(strings: GameStrings) extends SetupEvents[GameStringViewModel] {
  override def gameSizeRequested(maxSize: Int)(state: GameStringViewModel): GameStringViewModel =
    state.withMessage(strings.enterGameSize(1, maxSize))
  override def winningLengthRequested(
      gameSize: Int
  )(state: GameStringViewModel): GameStringViewModel =
    state.withMessage(strings.enterWinningLength(1, gameSize))
  override def userChoseInvalidOption(state: GameStringViewModel): GameStringViewModel =
    state.withMessage(strings.invalidSettingEntered)
  override def setupCompleted(gameSetup: GameSetup)(
      state: GameStringViewModel
  ): GameStringViewModel = state
}

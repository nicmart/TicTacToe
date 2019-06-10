package tictactoe.stringpresenter

import tictactoe.domain.setup.{GameSetup, SetupEvents}

class StringSetupEvents(strings: GameStrings) extends SetupEvents[GameStringViewModel] {
  override def gameSizeRequested(state: GameStringViewModel): GameStringViewModel =
    state.withMessage(strings.enterGameSize(1, 20))
  override def winningLengthRequested(state: GameStringViewModel): GameStringViewModel =
    state.withMessage(strings.enterWinningLength(1, 20))
  override def userChoseInvalidOption(state: GameStringViewModel): GameStringViewModel =
    state.withMessage(strings.invalidSettingEntered)
  override def setupCompleted(gameSetup: GameSetup)(
      state: GameStringViewModel
  ): GameStringViewModel = state
}

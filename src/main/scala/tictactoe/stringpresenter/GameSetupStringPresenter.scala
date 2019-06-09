package tictactoe.stringpresenter

import tictactoe.domain.setup.{SetupEvent, SetupEventStateTransition}

class GameSetupStringPresenter(strings: GameStrings)
    extends SetupEventStateTransition[GameStringViewModel] {
  override def receive(state: GameStringViewModel, event: SetupEvent): GameStringViewModel =
    event match {
      case SetupEvent.GameSizeRequested      => state.withMessage(strings.enterGameSize(1, 20))
      case SetupEvent.WinningLengthRequested => state.withMessage(strings.enterWinningLength(1, 20))
      case SetupEvent.UserChoseInvalidOption => state.withMessage(strings.invalidSettingEntered)
      case SetupEvent.SetupCompleted(_)      => state
    }
}

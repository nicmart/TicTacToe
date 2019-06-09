package tictactoe.stringpresenter

import tictactoe.domain.game.model.Player
import tictactoe.domain.game.model.State.Finished
import tictactoe.domain.game.model.State.Result.{Draw, Winner}
import tictactoe.domain.runner.{GameEvent, GameEventStateTransition}

class GameEventStringPresenter(boardPresenter: BoardStringPresenter, gameStrings: GameStrings)
    extends GameEventStateTransition[GameStringViewModel] {

  def receive(state: GameStringViewModel, event: GameEvent): GameStringViewModel =
    event match {

      case GameEvent.GameStarted(game) =>
        state.cleanMessages.withBoard(boardPresenter.render(game.board))

      case GameEvent.PlayerMoveRequested(_, player) =>
        state.withMessage(gameStrings.playerHasToChooseMove(playerName(player)))

      case GameEvent.PlayerMoved(game, _, _) =>
        state.cleanMessages.withBoard(boardPresenter.render(game.board))

      case GameEvent.GameEnded(game) =>
        game.state match {
          case Finished(Winner(player)) =>
            state.cleanMessages.withMessage(gameStrings.gameEndedWithWinner(playerName(player)))
          case Finished(Draw) => state.cleanMessages.withMessage(gameStrings.gameEndedWithDraw)
          case _              => state.cleanMessages.withMessage(gameStrings.unexpectedState)
        }

      case GameEvent.PlayerChoseInvalidMove(_, _) =>
        state.cleanMessages.withMessage(gameStrings.invalidMove)

      case GameEvent.PlayerChoseIllegalMove(_, _, _) =>
        state.cleanMessages.withMessage(gameStrings.invalidMove)
    }

  private def playerName(player: Player): String = player.fold("Player 1", "Player 2")
}

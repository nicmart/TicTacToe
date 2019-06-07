package tictactoe.stringpresenter

import tictactoe.domain.game.model.Player
import tictactoe.domain.game.model.State.Finished
import tictactoe.domain.game.model.State.Result.{Draw, Winner}
import tictactoe.domain.runner.GameEvent
import tictactoe.stringpresenter.GameStringViewModel._

class GameStringPresenter(boardPresenter: BoardStringPresenter, gameStrings: GameStrings) {

  def render(state: GameStringViewModel, event: GameEvent): GameStringViewModel =
    event match {

      case GameEvent.GameIsAboutToStart(_) =>
        state.setMessage(gameStrings.gameIsAboutToStart)

      case GameEvent.PlayerHasToChooseMove(game, player) =>
        Board(
          boardPresenter.render(game.board),
          gameStrings.playerHasToChooseMove(playerName(player))
        )

      case GameEvent.GameHasEnded(game) =>
        game.state match {
          case Finished(Winner(player)) =>
            state.setMessage(gameStrings.gameEndedWithWinner(playerName(player)))
          case Finished(Draw) => state.setMessage(gameStrings.gameEndedWithDraw)
          case _              => state.setMessage(gameStrings.unexpectedState)
        }

      case GameEvent.PlayerHasChosenInvalidMove(_, _) =>
        state.setMessage(gameStrings.invalidMove)

      case GameEvent.PlayerHasChosenIllegalMove(_, _, _) =>
        state.setMessage(gameStrings.invalidMove)
    }

  private def playerName(player: Player): String = player.fold("Player 1", "Player 2")
}

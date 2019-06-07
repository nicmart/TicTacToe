package tictactoe.stringpresenter

import tictactoe.domain.game.model.Player
import tictactoe.domain.game.model.State.Finished
import tictactoe.domain.game.model.State.Result.{Draw, Winner}
import tictactoe.domain.runner.GameEvent
import tictactoe.stringpresenter.GameStringViewModel._

class GameStringPresenter(boardPresenter: BoardStringPresenter, gameStrings: GameStrings) {

  def render(state: GameStringViewModel, event: GameEvent): GameStringViewModel =
    event match {

      case GameEvent.GameStarted(game) =>
        Board(Some(gameStrings.gameIsAboutToStart), boardPresenter.render(game.board))

      case GameEvent.PlayerMoveRequested(_, player) =>
        state.setMessage(gameStrings.playerHasToChooseMove(playerName(player)))

      case GameEvent.PlayerMoved(game, _, _) =>
        Board(None, boardPresenter.render(game.board))

      case GameEvent.GameEnded(game) =>
        game.state match {
          case Finished(Winner(player)) =>
            state.setMessage(gameStrings.gameEndedWithWinner(playerName(player)))
          case Finished(Draw) => state.setMessage(gameStrings.gameEndedWithDraw)
          case _              => state.setMessage(gameStrings.unexpectedState)
        }

      case GameEvent.PlayerChoseInvalidMove(_, _) =>
        state.setMessage(gameStrings.invalidMove)

      case GameEvent.PlayerChoseIllegalMove(_, _, _) =>
        state.setMessage(gameStrings.invalidMove)
    }

  private def playerName(player: Player): String = player.fold("Player 1", "Player 2")
}

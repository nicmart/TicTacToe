package tictactoe.stringpresenter

import tictactoe.domain.runner.{GameEvent, GameRunState}
import GameRunStateViewModel._
import tictactoe.domain.game.model.Player
import tictactoe.domain.game.model.State.Finished
import tictactoe.domain.game.model.State.Result.{Draw, Winner}

class GameRunStateStringPresenter(boardPresenter: BoardStringPresenter, gameStrings: GameStrings) {

  def render(gameRunState: GameRunState): GameRunStateViewModel = gameRunState.event match {

    case GameEvent.GameIsAboutToStart =>
      Message(gameStrings.gameIsAboutToStart)

    case GameEvent.PlayerHasToChooseMove(player) =>
      Board(
        boardPresenter.render(gameRunState.game.board),
        gameStrings.playerHasToChooseMove(playerName(player))
      )

    case GameEvent.GameHasEnded =>
      gameRunState.game.state match {
        case Finished(Winner(player)) =>
          Message(gameStrings.gameEndedWithWinner(playerName(player)))
        case Finished(Draw) => Message(gameStrings.gameEndedWithDraw)
        case _              => Message(gameStrings.unexpectedState)
      }

    case GameEvent.PlayerHasChosenInvalidMove(_) =>
      Message(gameStrings.invalidMove)

    case GameEvent.PlayerHasChosenIllegalMove(_, _) =>
      Message(gameStrings.invalidMove)
  }

  private def playerName(player: Player): String = player.fold("Player 1", "Player 2")
}

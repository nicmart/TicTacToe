package tictactoe.stringpresenter

import tictactoe.domain.game.model.State.Finished
import tictactoe.domain.game.model.State.Result.{Draw, Winner}
import tictactoe.domain.game.model.{Board, Player}
import tictactoe.domain.game.{Game, model}
import tictactoe.domain.runner.GameEvents

final class StringGameEvents(boardPresenter: BoardStringPresenter, gameStrings: GameStrings)
    extends GameEvents[GameStringViewModel => GameStringViewModel] {

  override def gameStarted(game: Game): GameStringViewModel => GameStringViewModel =
    _.cleanMessages.withBoard(boardPresenter.render(game.board))

  override def gameEnded(game: Game): GameStringViewModel => GameStringViewModel =
    state =>
      game.state match {
        case Finished(Winner(player)) =>
          state.cleanMessages.withMessage(gameStrings.gameEndedWithWinner(playerName(player)))
        case Finished(Draw) => state.cleanMessages.withMessage(gameStrings.gameEndedWithDraw)
        case _              => state.cleanMessages.withMessage(gameStrings.unexpectedState)
      }

  override def playerMoveRequested(
      game: Game,
      player: Player
  ): GameStringViewModel => GameStringViewModel =
    _.withMessage(gameStrings.playerHasToChooseMove(playerName(player)))

  override def playerMoved(
      game: Game,
      player: Player,
      move: Board.Cell
  ): GameStringViewModel => GameStringViewModel =
    _.cleanMessages.withBoard(boardPresenter.render(game.board))

  override def playerChoseInvalidMove(
      game: Game,
      error: model.Error
  ): GameStringViewModel => GameStringViewModel =
    _.cleanMessages.withMessage(gameStrings.invalidMove)

  override def playerChoseIllegalMove(
      game: Game,
      move: Board.Cell,
      error: model.Error
  ): GameStringViewModel => GameStringViewModel =
    _.cleanMessages.withMessage(gameStrings.invalidMove)

  private def playerName(player: Player): String = player.fold("Player 1", "Player 2")
}

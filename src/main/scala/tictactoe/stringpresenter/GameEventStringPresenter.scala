package tictactoe.stringpresenter

import tictactoe.domain.game.model.State.Finished
import tictactoe.domain.game.model.State.Result.{Draw, Winner}
import tictactoe.domain.game.model.{Board, Player}
import tictactoe.domain.game.{Game, model}
import tictactoe.domain.runner.GameEvents

final class GameEventStringPresenter(boardPresenter: BoardStringPresenter, gameStrings: GameStrings)
    extends GameEvents[GameStringViewModel] {

  override def gameStarted(game: Game)(state: GameStringViewModel): GameStringViewModel =
    state.cleanMessages.withBoard(boardPresenter.render(game.board))

  override def gameEnded(game: Game)(state: GameStringViewModel): GameStringViewModel =
    game.state match {
      case Finished(Winner(player)) =>
        state.cleanMessages.withMessage(gameStrings.gameEndedWithWinner(playerName(player)))
      case Finished(Draw) => state.cleanMessages.withMessage(gameStrings.gameEndedWithDraw)
      case _              => state.cleanMessages.withMessage(gameStrings.unexpectedState)
    }

  override def playerMoveRequested(game: Game, player: Player)(
      state: GameStringViewModel
  ): GameStringViewModel =
    state.withMessage(gameStrings.playerHasToChooseMove(playerName(player)))

  override def playerMoved(game: Game, player: Player, move: Board.Cell)(
      state: GameStringViewModel
  ): GameStringViewModel =
    state.cleanMessages.withBoard(boardPresenter.render(game.board))

  override def playerChoseInvalidMove(
      game: Game,
      error: model.Error
  )(state: GameStringViewModel): GameStringViewModel =
    state.cleanMessages.withMessage(gameStrings.invalidMove)

  override def playerChoseIllegalMove(
      game: Game,
      move: Board.Cell,
      error: model.Error
  )(state: GameStringViewModel): GameStringViewModel =
    state.cleanMessages.withMessage(gameStrings.invalidMove)

  private def playerName(player: Player): String = player.fold("Player 1", "Player 2")
}

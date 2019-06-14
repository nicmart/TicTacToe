package tictactoe.domain.runner

import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, Error, Player}

trait GameEvents[S] {
  def gameStarted(game: Game)(state: S): S
  def gameEnded(game: Game)(state: S): S
  def playerMoveRequested(game: Game, player: Player)(state: S): S
  def playerMoved(game: Game, player: Player, move: Board.Cell)(state: S): S
  def playerChoseInvalidMove(game: Game, error: Error)(state: S): S
  def playerChoseIllegalMove(game: Game, move: Board.Cell, error: Error)(state: S): S
}

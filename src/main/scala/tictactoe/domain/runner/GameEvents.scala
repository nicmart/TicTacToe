package tictactoe.domain.runner

import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, Error, Player}

trait GameEvents[T] {
  def gameStarted(game: Game): T
  def gameEnded(game: Game): T
  def playerMoveRequested(game: Game, player: Player): T
  def playerMoved(game: Game, player: Player, move: Board.Cell): T
  def playerChoseInvalidMove(game: Game, error: Error): T
  def playerChoseIllegalMove(game: Game, move: Board.Cell, error: Error): T
}

object GameEvents {
  final class Mapped[T1, T2](events: GameEvents[T1], f: T1 => T2) extends GameEvents[T2] {
    override def gameStarted(game: Game): T2 = f(events.gameStarted(game))
    override def gameEnded(game: Game): T2 = f(events.gameEnded(game))
    override def playerMoveRequested(game: Game, player: Player): T2 =
      f(events.playerMoveRequested(game, player))
    override def playerMoved(game: Game, player: Player, move: Board.Cell): T2 =
      f(events.playerMoved(game, player, move))
    override def playerChoseInvalidMove(game: Game, error: Error): T2 =
      f(events.playerChoseInvalidMove(game, error))
    override def playerChoseIllegalMove(game: Game, move: Board.Cell, error: Error): T2 =
      f(events.playerChoseIllegalMove(game, move, error))
  }
}

package tictactoe.domain.runner

import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, Error, Player}

sealed trait GameEvent {
  def game: Game
}

object GameEvent {
  case class GameStarted(game: Game) extends GameEvent
  case class GameEnded(game: Game) extends GameEvent
  case class PlayerMoveRequested(game: Game, player: Player) extends GameEvent
  case class PlayerMoved(game: Game, player: Player, move: Board.Cell) extends GameEvent
  case class PlayerChoseInvalidMove(game: Game, error: Error) extends GameEvent
  case class PlayerChoseIllegalMove(game: Game, move: Board.Cell, error: Error) extends GameEvent
}

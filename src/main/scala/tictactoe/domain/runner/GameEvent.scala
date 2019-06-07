package tictactoe.domain.runner

import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, Error, Player}

sealed trait GameEvent {
  def game: Game
}

object GameEvent {
  case class PlayerHasToChooseMove(game: Game, player: Player) extends GameEvent
  case class GameIsAboutToStart(game: Game) extends GameEvent
  case class GameHasEnded(game: Game) extends GameEvent
  case class PlayerHasChosenInvalidMove(game: Game, error: Error) extends GameEvent
  case class PlayerHasChosenIllegalMove(game: Game, move: Board.Cell, error: Error)
      extends GameEvent
}

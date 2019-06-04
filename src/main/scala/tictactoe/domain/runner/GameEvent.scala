package tictactoe.domain.runner

import tictactoe.domain.game.model.{Board, Error, Player}

sealed trait GameEvent

object GameEvent {
  case class PlayerHasToChooseMove(player: Player) extends GameEvent
  case object GameIsAboutToStart extends GameEvent
  case object GameHasEnded extends GameEvent
  case class PlayerHasChosenInvalidMove(error: Error) extends GameEvent
  case class PlayerHasChosenIllegalMove(move: Board.Cell, error: Error) extends GameEvent
}

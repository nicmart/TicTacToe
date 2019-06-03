package tictactoe.domain.runner

import scalaz.zio.ZIO
import tictactoe.domain.game.model.{Board, Error}

trait GameEvents {
  def playerHasToChooseMove: ZIO[WithGameRunState, Nothing, GameRunState]
  def gameIsAboutToStart: ZIO[WithGameRunState, Nothing, GameRunState]
  def playerHasChosenInvalidMove(error: Error): ZIO[WithGameRunState, Nothing, GameRunState]
  def playerHasChosenIllegalMove(move: Board.Cell, error: Error): ZIO[WithGameRunState, Nothing, GameRunState]
  def gameHasEnded: ZIO[WithGameRunState, Nothing, GameRunState]
}

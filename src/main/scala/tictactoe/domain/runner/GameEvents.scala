package tictactoe.domain.runner

import scalaz.zio.{IO, Ref, ZIO}
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, Error, Player}

trait GameEvents {
  def playerHasToChooseMove: ZIO[StateRef, Nothing, GameState]
  def gameIsAboutToStart: ZIO[StateRef, Nothing, GameState]
  def playerHasChosenInvalidMove(error: Error): ZIO[StateRef, Nothing, GameState]
  def playerHasChosenIllegalMove(move: Board.Cell, error: Error): ZIO[StateRef, Nothing, GameState]
  def gameHasEnded: ZIO[StateRef, Nothing, GameState]
}

case class GameState(game: Game)

trait StateRef {
  def gameState: Ref[GameState]
}

object StateRef {
  def getState: ZIO[StateRef, Error, GameState] =
    ZIO.fromFunctionM(_.gameState.get)

  def setState(gameState: GameState): ZIO[StateRef, Error, Unit] =
    ZIO.fromFunctionM(_.gameState.set(gameState))

  def updateState(f: GameState => GameState): ZIO[StateRef, Error, GameState] =
    ZIO.fromFunctionM(_.gameState.update(f))
}

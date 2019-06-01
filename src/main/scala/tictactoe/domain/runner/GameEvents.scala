package tictactoe.domain.runner

import scalaz.zio.IO
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, Error}

trait GameEvents {
  def gameIsAboutToStart(game: Game): IO[Error, Unit]
  def playerHasChosenMove(move: Board.Cell): IO[Error, Unit]
  def playerHasChosenInvalidMove(move: Board.Cell, error: Error): IO[Error, Unit]
  def gameHasBeenUpdated(game: Game): IO[Error, Unit]
  def gameHasEnded(game: Game): IO[Error, Unit]
}

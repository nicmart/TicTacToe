package tictactoe.domain.runner

import scalaz.zio.IO
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.{Board, Error}

trait GamePresenter {
  def playerHasChosenMove(move: Board.Cell): IO[Error, Unit]
  def gameHasBeenUpdated(game: Game): IO[Error, Unit]
  def gameHasEnded(game: Game): IO[Error, Unit]
  def gameIsAboutToStart(game: Game): IO[Error, Unit]
}

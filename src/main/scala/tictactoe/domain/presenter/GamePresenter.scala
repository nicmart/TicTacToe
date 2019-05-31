package tictactoe.domain.presenter

import scalaz.zio.IO
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Error

trait GamePresenter {
  def showGame(game: Game): IO[Error, Unit]
}

object GamePresenter {
  def apply(f: Game => IO[Error, Unit]): GamePresenter = f(_)
}

package tictactoe.domain.runner

import scalaz.zio.IO
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.Error

trait MovesSource {
  def askMove(game: Game): IO[Error, Cell]
}

object MovesSource {
  def apply(f: Game => IO[Error, Cell]): MovesSource = f(_)
}

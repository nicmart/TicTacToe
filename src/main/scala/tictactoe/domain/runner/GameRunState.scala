package tictactoe.domain.runner

import scalaz.zio.Ref
import tictactoe.domain.game.Game

trait GameState {
  def gameState: Ref[Game]
}

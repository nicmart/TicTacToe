package tictactoe.domain.runner

import scalaz.zio.Ref
import tictactoe.domain.game.Game

case class GameRunState(game: Game)

trait WithGameRunState {
  def gameState: Ref[GameRunState]
}

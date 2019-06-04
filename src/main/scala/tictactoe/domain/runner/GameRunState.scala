package tictactoe.domain.runner

import scalaz.zio.Ref
import tictactoe.domain.game.Game

final case class GameRunState(game: Game, event: GameEvent) {
  def withEvent(event: GameEvent): GameRunState = copy(event = event)
  def withGame(game: Game): GameRunState = copy(game = game)
}

trait WithGameRunState {
  def gameState: Ref[GameRunState]
}

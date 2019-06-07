package tictactoe.domain.runner

import scalaz.zio.ZIO
import tictactoe.domain.runner.GameRunner.HasStateRef

trait GameStateTransition[S] {
  def receive(event: GameEvent): ZIO[HasStateRef[S], Nothing, Unit]
}

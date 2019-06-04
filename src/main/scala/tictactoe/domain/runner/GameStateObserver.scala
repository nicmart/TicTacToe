package tictactoe.domain.runner

import scalaz.zio.UIO

trait GameStateObserver {
  def receive(state: GameRunState): UIO[Unit]
}

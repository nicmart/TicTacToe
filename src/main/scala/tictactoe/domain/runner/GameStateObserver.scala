package tictactoe.domain.runner

import scalaz.zio.{Ref, ZIO}

trait GameStateObserver[S] {
  def receive(event: GameEvent): ZIO[ObserverState[S], Nothing, Unit]
}

trait ObserverState[S] {
  def state: Ref[S]
}

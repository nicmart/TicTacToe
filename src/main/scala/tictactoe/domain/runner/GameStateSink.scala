package tictactoe.domain.runner

import scalaz.zio.UIO

trait GameStateSink[S] {
  def use(state: S): UIO[Unit]
}

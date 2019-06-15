package tictactoe.domain.runner

import scalaz.zio.UIO

trait GameStateSink[S] {
  def update(f: S => S): UIO[Unit]
}

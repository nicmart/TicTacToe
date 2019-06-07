package tictactoe.console

import scalaz.zio.{UIO, ZIO}
import tictactoe.domain.runner.GameStateSink
import tictactoe.stringpresenter.GameStringViewModel

final class ConsoleGameStateSink(view: GameStringView) extends GameStateSink[GameStringViewModel] {

  override def use(state: GameStringViewModel): UIO[Unit] =
    putStrLn(view.render(state))

  private def putStrLn(line: String, newLines: Int = 0): UIO[Unit] =
    ZIO.effectTotal(println(line + "\n" * newLines))
}

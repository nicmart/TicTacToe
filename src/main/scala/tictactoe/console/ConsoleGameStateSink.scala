package tictactoe.console

import scalaz.zio.{Ref, UIO, ZIO}
import tictactoe.domain.runner.GameStateSink
import tictactoe.stringpresenter.GameStringViewModel

final class ConsoleGameStateSink(view: GameStringView, screen: Ref[GameStringViewModel])
    extends GameStateSink[GameStringViewModel] {

  override def update(f: GameStringViewModel => GameStringViewModel): UIO[Unit] =
    for {
      currentScreen <- screen.get
      newScreen = f(currentScreen)
      _ <- screen.set(newScreen)
      _ <- putStrLn(view.render(newScreen))
    } yield ()

  private def putStrLn(line: String, newLines: Int = 0): UIO[Unit] =
    ZIO.effectTotal(println(line + "\n" * newLines))
}

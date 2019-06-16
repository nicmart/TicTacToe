package tictactoe.console

import tictactoe.domain.runner.GameStateSink
import tictactoe.stringpresenter.GameStringViewModel
import tictactoe.typeclasses.MonadE._
import tictactoe.typeclasses.{Delay, MonadE, URef}

final class ConsoleGameStateSink[F[+_, +_]: MonadE: Delay](
    view: GameStringView,
    screen: URef[F, GameStringViewModel]
) extends GameStateSink[F, GameStringViewModel] {

  override def update(f: GameStringViewModel => GameStringViewModel): F[Nothing, Unit] =
    for {
      currentScreen <- screen.get
      newScreen = f(currentScreen)
      _ <- screen.set(newScreen)
      _ <- putStrLn(view.render(newScreen))
    } yield ()

  private def putStrLn(line: String, newLines: Int = 0): F[Nothing, Unit] =
    Delay[F].delayTotal(println(line + "\n" * newLines))
}

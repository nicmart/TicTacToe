package tictactoe.console

import tictactoe.domain.runner.GameStateSink
import tictactoe.stringpresenter.GameStringViewModel
import tictactoe.typeclasses.MonadE._
import tictactoe.typeclasses.{MonadE, URef}

final class ConsoleGameStateSink[F[+_, +_]: MonadE](
    view: GameStringView,
    screen: URef[F, GameStringViewModel],
    console: Console[F]
) extends GameStateSink[F, GameStringViewModel] {

  override def update(f: GameStringViewModel => GameStringViewModel): F[Nothing, Unit] =
    for {
      currentScreen <- screen.get
      newScreen = f(currentScreen)
      _ <- screen.set(newScreen)
      _ <- console.put(view.render(newScreen))
    } yield ()
}

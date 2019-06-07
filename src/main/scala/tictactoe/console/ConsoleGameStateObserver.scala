package tictactoe.console

import scalaz.zio.{UIO, ZIO}
import tictactoe.domain.runner.GameRunner.HasStateRef
import tictactoe.domain.runner.{GameEvent, GameStateObserver}
import tictactoe.stringpresenter.{GameStringPresenter, GameStringViewModel}

final class ConsoleGameStateObserver(
    presenter: GameStringPresenter,
    view: GameRunStateStringView
) extends GameStateObserver[GameStringViewModel] {

  override def receive(
      event: GameEvent
  ): ZIO[HasStateRef[GameStringViewModel], Nothing, Unit] =
    for {
      currentViewModel <- ZIO.accessM[HasStateRef[GameStringViewModel]](_.state.get)
      newViewModel = presenter.render(currentViewModel, event)
      _ <- ZIO.accessM[HasStateRef[GameStringViewModel]](_.state.set(newViewModel))
      _ <- putStrLn(view.render(newViewModel))
    } yield ()

  private def putStrLn(line: String, newLines: Int = 0): UIO[Unit] =
    ZIO.effectTotal(println(line + "\n" * newLines))
}
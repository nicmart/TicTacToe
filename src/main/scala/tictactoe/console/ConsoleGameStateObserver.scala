package tictactoe.console

import scalaz.zio.{UIO, ZIO}
import tictactoe.domain.runner.{GameEvent, GameStateObserver, ObserverState}
import tictactoe.stringpresenter.{GameRunStateStringPresenter, GameRunStateStringViewModel}

final class ConsoleGameStateObserver(
    presenter: GameRunStateStringPresenter,
    view: GameRunStateStringView
) extends GameStateObserver[GameRunStateStringViewModel] {

  override def receive(
      event: GameEvent
  ): ZIO[ObserverState[GameRunStateStringViewModel], Nothing, Unit] =
    for {
      currentViewModel <- ZIO.accessM[ObserverState[GameRunStateStringViewModel]](_.state.get)
      newViewModel = presenter.render(currentViewModel, event)
      _ <- ZIO.accessM[ObserverState[GameRunStateStringViewModel]](_.state.set(newViewModel))
      _ <- putStrLn(view.render(newViewModel))
    } yield ()

  private def putStrLn(line: String, newLines: Int = 0): UIO[Unit] =
    ZIO.effectTotal(println(line + "\n" * newLines))
}

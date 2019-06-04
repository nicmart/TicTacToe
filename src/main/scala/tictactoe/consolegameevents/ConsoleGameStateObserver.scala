package tictactoe.consolegameevents

import scalaz.zio.{UIO, ZIO}
import tictactoe.domain.runner.{GameRunState, GameStateObserver}
import tictactoe.stringpresenter.GameRunStateStringPresenter

final class ConsoleGameStateObserver(
    presenter: GameRunStateStringPresenter,
    view: GameRunStateStringView
) extends GameStateObserver {

  override def receive(state: GameRunState): UIO[Unit] =
    putStrLn(view.render(presenter.render(state)))

  private def putStrLn(line: String, newLines: Int = 0): UIO[Unit] =
    ZIO.effectTotal(println(line + "\n" * newLines))
}

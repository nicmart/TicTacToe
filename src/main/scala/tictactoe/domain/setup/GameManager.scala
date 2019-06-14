package tictactoe.domain.setup

import scalaz.zio.{IO, ZIO}
import tictactoe.domain.runner.GameRunner.HasStateRef

class GameManager[S](setupRunner: GameSetupRunner[S], gameBuilder: GameBuilder[S]) {
  def run: ZIO[HasStateRef[S], Nothing, Unit] =
    for {
      setup <- setupRunner.runSetup
      runner <- gameBuilder.runner(setup)
      initialState = gameBuilder.initialState(setup)
      _ <- runner.runGame.provideSomeM(initialState).catchAll(_ => IO.unit)
      _ <- run
    } yield ()
}

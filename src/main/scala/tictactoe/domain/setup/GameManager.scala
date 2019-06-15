package tictactoe.domain.setup

import scalaz.zio.UIO

class GameManager[S](setupRunner: GameSetupRunner[S], gameBuilder: GameBuilder[S]) {
  def run: UIO[Unit] =
    for {
      setup <- setupRunner.runSetup
      runner <- gameBuilder.runner(setup)
      _ <- runner.runGame
      _ <- run
    } yield ()
}

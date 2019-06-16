package tictactoe.domain.setup

import tictactoe.typeclasses.MonadE
import tictactoe.typeclasses.MonadE._

class GameManager[F[+_, +_]: MonadE, S](
    setupRunner: GameSetupRunner[F],
    gameBuilder: GameBuilder[F, S]
) {
  def run: F[Nothing, Unit] =
    for {
      setup <- setupRunner.runSetup
      runner <- gameBuilder.runner(setup)
      _ <- runner.runGame
      _ <- run
    } yield ()
}

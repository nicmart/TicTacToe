package tictactoe.domain.setup

import scalaz.zio.{IO, UIO}

class GameManager[S](setupSource: SetupSource, gameBuilder: GameBuilder[S]) {
  def run: UIO[Unit] =
    for {
      setup <- askSetup
      runner = gameBuilder.runner(setup)
      initialState = gameBuilder.initialState(setup)
      _ <- runner.runGame.provideSomeM(initialState).catchAll(_ => IO.unit)
      continue <- askContinue
      _ <- if (continue) run else IO.unit
    } yield ()

  def askSetup: UIO[GameSetup] =
    setupSource.askSetup.catchAll(_ => askSetup)

  def askContinue: UIO[Boolean] =
    setupSource.askToContinue.catchAll(_ => askContinue)
}

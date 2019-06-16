package tictactoe.domain.setup

import tictactoe.domain.runner.GameRunner

trait GameBuilder[F[+_, +_], S] {
  def runner(setup: GameSetup): F[Nothing, GameRunner[F, S]]
}

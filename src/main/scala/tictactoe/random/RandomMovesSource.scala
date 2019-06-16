package tictactoe.random

import tictactoe.domain.game.model.Board
import tictactoe.domain.game.{Game, model}
import tictactoe.domain.runner.MovesSource
import tictactoe.typeclasses.MonadE
import tictactoe.typeclasses.MonadE._

class RandomMovesSource[F[+_, +_]: MonadE](random: Random[F]) extends MovesSource[F] {
  override def askMove(game: Game): F[model.Error, Board.Cell] = {
    val availableMoves = game.availableMoves
    random.nextInt(availableMoves.size).map(availableMoves)
  }
}

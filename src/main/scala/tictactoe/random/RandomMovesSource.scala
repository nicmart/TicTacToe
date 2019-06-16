package tictactoe.random

import tictactoe.domain.game.model.Board
import tictactoe.domain.game.{Game, model}
import tictactoe.domain.runner.MovesSource
import tictactoe.typeclasses.{Delay, MonadE}
import MonadE._

import scala.util.Random

class RandomMovesSource[F[+_, +_]: Delay: MonadE] extends MovesSource[F] {
  override def askMove(game: Game): F[model.Error, Board.Cell] = {
    val availableMoves = game.availableMoves
    nextInt(availableMoves.size).map(availableMoves)
  }

  private def nextInt(size: Int): F[Nothing, Int] =
    Delay[F].delayTotal(Random.nextInt(size))
}

package tictactoe.random

import scalaz.zio.{IO, UIO}
import tictactoe.domain.game.model.Board
import tictactoe.domain.game.{Game, model}
import tictactoe.domain.runner.MovesSource

import scala.util.Random

object RandomMovesSource extends MovesSource {
  override def askMove(game: Game): IO[model.Error, Board.Cell] = {
    val availableMoves = game.availableMoves
    nextInt(availableMoves.size).map(availableMoves)
  }

  private def nextInt(size: Int): UIO[Int] =
    UIO.effectTotal(Random.nextInt(size))
}

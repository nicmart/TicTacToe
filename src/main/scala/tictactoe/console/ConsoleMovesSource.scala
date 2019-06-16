package tictactoe.console

import tictactoe.domain.game.model.Board
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.Error.UnexpectedError
import tictactoe.domain.game.{Game, model}
import tictactoe.domain.runner.MovesSource
import tictactoe.typeclasses.{Delay, MonadE}
import tictactoe.typeclasses.MonadE._

import scala.io.StdIn
import scala.util.Try

class ConsoleMovesSource[F[+_, +_]: MonadE: Delay] extends MovesSource[F] {
  override def askMove(game: Game): F[model.Error, Board.Cell] =
    for {
      input <- readMove
      number <- MonadE[F].fromTry(Try(input.toInt)).mapError(toModelError)
      numberOIndexed = number - 1
      move = Cell(numberOIndexed % game.size, numberOIndexed / game.size)
    } yield move

  private def readMove: F[model.Error, String] =
    Delay[F].delay(StdIn.readLine().trim).mapError(toModelError)

  private def toModelError(throwable: Throwable): model.Error =
    UnexpectedError(throwable.getMessage)
}

package tictactoe.console

import scalaz.zio.{IO, ZIO}
import tictactoe.domain.game.model.Board
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.Error.UnexpectedError
import tictactoe.domain.game.{Game, model}
import tictactoe.domain.runner.MovesSource

import scala.io.StdIn
import scala.util.Try

class ConsoleMovesSource extends MovesSource {
  override def askMove(game: Game): IO[model.Error, Board.Cell] =
    for {
      input <- readMove
      number <- IO.fromTry(Try(input.toInt)).mapError(toModelError)
      numberOIndexed = number - 1
      move = Cell(numberOIndexed % game.size, numberOIndexed / game.size)
    } yield move

  private def readMove: IO[model.Error, String] =
    ZIO.effect(StdIn.readLine().trim).mapError(toModelError)

  private def toModelError(throwable: Throwable): model.Error =
    UnexpectedError(throwable.getMessage)
}

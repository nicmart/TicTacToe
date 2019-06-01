package tictactoe.infrastructure.console

import scalaz.zio.{IO, UIO, ZIO}
import tictactoe.domain.game.model.Board
import tictactoe.domain.game.{Game, model}
import tictactoe.domain.runner.GameEvents

final class ConsoleGameEvents extends GameEvents {
  override def playerHasChosenMove(move: Board.Cell): IO[model.Error, Unit] =
    putStrLn(s"You chose $move")

  override def playerHasChosenInvalidMove(
      move: Board.Cell,
      error: model.Error
  ): IO[model.Error, Unit] =
    putStrLn(error.toString)

  override def gameHasBeenUpdated(game: Game): IO[model.Error, Unit] =
    putStrLn(game.toString)

  override def gameHasEnded(game: Game): IO[model.Error, Unit] =
    putStrLn("Game Ended!!!") *> putStrLn(game.state.toString) *> putStrLn("bye!")

  override def gameIsAboutToStart(game: Game): IO[model.Error, Unit] =
    putStrLn("Welcome to the most over-engineered TicTacToe ever!!!")

  def putStrLn(line: String): UIO[Unit] =
    ZIO.effectTotal(println(line))
}

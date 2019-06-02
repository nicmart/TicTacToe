package tictactoe.infrastructure.console

import scalaz.zio.{IO, UIO, ZIO}
import tictactoe.domain.game.model.State.Finished
import tictactoe.domain.game.model.State.Result.{Draw, Winner}
import tictactoe.domain.game.model.{Board, Mark, Player}
import tictactoe.domain.game.{Game, model}
import tictactoe.domain.runner.GameEvents

final class ConsoleGameEvents(boardPresenter: BoardPresenter, boardView: BoardView)
    extends GameEvents {

  override def playerHasToChooseMove(player: Player): IO[model.Error, Unit] =
    putStrLn(s"${playerName(player)}, it's your turn, please move for God's sake\n")

  override def playerHasChosenMove(move: Board.Cell): IO[model.Error, Unit] =
    putStrLn("")

  override def playerHasChosenInvalidMove(
      move: Board.Cell,
      error: model.Error
  ): IO[model.Error, Unit] =
    putStrLn(error.toString)

  override def gameHasBeenUpdated(game: Game): IO[model.Error, Unit] =
    putStrLn(renderGame(game) + "\n\n")

  override def gameHasEnded(game: Game): IO[model.Error, Unit] =
    putStrLn(game.state match {
      case Finished(Winner(player)) =>
        s"${playerName(player)} won, just because he's been lucky.\n"
      case Finished(Draw) =>
        s"That was a very boring draw. Thanks for wasting my time.\n"
      case _ => "This case should never happen, I am giving up."
    })

  override def gameIsAboutToStart(game: Game): IO[model.Error, Unit] =
    putStrLn("Welcome to the most over-engineered TicTacToe ever!!!\n\n")

  private def putStrLn(line: String): UIO[Unit] =
    ZIO.effectTotal(println(line))

  private def playerName(player: Player): String = player.mark match {
    case Mark.X => "Player 1"
    case Mark.O => "Player 2"
  }

  private def renderGame(game: Game): String = boardView.render(boardPresenter.render(game.board))
}

package tictactoe.infrastructure.console

import scalaz.zio.{IO, UIO, ZIO}
import tictactoe.domain.game.model.State.Finished
import tictactoe.domain.game.model.State.Result.{Draw, Winner}
import tictactoe.domain.game.model.{Board, Player}
import tictactoe.domain.game.{Game, model}
import tictactoe.domain.runner.GameEvents

final class ConsoleGameEvents(
    boardPresenter: BoardPresenter,
    boardView: BoardView,
    gameStrings: ConsoleGameStrings
) extends GameEvents {

  override def playerHasToChooseMove(player: Player): IO[model.Error, Unit] =
    putStrLn(gameStrings.playerHasToChooseMove(playerName(player)))

  override def playerHasChosenMove(move: Board.Cell): IO[model.Error, Unit] =
    putStrLn("")

  override def playerHasChosenInvalidMove(
      move: Board.Cell,
      error: model.Error
  ): IO[model.Error, Unit] =
    putStrLn(gameStrings.invalidMove)

  override def gameHasBeenUpdated(game: Game): IO[model.Error, Unit] =
    putStrLn(renderGame(game) + "\n\n")

  override def gameHasEnded(game: Game): IO[model.Error, Unit] =
    putStrLn(game.state match {
      case Finished(Winner(player)) => gameStrings.gameEndedWithWinner(playerName(player))
      case Finished(Draw)           => gameStrings.gameEndedWithDraw
      case _                        => gameStrings.unexpectedState
    })

  override def gameIsAboutToStart(game: Game): IO[model.Error, Unit] =
    putStrLn(gameStrings.gameIsAboutToStart)

  private def putStrLn(line: String): UIO[Unit] =
    ZIO.effectTotal(println(line))

  private def playerName(player: Player): String = player.fold("Player 1", "Player 2")

  private def renderGame(game: Game): String = boardView.render(boardPresenter.render(game.board))
}

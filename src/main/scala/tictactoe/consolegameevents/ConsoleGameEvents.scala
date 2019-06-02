package tictactoe.consolegameevents

import scalaz.zio.{IO, UIO, ZIO}
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Player}
import tictactoe.domain.game.model.State.Finished
import tictactoe.domain.game.model.State.Result.{Draw, Winner}
import tictactoe.domain.runner.GameEvents
import tictactoe.stringpresenter.BoardStringPresenter

final class ConsoleGameEvents(
    boardPresenter: BoardStringPresenter,
    boardView: BoardStringView,
    gameStrings: ConsoleGameStrings
) extends GameEvents {

  override def playerHasToChooseMove(player: Player): IO[Error, Unit] =
    putStrLn(gameStrings.playerHasToChooseMove(playerName(player)), 1)

  override def playerHasChosenInvalidMove(error: Error): IO[Error, Unit] =
    putStrLn(gameStrings.invalidMove)

  override def playerHasChosenIllegalMove(move: Cell, error: Error): IO[Error, Unit] =
    putStrLn(gameStrings.invalidMove)

  override def gameHasBeenUpdated(game: Game): IO[Error, Unit] =
    putStrLn(renderGame(game), 2)

  override def gameHasEnded(game: Game): IO[Error, Unit] =
    putStrLn(
      game.state match {
        case Finished(Winner(player)) => gameStrings.gameEndedWithWinner(playerName(player))
        case Finished(Draw)           => gameStrings.gameEndedWithDraw
        case _                        => gameStrings.unexpectedState
      },
      1
    )

  override def gameIsAboutToStart(game: Game): IO[Error, Unit] =
    putStrLn(gameStrings.gameIsAboutToStart, 2)

  private def putStrLn(line: String, newLines: Int = 0): UIO[Unit] =
    ZIO.effectTotal(println(line + "\n" * newLines))

  private def playerName(player: Player): String = player.fold("Player 1", "Player 2")

  private def renderGame(game: Game): String = boardView.render(boardPresenter.render(game.board))
}

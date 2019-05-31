package tictactoe.domain.runner

import scalaz.zio.IO
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Mark, Player}

final case class GameRunner(
    game: Game,
    presenter: GamePresenter,
    playerXMoves: MovesSource,
    playerOMoves: MovesSource
) {

  def runGame: IO[Error, Unit] =
    for {
      _ <- presenter.gameIsAboutToStart(game)
      _ <- loop.either
      _ <- presenter.gameHasEnded(game)
    } yield ()

  private def loop: IO[Error, Nothing] = next.flatMap(_.loop)

  private def next: IO[Error, GameRunner] =
    for {
      _ <- presenter.gameHasBeenUpdated(game)
      currentPlayer <- currentPlayer
      move <- currentPlayerMoves(currentPlayer).askMove(game)
      _ <- presenter.playerHasChosenMove(move)
      gameNext <- makeMove(move)
      runnerNext = copy(game = gameNext)
    } yield runnerNext

  private def currentPlayer: IO[Error, Player] = IO.fromEither(game.currentPlayer)

  private def makeMove(move: Cell): IO[Error, Game] = IO.fromEither(game.makeMove(move))

  private def currentPlayerMoves(currentPlayer: Player): MovesSource =
    currentPlayer.mark match {
      case Mark.X => playerXMoves
      case Mark.O => playerOMoves
    }
}

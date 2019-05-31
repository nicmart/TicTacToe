package tictactoe.domain.runner

import scalaz.zio.IO
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Mark, Player}
import tictactoe.domain.presenter.GamePresenter

final case class GameRunner(
    game: Game,
    presenter: GamePresenter,
    playerXMoves: MovesSource,
    playerOMoves: MovesSource
) {

  def loop: IO[Error, Nothing] = next.flatMap(_.loop)

  def next: IO[Error, GameRunner] =
    for {
      _ <- presenter.showGame(game)
      currentPlayer <- currentPlayer
      move <- currentPlayerMoves(currentPlayer).askMove(game)
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

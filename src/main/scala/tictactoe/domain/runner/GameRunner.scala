package tictactoe.domain.runner

import scalaz.zio.IO
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Mark, Player}

final case class GameRunner(
    game: Game,
    presenter: GameEvents,
    playerXMoves: MovesSource,
    playerOMoves: MovesSource
) {

  def runGame: IO[Error, Unit] =
    for {
      _ <- presenter.gameIsAboutToStart(game)
      lastGame <- loop
      _ <- presenter.gameHasBeenUpdated(lastGame)
      _ <- presenter.gameHasEnded(lastGame)
    } yield ()

  private def loop: IO[Error, Game] = next.flatMap { nextRunner =>
    if (nextRunner.game.inProgress) nextRunner.loop
    else IO.succeed(nextRunner.game)
  }

  private def next: IO[Error, GameRunner] =
    for {
      _ <- presenter.gameHasBeenUpdated(game)
      currentPlayer <- currentPlayer
      currentPlayerMoves = currentPlayerMovesSource(currentPlayer)
      gameNext <- askMoveUntilValidAndMakeMove(currentPlayer, currentPlayerMoves)
      runnerNext = copy(game = gameNext)
    } yield runnerNext

  private def askMoveUntilValidAndMakeMove(player: Player, moves: MovesSource): IO[Error, Game] =
    for {
      _ <- presenter.playerHasToChooseMove(player)
      move <- moves.askMove(game)
      _ <- presenter.playerHasChosenMove(move)
      gameNext <- makeMove(move).catchAll(catchInvalidMove(player, move, moves))
    } yield gameNext

  private def catchInvalidMove(player: Player, move: Cell, moves: MovesSource)(
      error: Error
  ): IO[Error, Game] =
    presenter.playerHasChosenInvalidMove(move, error) andThen askMoveUntilValidAndMakeMove(
      player,
      moves
    )

  private def currentPlayer: IO[Error, Player] = IO.fromEither(game.currentPlayer)

  private def makeMove(move: Cell): IO[Error, Game] = IO.fromEither(game.makeMove(move))

  private def currentPlayerMovesSource(currentPlayer: Player): MovesSource =
    currentPlayer.mark match {
      case Mark.X => playerXMoves
      case Mark.O => playerOMoves
    }
}

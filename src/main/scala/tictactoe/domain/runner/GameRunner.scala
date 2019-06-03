package tictactoe.domain.runner

import scalaz.zio.IO
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Player}

final case class GameRunner(
    game: Game,
    events: GameEvents,
    player1Moves: MovesSource,
    player2Moves: MovesSource
) {

  def runGame: IO[Error, Unit] =
    for {
      _ <- events.gameIsAboutToStart(game)
      lastGame <- playUntilEnd
      _ <- events.gameHasBeenUpdated(lastGame)
      _ <- events.gameHasEnded(lastGame)
    } yield ()

  private def playUntilEnd: IO[Error, Game] = playSingleTurn.flatMap { nextRunner =>
    if (nextRunner.game.inProgress) nextRunner.playUntilEnd
    else IO.succeed(nextRunner.game)
  }

  private def playSingleTurn: IO[Error, GameRunner] =
    for {
      _ <- events.gameHasBeenUpdated(game)
      currentPlayer <- currentPlayer
      currentPlayerMoves = currentPlayerMovesSource(currentPlayer)
      gameNext <- askMoveUntilLegalAndMakeMove(currentPlayer, currentPlayerMoves)
      runnerNext = copy(game = gameNext)
    } yield runnerNext

  private def askMoveUntilLegalAndMakeMove(player: Player, moves: MovesSource): IO[Error, Game] =
    for {
      move <- askMoveUntilValid(player, moves)
      gameNext <- makeMove(move).catchAll(catchIllegalMove(player, move, moves))
    } yield gameNext

  private def askMoveUntilValid(player: Player, moves: MovesSource): IO[Error, Cell] =
    for {
      _ <- events.playerHasToChooseMove(player)
      move <- moves.askMove(game).catchAll(catchInvalidMove(player, moves))
    } yield move

  private def catchInvalidMove(player: Player, moves: MovesSource)(error: Error): IO[Error, Cell] =
    events.playerHasChosenInvalidMove(error) andThen askMoveUntilValid(player, moves)

  private def catchIllegalMove(player: Player, move: Cell, moves: MovesSource)(
      error: Error
  ): IO[Error, Game] =
    events.playerHasChosenIllegalMove(move, error) andThen askMoveUntilLegalAndMakeMove(
      player,
      moves
    )

  private def currentPlayer: IO[Error, Player] = IO.fromEither(game.currentPlayer)

  private def makeMove(move: Cell): IO[Error, Game] = IO.fromEither(game.makeMove(move))

  private def currentPlayerMovesSource(currentPlayer: Player): MovesSource =
    currentPlayer.fold(player1Moves, player2Moves)
}

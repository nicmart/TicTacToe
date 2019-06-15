package tictactoe.domain.runner

import scalaz.zio.{IO, Ref, UIO, ZIO}
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Player}

final case class GameRunner[S](
    game: Ref[Game],
    player1Moves: MovesSource,
    player2Moves: MovesSource,
    gameEvents: GameEvents[S],
    gameStateSink: GameStateSink[S]
) {
  def runGame: UIO[Unit] =
    for {
      _ <- notify(gameEvents.gameStarted(_)(_))
      _ <- playUntilEnd
      _ <- notify(gameEvents.gameEnded(_)(_))
    } yield ()

  private def playUntilEnd: UIO[Unit] =
    for {
      _ <- playSingleTurn
      game <- currentGame
      _ <- if (game.inProgress) playUntilEnd else ZIO.unit
    } yield ()

  private def playSingleTurn: UIO[Game] =
    for {
      player <- currentPlayer
      move <- askMoveUntilValid(player)
      game <- tryMove(move).catchAll(catchIllegalMove(move))
      _ <- setGame(game)
      _ <- notify(gameEvents.playerMoved(_, player, move)(_))
    } yield game

  private def askMoveUntilValid(player: Player): UIO[Cell] =
    for {
      _ <- notify(gameEvents.playerMoveRequested(_, player)(_))
      move <- askMove(player).catchAll(catchInvalidMove(player))
    } yield move

  private def catchInvalidMove(player: Player)(error: Error): UIO[Cell] =
    notify(gameEvents.playerChoseInvalidMove(_, error)(_)) *> askMoveUntilValid(player)

  private def catchIllegalMove(move: Cell)(error: Error): UIO[Game] =
    notify(gameEvents.playerChoseIllegalMove(_, move, error)(_)) *> playSingleTurn

  private def currentPlayer: UIO[Player] =
    currentGame.map(_.currentPlayer)

  private def tryMove(move: Cell): IO[Error, Game] =
    currentGame.flatMap(game => ZIO.fromEither(game.makeMove(move)))

  private def askMove(player: Player): IO[Error, Cell] =
    currentGame.flatMap(game => player.fold(player1Moves, player2Moves).askMove(game))

  private def currentGame: UIO[Game] =
    game.get

  private def setGame(newGame: Game): UIO[Unit] =
    game.set(newGame)

  private def notify(event: (Game, S) => S): UIO[Unit] =
    for {
      game <- currentGame
      _ <- gameStateSink.update(event(game, _))
    } yield ()
}

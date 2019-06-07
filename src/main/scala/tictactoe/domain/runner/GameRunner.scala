package tictactoe.domain.runner

import scalaz.zio.ZIO
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Player}
import tictactoe.domain.runner.GameEvent._
import GameRunner._

final case class GameRunner[S](
    player1Moves: MovesSource,
    player2Moves: MovesSource,
    gameStateObserver: GameStateObserver[S]
) {
  def runGame: ZIO[State[S], Error, Unit] =
    for {
      _ <- notify(GameIsAboutToStart)
      _ <- playUntilEnd
      _ <- notify(GameHasEnded)
    } yield ()

  private def playUntilEnd: ZIO[State[S], Error, Unit] =
    for {
      _ <- playSingleTurn
      game <- currentGame
      _ <- if (game.inProgress) playUntilEnd else ZIO.unit
    } yield ()

  private def playSingleTurn: ZIO[State[S], Error, Game] =
    for {
      move <- askMoveUntilValid
      game <- tryMove(move).catchAll(catchIllegalMove(move))
      _ <- setGame(game)
    } yield game

  private def askMoveUntilValid: ZIO[State[S], Error, Cell] =
    for {
      player <- currentPlayer
      _ <- notify(PlayerHasToChooseMove(_, player))
      move <- askMove(player).catchAll(catchInvalidMove)
    } yield move

  private def catchInvalidMove(error: Error): ZIO[State[S], Error, Cell] =
    notify(PlayerHasChosenInvalidMove(_, error)) *> askMoveUntilValid

  private def catchIllegalMove(move: Cell)(error: Error): ZIO[State[S], Error, Game] =
    notify(PlayerHasChosenIllegalMove(_, move, error)) *> playSingleTurn

  private def currentPlayer: ZIO[State[S], Error, Player] =
    currentGame.flatMap(game => ZIO.fromEither(game.currentPlayer))

  private def tryMove(move: Cell): ZIO[State[S], Error, Game] =
    currentGame.flatMap(game => ZIO.fromEither(game.makeMove(move)))

  private def askMove(currentPlayer: Player): ZIO[State[S], Error, Cell] =
    currentGame.flatMap(game => currentPlayer.fold(player1Moves, player2Moves).askMove(game))

  private def currentGame: ZIO[State[S], Nothing, Game] =
    ZIO.fromFunctionM(_.gameState.get)

  private def setGame(game: Game): ZIO[State[S], Nothing, Unit] =
    ZIO.fromFunctionM(_.gameState.set(game))

  private def notify(event: Game => GameEvent): ZIO[State[S], Nothing, Unit] =
    ZIO.accessM[State[S]](_.gameState.get).flatMap(game => gameStateObserver.receive(event(game)))
}

object GameRunner {
  final type State[S] = ObserverState[S] with GameState
}

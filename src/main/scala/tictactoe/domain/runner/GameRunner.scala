package tictactoe.domain.runner

import scalaz.zio.ZIO
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Player}
import tictactoe.domain.runner.GameEvent._

final case class GameRunner(
    player1Moves: MovesSource,
    player2Moves: MovesSource,
    gameStateObserver: GameStateObserver
) {

  def runGame: ZIO[WithGameRunState, Error, Unit] =
    for {
      _ <- withEvent(GameIsAboutToStart)
      _ <- playUntilEnd
      _ <- withEvent(GameHasEnded)
    } yield ()

  private def playUntilEnd: ZIO[WithGameRunState, Error, Unit] =
    for {
      _ <- playSingleTurn
      game <- currentGame
      _ <- if (game.inProgress) playUntilEnd else ZIO.unit
    } yield ()

  private def playSingleTurn: ZIO[WithGameRunState, Error, Game] =
    for {
      move <- askMoveUntilValid
      game <- tryMove(move).catchAll(catchIllegalMove(move))
      _ <- setGame(game)
    } yield game

  private def askMoveUntilValid: ZIO[WithGameRunState, Error, Cell] =
    for {
      player <- currentPlayer
      _ <- withEvent(PlayerHasToChooseMove(player))
      move <- askMove(player).catchAll(catchInvalidMove)
    } yield move

  private def catchInvalidMove(error: Error): ZIO[WithGameRunState, Error, Cell] =
    withEvent(PlayerHasChosenInvalidMove(error)) *> askMoveUntilValid

  private def catchIllegalMove(move: Cell)(error: Error): ZIO[WithGameRunState, Error, Game] =
    withEvent(PlayerHasChosenIllegalMove(move, error)) *> playSingleTurn

  private def currentPlayer: ZIO[WithGameRunState, Error, Player] =
    currentGame.flatMap(game => ZIO.fromEither(game.currentPlayer))

  private def tryMove(move: Cell): ZIO[WithGameRunState, Error, Game] =
    currentGame.flatMap(game => ZIO.fromEither(game.makeMove(move)))

  private def askMove(currentPlayer: Player): ZIO[WithGameRunState, Error, Cell] =
    currentGame.flatMap(game => currentPlayer.fold(player1Moves, player2Moves).askMove(game))

  private def currentGame: ZIO[WithGameRunState, Nothing, Game] =
    currentGameState.map(_.game)

  private def currentGameState: ZIO[WithGameRunState, Nothing, GameRunState] =
    ZIO.fromFunctionM(_.gameState.get)

  private def setGameState(gameState: GameRunState): ZIO[WithGameRunState, Nothing, Unit] =
    ZIO.fromFunctionM(_.gameState.set(gameState))

  private def setGame(game: Game): ZIO[WithGameRunState, Nothing, Unit] =
    ZIO.fromFunctionM(_.gameState.update(_.withGame(game)).unit)

  private def withEvent(event: GameEvent): ZIO[WithGameRunState, Nothing, Unit] =
    for {
      gameState <- currentGameState
      newGameState = gameState.withEvent(event)
      _ <- setGameState(newGameState)
      _ <- gameStateObserver.receive(newGameState)
    } yield ()

}

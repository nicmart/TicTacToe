package tictactoe.domain.runner

import scalaz.zio.ZIO
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Player}

final case class GameRunner(
    events: GameEvents,
    player1Moves: MovesSource,
    player2Moves: MovesSource
) {

  def runGame: ZIO[WithGameRunState, Error, Unit] =
    for {
      _ <- events.gameIsAboutToStart
      _ <- playUntilEnd
      _ <- events.gameHasEnded
    } yield ()

  private def playUntilEnd: ZIO[WithGameRunState, Error, Unit] =
    for {
      _ <- playSingleTurn
      game <- currentGame
      _ <- if (game.inProgress) playUntilEnd else ZIO.unit
    } yield ()

  private def playSingleTurn: ZIO[WithGameRunState, Error, Unit] =
    for {
      move <- askMoveUntilValid
      _ <- makeMove(move).catchAll(catchIllegalMove(move))
    } yield ()

  private def askMoveUntilValid: ZIO[WithGameRunState, Error, Cell] =
    for {
      _ <- events.playerHasToChooseMove
      player <- currentPlayer
      move <- askMove(player).catchAll(catchInvalidMove)
    } yield move

  private def catchInvalidMove(error: Error): ZIO[WithGameRunState, Error, Cell] =
    events.playerHasChosenInvalidMove(error) andThen askMoveUntilValid

  private def catchIllegalMove(move: Cell)(error: Error): ZIO[WithGameRunState, Error, Unit] =
    events.playerHasChosenIllegalMove(move, error) andThen playSingleTurn

  private def currentPlayer: ZIO[WithGameRunState, Error, Player] =
    currentGame.flatMap(game => ZIO.fromEither(game.currentPlayer))
    
  private def makeMove(move: Cell): ZIO[WithGameRunState, Error, Game] =
    currentGame.flatMap(game => ZIO.fromEither(game.makeMove(move)))

  private def askMove(currentPlayer: Player): ZIO[WithGameRunState, Error, Cell] =
    currentGame.flatMap(game => currentPlayer.fold(player1Moves, player2Moves).askMove(game))

  private def currentGame: ZIO[WithGameRunState, Error, Game] =
    ZIO.fromFunctionM(_.gameState.get.map(_.game))
}

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

  def runGame: ZIO[StateRef, Error, Unit] =
    for {
      _ <- events.gameIsAboutToStart
      _ <- playUntilEnd
      _ <- events.gameHasEnded
    } yield ()

  private def playUntilEnd: ZIO[StateRef, Error, Unit] =
    for {
      _ <- playSingleTurn
      game <- currentGame
      _ <- if (game.inProgress) playUntilEnd else ZIO.unit
    } yield ()

  private def playSingleTurn: ZIO[StateRef, Error, Unit] =
    for {
      move <- askMoveUntilValid
      _ <- makeMove(move).catchAll(catchIllegalMove(move))
    } yield ()

  private def askMoveUntilValid: ZIO[StateRef, Error, Cell] =
    for {
      _ <- events.playerHasToChooseMove
      player <- currentPlayer
      move <- askMove(player).catchAll(catchInvalidMove)
    } yield move

  private def catchInvalidMove(error: Error): ZIO[StateRef, Error, Cell] =
    events.playerHasChosenInvalidMove(error) andThen askMoveUntilValid

  private def catchIllegalMove(move: Cell)(error: Error): ZIO[StateRef, Error, Unit] =
    events.playerHasChosenIllegalMove(move, error) andThen playSingleTurn

  private def currentPlayer: ZIO[StateRef, Error, Player] =
    currentGame.flatMap(game => ZIO.fromEither(game.currentPlayer))
    
  private def makeMove(move: Cell): ZIO[StateRef, Error, Game] =
    currentGame.flatMap(game => ZIO.fromEither(game.makeMove(move)))

  private def askMove(currentPlayer: Player): ZIO[StateRef, Error, Cell] =
    currentGame.flatMap(game => currentPlayer.fold(player1Moves, player2Moves).askMove(game))

  private def currentGame: ZIO[StateRef, Error, Game] =
    ZIO.fromFunctionM(_.gameState.get.map(_.game))
}

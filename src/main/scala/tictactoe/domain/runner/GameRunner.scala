package tictactoe.domain.runner

import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Player}
import tictactoe.typeclasses.MonadE._
import tictactoe.typeclasses.{MonadE, URef}

final case class GameRunner[F[+_, +_], S](
    game: URef[F, Game],
    player1Moves: MovesSource[F],
    player2Moves: MovesSource[F],
    gameEvents: GameEvents[S],
    gameStateSink: GameStateSink[F, S]
)(implicit F: MonadE[F]) {

  def runGame: F[Nothing, Unit] =
    for {
      _ <- notify(gameEvents.gameStarted(_)(_))
      _ <- playUntilEnd
      _ <- notify(gameEvents.gameEnded(_)(_))
    } yield ()

  private def playUntilEnd: F[Nothing, Unit] =
    for {
      _ <- playSingleTurn
      game <- currentGame
      _ <- if (game.inProgress) playUntilEnd else F.unit
    } yield ()

  private def playSingleTurn: F[Nothing, Game] =
    for {
      player <- currentPlayer
      move <- askMoveUntilValid(player)
      game <- F.handleError(tryMove(move), catchIllegalMove(move))
      _ <- setGame(game)
      _ <- notify(gameEvents.playerMoved(_, player, move)(_))
    } yield game

  private def askMoveUntilValid(player: Player): F[Nothing, Cell] =
    for {
      _ <- notify(gameEvents.playerMoveRequested(_, player)(_))
      move <- F.handleError(askMove(player), catchInvalidMove(player))
    } yield move

  private def catchInvalidMove(player: Player)(error: Error): F[Nothing, Cell] =
    notify(gameEvents.playerChoseInvalidMove(_, error)(_)).flatMap(_ => askMoveUntilValid(player))

  private def catchIllegalMove(move: Cell)(error: Error): F[Nothing, Game] =
    notify(gameEvents.playerChoseIllegalMove(_, move, error)(_)).flatMap(_ => playSingleTurn)

  private def currentPlayer: F[Nothing, Player] =
    currentGame.map(_.currentPlayer)

  private def tryMove(move: Cell): F[Error, Game] =
    game.get.flatMap(game => F.fromEither(game.makeMove(move)))

  private def askMove(player: Player): F[Error, Cell] =
    currentGame.flatMap(game => player.fold(player1Moves, player2Moves).askMove(game))

  private def currentGame: F[Nothing, Game] =
    game.get

  private def setGame(newGame: Game): F[Nothing, Unit] =
    game.set(newGame)

  private def notify(event: (Game, S) => S): F[Nothing, Unit] =
    for {
      game <- currentGame
      _ <- gameStateSink.update(event(game, _))
    } yield ()
}

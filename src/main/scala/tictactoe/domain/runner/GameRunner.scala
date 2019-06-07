package tictactoe.domain.runner

import scalaz.zio.{Ref, ZIO}
import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.{Error, Player}
import tictactoe.domain.runner.GameEvent._
import GameRunner._

final case class GameRunner[S](
    player1Moves: MovesSource,
    player2Moves: MovesSource,
    gameStateTransition: GameStateTransition[S],
    gameStateSink: GameStateSink[S]
) {
  def runGame: ZIO[State[S], Error, Unit] =
    for {
      _ <- notify(GameStarted)
      _ <- playUntilEnd
      _ <- notify(GameEnded)
    } yield ()

  private def playUntilEnd: ZIO[State[S], Error, Unit] =
    for {
      _ <- playSingleTurn
      game <- currentGame
      _ <- if (game.inProgress) playUntilEnd else ZIO.unit
    } yield ()

  private def playSingleTurn: ZIO[State[S], Error, Game] =
    for {
      player <- currentPlayer
      move <- askMoveUntilValid(player)
      game <- tryMove(move).catchAll(catchIllegalMove(move))
      _ <- setGame(game)
      _ <- notify(GameEvent.PlayerMoved(_, player, move))
    } yield game

  private def askMoveUntilValid(player: Player): ZIO[State[S], Error, Cell] =
    for {
      _ <- notify(PlayerMoveRequested(_, player))
      move <- askMove(player).catchAll(catchInvalidMove(player))
    } yield move

  private def catchInvalidMove(player: Player)(error: Error): ZIO[State[S], Error, Cell] =
    notify(PlayerChoseInvalidMove(_, error)) *> askMoveUntilValid(player)

  private def catchIllegalMove(move: Cell)(error: Error): ZIO[State[S], Error, Game] =
    notify(PlayerChoseIllegalMove(_, move, error)) *> playSingleTurn

  private def currentPlayer: ZIO[State[S], Error, Player] =
    currentGame.flatMap(game => ZIO.fromEither(game.currentPlayer))

  private def tryMove(move: Cell): ZIO[State[S], Error, Game] =
    currentGame.flatMap(game => ZIO.fromEither(game.makeMove(move)))

  private def askMove(currentPlayer: Player): ZIO[State[S], Error, Cell] =
    currentGame.flatMap(game => currentPlayer.fold(player1Moves, player2Moves).askMove(game))

  private def currentGame: ZIO[State[S], Nothing, Game] =
    ZIO.fromFunctionM(_.game.get)

  private def setGame(game: Game): ZIO[State[S], Nothing, Unit] =
    ZIO.fromFunctionM(_.game.set(game))

  private def notify(event: Game => GameEvent): ZIO[State[S], Nothing, Unit] =
    for {
      game <- ZIO.accessM[State[S]](_.game.get)
      state <- ZIO.accessM[State[S]](_.state.get)
      newState = gameStateTransition.receive(state, event(game))
      _ <- ZIO.accessM[State[S]](_.state.set(newState))
      _ <- gameStateSink.use(newState)
    } yield ()
}

object GameRunner {

  trait HasGameRef {
    def game: Ref[Game]
  }

  trait HasStateRef[S] {
    def state: Ref[S]
  }

  final type State[S] = HasStateRef[S] with HasGameRef
}

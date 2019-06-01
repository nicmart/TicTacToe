package tictactoe.infrastructure.console
import scalaz.zio._
import tictactoe.domain.game.model.{Board, StandardGame}
import tictactoe.domain.runner.GameRunner

object ConsoleApp extends App {
  val runner: GameRunner =
    GameRunner(
      StandardGame.newGame(Board.Size(3)),
      new ConsoleGameEvents,
      new ConsoleMovesSource,
      new ConsoleMovesSource
    )

  def run(args: List[String]) =
    runner.runGame.either.const(0)
}

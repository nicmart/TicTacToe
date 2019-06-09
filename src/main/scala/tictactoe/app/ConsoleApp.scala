package tictactoe.app

import scalaz.zio.{App, ZIO}
import tictactoe.console._
import tictactoe.domain.setup.GameManager
import tictactoe.stringpresenter.GameStringViewModel

object ConsoleApp extends App {
  val manager = new GameManager[GameStringViewModel](
    new ConsoleSetupSource,
    new ConsoleGameBuilder
  )

  def run(args: List[String]): ZIO[ConsoleApp.Environment, Nothing, Int] =
    manager.run.const(0)
}

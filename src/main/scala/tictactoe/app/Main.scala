package tictactoe.app

import scalaz.zio.{App, IO, ZIO}
import tictactoe.typeclasses.instances.ZioInstances._
import tictactoe.underware.ZioConsole

object Main extends App {

  def run(args: List[String]): ZIO[Main.Environment, Nothing, Int] =
    app.manager.flatMap(_.run).const(0)

  private def app = new ConsoleApp[IO](ZioConsole, MakeZioRef)
}

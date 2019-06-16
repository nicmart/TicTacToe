package tictactoe.app

import scalaz.zio.{App, IO, ZIO}
import tictactoe.typeclasses.instances.EitherInstances.MakeEitherRef
import tictactoe.typeclasses.instances.EitherInstances._
import tictactoe.underware.EitherConsole

object SyncMain extends App {

  def run(args: List[String]): ZIO[Main.Environment, Nothing, Int] =
    IO.fromEither(app.manager.flatMap(_.run)).const(0)

  private def app = new ConsoleApp[Either](EitherConsole, MakeEitherRef)
}

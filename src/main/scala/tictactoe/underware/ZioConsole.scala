package tictactoe.underware

import scalaz.zio.IO
import tictactoe.console.Console

import scala.io.StdIn

object ZioConsole extends Console[IO] {
  def read: IO[Throwable, String] = IO.effect(StdIn.readLine().trim)
  def put(string: String): IO[Nothing, Unit] = IO.effectTotal(println(string))
}

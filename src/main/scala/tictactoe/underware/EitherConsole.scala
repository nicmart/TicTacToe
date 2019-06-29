package tictactoe.underware

import tictactoe.console.Console
import scala.io.StdIn
import scala.util.Try

object EitherConsole extends Console[Either] {
  def read: Either[Throwable, String] = Try(StdIn.readLine().trim).toEither
  def put(string: String): Either[Nothing, Unit] = Right(println(string))
}

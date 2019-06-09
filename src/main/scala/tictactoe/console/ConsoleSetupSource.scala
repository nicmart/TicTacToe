package tictactoe.console

import scalaz.zio.{IO, UIO, ZIO}
import tictactoe.domain.setup.{GameSetup, SetupSource}

import scala.io.StdIn
import scala.util.Try

class ConsoleSetupSource extends SetupSource {

  override def askSetup: UIO[GameSetup] =
    for {
      gameSize <- askGameSizeUntilValid
      winningLineLength <- askWinningLineLengthUntilValid(gameSize)
    } yield GameSetup(gameSize, winningLineLength)

  override def askToContinue: UIO[Boolean] =
    askToContinueUntilValid

  private def askToContinueUntilValid: UIO[Boolean] =
    for {
      _ <- putStrLn("Do you want to play another game (Y/N)?")
      input <- readLn.map(_.toLowerCase)
      validatedInput <- if (Set("y", "n").contains(input)) IO.succeed(input == "y")
      else askToContinueUntilValid
    } yield validatedInput

  private def askGameSizeUntilValid: UIO[Int] =
    for {
      _ <- putStrLn("Please enter a game size greater than 1")
      input <- readLn
      size <- IO.fromEither(Try(input.toInt).toEither).catchAll(_ => askGameSizeUntilValid)
      validSize <- if (size >= 1 && size <= 25) IO.succeed(size) else askGameSizeUntilValid
    } yield validSize

  private def askWinningLineLengthUntilValid(gameSize: Int): UIO[Int] =
    for {
      _ <- putStrLn("Please enter the length of the winning line")
      input <- readLn
      length <- IO
        .fromEither(Try(input.toInt).toEither)
        .catchAll(_ => askWinningLineLengthUntilValid(gameSize))
      validSize <- if (length >= 1 && length <= gameSize) IO.succeed(length)
      else askWinningLineLengthUntilValid(gameSize)
    } yield validSize

  private def putStrLn(line: String, newLines: Int = 0): UIO[Unit] =
    ZIO.effectTotal(println(line + "\n" * newLines))

  private def readLn: UIO[String] =
    ZIO.effectTotal(StdIn.readLine().trim)
}

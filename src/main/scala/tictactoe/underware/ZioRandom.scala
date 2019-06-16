package tictactoe.underware

import scalaz.zio.IO
import tictactoe.random.Random

class ZioRandom extends Random[IO] {
  override def nextInt(max: Int): IO[Nothing, Int] =
    IO.effectTotal(scala.util.Random.nextInt(max))
}

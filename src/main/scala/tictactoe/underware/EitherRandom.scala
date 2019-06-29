package tictactoe.underware

import tictactoe.random.Random

class EitherRandom extends Random[Either] {
  override def nextInt(max: Int): Either[Nothing, Int] =
    Right(scala.util.Random.nextInt(max))
}

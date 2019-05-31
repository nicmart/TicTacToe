package tictactoe.domain

import org.scalacheck.Shrink
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}

trait CommonTest
    extends FreeSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with EitherOps
    with CommonOps {
  implicit def noShrink[T]: Shrink[T] = Shrink.shrinkAny
}

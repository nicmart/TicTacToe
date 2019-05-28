package tictactoe.domain.model

import org.scalacheck.Shrink
import org.scalatest.{FreeSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

trait CommonTest
    extends FreeSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with EitherOps
    with CommonOps {
  implicit def noShrink[T]: Shrink[T] = Shrink.shrinkAny
}

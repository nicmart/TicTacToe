package tictactoe.domain.model

import org.scalatest.{FreeSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

trait CommonTest extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks with RightOps

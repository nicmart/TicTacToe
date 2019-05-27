package tictactoe.domain.model
import ScalaCheckDomainContext._

class RulesTest extends CommonTest {
  "Rules" - {
    "should determine the game state" - {
      "for an empty game" in {
        forAll(genNewGame) { game =>
          Rules.state(game) shouldBe GameState.InProgress
        }
      }
    }
  }
}

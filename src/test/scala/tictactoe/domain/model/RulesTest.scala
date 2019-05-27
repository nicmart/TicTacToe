package tictactoe.domain.model
import ScalaCheckDomainContext._
import tictactoe.domain.model.GameState.Result.Winner

class RulesTest extends CommonTest {
  "Rules" - {
    "should determine the game state" - {
      "for an empty game" in {
        forAll(genNewGame) { game =>
          Rules.state(game) shouldBe GameState.InProgress
        }
      }

      "for a winning game" in {
        forAll(genNewGame) { newGame =>
          forAll(genHistoryOfMovesWhereCurrentPlayerWins(newGame.size)) { moves =>
            val gameWithMoves = newGame.withMoves(moves)
            Rules.state(gameWithMoves) shouldBe GameState.Finished(Winner(newGame.currentMark))
          }
        }
      }
    }
  }
}

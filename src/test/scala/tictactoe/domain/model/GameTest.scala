package tictactoe.domain.model

import ScalaCheckDomainContext._

class GameTest extends CommonTest {
  "A Game" - {
    "should switch player after each move" in {
      forAll(genGameWithAvailableMove) {
        case (game, cell) =>
          whenever(game.inProgress) {
            val gameNext = game.makeMove(cell).getRight
            gameNext.currentMark shouldBe game.currentMark.switch
          }
      }
    }

    "should put the current player mark in the cell after each move" in {
      forAll(genGameWithAvailableMove) {
        case (game, cell) =>
          whenever(game.inProgress) {
            val gameNext = game.makeMove(cell).getRight
            gameNext.board.markAt(cell) shouldBe Some(game.currentMark)
          }
      }
    }
  }
}

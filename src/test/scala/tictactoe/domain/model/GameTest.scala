package tictactoe.domain.model

import ScalaCheckDomainContext._

class GameTest extends CommonTest {
  "A Game" - {
    "should switch player after each move" in {
      forAll(genGame) { game =>
        forAll(genValidCell(game.board.size.value)) { cell =>
          val gameNext = game.makeMove(cell).right.get
          gameNext.currentMark shouldBe game.currentMark.switch
        }
      }
    }

    "should put the current player mark in the cell after each move" in {
      forAll(genGame) { game =>
        forAll(genValidCell(game.board.size.value)) { cell =>
          val gameNext = game.makeMove(cell).right.get
          gameNext.board.markAt(cell) shouldBe Some(game.currentMark)
        }
      }
    }
  }
}

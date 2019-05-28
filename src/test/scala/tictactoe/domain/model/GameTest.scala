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

    "should return an error when moving on occupied cells" in {
      forAll(genInProgressGameWithMoveThatWillNotEndTheGame) {
        case (game, move) =>
          val boardWithMove = game.makeMove(move).getRight
          val error = boardWithMove.makeMove(move).getLeft
          error shouldBe Error.CellOccupied(boardWithMove, move)
      }
    }

    "should return an error when moving on a fineshed game" in {
      forAll(genWonGame) { game =>
        forAll(genValidCell(game.size)) { move =>
          val error = game.makeMove(move).getLeft
          error shouldBe Error.GameHasAlreadyEnded
        }
      }
    }
  }
}

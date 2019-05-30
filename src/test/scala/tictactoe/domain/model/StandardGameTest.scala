package tictactoe.domain.model

import ScalaCheckDomainContext._
import tictactoe.domain.model.State.Result.Winner

class StandardGameTest extends CommonTest {
  "A Standard TicTacToe Game" - {
    "should switch player after each move" in {
      forAll(genInProgressGameWithMoveThatWillNotEndTheGame) {
        case (game, cell) =>
          val gameNext = game.makeMove(cell).getRight
          gameNext.unsafeCurrentPlayer shouldBe game.unsafeCurrentPlayer.switch
      }
    }

    "should put the current player mark in the cell after each move" in {
      forAll(genGameWithAvailableMove) {
        case (game, cell) =>
          val gameNext = game.makeMove(cell).getRight
          gameNext.board.markAt(cell) shouldBe Some(game.unsafeCurrentPlayer.mark)

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

    "should return an error when moving on a finished game" in {
      forAll(genWonGame) { game =>
        forAll(genValidCell(game.size)) { move =>
          val error = game.makeMove(move).getLeft
          error shouldBe Error.GameHasAlreadyEnded
        }
      }
    }

    "for an empty game" in {
      forAll(genNewGame) { game =>
        game.state shouldBe a[State.InProgress]
      }
    }

    "for a winning game" in {
      forAll(genNewGame) { newGame =>
        forAll(genHistoryOfMovesWhereCurrentPlayerWins(newGame.size)) { moves =>
          val gameWithMoves = newGame.withMoves(moves)
          gameWithMoves.state shouldBe State.Finished(
            Winner(newGame.unsafeCurrentPlayer)
          )
        }
      }
    }
  }
}

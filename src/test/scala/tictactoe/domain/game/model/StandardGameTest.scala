package tictactoe.domain.game.model

import tictactoe.domain.CommonTest
import tictactoe.domain.ScalaCheckDomainContext._
import tictactoe.domain.game.model.State.Result.Winner

class StandardGameTest extends CommonTest {
  "A Standard TicTacToe Game" - {
    "should switch player after each move" in {
      forAll(genInProgressGameWithMoveThatWillNotEndTheGame) {
        case (game, cell) =>
          val gameNext = game.makeMove(cell).getRight
          gameNext.currentPlayer shouldBe game.currentPlayer.switch
      }
    }

    "should put the current player mark in the cell after each move" in {
      forAll(genGameWithAvailableMove) {
        case (game, cell) =>
          val gameNext = game.makeMove(cell).getRight
          gameNext.board.markAt(cell) shouldBe Some(game.currentPlayer.mark)
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
            Winner(newGame.currentPlayer)
          )
        }
      }
    }
  }
}

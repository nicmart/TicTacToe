package tictactoe.console

import org.scalacheck.Gen
import tictactoe.domain.CommonTest
import tictactoe.domain.ScalaCheckDomainContext._
import tictactoe.domain.game.model.Mark
import tictactoe.stringpresenter.BoardStringPresenter

class BoardStringPresenterTest extends CommonTest {
  "The Board Presenter" - {
    "should convert any board to a boardViewModel" in {
      forAll(genEmptyBoard) { emptyBoard =>
        forAll(genValidCells(emptyBoard.size.value)) { cells =>
          forAll(Gen.choose(0, cells.size)) { splitPoint =>
            val (cellsWithX, cellsWithO) = cells.splitAt(splitPoint)
            val board = emptyBoard
              .withCells(cellsWithX.map(_ -> Mark.X).toSeq)
              .withCells(cellsWithO.map(_ -> Mark.O).toSeq)

            val viewModel = presenter.render(board)

            board.emptyCells.foreach { cell =>
              viewModel.rows(cell.y)(cell.x) shouldBe (board.size.value * cell.y + cell.x + 1).toString
            }

            cellsWithX.foreach { cell =>
              viewModel.rows(cell.y)(cell.x) shouldBe "X"
            }

            cellsWithO.foreach { cell =>
              viewModel.rows(cell.y)(cell.x) shouldBe "O"
            }
          }
        }
      }
    }
  }

  lazy val presenter = new BoardStringPresenter(BoardStringPresenter.defaultMarkRendering)
}

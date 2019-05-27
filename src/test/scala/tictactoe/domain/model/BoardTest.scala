package tictactoe.domain.model

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}
import org.scalacheck.Arbitrary.arbitrary
import ScalaCheckDomainContext._
import tictactoe.domain.model.Board.Cell

class BoardTest extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {
  "A board" - {
    "should be completely empty when it is created" in {
      forAll(genEmptyBoard) { emptyBoard =>
        forAll(genValidCell(emptyBoard)) { cell =>
          emptyBoard.markAt(cell) shouldBe None
        }
      }
    }

    "should insert marks in valid cells" in {
      forAll(genEmptyBoard) { emptyBoard =>
        forAll(arbitrary[Mark], genValidCell(emptyBoard)) { (mark, cell) =>
          val boardWithMark = emptyBoard.withMark(mark, cell).right.get
          boardWithMark.markAt(cell) shouldBe Some(mark)
        }
      }
    }

    "should return all the horizontal lines" in {
      forAll(genEmptyBoard) { emptyBoard =>
        forAll(genValidCellCoordinate(emptyBoard), genLineOfCellValues(emptyBoard.size.value)) { (y, cellStates) =>
          val cellsToMarks = cellStates.zipWithIndex.flatMap {
            case (value, x) => value.map(mark => Cell(x, y) -> mark)
          }

          val boardWithMarks = emptyBoard.withCells(cellsToMarks)

          boardWithMarks.horizontalLines(y) shouldBe cellStates.toList
        }
      }
    }

    "should return all the vertical lines" in {
      forAll(genEmptyBoard) { emptyBoard =>
        forAll(genValidCellCoordinate(emptyBoard), genLineOfCellValues(emptyBoard.size.value)) { (x, cellStates) =>
          val cellsToMarks = cellStates.zipWithIndex.flatMap {
            case (value, y) => value.map(mark => Cell(x, y) -> mark)
          }

          val boardWithMarks = emptyBoard.withCells(cellsToMarks)

          boardWithMarks.verticalLines(x) shouldBe cellStates.toList
        }
      }
    }

    "should return all the diagonal lines" - {
      "first diagonal" in {
        forAll(genEmptyBoard) { emptyBoard =>
          forAll(genLineOfCellValues(emptyBoard.size.value)) { values =>
            val cellsToMarks = values.zipWithIndex.flatMap {
              case (value, x) => value.map(mark => Cell(x, x) -> mark)
            }

            val boardWithMarks = emptyBoard.withCells(cellsToMarks)

            boardWithMarks.diagonalLines(0) shouldBe values.toList
          }
        }
      }
      "second diagonal" in {
        forAll(genEmptyBoard) { emptyBoard =>
          forAll(genLineOfCellValues(emptyBoard.size.value)) { values =>
            val cellsToMarks = values.zipWithIndex.flatMap {
              case (value, x) => value.map(mark => Cell(emptyBoard.size.value - 1 - x, x) -> mark)
            }

            val boardWithMarks = emptyBoard.withCells(cellsToMarks)

            boardWithMarks.diagonalLines(1) shouldBe values.toList
          }
        }
      }
    }
  }

  implicit class BoardOps(board: Board) {
    def withCells(cellsStates: Seq[(Cell, Mark)]): Board =
      cellsStates.foldLeft(board) {
        case (board, (cell, mark)) => board.withMark(mark, cell).right.get
      }
  }
}

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
        forAll(genValidCellCoordinate(emptyBoard), genLineOfCellValues(emptyBoard.size.value)) { (line, values) =>
          val cellsToMarks = values.zipWithIndex.flatMap {
            case (value, column) => value.map(mark => Cell(line, column) -> mark)
          }

          val boardWithMarks = cellsToMarks.foldLeft(emptyBoard) {
            case (board, (cell, mark)) => board.withMark(mark, cell).right.get
          }

          boardWithMarks.horizontalLines(line) shouldBe values.toList
        }
      }
    }

    "should return all the vertical lines" in {
      forAll(genEmptyBoard) { emptyBoard =>
        forAll(genValidCellCoordinate(emptyBoard), genLineOfCellValues(emptyBoard.size.value)) { (column, values) =>
          val cellsToMarks = values.zipWithIndex.flatMap {
            case (value, line) => value.map(mark => Cell(line, column) -> mark)
          }

          val boardWithMarks = cellsToMarks.foldLeft(emptyBoard) {
            case (board, (cell, mark)) => board.withMark(mark, cell).right.get
          }

          boardWithMarks.verticalLines(column) shouldBe values.toList
        }
      }
    }
  }
}

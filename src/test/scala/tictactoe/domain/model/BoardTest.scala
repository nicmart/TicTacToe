package tictactoe.domain.model

import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}
import tictactoe.domain.model.Board.Cell
import tictactoe.domain.model.ScalaCheckDomainContext._

class BoardTest extends FreeSpec with Matchers with GeneratorDrivenPropertyChecks {
  "A board" - {
    "should be completely empty when it is created" in {
      forAll(genEmptyBoard) { emptyBoard =>
        forAll(genValidCell(emptyBoard.size.value)) { cell =>
          emptyBoard.markAt(cell) shouldBe None
        }
      }
    }

    "should insert marks in valid cells" in {
      forAll(genEmptyBoard) { emptyBoard =>
        forAll(arbitrary[Mark], genValidCell(emptyBoard.size.value)) { (mark, cell) =>
          val boardWithMark = emptyBoard.withMark(mark, cell).right.get
          boardWithMark.markAt(cell) shouldBe Some(mark)
        }
      }
    }

    "should return lines" in {
      forAll(genEmptyBoard) { emptyBoard =>
        forAll(genLine(emptyBoard.size.value)) { line =>
          val boardWithMarks = emptyBoard.withCells(line.cellsWithMarks)
          boardWithMarks.line(line) shouldBe line
          boardWithMarks.allLines should contain(line)
        }
      }
    }
  }

  implicit class BoardOps(board: Board) {
    def withCells(cellsStates: Seq[(Cell, Mark)]): Board =
      cellsStates.foldLeft(board) {
        case (board, (cell, mark)) => board.withMark(mark, cell).right.get
      }

    def line(line: Line): Line = line match {
      case Line.Horizontal(y, _)  => board.horizontalLines(y)
      case Line.Vertical(x, _)    => board.verticalLines(x)
      case Line.FirstDiagonal(_)  => board.firstDiagonalLine
      case Line.SecondDiagonal(_) => board.secondDiagonalLine
    }
  }
}

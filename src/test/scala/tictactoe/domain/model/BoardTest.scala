package tictactoe.domain.model

import org.scalacheck.Arbitrary.arbitrary
import tictactoe.domain.model.ScalaCheckDomainContext._

class BoardTest extends CommonTest {
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
}

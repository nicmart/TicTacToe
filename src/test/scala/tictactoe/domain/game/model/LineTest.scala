package tictactoe.domain.game.model

import tictactoe.domain.CommonTest
import tictactoe.domain.ScalaCheckDomainContext.{genEmptyBoard, genLine}

class LineTest extends CommonTest {

  "Line" - {
    "should return all the lines of a board" in {
      forAll(genEmptyBoard) { emptyBoard =>
        forAll(genLine(emptyBoard.size.value)) { line =>
          val allLines = Line.linesOfBoard(emptyBoard.size.value, emptyBoard.size.value)
          allLines should contain(line)
        }
      }
    }
  }
}

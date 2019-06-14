package tictactoe.console

import tictactoe.domain.CommonTest
import tictactoe.domain.game.model.Board
import tictactoe.stringview.{CellView, SimpleBoardStringView}
import tictactoe.underware.SizedString

class SimpleBoardStringViewTest extends CommonTest {
  "A SimpleBoardView" - {
    "should render an empty board" in {
      val board = Board.emptyBoard(Board.Size(3))
      val renderedBoard = view.render(board)
      val expectedRenderedBoard =
        """> ? | ? | ? 
           >---|---|---
           > ? | ? | ? 
           >---|---|---
           > ? | ? | ? """
          .stripMargin('>')

      renderedBoard shouldBe expectedRenderedBoard
    }
  }

  lazy val view =
    new SimpleBoardStringView(new CellView(_ => SizedString("?"), _ => SizedString("?")), 3)
}

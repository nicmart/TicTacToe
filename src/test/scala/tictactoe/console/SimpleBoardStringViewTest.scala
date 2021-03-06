package tictactoe.console

import tictactoe.domain.CommonTest
import tictactoe.stringview.SimpleBoardStringView
import tictactoe.stringpresenter.BoardStringViewModel
import tictactoe.underware.SizedString

class SimpleBoardStringViewTest extends CommonTest {
  "A SimpleBoardView" - {
    "should render an empty board" in {
      val boardViewModel = BoardStringViewModel.empty(3, SizedString("?"))
      val renderedBoard = view.render(boardViewModel)
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

  lazy val view = new SimpleBoardStringView(3)
}

package tictactoe.consolegameevents

import tictactoe.domain.CommonTest
import tictactoe.simplestringview.SimpleBoardStringView
import tictactoe.stringpresenter.BoardStringViewModel

class SimpleBoardStringViewTest extends CommonTest {
  "A SimpleBoardView" - {
    "should render an empty board" in {
      val boardViewModel = BoardStringViewModel.empty(3, "?")
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

  lazy val view = new SimpleBoardStringView
}
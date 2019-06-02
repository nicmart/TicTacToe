package tictactoe.infrastructure.console

import tictactoe.domain.CommonTest

class SimpleBoardViewTest extends CommonTest {
  "A SimpleBoardView" - {
    "should render an empty board" in {
      val boardViewModel = BoardViewModel.empty(3, "?")
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

  lazy val view = new SimpleBoardView
}

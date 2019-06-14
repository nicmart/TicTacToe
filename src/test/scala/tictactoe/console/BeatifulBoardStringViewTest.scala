package tictactoe.console

import tictactoe.domain.CommonTest
import tictactoe.stringpresenter.BoardStringViewModel
import tictactoe.stringview.BeautifulBoardStringView
import tictactoe.underware.SizedString

class BeatifulBoardStringViewTest extends CommonTest {
  "A BeautifulBoardView" - {
    "should render an empty board" in {
      val boardViewModel = BoardStringViewModel.empty(3, SizedString("?"))
      val renderedBoard = view.render(boardViewModel)
      val expectedRenderedBoard =
        """>┌───┬───┬───┐
           >│ ? │ ? │ ? │
           >├───┼───┼───┤
           >│ ? │ ? │ ? │
           >├───┼───┼───┤
           >│ ? │ ? │ ? │
           >└───┴───┴───┘"""
          .stripMargin('>')

      renderedBoard shouldBe expectedRenderedBoard
    }
  }

  lazy val view = new BeautifulBoardStringView(3)
}

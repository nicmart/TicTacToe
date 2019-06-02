package tictactoe.consolegameevents

import tictactoe.beautifulstringview.BeautifulBoardStringView
import tictactoe.domain.CommonTest
import tictactoe.stringpresenter.BoardStringViewModel

class BeatifulBoardStringViewTest extends CommonTest {
  "A BeautifulBoardView" - {
    "should render an empty board" in {
      val boardViewModel = BoardStringViewModel.empty(3, "?")
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

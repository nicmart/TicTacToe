package tictactoe.console

import tictactoe.stringpresenter.BoardStringViewModel

trait BoardStringView {
  def render(board: BoardStringViewModel): String
}

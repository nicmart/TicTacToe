package tictactoe.stringpresenter

import tictactoe.domain.game.model.Board

trait BoardStringView {
  def render(board: Board): String
}

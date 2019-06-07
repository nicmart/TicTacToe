package tictactoe.console

import tictactoe.stringpresenter.GameStringViewModel

trait GameRunStateStringView {
  def render(viewModel: GameStringViewModel): String
}

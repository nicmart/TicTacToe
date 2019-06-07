package tictactoe.console

import tictactoe.stringpresenter.GameStringViewModel

trait GameStringView {
  def render(viewModel: GameStringViewModel): String
}

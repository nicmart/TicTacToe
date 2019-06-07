package tictactoe.console

import tictactoe.stringpresenter.GameRunStateStringViewModel

trait GameRunStateStringView {
  def render(viewModel: GameRunStateStringViewModel): String
}

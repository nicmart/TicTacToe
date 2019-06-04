package tictactoe.consolegameevents

import tictactoe.stringpresenter.GameRunStateStringViewModel

trait GameRunStateStringView {
  def render(viewModel: GameRunStateStringViewModel): String
}

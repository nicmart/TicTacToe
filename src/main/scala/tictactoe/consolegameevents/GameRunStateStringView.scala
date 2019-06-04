package tictactoe.consolegameevents

import tictactoe.stringpresenter.GameRunStateViewModel

trait GameRunStateStringView {
  def render(viewModel: GameRunStateViewModel): String
}

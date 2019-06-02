package tictactoe.console

trait BoardView {
  def render(board: BoardViewModel): String
}

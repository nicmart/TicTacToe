package tictactoe.infrastructure.console

trait BoardView {
  def render(board: BoardViewModel): String
}

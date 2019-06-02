package tictactoe.view

import tictactoe.domain.game.model.Board

class BoardView {
  def renderBoard(board: Board): String = ???
}

case class BoardViewModel(size: Int, rows: String)

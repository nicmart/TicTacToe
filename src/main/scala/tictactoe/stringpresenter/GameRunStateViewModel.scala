package tictactoe.stringpresenter

sealed trait GameRunStateViewModel

object GameRunStateViewModel {
  case class Board(board: BoardStringViewModel, message: String) extends GameRunStateViewModel
  case class Message(string: String) extends GameRunStateViewModel
}
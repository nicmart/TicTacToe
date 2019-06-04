package tictactoe.stringpresenter

sealed trait GameRunStateStringViewModel

object GameRunStateStringViewModel {
  case class Board(board: BoardStringViewModel, message: String) extends GameRunStateStringViewModel
  case class Message(string: String) extends GameRunStateStringViewModel
}

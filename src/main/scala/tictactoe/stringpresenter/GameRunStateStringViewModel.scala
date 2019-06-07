package tictactoe.stringpresenter

import tictactoe.stringpresenter.GameRunStateStringViewModel.{Board, Message}

sealed trait GameRunStateStringViewModel {
  def setMessage(string: String): GameRunStateStringViewModel =
    this match {
      case Board(_, _) => Message(string)
      case Message(_)  => Message(string)
    }
}

object GameRunStateStringViewModel {
  case class Board(board: BoardStringViewModel, message: String) extends GameRunStateStringViewModel
  case class Message(string: String) extends GameRunStateStringViewModel
}

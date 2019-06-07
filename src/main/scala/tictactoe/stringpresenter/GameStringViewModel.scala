package tictactoe.stringpresenter

import tictactoe.stringpresenter.GameStringViewModel.{Board, Message}

sealed trait GameStringViewModel {
  def setMessage(string: String): GameStringViewModel =
    this match {
      case Board(_, _) => Message(string)
      case Message(_)  => Message(string)
    }
}

object GameStringViewModel {
  case class Board(header: Option[String], board: BoardStringViewModel) extends GameStringViewModel
  case class Message(string: String) extends GameStringViewModel
}

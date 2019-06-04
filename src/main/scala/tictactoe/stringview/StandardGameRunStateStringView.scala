package tictactoe.stringview

import tictactoe.consolegameevents.{BoardStringView, GameRunStateStringView}
import tictactoe.stringpresenter.GameRunStateStringViewModel

class StandardGameRunStateStringView(boardView: BoardStringView) extends GameRunStateStringView {
  override def render(runState: GameRunStateStringViewModel): String = runState match {
    case GameRunStateStringViewModel.Board(board, message) =>
      s"""
        |${boardView.render(board)}
        |
        |$message
      """.stripMargin
    case GameRunStateStringViewModel.Message(message) => message
  }
}

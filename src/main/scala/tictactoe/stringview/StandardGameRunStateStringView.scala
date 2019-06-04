package tictactoe.stringview

import tictactoe.consolegameevents.{BoardStringView, GameRunStateStringView}
import tictactoe.stringpresenter.GameRunStateViewModel

class StandardGameRunStateStringView(boardView: BoardStringView) extends GameRunStateStringView {
  override def render(runState: GameRunStateViewModel): String = runState match {
    case GameRunStateViewModel.Board(board, message) =>
      s"""
        |${boardView.render(board)}
        |
        |$message
      """.stripMargin
    case GameRunStateViewModel.Message(message) => message
  }
}

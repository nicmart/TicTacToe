package tictactoe.stringview

import tictactoe.console.{BoardStringView, GameRunStateStringView}
import tictactoe.stringpresenter.GameStringViewModel

class StandardGameRunStateStringView(boardView: BoardStringView) extends GameRunStateStringView {
  override def render(runState: GameStringViewModel): String = runState match {
    case GameStringViewModel.Board(board, message) =>
      s"""
        |${boardView.render(board)}
        |
        |$message
      """.stripMargin
    case GameStringViewModel.Message(message) => message
  }
}

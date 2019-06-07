package tictactoe.stringview

import tictactoe.console.{BoardStringView, GameStringView}
import tictactoe.stringpresenter.GameStringViewModel

class StandardGameStringView(boardView: BoardStringView) extends GameStringView {
  override def render(runState: GameStringViewModel): String = runState match {
    case GameStringViewModel.Board(Some(header), board) =>
      s"""
        |$header
        |
        |${boardView.render(board)}
      """.stripMargin
    case GameStringViewModel.Board(None, board) => boardView.render(board)
    case GameStringViewModel.Message(message)   => message
  }
}

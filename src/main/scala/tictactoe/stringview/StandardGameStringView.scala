package tictactoe.stringview

import tictactoe.console.GameStringView
import tictactoe.stringpresenter.GameStringViewModel
import tictactoe.underware.AnsiCodes.clearScreen

object StandardGameStringView extends GameStringView {
  override def render(runState: GameStringViewModel): String = runState match {
    case GameStringViewModel.GameScreen(header, board, messages) =>
      s"""
        |$clearScreen$header
        |
        |$board
        |
        |${messages.mkString("\n")}
      """.stripMargin

    case GameStringViewModel.NormalScreen(header, messages) =>
      s"""
         |$clearScreen$header
         |
         |${messages.mkString("\n")}
      """.stripMargin
  }
}

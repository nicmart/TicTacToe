package tictactoe.rudegamestrings

import tictactoe.consolegameevents.ConsoleGameStrings

object RudeConsoleGameStrings extends ConsoleGameStrings {

  override def invalidMove: String =
    "It looks like you don't even know the rules of the game. That was an invalid move, you moron."

  override def playerHasToChooseMove(player: String): String =
    s"$player, it's your turn, please move for God's sake!"

  override def gameEndedWithWinner(player: String): String =
    s"$player won, but just because he was lucky."

  override def gameEndedWithDraw: String =
    s"That was a very boring draw. Thanks for wasting my time."

  override def unexpectedState: String =
    "This case should never happen, I am giving up."

  override def gameIsAboutToStart: String =
    "Welcome to the most over-engineered TicTacToe ever!!!"

}

package tictactoe.stringpresenter

trait GameStrings {
  def invalidMove: String
  def playerHasToChooseMove(player: String): String
  def gameEndedWithWinner(player: String): String
  def gameEndedWithDraw: String
  def unexpectedState: String
  def gameIsAboutToStart: String
}

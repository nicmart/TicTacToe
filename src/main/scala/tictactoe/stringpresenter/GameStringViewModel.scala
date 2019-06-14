package tictactoe.stringpresenter

sealed trait GameStringViewModel {
  def header: String
  def messages: Seq[String]

  def withMessage(message: String): GameStringViewModel
  def cleanMessages: GameStringViewModel
  def withBoard(board: BoardStringViewModel): GameStringViewModel
}

object GameStringViewModel {
  case class GameScreen(header: String, board: BoardStringViewModel, messages: Vector[String])
      extends GameStringViewModel {
    override def withMessage(message: String): GameStringViewModel =
      copy(messages = messages :+ message)
    override def cleanMessages: GameStringViewModel = copy(messages = Vector.empty)
    override def withBoard(board: BoardStringViewModel): GameStringViewModel = copy(board = board)
  }

  case class NormalScreen(header: String, messages: Vector[String]) extends GameStringViewModel {
    override def withMessage(message: String): GameStringViewModel =
      copy(messages = messages :+ message)
    override def cleanMessages: GameStringViewModel = copy(messages = Vector.empty)
    override def withBoard(board: BoardStringViewModel): GameStringViewModel =
      GameScreen(header, board, messages)
  }
}

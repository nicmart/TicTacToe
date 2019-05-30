package tictactoe.domain.model

trait Game {
  def board: Board
  def state: GameState
  def currentPlayer: Mark
  def availableMoves: List[Board.Cell]
  def makeMove(cell: Board.Cell): Either[Error, StandardGame]

  final def size: Int = board.size.value
  final def inProgress: Boolean = state == GameState.InProgress
}

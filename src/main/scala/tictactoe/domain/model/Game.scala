package tictactoe.domain.model

sealed abstract case class Game(board: Board, currentMark: Mark) {
  def makeMove(cell: Board.Cell): Either[Error, Game] =
    for {
      newBoard <- board.withMark(currentMark, cell)
    } yield new Game(newBoard, currentMark.switch) {}
}

object Game {
  def newGame(size: Board.Size, currentPlayer: Mark): Game = new Game(Board.emptyBoard(size), currentPlayer) {}
}

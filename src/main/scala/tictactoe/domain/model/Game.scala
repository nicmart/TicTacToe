package tictactoe.domain.model

import tictactoe.domain.model.Board.Cell

sealed abstract case class Game(board: Board, currentMark: Mark, rules: Rules) {
  val size: Int = board.size.value
  val state: GameState = rules.state(this)
  val inProgress: Boolean = availableMoves.nonEmpty

  def availableMoves: List[Cell] = rules.availableMoves(this)

  def makeMove(cell: Board.Cell): Either[Error, Game] =
    for {
      _ <- Either.cond(inProgress, (), Error.GameHasAlreadyEnded)
      _ <- rules.checkIfLegalMove(this, cell)
      newBoard <- board.withMark(currentMark, cell)
    } yield new Game(newBoard, currentMark.switch, rules) {}
}

object Game {
  def newGame(size: Board.Size, currentPlayer: Mark, rules: Rules): Game =
    new Game(Board.emptyBoard(size), currentPlayer, rules) {}
}

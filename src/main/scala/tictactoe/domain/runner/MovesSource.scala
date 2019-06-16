package tictactoe.domain.runner

import tictactoe.domain.game.Game
import tictactoe.domain.game.model.Board.Cell
import tictactoe.domain.game.model.Error

trait MovesSource[F[_, _]] {
  def askMove(game: Game): F[Error, Cell]
}

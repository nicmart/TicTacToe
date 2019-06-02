package tictactoe.console

import tictactoe.console.BoardViewModel.Error.InvalidRows

sealed abstract case class BoardViewModel(size: Int, rows: List[List[String]])

object BoardViewModel {

  def apply(rows: List[List[String]]): Either[Error, BoardViewModel] =
    rows match {
      case Nil => Left(InvalidRows)
      case first :: otherRows if otherRows.forall(_.size == first.size) =>
        Right(new BoardViewModel(first.size, rows) {})
      case _ => Left(InvalidRows)
    }

  def empty(size: Int, emptySymbol: String): BoardViewModel =
    new BoardViewModel(size, List.fill(size, size)(emptySymbol)) {}

  sealed trait Error
  object Error {
    case object InvalidRows extends Error
  }
}

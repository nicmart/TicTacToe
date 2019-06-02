package tictactoe.stringpresenter

import tictactoe.stringpresenter.BoardStringViewModel.Error.InvalidRows

sealed abstract case class BoardStringViewModel(size: Int, rows: List[List[String]])

object BoardStringViewModel {

  def apply(rows: List[List[String]]): Either[Error, BoardStringViewModel] =
    rows match {
      case Nil => Left(InvalidRows)
      case first :: otherRows if otherRows.forall(_.size == first.size) =>
        Right(new BoardStringViewModel(first.size, rows) {})
      case _ => Left(InvalidRows)
    }

  def empty(size: Int, emptySymbol: String): BoardStringViewModel =
    new BoardStringViewModel(size, List.fill(size, size)(emptySymbol)) {}

  sealed trait Error
  object Error {
    case object InvalidRows extends Error
  }
}

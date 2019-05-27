package tictactoe.domain.model

sealed trait Error
object Error {
  case object InvalidXCoordinate extends Error
  case object InvalidYCoordinate extends Error
}

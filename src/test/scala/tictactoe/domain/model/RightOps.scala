package tictactoe.domain.model

trait RightOps {
  case class RightOnLeftException[E](error: E) extends Throwable {
    override def getMessage: String = error.toString
  }

  implicit class Ops[E, T](either: Either[E, T]) {
    def getRight: T = either.left.map(RightOnLeftException.apply).toTry.get
  }
}

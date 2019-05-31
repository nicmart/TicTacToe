package tictactoe.domain

trait EitherOps {
  case class RightOnLeftException[E](error: E) extends Throwable {
    override def getMessage: String = error.toString
  }

  case class LeftOnRightException[T](t: T) extends Throwable {
    override def getMessage: String = t.toString
  }

  implicit class Ops[E, T](either: Either[E, T]) {
    def getRight: T = either.left.map(RightOnLeftException.apply).toTry.get
    def getLeft: E = either.right.map(LeftOnRightException.apply).swap.toTry.get
  }
}

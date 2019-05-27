package tictactoe.domain.model

sealed trait Mark
object Mark {
  final case object X extends Mark
  final case object O extends Mark
}

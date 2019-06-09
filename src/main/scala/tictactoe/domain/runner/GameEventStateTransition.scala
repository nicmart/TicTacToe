package tictactoe.domain.runner

trait GameEventStateTransition[S] {
  def receive(state: S, event: GameEvent): S
}

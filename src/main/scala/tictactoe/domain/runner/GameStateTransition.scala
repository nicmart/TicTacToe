package tictactoe.domain.runner

trait GameStateTransition[S] {
  def receive(state: S, event: GameEvent): S
}

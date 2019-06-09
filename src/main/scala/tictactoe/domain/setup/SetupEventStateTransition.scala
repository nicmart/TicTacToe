package tictactoe.domain.setup

trait SetupEventStateTransition[S] {
  def receive(state: S, event: SetupEvent): S
}

package tictactoe.console

import tictactoe.stringpresenter.GameStrings
import tictactoe.underware.SizedString

case class ConsoleGameConfig(
    player1Mark: SizedString,
    player2Mark: SizedString,
    emptyCell: String => SizedString,
    strings: GameStrings
)

package tictactoe.underware

// See http://www.lihaoyi.com/post/BuildyourownCommandLinewithANSIescapecodes.html for a complete
// overview on this stuff
object AnsiCodes {
  val black = "\u001b[30m"
  val red = "\u001b[31m"
  val green = "\u001b[32m"
  val yellow = "\u001b[33m"
  val blue = "\u001b[34m"
  val magenta = "\u001b[35m"
  val cyan = "\u001b[36m"
  val white = "\u001b[37m"
  val brightBlack = "\u001b[30;1m"
  val brightRed = "\u001b[31;1m"
  val brightGreen = "\u001b[32;1m"
  val brightYellow = "\u001b[33;1m"
  val brightBlue = "\u001b[34;1m"
  val brightMagenta = "\u001b[35;1m"
  val brightCyan = "\u001b[36;1m"
  val brightWhite = "\u001b[37;1m"
  val gray = color(240)

  def color(n: Int): String =
    s"\u001b[38;5;${n}m"

  val reset = "\u001b[0m"

  def coloriseString(colorCode: String)(string: String): SizedString =
    SizedString(colorCode + string + reset, string.length)
}

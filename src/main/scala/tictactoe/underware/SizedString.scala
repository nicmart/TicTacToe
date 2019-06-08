package tictactoe.underware

final case class SizedString(string: String, size: Int) {
  def center(lineSize: Int, char: Char = ' '): String = {
    val remainingChars = lineSize - size
    val half = remainingChars / 2
    val spaces = char.toString * half
    val centered = spaces + string + spaces

    if (remainingChars % 2 == 0) centered else char + centered
  }
}

object SizedString {
  def apply(string: String): SizedString = SizedString(string, string.length)
}

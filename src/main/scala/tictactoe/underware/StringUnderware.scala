package tictactoe.underware

import java.text.BreakIterator

object StringUnderware {
  implicit class StringOps(string: String) {
    // see https://stackoverflow.com/questions/6828076/how-to-correctly-compute-the-length-of-a-string-in-java
    def unicodeLength(locale: java.util.Locale = java.util.Locale.ENGLISH): Int = {
      val charIterator = java.text.BreakIterator.getCharacterInstance(locale)
      charIterator.setText(string)

      var result = 0
      while (charIterator.next() != BreakIterator.DONE) result += 1
      result
    }

    def center(size: Int, char: Char = ' '): String = {
      val remainingChars = size - string.unicodeLength()
      val half = remainingChars / 2
      val spaces = char.toString * half
      val centered = spaces + string + spaces

      if (remainingChars % 2 == 0) centered else char + centered
    }
  }
}

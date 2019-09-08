package eu.ankudinov.urlshortener.numeric

object NumericEncoding {
  private val alfanumericCharacters =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!*'()=+$,~[]^_.".toArray.distinct
  private val base = alfanumericCharacters.length

  def encode(n: Long): String = {
    val sign = if (n < 0) "-" else ""

    val abs = math.abs(n)
    sign + (if (abs < base) {
              alfanumericCharacters(abs.toInt).toString
            } else {
              val remainder = (abs % base).toInt
              encode(abs / base) + alfanumericCharacters(remainder)
            })
  }

  def convertBack(s: String): Long =
    s.headOption.map {
      case '-' => -1 * convertBack(s.tail)
      case a   => alfanumericCharacters.indexOf(a).toLong * math.pow(base.toDouble, s.tail.length.toDouble).toLong + convertBack(s.tail)
    }.getOrElse(0)
}

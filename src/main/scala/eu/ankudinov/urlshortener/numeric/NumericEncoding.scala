package eu.ankudinov.urlshortener.numeric

/**
  The helper object providing the methods to encode and decode Long numbers
  into strings using the predefined alphabet.
  */
object NumericEncoding {

  val alphabet =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!*'()=+$,~[]^_-.".toArray.distinct

  private val base = alphabet.length

  /**
    * Encodes the absolute value of a Long number into a String using the [[NumericEncoding.alphabet]]
    */
  def encode(n: Long): String = {
    val abs = math.abs(n)
    if (abs < base) {
      alphabet(abs.toInt).toString
    } else {
      val remainder = (abs % base).toInt
      encode(abs / base) + alphabet(remainder)
    }
  }

  /**
    * Decodes the String acquired through [[NumericEncoding.encode]] back into a Long value.
    */
  def decode(s: String): Long =
    s.headOption.map { a =>
      alphabet.indexOf(a).toLong * math.pow(base.toDouble, s.tail.length.toDouble).toLong + decode(s.tail)
    }.getOrElse(0)
}

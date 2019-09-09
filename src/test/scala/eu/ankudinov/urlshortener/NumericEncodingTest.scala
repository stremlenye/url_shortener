package eu.ankudinov.urlshortener

import eu.ankudinov.urlshortener.numeric.NumericEncoding
import org.scalatest.FunSuite

class NumericEncodingTest extends FunSuite {
  test("should encode long number into a string and decode it back") {
    val x = 100L
    assert(NumericEncoding.decode(NumericEncoding.encode(x)) == x)
  }

  test("should encode Long.MaxValue") {
    assert(NumericEncoding.decode(NumericEncoding.encode(Long.MaxValue)) == Long.MaxValue)
  }

  test("Top boundary for long value which is encodable into string of size 7") {
    val maximumEncodedNumber = Stream.continually(NumericEncoding.alphabet.last).take(7).mkString
    assert(NumericEncoding.decode(maximumEncodedNumber) == 17565568854911L)
    assert(NumericEncoding.encode(17565568854911L) == maximumEncodedNumber)
  }
}

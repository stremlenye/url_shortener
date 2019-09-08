package eu.ankudinov.urlshortener

import eu.ankudinov.urlshortener.numeric.NumericEncoding
import org.scalatest.FunSuite

class NumericEncodingTest extends FunSuite {
  test("should work") {
    val x = 100L
    assert(NumericEncoding.convertBack(NumericEncoding.encode(x)) == x)
  }

  test("should work for negative numbers") {
    val x = -100L
    assert(NumericEncoding.convertBack(NumericEncoding.encode(x)) == x)
  }

  test("top boundary for long value which is encodable into string of size 7") {
    assert(NumericEncoding.convertBack("......") == 208422380088L)
  }
}

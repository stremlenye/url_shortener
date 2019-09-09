package eu.ankudinov.urlshortener.services

import cats.instances.either._
import eu.ankudinov.urlshortener.services.IdGen.IdGenException
import org.scalacheck.{Gen, Properties}
import org.scalacheck.Prop.forAll

class IdGenTest extends Properties("IdGen") {
  val generator = new IdGen[Either[IdGenException, *]]('A')

  val gen = Gen.oneOf(Stream.continually(generator.getNext.right.get.toString.length).take(100))

  property("IdGen should generate ids of length less then 8 characters") = forAll(gen)(_ <= 8)
}

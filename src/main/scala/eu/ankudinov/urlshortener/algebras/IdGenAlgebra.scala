package eu.ankudinov.urlshortener.algebras

import eu.ankudinov.urlshortener.domain.Id

import cats.tagless.FunctorK
import cats.tagless.Derive

trait IdGenAlgebra[F[_]] {
  def getNext: F[Id]
}

object IdGenAlgebra {
  implicit val functorK: FunctorK[IdGenAlgebra] = Derive.functorK
}

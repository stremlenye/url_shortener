package eu.ankudinov.urlshortener.algebras

import cats.tagless.FunctorK
import cats.tagless.Derive
import eu.ankudinov.urlshortener.domain.{Id, UrlEntity}

trait UrlRepositoryAlgebra[F[_]] {
  def fetch(id: Id): F[Option[UrlEntity]]
  def put(urlEntity: UrlEntity): F[UrlEntity]
}

object UrlRepositoryAlgebra {
  implicit val functorK: FunctorK[UrlRepositoryAlgebra] = Derive.functorK
}

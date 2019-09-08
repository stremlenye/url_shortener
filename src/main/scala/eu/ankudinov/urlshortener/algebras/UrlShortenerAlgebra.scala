package eu.ankudinov.urlshortener.algebras

import cats.tagless.FunctorK
import cats.tagless.Derive
import eu.ankudinov.urlshortener.domain.{Id, ShortUrl, UrlCreate}
import io.lemonlabs.uri.AbsoluteUrl

trait UrlShortenerAlgebra[F[_]] {
  def shortenUrl(payload: UrlCreate): F[ShortUrl]
  def fetchFullUrl(id: Id): F[AbsoluteUrl]
}

object UrlShortenerAlgebra {
  implicit val functorK: FunctorK[UrlShortenerAlgebra] = Derive.functorK
}

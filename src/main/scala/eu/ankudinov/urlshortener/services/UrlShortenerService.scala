package eu.ankudinov.urlshortener.services

import java.time.LocalDateTime

import cats.{MonadError, Show}
import eu.ankudinov.urlshortener.algebras.{IdGenAlgebra, LazyLoggerAlgebra, UrlRepositoryAlgebra, UrlShortenerAlgebra}
import eu.ankudinov.urlshortener.domain.{Id, UrlCreate, UrlEntity}
import eu.ankudinov.urlshortener.domain.types.ShortUrl
import eu.ankudinov.urlshortener.services.UrlShortenerService.{FullUrlNotFound, UrlShortenerServiceException}
import io.lemonlabs.uri.AbsoluteUrl
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.show._
import cats.syntax.option._
import eu.ankudinov.urlshortener.config.Config

class UrlShortenerService[F[_]](
  idGen: IdGenAlgebra[F],
  urlRepository: UrlRepositoryAlgebra[F],
  logger: LazyLoggerAlgebra[F],
  config: Config
)(
  implicit F: MonadError[F, UrlShortenerServiceException]
) extends UrlShortenerAlgebra[F] {

  def shortenUrl(payload: UrlCreate): F[ShortUrl] =
    for {
      id <- idGen.getNext
      _ <- urlRepository.put(UrlEntity(id, payload.url, LocalDateTime.now()))
      _ <- logger.info(show"Created new short url with id $id")
    } yield ShortUrl.from(config.selfUrl, id)

  def fetchFullUrl(id: Id): F[AbsoluteUrl] =
    for {
      _ <- logger.info(show"Fetching full url for id $id")
      entity <- urlRepository.fetch(id).flatMap(_.liftTo[F](FullUrlNotFound(id)))
    } yield entity.url
}

object UrlShortenerService {

  def apply[F[_] : MonadError[*[_], UrlShortenerServiceException]](
    idGen: IdGenAlgebra[F],
    urlRepository: UrlRepositoryAlgebra[F],
    logger: LazyLoggerAlgebra[F],
    config: Config
  ): UrlShortenerAlgebra[F] = new UrlShortenerService(idGen, urlRepository, logger, config)

  sealed abstract class UrlShortenerServiceException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
    def this(message: String) = this(message, null: Throwable)
    def this(cause: Throwable) = this(cause.getMessage, cause)
    def this(message: String, cause: Option[Throwable]) = this(message, cause.orNull)
  }

  object UrlShortenerServiceException {
    implicit val show: Show[UrlShortenerServiceException] = Show.fromToString
  }

  final case class FullUrlNotFound(id: Id) extends UrlShortenerServiceException(show"Full url with id $id not found")
  final case class UnderlyingServiceException(cause: Throwable) extends UrlShortenerServiceException("Underlying service failure", cause)
}

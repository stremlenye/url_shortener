package eu.ankudinov.urlshortener.apis

import cats.data.{EitherT, Kleisli}
import cats.effect.IO
import cats.{MonadError, ~>}
import doobie.util.transactor.Transactor
import eu.ankudinov.urlshortener.algebras.{LazyLoggerAlgebra, UrlRepositoryAlgebra, UrlShortenerAlgebra}
import eu.ankudinov.urlshortener.common.{LiftError, PersistenceException, executors}
import eu.ankudinov.urlshortener.config.Config
import eu.ankudinov.urlshortener.domain.{Id, UrlCreate}
import eu.ankudinov.urlshortener.http._
import eu.ankudinov.urlshortener.logging.PrintlnLogger
import eu.ankudinov.urlshortener.persistence.UrlRepository
import eu.ankudinov.urlshortener.services.UrlShortenerService.{FullUrlNotFound, UnderlyingServiceException}
import eu.ankudinov.urlshortener.services.{IdGen, UrlShortenerService}
import eu.ankudinov.urlshortener.common.conversions._
import eu.ankudinov.urlshortener.common.future._
import io.finch._
import io.finch.circe._
import io.finch.syntax._
import cats.syntax.apply._
import cats.tagless.syntax.functorK._
import com.twitter.finagle.http.Status
import eu.ankudinov.urlshortener.services.IdGen.IdGenException

class UrlShortenerApi(service: UrlShortenerAlgebra[HttpHandler]) {
  val root = "api"
  val shortenUrlEndpoint = post(jsonBody[UrlCreate])(service.shortenUrl _)

  val redirectToFullUrlEndpoint =
    get(path[Id])(service.fetchFullUrl(_: Id).map(_.flatMap(a => Output.unit(Status.SeeOther).withHeader("Location" -> a.toString()))))

  val api = shortenUrlEndpoint :+: redirectToFullUrlEndpoint
}

object UrlShortenerApi {
  import UrlShortenerService.UrlShortenerServiceException
  type ServiceContext[A] = EitherT[IO, UrlShortenerServiceException, A]

  implicit val liftPersistenceException: LiftError[PersistenceException, UrlShortenerServiceException] = UnderlyingServiceException(_)
  implicit val liftIdGenException: LiftError[IdGenException, UrlShortenerServiceException] = UnderlyingServiceException(_)

  def apply(config: Config): UrlShortenerApi = {

    val logger: LazyLoggerAlgebra[ServiceContext] = new PrintlnLogger[ServiceContext]

    def errorHandler: UrlShortenerServiceException => Output[Nothing] = {
      case e: FullUrlNotFound => NotFound(e)
      case other              => InternalServerError(other)
    }

    val globalContextShift = IO.contextShift(executors.globalEc)

    val toHandler: ServiceContext ~> HttpHandler =
      λ[ServiceContext ~> HttpHandler] { service =>
        Kleisli.liftF(service.recoverWith {
          case e => logger.error(e) *> MonadError[ServiceContext, UrlShortenerServiceException].raiseError(e)
        }.fold(errorHandler, Ok).forkAsTwitter(globalContextShift))
      }

    val service: UrlShortenerAlgebra[HttpHandler] = {
      implicit val ioContextShift = IO.contextShift(executors.blockingIoEc)
      implicit val transactor = Transactor.fromDriverManager[IO](
        config.postgres.driverClassName,
        config.postgres.url,
        config.postgres.user,
        config.postgres.password
      )
      val idGen = IdGen[ServiceContext, UrlShortenerServiceException](config.nodePrefix)
      val repository: UrlRepositoryAlgebra[ServiceContext] = UrlRepository.transactional[IO, UrlShortenerServiceException]
      UrlShortenerService[ServiceContext](idGen, repository, logger, config).mapK(toHandler)
    }

    new UrlShortenerApi(service)
  }
}

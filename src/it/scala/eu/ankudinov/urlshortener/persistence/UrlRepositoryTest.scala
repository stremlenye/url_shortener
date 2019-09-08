package eu.ankudinov.urlshortener.persistence

import java.time.LocalDateTime

import cats.effect.IO
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import eu.ankudinov.urlshortener.common.executors
import eu.ankudinov.urlshortener.config.Config
import eu.ankudinov.urlshortener.domain.UrlEntity
import eu.ankudinov.urlshortener.domain.types.Id
import io.lemonlabs.uri.AbsoluteUrl
import org.scalatest.{FunSuite, Matchers}
import pureconfig.loadConfig

class UrlRepositoryTest extends FunSuite with Matchers with IOChecker {
  implicit val contextShift = IO.contextShift(executors.blockingIoEc)

  val config: Config = loadConfig[Config].fold(
    f => {
      println(f)
      throw new RuntimeException()
    },
    identity
  )

  val transactor = Transactor.fromDriverManager[IO](
    config.postgres.driverClassName,
    config.postgres.url,
    config.postgres.user,
    config.postgres.password
  )

  val dummy = UrlEntity(Id("testtest"), AbsoluteUrl.parse("http://example.com/a/b/c"), LocalDateTime.now())

  test("insert")(check(UrlRepository.queries.insert(dummy)))
  test("fetch")(check(UrlRepository.queries.fetch(dummy.id)))
}

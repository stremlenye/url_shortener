package eu.ankudinov.urlshortener.services

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import eu.ankudinov.urlshortener.apis.UrlShortenerApi
import eu.ankudinov.urlshortener.config.Config
import eu.ankudinov.urlshortener.domain.UrlCreate
import eu.ankudinov.urlshortener.domain.types.ShortUrl
import io.circe.parser._
import io.circe.syntax._
import io.finch._
import io.finch.circe._
import io.finch.test._
import io.lemonlabs.uri.AbsoluteUrl
import org.scalatest.{Matchers, fixture}
import pureconfig.loadConfig

class UrlShortenerApiTest extends fixture.FunSuite with Matchers with ServiceSuite {
  override def createService(): Service[Request, Response] =
    loadConfig[Config]
      .map(UrlShortenerApi(_).api.toService).getOrElse(failure => sys.error(s"Failed loading configuration. Cause: $failure"))

  test("Service should return 404 for non existing url") { service =>
    assert(service(Input.get(s"/nonexistentid").request).status == Status.NotFound)
  }

  test("Service should return 400 for malformed request") { service =>
    val payload = s""" { "url": "::::::"  } """.asJson
    val response = service(Input.post("/").withBody[Application.Json](payload).request)
    assert(response.status == Status.BadRequest)
  }

  test("Service should return short url on well-formatted request which redirects to the original") { service =>
    val originalUrl = "http://example.com/a/b/c"
    val payload = UrlCreate(AbsoluteUrl.parse(originalUrl)).asJson
    val response = service(Input.post("/").withBody[Application.Json](payload).request)
    assert(response.status == Status.Ok)
    val shortUrl = decode[ShortUrl](response.contentString).toOption
    assert(shortUrl.isDefined)
    val redirectResponse = service(Input.get(shortUrl.map(_.url.path.toString()).get).request)
    assert(redirectResponse.status == Status.SeeOther)
    assert(redirectResponse.location.contains(originalUrl))
  }
}

package eu.ankudinov.urlshortener

import eu.ankudinov.urlshortener.apis.UrlShortenerApi
import com.twitter.finagle.Http
import com.twitter.util.Await
import eu.ankudinov.urlshortener.config.Config
import io.finch.circe._
import pureconfig.loadConfig

object Server extends App {

  loadConfig[Config].fold(
    failure => sys.error(s"Failed loading configuration. Cause: $failure"),
    (config: Config) => {
      val urlShortenerApi = UrlShortenerApi(config)
      Await.ready(Http.server.serve(":8081", urlShortenerApi.api.toService))
      ()
    }
  )
}

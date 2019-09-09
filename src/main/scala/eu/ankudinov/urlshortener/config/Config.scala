package eu.ankudinov.urlshortener.config

import io.lemonlabs.uri.AbsoluteUrl
import pureconfig.ConfigReader
import pureconfig.generic.semiauto._
import cats.syntax.either._
import pureconfig.error.ExceptionThrown

/**
* The application configuration
 * @param selfUrl Url of the API used to build the short urls
 * @param nodePrefix The node specific key prefix used to ensure keys uniqueness across nodes.
 * @param postgres Database configuration
 */
final case class Config(selfUrl: AbsoluteUrl, nodePrefix: Char, postgres: PostgresConfig)

object Config {
  implicit val absoluteUrlConfigReader: ConfigReader[AbsoluteUrl] =
    ConfigReader[String].emap(AbsoluteUrl.parseTry(_).toEither.leftMap(ExceptionThrown.apply))

  implicit val configReader: ConfigReader[Config] = deriveReader
}

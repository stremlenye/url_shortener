package eu.ankudinov.urlshortener.config

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

final case class PostgresConfig(driverClassName: String, url: String, user: String, password: String)

object PostgresConfig {
  implicit val configReader: ConfigReader[PostgresConfig] = deriveReader
}

package eu.ankudinov.urlshortener.domain

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import io.lemonlabs.uri.AbsoluteUrl
import eu.ankudinov.urlshortener.common.codecs._

/** User request payload model */
final case class UrlCreate (url: AbsoluteUrl)

object UrlCreate {
  implicit val decoder: Decoder[UrlCreate] = deriveDecoder
  implicit val encoder: Encoder[UrlCreate] = deriveEncoder
}

package eu.ankudinov.urlshortener.common

import io.circe.{Decoder, Encoder}
import io.lemonlabs.uri.AbsoluteUrl

object codecs {
  implicit val absoluteUrlEncoder: Encoder[AbsoluteUrl] = Encoder[String].contramap[AbsoluteUrl](_.toString())
  implicit val absoluteUrlDecoder: Decoder[AbsoluteUrl] = Decoder[String].map(AbsoluteUrl.parse(_))
}

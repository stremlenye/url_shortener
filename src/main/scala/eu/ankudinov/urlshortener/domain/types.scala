package eu.ankudinov.urlshortener.domain

import eu.ankudinov.urlshortener.common.codecs._
import cats.Show
import doobie.util.Meta
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import io.finch.DecodePath
import io.lemonlabs.uri.AbsoluteUrl
import io.lemonlabs.uri.typesafe.PathPart
import io.lemonlabs.uri.typesafe.dsl._

import scala.reflect.ClassTag

object types {
  @newtype case class Id(value: String)

  object Id {
    implicit val pathPart: PathPart[Id] = _.value
    implicit val show: Show[Id] = _.value
    implicit val decodePath: DecodePath[Id] = a => Option(a).map(Id.apply)
    implicit val meta: Meta[Id] = Meta[String].imap(Id.apply)(_.value)
    implicit val classTag: ClassTag[Id] = implicitly[ClassTag[String]].coerce
  }

  @newtype case class ShortUrl(url: AbsoluteUrl)

  object ShortUrl {
    implicit val encoder: Encoder[ShortUrl] = Encoder[AbsoluteUrl].contramap[ShortUrl](_.url)
    implicit val decoder: Decoder[ShortUrl] = Decoder[AbsoluteUrl].map[ShortUrl](apply)

    def from(baseUrl: AbsoluteUrl, id: Id): ShortUrl = ShortUrl((baseUrl / id).toAbsoluteUrl)
  }
}

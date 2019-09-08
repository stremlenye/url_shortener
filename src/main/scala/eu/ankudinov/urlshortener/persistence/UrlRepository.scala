package eu.ankudinov.urlshortener.persistence

import eu.ankudinov.urlshortener.algebras.UrlRepositoryAlgebra
import eu.ankudinov.urlshortener.common.{LiftError, PersistenceException}
import eu.ankudinov.urlshortener.domain.{Id, UrlEntity}
import eu.ankudinov.urlshortener.domain.types.Id._

import eu.ankudinov.urlshortener.util.transformations
import io.lemonlabs.uri.AbsoluteUrl

import java.sql.Timestamp
import java.time.{Clock, LocalDateTime, ZoneId}

import cats.tagless.syntax.functorK._
import cats.data.EitherT
import cats.effect.Bracket
import cats.instances.list._
import cats.syntax.foldable._
import doobie._
import doobie.implicits._


class UrlRepository extends UrlRepositoryAlgebra[ConnectionIO] {
  import UrlRepository.queries

  def fetch(id: Id): ConnectionIO[Option[UrlEntity]] =
    queries.fetch(id).option

  def put(urlEntity: UrlEntity): ConnectionIO[UrlEntity] =
    queries.insert(urlEntity).run.map(_ => urlEntity)
}

object UrlRepository {
  private[this] val clock = Clock.systemUTC()
  private[this] val utc: ZoneId = clock.getZone

  implicit val absoluteUrlMeta = Meta[String].imap(AbsoluteUrl.parse(_))(_.toString())
  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].timap(ts => LocalDateTime.ofInstant(ts.toInstant, utc))(Timestamp.valueOf)

  object queries {
    val table = fr"url"
    val fields = Seq("id", "url", "createdAt")
    val columns = fields.toList.map(Fragment.const(_)).intercalate(fr",")
    val keys = fields.toList.map(Fragment.const(_)).foldSmash(fr"(", fr",", fr")")

    def fetch(id: Id): Query0[UrlEntity] =
      (fr"select" ++ columns ++ fr"from" ++ table ++ fr"where id = $id").query[UrlEntity]

    def insert(urlEntity: UrlEntity): Update0 = {
      import urlEntity._
      (fr"insert into " ++ table ++ fr"values ($id, $url, $createdAt)").update
    }
  }

  def transactional[F[_], E <: Throwable](
    implicit F: Bracket[F, Throwable],
    transactor: Transactor[F],
    liftError: LiftError[PersistenceException, E]
  ): UrlRepositoryAlgebra[EitherT[F, E, *]] =
    (new UrlRepository).mapK[EitherT[F, E, *]](transformations.transactionalFallible[F, E])

}

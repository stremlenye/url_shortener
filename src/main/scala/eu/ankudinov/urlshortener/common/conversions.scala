package eu.ankudinov.urlshortener.common

import cats.effect.{ContextShift, IO}
import com.twitter.util.{Future, Promise}
import cats.syntax.apply._
import com.twitter.finagle.http._
import io.lemonlabs.uri.AbsoluteUrl

object conversions {
  implicit class IoOps[A] private[conversions] (val fa: IO[A]) extends AnyVal {

    def forkAsTwitter(cs: ContextShift[IO]): Future[A] = {
      val promise = new Promise[A]()
      (cs.shift *> fa).runAsync {
        case Left(e)  => IO.pure(promise.setException(e))
        case Right(a) => IO.pure(promise.setValue(a))
      }.unsafeRunSync
      promise
    }
  }

  implicit class AbsoluteUrlOps private[conversions] (val a: AbsoluteUrl) extends AnyVal {

    def toRedirectResponse: Response = {
      val response = Response(Status.MovedPermanently)
      response.location = a.toString()
      response
    }
  }
}

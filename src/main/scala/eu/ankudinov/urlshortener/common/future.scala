package eu.ankudinov.urlshortener.common

import cats.Functor
import com.twitter.util.Future

object future {
  implicit val twitterFutureFunctor: Functor[Future] = new Functor[Future] {
    override def map[A, B](fa : Future[A])(f : A => B) : Future[B] = fa.map(f)
  }
}

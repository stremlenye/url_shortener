package eu.ankudinov.urlshortener.util

import cats.data.EitherT
import cats.effect.Bracket
import cats.{Functor, MonadError, ~>}
import doobie._
import doobie.util.transactor.Transactor
import doobie.implicits._
import eu.ankudinov.urlshortener.common.{LiftError, PersistenceException}

object transformations {

  def transactionalFallible[F[_], E <: Throwable](
    implicit F: Bracket[F, Throwable],
    transactor: Transactor[F],
    liftError: LiftError[PersistenceException, E]
  ): ConnectionIO ~> EitherT[F, E, *] =
    λ[ConnectionIO ~> EitherT[F, E, *]] { op =>
      EitherT(F.attempt(op.transact(transactor))).leftMap(e => liftError(PersistenceException.handleException(op, e)))
    }

  def errorLifting[F[_] : Functor, E, X](implicit liftError: LiftError[E, X]): EitherT[F, E, *] ~> EitherT[F, X, *] =
    λ[EitherT[F, E, *] ~> EitherT[F, X, *]](_.leftMap(liftError.apply))

  def raising[F[_], E](implicit F: MonadError[F, _ >: E]): EitherT[F, E, *] ~> F =
    λ[EitherT[F, E, *] ~> F](_.foldF(e => F.raiseError(e), a => F.pure(a)))
}

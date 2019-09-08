package eu.ankudinov.urlshortener.services

import java.nio.ByteBuffer
import java.security.SecureRandom

import cats.data.EitherT
import cats.{Functor, MonadError}
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import eu.ankudinov.urlshortener.algebras.IdGenAlgebra
import eu.ankudinov.urlshortener.common.LiftError
import eu.ankudinov.urlshortener.util.transformations._
import eu.ankudinov.urlshortener.domain.Id
import eu.ankudinov.urlshortener.numeric.NumericEncoding
import eu.ankudinov.urlshortener.services.IdGen.{EncodingFailure, FailedGenerateNonNegativeNumber, IdGenException, NumberGenerationFailure}

class IdGen[F[_]](prefix: Char)(implicit F: MonadError[F, IdGenException]) extends IdGenAlgebra[F] {
  private val secureRandom = new SecureRandom()

  def getNext: F[Id] =
    for {
      key <- generateNonNegativeNumber
      value <- F.fromEither(encode(key))
    } yield Id((prefix + value))

  private def generateNonNegativeNumber: F[Long] =
    F.tailRecM(FailedGenerateNonNegativeNumber: IdGenException)(
      _ =>
        F.pure(generateNumber).map(_.map(adjust)).map(_.ensure(FailedGenerateNonNegativeNumber)(a => a >= 0 && a <= IdGen.max))
    )

  private def generateNumber: Either[NumberGenerationFailure, Long] =
    Either.catchNonFatal(ByteBuffer.wrap(secureRandom.generateSeed(10)).getLong).leftMap(NumberGenerationFailure.apply)

  private def encode(key: Long): Either[EncodingFailure, String] =
    Either.catchNonFatal(NumericEncoding.encode(key)).leftMap(EncodingFailure.apply)

  private def adjust(a: Long): Long = math.abs {
    if (a > IdGen.max) {
      val x = a.toDouble / IdGen.adjuster
      ((x - x.toLong) * IdGen.adjuster / 100).toLong
    } else a
  }
}

object IdGen {

  val max = 208422380088L
  val adjuster = 100000000000L

  def apply[F[_] : Functor, E](prefix: Char)(implicit liftError: LiftError[IdGenException, E], F: MonadError[F, E]): IdGenAlgebra[F] = {
    type G[A] = EitherT[F, IdGenException, A]
    IdGenAlgebra.functorK.mapK(new IdGen[G](prefix))(errorLifting[F, IdGenException, E] andThen raising)
  }

  sealed abstract class IdGenException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
    def this(message: String) = this(message, null: Throwable)
    def this(cause: Throwable) = this(cause.getMessage, cause)
    def this(message: String, cause: Option[Throwable]) = this(message, cause.orNull)
  }

  final case class NumberGenerationFailure(cause: Throwable) extends IdGenException("Number generation failure", cause)
  final case class EncodingFailure(cause: Throwable) extends IdGenException("Encoding failure", cause)
  final case object FailedGenerateNonNegativeNumber extends IdGenException("Failed to generate non negative number")
}

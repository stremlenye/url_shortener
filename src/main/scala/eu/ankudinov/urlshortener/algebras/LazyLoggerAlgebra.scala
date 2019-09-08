package eu.ankudinov.urlshortener.algebras

import cats.Show
import cats.syntax.show._

trait LazyLoggerAlgebra[F[_]] {
  def trace(msg : => String) : F[Unit]

  def trace[A : Show](msg : A) : F[Unit] = trace(msg.show)

  def info(msg : => String) : F[Unit]

  def info[A : Show](msg : A) : F[Unit] = info(msg.show)

  def warn(msg : => String) : F[Unit]

  def warn[A : Show](msg : A) : F[Unit] = warn(msg.show)

  def error(msg : => String) : F[Unit]

  def error[A : Show](msg : A) : F[Unit] = error(msg.show)

  def error(msg : => String, cause : Throwable) : F[Unit]

  def error[A : Show](msg : A, cause : Throwable) : F[Unit] = error(msg.show, cause)
}

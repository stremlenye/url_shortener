package eu.ankudinov.urlshortener.common

import java.sql.SQLException

import doobie.free.connection.ConnectionIO
import doobie.util.invariant._

abstract class PersistenceException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
  def this(message: String) = this(message, null: Throwable)
  def this(cause: Throwable) = this(cause.getMessage, cause)
  def this(message: String, cause: Option[Throwable]) = this(message, cause.orNull)
}

final case class GeneralStoreException(message: String, cause: Option[Throwable]) extends PersistenceException(message, cause)

final case class EntityNotFoundException(message: String) extends PersistenceException(message, Option.empty)

final case class DataIntegrityViolationException(message: String, cause: Option[Throwable]) extends PersistenceException(message, cause)

final case class StaleDataException(message: String, cause: Option[Throwable]) extends PersistenceException(message, cause)

final case class UniqueConstraintViolationException(message: String, cause: Option[Throwable]) extends PersistenceException(message, cause)

object PersistenceException {
  def handleException(op: ConnectionIO[_], ex: Throwable): PersistenceException = ex match {
    case sqlEx: SQLException =>
      sqlEx.getSQLState match {
        case "2F002" =>
          StaleDataException(sqlEx.getMessage, Some(sqlEx))
        case "23505" =>
          UniqueConstraintViolationException(sqlEx.getMessage, Some(sqlEx))
        case code if code.startsWith("23") =>
          DataIntegrityViolationException(s"sqlState= ${sqlEx.getSQLState}, errorCode = ${sqlEx.getErrorCode}", Some(sqlEx))
        case _ =>
          GeneralStoreException(s"sqlState= ${sqlEx.getSQLState}, errorCode = ${sqlEx.getErrorCode}", Some(sqlEx))
      }

    case nnp: NonNullableParameter =>
      DataIntegrityViolationException(nnp.getMessage, Some(nnp))
    case _ =>
      GeneralStoreException(s"Failed to perform $op because of $ex}", Some(ex))
  }
}

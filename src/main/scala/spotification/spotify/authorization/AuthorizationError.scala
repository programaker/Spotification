package spotification.spotify.authorization

import spotification.NonBlankString

sealed abstract class AuthorizationError extends Exception with Product with Serializable

object AuthorizationError {
  final case class ApiError(error: NonBlankString, state: Option[NonBlankString]) extends AuthorizationError
  final case class InternalError(cause: Throwable) extends AuthorizationError
}

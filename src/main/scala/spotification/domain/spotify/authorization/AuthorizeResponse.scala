package spotification.domain.spotify.authorization

import spotification.domain.NonBlankString

sealed abstract class AuthorizeResponse extends Product with Serializable

object AuthorizeResponse {
  final case class Success(code: NonBlankString, state: Option[NonBlankString]) extends AuthorizeResponse
  final case class Failure(error: NonBlankString, state: Option[NonBlankString]) extends AuthorizeResponse
}

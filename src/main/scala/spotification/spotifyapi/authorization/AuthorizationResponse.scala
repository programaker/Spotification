package spotification.spotifyapi.authorization

import spotification.spotifyapi.NonBlankString

sealed abstract class AuthorizationResponse extends Product with Serializable

object AuthorizationResponse {
  final case class Success(code: NonBlankString, state: Option[NonBlankString]) extends AuthorizationResponse
  final case class Failure(error: NonBlankString, state: Option[NonBlankString]) extends AuthorizationResponse
}

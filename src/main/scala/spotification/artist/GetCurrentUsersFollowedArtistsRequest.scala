package spotification.artist

import spotification.authorization.AccessToken

sealed abstract class GetCurrentUsersFollowedArtistsRequest extends Product with Serializable
object GetCurrentUsersFollowedArtistsRequest {
  final case class FirstRequest(accessToken: AccessToken)
}

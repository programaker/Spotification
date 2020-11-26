package spotification.artist

import spotification.authorization.AccessToken
import spotification.follow.FollowType

sealed abstract class GetCurrentUsersFollowedArtistsRequest extends Product with Serializable
object GetCurrentUsersFollowedArtistsRequest {
  final case class FirstRequest(
    accessToken: AccessToken,
    `type`: FollowType,
    limit: Option[Int]
  )
}

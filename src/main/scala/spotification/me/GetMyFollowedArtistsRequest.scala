package spotification.me

import spotification.authorization.AccessToken
import spotification.common.UriString
import spotification.follow.FollowType

sealed abstract class GetMyFollowedArtistsRequest extends Product with Serializable
object GetMyFollowedArtistsRequest {
  final case class FirstRequest(
    accessToken: AccessToken,
    `type`: FollowType,
    limit: Option[MyFollowedArtistsLimit]
  ) extends GetMyFollowedArtistsRequest
  object FirstRequest {
    def make(accessToken: AccessToken): FirstRequest =
      FirstRequest(
        accessToken,
        `type` = FollowType.Artist,
        limit = Some(MyFollowedArtistsLimit.MaxValue)
      )
  }

  final case class NextRequest(
    accessToken: AccessToken,
    nextUri: UriString
  ) extends GetMyFollowedArtistsRequest
}

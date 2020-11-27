package spotification.artist

import spotification.authorization.AccessToken
import spotification.follow.FollowType
import eu.timepit.refined.auto._
import spotification.common.UriString

sealed abstract class GetMyFollowedArtistsRequest extends Product with Serializable
object GetMyFollowedArtistsRequest {
  final case class FirstRequest(
    accessToken: AccessToken,
    `type`: FollowType,
    limit: Option[FollowedArtistsLimit]
  ) extends GetMyFollowedArtistsRequest
  object FirstRequest {
    def make(accessToken: AccessToken): FirstRequest =
      FirstRequest(
        accessToken,
        `type` = FollowType.Artist,
        limit = Some(FollowedArtistsLimit.MaxValue)
      )
  }

  final case class NextRequest(
    accessToken: AccessToken,
    nextUri: UriString
  ) extends GetMyFollowedArtistsRequest
}

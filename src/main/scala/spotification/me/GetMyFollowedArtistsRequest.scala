package spotification.me

import spotification.authorization.AccessToken
import spotification.common.UriString
import spotification.follow.FollowType
import spotification.me.GetMyFollowedArtistsRequest.RequestType
import spotification.me.GetMyFollowedArtistsRequest.RequestType.FirstRequest

final case class GetMyFollowedArtistsRequest(accessToken: AccessToken, requestType: RequestType)
object GetMyFollowedArtistsRequest {
  def make(accessToken: AccessToken): GetMyFollowedArtistsRequest =
    GetMyFollowedArtistsRequest(
      accessToken,
      FirstRequest(
        `type` = FollowType.Artist,
        limit = Some(MyFollowedArtistsLimit.MaxValue)
      )
    )

  sealed abstract class RequestType extends Product with Serializable
  object RequestType {
    final case class FirstRequest(
      `type`: FollowType,
      limit: Option[MyFollowedArtistsLimit]
    ) extends RequestType

    final case class NextRequest(
      nextUri: UriString
    ) extends RequestType
  }
}

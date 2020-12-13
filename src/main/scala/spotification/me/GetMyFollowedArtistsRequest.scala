package spotification.me

import spotification.authorization.AccessToken
import spotification.common.UriString
import spotification.follow.FollowType
import spotification.me.GetMyFollowedArtistsRequest.RequestType
import spotification.me.GetMyFollowedArtistsRequest.RequestType.{First, Next}

final case class GetMyFollowedArtistsRequest[T <: RequestType](accessToken: AccessToken, requestType: T)
object GetMyFollowedArtistsRequest {
  def first(accessToken: AccessToken): GetMyFollowedArtistsRequest[First] =
    GetMyFollowedArtistsRequest(accessToken, First(FollowType.Artist, Some(MyFollowedArtistsLimit.MaxValue)))

  def next(accessToken: AccessToken, nextUri: UriString): GetMyFollowedArtistsRequest[Next] =
    GetMyFollowedArtistsRequest(accessToken, Next(nextUri))

  sealed abstract class RequestType extends Product with Serializable
  object RequestType {
    final case class First(
      `type`: FollowType,
      limit: Option[MyFollowedArtistsLimit]
    ) extends RequestType

    final case class Next(
      nextUri: UriString
    ) extends RequestType
  }
}

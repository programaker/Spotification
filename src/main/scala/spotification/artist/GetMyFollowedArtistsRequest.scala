package spotification.artist

import spotification.artist.GetMyFollowedArtistsRequest.RequestType
import spotification.artist.GetMyFollowedArtistsRequest.RequestType.{First, Next}
import spotification.authorization.AccessToken
import spotification.common.UriString
import spotification.follow.FollowType

final case class GetMyFollowedArtistsRequest[T <: RequestType](accessToken: AccessToken, requestType: T)
object GetMyFollowedArtistsRequest {
  def first(accessToken: AccessToken): GetMyFollowedArtistsRequest[First] =
    GetMyFollowedArtistsRequest(accessToken, First(FollowType.Artist, Some(MyFollowedArtistsLimit.MaxValue)))

  def next(accessToken: AccessToken, nextUri: UriString): GetMyFollowedArtistsRequest[Next] =
    GetMyFollowedArtistsRequest(accessToken, Next(nextUri))

  sealed trait RequestType
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

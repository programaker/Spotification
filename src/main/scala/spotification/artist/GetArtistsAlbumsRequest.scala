package spotification.artist

import eu.timepit.refined.auto._
import spotification.artist.GetArtistsAlbumsRequest.RequestType
import spotification.artist.GetArtistsAlbumsRequest.RequestType.First
import spotification.authorization.AccessToken
import spotification.common.{NonNegativeInt, UriString}

final case class GetArtistsAlbumsRequest[T <: RequestType](accessToken: AccessToken, requestType: RequestType)
object GetArtistsAlbumsRequest {
  sealed abstract class RequestType extends Product with Serializable
  object RequestType {
    final case class First(
      artistId: ArtistId,
      include_groups: List[IncludeAlbumGroup],
      limit: ArtistAlbumsLimit,
      offset: NonNegativeInt
    ) extends RequestType

    final case class Next(
      uri: UriString
    ) extends RequestType
  }

  def first(accessToken: AccessToken, artistId: ArtistId): GetArtistsAlbumsRequest[First] =
    GetArtistsAlbumsRequest(
      accessToken,
      First(
        artistId,
        include_groups = List("album"),
        ArtistAlbumsLimit.MaxValue,
        offset = 0
      )
    )
}

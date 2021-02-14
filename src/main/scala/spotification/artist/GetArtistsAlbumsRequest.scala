package spotification.artist

import eu.timepit.refined.auto._
import spotification.artist.GetArtistsAlbumsRequest.RequestType
import spotification.artist.GetArtistsAlbumsRequest.RequestType.{First, Next}
import spotification.authorization.AccessToken
import spotification.common.{NonNegativeInt, UriString}

final case class GetArtistsAlbumsRequest[T <: RequestType](accessToken: AccessToken, requestType: T) {
  def next(uri: UriString): GetArtistsAlbumsRequest[Next] = GetArtistsAlbumsRequest.next(accessToken, uri)
}
object GetArtistsAlbumsRequest {
  sealed trait RequestType
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
      First(artistId, include_groups = List("album"), ArtistAlbumsLimit.MaxValue, offset = 0)
    )

  def next(accessToken: AccessToken, uri: UriString): GetArtistsAlbumsRequest[Next] =
    GetArtistsAlbumsRequest(accessToken, Next(uri))
}

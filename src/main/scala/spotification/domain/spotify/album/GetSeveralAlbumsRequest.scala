package spotification.domain.spotify.album

import spotification.domain.spotify.authorization.AccessToken

final case class GetSeveralAlbumsRequest(
  accessToken: AccessToken,
  ids: AlbumIdsToGet
)

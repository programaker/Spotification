package spotification.domain.spotify.playlist

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.{FieldsToReturn, NonNegativeInt, PositiveInt}

final case class PlaylistTracksRequest(
  accessToken: AccessToken,
  playlistId: PlaylistId,
  fields: FieldsToReturn,
  limit: PositiveInt,
  offset: NonNegativeInt
)

package spotification.domain.config

import spotification.domain.spotify.playlist.{PlaylistApiUri, PlaylistId}

final case class PlaylistConfig(
  releaseRadarId: PlaylistId,
  playlistApiUri: PlaylistApiUri
)

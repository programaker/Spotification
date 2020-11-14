package spotification.playlist

final case class MergePlaylistsRequest(mergedPlaylistId: PlaylistId, playlistsToMerge: List[PlaylistId])

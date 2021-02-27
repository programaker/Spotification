package spotification.playlist

import org.http4s.HttpRoutes
import spotification.album.httpclient.GetAlbumSampleTrackServiceLayer
import spotification.artist.httpclient.{GetArtistsAlbumsServiceLayer, GetMyFollowedArtistsServiceLayer}
import spotification.authorization.api.RequestAccessTokenProgramLayer
import spotification.common.GenericResponse
import spotification.common.api.{doRequest, handleGenericError, withDsl}
import spotification.common.httpclient.HttpClientR
import spotification.common.json.implicits.{GenericResponseSuccessEncoder, entityDecoderF, entityEncoderF}
import spotification.config.service._
import spotification.config.source.PlaylistConfigLayer
import spotification.log.impl.LogLayer
import spotification.playlist.httpclient.{
  AddItemsToPlaylistServiceLayer,
  CreatePlaylistServiceLayer,
  GetPlaylistsItemsServiceLayer,
  RemoveItemsFromPlaylistServiceLayer
}
import spotification.playlist.json.implicits.{MergePlaylistsRequestDecoder, ReleaseRadarNoSinglesRequestDecoder}
import spotification.playlist.program._
import spotification.user.httpclient.GetMyProfileServiceLayer
import zio.clock.Clock
import zio.interop.catz.taskConcurrentInstance
import zio.{RIO, RLayer}

package object api {
  type ReleaseRadarNoSinglesProgramLayerR = AuthorizationConfigR with HttpClientR with PlaylistConfigR
  val ReleaseRadarNoSinglesProgramLayer: RLayer[ReleaseRadarNoSinglesProgramLayerR, ReleaseRadarNoSinglesProgramR] =
    RequestAccessTokenProgramLayer ++
      PlaylistConfigLayer ++
      LogLayer ++
      GetPlaylistsItemsServiceLayer ++
      RemoveItemsFromPlaylistServiceLayer ++
      AddItemsToPlaylistServiceLayer

  type MergePlaylistsProgramLayerR = AuthorizationConfigR with HttpClientR with PlaylistConfigR
  val MergePlaylistsProgramLayer: RLayer[MergePlaylistsProgramLayerR, MergePlaylistsProgramR] =
    RequestAccessTokenProgramLayer ++
      PlaylistConfigLayer ++
      LogLayer ++
      GetPlaylistsItemsServiceLayer ++
      RemoveItemsFromPlaylistServiceLayer ++
      AddItemsToPlaylistServiceLayer ++
      Clock.live

  type AlbumAnniversariesPlaylistProgramLayerR = PlaylistConfigR
    with HttpClientR
    with AlbumConfigR
    with ArtistConfigR
    with MeConfigR
    with UserConfigR
    with AuthorizationConfigR
  val AlbumAnniversariesPlaylistProgramLayer
    : RLayer[AlbumAnniversariesPlaylistProgramLayerR, AlbumAnniversariesPlaylistProgramR] =
    AddItemsToPlaylistServiceLayer ++
      GetAlbumSampleTrackServiceLayer ++
      GetArtistsAlbumsServiceLayer ++
      GetMyFollowedArtistsServiceLayer ++
      CreatePlaylistServiceLayer ++
      Clock.live ++
      GetMyProfileServiceLayer ++
      RequestAccessTokenProgramLayer ++
      LogLayer

  type PlaylistsLayerR = ReleaseRadarNoSinglesProgramLayerR
    with MergePlaylistsProgramLayerR
    with AlbumAnniversariesPlaylistProgramLayerR
  val PlaylistsLayer: RLayer[PlaylistsLayerR, PlaylistProgramsR] =
    ReleaseRadarNoSinglesProgramLayer ++ MergePlaylistsProgramLayer ++ AlbumAnniversariesPlaylistProgramLayer

  def makePlaylistsApi[R <: PlaylistProgramsR]: HttpRoutes[RIO[R, *]] = withDsl { dsl =>
    import dsl._

    HttpRoutes.of[RIO[R, *]] {
      case rawReq @ PATCH -> Root / "release-radar-no-singles" =>
        doRequest(rawReq) { (refreshToken, req: ReleaseRadarNoSinglesRequest) =>
          releaseRadarNoSinglesProgram(refreshToken, req.releaseRadarId, req.releaseRadarNoSinglesId)
        }.foldM(
          handleGenericError(dsl, _),
          _ => Ok(GenericResponse.Success("Enjoy your albums-only Release Radar!"))
        )

      case rawReq @ PATCH -> Root / "merged-playlist" =>
        doRequest(rawReq) { (refreshToken, req: MergePlaylistsRequest) =>
          mergePlaylistsProgram(refreshToken, req.mergedPlaylistId, req.playlistsToMerge)
        }.foldM(
          handleGenericError(dsl, _),
          _ => Ok(GenericResponse.Success("Enjoy your merged playlist!"))
        )
    }
  }
}

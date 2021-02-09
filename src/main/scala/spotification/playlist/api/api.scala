package spotification.playlist

import org.http4s.HttpRoutes
import spotification.authorization.api.RequestAccessTokenProgramLayer
import spotification.common.GenericResponse
import spotification.common.api.{doRequest, handleGenericError, withDsl}
import spotification.common.httpclient.HttpClientR
import spotification.common.json.implicits.{GenericResponseSuccessEncoder, entityDecoderF, entityEncoderF}
import spotification.config.service.{AuthorizationConfigR, PlaylistConfigR}
import spotification.config.source.PlaylistConfigLayer
import spotification.log.impl.LogLayer
import spotification.playlist.httpclient.{
  AddItemsToPlaylistServiceLayer,
  GetPlaylistsItemsServiceLayer,
  RemoveItemsFromPlaylistServiceLayer
}
import spotification.playlist.json.implicits.{MergePlaylistsRequestDecoder, ReleaseRadarNoSinglesRequestDecoder}
import spotification.playlist.program._
import zio.clock.Clock
import zio.interop.catz.taskConcurrentInstance
import zio.{RIO, RLayer}

package object api {
  val ReleaseRadarNoSinglesProgramLayer
    : RLayer[AuthorizationConfigR with HttpClientR with PlaylistConfigR, ReleaseRadarNoSinglesProgramR] =
    RequestAccessTokenProgramLayer ++
      PlaylistConfigLayer ++
      LogLayer ++
      GetPlaylistsItemsServiceLayer ++
      RemoveItemsFromPlaylistServiceLayer ++
      AddItemsToPlaylistServiceLayer

  val MergePlaylistsProgramLayer
    : RLayer[AuthorizationConfigR with HttpClientR with PlaylistConfigR, MergePlaylistsProgramR] =
    RequestAccessTokenProgramLayer ++
      PlaylistConfigLayer ++
      LogLayer ++
      GetPlaylistsItemsServiceLayer ++
      RemoveItemsFromPlaylistServiceLayer ++
      AddItemsToPlaylistServiceLayer ++
      Clock.live

  val PlaylistsLayer: RLayer[AuthorizationConfigR with HttpClientR with PlaylistConfigR, PlaylistProgramsR] =
    ReleaseRadarNoSinglesProgramLayer ++ MergePlaylistsProgramLayer

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

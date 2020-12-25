package spotification.playlist

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.authorization.api.RequestAccessTokenProgramLayer
import spotification.common.GenericResponse
import spotification.common.api.{doRequest, handleGenericError}
import spotification.common.json.implicits.{GenericResponseSuccessEncoder, entityDecoderF, entityEncoderF}
import spotification.config.source.PlaylistConfigLayer
import spotification.log.impl.LogLayer
import spotification.playlist.httpclient.{
  AddItemsToPlaylistServiceLayer,
  GetPlaylistsItemsServiceLayer,
  RemoveItemsFromPlaylistServiceLayer
}
import spotification.playlist.json.implicits.{MergePlaylistsRequestDecoder, ReleaseRadarNoSinglesRequestDecoder}
import spotification.playlist.program.{
  MergePlaylistsProgramEnv,
  ReleaseRadarNoSinglesProgramEnv,
  mergePlaylistsProgram,
  releaseRadarNoSinglesProgram
}
import zio.clock.Clock
import zio.interop.catz.taskConcurrentInstance
import zio.{RIO, TaskLayer}

package object api {
  val ReleaseRadarNoSinglesLayer: TaskLayer[ReleaseRadarNoSinglesProgramEnv] =
    RequestAccessTokenProgramLayer ++
      PlaylistConfigLayer ++
      LogLayer ++
      GetPlaylistsItemsServiceLayer ++
      RemoveItemsFromPlaylistServiceLayer ++
      AddItemsToPlaylistServiceLayer

  val MergePlaylistsLayer: TaskLayer[MergePlaylistsProgramEnv] =
    RequestAccessTokenProgramLayer ++
      PlaylistConfigLayer ++
      LogLayer ++
      GetPlaylistsItemsServiceLayer ++
      RemoveItemsFromPlaylistServiceLayer ++
      AddItemsToPlaylistServiceLayer ++
      Clock.live

  type MakePlaylistsRoutsEnv = ReleaseRadarNoSinglesProgramEnv with MergePlaylistsProgramEnv
  def makePlaylistsRoutes[R <: MakePlaylistsRoutsEnv]: HttpRoutes[RIO[R, *]] = {
    val dsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
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

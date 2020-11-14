package spotification.playlist

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.authorization.api.SpotifyAuthorizationLayer
import spotification.common.api.{GenericResponse, doRequest, handleGenericError}
import spotification.config.source.PlaylistConfigLayer
import spotification.log.impl.LogLayer
import spotification.playlist.httpclient.PlaylistServiceLayer
import spotification.playlist.program.{
  MergePlaylistsEnv,
  ReleaseRadarNoSinglesEnv,
  mergePlaylistsProgram,
  releaseRadarNoSinglesProgram
}
import zio.{RIO, TaskLayer}
import zio.clock.Clock
import zio.interop.catz._
import spotification.json.implicits._
import io.circe.generic.auto._

package object api {
  type MakePlaylistsRoutsEnv = ReleaseRadarNoSinglesEnv with MergePlaylistsEnv

  val ReleaseRadarNoSinglesLayer: TaskLayer[ReleaseRadarNoSinglesEnv] =
    LogLayer ++
      PlaylistServiceLayer ++
      PlaylistConfigLayer ++
      SpotifyAuthorizationLayer

  val MergePlaylistsLayer: TaskLayer[MergePlaylistsEnv] =
    Clock.live ++
      LogLayer ++
      PlaylistServiceLayer ++
      PlaylistConfigLayer ++
      SpotifyAuthorizationLayer

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

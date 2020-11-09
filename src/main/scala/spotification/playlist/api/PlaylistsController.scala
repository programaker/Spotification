package spotification.playlist.api

import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.common.api.{GenericResponse, doRequest, handleGenericError}
import spotification.playlist.api.PlaylistsController.{MergePlaylistsRequest, ReleaseRadarNoSinglesRequest}
import spotification.playlist.PlaylistId
import spotification.playlist.program.{
  MergePlaylistsEnv,
  ReleaseRadarNoSinglesEnv,
  mergePlaylistsProgram,
  releaseRadarNoSinglesProgram
}
import spotification.json.implicits._
import zio.RIO
import zio.interop.catz._

final class PlaylistsController[R <: ReleaseRadarNoSinglesEnv with MergePlaylistsEnv] extends Http4sDsl[RIO[R, *]] {
  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case rawReq @ PATCH -> Root / "release-radar-no-singles" =>
      doRequest(rawReq) { (refreshToken, req: ReleaseRadarNoSinglesRequest) =>
        releaseRadarNoSinglesProgram(refreshToken, req.releaseRadarId, req.releaseRadarNoSinglesId)
      }.foldM(
        handleGenericError(this, _),
        _ => Ok(GenericResponse.Success("Enjoy your albums-only Release Radar!"))
      )

    case rawReq @ PATCH -> Root / "merged-playlist" =>
      doRequest(rawReq) { (refreshToken, req: MergePlaylistsRequest) =>
        mergePlaylistsProgram(refreshToken, req.mergedPlaylistId, req.playlistsToMerge)
      }.foldM(
        handleGenericError(this, _),
        _ => Ok(GenericResponse.Success("Enjoy your merged playlist!"))
      )
  }
}
object PlaylistsController {
  final case class ReleaseRadarNoSinglesRequest(releaseRadarId: PlaylistId, releaseRadarNoSinglesId: PlaylistId)
  final case class MergePlaylistsRequest(mergedPlaylistId: PlaylistId, playlistsToMerge: List[PlaylistId])
}

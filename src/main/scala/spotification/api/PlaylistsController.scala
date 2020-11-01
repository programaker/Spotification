package spotification.api

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.playlist.application.releaseradarnosingles._
import spotification.playlist.PlaylistId
import zio.RIO
import io.circe.generic.auto._
import spotification.playlist.application.mergeplaylists.{MergePlaylistsEnv, mergePlaylistsProgram}
import zio.interop.catz._
import spotification.common.infra.json.implicits._
import spotification.api.PlaylistsController.{MergePlaylistsRequest, ReleaseRadarNoSinglesRequest}

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

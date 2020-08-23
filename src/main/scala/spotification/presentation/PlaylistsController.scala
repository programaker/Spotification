package spotification.presentation

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.application.releaseradarnosingles._
import spotification.domain.spotify.playlist.PlaylistId
import zio.RIO
import io.circe.generic.auto._
import spotification.application.mergeplaylists.{MergePlaylistsEnv, mergePlaylistsProgram}
import zio.interop.catz._
import spotification.infra.json.implicits._
import spotification.presentation.PlaylistsController.{MergePlaylistsRequest, ReleaseRadarNoSinglesRequest}
import spotification.presentation._

final class PlaylistsController[R <: ReleaseRadarNoSinglesEnv with MergePlaylistsEnv] {
  private val H4sDsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
  import H4sDsl._

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case rawReq @ PATCH -> Root / "release-radar-no-singles" =>
      doRequest(rawReq) { (refreshToken, req: ReleaseRadarNoSinglesRequest) =>
        releaseRadarNoSinglesProgram(refreshToken, req.releaseRadarId, req.releaseRadarNoSinglesId)
      }.foldM(
        handleGenericError(H4sDsl, _),
        _ => Ok(GenericResponse.Success("Enjoy your albums-only Release Radar!"))
      )

    case rawReq @ PATCH -> Root / "merged-playlist" =>
      doRequest(rawReq) { (refreshToken, req: MergePlaylistsRequest) =>
        mergePlaylistsProgram(refreshToken, req.mergedPlaylistId, req.playlistsToMerge)
      }.foldM(
        handleGenericError(H4sDsl, _),
        _ => Ok(GenericResponse.Success("Enjoy your merged playlist!"))
      )
  }
}
object PlaylistsController {
  final case class ReleaseRadarNoSinglesRequest(releaseRadarId: PlaylistId, releaseRadarNoSinglesId: PlaylistId)
  final case class MergePlaylistsRequest(mergedPlaylistId: PlaylistId, playlistsToMerge: List[PlaylistId])
}

package spotification.presentation

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.application.mergeplaylists.{MergePlaylistsEnv, mergePlaylistsProgram}
import spotification.domain.spotify.playlist.PlaylistId
import zio.RIO
import io.circe.generic.auto._
import zio.interop.catz._
import spotification.infra.json.implicits._

class MergePlaylistsController[R <: MergePlaylistsEnv] {
  private val H4sDsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
  import H4sDsl._

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case rawReq @ PATCH -> Root =>
      doRequest(rawReq) { (refreshToken, req: MergePlaylistsController.Request) =>
        mergePlaylistsProgram(refreshToken, req.mergedPlaylistId, req.playlistsToMerge)
      }.foldM(
        handleGenericError(H4sDsl, _),
        _ => Ok(GenericResponse.Success("Enjoy your merged playlist!"))
      )
  }
}
object MergePlaylistsController {
  final case class Request(mergedPlaylistId: PlaylistId, playlistsToMerge: List[PlaylistId])
}

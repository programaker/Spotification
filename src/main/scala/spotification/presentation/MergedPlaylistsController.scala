package spotification.presentation

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.application.MergePlaylistsApp.mergePlaylistsProgram
import spotification.application.MergedPlaylistsEnv
import spotification.domain.spotify.playlist.PlaylistId
import spotification.presentation.Controller.{handleGenericError, requiredRefreshTokenFromRequest}
import zio.RIO

// ==========
// Despite IntelliJ telling that
// `import io.circe.generic.auto._`
// `import zio.interop.catz._`
// `import spotification.infra.json._`
// are not being used, they are required to compile
// ==========
import io.circe.generic.auto._
import zio.interop.catz._
import spotification.infra.Json.Implicits._

class MergedPlaylistsController[R <: MergedPlaylistsEnv] {
  private val H4sDsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
  import H4sDsl._

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case rawReq @ PATCH -> Root =>
      val resp = for {
        refreshToken <- requiredRefreshTokenFromRequest(rawReq)
        req          <- rawReq.as[MergedPlaylistsController.Request]
        _            <- mergePlaylistsProgram(refreshToken, req.mergedPlaylistId, req.playlistsToMerge)
      } yield ()

      resp.foldM(
        handleGenericError(H4sDsl, _),
        _ => Ok(GenericResponse.Success("Enjoy your merged playlist!"))
      )
  }
}
object MergedPlaylistsController {
  final case class Request(mergedPlaylistId: PlaylistId, playlistsToMerge: List[PlaylistId])
}

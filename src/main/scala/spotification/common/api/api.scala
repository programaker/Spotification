package spotification.common

import cats.Applicative
import io.circe.generic.auto._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes, Request, Response}
import spotification.authorization.RefreshToken
import spotification.authorization.api.{
  SpotifyAuthorizationLayer,
  makeSpotifyAuthorizationRoutes,
  requiredRefreshTokenFromRequest
}
import spotification.authorization.program.SpotifyAuthorizationEnv
import spotification.json.implicits._
import spotification.monitoring.api.makeHealthCheckRoutes
import spotification.playlist.api.{MergePlaylistsLayer, ReleaseRadarNoSinglesLayer, makePlaylistsRoutes}
import spotification.playlist.program.{MergePlaylistsEnv, ReleaseRadarNoSinglesEnv}
import spotification.track.api.{ShareTrackLayer, makeTracksRoutes}
import spotification.track.program.ShareTrackEnv
import zio.interop.catz.monadErrorInstance
import zio.{RIO, TaskLayer}

package object api {
  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]

  type ApiEnv = SpotifyAuthorizationEnv with ReleaseRadarNoSinglesEnv with MergePlaylistsEnv with ShareTrackEnv
  val ApiLayer: TaskLayer[ApiEnv] =
    SpotifyAuthorizationLayer ++ ReleaseRadarNoSinglesLayer ++ MergePlaylistsLayer ++ ShareTrackLayer

  def allRoutes[R <: ApiEnv]: Routes[RIO[R, *]] =
    Seq(
      "/health"                -> makeHealthCheckRoutes[R],
      "/authorization/spotify" -> makeSpotifyAuthorizationRoutes[R],
      "/playlists"             -> makePlaylistsRoutes[R],
      "/tracks"                -> makeTracksRoutes[R]
    )

  def handleGenericError[F[_]: Applicative](dsl: Http4sDsl[F], e: Throwable): F[Response[F]] = {
    import dsl._
    InternalServerError(GenericResponse.Error(e.getMessage))
  }

  def doRequest[R, A, B](
    rawReq: Request[RIO[R, *]]
  )(
    f: (RefreshToken, A) => RIO[R, B]
  )(implicit
    D: EntityDecoder[RIO[R, *], A]
  ): RIO[R, B] =
    for {
      refreshToken <- requiredRefreshTokenFromRequest(rawReq)
      a            <- rawReq.as[A]
      b            <- f(refreshToken, a)
    } yield b
}

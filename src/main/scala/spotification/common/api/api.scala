package spotification.common

import cats.Applicative
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes, Request, Response}
import spotification.authorization.RefreshToken
import spotification.authorization.api.{
  SpotifyAuthorizationLayer,
  requiredRefreshTokenFromRequest,
  spotifyAuthorizationApi
}
import spotification.authorization.program.SpotifyAuthorizationEnv
import spotification.common.httpclient.HttpClientEnv
import spotification.common.json.implicits.{GenericResponseErrorEncoder, entityEncoderF}
import spotification.config.service.{AuthorizationConfigEnv, PlaylistConfigEnv, TrackConfigEnv}
import spotification.monitoring.api.healthCheckApi
import spotification.playlist.api.{PlaylistsLayer, playlistsApi}
import spotification.playlist.program.PlaylistsEnv
import spotification.track.api.{TracksLayer, tracksApi}
import spotification.track.program.TracksEnv
import zio.interop.catz.monadErrorInstance
import zio.{RIO, RLayer}

package object api {
  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]

  type ApiEnv =
    SpotifyAuthorizationEnv with PlaylistsEnv with TracksEnv
  val ApiLayer: RLayer[HttpClientEnv with AuthorizationConfigEnv with PlaylistConfigEnv with TrackConfigEnv, ApiEnv] =
    SpotifyAuthorizationLayer ++ PlaylistsLayer ++ TracksLayer

  def allRoutes[R <: ApiEnv]: Routes[RIO[R, *]] =
    Seq(
      "/health"                -> healthCheckApi[R],
      "/authorization/spotify" -> spotifyAuthorizationApi[R],
      "/playlists"             -> playlistsApi[R],
      "/tracks"                -> tracksApi[R]
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

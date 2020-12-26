package spotification.common

import cats.Applicative
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes, Request, Response}
import spotification.authorization.RefreshToken
import spotification.authorization.api.{
  AuthorizationProgramsLayer,
  makeAuthorizationApi,
  requiredRefreshTokenFromRequest
}
import spotification.common.httpclient.HttpClientEnv
import spotification.common.json.implicits.{GenericResponseErrorEncoder, entityEncoderF}
import spotification.common.program.AllProgramsEnv
import spotification.config.service.{AuthorizationConfigEnv, PlaylistConfigEnv, TrackConfigEnv}
import spotification.monitoring.api.makeHealthCheckApi
import spotification.playlist.api.{PlaylistsLayer, makePlaylistsApi}
import spotification.track.api.{TracksLayer, makeTracksApi}
import zio.interop.catz.monadErrorInstance
import zio.{RIO, RLayer}

package object api {
  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]

  val AllProgramsLayer
    : RLayer[HttpClientEnv with AuthorizationConfigEnv with PlaylistConfigEnv with TrackConfigEnv, AllProgramsEnv] =
    AuthorizationProgramsLayer ++ PlaylistsLayer ++ TracksLayer

  def makeAllApis[R <: AllProgramsEnv]: Routes[RIO[R, *]] =
    Seq(
      "/health"                -> makeHealthCheckApi[R],
      "/authorization/spotify" -> makeAuthorizationApi[R],
      "/playlists"             -> makePlaylistsApi[R],
      "/tracks"                -> makeTracksApi[R]
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

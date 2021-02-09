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
import spotification.common.httpclient.HttpClientR
import spotification.common.json.implicits.{GenericResponseErrorEncoder, entityEncoderF}
import spotification.common.program.AllProgramsR
import spotification.config.service.{AuthorizationConfigR, PlaylistConfigR, TrackConfigR}
import spotification.monitoring.api.makeHealthCheckApi
import spotification.playlist.api.{PlaylistsLayer, makePlaylistsApi}
import spotification.track.api.{TracksLayer, makeTracksApi}
import zio.interop.catz.monadErrorInstance
import zio.{RIO, RLayer}

package object api {
  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]

  val AllProgramsLayer
    : RLayer[HttpClientR with AuthorizationConfigR with PlaylistConfigR with TrackConfigR, AllProgramsR] =
    AuthorizationProgramsLayer ++ PlaylistsLayer ++ TracksLayer

  def makeAllApis[R <: AllProgramsR]: Routes[RIO[R, *]] =
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

  def withDsl[R](f: Http4sDsl[RIO[R, *]] => HttpRoutes[RIO[R, *]]): HttpRoutes[RIO[R, *]] =
    f(Http4sDsl[RIO[R, *]])
}

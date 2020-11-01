package spotification.common

import cats.Applicative
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.AuthScheme.Bearer
import org.http4s.Credentials.Token
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import spotification.authorization.RefreshToken
import spotification.authorization.api.SpotifyAuthorizationController
import spotification.authorization.application.SpotifyAuthorizationEnv
import spotification.authorization.infra.SpotifyAuthorizationLayer
import spotification.common.application.refineRIO
import spotification.common.infra.json.implicits._
import spotification.playlist.api.PlaylistsController
import spotification.playlist.application.{MergePlaylistsEnv, ReleaseRadarNoSinglesEnv}
import spotification.playlist.infra.{MergePlaylistsLayer, ReleaseRadarNoSinglesLayer}
import spotification.track.api.TracksController
import spotification.track.application.ShareTrackEnv
import spotification.track.infra.ShareTrackLayer
import zio.{RIO, ZIO, _}

package object api {
  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]
  type ApiEnv = SpotifyAuthorizationEnv with ReleaseRadarNoSinglesEnv with MergePlaylistsEnv with ShareTrackEnv

  val ApiLayer: TaskLayer[ApiEnv] =
    SpotifyAuthorizationLayer ++ ReleaseRadarNoSinglesLayer ++ MergePlaylistsLayer ++ ShareTrackLayer

  def allRoutes[R <: ApiEnv]: Routes[RIO[R, *]] =
    Seq(
      "/health"                -> new HealthCheckController[R].routes,
      "/authorization/spotify" -> new SpotifyAuthorizationController[R].routes,
      "/playlists"             -> new PlaylistsController[R].routes,
      "/tracks"                -> new TracksController[R].routes
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

  def requiredRefreshTokenFromRequest[R](req: Request[RIO[R, *]]): RIO[R, RefreshToken] =
    refreshTokenFromRequest(req).flatMap {
      case Some(refreshToken) => ZIO.succeed(refreshToken)
      case None               => ZIO.fail(new Exception("Bearer refresh token absent from request"))
    }

  def refreshTokenFromRequest[R](req: Request[RIO[R, *]]): RIO[R, Option[RefreshToken]] =
    req.headers
      .get(Authorization)
      .map(_.credentials)
      .flatMap {
        case Token(Bearer, refreshTokenString) => Some(refreshTokenString)
        case _                                 => None
      }
      .map(refineRIO[R, NonBlankStringR](_))
      .sequence
      .map(_.map(RefreshToken(_)))
}
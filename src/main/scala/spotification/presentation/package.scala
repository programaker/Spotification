package spotification

import cats.Applicative
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.AuthScheme.Bearer
import org.http4s.Credentials.Token
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.http4s._
import spotification.application.mergeplaylists.MergePlaylistsEnv
import spotification.application.releaseradarnosingles.ReleaseRadarNoSinglesEnv
import spotification.application.sharetrack.ShareTrackEnv
import spotification.application.spotifyauthorization.SpotifyAuthorizationEnv
import spotification.domain.NonBlankStringR
import spotification.domain.spotify.authorization.RefreshToken
import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.ServerConfigModule
import spotification.infra.httpserver.HttpServer._
import spotification.infra.json.implicits._
import spotification.infra.refineRIO
import zio.clock.Clock
import zio.interop.catz._
import zio.{RIO, ZIO, _}

package object presentation {
  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]

  type PresentationEnv = SpotifyAuthorizationEnv with ReleaseRadarNoSinglesEnv with MergePlaylistsEnv with ShareTrackEnv
  object PresentationEnv {
    val layer: TaskLayer[PresentationEnv] =
      SpotifyAuthorizationEnv.layer ++ ReleaseRadarNoSinglesEnv.layer ++ MergePlaylistsEnv.layer ++ ShareTrackEnv.layer
  }

  type HttpAppEnv = ServerConfigModule with ExecutionContextModule with PresentationEnv with Clock
  object HttpAppEnv {
    val layer: TaskLayer[HttpAppEnv] =
      ServerConfigModule.layer ++ ExecutionContextModule.layer ++ PresentationEnv.layer ++ Clock.live
  }

  val runHttpApp: RIO[HttpAppEnv, Unit] = ZIO.runtime[HttpAppEnv].flatMap { implicit rt =>
    for {
      config <- ServerConfigModule.config
      ex     <- ExecutionContextModule.executionContext

      controllers = allRoutes[HttpAppEnv]
      app = addCors(addLogger(httpApp(controllers)))

      _ <- runHttpServer[RIO[HttpAppEnv, *]](config, app, ex)
    } yield ()
  }

  def allRoutes[R <: PresentationEnv]: Routes[RIO[R, *]] =
    Seq(
      "/health"        -> new HealthCheckController[R].routes,
      "/authorization" -> new AuthorizationController[R].routes,
      "/playlists"     -> new PlaylistsController[R].routes,
      "/tracks"        -> new TracksController[R].routes
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

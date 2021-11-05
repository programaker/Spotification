package spotification

import spotification.authorization.api.{AuthorizationProgramsLayer, makeAuthorizationApi}
import spotification.authorization.program.AuthorizationProgramsR
import spotification.common.api.Routes
import spotification.common.httpclient.{HttpClientLayer, HttpClientR}
import spotification.config.service._
import spotification.config.source._
import spotification.httpserver.{addCors, addLogger, makeHttpApp, runHttpServer}
import spotification.log.service.error
import spotification.monitoring.api.makeHealthCheckApi
import spotification.playlist.api.{PlaylistsLayer, PlaylistsLayerR, makePlaylistsApi}
import spotification.playlist.program.PlaylistProgramsR
import spotification.track.api.{TracksLayer, makeTracksApi}
import spotification.track.program.TrackProgramsR
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz.asyncInstance

import scala.util.control.NonFatal

object SpotificationHttpApp extends zio.App {
  type AllProgramsLayerR = AuthorizationConfigR with HttpClientR with PlaylistsLayerR with TrackConfigR
  type AllProgramsR = AuthorizationProgramsR with PlaylistProgramsR with TrackProgramsR
  val AllProgramsLayer: RLayer[AllProgramsLayerR, AllProgramsR] =
    AuthorizationProgramsLayer ++ PlaylistsLayer ++ TracksLayer

  type HttpAppR = ServerConfigR with AllProgramsR with Clock with Blocking
  val HttpAppLayer: TaskLayer[HttpAppR] =
    ServerConfigLayer >+>
      Clock.live >+>
      Blocking.live >+>
      ConcurrentConfigLayer >+>
      ClientConfigLayer >+>
      HttpClientLayer >+>
      AuthorizationConfigLayer >+>
      PlaylistConfigLayer >+>
      TrackConfigLayer >+>
      UserConfigLayer >+>
      MeConfigLayer >+>
      ArtistConfigLayer >+>
      AlbumConfigLayer >+>
      AllProgramsLayer

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    runHttpApp
      .catchSome { case NonFatal(e) => error(">>> Error <<<", e) }
      .provideCustomLayer(HttpAppLayer)
      .exitCode

  private def runHttpApp: RIO[HttpAppR, Unit] =
    for {
      config <- serverConfig

      controllers = makeAllApis[HttpAppR]
      app = addCors(addLogger(makeHttpApp(controllers)))

      _ <- runHttpServer[RIO[HttpAppR, *]](config, app)
    } yield ()

  private def makeAllApis[R <: AllProgramsR]: Routes[RIO[R, *]] =
    Seq(
      "/health"                -> makeHealthCheckApi[R],
      "/authorization/spotify" -> makeAuthorizationApi[R],
      "/playlists"             -> makePlaylistsApi[R],
      "/tracks"                -> makeTracksApi[R]
    )
}

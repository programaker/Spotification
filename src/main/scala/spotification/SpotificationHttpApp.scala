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
import zio.clock.Clock
import zio.interop.catz._

import scala.util.control.NonFatal

object SpotificationHttpApp extends zio.App {
  type AllProgramsLayerR = AuthorizationConfigR with HttpClientR with PlaylistsLayerR with TrackConfigR
  type AllProgramsR = AuthorizationProgramsR with PlaylistProgramsR with TrackProgramsR
  val AllProgramsLayer: RLayer[AllProgramsLayerR, AllProgramsR] =
    AuthorizationProgramsLayer ++ PlaylistsLayer ++ TracksLayer

  type HttpAppR = ServerConfigR with AllProgramsR with Clock
  val HttpAppLayer: TaskLayer[HttpAppR] =
    ServerConfigLayer >+>
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
      AllProgramsLayer >+>
      Clock.live

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    runHttpApp
      .catchSome { case NonFatal(e) => error(">>> Error <<<", e) }
      .provideCustomLayer(HttpAppLayer)
      .exitCode

  private def runHttpApp: RIO[HttpAppR, Unit] =
    ZIO.runtime[HttpAppR].flatMap { implicit rt =>
      for {
        config <- serverConfig

        ex = rt.platform.executor.asEC
        controllers = makeAllApis[HttpAppR]
        app = addCors(addLogger(makeHttpApp(controllers)))

        _ <- runHttpServer[RIO[HttpAppR, *]](config, app, ex)
      } yield ()
    }

  private def makeAllApis[R <: AllProgramsR]: Routes[RIO[R, *]] =
    Seq(
      "/health"                -> makeHealthCheckApi[R],
      "/authorization/spotify" -> makeAuthorizationApi[R],
      "/playlists"             -> makePlaylistsApi[R],
      "/tracks"                -> makeTracksApi[R]
    )
}

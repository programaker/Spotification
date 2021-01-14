package spotification.config

import pureconfig.ConfigSource
import spotification.config.service.{
  AlbumConfigR,
  ArtistConfigR,
  AuthorizationConfigR,
  ClientConfigR,
  ConcurrentConfigR,
  MeConfigR,
  PlaylistConfigR,
  ServerConfigR,
  TrackConfigR,
  UserConfigR
}
import zio.{Has, IO, Tag, TaskLayer, ZLayer}
import spotification.config.implicits.AppConfigReader

package object source {
  val AuthorizationConfigLayer: TaskLayer[AuthorizationConfigR] = makeLayer(_.authorization)
  val PlaylistConfigLayer: TaskLayer[PlaylistConfigR] = makeLayer(_.playlist)
  val ArtistConfigLayer: TaskLayer[ArtistConfigR] = makeLayer(_.artist)
  val AlbumConfigLayer: TaskLayer[AlbumConfigR] = makeLayer(_.album)
  val TrackConfigLayer: TaskLayer[TrackConfigR] = makeLayer(_.track)
  val MeConfigLayer: TaskLayer[MeConfigR] = makeLayer(_.me)
  val UserConfigLayer: TaskLayer[UserConfigR] = makeLayer(_.user)
  val ServerConfigLayer: TaskLayer[ServerConfigR] = makeLayer(_.server)
  val ClientConfigLayer: TaskLayer[ClientConfigR] = makeLayer(_.client)
  val ConcurrentConfigLayer: TaskLayer[ConcurrentConfigR] = makeLayer(_.concurrent)

  private def makeLayer[A: Tag](f: AppConfig => A): TaskLayer[Has[A]] =
    appConfigLayer.map(_.get).map(f).map(Has(_))

  private lazy val appConfigLayer: TaskLayer[Has[AppConfig]] =
    ZLayer.fromEffect {
      IO.fromEither(ConfigSource.default.load[AppConfig])
        .mapError(_.prettyPrint())
        .absorbWith(new Exception(_))
    }
}

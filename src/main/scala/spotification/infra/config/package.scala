package spotification.infra

import spotification.domain.config._
import zio._

package object config {
  type AuthorizationConfigModule = Has[AuthorizationConfig]
  object AuthorizationConfigModule {
    val config: RIO[AuthorizationConfigModule, AuthorizationConfig] = ZIO.access(_.get)
    val layer: TaskLayer[AuthorizationConfigModule] = makeLayer(_.get.authorization)
  }

  type PlaylistConfigModule = Has[PlaylistConfig]
  object PlaylistConfigModule {
    val config: RIO[PlaylistConfigModule, PlaylistConfig] = ZIO.access(_.get)
    val layer: TaskLayer[PlaylistConfigModule] = makeLayer(_.get.playlist)
  }

  type TrackConfigModule = Has[TrackConfig]
  object TrackConfigModule {
    val config: RIO[TrackConfigModule, TrackConfig] = ZIO.access(_.get)
    val layer: TaskLayer[TrackConfigModule] = makeLayer(_.get.track)
  }

  type ServerConfigModule = Has[ServerConfig]
  object ServerConfigModule {
    val config: RIO[ServerConfigModule, ServerConfig] = ZIO.access(_.get)
    val layer: TaskLayer[ServerConfigModule] = makeLayer(_.get.server)
  }

  type ClientConfigModule = Has[ClientConfig]
  object ClientConfigModule {
    val config: RIO[ClientConfigModule, ClientConfig] = ZIO.access(_.get)
    val layer: TaskLayer[ClientConfigModule] = makeLayer(_.get.client)
  }

  private def makeLayer[A: Tag](f: Has[AppConfig] => A): TaskLayer[Has[A]] =
    appConfigLayer.map(f).map(Has(_))

  private val appConfigLayer: TaskLayer[Has[AppConfig]] =
    ZLayer.fromEffect(PureConfigService.readAppConfig)
}

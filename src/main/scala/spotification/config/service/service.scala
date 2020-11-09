package spotification.config

import zio.{Has, RIO, ZIO}

package object service {
  type AuthorizationConfigEnv = Has[AuthorizationConfig]
  type PlaylistConfigEnv = Has[PlaylistConfig]
  type TrackConfigEnv = Has[TrackConfig]
  type ServerConfigEnv = Has[ServerConfig]
  type ClientConfigEnv = Has[ClientConfig]

  def authorizationConfig: RIO[AuthorizationConfigEnv, AuthorizationConfig] =
    ZIO.access(_.get)

  def playlistConfig: RIO[PlaylistConfigEnv, PlaylistConfig] =
    ZIO.access(_.get)

  def trackConfig: RIO[TrackConfigEnv, TrackConfig] =
    ZIO.access(_.get)

  def serverConfig: RIO[ServerConfigEnv, ServerConfig] =
    ZIO.access(_.get)

  def clientConfig: RIO[ClientConfigEnv, ClientConfig] =
    ZIO.access(_.get)
}

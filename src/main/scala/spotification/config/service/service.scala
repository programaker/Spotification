package spotification.config

import spotification.effect.accessRIO
import zio.Has
import zio.RIO

package object service {
  type AuthorizationConfigEnv = Has[AuthorizationConfig]
  def authorizationConfig: RIO[AuthorizationConfigEnv, AuthorizationConfig] = accessRIO

  type PlaylistConfigEnv = Has[PlaylistConfig]
  def playlistConfig: RIO[PlaylistConfigEnv, PlaylistConfig] = accessRIO

  type TrackConfigEnv = Has[TrackConfig]
  def trackConfig: RIO[TrackConfigEnv, TrackConfig] = accessRIO

  type MeConfigEnv = Has[MeConfig]
  def meConfig: RIO[MeConfigEnv, MeConfig] = accessRIO

  type UserConfigEnv = Has[UserConfig]
  def userConfig: RIO[UserConfigEnv, UserConfig] = accessRIO

  type ServerConfigEnv = Has[ServerConfig]
  def serverConfig: RIO[ServerConfigEnv, ServerConfig] = accessRIO

  type ClientConfigEnv = Has[ClientConfig]
  def clientConfig: RIO[ClientConfigEnv, ClientConfig] = accessRIO

  type ConcurrentConfigEnv = Has[ConcurrentConfig]
  def concurrentConfig: RIO[ConcurrentConfigEnv, ConcurrentConfig] = accessRIO
}

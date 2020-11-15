package spotification.config

import spotification.effect.accessRIO
import zio.Has
import zio.RIO

package object service {
  type AuthorizationConfigEnv = Has[AuthorizationConfig]
  type PlaylistConfigEnv = Has[PlaylistConfig]
  type TrackConfigEnv = Has[TrackConfig]
  type UserConfigEnv = Has[UserConfig]
  type ServerConfigEnv = Has[ServerConfig]
  type ClientConfigEnv = Has[ClientConfig]
  type ConcurrentConfigEnv = Has[ConcurrentConfig]

  def authorizationConfig: RIO[AuthorizationConfigEnv, AuthorizationConfig] = accessRIO
  def playlistConfig: RIO[PlaylistConfigEnv, PlaylistConfig] = accessRIO
  def trackConfig: RIO[TrackConfigEnv, TrackConfig] = accessRIO
  def userConfig: RIO[UserConfigEnv, UserConfig] = accessRIO
  def serverConfig: RIO[ServerConfigEnv, ServerConfig] = accessRIO
  def clientConfig: RIO[ClientConfigEnv, ClientConfig] = accessRIO
  def concurrentConfig: RIO[ConcurrentConfigEnv, ConcurrentConfig] = accessRIO
}

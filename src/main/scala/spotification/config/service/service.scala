package spotification.config

import spotification.effect.accessRIO
import zio.Has
import zio.RIO

package object service {
  type AuthorizationConfigR = Has[AuthorizationConfig]
  type PlaylistConfigR = Has[PlaylistConfig]
  type ArtistConfigR = Has[ArtistConfig]
  type AlbumConfigR = Has[AlbumConfig]
  type TrackConfigR = Has[TrackConfig]
  type MeConfigR = Has[MeConfig]
  type UserConfigR = Has[UserConfig]
  type ServerConfigR = Has[ServerConfig]
  type ClientConfigR = Has[ClientConfig]
  type ConcurrentConfigR = Has[ConcurrentConfig]

  def authorizationConfig: RIO[AuthorizationConfigR, AuthorizationConfig] = accessRIO
  def playlistConfig: RIO[PlaylistConfigR, PlaylistConfig] = accessRIO
  def artistConfig: RIO[ArtistConfigR, ArtistConfig] = accessRIO
  def albumConfig: RIO[AlbumConfigR, AlbumConfig] = accessRIO
  def trackConfig: RIO[TrackConfigR, TrackConfig] = accessRIO
  def meConfig: RIO[MeConfigR, MeConfig] = accessRIO
  def userConfig: RIO[UserConfigR, UserConfig] = accessRIO
  def serverConfig: RIO[ServerConfigR, ServerConfig] = accessRIO
  def clientConfig: RIO[ClientConfigR, ClientConfig] = accessRIO
  def concurrentConfig: RIO[ConcurrentConfigR, ConcurrentConfig] = accessRIO
}

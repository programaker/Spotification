package spotification.config

import spotification.effect.accessRIO
import zio.Has
import zio.RIO

package object service {
  type AuthorizationConfigR = Has[AuthorizationConfig]
  def authorizationConfig: RIO[AuthorizationConfigR, AuthorizationConfig] = accessRIO

  type PlaylistConfigR = Has[PlaylistConfig]
  def playlistConfig: RIO[PlaylistConfigR, PlaylistConfig] = accessRIO

  type ArtistConfigR = Has[ArtistConfig]
  def artistConfig: RIO[ArtistConfigR, ArtistConfig] = accessRIO

  type AlbumConfigR = Has[AlbumConfig]
  def albumConfig: RIO[AlbumConfigR, AlbumConfig] = accessRIO

  type TrackConfigR = Has[TrackConfig]
  def trackConfig: RIO[TrackConfigR, TrackConfig] = accessRIO

  type MeConfigR = Has[MeConfig]
  def meConfig: RIO[MeConfigR, MeConfig] = accessRIO

  type UserConfigR = Has[UserConfig]
  def userConfig: RIO[UserConfigR, UserConfig] = accessRIO

  type ServerConfigR = Has[ServerConfig]
  def serverConfig: RIO[ServerConfigR, ServerConfig] = accessRIO

  type ClientConfigR = Has[ClientConfig]
  def clientConfig: RIO[ClientConfigR, ClientConfig] = accessRIO
}

package spotification.config

import eu.timepit.refined.pureconfig._
import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader
import spotification.album.AlbumApiUri
import spotification.artist.ArtistApiUri
import spotification.authorization._
import spotification.me.MeApiUri
import spotification.playlist.{PlaylistApiUri, PlaylistId}
import spotification.track.TrackApiUri
import spotification.user.UserApiUri

object implicits {
  implicit val DirectoryConfigReader: ConfigReader[Directory] = deriveNewtypeReader(Directory.apply)
  implicit val BytesConfigReader: ConfigReader[Bytes] = deriveNewtypeReader(Bytes.apply)
  implicit val ClientIdConfigReader: ConfigReader[ClientId] = deriveNewtypeReader(ClientId.apply)
  implicit val ClientSecretConfigReader: ConfigReader[ClientSecret] = deriveNewtypeReader(ClientSecret.apply)
  implicit val PlaylistIdConfigReader: ConfigReader[PlaylistId] = deriveNewtypeReader(PlaylistId.apply)
  implicit val RedirectUriConfigReader: ConfigReader[RedirectUri] = deriveNewtypeReader(RedirectUri.apply)
  implicit val AuthorizeUriConfigReader: ConfigReader[AuthorizeUri] = deriveNewtypeReader(AuthorizeUri.apply)
  implicit val ApiTokenUriConfigReader: ConfigReader[ApiTokenUri] = deriveNewtypeReader(ApiTokenUri.apply)
  implicit val PlaylistApiUriConfigReader: ConfigReader[PlaylistApiUri] = deriveNewtypeReader(PlaylistApiUri.apply)
  implicit val TrackApiUriConfigReader: ConfigReader[TrackApiUri] = deriveNewtypeReader(TrackApiUri.apply)
  implicit val MeApiUriConfigReader: ConfigReader[MeApiUri] = deriveNewtypeReader(MeApiUri.apply)
  implicit val UserApiUriConfigReader: ConfigReader[UserApiUri] = deriveNewtypeReader(UserApiUri.apply)
  implicit val ArtistApiUriConfigReader: ConfigReader[ArtistApiUri] = deriveNewtypeReader(ArtistApiUri.apply)
  implicit val AlbumApiUriConfigReader: ConfigReader[AlbumApiUri] = deriveNewtypeReader(AlbumApiUri.apply)
  implicit val RefreshTokenConfigReader: ConfigReader[RefreshToken] = deriveNewtypeReader(RefreshToken.apply)

  implicit val RetryConfigReader: ConfigReader[RetryConfig] = deriveReader
  implicit val AuthorizationConfigReader: ConfigReader[AuthorizationConfig] = deriveReader
  implicit val PlaylistConfigReader: ConfigReader[PlaylistConfig] = deriveReader
  implicit val ArtistConfigReader: ConfigReader[ArtistConfig] = deriveReader
  implicit val AlbumConfigReader: ConfigReader[AlbumConfig] = deriveReader
  implicit val TrackConfigReader: ConfigReader[TrackConfig] = deriveReader
  implicit val MeConfigReader: ConfigReader[MeConfig] = deriveReader
  implicit val UserConfigReader: ConfigReader[UserConfig] = deriveReader
  implicit val ServerConfigReader: ConfigReader[ServerConfig] = deriveReader
  implicit val ClientConfigReader: ConfigReader[ClientConfig] = deriveReader
  implicit val AppConfigReader: ConfigReader[AppConfig] = deriveReader

  private def deriveNewtypeReader[A: ConfigReader, B](f: A => B): ConfigReader[B] = implicitly[ConfigReader[A]].map(f)
}

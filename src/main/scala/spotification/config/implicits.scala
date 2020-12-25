package spotification.config

import eu.timepit.refined.pureconfig._
import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader
import spotification.authorization._
import spotification.me.MeApiUri
import spotification.playlist.{PlaylistApiUri, PlaylistId}
import spotification.track.TrackApiUri
import spotification.user.UserApiUri

object implicits {
  implicit val DirectoryConfigReader: ConfigReader[Directory] = makeConfigReader(Directory.apply)
  implicit val BytesConfigReader: ConfigReader[Bytes] = makeConfigReader(Bytes.apply)
  implicit val ClientIdConfigReader: ConfigReader[ClientId] = makeConfigReader(ClientId.apply)
  implicit val ClientSecretConfigReader: ConfigReader[ClientSecret] = makeConfigReader(ClientSecret.apply)
  implicit val PlaylistIdConfigReader: ConfigReader[PlaylistId] = makeConfigReader(PlaylistId.apply)
  implicit val RedirectUriConfigReader: ConfigReader[RedirectUri] = makeConfigReader(RedirectUri.apply)
  implicit val AuthorizeUriConfigReader: ConfigReader[AuthorizeUri] = makeConfigReader(AuthorizeUri.apply)
  implicit val ApiTokenUriConfigReader: ConfigReader[ApiTokenUri] = makeConfigReader(ApiTokenUri.apply)
  implicit val PlaylistApiUriConfigReader: ConfigReader[PlaylistApiUri] = makeConfigReader(PlaylistApiUri.apply)
  implicit val TrackApiUriConfigReader: ConfigReader[TrackApiUri] = makeConfigReader(TrackApiUri.apply)
  implicit val MeApiUriConfigReader: ConfigReader[MeApiUri] = makeConfigReader(MeApiUri.apply)
  implicit val UserApiUriConfigReader: ConfigReader[UserApiUri] = makeConfigReader(UserApiUri.apply)
  implicit val RefreshTokenConfigReader: ConfigReader[RefreshToken] = makeConfigReader(RefreshToken.apply)

  implicit val RetryConfigReader: ConfigReader[RetryConfig] = deriveReader[RetryConfig]
  implicit val AuthorizationConfigReader: ConfigReader[AuthorizationConfig] = deriveReader[AuthorizationConfig]
  implicit val PlaylistConfigReader: ConfigReader[PlaylistConfig] = deriveReader[PlaylistConfig]
  implicit val TrackConfigReader: ConfigReader[TrackConfig] = deriveReader[TrackConfig]
  implicit val MeConfigReader: ConfigReader[MeConfig] = deriveReader[MeConfig]
  implicit val UserConfigReader: ConfigReader[UserConfig] = deriveReader[UserConfig]
  implicit val ServerConfigReader: ConfigReader[ServerConfig] = deriveReader[ServerConfig]
  implicit val ClientConfigReader: ConfigReader[ClientConfig] = deriveReader[ClientConfig]
  implicit val ConcurrentConfigReader: ConfigReader[ConcurrentConfig] = deriveReader[ConcurrentConfig]
  implicit val AppConfigReader: ConfigReader[AppConfig] = deriveReader[AppConfig]

  private def makeConfigReader[A: ConfigReader, B](f: A => B): ConfigReader[B] = implicitly[ConfigReader[A]].map(f)
}

package spotification.config.infra

import eu.timepit.refined.pureconfig._
import pureconfig.ConfigReader
import spotification.config.{Bytes, Directory}
import spotification.spotify.authorization.{RefreshToken, _}
import spotification.spotify.playlist.{PlaylistApiUri, PlaylistId}
import spotification.spotify.track.TrackApiUri

object implicits {
  implicit val directoryConfigReader: ConfigReader[Directory] =
    makeConfigReader(Directory.apply)

  implicit val bytesConfigReader: ConfigReader[Bytes] =
    makeConfigReader(Bytes.apply)

  implicit val clientIdConfigReader: ConfigReader[ClientId] =
    makeConfigReader(ClientId.apply)

  implicit val clientSecretConfigReader: ConfigReader[ClientSecret] =
    makeConfigReader(ClientSecret.apply)

  implicit val playlistIdConfigReader: ConfigReader[PlaylistId] =
    makeConfigReader(PlaylistId.apply)

  implicit val redirectUriConfigReader: ConfigReader[RedirectUri] =
    makeConfigReader(RedirectUri.apply)

  implicit val authorizeUriConfigReader: ConfigReader[AuthorizeUri] =
    makeConfigReader(AuthorizeUri.apply)

  implicit val apiTokenUriConfigReader: ConfigReader[ApiTokenUri] =
    makeConfigReader(ApiTokenUri.apply)

  implicit val playlistApiUriConfigReader: ConfigReader[PlaylistApiUri] =
    makeConfigReader(PlaylistApiUri.apply)

  implicit val trackApiUriConfigReader: ConfigReader[TrackApiUri] =
    makeConfigReader(TrackApiUri.apply)

  implicit val refreshTokenConfigReader: ConfigReader[RefreshToken] =
    makeConfigReader(RefreshToken.apply)

  private def makeConfigReader[A: ConfigReader, B](f: A => B): ConfigReader[B] =
    implicitly[ConfigReader[A]].map(f)
}

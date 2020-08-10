package spotification.infra.config

import eu.timepit.refined.pureconfig._
import pureconfig.ConfigReader
import spotification.domain.config.{Bytes, Directory}
import spotification.domain.spotify.authorization.{RefreshToken, _}
import spotification.domain.spotify.playlist.{PlaylistApiUri, PlaylistId}
import spotification.domain.spotify.track.TrackApiUri

object Config {
  object Implicits {
    implicit val DirectoryConfigReader: ConfigReader[Directory] =
      makeConfigReader(Directory.apply)

    implicit val BytesConfigReader: ConfigReader[Bytes] =
      makeConfigReader(Bytes.apply)

    implicit val ClientIdConfigReader: ConfigReader[ClientId] =
      makeConfigReader(ClientId.apply)

    implicit val ClientSecretConfigReader: ConfigReader[ClientSecret] =
      makeConfigReader(ClientSecret.apply)

    implicit val PlaylistIdConfigReader: ConfigReader[PlaylistId] =
      makeConfigReader(PlaylistId.apply)

    implicit val RedirectUriConfigReader: ConfigReader[RedirectUri] =
      makeConfigReader(RedirectUri.apply)

    implicit val AuthorizeUriConfigReader: ConfigReader[AuthorizeUri] =
      makeConfigReader(AuthorizeUri.apply)

    implicit val ApiTokenUriConfigReader: ConfigReader[ApiTokenUri] =
      makeConfigReader(ApiTokenUri.apply)

    implicit val PlaylistApiUriConfigReader: ConfigReader[PlaylistApiUri] =
      makeConfigReader(PlaylistApiUri.apply)

    implicit val TrackApiUriConfigReader: ConfigReader[TrackApiUri] =
      makeConfigReader(TrackApiUri.apply)

    implicit val RefreshTokenConfigReader: ConfigReader[RefreshToken] =
      makeConfigReader(RefreshToken.apply)

    private def makeConfigReader[A: ConfigReader, B](f: A => B): ConfigReader[B] =
      implicitly[ConfigReader[A]].map(f)
  }
}

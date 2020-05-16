package spotification.infra.config

import eu.timepit.refined.pureconfig._
import pureconfig.ConfigReader
import spotification.domain.config.{Bytes, Directory}
import spotification.domain.spotify.authorization._
import spotification.domain.spotify.playlist.{PlaylistApiUri, PlaylistId}

object Config {
  object Implicits {
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

    private def makeConfigReader[A: ConfigReader, B](f: A => B): ConfigReader[B] =
      implicitly[ConfigReader[A]].map(f)
  }
}

package spotification.infra.config

import eu.timepit.refined.pureconfig._
import pureconfig.ConfigReader
import spotification.domain.config.{Bytes, Directory}
import spotification.domain.spotify.authorization.{ApiTokenUri, AuthorizeUri, ClientId, ClientSecret, RedirectUri}
import spotification.domain.spotify.playlist.PlaylistId
import spotification.domain.{HexString32, SpotifyId, UriString}

object Config {
  object Implicits {
    implicit val directoryConfigReader: ConfigReader[Directory] =
      implicitly[ConfigReader[String]].map(Directory.apply)

    implicit val bytesConfigReader: ConfigReader[Bytes] =
      implicitly[ConfigReader[Long]].map(Bytes.apply)

    implicit val clientIdConfigReader: ConfigReader[ClientId] =
      implicitly[ConfigReader[HexString32]].map(ClientId.apply)

    implicit val clientSecretConfigReader: ConfigReader[ClientSecret] =
      implicitly[ConfigReader[HexString32]].map(ClientSecret.apply)

    implicit val playlistIdConfigReader: ConfigReader[PlaylistId] =
      implicitly[ConfigReader[SpotifyId]].map(PlaylistId.apply)

    implicit val redirectUriConfigReader: ConfigReader[RedirectUri] =
      implicitly[ConfigReader[UriString]].map(RedirectUri.apply)

    implicit val authorizeUriConfigReader: ConfigReader[AuthorizeUri] =
      implicitly[ConfigReader[UriString]].map(AuthorizeUri.apply)

    implicit val apiTokenUriConfigReader: ConfigReader[ApiTokenUri] =
      implicitly[ConfigReader[UriString]].map(ApiTokenUri.apply)
  }
}

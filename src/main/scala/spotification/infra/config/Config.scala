package spotification.infra.config

import eu.timepit.refined.pureconfig._
import pureconfig.ConfigReader
import spotification.domain.config.{Bytes, Directory}
import spotification.domain.spotify.authorization.{ClientId, ClientSecret}
import spotification.domain.spotify.playlist.PlaylistId
import spotification.domain.{HexString32, SpotifyId}

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
  }
}

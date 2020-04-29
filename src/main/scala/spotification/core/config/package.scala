package spotification.core

import io.estatico.newtype.macros.newtype
import spotification.core.config.ConfigModule.{LogConfigService, ServerConfigService, SpotifyConfigService}

package object config {
  type ConfigServices = SpotifyConfigService with ServerConfigService with LogConfigService
  @newtype case class Directory(value: String)
  @newtype case class Bytes(value: Long)
}

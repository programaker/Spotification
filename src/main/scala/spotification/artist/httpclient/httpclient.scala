package spotification.artist

import spotification.artist.service.{ArtistService, ArtistServiceEnv}
import spotification.common.httpclient.{H4sClient, HttpClientLayer}
import spotification.config.UserConfig
import spotification.config.source.UserConfigLayer
import zio.{TaskLayer, ZLayer}

package object httpclient {
  val ArtistServiceLayer: TaskLayer[ArtistServiceEnv] = {
    val l1 = ZLayer.fromServices[UserConfig, H4sClient, ArtistService] { (userConfig, httpClient) =>
      new H4sArtistService(userConfig.meApiUri, httpClient)
    }

    (UserConfigLayer ++ HttpClientLayer) >>> l1
  }
}

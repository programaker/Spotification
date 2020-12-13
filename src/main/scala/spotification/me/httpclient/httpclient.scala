package spotification.me

import spotification.common.httpclient.{H4sClient, HttpClientLayer}
import spotification.config.MeConfig
import spotification.config.source.MeConfigLayer
import spotification.me.service.{MeService, MeServiceEnv}
import zio.{TaskLayer, ZLayer}

package object httpclient {
  val MeServiceLayer: TaskLayer[MeServiceEnv] = {
    val l1 = ZLayer.fromServices[MeConfig, H4sClient, MeService] { (meConfig, httpClient) =>
      new H4sMeService(meConfig.meApiUri, httpClient)
    }

    (MeConfigLayer ++ HttpClientLayer) >>> l1
  }
}

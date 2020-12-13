package spotification.user

import spotification.common.httpclient.{H4sClient, HttpClientLayer}
import spotification.config.UserConfig
import spotification.config.source.UserConfigLayer
import spotification.user.service.{UserService, UserServiceEnv}
import zio.{TaskLayer, ZLayer}

package object httpclient {
  val UserServiceLayer: TaskLayer[UserServiceEnv] = {
    val l1 = ZLayer.fromServices[UserConfig, H4sClient, UserService] { (userConfig, httpClient) =>
      new H4sUserService(userConfig.userApiUri, httpClient)
    }

    (UserConfigLayer ++ HttpClientLayer) >>> l1
  }
}

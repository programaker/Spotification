package spotification.user

import cats.syntax.show._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientR, doRequest}
import spotification.common.json.implicits.ErrorResponseDecoder
import spotification.config.MeConfig
import spotification.config.service.MeConfigR
import spotification.user.json.implicits.GetMyProfileResponseDecoder
import spotification.user.service.{GetMyProfileService, GetMyProfileServiceR}
import zio.interop.catz.monadErrorInstance
import zio.{Task, URLayer, ZLayer}

package object httpclient {
  import H4sClient.Dsl._

  val GetMyProfileServiceLayer: URLayer[MeConfigR with HttpClientR, GetMyProfileServiceR] =
    ZLayer.fromServices[MeConfig, H4sClient, GetMyProfileService] { (config, http) => req =>
      Task
        .fromEither(Uri.fromString(config.meApiUri.show))
        .flatMap(doRequest[GetMyProfileResponse](http, _)(GET(_, authorizationBearerHeader(req.accessToken))))
    }
}

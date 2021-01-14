package spotification.user

import io.circe.syntax.EncoderOps
import org.http4s.Method.POST
import org.http4s.Uri
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientEnv, doRequest, eitherUriStringToH4s}
import spotification.common.json.implicits.{ErrorResponseDecoder, entityEncoderF}
import spotification.config.UserConfig
import spotification.config.service.UserConfigEnv
import spotification.playlist.userPlaylistsUri
import spotification.user.json.implicits.{CreatePlaylistRequestBodyEncoder, CreatePlaylistResponseDecoder}
import spotification.user.service.{CreatePlaylistService, CreatePlaylistServiceEnv}
import zio.interop.catz.monadErrorInstance
import zio.{Task, URLayer, ZLayer}

package object httpclient {
  import H4sClient.Dsl._

  val CreatePlaylistServiceLayer: URLayer[UserConfigEnv with HttpClientEnv, CreatePlaylistServiceEnv] =
    ZLayer.fromServices[UserConfig, H4sClient, CreatePlaylistService] { (config, http) => req =>
      val h4sUri = eitherUriStringToH4s(userPlaylistsUri(config.userApiUri, req.userId))
      val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))
      Task.fromEither(h4sUri).flatMap(doRequest[CreatePlaylistResponse](http, _)(post))
    }
}

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
import zio.{Has, Task, URLayer, ZLayer}

package object httpclient {
  import H4sClient.Dsl._

  val CreatePlaylistServiceLayer: URLayer[UserConfigEnv with HttpClientEnv, CreatePlaylistServiceEnv] =
    ContextLayer >>> ZLayer.fromService[Context, CreatePlaylistService](ctx => createPlaylist(ctx, _))

  private def createPlaylist(ctx: Context, req: CreatePlaylistRequest): Task[CreatePlaylistResponse] = {
    val h4sUri = eitherUriStringToH4s(userPlaylistsUri(ctx.userApiUri, req.userId))
    val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))
    Task.fromEither(h4sUri).flatMap(doRequest[CreatePlaylistResponse](ctx.httpClient, _)(post))
  }

  private final case class Context(userApiUri: UserApiUri, httpClient: H4sClient)
  private lazy val ContextLayer: URLayer[UserConfigEnv with HttpClientEnv, Has[Context]] =
    ZLayer.fromServices[UserConfig, H4sClient, Context] { (userConfig, httpClient) =>
      Context(userConfig.userApiUri, httpClient)
    }
}

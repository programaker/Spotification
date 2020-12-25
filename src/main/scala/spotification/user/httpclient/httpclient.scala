package spotification.user

import io.circe.syntax.EncoderOps
import org.http4s.Method.POST
import org.http4s.Uri
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientLayer, doRequest, eitherUriStringToH4s}
import spotification.common.json.implicits.{ErrorResponseDecoder, entityEncoderF}
import spotification.config.UserConfig
import spotification.config.source.UserConfigLayer
import spotification.playlist.userPlaylistsUri
import spotification.user.json.implicits.{CreatePlaylistRequestBodyEncoder, CreatePlaylistResponseDecoder}
import spotification.user.service.{CreatePlaylistService, CreatePlaylistServiceEnv}
import zio.{Has, Task, TaskLayer, ZLayer}
import zio.interop.catz.monadErrorInstance

package object httpclient {
  import H4sClient.Dsl._

  val CreatePlaylistServiceLayer: TaskLayer[CreatePlaylistServiceEnv] =
    Context.layer >>> ZLayer.fromService[Context, CreatePlaylistService](ctx => createPlaylist(ctx, _))

  private def createPlaylist(ctx: Context, req: CreatePlaylistRequest): Task[CreatePlaylistResponse] = {
    val h4sUri = eitherUriStringToH4s(userPlaylistsUri(ctx.userApiUri, req.userId))
    val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))
    Task.fromEither(h4sUri).flatMap(doRequest[CreatePlaylistResponse](ctx.httpClient, _)(post))
  }

  final private case class Context(userApiUri: UserApiUri, httpClient: H4sClient)
  private object Context {
    val layer: TaskLayer[Has[Context]] =
      (UserConfigLayer ++ HttpClientLayer) >>> ZLayer.fromServices[UserConfig, H4sClient, Context] {
        (userConfig, httpClient) =>
          Context(userConfig.userApiUri, httpClient)
      }
  }
}

package spotification.user.httpclient

import io.circe.syntax.EncoderOps
import org.http4s.Method.POST
import org.http4s.Uri
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, doRequest, eitherUriStringToH4s}
import spotification.common.json.implicits.{ErrorResponseDecoder, entityEncoderF}
import spotification.playlist.userPlaylistsUri
import spotification.user.json.implicits.{CreatePlaylistRequestBodyEncoder, CreatePlaylistResponseDecoder}
import spotification.user.{CreatePlaylistRequest, CreatePlaylistResponse, UserApiUri}
import zio.Task
import spotification.user.service.UserService
import zio.interop.catz.monadErrorInstance

final class H4sUserService(userApiUri: UserApiUri, httpClient: H4sClient) extends UserService {
  import H4sClient.Dsl._

  override def createPlaylist(req: CreatePlaylistRequest): Task[CreatePlaylistResponse] = {
    val h4sUri = eitherUriStringToH4s(userPlaylistsUri(userApiUri, req.userId))
    val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))
    Task.fromEither(h4sUri).flatMap(doRequest[CreatePlaylistResponse](httpClient, _)(post))
  }
}

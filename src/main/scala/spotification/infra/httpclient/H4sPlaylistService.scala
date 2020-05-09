package spotification.infra.httpclient

import cats.implicits._
import io.circe.jawn
import io.circe.generic.auto._
import org.http4s.Method._
import org.http4s.Uri
import spotification.domain.spotify.playlist.{PlaylistItemsRequest, PlaylistItemsResponse}
import spotification.infra.BaseEnv
import spotification.infra.httpclient.AuthorizationHttpClient.authorizationBearerHeader
import spotification.infra.httpclient.HttpClient.{H4sTaskClientDsl, _}
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, Task}
import zio.interop.catz._

// ==========
// Despite IntelliJ telling that
// `import io.circe.refined._`
// `import spotification.infra.Json.Implicits._`
// are not being used, they are required to compile
// ==========
import io.circe.refined._
import spotification.infra.Json.Implicits._

final class H4sPlaylistService(httpClient: H4sClient) extends PlaylistModule.Service {
  import H4sTaskClientDsl._
  private val PlaylistsApiUri: String = show"$ApiUri/playlists"

  override def getPlaylistItems(req: PlaylistItemsRequest): RIO[BaseEnv, PlaylistItemsResponse] = {
    val uri = Uri
      .fromString(show"$PlaylistsApiUri/${req.playlistId}/tracks")
      .map(_.withQueryParam("fields", req.fields.value))
      .map(_.withQueryParam("limit", req.limit.value))
      .map(_.withQueryParam("offset", req.offset.value))
      .leftMap(pf => new Exception(pf.message))

    RIO
      .fromEither(uri)
      .map(GET(_, authorizationBearerHeader(req.accessToken)))
      .flatMap(httpClient.expect[String])
      .flatMap(s => Task.fromEither(jawn.decode[PlaylistItemsResponse](s)))
  }
}

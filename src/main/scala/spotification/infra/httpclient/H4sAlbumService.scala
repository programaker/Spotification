package spotification.infra.httpclient

import spotification.domain.spotify.album.{AlbumApiUri, GetSeveralAlbumsRequest, GetSeveralAlbumsResponse}
import spotification.infra.httpclient.HttpClient.{H4sClientDsl, doRequest}
import spotification.infra.spotify.album.AlbumModule
import eu.timepit.refined.auto._
import cats.implicits._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.infra.httpclient.AuthorizationHttpClient.authorizationBearerHeader
import zio.Task
import io.circe.generic.auto._

// ==========
// Despite IntelliJ telling that
// `import zio.interop.catz._`
// `import io.circe.refined._`
// are not being used, they are required to compile
// ==========
import zio.interop.catz._
import io.circe.refined._

final class H4sAlbumService(albumApiUri: AlbumApiUri, httpClient: H4sClient) extends AlbumModule.Service {
  import H4sClientDsl._

  override def getSeveralAlbums(req: GetSeveralAlbumsRequest): Task[GetSeveralAlbumsResponse] = {
    val ids = req.ids.value.mkString_(",")
    val uri = Uri.fromString(albumApiUri.show).map(_.withQueryParam("ids", ids))
    val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))
    doRequest[GetSeveralAlbumsResponse](httpClient, uri)(get)
  }
}

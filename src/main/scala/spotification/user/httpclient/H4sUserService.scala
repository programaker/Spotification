package spotification.user.httpclient

import cats.syntax.show._
import io.circe.generic.auto.exportDecoder
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, doRequest}
import spotification.user.{GetMyProfileRequest, GetMyProfileResponse, MeApiUri}
import spotification.user.service.UserService
import spotification.json.implicits.UserIdDecoder
import zio.Task
import zio.interop.catz.monadErrorInstance

final class H4sUserService(meApiUri: MeApiUri, httpClient: H4sClient) extends UserService {
  import H4sClient.Dsl._

  override def getMyProfile(req: GetMyProfileRequest): Task[GetMyProfileResponse] =
    for {
      h4sUri <- Task.fromEither(Uri.fromString(meApiUri.show))

      resp <- doRequest[GetMyProfileResponse](httpClient, h4sUri)(GET(_, authorizationBearerHeader(req.accessToken)))
    } yield resp
}

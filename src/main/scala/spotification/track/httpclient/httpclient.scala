package spotification.track

import eu.timepit.refined.auto._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientEnv, doRequest}
import spotification.common.json.implicits.ErrorResponseDecoder
import spotification.config.TrackConfig
import spotification.config.service.TrackConfigEnv
import spotification.effect.leftStringEitherToTask
import spotification.track.json.implicits.GetTrackResponseDecoder
import spotification.track.service.{GetTrackService, GetTrackServiceEnv}
import zio.interop.catz.monadErrorInstance
import zio.{Has, Task, URLayer, ZLayer}

package object httpclient {
  import H4sClient.Dsl._

  val GetTrackServiceLayer: URLayer[TrackConfigEnv with HttpClientEnv, GetTrackServiceEnv] =
    ContextLayer >>> ZLayer.fromService(ctx => getTrack(ctx, _))

  private def getTrack(ctx: Context, req: GetTrackRequest): Task[GetTrackResponse] = {
    val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))

    for {
      trackUri <- leftStringEitherToTask(makeTrackUri(ctx.trackApiUri, req.trackId))
      h4sUri   <- Task.fromEither(Uri.fromString(trackUri))
      resp     <- doRequest[GetTrackResponse](ctx.httpClient, h4sUri)(get)
    } yield resp
  }

  private final case class Context(trackApiUri: TrackApiUri, httpClient: H4sClient)
  private lazy val ContextLayer: URLayer[TrackConfigEnv with HttpClientEnv, Has[Context]] =
    ZLayer.fromServices[TrackConfig, H4sClient, Context] { (config, httpClient) =>
      Context(config.trackApiUri, httpClient)
    }
}

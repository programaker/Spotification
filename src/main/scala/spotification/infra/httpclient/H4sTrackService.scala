package spotification.infra.httpclient

import spotification.domain.spotify.track.{GetTrackRequest, GetTrackResponse, TrackApiUri}
import spotification.infra.spotify.track.TrackModule
import spotification.infra.httpclient.HttpClient.H4sClientDsl
import zio.Task

final class H4sTrackService(trackApiUri: TrackApiUri, httpClient: H4sClient) extends TrackModule.Service {
  import H4sClientDsl._

  override def getTrack(req: GetTrackRequest): Task[GetTrackResponse.Success] = ???
}

package spotification

import org.http4s.HttpRoutes
import spotification.application.SpotifyAuthorizationEnv
import spotification.infra.BaseModule
import spotification.infra.config.SpotifyConfigModule
import spotification.infra.httpclient.HttpClientModule
import spotification.infra.spotify.authorization.AuthorizationModule
import zio.RLayer

package object presentation {
  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]

  type PresentationEnv = SpotifyAuthorizationEnv //while we have only one controller
  object PresentationEnv {
    val layer: RLayer[HttpClientModule, PresentationEnv] =
      AuthorizationModule.layer ++ SpotifyConfigModule.layer ++ BaseModule.layer
  }
}

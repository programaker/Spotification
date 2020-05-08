package spotification

import org.http4s.HttpRoutes
import spotification.infra.BaseModule
import spotification.infra.config.SpotifyConfigModule
import spotification.infra.httpclient.HttpClientModule
import spotification.infra.spotify.authorization.{AuthorizationEnv, AuthorizationModule}
import zio.RLayer

package object presentation {
  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]

  type PresentationModule = AuthorizationEnv //while we have only one controller
  object PresentationModule {
    val layer: RLayer[HttpClientModule, PresentationModule] =
      AuthorizationModule.layer ++ SpotifyConfigModule.layer ++ BaseModule.layer
  }
}

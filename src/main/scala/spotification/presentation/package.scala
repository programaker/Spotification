package spotification

import org.http4s.HttpRoutes
import spotification.application.SpotifyAuthorizationEnv
import spotification.infra.ConfigServiceAndHttpClientEnv
import zio.RLayer

package object presentation {
  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]

  //while we have only one controller, PresentationEnv === SpotifyAuthorizationEnv
  //but it will grow soon
  type PresentationEnv = SpotifyAuthorizationEnv
  object PresentationEnv {
    val layer: RLayer[ConfigServiceAndHttpClientEnv, PresentationEnv] = SpotifyAuthorizationEnv.layer
  }
}

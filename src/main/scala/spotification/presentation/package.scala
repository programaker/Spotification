package spotification

import org.http4s.HttpRoutes
import spotification.core.spotify.authorization.AuthorizationEnv
import zio.RIO

package object presentation {
  type PresentationEnv = AuthorizationEnv
  type PresentationIO[A] = RIO[PresentationEnv, A]

  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]
}

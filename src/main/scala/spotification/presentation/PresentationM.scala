package spotification.presentation

import org.http4s.HttpRoutes
import spotification.core.spotify.authorization.{AuthorizationEnv, AuthorizationIO}
import zio.{Has, RIO, ZIO}

private[presentation] trait PresentationM {

  // For now we only have Authorization
  // More Controllers will be added later
  type PresentationEnv = AuthorizationEnv
  type PresentationIO[A] = AuthorizationIO[A]

  type RouteMappings[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RouteMappings[F]]

  type PresentationRoutes = Routes[PresentationIO]

  type PresentationModule[F[_]] = Has[Routes[F]]
  object PresentationModule {
    def allRoutes[F[_]]: RIO[PresentationModule[F], Routes[F]] = ZIO.access(_.get)
  }

}

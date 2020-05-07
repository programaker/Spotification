package spotification.application

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV
import spotification.core.CoreServices
import spotification.core.config.ConfigModule
import spotification.core.spotify.authorization.AuthorizationModule
import spotification.infra.httpclient.HttpClientModule.HttpClientService
import zio.{IO, RIO, ZIO, ZLayer}

object ApplicationModule {
  def refineZIO[P, R, A](a: A)(implicit v: Validate[A, P]): ZIO[R, String, Refined[A, P]] =
    ZIO.fromFunctionM(_ => IO.fromEither(refineV[P](a)))

  def refineRIO[P, R, A](a: A)(implicit v: Validate[A, P]): RIO[R, Refined[A, P]] =
    refineZIO[P, R, A](a).absorbWith(new Exception(_))

  val layer: ZLayer[HttpClientService, Throwable, CoreServices] =
    ConfigModule.layer ++ AuthorizationModule.layer
}

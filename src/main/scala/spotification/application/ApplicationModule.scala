package spotification.application

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV
import HttpClientModule.HttpClientService
import zio.{IO, RIO, ZIO, ZLayer}

object ApplicationModule {
  def refineZIO[P, R, A](a: A)(implicit v: Validate[A, P]): ZIO[R, String, Refined[A, P]] =
    ZIO.fromFunctionM(_ => IO.fromEither(refineV[P](a)))

  def refineRIO[P, R, A](a: A)(implicit v: Validate[A, P]): RIO[R, Refined[A, P]] =
    refineZIO[P, R, A](a).absorbWith(new Exception(_))

  def layer: ZLayer[HttpClientService, Throwable, ApplicationServices] =
    ConfigModule.layer ++ AuthorizationModule.layer
}

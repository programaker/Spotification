package spotification.monitoring

import org.http4s.HttpRoutes
import spotification.common.GenericResponse
import spotification.common.api.withDsl
import spotification.common.json.implicits.{GenericResponseSuccessEncoder, entityEncoderF}
import zio.RIO
import zio.interop.catz.{deferInstance, monadErrorInstance}

package object api {
  def makeHealthCheckApi[R]: HttpRoutes[RIO[R, *]] = withDsl { dsl =>
    import dsl._

    HttpRoutes.of[RIO[R, *]] { case GET -> Root =>
      Ok(GenericResponse.Success("I'm doing well, thanks for asking ^_^"))
    }
  }
}

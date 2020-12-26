package spotification.monitoring

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.common.GenericResponse
import spotification.common.json.implicits.{GenericResponseSuccessEncoder, entityEncoderF}
import zio.RIO
import zio.interop.catz.{deferInstance, monadErrorInstance}

package object api {
  def makeHealthCheckApi[R]: HttpRoutes[RIO[R, *]] = {
    val dsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
    import dsl._

    HttpRoutes.of[RIO[R, *]] { case GET -> Root =>
      Ok(GenericResponse.Success("I'm doing well, thanks for asking ^_^"))
    }
  }
}

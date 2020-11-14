package spotification.monitoring

import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.common.api.GenericResponse
import spotification.json.implicits._
import zio.RIO
import zio.interop.catz.{deferInstance, monadErrorInstance}

package object api {
  def makeHealthCheckRoutes[R]: HttpRoutes[RIO[R, *]] = {
    val dsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
    import dsl._

    HttpRoutes.of[RIO[R, *]] {
      case GET -> Root => Ok(GenericResponse.Success("I'm doing well, thanks for asking ^_^"))
    }
  }
}

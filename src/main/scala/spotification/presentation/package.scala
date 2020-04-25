package spotification

import cats.Applicative
import org.http4s.Response
import org.http4s.dsl.Http4sDsl

package object presentation extends AuthorizationM {

  def handleGenericError[F[_]: Applicative](dsl: Http4sDsl[F], e: Throwable): F[Response[F]] = {
    import dsl._
    InternalServerError(e.getMessage)
  }

}

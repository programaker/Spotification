package spotification.spotify.authorization

import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.{OptionalQueryParamDecoderMatcher, QueryParamDecoderMatcher}
import spotification.common.presentation.JsonSupport
import spotification.spotify.authorization.application._

package object presentation {

  type AuthorizationResponse = AuthorizationIO[Response[AuthorizationIO]]

  val H4sAuthorizationDsl: Http4sDsl[AuthorizationIO] = Http4sDsl[AuthorizationIO]
  val AuthorizationJsonCodec: JsonSupport[AuthorizationEnv, Throwable] = new JsonSupport[AuthorizationEnv, Throwable] {}

  object CodeQP extends QueryParamDecoderMatcher[String]("code")
  object ErrorQP extends QueryParamDecoderMatcher[String]("error")
  object StateQP extends OptionalQueryParamDecoderMatcher[String]("state")

}

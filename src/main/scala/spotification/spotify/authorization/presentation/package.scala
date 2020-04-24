package spotification.spotify.authorization

import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.dsl.impl.OptionalQueryParamDecoderMatcher
import spotification.spotify.authorization.application._
import org.http4s.Response

package object presentation {

  val H4sAuthorizationDsl: Http4sDsl[AuthorizationIO] = Http4sDsl[AuthorizationIO]
  type AuthorizationResponse = AuthorizationIO[Response[AuthorizationIO]]

  object CodeQP extends QueryParamDecoderMatcher[String]("code")
  object ErrorQP extends QueryParamDecoderMatcher[String]("error")
  object StateQP extends OptionalQueryParamDecoderMatcher[String]("state")

}

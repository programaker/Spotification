package spotification.presentation

import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.{OptionalQueryParamDecoderMatcher, QueryParamDecoderMatcher}
import spotification.core.spotify.authorization.AuthorizationIO

private[presentation] trait AuthorizationM {

  val H4sAuthorizationDsl: Http4sDsl[AuthorizationIO] = Http4sDsl[AuthorizationIO]

  object CodeQP extends QueryParamDecoderMatcher[String]("code")
  object ErrorQP extends QueryParamDecoderMatcher[String]("error")
  object StateQP extends OptionalQueryParamDecoderMatcher[String]("state")

}

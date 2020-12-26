package spotification.authorization.api

import org.http4s.dsl.impl.{OptionalQueryParamDecoderMatcher, QueryParamDecoderMatcher}

object CodeQP extends QueryParamDecoderMatcher[String]("code")
object ErrorQP extends QueryParamDecoderMatcher[String]("error")
object StateQP extends OptionalQueryParamDecoderMatcher[String]("state")

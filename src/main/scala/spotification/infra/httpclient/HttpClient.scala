package spotification.infra.httpclient

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

import eu.timepit.refined.auto._
import org.http4s.client.dsl.Http4sClientDsl
import spotification.domain.spotify.authorization.Scope
import spotification.domain.spotify.authorization.Scope.joinScopes
import zio.Task

object HttpClient {
  val ApiUri: String = "https://api.spotify.com/v1"
  val H4sAuthorization: org.http4s.headers.Authorization.type = org.http4s.headers.Authorization
  val H4sTaskClientDsl: Http4sClientDsl[Task] = new Http4sClientDsl[Task] {}

  // HTTP4s Uri should be able to encode query params, but in my tests
  // URIs are not properly encoded:
  //
  // uri"https://foo.com".withQueryParam("redirect_uri", "https://bar.com")
  // > org.http4s.Uri = https://foo.com?redirect_uri=https%3A//bar.com <- did not encode `//`
  //
  // URLEncoder.encode("https://bar.com", UTF_8.toString)
  // > String = https%3A%2F%2Fbar.com <- encoded `//` correctly
  val encode: String => String =
    URLEncoder.encode(_, UTF_8)

  val makeQueryString: ParamMap => String =
    _.map { case (k, v) => s"$k=$v" } mkString "&"

  def addScopeParam(params: ParamMap, scopes: List[Scope]): Either[String, ParamMap] =
    joinScopes(scopes).map(s => params + ("scope" -> encode(s)))
}

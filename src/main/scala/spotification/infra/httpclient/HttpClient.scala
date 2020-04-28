package spotification.infra.httpclient

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import org.http4s.AuthScheme.{Basic, Bearer}
import org.http4s.Credentials.Token
import spotification.core.spotify.authorization.Authorization.base64Credentials
import spotification.core.spotify.authorization.Scope.joinScopes
import spotification.core.spotify.authorization.{AccessToken, ClientId, ClientSecret, Scope}

object HttpClient {
  // HTTP4s Uri should be able to encode query params, but in my tests
  // URIs are not properly encoded:
  //
  // uri"https://foo.com".withQueryParam("redirect_uri", "https://bar.com")
  // > org.http4s.Uri = https://foo.com?redirect_uri=https%3A//bar.com <- did not encode `//`
  //
  // URLEncoder.encode("https://bar.com", UTF_8.toString)
  // > String = https%3A%2F%2Fbar.com <- encoded `//` correctly
  val encode: String => String = URLEncoder.encode(_, UTF_8)

  /**
   * <p>Turns any Product type (ex: case classes) into a `Map[String, String]` that can be
   * used to build query string parameters or x-www-form-urlencoded.</p>
   * <p></p>
   * <p>The function only acts on fields of type `String` (required fields)
   * and `Option[String]` (optional fields); everything else will be ignored.</p>
   */
  def toParams[A <: Product](a: A)(implicit toMap: ToMapAux[A]): ParamMap =
    toMap(a).flatMap {
      case (k, v: String)        => Some(k.name -> encode(v))
      case (k, Some(v: String))  => Some(k.name -> encode(v))
      case (k, v: Refined[_, _]) => Some(k.name -> encode(s"$v"))
      case _                     => None
    }

  def addScopeParam(params: ParamMap, scopes: List[Scope]): Either[String, ParamMap] =
    joinScopes(scopes).map(s => params + ("scope" -> encode(s)))

  def authorizationBasicHeader(clientId: ClientId, clientSecret: ClientSecret): H4sAuthorization =
    H4sAuthorization(Token(Basic, base64Credentials(clientId, clientSecret)))

  def authorizationBearerHeader(accessToken: AccessToken): H4sAuthorization =
    H4sAuthorization(Token(Bearer, accessToken.value))
}

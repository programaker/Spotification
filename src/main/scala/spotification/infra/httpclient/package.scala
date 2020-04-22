package spotification.infra

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import eu.timepit.refined.api.Refined
import org.http4s.AuthScheme.{Basic, Bearer}
import org.http4s.Credentials.Token
import org.http4s.client.Client
import org.http4s.headers
import spotification.domain.spotify.authorization._
import zio.{Task, ZLayer}
import eu.timepit.refined.auto._
import shapeless.ops.product.ToMap
import spotification.domain.Val
import cats.implicits._
import org.http4s.client.dsl.Http4sClientDsl
import scope.{joinScopes, Scope}

package object httpclient {

  type H4sClient = Client[Task]
  type H4sClientDsl = Http4sClientDsl[Task]

  val AuthorizationLayer: ZLayer[H4sClient, Nothing, Authorization] =
    ZLayer.fromFunction(new H4sAuthorizationService(_))

  type H4sAuthorization = org.http4s.headers.Authorization
  val H4sAuthorization: org.http4s.headers.Authorization.type = org.http4s.headers.Authorization

  def authorizationBasicHeader(clientId: ClientId, clientSecret: ClientSecret): H4sAuthorization =
    H4sAuthorization(Token(Basic, base64Credentials(clientId, clientSecret)))

  def authorizationBearerHeader(accessToken: AccessToken): headers.Authorization =
    H4sAuthorization(Token(Bearer, accessToken.value))

  // HTTP4s Uri should be able to encode query params, but in my tests
  // URIs are not properly encoded:
  //
  // uri"https://foo.com".withQueryParam("redirect_uri", "https://bar.com")
  // > org.http4s.Uri = https://foo.com?redirect_uri=https%3A//bar.com <- did not encode `//`
  //
  // URLEncoder.encode("https://bar.com", UTF_8.toString)
  // > String = https%3A%2F%2Fbar.com <- encoded `//` correctly
  val encode: String => String = URLEncoder.encode(_, UTF_8)

  def base64(s: String): String = Base64.getEncoder.encodeToString(s.getBytes(UTF_8))

  def base64Credentials(clientId: ClientId, clientSecret: ClientSecret): String = base64(show"$clientId:$clientSecret")

  type ToMapAux[A] = ToMap.Aux[A, Symbol, Any]
  type ParamMap = Map[String, String]

  /**
   * <p>Turns any Product type (ex: case classes) into a `Map[String, String]` that can be
   * used to build query string parameters or x-www-form-urlencodeds.</p>
   * <p></p>
   * <p>The function only acts on fields of type `String` (required fields)
   * and `Option[String]` (optional fields); everything else will be ignored.</p>
   */
  def toParams[A <: Product](a: A)(implicit toMap: ToMapAux[A]): ParamMap =
    toMap(a).flatMap {
      case (k, v: String)        => Some(k.name -> encode(v))
      case (k, Some(v: String))  => Some(k.name -> encode(v))
      case (k, v: Refined[_, _]) => Some(k.name -> encode(s"$v"))
      case (k, v: Val[_])        => Some(k.name -> encode(s"$v"))
      case _                     => None
    }

  def addScopeParam(params: ParamMap, scopes: List[Scope]): Either[String, ParamMap] =
    joinScopes(scopes).map(s => params + ("scope" -> s))

}

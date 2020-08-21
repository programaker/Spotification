package spotification.domain.spotify

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import cats.Show
import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.refineV
import eu.timepit.refined.cats._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import spotification.domain._
import eu.timepit.refined.string.MatchesRegex
import spotification.domain.spotify.authorization.Scope.addScopeParam

package object authorization {
  type AuthorizationResponseTypeR = Equal["code"] //it's the only one that appeared until now
  type AuthorizationResponseType = String Refined AuthorizationResponseTypeR
  object AuthorizationResponseType {
    val Code: AuthorizationResponseType = "code"
  }

  type AccessTokenGrantTypeR = Equal["authorization_code"]
  type AccessTokenGrantType = String Refined AccessTokenGrantTypeR
  object AccessTokenGrantType {
    val AuthorizationCode: AccessTokenGrantType = "authorization_code"
  }

  type RefreshTokenGrantTypeR = Equal["refresh_token"]
  type RefreshTokenGrantType = String Refined RefreshTokenGrantTypeR
  object RefreshTokenGrantType {
    val RefreshToken: RefreshTokenGrantType = "refresh_token"
  }

  type TokenTypeR = Equal["Bearer"]
  type TokenType = String Refined TokenTypeR
  object TokenType {
    val Bearer: TokenType = "Bearer"
  }

  // ScopeString = space-separated kebab-case strings
  // ex: "playlist-read-private playlist-modify-private playlist-modify-public"
  type ScopeStringR = MatchesRegex["""^[a-z]([a-z-])+(\s([a-z-])+)*[a-z]$"""]
  type ScopeString = String Refined ScopeStringR

  type PlaylistScopeR =
    Equal["playlist-read-collaborative"] Or
      Equal["playlist-modify-public"] Or
      Equal["playlist-read-private"] Or
      Equal["playlist-modify-private"]

  type ScopeR = PlaylistScopeR //we can add more scopes later
  type Scope = String Refined ScopeR
  object Scope {
    def parseScope(rawScope: ScopeString): Either[String, List[Scope]] =
      rawScope.split("\\s").toList.map(refineV[ScopeR](_)).sequence[Either[String, *], Scope]

    def joinScopes(scopes: List[Scope]): Either[String, ScopeString] =
      refineV[ScopeStringR](scopes.mkString_(" "))

    def addScopeParam(params: ParamMap, scopes: List[Scope]): Either[String, ParamMap] =
      joinScopes(scopes).map(s => params + ("scope" -> Some(encode(s))))
  }

  @newtype case class AccessToken(value: NonBlankString)
  object AccessToken {
    implicit val AccessTokenShow: Show[AccessToken] = implicitly[Show[NonBlankString]].coerce
  }

  @newtype case class RefreshToken(value: NonBlankString)
  object RefreshToken {
    implicit val RefreshTokenShow: Show[RefreshToken] = implicitly[Show[NonBlankString]].coerce
  }

  @newtype case class ClientId(value: HexString32)
  object ClientId {
    implicit val ClientIdShow: Show[ClientId] = implicitly[Show[HexString32]].coerce
  }

  @newtype case class ClientSecret(value: HexString32)
  object ClientSecret {
    implicit val ClientSecretShow: Show[ClientSecret] = implicitly[Show[HexString32]].coerce
  }

  @newtype case class AuthorizeUri(value: UriString)
  object AuthorizeUri {
    implicit val AuthorizeUriShow: Show[AuthorizeUri] = implicitly[Show[UriString]].coerce
  }

  @newtype case class ApiTokenUri(value: UriString)
  object ApiTokenUri {
    implicit val ApiTokenUriShow: Show[ApiTokenUri] = implicitly[Show[UriString]].coerce
  }

  @newtype case class RedirectUri(value: UriString)
  object RedirectUri {
    implicit val RedirectUriShow: Show[RedirectUri] = implicitly[Show[UriString]].coerce
  }

  def base64Credentials(clientId: ClientId, clientSecret: ClientSecret): String =
    base64(show"$clientId:$clientSecret")

  def base64(s: String): String =
    Base64.getEncoder.encodeToString(s.getBytes(UTF_8))

  def makeAuthorizeUri(authorizeUri: AuthorizeUri, req: AuthorizeRequest): Either[String, UriString] = {
    val params = Map(
      "client_id"     -> Some(req.client_id.show),
      "response_type" -> Some(req.response_type.show),
      "redirect_uri"  -> Some(encode(req.redirect_uri.show)),
      "show_dialog"   -> req.show_dialog.map(_.show),
      "state"         -> req.state.map(_.show)
    )

    req.scope
      .map(addScopeParam(params, _))
      .getOrElse(Right(params))
      .map(makeQueryString)
      .map(q => show"$authorizeUri?$q")
      .flatMap(refineV[UriR](_))
  }
}

package spotification

import cats.Show
import cats.syntax.show._
import cats.syntax.traverse._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.cats._
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.refineV
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype
import spotification.common._

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

package object authorization {
  type AuthorizationResponseTypeP = Equal["code"] //it's the only one that appeared until now
  type AuthorizationResponseType = String Refined AuthorizationResponseTypeP
  object AuthorizationResponseType {
    val Code: AuthorizationResponseType = "code"
  }

  type AccessTokenGrantTypeP = Equal["authorization_code"]
  type AccessTokenGrantType = String Refined AccessTokenGrantTypeP
  object AccessTokenGrantType {
    val AuthorizationCode: AccessTokenGrantType = "authorization_code"
  }

  type RefreshTokenGrantTypeP = Equal["refresh_token"]
  type RefreshTokenGrantType = String Refined RefreshTokenGrantTypeP
  object RefreshTokenGrantType {
    val RefreshToken: RefreshTokenGrantType = "refresh_token"
  }

  type TokenTypeP = Equal["Bearer"]
  type TokenType = String Refined TokenTypeP

  // ScopeString = space-separated kebab-case strings
  // ex: "playlist-read-private playlist-modify-private playlist-modify-public"
  type ScopeStringP = MatchesRegex["""^[a-z]([a-z-])+(\s([a-z-])+)*[a-z]$"""]
  type ScopeString = String Refined ScopeStringP

  type PlaylistScopeP =
    Equal["playlist-read-collaborative"] Or
      Equal["playlist-modify-public"] Or
      Equal["playlist-read-private"] Or
      Equal["playlist-modify-private"]

  type UserScopeP =
    Equal["user-read-private"] Or
      Equal["user-read-email"] Or
      Equal["user-follow-read"]

  type ScopeP = PlaylistScopeP Or UserScopeP
  type Scope = String Refined ScopeP

  @newtype case class AccessToken(value: NonBlankString)
  object AccessToken {
    implicit val AccessTokenShow: Show[AccessToken] = deriving
  }

  @newtype case class RefreshToken(value: NonBlankString)
  object RefreshToken {
    implicit val RefreshTokenShow: Show[RefreshToken] = deriving
  }

  @newtype case class ClientId(value: HexString32)
  object ClientId {
    implicit val ClientIdShow: Show[ClientId] = deriving
  }

  @newtype case class ClientSecret(value: HexString32)
  object ClientSecret {
    implicit val ClientSecretShow: Show[ClientSecret] = deriving
  }

  @newtype case class AuthorizeUri(value: UriString)
  object AuthorizeUri {
    implicit val AuthorizeUriShow: Show[AuthorizeUri] = deriving
  }

  @newtype case class ApiTokenUri(value: UriString)
  object ApiTokenUri {
    implicit val ApiTokenUriShow: Show[ApiTokenUri] = deriving
  }

  @newtype case class RedirectUri(value: UriString)
  object RedirectUri {
    implicit val RedirectUriShow: Show[RedirectUri] = deriving
  }

  def parseScope(rawScope: ScopeString): Either[String, List[Scope]] =
    rawScope.split("\\s").toList.map(refineV[ScopeP](_)).sequence[Either[String, *], Scope]

  def joinScopes(scopes: List[Scope]): Either[String, ScopeString] =
    joinRefinedStrings(scopes, " ")

  def addScopeParam(params: ParamMap, scopes: List[Scope]): Either[String, ParamMap] =
    joinScopes(scopes).map(addRefinedStringParam("scope", params, _))

  def base64Credentials(clientId: ClientId, clientSecret: ClientSecret): String =
    base64(show"$clientId:$clientSecret")

  def base64(s: String): String =
    Base64.getEncoder.encodeToString(s.getBytes(UTF_8))

  def makeAuthorizeUri(authorizeUri: AuthorizeUri, req: AuthorizeRequest): Either[String, UriString] = {
    val params = Map(
      "client_id"     -> Some(req.client_id.show),
      "response_type" -> Some(req.response_type.show),
      "redirect_uri"  -> Some(encodeUrl(req.redirect_uri.show)),
      "show_dialog"   -> req.show_dialog.map(_.show),
      "state"         -> req.state.map(_.show)
    )

    req.scope
      .map(addScopeParam(params, _))
      .getOrElse(Right(params))
      .map(makeQueryString)
      .map(q => show"$authorizeUri?$q")
      .flatMap(refineV[UriStringP](_))
  }
}

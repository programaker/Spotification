package spotification.spotifyapi.authorization

sealed abstract class GrantType(val name: String) extends Product with Serializable

object GrantType {
  case object AuthorizationCode extends GrantType("authorization_code")
  case object RefreshToken extends GrantType("refresh_token")
}

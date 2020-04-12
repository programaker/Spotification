package spotification.spotify.authorization

sealed abstract class AuthorizeResponseType(val name: String)

object AuthorizeResponseType {
  object Code extends AuthorizeResponseType("code")
  object Token extends AuthorizeResponseType("token")
}

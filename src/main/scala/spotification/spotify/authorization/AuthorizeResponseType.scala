package spotification.spotify.authorization

final class AuthorizeResponseType private (val value: String) extends AnyVal

object AuthorizeResponseType {
  val Code: AuthorizeResponseType = new AuthorizeResponseType("code")
  val Token: AuthorizeResponseType = new AuthorizeResponseType("token")
}

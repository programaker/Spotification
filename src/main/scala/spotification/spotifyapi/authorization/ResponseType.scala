package spotification.spotifyapi.authorization

sealed abstract class ResponseType(val name: String)

object ResponseType {
  object Code extends ResponseType("code")
  object Token extends ResponseType("token")
}
